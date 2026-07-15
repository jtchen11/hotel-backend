-- 幂等记录表：用于防止重复请求
CREATE TABLE IF NOT EXISTS `idempotent_record` (
  `id` BIGINT AUTO_INCREMENT COMMENT '主键',
  `idempotent_key` VARCHAR(128) NOT NULL COMMENT '幂等键（业务唯一标识）',
  `status` VARCHAR(16) NOT NULL DEFAULT 'PROCESSING' COMMENT '状态：PROCESSING / SUCCESS / FAILED',
  `result` TEXT COMMENT '处理结果（JSON）',
  `created_at` DATETIME NOT NULL COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_idempotent_key` (`idempotent_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='幂等记录表';