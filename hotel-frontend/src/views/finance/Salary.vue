<template>
  <div class="salary-page">
    <!-- 顶部操作栏 -->
    <el-card class="toolbar-card">
      <div class="toolbar">
        <el-date-picker
          v-model="currentMonth"
          type="month"
          placeholder="选择月份"
          value-format="YYYY-MM"
          @change="loadSalaryData"
          style="width: 160px"
        />
        <el-button
          type="primary"
          @click="generateSalary"
          :loading="generateLoading"
          >生成工资</el-button
        >
        <el-button
          type="success"
          @click="exportExcel"
          :disabled="!salaryList.length"
          >导出Excel</el-button
        >
        <el-button
          type="warning"
          @click="batchPay"
          :disabled="!selectedRows.length"
          :loading="batchPayLoading"
          >批量发放</el-button
        >
      </div>
    </el-card>

    <el-row :gutter="20">
      <!-- 左侧 -->
      <el-col :xs="24" :sm="24" :md="17" :lg="17">
        <el-card class="salary-list-card">
          <template #header>📋 {{ currentMonth }} 员工工资</template>
          <el-table
            ref="salaryTable"
            :data="salaryList"
            border
            stripe
            size="small"
            v-loading="loading"
            @selection-change="handleSelectionChange"
            @row-click="handleRowClick"
          >
            <!-- 选择框 -->
            <el-table-column type="selection" width="36" align="center" />
            <!-- 姓名 -->
            <el-table-column
              prop="empName"
              label="姓名"
              fixed="left"
              width="80"
            />
            <!-- 基本工资 - 宽度 90 -->
            <el-table-column
              prop="baseSalary"
              label="基本工资"
              width="90"
              align="right"
            >
              <template #default="{ row }">¥{{ row.baseSalary }}</template>
            </el-table-column>
            <!-- 考勤扣款 - 宽度 90 -->
            <el-table-column
              prop="attendanceDeduction"
              label="考勤扣款"
              width="90"
              align="right"
            >
              <template #default="{ row }"
                >¥{{ row.attendanceDeduction }}</template
              >
            </el-table-column>
            <el-table-column label="绩效奖金" width="100" align="right">
              <template #default="{ row }">
                <div
                  class="editable-cell"
                  @click="openEditDialog(row, 'bonus')"
                >
                  ¥{{ row.bonus }}
                </div>
              </template>
            </el-table-column>
            <el-table-column label="加班费" width="90" align="right">
              <template #default="{ row }">
                <div
                  class="editable-cell"
                  @click="openEditDialog(row, 'overtimePay')"
                >
                  ¥{{ row.overtimePay }}
                </div>
              </template>
            </el-table-column>
            <el-table-column label="其他加项" width="90" align="right">
              <template #default="{ row }">
                <div
                  class="editable-cell"
                  @click="openEditDialog(row, 'otherAdd')"
                >
                  ¥{{ row.otherAdd }}
                </div>
              </template>
            </el-table-column>
            <el-table-column label="其他扣款" width="90" align="right">
              <template #default="{ row }">
                <div
                  class="editable-cell"
                  @click="openEditDialog(row, 'otherDeduct')"
                >
                  ¥{{ row.otherDeduct }}
                </div>
              </template>
            </el-table-column>
            <el-table-column
              prop="totalSalary"
              label="实发工资"
              width="100"
              align="right"
            >
              <template #default="{ row }">
                <span style="font-weight: 600">¥{{ row.totalSalary }}</span>
              </template>
            </el-table-column>
            <el-table-column label="发放状态" width="80" align="center">
              <template #default="{ row }">
                <el-tag
                  :type="row.payStatus === '已发放' ? 'success' : 'danger'"
                  size="small"
                >
                  {{ row.payStatus === "已发放" ? "已发" : "未发" }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column
              label="操作"
              width="80"
              fixed="right"
              align="center"
            >
              <template #default="{ row }">
                <el-button
                  v-if="row.payStatus !== '已发放'"
                  type="text"
                  size="small"
                  @click="paySingle(row.salaryId)"
                  >发放</el-button
                >
                <span v-else class="disabled-text">已发</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 右侧 -->
      <el-col :xs="24" :sm="24" :md="7" :lg="7">
        <el-card class="history-card">
          <template #header>📜 工资历史</template>
          <div class="history-header" v-if="selectedEmpName">
            {{ selectedEmpName }}
          </div>
          <el-table
            :data="historyList"
            border
            stripe
            v-loading="historyLoading"
            size="small"
            max-height="400"
          >
            <el-table-column prop="yearMonth" label="月份" width="95" />
            <el-table-column
              prop="totalSalary"
              label="实发"
              width="90"
              align="right"
            >
              <template #default="{ row }">¥{{ row.totalSalary }}</template>
            </el-table-column>
            <el-table-column label="状态" width="65" align="center">
              <template #default="{ row }">
                <el-tag
                  :type="row.payStatus === '已发放' ? 'success' : 'danger'"
                  size="small"
                >
                  {{ row.payStatus === "已发放" ? "已发" : "未发" }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="75" align="center">
              <template #default="{ row }">
                <el-button
                  type="text"
                  size="small"
                  @click="printSalarySlip(row)"
                  >工资条</el-button
                >
              </template>
            </el-table-column>
          </el-table>

          <el-divider style="margin: 12px 0 8px 0">扣款说明</el-divider>
          <div class="deduction-info">
            <p><strong>迟到</strong>：20元/次</p>
            <p><strong>早退</strong>：20元/次</p>
            <p><strong>旷工</strong>：扣除当日工资（基本工资 ÷ 21.75）</p>
            <p><strong>请假</strong>：扣除当日工资（基本工资 ÷ 21.75）</p>
            <p style="font-size: 12px; color: #909399; margin-top: 6px">
              * 绩效奖金、加班费等为手动编辑项，不自动扣款
            </p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 编辑弹窗 -->
    <el-dialog
      v-model="editDialogVisible"
      :title="'编辑 ' + editFieldName"
      width="400px"
    >
      <el-form :model="editForm" label-width="100px">
        <el-form-item :label="editFieldName">
          <el-input-number v-model="editForm.value" :precision="2" :step="50" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveEdit">保存</el-button>
      </template>
    </el-dialog>
    <SalarySlip ref="salarySlipRef" />
  </div>
</template>
<script setup>
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import request from "@/utils/request";
import SalarySlip from "@/components/SalarySlip.vue";
import axios from "axios";

const salarySlipRef = ref(null);
const currentMonth = ref(new Date().toISOString().slice(0, 7));
const salaryList = ref([]);
const loading = ref(false);
const generateLoading = ref(false);
const batchPayLoading = ref(false);
const selectedRows = ref([]);
const historyList = ref([]);
const historyLoading = ref(false);
const selectedEmpName = ref("");
const selectedEmpId = ref(null);

// 编辑相关
const editDialogVisible = ref(false);
const editFieldName = ref("");
const editForm = ref({ salaryId: null, field: "", value: 0 });

// 加载当月工资数据
const loadSalaryData = async () => {
  loading.value = true;
  try {
    const res = await request.get("/hr/salary/list", {
      params: { yearMonth: currentMonth.value, page: 1, size: 1000 },
    });
    salaryList.value = res.data.records || [];
  } catch (err) {
    ElMessage.error("加载工资数据失败");
  } finally {
    loading.value = false;
  }
};

// 生成工资
const generateSalary = async () => {
  await ElMessageBox.confirm(
    `确认生成 ${currentMonth.value} 月份的工资吗？如果已存在将覆盖原有记录。`,
    "提示",
    { type: "warning" },
  );
  generateLoading.value = true;
  try {
    await request.post("/hr/salary/generate", null, {
      params: { yearMonth: currentMonth.value },
    });
    ElMessage.success("工资生成成功");
    await loadSalaryData();
  } catch (err) {
    ElMessage.error(err.response?.data?.msg || "生成失败");
  } finally {
    generateLoading.value = false;
  }
};

// 批量发放
const batchPay = async () => {
  if (!selectedRows.value.length) return;
  const ids = selectedRows.value.map((r) => r.salaryId);
  await ElMessageBox.confirm(`确认发放 ${ids.length} 笔工资吗？`, "提示", {
    type: "warning",
  });
  batchPayLoading.value = true;
  try {
    await request.post("/hr/salary/pay/batch", ids);
    ElMessage.success("发放成功");
    await loadSalaryData();
    selectedRows.value = [];
  } catch (err) {
    ElMessage.error(err.response?.data?.msg || "发放失败");
  } finally {
    batchPayLoading.value = false;
  }
};

// 单独发放
const paySingle = async (salaryId) => {
  try {
    await request.post(`/hr/salary/pay/${salaryId}`);
    ElMessage.success("发放成功");
    await loadSalaryData();
    // 如果当前右侧历史显示的是该员工，刷新历史
    if (selectedEmpId.value) {
      await loadHistory(selectedEmpId.value);
    }
  } catch (err) {
    ElMessage.error(err.response?.data?.msg || "发放失败");
  }
};

// 表格多选
const handleSelectionChange = (rows) => {
  selectedRows.value = rows;
};

// 打开编辑弹窗
const openEditDialog = (row, field) => {
  if (row.payStatus === "已发放") {
    ElMessage.warning("已发放的工资不可编辑");
    return;
  }
  editForm.value = {
    salaryId: row.salaryId,
    field: field,
    value: row[field],
  };
  const fieldMap = {
    bonus: "绩效奖金",
    overtimePay: "加班费",
    otherAdd: "其他加项",
    otherDeduct: "其他扣款",
  };
  editFieldName.value = fieldMap[field] || field;
  editDialogVisible.value = true;
};

// 保存编辑
const saveEdit = async () => {
  const { salaryId, field, value } = editForm.value;
  try {
    await request.put("/hr/salary/update", { salaryId, field, value });
    ElMessage.success("更新成功");
    editDialogVisible.value = false;
    await loadSalaryData();
    if (selectedEmpId.value) {
      await loadHistory(selectedEmpId.value);
    }
  } catch (err) {
    ElMessage.error(err.response?.data?.msg || "更新失败");
  }
};

// 加载员工工资历史
const loadHistory = async (empId) => {
  historyLoading.value = true;
  try {
    const res = await request.get("/hr/salary/history", { params: { empId } });
    historyList.value = res.data || [];
  } catch (err) {
    ElMessage.error("加载历史失败");
  } finally {
    historyLoading.value = false;
  }
};

// 点击左侧员工行，查看历史
const handleRowClick = (row) => {
  selectedEmpId.value = row.empId;
  selectedEmpName.value = row.empName;
  loadHistory(row.empId);
};

const printSalarySlip = async (row) => {
  if (salarySlipRef.value) {
    await salarySlipRef.value.print(row.salaryId);
  }
};

const exportExcel = async () => {
  try {
    const token = localStorage.getItem("token");
    const baseURL = import.meta.env.VITE_API_BASE_URL; // http://localhost:8080/api
    const response = await axios.get(`${baseURL}/hr/salary/export`, {
      params: { yearMonth: currentMonth.value },
      responseType: "blob",
      headers: { Authorization: `Bearer ${token}` },
    });

    // 解析文件名
    const contentDisposition = response.headers["content-disposition"];
    let fileName = `工资表_${currentMonth.value}.xlsx`;
    if (contentDisposition) {
      const match = contentDisposition.match(/filename\*=UTF-8''([^;]+)/);
      if (match) fileName = decodeURIComponent(match[1]);
    }

    // 创建下载
    const blob = new Blob([response.data], {
      type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
    });
    const link = document.createElement("a");
    link.href = URL.createObjectURL(blob);
    link.download = fileName;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(link.href);
    ElMessage.success("导出成功");
  } catch (err) {
    console.error("导出失败:", err);
    ElMessage.error("导出失败：" + (err.message || ""));
  }
};

onMounted(() => {
  loadSalaryData();
});
</script>

<style scoped>
.salary-page {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}
.toolbar-card {
  margin-bottom: 20px;
  border-radius: 12px;
}
.toolbar {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}
.tip {
  margin-left: auto;
  color: #909399;
  font-size: 12px;
}
.salary-list-card,
.history-card {
  border-radius: 12px;
  height: 100%;
}
.history-header {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 12px;
  text-align: center;
  border-bottom: 1px solid #eef2f6;
  padding-bottom: 8px;
}
.editable-cell {
  cursor: pointer;
  color: #409eff;
  transition: 0.2s;
  display: inline-block;
  width: 100%;
}
.editable-cell:hover {
  color: #66b1ff;
  text-decoration: underline;
}
.disabled-text {
  color: #c0c4cc;
}
:deep(.el-table__row) {
  cursor: pointer;
}
:deep(.el-table__row:hover) {
  background-color: #f5f7fa;
}
:deep(.el-table .cell) {
  padding: 4px 4px;
}
.deduction-info {
  font-size: 13px;
  color: #606266;
  line-height: 1.8;
}
.deduction-info p {
  margin: 2px 0;
}
.deduction-info strong {
  color: #303133;
}
</style>
