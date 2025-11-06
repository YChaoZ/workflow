-- MVP2阶段新增表
-- 注意：wf_process_category表已在V1中创建，这里不再重复创建

-- 1. 任务意见表
CREATE TABLE IF NOT EXISTS wf_task_comment (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  task_id VARCHAR(64) NOT NULL COMMENT '任务ID',
  process_instance_id VARCHAR(64) NOT NULL COMMENT '流程实例ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  user_name VARCHAR(100) COMMENT '用户名',
  comment_type VARCHAR(20) NOT NULL COMMENT '意见类型：APPROVE-同意,REJECT-拒绝,TRANSFER-转办',
  comment_text TEXT COMMENT '意见内容',
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_process_instance (process_instance_id),
  INDEX idx_task (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务意见表';

-- 2. 任务附件表
CREATE TABLE IF NOT EXISTS wf_task_attachment (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  task_id VARCHAR(64) NOT NULL COMMENT '任务ID',
  process_instance_id VARCHAR(64) NOT NULL COMMENT '流程实例ID',
  file_name VARCHAR(255) NOT NULL COMMENT '文件名',
  file_path VARCHAR(500) NOT NULL COMMENT '文件路径',
  file_size BIGINT COMMENT '文件大小（字节）',
  file_type VARCHAR(50) COMMENT '文件类型',
  uploaded_by BIGINT NOT NULL COMMENT '上传人ID',
  uploaded_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  INDEX idx_process_instance (process_instance_id),
  INDEX idx_task (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务附件表';

