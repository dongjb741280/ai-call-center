package com.voxai.cc.cache;

import com.voxai.cc.service.CompanyService;
import com.voxai.cc.service.GroupService;
import com.voxai.cc.route.RouteSelection;
import com.voxai.cc.route.GatewayRouter;
import com.voxai.core.entity.RouteGetway;
import com.voxai.core.mapper.*;
import com.voxai.core.po.*;
import org.junit.jupiter.api.Test;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.springframework.test.util.ReflectionTestUtils;
import java.io.InputStream;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RouteCacheTest {
    private CacheService cache(RouteCallInfo... rules) {
        CacheService cache = new CacheService();
        CompanyInfo company = new CompanyInfo(); company.setId(7L);
        CompanyService companies = mock(CompanyService.class);
        when(companies.initAll()).thenReturn(new HashMap<>(Collections.singletonMap(7L, company)));
        GroupService groups = mock(GroupService.class);
        when(groups.getGroupByConpany(7L)).thenReturn(Collections.emptyList());
        RouteCallMapper routes = mock(RouteCallMapper.class);
        when(routes.selectListByMap(anyMap())).thenReturn(Arrays.asList(rules));
        ReflectionTestUtils.setField(cache, "companyService", companies);
        ReflectionTestUtils.setField(cache, "groupService", groups);
        ReflectionTestUtils.setField(cache, "routeCallMapper", routes);
        ReflectionTestUtils.setField(cache, "vdnPhoneMapper", mock(VdnPhoneMapper.class));
        ReflectionTestUtils.setField(cache, "playbackMapper", mock(PlaybackMapper.class));
        cache.initCompany();
        return cache;
    }

    private RouteCallInfo rule(long company, String prefix, int min, int max, long gateway) {
        RouteCallInfo rule = new RouteCallInfo();
        rule.setId(gateway); rule.setCompanyId(company); rule.setRouteNum(prefix);
        rule.setNumMin(min); rule.setNumMax(max); rule.setStatus(1);
        RouteGetway gw = new RouteGetway(); gw.setId(gateway); gw.setStatus(1);
        RouteGroupPo group = new RouteGroupPo(); group.setId(gateway); group.setStatus(1);
        group.setRouteGetways(Collections.singletonList(gw)); rule.setRouteGroupPo(group);
        return rule;
    }

    @Test void onlyPrefixesMatchAndLongestPrefixWinsRegardlessOfLoadOrder() {
        CacheService cache = cache(rule(7, "13", 1, 32, 1), rule(7, "138", 1, 32, 2));
        assertNull(cache.getRouteGetway(7L, "99138000"));
        assertEquals(2L, cache.getRouteGetway(7L, "13800000000").getId());
    }

    @Test void wildcardIsAlwaysLastEvenAgainstOneDigitPrefix() {
        CacheService cache = cache(rule(0, "*", 1, 32, 1), rule(7, "1", 1, 32, 2));
        assertEquals(2L, cache.getRouteGetway(7L, "10010").getId());
        assertEquals(1L, cache.getRouteGetway(7L, "02112345678").getId());
    }

    @Test void companyOverridesTheSameGlobalPrefixInEitherLoadOrder() {
        RouteCallInfo global = rule(0, "138", 1, 32, 1), tenant = rule(7, "138", 1, 32, 2);
        assertEquals(2L, cache(global, tenant).getRouteGetway(7L, "13800000000").getId());
        assertEquals(2L, cache(tenant, global).getRouteGetway(7L, "13800000000").getId());
    }

    @Test void lengthBoundsAreInclusiveAndUnknownCompanyIsNoRoute() {
        CacheService cache = cache(rule(7, "1", 11, 11, 1), rule(0, "*", 1, 32, 2));
        assertEquals(1L, cache.getRouteGetway(7L, "13800000000").getId());
        assertEquals(2L, cache.getRouteGetway(7L, "10010").getId());
        assertNull(cache.getRouteGetway(999L, "13800000000"));
        assertNull(cache.getRouteGetway(7L, null));
    }

    @Test void routeRewriteRemovesLeadingDigitsBeforeAddingReplacementPrefix() {
        assertEquals("93800138000", RouteSelection.rewrite("13800138000", 1, "9"));
        assertEquals("13800138000", RouteSelection.rewrite("13800138000", 0, ""));
    }

    @Test void gatewayGroupUsesConfiguredWeightsAndFailoverOrder() {
        RouteCallInfo first = rule(7, "9", 1, 32, 1);
        RouteGroupPo group = first.getRouteGroupPo();
        group.setSelectionStrategy("FAILOVER");
        RouteGetway backup = new RouteGetway(); backup.setId(2L); backup.setStatus(1);
        group.setRouteGetways(Arrays.asList((RouteGetway) group.getRouteGetways().get(0), backup));
        GatewayRouter router = new GatewayRouter(); router.load(Collections.singleton(7L), Collections.singletonList(first));
        assertEquals(1L, router.select(7L, "900").getId());
        assertEquals(2L, router.select(7L, "900").next().getId());
        assertTrue(GatewayRouter.isGatewayFailure("NETWORK_OUT_OF_ORDER"));
        assertFalse(GatewayRouter.isGatewayFailure("USER_BUSY"));
        assertFalse(GatewayRouter.isGatewayFailure("NO_ANSWER"));
    }

    @Test void onlyLineFailuresCoolDownTheGatewayForNewCalls() {
        RouteCallInfo rule = rule(7, "9", 1, 32, 1);
        RouteGroupPo group = rule.getRouteGroupPo();
        group.setSelectionStrategy("FAILOVER");
        RouteGetway backup = new RouteGetway(); backup.setId(2L); backup.setStatus(1);
        group.setRouteGetways(Arrays.asList(group.getRouteGetways().get(0), backup));
        GatewayRouter router = new GatewayRouter();
        router.load(Collections.singleton(7L), Collections.singletonList(rule));

        RouteSelection busy = router.select(7L, "900");
        busy.failed("USER_BUSY");
        assertEquals(1L, router.select(7L, "900").getId());

        router.select(7L, "900").failed("NETWORK_OUT_OF_ORDER");
        assertEquals(2L, router.select(7L, "900").getId());
    }

    @Test void gatewayRoundRobinAndWeightsSelectExpectedGateways() {
        RouteCallInfo rule = rule(7, "9", 1, 32, 1);
        RouteGroupPo group = rule.getRouteGroupPo();
        RouteGetway first = group.getRouteGetways().get(0); first.setWeight(2);
        RouteGetway second = new RouteGetway(); second.setId(2L); second.setStatus(1); second.setWeight(1);
        group.setRouteGetways(Arrays.asList(first, second));
        group.setSelectionStrategy("WEIGHTED");

        GatewayRouter weighted = new GatewayRouter(); weighted.load(Collections.singleton(7L), Collections.singletonList(rule));
        assertEquals(Arrays.asList(1L, 1L, 2L, 1L), Arrays.asList(
                weighted.select(7L, "900").getId(), weighted.select(7L, "900").getId(),
                weighted.select(7L, "900").getId(), weighted.select(7L, "900").getId()));

        group.setSelectionStrategy("ROUND_ROBIN");
        GatewayRouter roundRobin = new GatewayRouter(); roundRobin.load(Collections.singleton(7L), Collections.singletonList(rule));
        assertEquals(1L, roundRobin.select(7L, "900").getId());
        assertEquals(2L, roundRobin.select(7L, "900").getId());
        assertEquals(1L, roundRobin.select(7L, "900").getId());
    }

    @Test void routeAndGatewayMapperXmlLoadWithNewStrategyAndWeightFields() throws Exception {
        Configuration configuration = new Configuration();
        for (String mapper : Arrays.asList("RouteGroupMapper", "RouteGetwayGroupMapper")) {
            String resource = "mapper/" + mapper + ".xml";
            try (InputStream input = Resources.getResourceAsStream(resource)) {
                new XMLMapperBuilder(input, configuration, resource, configuration.getSqlFragments()).parse();
            }
        }
        assertTrue(configuration.hasStatement("com.voxai.core.mapper.RouteGroupMapper.selectById"));
        assertTrue(configuration.hasStatement("com.voxai.core.mapper.RouteGetwayGroupMapper.insertSelective"));
    }
}
