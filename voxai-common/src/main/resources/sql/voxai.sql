-- MySQL dump 10.13  Distrib 9.2.0, for macos14.7 (arm64)
--
-- Host: 127.0.0.1    Database: voxai
-- ------------------------------------------------------
-- Server version	9.2.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `cc_admin_log`
--

DROP TABLE IF EXISTS `cc_admin_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_admin_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cts` bigint NOT NULL DEFAULT '0',
  `uts` bigint NOT NULL DEFAULT '0',
  `company_id` bigint NOT NULL DEFAULT '0',
  `account` varchar(64) NOT NULL DEFAULT '',
  `op_time` bigint NOT NULL DEFAULT '0',
  `enterprise_id` bigint NOT NULL DEFAULT '0',
  `request_url` varchar(512) NOT NULL DEFAULT '',
  `duration` int NOT NULL DEFAULT '0',
  `user_ip` varchar(64) NOT NULL DEFAULT '',
  `region` varchar(128) NOT NULL DEFAULT '',
  `isp` varchar(64) NOT NULL DEFAULT '',
  `server_addr` varchar(256) NOT NULL DEFAULT '',
  `request_params` text,
  `status` int NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `idx_account` (`account`),
  KEY `idx_op_time` (`op_time`),
  KEY `idx_company_id` (`company_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_admin_menu`
--

DROP TABLE IF EXISTS `cc_admin_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_admin_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `menu_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '菜单id',
  `parent_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '父id',
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '菜单名称',
  `path_url` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '访问url',
  `path_method` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '访问方法',
  `menu_level` int NOT NULL DEFAULT '1' COMMENT '菜单等级',
  `menu_order` int NOT NULL DEFAULT '0' COMMENT '排序',
  `child_num` int NOT NULL DEFAULT '0' COMMENT '子节点数',
  `icon` varchar(255) NOT NULL COMMENT '图标',
  `create_default` int NOT NULL DEFAULT '0' COMMENT '创建时添加权限(1:是,0:否)',
  `init_default` int NOT NULL DEFAULT '0' COMMENT '已有账号授权(1:是,0:否)',
  `front_site` int NOT NULL DEFAULT '1' COMMENT '前端',
  `front_icon` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '图标',
  `end_site` int NOT NULL DEFAULT '1' COMMENT '后端',
  `status` int NOT NULL DEFAULT '1' COMMENT '数据状态',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uniq_menus_name` (`name`),
  UNIQUE KEY `uniq_menu_id` (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_admin_role`
--

DROP TABLE IF EXISTS `cc_admin_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_admin_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '所属企业',
  `role_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '角色名称',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_admin_role_menu`
--

DROP TABLE IF EXISTS `cc_admin_role_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_admin_role_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '权限ID',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uniq_role_perm` (`role_id`,`menu_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='权限角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_admin_user`
--

DROP TABLE IF EXISTS `cc_admin_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_admin_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业ID',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '用户名',
  `passwd` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '密码',
  `avatar` varchar(255) NOT NULL DEFAULT '',
  `nickname` varchar(255) NOT NULL DEFAULT '' COMMENT '昵称',
  `phone` varchar(64) NOT NULL DEFAULT '' COMMENT '电话',
  `email` varchar(255) NOT NULL DEFAULT '' COMMENT '邮箱',
  `user_type` int NOT NULL DEFAULT '1' COMMENT '类型',
  `status` int NOT NULL DEFAULT '0' COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uniq_username` (`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='企业管理员表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_admin_user_role`
--

DROP TABLE IF EXISTS `cc_admin_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_admin_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `uid` bigint NOT NULL DEFAULT '0' COMMENT '账号ID',
  `role_id` bigint NOT NULL DEFAULT '0' COMMENT '角色ID',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业ID',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_acmount_id` (`uid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='账号角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_agent`
--

DROP TABLE IF EXISTS `cc_agent`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_agent` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业ID',
  `agent_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '坐席工号',
  `agent_key` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '坐席账户',
  `agent_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '坐席名称',
  `agent_code` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '坐席分机号',
  `agent_type` int NOT NULL DEFAULT '2' COMMENT '座席类型：1:普通座席；2：班长',
  `passwd` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '座席密码',
  `sip_phone` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '绑定的电话号码',
  `record` int NOT NULL DEFAULT '0' COMMENT '是否录音 0 no 1 yes',
  `group_id` bigint NOT NULL DEFAULT '0' COMMENT '座席主要技能组  不能为空 必填项',
  `after_interval` int NOT NULL DEFAULT '5' COMMENT '话后自动空闲间隔时长',
  `display` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '主叫显号',
  `ring_time` int NOT NULL DEFAULT '10' COMMENT '振铃时长',
  `host` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '登录服务器地址',
  `ext1` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '扩展1',
  `ext2` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '扩展2',
  `ext3` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '扩展3',
  `state` int NOT NULL DEFAULT '0' COMMENT '坐席状态(1:在线,0:不在线)',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态：1 开通，0关闭',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uniq_agent_key` (`agent_key`,`company_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb3 COMMENT='座席工号表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_agent_group`
--

DROP TABLE IF EXISTS `cc_agent_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_agent_group` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业ID',
  `agent_id` bigint NOT NULL DEFAULT '0' COMMENT '坐席id',
  `agent_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `agent_type` int NOT NULL DEFAULT '1',
  `group_id` bigint NOT NULL DEFAULT '0' COMMENT '技能组id',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uniq_agent_group_agent_group` (`group_id`,`agent_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='坐席技能组表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_agent_sip`
--

DROP TABLE IF EXISTS `cc_agent_sip`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_agent_sip` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cts` bigint NOT NULL DEFAULT '0',
  `uts` bigint NOT NULL DEFAULT '0',
  `company_id` bigint DEFAULT '0',
  `sip` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `agent_id` bigint NOT NULL DEFAULT '0',
  `sip_pwd` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '',
  `status` int NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_agentsip_agent` (`agent_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='sip表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_agent_state_log`
--

DROP TABLE IF EXISTS `cc_agent_state_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_agent_state_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业id',
  `group_id` bigint NOT NULL DEFAULT '0' COMMENT '主技能组id',
  `agent_id` bigint NOT NULL DEFAULT '0' COMMENT '坐席id',
  `agent_key` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '坐席编号',
  `agent_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '坐席名称',
  `call_id` bigint NOT NULL DEFAULT '0' COMMENT '通话唯一标识',
  `login_type` int NOT NULL DEFAULT '1' COMMENT '登录类型',
  `work_type` int NOT NULL DEFAULT '1' COMMENT '工作类型',
  `host` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '服务站点',
  `remote_address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '远端地址',
  `before_state` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '变更之前状态',
  `before_time` bigint NOT NULL DEFAULT '0' COMMENT '更变之前时间',
  `state` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '变更之后状态',
  `state_time` bigint NOT NULL DEFAULT '0' COMMENT '当前时间(秒)',
  `duration` int NOT NULL DEFAULT '0' COMMENT '持续时间(秒)',
  `busy_desc` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '忙碌类型',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态',
  `ext1` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '扩展字段1',
  `ext2` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '扩展字段2',
  `ext3` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '扩展字段3',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_agentstate_agentkey` (`agent_key`) USING BTREE,
  KEY `idx_agentstate_cts` (`state_time`) USING BTREE,
  KEY `idx_agentstate_group_id` (`group_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2267 DEFAULT CHARSET=utf8mb3 COMMENT='坐席状态历史表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_agent_state_log_202607`
--

DROP TABLE IF EXISTS `cc_agent_state_log_202607`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_agent_state_log_202607` (
  `id` bigint NOT NULL DEFAULT '0' COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业id',
  `group_id` bigint NOT NULL DEFAULT '0' COMMENT '主技能组id',
  `agent_id` bigint NOT NULL DEFAULT '0' COMMENT '坐席id',
  `agent_key` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '坐席编号',
  `agent_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '坐席名称',
  `call_id` bigint NOT NULL DEFAULT '0' COMMENT '通话唯一标识',
  `login_type` int NOT NULL DEFAULT '1' COMMENT '登录类型',
  `work_type` int NOT NULL DEFAULT '1' COMMENT '工作类型',
  `host` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '服务站点',
  `remote_address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '远端地址',
  `before_state` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '变更之前状态',
  `before_time` bigint NOT NULL DEFAULT '0' COMMENT '更变之前时间',
  `state` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '变更之后状态',
  `state_time` bigint NOT NULL DEFAULT '0' COMMENT '当前时间(秒)',
  `duration` int NOT NULL DEFAULT '0' COMMENT '持续时间(秒)',
  `busy_desc` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '忙碌类型',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态',
  `ext1` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '扩展字段1',
  `ext2` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '扩展字段2',
  `ext3` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '扩展字段3'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_agent_state_log_202608`
--

DROP TABLE IF EXISTS `cc_agent_state_log_202608`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_agent_state_log_202608` (
  `id` bigint NOT NULL DEFAULT '0' COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业id',
  `group_id` bigint NOT NULL DEFAULT '0' COMMENT '主技能组id',
  `agent_id` bigint NOT NULL DEFAULT '0' COMMENT '坐席id',
  `agent_key` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '坐席编号',
  `agent_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '坐席名称',
  `call_id` bigint NOT NULL DEFAULT '0' COMMENT '通话唯一标识',
  `login_type` int NOT NULL DEFAULT '1' COMMENT '登录类型',
  `work_type` int NOT NULL DEFAULT '1' COMMENT '工作类型',
  `host` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '服务站点',
  `remote_address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '远端地址',
  `before_state` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '变更之前状态',
  `before_time` bigint NOT NULL DEFAULT '0' COMMENT '更变之前时间',
  `state` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '变更之后状态',
  `state_time` bigint NOT NULL DEFAULT '0' COMMENT '当前时间(秒)',
  `duration` int NOT NULL DEFAULT '0' COMMENT '持续时间(秒)',
  `busy_desc` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '忙碌类型',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态',
  `ext1` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '扩展字段1',
  `ext2` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '扩展字段2',
  `ext3` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '扩展字段3'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_agent_state_log_202609`
--

DROP TABLE IF EXISTS `cc_agent_state_log_202609`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_agent_state_log_202609` (
  `id` bigint NOT NULL DEFAULT '0' COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业id',
  `group_id` bigint NOT NULL DEFAULT '0' COMMENT '主技能组id',
  `agent_id` bigint NOT NULL DEFAULT '0' COMMENT '坐席id',
  `agent_key` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '坐席编号',
  `agent_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '坐席名称',
  `call_id` bigint NOT NULL DEFAULT '0' COMMENT '通话唯一标识',
  `login_type` int NOT NULL DEFAULT '1' COMMENT '登录类型',
  `work_type` int NOT NULL DEFAULT '1' COMMENT '工作类型',
  `host` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '服务站点',
  `remote_address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '远端地址',
  `before_state` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '变更之前状态',
  `before_time` bigint NOT NULL DEFAULT '0' COMMENT '更变之前时间',
  `state` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '变更之后状态',
  `state_time` bigint NOT NULL DEFAULT '0' COMMENT '当前时间(秒)',
  `duration` int NOT NULL DEFAULT '0' COMMENT '持续时间(秒)',
  `busy_desc` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '忙碌类型',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态',
  `ext1` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '扩展字段1',
  `ext2` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '扩展字段2',
  `ext3` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '扩展字段3'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_agent_state_log_202610`
--

DROP TABLE IF EXISTS `cc_agent_state_log_202610`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_agent_state_log_202610` (
  `id` bigint NOT NULL DEFAULT '0' COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业id',
  `group_id` bigint NOT NULL DEFAULT '0' COMMENT '主技能组id',
  `agent_id` bigint NOT NULL DEFAULT '0' COMMENT '坐席id',
  `agent_key` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '坐席编号',
  `agent_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '坐席名称',
  `call_id` bigint NOT NULL DEFAULT '0' COMMENT '通话唯一标识',
  `login_type` int NOT NULL DEFAULT '1' COMMENT '登录类型',
  `work_type` int NOT NULL DEFAULT '1' COMMENT '工作类型',
  `host` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '服务站点',
  `remote_address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '远端地址',
  `before_state` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '变更之前状态',
  `before_time` bigint NOT NULL DEFAULT '0' COMMENT '更变之前时间',
  `state` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '变更之后状态',
  `state_time` bigint NOT NULL DEFAULT '0' COMMENT '当前时间(秒)',
  `duration` int NOT NULL DEFAULT '0' COMMENT '持续时间(秒)',
  `busy_desc` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '忙碌类型',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态',
  `ext1` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '扩展字段1',
  `ext2` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '扩展字段2',
  `ext3` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '扩展字段3'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_agent_state_log_202611`
--

DROP TABLE IF EXISTS `cc_agent_state_log_202611`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_agent_state_log_202611` (
  `id` bigint NOT NULL DEFAULT '0' COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业id',
  `group_id` bigint NOT NULL DEFAULT '0' COMMENT '主技能组id',
  `agent_id` bigint NOT NULL DEFAULT '0' COMMENT '坐席id',
  `agent_key` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '坐席编号',
  `agent_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '坐席名称',
  `call_id` bigint NOT NULL DEFAULT '0' COMMENT '通话唯一标识',
  `login_type` int NOT NULL DEFAULT '1' COMMENT '登录类型',
  `work_type` int NOT NULL DEFAULT '1' COMMENT '工作类型',
  `host` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '服务站点',
  `remote_address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '远端地址',
  `before_state` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '变更之前状态',
  `before_time` bigint NOT NULL DEFAULT '0' COMMENT '更变之前时间',
  `state` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '变更之后状态',
  `state_time` bigint NOT NULL DEFAULT '0' COMMENT '当前时间(秒)',
  `duration` int NOT NULL DEFAULT '0' COMMENT '持续时间(秒)',
  `busy_desc` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '忙碌类型',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态',
  `ext1` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '扩展字段1',
  `ext2` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '扩展字段2',
  `ext3` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '扩展字段3'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_agent_state_log_202612`
--

DROP TABLE IF EXISTS `cc_agent_state_log_202612`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_agent_state_log_202612` (
  `id` bigint NOT NULL DEFAULT '0' COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业id',
  `group_id` bigint NOT NULL DEFAULT '0' COMMENT '主技能组id',
  `agent_id` bigint NOT NULL DEFAULT '0' COMMENT '坐席id',
  `agent_key` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '坐席编号',
  `agent_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '坐席名称',
  `call_id` bigint NOT NULL DEFAULT '0' COMMENT '通话唯一标识',
  `login_type` int NOT NULL DEFAULT '1' COMMENT '登录类型',
  `work_type` int NOT NULL DEFAULT '1' COMMENT '工作类型',
  `host` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '服务站点',
  `remote_address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '远端地址',
  `before_state` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '变更之前状态',
  `before_time` bigint NOT NULL DEFAULT '0' COMMENT '更变之前时间',
  `state` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '变更之后状态',
  `state_time` bigint NOT NULL DEFAULT '0' COMMENT '当前时间(秒)',
  `duration` int NOT NULL DEFAULT '0' COMMENT '持续时间(秒)',
  `busy_desc` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '忙碌类型',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态',
  `ext1` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '扩展字段1',
  `ext2` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '扩展字段2',
  `ext3` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '扩展字段3'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_ai_engine`
--

DROP TABLE IF EXISTS `cc_ai_engine`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_ai_engine` (
  `id` bigint NOT NULL,
  `cts` bigint DEFAULT NULL,
  `uts` bigint DEFAULT NULL,
  `company_id` bigint DEFAULT NULL,
  `name` varchar(64) DEFAULT NULL,
  `mrcp_profile` varchar(64) DEFAULT NULL,
  `ai_type` int DEFAULT NULL,
  `ai_product` varchar(64) DEFAULT NULL,
  `voice` varchar(64) DEFAULT NULL,
  `grammar` varchar(255) DEFAULT NULL,
  `ai_params` text,
  `tts_template` varchar(255) DEFAULT NULL,
  `status` int DEFAULT '2',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_black_phone`
--

DROP TABLE IF EXISTS `cc_black_phone`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_black_phone` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cts` bigint DEFAULT NULL,
  `uts` bigint DEFAULT NULL,
  `company_id` bigint DEFAULT NULL,
  `num_prefix` varchar(32) DEFAULT NULL,
  `type` int DEFAULT NULL,
  `call_direction` int DEFAULT NULL,
  `status` int DEFAULT '2',
  `thrid_url` varchar(255) DEFAULT NULL,
  `thrid_method` varchar(255) DEFAULT NULL,
  `request_body` text,
  `response_body` text,
  `schedule_id` varchar(255) DEFAULT NULL,
  `schedule_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=187 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_call_detail`
--

DROP TABLE IF EXISTS `cc_call_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_call_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `start_time` bigint NOT NULL DEFAULT '0' COMMENT '开始时间',
  `end_time` bigint NOT NULL DEFAULT '0' COMMENT '结束时间',
  `call_id` bigint NOT NULL DEFAULT '0' COMMENT '通话ID',
  `detail_index` int NOT NULL DEFAULT '1' COMMENT '顺序',
  `transfer_type` int NOT NULL DEFAULT '0' COMMENT '1:进vdn,2:进ivr,3:技能组,4:按键收号,5:外线,6:机器人,10:服务评价',
  `transfer_id` bigint NOT NULL DEFAULT '0' COMMENT '转接ID',
  `reason` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '出队列原因:排队挂机或者转坐席',
  `ext1` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展字段1',
  `ext2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展字段2',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_calldetail_call_id` (`call_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=107 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='通话流程表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_call_detail_202610`
--

DROP TABLE IF EXISTS `cc_call_detail_202610`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_call_detail_202610` (
  `id` bigint NOT NULL DEFAULT '0' COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `start_time` bigint NOT NULL DEFAULT '0' COMMENT '开始时间',
  `end_time` bigint NOT NULL DEFAULT '0' COMMENT '结束时间',
  `call_id` bigint NOT NULL DEFAULT '0' COMMENT '通话ID',
  `detail_index` int NOT NULL DEFAULT '1' COMMENT '顺序',
  `transfer_type` int NOT NULL DEFAULT '0' COMMENT '1:进vdn,2:进ivr,3:技能组,4:按键收号,5:外线,6:机器人,10:服务评价',
  `transfer_id` bigint NOT NULL DEFAULT '0' COMMENT '转接ID',
  `reason` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '出队列原因:排队挂机或者转坐席',
  `ext1` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展字段1',
  `ext2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展字段2',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_call_device`
--

DROP TABLE IF EXISTS `cc_call_device`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_call_device` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '租户id',
  `call_id` bigint NOT NULL DEFAULT '0' COMMENT '通话ID',
  `device_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '设备id',
  `agent_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '坐席',
  `agent_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '坐席名称',
  `device_type` int NOT NULL DEFAULT '1' COMMENT '1:坐席,2:客户,3:外线',
  `cdr_type` int NOT NULL DEFAULT '1' COMMENT '1:呼入,2:外呼,3:内呼,4:转接,5:咨询,6:监听,7:强插',
  `from_agent` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '转接或咨询发起者',
  `caller` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '主叫',
  `called` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '被叫',
  `display` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '显号',
  `called_location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '被叫归属地',
  `caller_location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '被叫归属地',
  `call_time` bigint NOT NULL DEFAULT '0' COMMENT '呼叫开始时间',
  `ring_start_time` bigint NOT NULL DEFAULT '0' COMMENT '振铃开始时间',
  `ring_end_time` bigint NOT NULL DEFAULT '0' COMMENT '振铃结束时间',
  `answer_time` bigint NOT NULL DEFAULT '0' COMMENT '接通时间',
  `bridge_time` bigint NOT NULL DEFAULT '0' COMMENT '桥接时间',
  `end_time` bigint NOT NULL DEFAULT '0' COMMENT '结束时间',
  `talk_time` bigint NOT NULL DEFAULT '0' COMMENT '通话时长',
  `record_start_time` bigint NOT NULL DEFAULT '0' COMMENT '录音开始时间',
  `record_time` bigint NOT NULL DEFAULT '0' COMMENT '录音时长',
  `sip_protocol` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '信令协议(tcp/udp)',
  `record` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '录音地址',
  `record2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '备用录音地址',
  `record3` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '备用录音地址',
  `channel_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '呼叫地址',
  `hangup_cause` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '挂机原因',
  `ring_cause` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '回铃音识别',
  `sip_status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'sip状态',
  `ext1` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展字段1',
  `ext2` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展字段2',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_calldetail__call_id` (`call_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=947 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='话单明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_call_device_202605`
--

DROP TABLE IF EXISTS `cc_call_device_202605`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_call_device_202605` (
  `id` bigint NOT NULL DEFAULT '0' COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '租户id',
  `call_id` bigint NOT NULL DEFAULT '0' COMMENT '通话ID',
  `device_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '设备id',
  `agent_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '坐席',
  `agent_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '坐席名称',
  `device_type` int NOT NULL DEFAULT '1' COMMENT '1:坐席,2:客户,3:外线',
  `cdr_type` int NOT NULL DEFAULT '1' COMMENT '1:呼入,2:外呼,3:内呼,4:转接,5:咨询,6:监听,7:强插',
  `from_agent` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '转接或咨询发起者',
  `caller` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '主叫',
  `called` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '被叫',
  `display` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '显号',
  `called_location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '被叫归属地',
  `caller_location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '被叫归属地',
  `call_time` bigint NOT NULL DEFAULT '0' COMMENT '呼叫开始时间',
  `ring_start_time` bigint NOT NULL DEFAULT '0' COMMENT '振铃开始时间',
  `ring_end_time` bigint NOT NULL DEFAULT '0' COMMENT '振铃结束时间',
  `answer_time` bigint NOT NULL DEFAULT '0' COMMENT '接通时间',
  `bridge_time` bigint NOT NULL DEFAULT '0' COMMENT '桥接时间',
  `end_time` bigint NOT NULL DEFAULT '0' COMMENT '结束时间',
  `talk_time` bigint NOT NULL DEFAULT '0' COMMENT '通话时长',
  `record_start_time` bigint NOT NULL DEFAULT '0' COMMENT '录音开始时间',
  `record_time` bigint NOT NULL DEFAULT '0' COMMENT '录音时长',
  `sip_protocol` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '信令协议(tcp/udp)',
  `record` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '录音地址',
  `record2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '备用录音地址',
  `record3` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '备用录音地址',
  `channel_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '呼叫地址',
  `hangup_cause` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '挂机原因',
  `ring_cause` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '回铃音识别',
  `sip_status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'sip状态',
  `ext1` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展字段1',
  `ext2` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展字段2',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_call_device_202606`
--

DROP TABLE IF EXISTS `cc_call_device_202606`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_call_device_202606` (
  `id` bigint NOT NULL DEFAULT '0' COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '租户id',
  `call_id` bigint NOT NULL DEFAULT '0' COMMENT '通话ID',
  `device_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '设备id',
  `agent_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '坐席',
  `agent_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '坐席名称',
  `device_type` int NOT NULL DEFAULT '1' COMMENT '1:坐席,2:客户,3:外线',
  `cdr_type` int NOT NULL DEFAULT '1' COMMENT '1:呼入,2:外呼,3:内呼,4:转接,5:咨询,6:监听,7:强插',
  `from_agent` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '转接或咨询发起者',
  `caller` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '主叫',
  `called` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '被叫',
  `display` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '显号',
  `called_location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '被叫归属地',
  `caller_location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '被叫归属地',
  `call_time` bigint NOT NULL DEFAULT '0' COMMENT '呼叫开始时间',
  `ring_start_time` bigint NOT NULL DEFAULT '0' COMMENT '振铃开始时间',
  `ring_end_time` bigint NOT NULL DEFAULT '0' COMMENT '振铃结束时间',
  `answer_time` bigint NOT NULL DEFAULT '0' COMMENT '接通时间',
  `bridge_time` bigint NOT NULL DEFAULT '0' COMMENT '桥接时间',
  `end_time` bigint NOT NULL DEFAULT '0' COMMENT '结束时间',
  `talk_time` bigint NOT NULL DEFAULT '0' COMMENT '通话时长',
  `record_start_time` bigint NOT NULL DEFAULT '0' COMMENT '录音开始时间',
  `record_time` bigint NOT NULL DEFAULT '0' COMMENT '录音时长',
  `sip_protocol` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '信令协议(tcp/udp)',
  `record` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '录音地址',
  `record2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '备用录音地址',
  `record3` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '备用录音地址',
  `channel_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '呼叫地址',
  `hangup_cause` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '挂机原因',
  `ring_cause` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '回铃音识别',
  `sip_status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'sip状态',
  `ext1` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展字段1',
  `ext2` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展字段2',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_call_device_202607`
--

DROP TABLE IF EXISTS `cc_call_device_202607`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_call_device_202607` (
  `id` bigint NOT NULL DEFAULT '0' COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '租户id',
  `call_id` bigint NOT NULL DEFAULT '0' COMMENT '通话ID',
  `device_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '设备id',
  `agent_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '坐席',
  `agent_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '坐席名称',
  `device_type` int NOT NULL DEFAULT '1' COMMENT '1:坐席,2:客户,3:外线',
  `cdr_type` int NOT NULL DEFAULT '1' COMMENT '1:呼入,2:外呼,3:内呼,4:转接,5:咨询,6:监听,7:强插',
  `from_agent` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '转接或咨询发起者',
  `caller` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '主叫',
  `called` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '被叫',
  `display` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '显号',
  `called_location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '被叫归属地',
  `caller_location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '被叫归属地',
  `call_time` bigint NOT NULL DEFAULT '0' COMMENT '呼叫开始时间',
  `ring_start_time` bigint NOT NULL DEFAULT '0' COMMENT '振铃开始时间',
  `ring_end_time` bigint NOT NULL DEFAULT '0' COMMENT '振铃结束时间',
  `answer_time` bigint NOT NULL DEFAULT '0' COMMENT '接通时间',
  `bridge_time` bigint NOT NULL DEFAULT '0' COMMENT '桥接时间',
  `end_time` bigint NOT NULL DEFAULT '0' COMMENT '结束时间',
  `talk_time` bigint NOT NULL DEFAULT '0' COMMENT '通话时长',
  `record_start_time` bigint NOT NULL DEFAULT '0' COMMENT '录音开始时间',
  `record_time` bigint NOT NULL DEFAULT '0' COMMENT '录音时长',
  `sip_protocol` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '信令协议(tcp/udp)',
  `record` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '录音地址',
  `record2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '备用录音地址',
  `record3` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '备用录音地址',
  `channel_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '呼叫地址',
  `hangup_cause` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '挂机原因',
  `ring_cause` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '回铃音识别',
  `sip_status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'sip状态',
  `ext1` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展字段1',
  `ext2` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展字段2',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_call_device_202608`
--

DROP TABLE IF EXISTS `cc_call_device_202608`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_call_device_202608` (
  `id` bigint NOT NULL DEFAULT '0' COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '租户id',
  `call_id` bigint NOT NULL DEFAULT '0' COMMENT '通话ID',
  `device_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '设备id',
  `agent_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '坐席',
  `agent_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '坐席名称',
  `device_type` int NOT NULL DEFAULT '1' COMMENT '1:坐席,2:客户,3:外线',
  `cdr_type` int NOT NULL DEFAULT '1' COMMENT '1:呼入,2:外呼,3:内呼,4:转接,5:咨询,6:监听,7:强插',
  `from_agent` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '转接或咨询发起者',
  `caller` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '主叫',
  `called` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '被叫',
  `display` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '显号',
  `called_location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '被叫归属地',
  `caller_location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '被叫归属地',
  `call_time` bigint NOT NULL DEFAULT '0' COMMENT '呼叫开始时间',
  `ring_start_time` bigint NOT NULL DEFAULT '0' COMMENT '振铃开始时间',
  `ring_end_time` bigint NOT NULL DEFAULT '0' COMMENT '振铃结束时间',
  `answer_time` bigint NOT NULL DEFAULT '0' COMMENT '接通时间',
  `bridge_time` bigint NOT NULL DEFAULT '0' COMMENT '桥接时间',
  `end_time` bigint NOT NULL DEFAULT '0' COMMENT '结束时间',
  `talk_time` bigint NOT NULL DEFAULT '0' COMMENT '通话时长',
  `record_start_time` bigint NOT NULL DEFAULT '0' COMMENT '录音开始时间',
  `record_time` bigint NOT NULL DEFAULT '0' COMMENT '录音时长',
  `sip_protocol` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '信令协议(tcp/udp)',
  `record` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '录音地址',
  `record2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '备用录音地址',
  `record3` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '备用录音地址',
  `channel_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '呼叫地址',
  `hangup_cause` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '挂机原因',
  `ring_cause` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '回铃音识别',
  `sip_status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'sip状态',
  `ext1` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展字段1',
  `ext2` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展字段2',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_call_device_202609`
--

DROP TABLE IF EXISTS `cc_call_device_202609`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_call_device_202609` (
  `id` bigint NOT NULL DEFAULT '0' COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '租户id',
  `call_id` bigint NOT NULL DEFAULT '0' COMMENT '通话ID',
  `device_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '设备id',
  `agent_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '坐席',
  `agent_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '坐席名称',
  `device_type` int NOT NULL DEFAULT '1' COMMENT '1:坐席,2:客户,3:外线',
  `cdr_type` int NOT NULL DEFAULT '1' COMMENT '1:呼入,2:外呼,3:内呼,4:转接,5:咨询,6:监听,7:强插',
  `from_agent` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '转接或咨询发起者',
  `caller` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '主叫',
  `called` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '被叫',
  `display` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '显号',
  `called_location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '被叫归属地',
  `caller_location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '被叫归属地',
  `call_time` bigint NOT NULL DEFAULT '0' COMMENT '呼叫开始时间',
  `ring_start_time` bigint NOT NULL DEFAULT '0' COMMENT '振铃开始时间',
  `ring_end_time` bigint NOT NULL DEFAULT '0' COMMENT '振铃结束时间',
  `answer_time` bigint NOT NULL DEFAULT '0' COMMENT '接通时间',
  `bridge_time` bigint NOT NULL DEFAULT '0' COMMENT '桥接时间',
  `end_time` bigint NOT NULL DEFAULT '0' COMMENT '结束时间',
  `talk_time` bigint NOT NULL DEFAULT '0' COMMENT '通话时长',
  `record_start_time` bigint NOT NULL DEFAULT '0' COMMENT '录音开始时间',
  `record_time` bigint NOT NULL DEFAULT '0' COMMENT '录音时长',
  `sip_protocol` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '信令协议(tcp/udp)',
  `record` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '录音地址',
  `record2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '备用录音地址',
  `record3` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '备用录音地址',
  `channel_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '呼叫地址',
  `hangup_cause` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '挂机原因',
  `ring_cause` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '回铃音识别',
  `sip_status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'sip状态',
  `ext1` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展字段1',
  `ext2` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展字段2',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_call_device_202610`
--

DROP TABLE IF EXISTS `cc_call_device_202610`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_call_device_202610` (
  `id` bigint NOT NULL DEFAULT '0' COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '租户id',
  `call_id` bigint NOT NULL DEFAULT '0' COMMENT '通话ID',
  `device_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '设备id',
  `agent_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '坐席',
  `agent_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '坐席名称',
  `device_type` int NOT NULL DEFAULT '1' COMMENT '1:坐席,2:客户,3:外线',
  `cdr_type` int NOT NULL DEFAULT '1' COMMENT '1:呼入,2:外呼,3:内呼,4:转接,5:咨询,6:监听,7:强插',
  `from_agent` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '转接或咨询发起者',
  `caller` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '主叫',
  `called` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '被叫',
  `display` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '显号',
  `called_location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '被叫归属地',
  `caller_location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '被叫归属地',
  `call_time` bigint NOT NULL DEFAULT '0' COMMENT '呼叫开始时间',
  `ring_start_time` bigint NOT NULL DEFAULT '0' COMMENT '振铃开始时间',
  `ring_end_time` bigint NOT NULL DEFAULT '0' COMMENT '振铃结束时间',
  `answer_time` bigint NOT NULL DEFAULT '0' COMMENT '接通时间',
  `bridge_time` bigint NOT NULL DEFAULT '0' COMMENT '桥接时间',
  `end_time` bigint NOT NULL DEFAULT '0' COMMENT '结束时间',
  `talk_time` bigint NOT NULL DEFAULT '0' COMMENT '通话时长',
  `record_start_time` bigint NOT NULL DEFAULT '0' COMMENT '录音开始时间',
  `record_time` bigint NOT NULL DEFAULT '0' COMMENT '录音时长',
  `sip_protocol` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '信令协议(tcp/udp)',
  `record` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '录音地址',
  `record2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '备用录音地址',
  `record3` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '备用录音地址',
  `channel_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '呼叫地址',
  `hangup_cause` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '挂机原因',
  `ring_cause` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '回铃音识别',
  `sip_status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'sip状态',
  `ext1` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展字段1',
  `ext2` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展字段2',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_call_device_202611`
--

DROP TABLE IF EXISTS `cc_call_device_202611`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_call_device_202611` (
  `id` bigint NOT NULL DEFAULT '0' COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '租户id',
  `call_id` bigint NOT NULL DEFAULT '0' COMMENT '通话ID',
  `device_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '设备id',
  `agent_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '坐席',
  `agent_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '坐席名称',
  `device_type` int NOT NULL DEFAULT '1' COMMENT '1:坐席,2:客户,3:外线',
  `cdr_type` int NOT NULL DEFAULT '1' COMMENT '1:呼入,2:外呼,3:内呼,4:转接,5:咨询,6:监听,7:强插',
  `from_agent` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '转接或咨询发起者',
  `caller` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '主叫',
  `called` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '被叫',
  `display` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '显号',
  `called_location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '被叫归属地',
  `caller_location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '被叫归属地',
  `call_time` bigint NOT NULL DEFAULT '0' COMMENT '呼叫开始时间',
  `ring_start_time` bigint NOT NULL DEFAULT '0' COMMENT '振铃开始时间',
  `ring_end_time` bigint NOT NULL DEFAULT '0' COMMENT '振铃结束时间',
  `answer_time` bigint NOT NULL DEFAULT '0' COMMENT '接通时间',
  `bridge_time` bigint NOT NULL DEFAULT '0' COMMENT '桥接时间',
  `end_time` bigint NOT NULL DEFAULT '0' COMMENT '结束时间',
  `talk_time` bigint NOT NULL DEFAULT '0' COMMENT '通话时长',
  `record_start_time` bigint NOT NULL DEFAULT '0' COMMENT '录音开始时间',
  `record_time` bigint NOT NULL DEFAULT '0' COMMENT '录音时长',
  `sip_protocol` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '信令协议(tcp/udp)',
  `record` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '录音地址',
  `record2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '备用录音地址',
  `record3` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '备用录音地址',
  `channel_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '呼叫地址',
  `hangup_cause` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '挂机原因',
  `ring_cause` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '回铃音识别',
  `sip_status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'sip状态',
  `ext1` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展字段1',
  `ext2` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展字段2',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_call_device_202612`
--

DROP TABLE IF EXISTS `cc_call_device_202612`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_call_device_202612` (
  `id` bigint NOT NULL DEFAULT '0' COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '租户id',
  `call_id` bigint NOT NULL DEFAULT '0' COMMENT '通话ID',
  `device_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '设备id',
  `agent_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '坐席',
  `agent_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '坐席名称',
  `device_type` int NOT NULL DEFAULT '1' COMMENT '1:坐席,2:客户,3:外线',
  `cdr_type` int NOT NULL DEFAULT '1' COMMENT '1:呼入,2:外呼,3:内呼,4:转接,5:咨询,6:监听,7:强插',
  `from_agent` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '转接或咨询发起者',
  `caller` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '主叫',
  `called` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '被叫',
  `display` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '显号',
  `called_location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '被叫归属地',
  `caller_location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '被叫归属地',
  `call_time` bigint NOT NULL DEFAULT '0' COMMENT '呼叫开始时间',
  `ring_start_time` bigint NOT NULL DEFAULT '0' COMMENT '振铃开始时间',
  `ring_end_time` bigint NOT NULL DEFAULT '0' COMMENT '振铃结束时间',
  `answer_time` bigint NOT NULL DEFAULT '0' COMMENT '接通时间',
  `bridge_time` bigint NOT NULL DEFAULT '0' COMMENT '桥接时间',
  `end_time` bigint NOT NULL DEFAULT '0' COMMENT '结束时间',
  `talk_time` bigint NOT NULL DEFAULT '0' COMMENT '通话时长',
  `record_start_time` bigint NOT NULL DEFAULT '0' COMMENT '录音开始时间',
  `record_time` bigint NOT NULL DEFAULT '0' COMMENT '录音时长',
  `sip_protocol` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '信令协议(tcp/udp)',
  `record` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '录音地址',
  `record2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '备用录音地址',
  `record3` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '备用录音地址',
  `channel_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '呼叫地址',
  `hangup_cause` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '挂机原因',
  `ring_cause` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '回铃音识别',
  `sip_status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'sip状态',
  `ext1` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展字段1',
  `ext2` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展字段2',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_call_dtmf`
--

DROP TABLE IF EXISTS `cc_call_dtmf`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_call_dtmf` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `dtmf_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '按键号码',
  `process_id` bigint NOT NULL DEFAULT '0' COMMENT '业务流程id',
  `call_id` bigint NOT NULL DEFAULT '0' COMMENT '通话标识id',
  `dtmf_time` bigint NOT NULL DEFAULT '0' COMMENT '按键时间',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='呼叫按键表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_call_log`
--

DROP TABLE IF EXISTS `cc_call_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_call_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '落单时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业id',
  `call_id` bigint NOT NULL DEFAULT '0' COMMENT '话单id',
  `caller_display` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '主叫显号',
  `caller` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '主叫',
  `called_display` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '被叫显号',
  `called` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '被叫',
  `number_location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '客户号码归属地',
  `agent_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '坐席',
  `agent_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '坐席名称',
  `group_id` bigint NOT NULL DEFAULT '0' COMMENT '技能组',
  `login_type` int NOT NULL DEFAULT '1' COMMENT '1:sip号,2:webrtc,3:手机',
  `task_id` bigint NOT NULL DEFAULT '0' COMMENT '任务ID',
  `ivr_id` bigint NOT NULL DEFAULT '0' COMMENT 'ivr',
  `bot_id` bigint NOT NULL DEFAULT '0' COMMENT '机器人id',
  `call_time` bigint NOT NULL DEFAULT '0' COMMENT '呼叫开始时间',
  `answer_time` bigint NOT NULL DEFAULT '0' COMMENT '接听时间',
  `end_time` bigint NOT NULL DEFAULT '0' COMMENT '结束时间',
  `call_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '呼叫类型',
  `direction` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '呼叫方向',
  `answer_flag` int NOT NULL DEFAULT '0' COMMENT '通话标识(0:接通,1:坐席未接用户未接,2:坐席接通用户未接通,3:用户接通坐席未接通)',
  `wait_time` bigint NOT NULL DEFAULT '0' COMMENT '累计等待时长',
  `answer_count` int NOT NULL DEFAULT '0' COMMENT '应答设备数',
  `hangup_dir` int NOT NULL DEFAULT '1' COMMENT '挂机方向(1:主叫挂机,2:被叫挂机,3:系统挂机)',
  `sdk_hangup` int NOT NULL DEFAULT '0' COMMENT '是否sdk挂机(1:sdk挂机)',
  `hangup_code` int NOT NULL DEFAULT '0' COMMENT '挂机原因',
  `media_host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '媒体服务器',
  `cti_host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'cti地址',
  `client_host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '客户端地址',
  `record` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '录音地址',
  `record2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '备用录音地址',
  `record3` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '备用录音地址',
  `record_type` int NOT NULL DEFAULT '1' COMMENT '录音状态',
  `record_start_time` bigint NOT NULL DEFAULT '0' COMMENT '录音开始时间',
  `record_time` bigint NOT NULL DEFAULT '0' COMMENT '录音时间',
  `talk_time` bigint NOT NULL DEFAULT '0' COMMENT '通话时长',
  `frist_queue_time` bigint NOT NULL DEFAULT '0' COMMENT '第一次进队列时间',
  `queue_start_time` bigint NOT NULL DEFAULT '0' COMMENT '进队列时间',
  `queue_end_time` bigint NOT NULL DEFAULT '0' COMMENT '出队列时间',
  `month_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '月份',
  `follow_data` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '通话随路数据(2048)',
  `uuid1` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展1',
  `uuid2` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展2',
  `ext1` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展3',
  `ext2` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展4',
  `ext3` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展5',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uniq_calllog_call_id` (`call_id`) USING BTREE,
  KEY `idx_call_log_agent` (`agent_key`) USING BTREE,
  KEY `idx_call_log_group` (`group_id`) USING BTREE,
  KEY `idx_calllog_create_time` (`company_id`,`call_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=467 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='话单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_call_log_202607`
--

DROP TABLE IF EXISTS `cc_call_log_202607`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_call_log_202607` (
  `id` bigint NOT NULL DEFAULT '0',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '落单时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业id',
  `call_id` bigint NOT NULL DEFAULT '0' COMMENT '话单id',
  `caller_display` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '主叫显号',
  `caller` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '主叫',
  `called_display` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '被叫显号',
  `called` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '被叫',
  `number_location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '客户号码归属地',
  `agent_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '坐席',
  `agent_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '坐席名称',
  `group_id` bigint NOT NULL DEFAULT '0' COMMENT '技能组',
  `login_type` int NOT NULL DEFAULT '1' COMMENT '1:sip号,2:webrtc,3:手机',
  `task_id` bigint NOT NULL DEFAULT '0' COMMENT '任务ID',
  `ivr_id` bigint NOT NULL DEFAULT '0' COMMENT 'ivr',
  `bot_id` bigint NOT NULL DEFAULT '0' COMMENT '机器人id',
  `call_time` bigint NOT NULL DEFAULT '0' COMMENT '呼叫开始时间',
  `answer_time` bigint NOT NULL DEFAULT '0' COMMENT '接听时间',
  `end_time` bigint NOT NULL DEFAULT '0' COMMENT '结束时间',
  `call_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '呼叫类型',
  `direction` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '呼叫方向',
  `answer_flag` int NOT NULL DEFAULT '0' COMMENT '通话标识(0:接通,1:坐席未接用户未接,2:坐席接通用户未接通,3:用户接通坐席未接通)',
  `wait_time` bigint NOT NULL DEFAULT '0' COMMENT '累计等待时长',
  `answer_count` int NOT NULL DEFAULT '0' COMMENT '应答设备数',
  `hangup_dir` int NOT NULL DEFAULT '1' COMMENT '挂机方向(1:主叫挂机,2:被叫挂机,3:系统挂机)',
  `sdk_hangup` int NOT NULL DEFAULT '0' COMMENT '是否sdk挂机(1:sdk挂机)',
  `hangup_code` int NOT NULL DEFAULT '0' COMMENT '挂机原因',
  `media_host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '媒体服务器',
  `cti_host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'cti地址',
  `client_host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '客户端地址',
  `record` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '录音地址',
  `record2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '备用录音地址',
  `record3` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '备用录音地址',
  `record_type` int NOT NULL DEFAULT '1' COMMENT '录音状态',
  `record_start_time` bigint NOT NULL DEFAULT '0' COMMENT '录音开始时间',
  `record_time` bigint NOT NULL DEFAULT '0' COMMENT '录音时间',
  `talk_time` bigint NOT NULL DEFAULT '0' COMMENT '通话时长',
  `frist_queue_time` bigint NOT NULL DEFAULT '0' COMMENT '第一次进队列时间',
  `queue_start_time` bigint NOT NULL DEFAULT '0' COMMENT '进队列时间',
  `queue_end_time` bigint NOT NULL DEFAULT '0' COMMENT '出队列时间',
  `month_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '月份',
  `follow_data` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '通话随路数据(2048)',
  `uuid1` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展1',
  `uuid2` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展2',
  `ext1` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展3',
  `ext2` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展4',
  `ext3` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展5',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_call_log_202608`
--

DROP TABLE IF EXISTS `cc_call_log_202608`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_call_log_202608` (
  `id` bigint NOT NULL DEFAULT '0',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '落单时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业id',
  `call_id` bigint NOT NULL DEFAULT '0' COMMENT '话单id',
  `caller_display` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '主叫显号',
  `caller` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '主叫',
  `called_display` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '被叫显号',
  `called` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '被叫',
  `number_location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '客户号码归属地',
  `agent_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '坐席',
  `agent_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '坐席名称',
  `group_id` bigint NOT NULL DEFAULT '0' COMMENT '技能组',
  `login_type` int NOT NULL DEFAULT '1' COMMENT '1:sip号,2:webrtc,3:手机',
  `task_id` bigint NOT NULL DEFAULT '0' COMMENT '任务ID',
  `ivr_id` bigint NOT NULL DEFAULT '0' COMMENT 'ivr',
  `bot_id` bigint NOT NULL DEFAULT '0' COMMENT '机器人id',
  `call_time` bigint NOT NULL DEFAULT '0' COMMENT '呼叫开始时间',
  `answer_time` bigint NOT NULL DEFAULT '0' COMMENT '接听时间',
  `end_time` bigint NOT NULL DEFAULT '0' COMMENT '结束时间',
  `call_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '呼叫类型',
  `direction` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '呼叫方向',
  `answer_flag` int NOT NULL DEFAULT '0' COMMENT '通话标识(0:接通,1:坐席未接用户未接,2:坐席接通用户未接通,3:用户接通坐席未接通)',
  `wait_time` bigint NOT NULL DEFAULT '0' COMMENT '累计等待时长',
  `answer_count` int NOT NULL DEFAULT '0' COMMENT '应答设备数',
  `hangup_dir` int NOT NULL DEFAULT '1' COMMENT '挂机方向(1:主叫挂机,2:被叫挂机,3:系统挂机)',
  `sdk_hangup` int NOT NULL DEFAULT '0' COMMENT '是否sdk挂机(1:sdk挂机)',
  `hangup_code` int NOT NULL DEFAULT '0' COMMENT '挂机原因',
  `media_host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '媒体服务器',
  `cti_host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'cti地址',
  `client_host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '客户端地址',
  `record` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '录音地址',
  `record2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '备用录音地址',
  `record3` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '备用录音地址',
  `record_type` int NOT NULL DEFAULT '1' COMMENT '录音状态',
  `record_start_time` bigint NOT NULL DEFAULT '0' COMMENT '录音开始时间',
  `record_time` bigint NOT NULL DEFAULT '0' COMMENT '录音时间',
  `talk_time` bigint NOT NULL DEFAULT '0' COMMENT '通话时长',
  `frist_queue_time` bigint NOT NULL DEFAULT '0' COMMENT '第一次进队列时间',
  `queue_start_time` bigint NOT NULL DEFAULT '0' COMMENT '进队列时间',
  `queue_end_time` bigint NOT NULL DEFAULT '0' COMMENT '出队列时间',
  `month_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '月份',
  `follow_data` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '通话随路数据(2048)',
  `uuid1` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展1',
  `uuid2` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展2',
  `ext1` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展3',
  `ext2` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展4',
  `ext3` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展5',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_call_log_202609`
--

DROP TABLE IF EXISTS `cc_call_log_202609`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_call_log_202609` (
  `id` bigint NOT NULL DEFAULT '0',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '落单时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业id',
  `call_id` bigint NOT NULL DEFAULT '0' COMMENT '话单id',
  `caller_display` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '主叫显号',
  `caller` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '主叫',
  `called_display` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '被叫显号',
  `called` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '被叫',
  `number_location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '客户号码归属地',
  `agent_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '坐席',
  `agent_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '坐席名称',
  `group_id` bigint NOT NULL DEFAULT '0' COMMENT '技能组',
  `login_type` int NOT NULL DEFAULT '1' COMMENT '1:sip号,2:webrtc,3:手机',
  `task_id` bigint NOT NULL DEFAULT '0' COMMENT '任务ID',
  `ivr_id` bigint NOT NULL DEFAULT '0' COMMENT 'ivr',
  `bot_id` bigint NOT NULL DEFAULT '0' COMMENT '机器人id',
  `call_time` bigint NOT NULL DEFAULT '0' COMMENT '呼叫开始时间',
  `answer_time` bigint NOT NULL DEFAULT '0' COMMENT '接听时间',
  `end_time` bigint NOT NULL DEFAULT '0' COMMENT '结束时间',
  `call_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '呼叫类型',
  `direction` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '呼叫方向',
  `answer_flag` int NOT NULL DEFAULT '0' COMMENT '通话标识(0:接通,1:坐席未接用户未接,2:坐席接通用户未接通,3:用户接通坐席未接通)',
  `wait_time` bigint NOT NULL DEFAULT '0' COMMENT '累计等待时长',
  `answer_count` int NOT NULL DEFAULT '0' COMMENT '应答设备数',
  `hangup_dir` int NOT NULL DEFAULT '1' COMMENT '挂机方向(1:主叫挂机,2:被叫挂机,3:系统挂机)',
  `sdk_hangup` int NOT NULL DEFAULT '0' COMMENT '是否sdk挂机(1:sdk挂机)',
  `hangup_code` int NOT NULL DEFAULT '0' COMMENT '挂机原因',
  `media_host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '媒体服务器',
  `cti_host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'cti地址',
  `client_host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '客户端地址',
  `record` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '录音地址',
  `record2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '备用录音地址',
  `record3` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '备用录音地址',
  `record_type` int NOT NULL DEFAULT '1' COMMENT '录音状态',
  `record_start_time` bigint NOT NULL DEFAULT '0' COMMENT '录音开始时间',
  `record_time` bigint NOT NULL DEFAULT '0' COMMENT '录音时间',
  `talk_time` bigint NOT NULL DEFAULT '0' COMMENT '通话时长',
  `frist_queue_time` bigint NOT NULL DEFAULT '0' COMMENT '第一次进队列时间',
  `queue_start_time` bigint NOT NULL DEFAULT '0' COMMENT '进队列时间',
  `queue_end_time` bigint NOT NULL DEFAULT '0' COMMENT '出队列时间',
  `month_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '月份',
  `follow_data` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '通话随路数据(2048)',
  `uuid1` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展1',
  `uuid2` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展2',
  `ext1` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展3',
  `ext2` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展4',
  `ext3` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展5',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_call_log_202610`
--

DROP TABLE IF EXISTS `cc_call_log_202610`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_call_log_202610` (
  `id` bigint NOT NULL DEFAULT '0',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '落单时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业id',
  `call_id` bigint NOT NULL DEFAULT '0' COMMENT '话单id',
  `caller_display` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '主叫显号',
  `caller` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '主叫',
  `called_display` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '被叫显号',
  `called` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '被叫',
  `number_location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '客户号码归属地',
  `agent_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '坐席',
  `agent_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '坐席名称',
  `group_id` bigint NOT NULL DEFAULT '0' COMMENT '技能组',
  `login_type` int NOT NULL DEFAULT '1' COMMENT '1:sip号,2:webrtc,3:手机',
  `task_id` bigint NOT NULL DEFAULT '0' COMMENT '任务ID',
  `ivr_id` bigint NOT NULL DEFAULT '0' COMMENT 'ivr',
  `bot_id` bigint NOT NULL DEFAULT '0' COMMENT '机器人id',
  `call_time` bigint NOT NULL DEFAULT '0' COMMENT '呼叫开始时间',
  `answer_time` bigint NOT NULL DEFAULT '0' COMMENT '接听时间',
  `end_time` bigint NOT NULL DEFAULT '0' COMMENT '结束时间',
  `call_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '呼叫类型',
  `direction` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '呼叫方向',
  `answer_flag` int NOT NULL DEFAULT '0' COMMENT '通话标识(0:接通,1:坐席未接用户未接,2:坐席接通用户未接通,3:用户接通坐席未接通)',
  `wait_time` bigint NOT NULL DEFAULT '0' COMMENT '累计等待时长',
  `answer_count` int NOT NULL DEFAULT '0' COMMENT '应答设备数',
  `hangup_dir` int NOT NULL DEFAULT '1' COMMENT '挂机方向(1:主叫挂机,2:被叫挂机,3:系统挂机)',
  `sdk_hangup` int NOT NULL DEFAULT '0' COMMENT '是否sdk挂机(1:sdk挂机)',
  `hangup_code` int NOT NULL DEFAULT '0' COMMENT '挂机原因',
  `media_host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '媒体服务器',
  `cti_host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'cti地址',
  `client_host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '客户端地址',
  `record` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '录音地址',
  `record2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '备用录音地址',
  `record3` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '备用录音地址',
  `record_type` int NOT NULL DEFAULT '1' COMMENT '录音状态',
  `record_start_time` bigint NOT NULL DEFAULT '0' COMMENT '录音开始时间',
  `record_time` bigint NOT NULL DEFAULT '0' COMMENT '录音时间',
  `talk_time` bigint NOT NULL DEFAULT '0' COMMENT '通话时长',
  `frist_queue_time` bigint NOT NULL DEFAULT '0' COMMENT '第一次进队列时间',
  `queue_start_time` bigint NOT NULL DEFAULT '0' COMMENT '进队列时间',
  `queue_end_time` bigint NOT NULL DEFAULT '0' COMMENT '出队列时间',
  `month_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '月份',
  `follow_data` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '通话随路数据(2048)',
  `uuid1` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展1',
  `uuid2` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展2',
  `ext1` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展3',
  `ext2` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展4',
  `ext3` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展5',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_call_log_202611`
--

DROP TABLE IF EXISTS `cc_call_log_202611`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_call_log_202611` (
  `id` bigint NOT NULL DEFAULT '0',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '落单时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业id',
  `call_id` bigint NOT NULL DEFAULT '0' COMMENT '话单id',
  `caller_display` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '主叫显号',
  `caller` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '主叫',
  `called_display` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '被叫显号',
  `called` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '被叫',
  `number_location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '客户号码归属地',
  `agent_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '坐席',
  `agent_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '坐席名称',
  `group_id` bigint NOT NULL DEFAULT '0' COMMENT '技能组',
  `login_type` int NOT NULL DEFAULT '1' COMMENT '1:sip号,2:webrtc,3:手机',
  `task_id` bigint NOT NULL DEFAULT '0' COMMENT '任务ID',
  `ivr_id` bigint NOT NULL DEFAULT '0' COMMENT 'ivr',
  `bot_id` bigint NOT NULL DEFAULT '0' COMMENT '机器人id',
  `call_time` bigint NOT NULL DEFAULT '0' COMMENT '呼叫开始时间',
  `answer_time` bigint NOT NULL DEFAULT '0' COMMENT '接听时间',
  `end_time` bigint NOT NULL DEFAULT '0' COMMENT '结束时间',
  `call_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '呼叫类型',
  `direction` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '呼叫方向',
  `answer_flag` int NOT NULL DEFAULT '0' COMMENT '通话标识(0:接通,1:坐席未接用户未接,2:坐席接通用户未接通,3:用户接通坐席未接通)',
  `wait_time` bigint NOT NULL DEFAULT '0' COMMENT '累计等待时长',
  `answer_count` int NOT NULL DEFAULT '0' COMMENT '应答设备数',
  `hangup_dir` int NOT NULL DEFAULT '1' COMMENT '挂机方向(1:主叫挂机,2:被叫挂机,3:系统挂机)',
  `sdk_hangup` int NOT NULL DEFAULT '0' COMMENT '是否sdk挂机(1:sdk挂机)',
  `hangup_code` int NOT NULL DEFAULT '0' COMMENT '挂机原因',
  `media_host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '媒体服务器',
  `cti_host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'cti地址',
  `client_host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '客户端地址',
  `record` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '录音地址',
  `record2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '备用录音地址',
  `record3` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '备用录音地址',
  `record_type` int NOT NULL DEFAULT '1' COMMENT '录音状态',
  `record_start_time` bigint NOT NULL DEFAULT '0' COMMENT '录音开始时间',
  `record_time` bigint NOT NULL DEFAULT '0' COMMENT '录音时间',
  `talk_time` bigint NOT NULL DEFAULT '0' COMMENT '通话时长',
  `frist_queue_time` bigint NOT NULL DEFAULT '0' COMMENT '第一次进队列时间',
  `queue_start_time` bigint NOT NULL DEFAULT '0' COMMENT '进队列时间',
  `queue_end_time` bigint NOT NULL DEFAULT '0' COMMENT '出队列时间',
  `month_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '月份',
  `follow_data` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '通话随路数据(2048)',
  `uuid1` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展1',
  `uuid2` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展2',
  `ext1` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展3',
  `ext2` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展4',
  `ext3` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展5',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_call_log_202612`
--

DROP TABLE IF EXISTS `cc_call_log_202612`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_call_log_202612` (
  `id` bigint NOT NULL DEFAULT '0',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '落单时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业id',
  `call_id` bigint NOT NULL DEFAULT '0' COMMENT '话单id',
  `caller_display` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '主叫显号',
  `caller` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '主叫',
  `called_display` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '被叫显号',
  `called` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '被叫',
  `number_location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '客户号码归属地',
  `agent_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '坐席',
  `agent_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '坐席名称',
  `group_id` bigint NOT NULL DEFAULT '0' COMMENT '技能组',
  `login_type` int NOT NULL DEFAULT '1' COMMENT '1:sip号,2:webrtc,3:手机',
  `task_id` bigint NOT NULL DEFAULT '0' COMMENT '任务ID',
  `ivr_id` bigint NOT NULL DEFAULT '0' COMMENT 'ivr',
  `bot_id` bigint NOT NULL DEFAULT '0' COMMENT '机器人id',
  `call_time` bigint NOT NULL DEFAULT '0' COMMENT '呼叫开始时间',
  `answer_time` bigint NOT NULL DEFAULT '0' COMMENT '接听时间',
  `end_time` bigint NOT NULL DEFAULT '0' COMMENT '结束时间',
  `call_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '呼叫类型',
  `direction` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '呼叫方向',
  `answer_flag` int NOT NULL DEFAULT '0' COMMENT '通话标识(0:接通,1:坐席未接用户未接,2:坐席接通用户未接通,3:用户接通坐席未接通)',
  `wait_time` bigint NOT NULL DEFAULT '0' COMMENT '累计等待时长',
  `answer_count` int NOT NULL DEFAULT '0' COMMENT '应答设备数',
  `hangup_dir` int NOT NULL DEFAULT '1' COMMENT '挂机方向(1:主叫挂机,2:被叫挂机,3:系统挂机)',
  `sdk_hangup` int NOT NULL DEFAULT '0' COMMENT '是否sdk挂机(1:sdk挂机)',
  `hangup_code` int NOT NULL DEFAULT '0' COMMENT '挂机原因',
  `media_host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '媒体服务器',
  `cti_host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'cti地址',
  `client_host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '客户端地址',
  `record` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '录音地址',
  `record2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '备用录音地址',
  `record3` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '备用录音地址',
  `record_type` int NOT NULL DEFAULT '1' COMMENT '录音状态',
  `record_start_time` bigint NOT NULL DEFAULT '0' COMMENT '录音开始时间',
  `record_time` bigint NOT NULL DEFAULT '0' COMMENT '录音时间',
  `talk_time` bigint NOT NULL DEFAULT '0' COMMENT '通话时长',
  `frist_queue_time` bigint NOT NULL DEFAULT '0' COMMENT '第一次进队列时间',
  `queue_start_time` bigint NOT NULL DEFAULT '0' COMMENT '进队列时间',
  `queue_end_time` bigint NOT NULL DEFAULT '0' COMMENT '出队列时间',
  `month_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '月份',
  `follow_data` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '通话随路数据(2048)',
  `uuid1` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展1',
  `uuid2` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展2',
  `ext1` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展3',
  `ext2` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展4',
  `ext3` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展5',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_company`
--

DROP TABLE IF EXISTS `cc_company`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_company` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '名称',
  `id_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '父企业ID',
  `pid` bigint NOT NULL DEFAULT '0' COMMENT '父企业',
  `company_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '简称',
  `gmt` int NOT NULL DEFAULT '0' COMMENT '时区',
  `contact` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '联系人',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '电话',
  `balance` bigint NOT NULL DEFAULT '0' COMMENT '金额',
  `bill_type` int NOT NULL DEFAULT '0' COMMENT '1:呼出计费,2:呼入计费,3:双向计费,0:全免费',
  `pay_type` int NOT NULL DEFAULT '0' COMMENT '0:预付费;1:后付费',
  `hidden_customer` int NOT NULL DEFAULT '0' COMMENT '隐藏客户号码(0:不隐藏;1:隐藏)',
  `secret_type` int NOT NULL DEFAULT '0' COMMENT '坐席密码等级',
  `secret_key` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '验证秘钥',
  `ivr_limit` int NOT NULL DEFAULT '0' COMMENT 'IVR通道数',
  `agent_limit` int NOT NULL DEFAULT '0' COMMENT '开通坐席',
  `agent_size` int DEFAULT NULL COMMENT '企业坐席数量',
  `group_limit` int NOT NULL DEFAULT '0' COMMENT '开通技能组',
  `outbound_limit` int DEFAULT NULL COMMENT '外呼并发限制',
  `expire_time` bigint DEFAULT NULL COMMENT '到期时间(时间戳)',
  `group_agent_limit` int NOT NULL DEFAULT '0' COMMENT '单技能组中坐席上限',
  `record_storage` int NOT NULL DEFAULT '0' COMMENT '录音保留天数',
  `notify_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '话单回调通知',
  `display_number` varchar(255) NOT NULL DEFAULT '' COMMENT '外显号码',
  `oss_product` varchar(64) NOT NULL DEFAULT '' COMMENT '录音OSS产品',
  `record_file_type` varchar(32) NOT NULL DEFAULT '' COMMENT '录音文件类型',
  `record_encrypt` varchar(32) NOT NULL DEFAULT '' COMMENT '录音文件加密',
  `cdr_encrypt_push` varchar(32) NOT NULL DEFAULT '' COMMENT '话单加密推送',
  `ext1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展1',
  `ext2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展2',
  `ext3` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展3',
  `ext4` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展4',
  `ext5` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展5',
  `status` int NOT NULL DEFAULT '0' COMMENT '状态(0:禁用企业,1:免费企业;2:试用企业,3:付费企业)',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `company_unqidx_name` (`name`) USING BTREE,
  UNIQUE KEY `company_unqidx_code` (`company_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='企业信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_company_display`
--

DROP TABLE IF EXISTS `cc_company_display`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_company_display` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '号码池',
  `type` int NOT NULL DEFAULT '0' COMMENT '1:主叫显号,2:被叫显号',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `uni_idx_company_display` (`company_id`,`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='号码池表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_company_phone`
--

DROP TABLE IF EXISTS `cc_company_phone`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_company_phone` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业id',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '号码',
  `type` int NOT NULL DEFAULT '0' COMMENT '1:呼入号码,2:主叫显号,3:被叫显号',
  `status` int NOT NULL DEFAULT '1' COMMENT '1:未启用,2:启用',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uni_idx_companyphone_phone` (`company_id`,`phone`,`type`) USING BTREE,
  KEY `idx_companyhpone_company_id` (`company_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='企业号码';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_company_phone_group`
--

DROP TABLE IF EXISTS `cc_company_phone_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_company_phone_group` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业ID',
  `display_id` bigint NOT NULL DEFAULT '0' COMMENT '号码池id',
  `phone_id` bigint NOT NULL DEFAULT '0' COMMENT '号码id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='技能组号码池';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_company_stat`
--

DROP TABLE IF EXISTS `cc_company_stat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_company_stat` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业id',
  `type` int NOT NULL DEFAULT '1' COMMENT '报表类型(1:坐席报表)',
  `status` int NOT NULL DEFAULT '1' COMMENT '是否企业(1:启用;0:不启用)',
  `stat_time` bigint NOT NULL DEFAULT '0',
  `agent_total` int NOT NULL DEFAULT '0',
  `agent_used` int NOT NULL DEFAULT '0',
  `call_total` int NOT NULL DEFAULT '0',
  `plan_total` int NOT NULL DEFAULT '0',
  `outbound_total` int NOT NULL DEFAULT '0',
  `outbound_answered` int NOT NULL DEFAULT '0',
  `task_outbound` int NOT NULL DEFAULT '0',
  `agent_outbound_answered` int NOT NULL DEFAULT '0',
  `inbound_total` int NOT NULL DEFAULT '0',
  `inbound_assigned` int NOT NULL DEFAULT '0',
  `inbound_answered` int NOT NULL DEFAULT '0',
  `ivr_calls` int NOT NULL DEFAULT '0',
  `abandoned_cnt` int NOT NULL DEFAULT '0',
  `recording_cnt` int NOT NULL DEFAULT '0',
  `recording_size` bigint NOT NULL DEFAULT '0',
  `call_duration` bigint NOT NULL DEFAULT '0',
  `inbound_duration` bigint NOT NULL DEFAULT '0',
  `outbound_duration` bigint NOT NULL DEFAULT '0',
  `queue_cnt` int NOT NULL DEFAULT '0',
  `queue_duration` bigint NOT NULL DEFAULT '0',
  `login_cnt` int NOT NULL DEFAULT '0',
  `login_duration` bigint NOT NULL DEFAULT '0',
  `idle_cnt` int NOT NULL DEFAULT '0',
  `idle_duration` bigint NOT NULL DEFAULT '0',
  `busy_cnt` int NOT NULL DEFAULT '0',
  `busy_duration` bigint NOT NULL DEFAULT '0',
  `after_call_cnt` int NOT NULL DEFAULT '0',
  `after_call_duration` bigint NOT NULL DEFAULT '0',
  `custom_busy_duration` bigint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_com_stat_type` (`company_id`,`type`,`stat_time`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_group`
--

DROP TABLE IF EXISTS `cc_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_group` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '新增时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业ID',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '技能组名称',
  `after_interval` int NOT NULL DEFAULT '5' COMMENT '话后自动空闲时长',
  `caller_display_id` bigint NOT NULL DEFAULT '0' COMMENT '主叫显号号码池',
  `called_display_id` bigint NOT NULL DEFAULT '0' COMMENT '被叫显号号码池',
  `record_type` int NOT NULL DEFAULT '1' COMMENT '1:振铃录音,2:接通录音',
  `level_value` int NOT NULL DEFAULT '1' COMMENT '技能组优先级',
  `tts_engine` bigint NOT NULL DEFAULT '0' COMMENT 'tts引擎id',
  `play_content` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '转坐席时播放内容',
  `evaluate` bigint NOT NULL DEFAULT '0' COMMENT '转服务评价(0:否,1:是)',
  `queue_play` bigint NOT NULL DEFAULT '0' COMMENT '排队音',
  `transfer_play` bigint NOT NULL DEFAULT '0' COMMENT '转接提示音',
  `call_time_out` int NOT NULL DEFAULT '30' COMMENT '外呼呼叫超时时间',
  `group_type` int NOT NULL DEFAULT '0' COMMENT '技能组类型',
  `notify_position` int NOT NULL DEFAULT '0' COMMENT '0:不播放排队位置,1:播放排队位置',
  `notify_rate` int NOT NULL DEFAULT '10' COMMENT '频次',
  `notify_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '您前面还有$位用户在等待',
  `call_memory` int NOT NULL DEFAULT '1' COMMENT '主叫记忆(1:开启,0:不开启)',
  `ext1` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展1',
  `ext2` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展2',
  `ext3` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展3',
  `ext4` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展4',
  `ext5` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '扩展5',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uniq_idx_company_name` (`company_id`,`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=178 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='技能组表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_group_agent_strategy`
--

DROP TABLE IF EXISTS `cc_group_agent_strategy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_group_agent_strategy` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT ' 创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业ID',
  `group_id` bigint NOT NULL DEFAULT '0' COMMENT '技能组id',
  `strategy_type` int NOT NULL DEFAULT '1' COMMENT '1:内置策略,2:自定义',
  `strategy_value` int NOT NULL DEFAULT '1' COMMENT '(1最长空闲时间、2最长平均空闲、3最少应答次数、4最少通话时长、5最长话后时长、6轮选、7随机)',
  `custom_expression` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '自定义表达式',
  `status` int NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='技能组中坐席分配策略';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_group_memory`
--

DROP TABLE IF EXISTS `cc_group_memory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_group_memory` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业ID',
  `agent_key` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '坐席',
  `group_id` bigint NOT NULL DEFAULT '0' COMMENT '技能组ID',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '客户电话',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `unq_idx_group_id` (`group_id`,`phone`,`agent_key`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='坐席与客户记忆表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_group_memory_config`
--

DROP TABLE IF EXISTS `cc_group_memory_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_group_memory_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业ID',
  `group_id` bigint NOT NULL DEFAULT '0' COMMENT '技能组ID',
  `success_strategy` int NOT NULL DEFAULT '0' COMMENT '匹配成功策略',
  `success_strategy_value` bigint NOT NULL DEFAULT '0' COMMENT '匹配成功策略值',
  `fail_strategy` int NOT NULL DEFAULT '0' COMMENT '匹配失败策略',
  `fail_strategy_value` bigint NOT NULL DEFAULT '0' COMMENT '匹配失败策略值',
  `memory_day` int NOT NULL DEFAULT '30' COMMENT '记忆天数',
  `inbound_cover` int NOT NULL DEFAULT '0' COMMENT '呼入覆盖',
  `outbound_cover` int NOT NULL DEFAULT '0' COMMENT '外呼覆盖',
  `status` int NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uni_idx_group` (`group_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='技能组坐席记忆配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_group_overflow`
--

DROP TABLE IF EXISTS `cc_group_overflow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_group_overflow` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `group_id` bigint NOT NULL DEFAULT '0' COMMENT '技能组ID',
  `overflow_id` bigint NOT NULL DEFAULT '0' COMMENT '溢出策略ID',
  `level_value` int NOT NULL DEFAULT '1' COMMENT '优先级',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uni_idx_group_overflow` (`group_id`,`overflow_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='技能组排队策略表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_group_strategy_exp`
--

DROP TABLE IF EXISTS `cc_group_strategy_exp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_group_strategy_exp` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业ID',
  `group_id` bigint NOT NULL DEFAULT '0' COMMENT '技能组id',
  `strategy_key` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '自定义值',
  `strategy_present` int NOT NULL DEFAULT '1' COMMENT '百分百',
  `strategy_type` int NOT NULL DEFAULT '1' COMMENT '类型',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='坐席自定义策略表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_ivr_flow`
--

DROP TABLE IF EXISTS `cc_ivr_flow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_ivr_flow` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cts` bigint NOT NULL DEFAULT '0',
  `call_id` bigint NOT NULL DEFAULT '0',
  `ivr_id` bigint NOT NULL DEFAULT '0',
  `node_id` varchar(64) NOT NULL DEFAULT '',
  `node_type` varchar(32) NOT NULL DEFAULT '',
  `node_name` varchar(128) NOT NULL DEFAULT '',
  `key_press` varchar(16) NOT NULL DEFAULT '',
  `start_time` bigint NOT NULL DEFAULT '0',
  `end_time` bigint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_call_id` (`call_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_ivr_workflow`
--

DROP TABLE IF EXISTS `cc_ivr_workflow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_ivr_workflow` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业id',
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '流程名称',
  `oss_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '流程文件名',
  `init_params` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '用来存贮 ivr 流程启动所需要的参数描述',
  `create_user` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '流程发布人',
  `verify_user` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '流程审核人',
  `content` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci COMMENT '流程内容(ivr)',
  `voice_item` varchar(10000) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '该流程用到的语音文件id，以英文逗号,分隔',
  `type` int NOT NULL DEFAULT '1' COMMENT '1转接，2咨询',
  `status` int NOT NULL COMMENT '流程状态    1：待发布   2：审核中  3：审核未通过  4：审核通过  5：已上线(ivr)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3 COMMENT='ivr流程表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_overflow_config`
--

DROP TABLE IF EXISTS `cc_overflow_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_overflow_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint DEFAULT '0' COMMENT '创建时间',
  `uts` bigint DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint DEFAULT '0' COMMENT '企业id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '名称',
  `handle_type` int NOT NULL DEFAULT '1' COMMENT '1:排队,2:溢出,3:挂机',
  `busy_type` int DEFAULT '1' COMMENT '排队方式(1:先进先出,2:vip,3:自定义)',
  `queue_timeout` int DEFAULT '60' COMMENT '排队超时时间',
  `busy_timeout_type` int DEFAULT '1' COMMENT '排队超时(1:溢出,2:挂机)',
  `overflow_type` int DEFAULT '1' COMMENT '溢出(1:group,2:ivr,3:vdn)',
  `overflow_value` int DEFAULT '0' COMMENT '溢出值',
  `lineup_expression` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '自定义排队表达式',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uni_idx_name` (`company_id`,`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='溢出策略表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_overflow_exp`
--

DROP TABLE IF EXISTS `cc_overflow_exp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_overflow_exp` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业ID',
  `overflow_id` bigint NOT NULL DEFAULT '0' COMMENT '溢出策略ID',
  `exp_key` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '自定义值',
  `rate` int NOT NULL DEFAULT '1' COMMENT '权重',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='自定义溢出策略优先级';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_overflow_front`
--

DROP TABLE IF EXISTS `cc_overflow_front`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_overflow_front` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业ID',
  `overflow_id` bigint NOT NULL DEFAULT '0' COMMENT '策略ID',
  `front_type` int NOT NULL DEFAULT '1' COMMENT '1:队列长度; 2:队列等待最大时长; 3:呼损率',
  `compare_condition` int NOT NULL DEFAULT '0' COMMENT '0:全部; 1:小于或等于; 2:等于; 3:大于或等于; 4:大于',
  `rank_value_start` int NOT NULL DEFAULT '0',
  `rank_value` int NOT NULL DEFAULT '0' COMMENT '符号条件值',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='溢出策略前置条件';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_phone_area`
--

DROP TABLE IF EXISTS `cc_phone_area`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_phone_area` (
  `id` int NOT NULL,
  `cts` bigint DEFAULT NULL COMMENT '创建时间',
  `uts` bigint DEFAULT NULL COMMENT '修改时间',
  `num_prefix` varchar(32) DEFAULT NULL COMMENT '号码前缀',
  `operator` varchar(32) DEFAULT NULL COMMENT '运营商代码',
  `num_type` int DEFAULT NULL COMMENT '号码类型',
  `num_code` varchar(32) DEFAULT NULL COMMENT '区号',
  `num_province` varchar(64) DEFAULT NULL COMMENT '省',
  `num_city` varchar(64) DEFAULT NULL COMMENT '市',
  `status` int DEFAULT '1' COMMENT '状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_platform_license`
--

DROP TABLE IF EXISTS `cc_platform_license`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_platform_license` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `platform_license` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '平台信息',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='LICENSE';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_playback`
--

DROP TABLE IF EXISTS `cc_playback`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_playback` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL COMMENT '企业ID',
  `playback` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '放音文件',
  `status` int NOT NULL DEFAULT '1' COMMENT '1:待审核,2:审核通过',
  `name` varchar(255) DEFAULT NULL,
  `oss_id` varchar(500) DEFAULT NULL,
  `type` int DEFAULT NULL,
  `engine_id` bigint DEFAULT NULL,
  `engine_name` varchar(64) DEFAULT NULL,
  `tts_params` varchar(64) DEFAULT NULL,
  `hash_code` varchar(128) DEFAULT NULL,
  `speech_text` text,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=299 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='语音文件表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_push_log`
--

DROP TABLE IF EXISTS `cc_push_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_push_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业id',
  `call_id` bigint NOT NULL DEFAULT '0' COMMENT 'callid',
  `cdr_notify_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '发送次数',
  `content` varchar(10240) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '推送内容',
  `push_times` int NOT NULL DEFAULT '1' COMMENT '推送次数',
  `push_response` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '推送返回值',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态(1:推送，0:不推送)',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_push_fail_uts` (`uts`)
) ENGINE=InnoDB AUTO_INCREMENT=651 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='话单推送记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_route_call`
--

DROP TABLE IF EXISTS `cc_route_call`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_route_call` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '所属企业',
  `route_group_id` bigint NOT NULL DEFAULT '0' COMMENT '所属路由组',
  `route_num` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '字冠号码',
  `num_max` int NOT NULL DEFAULT '0' COMMENT '最长',
  `num_min` int NOT NULL DEFAULT '0' COMMENT '最短',
  `caller_change` int NOT NULL DEFAULT '0' COMMENT '主叫替换规则',
  `caller_change_num` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '替换号码',
  `called_change` int NOT NULL DEFAULT '0' COMMENT '被叫替换规则',
  `called_change_num` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '替换号码',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `unq_idx_route` (`company_id`,`route_num`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字冠路由表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_route_getway`
--

DROP TABLE IF EXISTS `cc_route_getway`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_route_getway` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '号码',
  `media_host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '媒体地址',
  `media_port` int NOT NULL DEFAULT '0' COMMENT '媒体端口',
  `caller_prefix` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '主叫号码前缀',
  `called_prefix` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '被叫号码前缀',
  `profile` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'fs的context规则',
  `sip_header1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'sip头1',
  `sip_header2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'sip头2',
  `sip_header3` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'sip头3',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uindex_getway_name` (`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='媒体网关表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_route_getway_group`
--

DROP TABLE IF EXISTS `cc_route_getway_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_route_getway_group` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `getway_id` bigint NOT NULL DEFAULT '0' COMMENT '媒体网关',
  `route_group_id` bigint NOT NULL DEFAULT '0' COMMENT '网关组',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='路由字冠关联组表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_route_group`
--

DROP TABLE IF EXISTS `cc_route_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_route_group` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `route_group` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '网关组',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='网关组';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_sip_gateway`
--

DROP TABLE IF EXISTS `cc_sip_gateway`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_sip_gateway` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业id',
  `company_code` varchar(255) DEFAULT '' COMMENT '企业编码',
  `company_name` varchar(255) DEFAULT '' COMMENT '企业名称',
  `username` varchar(255) NOT NULL DEFAULT '' COMMENT '账号',
  `passwd` varchar(255) NOT NULL DEFAULT '' COMMENT '密码',
  `internal` varchar(255) DEFAULT '' COMMENT '网关内网',
  `external` varchar(255) DEFAULT '' COMMENT '网关外网',
  `register_addr` varchar(255) DEFAULT '' COMMENT '注册地址',
  `register_time` int DEFAULT '0' COMMENT '注册时间',
  `expire` int DEFAULT '0' COMMENT '注册周期(秒)',
  `status` int DEFAULT '1' COMMENT '状态(0:删除,1:不在线,2:在线)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='网关注册账号表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_skill`
--

DROP TABLE IF EXISTS `cc_skill`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_skill` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '名称',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '备注',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业id',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uniq_skill_name` (`company_id`,`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='技能表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_skill_agent`
--

DROP TABLE IF EXISTS `cc_skill_agent`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_skill_agent` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业id',
  `skill_id` bigint NOT NULL DEFAULT '0' COMMENT '技能id',
  `agent_id` bigint NOT NULL DEFAULT '0' COMMENT '坐席id',
  `rank_value` int NOT NULL DEFAULT '0' COMMENT '范围',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `id` (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='坐席技能表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_skill_group`
--

DROP TABLE IF EXISTS `cc_skill_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_skill_group` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业ID',
  `level_value` int NOT NULL DEFAULT '1',
  `skill_id` bigint NOT NULL DEFAULT '0' COMMENT '技能ID',
  `group_id` bigint NOT NULL DEFAULT '0' COMMENT '技能组ID',
  `rank_type` int NOT NULL DEFAULT '1' COMMENT '等级类型(1:全部,2:等于,3:>,4:<,5:介于)',
  `rank_value_start` int NOT NULL DEFAULT '0' COMMENT '介于的开始值',
  `rank_value` int NOT NULL DEFAULT '1' COMMENT '等级值',
  `match_type` int NOT NULL DEFAULT '1' COMMENT '匹配规则(1:低到高,2:高到低)',
  `share_value` int NOT NULL DEFAULT '100' COMMENT '占用率',
  `status` int NOT NULL DEFAULT '0' COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_group_id` (`group_id`) USING BTREE,
  KEY `id` (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='技能组技能表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_stat_day_agent`
--

DROP TABLE IF EXISTS `cc_stat_day_agent`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_stat_day_agent` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT 'cts',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业id',
  `agent_key` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '坐席编号',
  `agent_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '坐席名称',
  `stat_time` bigint NOT NULL DEFAULT '0' COMMENT '统计时间',
  `callout_cnt` bigint NOT NULL DEFAULT '0' COMMENT '外呼总量',
  `callout_answer_cnt` bigint NOT NULL DEFAULT '0' COMMENT '外呼接通量',
  `callin_cnt` bigint NOT NULL DEFAULT '0' COMMENT '呼入分配量',
  `callin_answer_cnt` bigint NOT NULL DEFAULT '0' COMMENT '呼入接起量',
  `login_cnt` bigint NOT NULL DEFAULT '0' COMMENT '登录次数',
  `ready_cnt` bigint NOT NULL DEFAULT '0' COMMENT '空闲次数',
  `not_ready_cnt` bigint NOT NULL DEFAULT '0' COMMENT '忙碌次数',
  `after_cnt` bigint NOT NULL DEFAULT '0' COMMENT '话后次数',
  `login_time` bigint NOT NULL DEFAULT '0' COMMENT '登录总时长',
  `ready_time` bigint NOT NULL DEFAULT '0' COMMENT '空闲总时长',
  `not_ready_time` bigint NOT NULL DEFAULT '0' COMMENT '忙碌总时长',
  `busy_time` bigint NOT NULL DEFAULT '0' COMMENT '自定义忙碌总时间',
  `after_time` bigint NOT NULL DEFAULT '0' COMMENT '话后总时长',
  `talk_time` bigint NOT NULL DEFAULT '0' COMMENT '通话总时长',
  `callin_talk_time` bigint NOT NULL DEFAULT '0' COMMENT '呼入通话总时长',
  `callout_talk_time` bigint NOT NULL DEFAULT '0' COMMENT '外呼通话总时长',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态',
  `logout_cnt` bigint NOT NULL DEFAULT '0',
  `talk_cnt` bigint NOT NULL DEFAULT '0',
  `hold_time` bigint NOT NULL DEFAULT '0',
  `hold_cnt` bigint NOT NULL DEFAULT '0',
  `conference_time` bigint NOT NULL DEFAULT '0',
  `conference_cnt` bigint NOT NULL DEFAULT '0',
  `consult_time` bigint NOT NULL DEFAULT '0',
  `consult_cnt` bigint NOT NULL DEFAULT '0',
  `consulted_time` bigint NOT NULL DEFAULT '0',
  `consulted_cnt` bigint NOT NULL DEFAULT '0',
  `transferred_time` bigint NOT NULL DEFAULT '0',
  `transferred_cnt` bigint NOT NULL DEFAULT '0',
  `transfer_cnt` bigint NOT NULL DEFAULT '0',
  `call15s_cnt` bigint NOT NULL DEFAULT '0',
  `rating_very_satisfied` bigint NOT NULL DEFAULT '0',
  `rating_satisfied` bigint NOT NULL DEFAULT '0',
  `rating_normal` bigint NOT NULL DEFAULT '0',
  `rating_dissatisfied` bigint NOT NULL DEFAULT '0',
  `rating_very_dissatisfied` bigint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uniq_idx_state_agenthourwork_agent` (`stat_time`,`agent_key`) USING BTREE,
  FULLTEXT KEY `idx_state_agenthourwork_agent` (`agent_key`)
) ENGINE=InnoDB AUTO_INCREMENT=310 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_stat_hour_agent`
--

DROP TABLE IF EXISTS `cc_stat_hour_agent`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_stat_hour_agent` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT 'cts',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业id',
  `agent_key` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '坐席编号',
  `agent_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '坐席名称',
  `stat_time` bigint NOT NULL DEFAULT '0' COMMENT '统计时间',
  `callout_cnt` bigint NOT NULL DEFAULT '0' COMMENT '外呼总量',
  `callout_answer_cnt` bigint NOT NULL DEFAULT '0' COMMENT '外呼接通量',
  `callin_cnt` bigint NOT NULL DEFAULT '0' COMMENT '呼入分配量',
  `callin_answer_cnt` bigint NOT NULL DEFAULT '0' COMMENT '呼入接起量',
  `login_cnt` bigint NOT NULL DEFAULT '0' COMMENT '登录次数',
  `ready_cnt` bigint NOT NULL DEFAULT '0' COMMENT '空闲次数',
  `not_ready_cnt` bigint NOT NULL DEFAULT '0' COMMENT '忙碌次数',
  `after_cnt` bigint NOT NULL DEFAULT '0' COMMENT '话后次数',
  `login_time` bigint NOT NULL DEFAULT '0' COMMENT '登录总时长',
  `ready_time` bigint NOT NULL DEFAULT '0' COMMENT '空闲总时长',
  `not_ready_time` bigint NOT NULL DEFAULT '0' COMMENT '忙碌总时长',
  `busy_time` bigint NOT NULL DEFAULT '0' COMMENT '自定义忙碌总时间',
  `after_time` bigint NOT NULL DEFAULT '0' COMMENT '话后总时长',
  `talk_time` bigint NOT NULL DEFAULT '0' COMMENT '通话总时长',
  `callin_talk_time` bigint NOT NULL DEFAULT '0' COMMENT '呼入通话总时长',
  `callout_talk_time` bigint NOT NULL DEFAULT '0' COMMENT '外呼通话总时长',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态',
  `logout_cnt` bigint NOT NULL DEFAULT '0',
  `talk_cnt` bigint NOT NULL DEFAULT '0',
  `hold_time` bigint NOT NULL DEFAULT '0',
  `hold_cnt` bigint NOT NULL DEFAULT '0',
  `conference_time` bigint NOT NULL DEFAULT '0',
  `conference_cnt` bigint NOT NULL DEFAULT '0',
  `consult_time` bigint NOT NULL DEFAULT '0',
  `consult_cnt` bigint NOT NULL DEFAULT '0',
  `consulted_time` bigint NOT NULL DEFAULT '0',
  `consulted_cnt` bigint NOT NULL DEFAULT '0',
  `transferred_time` bigint NOT NULL DEFAULT '0',
  `transferred_cnt` bigint NOT NULL DEFAULT '0',
  `transfer_cnt` bigint NOT NULL DEFAULT '0',
  `call15s_cnt` bigint NOT NULL DEFAULT '0',
  `rating_very_satisfied` bigint NOT NULL DEFAULT '0',
  `rating_satisfied` bigint NOT NULL DEFAULT '0',
  `rating_normal` bigint NOT NULL DEFAULT '0',
  `rating_dissatisfied` bigint NOT NULL DEFAULT '0',
  `rating_very_dissatisfied` bigint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uniq_idx_state_agenthourwork_agent` (`stat_time`,`agent_key`) USING BTREE,
  FULLTEXT KEY `idx_state_agenthourwork_agent` (`agent_key`)
) ENGINE=InnoDB AUTO_INCREMENT=971 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_station`
--

DROP TABLE IF EXISTS `cc_station`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_station` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `application_id` int NOT NULL DEFAULT '0' COMMENT '应用id',
  `app_name` varchar(64) DEFAULT NULL,
  `application_type` int NOT NULL DEFAULT '0' COMMENT '服务类型',
  `application_group` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '服务所在的组',
  `application_host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '服务ip地址',
  `application_port` int NOT NULL DEFAULT '0' COMMENT 'http服务端口',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `pwd` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态',
  `ws_host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '软电话 WebSocket SIP 地址',
  `ws_port` int NOT NULL DEFAULT '0' COMMENT '软电话 WebSocket SIP 端口',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='站点类型配置信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_task_config`
--

DROP TABLE IF EXISTS `cc_task_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_task_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业ID',
  `name` varchar(64) NOT NULL DEFAULT '' COMMENT '任务名称',
  `call_start_time` varchar(8) DEFAULT NULL COMMENT '呼叫开始时间',
  `call_end_time` varchar(8) DEFAULT NULL COMMENT '呼叫结束时间',
  `close_time` bigint DEFAULT NULL COMMENT '关闭时间',
  `transfer_type` varchar(32) DEFAULT NULL COMMENT '转接类型',
  `total_count` int DEFAULT '0' COMMENT '总数量',
  `completed_count` int DEFAULT '0' COMMENT '已完成数量',
  `answered_count` int DEFAULT '0' COMMENT '接通数',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态 1:运行 2:暂停 3:关闭',
  `display_number` varchar(32) DEFAULT NULL COMMENT '外显号码',
  `call_rounds` int DEFAULT '1' COMMENT '呼叫轮次',
  `current_round` int DEFAULT '0' COMMENT '当前轮次',
  `data_source` varchar(64) DEFAULT NULL COMMENT '数据源',
  `transfer_name` varchar(64) DEFAULT NULL COMMENT '转接名称',
  `dialed_count` int DEFAULT '0' COMMENT '已拨数',
  `cruise_type` varchar(32) DEFAULT NULL COMMENT '巡航方式',
  `concurrency` int DEFAULT '5' COMMENT '任务并发',
  `number_pool_name` varchar(64) DEFAULT NULL COMMENT '号码池名称',
  `after_call_duration` int DEFAULT '0' COMMENT '话后时长',
  `call_timeout` int DEFAULT '60' COMMENT '外呼超时',
  `status_notify` varchar(64) DEFAULT NULL COMMENT '任务状态通知',
  `traffic_notify` varchar(64) DEFAULT NULL COMMENT '话务通知',
  `list_total` int DEFAULT '0' COMMENT '列表总数',
  `pause_start_time` bigint DEFAULT NULL COMMENT '暂停开始时间',
  `pause_end_time` bigint DEFAULT NULL COMMENT '暂停结束时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='外呼任务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_task_contact`
--

DROP TABLE IF EXISTS `cc_task_contact`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_task_contact` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `task_id` bigint NOT NULL COMMENT '任务ID',
  `name` varchar(64) DEFAULT NULL COMMENT '联系人名称',
  `phone` varchar(32) DEFAULT NULL COMMENT '号码',
  `current_round` int DEFAULT '1' COMMENT '当前轮次',
  `uuid` varchar(64) DEFAULT NULL COMMENT 'UUID',
  `call_time` bigint DEFAULT NULL COMMENT '拨打时间',
  `answer_time` bigint DEFAULT NULL COMMENT '应答时间',
  `call_status` varchar(16) DEFAULT NULL COMMENT '拨打状态',
  `call_result` varchar(16) DEFAULT NULL COMMENT '呼叫结果',
  `agent_code` varchar(32) DEFAULT NULL COMMENT '坐席编号',
  `bridge_time` bigint DEFAULT NULL COMMENT '桥接时间',
  PRIMARY KEY (`id`),
  KEY `idx_task_id` (`task_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='任务联系人';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_task_field`
--

DROP TABLE IF EXISTS `cc_task_field`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_task_field` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cts` bigint NOT NULL DEFAULT '0',
  `uts` bigint NOT NULL DEFAULT '0',
  `company_id` bigint NOT NULL DEFAULT '0',
  `source_id` bigint DEFAULT NULL COMMENT '数据源ID',
  `name` varchar(64) NOT NULL DEFAULT '',
  `is_phone` int DEFAULT '0' COMMENT '号码字段 1:是 0:否',
  `field_type` varchar(32) DEFAULT '自定义字段' COMMENT '字段类型',
  `status` int NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='外呼字段';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_task_pause`
--

DROP TABLE IF EXISTS `cc_task_pause`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_task_pause` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `task_id` bigint NOT NULL COMMENT '任务ID',
  `pause_start_time` bigint DEFAULT NULL COMMENT '暂停开始时间',
  `pause_end_time` bigint DEFAULT NULL COMMENT '暂停结束时间',
  PRIMARY KEY (`id`),
  KEY `idx_task_id` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='任务暂停记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_task_source`
--

DROP TABLE IF EXISTS `cc_task_source`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_task_source` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cts` bigint NOT NULL DEFAULT '0',
  `uts` bigint NOT NULL DEFAULT '0',
  `company_id` bigint NOT NULL DEFAULT '0',
  `name` varchar(64) NOT NULL DEFAULT '',
  `status` int NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='外呼数据源';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_vdn_code`
--

DROP TABLE IF EXISTS `cc_vdn_code`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_vdn_code` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT 'vdn名称',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='呼入路由表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_vdn_config`
--

DROP TABLE IF EXISTS `cc_vdn_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_vdn_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '子码日程',
  `vdn_id` bigint NOT NULL DEFAULT '0' COMMENT 'vdn_id',
  `schedule_id` bigint NOT NULL DEFAULT '0' COMMENT '日程id',
  `route_type` int NOT NULL DEFAULT '1' COMMENT '路由类型(1:技能组,2:放音,3:ivr,4:坐席,5:外呼)',
  `route_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '0' COMMENT '路由类型值',
  `play_type` int NOT NULL DEFAULT '0' COMMENT '放音类型(1:按键导航,2:技能组,3:ivr,4:路由字码,5:挂机)',
  `play_value` bigint NOT NULL DEFAULT '0' COMMENT '放音类型对应值',
  `dtmf_end` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '*' COMMENT '结束音',
  `retry` int NOT NULL DEFAULT '1' COMMENT '重复播放次数',
  `dtmf_max` int NOT NULL DEFAULT '1' COMMENT '最大收键长度',
  `dtmf_min` int NOT NULL DEFAULT '1' COMMENT '最小收键长度',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='路由字码表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_vdn_dtmf`
--

DROP TABLE IF EXISTS `cc_vdn_dtmf`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_vdn_dtmf` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业ID',
  `navigate_id` bigint NOT NULL DEFAULT '0' COMMENT '按键导航ID',
  `dtmf` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '1' COMMENT '按键',
  `route_type` int NOT NULL DEFAULT '0' COMMENT '路由类型(1:技能组,2:IVR,3:路由字码,4:坐席分机,5:挂机)',
  `route_value` bigint NOT NULL DEFAULT '0' COMMENT '路由值',
  `status` int DEFAULT '1' COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='按键导航表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_vdn_phone`
--

DROP TABLE IF EXISTS `cc_vdn_phone`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_vdn_phone` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业ID',
  `vdn_id` bigint NOT NULL DEFAULT '0' COMMENT '路由码',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '特服号',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态',
  `caller_prefix` varchar(32) DEFAULT NULL COMMENT '主叫前缀去除',
  `called_prefix` varchar(32) DEFAULT NULL COMMENT '被叫前缀去除',
  `called_suffix` varchar(32) DEFAULT NULL COMMENT '被叫后缀去除',
  `ringing` varchar(32) DEFAULT NULL COMMENT 'ringing设置',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uni_idx_phone` (`vdn_id`,`company_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='路由号码表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_vdn_schedule`
--

DROP TABLE IF EXISTS `cc_vdn_schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cc_vdn_schedule` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `cts` bigint NOT NULL DEFAULT '0' COMMENT '创建时间',
  `uts` bigint NOT NULL DEFAULT '0' COMMENT '修改时间',
  `company_id` bigint NOT NULL DEFAULT '0' COMMENT '企业ID',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '日程名称',
  `level_value` int NOT NULL DEFAULT '1' COMMENT '优先级',
  `type` int NOT NULL DEFAULT '1' COMMENT '1:指定时间,2:相对时间',
  `start_day` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '开始日期',
  `end_day` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '结束日期',
  `start_time` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '开始时间',
  `end_time` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '结束时间',
  `mon` int NOT NULL DEFAULT '1' COMMENT '周一',
  `tue` int NOT NULL DEFAULT '1' COMMENT '周二',
  `wed` int NOT NULL DEFAULT '1' COMMENT '周三',
  `thu` int NOT NULL DEFAULT '1' COMMENT '周四',
  `fri` int NOT NULL DEFAULT '1' COMMENT '周五',
  `sat` int NOT NULL DEFAULT '1' COMMENT '周六',
  `sun` int NOT NULL DEFAULT '1' COMMENT '周天',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态',
  `work_type` varchar(32) DEFAULT NULL COMMENT '时间类型(上班/下班/周末)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='日程表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `QRTZ_BLOB_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_BLOB_TRIGGERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_BLOB_TRIGGERS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `BLOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `SCHED_NAME` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `QRTZ_CALENDARS`
--

DROP TABLE IF EXISTS `QRTZ_CALENDARS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_CALENDARS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `CALENDAR_NAME` varchar(200) NOT NULL,
  `CALENDAR` blob NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`CALENDAR_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `QRTZ_CRON_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_CRON_TRIGGERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_CRON_TRIGGERS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `CRON_EXPRESSION` varchar(120) NOT NULL,
  `TIME_ZONE_ID` varchar(80) DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `QRTZ_FIRED_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_FIRED_TRIGGERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_FIRED_TRIGGERS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `ENTRY_ID` varchar(95) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `INSTANCE_NAME` varchar(200) NOT NULL,
  `FIRED_TIME` bigint NOT NULL,
  `SCHED_TIME` bigint NOT NULL,
  `PRIORITY` int NOT NULL,
  `STATE` varchar(16) NOT NULL,
  `JOB_NAME` varchar(200) DEFAULT NULL,
  `JOB_GROUP` varchar(200) DEFAULT NULL,
  `IS_NONCONCURRENT` varchar(1) DEFAULT NULL,
  `REQUESTS_RECOVERY` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`ENTRY_ID`),
  KEY `IDX_QRTZ_FT_TRIG_INST_NAME` (`SCHED_NAME`,`INSTANCE_NAME`),
  KEY `IDX_QRTZ_FT_INST_JOB_REQ_RCVRY` (`SCHED_NAME`,`INSTANCE_NAME`,`REQUESTS_RECOVERY`),
  KEY `IDX_QRTZ_FT_J_G` (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_FT_JG` (`SCHED_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_FT_T_G` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_QRTZ_FT_TG` (`SCHED_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `QRTZ_JOB_DETAILS`
--

DROP TABLE IF EXISTS `QRTZ_JOB_DETAILS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_JOB_DETAILS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `JOB_NAME` varchar(200) NOT NULL,
  `JOB_GROUP` varchar(200) NOT NULL,
  `DESCRIPTION` varchar(250) DEFAULT NULL,
  `JOB_CLASS_NAME` varchar(250) NOT NULL,
  `IS_DURABLE` varchar(1) NOT NULL,
  `IS_NONCONCURRENT` varchar(1) NOT NULL,
  `IS_UPDATE_DATA` varchar(1) NOT NULL,
  `REQUESTS_RECOVERY` varchar(1) NOT NULL,
  `JOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_J_REQ_RECOVERY` (`SCHED_NAME`,`REQUESTS_RECOVERY`),
  KEY `IDX_QRTZ_J_GRP` (`SCHED_NAME`,`JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `QRTZ_LOCKS`
--

DROP TABLE IF EXISTS `QRTZ_LOCKS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_LOCKS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `LOCK_NAME` varchar(40) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`LOCK_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `QRTZ_PAUSED_TRIGGER_GRPS`
--

DROP TABLE IF EXISTS `QRTZ_PAUSED_TRIGGER_GRPS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_PAUSED_TRIGGER_GRPS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `QRTZ_SCHEDULER_STATE`
--

DROP TABLE IF EXISTS `QRTZ_SCHEDULER_STATE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_SCHEDULER_STATE` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `INSTANCE_NAME` varchar(200) NOT NULL,
  `LAST_CHECKIN_TIME` bigint NOT NULL,
  `CHECKIN_INTERVAL` bigint NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`INSTANCE_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `QRTZ_SIMPLE_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_SIMPLE_TRIGGERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_SIMPLE_TRIGGERS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `REPEAT_COUNT` bigint NOT NULL,
  `REPEAT_INTERVAL` bigint NOT NULL,
  `TIMES_TRIGGERED` bigint NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `QRTZ_SIMPROP_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_SIMPROP_TRIGGERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_SIMPROP_TRIGGERS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `STR_PROP_1` varchar(512) DEFAULT NULL,
  `STR_PROP_2` varchar(512) DEFAULT NULL,
  `STR_PROP_3` varchar(512) DEFAULT NULL,
  `INT_PROP_1` int DEFAULT NULL,
  `INT_PROP_2` int DEFAULT NULL,
  `LONG_PROP_1` bigint DEFAULT NULL,
  `LONG_PROP_2` bigint DEFAULT NULL,
  `DEC_PROP_1` decimal(13,4) DEFAULT NULL,
  `DEC_PROP_2` decimal(13,4) DEFAULT NULL,
  `BOOL_PROP_1` varchar(1) DEFAULT NULL,
  `BOOL_PROP_2` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `QRTZ_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_TRIGGERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `QRTZ_TRIGGERS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `JOB_NAME` varchar(200) NOT NULL,
  `JOB_GROUP` varchar(200) NOT NULL,
  `DESCRIPTION` varchar(250) DEFAULT NULL,
  `NEXT_FIRE_TIME` bigint DEFAULT NULL,
  `PREV_FIRE_TIME` bigint DEFAULT NULL,
  `PRIORITY` int DEFAULT NULL,
  `TRIGGER_STATE` varchar(16) NOT NULL,
  `TRIGGER_TYPE` varchar(8) NOT NULL,
  `START_TIME` bigint NOT NULL,
  `END_TIME` bigint DEFAULT NULL,
  `CALENDAR_NAME` varchar(200) DEFAULT NULL,
  `MISFIRE_INSTR` smallint DEFAULT NULL,
  `JOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_QRTZ_T_J` (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_T_JG` (`SCHED_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_T_C` (`SCHED_NAME`,`CALENDAR_NAME`),
  KEY `IDX_QRTZ_T_G` (`SCHED_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_QRTZ_T_STATE` (`SCHED_NAME`,`TRIGGER_STATE`),
  KEY `IDX_QRTZ_T_N_STATE` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
  KEY `IDX_QRTZ_T_N_G_STATE` (`SCHED_NAME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
  KEY `IDX_QRTZ_T_NEXT_FIRE_TIME` (`SCHED_NAME`,`NEXT_FIRE_TIME`),
  KEY `IDX_QRTZ_T_NFT_ST` (`SCHED_NAME`,`TRIGGER_STATE`,`NEXT_FIRE_TIME`),
  KEY `IDX_QRTZ_T_NFT_MISFIRE` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`),
  KEY `IDX_QRTZ_T_NFT_ST_MISFIRE` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`,`TRIGGER_STATE`),
  KEY `IDX_QRTZ_T_NFT_ST_MISFIRE_GRP` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`,`TRIGGER_GROUP`,`TRIGGER_STATE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping events for database 'voxai'
--

--
-- Dumping routines for database 'voxai'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-27  8:48:13
