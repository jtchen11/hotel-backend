-- 审计日志表：记录所有写操作（增删改）的审计信息
CREATE TABLE IF NOT EXISTS `audit_log` (
  `id` BIGINT AUTO_INCREMENT COMMENT '主键',
  `operator` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '操作人',
  `operation` VARCHAR(256) NOT NULL DEFAULT '' COMMENT '操作描述（方法 + URL + 耗时）',
  `detail` TEXT COMMENT '操作详情（JSON 格式的关键参数）',
  `result` VARCHAR(16) NOT NULL DEFAULT 'SUCCESS' COMMENT '结果：SUCCESS / FAILED',
  `ip` VARCHAR(64) DEFAULT '' COMMENT '客户端 IP',
  `created_at` DATETIME NOT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `idx_operator` (`operator`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审计日志表';
