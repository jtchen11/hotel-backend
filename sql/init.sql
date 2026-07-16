-- ====================================
-- P1: 数据库索引优化
-- ====================================

-- 员工姓名：加速登录查询和员工搜索
CREATE INDEX idx_employee_emp_name ON employee (emp_name);

-- 房间号：加速房态查询和预订校验
CREATE INDEX idx_room_room_number ON room (room_number);

-- 客人电话：加速宾客查询和订单关联
CREATE INDEX idx_guest_phone ON guest (phone);

-- 入住日期：加速入住记录查询和时间范围统计
CREATE INDEX idx_guest_check_in_date ON guest (check_in_date);
