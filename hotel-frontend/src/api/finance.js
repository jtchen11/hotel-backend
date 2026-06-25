import request from '@/utils/request';

// 押金
export const addDeposit = (data) => request.post('/finance/deposit', data);
export const getDepositBalance = (guestId) => request.get(`/finance/deposit/balance/${guestId}`);

// 结账
export const getSettlePreview = (guestId) => request.get(`/finance/settle/prepare/${guestId}`);
export const settle = (guestId) => request.post(`/finance/settle?guestId=${guestId}`);

// 员工
export const listEmployees = (params) => request.get('/hr/employee/list', { params });
export const addEmployee = (data) => request.post('/hr/employee', data);
export const updateEmployee = (data) => request.put('/hr/employee', data);
export const deleteEmployee = (empId) => request.delete(`/hr/employee/${empId}`);

// 考勤
export const punch = (empId, type) => request.post('/hr/attendance/punch', null, { params: { empId, type } });
export const listAttendance = (params) => request.get('/hr/attendance/list', { params });
export const updateAttendance = (data) => request.put('/hr/attendance', data);

// 工资
export const generateSalary = (yearMonth) => request.post('/hr/salary/generate', null, { params: { yearMonth } });
export const listSalary = (params) => request.get('/hr/salary/list', { params });
export const updateBonus = (data) => request.put('/hr/salary/bonus', data);
export const markPaid = (salaryId) => request.post(`/hr/salary/pay/${salaryId}`);