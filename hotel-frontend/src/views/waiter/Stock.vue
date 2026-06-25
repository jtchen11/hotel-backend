<template>
  <div class="stock-page">
    <div class="page-header">
      <h2>库房管理</h2>
      <p>物资库存管理 · 入库出库登记 · 菜品库存补货</p>
    </div>

    <el-row :gutter="20">
      <!-- 左侧：物资管理 -->
      <el-col :xs="24" :sm="24" :md="16" :lg="16">
        <el-card shadow="hover" class="left-card">
          <div class="action-bar">
            <el-input
                v-model="name"
                placeholder="输入物品名称搜索"
                clearable
                class="search-input"
                @keyup.enter="load"
            />
            <el-button type="primary" icon="Search" @click="load">搜索</el-button>
            <el-button icon="Refresh" @click="name='';load()">重置</el-button>
            <div class="btn-group">
              <el-button type="success" icon="Plus" @click="inShow=true">入库</el-button>
              <el-button type="warning" icon="Minus" @click="outShow=true">出库</el-button>
            </div>
          </div>

          <el-table
              :data="list"
              border
              stripe
              size="default"
              :header-cell-style="{ background: '#f0f7ff', color: '#1f68b9' }"
          >
            <el-table-column prop="itemName" label="物品名称" min-width="120" />
            <el-table-column prop="category" label="分类" min-width="100" />
            <el-table-column prop="specification" label="规格" min-width="100" />
            <el-table-column prop="currentQuantity" label="当前库存" min-width="100">
              <template #default="{ row }">
                <span :class="row.currentQuantity <= row.warningLevel ? 'warn-text' : 'normal-text'">
                  {{ row.currentQuantity }} {{ row.unit }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="warningLevel" label="预警线" min-width="80" />
            <el-table-column prop="unitPrice" label="单价" min-width="100">
              <template #default="{ row }">¥ {{ row.unitPrice }}</template>
            </el-table-column>
            <el-table-column prop="updateTime" label="最后更新" width="180" />
          </el-table>
        </el-card>

        <!-- 出入库记录（上下布局） -->
        <el-card shadow="hover" class="record-card" style="margin-top: 20px;">
          <template #header>
            <div style="display:flex;justify-content:space-between;align-items:center;flex-wrap:wrap;gap:10px;">
              <span>📋 出入库记录</span>
              <div style="display:flex;gap:10px;align-items:center;flex-wrap:wrap;">
                <el-select
                    v-model="filterStockId"
                    placeholder="筛选物品"
                    clearable
                    filterable
                    size="small"
                    style="width:150px"
                    @change="loadRecords"
                >
                  <el-option
                      v-for="item in list"
                      :key="item.stockId"
                      :label="item.itemName"
                      :value="item.stockId"
                  />
                </el-select>
                <el-input
                    v-model="filterReceiver"
                    placeholder="领用人筛选"
                    clearable
                    size="small"
                    style="width:130px"
                    @input="applyFilters"
                />
                <el-select
                    v-model="filterDepartment"
                    placeholder="部门筛选"
                    clearable
                    filterable
                    size="small"
                    style="width:150px"
                    @change="applyFilters"
                >
                  <el-option
                      v-for="dept in departmentOptions"
                      :key="dept"
                      :label="dept"
                      :value="dept"
                  />
                </el-select>
              </div>
            </div>
          </template>

          <!-- 入库记录（上） -->
          <div style="margin-bottom:20px;">
            <div style="font-weight:bold;color:#67c23a;margin-bottom:10px;">
              📥 入库记录（{{ filteredInRecords.length }}条）
              <el-button size="small" type="text" @click="filterStockId=null;filterReceiver='';filterDepartment='';loadRecords()" style="margin-left:10px;">
                重置筛选
              </el-button>
            </div>
            <el-table :data="filteredInRecords" border stripe size="small" v-loading="recordLoading" max-height="300">
              <el-table-column prop="itemName" label="物品名称" min-width="100">
                <template #default="{ row }">
                  <el-link type="primary" @click="filterByItem(row.stockId)">
                    {{ row.itemName }}
                  </el-link>
                </template>
              </el-table-column>
              <el-table-column prop="quantity" label="数量" width="70" />
              <el-table-column label="单价" width="80">
                <template #default="{ row }">¥{{ row.unitPrice }}</template>
              </el-table-column>
              <el-table-column label="总金额" width="90">
                <template #default="{ row }">¥{{ row.totalAmount }}</template>
              </el-table-column>
              <el-table-column prop="supplier" label="供应商" min-width="80" />
              <el-table-column prop="operator" label="操作员" width="80" />
              <el-table-column label="时间" width="150">
                <template #default="{ row }">{{ formatTime(row.time) }}</template>
              </el-table-column>
              <el-table-column prop="remark" label="备注" min-width="80" />
            </el-table>
            <div v-if="filteredInRecords.length === 0" style="text-align:center;color:#909399;padding:10px;">
              暂无入库记录
            </div>
          </div>

          <!-- 出库记录（下） -->
          <div>
            <div style="font-weight:bold;color:#E6A23C;margin-bottom:10px;">
              📤 出库记录（{{ filteredOutRecords.length }}条）
            </div>
            <el-table :data="filteredOutRecords" border stripe size="small" v-loading="recordLoading" max-height="300">
              <el-table-column prop="itemName" label="物品名称" min-width="100">
                <template #default="{ row }">
                  <el-link type="primary" @click="filterByItem(row.stockId)">
                    {{ row.itemName }}
                  </el-link>
                </template>
              </el-table-column>
              <el-table-column prop="quantity" label="数量" width="70" />
              <el-table-column prop="receiver" label="领用人" min-width="80">
                <template #default="{ row }">
                  <el-link type="primary" @click="filterByReceiver(row.receiver)">
                    {{ row.receiver }}
                  </el-link>
                </template>
              </el-table-column>
              <el-table-column prop="department" label="部门" min-width="80">
                <template #default="{ row }">
                  <el-link type="primary" @click="filterByDepartment(row.department)">
                    {{ row.department }}
                  </el-link>
                </template>
              </el-table-column>
              <el-table-column prop="purpose" label="用途" min-width="80" />
              <el-table-column prop="operator" label="操作员" width="80" />
              <el-table-column label="时间" width="150">
                <template #default="{ row }">{{ formatTime(row.time) }}</template>
              </el-table-column>
            </el-table>
            <div v-if="filteredOutRecords.length === 0" style="text-align:center;color:#909399;padding:10px;">
              暂无出库记录
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：预警 + 菜品库存（保持不变） -->
      <el-col :xs="24" :sm="24" :md="8" :lg="8">
        <!-- 库存预警卡片 -->
        <el-card shadow="hover" class="warning-card" style="margin-bottom: 20px">
          <template #header>
            <span>⚠️ 库存预警（低于预警线）</span>
          </template>
          <el-table
              v-if="warningList.length > 0"
              :data="warningList"
              border
              size="small"
              style="width:100%"
          >
            <el-table-column prop="itemName" label="物品名称" />
            <el-table-column prop="currentQuantity" label="当前库存" />
            <el-table-column prop="warningLevel" label="预警线" />
            <el-table-column label="操作" width="80">
              <template #default="{ row }">
                <el-button size="small" type="primary" @click="quickIn(row)">入库</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div v-else style="text-align:center;padding:30px 0;color:#67c23a;font-size:16px;">
            ✅ 所有库存正常
          </div>
        </el-card>

        <!-- 菜品库存管理 -->
        <el-card shadow="hover" class="menu-stock-card">
          <template #header>
            <span>🍽️ 菜品库存管理</span>
          </template>
          <el-table :data="menuList" size="small" border max-height="600">
            <el-table-column prop="name" label="菜品名" width="100" />
            <el-table-column prop="stockQuantity" label="当前库存" width="80">
              <template #default="{ row }">
                <span :class="row.stockQuantity <= 0 ? 'warn-text' : ''">{{ row.stockQuantity }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="60" />
            <el-table-column label="补货" width="150">
              <template #default="{ row }">
                <el-input-number v-model="row.addNum" :min="1" size="small" style="width: 80px" />
                <el-button size="small" type="primary" @click="addMenuStock(row)">增加</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 入库弹窗（保持不变） -->
    <el-dialog v-model="inShow" title="物品入库" width="550px">
      <div class="dialog-form">
        <div style="display: flex; gap: 8px; align-items: center;">
          <el-select v-model="inData.stockId" placeholder="选择已有物品" style="flex: 1" clearable>
            <el-option v-for="s in list" :key="s.stockId" :label="s.itemName" :value="s.stockId" />
          </el-select>
          <el-button type="primary" plain @click="showNewItemDialog = true">+ 新增物品</el-button>
        </div>
        <el-input-number v-model="inData.quantity" :min="1" placeholder="入库数量" style="width:100%" />
        <el-input v-model="inData.unitPrice" placeholder="入库单价" />
        <el-input v-model="inData.supplier" placeholder="供应商" />
        <el-input v-model="inData.remark" type="textarea" placeholder="备注（可选）" :rows="2" />
      </div>
      <template #footer>
        <el-button @click="inShow=false">取消</el-button>
        <el-button type="success" @click="doIn">确认入库</el-button>
      </template>
    </el-dialog>

    <!-- 新增物品弹窗（保持不变） -->
    <el-dialog v-model="showNewItemDialog" title="新增物品" width="500px">
      <el-form :model="newItem" label-width="80px">
        <el-form-item label="物品名称" required>
          <el-input v-model="newItem.itemName" />
        </el-form-item>
        <el-form-item label="分类" required>
          <el-select v-model="newItem.category" placeholder="请选择分类" allow-create filterable>
            <el-option v-for="cat in categoryOptions" :key="cat" :label="cat" :value="cat" />
          </el-select>
        </el-form-item>
        <el-form-item label="规格">
          <el-input v-model="newItem.specification" placeholder="如：500ml" />
        </el-form-item>
        <el-form-item label="单位" required>
          <el-input v-model="newItem.unit" placeholder="如：瓶、袋、个" />
        </el-form-item>
        <el-form-item label="预警阈值">
          <el-input-number v-model="newItem.warningLevel" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showNewItemDialog=false">取消</el-button>
        <el-button type="primary" @click="createAndSelect">保存并选中</el-button>
      </template>
    </el-dialog>

    <!-- 出库弹窗（改造版） -->
    <el-dialog v-model="outShow" title="物品出库" width="550px">
      <el-form :model="outData" :rules="outRules" ref="outFormRef" label-width="100px">
        <el-form-item label="出库物品" prop="stockId">
          <el-select v-model="outData.stockId" placeholder="请选择出库物品" style="width:100%">
            <el-option v-for="s in list" :key="s.stockId" :label="s.itemName" :value="s.stockId" />
          </el-select>
        </el-form-item>
        <el-form-item label="出库数量" prop="quantity">
          <el-input-number v-model="outData.quantity" :min="1" style="width:100%" />
        </el-form-item>
        <el-form-item label="领用人" prop="receiver">
          <el-select
              v-model="outData.receiver"
              filterable
              remote
              reserve-keyword
              placeholder="输入姓名搜索在职员工"
              :remote-method="searchEmployee"
              :loading="employeeLoading"
              style="width:100%"
              @change="onEmployeeSelect"
          >
            <el-option
                v-for="emp in employeeOptions"
                :key="emp.empId"
                :label="`${emp.empName} - ${emp.department}`"
                :value="emp.empName"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="领用部门" prop="department">
          <el-input v-model="outData.department" disabled placeholder="选择领用人后自动填充" />
        </el-form-item>
        <el-form-item label="用途" prop="purpose">
          <el-input v-model="outData.purpose" type="textarea" placeholder="请输入用途" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="outShow=false">取消</el-button>
        <el-button type="warning" @click="doOut">确认出库</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const name = ref('')
const list = ref([])
const warningList = ref([])
const menuList = ref([])
const inShow = ref(false)
const outShow = ref(false)
const showNewItemDialog = ref(false)

// ========== 出入库记录 ==========
const inRecords = ref([])
const outRecords = ref([])
const recordLoading = ref(false)
const filterStockId = ref(null)
const filterReceiver = ref('')
const filterDepartment = ref('')

// ========== 员工搜索 ==========
const employeeOptions = ref([])
const employeeLoading = ref(false)
const outFormRef = ref(null)

// 出库表单
const outData = ref({
  stockId: null,
  quantity: 1,
  receiver: '',
  department: '',
  purpose: ''
})
// 部门选项（从出库记录中自动提取）
const departmentOptions = computed(() => {
  const depts = outRecords.value
      .map(r => r.department)
      .filter(d => d && d.trim() !== '')
  return [...new Set(depts)].sort()
})
// 出库校验规则
const outRules = {
  stockId: [{ required: true, message: '请选择出库物品', trigger: 'change' }],
  quantity: [
    { required: true, message: '请输入出库数量', trigger: 'blur' },
    { type: 'number', min: 1, message: '数量必须大于0', trigger: 'blur' }
  ],
  receiver: [{ required: true, message: '请选择领用人', trigger: 'change' }],
  department: [{ required: true, message: '领用部门自动填充', trigger: 'change' }],
  purpose: [{ required: true, message: '请输入用途', trigger: 'blur' }]
}

// 入库表单数据
const inData = ref({
  stockId: null,
  quantity: 1,
  unitPrice: '',
  supplier: '',
  remark: ''
})

// 新增物品表单
const newItem = ref({
  itemName: '',
  category: '',
  specification: '',
  unit: '',
  warningLevel: 10
})

// 从现有物品中提取分类选项
const categoryOptions = computed(() => {
  const cats = list.value.map(item => item.category).filter(c => c && c.trim())
  return [...new Set(cats)]
})

// ========== 计算属性：过滤后的记录 ==========
const filteredInRecords = computed(() => {
  return inRecords.value
})

const filteredOutRecords = computed(() => {
  let result = outRecords.value
  // 按领用人筛选
  if (filterReceiver.value && filterReceiver.value.trim()) {
    const keyword = filterReceiver.value.trim().toLowerCase()
    result = result.filter(r => r.receiver && r.receiver.toLowerCase().includes(keyword))
  }
  // 按部门筛选
  if (filterDepartment.value && filterDepartment.value.trim()) {
    const keyword = filterDepartment.value.trim().toLowerCase()
    result = result.filter(r => r.department && r.department.toLowerCase().includes(keyword))
  }
  return result
})

// ========== 加载数据 ==========
const load = async () => {
  try {
    const res = await request.get('/warehouse/stock', { params: { itemName: name.value } })
    list.value = res.data || []
  } catch (err) {
    ElMessage.error('加载库存列表失败')
  }
}

const loadWarning = async () => {
  try {
    const res = await request.get('/warehouse/warning')
    warningList.value = res.data || []
  } catch (err) {
    ElMessage.error('加载预警列表失败')
  }
}

const loadMenuStocks = async () => {
  try {
    const res = await request.get('/warehouse/menuStocks')
    menuList.value = (res.data || []).map(m => ({ ...m, addNum: 1 }))
  } catch (err) {
    ElMessage.error('加载菜品库存失败')
  }
}

const loadRecords = async () => {
  recordLoading.value = true
  try {
    const params = {}
    if (filterStockId.value) params.stockId = filterStockId.value

    const res = await request.get('/warehouse/records', { params })
    if (res.code === 200) {
      const data = res.data
      inRecords.value = data.inRecords || []
      outRecords.value = data.outRecords || []
    } else {
      ElMessage.warning(res.msg || '加载记录失败')
      inRecords.value = []
      outRecords.value = []
    }
  } catch (err) {
    console.error('加载出入库记录失败:', err)
    ElMessage.error('加载出入库记录失败')
    inRecords.value = []
    outRecords.value = []
  } finally {
    recordLoading.value = false
  }
}

// 前端筛选（输入框变化时触发）
const applyFilters = () => {
  // 下拉选择不需要额外操作，v-model 自动响应
  console.log('当前筛选 - 部门:', filterDepartment.value)
}

// ========== 员工搜索 ==========
const searchEmployee = async (keyword) => {
  if (!keyword || keyword.trim() === '') {
    employeeOptions.value = []
    return
  }
  employeeLoading.value = true
  try {
    const res = await request.get('/employee/search', { params: { keyword: keyword.trim() } })
    employeeOptions.value = res.data || []
  } catch (err) {
    console.error('搜索员工失败:', err)
    employeeOptions.value = []
  } finally {
    employeeLoading.value = false
  }
}

const onEmployeeSelect = (val) => {
  const emp = employeeOptions.value.find(e => e.empName === val)
  if (emp) {
    outData.value.department = emp.department
  } else {
    outData.value.department = ''
  }
}

// ========== 快速入库 ==========
const quickIn = (row) => {
  inData.value.stockId = row.stockId
  inData.value.quantity = 1
  inData.value.unitPrice = ''
  inData.value.supplier = ''
  inData.value.remark = ''
  inShow.value = true
}

// ========== 新增物品 ==========
const createAndSelect = async () => {
  if (!newItem.value.itemName) return ElMessage.warning('物品名称不能为空')
  if (!newItem.value.category) return ElMessage.warning('请选择分类')
  if (!newItem.value.unit) return ElMessage.warning('请输入单位')
  try {
    const res = await request.post('/warehouse/addStock', newItem.value)
    const newStockId = res.data.stockId
    ElMessage.success('物品新增成功')
    await load()
    inData.value.stockId = newStockId
    showNewItemDialog.value = false
    newItem.value = { itemName: '', category: '', specification: '', unit: '', warningLevel: 10 }
  } catch (err) {
    ElMessage.error(err.response?.data?.msg || '新增失败')
  }
}

// ========== 入库 ==========
const doIn = async () => {
  if (!inData.value.stockId) return ElMessage.warning('请选择物品')
  if (!inData.value.unitPrice || parseFloat(inData.value.unitPrice) <= 0) {
    return ElMessage.warning('请输入有效的单价')
  }
  try {
    await request.post('/warehouse/stockIn', {
      stockId: inData.value.stockId,
      quantity: inData.value.quantity,
      unitPrice: parseFloat(inData.value.unitPrice),
      supplier: inData.value.supplier,
      remark: inData.value.remark
    })
    ElMessage.success('入库成功')
    inShow.value = false
    inData.value = { stockId: null, quantity: 1, unitPrice: '', supplier: '', remark: '' }
    load()
    loadWarning()
    loadRecords()
  } catch (err) {
    ElMessage.error(err.response?.data?.msg || '入库失败')
  }
}

// ========== 出库 ==========
const doOut = async () => {
  try {
    await outFormRef.value.validate()
  } catch {
    return
  }

  const stock = list.value.find(s => s.stockId === outData.value.stockId)
  if (!stock) return ElMessage.error('物品不存在')
  if (stock.currentQuantity < outData.value.quantity) {
    return ElMessage.warning(`库存不足！当前库存：${stock.currentQuantity} ${stock.unit}`)
  }

  const after = stock.currentQuantity - outData.value.quantity
  if (after <= stock.warningLevel) {
    try {
      await ElMessageBox.confirm(
          '出库后库存将低于预警线，是否继续？',
          '⚠️ 库存预警',
          { confirmButtonText: '继续', cancelButtonText: '取消', type: 'warning' }
      )
    } catch {
      return
    }
  }

  try {
    await request.post('/warehouse/stockOut', {
      stockId: outData.value.stockId,
      quantity: outData.value.quantity,
      receiver: outData.value.receiver,
      department: outData.value.department,
      purpose: outData.value.purpose
    })
    ElMessage.success('出库成功')
    outShow.value = false
    outData.value = { stockId: null, quantity: 1, receiver: '', department: '', purpose: '' }
    employeeOptions.value = []
    load()
    loadWarning()
    loadRecords()
  } catch (err) {
    ElMessage.error(err.response?.data?.msg || '出库失败')
  }
}

// ========== 菜品补货 ==========
const addMenuStock = async (menu) => {
  if (!menu.addNum || menu.addNum < 1) {
    return ElMessage.warning('补货数量必须大于0')
  }
  try {
    await request.post('/warehouse/menuStock/add', null, {
      params: { menuId: menu.menuId, addQuantity: menu.addNum }
    })
    ElMessage.success('补货成功')
    loadMenuStocks()
  } catch (err) {
    ElMessage.error('补货失败')
  }
}

// ========== 记录筛选快捷操作 ==========
const filterByItem = (stockId) => {
  filterStockId.value = stockId
  loadRecords()
  document.querySelector('.record-card')?.scrollIntoView({ behavior: 'smooth' })
}

const filterByReceiver = (receiver) => {
  filterReceiver.value = receiver
  // 清空部门筛选
  filterDepartment.value = ''
  ElMessage.info(`已筛选领用人：${receiver}`)
}

const filterByDepartment = (department) => {
  filterDepartment.value = department
  filterReceiver.value = ''
  ElMessage.info(`已筛选部门：${department}`)
}

// ========== 工具方法 ==========
const formatTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getMonth()+1}/${date.getDate()} ${date.getHours().toString().padStart(2,'0')}:${date.getMinutes().toString().padStart(2,'0')}`
}

// ========== 生命周期 ==========
onMounted(() => {
  load()
  loadWarning()
  loadMenuStocks()
  loadRecords()
})
</script>

<style scoped>
.stock-page {
  padding: 24px 32px;
  background: #f5f7fa;
  min-height: 100vh;
}
.page-header {
  margin-bottom: 24px;
}
.page-header h2 {
  font-size: 24px;
  font-weight: bold;
  color: #2d4a77;
  margin: 0;
}
.page-header p {
  color: #909399;
  margin-top: 6px;
}
.left-card {
  border-radius: 16px;
  overflow: hidden;
}
.action-bar {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #eef2f6;
}
.search-input {
  width: 260px;
}
.btn-group {
  margin-left: auto;
  display: flex;
  gap: 10px;
}
.warning-card,
.menu-stock-card {
  border-radius: 16px;
  overflow: hidden;
}
.warning-card :deep(.el-card__header),
.menu-stock-card :deep(.el-card__header) {
  background-color: #fafbfc;
  border-bottom: 1px solid #eef2f6;
}
.record-card :deep(.el-card__header) {
  background-color: #fafbfc;
  border-bottom: 1px solid #eef2f6;
}
.dialog-form {
  display: flex;
  flex-direction: column;
  gap: 18px;
}
.warn-text {
  color: #f56c6c;
  font-weight: bold;
}
.normal-text {
  color: #00c48c;
  font-weight: bold;
}
:deep(.el-table) {
  --el-table-row-hover-bg-color: #f8fafc;
}
:deep(.el-link) {
  cursor: pointer;
  font-weight: 500;
}
</style>