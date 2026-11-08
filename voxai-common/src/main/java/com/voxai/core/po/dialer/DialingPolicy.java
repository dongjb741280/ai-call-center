package com.voxai.core.po.dialer;

/** Bounded answer-rate prediction. Cold start and excessive abandonment use one line per ready agent. */
public final class DialingPolicy {
    private DialingPolicy() { }

    public static int allowance(String mode, int concurrency, boolean humanTarget, DialerCapacity metrics,
                                int companyLimit, double maxRatio, double maxAbandonRate) {
        int taskRoom = Math.max(0, concurrency - metrics.getActiveCalls());
        int companyRoom = Math.max(0, companyLimit - metrics.getCompanyActiveCalls());
        int room = Math.min(taskRoom, companyRoom);
        if (!humanTarget) { return "cruise".equals(mode) ? room : 0; }
        if (metrics.getReadyAgents() <= 0) { return 0; }
        double ratio = 1;
        if ("predictive".equals(mode) && metrics.getCompletedCalls() >= 20) {
            double abandonRate = metrics.getAnsweredCalls() == 0 ? 0 :
                    (double) metrics.getAbandonedCalls() / metrics.getAnsweredCalls();
            if (abandonRate <= maxAbandonRate) {
                double answerRate = Math.max(0.2, (double) metrics.getAnsweredCalls() / metrics.getCompletedCalls());
                ratio = Math.max(1, Math.min(maxRatio, 1 / answerRate));
            }
        }
        int humanRoom = Math.max(0, (int) Math.floor(metrics.getReadyAgents() * ratio) - metrics.getTargetActiveCalls());
        return Math.min(room, humanRoom);
    }
}
