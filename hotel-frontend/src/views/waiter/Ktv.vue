﻿
<template>
  <div class="ktv-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span class="head-title">KTV管理</span>
          <el-button type="primary" icon="Refresh" @click="fetchRooms"
            >刷新数据</el-button
          >
        </div>
      </template>

      <el-row :gutter="24">
        <el-col
          :span="8"
          v-for="room in roomList"
          :key="room.ktvId"
          class="mb-5"
        >
          <el-card shadow="hover" class="room-card">
            <div class="room-top">
              <div class="room-name">{{ room.roomName }}</div>
              <el-tag
                :type="getStatusTagType(room.status)"
                size="small"
                effect="light"
              >
                {{ room.status }}
              </el-tag>
            </div>

            <div class="room-middle">
              <div class="room-type-text">房型：{{ room.roomType }}</div>
              <div class="room-price-text">
                单价 <span class="price-num">¥{{ room.hourlyRate }}</span
                >/小时
              </div>
              <div class="room-desc" v-if="room.description">
                <el-icon><InfoFilled /></el-icon>
                <span>{{ room.description }}</span>
              </div>
            </div>

            <div class="room-bottom">
              <div class="btn-group">
                <el-button
                  type="success"
                  size="small"
                  :disabled="room.status !== '空闲'"
                  @click="openStartDialog(room)"
                >
                  开房
                </el-button>
                <el-button
                  type="danger"
                  size="small"
                  :disabled="room.status !== '使用中'"
                  @click="openEndDialog(room)"
                >
                  结账
                </el-button>
              </div>
              <el-select
                v-model="room.status"
                size="small"
                style="width: 100px; margin-top: 8px"
                :disabled="room.status === '使用中'"
                @change="changeStatus(room)"
              >
                <el-option label="空闲" value="空闲" />
                <el-option label="打扫中" value="打扫中" />
                <el-option label="维修中" value="维修中" />
              </el-select>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <!-- 开房弹窗 -->
    <el-dialog
      title="新开KTV包房"
      v-model="startDialogVisible"
      width="35%"
      draggable
    >
      <el-form :model="startForm" label-width="90px">
        <el-form-item label="目标包房">
          <el-input v-model="startForm.roomName" disabled />
        </el-form-item>
        <el-form-item label="在住客人">
          <el-select
            v-model="startForm.guestId"
            filterable
            remote
            reserve-keyword
            placeholder="输入姓名/手机号快速查询在住客人"
            :remote-method="searchGuest"
            :loading="guestLoading"
            style="width: 100%"
          >
            <el-option
              v-for="g in guestOptions"
              :key="g.guestId"
              :label="`${g.name} | ${g.phone}`"
              :value="g.guestId"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="startDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmStart">确认开房</el-button>
      </template>
    </el-dialog>

    <!-- KTV结账弹窗 -->
    <el-dialog
      title="KTV账单确认结账"
      v-model="endDialogVisible"
      width="420px"
      draggable
    >
      <el-form :model="endForm" label-width="100px">
        <el-form-item label="包房名称">
          <el-input v-model="endForm.roomName" disabled />
        </el-form-item>
        <el-form-item label="消费客人">
          <el-input v-model="endForm.guestName" disabled />
        </el-form-item>
        <el-form-item label="使用时长(小时)">
          <!-- 使用 @blur 在失去焦点时取整 -->
          <el-input
            v-model="endForm.duration"
            placeholder="输入数字，失去焦点自动向上取整"
            @blur="onDurationBlur"
            @keyup.enter="onDurationBlur"
            clearable
          />
          <div style="font-size: 12px; color: #909399; margin-top: 5px">
            💡 自动计算（向上取整）：{{ endForm.autoDuration }} 小时
            <span
              v-if="endForm.maxHour"
              style="color: #e6a23c; margin-left: 8px"
            >
              （房间最大封顶：{{ endForm.maxHour }} 小时，手动可超过）
            </span>
          </div>
        </el-form-item>
        <el-form-item label="应付金额">
          <span style="font-size: 20px; color: #f56c6c; font-weight: bold">
            ¥{{ endForm.totalFee.toFixed(2) }}
          </span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="endDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmEndKtv"
          >确认结账扣费</el-button
        >
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { InfoFilled } from "@element-plus/icons-vue";
import request from "@/utils/request";

const roomList = ref([]);
const startDialogVisible = ref(false);
const startForm = ref({ ktvId: null, roomName: "", guestId: null });
const guestOptions = ref([]);
const guestLoading = ref(false);

// KTV结账弹窗相关
const endDialogVisible = ref(false);
const endForm = ref({
  guestId: null,
  roomName: "",
  guestName: "",
  duration: "", // 输入框绑定值（失去焦点后自动变为整数）
  totalFee: 0,
  autoDuration: 0,
  maxHour: 0,
  hourlyRate: 0,
  ktvId: null,
});

// 获取包房列表
const fetchRooms = async () => {
  const res = await request.get("/ktv/rooms");
  roomList.value = res.data;
};

// 状态标签
const getStatusTagType = (status) => {
  if (status === "空闲") return "success";
  if (status === "使用中") return "danger";
  if (status === "打扫中") return "warning";
  return "info";
};

// 开房逻辑
const openStartDialog = (room) => {
  startForm.value = {
    ktvId: room.ktvId,
    roomName: room.roomName,
    guestId: null,
  };
  startDialogVisible.value = true;
  guestOptions.value = [];
};

const searchGuest = async (keyword) => {
  if (!keyword) {
    guestOptions.value = [];
    return;
  }
  guestLoading.value = true;
  const res = await request.get("/guest/list", {
    params: { status: "在住", keyword },
  });
  guestOptions.value = res.data || [];
  guestLoading.value = false;
};

const confirmStart = async () => {
  if (!startForm.value.guestId) return ElMessage.warning("请先选择入住客人");
  await request.post("/ktv/start", startForm.value);
  ElMessage.success("开房办理成功！");
  startDialogVisible.value = false;
  fetchRooms();
};

// 结账弹窗
const openEndDialog = async (room) => {
  try {
    const guestRes = await request.get(`/ktv/getCurrentGuest/${room.ktvId}`);
    const { guestId, name } = guestRes.data;

    const bill = await request.get("/ktv/prepare", { params: { guestId } });
    const { duration, totalFee, roomName, hourlyRate, maxHour } = bill.data;

    endForm.value = {
      guestId: guestId,
      roomName: roomName,
      guestName: name,
      duration: String(duration), // 默认显示自动计算值
      totalFee: totalFee,
      autoDuration: duration,
      maxHour: maxHour || 0,
      hourlyRate: hourlyRate,
      ktvId: room.ktvId,
    };
    endDialogVisible.value = true;
  } catch (e) {
    ElMessage.error(e.response?.data?.msg || "获取账单信息失败");
  }
};

// 失去焦点时向上取整（用户输入完成后触发）
const onDurationBlur = () => {
  const val = endForm.value.duration;
  // 如果为空，清空并重置金额
  if (!val || val.trim() === "") {
    endForm.value.duration = "";
    endForm.value.totalFee = 0;
    return;
  }

  const num = parseFloat(val);
  // 非数字或 <=0，清空
  if (isNaN(num) || num <= 0) {
    endForm.value.duration = "";
    endForm.value.totalFee = 0;
    ElMessage.warning("请输入大于0的数字");
    return;
  }

  // 向上取整
  const rounded = Math.ceil(num);
  // 将取整后的值回填到输入框
  endForm.value.duration = String(rounded);

  // 重新计算金额
  if (rounded > 0 && endForm.value.hourlyRate > 0) {
    endForm.value.totalFee =
      Math.round(rounded * endForm.value.hourlyRate * 100) / 100;
  }
};

// 确认结账
const confirmEndKtv = async () => {
  // 先触发一次取整，确保输入值已被处理
  onDurationBlur();

  const finalDuration = parseInt(endForm.value.duration);
  if (!finalDuration || finalDuration <= 0) {
    return ElMessage.warning("请填写有效的使用时长（大于0的数字）");
  }

  try {
    await request.post("/ktv/end", {
      guestId: endForm.value.guestId,
      customDuration: finalDuration,
    });
    ElMessage.success("结账完成，房间转为待打扫！");
    endDialogVisible.value = false;
    fetchRooms();
  } catch (e) {
    ElMessage.error(e.response?.data?.msg || "结账失败");
  }
};

// 修改状态
const changeStatus = async (room) => {
  await request.put(`/ktv/room/${room.ktvId}/status`, null, {
    params: { status: room.status },
  });
  ElMessage.success("房间状态修改成功");
  fetchRooms();
};

onMounted(fetchRooms);
</script>

<style scoped>
.ktv-container {
  padding: 24px;
  background: #f7f8fa;
  min-height: calc(100vh - 60px);
}
.box-card {
  border-radius: 12px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.head-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}
.room-card {
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
}
.room-card:hover {
  transform: translateY(-4px);
}
.room-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
  border-bottom: 1px solid #f2f3f5;
}
.room-name {
  font-size: 16px;
  font-weight: 600;
}
.room-middle {
  text-align: center;
  padding: 16px;
  line-height: 2;
}
.room-type-text {
  color: #666;
  font-size: 14px;
}
.room-price-text {
  margin-top: 6px;
  font-size: 14px;
  color: #606266;
}
.price-num {
  font-size: 18px;
  color: #f56c6c;
  font-weight: bold;
}
.room-desc {
  margin-top: 12px;
  padding-top: 10px;
  border-top: 1px dashed #e9e9e9;
  font-size: 12px;
  color: #909399;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
}
.room-desc .el-icon {
  font-size: 14px;
}
.room-bottom {
  padding: 14px 16px;
  border-top: 1px solid #eee;
}
.btn-group {
  display: flex;
  gap: 10px;
}
</style>
