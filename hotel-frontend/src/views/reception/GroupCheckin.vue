<template>
  <div
    class="group-checkin-page"
    style="padding: 20px; max-width: 1300px; margin: 0 auto"
  >
    <h2 style="margin-bottom: 20px">团体预订</h2>
    <el-steps
      :active="step"
      finish-status="success"
      align-center
      style="margin-bottom: 30px"
    >
      <el-step title="选择房间(可多选)" />
      <el-step title="填写团体信息" />
      <el-step title="确认批量入住" />
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
      <el-button type="primary" @click="loadRoomByDate">筛选空闲房间</el-button>
    </div>

    <!-- 第一步：多选房间 -->
    <div v-if="step === 1">
      <div v-loading="loading" class="room-grid">
        <div
          v-for="item in allRoomList"
          :key="item.roomId"
          class="room-card"
          :class="{
            selected: selectRoomIds.includes(item.roomId),
            disable: item.isBusy,
          }"
          @click="toggleSelect(item)"
        >
          <div class="room-num">{{ item.roomNumber }}</div>
          <div class="room-type">{{ item.roomType }}</div>
          <div class="room-price">¥{{ item.price }}</div>
          <div v-if="item.isBusy" class="tip">已占用</div>
          <div v-else style="color: #67c23a">空闲</div>
        </div>
      </div>
      <div style="margin: 15px 0">已选房间：{{ selectRoomIds.length }}间</div>
      <div style="text-align: right">
        <el-button
          type="primary"
          :disabled="selectRoomIds.length === 0"
          @click="next"
          >下一步</el-button
        >
      </div>
    </div>

    <!-- 第二步：填写团体信息 -->
    <div v-else-if="step === 2">
      <el-form :model="group" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="团体名称" prop="groupName">
          <el-input v-model="group.groupName"></el-input>
        </el-form-item>
        <el-form-item label="领队姓名" prop="groupLeader">
          <el-input v-model="group.groupLeader"></el-input>
        </el-form-item>
        <el-form-item label="领队电话" prop="leaderPhone">
          <el-input v-model="group.leaderPhone"></el-input>
        </el-form-item>
        <el-form-item label="总人数" prop="personCount">
          <el-input v-model.number="group.personCount"></el-input>
        </el-form-item>
      </el-form>
      <div
        style="
          display: flex;
          gap: 10px;
          justify-content: right;
          margin-top: 20px;
        "
      >
        <el-button @click="prev">上一步</el-button>
        <el-button type="primary" @click="next">下一步</el-button>
      </div>
    </div>
    <!-- 第三步：确认单预览 -->
    <div v-else-if="step === 3">
      <div style="border: 1px solid #eee; padding: 20px; border-radius: 8px">
        <h3>团体入住确认单</h3>
        <p>团体名称：{{ group.groupName }}</p>
        <p>领队：{{ group.groupLeader }}</p>
        <p>领队电话：{{ group.leaderPhone }}</p>
        <p>总人数：{{ group.personCount }}人</p>
        <p>入住日期：{{ searchIn }}</p>
        <p>退房日期：{{ searchOut }}</p>
        <p>入住房间数：{{ selectRoomIds.length }}间</p>
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
        <el-button type="primary" @click="submitGroupCheckIn"
          >确认批量预订</el-button
        >
      </div>
    </div>
  </div>
</template>

<script>
import request from "@/utils/request";
export default {
  data() {
    return {
      step: 1,
      loading: false,
      allRoomList: [],
      selectRoomIds: [],
      searchIn: "",
      searchOut: "",
      dateRange: [],
      group: {
        groupName: "",
        groupLeader: "",
        leaderPhone: "",
        personCount: "",
        formRef: null,
        rules: {
          groupName: [
            { required: true, message: "请输入团体名称", trigger: "blur" },
          ],
          groupLeader: [
            { required: true, message: "请输入领队姓名", trigger: "blur" },
          ],
          leaderPhone: [
            { required: true, message: "请输入领队电话", trigger: "blur" },
            {
              pattern: /^1[3-9]\d{9}$/,
              message: "请输入正确的手机号",
              trigger: "blur",
            },
          ],
          personCount: [
            { required: true, message: "请输入总人数", trigger: "blur" },
            {
              type: "number",
              min: 1,
              message: "人数必须大于0",
              trigger: "blur",
            },
          ],
        },
      },
    };
  },
  mounted() {
    this.initDateRange();
    this.loadRoomByDate();
  },
  methods: {
    initDateRange() {
      const now = new Date();
      const next = new Date();
      next.setDate(next.getDate() + 1);
      const start = now.toISOString().slice(0, 10);
      const end = next.toISOString().slice(0, 10);
      this.dateRange = [start, end];
      this.searchIn = start;
      this.searchOut = end;
    },
    onDateRangeChange(val) {
      if (val && val.length === 2) {
        this.searchIn = val[0];
        this.searchOut = val[1];
      } else {
        this.searchIn = "";
        this.searchOut = "";
      }
    },
    async loadRoomByDate() {
      if (!this.searchIn || !this.searchOut)
        return this.$message.warning("请选择完整的日期范围");
      this.loading = true;
      const res = await request.get("/reception/room/dateList", {
        params: { inDate: this.searchIn, outDate: this.searchOut },
      });
      this.allRoomList = res.data;
      this.selectRoomIds = [];
      this.loading = false;
    },
    toggleSelect(room) {
      if (room.isBusy) return;
      let idx = this.selectRoomIds.indexOf(room.roomId);
      if (idx > -1) {
        this.selectRoomIds.splice(idx, 1);
      } else {
        this.selectRoomIds.push(room.roomId);
      }
    },
    next() {
      if (this.step === 2) {
        this.$refs.formRef.validate((valid) => {
          if (valid) {
            this.step++;
          }
        });
      } else {
        this.step++;
      }
    },
    prev() {
      this.step--;
    },
    async submitGroupCheckIn() {
      try {
        const param = {
          ...this.group,
          inDate: this.searchIn,
          outDate: this.searchOut,
          roomIdList: this.selectRoomIds,
        };
        const res = await request.post("/reception/groupcheckin", param);
        if (res.code === 200) {
          this.$message.success("团体批量遇到成功！");
          this.step = 1;
          this.selectRoomIds = [];
          this.group = {
            groupName: "",
            groupLeader: "",
            leaderPhone: "",
            personCount: "",
          };
          this.loadRoomByDate();
        } else {
          this.$message.error(res.msg);
        }
      } catch (err) {
        this.$message.error("入住失败：" + err.message);
        console.log(err);
      }
    },
  },
};
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
