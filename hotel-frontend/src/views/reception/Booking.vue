<template>
  <div class="booking-page">
    <!-- 标题 + 模式切换 -->
    <div class="page-header">
      <h2>创建预订</h2>
      <el-segmented v-model="mode" :options="[
        { label: '散客', value: 'individual' },
        { label: '团队', value: 'team' },
      ]" size="small" />
    </div>

    <div class="booking-body">
      <!-- 左栏：日期 + 房型 -->
      <div class="left-col">
        <div class="section-title">入住日期</div>
        <el-date-picker v-model="dateRange" type="daterange" value-format="YYYY-MM-DD"
          range-separator="至" start-placeholder="入住" end-placeholder="离店"
          @change="onDateChange" style="width:100%;margin-bottom:24px" />

        <div class="section-title">选择房型</div>
        <div v-loading="loading" class="type-list">
          <div v-for="t in typeList" :key="t.id" class="type-card"
            :class="{ selected: selectedType?.id === t.id }" @click="pickType(t)">
            <div class="type-name">{{ t.name }}</div>
            <div class="type-meta">
              <span class="type-price">¥{{ t.price }}</span>
              <span class="type-count">余 {{ t.availableCount }} 间</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右栏：表单 -->
      <div class="right-col">
        <template v-if="mode === 'individual'">
          <!-- 价格预览 -->
          <div class="price-preview" v-if="selectedType">
            <div class="price-label">费用预览</div>
            <div class="price-row"><span>房型</span><span>{{ selectedType.name }}</span></div>
            <div class="price-row"><span>单价</span><span>¥{{ selectedType.price }}/晚</span></div>
            <div class="price-row"><span>天数</span><span>{{ nights }} 晚</span></div>
            <div class="price-row total"><span>预估总价</span><span>¥{{ totalPrice }}</span></div>
          </div>
          <div class="section-title">客人信息</div>
          <el-form :model="form" :rules="rules" ref="formRef" label-width="0" size="large">
            <el-form-item prop="guestName"><el-input v-model="form.guestName" placeholder="客人姓名 *" /></el-form-item>
            <el-form-item prop="phone"><el-input v-model="form.phone" placeholder="联系电话 *" /></el-form-item>
            <el-form-item><el-input v-model="form.remark" placeholder="备注（选填）" type="textarea" :rows="3" /></el-form-item>
          </el-form>
        </template>

        <template v-else>
          <div class="section-title">团队信息</div>
          <el-form :model="teamForm" :rules="teamRules" ref="teamFormRef" size="large">
            <el-form-item prop="groupName"><el-input v-model="teamForm.groupName" placeholder="团队名称" /></el-form-item>
            <el-form-item prop="groupLeader"><el-input v-model="teamForm.groupLeader" placeholder="领队姓名" /></el-form-item>
            <el-form-item prop="leaderPhone"><el-input v-model="teamForm.leaderPhone" placeholder="联系电话" /></el-form-item>
            <el-form-item label="房间数" label-width="60px">
              <el-input-number v-model="teamRoomCount" :min="1" :max="selectedType?.availableCount || 99" style="width:100%" />
            </el-form-item>
          </el-form>
        </template>

        <!-- 提交 -->
        <el-button type="primary" size="large" style="width:100%;margin-top:16px"
          :loading="submitting" :disabled="!selectedType"
          @click="mode === 'individual' ? submitBooking() : submitTeamBooking()">
          {{ mode === 'individual' ? '确认预订' : '确认团队预订' }}
        </el-button>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, computed, onMounted } from "vue"
import { useRouter } from "vue-router"
import request from "@/utils/request"
import { ElMessage } from "element-plus"

const router = useRouter()
const mode = ref("individual")
const loading = ref(false)
const submitting = ref(false)
const typeList = ref([])
const selectedType = ref(null)
const formRef = ref(null)
const teamFormRef = ref(null)

const today = new Date().toISOString().slice(0, 10)
const tomorrow = new Date(Date.now() + 86400000).toISOString().slice(0, 10)
const dateRange = ref([today, tomorrow])

// Individual
const form = ref({ guestName: "", phone: "", remark: "" })
const rules = {
  guestName: [{ required: true, message: "请输入客人姓名", trigger: "blur" }],
  phone: [{ required: true, message: "请输入联系电话", trigger: "blur" }, { pattern: /^1[3-9]\d{9}$/, message: "手机号格式不正确（11位）", trigger: "blur" }],
}

// Team
const teamForm = ref({ groupName: "", groupLeader: "", leaderPhone: "" })
const teamRoomCount = ref(1)
const teamRules = {
  groupName: [{ required: true, message: "请输入团队名称", trigger: "blur" }],
  groupLeader: [{ required: true, message: "请输入领队姓名", trigger: "blur" }],
  leaderPhone: [{ required: true, message: "请输入联系电话", trigger: "blur" }],
}

const nights = computed(() => {
  if (!dateRange.value?.[0] || !dateRange.value?.[1]) return 0
  const d1 = new Date(dateRange.value[0]), d2 = new Date(dateRange.value[1])
  return Math.max(1, Math.round((d2 - d1) / 86400000))
})

const totalPrice = computed(() => {
  if (!selectedType.value) return 0
  return selectedType.value.price * nights.value
})

const loadTypes = async () => {
  if (!dateRange.value?.[0] || !dateRange.value?.[1]) return
  loading.value = true
  try {
    const res = await request.get("/reception/room/typeList", {
      params: { inDate: dateRange.value[0], outDate: dateRange.value[1] },
    })
    typeList.value = res.data || []
  } finally { loading.value = false }
}

const onDateChange = () => { selectedType.value = null; loadTypes() }
const pickType = (t) => { selectedType.value = t }

const submitBooking = async () => {
  try { await formRef.value.validate() } catch { return }
  submitting.value = true
  try {
    const res = await request.post("/reception/booking", {
      roomType: selectedType.value.name, guestName: form.value.guestName,
      phone: form.value.phone, inDate: dateRange.value[0], outDate: dateRange.value[1],
      remark: form.value.remark,
    })
    if (res.code === 200) {
      ElMessage.success(`预订成功，房间号：${res.data.roomNumber}`)
      router.push("/reception")
    } else { ElMessage.error(res.msg || "预订失败") }
  } catch { ElMessage.error("提交失败") }
  finally { submitting.value = false }
}

const submitTeamBooking = async () => {
  try { await teamFormRef.value.validate() } catch { return }
  if (!selectedType.value) { ElMessage.warning("请选择房型"); return }
  submitting.value = true
  try {
    const res = await request.post("/reception/teamBooking", {
      groupName: teamForm.value.groupName, groupLeader: teamForm.value.groupLeader,
      leaderPhone: teamForm.value.leaderPhone, roomType: selectedType.value.name,
      roomCount: teamRoomCount.value, inDate: dateRange.value[0], outDate: dateRange.value[1],
    })
    if (res.code === 200) {
      ElMessage.success(`团队预订成功，房间：${res.data.roomNumbers.join("、")}`)
      router.push("/reception")
    } else { ElMessage.error(res.msg || "预订失败") }
  } catch { ElMessage.error("提交失败") }
  finally { submitting.value = false }
}

onMounted(loadTypes)
</script>

<style scoped>
.booking-page { padding: 24px; min-height: calc(100vh - 100px); }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.page-header h2 { margin: 0; font-size: 22px; font-weight: 600; }

.booking-body { display: flex; gap: 32px; align-items: stretch; min-height: calc(100vh - 180px); }
.left-col { width: 360px; flex-shrink: 0; }
.right-col { flex: 1; display: flex; flex-direction: column; }

.section-title { font-size: 13px; color: #909399; margin-bottom: 8px; font-weight: 500; }

.type-list { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; }
.type-card {
  border: 1px solid #e8e8e8; border-radius: 10px; padding: 14px;
  cursor: pointer; transition: all 0.2s; background: #fafafa;
}
.type-card:hover { border-color: #c0a080; box-shadow: 0 2px 8px rgba(0,0,0,0.06); }
.type-card.selected { border-color: #c0a080; background: #f5f0eb; }
.type-name { font-size: 15px; font-weight: 600; margin-bottom: 6px; }
.type-meta { display: flex; justify-content: space-between; font-size: 14px; }
.type-price { color: #b8860b; font-weight: 600; }
.type-count { color: #909399; }

.total-row { display: flex; justify-content: space-between; align-items: center; padding: 12px 0; border-top: 1px solid #eee; margin-top: 12px; }
.total-label { font-size: 14px; color: #666; }
.total-price { font-size: 22px; font-weight: bold; color: #f56c6c; }

.price-preview { background: #f5f0eb; border-radius: 10px; padding: 16px; margin-bottom: 20px; }
.price-label { font-weight: 600; font-size: 15px; margin-bottom: 10px; color: #5a4a3a; }
.price-row { display: flex; justify-content: space-between; font-size: 14px; padding: 4px 0; color: #666; }
.price-row.total { border-top: 1px dashed #d4c5b3; margin-top: 6px; padding-top: 8px; font-weight: 600; color: #b8860b; font-size: 16px; }
</style>
