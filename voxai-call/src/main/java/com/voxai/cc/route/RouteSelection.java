package com.voxai.cc.route;

import com.voxai.core.entity.RouteGetway;
import com.voxai.core.po.RouteCallInfo;
import org.springframework.beans.BeanUtils;
import java.util.*;

/** A per-call snapshot; never attach number changes to a shared cached gateway. */
public final class RouteSelection extends RouteGetway {
    private final RouteCallInfo rule;
    private final List<RouteGetway> candidates;
    private final GatewayRouter router;

    RouteSelection(RouteCallInfo rule, List<RouteGetway> candidates, GatewayRouter router) {
        BeanUtils.copyProperties(candidates.get(0), this);
        this.rule = rule;
        this.candidates = Collections.unmodifiableList(new ArrayList<>(candidates));
        this.router = router;
    }

    public String caller(String original) { return rewrite(original, rule.getCallerChange(), rule.getCallerChangeNum()); }
    public String called(String original) { return rewrite(original, rule.getCalledChange(), rule.getCalledChangeNum()); }

    /** N means strip N leading characters, then prepend the replacement. */
    public static String rewrite(String original, Integer strip, String replacement) {
        int count = strip == null ? 0 : strip;
        if (count < 0 || original == null && count > 0 || original != null && count > original.length()) {
            throw new IllegalArgumentException("Number is shorter than its route rewrite rule");
        }
        return (replacement == null ? "" : replacement) + (original == null ? "" : original.substring(count));
    }

    public void failed(String cause) { if (GatewayRouter.isGatewayFailure(cause)) router.failed(getId()); }
    public void succeeded() { router.succeeded(getId()); }

    public RouteSelection next() {
        List<RouteGetway> remaining = new ArrayList<>();
        for (int i = 1; i < candidates.size(); i++) {
            if (router.available(candidates.get(i).getId())) remaining.add(candidates.get(i));
        }
        return remaining.isEmpty() ? null : new RouteSelection(rule, remaining, router);
    }
}
