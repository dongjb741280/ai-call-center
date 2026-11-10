package com.voxai.cc.route;

import com.voxai.core.entity.RouteGetway;
import com.voxai.core.po.RouteCallInfo;
import com.voxai.core.po.RouteGroupPo;
import java.time.Clock;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/** Immutable rule snapshots with independent, thread-safe group cursors and gateway cooldowns. */
public final class GatewayRouter {
    private volatile Map<Long, List<RouteCallInfo>> rules = Collections.emptyMap();
    private final Map<String, AtomicLong> cursors = new ConcurrentHashMap<>();
    private final Map<Long, Long> unavailableUntil = new ConcurrentHashMap<>();
    private final Clock clock;
    private static final Set<String> FAILURES = new HashSet<>(Arrays.asList(
            "NETWORK_OUT_OF_ORDER", "DESTINATION_OUT_OF_ORDER", "NORMAL_TEMPORARY_FAILURE",
            "SWITCH_CONGESTION", "NORMAL_CIRCUIT_CONGESTION", "GATEWAY_DOWN", "INVALID_GATEWAY",
            "RECOVERY_ON_TIMER_EXPIRE"));

    public GatewayRouter() { this(Clock.systemUTC()); }
    GatewayRouter(Clock clock) { this.clock = clock; }

    public void load(Collection<Long> companies, List<RouteCallInfo> all) {
        Map<Long, List<RouteCallInfo>> snapshot = new HashMap<>();
        for (Long company : companies) {
            Map<String, RouteCallInfo> merged = new HashMap<>();
            // Global first, tenant second: an identical tenant prefix overrides the entire global rule.
            for (long owner : new long[] {0L, company}) {
                all.stream().filter(r -> Objects.equals(r.getCompanyId(), owner))
                        .filter(r -> r.getRouteNum() != null && !r.getRouteNum().isEmpty())
                        .filter(r -> r.getStatus() == null || r.getStatus() == 1)
                        .sorted(Comparator.comparing(RouteCallInfo::getId, Comparator.nullsLast(Comparator.reverseOrder())))
                        .forEach(r -> merged.put(r.getRouteNum(), r));
            }
            List<RouteCallInfo> ordered = new ArrayList<>(merged.values());
            ordered.sort(Comparator.comparing((RouteCallInfo r) -> "*".equals(r.getRouteNum()))
                    .thenComparing(Comparator.comparingInt((RouteCallInfo r) -> r.getRouteNum().length()).reversed())
                    .thenComparing(RouteCallInfo::getRouteNum));
            snapshot.put(company, Collections.unmodifiableList(ordered));
        }
        rules = Collections.unmodifiableMap(snapshot);
    }

    public RouteSelection select(Long company, String called) {
        if (company == null || called == null || called.trim().isEmpty()) return null;
        for (RouteCallInfo rule : rules.getOrDefault(company, Collections.emptyList())) {
            if (!"*".equals(rule.getRouteNum()) && !called.startsWith(rule.getRouteNum())) continue;
            int min = rule.getNumMin() == null ? 0 : rule.getNumMin();
            int max = rule.getNumMax() == null || rule.getNumMax() == 0 ? Integer.MAX_VALUE : rule.getNumMax();
            if (called.length() < min || called.length() > max) continue;
            RouteGroupPo group = rule.getRouteGroupPo();
            // Once a rule matches, do not escape its tenant policy via another rule.
            if (group == null || group.getStatus() != null && group.getStatus() != 1 || group.getRouteGetways() == null) return null;
            List<RouteGetway> gateways = group.getRouteGetways().stream()
                    .filter(Objects::nonNull).filter(g -> g.getStatus() == null || g.getStatus() == 1)
                    .filter(g -> available(g.getId())).collect(Collectors.toList());
            if (gateways.isEmpty()) return null;
            String strategy = group.getSelectionStrategy();
            int first = 0;
            if (!"FAILOVER".equals(strategy)) {
                long total = "WEIGHTED".equals(strategy) ? gateways.stream().mapToLong(GatewayRouter::weight).sum() : gateways.size();
                long ticket = Math.floorMod(cursors.computeIfAbsent(company + ":" + group.getId(), k -> new AtomicLong()).getAndIncrement(), total);
                if ("WEIGHTED".equals(strategy)) {
                    while (ticket >= weight(gateways.get(first))) ticket -= weight(gateways.get(first++));
                } else { first = (int) ticket; }
            }
            Collections.rotate(gateways, -first);
            return new RouteSelection(rule, gateways, this);
        }
        return null;
    }

    private static int weight(RouteGetway gateway) { return gateway.getWeight() == null || gateway.getWeight() < 1 ? 1 : gateway.getWeight(); }
    public static boolean isGatewayFailure(String cause) { return FAILURES.contains(cause); }
    boolean available(Long id) { return id == null || unavailableUntil.getOrDefault(id, 0L) <= clock.millis(); }
    void failed(Long id) { if (id != null) unavailableUntil.put(id, clock.millis() + 30_000); }
    void succeeded(Long id) { if (id != null) unavailableUntil.remove(id); }
}
