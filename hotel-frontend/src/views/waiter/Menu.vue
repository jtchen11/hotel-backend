<template>
  <div class="menu-container">
    <div class="header-card">
      <h2 class="title">餐饮菜单</h2>
      <div class="guest-select">
        <el-select
          v-model="selectedGuestId"
          filterable
          remote
          reserve-keyword
          placeholder="选择在住客人"
          :remote-method="searchGuest"
          :loading="guestLoading"
          style="width: 240px; height: 38px"
        >
          <el-option
            v-for="g in guestOptions"
            :key="g.guestId"
            :label="`${g.name} (${g.phone}) - ${g.roomNumber || '无房间'}`"
            :value="g.guestId"
          />
        </el-select>
      </div>
      <div class="filter-box">
        <el-select
          v-model="category"
          @change="getMenu"
          placeholder="菜品分类"
          style="width: 180px"
        >
          <el-option label="全部菜品" value="" />
          <el-option label="热菜" value="热菜" />
          <el-option label="凉菜" value="凉菜" />
          <el-option label="酒水" value="酒水" />
          <el-option label="饮品" value="饮品" />
          <el-option label="主食" value="主食" />
        </el-select>
      </div>
    </div>

    <div class="main-content">
      <div class="menu-grid-wrapper">
        <div class="menu-grid">
          <div
            class="food-card"
            v-for="item in list"
            :key="item.menuId"
            :class="{ 'sold-out': item.status === '售罄' }"
          >
            <div class="img-box">
              <img
                :src="item.image"
                alt=""
                @error="
                  $event.target.src =
                    'https://img0.baidu.com/it/u=2538667333,3829791212&fm=253&fmt=auto&app=138&f=JPEG'
                "
              />
            </div>
            <div class="info">
              <div class="name">{{ item.name }}</div>
              <div class="spec" v-if="item.unit">{{ item.unit }}</div>
              <div class="price">¥{{ cleanPrice(item.price) }} 元</div>
              <div class="desc" v-if="item.description">
                {{ item.description }}
              </div>
            </div>
            <div class="ctrl">
              <el-button
                size="small"
                :disabled="item.status === '售罄'"
                @click="item.count = item.count > 0 ? item.count - 1 : 0"
                >-</el-button
              >
              <span class="num-text">{{ item.count }}</span>
              <el-button
                size="small"
                :disabled="item.status === '售罄'"
                @click="item.count < 20 && item.count++"
                >+</el-button
              >
              <el-button
                type="primary"
                size="small"
                class="add-btn"
                :disabled="item.status === '售罄'"
                @click="addCart(item)"
                >点菜</el-button
              >
            </div>
          </div>
        </div>
      </div>

      <div class="cart-sidebar">
        <div class="cart-header">
          <span>已点菜单</span>
          <el-badge
            :value="totalItemCount"
            :hidden="totalItemCount === 0"
            class="item"
          />
        </div>
        <div class="cart-items" v-if="cart.length > 0">
          <div class="cart-item" v-for="(item, idx) in cart" :key="idx">
            <div class="item-name">{{ item.name }}</div>
            <div class="item-price">¥{{ cleanPrice(item.price) }} 元</div>
            <div class="item-ctrl">
              <el-button size="mini" @click="updateQuantity(item, -1)"
                >-</el-button
              >
              <span>{{ item.count }}</span>
              <el-button size="mini" @click="updateQuantity(item, 1)"
                >+</el-button
              >
              <el-button size="mini" type="danger" @click="removeItem(idx)"
                >删除</el-button
              >
            </div>
            <div class="item-subtotal">
              小计：¥{{ (cleanPrice(item.price) * item.count).toFixed(2) }} 元
            </div>
          </div>
        </div>
        <div class="cart-empty" v-else>暂无点菜</div>
        <div class="cart-footer">
          <div class="total-price">总价：¥{{ totalPrice }} 元</div>
          <el-button type="success" @click="submitOrder">确认点菜</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { ElMessage } from "element-plus";
import request from "@/utils/request";

const category = ref("");
const list = ref([]);
const cart = ref([]);

const selectedGuestId = ref(null);
const guestOptions = ref([]);
const guestLoading = ref(false);

const totalItemCount = computed(() => {
  return cart.value.reduce((sum, item) => sum + item.count, 0);
});

const totalPrice = computed(() => {
  return cart.value
    .reduce((sum, item) => sum + cleanPrice(item.price) * item.count, 0)
    .toFixed(2);
});

const searchGuest = async (query) => {
  if (!query) {
    guestOptions.value = [];
    return;
  }
  guestLoading.value = true;
  try {
    const res = await request.get("/guest/list", {
      params: { status: "在住", keyword: query },
    });
    guestOptions.value = res.data || [];
  } catch (error) {
    console.error(error);
  } finally {
    guestLoading.value = false;
  }
};

const getMenu = async () => {
  const res = await request.get("/dining/menu", {
    params: { category: category.value },
  });
  list.value = res.data.map((x) => ({ ...x, count: 0 }));
};

const cleanPrice = (p) => {
  if (!p) return 0;
  return parseFloat(String(p).replace(/[^0-9.]/g, ""));
};

const addCart = (item) => {
  if (item.count < 1) {
    ElMessage.warning("请先选择数量");
    return;
  }
  const exist = cart.value.find((i) => i.menuId === item.menuId);
  if (exist) {
    exist.count += item.count;
  } else {
    cart.value.push({
      menuId: item.menuId,
      name: item.name,
      price: cleanPrice(item.price),
      count: item.count,
    });
  }
  ElMessage.success(`已点菜：${item.name} x${item.count}`);
  item.count = 0;
};

const updateQuantity = (item, delta) => {
  const newCount = item.count + delta;
  if (newCount <= 0) {
    const idx = cart.value.findIndex((i) => i.menuId === item.menuId);
    if (idx !== -1) cart.value.splice(idx, 1);
  } else if (newCount <= 20) {
    item.count = newCount;
  }
};

const removeItem = (idx) => {
  cart.value.splice(idx, 1);
};

const submitOrder = async () => {
  if (!selectedGuestId.value) {
    ElMessage.warning("请先选择客人");
    return;
  }
  if (cart.value.length === 0) {
    ElMessage.warning("请先点菜");
    return;
  }
  const data = cart.value.map((item) => ({
    guestId: selectedGuestId.value,
    itemName: item.name,
    price: item.price,
    quantity: item.count,
  }));
  try {
    await request.post("/dining/order", data);
    ElMessage.success("点菜成功！");
    cart.value = [];
    list.value.forEach((x) => (x.count = 0));
  } catch (error) {
    ElMessage.error("点菜失败");
  }
};

onMounted(() => {
  getMenu();
});
</script>

<style scoped>
.menu-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 20px;
  box-sizing: border-box;
  background: #f5f7fa;
  overflow: hidden;
}

.header-card {
  background: #fff;
  border-radius: 14px;
  padding: 20px 25px;
  margin-bottom: 25px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 15px;
  flex-shrink: 0;
  box-shadow: 0 3px 12px rgba(0, 0, 0, 0.06);
}

.title {
  font-size: 24px;
  margin: 0;
  color: #303133;
}

.main-content {
  flex: 1;
  display: flex;
  gap: 20px;
  min-height: 0;
}

.menu-grid-wrapper {
  flex: 1;
  overflow-y: auto;
  padding-right: 5px;
}

.menu-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 18px;
}

.cart-sidebar {
  width: 320px;
  flex-shrink: 0;
  background: #fff;
  border-radius: 14px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  height: 100%;
  box-shadow: 0 3px 15px rgba(0, 0, 0, 0.07);
}

.cart-header {
  flex-shrink: 0;
  padding: 18px;
  border-bottom: 1px solid #eee;
  font-weight: bold;
  font-size: 16px;
}

.cart-items {
  flex: 1;
  overflow-y: auto;
  padding: 0 16px;
}

.cart-footer {
  flex-shrink: 0;
  padding: 18px;
  border-top: 1px solid #eee;
  text-align: center;
}

.cart-item {
  border-bottom: 1px solid #f0f0f0;
  padding: 12px 0;
}

.item-name {
  font-weight: 500;
  font-size: 14px;
}

.item-price {
  color: #f56c6c;
  font-size: 13px;
}

.item-ctrl {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 6px 0;
}

.item-subtotal {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.cart-empty {
  text-align: center;
  color: #909399;
  padding: 30px;
}

.total-price {
  margin-bottom: 12px;
  font-weight: bold;
  color: #f56c6c;
  font-size: 16px;
}

.food-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 3px 10px #0000000f;
  transition: all 0.25s ease;
}
.food-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
}
.img-box {
  width: 100%;
  height: 160px;
}
.img-box img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.info {
  padding: 12px;
}
.name {
  font-size: 15px;
  font-weight: 500;
}
.spec {
  font-size: 12px;
  color: #999;
  margin: 3px 0;
}
.desc {
  font-size: 12px;
  color: #666;
  margin: 3px 0;
}
.price {
  color: #f56c6c;
  font-weight: bold;
  margin-top: 6px;
  font-size: 15px;
}
.ctrl {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  border-top: 1px solid #eee;
}
.num-text {
  width: 26px;
  text-align: center;
}

/* 下拉框全局美化 */
:deep(.el-select__wrapper) {
  --el-select-input-height: 38px;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border: 1px solid #e4e7ed;
  transition: 0.2s;
}
:deep(.el-select__wrapper:hover) {
  border-color: #409eff;
  box-shadow: 0 2px 10px rgba(64, 158, 255, 0.15);
}
:deep(.el-select__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.12);
}
:deep(.el-select-dropdown) {
  border-radius: 12px;
  padding: 6px;
}
:deep(.el-select-dropdown__item) {
  border-radius: 8px;
  margin: 2px 4px;
  height: 36px;
}
.food-card.sold-out {
  opacity: 0.6;
  background: #f5f5f5;
}
</style>
