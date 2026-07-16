<template>
  <div class="guest-bill-container">
    <!-- 上半部分：在住客人列表 + 账单详情 (代码保持不变) -->
    <el-row :gutter="20" class="top-section">
      <!-- 左侧：在住客人列表 -->
      <el-col :xs="24" :sm="24" :md="8" :lg="8">
        <el-card class="guest-list-card" style="height: 100%">
          <template #header>
            <span>🏠 在住客人</span>
          </template>
          <el-input
            v-model="searchKeyword"
            placeholder="搜索姓名/房号"
            clearable
            size="small"
            style="margin-bottom: 12px"
          />
          <div class="guest-list">
            <div
              v-for="guest in filteredGuests"
              :key="guest.guestId"
              class="guest-item"
              :class="{ active: selectedGuestId === guest.guestId }"
              @click="selectGuest(guest.guestId)"
            >
              <div class="guest-info">
                <span>{{ guest.roomNumber }} - {{ guest.name }}</span>
                <span class="guest-phone">{{ guest.phone }}</span>
              </div>
              <div class="guest-balance">押金: ¥{{ guest.depositBalance }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：账单详情（不含历史订单） -->
      <el-col :xs="24" :sm="24" :md="16" :lg="16">
        <el-card v-if="billData" class="bill-detail-card" style="height: 100%">
          <template #header>
            <div class="bill-header">
              <span>💰 账单详情 - {{ billData.guestName }}</span>
              <div>
                <el-button
                  type="success"
                  size="small"
                  @click="openDepositDialog('追加')"
                  >追加押金</el-button
                >
                <el-button
                  type="warning"
                  size="small"
                  @click="openDepositDialog('退还')"
                  >退还押金</el-button
                >
                <el-button
                  type="primary"
                  size="small"
                  @click="openAddMiscDialog"
                  >添加杂项</el-button
                >
              </div>
            </div>
          </template>

          <!-- 基本信息 -->
          <el-descriptions :column="3" border>
            <el-descriptions-item label="客人姓名">{{
              billData.guestName
            }}</el-descriptions-item>
            <el-descriptions-item label="房间号">{{
              billData.roomNumber
            }}</el-descriptions-item>
            <el-descriptions-item label="单日房费"
              >¥{{ billData.roomPrice }}</el-descriptions-item
            >
            <el-descriptions-item label="入住时间">{{
              formatDate(billData.checkInDate)
            }}</el-descriptions-item>
            <el-descriptions-item label="预离时间">{{
              formatDate(billData.preLeaveDate)
            }}</el-descriptions-item>
            <el-descriptions-item label="押金余额">
              <span
                :class="
                  billData.depositBalance >= 0 ? 'text-success' : 'text-danger'
                "
              >
                ¥{{ billData.depositBalance }}
              </span>
            </el-descriptions-item>
          </el-descriptions>

          <!-- 未结消费 -->
          <el-divider>未结消费</el-divider>
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="info-card">
                餐饮挂账：¥{{ billData.unsettledFoodFee }}
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-card">
                杂项：¥{{ billData.unsettledDamageFee }}
              </div>
            </el-col>
            <el-col :span="8">
              <div
                class="info-card"
                style="background: #e6f7ff; border: 1px solid #91d5ff"
              >
                总额（预估）：¥{{ estimatedTotal.toFixed(2) }}
              </div>
            </el-col>
          </el-row>

          <!-- 餐饮挂账（按订单分组） -->
          <div style="margin-top: 12px">
            <h4 style="margin-bottom: 8px">🍽️ 餐饮挂账明细</h4>
            <el-collapse v-model="activeFoodOrders">
              <el-collapse-item
                v-for="(group, index) in groupedFoodDetails"
                :key="index"
                :name="index"
              >
                <!-- 标题保持不变 -->
                <template #title>
                  <div
                    style="
                      display: flex;
                      justify-content: space-between;
                      width: 100%;
                      padding-right: 20px;
                    "
                  >
                    <span>
                      <span style="font-weight: bold"
                        >订单 #{{ group.orderId || "临时" }}</span
                      >
                      <span
                        style="
                          margin-left: 15px;
                          color: #909399;
                          font-size: 13px;
                        "
                      >
                        {{
                          group.createTime
                            ? formatDateTime(group.createTime)
                            : ""
                        }}
                      </span>
                      <span
                        style="
                          margin-left: 15px;
                          color: #909399;
                          font-size: 13px;
                        "
                      >
                        操作员：{{ group.operator || "" }}
                      </span>
                    </span>
                    <span style="color: #f56c6c; font-weight: bold"
                      >合计：¥{{ group.totalAmount.toFixed(2) }}</span
                    >
                  </div>
                </template>
                <el-table :data="group.items" border size="small">
                  <el-table-column prop="itemName" label="项目" />
                  <el-table-column prop="quantity" label="数量" width="80" />
                  <el-table-column prop="price" label="单价" width="100">
                    <template #default="{ row }">¥{{ row.price }}</template>
                  </el-table-column>
                  <el-table-column prop="amount" label="金额" width="100">
                    <template #default="{ row }">¥{{ row.amount }}</template>
                  </el-table-column>
                </el-table>
              </el-collapse-item>
            </el-collapse>
            <div
              v-if="groupedFoodDetails.length === 0"
              style="text-align: center; color: #909399; padding: 20px 0"
            ></div>
          </div>

          <!-- 杂项明细 -->
          <div style="margin-top: 16px">
            <h4 style="margin-bottom: 8px">📌 杂项明细</h4>
            <el-table :data="miscDetails" border size="small">
              <el-table-column prop="itemName" label="项目" />
              <el-table-column prop="quantity" label="数量" width="80" />
              <el-table-column prop="price" label="单价" width="100">
                <template #default="{ row }">¥{{ row.price }}</template>
              </el-table-column>
              <el-table-column prop="amount" label="金额" width="100">
                <template #default="{ row }">¥{{ row.amount }}</template>
              </el-table-column>
            </el-table>
            <div
              v-if="miscDetails.length === 0"
              style="text-align: center; color: #909399; padding: 20px 0"
            ></div>
          </div>

          <!-- KTV 已结消费 -->
          <el-divider>KTV 已结消费</el-divider>
          <el-table :data="billData.ktvList" border size="small">
            <el-table-column prop="itemName" label="包房" />
            <el-table-column prop="amount" label="金额" width="100"
              ><template #default="{ row }"
                >¥{{ row.amount }}</template
              ></el-table-column
            >
            <el-table-column prop="endTime" label="结账时间" width="160" />
          </el-table>

          <!-- 折扣与结账 -->
          <div class="settle-actions">
            <div class="discount-box">
              <span>折扣：</span>
              <el-input-number
                v-model="discountRate"
                :min="0.5"
                :max="1"
                :step="0.05"
                :precision="2"
                style="width: 120px"
              />
              <span class="discount-hint">（1表示无折扣）</span>
            </div>
            <div class="pay-method-box">
              <span>支付方式：</span>
              <el-select v-model="payMethod" style="width: 130px">
                <el-option label="现金" value="现金" />
                <el-option label="微信" value="微信" />
                <el-option label="支付宝" value="支付宝" />
                <el-option label="银行卡" value="银行卡" />
                <el-option label="其他" value="其他" />
              </el-select>
            </div>
            <el-button type="danger" size="large" @click="confirmSettle"
              >确认结账</el-button
            >
          </div>
        </el-card>
        <el-empty v-else description="请从左侧选择客人" style="height: 100%" />
      </el-col>
    </el-row>

    <!-- 下半部分：全部已结账订单列表 -->
    <el-row style="margin-top: 20px">
      <el-col :span="24">
        <el-card class="settled-orders-card">
          <template #header>📜 全部已结账订单</template>
          <el-table
            :data="settledOrders"
            border
            stripe
            v-loading="loadingOrders"
          >
            <el-table-column prop="settleTime" label="结账时间" width="160" />
            <el-table-column prop="guestName" label="客人姓名" width="100" />
            <el-table-column label="房间号" width="120">
              <template #default="{ row }">
                {{ row.roomNumber }}{{ row.roomType ? "-" + row.roomType : "" }}
              </template>
            </el-table-column>
            <el-table-column prop="orderId" label="订单号" width="100" />
            <el-table-column prop="operator" label="操作员" width="100" />
            <el-table-column prop="totalAmount" label="消费总额" width="110">
              <template #default="{ row }">¥{{ row.totalAmount }}</template>
            </el-table-column>
            <el-table-column prop="depositTotal" label="押金总额" width="110">
              <template #default="{ row }">¥{{ row.depositTotal }}</template>
            </el-table-column>
            <el-table-column label="实收/退" width="120">
              <template #default="{ row }">
                <span
                  :class="
                    row.totalAmount - row.depositTotal >= 0
                      ? 'text-danger'
                      : 'text-success'
                  "
                >
                  {{
                    row.totalAmount - row.depositTotal >= 0
                      ? "补款: "
                      : "退款: "
                  }}
                  ¥{{ Math.abs(row.totalAmount - row.depositTotal).toFixed(2) }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="明细" width="320">
              <template #default="{ row }">
                <div class="detail-summary">
                  <span v-if="row.roomFee">房费: ¥{{ row.roomFee }}</span>
                  <span v-if="row.foodFee">餐饮: ¥{{ row.foodFee }}</span>
                  <span v-if="row.ktvFee">KTV: ¥{{ row.ktvFee }}</span>
                  <span v-if="row.otherFee">杂项: ¥{{ row.otherFee }}</span>
                  <el-button
                    type="text"
                    style="margin-left: auto"
                    @click="showOrderDetails(row.orderId)"
                    >详情</el-button
                  >
                </div>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button type="text" @click="printBill(row.orderId)"
                  >打印</el-button
                >
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            v-model:current-page="orderPage"
            v-model:page-size="orderPageSize"
            :total="orderTotal"
            layout="total, sizes, prev, pager, next"
            @current-change="loadSettledOrders"
            @size-change="loadSettledOrders"
            style="margin-top: 16px; justify-content: flex-end"
          />
        </el-card>
      </el-col>
    </el-row>

    <!-- 押金操作弹窗（不变） -->
    <el-dialog
      v-model="depositDialogVisible"
      :title="depositType === '追加' ? '追加押金' : '退还押金'"
      width="400px"
    >
      <el-form :model="depositForm" label-width="80px">
        <el-form-item label="金额">
          <el-input-number
            v-model="depositForm.amount"
            :precision="2"
            :step="100"
            :min="0"
          />
        </el-form-item>
        <el-form-item label="支付方式" v-if="depositType === '追加'">
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
        <el-button type="primary" @click="submitDeposit">确认</el-button>
      </template>
    </el-dialog>
    <el-dialog
      v-model="orderDetailsDialogVisible"
      title="订单明细"
      width="600px"
    >
      <el-table :data="orderDetails" border size="small">
        <el-table-column prop="itemName" label="项目" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="price" label="单价" width="100">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="100">
          <template #default="{ row }">¥{{ row.amount }}</template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 打印组件 -->
    <PrintBill ref="printRef" />
    <!-- 添加杂项弹窗 -->
    <el-dialog v-model="miscDialogVisible" title="添加杂项费用" width="400px">
      <el-form :model="miscForm" label-width="80px">
        <el-form-item label="项目名称">
          <el-input
            v-model="miscForm.itemName"
            placeholder="如：洗衣费、加床费等"
          />
        </el-form-item>
        <el-form-item label="金额">
          <el-input-number
            v-model="miscForm.amount"
            :precision="2"
            :step="10"
            :min="0"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="miscForm.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="miscDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitMisc">确认添加</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import request from "@/utils/request";
import { PAY_METHODS } from "@/constants/payment";
import PrintBill from "@/components/PrintBill.vue";
// 折叠面板展开的订单索引（默认全部折叠）
const activeFoodOrders = ref([]);
const route = useRoute();
const router = useRouter();
const searchKeyword = ref("");
const allGuests = ref([]);
const selectedGuestId = ref(null);
const billData = ref(null);
const discountRate = ref(1.0);
const depositDialogVisible = ref(false);
const depositType = ref("追加");
const depositForm = ref({ amount: 0, payMethod: PAY_METHODS.WECHAT, remark: "" });
const printRef = ref(null);
const orderDetails = ref([]);
const orderDetailsDialogVisible = ref(false);
const payMethod = ref(PAY_METHODS.CASH);
const estimatedTotal = computed(() => {
  if (!billData.value) return 0;
  const checkIn = new Date(billData.value.checkInDate);
  const now = new Date(); // 当前时间作为结算时间
  // 计算天数差，不足一天按一天计（向上取整）
  const diffTime = Math.abs(now - checkIn);
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) || 1;
  const roomFee = diffDays * (billData.value.roomPrice || 0);
  const foodFee = billData.value.unsettledFoodFee || 0;
  const miscFee = billData.value.unsettledDamageFee || 0;
  const rate = discountRate.value || 1;
  return (roomFee + foodFee) * rate + miscFee;
});
// 按订单分组餐饮明细
const groupedFoodDetails = computed(() => {
  const foodItems =
    billData.value?.unsettledDetails?.filter((d) => d.itemType === "餐饮") ||
    [];
  const groupMap = new Map();
  foodItems.forEach((item) => {
    const key = item.orderId || "temp_" + item.createTime;
    if (!groupMap.has(key)) {
      groupMap.set(key, {
        orderId: item.orderId,
        items: [],
        totalAmount: 0,
        createTime: item.createTime,
        operator: item.operator,
      });
    }
    const group = groupMap.get(key);
    group.items.push(item);
    group.totalAmount += item.amount;
  });
  return Array.from(groupMap.values());
});

// 杂项明细
const miscDetails = computed(() => {
  return (
    billData.value?.unsettledDetails?.filter((d) => d.itemType === "杂项") || []
  );
});
// 显示订单明细
const showOrderDetails = async (orderId) => {
  const res = await request.get(`/finance/order/details/${orderId}`);
  orderDetails.value = res.data || [];
  orderDetailsDialogVisible.value = true;
};
// 全部已结账订单相关
const settledOrders = ref([]);
const orderPage = ref(1);
const orderPageSize = ref(10);
const orderTotal = ref(0);
const loadingOrders = ref(false);
// 过滤客人列表
const filteredGuests = computed(() => {
  if (!searchKeyword.value) return allGuests.value;
  const kw = searchKeyword.value.toLowerCase();
  return allGuests.value.filter(
    (g) =>
      g.name.toLowerCase().includes(kw) ||
      g.roomNumber.toLowerCase().includes(kw),
  );
});

// 加载在住客人列表
const loadGuestList = async () => {
  const res = await request.get("/finance/livingGuests");
  allGuests.value = res.data || [];
  const guestId = route.query.guestId;
  if (guestId) {
    selectGuest(parseInt(guestId));
  } else if (allGuests.value.length) {
    selectGuest(allGuests.value[0].guestId);
  }
};

// 选择客人并加载账单
const selectGuest = async (guestId) => {
  selectedGuestId.value = guestId;
  discountRate.value = 1.0;
  const res = await request.get(`/finance/bill/${guestId}`);
  billData.value = res.data;
};

// 加载全部已结账订单
const loadSettledOrders = async () => {
  loadingOrders.value = true;
  try {
    const res = await request.get("/finance/settle/list", {
      params: { page: orderPage.value, size: orderPageSize.value },
    });
    settledOrders.value = res.data.records || [];
    orderTotal.value = res.data.total || 0;
  } catch (error) {
    ElMessage.error("加载已结账订单失败");
  } finally {
    loadingOrders.value = false;
  }
};

// 打开押金操作弹窗
const openDepositDialog = (type) => {
  if (!selectedGuestId.value) return ElMessage.warning("请先选择客人");
  depositType.value = type;
  depositForm.value.amount = 0;
  depositForm.value.payMethod = "微信";
  depositForm.value.remark = "";
  depositDialogVisible.value = true;
};

// 提交押金操作
const submitDeposit = async () => {
  if (depositForm.value.amount <= 0) return ElMessage.warning("请输入有效金额");
  await request.post("/finance/deposit", {
    guestId: selectedGuestId.value,
    amount: depositForm.value.amount,
    type: depositType.value,
    payMethod: depositForm.value.payMethod,
    remark: depositForm.value.remark,
  });
  ElMessage.success(`${depositType.value === "追加" ? "加收" : "退还"}成功`);
  depositDialogVisible.value = false;
  await selectGuest(selectedGuestId.value);
  await loadGuestList();
};

// 确认结账
const confirmSettle = async () => {
  if (!selectedGuestId.value) return;
  await ElMessageBox.confirm("确认结账？结账后客人将离店", "提示", {
    type: "warning",
  });
  const res = await request.post("/finance/settle", null, {
    params: {
      guestId: selectedGuestId.value,
      discountRate: discountRate.value,
      payMethod: payMethod.value, // 新增：传递支付方式
    },
  });
  ElMessage.success("结账成功");
  // 刷新列表
  await loadGuestList();
  await loadSettledOrders();
  if (allGuests.value.length) {
    selectGuest(allGuests.value[0].guestId);
  } else {
    billData.value = null;
    selectedGuestId.value = null;
  }
  if (res.data.orderId) {
    ElMessageBox.confirm("是否打印账单？", "提示", {
      confirmButtonText: "打印",
      cancelButtonText: "取消",
      type: "info",
    })
      .then(() => {
        printBill(res.data.orderId);
      })
      .catch(() => {});
  }
};
// 添加杂项相关
const miscDialogVisible = ref(false);
const miscForm = reactive({
  itemName: "",
  amount: 0,
  remark: "",
});

// 打开添加杂项弹窗
const openAddMiscDialog = () => {
  miscForm.itemName = "";
  miscForm.amount = 0;
  miscForm.remark = "";
  miscDialogVisible.value = true;
};

// 提交杂项
const submitMisc = async () => {
  if (!miscForm.itemName) return ElMessage.warning("请输入项目名称");
  if (miscForm.amount <= 0) return ElMessage.warning("请输入有效金额");
  try {
    await request.post("/finance/charge", {
      guestId: selectedGuestId.value,
      itemName: miscForm.itemName,
      amount: miscForm.amount,
      remark: miscForm.remark,
    });
    ElMessage.success("杂项添加成功");
    miscDialogVisible.value = false;
    // 刷新当前账单
    await selectGuest(selectedGuestId.value);
  } catch (err) {
    ElMessage.error(err.response?.data?.msg || "添加失败");
  }
};
const formatDate = (date) => {
  if (!date) return "";
  const d = new Date(date);
  return d.toLocaleDateString();
};
// 打印账单
const printBill = (orderId) => {
  printRef.value?.print(orderId);
};
const formatDateTime = (dateStr) => {
  if (!dateStr) return "";
  const d = new Date(dateStr);
  return d.toLocaleString("zh-CN", { hour12: false });
};

onMounted(() => {
  loadGuestList();
  loadSettledOrders();
});
</script>

<style scoped>
.guest-bill-container {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}
.top-section {
  display: flex;
  align-items: stretch;
}
.guest-list-card,
.bill-detail-card {
  border-radius: 12px;
}
.guest-list-card {
  height: 100%;
  overflow-y: auto;
}
.guest-list {
  max-height: calc(100vh - 180px);
  overflow-y: auto;
}
.guest-item {
  padding: 12px;
  border-bottom: 1px solid #eef2f6;
  cursor: pointer;
  transition: background 0.2s;
}
.guest-item:hover {
  background-color: #f5f7fa;
}
.guest-item.active {
  background-color: #e6f7ff;
  border-left: 3px solid #409eff;
}
.guest-info {
  display: flex;
  justify-content: space-between;
}
.guest-phone {
  color: #909399;
  font-size: 12px;
}
.guest-balance {
  font-size: 12px;
  color: #f56c6c;
  margin-top: 4px;
}
.bill-detail-card {
  height: 100%;
}
.info-card {
  background: #f9f9f9;
  border-radius: 8px;
  padding: 16px;
  text-align: center;
  font-size: 16px;
  font-weight: bold;
}
.settle-actions {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 20px;
  margin: 20px 0;
}
.discount-box {
  display: flex;
  align-items: center;
  gap: 12px;
}
.discount-hint {
  font-size: 12px;
  color: #909399;
}
.text-success {
  color: #67c23a;
}
.text-danger {
  color: #f56c6c;
}
.bill-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.settled-orders-card {
  border-radius: 12px;
}
.text-success {
  color: #67c23a;
}
.text-danger {
  color: #f56c6c;
}
.detail-summary {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
  justify-content: flex-end;
}
.detail-summary span {
  white-space: nowrap;
}
.el-col[style*="display: flex"] {
  display: flex;
  align-items: center;
  justify-content: center;
}
.empty-text {
  text-align: center;
  color: #909399;
  padding: 20px 0;
  font-size: 14px;
}
.pay-method-box {
  display: flex;
  align-items: center;
  gap: 12px;
}
.pay-method-box .el-select {
  width: 130px;
}
</style>
