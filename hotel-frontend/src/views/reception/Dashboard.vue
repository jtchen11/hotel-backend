<template>
  <div class="home-page" style="padding: 20px">
    <h2 class="page-title">前台首页</h2>
    <!-- 顶部统计卡片 -->
    <div
      class="top-card-box"
      style="
        display: grid;
        grid-template-columns: repeat(5, 1fr);
        gap: 20px;
        margin-bottom: 35px;
      "
    >
      <div class="stat-card">
        <div class="stat-value">{{ roomData.total }}</div>
        <div class="stat-label">总房间数</div>
      </div>
      <div class="stat-card">
        <div class="stat-value" style="color:#52c41a">{{ roomData.free }}</div>
        <div class="stat-label">空闲</div>
      </div>
      <div class="stat-card">
        <div class="stat-value" style="color:#fa8c16">{{ roomData.used }}</div>
        <div class="stat-label">占用</div>
      </div>
      <div class="stat-card">
        <div class="stat-value" style="color:#909399">{{ roomData.repair }}</div>
        <div class="stat-label">维修中</div>
      </div>
      <div class="stat-card">
        <div class="stat-value" style="color:#409eff">{{ roomData.clean }}</div>
        <div class="stat-label">打扫中</div>
      </div>
    </div>

    <!-- 左右两栏布局 -->
    <div style="display: flex; gap: 20px">
      <!-- 左侧：两段式房间/客人列表 -->
      <div style="flex: 2">
        <el-card class="section-card">
          <template #header>
            <span>🏠 所有房间列表</span>
          </template>
          <el-table :data="allRooms" border stripe size="small" max-height="350">
            <el-table-column prop="roomNumber" label="房间号" width="100" />
            <el-table-column prop="roomType" label="房型" width="250" />
            <el-table-column prop="price" label="价格" width="150">
              <template #default="{ row }">¥{{ row.price }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="150">
              <template #default="{ row }">
                <el-tag :type="statusTagType(row.status)" size="small">{{
                  row.status === '已锁' ? '空闲' : row.status
                }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="250">
              <template #default="{ row }">
                <el-select
                  v-model="row.status"
                  @change="changeStatus(row)"
                  size="small"
                  :disabled="row.status === '占用' || row.status === '已预订'"
                >
                  <el-option label="空闲" value="空闲" />
                  <el-option label="维修中" value="维修中" />
                  <el-option label="打扫中" value="打扫中" />
                </el-select>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <!-- 下半部分：在住客人列表（含挂账总额） -->
        <el-card class="section-card" style="margin-top: 20px">
          <template #header>
            <span>👥 当前在住客人</span>
          </template>
          <el-table
            :data="livingList"
            border
            stripe
            size="small"
            :row-style="rowStyle"
          >
            <el-table-column prop="roomNumber" label="房间号" width="100" />
            <el-table-column prop="name" label="客人姓名" />
            <el-table-column prop="phone" label="电话" />
            <el-table-column prop="checkInDate" label="入住时间" width="160" />
            <el-table-column prop="preLeaveDate" label="离店时间" width="160" />
            <el-table-column prop="consume" label="挂账总额" width="120">
              <template #default="{ row }"
                >¥{{ row.consume.toFixed(2) }}</template
              >
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-button size="small" type="primary" @click="openExtendDialog(row)">续房</el-button>
                </template>
            </el-table-column>
          </el-table>
        </el-card>
      </div>

      <div style="flex: 1; display: flex; flex-direction: column; gap: 20px">
        <!-- 待入住预订卡片 -->
        <el-card class="msg-card">
          <template #header>
            <span>📅 待入住预订</span>
          </template>
          <div v-if="pendingBookings.length === 0" class="empty-msg">
            暂无待入住预订
          </div>
          <div v-else class="booking-list">
            <div v-if="individualBookings.length > 0" style="font-size:13px;color:#909399;margin-bottom:8px">散客预订</div>
            <div
              v-for="b in individualBookings"
              :key="'i' + b.guestId"
              class="booking-item"
            >
              <div>
                <el-tag size="mini" type="warning" style="margin-right:6px">散客</el-tag>
                <el-tag v-if="isOverdue(b.checkInDate)" size="mini" type="danger" style="margin-right:6px">逾期</el-tag>
                <strong>{{ b.name }}</strong> | {{ b.phone }}
              </div>
              <div>房间：{{ b.roomNumber }} ({{ b.roomType }})</div>
              <div class="date-row">
                <span>预订入住：{{ formatDate(b.checkInDate) }}</span>
                <span>计划离店：{{ formatDate(b.preLeaveDate) }}</span>
              </div>
              <el-button type="primary" size="small" @click="openCheckinDialog(b)">办理入住</el-button>
              <el-button type="danger" size="small" @click="cancelBooking(b.guestId)">取消预约</el-button>
            </div>
            <div v-if="teamGroups.length > 0" style="font-size:13px;color:#909399;margin:12px 0 8px">团体预订</div>
            <div
              v-for="g in teamGroups"
              :key="'g' + g.groupId"
              class="booking-item"
              style="background:#f0f9eb"
            >
              <div>
                <el-tag size="mini" type="success" style="margin-right:6px">团体</el-tag>
                <el-tag v-if="isOverdue(g.checkInDate)" size="mini" type="danger" style="margin-right:6px">逾期</el-tag>
                <strong>{{ g.groupName }}</strong>
                <span style="margin-left:8px;color:#909399;font-size:12px">{{ g.rooms.length }}间房</span>
              </div>
              <div>领队：{{ g.groupLeader }} | {{ g.leaderPhone }}</div>
              <div class="date-row">
                <span>预订入住：{{ formatDate(g.checkInDate) }}</span>
                <span>计划离店：{{ formatDate(g.preLeaveDate) }}</span>
              </div>
              <div style="font-size:12px;color:#909399;margin-top:4px">
                房间：<template v-for="(r, ri) in g.rooms" :key="r.roomId">{{ ri > 0 ? ", " : "" }}{{ r.roomNumber }}</template>
              </div>
              <el-button type="primary" size="small" @click="openTeamCheckin(g)">办理入住</el-button>
              <el-button type="danger" size="small" @click="cancelTeamBooking(g)">取消预约</el-button>
            </div>
          </div>
        </el-card>
        <el-card class="msg-card">
          <template #header>
            <div
              style="
                display: flex;
                justify-content: space-between;
                align-items: center;
              "
            >
              <span>
                💬 未读留言
                <el-badge
                  :value="unreadCount"
                  :hidden="unreadCount === 0"
                  type="danger"
                  style="margin-left: 8px"
                />
              </span>
              <el-button
                type="text"
                @click="$router.push('/reception/messages')"
                >查看全部</el-button
              >
            </div>
          </template>
          <div v-if="unreadMessages.length === 0" class="empty-msg">
            暂无未读留言
          </div>
          <div v-else class="msg-list">
            <div
              v-for="msg in unreadMessages"
              :key="msg.messageId"
              class="msg-item"
            >
              <div class="msg-content">{{ msg.content }}</div>
              <div class="msg-meta">
                {{ msg.roomNumber }} · {{ msg.createTime }}
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </div>
    
    
  </div>
  <!-- 续房弹窗 -->
    <el-dialog v-model="extendDialogVisible" title="续房" width="420px">
      <el-form label-width="100px">
        <el-form-item label="客人姓名">{{ extendGuest?.name }}</el-form-item>
        <el-form-item label="房间号">{{ extendGuest?.roomNumber }}</el-form-item>
        <el-form-item label="原离店日期">{{ extendGuest?.preLeaveDate }}</el-form-item>
        <el-form-item label="新离店日期">
          <el-date-picker v-model="extendNewDate" type="date" value-format="YYYY-MM-DD"
            placeholder="选择新离店日期" style="width:100%" />
        </el-form-item>
        <el-form-item label="补收押金">
          <el-input v-model.number="extendDeposit" placeholder="0">
            <template #prefix>¥</template>
          </el-input>
        </el-form-item>
        <el-form-item label="支付方式" v-if="extendDeposit > 0">
          <el-select v-model="extendPayMethod" style="width:100%">
            <el-option v-for="pm in PAY_METHODS" :key="pm.value" :label="pm.label" :value="pm.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="extendRemark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="extendDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmExtend" :loading="extending">确认续房</el-button>
      </template>
    </el-dialog>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import request from "@/utils/request";
import { PAY_METHODS } from "@/constants/payment";

const router = useRouter();

const roomData = ref({ total: 0, free: 0, used: 0, repair: 0, clean: 0 });
const allRooms = ref([]);
const livingList = ref([]);
const unreadMessages = ref([]);
const unreadCount = ref(0);
const pendingBookings = ref([]);
const extendDialogVisible = ref(false);
const extendGuest = ref(null);
const extendNewDate = ref("");
const extendDeposit = ref(0);
const extendPayMethod = ref("现金");
const extendRemark = ref("");
const extending = ref(false);

const individualBookings = computed(() => {
  return (pendingBookings.value || []).filter(b => !b.guestType || b.guestType === "散客");
});

const teamGroups = computed(() => {
  const groups = {};
  (pendingBookings.value || []).filter(b => b.guestType === "团体").forEach(b => {
    if (!groups[b.groupId]) {
      groups[b.groupId] = {
        groupId: b.groupId,
        groupName: b.groupName || ("团队#" + b.groupId),
        rooms: []
      };
    }
    groups[b.groupId].rooms.push(b);
    if (!groups[b.groupId].groupLeader && b.name) groups[b.groupId].groupLeader = b.name;
    if (!groups[b.groupId].leaderPhone && b.phone) groups[b.groupId].leaderPhone = b.phone;
    if (!groups[b.groupId].checkInDate && b.checkInDate) groups[b.groupId].checkInDate = b.checkInDate;
    if (!groups[b.groupId].preLeaveDate && b.preLeaveDate) groups[b.groupId].preLeaveDate = b.preLeaveDate;
  });
  return Object.values(groups);
});

const isTodayLeave = (preLeaveDateStr) => {
  if (!preLeaveDateStr) return false;
  const today = new Date();
  const year = today.getFullYear();
  const month = String(today.getMonth() + 1).padStart(2, "0");
  const day = String(today.getDate()).padStart(2, "0");
  const todayStr = `${year}-${month}-${day}`;
  return preLeaveDateStr === todayStr;
};

const rowStyle = ({ row }) => {
  const today = isTodayLeave(row.preLeaveDate);
  if (today) {
    return { backgroundColor: "#fef0f0", color: "#f56c6c" };
  }
  return {};
};

const statusTagType = (status) => {
  const map = {
    空闲: "success",
    已锁: "success",
    维修中: "info",
    打扫中: "warning",
    占用: "danger",
    已预订: "primary",
  };
  return map[status] || "info";
};

const loadData = async () => {
  const res = await request.get("/reception/room/statusList");
  const all = res.data;
  let free = 0, used = 0, repair = 0, clean = 0;
  all.forEach((r) => {
    if (r.status === "空闲") free++;
    else if (r.status === "占用") used++;
    else if (r.status === "维修中") repair++;
    else if (r.status === "打扫中") clean++;
  });
  roomData.value = { total: all.length, free, used, repair, clean };
  allRooms.value = all;
};

const loadLivingList = async () => {
  try {
    const res = await request.get("/reception/livingWithConsume");
    livingList.value = res.data || [];
  } catch (error) {}
};

const loadUnreadMessages = async () => {
  const res = await request.get("/reception/message/list", {
    params: { isRead: 0 },
  });
  unreadMessages.value = res.data || [];
  unreadCount.value = unreadMessages.value.length;
};

const loadPendingBookings = async () => {
  const res = await request.get("/reception/pendingBookings");
  pendingBookings.value = res.data || [];
};

const cancelBooking = async (guestId) => {
  try {
    await ElMessageBox.confirm("确定取消该预约吗？", "提示", { type: "warning" });
    await request.post(`/reception/cancelBooking?guestId=${guestId}`);
    ElMessage.success("取消成功");
    loadPendingBookings();
  } catch (err) {
    if (err !== "cancel") {
      ElMessage.error("取消失败");
    }
  }
};

const changeStatus = async (row) => {
  if (row.status === "占用") {
    ElMessage.warning("房间正在使用中，不能修改状态");
    return;
  }
  const res = await request.put(
    `/reception/room/updateStatus?roomId=${row.roomId}&status=${row.status}`,
  );
  ElMessage.success(res.msg || "状态修改成功");
  loadData();
};

const viewGuestDetail = (guestId) => {
  ElMessage.info(`查看客人详情 (guestId: ${guestId})`);
};

const todayStr = new Date().toISOString().slice(0, 10)
const isOverdue = (d) => d ? String(d).slice(0, 10) < todayStr : false

const formatDate = (dateStr) => {
  if (!dateStr) return "";
  const d = new Date(dateStr);
  return `${d.getMonth() + 1}/${d.getDate()} ${d.getHours()}:${d.getMinutes()}`;
};

const openCheckinDialog = (booking) => {
  if (booking.guestType === "团体") {
    router.push("/reception/checkin?groupId=" + booking.groupId);
  } else {
    router.push("/reception/checkin?guestId=" + booking.guestId);
  }
};

const openTeamCheckin = (group) => {
  router.push("/reception/checkin?groupId=" + group.groupId);
};

const cancelTeamBooking = (group) => {
  ElMessageBox.confirm("确定取消该团体 " + group.groupName + " 的全部预订吗？", "提示", { type: "warning" }).then(async () => {
    try {
      for (const r of group.rooms) {
        await request.post("/reception/cancelBooking?guestId=" + r.guestId);
      }
      ElMessage.success("取消成功");
      loadPendingBookings();
    } catch {
      ElMessage.error("取消失败");
    }
  }).catch(() => {});
};

const openExtendDialog = (row) => {
  extendGuest.value = {
    guestId: row.guestId,
    name: row.name,
    roomNumber: row.roomNumber,
    preLeaveDate: row.preLeaveDate,
    roomId: row.roomId,
  };
  if (row.preLeaveDate) {
    const d = new Date(row.preLeaveDate);
    d.setDate(d.getDate() + 1);
    extendNewDate.value = d.toISOString().slice(0, 10);
  } else {
    extendNewDate.value = "";
  }
  extendDeposit.value = 0;
  extendPayMethod.value = "现金";
  extendRemark.value = "";
  extendDialogVisible.value = true;
};

const confirmExtend = async () => {
  if (!extendNewDate.value || !extendGuest.value) return;
  if (extendNewDate.value <= extendGuest.value.preLeaveDate) {
    ElMessage.warning("新离店日期必须晚于原离店日期");
    return;
  }
  extending.value = true;
  try {
    const res = await request.post("/reception/extendStay", {
      guestId: extendGuest.value.guestId,
      newLeaveDate: extendNewDate.value,
      additionalDeposit: extendDeposit.value || 0,
      payMethod: extendPayMethod.value,
      remark: extendRemark.value,
    });
    if (res.code === 200) {
      ElMessage.success("续房成功");
      extendDialogVisible.value = false;
      loadLivingList();
    } else {
      ElMessage.error(res.msg || "续房失败");
    }
  } catch {
    ElMessage.error("续房失败");
  } finally {
    extending.value = false;
  }
};

onMounted(() => {
  loadData();
  loadLivingList();
  loadUnreadMessages();
  loadPendingBookings();
});
</script>

<style scoped>
.section-card {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}
.msg-card {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  height: fit-content;
}
.msg-list {
  max-height: 400px;
  overflow-y: auto;
}
.msg-item {
  border-bottom: 1px solid #eee;
  padding: 10px 0;
}
.msg-content {
  font-size: 13px;
  color: #333;
}
.msg-meta {
  font-size: 11px;
  color: #999;
  margin-top: 4px;
}
.empty-msg {
  text-align: center;
  color: #999;
  padding: 20px;
}
.booking-list {
  max-height: 400px;
  overflow-y: auto;
}
.booking-item {
  border-bottom: 1px solid #eee;
  padding: 10px 0;
}
.booking-item:last-child {
  border-bottom: none;
}
.date-row {
  display: flex;
  gap: 20px;
  margin: 5px 0;
}
.booking-actions {
  display: flex;
  gap: 10px;
  margin-top: 8px;
}
</style>



