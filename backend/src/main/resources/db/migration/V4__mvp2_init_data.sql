-- MVP2阶段初始化数据
-- 注意：V2已经插入了基础分类（APPROVAL, FINANCE, HR, OA），这里只插入详细分类

-- 使用V2插入的HR分类作为父分类，添加人事管理子分类
INSERT INTO wf_process_category (category_name, category_code, parent_id, sort_order) 
SELECT '请假审批', 'HR_LEAVE', id, 1 FROM wf_process_category WHERE category_code='HR';

INSERT INTO wf_process_category (category_name, category_code, parent_id, sort_order) 
SELECT '出差申请', 'HR_TRAVEL', id, 2 FROM wf_process_category WHERE category_code='HR';

INSERT INTO wf_process_category (category_name, category_code, parent_id, sort_order) 
SELECT '入职申请', 'HR_ONBOARDING', id, 3 FROM wf_process_category WHERE category_code='HR';

INSERT INTO wf_process_category (category_name, category_code, parent_id, sort_order) 
SELECT '离职申请', 'HR_RESIGNATION', id, 4 FROM wf_process_category WHERE category_code='HR';

INSERT INTO wf_process_category (category_name, category_code, parent_id, sort_order) 
SELECT '转正申请', 'HR_REGULARIZATION', id, 5 FROM wf_process_category WHERE category_code='HR';

-- 财务管理子分类
INSERT INTO wf_process_category (category_name, category_code, parent_id, sort_order) 
SELECT '报销申请', 'FINANCE_REIMBURSEMENT', id, 1 FROM wf_process_category WHERE category_code='FINANCE';

INSERT INTO wf_process_category (category_name, category_code, parent_id, sort_order) 
SELECT '付款申请', 'FINANCE_PAYMENT', id, 2 FROM wf_process_category WHERE category_code='FINANCE';

INSERT INTO wf_process_category (category_name, category_code, parent_id, sort_order) 
SELECT '借款申请', 'FINANCE_LOAN', id, 3 FROM wf_process_category WHERE category_code='FINANCE';

-- 审批流程子分类  
INSERT INTO wf_process_category (category_name, category_code, parent_id, sort_order) 
SELECT '用印申请', 'APPROVAL_SEAL', id, 1 FROM wf_process_category WHERE category_code='APPROVAL';

