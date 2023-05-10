-- phpMyAdmin SQL Dump
-- version 3.0.1.1
-- http://www.phpmyadmin.net
--
-- �������汾: 5.1.29
-- PHP �汾: 5.2.6

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

-- --------------------------------------------------------

-- ----------------------------
-- Table structure for `admin`
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
  `username` varchar(20) NOT NULL DEFAULT '',
  `password` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_admin
-- ----------------------------
INSERT INTO `admin` VALUES ('a', 'a'); 

CREATE TABLE IF NOT EXISTS `t_userInfo` (
  `userInfoname` varchar(20)  NOT NULL COMMENT 'userInfoname',
  `password` varchar(20)  NOT NULL COMMENT '��¼����',
  `name` varchar(20)  NOT NULL COMMENT '����',
  `sex` varchar(3)  NOT NULL COMMENT '�Ա�',
  `birthday` varchar(20)  NULL COMMENT '��������',
  `telephone` varchar(20)  NULL COMMENT '��ϵ�绰',
  `email` varchar(40)  NULL COMMENT '�����ַ',
  `address` varchar(60)  NULL COMMENT '�û�סַ',
  `userPhoto` varchar(60)  NOT NULL COMMENT '������Ƭ',
  PRIMARY KEY (`userInfoname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `t_surveyInfo` (
  `paperId` int(11) NOT NULL AUTO_INCREMENT COMMENT '��¼���',
  `questionPaperName` varchar(30)  NOT NULL COMMENT '�ʾ�����',
  `faqiren` varchar(20)  NULL COMMENT '������',
  `description` varchar(100)  NULL COMMENT '�ʾ�����',
  `startDate` varchar(20)  NULL COMMENT '��������',
  `endDate` varchar(20)  NULL COMMENT '��������',
  `zhutitupian` varchar(60)  NOT NULL COMMENT '����ͼƬ',
  `publishFlag` int(11) NOT NULL COMMENT '��˱�־',
  PRIMARY KEY (`paperId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;

CREATE TABLE IF NOT EXISTS `t_questionInfo` (
  `titileId` int(11) NOT NULL AUTO_INCREMENT COMMENT '��¼���',
  `questionPaperObj` int(11) NOT NULL COMMENT '�ʾ�����',
  `titleValue` varchar(50)  NOT NULL COMMENT '��������',
  PRIMARY KEY (`titileId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;

CREATE TABLE IF NOT EXISTS `t_selectOption` (
  `optionId` int(11) NOT NULL AUTO_INCREMENT COMMENT '��¼���',
  `questionObj` int(11) NOT NULL COMMENT '������Ϣ',
  `optionContent` varchar(50)  NOT NULL COMMENT 'ѡ������',
  PRIMARY KEY (`optionId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;

CREATE TABLE IF NOT EXISTS `t_answer` (
  `answerId` int(11) NOT NULL AUTO_INCREMENT COMMENT '��¼���',
  `selectOptionObj` int(11) NOT NULL COMMENT 'ѡ����Ϣ',
  `userObj` varchar(20)  NOT NULL COMMENT '�û�',
  PRIMARY KEY (`answerId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;

CREATE TABLE IF NOT EXISTS `t_guestBook` (
  `guestBookId` int(11) NOT NULL AUTO_INCREMENT COMMENT '��¼���',
  `title` varchar(50)  NOT NULL COMMENT '���Ա���',
  `content` varchar(200)  NOT NULL COMMENT '��������',
  `userObj` varchar(20)  NOT NULL COMMENT '������',
  `addTime` varchar(20)  NULL COMMENT '����ʱ��',
  PRIMARY KEY (`guestBookId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;

ALTER TABLE t_questionInfo ADD CONSTRAINT FOREIGN KEY (questionPaperObj) REFERENCES t_surveyInfo(paperId);
ALTER TABLE t_selectOption ADD CONSTRAINT FOREIGN KEY (questionObj) REFERENCES t_questionInfo(titileId);
ALTER TABLE t_answer ADD CONSTRAINT FOREIGN KEY (selectOptionObj) REFERENCES t_selectOption(optionId);
ALTER TABLE t_answer ADD CONSTRAINT FOREIGN KEY (userObj) REFERENCES t_userInfo(userInfoname);
ALTER TABLE t_guestBook ADD CONSTRAINT FOREIGN KEY (userObj) REFERENCES t_userInfo(userInfoname);


