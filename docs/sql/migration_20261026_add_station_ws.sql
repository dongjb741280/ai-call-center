-- ============================================================================
-- 迁移：为 cc_station 增加软电话 WebSocket SIP 端点字段
-- 日期：2026-10-26
--
-- 背景：软电话（浏览器 WebRTC/JsSIP）注册需要连接 FreeSWITCH 的 WebSocket SIP
--       端点（mod_sofia 的 ws-binding，如 172.20.10.5:5066）。
--       该地址与后端连 FreeSWITCH 的 ESL 地址（application_host/application_port，
--       如 localhost:8022）不是同一个，且浏览器需要「可达地址」而非回环地址。
--       因此新增 ws_host / ws_port 存浏览器可达的 WS 端点。
--
-- 注意：必须先执行本迁移，再重建/重启后端。
--       因为 StationMapper 的 Base_Column_List 已包含 ws_host/ws_port，
--       若列未加就重载代码，/voxai-admin/admin/station 会报 Unknown column。
-- ============================================================================

ALTER TABLE cc_station
  ADD COLUMN ws_host varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '软电话 WebSocket SIP 地址' AFTER status,
  ADD COLUMN ws_port int NOT NULL DEFAULT '0' COMMENT '软电话 WebSocket SIP 端口' AFTER ws_host;

-- 为本地 FreeSWITCH 站点填 WebSocket SIP 端点（IP 按实际环境调整）
UPDATE cc_station
SET ws_host = '172.20.10.5', ws_port = 5066
WHERE application_host = 'localhost' AND application_port = 8022;
