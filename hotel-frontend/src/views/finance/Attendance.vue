<template>
  <div class="attendance-page">
    <!-- 顶部统计卡片（根据模式显示不同内容） -->
    <el-row :gutter="20" class="stats-row">
      <template v-if="!selectedEmpId">
        <el-col :span="6">
          <el-card shadow="hover" class="stats-card">
            <div class="stats-number">{{ stats.totalWorkDays }}</div>
            <div class="stats-label">本月应出勤</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stats-card">
            <div class="stats-number">
              {{ selectedDateStats.presentCount }}/{{
                selectedDateStats.totalEmployees
              }}
            </div>
            <div class="stats-label">出勤人数</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stats-card">
            <div class="stats-number">
              {{ selectedDateStats.lateEarlyCount }}
            </div>
            <div class="stats-label">迟到/早退</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stats-card">
            <div class="stats-number">{{ selectedDateStats.leaveCount }}</div>
            <div class="stats-label">请假人数</div>
          </el-card>
        </el-col>
      </template>
      <template v-else>
        <el-col :span="5">
          <el-card shadow="hover" class="stats-card">
            <div class="stats-number">{{ empStats.totalWorkDays }}</div>
            <div class="stats-label">本月应出勤</div>
          </el-card>
        </el-col>
        <el-col :span="5">
          <el-card shadow="hover" class="stats-card">
            <div class="stats-number">{{ empStats.normalDays }}</div>
            <div class="stats-label">正常出勤</div>
          </el-card>
        </el-col>
        <el-col :span="5">
          <el-card shadow="hover" class="stats-card">
            <div class="stats-number" style="color: #e6a23c">
              {{ empStats.lateEarlyCount }}
            </div>
            <div class="stats-label">迟到/早退</div>
          </el-card>
        </el-col>
        <el-col :span="5">
          <el-card shadow="hover" class="stats-card">
            <div class="stats-number" style="color: #409eff">
              {{ empStats.leaveDays }}
            </div>
            <div class="stats-label">请假</div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="stats-card">
            <div class="stats-number" style="color: #f56c6c">
              {{ empStats.absentDays }}
            </div>
            <div class="stats-label">缺勤/旷工</div>
          </el-card>
        </el-col>
      </template>
    </el-row>

    <el-row :gutter="20">
      <!-- 左侧：考勤日历（带小圆点 + 背景色） -->
      <el-col :xs="24" :sm="24" :md="10" :lg="10">
        <el-card class="calendar-card">
          <template #header>
            <div
              style="
                display: flex;
                justify-content: space-between;
                align-items: center;
              "
            >
              <span>📅 考勤日历</span>
              <div>
                <el-date-picker
                  v-model="currentMonth"
                  type="month"
                  placeholder="选择月份"
                  value-format="YYYY-MM"
                  @change="onMonthChange"
                  style="width: 140px"
                />
                <el-button
                  v-if="selectedEmpId"
                  type="text"
                  @click="clearSelectedEmp"
                  style="margin-left: 12px"
                  >返回全局视图</el-button
                >
              </div>
            </div>
          </template>
          <el-calendar v-model="calendarDate">
            <template #date-cell="{ data }">
              <div class="calendar-cell" :class="getDateStatusClass(data.day)">
                <span class="day-num">{{ data.day.split("-")[2] }}</span>
                <div class="status-dots">
                  <template v-if="!selectedEmpId">
                    <span
                      v-if="getDateStatus(data.day).hasNormal"
                      class="dot dot-normal"
                    ></span>
                    <span
                      v-if="getDateStatus(data.day).hasLate"
                      class="dot dot-late"
                    ></span>
                    <span
                      v-if="getDateStatus(data.day).hasAbsent"
                      class="dot dot-absent"
                    ></span>
                    <span
                      v-if="getDateStatus(data.day).hasNotPunch"
                      class="dot dot-not"
                    ></span>
                    <span
                      v-if="getDateStatus(data.day).hasLeave"
                      class="dot dot-leave"
                    ></span>
                  </template>
                  <template v-else>
                    <span class="dot" :class="getEmpDotClass(data.day)"></span>
                  </template>
                </div>
              </div>
            </template>
          </el-calendar>
        </el-card>
      </el-col>

      <!-- 右侧：打卡模块 + 当日考勤表格 -->
      <el-col :xs="24" :sm="24" :md="14" :lg="14">
        <!-- 今日打卡卡片 -->
        <el-card class="punch-card">
          <template #header>⏰ 今日打卡</template>
          <div
            style="
              display: flex;
              flex-wrap: nowrap;
              align-items: center;
              gap: 8px;
            "
          >
            <el-select
              v-model="punchEmpId"
              placeholder="员工"
              clearable
              filterable
              style="width: 100px"
            >
              <el-option
                v-for="e in activeEmployees"
                :key="e.empId"
                :label="e.empName"
                :value="e.empId"
              />
            </el-select>
            <el-date-picker
              v-model="punchDate"
              type="date"
              placeholder="日期"
              value-format="YYYY-MM-DD"
              :disabled-date="disabledDate"
              style="width: 150px"
            />
            <el-button type="primary" size="default" @click="doPunch('in')"
              >上班</el-button
            >
            <el-button type="warning" size="default" @click="doPunch('out')"
              >下班</el-button
            >
            <el-button type="success" size="default" @click="doLeave"
              >请假</el-button
            >
          </div>
        </el-card>

        <!-- 考勤表格（全局模式显示选中日期所有员工，员工模式显示个人整月记录） -->
        <el-card class="day-attendance-card">
          <template #header>
            <div
              style="
                display: flex;
                justify-content: space-between;
                align-items: center;
              "
            >
              <span>{{ tableTitle }}</span>
              <el-button type="text" size="small" @click="refreshTable"
                >刷新</el-button
              >
            </div>
          </template>
          <div :class="{ 'scroll-table': selectedEmpId }">
            <el-table
              :data="tableData"
              border
              stripe
              size="small"
              v-loading="tableLoading"
              max-height="500"
            >
              <el-table-column
                prop="empName"
                label="员工姓名"
                v-if="!selectedEmpId"
              />
              <el-table-column prop="workDate" label="日期" width="120" />
              <el-table-column prop="status" label="状态" width="80">
                <template #default="{ row }">
                  <el-tag :type="statusTagType(row.status)" size="small">{{
                    row.status || "未打卡"
                  }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column
                prop="checkInTime"
                label="上班时间"
                width="150"
              />
              <el-table-column
                prop="checkOutTime"
                label="下班时间"
                width="150"
              />
              <el-table-column prop="workHours" label="工作时长" width="80" />
              <el-table-column label="操作" width="80" v-if="!selectedEmpId">
                <template #default="{ row }">
                  <el-button type="text" @click="editAttendance(row)"
                    >编辑</el-button
                  >
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 编辑考勤弹窗 -->
    <el-dialog v-model="editDialogVisible" title="编辑考勤" width="500px">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="日期">
          <el-date-picker
            v-model="editForm.workDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择日期"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="editForm.status">
            <el-option label="正常" value="正常" />
            <el-option label="迟到" value="迟到" />
            <el-option label="旷工" value="旷工" />
            <el-option label="早退" value="早退" />
            <el-option label="请假" value="请假" />
          </el-select>
        </el-form-item>
        <el-form-item label="工作时长">
          <el-input-number
            v-model="editForm.workHours"
            :precision="1"
            :step="0.5"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="editForm.remark" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveAttendance">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from "vue";
import { ElMessage } from "element-plus";
import request from "@/utils/request";

// 日期相关
const currentMonth = ref(new Date().toISOString().slice(0, 7));
const calendarDate = ref(new Date());
const selectedEmpId = ref(null);
const selectedEmpName = ref("");
const employees = ref([]);
const punchEmpId = ref(null);
const punchDate = ref(new Date().toISOString().slice(0, 10));
// 表格相关
const tableData = ref([]);
const tableLoading = ref(false);
const editDialogVisible = ref(false);
const editForm = ref({
  attendanceId: null,
  empId: null,
  workDate: "",
  status: "",
  workHours: 0,
  remark: "",
});
// 统计数据
const stats = ref({ totalWorkDays: 0 });
const empStats = ref({
  totalWorkDays: 0,
  normalDays: 0,
  lateEarlyCount: 0,
  leaveDays: 0,
  absentDays: 0,
});
const selectedDateStats = ref({
  presentCount: 0,
  lateEarlyCount: 0,
  leaveCount: 0,
  absentCount: 0,
  totalEmployees: 0,
});
// 日历数据映射
const calendarDataMap = ref({}); // 全局模式：日期 -> { hasNormal, hasLate, hasAbsent, hasNotPunch }
const empCalendarMap = ref({}); // 员工模式：日期 -> 状态字符串

// 辅助函数
const formatDate = (date) => {
  if (!date) return "";
  const d = new Date(date);
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
};

const countWorkDaysInMonth = (yearMonth) => {
  const [year, month] = yearMonth.split("-").map(Number);
  const lastDay = new Date(year, month, 0).getDate();
  let workDays = 0;
  for (let d = 1; d <= lastDay; d++) {
    const dayOfWeek = new Date(year, month - 1, d).getDay();
    if (dayOfWeek !== 0 && dayOfWeek !== 6) workDays++;
  }
  return workDays;
};
// 所有员工
const activeEmployees = ref([]); // 当前在职（用于打卡下拉）

const loadEmployees = async () => {
  const res = await request.get("/hr/employee/all");
  employees.value = res.data;
  activeEmployees.value = res.data.filter((e) => e.status === "在职");
};
const disabledDate = (time) => {
  return time.getTime() > Date.now();
};

// ========== 全局模式 ==========
const loadGlobalData = async () => {
  const res = await request.get("/hr/attendance/list", {
    params: { page: 1, size: 1000, yearMonth: currentMonth.value },
  });
  const records = res.data.records || [];
  const dateMap = {};
  for (const att of records) {
    const date = att.workDate;
    if (!dateMap[date])
      dateMap[date] = {
        hasNormal: false,
        hasLate: false,
        hasAbsent: false,
        hasLeave: false,
        hasNotPunch: true,
      };
    const status = att.status;
    if (status === "正常") {
      dateMap[date].hasNormal = true;
      dateMap[date].hasNotPunch = false;
    } else if (status === "迟到" || status === "早退") {
      dateMap[date].hasLate = true;
      dateMap[date].hasNotPunch = false;
    } else if (status === "旷工") {
      dateMap[date].hasAbsent = true;
      dateMap[date].hasNotPunch = false;
    } else if (status === "请假") {
      dateMap[date].hasLeave = true;
      dateMap[date].hasNotPunch = false;
    }
  }
  calendarDataMap.value = dateMap;
  stats.value.totalWorkDays = countWorkDaysInMonth(currentMonth.value);
};

// 加载选中日期的统计（出勤人数、迟到人数、旷工人数）
const loadSelectedDateStats = async (dateObj) => {
  const dateStr = formatDate(dateObj);
  if (!dateStr) return;

  const allEmp = employees.value;
  const activeEmployees = allEmp.filter((emp) => {
    const hire = emp.hireDate;
    const leave = emp.leaveDate;
    return hire && hire <= dateStr && (leave == null || leave > dateStr);
  });
  const totalEmployees = activeEmployees.length;

  const res = await request.get("/hr/attendance/list", {
    params: { page: 1, size: 1000, yearMonth: dateStr.slice(0, 7) },
  });
  const allRecords = res.data.records || [];
  const dayRecords = allRecords.filter((r) => r.workDate === dateStr);

  let presentCount = 0,
    lateEarlyCount = 0,
    leaveCount = 0;
  for (const att of dayRecords) {
    if (att.status === "正常") {
      presentCount++;
    } else if (att.status === "迟到" || att.status === "早退") {
      presentCount++;
      lateEarlyCount++;
    } else if (att.status === "请假") {
      leaveCount++;
    }
  }

  const now = new Date();
  const isToday = dateStr === formatDate(now);
  let absentCount = totalEmployees - presentCount - leaveCount;
  if (isToday && now.getHours() < 18) {
    absentCount = 0;
  }

  selectedDateStats.value = {
    presentCount,
    lateEarlyCount,
    leaveCount,
    absentCount,
    totalEmployees,
  };
};

// 加载全局表格（选中日期的所有员工记录）
const loadGlobalTable = async (dateObj) => {
  const dateStr = formatDate(dateObj);
  if (!dateStr) return;
  tableLoading.value = true;
  try {
    const res = await request.get("/hr/attendance/list", {
      params: { page: 1, size: 1000, yearMonth: dateStr.slice(0, 7) },
    });
    const allRecords = res.data.records || [];
    tableData.value = allRecords.filter((r) => r.workDate === dateStr);
  } finally {
    tableLoading.value = false;
  }
};

// 获取全局模式日历背景色
const getDateStatusClass = (date) => {
  const info = calendarDataMap.value[date];
  if (!info) return "";
  if (info.hasLeave) return "leave-day";
  if (info.hasAbsent) return "absent-day";
  if (info.hasLate) return "late-day";
  if (info.hasNormal) return "normal-day";
  return "not-punch-day";
};

// 获取全局模式小圆点
const getDateStatus = (date) => {
  return (
    calendarDataMap.value[date] || {
      hasNormal: false,
      hasLate: false,
      hasAbsent: false,
      hasLeave: false,
      hasNotPunch: true,
    }
  );
};

// ========== 员工模式 ==========
const loadEmpData = async (empId) => {
  const res = await request.get("/hr/attendance/list", {
    params: { page: 1, size: 1000, yearMonth: currentMonth.value, empId },
  });
  const records = res.data.records || [];
  const map = {};
  let normalDays = 0,
    lateCount = 0,
    earlyLeaveCount = 0,
    leaveDays = 0,
    absentCount = 0;
  const totalWorkDays = countWorkDaysInMonth(currentMonth.value);

  // 记录所有有记录的日期（去重）
  const recordedDates = new Set();

  for (const att of records) {
    const date = att.workDate;
    map[date] = att.status;
    recordedDates.add(date);

    if (att.status === "正常") normalDays++;
    else if (att.status === "迟到") lateCount++;
    else if (att.status === "早退") earlyLeaveCount++;
    else if (att.status === "请假") leaveDays++;
    else if (att.status === "旷工") absentCount++;
  }

  // 迟到早退合并
  const lateEarlyCount = lateCount + earlyLeaveCount;

  // 有记录天数（含所有状态）
  const recordedDays = recordedDates.size;
  // 无记录缺勤天数
  const missingDays = Math.max(0, totalWorkDays - recordedDays);
  // 缺勤/旷工 = 明确旷工 + 无记录缺勤
  const absentDays = absentCount + missingDays;

  empCalendarMap.value = map;
  empStats.value = {
    totalWorkDays,
    normalDays,
    lateEarlyCount,
    leaveDays,
    absentDays,
  };
  // 员工模式表格显示整月记录
  tableData.value = records.sort((a, b) =>
    a.workDate.localeCompare(b.workDate),
  );
};

// 获取员工模式小圆点样式
const getEmpDotClass = (dateObj) => {
  const dateStr = formatDate(dateObj);
  const status = empCalendarMap.value[dateStr];
  if (!status) return "dot-not";
  if (status === "旷工") return "dot-absent";
  if (status === "迟到" || status === "早退") return "dot-late";
  if (status === "正常") return "dot-normal";
  if (status === "请假") return "dot-leave";
  return "dot-not";
};

// ========== 视图切换 ==========
const selectEmployee = (empId, empName) => {
  if (selectedEmpId.value === empId) return;
  selectedEmpId.value = empId;
  selectedEmpName.value = empName;
  loadEmpData(empId);
  // 员工模式下，日历日期不变（但背景色自动变成员工模式）
};

const clearSelectedEmp = () => {
  selectedEmpId.value = null;
  selectedEmpName.value = "";
  loadGlobalData();
  const today = new Date().toISOString().slice(0, 10);
  calendarDate.value = today;
  loadGlobalTable(today);
  loadSelectedDateStats(today);
};

const refreshTable = () => {
  if (selectedEmpId.value) {
    loadEmpData(selectedEmpId.value);
  } else {
    loadGlobalTable(calendarDate.value);
    loadSelectedDateStats(calendarDate.value);
  }
};

// 月份切换
const onMonthChange = () => {
  if (selectedEmpId.value) {
    loadEmpData(selectedEmpId.value);
  } else {
    loadGlobalData();
    const firstDay = currentMonth.value + "-01";
    calendarDate.value = firstDay;
    loadGlobalTable(firstDay);
    loadSelectedDateStats(firstDay);
  }
};

// 监听日历日期变化
watch(calendarDate, (newDate) => {
  if (!newDate) return;
  if (!selectedEmpId.value) {
    loadGlobalTable(newDate);
    loadSelectedDateStats(newDate);
  }
  // 员工模式下不改变表格（已经是整月）
});

// 监听打卡员工下拉框变化，自动切换到员工个人视图
watch(punchEmpId, (newEmpId) => {
  if (newEmpId) {
    const emp = employees.value.find((e) => e.empId === newEmpId);
    if (emp) {
      selectEmployee(emp.empId, emp.empName);
    }
  }
});

// 表格标题
const tableTitle = computed(() => {
  if (selectedEmpId.value) {
    return `${selectedEmpName.value} - 月度考勤记录`;
  } else {
    return `${formatDate(calendarDate.value)} 考勤记录`;
  }
});

const selectedDateStr = computed(() => {
  return calendarDate.value;
});

// 打卡
const doPunch = async (type) => {
  if (!punchEmpId.value) {
    ElMessage.warning("请选择员工");
    return;
  }
  const today = new Date().toISOString().slice(0, 10);
  if (punchDate.value !== today) {
    ElMessage.warning("打卡只能操作当天，如需补录历史请使用“编辑”功能");
    return;
  }
  try {
    await request.post("/hr/attendance/punch", null, {
      params: {
        empId: punchEmpId.value,
        type: type,
        date: punchDate.value,
      },
    });
    ElMessage.success("打卡成功");
    if (selectedEmpId.value && selectedEmpId.value === punchEmpId.value) {
      await loadEmpData(selectedEmpId.value);
    } else if (!selectedEmpId.value) {
      await loadGlobalData();
      await loadGlobalTable(calendarDate.value);
      await loadSelectedDateStats(calendarDate.value);
    }
  } catch (err) {
    ElMessage.error(err.response?.data?.msg || "打卡失败");
  }
};

// 编辑考勤（仅全局模式）
const editAttendance = (row) => {
  editForm.value = { ...row };
  editDialogVisible.value = true;
};
const saveAttendance = async () => {
  await request.put("/hr/attendance", editForm.value);
  ElMessage.success("保存成功");
  editDialogVisible.value = false;
  if (!selectedEmpId.value) {
    await loadGlobalData();
    await loadGlobalTable(calendarDate.value);
    await loadSelectedDateStats(calendarDate.value);
  }
};

const statusTagType = (status) => {
  if (status === "正常") return "success";
  if (status === "迟到") return "warning";
  if (status === "早退") return "warning";
  if (status === "旷工") return "danger";
  return "info";
};

const doLeave = async () => {
  if (!punchEmpId.value) {
    ElMessage.warning("请选择员工");
    return;
  }
  try {
    await request.post("/hr/attendance/leave", null, {
      params: {
        empId: punchEmpId.value,
        date: punchDate.value,
      },
    });
    ElMessage.success("已标记请假");
    // 刷新当前视图（同 doPunch）
    if (selectedEmpId.value && selectedEmpId.value === punchEmpId.value) {
      await loadEmpData(selectedEmpId.value);
    } else if (!selectedEmpId.value) {
      await loadGlobalData();
      await loadGlobalTable(calendarDate.value);
      await loadSelectedDateStats(calendarDate.value);
    }
  } catch (err) {
    ElMessage.error(err.response?.data?.msg || "操作失败");
  }
};

const getEmpDateClass = (dateObj) => {
  const dateStr = formatDate(dateObj);
  const status = empCalendarMap.value[dateStr];
  if (!status) return "not-punch-day";
  if (status === "旷工") return "absent-day";
  if (status === "迟到" || status === "早退") return "late-day";
  if (status === "正常") return "normal-day";
  if (status === "请假") return "leave-day";
  return "not-punch-day";
};

// 初始化
onMounted(async () => {
  await loadEmployees();
  await loadGlobalData();
  const today = new Date().toISOString().slice(0, 10);
  calendarDate.value = today;
  await loadGlobalTable(today);
  await loadSelectedDateStats(today);
});
</script>

<style scoped>
.attendance-page {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}
.stats-row {
  margin-bottom: 20px;
}
.stats-card {
  text-align: center;
  border-radius: 12px;
}
.stats-number {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}
.stats-label {
  font-size: 14px;
  color: #909399;
  margin-top: 8px;
}
.stats-hint {
  font-size: 12px;
  color: #c0c4cc;
  margin-top: 4px;
}
.calendar-card {
  border-radius: 12px;
}
.punch-card,
.day-attendance-card {
  border-radius: 12px;
  margin-bottom: 20px;
}
/* 日历单元格自定义样式 */
.calendar-cell {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 60px;
}
.day-num {
  font-size: 16px;
  font-weight: bold;
}
.status-dots {
  display: flex;
  gap: 4px;
  margin-top: 4px;
}
.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}
.dot-normal {
  background-color: #67c23a;
}
.dot-late {
  background-color: #e6a23c;
}
.dot-absent {
  background-color: #f56c6c;
}
.dot-not {
  background-color: #909399;
}
/* 整格背景色 */
:deep(.el-calendar-table td) {
  transition: background 0.2s;
}
:deep(.normal-day) {
  background-color: #f0f9eb;
}
:deep(.late-day) {
  background-color: #fdf6ec;
}
:deep(.absent-day) {
  background-color: #fef0f0;
}
:deep(.not-punch-day) {
  background-color: #f5f7fa;
}
/* 请假背景色（浅蓝色） */
:deep(.leave-day) {
  background-color: #e6f7ff;
}

/* 请假圆点（蓝色） */
.dot-leave {
  background-color: #409eff;
}
.scroll-table {
  max-height: 500px;
  overflow-y: auto;
}
</style>
