-- ============================================================================
-- 迁移：坐席主技能组从 cc_agent.group_id 迁移到 cc_agent_group.is_primary
-- 日期：2026-11-03
--
-- 背景：cc_agent.group_id（主技能组）与 cc_agent_group（多技能组关联）存在重复语义。
--       统一以 cc_agent_group 为唯一权威来源，用 is_primary 标记主技能组，
--       并移除 cc_agent.group_id 冗余字段。
--
-- 注意：必须先执行本迁移，再重启后端。
--       AgentMapper 已不再 select cc_agent.group_id，
--       AgentGroupMapper 已 select cc_agent_group.is_primary，
--       若列未迁移就重载代码会报 Unknown column。
-- ============================================================================

-- 1. cc_agent_group 增加主组标识
ALTER TABLE cc_agent_group
  ADD COLUMN is_primary tinyint NOT NULL DEFAULT 0 COMMENT '是否主技能组 1:是 0:否' AFTER status;

-- 2. 回填主组标识：旧代码用「坐席的第一个技能组」作为主组（selectByAgent().get(0)），
--    此处按 MIN(id) 取每个坐席的第一条关联记录标记为主组
UPDATE cc_agent_group ag
  JOIN (
      SELECT agent_id, MIN(id) AS first_id
      FROM cc_agent_group
      WHERE agent_id <> 0
      GROUP BY agent_id
  ) t ON ag.agent_id = t.agent_id AND ag.id = t.first_id
SET ag.is_primary = 1;

-- 3. 删除 cc_agent.group_id 冗余字段
ALTER TABLE cc_agent DROP COLUMN group_id;
