-- MySQL dump 10.13  Distrib 8.0.46, for Linux (aarch64)
--
-- Host: localhost    Database: entrepreneurship_db
-- ------------------------------------------------------
-- Server version	8.0.46

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
-- Table structure for table `blockchain_record`
--

DROP TABLE IF EXISTS `blockchain_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `blockchain_record` (
  `id` int NOT NULL AUTO_INCREMENT,
  `project_id` int NOT NULL,
  `tx_hash` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `record_type` enum('rating','investment','milestone') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `data` json DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_blockchain_project` (`project_id`),
  CONSTRAINT `fk_blockchain_project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `blockchain_record`
--

LOCK TABLES `blockchain_record` WRITE;
/*!40000 ALTER TABLE `blockchain_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `blockchain_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `consultation`
--

DROP TABLE IF EXISTS `consultation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `consultation` (
  `id` int NOT NULL AUTO_INCREMENT,
  `student_id` int NOT NULL,
  `mentor_id` int NOT NULL,
  `project_id` int DEFAULT NULL,
  `topic` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `content` text COLLATE utf8mb4_unicode_ci,
  `status` enum('pending','accepted','completed','cancelled') COLLATE utf8mb4_unicode_ci DEFAULT 'pending',
  `scheduled_time` datetime DEFAULT NULL,
  `notes` text COLLATE utf8mb4_unicode_ci,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_consultation_student` (`student_id`),
  KEY `fk_consultation_mentor` (`mentor_id`),
  KEY `fk_consultation_project` (`project_id`),
  CONSTRAINT `fk_consultation_mentor` FOREIGN KEY (`mentor_id`) REFERENCES `mentor_info` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_consultation_project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_consultation_student` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=104 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `consultation`
--

LOCK TABLES `consultation` WRITE;
/*!40000 ALTER TABLE `consultation` DISABLE KEYS */;
INSERT INTO `consultation` VALUES (1,2,1,101,'商业模式评估','请导师帮忙评估项目商业模式和落地路径。','pending',NULL,NULL,'2026-05-20 09:00:00'),(101,5,1,104,'平台冷启动策略','想咨询创业平台前期如何吸引第一批导师和项目入驻。','completed','2026-05-21 15:00:00','建议先绑定校内双创中心，设计导师任务和项目曝光激励。','2026-05-19 16:20:00'),(102,5,1,105,'硬件成本控制','实验室安全巡检项目如何控制传感器和边缘计算盒子的成本？','pending',NULL,NULL,'2026-05-22 10:10:00'),(103,2,1,102,'AI产品合规咨询','AI就业辅导助手在简历数据处理上有哪些合规注意点？','completed','2026-05-23 14:30:00','需要明确数据授权、删除机制，并避免输出歧视性岗位建议。','2026-05-22 18:00:00');
/*!40000 ALTER TABLE `consultation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `feedback`
--

DROP TABLE IF EXISTS `feedback`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feedback` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `title` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `content` text COLLATE utf8mb4_unicode_ci,
  `status` enum('pending','processing','resolved') COLLATE utf8mb4_unicode_ci DEFAULT 'pending',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_feedback_user` (`user_id`),
  CONSTRAINT `fk_feedback_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=104 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feedback`
--

LOCK TABLES `feedback` WRITE;
/*!40000 ALTER TABLE `feedback` DISABLE KEYS */;
INSERT INTO `feedback` VALUES (101,5,'功能建议','希望消息中心显示对方姓名','消息列表只显示用户ID时不便于确认沟通对象。','pending','2026-05-22 11:00:00'),(102,2,'体验反馈','项目审核状态希望更明显','我的项目中希望能直接看到审核状态和驳回原因。','processing','2026-05-21 10:00:00'),(103,4,'数据问题','投资记录需要关联项目名称','投资者工作台的投资记录应展示项目名称和状态。','resolved','2026-05-20 10:00:00');
/*!40000 ALTER TABLE `feedback` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `investment`
--

DROP TABLE IF EXISTS `investment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `investment` (
  `id` int NOT NULL AUTO_INCREMENT,
  `investor_id` int NOT NULL,
  `project_id` int NOT NULL,
  `amount` decimal(15,2) DEFAULT NULL,
  `status` enum('pending','confirmed','completed','cancelled') COLLATE utf8mb4_unicode_ci DEFAULT 'pending',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_investment_investor` (`investor_id`),
  KEY `fk_investment_project` (`project_id`),
  CONSTRAINT `fk_investment_investor` FOREIGN KEY (`investor_id`) REFERENCES `investor_info` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_investment_project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=105 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `investment`
--

LOCK TABLES `investment` WRITE;
/*!40000 ALTER TABLE `investment` DISABLE KEYS */;
INSERT INTO `investment` VALUES (101,1,101,50.00,'confirmed','2026-05-17 11:00:00'),(102,1,104,30.00,'pending','2026-05-22 16:00:00'),(103,4,104,20.00,'confirmed','2026-05-24 10:20:00'),(104,1,103,10.00,'cancelled','2026-05-12 12:00:00');
/*!40000 ALTER TABLE `investment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `investor_info`
--

DROP TABLE IF EXISTS `investor_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `investor_info` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `company` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `investment_field` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `budget` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_investor_user` (`user_id`),
  CONSTRAINT `fk_investor_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `investor_info`
--

LOCK TABLES `investor_info` WRITE;
/*!40000 ALTER TABLE `investor_info` DISABLE KEYS */;
INSERT INTO `investor_info` VALUES (1,4,'青创资本','人工智能,教育科技,环保科技','100万-500万','2026-05-23 00:49:20'),(4,14,'未来种子基金','校园服务,智能硬件,文化创意','50万-200万','2026-05-25 10:18:19');
/*!40000 ALTER TABLE `investor_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mentor_info`
--

DROP TABLE IF EXISTS `mentor_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mentor_info` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `expertise` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `introduction` text COLLATE utf8mb4_unicode_ci,
  `availability` text COLLATE utf8mb4_unicode_ci,
  `rating` decimal(3,2) DEFAULT '0.00',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_mentor_user` (`user_id`),
  CONSTRAINT `fk_mentor_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mentor_info`
--

LOCK TABLES `mentor_info` WRITE;
/*!40000 ALTER TABLE `mentor_info` DISABLE KEYS */;
INSERT INTO `mentor_info` VALUES (1,3,'人工智能,商业模式,项目孵化','资深创业导师，长期辅导大学生创新创业项目。','周一至周五 14:00-18:00',4.90,'2026-05-23 00:49:20');
/*!40000 ALTER TABLE `mentor_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `message`
--

DROP TABLE IF EXISTS `message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `message` (
  `id` int NOT NULL AUTO_INCREMENT,
  `sender_id` int NOT NULL,
  `receiver_id` int NOT NULL,
  `content` text COLLATE utf8mb4_unicode_ci,
  `msg_type` enum('text','system') COLLATE utf8mb4_unicode_ci DEFAULT 'text',
  `is_read` tinyint DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_message_sender` (`sender_id`),
  KEY `fk_message_receiver` (`receiver_id`),
  CONSTRAINT `fk_message_receiver` FOREIGN KEY (`receiver_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_message_sender` FOREIGN KEY (`sender_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=109 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `message`
--

LOCK TABLES `message` WRITE;
/*!40000 ALTER TABLE `message` DISABLE KEYS */;
INSERT INTO `message` VALUES (101,1,5,'你的项目“大学生创业资源匹配平台”已通过审核，可进入公开项目库展示。','system',0,'2026-05-16 12:10:00'),(102,3,5,'关于平台冷启动策略，我已经给出回复，建议先从校内双创中心切入。','text',0,'2026-05-21 16:00:00'),(103,5,3,'导师您好，我想继续咨询实验室安全巡检项目的硬件成本控制。','text',1,'2026-05-22 10:15:00'),(104,4,5,'我们对你的创业资源匹配平台感兴趣，希望进一步了解团队情况。','text',0,'2026-05-23 09:30:00'),(105,1,3,'本周新增两条创业者咨询，请及时处理。','system',0,'2026-05-22 09:00:00'),(106,5,1,'请问项目审核通过后如何报名路演？','text',1,'2026-05-20 13:00:00'),(107,1,4,'已有两个已审核项目进入推荐项目池，可在投资者工作台查看。','system',0,'2026-05-22 18:00:00'),(108,1,14,'投资者账号已创建，可完善机构资料并浏览推荐项目。','system',0,'2026-05-25 10:20:00');
/*!40000 ALTER TABLE `message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification`
--

DROP TABLE IF EXISTS `notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `title` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `content` text COLLATE utf8mb4_unicode_ci,
  `type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_read` tinyint DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_notification_user` (`user_id`),
  CONSTRAINT `fk_notification_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification`
--

LOCK TABLES `notification` WRITE;
/*!40000 ALTER TABLE `notification` DISABLE KEYS */;
/*!40000 ALTER TABLE `notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `project`
--

DROP TABLE IF EXISTS `project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `project` (
  `id` int NOT NULL AUTO_INCREMENT,
  `student_id` int NOT NULL,
  `title` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `business_plan` text COLLATE utf8mb4_unicode_ci,
  `team_info` text COLLATE utf8mb4_unicode_ci,
  `category` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `funding_target` decimal(15,2) DEFAULT NULL,
  `team_size` int DEFAULT NULL,
  `status` enum('pending','approved','rejected','closed') COLLATE utf8mb4_unicode_ci DEFAULT 'pending',
  `views` int DEFAULT '0',
  `rating` decimal(3,2) DEFAULT '0.00',
  `trust_score` int DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_project_student` (`student_id`),
  CONSTRAINT `fk_project_student` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=106 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `project`
--

LOCK TABLES `project` WRITE;
/*!40000 ALTER TABLE `project` DISABLE KEYS */;
INSERT INTO `project` VALUES (101,2,'智慧校园低碳管理平台','通过用能数据采集和行为激励，帮助高校降低宿舍与教学楼能耗。','先从校园试点切入，提供设备接入、数据看板和节能积分服务。','团队包含物联网、后端、运营和校园推广成员。','环保科技',120.00,5,'approved',86,4.80,92,'2026-05-01 09:30:00','2026-05-02 10:00:00'),(102,2,'AI就业辅导助手','面向大学生提供简历诊断、模拟面试和岗位匹配。','采用SaaS订阅加高校就业中心合作模式。','团队由NLP算法、前端产品和就业指导老师组成。','人工智能',80.00,4,'pending',42,4.30,78,'2026-05-08 15:20:00','2026-05-08 15:20:00'),(103,2,'校园二手循环小站','聚合毕业季闲置物品流转、公益捐赠与社群交易。','以校园社群切入，收取认证服务和增值推广费用。','团队负责运营、设计、前端和线下履约。','电子商务',30.00,3,'rejected',18,3.60,61,'2026-05-10 11:10:00','2026-05-11 11:00:00'),(104,5,'大学生创业资源匹配平台','为创业者匹配导师、投资人、培训活动和路演机会。','以项目库为核心，后续拓展高校双创管理服务。','测试账号项目团队，负责产品、开发、运营。','教育科技',60.00,6,'approved',73,4.70,88,'2026-05-15 10:15:00','2026-05-16 12:00:00'),(105,5,'智能实验室安全巡检','使用视觉识别和传感器监控实验室安全风险。','硬件盒子结合订阅式云端预警服务。','团队包含嵌入式、AI视觉和校园安全顾问。','人工智能',150.00,5,'pending',27,4.10,74,'2026-05-18 14:30:00','2026-05-18 14:30:00');
/*!40000 ALTER TABLE `project` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `project_rating`
--

DROP TABLE IF EXISTS `project_rating`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `project_rating` (
  `id` int NOT NULL AUTO_INCREMENT,
  `project_id` int NOT NULL,
  `user_id` int NOT NULL,
  `rating` tinyint DEFAULT NULL,
  `comment` text COLLATE utf8mb4_unicode_ci,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_rating_project` (`project_id`),
  KEY `fk_rating_user` (`user_id`),
  CONSTRAINT `fk_rating_project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_rating_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `project_rating`
--

LOCK TABLES `project_rating` WRITE;
/*!40000 ALTER TABLE `project_rating` DISABLE KEYS */;
/*!40000 ALTER TABLE `project_rating` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roadshow`
--

DROP TABLE IF EXISTS `roadshow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roadshow` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `organizer_id` int NOT NULL,
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `location` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` enum('upcoming','ongoing','completed') COLLATE utf8mb4_unicode_ci DEFAULT 'upcoming',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_roadshow_organizer` (`organizer_id`),
  CONSTRAINT `fk_roadshow_organizer` FOREIGN KEY (`organizer_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=104 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roadshow`
--

LOCK TABLES `roadshow` WRITE;
/*!40000 ALTER TABLE `roadshow` DISABLE KEYS */;
INSERT INTO `roadshow` VALUES (101,'五月大学生创业项目路演','面向校内外投资人展示优质大学生创业项目。',1,'2026-05-30 14:00:00','2026-05-30 17:30:00','大学科技园路演厅','upcoming','2026-05-16 10:00:00'),(102,'AI与教育科技专题路演','聚焦AI应用、教育科技和校园服务方向。',1,'2026-05-25 15:00:00','2026-05-25 18:00:00','线上直播间','ongoing','2026-05-20 10:00:00'),(103,'春季优秀项目复盘会','复盘已完成路演项目的融资反馈与落地进展。',1,'2026-05-10 09:30:00','2026-05-10 11:30:00','创新创业学院报告厅','completed','2026-05-02 10:00:00');
/*!40000 ALTER TABLE `roadshow` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roadshow_project`
--

DROP TABLE IF EXISTS `roadshow_project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roadshow_project` (
  `id` int NOT NULL AUTO_INCREMENT,
  `roadshow_id` int NOT NULL,
  `project_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_roadshow_project_roadshow` (`roadshow_id`),
  KEY `fk_roadshow_project_project` (`project_id`),
  CONSTRAINT `fk_roadshow_project_project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_roadshow_project_roadshow` FOREIGN KEY (`roadshow_id`) REFERENCES `roadshow` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=105 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roadshow_project`
--

LOCK TABLES `roadshow_project` WRITE;
/*!40000 ALTER TABLE `roadshow_project` DISABLE KEYS */;
INSERT INTO `roadshow_project` VALUES (101,101,101),(102,101,104),(103,102,104),(104,103,101);
/*!40000 ALTER TABLE `roadshow_project` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `training`
--

DROP TABLE IF EXISTS `training`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `training` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `mentor_id` int DEFAULT NULL,
  `type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `location` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `max_participants` int DEFAULT '50',
  `current_participants` int DEFAULT '0',
  `status` enum('upcoming','ongoing','completed') COLLATE utf8mb4_unicode_ci DEFAULT 'upcoming',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_training_mentor` (`mentor_id`),
  CONSTRAINT `fk_training_mentor` FOREIGN KEY (`mentor_id`) REFERENCES `mentor_info` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=104 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `training`
--

LOCK TABLES `training` WRITE;
/*!40000 ALTER TABLE `training` DISABLE KEYS */;
INSERT INTO `training` VALUES (101,'商业计划书打磨工作坊','围绕市场验证、财务测算和融资表达，帮助团队完善BP。',1,'创业指导','2026-05-28 14:00:00','2026-05-28 17:00:00','创新创业学院 A201',50,2,'upcoming','2026-05-18 09:00:00'),(102,'AI项目从原型到MVP','拆解AI应用从需求、数据、模型到产品验证的完整路径。',1,'技术产品','2026-05-25 09:00:00','2026-05-25 12:00:00','线上会议室',80,1,'ongoing','2026-05-19 09:00:00'),(103,'路演表达与投资人沟通','训练三分钟路演结构、问答应对和融资条款基础。',1,'融资路演','2026-05-12 19:00:00','2026-05-12 21:00:00','大学生活动中心 B101',60,2,'completed','2026-05-01 09:00:00');
/*!40000 ALTER TABLE `training` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `training_registration`
--

DROP TABLE IF EXISTS `training_registration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `training_registration` (
  `id` int NOT NULL AUTO_INCREMENT,
  `training_id` int NOT NULL,
  `user_id` int NOT NULL,
  `status` enum('registered','attended','cancelled') COLLATE utf8mb4_unicode_ci DEFAULT 'registered',
  `register_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_registration_training` (`training_id`),
  KEY `fk_registration_user` (`user_id`),
  CONSTRAINT `fk_registration_training` FOREIGN KEY (`training_id`) REFERENCES `training` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_registration_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=105 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `training_registration`
--

LOCK TABLES `training_registration` WRITE;
/*!40000 ALTER TABLE `training_registration` DISABLE KEYS */;
INSERT INTO `training_registration` VALUES (101,101,5,'registered','2026-05-20 10:00:00'),(102,101,2,'registered','2026-05-20 10:05:00'),(103,102,5,'registered','2026-05-23 12:20:00'),(104,103,2,'attended','2026-05-05 16:20:00');
/*!40000 ALTER TABLE `training_registration` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'MD5åŠ å¯†',
  `role` enum('student','mentor','investor','admin') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'student',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `avatar` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` tinyint DEFAULT '1',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin','0192023a7bbd73250516f069df18b500','admin','系统管理员',NULL,NULL,NULL,1,'2026-05-22 15:53:01',NULL),(2,'student','e10adc3949ba59abbe56e057f20f883e','student','学生演示账号','student@example.com',NULL,NULL,1,'2026-05-23 00:49:06',NULL),(3,'mentor','e10adc3949ba59abbe56e057f20f883e','mentor','导师演示账号','mentor@example.com','',NULL,1,'2026-05-23 00:49:06','2026-05-24 23:34:05'),(4,'investor','e10adc3949ba59abbe56e057f20f883e','investor','投资者演示账号','investor@example.com',NULL,NULL,1,'2026-05-23 00:49:06',NULL),(5,'test','e10adc3949ba59abbe56e057f20f883e','student','测试创业者','test@example.com',NULL,NULL,1,'2026-05-24 23:06:58','2026-05-24 23:06:58'),(9,'daoshi','e10adc3949ba59abbe56e057f20f883e','student','daoshi','daoshi@qq.com',NULL,NULL,1,'2026-05-25 09:24:19','2026-05-25 09:24:19'),(14,'touzi','e10adc3949ba59abbe56e057f20f883e','investor','投资者测试账号','touzi@example.com',NULL,NULL,1,'2026-05-25 10:18:19','2026-05-25 10:18:19');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-25  2:22:43
