<template>
  <div class="room-status">
    <h2>房态图</h2>
    <div class="room-grid">
      <div
        v-for="room in rooms"
        :key="room.roomId"
        class="room-card"
        @click="showRoomDetail(room)"
      >
        <div class="img-wrap">
          <img
            :src="getImageUrl(room.roomNumber)"
            alt="房型图"
            @error="handleImageError"
          />
        </div>
        <div class="room-info">
          <div class="room-number">{{ room.roomNumber }}</div>
          <div class="room-type">{{ room.roomType }}{{ room.description ? ' · ' + room.description : '' }}</div>
          <div :class="getStatusClass(room.status)">{{ getDisplayStatus(room) }}</div>
          <div v-if="room.guestName" class="guest-name">
            客人：{{ room.guestName }}
          </div>
          <div class="room-price">¥{{ room.price }}</div>
        </div>
        <div class="room-actions">
          <el-button
            size="small"
            type="primary"
            @click.stop="openStatusDialog(room)"
            >修改状态</el-button
          >
          <el-button
            v-if="room.status === '占用'"
            size="small"
            type="warning"
            @click.stop="openChangeRoomDialog(room)"
            >换房</el-button
          >
        </div>
      </div>
    </div>

    <!-- 修改状态对话框 -->
    <el-dialog title="修改房间状态" v-model="statusDialogVisible" width="400px">
      <el-form label-width="80px">
        <el-form-item label="房间号">{{ currentRoom.roomNumber }}</el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="newStatus"
            :disabled="currentRoom.status === '占用'"
          >
            <el-option label="空闲" value="空闲" />
            <el-option label="占用" value="占用" :disabled="true" />
            <el-option label="维修中" value="维修中" />
            <el-option label="打扫中" value="打扫中" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="statusDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveStatus">保存</el-button>
      </template>
    </el-dialog>

    <!-- 换房对话框 -->
    <el-dialog title="换房" v-model="changeDialogVisible" width="450px">
      <el-form label-width="80px">
        <el-form-item label="当前房间">{{
          currentRoom.roomNumber
        }}</el-form-item>
        <el-form-item label="当前客人">{{
          currentRoom.guestName
        }}</el-form-item>
        <el-form-item label="目标房间">
          <el-select
            v-model="targetRoomId"
            placeholder="请选择空闲房间"
            filterable
          >
            <el-option
              v-for="r in freeRooms"
              :key="r.roomId"
              :label="`${r.roomNumber} (${r.roomType}) ¥${r.price}`"
              :value="r.roomId"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="changeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmChange">确认换房</el-button>
      </template>
    </el-dialog>
    <!-- 详情对话框 -->
    <el-dialog title="房间详情" v-model="detailDialogVisible" width="550px">
      <div v-if="roomDetail">
        <h4>当前入住客人</h4>
        <div v-if="roomDetail.currentGuest">
          <p>姓名：{{ roomDetail.currentGuest.name }}</p>
          <p>电话：{{ roomDetail.currentGuest.phone }}</p>
          <p>入住时间：{{ roomDetail.currentGuest.checkInDate }}</p>
          <p>预离日期：{{ roomDetail.currentGuest.preLeaveDate }}</p>
          <!-- 新增 -->
        </div>
        <div v-else>暂无在住客人</div>
        <h4>未来预订（按入住时间排序）</h4>
        <div v-if="roomDetail.bookings.length">
          <div
            v-for="b in roomDetail.bookings"
            :key="b.checkInDate"
            class="booking-item-detail"
          >
            <span>{{ b.name }} ({{ b.phone }})</span>
            <span
              >{{ formatDate(b.checkInDate) }} 至
              {{ formatDate(b.preLeaveDate) }}</span
            >
          </div>
        </div>
        <div v-else>暂无未来预订</div>
      </div>
      <template #footer>
        <div style="display: flex; justify-content: space-between">
          <div>
            <el-input
              v-model="damageItem"
              placeholder="损坏物品名称"
              style="width: 150px; margin-right: 10px"
            ></el-input>
            <el-input-number
              v-model="damageAmount"
              placeholder="赔偿金额(元)"
              style="width: 180px"
            ></el-input-number>
            <el-button type="danger" @click="registerDamage"
              >登记损坏</el-button
            >
          </div>
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import request from "@/utils/request";

const rooms = ref([]);
const statusDialogVisible = ref(false);
const changeDialogVisible = ref(false);
const currentRoom = ref({});
const newStatus = ref("");
const targetRoomId = ref(null);
const freeRooms = ref([]);
const detailDialogVisible = ref(false);
const roomDetail = ref(null);
const damageItem = ref("");
const damageAmount = ref(0);
const currentRoomForDetail = ref(null);

const getRooms = async () => {
  const today = new Date().toISOString().slice(0, 10);
  const [statusRes, dateRes] = await Promise.all([
    request.get("/reception/room/statusList"),
    request.get("/reception/room/dateList", { params: { inDate: today, outDate: today } })
  ]);
  const statusMap = {};
  (statusRes.data || []).forEach(r => { statusMap[r.roomId] = r; });
  (dateRes.data || []).forEach(r => {
    if (statusMap[r.roomId]) {
      statusMap[r.roomId].isBusy = r.isBusy;
    }
  });
  rooms.value = Object.values(statusMap);
};

const getImageUrl = (roomNumber) => {
  return `http://localhost:8080/uploads/rooms/${roomNumber}.jpg`;
};

const handleImageError = (e) => {
  e.target.src = "https://via.placeholder.com/200x150?text=No+Image";
};

const getStatusClass = (status) => {
  if (status === "维修中") return "status-repair";
  if (status === "打扫中") return "status-clean";
  if (status === "占用") return "status-used";
  return "";
};

const getDisplayStatus = (room) => {
  if (room.status === "维修中") return "维修中";
  if (room.status === "打扫中") return "打扫中";
  if (room.status === "占用") return "占用";
  if (room.isBusy) return "已锁";
  return "空闲";
};

const openStatusDialog = (room) => {
  currentRoom.value = { ...room };
  newStatus.value = room.status;
  statusDialogVisible.value = true;
};

const saveStatus = async () => {
  if (currentRoom.value.status === "占用") {
    ElMessage.warning("房间正在使用中，不能修改状态");
    return;
  }
  const res = await request.put(
    `/reception/room/updateStatus?roomId=${currentRoom.value.roomId}&status=${newStatus.value}`,
  );
  ElMessage.success(res.msg || "状态修改成功");
  statusDialogVisible.value = false;
  getRooms();
};

const openChangeRoomDialog = async (room) => {
  const today = new Date().toISOString().slice(0, 10);
  const tomorrow = new Date(Date.now() + 86400000).toISOString().slice(0, 10);
  const res = await request.get("/reception/room/dateList", { params: { inDate: today, outDate: tomorrow } });
  freeRooms.value = (res.data || []).filter((r) => !r.isBusy && r.status !== "维修中" && r.status !== "占用" && r.roomId !== currentRoom.value?.roomId);
  if (freeRooms.value.length === 0) {
    ElMessage.warning("当前没有可用房间可供换房");
    return;
  }
  currentRoom.value = { ...room };
  targetRoomId.value = null;
  changeDialogVisible.value = true;
};

const confirmChange = async () => {
  if (!targetRoomId.value) {
    ElMessage.warning("请选择目标房间");
    return;
  }
  if (!currentRoom.value.guestId) {
    ElMessage.error("无法获取客人信息，请刷新页面重试");
    return;
  }
  await request.post("/reception/changeRoom", null, {
    params: {
      guestId: currentRoom.value.guestId,
      newRoomId: targetRoomId.value,
    },
  });
  ElMessage.success("换房成功");
  changeDialogVisible.value = false;
  getRooms();
};

const showRoomDetail = async (room) => {
  currentRoomForDetail.value = room;
  let currentGuest = null;
  if (room.status === "占用" && room.guestId) {
    const res = await request.get(`/guest/detail/${room.guestId}`);
    currentGuest = res.data;
  }
  const bookingsRes = await request.get(
    `/reception/room/bookings/${room.roomId}`,
  );
  roomDetail.value = {
    currentGuest: currentGuest,
    bookings: bookingsRes.data || [],
  };
  detailDialogVisible.value = true;
};

const registerDamage = async () => {
  if (!damageItem.value || damageAmount.value <= 0) {
    ElMessage.warning("请填写损坏物品名称和有效的赔偿金额");
    return;
  }
  const roomId = currentRoomForDetail.value?.roomId;
  if (!roomId) {
    ElMessage.error("房间信息丢失，请重新打开");
    return;
  }
  try {
    await request.post("/reception/damage", {
      roomId: roomId,
      itemName: damageItem.value,
      amount: damageAmount.value,
    });
    ElMessage.success("损坏登记成功");
    damageItem.value = "";
    damageAmount.value = 0;
    getRooms();
  } catch (err) {
    ElMessage.error(err.response?.data?.msg || "登记失败");
  }
};

const formatDate = (dateValue) => {
  if (!dateValue) return "";
  let date;
  if (typeof dateValue === "string") {
    date = new Date(dateValue);
  } else if (dateValue instanceof Date) {
    date = dateValue;
  } else {
    if (dateValue.year && dateValue.month && dateValue.day) {
      const year = dateValue.year;
      const month = String(dateValue.month).padStart(2, "0");
      const day = String(dateValue.day).padStart(2, "0");
      return `${year}-${month}-${day}`;
    }
    return dateValue;
  }
  if (isNaN(date.getTime())) return dateValue;
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
};

onMounted(() => {
  getRooms();
});
</script>

<style scoped>
.room-status {
  padding: 20px;
}
.room-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}
.room-card {
  border: 1px solid #e4e7ed;
  border-radius: 12px;
  overflow: hidden;
  background: white;
  transition: transform 0.2s;
}
.room-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}
.img-wrap {
  height: 160px;
  background-color: #f5f5f5;
}
.img-wrap img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.room-info {
  padding: 12px;
  height: 120px;
}
.room-number {
  font-size: 18px;
  font-weight: bold;
}
.room-type {
  font-size: 14px;
  color: #666;
  margin: 4px 0;
}
.guest-name {
  font-size: 13px;
  color: #409eff;
  margin: 4px 0;
}
.room-price {
  font-size: 14px;
  font-weight: bold;
  color: #f56c6c;
  margin-top: 6px;
}
.room-actions {
  padding: 10px 12px;
  border-top: 1px solid #eee;
  display: flex;
  gap: 8px;
  justify-content: space-between;
}
.status-free {
  color: #67c23a;
}
.status-used {
  color: #f56c6c;
}
.status-repair {
  color: #909399;
}
.status-clean {
  color: #409eff;
}
.status-locked {
  color: #b0b0b0;
}
</style>
