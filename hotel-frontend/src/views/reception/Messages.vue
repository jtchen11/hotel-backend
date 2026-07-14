<template>
  <div>
    <h2>留言管理</h2>
    <div
      style="
        display: flex;
        align-items: center;
        gap: 16px;
        margin-bottom: 20px;
        flex-wrap: wrap;
      "
    >
      <el-button type="primary" @click="dialog = true">新增留言</el-button>
      <el-select
        v-model="filterMsgType"
        placeholder="留言类型"
        clearable
        @change="getList"
        style="width: 140px"
      >
        <el-option label="普通留言" value="普通留言" />
        <el-option label="叫早服务" value="叫早服务" />
        <el-option label="换房记录" value="换房记录" />
        <el-option label="损坏记录" value="损坏记录" />
      </el-select>
      <el-select
        v-model="filterIsRead"
        placeholder="已读状态"
        clearable
        @change="getList"
        style="width: 120px"
      >
        <el-option label="未读" :value="0" />
        <el-option label="已读" :value="1" />
      </el-select>
      <el-button type="primary" @click="getList">查询</el-button>
    </div>
    <el-table :data="msgList" border style="width: 100%; margin-top: 20px">
      <el-table-column prop="messageId" label="ID" />
      <el-table-column prop="roomNumber" label="房间号" />
      <el-table-column prop="content" label="内容" />
      <el-table-column prop="msgType" label="类型" />
      <el-table-column prop="createTime" label="时间" />
      <el-table-column label="状态">
        <template v-slot="{ row }">
          <span :style="row.isRead ? { color: 'green' } : { color: 'red' }">
            {{ row.isRead ? "已读" : "未读" }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template v-slot="{ row }">
          <el-button
            type="text"
            @click="read(row.messageId)"
            :disabled="row.isRead === 1"
            :style="row.isRead ? { color: '#cccccc' } : { color: '#409EFF' }"
            >标记已读</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialog" title="新增留言">
      <el-form :model="msg">
        <!-- 替换原来的房间选择 -->
        <el-form-item label="选择客人">
          <el-select
            v-model="selectedGuest"
            filterable
            remote
            reserve-keyword
            :remote-method="searchGuest"
            :loading="guestLoading"
            placeholder="请输入客人姓名/房号/电话"
            clearable
            @change="onGuestChange"
          >
            <el-option
              v-for="g in guestOptions"
              :key="g.guestId"
              :label="`${g.name} - ${g.roomNumber} - ${g.phone}`"
              :value="g"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="msg.content" type="textarea" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="msg.msgType">
            <el-option label="普通留言" value="普通留言" />
            <el-option label="叫早服务" value="叫早服务" />
            <el-option label="换房记录" value="换房记录" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog = false">取消</el-button>
        <el-button type="primary" @click="addMsg">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import request from "@/utils/request";

const msgList = ref([]);
const roomList = ref([]);
const dialog = ref(false);
const msg = ref({ roomId: "", content: "", msgType: "普通留言" });
const filterMsgType = ref("");
const filterIsRead = ref(null);
const selectedGuest = ref(null);
const guestOptions = ref([]);
const guestLoading = ref(false);

const getList = async () => {
  const params = {};
  if (filterMsgType.value) params.msgType = filterMsgType.value;
  if (filterIsRead.value !== null && filterIsRead.value !== "")
    params.isRead = filterIsRead.value;
  const res = await request.get("/reception/message/list", { params });
  msgList.value = res.data;
};

const getRoomList = async () => {
  const res = await request.get("/reception/room/statusList");
  roomList.value = res.data;
};

const searchGuest = async (query) => {
  if (!query || query.trim() === "") {
    guestOptions.value = [];
    return;
  }
  guestLoading.value = true;
  try {
    const res = await request.get("/reception/message/guest/search", {
      params: { keyword: query.trim() },
    });
    guestOptions.value = res.data || [];
  } catch (err) {
    console.error(err);
    ElMessage.error("搜索客人失败");
  } finally {
    guestLoading.value = false;
  }
};

const onGuestChange = (guest) => {
  if (guest) {
    msg.value.roomId = guest.roomId;
  } else {
    msg.value.roomId = "";
  }
};

const addMsg = async () => {
  if (!msg.value.roomId) {
    ElMessage.warning("请选择一位在住客人");
    return;
  }
  if (!msg.value.content.trim()) {
    ElMessage.warning("请输入留言内容");
    return;
  }
  const res = await request.post("/reception/message/add", msg.value);
  if (res.code !== 200) {
    ElMessage.error(res.msg);
    return;
  }
  ElMessage.success("添加成功");
  dialog.value = false;
  msg.value = { roomId: "", content: "", msgType: "普通留言" };
  selectedGuest.value = null;
  guestOptions.value = [];
  getList();
};

const read = async (id) => {
  await request.put(`/reception/message/read/${id}`);
  ElMessage.success("已标记已读");
  getList();
};

onMounted(() => {
  getList();
  getRoomList();
});
</script>
