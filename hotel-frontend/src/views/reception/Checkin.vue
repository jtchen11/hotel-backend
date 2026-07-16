<template>
  <div class="checkin-layout">
    <div class="left-panel">
      <el-card style="margin-bottom:12px">
        <el-segmented v-model="mode" :options="[
          { label: '散客入住', value: 'individual' },
          { label: '团队入住', value: 'team' },
        ]" @change="onModeChange" style="width:100%" />
      </el-card>

      <el-card v-if="mode === 'individual'">
        <template #header><span style="font-weight:600">关联预订</span></template>
        <div style="display:flex;gap:8px;margin-bottom:8px">
          <el-input v-model="searchKeyword" placeholder="姓名/电话搜索" clearable @input="onSearchInput" />
          <el-button type="primary" @click="searchBooking" :loading="searching" size="small">搜索</el-button>
        </div>
        <div v-if="bookingList.length > 0" class="booking-scroll">
          <div v-for="b in bookingList" :key="b.guestId" class="booking-card"
            :class="{ active: selectedGuest?.guestId === b.guestId }" @click="selectBooking(b)">
            <div class="card-top"><span class="card-name">{{ b.name }}</span><el-tag size="small" type="warning">散客</el-tag></div>
            <div class="card-info">{{ b.roomNumber }} · {{ b.roomType }}</div>
            <div class="card-date">{{ formatDate(b.checkInDate) }} → {{ formatDate(b.preLeaveDate) }}</div>
          </div>
        </div>
        <div v-else-if="searched && bookingList.length === 0" style="color:#909399;font-size:13px">未找到预订记录</div>
      </el-card>

      <el-card v-else>
        <template #header><span style="font-weight:600">搜索团队预订</span></template>
        <div style="display:flex;gap:8px;margin-bottom:8px">
          <el-input v-model="teamSearchKeyword" placeholder="团名/领队电话" clearable @input="onTeamSearchInput" />
          <el-button type="primary" @click="searchTeamBooking" :loading="searching" size="small">搜索</el-button>
        </div>
        <div v-if="teamBookingList.length > 0" class="booking-scroll">
          <div v-for="group in teamBookingList" :key="group.groupId" class="booking-card" style="padding:0">
            <div class="team-group-header" @click="selectTeamGroup(group)">
              <el-tag size="small" type="success" style="margin-right:6px">团队</el-tag>
              <strong>{{ group.groupName }}</strong> <span style="margin-left:auto;color:#909399;font-size:12px">{{ group.rooms.length }}间</span>
            </div>
            <div v-if="selectedTeamGroup?.groupId === group.groupId" style="padding:8px">
              <div v-for="r in group.rooms" :key="r.guestId" class="booking-card"
                :class="{ active: teamSelectedGuest?.guestId === r.guestId }" @click="selectTeamBooking(r)">
                <div class="card-top"><span class="card-name">{{ r.roomNumber }}</span></div>
                <div class="card-info">{{ r.guestName }} · {{ r.roomType }}</div>
                <div class="card-date">{{ formatDate(r.checkInDate) }} → {{ formatDate(r.preLeaveDate) }}</div>
              </div>
            </div>
          </div>
        </div>
        <div v-else-if="teamSearched && teamBookingList.length === 0" style="color:#909399;font-size:13px">未找到团队预订</div>
      </el-card>
    </div>

    <div class="right-panel">
      <template v-if="mode === 'individual'">
        <el-card style="margin-bottom:16px">
          <template #header>
            <div style="display:flex;justify-content:space-between;align-items:center;flex-wrap:wrap;gap:8px">
              <span style="font-weight:600">选择房间</span>
              <div v-if="!selectedGuest" style="display:flex;gap:6px;align-items:center">
                <el-date-picker v-model="dateRange" type="daterange" value-format="YYYY-MM-DD" size="small" range-separator="至" start-placeholder="入住" end-placeholder="离店" style="width:250px" @change="onDateChange" />
              </div>
              <span v-else style="font-size:12px;color:#909399">{{ formatDate(selectedGuest.checkInDate) }} -> {{ formatDate(selectedGuest.preLeaveDate) }}</span>
            </div>
          </template>
          <div style="display:flex;gap:8px;margin-bottom:12px;flex-wrap:wrap">
            <el-tag size="small" v-for="f in floors" :key="f" :type="floorFilter === f ? 'primary' : 'info'"
              style="cursor:pointer" @click="floorFilter = floorFilter === f ? null : f">{{ f }}F</el-tag>
            <el-tag size="small" style="cursor:pointer" :type="!floorFilter ? 'primary' : 'info'" @click="floorFilter = null">全部</el-tag>
            <el-tag size="small" style="cursor:pointer" :type="showAllRooms ? 'primary' : 'info'" @click="showAllRooms = !showAllRooms">
              {{ showAllRooms ? '显示全部' : '只显示关联' }}
            </el-tag>
          </div>
          <div v-loading="loading" class="room-grid">
            <div v-for="r in displayRooms" :key="r.roomId" class="room-cell" :class="roomClass(r)" @click="clickRoom(r)">
              <div class="room-number">{{ r.roomNumber }}</div>
              <div class="room-type-tag">{{ r.roomType }}</div>
              <div class="room-price">¥{{ r.price }}</div>
              <el-tag :type="statusTag(r.status)" size="small" effect="plain">{{ r.status }}</el-tag>
            </div>
          </div>
        </el-card>
        <el-card style="margin-bottom:16px">
          <template #header><span style="font-weight:600">客人信息</span></template>
          <el-form :model="form" :rules="rules" ref="formRef" label-width="80px" size="small">
            <el-row :gutter="16">
              <el-col :span="8"><el-form-item label="姓名" prop="name"><el-input v-model="form.name" /></el-form-item></el-col>
              <el-col :span="8"><el-form-item label="身份证" prop="idCard"><el-input v-model="form.idCard" /></el-form-item></el-col>
              <el-col :span="8"><el-form-item label="电话" prop="phone"><el-input v-model="form.phone" /></el-form-item></el-col>
            </el-row>
            <el-form-item label="预计离店" prop="outDate">
              <el-date-picker v-model="form.outDate" type="date" value-format="YYYY-MM-DD" style="width:240px" />
            </el-form-item>
            <el-row :gutter="16">
              <el-col :span="8"><el-form-item label="性别"><el-radio-group v-model="form.gender"><el-radio value="男">男</el-radio><el-radio value="女">女</el-radio></el-radio-group></el-form-item></el-col>
              <el-col :span="8"><el-form-item label="籍贯"><el-input v-model="form.nativePlace" placeholder="选填" /></el-form-item></el-col>
              <el-col :span="8"><el-form-item label="职业"><el-input v-model="form.occupation" placeholder="选填" /></el-form-item></el-col>
            </el-row>
            <el-row :gutter="16">
              <el-col :span="12"><el-form-item label="工作单位"><el-input v-model="form.company" placeholder="选填" /></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="入住事由"><el-input v-model="form.reason" placeholder="选填" /></el-form-item></el-col>
            </el-row>
          </el-form>
        </el-card>
        <el-card style="margin-bottom:16px">
          <template #header><span style="font-weight:600">押金</span></template>
          <div style="display:flex;gap:16px;align-items:center">
            <span style="color:#909399;font-size:13px">建议 ¥500</span>
            <el-input-number v-model="depositAmount" :min="0" :precision="2" :step="100" size="small" />
            <el-select v-model="payMethod" size="small" style="width:140px">
              <el-option v-for="p in payOptions" :key="p.value" :label="p.label" :value="p.value" />
            </el-select>
          </div>
        </el-card>
        <div style="text-align:right">
          <el-button type="primary" size="large" :loading="submitting" :disabled="!selectedRoom" @click="submitCheckin">确认入住</el-button>
        </div>
      </template>

      <template v-else>
        <el-card style="margin-bottom:16px">
          <template #header>
            <span style="font-weight:600">选择房间</span>
            <span v-if="selectedTeamGroup" style="margin-left:8px;font-size:13px;color:#909399">{{ selectedTeamGroup.groupName }}（{{ selectedTeamGroup.rooms.length }}间）</span>
          </template>
          <div v-if="!selectedTeamGroup" style="padding:20px;text-align:center;color:#909399;font-size:14px">请在左侧选择团队</div>
          <div v-else>
            <div style="display:flex;gap:8px;margin-bottom:12px;flex-wrap:wrap">
              <el-radio-group v-model="roomFilter" size="small" @change="floorFilter = null">
                <el-radio-button value="all">全部</el-radio-button>
                <el-radio-button value="sameType">同房型</el-radio-button>
              </el-radio-group>
              <span style="flex:1"></span>
              <el-tag size="small" v-for="f in floors" :key="f" :type="floorFilter === f ? 'primary' : 'info'"
                style="cursor:pointer;margin-left:4px" @click="floorFilter = floorFilter === f ? null : f">{{ f }}F</el-tag>
            </div>
            <div class="room-grid">
              <div v-for="r in teamFilteredRooms" :key="r.roomId" class="room-cell" :class="teamRoomClass(r)" @click="clickTeamRoom(r)">
                <div class="room-number">{{ r.roomNumber }}</div>
                <div class="room-type-tag">{{ r.roomType }}</div>
                <div class="room-price">¥{{ r.price }}</div>
                <el-tag :type="statusTag(r.status)" size="small" effect="plain">{{ r.status }}</el-tag>
              </div>
            </div>
          </div>
        </el-card>
        <el-card v-if="teamSelectedGuest" style="margin-bottom:16px">
          <template #header><span style="font-weight:600">入住人 — {{ teamSelectedRoom?.roomNumber || teamSelectedGuest.roomNumber }}</span></template>
          <el-form :model="teamGuestForm" :rules="teamGuestRules" ref="teamGuestFormRef" label-width="80px" size="small">
            <el-row :gutter="16">
              <el-col :span="8"><el-form-item label="姓名" prop="name"><el-input v-model="teamGuestForm.name" /></el-form-item></el-col>
              <el-col :span="8"><el-form-item label="身份证" prop="idCard"><el-input v-model="teamGuestForm.idCard" /></el-form-item></el-col>
              <el-col :span="8"><el-form-item label="电话" prop="phone"><el-input v-model="teamGuestForm.phone" /></el-form-item></el-col>
            </el-row>
            <el-row :gutter="16">
              <el-col :span="8"><el-form-item label="性别"><el-radio-group v-model="teamGuestForm.gender"><el-radio value="男">男</el-radio><el-radio value="女">女</el-radio></el-radio-group></el-form-item></el-col>
              <el-col :span="8"><el-form-item label="籍贯"><el-input v-model="teamGuestForm.nativePlace" placeholder="选填" /></el-form-item></el-col>
              <el-col :span="8"><el-form-item label="职业"><el-input v-model="teamGuestForm.occupation" placeholder="选填" /></el-form-item></el-col>
            </el-row>
            <el-row :gutter="16">
              <el-col :span="12"><el-form-item label="工作单位"><el-input v-model="teamGuestForm.company" placeholder="选填" /></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="入住事由"><el-input v-model="teamGuestForm.reason" placeholder="选填" /></el-form-item></el-col>
            </el-row>
            <el-form-item label="预计离店" prop="outDate">
              <el-date-picker v-model="teamGuestForm.outDate" type="date" value-format="YYYY-MM-DD" style="width:240px" />
            </el-form-item>
            <el-form-item label="押金">
              <div style="display:flex;gap:12px;align-items:center">
                <el-input-number v-model="teamDepositAmount" :min="0" :precision="2" :step="100" size="small" />
                <el-select v-model="teamPayMethod" size="small" style="width:140px">
                  <el-option v-for="p in payOptions" :key="p.value" :label="p.label" :value="p.value" />
                </el-select>
                <el-button type="primary" size="small" :loading="submitting" :disabled="!teamSelectedGuest?.guestId" @click="submitTeamCheckin">确认入住</el-button>
              </div>
            </el-form-item>
          </el-form>
        </el-card>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue"
import { useRouter, useRoute } from "vue-router"
import request from "@/utils/request"
import { ElMessage } from "element-plus"
import { PAY_METHODS, PAY_METHOD_OPTIONS } from "@/constants/payment"

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const searching = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const mode = ref("individual")
const today = new Date().toISOString().slice(0, 10)
const payOptions = PAY_METHOD_OPTIONS

const searchKeyword = ref("")
const bookingList = ref([])
const selectedGuest = ref(null)
const searched = ref(false)
const rooms = ref([])
const selectedRoom = ref(null)
const floorFilter = ref(null)
const roomFilter = ref('all')
const showAllRooms = ref(true)
const dateRange = ref([new Date().toISOString().split('T')[0], new Date(Date.now() + 86400000).toISOString().split('T')[0]])
const form = ref({ name: "", idCard: "", phone: "", outDate: "", gender: "", nativePlace: "", company: "", occupation: "", reason: "", remark: "" })
const depositAmount = ref(500)
const payMethod = ref(PAY_METHODS.WECHAT)

const rules = {
  name: [{ required: true, message: "请输入姓名", trigger: "blur" }],
  idCard: [{ required: true, message: "请输入身份证号", trigger: "blur" }, { pattern: /^[1-9]\d{5}(18|19|20)?\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dXx]$/, message: "身份证号格式不正确（18位）", trigger: "blur" }],
  phone: [{ required: true, message: "请输入电话", trigger: "blur" }, { pattern: /^1[3-9]\d{9}$/, message: "手机号格式不正确（11位）", trigger: "blur" }],
  outDate: [{ required: true, message: "请选择离店日期", trigger: "change" }],
}

const floors = computed(() => [...new Set(rooms.value.map(r => r.floor))].sort())
const displayRooms = computed(() => {
  let list = rooms.value
  if (floorFilter.value) list = list.filter(r => r.floor === floorFilter.value)
  if (!showAllRooms.value && selectedGuest.value) {
    list = list.filter(r => r.roomType === selectedGuest.value.roomType)
  }
  return list
})

const formatDate = (d) => d ? d.slice(0, 10) : ""
const statusTag = (s) => ({ "空闲": "success", "占用": "danger", "维修中": "warning", "打扫中": "", "已锁": "info" }[s] || "info")

const roomClass = (r) => {
  const cls = []
  if (selectedRoom.value?.roomId === r.roomId) cls.push("room-selected")
  if (r.status == '维修中') cls.push('room-maintenance')
  else if (r.status == '打扫中') cls.push('room-cleaning')
  else if (!r.canSelect) cls.push('room-locked')
  else cls.push('room-free')
  return cls
}

const canSelectRoom = (r) => r.canSelect || (r.status == '已锁' && selectedGuest.value?.roomId === r.roomId)
const clickRoom = (r) => {
  if (!canSelectRoom(r)) { ElMessage.warning("该房间在所选日期不可用"); return }
  selectedRoom.value = r
}

const loadRooms = async (inD, outD) => {
  loading.value = true
  try {
    const params = { inDate: inD || dateRange.value[0], outDate: outD || dateRange.value[1] }
    const res = await request.get("/reception/room/dateList", { params })
    const selectedGuestId = selectedGuest.value?.guestId
    rooms.value = (res.data || []).map(r => ({ ...r, canSelect: (selectedGuestId && r.roomId === selectedGuest.value?.roomId) || (!r.isBusy && r.status != '维修中' && r.status != '打扫中') }))
  } finally { loading.value = false }
}

const onDateChange = () => {
  loadRooms()
}

let searchTimer = null
const onSearchInput = () => {
  if (searchTimer) clearTimeout(searchTimer)
  searchTimer = setTimeout(() => searchBooking(), 300)
}

const searchBooking = async () => {
  if (!searchKeyword.value) return
  searched.value = true; searching.value = true
  try {
    const res = await request.get("/reception/pendingBookings", { params: { keyword: searchKeyword.value, guestType: "散客" } })
    bookingList.value = res.data || []
  } finally { searching.value = false }
}

const selectBooking = (b) => {
  selectedGuest.value = b; selectedRoom.value = null; showAllRooms.value = false; roomFilter.value = 'sameType'
  dateRange.value = [formatDate(b.checkInDate), formatDate(b.preLeaveDate)]
  loadRooms(dateRange.value[0], dateRange.value[1])
  form.value.name = b.name; form.value.phone = b.phone || ""
  form.value.outDate = formatDate(b.preLeaveDate) || ""
}


const submitCheckin = async () => {
  if (!selectedRoom.value) { ElMessage.warning("请选择房间"); return }
  try { await formRef.value.validate() } catch { return }
  submitting.value = true
  try {
    const params = {
      roomId: selectedRoom.value.roomId, name: form.value.name, idCard: form.value.idCard,
      phone: form.value.phone, inDate: dateRange.value[0], outDate: form.value.outDate,
      depositAmount: depositAmount.value, payMethod: payMethod.value,
      gender: form.value.gender, nativePlace: form.value.nativePlace,
      company: form.value.company, occupation: form.value.occupation,
      reason: form.value.reason, remark: form.value.remark,
    }
    if (selectedGuest.value) params.guestId = selectedGuest.value.guestId
    const res = await request.post("/reception/checkin", params)
    if (res.code === 200) { ElMessage.success("入住成功"); router.push("/reception") }
    else { ElMessage.error(res.msg || "入住失败") }
  } catch { ElMessage.error("提交失败") }
  finally { submitting.value = false }
}

const teamSearchKeyword = ref("")
const teamBookingList = ref([])
const teamSearched = ref(false)
const selectedTeamGroup = ref(null)
const teamSelectedGuest = ref(null)
const teamSelectedRoom = ref(null)
const teamGuestFormRef = ref(null)
const teamGuestForm = ref({ name: "", idCard: "", phone: "", outDate: "", gender: "", nativePlace: "", company: "", occupation: "", reason: "" })
const teamDepositAmount = ref(500)
const teamPayMethod = ref(PAY_METHODS.WECHAT)

const teamGuestRules = {
  name: [{ required: true, message: "请输入姓名", trigger: "blur" }],
  idCard: [{ required: true, message: "请输入身份证号", trigger: "blur" }, { pattern: /^[1-9]\d{5}(18|19|20)?\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dXx]$/, message: "身份证号格式不正确（18位）", trigger: "blur" }],
  phone: [{ required: true, message: "请输入电话", trigger: "blur" }, { pattern: /^1[3-9]\d{9}$/, message: "手机号格式不正确（11位）", trigger: "blur" }],
  outDate: [{ required: true, message: "请选择离店日期", trigger: "change" }],
}

let teamSearchTimer = null
const onTeamSearchInput = () => {
  if (teamSearchTimer) clearTimeout(teamSearchTimer)
  teamSearchTimer = setTimeout(() => searchTeamBooking(), 300)
}

const searchTeamBooking = async () => {
  if (!teamSearchKeyword.value) return
  teamSearched.value = true; searching.value = true
  try {
    const res = await request.get("/reception/group/search", { params: { keyword: teamSearchKeyword.value } })
    teamBookingList.value = res.data || []
  } finally { searching.value = false }
}

const selectTeamGroup = (group) => {
  selectedTeamGroup.value = (selectedTeamGroup.value?.groupId === group.groupId) ? null : group
}

const selectTeamBooking = (b) => {
  teamSelectedGuest.value = b
  teamGuestForm.value = { name: "", idCard: "", phone: b.phone || "", outDate: formatDate(b.preLeaveDate) || "", gender: "", nativePlace: "", company: "", occupation: "", reason: "" }
}

const clickTeamRoom = (r) => {
  // 如果是预订时锁定的房间，允许选中
  if (teamSelectedGuest.value && r.roomId === teamSelectedGuest.value.roomId) {
    teamSelectedRoom.value = r; return
  }
  if (r.isBusy || r.status == "维修中" || r.status == "打扫中") { ElMessage.warning("该房间不可用"); return }
  teamSelectedRoom.value = r
}

const teamFilteredRooms = computed(() => {
  if (!selectedTeamGroup.value) return []
  const teamRoomType = selectedTeamGroup.value.rooms?.[0]?.roomType || ''
  let list = rooms.value
  if (floorFilter.value) list = list.filter(r => r.floor === floorFilter.value)
  if (roomFilter.value === 'sameType') {
    list = list.filter(r => r.roomType === teamRoomType)
  }
  return list
})

const teamRoomClass = (r) => {
  const cls = []
  if (r.status == '维修中') cls.push('room-maintenance')
  else if (r.status == '打扫中') cls.push('room-cleaning')
  else if (r.isBusy && !(teamSelectedGuest.value && r.roomId === teamSelectedGuest.value.roomId)) cls.push('room-locked')
  else cls.push('room-free')
  return cls
}

const submitTeamCheckin = async () => {
  if (!teamSelectedGuest.value) { ElMessage.warning("请选择房间"); return }
  try { await teamGuestFormRef.value.validate() } catch { return }
  submitting.value = true
  try {
    const roomId = teamSelectedRoom.value?.roomId || teamSelectedGuest.value?.roomId
    if (!roomId) { ElMessage.warning('请选择房间'); submitting.value = false; return }
    const res = await request.post("/reception/groupCheckinMember", {
      guestId: teamSelectedGuest.value.guestId, roomId: roomId,
      name: teamGuestForm.value.name, idCard: teamGuestForm.value.idCard,
      phone: teamGuestForm.value.phone, outDate: teamGuestForm.value.outDate,
      depositAmount: teamDepositAmount.value, payMethod: teamPayMethod.value,
      gender: teamGuestForm.value.gender, nativePlace: teamGuestForm.value.nativePlace,
      company: teamGuestForm.value.company, occupation: teamGuestForm.value.occupation,
      reason: teamGuestForm.value.reason,
    })
    if (res.code === 200) { ElMessage.success("入住成功"); teamSelectedGuest.value = null; searchTeamBooking() }
    else { ElMessage.error(res.msg || "入住失败") }
  } catch { ElMessage.error("提交失败") }
  finally { submitting.value = false }
}

const onModeChange = () => {
  teamSelectedGuest.value = null; selectedTeamGroup.value = null
  selectedGuest.value = null; selectedRoom.value = null
}

onMounted(async () => {
  await loadRooms()
  // 默认加载所有待入住预订
  searched.value = true
  try {
    const res = await request.get("/reception/pendingBookings", { params: { guestType: "散客" } })
    bookingList.value = res.data || []
  } catch {}
  // 加载团队预订列表
  teamSearched.value = true
  try {
    const res = await request.get("/reception/group/search", { params: { keyword: "" } })
    if (res.data) teamBookingList.value = res.data
  } catch {}

  const guestId = route.query.guestId
  const groupId = route.query.groupId
  if (guestId) {
    mode.value = "individual"
    const found = bookingList.value.find(b => b.guestId == guestId)
    if (found) selectBooking(found)
    bookingList.value = found ? [found] : []
  }
  if (groupId) {
    mode.value = "team"
    try {
      const res = await request.get("/reception/group/search", { params: { keyword: "" } })
      if (res.data) {
        teamBookingList.value = res.data
        const found = teamBookingList.value.find(g => g.groupId == groupId)
        if (found) {
          selectedTeamGroup.value = found
        }
      }
    } catch {}
  }
})
</script>

<style scoped>
.checkin-layout { display: flex; gap: 20px; padding: 20px; align-items: flex-start; }
.left-panel { width: 300px; flex-shrink: 0; }
.right-panel { flex: 1; min-width: 0; }
.booking-scroll { max-height: 400px; overflow-y: auto; margin-top: 8px; }
.booking-card { padding: 10px 12px; border: 1px solid #e4e7ed; border-radius: 8px; margin-bottom: 8px; cursor: pointer; transition: all 0.15s; }
.booking-card:hover { border-color: #409eff; background: #f5f7fa; }
.booking-card.active { border-color: #409eff; background: #ecf5ff; }
.card-top { display: flex; justify-content: space-between; align-items: center; margin-bottom: 4px; }
.card-name { font-weight: 500; font-size: 14px; }
.card-info { font-size: 12px; color: #606266; }
.card-date { font-size: 11px; color: #909399; margin-top: 2px; }
.team-group-header { display: flex; align-items: center; padding: 10px 12px; cursor: pointer; border-bottom: 1px solid #eee; }
.team-group-header:hover { background: #f5f7fa; }
.room-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(110px, 1fr)); gap: 8px; max-height: 400px; overflow-y: auto; }
.room-cell { border: 2px solid #e4e7ed; border-radius: 8px; padding: 10px 6px; text-align: center; cursor: pointer; transition: 0.15s; }
.room-cell:hover { box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
.room-number { font-size: 18px; font-weight: bold; }
.room-price { font-size: 12px; color: #f56c6c; margin: 4px 0; }
.room-free { border-color: #67c23a; background: #f0f9eb; }
.room-occupied { border-color: #f56c6c; background: #fef0f0; }
.room-maintenance { border-color: #e6a23c; background: #fdf6ec; }
.room-cleaning { border-color: #409eff; background: #ecf5ff; }
.room-locked { border-color: #b0b0b0; background: #fafafa; border-style: dashed; }
.room-selected { border-width: 3px; box-shadow: 0 0 0 2px rgba(64,158,255,0.3); }
.room-disabled { opacity: 0.5; cursor: not-allowed; }
</style>
