-- MySQL dump 10.13  Distrib 5.6.19, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: XL_db
-- ------------------------------------------------------
-- Server version	5.6.19-0ubuntu0.14.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `QRcode`
--

DROP TABLE IF EXISTS `QRcode`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `QRcode` (
  `qid` int(10) NOT NULL AUTO_INCREMENT,
  `uid` int(10) NOT NULL,
  `content_info` varchar(50) DEFAULT NULL,
  `expir_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `authority` int(4) DEFAULT NULL,
  PRIMARY KEY (`qid`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRcode`
--

LOCK TABLES `QRcode` WRITE;
/*!40000 ALTER TABLE `QRcode` DISABLE KEYS */;
INSERT INTO `QRcode` VALUES (1,1,'2','2015-07-28 06:47:32',0),(2,1,NULL,'2015-08-12 09:46:57',0);
/*!40000 ALTER TABLE `QRcode` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `UserContact`
--

DROP TABLE IF EXISTS `UserContact`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `UserContact` (
  `cid` int(10) NOT NULL AUTO_INCREMENT,
  `uid` int(10) DEFAULT NULL,
  `type` int(5) DEFAULT NULL,
  `content` varchar(512) DEFAULT NULL,
  PRIMARY KEY (`cid`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `UserContact`
--

LOCK TABLES `UserContact` WRITE;
/*!40000 ALTER TABLE `UserContact` DISABLE KEYS */;
INSERT INTO `UserContact` VALUES (1,1,1,'5788'),(2,1,2,'578867817@qq.com'),(3,1,3,'weixin'),(4,1,0,'18829290435'),(5,2,0,'18829290001'),(6,3,0,'18829290002'),(7,4,0,'18829290003'),(8,5,0,'18829290004'),(9,1,4,'weibowangweihao'),(10,1,1,'19999999'),(11,1,1,'19999999'),(12,1,1,'19999999'),(13,1,1,'18829292929'),(14,1,1,'18829292929'),(15,1,1,'18829292929'),(16,1,1,'18829292929'),(17,1,1,'18829292929'),(18,NULL,11,'18829292929'),(19,NULL,11,'18829292929'),(20,NULL,11,'18829292929'),(21,NULL,11,'18829292929'),(22,NULL,11,'18829292929'),(23,NULL,11,'18829292929'),(24,NULL,11,'18829292929'),(25,NULL,11,'18829292929'),(26,NULL,11,'18829292929'),(27,NULL,11,'18829292929'),(28,NULL,11,'18829292929'),(29,NULL,11,'18829292929'),(30,NULL,11,'18829292929'),(31,NULL,11,'18829292929'),(32,NULL,11,'18829292929'),(33,2,11,'18829292929'),(34,2,11,'18829292929'),(35,2,11,'18829292929'),(36,2,11,'18829292929');
/*!40000 ALTER TABLE `UserContact` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `UserFriend`
--

DROP TABLE IF EXISTS `UserFriend`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `UserFriend` (
  `fid` int(15) NOT NULL AUTO_INCREMENT,
  `uid` int(10) DEFAULT NULL,
  `friendId` int(10) DEFAULT NULL,
  `isUpdate` int(5) DEFAULT '0',
  `remark` varchar(20) DEFAULT '',
  PRIMARY KEY (`fid`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `UserFriend`
--

LOCK TABLES `UserFriend` WRITE;
/*!40000 ALTER TABLE `UserFriend` DISABLE KEYS */;
INSERT INTO `UserFriend` VALUES (2,1,3,0,'赵四'),(3,1,4,0,'王五'),(4,1,5,0,'小六');
/*!40000 ALTER TABLE `UserFriend` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `UserInfo`
--

DROP TABLE IF EXISTS `UserInfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `UserInfo` (
  `uid` int(10) NOT NULL AUTO_INCREMENT,
  `account` varchar(20) NOT NULL,
  `password` varchar(20) NOT NULL,
  `name` varchar(20) DEFAULT NULL,
  `head` blob,
  PRIMARY KEY (`uid`),
  UNIQUE KEY `UQ_account` (`account`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `UserInfo`
--

LOCK TABLES `UserInfo` WRITE;
/*!40000 ALTER TABLE `UserInfo` DISABLE KEYS */;
INSERT INTO `UserInfo` VALUES (1,'wangweihao','weihao','hahahahaha','*********'),(2,'weihao','weihao','wangweihao','aweduiwhuihbdek'),(3,'wangweihaohao','123123123','111','asd'),(10,'qiqiqqqqiqi','hahahahaha',NULL,NULL),(14,'wwwww','123123',NULL,NULL),(15,'zhuchenguang','hahahahaha',NULL,NULL),(16,'wangw','123123123',NULL,NULL),(18,'wangwe','123123123',NULL,NULL);
/*!40000 ALTER TABLE `UserInfo` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-08-14 19:36:23
