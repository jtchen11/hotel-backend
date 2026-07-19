/*
Navicat MySQL Data Transfer

Source Server         : MySQL01
Source Server Version : 80035
Source Host           : localhost:3306
Source Database       : resturant_system

Target Server Type    : MYSQL
Target Server Version : 80035
File Encoding         : 65001

Date: 2026-06-27 18:30:34
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `attendance`
-- ----------------------------
DROP TABLE IF EXISTS `attendance`;
CREATE TABLE `attendance` (
  `attendance_id` int NOT NULL AUTO_INCREMENT COMMENT '考勤编号',
  `emp_id` int NOT NULL COMMENT '员工编号',
  `work_date` date NOT NULL COMMENT '日期',
  `status` varchar(20) NOT NULL COMMENT '出勤状态',
  `check_in_time` datetime DEFAULT NULL COMMENT '上班时间',
  `check_out_time` datetime DEFAULT NULL COMMENT '下班时间',
  `work_hours` decimal(5,1) DEFAULT NULL COMMENT '工作时长',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`attendance_id`),
  UNIQUE KEY `uk_emp_date` (`emp_id`,`work_date`),
  CONSTRAINT `attendance_ibfk_1` FOREIGN KEY (`emp_id`) REFERENCES `employee` (`emp_id`)
) ENGINE=InnoDB AUTO_INCREMENT=115 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='考勤记录表';

-- ----------------------------
-- Records of attendance
-- ----------------------------
INSERT INTO `attendance` VALUES ('1', '1', '2026-05-27', '正常', '2026-05-27 08:00:00', '2026-05-27 17:00:00', '8.0', '');
INSERT INTO `attendance` VALUES ('2', '2', '2026-05-27', '迟到', '2026-05-27 08:35:00', '2026-05-27 17:00:00', '7.5', '堵车');
INSERT INTO `attendance` VALUES ('3', '3', '2026-05-27', '正常', '2026-05-27 09:00:00', '2026-05-27 21:00:00', '8.0', '餐饮部工时');
INSERT INTO `attendance` VALUES ('4', '4', '2026-05-27', '正常', '2026-05-27 08:30:00', '2026-05-27 17:30:00', '8.0', '');
INSERT INTO `attendance` VALUES ('5', '5', '2026-05-27', '正常', '2026-05-27 09:00:00', '2026-05-27 18:00:00', '8.0', '');
INSERT INTO `attendance` VALUES ('6', '6', '2026-05-27', '旷工', null, null, '0.0', '未请假');
INSERT INTO `attendance` VALUES ('7', '7', '2026-05-27', '正常', '2026-05-27 08:00:00', '2026-05-27 17:00:00', '8.0', '');
INSERT INTO `attendance` VALUES ('8', '8', '2026-05-27', '正常', '2026-05-27 08:30:00', '2026-05-27 17:30:00', '8.0', '');
INSERT INTO `attendance` VALUES ('9', '9', '2026-05-27', '正常', '2026-05-27 09:00:00', '2026-05-27 21:00:00', '8.0', '');
INSERT INTO `attendance` VALUES ('10', '21', '2026-06-14', '迟到', '2026-06-14 12:24:10', '2026-06-14 15:30:21', '3.1', null);
INSERT INTO `attendance` VALUES ('11', '19', '2026-06-14', '旷工', '2026-06-14 12:34:47', '2026-06-14 18:05:07', '5.5', '偷喝酒扣100');
INSERT INTO `attendance` VALUES ('12', '20', '2026-06-14', '旷工', '2026-06-14 14:32:03', '2026-06-14 17:54:18', '3.4', null);
INSERT INTO `attendance` VALUES ('13', '9', '2026-06-14', '早退', '2026-06-14 14:36:38', '2026-06-14 17:54:21', '3.3', null);
INSERT INTO `attendance` VALUES ('14', '22', '2026-06-14', '正常', '2026-06-14 14:37:35', '2026-06-14 17:54:15', '3.3', null);
INSERT INTO `attendance` VALUES ('15', '8', '2026-06-14', '迟到', '2026-06-14 14:38:25', '2026-06-14 17:54:27', '3.3', null);
INSERT INTO `attendance` VALUES ('16', '6', '2026-06-14', '迟到', '2026-06-14 15:07:38', '2026-06-14 17:54:30', '2.8', null);
INSERT INTO `attendance` VALUES ('17', '2', '2026-06-14', '旷工', '2026-06-14 15:20:58', '2026-06-14 18:05:26', '2.7', null);
INSERT INTO `attendance` VALUES ('18', '3', '2026-06-14', '迟到', '2026-06-14 15:30:36', '2026-06-14 18:05:28', '2.6', null);
INSERT INTO `attendance` VALUES ('19', '4', '2026-06-14', '迟到', '2026-06-14 16:01:57', '2026-06-14 18:05:21', '2.1', null);
INSERT INTO `attendance` VALUES ('31', '1', '2026-06-16', '早退', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('32', '3', '2026-06-16', '迟到', '2026-06-16 20:11:53', '2026-06-16 20:11:59', '0.0', null);
INSERT INTO `attendance` VALUES ('33', '4', '2026-06-16', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('34', '5', '2026-06-16', '迟到', '2026-06-16 20:12:15', '2026-06-16 20:12:16', '0.0', null);
INSERT INTO `attendance` VALUES ('35', '6', '2026-06-16', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('36', '7', '2026-06-16', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('37', '19', '2026-06-16', '请假', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('38', '2', '2026-06-16', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('39', '8', '2026-06-16', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('40', '9', '2026-06-16', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('41', '22', '2026-06-16', '请假', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('42', '7', '2026-06-04', '迟到', '2026-06-16 17:39:02', '2026-06-16 17:39:05', '0.0', null);
INSERT INTO `attendance` VALUES ('44', '19', '2026-06-04', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('45', '21', '2026-06-16', '迟到', '2026-06-16 17:46:58', '2026-06-16 17:47:53', '0.0', null);
INSERT INTO `attendance` VALUES ('48', '2', '2026-06-15', '请假', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('49', '2', '2026-06-17', '请假', '2026-06-17 08:49:46', '2026-06-17 15:15:40', '6.4', null);
INSERT INTO `attendance` VALUES ('50', '3', '2026-06-17', '迟到', '2026-06-17 13:50:47', '2026-06-17 15:16:35', '1.4', null);
INSERT INTO `attendance` VALUES ('51', '5', '2026-06-17', '早退', '2026-06-17 13:50:58', '2026-06-17 15:17:32', '1.4', null);
INSERT INTO `attendance` VALUES ('52', '1', '2026-06-17', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('53', '4', '2026-06-17', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('54', '6', '2026-06-17', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('55', '7', '2026-06-17', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('56', '8', '2026-06-17', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('57', '9', '2026-06-17', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('58', '19', '2026-06-17', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('59', '20', '2026-06-17', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('60', '22', '2026-06-17', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('61', '23', '2026-06-17', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('62', '25', '2026-06-17', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('63', '1', '2026-06-19', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('64', '2', '2026-06-19', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('65', '3', '2026-06-19', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('66', '4', '2026-06-19', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('67', '5', '2026-06-19', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('68', '6', '2026-06-19', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('69', '7', '2026-06-19', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('70', '8', '2026-06-19', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('71', '9', '2026-06-19', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('72', '19', '2026-06-19', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('73', '20', '2026-06-19', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('74', '22', '2026-06-19', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('75', '23', '2026-06-19', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('76', '25', '2026-06-19', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('77', '26', '2026-06-19', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('78', '27', '2026-06-19', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('79', '1', '2026-06-23', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('80', '2', '2026-06-23', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('81', '3', '2026-06-23', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('82', '4', '2026-06-23', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('83', '5', '2026-06-23', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('84', '6', '2026-06-23', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('85', '7', '2026-06-23', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('86', '8', '2026-06-23', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('87', '9', '2026-06-23', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('88', '19', '2026-06-23', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('89', '20', '2026-06-23', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('90', '22', '2026-06-23', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('91', '23', '2026-06-23', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('92', '25', '2026-06-23', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('93', '26', '2026-06-23', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('94', '27', '2026-06-23', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('95', '1', '2026-06-25', '早退', '2026-06-25 08:09:25', '2026-06-25 08:48:54', '0.5', '上班偷吃扣200');
INSERT INTO `attendance` VALUES ('96', '2', '2026-06-25', '正常', '2026-06-25 08:10:02', null, null, null);
INSERT INTO `attendance` VALUES ('97', '5', '2026-06-25', '早退', '2026-06-25 08:10:11', '2026-06-25 10:32:49', '2.4', null);
INSERT INTO `attendance` VALUES ('98', '25', '2026-06-25', '请假', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('99', '7', '2026-06-25', '请假', '2026-06-25 10:32:52', '2026-06-25 10:32:52', '0.0', null);
INSERT INTO `attendance` VALUES ('100', '1', '2026-06-26', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('101', '2', '2026-06-26', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('102', '3', '2026-06-26', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('103', '4', '2026-06-26', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('104', '5', '2026-06-26', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('105', '6', '2026-06-26', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('106', '7', '2026-06-26', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('107', '8', '2026-06-26', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('108', '9', '2026-06-26', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('109', '19', '2026-06-26', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('110', '20', '2026-06-26', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('111', '22', '2026-06-26', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('112', '25', '2026-06-26', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('113', '26', '2026-06-26', '旷工', null, null, '0.0', null);
INSERT INTO `attendance` VALUES ('114', '27', '2026-06-26', '旷工', null, null, '0.0', null);

-- ----------------------------
-- Table structure for `deposit`
-- ----------------------------
DROP TABLE IF EXISTS `deposit`;
CREATE TABLE `deposit` (
  `deposit_id` int NOT NULL AUTO_INCREMENT COMMENT '押金编号',
  `guest_id` int NOT NULL COMMENT '客人编号',
  `amount` decimal(10,2) NOT NULL COMMENT '金额',
  `type` varchar(20) NOT NULL COMMENT '类型 收取/追加/退还/冲抵',
  `pay_method` varchar(20) DEFAULT NULL COMMENT '支付方式',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `operator` varchar(50) DEFAULT NULL COMMENT '操作员',
  PRIMARY KEY (`deposit_id`),
  KEY `guest_id` (`guest_id`),
  CONSTRAINT `deposit_ibfk_1` FOREIGN KEY (`guest_id`) REFERENCES `guest` (`guest_id`)
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='押金记录表';

-- ----------------------------
-- Records of deposit
-- ----------------------------
INSERT INTO `deposit` VALUES ('1', '9', '500.00', '收取', '微信', '入住押金', '2026-05-26 14:05:00', '张丽华');
INSERT INTO `deposit` VALUES ('2', '10', '800.00', '收取', '支付宝', '入住押金', '2026-05-27 09:35:00', '王敏');
INSERT INTO `deposit` VALUES ('3', '10', '300.00', '追加', '现金', '增加押金', '2026-05-27 20:00:00', '李强');
INSERT INTO `deposit` VALUES ('4', '11', '1000.00', '收取', '银行卡', '入住押金', '2026-05-25 20:10:00', '张丽华');
INSERT INTO `deposit` VALUES ('5', '12', '600.00', '收取', '微信', '入住押金', '2026-05-27 16:40:00', '王敏');
INSERT INTO `deposit` VALUES ('6', '15', '500.00', '收取', '现金', '团队押金', '2026-05-27 15:10:00', '张丽华');
INSERT INTO `deposit` VALUES ('7', '16', '500.00', '收取', '现金', '团队押金', '2026-05-27 15:10:00', '张丽华');
INSERT INTO `deposit` VALUES ('8', '13', '400.00', '退还', '微信', '离店结算后退还', '2026-05-23 11:30:00', '王敏');
INSERT INTO `deposit` VALUES ('9', '12', '300.00', '收取', '微信', '', '2026-06-06 16:40:00', '陈芳');
INSERT INTO `deposit` VALUES ('10', '24', '200.00', '收取', '微信', '', '2026-06-06 22:09:50', '陈芳');
INSERT INTO `deposit` VALUES ('11', '24', '-200.00', '退还', '原路返回', '结账自动退还', '2026-06-06 22:11:24', '陈芳');
INSERT INTO `deposit` VALUES ('12', '26', '500.00', '收取', '微信', '入住押金', '2026-06-07 00:44:35', null);
INSERT INTO `deposit` VALUES ('13', '32', '500.00', '收取', '微信', '入住押金', '2026-06-07 01:04:04', null);
INSERT INTO `deposit` VALUES ('14', '33', '500.00', '收取', '微信', '入住押金', '2026-06-07 01:04:42', null);
INSERT INTO `deposit` VALUES ('15', '34', '500.00', '收取', '微信', '入住押金', '2026-06-07 01:08:51', null);
INSERT INTO `deposit` VALUES ('16', '33', '-500.00', '退还', '原路返回', '结账自动退还', '2026-06-07 01:09:32', '陈芳');
INSERT INTO `deposit` VALUES ('17', '32', '-500.00', '退还', '原路返回', '结账自动退还', '2026-06-07 01:09:38', '陈芳');
INSERT INTO `deposit` VALUES ('18', '26', '-500.00', '退还', '原路返回', '结账自动退还', '2026-06-07 01:09:57', '陈芳');
INSERT INTO `deposit` VALUES ('19', '35', '500.00', '收取', '微信', '入住押金', '2026-06-07 01:23:28', null);
INSERT INTO `deposit` VALUES ('20', '35', '500.00', '收取', '微信', '入住押金', '2026-06-07 01:26:07', null);
INSERT INTO `deposit` VALUES ('21', '39', '500.00', '收取', '微信', '入住押金', '2026-06-07 01:32:50', null);
INSERT INTO `deposit` VALUES ('22', '57', '500.00', '收取', '微信', '入住押金', '2026-06-09 22:42:26', '张丽华');
INSERT INTO `deposit` VALUES ('23', '56', '500.00', '收取', '微信', '入住押金', '2026-06-09 23:51:53', '张丽华');
INSERT INTO `deposit` VALUES ('24', '58', '500.00', '收取', '微信', '入住押金', '2026-06-09 23:52:58', '张丽华');
INSERT INTO `deposit` VALUES ('25', '59', '500.00', '收取', '微信', '入住押金', '2026-06-09 23:53:01', '张丽华');
INSERT INTO `deposit` VALUES ('26', '61', '500.00', '收取', '微信', '入住押金', '2026-06-09 23:53:05', '张丽华');
INSERT INTO `deposit` VALUES ('27', '60', '200.00', '收取', '微信', '入住押金', '2026-06-09 23:55:07', '张丽华');
INSERT INTO `deposit` VALUES ('28', '64', '100.00', '收取', '微信', '入住押金', '2026-06-09 23:58:27', '张丽华');
INSERT INTO `deposit` VALUES ('29', '39', '100.00', '追加', '微信', '', '2026-06-11 00:10:58', '陈芳');
INSERT INTO `deposit` VALUES ('30', '59', '-100.00', '退还', '微信', '', '2026-06-11 01:10:14', '陈芳');
INSERT INTO `deposit` VALUES ('31', '58', '-100.00', '退还', '微信', '', '2026-06-11 08:42:32', '陈芳');
INSERT INTO `deposit` VALUES ('32', '58', '100.00', '收取', '微信', '', '2026-06-11 08:42:38', '陈芳');
INSERT INTO `deposit` VALUES ('33', '58', '100.00', '追加', '微信', '', '2026-06-11 08:42:43', '陈芳');
INSERT INTO `deposit` VALUES ('34', '68', '500.00', '收取', '微信', '入住押金', '2026-06-11 10:13:06', '张丽华');
INSERT INTO `deposit` VALUES ('35', '68', '500.00', '收取', '微信', '入住押金', '2026-06-11 10:13:06', '张丽华');
INSERT INTO `deposit` VALUES ('36', '69', '500.00', '收取', '微信', '入住押金', '2026-06-11 10:13:10', '张丽华');
INSERT INTO `deposit` VALUES ('37', '59', '100.00', '收取', '微信', '', '2026-06-11 13:47:38', '陈芳');
INSERT INTO `deposit` VALUES ('38', '59', '0.20', '收取', '微信', '但是', '2026-06-11 17:12:18', '陈芳');
INSERT INTO `deposit` VALUES ('39', '68', '-436.00', '退还', '原路返回', '结账自动退还', '2026-06-13 17:49:11', '陈芳');
INSERT INTO `deposit` VALUES ('40', '9', '-100.00', '退还', '微信', '', '2026-06-13 19:05:34', '陈芳');
INSERT INTO `deposit` VALUES ('41', '71', '500.00', '收取', '微信', '入住押金', '2026-06-16 21:52:22', '张丽华');
INSERT INTO `deposit` VALUES ('42', '16', '100.00', '追加', '微信', '', '2026-06-16 22:05:26', '陈芳');
INSERT INTO `deposit` VALUES ('43', '71', '-100.00', '退还', '微信', '', '2026-06-16 22:05:59', '陈芳');
INSERT INTO `deposit` VALUES ('44', '16', '-100.00', '退还', '微信', '', '2026-06-16 22:06:05', '陈芳');
INSERT INTO `deposit` VALUES ('45', '16', '-100.00', '退还', '微信', '', '2026-06-16 22:06:09', '陈芳');
INSERT INTO `deposit` VALUES ('46', '71', '100.00', '追加', '支付宝', '', '2026-06-16 22:08:16', '陈芳');
INSERT INTO `deposit` VALUES ('47', '72', '500.00', '收取', '微信', '入住押金', '2026-06-16 22:15:23', '张丽华');
INSERT INTO `deposit` VALUES ('48', '73', '500.00', '收取', '微信', '入住押金', '2026-06-16 22:15:54', '张丽华');
INSERT INTO `deposit` VALUES ('49', '73', '-321.80', '退还', '原路返回', '结账自动退还', '2026-06-16 22:16:23', '陈芳');
INSERT INTO `deposit` VALUES ('50', '74', '500.00', '收取', '微信', '入住押金', '2026-06-16 22:27:09', '张丽华');
INSERT INTO `deposit` VALUES ('51', '74', '-242.00', '退还', '原路返回', '结账自动退还', '2026-06-16 22:27:23', '陈芳');
INSERT INTO `deposit` VALUES ('52', '76', '500.00', '收取', '微信', '入住押金', '2026-06-17 13:28:29', '张丽华');
INSERT INTO `deposit` VALUES ('53', '76', '-303.20', '退还', '原路返回', '结账自动退还', '2026-06-17 13:30:22', '陈芳');
INSERT INTO `deposit` VALUES ('54', '111', '500.00', '收取', '微信', '入住押金', '2026-06-17 14:07:16', '张丽华');
INSERT INTO `deposit` VALUES ('55', '111', '-280.00', '退还', '原路返回', '结账自动退还', '2026-06-17 14:08:52', '陈芳');
INSERT INTO `deposit` VALUES ('56', '111', '400.00', '追加', '微信', '', '2026-06-17 14:19:47', '陈芳');
INSERT INTO `deposit` VALUES ('57', '111', '-273.20', '退还', '原路返回', '结账自动退还', '2026-06-17 14:20:12', '陈芳');
INSERT INTO `deposit` VALUES ('58', '111', '-148.00', '退还', '原路返回', '结账自动退还', '2026-06-17 14:22:54', '陈芳');
INSERT INTO `deposit` VALUES ('59', '112', '500.00', '收取', '微信', '入住押金', '2026-06-17 17:43:21', '张丽华');
INSERT INTO `deposit` VALUES ('60', '113', '500.00', '收取', '微信', '入住押金', '2026-06-17 17:46:25', '张丽华');
INSERT INTO `deposit` VALUES ('61', '112', '500.00', '收取', '微信', '入住押金', '2026-06-17 17:47:26', '张丽华');
INSERT INTO `deposit` VALUES ('62', '134', '500.00', '收取', '微信', '入住押金', '2026-06-25 08:06:36', '张丽华');
INSERT INTO `deposit` VALUES ('63', '143', '500.00', '收取', '微信', '入住押金', '2026-06-26 23:38:34', '张丽华');
INSERT INTO `deposit` VALUES ('64', '143', '-312.00', '退还', '原路返回', '结账自动退还', '2026-06-26 23:38:59', '陈芳');

-- ----------------------------
-- Table structure for `employee`
-- ----------------------------
DROP TABLE IF EXISTS `employee`;
CREATE TABLE `employee` (
  `emp_id` int NOT NULL AUTO_INCREMENT COMMENT '员工编号',
  `emp_name` varchar(50) NOT NULL COMMENT '姓名',
  `gender` varchar(10) DEFAULT NULL COMMENT '性别',
  `age` int DEFAULT NULL COMMENT '年龄',
  `id_card` varchar(18) NOT NULL COMMENT '身份证号',
  `phone` varchar(20) NOT NULL COMMENT '电话',
  `education` varchar(20) DEFAULT NULL COMMENT '学历',
  `department` varchar(50) DEFAULT NULL COMMENT '部门',
  `position` varchar(50) DEFAULT NULL COMMENT '职务',
  `hire_date` date DEFAULT NULL COMMENT '入职日期',
  `base_salary` decimal(10,2) DEFAULT NULL COMMENT '基本工资',
  `status` varchar(20) DEFAULT '在职' COMMENT '状态',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `password` varchar(100) DEFAULT '123456' COMMENT '登录密码',
  `role` varchar(20) DEFAULT '前台' COMMENT '角色：总经理/财务/前台/餐饮/库房/人事',
  `leave_date` date DEFAULT NULL COMMENT '离职日期',
  PRIMARY KEY (`emp_id`),
  UNIQUE KEY `id_card` (`id_card`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='员工信息表';

-- ----------------------------
-- Records of employee
-- ----------------------------
INSERT INTO `employee` VALUES ('1', '张丽华', '女', '32', '410101199403124567', '13800001111', '本科', '前台', '接待主管', '2018-05-10', '4500.00', '在职', '2026-05-28 09:52:58', '123456', '前台接待员', null);
INSERT INTO `employee` VALUES ('2', '王敏', '女', '28', '410101199803215678', '13800002222', '大专', '前台', '接待员', '2020-03-15', '3800.00', '在职', '2026-05-28 09:52:58', '123456', '前台接待员', null);
INSERT INTO `employee` VALUES ('3', '李强', '男', '35', '410101198712056789', '13800003333', '大专', '餐饮部', '服务员', '2019-07-20', '4000.00', '在职', '2026-05-28 09:52:58', '123456', '营业服务员', null);
INSERT INTO `employee` VALUES ('4', '陈芳', '女', '30', '410101199505016789', '13800004444', '本科', '财务部', '会计', '2017-11-01', '5200.00', '在职', '2026-05-28 09:52:58', '123456', '财务管理员', null);
INSERT INTO `employee` VALUES ('5', '赵磊', '男', '40', '410101198404156789', '13800005555', '本科', '总经理室', '总经理', '2015-01-15', '15000.00', '在职', '2026-05-28 09:52:58', '123456', '总经理', null);
INSERT INTO `employee` VALUES ('6', '孙敏', '女', '29', '410101199702286789', '13800006666', '大专', '康乐部', 'KTV服务员', '2021-06-10', '3600.00', '在职', '2026-05-28 09:52:58', '123456', '营业服务员', null);
INSERT INTO `employee` VALUES ('7', '周华', '男', '45', '410101197812046789', '13800007777', '高中', '库房', '库管员', '2016-09-01', '3500.00', '在职', '2026-05-28 09:52:58', '123456', '营业服务员', null);
INSERT INTO `employee` VALUES ('8', '吴静', '女', '27', '410101199903156789', '13800008888', '本科', '人事部', '人事专员', '2022-02-20', '4200.00', '在职', '2026-05-28 09:52:58', '123456', '财务管理员', null);
INSERT INTO `employee` VALUES ('9', '郑伟', '男', '33', '410101199311236789', '13800009999', '大专', '餐饮部', '领班', '2019-04-12', '4800.00', '在职', '2026-05-28 09:52:58', '123456', '营业服务员', null);
INSERT INTO `employee` VALUES ('19', 'admin', '男', '30', '111111111111111111', '13800000000', '本科', '总经理室', '总经理', '2026-05-31', '15000.00', '在职', '2026-05-31 23:55:30', '123456', '总经理', null);
INSERT INTO `employee` VALUES ('20', '陈吉', '女', '20', '440105200505196623', '18588648650', '本科', '康乐部', '总经理', '2026-06-16', '80000.00', '在职', '2026-06-14 03:36:45', '123456', '总经理', '2026-06-16');
INSERT INTO `employee` VALUES ('21', '级', '男', '58', '444444444444444444', '11111111111', '本科', '负责人', '负责人', '2026-06-17', '555.00', '离职', '2026-06-14 03:37:48', '123456', '财务管理员', '2026-06-17');
INSERT INTO `employee` VALUES ('22', '思玙', '男', '18', '445445445444544445', '18588888888', '本科', '康乐部', '经理', '2026-05-29', '50000.00', '在职', '2026-06-14 13:43:53', '123456', '营业服务员', null);
INSERT INTO `employee` VALUES ('23', '郑凯禧', '女', '25', '445445444545454545', '13288588588', '本科', '人事部', '经理', '2026-06-25', '50000.00', '离职', '2026-06-16 18:39:34', '123456', '营业服务员', '2026-06-25');
INSERT INTO `employee` VALUES ('24', '郑凯', '女', '21', '440441444545478751', '18888648651', '本科', '库房', '员工', '2026-06-09', '3330.00', '离职', '2026-06-16 19:42:17', '123456', '营业服务员', '2026-06-16');
INSERT INTO `employee` VALUES ('25', '陈n', '女', '88', '445444545454554', '18588648650', '本科', '总经理室', '总经理', '2026-06-16', '8000.00', '在职', '2026-06-17 09:22:53', '123456', '总经理', null);
INSERT INTO `employee` VALUES ('26', '伍yoyo', '女', '19', '440105200502035985', '18588648650', '本科', '人事部', '经理', '2026-06-09', '10000.00', '在职', '2026-06-18 04:01:15', '050519', '财务管理员', null);
INSERT INTO `employee` VALUES ('27', '陈思思', '女', '23', '44010520050203598x', '13255655685', '本科', '康乐部', '员工', '2026-06-25', '3500.00', '在职', '2026-06-18 04:02:09', '232323', '营业服务员', null);

-- ----------------------------
-- Table structure for `group_info`
-- ----------------------------
DROP TABLE IF EXISTS `group_info`;
CREATE TABLE `group_info` (
  `group_id` int NOT NULL AUTO_INCREMENT COMMENT '团体编号',
  `group_name` varchar(200) NOT NULL COMMENT '团体名称',
  `group_leader` varchar(50) DEFAULT NULL COMMENT '领队姓名',
  `leader_phone` varchar(20) DEFAULT NULL COMMENT '领队电话',
  `person_count` int DEFAULT NULL COMMENT '人数',
  `check_in_date` datetime DEFAULT NULL COMMENT '入住日期',
  `pre_leave_date` date DEFAULT NULL COMMENT '预离日期',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`group_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='团体信息表';

-- ----------------------------
-- Records of group_info
-- ----------------------------
INSERT INTO `group_info` VALUES ('1', '北京电影考察团', '张艺谋', '13611112222', '8', '2026-05-27 15:00:00', '2026-05-31', '2026-05-28 09:52:58');
INSERT INTO `group_info` VALUES ('2', '北京电影考察团', '张艺谋', '13611112222', '8', '2026-05-27 15:00:00', '2026-05-31', '2026-05-28 10:17:28');
INSERT INTO `group_info` VALUES ('3', '艺术', '陈', '2', '2', '2026-06-06 00:00:00', '2026-06-07', '2026-06-07 00:16:13');
INSERT INTO `group_info` VALUES ('5', '8', '嚼嚼嚼', '58451451', '2', '2026-06-09 00:00:00', '2026-06-10', '2026-06-09 22:42:19');
INSERT INTO `group_info` VALUES ('6', '111', '111', '14111111111', '6', '2026-06-09 00:00:00', '2026-06-10', '2026-06-09 23:51:21');
INSERT INTO `group_info` VALUES ('7', '22', '22', '11111111111', '4', '2026-06-14 00:00:00', '2026-07-16', '2026-06-09 23:53:45');
INSERT INTO `group_info` VALUES ('8', '44', '44', '4444', '4', '2026-06-09 00:00:00', '2026-06-10', '2026-06-09 23:58:15');
INSERT INTO `group_info` VALUES ('9', '西藏旅游团', '小麦麦', '11111111111', '8', '2026-06-18 00:00:00', '2026-06-19', '2026-06-18 10:59:27');
INSERT INTO `group_info` VALUES ('10', '西藏旅游团', '小麦麦', '13588648650', '4', '2026-06-25 00:00:00', '2026-06-26', '2026-06-25 08:57:55');

-- ----------------------------
-- Table structure for `guest`
-- ----------------------------
DROP TABLE IF EXISTS `guest`;
CREATE TABLE `guest` (
  `guest_id` int NOT NULL AUTO_INCREMENT COMMENT '客人编号',
  `name` varchar(50) NOT NULL COMMENT '姓名',
  `gender` varchar(10) DEFAULT NULL COMMENT '性别',
  `native_place` varchar(100) DEFAULT NULL COMMENT '籍贯',
  `company` varchar(200) DEFAULT NULL COMMENT '工作单位',
  `occupation` varchar(50) DEFAULT NULL COMMENT '职业',
  `reason` varchar(200) DEFAULT NULL COMMENT '住店事由',
  `id_card` varchar(18) NOT NULL COMMENT '身份证号',
  `phone` varchar(20) NOT NULL COMMENT '联系电话',
  `check_in_date` datetime NOT NULL COMMENT '入住日期',
  `pre_leave_date` date NOT NULL COMMENT '预离日期',
  `actual_leave_date` datetime DEFAULT NULL COMMENT '实际离店日期',
  `room_id` int DEFAULT NULL COMMENT '入住房间编号',
  `guest_type` varchar(20) DEFAULT '散客' COMMENT '客人类型',
  `group_id` int DEFAULT NULL COMMENT '团体编号',
  `status` varchar(20) DEFAULT '在住' COMMENT '状态',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`guest_id`),
  KEY `room_id` (`room_id`),
  KEY `group_id` (`group_id`),
  KEY `idx_id_card` (`id_card`),
  CONSTRAINT `guest_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `room` (`room_id`),
  CONSTRAINT `guest_ibfk_2` FOREIGN KEY (`group_id`) REFERENCES `group_info` (`group_id`)
) ENGINE=InnoDB AUTO_INCREMENT=147 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='客人信息表';

-- ----------------------------
-- Records of guest
-- ----------------------------
INSERT INTO `guest` VALUES ('9', '刘德华', '男', '香港', '演艺公司', '艺人', '演出', '110101198009271234', '13912345678', '2026-05-26 14:00:00', '2026-05-29', '2026-06-16 21:53:06', '10', '散客', null, '已离店', '2026-05-28 10:17:28');
INSERT INTO `guest` VALUES ('10', '张学友', '男', '香港', null, '歌手', '个人度假', '110101197107101234', '13912345679', '2026-05-27 09:30:00', '2026-05-30', null, '3', '散客', null, '已退房', '2026-05-28 10:17:28');
INSERT INTO `guest` VALUES ('11', '林青霞', '女', '台湾', '影视公司', '演员', '拍摄', '110101195411031234', '13912345680', '2026-05-25 20:00:00', '2026-05-28', '2026-06-06 18:07:15', '6', '散客', null, '已离店', '2026-05-28 10:17:28');
INSERT INTO `guest` VALUES ('12', '李连杰', '男', '北京', '武术协会', '运动员', '比赛', '110101196304261234', '13912345681', '2026-05-27 16:30:00', '2026-05-29', '2026-06-06 16:40:25', '12', '散客', null, '已离店', '2026-05-28 10:17:28');
INSERT INTO `guest` VALUES ('13', '周润发', '男', '香港', null, '退休', '旅游', '110101195505181234', '13912345682', '2026-05-20 12:00:00', '2026-05-23', '2026-05-23 11:00:00', '5', '散客', null, '已离店', '2026-05-28 10:17:28');
INSERT INTO `guest` VALUES ('14', '巩俐', '女', '山东', '演艺', '演员', '演出', '110101196512311234', '13912345683', '2026-05-22 08:00:00', '2026-05-25', '2026-05-25 10:30:00', '8', '散客', null, '已离店', '2026-05-28 10:17:28');
INSERT INTO `guest` VALUES ('15', '张艺谋', '男', '陕西', '电影厂', '导演', '采风', '610101197004021234', '13912345684', '2026-05-27 15:00:00', '2026-05-31', null, '5', '团体', null, '已退房', '2026-05-28 10:17:28');
INSERT INTO `guest` VALUES ('16', '陈凯歌', '男', '北京', '电影厂', '导演', '采风', '110101195208121234', '13912345685', '2026-05-27 15:00:00', '2026-06-25', null, '11', '团体', null, '在住', '2026-05-28 10:17:28');
INSERT INTO `guest` VALUES ('24', '55', '男', null, null, null, null, '55', '55', '2026-06-06 00:00:00', '2026-06-07', '2026-06-06 22:11:24', '2', '散客', null, '已离店', '2026-06-06 17:24:08');
INSERT INTO `guest` VALUES ('25', '66', '男', null, null, null, null, '66', '66', '2026-06-06 00:00:00', '2026-06-07', '2026-06-07 00:17:18', '7', '散客', null, '已离店', '2026-06-06 22:09:00');
INSERT INTO `guest` VALUES ('26', '44', '男', null, null, null, null, '44', '44', '2026-06-07 00:44:35', '2026-06-26', '2026-06-07 01:09:57', '4', '散客', null, '已离店', '2026-06-06 22:28:52');
INSERT INTO `guest` VALUES ('27', '88', '男', null, null, null, null, '88', '88', '2026-06-06 00:00:00', '2026-06-07', '2026-06-11 00:34:26', '9', '散客', null, '已离店', '2026-06-06 23:30:46');
INSERT INTO `guest` VALUES ('28', 'c', '男', null, null, null, null, '陈', '陈', '2026-06-07 00:00:00', '2026-06-09', '2026-06-14 00:41:54', '12', '散客', null, '已离店', '2026-06-06 23:32:10');
INSERT INTO `guest` VALUES ('29', '陈', null, null, null, null, null, 'GROUP3_1', '2', '2026-06-06 00:00:00', '2026-06-07', '2026-06-07 01:09:46', '12', '团体', null, '已离店', '2026-06-07 00:16:13');
INSERT INTO `guest` VALUES ('30', '陈', null, null, null, null, null, 'GROUP3_2', '2', '2026-06-06 00:00:00', '2026-06-07', '2026-06-07 01:09:42', '11', '团体', null, '已离店', '2026-06-07 00:16:13');
INSERT INTO `guest` VALUES ('31', '陈', null, null, null, null, null, 'GROUP3_3', '2', '2026-06-06 00:00:00', '2026-06-07', '2026-06-07 01:09:51', '10', '团体', null, '已离店', '2026-06-07 00:16:13');
INSERT INTO `guest` VALUES ('32', '2', '男', null, null, null, null, '22', '22', '2026-06-07 01:04:04', '2026-06-10', '2026-06-07 01:09:38', '3', '散客', null, '已离店', '2026-06-07 00:47:06');
INSERT INTO `guest` VALUES ('33', '11', '男', null, null, null, null, '111', '111', '2026-06-07 01:04:42', '2026-06-20', '2026-06-07 01:09:32', '7', '散客', null, '已离店', '2026-06-07 01:04:35');
INSERT INTO `guest` VALUES ('34', '99', '男', null, null, null, null, '99', '999', '2026-06-07 01:08:51', '2026-06-13', '2026-06-14 02:07:52', '5', '散客', null, '已离店', '2026-06-07 01:08:39');
INSERT INTO `guest` VALUES ('35', '1', '男', null, null, null, null, '11', '111', '2026-06-07 01:26:07', '2026-06-10', '2026-06-14 01:46:15', '2', '散客', null, '已离店', '2026-06-07 01:12:57');
INSERT INTO `guest` VALUES ('39', '呃呃', '男', null, null, null, null, '2342343', '2134134', '2026-06-07 01:32:50', '2026-06-20', '2026-06-13 18:48:57', '6', '散客', null, '已离店', '2026-06-07 01:30:22');
INSERT INTO `guest` VALUES ('56', '嚼嚼嚼', null, null, null, null, null, 'GROUP5_1', '58451451', '2026-06-09 23:51:53', '2026-06-10', '2026-06-14 02:39:01', '3', '团体', null, '已离店', '2026-06-09 22:42:19');
INSERT INTO `guest` VALUES ('57', '嚼嚼嚼', null, null, null, null, null, 'GROUP5_2', '58451451', '2026-06-09 22:42:26', '2026-06-10', '2026-06-10 20:20:39', '4', '团体', null, '已离店', '2026-06-09 22:42:19');
INSERT INTO `guest` VALUES ('58', '思', '女', null, null, null, null, '111111111111111111', '14111111111', '2026-06-09 23:52:58', '2026-06-10', '2026-06-16 20:55:21', '10', '散客', null, '已离店', '2026-06-09 23:47:03');
INSERT INTO `guest` VALUES ('59', '111', null, null, null, null, null, 'GROUP6_1', '14111111111', '2026-06-09 23:53:01', '2026-06-10', '2026-06-11 01:14:20', '7', '团体', null, '已离店', '2026-06-09 23:51:21');
INSERT INTO `guest` VALUES ('60', '111', null, null, null, null, null, 'GROUP6_2', '14111111111', '2026-06-09 23:55:07', '2026-06-10', '2026-06-10 21:24:23', '8', '团体', null, '已离店', '2026-06-09 23:51:21');
INSERT INTO `guest` VALUES ('61', '111', null, null, null, null, null, 'GROUP6_3', '14111111111', '2026-06-09 23:53:05', '2026-06-10', '2026-06-10 21:24:01', '9', '团体', null, '已离店', '2026-06-09 23:51:21');
INSERT INTO `guest` VALUES ('64', '44', null, null, null, null, null, 'GROUP8_1', '4444', '2026-06-09 23:58:27', '2026-06-10', '2026-06-10 21:24:14', '11', '团体', null, '已离店', '2026-06-09 23:58:15');
INSERT INTO `guest` VALUES ('68', '小思', '女', null, null, null, null, '431126200501190348', '15013033312', '2026-06-11 10:13:06', '2026-06-13', '2026-06-13 17:49:11', '1', '散客', null, '已离店', '2026-06-11 10:10:17');
INSERT INTO `guest` VALUES ('69', '小盘', '女', null, null, null, null, '431126200501190347', '15012311123', '2026-06-11 10:13:10', '2026-06-13', '2026-06-14 01:16:00', '2', '散客', null, '已离店', '2026-06-11 10:10:57');
INSERT INTO `guest` VALUES ('71', '小盘', '男', null, null, null, null, '44020250259296322', '15012311123', '2026-06-16 21:52:22', '2026-06-19', '2026-06-17 08:50:48', '1', '散客', null, '已离店', '2026-06-16 21:52:13');
INSERT INTO `guest` VALUES ('72', '赵静雨', '女', null, null, null, null, '4582222659956295', '17337501959', '2026-06-16 22:15:23', '2026-06-20', '2026-06-25 10:32:00', '10', '散客', null, '已离店', '2026-06-16 21:55:57');
INSERT INTO `guest` VALUES ('73', '陈颉桐', '男', null, null, null, null, '417427542742', '18588648650', '2026-06-16 22:15:54', '2026-06-17', '2026-06-16 22:16:23', '5', '散客', null, '已离店', '2026-06-16 22:15:51');
INSERT INTO `guest` VALUES ('74', '陈颉桐', '男', null, null, null, null, '4488522', '18588648650', '2026-06-16 22:27:09', '2026-06-17', '2026-06-16 22:27:23', '3', '散客', null, '已离店', '2026-06-16 22:16:44');
INSERT INTO `guest` VALUES ('76', '赵静雯', '男', null, null, null, null, '88888888888', '17337501959', '2026-06-17 13:28:29', '2026-06-18', '2026-06-17 13:30:22', '2', '散客', null, '已离店', '2026-06-17 09:03:54');
INSERT INTO `guest` VALUES ('77', '陈小明', '男', '广东', null, null, null, '440101199001011234', '13900000001', '2026-01-15 14:00:00', '2026-01-18', '2026-01-18 10:00:00', '1', '散客', null, '已离店', '2026-06-17 10:27:06');
INSERT INTO `guest` VALUES ('79', '陈小明', '男', '广东', null, null, null, '440101199001011235', '13900000001', '2026-01-15 14:00:00', '2026-01-18', '2026-01-18 10:00:00', '1', '散客', null, '已离店', '2026-06-17 10:35:05');
INSERT INTO `guest` VALUES ('80', '李丽', '女', '上海', null, null, null, '310101199002022347', '13900000002', '2026-02-10 10:00:00', '2026-02-12', '2026-02-12 09:00:00', '3', '散客', null, '已离店', '2026-06-17 10:35:05');
INSERT INTO `guest` VALUES ('81', '王强', '男', '四川', null, null, null, '510101199103043458', '13900000003', '2026-03-05 16:00:00', '2026-03-10', '2026-03-10 11:00:00', '5', '散客', null, '已离店', '2026-06-17 10:35:05');
INSERT INTO `guest` VALUES ('82', '赵静', '女', '浙江', null, null, null, '330101199104205679', '13900000004', '2026-04-20 12:00:00', '2026-04-24', '2026-04-24 08:00:00', '6', '散客', null, '已离店', '2026-06-17 10:35:05');
INSERT INTO `guest` VALUES ('83', '孙浩', '男', '江苏', null, null, null, '320101199105015679', '13900000005', '2026-05-01 15:00:00', '2026-05-03', '2026-05-03 09:00:00', '9', '散客', null, '已离店', '2026-06-17 10:35:05');
INSERT INTO `guest` VALUES ('84', '周梅', '女', '福建', null, null, null, '350101199105156780', '13900000006', '2026-05-15 13:00:00', '2026-05-18', '2026-05-18 10:00:00', '11', '散客', null, '已离店', '2026-06-17 10:35:06');
INSERT INTO `guest` VALUES ('85', '吴华', '男', '湖南', null, null, null, '430101199106015679', '13900000007', '2026-06-01 10:00:00', '2026-06-04', '2026-06-04 09:00:00', '4', '散客', null, '已离店', '2026-06-17 10:35:06');
INSERT INTO `guest` VALUES ('86', '郑明', '男', '湖北', null, null, null, '420101199106105679', '13900000008', '2026-06-10 14:00:00', '2026-06-12', '2026-06-12 08:00:00', '8', '散客', null, '已离店', '2026-06-17 10:35:06');
INSERT INTO `guest` VALUES ('89', '杨雪', '女', '辽宁', null, null, null, '210101199102285679', '13900000011', '2026-02-28 09:00:00', '2026-03-02', '2026-03-02 10:00:00', '12', '散客', null, '已离店', '2026-06-17 10:35:06');
INSERT INTO `guest` VALUES ('90', '朱伟', '男', '黑龙江', null, null, null, '230101199103155679', '13900000012', '2026-03-15 14:00:00', '2026-03-18', '2026-03-18 09:00:00', '7', '散客', null, '已离店', '2026-06-17 10:35:06');
INSERT INTO `guest` VALUES ('91', '何欣', '女', '安徽', null, null, null, '340101199104055679', '13900000013', '2026-04-05 11:00:00', '2026-04-08', '2026-04-08 10:00:00', '9', '散客', null, '已离店', '2026-06-17 10:35:06');
INSERT INTO `guest` VALUES ('92', '罗刚', '男', '江西', null, null, null, '360101199105255679', '13900000014', '2026-05-25 10:00:00', '2026-05-28', '2026-05-28 08:00:00', '5', '散客', null, '已离店', '2026-06-17 10:35:06');
INSERT INTO `guest` VALUES ('93', '谢芳', '女', '山东', null, null, null, '370101199103205679', '13900000015', '2026-03-20 09:00:00', '2026-03-23', '2026-03-23 11:00:00', '6', '散客', null, '已离店', '2026-06-17 10:35:06');
INSERT INTO `guest` VALUES ('94', '韩冰', '男', '吉林', null, null, null, '220101199104155679', '13900000016', '2026-04-15 14:00:00', '2026-04-19', '2026-04-19 08:00:00', '2', '散客', null, '已离店', '2026-06-17 10:35:07');
INSERT INTO `guest` VALUES ('95', '唐丽', '女', '广西', null, null, null, '450101199105105679', '13900000017', '2026-05-10 12:00:00', '2026-05-13', '2026-05-13 10:00:00', '4', '散客', null, '已离店', '2026-06-17 10:35:07');
INSERT INTO `guest` VALUES ('96', '曹峰', '男', '陕西', null, null, null, '610101199105205679', '13900000018', '2026-05-20 16:00:00', '2026-05-23', '2026-05-23 09:00:00', '8', '散客', null, '已离店', '2026-06-17 10:35:07');
INSERT INTO `guest` VALUES ('97', '邓洁', '女', '云南', null, null, null, '530101199106025679', '13900000019', '2026-06-02 08:00:00', '2026-06-06', '2026-06-06 08:00:00', '12', '散客', null, '已离店', '2026-06-17 10:35:07');
INSERT INTO `guest` VALUES ('99', '赵刚', '男', '北京', null, null, null, '110101199001151234', '13900000100', '2025-01-15 14:00:00', '2025-01-18', '2025-01-18 10:00:00', '1', '散客', null, '已离店', '2026-06-17 11:02:40');
INSERT INTO `guest` VALUES ('100', '钱芳', '女', '上海', null, null, null, '310101199002151234', '13900000101', '2025-02-15 10:00:00', '2025-02-18', '2025-02-18 09:00:00', '2', '散客', null, '已离店', '2026-06-17 11:02:40');
INSERT INTO `guest` VALUES ('101', '孙强', '男', '广东', null, null, null, '440101199003151234', '13900000102', '2025-03-15 16:00:00', '2025-03-18', '2025-03-18 11:00:00', '3', '散客', null, '已离店', '2026-06-17 11:02:41');
INSERT INTO `guest` VALUES ('102', '李红', '女', '浙江', null, null, null, '330101199004151234', '13900000103', '2025-04-15 12:00:00', '2025-04-18', '2025-04-18 08:00:00', '4', '散客', null, '已离店', '2026-06-17 11:02:41');
INSERT INTO `guest` VALUES ('103', '周杰', '男', '江苏', null, null, null, '320101199005151234', '13900000104', '2025-05-15 15:00:00', '2025-05-18', '2025-05-18 09:00:00', '5', '散客', null, '已离店', '2026-06-17 11:02:41');
INSERT INTO `guest` VALUES ('104', '吴丽', '女', '湖南', null, null, null, '430101199006151234', '13900000105', '2025-06-15 10:00:00', '2025-06-18', '2025-06-18 10:00:00', '6', '散客', null, '已离店', '2026-06-17 11:02:41');
INSERT INTO `guest` VALUES ('105', '郑伟', '男', '湖北', null, null, null, '420101199007151234', '13900000106', '2025-07-15 14:00:00', '2025-07-18', '2025-07-18 11:00:00', '7', '散客', null, '已离店', '2026-06-17 11:02:41');
INSERT INTO `guest` VALUES ('106', '王芳', '女', '四川', null, null, null, '510101199008151234', '13900000107', '2025-08-15 09:00:00', '2025-08-18', '2025-08-18 08:00:00', '8', '散客', null, '已离店', '2026-06-17 11:02:41');
INSERT INTO `guest` VALUES ('107', '陈静', '女', '福建', null, null, null, '350101199009151234', '13900000108', '2025-09-15 13:00:00', '2025-09-18', '2025-09-18 10:00:00', '9', '散客', null, '已离店', '2026-06-17 11:02:41');
INSERT INTO `guest` VALUES ('108', '林峰', '男', '山东', null, null, null, '370101199010151234', '13900000109', '2025-10-15 16:00:00', '2025-10-18', '2025-10-18 09:00:00', '10', '散客', null, '已离店', '2026-06-17 11:02:41');
INSERT INTO `guest` VALUES ('109', '黄菊', '女', '河南', null, null, null, '410101199011151234', '13900000110', '2025-11-15 12:00:00', '2025-11-18', '2025-11-18 10:00:00', '11', '散客', null, '已离店', '2026-06-17 11:02:41');
INSERT INTO `guest` VALUES ('110', '刘猛', '男', '辽宁', null, null, null, '210101199012151234', '13900000111', '2025-12-15 14:00:00', '2025-12-18', '2025-12-18 09:00:00', '12', '散客', null, '已离店', '2026-06-17 11:02:42');
INSERT INTO `guest` VALUES ('111', '我', '男', null, null, null, null, '444555555555555555', '185888648650', '2026-06-17 14:07:16', '2026-06-18', '2026-06-17 14:22:54', '2', '散客', null, '已离店', '2026-06-17 14:07:00');
INSERT INTO `guest` VALUES ('112', '来来来', '男', null, null, null, null, '185886486820', '265698653', '2026-06-17 17:47:26', '2026-06-20', '2026-06-24 20:53:37', '6', '散客', null, '已离店', '2026-06-17 17:41:49');
INSERT INTO `guest` VALUES ('113', '王菲菲', '女', null, null, null, null, '851485196', '4612485', '2026-06-17 17:46:25', '2026-06-25', null, '4', '散客', null, '在住', '2026-06-17 17:45:44');
INSERT INTO `guest` VALUES ('130', '赵静痛', '女', '湖北省', '广州应用科技学院', '', '', '440105200404235564', '17337501959', '2026-07-03 00:00:00', '2026-07-27', null, '1', '散客', null, '已预订', '2026-06-19 03:17:07');
INSERT INTO `guest` VALUES ('134', '赵静雯', '男', '吉林省', '广州应用科技学院', '', '', '440108200303215545', '17337501959', '2026-06-25 08:06:36', '2026-06-26', '2026-06-27 02:13:55', '1', '散客', null, '已离店', '2026-06-24 21:31:13');
INSERT INTO `guest` VALUES ('136', '何雨', '女', '辽宁省', '广州应用科技学院', '', '', '440108197808035545', '17337501959', '2026-06-28 00:00:00', '2026-07-01', null, '1', '散客', null, '已预订', '2026-06-25 08:00:24');
INSERT INTO `guest` VALUES ('143', '陈', '男', '江西省', '广州应用科技学院', '', '', '440105200505196623', '17337501959', '2026-06-26 23:38:34', '2026-06-27', '2026-06-26 23:38:59', '2', '散客', null, '已离店', '2026-06-26 23:12:06');
INSERT INTO `guest` VALUES ('146', '陈', '男', '云南省', '广州应用科技学院', '', '', '440105200505196623', '17337501958', '2026-06-26 00:00:00', '2026-06-27', null, '2', '散客', null, '已预订', '2026-06-26 23:43:48');

-- ----------------------------
-- Table structure for `ktv_record`
-- ----------------------------
DROP TABLE IF EXISTS `ktv_record`;
CREATE TABLE `ktv_record` (
  `record_id` int NOT NULL AUTO_INCREMENT COMMENT '记录编号',
  `ktv_id` int NOT NULL COMMENT '包房编号',
  `guest_id` int NOT NULL COMMENT '客人编号',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `duration` decimal(5,1) DEFAULT NULL COMMENT '消费时长 小时',
  `total_fee` decimal(10,2) DEFAULT NULL COMMENT '消费总额',
  `is_settled` tinyint DEFAULT '0' COMMENT '是否入账 0-未入账 1-已入账',
  `order_id` int DEFAULT NULL COMMENT '关联账单编号',
  `operator` varchar(50) DEFAULT NULL COMMENT '操作员',
  PRIMARY KEY (`record_id`),
  KEY `ktv_id` (`ktv_id`),
  KEY `guest_id` (`guest_id`),
  KEY `order_id` (`order_id`),
  CONSTRAINT `ktv_record_ibfk_1` FOREIGN KEY (`ktv_id`) REFERENCES `ktv_room` (`ktv_id`),
  CONSTRAINT `ktv_record_ibfk_2` FOREIGN KEY (`guest_id`) REFERENCES `guest` (`guest_id`),
  CONSTRAINT `ktv_record_ibfk_3` FOREIGN KEY (`order_id`) REFERENCES `order_main` (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='KTV消费记录表';

-- ----------------------------
-- Records of ktv_record
-- ----------------------------
INSERT INTO `ktv_record` VALUES ('1', '2', '10', '2026-05-27 20:00:00', '2026-05-27 22:30:00', '2.5', '270.00', '0', '2', '孙敏');
INSERT INTO `ktv_record` VALUES ('2', '1', '16', '2026-06-02 09:43:13', '2026-06-02 10:22:13', '0.7', '47.60', '0', null, '李强');
INSERT INTO `ktv_record` VALUES ('3', '1', '16', '2026-06-02 10:23:20', '2026-06-02 10:23:23', '0.0', '0.00', '0', null, '李强');
INSERT INTO `ktv_record` VALUES ('4', '1', '16', '2026-06-02 10:37:16', '2026-06-02 10:39:02', '0.0', '0.00', '0', null, '李强');
INSERT INTO `ktv_record` VALUES ('5', '1', '16', '2026-06-02 10:55:31', '2026-06-02 10:55:45', '0.0', '0.00', '0', null, '李强');
INSERT INTO `ktv_record` VALUES ('6', '1', '12', '2026-06-02 12:03:12', '2026-06-02 12:03:24', '0.0', '0.00', '0', null, '李强');
INSERT INTO `ktv_record` VALUES ('7', '1', '12', '2026-06-02 07:03:36', '2026-06-02 12:04:30', '5.0', '340.00', '0', null, '李强');
INSERT INTO `ktv_record` VALUES ('8', '1', '16', '2026-06-02 15:10:15', '2026-06-02 15:10:23', '0.0', '0.00', '0', null, '李强');
INSERT INTO `ktv_record` VALUES ('9', '1', '16', '2026-06-03 11:18:51', '2026-06-03 11:19:06', '0.0', '0.00', '0', null, '李强');
INSERT INTO `ktv_record` VALUES ('10', '1', '11', '2026-06-05 09:03:18', '2026-06-05 09:03:22', '0.0', '0.00', '0', null, '李强');
INSERT INTO `ktv_record` VALUES ('11', '2', '12', '2026-06-05 09:05:29', '2026-06-05 09:05:31', '0.0', '0.00', '0', null, '李强');
INSERT INTO `ktv_record` VALUES ('12', '3', '16', '2026-06-05 20:40:24', '2026-06-09 21:39:10', '6.0', '1008.00', '1', '8', '李强');
INSERT INTO `ktv_record` VALUES ('13', '1', '11', '2026-06-06 00:46:21', '2026-06-07 17:36:58', '3.0', '204.00', '1', '3', '李强');
INSERT INTO `ktv_record` VALUES ('14', '6', '39', '2026-06-07 20:56:30', '2026-06-07 20:56:33', '0.0', '0.00', '1', '33', '李强');
INSERT INTO `ktv_record` VALUES ('15', '1', '16', '2026-06-09 20:33:05', '2026-06-09 22:36:37', '3.0', '204.00', '1', '8', '李强');
INSERT INTO `ktv_record` VALUES ('16', '4', '34', '2026-06-09 22:36:59', '2026-06-14 02:07:27', '3.0', '294.00', '1', '24', '李强');
INSERT INTO `ktv_record` VALUES ('17', '3', '57', '2026-06-09 22:51:58', '2026-06-10 20:19:45', '6.0', '1008.00', '1', '34', '李强');
INSERT INTO `ktv_record` VALUES ('18', '1', '16', '2026-06-12 18:46:22', '2026-06-13 18:47:30', '3.0', '204.00', '1', '8', '李强');
INSERT INTO `ktv_record` VALUES ('19', '1', '71', '2026-06-16 20:11:55', '2026-06-16 22:12:25', '2.0', '136.00', '1', '66', '李强');
INSERT INTO `ktv_record` VALUES ('20', '2', '71', '2026-06-16 22:12:33', '2026-06-16 22:13:09', '0.0', '0.00', '1', '66', '李强');
INSERT INTO `ktv_record` VALUES ('21', '2', '72', '2026-06-17 08:11:14', '2026-06-17 09:11:17', '0.0', '0.00', '1', '122', '李强');
INSERT INTO `ktv_record` VALUES ('22', '4', '76', '2026-06-17 13:29:28', '2026-06-17 13:29:33', '0.0', '0.00', '1', '103', '李强');
INSERT INTO `ktv_record` VALUES ('23', '5', '111', '2026-06-17 13:07:48', '2026-06-17 14:07:51', '0.0', '0.00', '1', '106', '李强');
INSERT INTO `ktv_record` VALUES ('24', '1', '111', '2026-06-17 13:10:34', '2026-06-17 14:10:55', '1.0', '68.00', '1', '108', '李强');
INSERT INTO `ktv_record` VALUES ('25', '7', '111', '2026-06-17 13:19:20', '2026-06-17 14:19:33', '1.0', '148.00', '1', '110', '李强');
INSERT INTO `ktv_record` VALUES ('26', '1', '111', '2026-06-17 12:22:18', '2026-06-17 14:22:30', '2.0', '136.00', '1', '112', '李强');
INSERT INTO `ktv_record` VALUES ('27', '1', '72', '2026-06-18 01:41:35', '2026-06-18 01:41:39', '0.0', '0.00', '1', '122', '李强');
INSERT INTO `ktv_record` VALUES ('28', '1', '72', '2026-06-18 04:23:52', '2026-06-20 23:47:19', '3.0', '204.00', '1', '122', '李强');
INSERT INTO `ktv_record` VALUES ('29', '2', '112', '2026-06-18 11:34:36', '2026-06-20 23:47:54', '5.0', '540.00', '1', '118', '李强');
INSERT INTO `ktv_record` VALUES ('30', '8', '113', '2026-06-17 11:39:05', null, null, null, '0', null, '李强');
INSERT INTO `ktv_record` VALUES ('31', '3', '72', '2026-06-21 00:49:43', '2026-06-21 00:49:53', '8.0', '1344.00', '1', '122', '李强');
INSERT INTO `ktv_record` VALUES ('32', '4', '72', '2026-06-21 01:13:13', '2026-06-24 21:44:29', '7.0', '686.00', '1', '122', '李强');
INSERT INTO `ktv_record` VALUES ('33', '1', '16', '2026-06-24 21:43:17', '2026-06-25 10:30:31', '8.0', '544.00', '1', null, '李强');

-- ----------------------------
-- Table structure for `ktv_room`
-- ----------------------------
DROP TABLE IF EXISTS `ktv_room`;
CREATE TABLE `ktv_room` (
  `ktv_id` int NOT NULL AUTO_INCREMENT COMMENT '包房编号',
  `room_name` varchar(50) NOT NULL COMMENT '包房名称',
  `room_type` varchar(20) DEFAULT NULL COMMENT '包房类型',
  `hourly_rate` decimal(10,2) NOT NULL COMMENT '每小时单价',
  `min_hours` int DEFAULT '1' COMMENT '最低消费时长',
  `status` varchar(20) DEFAULT '空闲' COMMENT '状态',
  `description` text COMMENT '描述',
  `max_hour` int NOT NULL DEFAULT '6' COMMENT '包间最大计费时长(小时)',
  PRIMARY KEY (`ktv_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='KTV包房表';

-- ----------------------------
-- Records of ktv_room
-- ----------------------------
INSERT INTO `ktv_room` VALUES ('1', 'K1', '小包', '68.00', '1', '打扫中', '可容4-6人', '3');
INSERT INTO `ktv_room` VALUES ('2', 'K2', '中包', '108.00', '2', '空闲', '可容8-10人', '5');
INSERT INTO `ktv_room` VALUES ('3', 'K3', '大包', '168.00', '2', '维修中', '可容15-20人', '6');
INSERT INTO `ktv_room` VALUES ('4', 'K4', '小包', '98.00', '1', '空闲', '4-6人小包，独立空调控温、高清点歌系统、皮质小沙发', '3');
INSERT INTO `ktv_room` VALUES ('5', 'K5', '小包', '118.00', '1', '空闲', '4-6人小包，独立卫生间+中央空调，新款触控点歌面板', '3');
INSERT INTO `ktv_room` VALUES ('6', 'K6', '中包', '128.00', '2', '打扫中', '8-10人中包，变频独立空调、升级音箱、长条连体沙发', '5');
INSERT INTO `ktv_room` VALUES ('7', 'K7', '中包', '148.00', '2', '打扫中', '8-10人中包，变频独立空调、升级音箱、长条连体沙发', '5');
INSERT INTO `ktv_room` VALUES ('8', 'K8', '大包', '188.00', '2', '使用中', '15-20人大包，内设休闲沙发区、大功率音响、超大茶几', '6');
INSERT INTO `ktv_room` VALUES ('9', 'K9', '大包', '208.00', '2', '空闲', '15-20人大包，独立卫浴+全屋空调，派对优选大包房型', '6');
INSERT INTO `ktv_room` VALUES ('10', 'K10', 'VIP包', '268.00', '2', '空闲', '25-30人VIP房，独立卫生间+专属休息区、顶配专业舞台音响', '8');
INSERT INTO `ktv_room` VALUES ('11', 'K11', 'VIP包', '278.00', '2', '空闲', '25-30人尊享VIP，私密休息隔间、独立卫浴、大屏投影点歌', '8');
INSERT INTO `ktv_room` VALUES ('12', 'K12', '商务包', '198.00', '2', '空闲', '12-15人商务房，高清投影设备、商务洽谈小茶区、降噪音响', '7');

-- ----------------------------
-- Table structure for `menu`
-- ----------------------------
DROP TABLE IF EXISTS `menu`;
CREATE TABLE `menu` (
  `menu_id` int NOT NULL AUTO_INCREMENT COMMENT '菜品编号',
  `name` varchar(100) NOT NULL COMMENT '菜品名称',
  `category` varchar(50) DEFAULT NULL COMMENT '分类',
  `price` decimal(10,2) NOT NULL COMMENT '价格',
  `unit` varchar(20) DEFAULT '份' COMMENT '单位',
  `image` varchar(500) DEFAULT NULL COMMENT '图片路径',
  `status` varchar(20) DEFAULT '在售' COMMENT '状态',
  `description` text COMMENT '描述',
  `stock_quantity` int NOT NULL DEFAULT '0' COMMENT '库存数量',
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜单表';

-- ----------------------------
-- Records of menu
-- ----------------------------
INSERT INTO `menu` VALUES ('1', '宫保鸡丁', '热菜', '48.00', '份', '/uploads/food/宫保鸡丁.jpg', '在售', '微辣开胃', '985');
INSERT INTO `menu` VALUES ('2', '清蒸鲈鱼', '热菜', '98.00', '条', '/uploads/food/清蒸鲈鱼.jpg', '在售', '肉质细嫩', '994');
INSERT INTO `menu` VALUES ('3', '酸菜鱼', '热菜', '68.00', '份', '/uploads/food/酸菜鱼.jpg', '在售', '鱼肉滑嫩', '997');
INSERT INTO `menu` VALUES ('4', '时蔬', '热菜', '28.00', '份', '/uploads/food/时蔬.jpg', '在售', '清淡爽口', '998');
INSERT INTO `menu` VALUES ('5', '米饭', '主食', '3.00', '碗', '/uploads/food/米饭.jpg', '在售', '香软可口', '999');
INSERT INTO `menu` VALUES ('6', '青岛啤酒', '酒水', '15.00', '瓶', '/uploads/food/青岛啤酒.jpg', '在售', '口感纯正', '995');
INSERT INTO `menu` VALUES ('7', '可乐', '酒水', '8.00', '听', '/uploads/food/可乐.jpg', '在售', '解渴消暑', '998');
INSERT INTO `menu` VALUES ('8', '水果拼盘', '凉菜', '38.00', '例', '/uploads/food/水果拼盘.jpg', '在售', '营养准衡', '99');
INSERT INTO `menu` VALUES ('9', '北京烤鸭', '热菜', '188.00', '只', '/uploads/food/北京烤鸭.jpg', '在售', '皮脆肉香，需提前预约', '999');
INSERT INTO `menu` VALUES ('10', '凉拌秋葵', '凉菜', '22.00', '份', '/uploads/food/凉拌秋葵.jpg', '在售', '清爽开胃', '998');
INSERT INTO `menu` VALUES ('11', '擂椒皮蛋', '凉菜', '18.00', '份', '/uploads/food/擂椒皮蛋.jpg', '在售', '香辣可口', '998');
INSERT INTO `menu` VALUES ('12', '凉拌豆皮', '凉菜', '14.00', '份', '/uploads/food/凉拌豆皮.jpg', '在售', '豆香浓郁', '1');
INSERT INTO `menu` VALUES ('13', '凉拌牛肉', '凉菜', '42.00', '份', '/uploads/food/凉拌牛肉.jpg', '在售', '麻辣鲜香', '1000');
INSERT INTO `menu` VALUES ('14', '凉拌鸡爪', '凉菜', '28.00', '份', '/uploads/food/凉拌鸡爪.jpg', '在售', 'Q弹入味', '999');
INSERT INTO `menu` VALUES ('15', '红烧肉', '热菜', '39.00', '份', '/uploads/food/红烧肉.jpg', '在售', '肥而不腻', '999');
INSERT INTO `menu` VALUES ('16', '鱼香肉丝', '热菜', '30.00', '份', '/uploads/food/鱼香肉丝.jpg', '在售', '酸甜微辣', '999');
INSERT INTO `menu` VALUES ('17', '麻婆豆腐', '热菜', '22.00', '份', '/uploads/food/麻婆豆腐.jpg', '在售', '麻辣嫩滑', '999');
INSERT INTO `menu` VALUES ('18', '水煮肉片', '热菜', '46.00', '份', '/uploads/food/水煮肉片.jpg', '在售', '麻辣鲜香', '999');
INSERT INTO `menu` VALUES ('19', '锅包肉', '热菜', '36.00', '份', '/uploads/food/锅包肉.jpg', '在售', '酸甜酥脆', '999');
INSERT INTO `menu` VALUES ('20', '彩虹鸡尾酒', '酒水', '45.00', '杯', '/uploads/food/彩虹鸡尾酒.jpg', '在售', '色彩缤纷', '999');
INSERT INTO `menu` VALUES ('21', '纯粮白酒', '酒水', '68.00', '瓶', '/uploads/food/纯粮白酒.jpg', '在售', '清香型', '999');
INSERT INTO `menu` VALUES ('22', '干红葡萄酒', '酒水', '128.00', '瓶', '/uploads/food/干红葡萄酒.jpg', '在售', '进口红酒', '999');
INSERT INTO `menu` VALUES ('23', '鲜榨橙汁', '饮品', '22.00', '杯', '/uploads/food/鲜榨橙汁.jpg', '在售', '新鲜现榨', '999');
INSERT INTO `menu` VALUES ('24', '原味老酸奶', '饮品', '15.00', '杯', '/uploads/food/原味老酸奶.jpg', '在售', '传统工艺', '999');
INSERT INTO `menu` VALUES ('25', '珍珠奶茶', '饮品', '18.00', '杯', '/uploads/food/珍珠奶茶.jpg', '在售', 'Q弹珍珠', '999');

-- ----------------------------
-- Table structure for `message`
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
  `message_id` int NOT NULL AUTO_INCREMENT COMMENT '留言编号',
  `guest_id` int NOT NULL COMMENT '客人编号',
  `content` text NOT NULL COMMENT '留言内容',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '留言时间',
  `is_read` tinyint DEFAULT '0' COMMENT '是否已读 0-未读 1-已读',
  `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
  `operator` varchar(50) DEFAULT NULL COMMENT '登记人',
  `msg_type` varchar(20) DEFAULT '普通留言',
  PRIMARY KEY (`message_id`),
  KEY `guest_id` (`guest_id`),
  CONSTRAINT `message_ibfk_1` FOREIGN KEY (`guest_id`) REFERENCES `guest` (`guest_id`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='留言信息表';

-- ----------------------------
-- Records of message
-- ----------------------------
INSERT INTO `message` VALUES ('1', '9', '302客人需要加一床被子', '2026-05-28 08:30:00', '1', '2026-06-06 22:12:51', '前台王敏', '普通留言');
INSERT INTO `message` VALUES ('2', '10', '103房间张学友先生询问附近健身房', '2026-05-27 21:00:00', '1', '2026-05-27 21:30:00', '前台张丽华', '普通留言');
INSERT INTO `message` VALUES ('3', '11', '202房间林青霞女士留言：请明早7点叫早', '2026-05-27 22:15:00', '1', '2026-06-06 22:12:52', '夜班李强', '叫早服务');
INSERT INTO `message` VALUES ('4', '13', '退房客人周润发感谢酒店服务', '2026-05-23 10:00:00', '1', '2026-05-23 10:05:00', '前台王敏', '感谢留言');
INSERT INTO `message` VALUES ('5', '15', '换房记录：从 304 换至 201', '2026-06-05 20:41:33', '1', '2026-06-06 22:12:51', null, '换房记录');
INSERT INTO `message` VALUES ('6', '12', '换房记录：从 301 换至 101', '2026-06-05 20:42:00', '1', '2026-06-06 22:12:51', null, '换房记录');
INSERT INTO `message` VALUES ('7', '12', '换房记录：从 101 换至 301', '2026-06-05 20:42:18', '1', '2026-06-06 22:12:50', null, '换房记录');
INSERT INTO `message` VALUES ('8', '12', '换房记录：从 301 换至 304', '2026-06-05 20:42:22', '1', '2026-06-06 17:31:17', null, '换房记录');
INSERT INTO `message` VALUES ('9', '16', '换房记录：从 303 换至 101', '2026-06-06 17:31:58', '1', '2026-06-06 22:12:49', null, '换房记录');
INSERT INTO `message` VALUES ('10', '16', '换房记录：从 101 换至 303', '2026-06-06 22:10:29', '1', '2026-06-06 22:12:49', null, '换房记录');
INSERT INTO `message` VALUES ('11', '9', '换房记录：从 102 换至 302', '2026-06-06 22:10:33', '1', '2026-06-06 22:12:48', null, '换房记录');
INSERT INTO `message` VALUES ('12', '25', '换房记录：从 103 换至 203', '2026-06-07 00:16:52', '0', null, null, '换房记录');
INSERT INTO `message` VALUES ('13', '35', '换房记录：从 304 换至 103', '2026-06-07 01:26:28', '0', null, null, '换房记录');
INSERT INTO `message` VALUES ('14', '28', '换房记录：从 101 换至 304', '2026-06-07 01:26:49', '0', null, null, '换房记录');
INSERT INTO `message` VALUES ('15', '27', '大番薯', '2026-06-07 17:51:08', '0', null, null, '叫早服务');
INSERT INTO `message` VALUES ('16', '27', '物品损坏：杯子，赔偿金额：4', '2026-06-09 18:43:32', '0', null, '张丽华', '损坏记录');
INSERT INTO `message` VALUES ('17', '27', '换房记录：从 102 换至 301', '2026-06-09 18:56:30', '0', null, '张丽华', '换房记录');
INSERT INTO `message` VALUES ('18', '35', '物品损坏：大巴，赔偿金额：50', '2026-06-09 22:38:34', '0', null, '张丽华', '损坏记录');
INSERT INTO `message` VALUES ('19', '35', '换房记录：从 103 换至 102', '2026-06-09 22:38:41', '0', null, '张丽华', '换房记录');
INSERT INTO `message` VALUES ('20', '58', '换房记录：从 101 换至 302', '2026-06-09 23:56:50', '0', null, '张丽华', '换房记录');
INSERT INTO `message` VALUES ('21', '28', '拿双拖鞋', '2026-06-10 00:07:03', '1', '2026-06-10 00:07:42', null, '普通留言');
INSERT INTO `message` VALUES ('22', '39', '333', '2026-06-10 00:54:25', '1', '2026-06-10 00:55:17', null, '普通留言');
INSERT INTO `message` VALUES ('23', '16', '物品损坏：水杯，赔偿金额：10', '2026-06-13 17:32:35', '0', null, '张丽华', '损坏记录');
INSERT INTO `message` VALUES ('24', '71', '物品损坏：玻璃杯，赔偿金额：10', '2026-06-16 22:11:03', '0', null, '张丽华', '损坏记录');
INSERT INTO `message` VALUES ('25', '72', '换房记录：从 102 换至 203', '2026-06-17 00:23:03', '0', null, '张丽华', '换房记录');
INSERT INTO `message` VALUES ('26', '72', '换房记录：从 203 换至 302', '2026-06-17 00:23:49', '0', null, '张丽华', '换房记录');
INSERT INTO `message` VALUES ('27', '113', '物品损坏：杯子一个，赔偿金额：100', '2026-06-18 11:00:01', '0', null, '张丽华', '损坏记录');
INSERT INTO `message` VALUES ('28', '113', '物品损坏：床单，赔偿金额：5', '2026-06-18 17:42:57', '0', null, '张丽华', '损坏记录');
INSERT INTO `message` VALUES ('29', '112', '换房记录：从 302 换至 304', '2026-06-20 02:58:33', '0', null, '张丽华', '换房记录');
INSERT INTO `message` VALUES ('30', '112', '换房记录：从 304 换至 202', '2026-06-20 03:01:22', '0', null, '张丽华', '换房记录');
INSERT INTO `message` VALUES ('31', '113', '物品损坏：杯子，赔偿金额：10', '2026-06-20 03:34:39', '0', null, '张丽华', '损坏记录');
INSERT INTO `message` VALUES ('32', '112', '需要一床厚被子', '2026-06-20 03:47:36', '0', null, null, '普通留言');
INSERT INTO `message` VALUES ('33', '113', '物品损坏：杯子，赔偿金额：100', '2026-06-25 08:58:54', '0', null, '张丽华', '损坏记录');
INSERT INTO `message` VALUES ('34', '113', '换房记录：从 101 换至 104', '2026-06-25 08:59:15', '0', null, '张丽华', '换房记录');
INSERT INTO `message` VALUES ('35', '134', '换房记录：从 103 换至 101', '2026-06-25 09:00:43', '0', null, '张丽华', '换房记录');
INSERT INTO `message` VALUES ('36', '113', '8:00', '2026-06-25 09:01:31', '1', '2026-06-25 10:29:20', null, '叫早服务');
INSERT INTO `message` VALUES ('37', '113', '8点', '2026-06-27 01:37:57', '0', null, null, '叫早服务');

-- ----------------------------
-- Table structure for `order_detail`
-- ----------------------------
DROP TABLE IF EXISTS `order_detail`;
CREATE TABLE `order_detail` (
  `detail_id` int NOT NULL AUTO_INCREMENT COMMENT '明细编号',
  `order_id` int DEFAULT NULL,
  `item_type` varchar(20) NOT NULL COMMENT '消费类型',
  `item_name` varchar(200) NOT NULL COMMENT '项目名称',
  `quantity` int DEFAULT '1' COMMENT '数量',
  `price` decimal(10,2) NOT NULL COMMENT '单价',
  `amount` decimal(10,2) DEFAULT NULL COMMENT '金额',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '消费时间',
  `operator` varchar(50) DEFAULT NULL COMMENT '操作员',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `guest_id` int NOT NULL,
  `is_settled` tinyint DEFAULT '0' COMMENT '0未结账，1已结账',
  PRIMARY KEY (`detail_id`),
  KEY `order_id` (`order_id`),
  KEY `guest_id` (`guest_id`),
  CONSTRAINT `order_detail_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `order_main` (`order_id`),
  CONSTRAINT `order_detail_ibfk_2` FOREIGN KEY (`guest_id`) REFERENCES `guest` (`guest_id`)
) ENGINE=InnoDB AUTO_INCREMENT=282 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='消费明细表';

-- ----------------------------
-- Records of order_detail
-- ----------------------------
INSERT INTO `order_detail` VALUES ('1', '2', '餐饮', '宫保鸡丁', '1', '48.00', '48.00', '2026-05-27 19:00:00', '李强', '', '10', '0');
INSERT INTO `order_detail` VALUES ('2', '2', '餐饮', '米饭', '2', '3.00', '6.00', '2026-05-27 19:00:00', '李强', '', '10', '0');
INSERT INTO `order_detail` VALUES ('3', '2', '餐饮', '青岛啤酒', '2', '15.00', '30.00', '2026-05-27 19:30:00', '李强', '', '10', '0');
INSERT INTO `order_detail` VALUES ('4', '11', '餐饮', '清蒸鲈鱼', '1', '98.00', '98.00', '2026-05-26 12:30:00', '李强', '', '11', '1');
INSERT INTO `order_detail` VALUES ('5', '11', '餐饮', '时蔬', '1', '28.00', '28.00', '2026-05-26 12:30:00', '李强', '', '11', '1');
INSERT INTO `order_detail` VALUES ('6', '5', '房费', '房费', '3', '198.00', '594.00', '2026-05-23 10:00:00', '系统', '', '13', '0');
INSERT INTO `order_detail` VALUES ('7', '5', '餐饮', '早餐', '2', '38.00', '76.00', '2026-05-21 08:00:00', '李强', '', '13', '0');
INSERT INTO `order_detail` VALUES ('8', '5', '餐饮', '晚餐自助', '2', '85.00', '170.00', '2026-05-22 18:00:00', '李强', '', '13', '0');
INSERT INTO `order_detail` VALUES ('9', '6', '房费', '房费', '3', '188.00', '564.00', '2026-05-25 10:00:00', '系统', '', '14', '0');
INSERT INTO `order_detail` VALUES ('10', '6', '餐饮', '矿泉水', '2', '5.00', '10.00', '2026-05-23 09:00:00', '李强', '', '14', '0');
INSERT INTO `order_detail` VALUES ('11', '6', '餐饮', '咖啡', '1', '25.00', '25.00', '2026-05-24 15:00:00', '李强', '', '14', '0');
INSERT INTO `order_detail` VALUES ('12', '6', '杂项', '洗衣服务', '1', '50.00', '50.00', '2026-05-24 10:00:00', '前台王敏', '西装干洗', '14', '0');
INSERT INTO `order_detail` VALUES ('13', null, 'KTV', 'K1 0.7小时', '1', '47.60', '47.60', '2026-06-02 10:22:13', '李强', null, '16', '0');
INSERT INTO `order_detail` VALUES ('14', null, 'KTV', 'K1 0.0小时', '1', '0.00', '0.00', '2026-06-02 10:23:23', '李强', null, '16', '0');
INSERT INTO `order_detail` VALUES ('15', null, 'KTV', 'K1 0.0小时', '1', '0.00', '0.00', '2026-06-02 10:39:02', '李强', null, '16', '0');
INSERT INTO `order_detail` VALUES ('16', null, 'KTV', 'K1 0.0小时', '1', '0.00', '0.00', '2026-06-02 10:55:45', '李强', null, '16', '0');
INSERT INTO `order_detail` VALUES ('17', null, 'KTV', 'K1 0.0小时', '1', '0.00', '0.00', '2026-06-02 12:03:24', '李强', null, '12', '0');
INSERT INTO `order_detail` VALUES ('18', null, 'KTV', 'K1 5.0小时', '1', '340.00', '340.00', '2026-06-02 12:04:30', '李强', null, '12', '0');
INSERT INTO `order_detail` VALUES ('19', null, '餐饮', '酸菜鱼', '1', '68.00', '68.00', '2026-06-02 12:35:35', '李强', null, '16', '0');
INSERT INTO `order_detail` VALUES ('20', null, '餐饮', '水果拼盘', '1', '38.00', '38.00', '2026-06-02 13:20:03', '李强', null, '16', '0');
INSERT INTO `order_detail` VALUES ('21', null, '餐饮', '时蔬', '1', '28.00', '28.00', '2026-06-02 13:20:03', '李强', null, '16', '0');
INSERT INTO `order_detail` VALUES ('22', null, '餐饮', '水果拼盘', '2', '38.00', '76.00', '2026-06-02 13:48:08', '李强', null, '16', '0');
INSERT INTO `order_detail` VALUES ('23', null, '餐饮', '清蒸鲈鱼', '1', '98.00', '98.00', '2026-06-02 13:48:08', '李强', null, '16', '0');
INSERT INTO `order_detail` VALUES ('24', null, '餐饮', '宫保鸡丁', '1', '48.00', '48.00', '2026-06-02 13:48:08', '李强', null, '16', '0');
INSERT INTO `order_detail` VALUES ('25', null, 'KTV', 'K1 0.0小时', '1', '0.00', '0.00', '2026-06-02 15:10:23', '李强', null, '16', '0');
INSERT INTO `order_detail` VALUES ('26', null, '餐饮', '宫保鸡丁', '1', '48.00', '48.00', '2026-06-02 15:10:38', '李强', null, '16', '0');
INSERT INTO `order_detail` VALUES ('27', null, 'KTV', 'K1 0.0小时', '1', '0.00', '0.00', '2026-06-03 11:19:06', '李强', null, '16', '0');
INSERT INTO `order_detail` VALUES ('28', '11', '餐饮', '凉拌豆皮', '1', '14.00', '14.00', '2026-06-05 09:02:25', '李强', null, '11', '1');
INSERT INTO `order_detail` VALUES ('29', '11', '餐饮', '擂椒皮蛋', '1', '18.00', '18.00', '2026-06-05 09:02:25', '李强', null, '11', '1');
INSERT INTO `order_detail` VALUES ('30', '11', '餐饮', '凉拌秋葵', '1', '22.00', '22.00', '2026-06-05 09:02:25', '李强', null, '11', '1');
INSERT INTO `order_detail` VALUES ('31', '11', '餐饮', '北京烤鸭', '1', '188.00', '188.00', '2026-06-05 09:02:25', '李强', null, '11', '1');
INSERT INTO `order_detail` VALUES ('32', null, 'KTV', 'K1 0.0小时', '1', '0.00', '0.00', '2026-06-05 09:03:22', '李强', null, '11', '0');
INSERT INTO `order_detail` VALUES ('33', null, 'KTV', 'K2 0.0小时', '1', '0.00', '0.00', '2026-06-05 09:05:31', '李强', null, '12', '0');
INSERT INTO `order_detail` VALUES ('34', '9', '餐饮', '宫保鸡丁', '1', '48.00', '48.00', '2026-06-06 00:46:12', '李强', null, '12', '1');
INSERT INTO `order_detail` VALUES ('35', '51', '餐饮', '清蒸鲈鱼', '1', '98.00', '98.00', '2026-06-07 17:32:36', '李强', null, '39', '1');
INSERT INTO `order_detail` VALUES ('36', '51', '餐饮', '宫保鸡丁', '1', '48.00', '48.00', '2026-06-07 17:32:36', '李强', null, '39', '1');
INSERT INTO `order_detail` VALUES ('37', null, 'KTV', 'K1 3.0小时', '1', '204.00', '204.00', '2026-06-07 17:36:58', '李强', null, '11', '0');
INSERT INTO `order_detail` VALUES ('38', '51', '餐饮', '青岛啤酒', '3', '15.00', '45.00', '2026-06-07 20:19:21', '李强', null, '39', '1');
INSERT INTO `order_detail` VALUES ('39', null, 'KTV', 'K6 0.0小时', '1', '0.00', '0.00', '2026-06-07 20:56:33', '李强', null, '39', '0');
INSERT INTO `order_detail` VALUES ('40', '45', '杂项', '物品损坏赔偿 - 杯子', '1', '4.00', '4.00', '2026-06-09 18:43:32', '张丽华', null, '27', '1');
INSERT INTO `order_detail` VALUES ('41', null, '餐饮', '时蔬', '2', '28.00', '56.00', '2026-06-09 21:38:56', '李强', null, '16', '0');
INSERT INTO `order_detail` VALUES ('42', null, '餐饮', '酸菜鱼', '3', '68.00', '204.00', '2026-06-09 21:38:56', '李强', null, '16', '0');
INSERT INTO `order_detail` VALUES ('43', null, '餐饮', '清蒸鲈鱼', '2', '98.00', '196.00', '2026-06-09 21:38:56', '李强', null, '16', '0');
INSERT INTO `order_detail` VALUES ('44', '8', 'KTV', 'K3 6.0小时', '1', '1008.00', '1008.00', '2026-06-09 21:39:10', '李强', null, '16', '1');
INSERT INTO `order_detail` VALUES ('45', '45', '餐饮', '擂椒皮蛋', '2', '18.00', '36.00', '2026-06-09 22:32:52', '李强', null, '27', '1');
INSERT INTO `order_detail` VALUES ('46', '8', 'KTV', 'K1 3.0小时', '1', '204.00', '204.00', '2026-06-09 22:36:37', '李强', null, '16', '1');
INSERT INTO `order_detail` VALUES ('47', '54', '杂项', '物品损坏赔偿 - 大巴', '1', '50.00', '50.00', '2026-06-09 22:38:34', '张丽华', null, '35', '1');
INSERT INTO `order_detail` VALUES ('48', '41', '餐饮', '宫保鸡丁', '4', '48.00', '192.00', '2026-06-09 23:26:32', '李强', null, '57', '1');
INSERT INTO `order_detail` VALUES ('49', '44', '餐饮', '凉拌豆皮', '1', '14.00', '14.00', '2026-06-10 01:25:09', '孙敏', null, '60', '1');
INSERT INTO `order_detail` VALUES ('50', null, '餐饮', '宫保鸡丁', '1', '48.00', '48.00', '2026-06-10 16:32:44', '李强', null, '16', '0');
INSERT INTO `order_detail` VALUES ('51', '34', 'KTV', 'K3 6.0小时', '1', '1008.00', '1008.00', '2026-06-10 20:19:45', '李强', null, '57', '1');
INSERT INTO `order_detail` VALUES ('52', null, '杂项', '物品损坏赔偿 - 水杯', '1', '10.00', '10.00', '2026-06-13 17:32:35', '张丽华', null, '16', '0');
INSERT INTO `order_detail` VALUES ('53', '8', 'KTV', 'K1 3.0小时', '1', '204.00', '204.00', '2026-06-13 18:47:30', '李强', null, '16', '1');
INSERT INTO `order_detail` VALUES ('54', '24', 'KTV', 'K4 3.0小时', '1', '294.00', '294.00', '2026-06-14 02:07:27', '李强', null, '34', '1');
INSERT INTO `order_detail` VALUES ('55', '66', '杂项', '物品损坏赔偿 - 玻璃杯', '1', '10.00', '10.00', '2026-06-16 22:11:03', '张丽华', null, '71', '1');
INSERT INTO `order_detail` VALUES ('56', '66', '餐饮', '清蒸鲈鱼', '1', '98.00', '98.00', '2026-06-16 22:11:47', '李强', null, '71', '1');
INSERT INTO `order_detail` VALUES ('57', '66', '餐饮', '宫保鸡丁', '1', '48.00', '48.00', '2026-06-16 22:11:47', '李强', null, '71', '1');
INSERT INTO `order_detail` VALUES ('58', '66', '餐饮', '时蔬', '1', '28.00', '28.00', '2026-06-16 22:11:47', '李强', null, '71', '1');
INSERT INTO `order_detail` VALUES ('59', '66', '餐饮', '可乐', '1', '8.00', '8.00', '2026-06-16 22:11:47', '李强', null, '71', '1');
INSERT INTO `order_detail` VALUES ('60', null, 'KTV', 'K1 2.0小时', '1', '136.00', '136.00', '2026-06-16 22:12:25', '李强', null, '71', '1');
INSERT INTO `order_detail` VALUES ('61', null, 'KTV', 'K2 0.0小时', '1', '0.00', '0.00', '2026-06-16 22:13:09', '李强', null, '71', '1');
INSERT INTO `order_detail` VALUES ('62', null, '杂项', '洗衣费', '1', '50.00', '50.00', '2026-06-17 00:41:14', '陈芳', '', '16', '0');
INSERT INTO `order_detail` VALUES ('63', null, '杂项', '加床', '1', '10.00', '10.00', '2026-06-17 01:10:09', '陈芳', '', '16', '0');
INSERT INTO `order_detail` VALUES ('64', null, '杂项', '床单脏污清洗', '1', '50.00', '50.00', '2026-06-17 01:18:55', '陈芳', '', '16', '0');
INSERT INTO `order_detail` VALUES ('65', '122', '餐饮', '擂椒皮蛋', '1', '18.00', '18.00', '2026-06-17 01:49:08', '李强', null, '72', '1');
INSERT INTO `order_detail` VALUES ('66', '122', '餐饮', '青岛啤酒', '4', '15.00', '60.00', '2026-06-17 01:49:08', '李强', null, '72', '1');
INSERT INTO `order_detail` VALUES ('67', '122', '餐饮', '清蒸鲈鱼', '1', '98.00', '98.00', '2026-06-17 01:49:08', '李强', null, '72', '1');
INSERT INTO `order_detail` VALUES ('68', '122', '杂项', '冰箱可乐', '1', '10.00', '10.00', '2026-06-17 01:50:10', '陈芳', '', '72', '1');
INSERT INTO `order_detail` VALUES ('69', '122', '餐饮', '宫保鸡丁', '1', '48.00', '48.00', '2026-06-17 09:11:02', '李强', null, '72', '1');
INSERT INTO `order_detail` VALUES ('70', null, 'KTV', 'K2 0.0小时', '1', '0.00', '0.00', '2026-06-17 09:11:17', '李强', null, '72', '1');
INSERT INTO `order_detail` VALUES ('71', '69', '房费', '房费（3天）', '3', '188.00', '564.00', '2026-01-18 10:00:00', '系统', null, '79', '1');
INSERT INTO `order_detail` VALUES ('72', '69', '餐饮', '宫保鸡丁', '1', '48.00', '48.00', '2026-01-16 18:00:00', '李强', null, '79', '1');
INSERT INTO `order_detail` VALUES ('73', '69', '餐饮', '米饭', '2', '3.00', '6.00', '2026-01-16 18:00:00', '李强', null, '79', '1');
INSERT INTO `order_detail` VALUES ('74', '69', '餐饮', '青岛啤酒', '2', '15.00', '30.00', '2026-01-16 19:00:00', '李强', null, '79', '1');
INSERT INTO `order_detail` VALUES ('75', '69', '餐饮', '可乐', '2', '8.00', '16.00', '2026-01-17 12:00:00', '李强', null, '79', '1');
INSERT INTO `order_detail` VALUES ('76', '70', '房费', '房费（2天）', '2', '258.00', '516.00', '2026-02-12 09:00:00', '系统', null, '80', '1');
INSERT INTO `order_detail` VALUES ('77', '70', '餐饮', '清蒸鲈鱼', '1', '98.00', '98.00', '2026-02-10 19:00:00', '李强', null, '80', '1');
INSERT INTO `order_detail` VALUES ('78', '70', '餐饮', '时蔬', '1', '28.00', '28.00', '2026-02-10 19:00:00', '李强', null, '80', '1');
INSERT INTO `order_detail` VALUES ('79', '70', '餐饮', '米饭', '2', '3.00', '6.00', '2026-02-11 12:30:00', '李强', null, '80', '1');
INSERT INTO `order_detail` VALUES ('80', '70', '餐饮', '鲜榨橙汁', '1', '22.00', '22.00', '2026-02-11 12:30:00', '李强', null, '80', '1');
INSERT INTO `order_detail` VALUES ('81', '71', '房费', '房费（5天）', '5', '198.00', '990.00', '2026-03-10 11:00:00', '系统', null, '81', '1');
INSERT INTO `order_detail` VALUES ('82', '71', '餐饮', '酸菜鱼', '1', '68.00', '68.00', '2026-03-06 18:30:00', '李强', null, '81', '1');
INSERT INTO `order_detail` VALUES ('83', '71', '餐饮', '水煮肉片', '1', '46.00', '46.00', '2026-03-06 18:30:00', '李强', null, '81', '1');
INSERT INTO `order_detail` VALUES ('84', '71', '餐饮', '米饭', '3', '3.00', '9.00', '2026-03-06 18:30:00', '李强', null, '81', '1');
INSERT INTO `order_detail` VALUES ('85', '71', '餐饮', '青岛啤酒', '5', '15.00', '75.00', '2026-03-07 20:00:00', '李强', null, '81', '1');
INSERT INTO `order_detail` VALUES ('86', '71', '餐饮', '干红葡萄酒', '1', '128.00', '128.00', '2026-03-08 19:00:00', '李强', null, '81', '1');
INSERT INTO `order_detail` VALUES ('87', '72', '房费', '房费（4天）', '4', '268.00', '1072.00', '2026-04-24 08:00:00', '系统', null, '82', '1');
INSERT INTO `order_detail` VALUES ('88', '72', '餐饮', '北京烤鸭', '1', '188.00', '188.00', '2026-04-21 19:00:00', '李强', null, '82', '1');
INSERT INTO `order_detail` VALUES ('89', '72', '餐饮', '可乐', '3', '8.00', '24.00', '2026-04-21 19:00:00', '李强', null, '82', '1');
INSERT INTO `order_detail` VALUES ('90', '72', '餐饮', '凉拌秋葵', '1', '22.00', '22.00', '2026-04-22 12:30:00', '李强', null, '82', '1');
INSERT INTO `order_detail` VALUES ('91', '72', '杂项', '洗衣服务', '1', '50.00', '50.00', '2026-04-23 10:00:00', '前台', null, '82', '1');
INSERT INTO `order_detail` VALUES ('92', '73', '房费', '房费（2天）', '2', '268.00', '536.00', '2026-05-03 09:00:00', '系统', null, '83', '1');
INSERT INTO `order_detail` VALUES ('93', '73', '餐饮', '鱼香肉丝', '1', '30.00', '30.00', '2026-05-01 18:00:00', '李强', null, '83', '1');
INSERT INTO `order_detail` VALUES ('94', '73', '餐饮', '锅包肉', '1', '36.00', '36.00', '2026-05-01 18:00:00', '李强', null, '83', '1');
INSERT INTO `order_detail` VALUES ('95', '73', '餐饮', '米饭', '2', '3.00', '6.00', '2026-05-01 18:00:00', '李强', null, '83', '1');
INSERT INTO `order_detail` VALUES ('96', '73', '餐饮', '青岛啤酒', '4', '15.00', '60.00', '2026-05-02 19:00:00', '李强', null, '83', '1');
INSERT INTO `order_detail` VALUES ('97', '73', '餐饮', '原味老酸奶', '2', '15.00', '30.00', '2026-05-02 19:00:00', '李强', null, '83', '1');
INSERT INTO `order_detail` VALUES ('98', '74', '房费', '房费（3天）', '3', '198.00', '594.00', '2026-05-18 10:00:00', '系统', null, '84', '1');
INSERT INTO `order_detail` VALUES ('99', '74', '餐饮', '宫保鸡丁', '2', '48.00', '96.00', '2026-05-16 18:30:00', '李强', null, '84', '1');
INSERT INTO `order_detail` VALUES ('100', '74', '餐饮', '清蒸鲈鱼', '1', '98.00', '98.00', '2026-05-16 18:30:00', '李强', null, '84', '1');
INSERT INTO `order_detail` VALUES ('101', '74', '餐饮', '时蔬', '1', '28.00', '28.00', '2026-05-16 18:30:00', '李强', null, '84', '1');
INSERT INTO `order_detail` VALUES ('102', '74', 'KTV', 'K1 2小时', '1', '136.00', '136.00', '2026-05-17 20:00:00', '孙敏', null, '84', '1');
INSERT INTO `order_detail` VALUES ('103', '75', '房费', '房费（3天）', '3', '188.00', '564.00', '2026-06-04 09:00:00', '系统', null, '85', '1');
INSERT INTO `order_detail` VALUES ('104', '75', '餐饮', '麻婆豆腐', '1', '22.00', '22.00', '2026-06-01 18:00:00', '李强', null, '85', '1');
INSERT INTO `order_detail` VALUES ('105', '75', '餐饮', '水煮肉片', '1', '46.00', '46.00', '2026-06-01 18:00:00', '李强', null, '85', '1');
INSERT INTO `order_detail` VALUES ('106', '75', '餐饮', '米饭', '2', '3.00', '6.00', '2026-06-01 18:00:00', '李强', null, '85', '1');
INSERT INTO `order_detail` VALUES ('107', '75', '餐饮', '青岛啤酒', '3', '15.00', '45.00', '2026-06-02 20:00:00', '李强', null, '85', '1');
INSERT INTO `order_detail` VALUES ('108', '75', '杂项', '加床', '1', '10.00', '10.00', '2026-06-02 21:00:00', '前台', null, '85', '1');
INSERT INTO `order_detail` VALUES ('109', '76', '房费', '房费（2天）', '2', '198.00', '396.00', '2026-06-12 08:00:00', '系统', null, '86', '1');
INSERT INTO `order_detail` VALUES ('110', '76', '餐饮', '擂椒皮蛋', '1', '18.00', '18.00', '2026-06-10 19:00:00', '李强', null, '86', '1');
INSERT INTO `order_detail` VALUES ('111', '76', '餐饮', '凉拌牛肉', '1', '42.00', '42.00', '2026-06-10 19:00:00', '李强', null, '86', '1');
INSERT INTO `order_detail` VALUES ('112', '76', '餐饮', '米饭', '2', '3.00', '6.00', '2026-06-10 19:00:00', '李强', null, '86', '1');
INSERT INTO `order_detail` VALUES ('113', '76', '餐饮', '珍珠奶茶', '2', '18.00', '36.00', '2026-06-11 15:00:00', '李强', null, '86', '1');
INSERT INTO `order_detail` VALUES ('126', '79', '房费', '房费（2天）', '2', '198.00', '396.00', '2026-03-02 10:00:00', '系统', null, '89', '1');
INSERT INTO `order_detail` VALUES ('127', '79', '餐饮', '凉拌牛肉', '1', '42.00', '42.00', '2026-02-28 18:00:00', '李强', null, '89', '1');
INSERT INTO `order_detail` VALUES ('128', '79', '餐饮', '可乐', '3', '8.00', '24.00', '2026-02-28 18:00:00', '李强', null, '89', '1');
INSERT INTO `order_detail` VALUES ('129', '79', '餐饮', '米饭', '2', '3.00', '6.00', '2026-02-28 18:00:00', '李强', null, '89', '1');
INSERT INTO `order_detail` VALUES ('130', '79', '杂项', '洗衣服务', '1', '50.00', '50.00', '2026-03-01 10:00:00', '前台', null, '89', '1');
INSERT INTO `order_detail` VALUES ('131', '80', '房费', '房费（3天）', '3', '488.00', '1464.00', '2026-03-18 09:00:00', '系统', null, '90', '1');
INSERT INTO `order_detail` VALUES ('132', '80', '餐饮', '锅包肉', '2', '36.00', '72.00', '2026-03-16 18:00:00', '李强', null, '90', '1');
INSERT INTO `order_detail` VALUES ('133', '80', '餐饮', '宫保鸡丁', '1', '48.00', '48.00', '2026-03-16 18:00:00', '李强', null, '90', '1');
INSERT INTO `order_detail` VALUES ('134', '80', '餐饮', '米饭', '3', '3.00', '9.00', '2026-03-16 18:00:00', '李强', null, '90', '1');
INSERT INTO `order_detail` VALUES ('135', '80', '餐饮', '青岛啤酒', '5', '15.00', '75.00', '2026-03-17 20:00:00', '李强', null, '90', '1');
INSERT INTO `order_detail` VALUES ('136', '81', '房费', '房费（3天）', '3', '268.00', '804.00', '2026-04-08 10:00:00', '系统', null, '91', '1');
INSERT INTO `order_detail` VALUES ('137', '81', '餐饮', '麻婆豆腐', '1', '22.00', '22.00', '2026-04-06 18:30:00', '李强', null, '91', '1');
INSERT INTO `order_detail` VALUES ('138', '81', '餐饮', '鱼香肉丝', '1', '30.00', '30.00', '2026-04-06 18:30:00', '李强', null, '91', '1');
INSERT INTO `order_detail` VALUES ('139', '81', '餐饮', '米饭', '2', '3.00', '6.00', '2026-04-06 18:30:00', '李强', null, '91', '1');
INSERT INTO `order_detail` VALUES ('140', '81', '餐饮', '酸菜鱼', '1', '68.00', '68.00', '2026-04-07 12:00:00', '李强', null, '91', '1');
INSERT INTO `order_detail` VALUES ('141', '81', 'KTV', 'K1 2小时', '1', '136.00', '136.00', '2026-04-07 20:00:00', '孙敏', null, '91', '1');
INSERT INTO `order_detail` VALUES ('142', '82', '房费', '房费（3天）', '3', '198.00', '594.00', '2026-05-28 08:00:00', '系统', null, '92', '1');
INSERT INTO `order_detail` VALUES ('143', '82', '餐饮', '红烧肉', '1', '39.00', '39.00', '2026-05-25 18:30:00', '李强', null, '92', '1');
INSERT INTO `order_detail` VALUES ('144', '82', '餐饮', '清蒸鲈鱼', '1', '98.00', '98.00', '2026-05-25 18:30:00', '李强', null, '92', '1');
INSERT INTO `order_detail` VALUES ('145', '82', '餐饮', '米饭', '2', '3.00', '6.00', '2026-05-26 12:00:00', '李强', null, '92', '1');
INSERT INTO `order_detail` VALUES ('146', '83', '房费', '房费（3天）', '3', '268.00', '804.00', '2026-03-23 11:00:00', '系统', null, '93', '1');
INSERT INTO `order_detail` VALUES ('147', '83', '餐饮', '北京烤鸭', '1', '188.00', '188.00', '2026-03-21 18:00:00', '李强', null, '93', '1');
INSERT INTO `order_detail` VALUES ('148', '83', '餐饮', '时蔬', '1', '28.00', '28.00', '2026-03-21 18:00:00', '李强', null, '93', '1');
INSERT INTO `order_detail` VALUES ('149', '83', '餐饮', '可乐', '2', '8.00', '16.00', '2026-03-22 12:30:00', '李强', null, '93', '1');
INSERT INTO `order_detail` VALUES ('150', '83', '杂项', '加床', '1', '10.00', '10.00', '2026-03-22 20:00:00', '前台', null, '93', '1');
INSERT INTO `order_detail` VALUES ('151', '84', '房费', '房费（4天）', '4', '188.00', '752.00', '2026-04-19 08:00:00', '系统', null, '94', '1');
INSERT INTO `order_detail` VALUES ('152', '84', '餐饮', '锅包肉', '2', '36.00', '72.00', '2026-04-16 18:00:00', '李强', null, '94', '1');
INSERT INTO `order_detail` VALUES ('153', '84', '餐饮', '麻婆豆腐', '1', '22.00', '22.00', '2026-04-16 18:00:00', '李强', null, '94', '1');
INSERT INTO `order_detail` VALUES ('154', '84', '餐饮', '米饭', '3', '3.00', '9.00', '2026-04-16 18:00:00', '李强', null, '94', '1');
INSERT INTO `order_detail` VALUES ('155', '84', '餐饮', '青岛啤酒', '6', '15.00', '90.00', '2026-04-17 20:00:00', '李强', null, '94', '1');
INSERT INTO `order_detail` VALUES ('156', '84', '餐饮', '珍珠奶茶', '2', '18.00', '36.00', '2026-04-18 15:00:00', '李强', null, '94', '1');
INSERT INTO `order_detail` VALUES ('157', '84', 'KTV', 'K1 2小时', '1', '136.00', '136.00', '2026-04-17 22:00:00', '孙敏', null, '94', '1');
INSERT INTO `order_detail` VALUES ('158', '85', '房费', '房费（3天）', '3', '188.00', '564.00', '2026-05-13 10:00:00', '系统', null, '95', '1');
INSERT INTO `order_detail` VALUES ('159', '85', '餐饮', '酸菜鱼', '1', '68.00', '68.00', '2026-05-11 18:30:00', '李强', null, '95', '1');
INSERT INTO `order_detail` VALUES ('160', '85', '餐饮', '水煮肉片', '1', '46.00', '46.00', '2026-05-11 18:30:00', '李强', null, '95', '1');
INSERT INTO `order_detail` VALUES ('161', '85', '餐饮', '米饭', '2', '3.00', '6.00', '2026-05-11 18:30:00', '李强', null, '95', '1');
INSERT INTO `order_detail` VALUES ('162', '85', '餐饮', '青岛啤酒', '3', '15.00', '45.00', '2026-05-12 19:00:00', '李强', null, '95', '1');
INSERT INTO `order_detail` VALUES ('163', '85', '杂项', '洗衣服务', '1', '50.00', '50.00', '2026-05-12 09:00:00', '前台', null, '95', '1');
INSERT INTO `order_detail` VALUES ('164', '86', '房费', '房费（3天）', '3', '198.00', '594.00', '2026-05-23 09:00:00', '系统', null, '96', '1');
INSERT INTO `order_detail` VALUES ('165', '86', '餐饮', '红烧肉', '1', '39.00', '39.00', '2026-05-21 18:00:00', '李强', null, '96', '1');
INSERT INTO `order_detail` VALUES ('166', '86', '餐饮', '宫保鸡丁', '2', '48.00', '96.00', '2026-05-21 18:00:00', '李强', null, '96', '1');
INSERT INTO `order_detail` VALUES ('167', '86', '餐饮', '米饭', '3', '3.00', '9.00', '2026-05-21 18:00:00', '李强', null, '96', '1');
INSERT INTO `order_detail` VALUES ('168', '86', '餐饮', '干红葡萄酒', '1', '128.00', '128.00', '2026-05-22 19:00:00', '李强', null, '96', '1');
INSERT INTO `order_detail` VALUES ('169', '87', '房费', '房费（4天）', '4', '198.00', '792.00', '2026-06-06 08:00:00', '系统', null, '97', '1');
INSERT INTO `order_detail` VALUES ('170', '87', '餐饮', '清蒸鲈鱼', '1', '98.00', '98.00', '2026-06-03 18:30:00', '李强', null, '97', '1');
INSERT INTO `order_detail` VALUES ('171', '87', '餐饮', '鱼香肉丝', '1', '30.00', '30.00', '2026-06-03 18:30:00', '李强', null, '97', '1');
INSERT INTO `order_detail` VALUES ('172', '87', '餐饮', '米饭', '2', '3.00', '6.00', '2026-06-03 18:30:00', '李强', null, '97', '1');
INSERT INTO `order_detail` VALUES ('173', '87', '餐饮', '青岛啤酒', '4', '15.00', '60.00', '2026-06-04 20:00:00', '李强', null, '97', '1');
INSERT INTO `order_detail` VALUES ('174', '87', '餐饮', '鲜榨橙汁', '2', '22.00', '44.00', '2026-06-05 12:00:00', '李强', null, '97', '1');
INSERT INTO `order_detail` VALUES ('179', '89', '房费', '房费（3天）', '3', '188.00', '564.00', '2025-01-18 10:00:00', '系统', null, '99', '1');
INSERT INTO `order_detail` VALUES ('180', '89', '餐饮', '宫保鸡丁', '1', '48.00', '48.00', '2025-01-16 18:00:00', '李强', null, '99', '1');
INSERT INTO `order_detail` VALUES ('181', '89', '餐饮', '米饭', '2', '3.00', '6.00', '2025-01-16 18:00:00', '李强', null, '99', '1');
INSERT INTO `order_detail` VALUES ('182', '89', '餐饮', '青岛啤酒', '2', '15.00', '30.00', '2025-01-16 19:00:00', '李强', null, '99', '1');
INSERT INTO `order_detail` VALUES ('183', '89', '餐饮', '可乐', '2', '8.00', '16.00', '2025-01-17 12:00:00', '李强', null, '99', '1');
INSERT INTO `order_detail` VALUES ('184', '90', '房费', '房费（3天）', '3', '188.00', '564.00', '2025-02-18 09:00:00', '系统', null, '100', '1');
INSERT INTO `order_detail` VALUES ('185', '90', '餐饮', '清蒸鲈鱼', '1', '98.00', '98.00', '2025-02-16 19:00:00', '李强', null, '100', '1');
INSERT INTO `order_detail` VALUES ('186', '90', '餐饮', '时蔬', '1', '28.00', '28.00', '2025-02-16 19:00:00', '李强', null, '100', '1');
INSERT INTO `order_detail` VALUES ('187', '90', '餐饮', '米饭', '2', '3.00', '6.00', '2025-02-17 12:30:00', '李强', null, '100', '1');
INSERT INTO `order_detail` VALUES ('188', '90', '餐饮', '鲜榨橙汁', '1', '22.00', '22.00', '2025-02-17 12:30:00', '李强', null, '100', '1');
INSERT INTO `order_detail` VALUES ('189', '91', '房费', '房费（3天）', '3', '258.00', '774.00', '2025-03-18 11:00:00', '系统', null, '101', '1');
INSERT INTO `order_detail` VALUES ('190', '91', '餐饮', '酸菜鱼', '1', '68.00', '68.00', '2025-03-16 18:30:00', '李强', null, '101', '1');
INSERT INTO `order_detail` VALUES ('191', '91', '餐饮', '水煮肉片', '1', '46.00', '46.00', '2025-03-16 18:30:00', '李强', null, '101', '1');
INSERT INTO `order_detail` VALUES ('192', '91', '餐饮', '米饭', '2', '3.00', '6.00', '2025-03-16 18:30:00', '李强', null, '101', '1');
INSERT INTO `order_detail` VALUES ('193', '91', '餐饮', '青岛啤酒', '2', '15.00', '30.00', '2025-03-17 20:00:00', '李强', null, '101', '1');
INSERT INTO `order_detail` VALUES ('194', '91', 'KTV', 'K1 2小时', '1', '136.00', '136.00', '2025-03-17 21:00:00', '孙敏', null, '101', '1');
INSERT INTO `order_detail` VALUES ('195', '92', '房费', '房费（3天）', '3', '188.00', '564.00', '2025-04-18 08:00:00', '系统', null, '102', '1');
INSERT INTO `order_detail` VALUES ('196', '92', '餐饮', '北京烤鸭', '1', '188.00', '188.00', '2025-04-16 19:00:00', '李强', null, '102', '1');
INSERT INTO `order_detail` VALUES ('197', '92', '餐饮', '可乐', '2', '8.00', '16.00', '2025-04-16 19:00:00', '李强', null, '102', '1');
INSERT INTO `order_detail` VALUES ('198', '92', '餐饮', '米饭', '2', '3.00', '6.00', '2025-04-17 12:30:00', '李强', null, '102', '1');
INSERT INTO `order_detail` VALUES ('199', '93', '房费', '房费（3天）', '3', '198.00', '594.00', '2025-05-18 09:00:00', '系统', null, '103', '1');
INSERT INTO `order_detail` VALUES ('200', '93', '餐饮', '鱼香肉丝', '1', '30.00', '30.00', '2025-05-16 18:00:00', '李强', null, '103', '1');
INSERT INTO `order_detail` VALUES ('201', '93', '餐饮', '锅包肉', '1', '36.00', '36.00', '2025-05-16 18:00:00', '李强', null, '103', '1');
INSERT INTO `order_detail` VALUES ('202', '93', '餐饮', '米饭', '2', '3.00', '6.00', '2025-05-16 18:00:00', '李强', null, '103', '1');
INSERT INTO `order_detail` VALUES ('203', '93', '餐饮', '青岛啤酒', '3', '15.00', '45.00', '2025-05-17 19:00:00', '李强', null, '103', '1');
INSERT INTO `order_detail` VALUES ('204', '93', '餐饮', '原味老酸奶', '2', '15.00', '30.00', '2025-05-17 19:00:00', '李强', null, '103', '1');
INSERT INTO `order_detail` VALUES ('205', '94', '房费', '房费（3天）', '3', '268.00', '804.00', '2025-06-18 10:00:00', '系统', null, '104', '1');
INSERT INTO `order_detail` VALUES ('206', '94', '餐饮', '麻婆豆腐', '1', '22.00', '22.00', '2025-06-16 18:00:00', '李强', null, '104', '1');
INSERT INTO `order_detail` VALUES ('207', '94', '餐饮', '水煮肉片', '1', '46.00', '46.00', '2025-06-16 18:00:00', '李强', null, '104', '1');
INSERT INTO `order_detail` VALUES ('208', '94', '餐饮', '米饭', '2', '3.00', '6.00', '2025-06-16 18:00:00', '李强', null, '104', '1');
INSERT INTO `order_detail` VALUES ('209', '94', '餐饮', '青岛啤酒', '4', '15.00', '60.00', '2025-06-17 20:00:00', '李强', null, '104', '1');
INSERT INTO `order_detail` VALUES ('210', '94', '餐饮', '珍珠奶茶', '2', '18.00', '36.00', '2025-06-17 15:00:00', '李强', null, '104', '1');
INSERT INTO `order_detail` VALUES ('211', '94', '杂项', '洗衣服务', '1', '50.00', '50.00', '2025-06-17 09:00:00', '前台', null, '104', '1');
INSERT INTO `order_detail` VALUES ('212', '95', '房费', '房费（3天）', '3', '488.00', '1464.00', '2025-07-18 11:00:00', '系统', null, '105', '1');
INSERT INTO `order_detail` VALUES ('213', '95', '餐饮', '红烧肉', '1', '39.00', '39.00', '2025-07-16 18:30:00', '李强', null, '105', '1');
INSERT INTO `order_detail` VALUES ('214', '95', '餐饮', '清蒸鲈鱼', '1', '98.00', '98.00', '2025-07-16 18:30:00', '李强', null, '105', '1');
INSERT INTO `order_detail` VALUES ('215', '95', '餐饮', '米饭', '2', '3.00', '6.00', '2025-07-16 18:30:00', '李强', null, '105', '1');
INSERT INTO `order_detail` VALUES ('216', '95', '餐饮', '干红葡萄酒', '1', '128.00', '128.00', '2025-07-17 19:00:00', '李强', null, '105', '1');
INSERT INTO `order_detail` VALUES ('217', '95', 'KTV', 'K1 3小时', '1', '204.00', '204.00', '2025-07-17 21:00:00', '孙敏', null, '105', '1');
INSERT INTO `order_detail` VALUES ('218', '96', '房费', '房费（3天）', '3', '198.00', '594.00', '2025-08-18 08:00:00', '系统', null, '106', '1');
INSERT INTO `order_detail` VALUES ('219', '96', '餐饮', '宫保鸡丁', '2', '48.00', '96.00', '2025-08-16 18:00:00', '李强', null, '106', '1');
INSERT INTO `order_detail` VALUES ('220', '96', '餐饮', '时蔬', '1', '28.00', '28.00', '2025-08-16 18:00:00', '李强', null, '106', '1');
INSERT INTO `order_detail` VALUES ('221', '96', '餐饮', '米饭', '2', '3.00', '6.00', '2025-08-16 18:00:00', '李强', null, '106', '1');
INSERT INTO `order_detail` VALUES ('222', '96', '餐饮', '青岛啤酒', '3', '15.00', '45.00', '2025-08-17 20:00:00', '李强', null, '106', '1');
INSERT INTO `order_detail` VALUES ('223', '96', '餐饮', '鲜榨橙汁', '1', '22.00', '22.00', '2025-08-17 15:00:00', '李强', null, '106', '1');
INSERT INTO `order_detail` VALUES ('224', '97', '房费', '房费（3天）', '3', '268.00', '804.00', '2025-09-18 10:00:00', '系统', null, '107', '1');
INSERT INTO `order_detail` VALUES ('225', '97', '餐饮', '酸菜鱼', '1', '68.00', '68.00', '2025-09-16 18:30:00', '李强', null, '107', '1');
INSERT INTO `order_detail` VALUES ('226', '97', '餐饮', '水煮肉片', '1', '46.00', '46.00', '2025-09-16 18:30:00', '李强', null, '107', '1');
INSERT INTO `order_detail` VALUES ('227', '97', '餐饮', '米饭', '2', '3.00', '6.00', '2025-09-16 18:30:00', '李强', null, '107', '1');
INSERT INTO `order_detail` VALUES ('228', '97', '餐饮', '青岛啤酒', '3', '15.00', '45.00', '2025-09-17 20:00:00', '李强', null, '107', '1');
INSERT INTO `order_detail` VALUES ('229', '97', '餐饮', '干红葡萄酒', '1', '128.00', '128.00', '2025-09-17 19:00:00', '李强', null, '107', '1');
INSERT INTO `order_detail` VALUES ('230', '98', '房费', '房费（3天）', '3', '518.00', '1554.00', '2025-10-18 09:00:00', '系统', null, '108', '1');
INSERT INTO `order_detail` VALUES ('231', '98', '餐饮', '北京烤鸭', '1', '188.00', '188.00', '2025-10-16 19:00:00', '李强', null, '108', '1');
INSERT INTO `order_detail` VALUES ('232', '98', '餐饮', '清蒸鲈鱼', '1', '98.00', '98.00', '2025-10-16 19:00:00', '李强', null, '108', '1');
INSERT INTO `order_detail` VALUES ('233', '98', '餐饮', '时蔬', '1', '28.00', '28.00', '2025-10-16 19:00:00', '李强', null, '108', '1');
INSERT INTO `order_detail` VALUES ('234', '98', '餐饮', '米饭', '2', '3.00', '6.00', '2025-10-16 19:00:00', '李强', null, '108', '1');
INSERT INTO `order_detail` VALUES ('235', '98', '杂项', '加床', '1', '10.00', '10.00', '2025-10-17 22:00:00', '前台', null, '108', '1');
INSERT INTO `order_detail` VALUES ('236', '98', '杂项', '物品损坏赔偿 - 水杯', '1', '20.00', '20.00', '2025-10-17 20:00:00', '前台', null, '108', '1');
INSERT INTO `order_detail` VALUES ('237', '99', '房费', '房费（3天）', '3', '198.00', '594.00', '2025-11-18 10:00:00', '系统', null, '109', '1');
INSERT INTO `order_detail` VALUES ('238', '99', '餐饮', '宫保鸡丁', '1', '48.00', '48.00', '2025-11-16 18:30:00', '李强', null, '109', '1');
INSERT INTO `order_detail` VALUES ('239', '99', '餐饮', '红烧肉', '1', '39.00', '39.00', '2025-11-16 18:30:00', '李强', null, '109', '1');
INSERT INTO `order_detail` VALUES ('240', '99', '餐饮', '米饭', '2', '3.00', '6.00', '2025-11-16 18:30:00', '李强', null, '109', '1');
INSERT INTO `order_detail` VALUES ('241', '99', '餐饮', '青岛啤酒', '4', '15.00', '60.00', '2025-11-17 20:00:00', '李强', null, '109', '1');
INSERT INTO `order_detail` VALUES ('242', '99', '餐饮', '珍珠奶茶', '2', '18.00', '36.00', '2025-11-17 15:00:00', '李强', null, '109', '1');
INSERT INTO `order_detail` VALUES ('243', '100', '房费', '房费（3天）', '3', '198.00', '594.00', '2025-12-18 09:00:00', '系统', null, '110', '1');
INSERT INTO `order_detail` VALUES ('244', '100', '餐饮', '凉拌牛肉', '1', '42.00', '42.00', '2025-12-16 18:00:00', '李强', null, '110', '1');
INSERT INTO `order_detail` VALUES ('245', '100', '餐饮', '可乐', '3', '8.00', '24.00', '2025-12-16 18:00:00', '李强', null, '110', '1');
INSERT INTO `order_detail` VALUES ('246', '100', '餐饮', '米饭', '2', '3.00', '6.00', '2025-12-16 18:00:00', '李强', null, '110', '1');
INSERT INTO `order_detail` VALUES ('247', '100', '餐饮', '青岛啤酒', '2', '15.00', '30.00', '2025-12-17 19:00:00', '李强', null, '110', '1');
INSERT INTO `order_detail` VALUES ('248', '100', '餐饮', '原味老酸奶', '2', '15.00', '30.00', '2025-12-17 19:00:00', '李强', null, '110', '1');
INSERT INTO `order_detail` VALUES ('249', '103', '杂项', '洗衣费', '1', '10.00', '10.00', '2026-06-17 13:28:53', '陈芳', '', '76', '1');
INSERT INTO `order_detail` VALUES ('250', '103', '餐饮', '宫保鸡丁', '1', '48.00', '48.00', '2026-06-17 13:29:16', '李强', null, '76', '1');
INSERT INTO `order_detail` VALUES ('251', null, 'KTV', 'K4 0.0小时', '1', '0.00', '0.00', '2026-06-17 13:29:33', '李强', null, '76', '1');
INSERT INTO `order_detail` VALUES ('252', '106', '餐饮', '凉拌秋葵', '1', '22.00', '22.00', '2026-06-17 14:07:38', '李强', null, '111', '1');
INSERT INTO `order_detail` VALUES ('253', null, 'KTV', 'K5 0.0小时', '1', '0.00', '0.00', '2026-06-17 14:07:51', '李强', null, '111', '1');
INSERT INTO `order_detail` VALUES ('254', '106', '杂项', '杯子', '1', '10.00', '10.00', '2026-06-17 14:08:47', '陈芳', '', '111', '1');
INSERT INTO `order_detail` VALUES ('255', '108', '杂项', '杯子', '1', '10.00', '10.00', '2026-06-17 14:10:12', '陈芳', '', '111', '1');
INSERT INTO `order_detail` VALUES ('256', '108', '餐饮', '宫保鸡丁', '1', '48.00', '48.00', '2026-06-17 14:10:26', '李强', null, '111', '1');
INSERT INTO `order_detail` VALUES ('257', null, 'KTV', 'K1 1.0小时', '1', '68.00', '68.00', '2026-06-17 14:10:55', '李强', null, '111', '1');
INSERT INTO `order_detail` VALUES ('258', '110', '餐饮', '宫保鸡丁', '1', '48.00', '48.00', '2026-06-17 14:19:13', '李强', null, '111', '1');
INSERT INTO `order_detail` VALUES ('259', null, 'KTV', 'K7 1.0小时', '1', '148.00', '148.00', '2026-06-17 14:19:33', '李强', null, '111', '1');
INSERT INTO `order_detail` VALUES ('260', '110', '杂项', '杯子', '1', '10.00', '10.00', '2026-06-17 14:20:02', '陈芳', '', '111', '1');
INSERT INTO `order_detail` VALUES ('261', '112', '餐饮', '宫保鸡丁', '1', '48.00', '48.00', '2026-06-17 14:22:12', '李强', null, '111', '1');
INSERT INTO `order_detail` VALUES ('262', null, 'KTV', 'K1 2.0小时', '1', '136.00', '136.00', '2026-06-17 14:22:30', '李强', null, '111', '1');
INSERT INTO `order_detail` VALUES ('263', '112', '杂项', '洗衣费', '1', '10.00', '10.00', '2026-06-17 14:22:48', '陈芳', '', '111', '1');
INSERT INTO `order_detail` VALUES ('264', null, 'KTV', 'K1 0.0小时', '1', '0.00', '0.00', '2026-06-18 01:41:39', '李强', null, '72', '1');
INSERT INTO `order_detail` VALUES ('265', null, '杂项', '物品损坏赔偿 - 杯子一个', '1', '100.00', '100.00', '2026-06-18 11:00:01', '张丽华', null, '113', '0');
INSERT INTO `order_detail` VALUES ('266', null, '杂项', '物品损坏赔偿 - 床单', '1', '5.00', '5.00', '2026-06-18 17:42:57', '张丽华', null, '113', '0');
INSERT INTO `order_detail` VALUES ('267', null, '杂项', '物品损坏赔偿 - 杯子', '1', '10.00', '10.00', '2026-06-20 03:34:39', '张丽华', null, '113', '0');
INSERT INTO `order_detail` VALUES ('268', '116', '餐饮', '宫保鸡丁', '1', '48.00', '48.00', '2026-06-20 23:47:14', '李强', null, '113', '0');
INSERT INTO `order_detail` VALUES ('269', '116', '餐饮', '清蒸鲈鱼', '1', '98.00', '98.00', '2026-06-20 23:47:14', '李强', null, '113', '0');
INSERT INTO `order_detail` VALUES ('270', null, 'KTV', 'K1 3小时', '1', '204.00', '204.00', '2026-06-20 23:47:19', '李强', null, '72', '1');
INSERT INTO `order_detail` VALUES ('271', null, 'KTV', 'K2 5小时', '1', '540.00', '540.00', '2026-06-20 23:47:54', '李强', null, '112', '1');
INSERT INTO `order_detail` VALUES ('272', null, 'KTV', 'K3 8小时', '1', '1344.00', '1344.00', '2026-06-21 00:49:53', '李强', null, '72', '1');
INSERT INTO `order_detail` VALUES ('273', '118', '餐饮', '清蒸鲈鱼', '1', '98.00', '98.00', '2026-06-21 01:02:50', '李强', null, '112', '1');
INSERT INTO `order_detail` VALUES ('274', '118', '餐饮', '宫保鸡丁', '1', '48.00', '48.00', '2026-06-21 01:02:50', '李强', null, '112', '1');
INSERT INTO `order_detail` VALUES ('275', '122', '餐饮', '宫保鸡丁', '1', '48.00', '48.00', '2026-06-24 21:42:43', '李强', null, '72', '1');
INSERT INTO `order_detail` VALUES ('276', '122', '餐饮', '酸菜鱼', '1', '68.00', '68.00', '2026-06-24 21:42:43', '李强', null, '72', '1');
INSERT INTO `order_detail` VALUES ('277', null, 'KTV', 'K4 7小时', '1', '686.00', '686.00', '2026-06-24 21:44:29', '李强', null, '72', '1');
INSERT INTO `order_detail` VALUES ('278', null, '杂项', '物品损坏赔偿 - 杯子', '1', '100.00', '100.00', '2026-06-25 08:58:54', '张丽华', null, '113', '0');
INSERT INTO `order_detail` VALUES ('279', '122', '餐饮', '清蒸鲈鱼', '1', '98.00', '98.00', '2026-06-25 10:30:07', '李强', null, '72', '1');
INSERT INTO `order_detail` VALUES ('280', '122', '餐饮', '酸菜鱼', '1', '68.00', '68.00', '2026-06-25 10:30:07', '李强', null, '72', '1');
INSERT INTO `order_detail` VALUES ('281', null, 'KTV', 'K1 8小时', '1', '544.00', '544.00', '2026-06-25 10:30:31', '李强', null, '16', '1');

-- ----------------------------
-- Table structure for `order_main`
-- ----------------------------
DROP TABLE IF EXISTS `order_main`;
CREATE TABLE `order_main` (
  `order_id` int NOT NULL AUTO_INCREMENT COMMENT '账单编号',
  `guest_id` int NOT NULL COMMENT '客人编号',
  `room_id` int DEFAULT NULL COMMENT '房间编号',
  `room_fee` decimal(10,2) DEFAULT '0.00' COMMENT '房费总额',
  `food_fee` decimal(10,2) DEFAULT '0.00' COMMENT '餐饮总额',
  `ktv_fee` decimal(10,2) DEFAULT '0.00' COMMENT 'KTV总额',
  `other_fee` decimal(10,2) DEFAULT '0.00' COMMENT '杂项总额',
  `total_amount` decimal(10,2) DEFAULT '0.00' COMMENT '消费总计',
  `deposit_total` decimal(10,2) DEFAULT '0.00' COMMENT '押金总额',
  `refund_amount` decimal(10,2) DEFAULT '0.00' COMMENT '应退金额',
  `status` varchar(20) DEFAULT '未结算' COMMENT '状态',
  `settle_time` datetime DEFAULT NULL COMMENT '结算时间',
  `operator` varchar(50) DEFAULT NULL COMMENT '操作员',
  `pay_method` varchar(50) DEFAULT NULL COMMENT '支付方式：现金/微信/支付宝/银行卡/其他',
  PRIMARY KEY (`order_id`),
  KEY `guest_id` (`guest_id`),
  KEY `room_id` (`room_id`),
  KEY `idx_pay_method` (`pay_method`),
  CONSTRAINT `order_main_ibfk_1` FOREIGN KEY (`guest_id`) REFERENCES `guest` (`guest_id`),
  CONSTRAINT `order_main_ibfk_2` FOREIGN KEY (`room_id`) REFERENCES `room` (`room_id`)
) ENGINE=InnoDB AUTO_INCREMENT=126 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='消费主单表';

-- ----------------------------
-- Records of order_main
-- ----------------------------
INSERT INTO `order_main` VALUES ('1', '9', '10', '0.00', '0.00', '0.00', '0.00', '0.00', '500.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('2', '10', '3', '0.00', '0.00', '0.00', '0.00', '0.00', '1100.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('3', '11', '6', '0.00', '0.00', '0.00', '0.00', '0.00', '1000.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('4', '12', '12', '0.00', '0.00', '0.00', '0.00', '0.00', '600.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('5', '13', '5', '594.00', '246.00', '0.00', '0.00', '840.00', '400.00', '0.00', '已结算', '2026-05-23 11:30:00', '王敏', null);
INSERT INTO `order_main` VALUES ('6', '14', '8', '564.00', '120.00', '0.00', '50.00', '734.00', '0.00', '0.00', '已结算', '2026-05-25 10:30:00', '张丽华', null);
INSERT INTO `order_main` VALUES ('7', '15', '5', '0.00', '0.00', '0.00', '0.00', '0.00', '500.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('8', '16', '11', '0.00', '0.00', '0.00', '0.00', '0.00', '500.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('9', '12', '12', '1980.00', '48.00', '0.00', '0.00', '2028.00', '900.00', '0.00', '已结算', '2026-06-06 16:40:25', '陈芳', null);
INSERT INTO `order_main` VALUES ('10', '24', '2', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('11', '11', '6', '3216.00', '368.00', '0.00', '0.00', '3584.00', '1000.00', '0.00', '已结算', '2026-06-06 18:07:15', '陈芳', null);
INSERT INTO `order_main` VALUES ('12', '25', '7', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('13', '24', '2', '0.00', '0.00', '0.00', '0.00', '0.00', '200.00', '200.00', '已结算', '2026-06-06 22:11:24', '陈芳', null);
INSERT INTO `order_main` VALUES ('14', '26', '4', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('15', '27', '9', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('16', '28', '12', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('17', '29', '12', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('18', '30', '11', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('19', '31', '10', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('20', '25', '7', '488.00', '0.00', '0.00', '0.00', '488.00', '0.00', '0.00', '已结算', '2026-06-07 00:17:18', '陈芳', null);
INSERT INTO `order_main` VALUES ('21', '26', '4', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('22', '32', '3', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('23', '33', '7', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('24', '34', '5', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('25', '33', '7', '0.00', '0.00', '0.00', '0.00', '0.00', '500.00', '500.00', '已结算', '2026-06-07 01:09:32', '陈芳', null);
INSERT INTO `order_main` VALUES ('26', '32', '3', '0.00', '0.00', '0.00', '0.00', '0.00', '500.00', '500.00', '已结算', '2026-06-07 01:09:38', '陈芳', null);
INSERT INTO `order_main` VALUES ('27', '30', '11', '198.00', '0.00', '0.00', '0.00', '198.00', '0.00', '0.00', '已结算', '2026-06-07 01:09:42', '陈芳', null);
INSERT INTO `order_main` VALUES ('28', '29', '12', '198.00', '0.00', '0.00', '0.00', '198.00', '0.00', '0.00', '已结算', '2026-06-07 01:09:46', '陈芳', null);
INSERT INTO `order_main` VALUES ('29', '31', '10', '518.00', '0.00', '0.00', '0.00', '518.00', '0.00', '0.00', '已结算', '2026-06-07 01:09:51', '陈芳', null);
INSERT INTO `order_main` VALUES ('30', '26', '4', '0.00', '0.00', '0.00', '0.00', '0.00', '500.00', '500.00', '已结算', '2026-06-07 01:09:57', '陈芳', null);
INSERT INTO `order_main` VALUES ('31', '35', '2', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('32', '35', '2', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('33', '39', '6', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('34', '57', '4', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('35', '56', '3', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('36', '58', '10', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('37', '59', '7', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('38', '61', '9', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('39', '60', '8', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('40', '64', '11', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('41', '57', '4', '376.00', '192.00', '1008.00', '0.00', '568.00', '500.00', '0.00', '已结算', '2026-06-10 20:20:39', '陈芳', null);
INSERT INTO `order_main` VALUES ('42', '61', '9', '536.00', '0.00', '0.00', '0.00', '536.00', '500.00', '0.00', '已结算', '2026-06-10 21:24:01', '陈芳', null);
INSERT INTO `order_main` VALUES ('43', '64', '11', '396.00', '0.00', '0.00', '0.00', '396.00', '100.00', '0.00', '已结算', '2026-06-10 21:24:14', '陈芳', null);
INSERT INTO `order_main` VALUES ('44', '60', '8', '396.00', '14.00', '0.00', '0.00', '410.00', '200.00', '0.00', '已结算', '2026-06-10 21:24:23', '陈芳', null);
INSERT INTO `order_main` VALUES ('45', '27', '9', '1608.00', '36.00', '0.00', '4.00', '1648.00', '0.00', '0.00', '已结算', '2026-06-11 00:34:26', '陈芳', null);
INSERT INTO `order_main` VALUES ('46', '59', '7', '1464.00', '0.00', '0.00', '0.00', '1464.00', '400.00', '0.00', '已结算', '2026-06-11 01:14:19', '陈芳', null);
INSERT INTO `order_main` VALUES ('47', '68', '1', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('48', '68', '1', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('49', '69', '2', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('50', '68', '1', '564.00', '0.00', '0.00', '0.00', '564.00', '1000.00', '436.00', '已结算', '2026-06-13 17:49:11', '陈芳', null);
INSERT INTO `order_main` VALUES ('51', '39', '6', '1876.00', '191.00', '0.00', '0.00', '1860.30', '600.00', '0.00', '已结算', '2026-06-13 18:48:57', '陈芳', null);
INSERT INTO `order_main` VALUES ('52', '28', '12', '1584.00', '0.00', '0.00', '0.00', '1584.00', '0.00', '0.00', '已结算', '2026-06-14 00:41:54', '陈芳', null);
INSERT INTO `order_main` VALUES ('53', '69', '2', '752.00', '0.00', '0.00', '0.00', '752.00', '500.00', '0.00', '已结算', '2026-06-14 01:16:00', '陈芳', null);
INSERT INTO `order_main` VALUES ('54', '35', '2', '1504.00', '0.00', '0.00', '50.00', '1320.90', '1000.00', '0.00', '已结算', '2026-06-14 01:46:15', '陈芳', null);
INSERT INTO `order_main` VALUES ('55', '34', '5', '1584.00', '0.00', '294.00', '0.00', '1584.00', '500.00', '0.00', '已结算', '2026-06-14 02:07:52', '陈芳', null);
INSERT INTO `order_main` VALUES ('56', '56', '3', '1548.00', '0.00', '0.00', '0.00', '1238.40', '500.00', '0.00', '已结算', '2026-06-14 02:39:01', '陈芳', null);
INSERT INTO `order_main` VALUES ('57', '58', '10', '4144.00', '0.00', '0.00', '0.00', '4144.00', '600.00', '0.00', '已结算', '2026-06-16 20:55:21', '陈芳', null);
INSERT INTO `order_main` VALUES ('58', '71', '1', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('59', '9', '10', '11396.00', '0.00', '0.00', '0.00', '11396.00', '400.00', '0.00', '已结算', '2026-06-16 21:53:06', '陈芳', null);
INSERT INTO `order_main` VALUES ('60', '72', '10', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('61', '73', '5', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('62', '73', '5', '198.00', '0.00', '0.00', '0.00', '178.20', '500.00', '321.80', '已结算', '2026-06-16 22:16:23', '陈芳', null);
INSERT INTO `order_main` VALUES ('63', '74', '3', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('64', '74', '3', '258.00', '0.00', '0.00', '0.00', '258.00', '500.00', '242.00', '已结算', '2026-06-16 22:27:23', '陈芳', null);
INSERT INTO `order_main` VALUES ('65', '72', '10', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('66', '71', '1', '376.00', '182.00', '136.00', '10.00', '568.00', '500.00', '0.00', '已结算', '2026-06-17 08:50:48', '陈芳', null);
INSERT INTO `order_main` VALUES ('67', '72', '10', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('68', '77', '1', '564.00', '200.00', '0.00', '0.00', '764.00', '0.00', '0.00', '已结算', '2026-01-18 10:30:00', '系统', null);
INSERT INTO `order_main` VALUES ('69', '79', '1', '564.00', '200.00', '0.00', '0.00', '764.00', '0.00', '0.00', '已结算', '2026-01-18 10:30:00', '系统', null);
INSERT INTO `order_main` VALUES ('70', '80', '3', '516.00', '150.00', '0.00', '0.00', '666.00', '0.00', '0.00', '已结算', '2026-02-12 09:30:00', '系统', null);
INSERT INTO `order_main` VALUES ('71', '81', '5', '990.00', '300.00', '0.00', '0.00', '1290.00', '0.00', '0.00', '已结算', '2026-03-10 11:30:00', '系统', null);
INSERT INTO `order_main` VALUES ('72', '82', '6', '1072.00', '250.00', '0.00', '0.00', '1322.00', '0.00', '0.00', '已结算', '2026-04-24 08:30:00', '系统', null);
INSERT INTO `order_main` VALUES ('73', '83', '9', '536.00', '180.00', '0.00', '0.00', '716.00', '0.00', '0.00', '已结算', '2026-05-03 09:30:00', '系统', null);
INSERT INTO `order_main` VALUES ('74', '84', '11', '594.00', '220.00', '0.00', '0.00', '814.00', '0.00', '0.00', '已结算', '2026-05-18 10:30:00', '系统', null);
INSERT INTO `order_main` VALUES ('75', '85', '4', '564.00', '160.00', '0.00', '0.00', '724.00', '0.00', '0.00', '已结算', '2026-06-04 09:30:00', '系统', null);
INSERT INTO `order_main` VALUES ('76', '86', '8', '396.00', '100.00', '0.00', '0.00', '496.00', '0.00', '0.00', '已结算', '2026-06-12 08:30:00', '系统', null);
INSERT INTO `order_main` VALUES ('79', '89', '12', '396.00', '120.00', '0.00', '0.00', '516.00', '0.00', '0.00', '已结算', '2026-03-02 10:30:00', '系统', null);
INSERT INTO `order_main` VALUES ('80', '90', '7', '1464.00', '200.00', '0.00', '0.00', '1664.00', '0.00', '0.00', '已结算', '2026-03-18 09:30:00', '系统', null);
INSERT INTO `order_main` VALUES ('81', '91', '9', '804.00', '180.00', '0.00', '0.00', '984.00', '0.00', '0.00', '已结算', '2026-04-08 10:30:00', '系统', null);
INSERT INTO `order_main` VALUES ('82', '92', '5', '594.00', '140.00', '0.00', '0.00', '734.00', '0.00', '0.00', '已结算', '2026-05-28 08:30:00', '系统', null);
INSERT INTO `order_main` VALUES ('83', '93', '6', '804.00', '240.00', '0.00', '0.00', '1044.00', '0.00', '0.00', '已结算', '2026-03-23 11:30:00', '系统', null);
INSERT INTO `order_main` VALUES ('84', '94', '2', '752.00', '310.00', '0.00', '0.00', '1062.00', '0.00', '0.00', '已结算', '2026-04-19 08:30:00', '系统', null);
INSERT INTO `order_main` VALUES ('85', '95', '4', '564.00', '190.00', '0.00', '0.00', '754.00', '0.00', '0.00', '已结算', '2026-05-13 10:30:00', '系统', null);
INSERT INTO `order_main` VALUES ('86', '96', '8', '594.00', '270.00', '0.00', '0.00', '864.00', '0.00', '0.00', '已结算', '2026-05-23 09:30:00', '系统', null);
INSERT INTO `order_main` VALUES ('87', '97', '12', '792.00', '260.00', '0.00', '0.00', '1052.00', '0.00', '0.00', '已结算', '2026-06-06 08:30:00', '系统', null);
INSERT INTO `order_main` VALUES ('89', '99', '1', '564.00', '120.00', '0.00', '0.00', '684.00', '0.00', '0.00', '已结算', '2025-01-18 10:30:00', '系统', null);
INSERT INTO `order_main` VALUES ('90', '100', '2', '564.00', '150.00', '0.00', '0.00', '714.00', '0.00', '0.00', '已结算', '2025-02-18 09:30:00', '系统', null);
INSERT INTO `order_main` VALUES ('91', '101', '3', '774.00', '180.00', '136.00', '0.00', '1090.00', '0.00', '0.00', '已结算', '2025-03-18 11:30:00', '系统', null);
INSERT INTO `order_main` VALUES ('92', '102', '4', '564.00', '210.00', '0.00', '0.00', '774.00', '0.00', '0.00', '已结算', '2025-04-18 08:30:00', '系统', null);
INSERT INTO `order_main` VALUES ('93', '103', '5', '594.00', '160.00', '0.00', '0.00', '754.00', '0.00', '0.00', '已结算', '2025-05-18 09:30:00', '系统', null);
INSERT INTO `order_main` VALUES ('94', '104', '6', '804.00', '230.00', '0.00', '50.00', '1084.00', '0.00', '0.00', '已结算', '2025-06-18 10:30:00', '系统', null);
INSERT INTO `order_main` VALUES ('95', '105', '7', '1464.00', '280.00', '204.00', '0.00', '1948.00', '0.00', '0.00', '已结算', '2025-07-18 11:30:00', '系统', null);
INSERT INTO `order_main` VALUES ('96', '106', '8', '594.00', '190.00', '0.00', '0.00', '784.00', '0.00', '0.00', '已结算', '2025-08-18 08:30:00', '系统', null);
INSERT INTO `order_main` VALUES ('97', '107', '9', '804.00', '200.00', '0.00', '0.00', '1004.00', '0.00', '0.00', '已结算', '2025-09-18 10:30:00', '系统', null);
INSERT INTO `order_main` VALUES ('98', '108', '10', '1554.00', '350.00', '0.00', '50.00', '1954.00', '0.00', '0.00', '已结算', '2025-10-18 09:30:00', '系统', null);
INSERT INTO `order_main` VALUES ('99', '109', '11', '594.00', '220.00', '0.00', '0.00', '814.00', '0.00', '0.00', '已结算', '2025-11-18 10:30:00', '系统', null);
INSERT INTO `order_main` VALUES ('100', '110', '12', '594.00', '140.00', '0.00', '0.00', '734.00', '0.00', '0.00', '已结算', '2025-12-18 09:30:00', '系统', null);
INSERT INTO `order_main` VALUES ('101', '76', '2', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('102', '76', '2', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('103', '76', '2', '188.00', '48.00', '0.00', '10.00', '196.80', '500.00', '303.20', '已结算', '2026-06-17 13:30:22', '陈芳', null);
INSERT INTO `order_main` VALUES ('104', '111', '2', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('105', '111', '2', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('106', '111', '2', '188.00', '22.00', '0.00', '10.00', '220.00', '500.00', '280.00', '已结算', '2026-06-17 14:08:52', '陈芳', null);
INSERT INTO `order_main` VALUES ('107', '111', '2', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('108', '111', '2', '188.00', '48.00', '68.00', '10.00', '266.80', '220.00', '0.00', '已结算', '2026-06-17 14:11:11', '陈芳', null);
INSERT INTO `order_main` VALUES ('109', '111', '2', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('110', '111', '2', '188.00', '48.00', '148.00', '10.00', '346.80', '620.00', '273.20', '已结算', '2026-06-17 14:20:12', '陈芳', null);
INSERT INTO `order_main` VALUES ('111', '111', '2', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('112', '111', '2', '188.00', '48.00', '136.00', '10.00', '198.80', '346.80', '148.00', '已结算', '2026-06-17 14:22:54', '陈芳', null);
INSERT INTO `order_main` VALUES ('113', '112', '6', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('114', '113', '4', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('115', '112', '6', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('116', '113', '4', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('117', '112', '6', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('118', '112', '6', '2144.00', '146.00', '540.00', '0.00', '2290.00', '1000.00', '0.00', '已结算', '2026-06-24 20:53:37', '陈芳', null);
INSERT INTO `order_main` VALUES ('119', '72', '10', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('120', '134', '1', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('121', '72', '10', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('122', '72', '10', '5180.00', '506.00', '2234.00', '10.00', '4843.10', '500.00', '0.00', '已结算', '2026-06-25 10:32:00', '陈芳', null);
INSERT INTO `order_main` VALUES ('123', '143', '2', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '0.00', '未结算', null, null, null);
INSERT INTO `order_main` VALUES ('124', '143', '2', '188.00', '0.00', '0.00', '0.00', '188.00', '500.00', '312.00', '已结算', '2026-06-26 23:38:59', '陈芳', null);
INSERT INTO `order_main` VALUES ('125', '134', '1', '564.00', '0.00', '0.00', '0.00', '564.00', '500.00', '0.00', '已结算', '2026-06-27 02:13:55', '陈芳', '支付宝');

-- ----------------------------
-- Table structure for `room`
-- ----------------------------
DROP TABLE IF EXISTS `room`;
CREATE TABLE `room` (
  `room_id` int NOT NULL AUTO_INCREMENT COMMENT '房间编号',
  `room_number` varchar(10) NOT NULL COMMENT '房间号',
  `room_type` varchar(50) NOT NULL COMMENT '房间类型',
  `floor` int DEFAULT NULL COMMENT '楼层',
  `price` decimal(10,2) NOT NULL COMMENT '单价',
  `status` varchar(20) DEFAULT '空闲' COMMENT '状态',
  `description` text COMMENT '描述',
  `image` varchar(500) DEFAULT NULL COMMENT '图片路径',
  PRIMARY KEY (`room_id`),
  UNIQUE KEY `room_number` (`room_number`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='房间信息表';

-- ----------------------------
-- Records of room
-- ----------------------------
INSERT INTO `room` VALUES ('1', '101', '标准间', '1', '188.00', '打扫中', '朝南，安静', '/uploads/rooms/101.jpg');
INSERT INTO `room` VALUES ('2', '102', '标准间', '1', '188.00', '打扫中', '', '/uploads/rooms/102.jpg');
INSERT INTO `room` VALUES ('3', '103', '大床房', '1', '258.00', '空闲', '', '/uploads/rooms/103.jpg');
INSERT INTO `room` VALUES ('4', '104', '标准间', '1', '188.00', '占用', '', '/uploads/rooms/104.jpg');
INSERT INTO `room` VALUES ('5', '201', '标准间', '2', '198.00', '维修中', '', '/uploads/rooms/201.jpg');
INSERT INTO `room` VALUES ('6', '202', '大床房', '2', '268.00', '打扫中', '', '/uploads/rooms/202.jpg');
INSERT INTO `room` VALUES ('7', '203', '套房', '2', '488.00', '空闲', '带客厅', '/uploads/rooms/203.jpg');
INSERT INTO `room` VALUES ('8', '204', '标准间', '2', '198.00', '空闲', '空调故障', '/uploads/rooms/204.jpg');
INSERT INTO `room` VALUES ('9', '301', '大床房', '3', '268.00', '空闲', '', '/uploads/rooms/301.jpg');
INSERT INTO `room` VALUES ('10', '302', '套房', '3', '518.00', '打扫中', '江景房', '/uploads/rooms/302.jpg');
INSERT INTO `room` VALUES ('11', '303', '标准间', '3', '198.00', '占用', '', '/uploads/rooms/303.jpg');
INSERT INTO `room` VALUES ('12', '304', '标准间', '3', '198.00', '空闲', '', '/uploads/rooms/304.jpg');

-- ----------------------------
-- Table structure for `salary`
-- ----------------------------
DROP TABLE IF EXISTS `salary`;
CREATE TABLE `salary` (
  `salary_id` int NOT NULL AUTO_INCREMENT COMMENT '工资编号',
  `emp_id` int NOT NULL COMMENT '员工编号',
  `year_month` varchar(7) NOT NULL COMMENT '工资月份',
  `base_salary` decimal(10,2) DEFAULT NULL COMMENT '基本工资',
  `attendance_deduction` decimal(10,2) DEFAULT '0.00' COMMENT '考勤扣款',
  `bonus` decimal(10,2) DEFAULT '0.00' COMMENT '奖金',
  `overtime_pay` decimal(10,2) DEFAULT '0.00' COMMENT '加班费',
  `other_add` decimal(10,2) DEFAULT '0.00' COMMENT '其他加项',
  `other_deduct` decimal(10,2) DEFAULT '0.00' COMMENT '其他扣款',
  `total_salary` decimal(10,2) DEFAULT NULL COMMENT '实发工资',
  `pay_status` varchar(20) DEFAULT '未发放' COMMENT '发放状态',
  `pay_date` date DEFAULT NULL COMMENT '发放日期',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `operator` varchar(50) DEFAULT NULL COMMENT '操作员',
  PRIMARY KEY (`salary_id`),
  KEY `emp_id` (`emp_id`),
  CONSTRAINT `salary_ibfk_1` FOREIGN KEY (`emp_id`) REFERENCES `employee` (`emp_id`)
) ENGINE=InnoDB AUTO_INCREMENT=320 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='工资表';

-- ----------------------------
-- Records of salary
-- ----------------------------
INSERT INTO `salary` VALUES ('10', '1', '2026-07', '4500.00', '0.00', '0.00', '0.00', '0.00', '0.00', '4500.00', '已发放', '2026-06-11', '2026-06-11 00:36:39', '陈芳');
INSERT INTO `salary` VALUES ('11', '2', '2026-07', '3800.00', '0.00', '0.00', '0.00', '0.00', '0.00', '3800.00', '未发放', null, '2026-06-11 00:36:39', '陈芳');
INSERT INTO `salary` VALUES ('12', '3', '2026-07', '4000.00', '0.00', '0.00', '0.00', '0.00', '0.00', '4000.00', '未发放', null, '2026-06-11 00:36:39', '陈芳');
INSERT INTO `salary` VALUES ('13', '4', '2026-07', '5200.00', '0.00', '0.00', '0.00', '0.00', '0.00', '5200.00', '未发放', null, '2026-06-11 00:36:39', '陈芳');
INSERT INTO `salary` VALUES ('14', '5', '2026-07', '15000.00', '0.00', '0.00', '0.00', '0.00', '0.00', '15000.00', '未发放', null, '2026-06-11 00:36:39', '陈芳');
INSERT INTO `salary` VALUES ('15', '7', '2026-07', '3500.00', '0.00', '0.00', '0.00', '0.00', '0.00', '3500.00', '未发放', null, '2026-06-11 00:36:39', '陈芳');
INSERT INTO `salary` VALUES ('16', '8', '2026-07', '4200.00', '0.00', '0.00', '0.00', '0.00', '0.00', '4200.00', '未发放', null, '2026-06-11 00:36:39', '陈芳');
INSERT INTO `salary` VALUES ('17', '9', '2026-07', '4800.00', '0.00', '0.00', '0.00', '0.00', '0.00', '4800.00', '未发放', null, '2026-06-11 00:36:39', '陈芳');
INSERT INTO `salary` VALUES ('18', '19', '2026-07', '15000.00', '0.00', '0.00', '0.00', '0.00', '0.00', '15000.00', '未发放', null, '2026-06-11 00:36:39', '陈芳');
INSERT INTO `salary` VALUES ('19', '1', '2026-02', '4500.00', '0.00', '0.00', '0.00', '0.00', '0.00', '4500.00', '未发放', null, '2026-06-11 00:37:02', '陈芳');
INSERT INTO `salary` VALUES ('20', '2', '2026-02', '3800.00', '0.00', '0.00', '0.00', '0.00', '0.00', '3800.00', '未发放', null, '2026-06-11 00:37:02', '陈芳');
INSERT INTO `salary` VALUES ('21', '3', '2026-02', '4000.00', '0.00', '0.00', '0.00', '0.00', '0.00', '4000.00', '未发放', null, '2026-06-11 00:37:02', '陈芳');
INSERT INTO `salary` VALUES ('22', '4', '2026-02', '5200.00', '0.00', '0.00', '0.00', '0.00', '0.00', '5200.00', '未发放', null, '2026-06-11 00:37:02', '陈芳');
INSERT INTO `salary` VALUES ('23', '5', '2026-02', '15000.00', '0.00', '0.00', '0.00', '0.00', '0.00', '15000.00', '未发放', null, '2026-06-11 00:37:02', '陈芳');
INSERT INTO `salary` VALUES ('24', '7', '2026-02', '3500.00', '0.00', '0.00', '0.00', '0.00', '0.00', '3500.00', '未发放', null, '2026-06-11 00:37:02', '陈芳');
INSERT INTO `salary` VALUES ('25', '8', '2026-02', '4200.00', '0.00', '0.00', '0.00', '0.00', '0.00', '4200.00', '未发放', null, '2026-06-11 00:37:02', '陈芳');
INSERT INTO `salary` VALUES ('26', '9', '2026-02', '4800.00', '0.00', '0.00', '0.00', '0.00', '0.00', '4800.00', '未发放', null, '2026-06-11 00:37:02', '陈芳');
INSERT INTO `salary` VALUES ('27', '19', '2026-02', '15000.00', '0.00', '0.00', '0.00', '0.00', '0.00', '15000.00', '未发放', null, '2026-06-11 00:37:02', '陈芳');
INSERT INTO `salary` VALUES ('36', '19', '2026-03', '15000.00', '0.00', '0.00', '0.00', '0.00', '0.00', '15000.00', '已发放', '2026-06-13', '2026-06-11 00:37:06', '陈芳');
INSERT INTO `salary` VALUES ('38', '1', '2026-10', '4500.00', '0.00', '0.00', '0.00', '0.00', '0.00', '4500.00', '未发放', null, '2026-06-13 17:34:26', '陈芳');
INSERT INTO `salary` VALUES ('39', '2', '2026-10', '3800.00', '0.00', '0.00', '0.00', '0.00', '0.00', '3800.00', '未发放', null, '2026-06-13 17:34:26', '陈芳');
INSERT INTO `salary` VALUES ('40', '3', '2026-10', '4000.00', '0.00', '0.00', '0.00', '0.00', '0.00', '4000.00', '未发放', null, '2026-06-13 17:34:26', '陈芳');
INSERT INTO `salary` VALUES ('41', '4', '2026-10', '5200.00', '0.00', '0.00', '0.00', '0.00', '0.00', '5200.00', '未发放', null, '2026-06-13 17:34:26', '陈芳');
INSERT INTO `salary` VALUES ('42', '5', '2026-10', '15000.00', '0.00', '0.00', '0.00', '0.00', '0.00', '15000.00', '未发放', null, '2026-06-13 17:34:26', '陈芳');
INSERT INTO `salary` VALUES ('43', '6', '2026-10', '3600.00', '0.00', '0.00', '0.00', '0.00', '0.00', '3600.00', '未发放', null, '2026-06-13 17:34:26', '陈芳');
INSERT INTO `salary` VALUES ('44', '7', '2026-10', '3500.00', '0.00', '0.00', '0.00', '0.00', '0.00', '3500.00', '未发放', null, '2026-06-13 17:34:26', '陈芳');
INSERT INTO `salary` VALUES ('45', '8', '2026-10', '4200.00', '0.00', '0.00', '0.00', '0.00', '0.00', '4200.00', '未发放', null, '2026-06-13 17:34:26', '陈芳');
INSERT INTO `salary` VALUES ('46', '9', '2026-10', '4800.00', '0.00', '0.00', '0.00', '0.00', '0.00', '4800.00', '未发放', null, '2026-06-13 17:34:26', '陈芳');
INSERT INTO `salary` VALUES ('47', '19', '2026-10', '15000.00', '0.00', '0.00', '0.00', '0.00', '0.00', '15000.00', '未发放', null, '2026-06-13 17:34:26', '陈芳');
INSERT INTO `salary` VALUES ('58', '6', '2026-07', '3600.00', '0.00', '0.00', '0.00', '0.00', '0.00', '3600.00', '未发放', null, '2026-06-13 18:03:31', '陈芳');
INSERT INTO `salary` VALUES ('69', '20', '2026-05', '80000.00', '0.00', '0.00', '0.00', '0.00', '0.00', '80000.00', '已发放', '2026-06-14', '2026-06-14 12:27:29', '陈芳');
INSERT INTO `salary` VALUES ('71', '21', '2026-06', '555.00', '20.00', '200.00', '100.00', '0.00', '0.00', '835.00', '未发放', null, '2026-06-14 12:37:17', '陈芳');
INSERT INTO `salary` VALUES ('74', '1', '2026-01', '4500.00', '0.00', '0.00', '0.00', '0.00', '0.00', '4500.00', '未发放', null, '2026-06-14 17:52:57', '陈芳');
INSERT INTO `salary` VALUES ('75', '2', '2026-01', '3800.00', '0.00', '0.00', '0.00', '0.00', '0.00', '3800.00', '未发放', null, '2026-06-14 17:52:57', '陈芳');
INSERT INTO `salary` VALUES ('76', '3', '2026-01', '4000.00', '0.00', '0.00', '0.00', '0.00', '0.00', '4000.00', '未发放', null, '2026-06-14 17:52:57', '陈芳');
INSERT INTO `salary` VALUES ('77', '4', '2026-01', '5200.00', '0.00', '0.00', '0.00', '0.00', '0.00', '5200.00', '未发放', null, '2026-06-14 17:52:57', '陈芳');
INSERT INTO `salary` VALUES ('78', '5', '2026-01', '15000.00', '0.00', '0.00', '0.00', '0.00', '0.00', '15000.00', '未发放', null, '2026-06-14 17:52:57', '陈芳');
INSERT INTO `salary` VALUES ('79', '6', '2026-01', '3600.00', '0.00', '0.00', '0.00', '0.00', '0.00', '3600.00', '未发放', null, '2026-06-14 17:52:57', '陈芳');
INSERT INTO `salary` VALUES ('80', '7', '2026-01', '3500.00', '0.00', '0.00', '0.00', '0.00', '0.00', '3500.00', '未发放', null, '2026-06-14 17:52:57', '陈芳');
INSERT INTO `salary` VALUES ('81', '8', '2026-01', '4200.00', '0.00', '0.00', '0.00', '0.00', '0.00', '4200.00', '未发放', null, '2026-06-14 17:52:57', '陈芳');
INSERT INTO `salary` VALUES ('82', '9', '2026-01', '4800.00', '0.00', '0.00', '0.00', '0.00', '0.00', '4800.00', '未发放', null, '2026-06-14 17:52:57', '陈芳');
INSERT INTO `salary` VALUES ('129', '1', '2026-04', '4500.00', '0.00', '0.00', '0.00', '0.00', '0.00', '4500.00', '未发放', null, '2026-06-16 20:49:56', '陈芳');
INSERT INTO `salary` VALUES ('130', '2', '2026-04', '3800.00', '0.00', '0.00', '0.00', '0.00', '0.00', '3800.00', '未发放', null, '2026-06-16 20:49:56', '陈芳');
INSERT INTO `salary` VALUES ('131', '3', '2026-04', '4000.00', '0.00', '0.00', '0.00', '0.00', '0.00', '4000.00', '未发放', null, '2026-06-16 20:49:56', '陈芳');
INSERT INTO `salary` VALUES ('132', '4', '2026-04', '5200.00', '0.00', '0.00', '0.00', '0.00', '0.00', '5200.00', '未发放', null, '2026-06-16 20:49:56', '陈芳');
INSERT INTO `salary` VALUES ('133', '5', '2026-04', '15000.00', '0.00', '0.00', '0.00', '0.00', '0.00', '15000.00', '未发放', null, '2026-06-16 20:49:56', '陈芳');
INSERT INTO `salary` VALUES ('134', '6', '2026-04', '3600.00', '0.00', '0.00', '0.00', '0.00', '0.00', '3600.00', '未发放', null, '2026-06-16 20:49:56', '陈芳');
INSERT INTO `salary` VALUES ('135', '7', '2026-04', '3500.00', '0.00', '0.00', '0.00', '0.00', '0.00', '3500.00', '未发放', null, '2026-06-16 20:49:56', '陈芳');
INSERT INTO `salary` VALUES ('136', '8', '2026-04', '4200.00', '0.00', '0.00', '0.00', '0.00', '0.00', '4200.00', '未发放', null, '2026-06-16 20:49:56', '陈芳');
INSERT INTO `salary` VALUES ('137', '9', '2026-04', '4800.00', '0.00', '0.00', '0.00', '0.00', '0.00', '4800.00', '未发放', null, '2026-06-16 20:49:56', '陈芳');
INSERT INTO `salary` VALUES ('138', '1', '2026-03', '4500.00', '0.00', '0.00', '0.00', '0.00', '0.00', '4500.00', '未发放', null, '2026-06-16 20:52:28', '陈芳');
INSERT INTO `salary` VALUES ('139', '2', '2026-03', '3800.00', '0.00', '0.00', '0.00', '0.00', '0.00', '3800.00', '未发放', null, '2026-06-16 20:52:28', '陈芳');
INSERT INTO `salary` VALUES ('140', '3', '2026-03', '4000.00', '0.00', '0.00', '0.00', '0.00', '0.00', '4000.00', '未发放', null, '2026-06-16 20:52:28', '陈芳');
INSERT INTO `salary` VALUES ('141', '4', '2026-03', '5200.00', '0.00', '0.00', '0.00', '0.00', '0.00', '5200.00', '未发放', null, '2026-06-16 20:52:28', '陈芳');
INSERT INTO `salary` VALUES ('142', '5', '2026-03', '15000.00', '0.00', '0.00', '0.00', '0.00', '0.00', '15000.00', '未发放', null, '2026-06-16 20:52:28', '陈芳');
INSERT INTO `salary` VALUES ('143', '6', '2026-03', '3600.00', '0.00', '0.00', '0.00', '0.00', '0.00', '3600.00', '未发放', null, '2026-06-16 20:52:28', '陈芳');
INSERT INTO `salary` VALUES ('144', '7', '2026-03', '3500.00', '0.00', '0.00', '0.00', '0.00', '0.00', '3500.00', '未发放', null, '2026-06-16 20:52:28', '陈芳');
INSERT INTO `salary` VALUES ('145', '8', '2026-03', '4200.00', '0.00', '0.00', '0.00', '0.00', '0.00', '4200.00', '未发放', null, '2026-06-16 20:52:28', '陈芳');
INSERT INTO `salary` VALUES ('146', '9', '2026-03', '4800.00', '0.00', '0.00', '0.00', '0.00', '0.00', '4800.00', '未发放', null, '2026-06-16 20:52:28', '陈芳');
INSERT INTO `salary` VALUES ('220', '1', '2026-05', '4500.00', '4138.00', '0.00', '0.00', '0.00', '0.00', '362.00', '未发放', null, '2026-06-17 15:46:11', '陈芳');
INSERT INTO `salary` VALUES ('221', '2', '2026-05', '3800.00', '3514.20', '0.00', '0.00', '0.00', '0.00', '285.80', '未发放', null, '2026-06-17 15:46:11', '陈芳');
INSERT INTO `salary` VALUES ('222', '3', '2026-05', '4000.00', '3678.20', '0.00', '0.00', '0.00', '0.00', '321.80', '未发放', null, '2026-06-17 15:46:11', '陈芳');
INSERT INTO `salary` VALUES ('223', '4', '2026-05', '5200.00', '4781.60', '0.00', '0.00', '0.00', '0.00', '418.40', '未发放', null, '2026-06-17 15:46:11', '陈芳');
INSERT INTO `salary` VALUES ('224', '5', '2026-05', '15000.00', '13793.20', '0.00', '0.00', '0.00', '0.00', '1206.80', '未发放', null, '2026-06-17 15:46:11', '陈芳');
INSERT INTO `salary` VALUES ('225', '6', '2026-05', '3600.00', '3475.92', '0.00', '0.00', '0.00', '0.00', '124.08', '未发放', null, '2026-06-17 15:46:11', '陈芳');
INSERT INTO `salary` VALUES ('226', '7', '2026-05', '3500.00', '3218.40', '0.00', '0.00', '0.00', '0.00', '281.60', '未发放', null, '2026-06-17 15:46:11', '陈芳');
INSERT INTO `salary` VALUES ('227', '8', '2026-05', '4200.00', '3862.00', '0.00', '0.00', '0.00', '0.00', '338.00', '未发放', null, '2026-06-17 15:46:11', '陈芳');
INSERT INTO `salary` VALUES ('228', '9', '2026-05', '4800.00', '4413.80', '0.00', '0.00', '0.00', '0.00', '386.20', '未发放', null, '2026-06-17 15:46:11', '陈芳');
INSERT INTO `salary` VALUES ('229', '19', '2026-05', '15000.00', '14482.86', '0.00', '0.00', '0.00', '0.00', '517.14', '未发放', null, '2026-06-17 15:46:11', '陈芳');
INSERT INTO `salary` VALUES ('230', '22', '2026-05', '50000.00', '48275.85', '0.00', '0.00', '0.00', '0.00', '1724.15', '已发放', '2026-06-17', '2026-06-17 15:46:11', '陈芳');
INSERT INTO `salary` VALUES ('301', '23', '2026-06', '50000.00', '50574.70', '0.00', '0.00', '0.00', '0.00', '-574.70', '未发放', null, '2026-06-25 08:10:30', '陈芳');
INSERT INTO `salary` VALUES ('305', '1', '2026-06', '4500.00', '4178.00', '0.00', '0.00', '0.00', '0.00', '322.00', '未发放', null, '2026-06-25 10:33:36', '陈芳');
INSERT INTO `salary` VALUES ('306', '2', '2026-06', '3800.00', '3668.91', '0.00', '0.00', '0.00', '0.00', '131.09', '未发放', null, '2026-06-25 10:33:36', '陈芳');
INSERT INTO `salary` VALUES ('307', '3', '2026-06', '4000.00', '3554.29', '0.00', '0.00', '0.00', '0.00', '445.71', '未发放', null, '2026-06-25 10:33:36', '陈芳');
INSERT INTO `salary` VALUES ('308', '4', '2026-06', '5200.00', '5040.68', '0.00', '0.00', '0.00', '0.00', '159.32', '未发放', null, '2026-06-25 10:33:36', '陈芳');
INSERT INTO `salary` VALUES ('309', '5', '2026-06', '15000.00', '13163.54', '0.00', '0.00', '0.00', '0.00', '1836.46', '未发放', null, '2026-06-25 10:33:36', '陈芳');
INSERT INTO `salary` VALUES ('310', '6', '2026-06', '3600.00', '3495.92', '0.00', '0.00', '0.00', '0.00', '104.08', '未发放', null, '2026-06-25 10:33:36', '陈芳');
INSERT INTO `salary` VALUES ('311', '7', '2026-06', '3500.00', '3399.32', '0.00', '0.00', '0.00', '0.00', '100.68', '未发放', null, '2026-06-25 10:33:36', '陈芳');
INSERT INTO `salary` VALUES ('312', '8', '2026-06', '4200.00', '4075.10', '0.00', '0.00', '0.00', '0.00', '124.90', '未发放', null, '2026-06-25 10:33:36', '陈芳');
INSERT INTO `salary` VALUES ('313', '9', '2026-06', '4800.00', '4654.49', '0.00', '0.00', '0.00', '0.00', '145.51', '未发放', null, '2026-06-25 10:33:36', '陈芳');
INSERT INTO `salary` VALUES ('314', '19', '2026-06', '15000.00', '15172.52', '0.00', '0.00', '0.00', '0.00', '-172.52', '未发放', null, '2026-06-25 10:33:36', '陈芳');
INSERT INTO `salary` VALUES ('315', '20', '2026-06', '80000.00', '80919.52', '0.00', '0.00', '0.00', '0.00', '-919.52', '未发放', null, '2026-06-25 10:33:36', '陈芳');
INSERT INTO `salary` VALUES ('316', '22', '2026-06', '50000.00', '48275.85', '0.00', '0.00', '0.00', '0.00', '1724.15', '未发放', null, '2026-06-25 10:33:36', '陈芳');
INSERT INTO `salary` VALUES ('317', '25', '2026-06', '8000.00', '8092.04', '0.00', '0.00', '0.00', '0.00', '-92.04', '未发放', null, '2026-06-25 10:33:36', '陈芳');
INSERT INTO `salary` VALUES ('318', '26', '2026-06', '10000.00', '10114.94', '0.00', '0.00', '0.00', '0.00', '-114.94', '未发放', null, '2026-06-25 10:33:36', '陈芳');
INSERT INTO `salary` VALUES ('319', '27', '2026-06', '3500.00', '3540.24', '0.00', '0.00', '0.00', '0.00', '-40.24', '未发放', null, '2026-06-25 10:33:36', '陈芳');

-- ----------------------------
-- Table structure for `stock`
-- ----------------------------
DROP TABLE IF EXISTS `stock`;
CREATE TABLE `stock` (
  `stock_id` int NOT NULL AUTO_INCREMENT COMMENT '物品编号',
  `item_name` varchar(100) NOT NULL COMMENT '物品名称',
  `category` varchar(50) DEFAULT NULL COMMENT '分类',
  `specification` varchar(50) DEFAULT NULL COMMENT '规格',
  `unit` varchar(20) DEFAULT NULL COMMENT '单位',
  `current_quantity` int DEFAULT '0' COMMENT '当前库存',
  `unit_price` decimal(10,2) DEFAULT NULL COMMENT '单价',
  `warning_level` int DEFAULT '10' COMMENT '预警数量',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`stock_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='库存物品表';

-- ----------------------------
-- Records of stock
-- ----------------------------
INSERT INTO `stock` VALUES ('1', '青岛啤酒', '酒水', '500ml', '瓶', '21', '11.97', '20', '2026-06-24 21:32:44');
INSERT INTO `stock` VALUES ('2', '可口可乐', '酒水', '330ml', '听', '67', '4.95', '15', '2026-06-25 10:31:00');
INSERT INTO `stock` VALUES ('3', '大米', '食品', '50kg', '袋', '10', '110.00', '3', '2026-05-28 10:17:29');
INSERT INTO `stock` VALUES ('4', '面粉', '食品', '25kg', '袋', '8', '55.00', '2', '2026-05-28 10:17:29');
INSERT INTO `stock` VALUES ('5', '洗发水', '日用品', '200ml', '瓶', '30', '18.00', '10', '2026-05-28 10:17:29');
INSERT INTO `stock` VALUES ('6', '卫生纸', '日用品', '10卷装', '提', '11', '12.73', '10', '2026-06-18 10:00:02');
INSERT INTO `stock` VALUES ('7', '强爽', '酒水', '600ml', '听', '106', '5.27', '10', '2026-06-24 21:50:59');

-- ----------------------------
-- Table structure for `stock_in`
-- ----------------------------
DROP TABLE IF EXISTS `stock_in`;
CREATE TABLE `stock_in` (
  `in_id` int NOT NULL AUTO_INCREMENT COMMENT '入库编号',
  `stock_id` int NOT NULL COMMENT '物品编号',
  `quantity` int NOT NULL COMMENT '入库数量',
  `unit_price` decimal(10,2) DEFAULT NULL COMMENT '入库单价',
  `total_amount` decimal(10,2) DEFAULT NULL COMMENT '总金额',
  `supplier` varchar(200) DEFAULT NULL COMMENT '供应商',
  `in_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间',
  `operator` varchar(50) DEFAULT NULL COMMENT '操作员',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`in_id`),
  KEY `stock_id` (`stock_id`),
  CONSTRAINT `stock_in_ibfk_1` FOREIGN KEY (`stock_id`) REFERENCES `stock` (`stock_id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='入库记录表';

-- ----------------------------
-- Records of stock_in
-- ----------------------------
INSERT INTO `stock_in` VALUES ('1', '1', '50', '12.00', '600.00', '青岛啤酒厂', '2026-05-26 14:00:00', '周华', '补货');
INSERT INTO `stock_in` VALUES ('2', '2', '30', '5.00', '150.00', '可乐公司', '2026-05-26 14:30:00', '周华', '');
INSERT INTO `stock_in` VALUES ('3', '3', '5', '110.00', '550.00', '粮油批发', '2026-05-20 09:00:00', '周华', '');
INSERT INTO `stock_in` VALUES ('4', '5', '20', '18.00', '360.00', '日化商贸', '2026-05-25 10:00:00', '周华', '');
INSERT INTO `stock_in` VALUES ('6', '1', '1', '8.00', null, null, '2026-06-09 21:40:10', null, null);
INSERT INTO `stock_in` VALUES ('11', '7', '1', '5.00', null, null, '2026-06-10 01:02:49', null, null);
INSERT INTO `stock_in` VALUES ('12', '7', '2', '12.00', '24.00', '江小白', '2026-06-10 01:21:50', '孙敏', '');
INSERT INTO `stock_in` VALUES ('13', '7', '8', '10.00', '80.00', '娃哈哈', '2026-06-14 00:58:13', '李强', '');
INSERT INTO `stock_in` VALUES ('14', '7', '1', '8.00', '8.00', '', '2026-06-14 00:58:41', '李强', '');
INSERT INTO `stock_in` VALUES ('15', '6', '6', '5.00', '30.00', '思思', '2026-06-18 10:00:02', '李强', '');
INSERT INTO `stock_in` VALUES ('16', '2', '1', '4.00', '4.00', '脆波波', '2026-06-24 21:46:59', '李强', '');
INSERT INTO `stock_in` VALUES ('17', '7', '100', '5.00', '500.00', '脆啵啵', '2026-06-24 21:50:59', '李强', '');
INSERT INTO `stock_in` VALUES ('18', '2', '1', '2.00', '2.00', '', '2026-06-25 10:31:00', '李强', '');

-- ----------------------------
-- Table structure for `stock_out`
-- ----------------------------
DROP TABLE IF EXISTS `stock_out`;
CREATE TABLE `stock_out` (
  `out_id` int NOT NULL AUTO_INCREMENT COMMENT '出库编号',
  `stock_id` int NOT NULL COMMENT '物品编号',
  `quantity` int NOT NULL COMMENT '出库数量',
  `receiver` varchar(50) DEFAULT NULL COMMENT '领用人',
  `department` varchar(50) DEFAULT NULL COMMENT '领用部门',
  `purpose` varchar(200) DEFAULT NULL COMMENT '用途',
  `out_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '出库时间',
  `operator` varchar(50) DEFAULT NULL COMMENT '操作员',
  PRIMARY KEY (`out_id`),
  KEY `stock_id` (`stock_id`),
  CONSTRAINT `stock_out_ibfk_1` FOREIGN KEY (`stock_id`) REFERENCES `stock` (`stock_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='出库记录表';

-- ----------------------------
-- Records of stock_out
-- ----------------------------
INSERT INTO `stock_out` VALUES ('1', '1', '20', '李强', '餐饮部', '餐厅用酒', '2026-05-27 17:00:00', '周华');
INSERT INTO `stock_out` VALUES ('2', '2', '10', '李强', '餐饮部', '餐厅用', '2026-05-27 17:05:00', '周华');
INSERT INTO `stock_out` VALUES ('3', '3', '1', '周华', '库房', '食堂领用', '2026-05-28 08:00:00', '周华');
INSERT INTO `stock_out` VALUES ('4', '5', '2', '孙敏', '康乐部', 'KTV包间用', '2026-05-27 19:30:00', '周华');
INSERT INTO `stock_out` VALUES ('5', '2', '3', null, null, null, '2026-06-07 21:11:53', null);
INSERT INTO `stock_out` VALUES ('6', '6', '45', null, null, null, '2026-06-09 22:52:54', null);
INSERT INTO `stock_out` VALUES ('7', '7', '1', '孙敏', '人事', '娱乐', '2026-06-10 01:24:22', '孙敏');
INSERT INTO `stock_out` VALUES ('8', '1', '100', '陈吉', '人事部', '团建', '2026-06-14 00:59:08', '李强');
INSERT INTO `stock_out` VALUES ('9', '7', '5', '陈吉', '人事部', '团建', '2026-06-19 02:12:20', '李强');
INSERT INTO `stock_out` VALUES ('10', '2', '1', '222', '', '', '2026-06-19 02:13:49', '李强');
INSERT INTO `stock_out` VALUES ('11', '2', '1', '陈n', '总经理室', '私用', '2026-06-19 02:43:31', '李强');
INSERT INTO `stock_out` VALUES ('12', '1', '1', '李强', '餐饮部', '团建', '2026-06-24 21:32:44', '李强');
INSERT INTO `stock_out` VALUES ('13', '2', '10', '李强', '餐饮部', '聚餐', '2026-06-24 21:48:00', '李强');
