<template>
  <div class="unsettled-container">
    <!-- 顶部客人选择 -->
    <div class="tool-bar">
      <el-select
        v-model="selectedGuestId"
        filterable
        remote
        reserve-keyword
        clearable
        placeholder="选择客人（清空显示一月总览）"
        :remote-method="searchGuest"
        :loading="guestLoading"
        style="width: 300px"
        @change="loadData"
      >
        <el-option
          v-for="g in guestOptions"
          :key="g.guestId"
          :label="`${g.name} (${g.phone}) - ${g.roomNumber || '无房间'}`"
          :value="g.guestId"
        />
      </el-select>
    </div>

    <!-- 左右两栏 -->
    <div class="split-layout">
      <!-- 左侧：点餐订单 -->
      <div class="left-panel">
        <div class="panel-title">点餐订单（挂账）</div>
        <el-collapse v-model="activeNames" v-if="mealOrders.length">
          <el-collapse-item
            v-for="(order, idx) in mealOrders"
            :key="idx"
            :name="idx"
          >
            <template #title>
              <div class="order-title">
                <span>{{ order.guestName }}</span>
                <span class="order-time">{{
                  formatTime(order.orderTime)
                }}</span>
                <span class="order-total"
                  >总价：¥{{ order.totalAmount.toFixed(2) }}</span
                >
              </div>
            </template>
            <el-table :data="order.items" size="small" border>
              <el-table-column prop="itemName" label="菜品" />
              <el-table-column prop="quantity" label="数量" width="80" />
              <el-table-column prop="price" label="单价" width="100">
                <template #default="{ row }"
                  >¥{{ row.price.toFixed(2) }}</template
                >
              </el-table-column>
              <el-table-column prop="amount" label="小计" width="100">
                <template #default="{ row }"
                  >¥{{ row.amount.toFixed(2) }}</template
                >
              </el-table-column>
            </el-table>
            <div class="order-footer">操作员：{{ order.operator }}</div>
          </el-collapse-item>
        </el-collapse>
        <el-empty v-else description="暂无未结点餐订单" />
      </div>

      <!-- 右侧：KTV记录 -->
      <div class="right-panel">
        <div class="panel-title">KTV消费记录</div>
        <el-table :data="ktvRecords" border v-if="ktvRecords.length">
          <el-table-column prop="roomName" label="包房" />
          <el-table-column prop="guestName" label="订房人" />
          <el-table-column prop="duration" label="时长(小时)" />
          <el-table-column prop="totalFee" label="消费金额" width="100">
            <template #default="{ row }"
              >¥{{ row.totalFee.toFixed(2) }}</template
            >
          </el-table-column>
          <el-table-column prop="endTime" label="结账时间" width="160">
            <template #default="{ row }">{{
              formatTime(row.endTime)
            }}</template>
          </el-table-column>
          <el-table-column prop="operator" label="操作员" />
        </el-table>
        <el-empty v-else description="暂无KTV消费记录" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { getUnsettled } from "@/api/operation";
import request from "@/utils/request";

const selectedGuestId = ref(null);
const guestOptions = ref([]);
const guestLoading = ref(false);
const mealOrders = ref([]);
const activeNames = ref([]);
const ktvRecords = ref([]);

const searchGuest = async (query) => {
  if (!query) {
    guestOptions.value = [];
    return;
  }
  guestLoading.value = true;
  try {
    const res = await request.get("/guest/list", {
      params: { status: "在住", keyword: query },
    });
    guestOptions.value = res.data || [];
  } catch (error) {
    console.error(error);
  } finally {
    guestLoading.value = false;
  }
};

const formatTime = (dateStr) => {
  if (!dateStr) return "";
  const date = new Date(dateStr);
  return `${date.getMonth() + 1}/${date.getDate()} ${date.getHours().toString().padStart(2, "0")}:${date.getMinutes().toString().padStart(2, "0")}`;
};

const groupByTimeWindow = (details, minutes = 10) => {
  if (!details.length) return [];
  const sorted = [...details].sort(
    (a, b) => new Date(a.createTime) - new Date(b.createTime),
  );
  const groups = [];
  let currentGroup = [];
  let lastTime = null;
  for (const item of sorted) {
    const curTime = new Date(item.createTime);
    if (lastTime && curTime - lastTime > minutes * 60 * 1000) {
      groups.push([...currentGroup]);
      currentGroup = [];
    }
    currentGroup.push(item);
    lastTime = curTime;
  }
  if (currentGroup.length) groups.push(currentGroup);
  return groups
    .map((group) => {
      const totalAmount = group.reduce((sum, i) => sum + (i.amount || 0), 0);
      const orderTime = group[group.length - 1].createTime;
      const operator = group[group.length - 1].operator;
      return {
        guestName: group[0].guestName || `客人ID:${group[0].guestId}`,
        orderTime,
        totalAmount,
        operator,
        items: group.map((i) => ({
          itemName: i.itemName,
          quantity: i.quantity,
          price: i.price,
          amount: i.amount,
        })),
      };
    })
    .reverse();
};

const loadData = async () => {
  ktvRecords.value = [];
  if (selectedGuestId.value) {
    // 个人账单
    const res = await request.get("/dining/unsettled", {
      params: { guestId: selectedGuestId.value },
    });
    const details = res.data || [];
    const diningDetails = details.filter((d) => d.itemType === "餐饮");
    mealOrders.value = groupByTimeWindow(diningDetails);

    // KTV个人记录（已修复）
    const ktvRes = await request.get("/ktv/records", {
      params: { guestId: selectedGuestId.value },
    });
    ktvRecords.value = ktvRes.data || [];
  } else {
    // 一月总览：点餐订单
    const res = await request.get("/dining/allUnsettled", {
      params: { days: 30 },
    });
    const details = res.data || [];
    const groupedByGuest = {};
    for (const d of details) {
      if (!groupedByGuest[d.guestId]) groupedByGuest[d.guestId] = [];
      groupedByGuest[d.guestId].push(d);
    }
    const allOrders = [];
    for (const guestId in groupedByGuest) {
      const guestOrders = groupByTimeWindow(groupedByGuest[guestId]);
      allOrders.push(...guestOrders);
    }
    allOrders.sort((a, b) => new Date(b.orderTime) - new Date(a.orderTime));
    mealOrders.value = allOrders;

    // 一月总览：KTV记录（已修复）
    const ktvResAll = await request.get("/ktv/allRecords", {
      params: { days: 30 },
    });
    ktvRecords.value = ktvResAll.data || [];
  }
  if (mealOrders.value.length) activeNames.value = [0];
};

onMounted(() => {
  loadData();
});
</script>

<style scoped>
.unsettled-container {
  padding: 20px;
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}

.tool-bar {
  margin-bottom: 20px;
  flex-shrink: 0;
}

.split-layout {
  flex: 1;
  display: flex;
  gap: 20px;
  min-height: 0;
}

.left-panel,
.right-panel {
  flex: 1;
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  overflow: auto;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.panel-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 2px solid #409eff;
  flex-shrink: 0;
}

.order-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding-right: 20px;
}

.order-time {
  color: #909399;
  font-size: 13px;
}

.order-total {
  color: #f56c6c;
  font-weight: bold;
}

.order-footer {
  margin-top: 10px;
  text-align: right;
  font-size: 12px;
  color: #909399;
}

:deep(.el-collapse-item__header) {
  background-color: #fafafa;
  border-radius: 4px;
  padding: 0 12px;
}
</style>
