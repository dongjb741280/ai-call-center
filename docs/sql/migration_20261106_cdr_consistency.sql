-- MySQL 5.7+/8.0: run BEFORE deploying the CDR writer (maintenance window recommended).
-- This migration does NOT delete duplicates or convert historical durations.
-- Duplicate natural keys abort the migration for explicit reconciliation.
CREATE TABLE IF NOT EXISTS cc_cdr_notify (
    call_id BIGINT NOT NULL PRIMARY KEY,
    company_id BIGINT NOT NULL,
    cdr_notify_url VARCHAR(255) NOT NULL,
    content LONGTEXT NOT NULL,
    push_times INT NOT NULL DEFAULT 0,
    push_response VARCHAR(255) NOT NULL DEFAULT '',
    status INT NOT NULL DEFAULT 1 COMMENT '1 pending, 2 delivered, 3 exhausted',
    next_attempt_at BIGINT NOT NULL COMMENT 'epoch milliseconds',
    lease_until BIGINT NOT NULL DEFAULT 0 COMMENT 'epoch milliseconds',
    lease_token VARCHAR(36) NOT NULL DEFAULT '',
    KEY idx_cdr_notify_due (status, next_attempt_at, lease_until)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DELIMITER $$
DROP PROCEDURE IF EXISTS migrate_cdr_keys$$
CREATE PROCEDURE migrate_cdr_keys()
BEGIN
    DECLARE finished INT DEFAULT 0;
    DECLARE target VARCHAR(64);
    DECLARE key_name VARCHAR(64);
    DECLARE key_columns VARCHAR(100);
    DECLARE cursor_tables CURSOR FOR
        SELECT table_name FROM information_schema.tables
        WHERE table_schema = DATABASE()
        AND table_name REGEXP '^cc_call_(log|device|detail)(_[0-9]{6})?$';
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET finished = 1;
    OPEN cursor_tables;
    table_loop: LOOP
        FETCH cursor_tables INTO target;
        IF finished = 1 THEN LEAVE table_loop; END IF;
        IF target REGEXP '^cc_call_log' THEN
            SET key_name = 'uniq_calllog_call_id'; SET key_columns = '`call_id`';
        ELSEIF target REGEXP '^cc_call_device' THEN
            SET key_name = 'uniq_cdr_device'; SET key_columns = '`call_id`,`device_id`';
        ELSE
            SET key_name = 'uniq_cdr_detail'; SET key_columns = '`call_id`,`detail_index`';
        END IF;
        IF NOT EXISTS (SELECT 1 FROM information_schema.statistics
                       WHERE table_schema = DATABASE() AND table_name = target AND index_name = key_name) THEN
            -- ALTER fails without changing that table if duplicate keys exist.
            SET @cdr_ddl = CONCAT('ALTER TABLE `', target, '` ADD UNIQUE KEY `', key_name, '` (', key_columns, ')');
            PREPARE cdr_stmt FROM @cdr_ddl; EXECUTE cdr_stmt; DEALLOCATE PREPARE cdr_stmt;
        END IF;
    END LOOP;
    CLOSE cursor_tables;
END$$
CALL migrate_cdr_keys()$$
DROP PROCEDURE migrate_cdr_keys$$
DELIMITER ;
