-- phpMyAdmin SQL Dump
-- version 3.5.1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Nov 29, 2013 at 09:54 PM
-- Server version: 5.5.24-log
-- PHP Version: 5.4.3

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `24x7monitoring`
--
DROP DATABASE `24x7monitoring`;
CREATE DATABASE `24x7monitoring` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `24x7monitoring`;

-- --------------------------------------------------------

--
-- Table structure for table `EXCEPTION_LOGGER`
--

DROP TABLE IF EXISTS `EXCEPTION_LOGGER`;
CREATE TABLE IF NOT EXISTS `EXCEPTION_LOGGER` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `EXCEPTION_MESSAGE` longtext NOT NULL,
  `STACKTRACE` longtext NOT NULL,
  `CREATION_DATE` datetime NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `CREATION_DATE` (`CREATION_DATE`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=2357 ;

-- --------------------------------------------------------

--
-- Table structure for table `HTTP_REQUESTS`
--

DROP TABLE IF EXISTS `HTTP_REQUESTS`;
CREATE TABLE IF NOT EXISTS `HTTP_REQUESTS` (
  `REQUEST` varchar(500) NOT NULL,
  PRIMARY KEY (`REQUEST`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `MANAGED_ALERTS`
--

DROP TABLE IF EXISTS `MANAGED_ALERTS`;
CREATE TABLE IF NOT EXISTS `MANAGED_ALERTS` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `ITEM_NAME` varchar(500) NOT NULL,
  `ITEM_TYPE` varchar(500) NOT NULL,
  `THRESHOLD` bigint(20) NOT NULL,
  `TIME_TO_ALERT_IN_MINS` int(11) NOT NULL,
  `ALERT_EMAIL` varchar(100) NOT NULL,
  `ALERT_SMS` varchar(50) DEFAULT NULL,
  `ENABLED` int(11) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=6 ;

-- --------------------------------------------------------

--
-- Table structure for table `METHOD_SIGNATURES`
--

DROP TABLE IF EXISTS `METHOD_SIGNATURES`;
CREATE TABLE IF NOT EXISTS `METHOD_SIGNATURES` (
  `METHOD_SIGNATURE` varchar(500) NOT NULL,
  PRIMARY KEY (`METHOD_SIGNATURE`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `MONITORED_ITEM_TRACER`
--

DROP TABLE IF EXISTS `MONITORED_ITEM_TRACER`;
CREATE TABLE IF NOT EXISTS `MONITORED_ITEM_TRACER` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `ITEM_NAME` varchar(20000) DEFAULT NULL,
  `TYPE` varchar(50) NOT NULL,
  `AVERAGE` float NOT NULL,
  `MIN` bigint(20) NOT NULL,
  `MAX` bigint(20) NOT NULL,
  `COUNT` bigint(20) NOT NULL,
  `CREATION_DATE` datetime NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `AVERAGE` (`AVERAGE`,`CREATION_DATE`),
  KEY `METHOD_NAME` (`ITEM_NAME`(767)),
  KEY `TYPE` (`TYPE`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=133504 ;

-- --------------------------------------------------------

--
-- Table structure for table `REPORT_SCHEDULE`
--

DROP TABLE IF EXISTS `REPORT_SCHEDULE`;
CREATE TABLE IF NOT EXISTS `REPORT_SCHEDULE` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `ITEM_NAME` varchar(500) NOT NULL,
  `ITEM_TYPE` varchar(500) NOT NULL,
  `FREQUENCY` varchar(100) NOT NULL,
  `DAY_OF_MONTH` int(11) NOT NULL,
  `DAY_OF_WEEK` int(11) NOT NULL,
  `REPORT_HOUR` int(11) NOT NULL,
  `REPORT_MINUTE` int(11) NOT NULL,
  `REPORT_EMAIL` varchar(100) NOT NULL,
  `ENABLED` int(11) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=6 ;

-- --------------------------------------------------------

--
-- Table structure for table `SQL_QUERIES`
--

DROP TABLE IF EXISTS `SQL_QUERIES`;
CREATE TABLE IF NOT EXISTS `SQL_QUERIES` (
  `SQL_QUERY` varchar(20000) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
