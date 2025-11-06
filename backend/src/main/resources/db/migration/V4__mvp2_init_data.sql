-- MVP2阶段初始化数据

-- 初始化流程分类数据
INSERT INTO wf_process_category (name, code, parent_id, sort_order, description) VALUES
('行政管理', 'administration', 0, 1, '行政管理类流程'),
('人事管理', 'hr', 0, 2, '人事管理类流程'),
('财务管理', 'finance', 0, 3, '财务管理类流程'),
('采购管理', 'procurement', 0, 4, '采购管理类流程'),
('项目管理', 'project', 0, 5, '项目管理类流程');

-- 行政管理子分类
INSERT INTO wf_process_category (name, code, parent_id, sort_order, description) 
SELECT '请假审批', 'leave', id, 1, '各类请假申请' FROM wf_process_category WHERE code='administration';

INSERT INTO wf_process_category (name, code, parent_id, sort_order, description) 
SELECT '出差申请', 'travel', id, 2, '出差申请流程' FROM wf_process_category WHERE code='administration';

INSERT INTO wf_process_category (name, code, parent_id, sort_order, description) 
SELECT '用印申请', 'seal', id, 3, '公章使用申请' FROM wf_process_category WHERE code='administration';

-- 人事管理子分类
INSERT INTO wf_process_category (name, code, parent_id, sort_order, description) 
SELECT '入职申请', 'onboarding', id, 1, '员工入职流程' FROM wf_process_category WHERE code='hr';

INSERT INTO wf_process_category (name, code, parent_id, sort_order, description) 
SELECT '离职申请', 'resignation', id, 2, '员工离职流程' FROM wf_process_category WHERE code='hr';

INSERT INTO wf_process_category (name, code, parent_id, sort_order, description) 
SELECT '转正申请', 'regularization', id, 3, '员工转正流程' FROM wf_process_category WHERE code='hr';

-- 财务管理子分类
INSERT INTO wf_process_category (name, code, parent_id, sort_order, description) 
SELECT '报销申请', 'reimbursement', id, 1, '费用报销流程' FROM wf_process_category WHERE code='finance';

INSERT INTO wf_process_category (name, code, parent_id, sort_order, description) 
SELECT '付款申请', 'payment', id, 2, '对外付款流程' FROM wf_process_category WHERE code='finance';

INSERT INTO wf_process_category (name, code, parent_id, sort_order, description) 
SELECT '借款申请', 'loan', id, 3, '员工借款流程' FROM wf_process_category WHERE code='finance';

