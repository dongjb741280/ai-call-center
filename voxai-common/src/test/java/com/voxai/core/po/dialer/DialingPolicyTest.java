package com.voxai.core.po.dialer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DialingPolicyTest {
    private DialerCapacity metrics() {
        DialerCapacity metrics=new DialerCapacity(); metrics.setReadyAgents(4); metrics.setActiveCalls(1); metrics.setTargetActiveCalls(2); metrics.setCompanyActiveCalls(3); return metrics;
    }
    private int slots(String mode,boolean human,DialerCapacity metrics) { return DialingPolicy.allowance(mode,10,human,metrics,8,2,0.03); }
    @Test void cruiseConsumesOnlyAvailableHumanAndCompanyCapacity() { assertEquals(2,slots("cruise",true,metrics())); }
    @Test void predictiveColdStartIsConservative() { assertEquals(2,slots("predictive",true,metrics())); }
    @Test void predictiveRatioUsesActualAnswerRateAndIsCapped() {
        DialerCapacity metrics=metrics(); metrics.setCompletedCalls(100); metrics.setAnsweredCalls(20); assertEquals(5,slots("predictive",true,metrics));
        metrics.setCompanyActiveCalls(0); assertEquals(6,slots("predictive",true,metrics));
    }
    @Test void abandonmentDisablesOverdialing() { DialerCapacity metrics=metrics(); metrics.setCompletedCalls(100); metrics.setAnsweredCalls(20); metrics.setAbandonedCalls(1); assertEquals(2,slots("predictive",true,metrics)); }
    @Test void zeroAgentsAndFullCompanyQuotaStopHumanDialing() {
        DialerCapacity metrics=metrics(); metrics.setReadyAgents(0); assertEquals(0,slots("predictive",true,metrics)); metrics.setReadyAgents(4); metrics.setCompanyActiveCalls(8); assertEquals(0,slots("cruise",true,metrics));
    }
    @Test void ivrOnlyAcceptsCruiseAndStillEnforcesQuota() { assertEquals(5,slots("cruise",false,metrics())); assertEquals(0,slots("predictive",false,metrics())); }
}
