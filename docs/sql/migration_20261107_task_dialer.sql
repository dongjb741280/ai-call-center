-- 在 migration_20261107_task_safety.sql 后执行。新增调度日志用于幂等发起与崩溃恢复。
-- 已分配 call_id 的拨打不再因引擎租约超时自动重拨，必须先核对媒体通道和最终话单。
ALTER TABLE cc_task_attempt
    ADD COLUMN media_host VARCHAR(128) NULL COMMENT '固定的FreeSWITCH ESL节点' AFTER call_id,
    ADD COLUMN dispatch_time BIGINT NULL COMMENT '发起保留时间（秒）' AFTER media_host,
    ADD UNIQUE KEY uk_attempt_call (call_id);
