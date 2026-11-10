-- Apply before deploying the routing update (MySQL).
-- Existing groups use round robin; membership row ID preserves primary/backup order.
-- Run once before the updated admin/call services are deployed.
ALTER TABLE cc_route_group
    ADD COLUMN selection_strategy varchar(16) NOT NULL DEFAULT 'ROUND_ROBIN'
        COMMENT 'ROUND_ROBIN/WEIGHTED/FAILOVER';
ALTER TABLE cc_route_getway_group
    ADD COLUMN weight int NOT NULL DEFAULT 1 COMMENT '组内网关权重1-1000';

-- Audit legacy rules before enabling length checks and number rewrites.
-- Zero length bounds mean unbounded for legacy data; inverted nonzero bounds never match.
SELECT id, company_id, route_num, num_min, num_max, caller_change, caller_change_num,
       called_change, called_change_num
FROM cc_route_call
WHERE status = 1 AND (
    num_min < 0 OR num_max < 0 OR (num_max > 0 AND num_min > num_max)
    OR caller_change < 0 OR called_change < 0
    OR caller_change > 0 OR called_change > 0
    OR caller_change_num <> '' OR called_change_num <> ''
);
-- Rewrite contract: remove N leading characters, then prepend *_change_num.
-- 0 + empty string leaves a number unchanged. Review legacy nonempty values explicitly;
-- this migration does not silently reset customer rules.
