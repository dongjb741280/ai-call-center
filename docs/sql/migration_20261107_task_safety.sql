-- 外呼任务安全与可恢复领取模型。
-- 先备份数据库；在低峰执行。DDL 仅新增字段/索引/表，不删除历史数据。
-- 执行前检查下列查询结果；若有重复名称或非空名单 UUID 重复，先人工处理后再加唯一索引。
-- SELECT company_id, name, COUNT(*) FROM cc_task_source GROUP BY company_id, name HAVING COUNT(*) > 1;
-- SELECT task_id, uuid, COUNT(*) FROM cc_task_contact WHERE uuid IS NOT NULL GROUP BY task_id, uuid HAVING COUNT(*) > 1;
ALTER TABLE cc_task_config
    ADD COLUMN source_id BIGINT NULL COMMENT '稳定数据源ID' AFTER data_source,
    ADD COLUMN transfer_id BIGINT NULL COMMENT '稳定转接目标ID' AFTER transfer_type,
    ADD COLUMN number_pool_id BIGINT NULL COMMENT '稳定号码池ID' AFTER number_pool_name,
    ADD COLUMN retry_interval INT NOT NULL DEFAULT 300 COMMENT '重拨间隔（秒）' AFTER call_timeout,
    ADD COLUMN requested_status INT NULL COMMENT '等待外部引擎确认的状态' AFTER status,
    ADD COLUMN command_id VARCHAR(64) NULL COMMENT '幂等命令ID' AFTER requested_status,
    ADD COLUMN command_time BIGINT NULL COMMENT '命令时间（秒）' AFTER command_id,
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0 COMMENT '乐观锁版本' AFTER command_time,
    ADD COLUMN archived TINYINT NOT NULL DEFAULT 0 COMMENT '归档标记' AFTER version;
ALTER TABLE cc_task_config ADD INDEX idx_task_company_status (company_id, status, archived);
ALTER TABLE cc_task_config ADD UNIQUE KEY uk_task_company_command (company_id, command_id);

ALTER TABLE cc_task_source ADD COLUMN version BIGINT NOT NULL DEFAULT 0 COMMENT '乐观锁版本';
ALTER TABLE cc_task_source ADD UNIQUE KEY uk_task_source_company_name (company_id, name);

ALTER TABLE cc_task_contact
    ADD COLUMN source_id BIGINT NULL COMMENT '稳定数据源ID' AFTER task_id,
    ADD COLUMN custom_data JSON NULL COMMENT '客户自定义字段JSON对象' AFTER uuid,
    ADD COLUMN active_attempt_id VARCHAR(64) NULL COMMENT '当前拨打记录' AFTER call_result,
    ADD COLUMN next_attempt_at BIGINT NOT NULL DEFAULT 0 COMMENT '下次允许拨打时间（秒）' AFTER active_attempt_id;
ALTER TABLE cc_task_contact ADD UNIQUE KEY uk_task_contact_uuid (task_id, uuid);
ALTER TABLE cc_task_contact ADD INDEX idx_task_contact_claim (task_id, call_status, next_attempt_at, current_round);

CREATE TABLE cc_task_attempt (
    id BIGINT NOT NULL AUTO_INCREMENT,
    attempt_id VARCHAR(64) NOT NULL,
    company_id BIGINT NOT NULL,
    task_id BIGINT NOT NULL,
    contact_id BIGINT NOT NULL,
    request_id VARCHAR(64) NOT NULL,
    lease_token VARCHAR(64) NOT NULL,
    lease_until BIGINT NOT NULL,
    call_id BIGINT NULL,
    round INT NOT NULL,
    state VARCHAR(16) NOT NULL,
    result VARCHAR(16) NULL,
    cts BIGINT NOT NULL,
    uts BIGINT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_attempt_id (attempt_id),
    UNIQUE KEY uk_attempt_request (company_id, request_id),
    KEY idx_attempt_lease (state, lease_until),
    KEY idx_attempt_task (company_id, task_id, cts)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='外呼名单单次拨打记录';

-- 旧数据无法推断稳定的转接目标ID和任务来源ID；迁移后只能由管理端补齐后启动任务。
