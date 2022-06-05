CREATE DATABASE IF NOT EXISTS monitor;
USE monitor;

--DROP table if exists metrics;

CREATE TABLE `metrics` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `date_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `cpu_percent` float NOT NULL,
  `ram_usage` float NOT NULL,
  `disk_usage` varchar(255) NOT NULL,
  `host` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  KEY `date_time` (`date_time`,`host`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

COMMIT;