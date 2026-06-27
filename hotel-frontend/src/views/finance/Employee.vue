<template>
  <div class="employee-container">
    <el-row :gutter="20">
      <!-- 左侧：员工列表 -->
      <el-col :xs="24" :sm="24" :md="8" :lg="8">
        <el-card class="employee-list-card">
          <template #header>
            <div
              style="
                display: flex;
                justify-content: space-between;
                align-items: center;
              "
            >
              <span>员工列表</span>
              <el-button type="primary" size="small" @click="openAddDialog"
                >+ 新增</el-button
              >
            </div>
          </template>
          <el-input
            v-model="searchKeyword"
            placeholder="搜索姓名/电话"
            clearable
            size="small"
            style="margin-bottom: 12px"
            @input="loadEmployeeList"
          />
          <div style="display: flex; gap: 8px; margin-bottom: 12px">
            <el-select
              v-model="filterRole"
              clearable
              placeholder="角色"
              size="small"
              @change="loadEmployeeList"
            >
              <el-option label="前台接待员" value="前台接待员" />
              <el-option label="营业服务员" value="营业服务员" />
              <el-option label="财务管理员" value="财务管理员" />
              <el-option label="总经理" value="总经理" />
            </el-select>
            <el-select
              v-model="filterStatus"
              clearable
              placeholder="状态"
              size="small"
              @change="loadEmployeeList"
            >
              <el-option label="在职" value="在职" />
              <el-option label="离职" value="离职" />
            </el-select>
          </div>
          <div
            class="employee-list"
            style="max-height: calc(100vh - 280px); overflow-y: auto"
          >
            <div
              v-for="emp in employeeList"
              :key="emp.empId"
              class="employee-item"
              :class="{ active: selectedEmpId === emp.empId }"
              @click="selectEmployee(emp.empId)"
            >
              <div class="emp-info">
                <span class="emp-name">{{ emp.empName }}</span>
                <span class="emp-role-dept"
                  >{{ emp.role }} - {{ emp.department || "无部门" }}</span
                >
              </div>
              <div class="emp-status">
                <el-tag
                  :type="emp.status === '在职' ? 'success' : 'info'"
                  size="small"
                >
                  {{ emp.status }}
                </el-tag>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：员工详情 -->
      <el-col :xs="24" :sm="24" :md="16" :lg="16">
        <el-card v-if="currentEmployee" class="employee-detail-card">
          <template #header>
            <div class="detail-header">
              <span>员工详情 - {{ currentEmployee.empName }}</span>
              <div>
                <el-button type="primary" size="small" @click="openEditDialog"
                  >编辑</el-button
                >
                <el-button
                  v-if="currentEmployee.status === '在职'"
                  type="warning"
                  size="small"
                  @click="handleResign(currentEmployee.empId)"
                  >离职</el-button
                >
                <el-button
                  v-if="currentEmployee.status === '离职'"
                  type="success"
                  size="small"
                  @click="handleReinstate(currentEmployee.empId)"
                  >重新聘用</el-button
                >
                <el-button
                  type="danger"
                  size="small"
                  @click="handleDelete(currentEmployee.empId)"
                  >删除</el-button
                >
              </div>
            </div>
          </template>

          <!-- 基本信息 -->
          <el-descriptions :column="3" border>
            <el-descriptions-item label="姓名">{{
              currentEmployee.empName
            }}</el-descriptions-item>
            <el-descriptions-item label="性别">{{
              currentEmployee.gender || "未知"
            }}</el-descriptions-item>
            <el-descriptions-item label="年龄">{{
              currentEmployee.age || "-"
            }}</el-descriptions-item>
            <el-descriptions-item label="身份证">{{
              currentEmployee.idCard
            }}</el-descriptions-item>
            <el-descriptions-item label="电话">{{
              currentEmployee.phone
            }}</el-descriptions-item>
            <el-descriptions-item label="学历">{{
              currentEmployee.education || "-"
            }}</el-descriptions-item>
            <el-descriptions-item label="部门">{{
              currentEmployee.department || "-"
            }}</el-descriptions-item>
            <el-descriptions-item label="职务">{{
              currentEmployee.position || "-"
            }}</el-descriptions-item>
            <el-descriptions-item label="基本工资"
              >¥{{ currentEmployee.baseSalary }}</el-descriptions-item
            >
            <el-descriptions-item label="角色">{{
              currentEmployee.role
            }}</el-descriptions-item>
            <el-descriptions-item label="状态">{{
              currentEmployee.status
            }}</el-descriptions-item>
            <el-descriptions-item label="入职日期">{{
              currentEmployee.hireDate || "-"
            }}</el-descriptions-item>
            <el-descriptions-item label="离职日期">{{
              currentEmployee.leaveDate || "-"
            }}</el-descriptions-item>
          </el-descriptions>

          <!-- 工资历史 -->
          <el-divider>工资历史</el-divider>
          <el-table
            :data="salaryHistory"
            border
            size="small"
            v-loading="loadingSalary"
          >
            <el-table-column prop="yearMonth" label="月份" width="100" />
            <el-table-column prop="baseSalary" label="基本工资">
              <template #default="{ row }">¥{{ row.baseSalary }}</template>
            </el-table-column>
            <el-table-column prop="totalSalary" label="实发工资">
              <template #default="{ row }">¥{{ row.totalSalary }}</template>
            </el-table-column>
            <el-table-column prop="payStatus" label="发放状态" width="100">
              <template #default="{ row }">
                <el-tag
                  :type="row.payStatus === '已发放' ? 'success' : 'danger'"
                  >{{ row.payStatus }}</el-tag
                >
              </template>
            </el-table-column>
            <el-table-column prop="payDate" label="发放日期" width="120">
              <template #default="{ row }">
                {{ row.payDate || "-" }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button type="text" @click="printSalarySlip(row)"
                  >工资条</el-button
                >
              </template>
            </el-table-column>
          </el-table>

          <!-- 考勤月度汇总 -->
          <el-divider>考勤月度汇总</el-divider>
          <el-collapse
            v-model="activeAttendanceMonth"
            v-loading="loadingAttendance"
          >
            <el-collapse-item
              v-for="monthData in attendanceMonths"
              :key="monthData.month"
              :name="monthData.month"
            >
              <template #title>
                <div
                  style="
                    display: flex;
                    justify-content: space-between;
                    width: 100%;
                    padding-right: 20px;
                  "
                >
                  <span style="font-weight: bold">{{ monthData.month }}</span>
                  <span>
                    出勤：{{ monthData.presentDays }} 天 &nbsp;|&nbsp; 迟到：{{
                      monthData.lateCount
                    }}
                    次 &nbsp;|&nbsp; 早退：{{ monthData.earlyLeaveCount }} 次
                    &nbsp;|&nbsp; 缺勤/旷工：{{ monthData.absentTotal }} 天
                    <span
                      v-if="monthData.missingDays > 0"
                      style="color: #909399; font-size: 12px"
                    >
                      （含无记录 {{ monthData.missingDays }} 天）
                    </span>
                    &nbsp;|&nbsp; 请假：{{ monthData.leaveCount }} 天
                  </span>
                </div>
              </template>
              <el-table :data="monthData.details" border size="small">
                <el-table-column prop="workDate" label="日期" width="120" />
                <el-table-column prop="status" label="状态" width="100">
                  <template #default="{ row }">
                    <el-tag :type="statusTagType(row.status)" size="small">{{
                      row.status || "未打卡"
                    }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column
                  prop="checkInTime"
                  label="上班时间"
                  width="160"
                >
                  <template #default="{ row }">{{
                    row.checkInTime || "-"
                  }}</template>
                </el-table-column>
                <el-table-column
                  prop="checkOutTime"
                  label="下班时间"
                  width="160"
                >
                  <template #default="{ row }">{{
                    row.checkOutTime || "-"
                  }}</template>
                </el-table-column>
                <el-table-column prop="workHours" label="工作时长" width="100">
                  <template #default="{ row }">{{
                    row.workHours !== null ? row.workHours : "-"
                  }}</template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
          </el-collapse>
          <el-empty
            v-if="!attendanceMonths.length && !loadingAttendance"
            description="暂无考勤记录"
          />
        </el-card>
        <el-empty v-else description="请从左侧选择员工" />
      </el-col>
    </el-row>

    <!-- 编辑员工弹窗 -->
    <el-dialog v-model="editDialogVisible" title="编辑员工" width="500px">
      <el-form
        :model="editForm"
        :rules="editRules"
        ref="editFormRef"
        label-width="80px"
      >
        <el-form-item label="姓名"
          ><el-input v-model="editForm.empName"
        /></el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="editForm.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="年龄">
          <el-input-number v-model="addForm.age" :min="0" :max="120" />
        </el-form-item>
        <el-form-item label="身份证"
          ><el-input v-model="editForm.idCard" :disabled="true"
        /></el-form-item>
        <el-form-item label="电话"
          ><el-input v-model="editForm.phone"
        /></el-form-item>
        <el-form-item label="学历"
          ><el-input v-model="editForm.education"
        /></el-form-item>
        <el-form-item label="部门"
          ><el-input v-model="editForm.department"
        /></el-form-item>
        <el-form-item label="职务"
          ><el-input v-model="editForm.position"
        /></el-form-item>
        <el-form-item label="基本工资">
          <el-input-number v-model="editForm.baseSalary" :precision="2" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="editForm.role">
            <el-option label="前台接待员" value="前台接待员" />
            <el-option label="营业服务员" value="营业服务员" />
            <el-option label="财务管理员" value="财务管理员" />
            <el-option label="总经理" value="总经理" />
          </el-select>
        </el-form-item>
        <el-form-item label="密码">
          <el-input
            v-model="addForm.password"
            placeholder="不填默认为123456"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">保存</el-button>
      </template>
    </el-dialog>

    <!-- 新增员工弹窗 -->
    <el-dialog v-model="addDialogVisible" title="新增员工" width="500px">
      <el-form
        :model="addForm"
        :rules="addRules"
        ref="addFormRef"
        label-width="80px"
      >
        <el-form-item label="姓名"
          ><el-input v-model="addForm.empName"
        /></el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="addForm.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="年龄">
          <el-input-number v-model="addForm.age" :min="0" :max="120" />
        </el-form-item>
        <el-form-item label="身份证"
          ><el-input v-model="addForm.idCard"
        /></el-form-item>
        <el-form-item label="电话"
          ><el-input v-model="addForm.phone"
        /></el-form-item>
        <el-form-item label="学历"
          ><el-input v-model="addForm.education"
        /></el-form-item>
        <el-form-item label="部门"
          ><el-input v-model="addForm.department"
        /></el-form-item>
        <el-form-item label="职务"
          ><el-input v-model="addForm.position"
        /></el-form-item>
        <el-form-item label="基本工资">
          <el-input-number v-model="addForm.baseSalary" :precision="2" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="addForm.role">
            <el-option label="前台接待员" value="前台接待员" />
            <el-option label="营业服务员" value="营业服务员" />
            <el-option label="财务管理员" value="财务管理员" />
            <el-option label="总经理" value="总经理" />
          </el-select>
        </el-form-item>
        <el-form-item label="入职日期">
          <el-date-picker
            v-model="addForm.hireDate"
            type="date"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="密码">
          <el-input
            v-model="addForm.password"
            placeholder="不填默认为123456"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAdd">保存</el-button>
      </template>
    </el-dialog>
    <SalarySlip ref="salarySlipRef" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import request from "@/utils/request";
import {
  listEmployees,
  addEmployee,
  updateEmployee,
  deleteEmployee,
} from "@/api/finance";
import SalarySlip from "@/components/SalarySlip.vue";
const employeeList = ref([]);
const searchKeyword = ref("");
const filterRole = ref("");
const filterStatus = ref("");
const selectedEmpId = ref(null);
const currentEmployee = ref(null);
const salaryHistory = ref([]);
const loadingSalary = ref(false);
// 新增表单的 ref（与模板中的 ref 属性名一致）
const addFormRef = ref(null);
// 编辑表单的 ref
const editFormRef = ref(null);
const editDialogVisible = ref(false);
const editForm = reactive({
  empId: null,
  empName: "",
  gender: "男",
  idCard: "",
  phone: "",
  education: "",
  department: "",
  position: "",
  baseSalary: 0,
  role: "",
});
const addDialogVisible = ref(false);
const addForm = reactive({
  empName: "",
  gender: "男",
  age: null,
  idCard: "",
  phone: "",
  education: "",
  department: "",
  position: "",
  baseSalary: 0,
  role: "",
  hireDate: "",
  password: "",
});
const attendanceMonths = ref([]); // 考勤月度汇总数组
const loadingAttendance = ref(false); // 加载考勤数据的状态
const activeAttendanceMonth = ref(""); // 当前展开的月份（可控制默认展开）
const salarySlipRef = ref(null);
const editRules = {
  phone: [
    { required: true, message: "请输入手机号", trigger: "blur" },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: "请输入正确的手机号",
      trigger: "blur",
    },
  ],
  password: [
    {
      validator: (rule, value, callback) => {
        if (value && !/^\d{6}$/.test(value)) {
          callback(new Error("密码必须为6位数字"));
        } else {
          callback();
        }
      },
      trigger: "blur",
    },
  ],
};
const addRules = {
  idCard: [
    { required: true, message: "请输入身份证号", trigger: "blur" },
    {
      pattern:
        /^[1-9]\d{5}(18|19|20)?\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dXx]$/,
      message: "请输入正确的身份证号码（18位）",
      trigger: "blur",
    },
  ],
  phone: [
    { required: true, message: "请输入手机号", trigger: "blur" },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: "请输入正确的手机号",
      trigger: "blur",
    },
  ],
  password: [
    {
      validator: (rule, value, callback) => {
        if (value && !/^\d{6}$/.test(value)) {
          callback(new Error("密码必须为6位数字"));
        } else {
          callback();
        }
      },
      trigger: "blur",
    },
  ],
};
const printSalarySlip = async (row) => {
  if (salarySlipRef.value) {
    await salarySlipRef.value.print(row.salaryId);
  } else {
    ElMessage.warning("打印组件未加载");
  }
};
const loadEmployeeList = async () => {
  const params = {
    page: 1,
    size: 1000,
    keyword: searchKeyword.value,
    role: filterRole.value,
    status: filterStatus.value,
  };
  const res = await listEmployees(params);
  employeeList.value = res.data.records;
  if (employeeList.value.length && !selectedEmpId.value) {
    selectEmployee(employeeList.value[0].empId);
  }
};
const statusTagType = (status) => {
  if (status === "正常") return "success";
  if (status === "迟到") return "warning";
  if (status === "早退") return "warning";
  if (status === "旷工") return "danger";
  if (status === "请假") return "info";
  return "info";
};
const loadAttendanceRecords = async (empId) => {
  loadingAttendance.value = true;
  try {
    const res = await request.get("/hr/attendance/list", {
      params: { empId, page: 1, size: 10000 },
    });
    const records = res.data.records || [];

    // 按月份分组并统计
    const monthMap = new Map();
    records.forEach((att) => {
      const yearMonth = att.workDate.substring(0, 7);
      if (!monthMap.has(yearMonth)) {
        monthMap.set(yearMonth, {
          month: yearMonth,
          details: [],
          totalDays: 0,
          presentDays: 0,
          lateCount: 0,
          earlyLeaveCount: 0,
          absentCount: 0,
          leaveCount: 0,
          recordedDays: new Set(),
        });
      }
      const monthData = monthMap.get(yearMonth);
      monthData.details.push(att);
      monthData.totalDays++;
      monthData.recordedDays.add(att.workDate);

      if (
        att.status === "正常" ||
        att.status === "迟到" ||
        att.status === "早退"
      ) {
        monthData.presentDays++;
      }
      if (att.status === "迟到") monthData.lateCount++;
      if (att.status === "早退") monthData.earlyLeaveCount++;
      if (att.status === "旷工") monthData.absentCount++;
      if (att.status === "请假") monthData.leaveCount++;
    });

    // 补充计算每个月的应出勤天数和缺勤天数
    attendanceMonths.value = Array.from(monthMap.values())
      .map((monthData) => {
        // 计算应出勤天数（周一至周五）
        const [year, month] = monthData.month.split("-").map(Number);
        const lastDay = new Date(year, month, 0).getDate();
        let workDays = 0;
        for (let d = 1; d <= lastDay; d++) {
          const dayOfWeek = new Date(year, month - 1, d).getDay();
          if (dayOfWeek !== 0 && dayOfWeek !== 6) workDays++;
        }
        const recordedDays = monthData.recordedDays.size;
        const missingDays = Math.max(0, workDays - recordedDays);
        return {
          ...monthData,
          totalWorkDays: workDays,
          recordedDays: recordedDays,
          missingDays: missingDays,
          absentTotal: monthData.absentCount + missingDays, // 合并
        };
      })
      .sort((a, b) => b.month.localeCompare(a.month));

    if (attendanceMonths.value.length > 0) {
      activeAttendanceMonth.value = attendanceMonths.value[0].month;
    }
  } catch (err) {
    ElMessage.error("加载考勤记录失败");
  } finally {
    loadingAttendance.value = false;
  }
};
// 选择员工
const selectEmployee = async (empId) => {
  selectedEmpId.value = empId;
  // 加载员工详情
  const res = await request.get(`/hr/employee/detail/${empId}`);
  currentEmployee.value = res.data.employee;
  // 加载工资历史
  loadingSalary.value = true;
  try {
    const salaryRes = await request.get("/hr/salary/list", {
      params: { empId, page: 1, size: 100 },
    });
    salaryHistory.value = salaryRes.data.records || [];
  } finally {
    loadingSalary.value = false;
  }
  // 加载考勤记录
  await loadAttendanceRecords(empId);
};

// 新增
const openAddDialog = () => {
  Object.assign(addForm, {
    empName: "",
    gender: "男",
    age: null,
    idCard: "",
    phone: "",
    education: "",
    department: "",
    position: "",
    baseSalary: 0,
    role: "",
    hireDate: "",
    password: "",
  });
  addDialogVisible.value = true;
};
const submitAdd = async () => {
  await addFormRef.value.validate(); // 先验证
  if (!addForm.empName) return ElMessage.warning("姓名不能为空");
  // 若未选入职日期，默认今天
  if (!addForm.hireDate) {
    addForm.hireDate = new Date().toISOString().slice(0, 10);
  }
  await addEmployee(addForm);
  ElMessage.success("新增成功");
  addDialogVisible.value = false;
  loadEmployeeList();
};

// 编辑
const openEditDialog = () => {
  Object.assign(editForm, currentEmployee.value);
  editDialogVisible.value = true;
};
const submitEdit = async () => {
  await editFormRef.value.validate();
  const submitData = { ...editForm };
  delete submitData.idCard;
  await updateEmployee(submitData);
  ElMessage.success("修改成功");
  editDialogVisible.value = false;
  loadEmployeeList();
  if (selectedEmpId.value) selectEmployee(selectedEmpId.value);
};

// 离职
const handleResign = async (empId) => {
  await ElMessageBox.confirm("确定将该员工设为离职状态吗？", "提示", {
    type: "warning",
  });
  await request.post(`/hr/employee/resign/${empId}`);
  ElMessage.success("已标记离职");
  loadEmployeeList();
  if (selectedEmpId.value === empId) selectEmployee(empId);
};

// 重新聘用
const handleReinstate = async (empId) => {
  await ElMessageBox.confirm("确定重新聘用该员工吗？", "提示", {
    type: "info",
  });
  await request.post(`/hr/employee/reinstate/${empId}`);
  ElMessage.success("已恢复在职状态");
  loadEmployeeList();
  if (selectedEmpId.value === empId) selectEmployee(empId);
};

// 删除（后端校验）
const handleDelete = async (empId) => {
  try {
    await deleteEmployee(empId);
    ElMessage.success("删除成功");
    loadEmployeeList();
    if (selectedEmpId.value === empId) {
      selectedEmpId.value = null;
      currentEmployee.value = null;
      salaryHistory.value = [];
    }
  } catch (err) {
    ElMessage.error(err.response?.data?.msg || "删除失败");
  }
};

onMounted(() => {
  loadEmployeeList();
});
</script>

<style scoped>
.employee-container {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}
.employee-list-card {
  border-radius: 12px;
  height: calc(100vh - 70px);
  overflow-y: auto;
}
.employee-item {
  padding: 12px;
  border-bottom: 1px solid #eef2f6;
  cursor: pointer;
  transition: background 0.2s;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.employee-item:hover {
  background-color: #f5f7fa;
}
.employee-item.active {
  background-color: #e6f7ff;
  border-left: 3px solid #409eff;
}
.emp-info {
  display: flex;
  flex-direction: column;
}
.emp-name {
  font-weight: 500;
}
.emp-role-dept {
  font-size: 12px;
  color: #909399;
}
.employee-detail-card {
  border-radius: 12px;
}
.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
