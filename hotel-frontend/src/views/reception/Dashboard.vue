<template>
  <div class="home-page" style="padding: 20px">
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
      <el-card shadow="hover">
        <div style="font-size: 32px; font-weight: bold">
          {{ roomData.total }}
        </div>
        <div style="color: #666">总房间数</div>
      </el-card>
      <el-card shadow="hover">
        <div style="font-size: 32px; font-weight: bold; color: #52c41a">
          {{ roomData.free }}
        </div>
        <div style="color: #666">空闲</div>
      </el-card>
      <el-card shadow="hover">
        <div style="font-size: 32px; font-weight: bold; color: #fa8c16">
          {{ roomData.used }}
        </div>
        <div style="color: #666">占用</div>
      </el-card>
      <el-card shadow="hover">
        <div style="font-size: 32px; font-weight: bold; color: #909399">
          {{ roomData.repair }}
        </div>
        <div style="color: #666">维修中</div>
      </el-card>
      <el-card shadow="hover">
        <div style="font-size: 32px; font-weight: bold; color: #409eff">
          {{ roomData.clean }}
        </div>
        <div style="color: #666">打扫中</div>
      </el-card>
    </div>

    <!-- 左右两栏布局 -->
    <div style="display: flex; gap: 20px">
      <!-- 左侧：两段式房间/客人列表 -->
      <div style="flex: 2">
        <el-card class="section-card">
          <template #header>
            <span>🏠 所有房间列表</span>
          </template>
          <el-table :data="allRooms" border stripe size="small">
            <el-table-column prop="roomNumber" label="房间号" width="100" />
            <el-table-column prop="roomType" label="房型" width="250" />
            <el-table-column prop="price" label="价格" width="150">
              <template #default="{ row }">¥{{ row.price }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="150">
              <template #default="{ row }">
                <el-tag :type="statusTagType(row.status)" size="small">{{
                  row.status
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
            <div
              v-for="b in pendingBookings"
              :key="b.guestId"
              class="booking-item"
            >
              <div>
                <strong>{{ b.name }}</strong> | {{ b.phone }}
              </div>
              <div>房间：{{ b.roomNumber }} ({{ b.roomType }})</div>
              <div class="date-row">
                <span>预订入住：{{ formatDate(b.checkInDate) }}</span>
                <span>计划离店：{{ formatDate(b.preLeaveDate) }}</span>
              </div>
              <el-button
                type="primary"
                size="small"
                @click="openCheckinDialog(b)"
                >办理入住</el-button
              >
              <el-button
                type="danger"
                size="small"
                @click="cancelBooking(b.guestId)"
                >取消预约</el-button
              >
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
    <!-- 入住办理弹窗（收取押金） -->
    <el-dialog title="办理入住" v-model="checkinDialogVisible" width="400px">
      <el-form :model="checkinForm" label-width="100px">
        <el-form-item label="客人姓名">{{ checkinForm.name }}</el-form-item>
        <el-form-item label="房间号">{{ checkinForm.roomNumber }}</el-form-item>
        <el-form-item label="押金金额">
          <el-input-number
            v-model="checkinForm.depositAmount"
            :min="200"
            :precision="2"
            :step="100"
          />
        </el-form-item>
        <el-form-item label="支付方式">
          <el-select v-model="checkinForm.payMethod">
            <el-option label="微信" value="微信" />
            <el-option label="支付宝" value="支付宝" />
            <el-option label="现金" value="现金" />
            <el-option label="银行卡" value="银行卡" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="checkinDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmCheckin">确认入住</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import request from "@/utils/request";
export default {
  data() {
    return {
      roomData: { total: 0, free: 0, used: 0, repair: 0, clean: 0 },
      allRooms: [], // 新增：所有房间列表
      livingList: [],
      unreadMessages: [],
      unreadCount: 0,
      pendingBookings: [],
      checkinDialogVisible: false,
      checkinForm: {
        guestId: null,
        name: "",
        roomNumber: "",
        depositAmount: 0,
        payMethod: "微信",
      },
    };
  },
  mounted() {
    this.loadData();
    this.loadLivingList();
    this.loadUnreadMessages();
    this.loadPendingBookings();
  },
  methods: {
    // 判断离店日期是否为今天
    isTodayLeave(preLeaveDateStr) {
      if (!preLeaveDateStr) return false;
      // 获取本地日期的 YYYY-MM-DD 字符串
      const today = new Date();
      const year = today.getFullYear();
      const month = String(today.getMonth() + 1).padStart(2, "0");
      const day = String(today.getDate()).padStart(2, "0");
      const todayStr = `${year}-${month}-${day}`;
      console.log("比较:", preLeaveDateStr, "vs", todayStr); // 调试
      return preLeaveDateStr === todayStr;
    },

    // 表格行样式
    rowStyle({ row }) {
      const isToday = this.isTodayLeave(row.preLeaveDate);
      console.log(
        "rowStyle:",
        row.roomNumber,
        row.preLeaveDate,
        "isTodayLeave:",
        isToday,
      );
      if (isToday) {
        return { backgroundColor: "#fef0f0", color: "#f56c6c" };
      }
      return {};
    },
    statusTagType(status) {
      const map = {
        空闲: "success",
        维修中: "info",
        打扫中: "warning",
        占用: "danger",
        已预订: "primary",
      };
      return map[status] || "info";
    },
    async cancelBooking(guestId) {
      try {
        await this.$confirm("确定取消该预约吗？", "提示", { type: "warning" });
        await request.post(`/reception/cancelBooking?guestId=${guestId}`);
        this.$message.success("取消成功");
        this.loadPendingBookings(); // 刷新待入住列表
      } catch (err) {
        if (err !== "cancel") {
          this.$message.error("取消失败");
        }
      }
    },
    async loadData() {
      const res = await request.get("/reception/room/statusList");
      const all = res.data;
      let free = 0,
        used = 0,
        repair = 0,
        clean = 0;
      all.forEach((r) => {
        if (r.status === "空闲") free++;
        else if (r.status === "占用") used++;
        else if (r.status === "维修中") repair++;
        else if (r.status === "打扫中") clean++;
      });
      this.roomData = { total: all.length, free, used, repair, clean };
      this.allRooms = all;
    },
    async loadLivingList() {
      try {
        const res = await request.get("/reception/livingWithConsume");
        console.log("livingList API 完整响应:", res);
        console.log("livingList 数据数组:", res.data);
        if (res.data && res.data.length > 0) {
          console.log("第一条数据示例:", res.data[0]);
          console.log("preLeaveDate 字段值:", res.data[0].preLeaveDate);
        } else {
          console.log("livingList 无数据");
        }
        this.livingList = res.data || [];
      } catch (error) {
        console.error("加载在住客人列表失败:", error);
      }
    },
    async loadUnreadMessages() {
      const res = await request.get("/reception/message/list", {
        params: { isRead: 0 },
      });
      this.unreadMessages = res.data || [];
      this.unreadCount = this.unreadMessages.length;
    },
    async changeStatus(row) {
      if (row.status === "占用") {
        this.$message.warning("房间正在使用中，不能修改状态");
        return;
      }
      await request.put(
        `/reception/room/updateStatus?roomId=${row.roomId}&status=${row.status}`,
      );
      this.$message.success("状态修改成功");
      this.loadData(); // 刷新可分配房间
    },
    viewGuestDetail(guestId) {
      this.$message.info(`查看客人详情 (guestId: ${guestId})`);
    },
    async loadPendingBookings() {
      const res = await request.get("/reception/pendingBookings");
      this.pendingBookings = res.data || [];
    },
    formatDate(dateStr) {
      if (!dateStr) return "";
      const d = new Date(dateStr);
      return `${d.getMonth() + 1}/${d.getDate()} ${d.getHours()}:${d.getMinutes()}`;
    },
    openCheckinDialog(booking) {
      this.checkinForm = {
        guestId: booking.guestId,
        name: booking.name,
        roomNumber: booking.roomNumber,
        depositAmount: 500,
        payMethod: "微信",
      };
      this.checkinDialogVisible = true;
    },
    async confirmCheckin() {
      if (this.checkinForm.depositAmount <= 0) {
        this.$message.warning("请输入押金金额");
        return;
      }
      await request.post("/reception/checkinFromBooking", {
        guestId: this.checkinForm.guestId,
        depositAmount: this.checkinForm.depositAmount,
        payMethod: this.checkinForm.payMethod,
      });
      this.$message.success("入住成功");
      this.checkinDialogVisible = false;
      // 刷新三个列表
      this.loadData();
      this.loadLivingList();
      this.loadPendingBookings();
      this.loadUnreadMessages(); // 可选
    },
  },
};
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
