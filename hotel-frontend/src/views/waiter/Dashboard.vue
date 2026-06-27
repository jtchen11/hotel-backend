<template>
  <div class="waiter-dashboard">
    <!-- 顶部标题区域 -->
    <div class="dashboard-header">
      <div>
        <h2>营业服务员工作台</h2>
        <p class="subtitle">实时营业数据 · 今日工作概览</p>
      </div>
      <div class="time-info">
        <span class="date-text">{{ currentDate }}</span>
        <span class="status-tag running">营业中</span>
      </div>
    </div>

    <!-- 四项统计卡片 -->
    <div class="data-grid">
      <div class="data-card">
        <div class="card-left">
          <p class="label">今日已开单</p>
          <p class="number">{{ todayOrderCount }}</p>
        </div>
        <div class="card-icon green">📄</div>
      </div>
      <div class="data-card">
        <div class="card-left">
          <p class="label">今日营业额</p>
          <p class="number">¥{{ todayRevenue }}</p>
        </div>
        <div class="card-icon orange">💰</div>
      </div>
      <div class="data-card">
        <div class="card-left">
          <p class="label">KTV使用中</p>
          <p class="number">{{ ktvUsing }} / {{ totalKtv }}</p>
        </div>
        <div class="card-icon blue">🎤</div>
      </div>
      <div class="data-card">
        <div class="card-left">
          <p class="label">待结账订单</p>
          <p class="number">{{ unsettledCount }}</p>
        </div>
        <div class="card-icon red">💳</div>
      </div>
    </div>

    <!-- 快捷操作区：只剩2个按钮 -->
    <div class="quick-section">
      <h3>⚡ 工作台快捷操作</h3>
      <div class="quick-grid">
        <div class="quick-item" @click="getDashboardData">
          <div class="q-icon">🔄</div>
          <span>刷新数据</span>
        </div>
        <div class="quick-item" @click="handleCheckStock">
          <div class="q-icon">📦</div>
          <span>库存提醒</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import request from "@/utils/request";

const currentDate = ref("");
const todayOrderCount = ref(0);
const todayRevenue = ref(0);
const ktvUsing = ref(0);
const totalKtv = ref(0);
const unsettledCount = ref(0);

const getNowDate = () => {
  const date = new Date();
  currentDate.value = `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, "0")}-${date.getDate().toString().padStart(2, "0")} ${date.getHours().toString().padStart(2, "0")}:${date.getMinutes().toString().padStart(2, "0")}`;
};

const getDashboardData = async () => {
  try {
    const res = await request.get("/waiter/dashboard");
    if (res.code === 200) {
      const data = res.data;
      todayOrderCount.value = data.todayOrderCount;
      todayRevenue.value = data.todayRevenue;
      ktvUsing.value = data.ktvUsing;
      totalKtv.value = data.totalKtv;
      unsettledCount.value = data.unsettledCount;
      getNowDate();
      ElMessage.success("数据刷新成功");
    }
  } catch (err) {
    ElMessage.error("后端未启动或接口异常");
    console.error(err);
  }
};

import { useRouter } from "vue-router";

const router = useRouter();

const handleCheckStock = async () => {
  try {
    const res = await request.get("/warehouse/warning");
    const warningList = res.data || [];
    if (warningList.length === 0) {
      ElMessage.success("所有库存正常，没有低于预警线的物品");
    } else {
      // 取前3个物品名称作为摘要
      const names = warningList
        .slice(0, 3)
        .map(
          (item) =>
            `${item.itemName}（${item.currentQuantity}${item.unit || "件"}）`,
        )
        .join("、");
      const more = warningList.length > 3 ? `等${warningList.length}项` : "";
      ElMessage({
        message: `⚠️ ${names}${more} 库存低于预警线，点击查看详情`,
        type: "warning",
        duration: 5000,
        onClick: () => {
          router.push("/warehouse/stock"); // 跳转到库存管理页面
        },
      });
    }
  } catch (err) {
    console.error("获取库存预警数据失败:", err);
    ElMessage.error("获取库存预警数据失败，请稍后重试");
  }
};
onMounted(() => {
  getDashboardData();
});
</script>

<style scoped>
.waiter-dashboard {
  padding: 24px 32px;
  background: #f5f7fa;
  min-height: 100vh;
}
.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.dashboard-header h2 {
  margin: 0;
  font-size: 22px;
  color: #222;
}
.subtitle {
  margin: 4px 0 0;
  color: #909399;
  font-size: 14px;
}
.time-info {
  display: flex;
  align-items: center;
  gap: 12px;
}
.date-text {
  color: #666;
  font-size: 14px;
}
.status-tag {
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}
.status-tag.running {
  background: #e6f7ef;
  color: #00b42a;
}
.data-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 32px;
}
.data-card {
  background: #fff;
  padding: 28px 24px;
  border-radius: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}
.card-left .label {
  margin: 0 0 6px;
  color: #909399;
  font-size: 14px;
}
.card-left .number {
  margin: 0;
  font-size: 28px;
  font-weight: bold;
  color: #222;
}
.card-icon {
  width: 52px;
  height: 52px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
}
.card-icon.green {
  background: #e6f7ef;
}
.card-icon.orange {
  background: #fff7e6;
}
.card-icon.blue {
  background: #e6f7ff;
}
.card-icon.red {
  background: #fff1f0;
}
.quick-section h3 {
  font-size: 16px;
  color: #333;
  margin-bottom: 16px;
}
/* 改成两列布局 */
.quick-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}
.quick-item {
  background: #fff;
  padding: 20px;
  border-radius: 12px;
  text-align: center;
  cursor: pointer;
  transition: 0.2s;
}
.quick-item:hover {
  transform: translateY(-2px);
  background: #f0f7ff;
}
.q-icon {
  font-size: 24px;
  margin-bottom: 6px;
}
.quick-item span {
  font-size: 13px;
  color: #555;
}
</style>
