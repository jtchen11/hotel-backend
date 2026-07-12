<template>
  <div class="finance-dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="12" :sm="12" :md="6" :lg="6">
        <el-card shadow="hover" class="stats-card">
          <div class="stats-number">{{ stats.todayCheckin }}</div>
          <div class="stats-label">今日入住</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6" :lg="6">
        <el-card shadow="hover" class="stats-card">
          <div class="stats-number">{{ stats.todayCheckout }}</div>
          <div class="stats-label">今日退房</div>
          <el-tooltip content="实际办理退房的客人数量" placement="top">
            <el-icon class="help-icon"><QuestionFilled /></el-icon>
          </el-tooltip>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6" :lg="6">
        <el-card shadow="hover" class="stats-card">
          <div class="stats-number">{{ livingCount }}</div>
          <div class="stats-label">当前在住</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6" :lg="6">
        <el-card shadow="hover" class="stats-card">
          <div class="stats-number">¥{{ stats.todayRevenue }}</div>
          <div class="stats-label">今日营收</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :xs="24" :sm="24" :md="16" :lg="16">
        <!-- 在住客人列表 -->
        <el-card class="living-guests-card" style="margin-bottom: 20px">
          <template #header>
            <span>🏠 当前在住客人</span>
          </template>
          <el-table
            :data="livingGuests"
            border
            stripe
            v-loading="loadingLiving"
            style="width: 100%"
          >
            <el-table-column prop="roomNumber" label="房间号" width="100" />
            <el-table-column prop="name" label="客人姓名" width="100" />
            <el-table-column prop="phone" label="手机号" width="120" />
            <el-table-column prop="preLeaveDate" label="预离日期" width="120" />
            <el-table-column prop="depositBalance" label="押金余额" width="120">
              <template #default="{ row }">¥{{ row.depositBalance }}</template>
            </el-table-column>
            <el-table-column
              prop="unsettledAmount"
              label="挂账总额"
              width="120"
            >
              <template #default="{ row }">¥{{ row.unsettledAmount }}</template>
            </el-table-column>
            <el-table-column label="操作" width="195" fixed="right">
              <template #default="{ row }">
                <el-button
                  size="small"
                  type="success"
                  @click="addDepositQuick(row.guestId)"
                  >加收押金</el-button
                >
                <el-button
                  size="small"
                  type="danger"
                  @click="goToSettle(row.guestId)"
                  >结账</el-button
                >
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <!-- 今日打卡列表 -->
        <el-card class="attendance-card">
          <template #header>⏰ 今日打卡</template>
          <el-table :data="todayAttendance" border stripe size="small">
            <el-table-column prop="empName" label="姓名" width="100" />
            <el-table-column prop="department" label="部门" width="100" />
            <el-table-column prop="checkInTime" label="上班时间" width="160" />
            <el-table-column prop="checkOutTime" label="下班时间" width="160" />
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="statusTagType(row.status)" size="small">{{
                  row.status
                }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="workHours" label="工作时长" width="100">
              <template #default="{ row }">{{
                row.workHours ? row.workHours + " 小时" : "--"
              }}</template>
            </el-table-column>
            <el-table-column label="备注" width="175">
              <template #default="{ row }">
                <div class="remark-cell" @click="editRemark(row)">
                  <span>{{ row.remark || "点击添加备注" }}</span>
                  <el-icon><Edit /></el-icon>
                </div>
              </template>
            </el-table-column>
          </el-table>
          <div style="margin-top: 12px; text-align: right">
            <el-button type="text" @click="$router.push('/finance/attendance')"
              >查看全部</el-button
            >
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="24" :md="8" :lg="8">
        <!-- 待处理事项卡片 -->
        <el-card class="pending-card" style="margin-bottom: 20px">
          <template #header>⚠️ 待处理事项</template>
          <div class="pending-item" @click="goToUnsettled">
            <span>🏠 超期未退房客人</span>
            <el-badge :value="pendingUnsettled" class="badge-right" />
          </div>
          <div class="pending-item" @click="goToSalary">
            <span>💰 待发放工资</span>
            <el-badge :value="pendingSalary" class="badge-right" />
          </div>
          <div class="pending-item" @click="toggleWarningList">
            <span>📦 库存预警</span>
            <el-badge :value="warningCount" class="badge-right" />
          </div>
          <el-collapse-transition>
            <div v-show="showWarningList" class="warning-detail">
              <el-table
                :data="warningList"
                border
                size="small"
                v-loading="warningLoading"
              >
                <el-table-column prop="itemName" label="物品名称" />
                <el-table-column prop="category" label="分类" />
                <el-table-column prop="currentQuantity" label="当前库存" />
                <el-table-column prop="warningLevel" label="预警线" />
              </el-table>
            </div>
          </el-collapse-transition>
        </el-card>

        <!-- 近期订单统计（近7天营收） -->
        <el-card class="stats-chart-card">
          <template #header>📈 近期订单统计</template>
          <div ref="chartRef" style="height: 250px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 备注编辑弹窗 -->
    <el-dialog v-model="remarkDialogVisible" title="编辑备注" width="400px">
      <el-input
        v-model="remarkContent"
        type="textarea"
        :rows="3"
        placeholder="请输入备注信息"
      />
      <template #footer>
        <el-button @click="remarkDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveRemark">保存</el-button>
      </template>
    </el-dialog>

    <!-- 加收押金弹窗 -->
    <el-dialog v-model="depositDialogVisible" title="加收押金" width="400px">
      <el-form :model="depositForm" label-width="80px">
        <el-form-item label="金额">
          <el-input-number
            v-model="depositForm.amount"
            :precision="2"
            :step="100"
            :min="0"
          />
        </el-form-item>
        <el-form-item label="支付方式">
          <el-select v-model="depositForm.payMethod">
            <el-option label="微信" value="微信" />
            <el-option label="支付宝" value="支付宝" />
            <el-option label="现金" value="现金" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="depositForm.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="depositDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitDeposit">确认加收</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, nextTick } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { QuestionFilled } from "@element-plus/icons-vue";
import * as echarts from "echarts";
import request from "@/utils/request";
import { PAY_METHODS, PAY_METHOD_OPTIONS } from "@/constants/payment";
import { Edit } from "@element-plus/icons-vue";

const warningLoading = ref(false);

const toggleWarningList = async () => {
  showWarningList.value = !showWarningList.value;
  if (showWarningList.value && warningList.value.length === 0) {
    await loadWarningList();
  }
};

const loadWarningList = async () => {
  warningLoading.value = true;
  try {
    const res = await request.get("/warehouse/warning");
    warningList.value = res.data || [];
  } catch (error) {
    ElMessage.error("加载预警列表失败");
  } finally {
    warningLoading.value = false;
  }
};
const showWarningList = ref(false); // 控制预警列表是否展开
const warningList = ref([]); // 预警物品列表
const remarkDialogVisible = ref(false);
const currentAttendance = ref(null);
const remarkContent = ref("");

const editRemark = (row) => {
  currentAttendance.value = row;
  remarkContent.value = row.remark || "";
  remarkDialogVisible.value = true;
};

const saveRemark = async () => {
  if (!currentAttendance.value?.attendanceId) {
    ElMessage.warning("无法获取考勤记录ID");
    return;
  }
  try {
    await request.put("/hr/attendance", {
      attendanceId: currentAttendance.value.attendanceId,
      remark: remarkContent.value,
    });
    ElMessage.success("备注已保存");
    remarkDialogVisible.value = false;
    // 刷新今日打卡列表
    await loadTodayAttendance();
  } catch (error) {
    ElMessage.error("保存备注失败");
  }
};

const router = useRouter();
const loadingLiving = ref(false);
const livingGuests = ref([]);
const todayAttendance = ref([]);
const stats = ref({ todayCheckin: 0, todayCheckout: 0, todayRevenue: 0 });
const livingCount = ref(0);
const pendingUnsettled = ref(0);
const pendingSalary = ref(0);
const warningCount = ref(0);
const depositDialogVisible = ref(false);
const depositForm = ref({
  guestId: null,
  amount: 0,
  payMethod: PAY_METHODS.WECHAT,
  remark: "",
});
const chartRef = ref(null);
let chart = null;

// 加载在住客人（按预离日期升序）
const loadLivingGuests = async () => {
  loadingLiving.value = true;
  try {
    const res = await request.get("/finance/livingGuests");
    // 后端应返回按 pre_leave_date ASC 排序的数据
    livingGuests.value = res.data || [];
    livingCount.value = livingGuests.value.length;
  } catch (error) {
    ElMessage.error("加载在住客人列表失败");
  } finally {
    loadingLiving.value = false;
  }
};

// 加载今日打卡
const loadTodayAttendance = async () => {
  try {
    const res = await request.get("/finance/todayAttendance");
    todayAttendance.value = res.data || [];
  } catch (error) {
    ElMessage.error("加载打卡记录失败");
  }
};

// 加载统计数据
const loadStats = async () => {
  try {
    const res = await request.get("/finance/dashboardStats");
    stats.value = res.data;
  } catch (error) {
    ElMessage.error("加载统计数据失败");
  }
};

// 加载待处理数量
const loadPending = async () => {
  try {
    const unsettledRes = await request.get("/finance/unsettledCount");
    pendingUnsettled.value = unsettledRes.data || 0;
    const salaryRes = await request.get("/hr/salary/pendingCount");
    pendingSalary.value = salaryRes.data || 0;
    const warnRes = await request.get("/warehouse/warning");
    warningCount.value = warnRes.data?.length || 0;
  } catch (error) {
    console.error("加载待处理事项失败", error);
  }
};

// 加载近7天营收数据（用于图表）
const loadWeeklyRevenue = async () => {
  try {
    const res = await request.get("/finance/weeklyRevenue");
    const data = res.data || [];
    if (chart) {
      chart.setOption({
        xAxis: { type: "category", data: data.map((d) => d.date) },
        series: [
          { type: "line", data: data.map((d) => d.revenue), smooth: true },
        ],
      });
    }
  } catch (error) {
    console.error("加载营收统计失败", error);
  }
};

// 初始化图表
const initChart = () => {
  if (chartRef.value) {
    chart = echarts.init(chartRef.value);
    chart.setOption({
      tooltip: { trigger: "axis" },
      xAxis: { type: "category", name: "日期" },
      yAxis: { type: "value", name: "营收(¥)" },
      series: [{ type: "line", data: [], smooth: true }],
    });
    loadWeeklyRevenue();
  }
};

// 加收押金快捷方法
const addDepositQuick = (guestId) => {
  depositForm.value.guestId = guestId;
  depositForm.value.amount = 0;
  depositForm.value.payMethod = PAY_METHODS.WECHAT;
  depositForm.value.remark = "";
  depositDialogVisible.value = true;
};

// 提交加收押金
const submitDeposit = async () => {
  if (!depositForm.value.guestId) return;
  if (depositForm.value.amount <= 0) return ElMessage.warning("请输入有效金额");
  await request.post("/finance/deposit", {
    guestId: depositForm.value.guestId,
    amount: depositForm.value.amount,
    type: "追加",
    payMethod: depositForm.value.payMethod,
    remark: depositForm.value.remark,
  });
  ElMessage.success("加收成功");
  depositDialogVisible.value = false;
  loadLivingGuests(); // 刷新列表
};

// 跳转到结账页面
const goToSettle = (guestId) => {
  router.push({ path: "/finance/bill", query: { guestId, action: "settle" } });
};

// 待处理事项跳转
const goToUnsettled = () => router.push("/finance/bill?filter=unsettled");
const goToSalary = () => router.push("/finance/salary");

// 状态标签样式
const statusTagType = (status) => {
  if (status === "正常") return "success";
  if (status === "迟到") return "warning";
  if (status === "早退") return "warning";
  if (status === "旷工") return "danger";
  return "info";
};

onMounted(() => {
  loadLivingGuests();
  loadTodayAttendance();
  loadStats();
  loadPending();
  nextTick(() => initChart());
});

watch(
  () => router.currentRoute.value,
  () => {
    loadLivingGuests();
    loadTodayAttendance();
    loadStats();
    loadPending();
  },
);
</script>

<style scoped>
.finance-dashboard {
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
  position: relative;
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
.help-icon {
  position: absolute;
  top: 12px;
  right: 12px;
  color: #909399;
  cursor: pointer;
}
.living-guests-card,
.attendance-card,
.pending-card,
.stats-chart-card {
  border-radius: 12px;
  margin-bottom: 20px;
}
.pending-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #eef2f6;
  cursor: pointer;
}
.expand-icon {
  margin-right: 12px;
  font-size: 14px;
}
.warning-detail {
  margin-top: 12px;
  padding: 0 0 12px 0;
}
.pending-item:last-child {
  border-bottom: none;
}
.pending-item:hover {
  background-color: #f5f7fa;
}
.badge-right {
  margin-right: 12px;
}
.remark-cell {
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: #409eff;
}
.remark-cell:hover {
  opacity: 0.8;
}
</style>
