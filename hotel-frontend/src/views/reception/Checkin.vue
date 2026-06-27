<template>
  <div
    class="checkin-page"
    style="padding: 20px; max-width: 1300px; margin: 0 auto"
  >
    <h2 style="margin-bottom: 20px">散客预订</h2>
    <el-steps
      :active="step"
      finish-status="success"
      align-center
      style="margin-bottom: 30px"
    >
      <el-step title="选择房间" />
      <el-step title="填写客人信息" />
      <el-step title="确认入住" />
    </el-steps>

    <!-- 日期范围选择器 -->
    <div
      style="display: flex; gap: 12px; align-items: center; margin-bottom: 20px"
    >
      <span>入住 / 退房日期：</span>
      <el-date-picker
        v-model="dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="入住日期"
        end-placeholder="退房日期"
        value-format="YYYY-MM-DD"
        @change="onDateRangeChange"
      />
      <el-button type="primary" @click="loadAllRoomByDate"
        >应用日期筛选</el-button
      >
    </div>

    <!-- 第一步：选择房间 -->
    <div v-if="step === 1">
      <div v-loading="loading" class="room-grid">
        <div
          v-for="r in allRoomList"
          :key="r.roomId"
          class="room-card"
          :class="{
            selected: selectedRoom?.roomId === r.roomId,
            disable: r.isBusy,
          }"
          @click="pickRoom(r)"
        >
          <div class="room-num">{{ r.roomNumber }}</div>
          <div class="room-type">{{ r.roomType }}</div>
          <div class="room-price">¥{{ r.price }}</div>
          <div v-if="r.isBusy" class="tip">当前时段已占用</div>
          <div v-else class="room-status" style="color: #67c23a">空闲可订</div>
        </div>
      </div>
      <div style="text-align: right; margin-top: 20px">
        <el-button
          type="primary"
          :disabled="!selectedRoom || selectedRoom.isBusy"
          @click="next"
          >下一步</el-button
        >
      </div>
    </div>

    <!-- 第二步：填写客人信息（含校验 + 籍贯下拉） -->
    <div v-else-if="step === 2">
      <el-form :model="guest" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="guest.name" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="guest.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="身份证" prop="idCard">
          <el-input v-model="guest.idCard" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="guest.phone" />
        </el-form-item>
        <el-form-item label="籍贯" prop="nativePlace">
          <el-select
            v-model="guest.nativePlace"
            filterable
            allow-create
            placeholder="请选择或输入籍贯"
            style="width: 100%"
          >
            <el-option
              v-for="province in provinceList"
              :key="province"
              :label="province"
              :value="province"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="工作单位">
          <el-input v-model="guest.company" />
        </el-form-item>
        <el-form-item label="职业">
          <el-input v-model="guest.occupation" />
        </el-form-item>
        <el-form-item label="住店事由">
          <el-input v-model="guest.reason" />
        </el-form-item>
      </el-form>
      <div style="display: flex; gap: 10px; justify-content: right">
        <el-button @click="prev">上一步</el-button>
        <el-button type="primary" @click="next">下一步</el-button>
      </div>
    </div>

    <!-- 第三步：确认入住 -->
    <div v-else-if="step === 3">
      <div style="padding: 20px; border: 1px solid #eee; border-radius: 8px">
        <h3>入住确认单</h3>
        <p>姓名：{{ guest.name }}</p>
        <p>性别：{{ guest.gender }}</p>
        <p>身份证：{{ guest.idCard }}</p>
        <p>电话：{{ guest.phone }}</p>
        <p>籍贯：{{ guest.nativePlace }}</p>
        <p>工作单位：{{ guest.company || "无" }}</p>
        <p>职业：{{ guest.occupation || "无" }}</p>
        <p>住店事由：{{ guest.reason || "无" }}</p>
        <p>房间：{{ selectedRoom?.roomNumber }}</p>
        <p>入住：{{ searchIn }}</p>
        <p>退房：{{ searchOut }}</p>
      </div>
      <div
        style="
          display: flex;
          gap: 10px;
          justify-content: right;
          margin-top: 20px;
        "
      >
        <el-button @click="prev">上一步</el-button>
        <el-button type="primary" @click="submitCheckIn"
          >确认提交预订</el-button
        >
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import request from "@/utils/request";
import { ElMessage } from "element-plus";

// ---------- 状态 ----------
const step = ref(1);
const loading = ref(false);
const allRoomList = ref([]);
const selectedRoom = ref(null);
const searchIn = ref("");
const searchOut = ref("");
const dateRange = ref([]);

// 客人信息（包含所有字段）
const guest = ref({
  name: "",
  gender: "男",
  idCard: "",
  phone: "",
  nativePlace: "",
  company: "",
  occupation: "",
  reason: "",
  roomId: null,
});

// 表单引用（用于校验）
const formRef = ref(null);

// ---------- 中国34个省级行政区（籍贯下拉数据） ----------
const provinceList = [
  "北京市",
  "天津市",
  "上海市",
  "重庆市",
  "河北省",
  "山西省",
  "辽宁省",
  "吉林省",
  "黑龙江省",
  "江苏省",
  "浙江省",
  "安徽省",
  "福建省",
  "江西省",
  "山东省",
  "河南省",
  "湖北省",
  "湖南省",
  "广东省",
  "海南省",
  "四川省",
  "贵州省",
  "云南省",
  "陕西省",
  "甘肃省",
  "青海省",
  "台湾省",
  "内蒙古自治区",
  "广西壮族自治区",
  "西藏自治区",
  "宁夏回族自治区",
  "新疆维吾尔自治区",
  "香港特别行政区",
  "澳门特别行政区",
];

// ---------- 表单校验规则 ----------
const rules = {
  name: [{ required: true, message: "请输入姓名", trigger: "blur" }],
  idCard: [
    { required: true, message: "请输入身份证号", trigger: "blur" },
    {
      pattern:
        /^[1-9]\d{5}(18|19|20)?\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dXx]$/,
      message: "请输入正确的18位身份证号",
      trigger: "blur",
    },
  ],
  phone: [
    { required: true, message: "请输入手机号", trigger: "blur" },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: "请输入正确的手机号",
      trigger: "blur",
    },
  ],
  nativePlace: [
    { required: true, message: "请选择或输入籍贯", trigger: "change" },
  ],
};

// ---------- 方法 ----------
// 日期范围变化同步
const onDateRangeChange = (val) => {
  if (val && val.length === 2) {
    searchIn.value = val[0];
    searchOut.value = val[1];
  } else {
    searchIn.value = "";
    searchOut.value = "";
  }
};

// 加载房间列表
const loadAllRoomByDate = async () => {
  if (!searchIn.value || !searchOut.value)
    return ElMessage.warning("请选择完整的日期范围");
  loading.value = true;
  try {
    const res = await request.get("/reception/room/dateList", {
      params: { inDate: searchIn.value, outDate: searchOut.value },
    });
    allRoomList.value = res.data;
  } finally {
    loading.value = false;
  }
};

// 选择房间（只根据 isBusy 判断）
const pickRoom = (room) => {
  if (room.isBusy) return;
  selectedRoom.value = room;
  guest.value.roomId = room.roomId;
};

// 下一步（含表单校验）
const next = async () => {
  if (step.value === 1) {
    if (!selectedRoom.value || selectedRoom.value.isBusy) {
      ElMessage.warning("请选择一个空闲房间");
      return;
    }
    step.value++;
  } else if (step.value === 2) {
    try {
      await formRef.value.validate(); // 触发校验
      step.value++; // 校验通过才跳转
    } catch {}
  }
};

// 上一步
const prev = () => step.value--;

// 提交预订
const submitCheckIn = async () => {
  const param = {
    ...guest.value,
    checkInDate: searchIn.value,
    preLeaveDate: searchOut.value,
  };
  try {
    const res = await request.post("/reception/checkin", param);
    if (res.code === 200) {
      ElMessage.success("预定成功");
      // 重置状态
      step.value = 1;
      selectedRoom.value = null;
      guest.value = {
        name: "",
        gender: "男",
        idCard: "",
        phone: "",
        nativePlace: "",
        company: "",
        occupation: "",
        reason: "",
        roomId: null,
      };
      // 刷新房间列表
      loadAllRoomByDate();
    } else {
      ElMessage.error(res.msg || "预订失败");
    }
  } catch (error) {
    ElMessage.error("提交失败，请稍后重试");
    console.error(error);
  }
};

// ---------- 生命周期：初始化日期 ----------
onMounted(() => {
  const now = new Date();
  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);
  const start = now.toISOString().slice(0, 10);
  const end = tomorrow.toISOString().slice(0, 10);
  dateRange.value = [start, end];
  searchIn.value = start;
  searchOut.value = end;
  loadAllRoomByDate();
});
</script>

<style scoped>
.room-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}
.room-card {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 12px;
  text-align: center;
  cursor: pointer;
}
.room-card.selected {
  border: 2px solid #409eff;
  background: #e8f3ff;
}
.room-card.disable {
  background: #fff2e6;
  cursor: not-allowed;
}
.tip {
  color: #ff6000;
  font-weight: bold;
}
</style>
