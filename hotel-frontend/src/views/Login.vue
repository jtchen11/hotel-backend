<template>
  <div class="login-page">
    <!-- ===== 固定顶部栏（宝格丽风格 · 加高加大版） ===== -->
    <header class="header">
      <div class="header-left">
        <span class="header-phone">📞 +86 020-8888-6666</span>
      </div>
      <div class="header-center">
        <div class="brand-wrapper">
          <span class="brand-name">橙皮荔饭店</span>
          <span class="brand-sub">· 奢享城市静谧</span>
        </div>
        <span class="brand-sub-en">CPLee HOTEL</span>
      </div>
      <div class="header-right">
        <el-button class="login-btn" @click="showLoginDialog = true">
          <el-icon><User /></el-icon>
          <span>员工登录</span>
        </el-button>
      </div>
    </header>

    <!-- ===== 主内容 ===== -->
    <main class="main-content">
      <!-- 1. 外景轮播 -->
      <section class="section hero-section">
        <el-carousel
          height="640px"
          indicator-position="outside"
          arrow="always"
          :interval="5000"
        >
          <el-carousel-item v-for="(img, idx) in exteriorImages" :key="idx">
            <el-image
              :src="img"
              fit="cover"
              style="width: 100%; height: 640px"
              :preview-src-list="exteriorImages"
            />
          </el-carousel-item>
        </el-carousel>
      </section>

      <!-- ===== 2. 地理位置 ===== -->
      <section class="section location-section">
        <div class="location-content">
          <div class="location-text">
            <div class="section-label">— 地理位置 —</div>
            <h2 class="section-title">抵达橙皮荔</h2>
            <p class="text-body">
              饭店位于广东省广州市天河区珠江东路6号（广州周大福金融中心），<br />
              地处珠江新城核心区域，毗邻花城广场、广州塔。
            </p>
            <div class="location-info-grid">
              <div class="info-item">
                <span class="info-icon">✈️</span>
                <span>白云机场 40分钟车程</span>
              </div>
              <div class="info-item">
                <span class="info-icon">🚄</span>
                <span>广州东站 10分钟车程</span>
              </div>
              <div class="info-item">
                <span class="info-icon">📍</span>
                <span>珠江东路6号 · 周大福金融中心</span>
              </div>
              <div class="info-item">
                <span class="info-icon">📞</span>
                <span>咨询预订：+86 020-8888-6666</span>
              </div>
            </div>
            <div class="promo-banner">
              <span class="promo-icon">✨</span>
              <span class="promo-text"
                >即日起至12月31日，畅享低至五折住房餐饮优惠</span
              >
            </div>
          </div>
          <div class="location-map">
            <div class="map-container">
              <baidu-map
                class="bm-view"
                :center="{ lng: 113.325, lat: 23.12 }"
                :zoom="17"
                :scroll-wheel-zoom="true"
              >
                <bm-marker :position="{ lng: 113.325, lat: 23.12 }" />
              </baidu-map>
            </div>
          </div>
        </div>
      </section>

      <!-- ===== 3. 空间探索（楼层平面·轮播） ===== -->
      <section class="section explore-section">
        <div class="section-header">
          <span class="section-label">— 空间探索 —</span>
          <h2 class="section-title">楼层平面布局</h2>
        </div>
        <el-carousel
          class="explore-carousel"
          height="360px"
          indicator-position="outside"
          arrow="always"
          :interval="4000"
        >
          <el-carousel-item v-for="(floor, idx) in floorPlans" :key="idx">
            <div class="explore-item">
              <div class="explore-image">
                <el-image
                  :src="floor.image"
                  fit="cover"
                  style="width: 100%; height: 100%"
                  :preview-src-list="floorPlans.map((f) => f.image)"
                />
              </div>
              <div class="explore-text">
                <span class="explore-number">{{
                  floor.label.split(" ")[0]
                }}</span>
                <h3 class="explore-title">{{ floor.label }}</h3>
                <p class="explore-desc">{{ floor.description }}</p>
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>
      </section>

      <!-- ===== 4. 内部设施 ===== -->
      <section class="section facilities-section">
        <div class="section-header">
          <span class="section-label">— 设施体验 —</span>
          <h2 class="section-title">服务及设施</h2>
        </div>
        <div class="facilities-grid">
          <div
            v-for="(item, idx) in facilities"
            :key="idx"
            class="facility-card"
          >
            <div class="facility-carousel-wrapper">
              <el-carousel
                class="facility-carousel"
                height="300px"
                indicator-position="inside"
                arrow="always"
                :interval="4000"
              >
                <el-carousel-item
                  v-for="(img, imgIdx) in item.images"
                  :key="imgIdx"
                >
                  <el-image
                    :src="img"
                    fit="cover"
                    style="width: 100%; height: 300px"
                    :preview-src-list="item.images"
                  />
                </el-carousel-item>
              </el-carousel>
            </div>
            <div class="facility-info">
              <h4 class="facility-name">{{ item.name }}</h4>
              <p class="facility-desc">{{ item.desc }}</p>
            </div>
          </div>
        </div>
      </section>

      <!-- ===== 5. 菜肴精选 ===== -->
      <section class="section dishes-section">
        <div class="section-header">
          <span class="section-label">— 味觉盛宴 —</span>
          <h2 class="section-title">菜肴精选</h2>
          <p
            class="text-body"
            style="color: #909399; font-size: 14px; margin-top: 4px"
          >
            早餐厅每日 7:00–9:30 供应 · 其他时段可挂账点餐送餐到房
          </p>
        </div>
        <div class="dish-scroll-wrapper" v-loading="dishLoading">
          <div class="dish-scroll">
            <div v-for="(dish, idx) in dishList" :key="idx" class="dish-item">
              <el-image
                :src="dish.image || placeholderImg"
                fit="cover"
                style="
                  width: 180px;
                  height: 180px;
                  border-radius: 8px;
                  flex-shrink: 0;
                "
                :preview-src-list="
                  dishList.map((d) => d.image || placeholderImg)
                "
              />
              <span class="dish-name">{{ dish.name }}</span>
            </div>
          </div>
        </div>
      </section>

      <!-- 底部 -->
      <footer class="footer">
        <span>橙皮荔饭店 · 服务指南 </span>
        <span class="footer-phone">📞 +86 020-8888-6666</span>
      </footer>
    </main>

    <!-- ===== 登录弹窗 ===== -->
    <el-dialog
      v-model="showLoginDialog"
      title="员工登录"
      width="400px"
      :close-on-click-modal="true"
      class="login-dialog"
      destroy-on-close
    >
      <el-form :model="form" :rules="rules" ref="loginFormRef" label-width="0">
        <el-form-item prop="empName">
          <el-input
            v-model="form.empName"
            placeholder="用户名"
            prefix-icon="User"
            size="large"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            prefix-icon="Lock"
            size="large"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            style="width: 100%"
            @click="handleLogin"
            :loading="loading"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { User } from "@element-plus/icons-vue";
import { useUserStore } from "@/store/user";
import { login } from "@/api/login";
import request from "@/utils/request";

const router = useRouter();
const userStore = useUserStore();
const loginFormRef = ref();
const loading = ref(false);
const showLoginDialog = ref(false);

const form = reactive({
  empName: "",
  password: "",
});

const rules = {
  empName: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }],
};

const handleLogin = async () => {
  try {
    await loginFormRef.value.validate();
  } catch {
    return;
  }
  loading.value = true;
  try {
    const res = await login(form);
    if (res.code === 200) {
      const { token, userInfo } = res.data;
      userStore.setToken(token);
      userStore.setUserInfo(userInfo);
      ElMessage.success(`欢迎回来，${userInfo.empName}`);
      showLoginDialog.value = false;
      const roleHome = {
        前台接待员: "/reception",
        营业服务员: "/waiter",
        财务管理员: "/finance",
        总经理: "/gm",
      };
      router.push(roleHome[userInfo.role] || "/");
    } else {
      ElMessage.error(res.msg || "登录失败");
    }
  } catch (err) {
    console.error(err);
    ElMessage.error("登录异常，请稍后重试");
  } finally {
    loading.value = false;
  }
};

// ====== 服务指南数据 ======
const dishList = ref([]);
const dishLoading = ref(false);
const placeholderImg = "/images/placeholder.jpg";

const exteriorImages = [
  "/images/guide/exterior/exterior-1.jpg",
  "/images/guide/exterior/exterior-2.jpg",
  "/images/guide/exterior/exterior-3.jpg",
  "/images/guide/exterior/exterior-4.jpg",
  "/images/guide/exterior/exterior-5.jpg",
];

const floorPlans = [
  {
    label: "1F 大堂 · 早餐厅",
    image: "/images/guide/floors/floor-1f.jpg",
    description: "典雅大堂，每日 7:00–9:30 供应自助早餐，开启您的美好一天。",
  },
  {
    label: "2F 会议室",
    image: "/images/guide/floors/floor-2f.jpg",
    description: "多功能会议空间，可容纳 50 人，配备高清投影及视频会议系统。",
  },
  {
    label: "3F 标准客房",
    image: "/images/guide/floors/floor-3f.jpg",
    description: "舒适温馨的城市景观房，配备高品质床品及智能客房控制系统。",
  },
  {
    label: "4F 豪华客房",
    image: "/images/guide/floors/floor-4f.jpg",
    description: "宽敞明亮的江景客房，俯瞰珠江两岸璀璨夜景。",
  },
  {
    label: "5F 行政套房",
    image: "/images/guide/floors/floor-5f.jpg",
    description: "尊享行政礼遇，专属酒廊、私人管家服务，彰显非凡品味。",
  },
  {
    label: "6F KTV包房",
    image: "/images/guide/floors/floor-6f.jpg",
    description: "高端专业音响设备，私密优雅的聚会空间，尽享欢唱时光。",
  },
  {
    label: "7F 洗衣 · 健身",
    image: "/images/guide/floors/floor-7f.jpg",
    description:
      "24 小时自助洗衣服务及现代化健身中心，配备顶级有氧与力量训练设备。",
  },
  {
    label: "8F 赤醉酒吧",
    image: "/images/guide/floors/floor-8f.jpg",
    description:
      "顶层酒吧，俯瞰珠江新城璀璨夜景，精选全球经典鸡尾酒与珍酿威士忌。",
  },
];

const facilities = [
  {
    name: "赤醉酒吧",
    desc: "位于饭店顶层，俯瞰珠江新城璀璨夜景，精选全球经典鸡尾酒与珍酿威士忌，营造优雅私密的社交空间。",
    images: [
      "/images/guide/facilities/bar-1.jpg",
      "/images/guide/facilities/bar-2.jpg",
      "/images/guide/facilities/bar-3.jpg",
    ],
  },
  {
    name: "KTV 包房",
    desc: "配备顶级音响系统与智能点歌平台，拥有多种主题包房，是商务宴请、朋友聚会的理想之选。",
    images: [
      "/images/guide/facilities/ktv-1.jpg",
      "/images/guide/facilities/ktv-2.jpg",
      "/images/guide/facilities/ktv-3.jpg",
    ],
  },
  {
    name: "健身中心",
    desc: "24 小时开放的现代化健身空间，配备顶级有氧与力量训练设备，设有瑜伽室与专业健身指导。",
    images: [
      "/images/guide/facilities/gym-1.jpg",
      "/images/guide/facilities/gym-2.jpg",
      "/images/guide/facilities/gym-3.jpg",
    ],
  },
  {
    name: "会议中心",
    desc: "多规格会议厅与宴会场地，配备智能多媒体设备及专业会务服务，满足各类商务活动需求。",
    images: [
      "/images/guide/facilities/meeting-1.jpg",
      "/images/guide/facilities/meeting-2.jpg",
      "/images/guide/facilities/meeting-3.jpg",
    ],
  },
];

const loadDishes = async () => {
  dishLoading.value = true;
  try {
    const res = await request.get("/dining/menu");
    const dishes = (res.data || []).filter((d) => d.image).slice(0, 20);
    dishList.value = dishes.length ? dishes : [];
  } catch {
    dishList.value = [];
  } finally {
    dishLoading.value = false;
  }
};

onMounted(() => {
  loadDishes();
});
</script>

<style scoped>
/* ===== 全局重置 ===== */
.login-page {
  height: 100vh;
  overflow-y: auto !important;
  overflow-x: hidden !important;
  display: flex;
  flex-direction: column;
  background: #f8f6f3;
}

.main-content {
  flex: 1;
  overflow: visible !important;
  padding-bottom: 40px;
}

/* ===== 顶部栏（宝格丽风格 · 加高加大版） ===== */
.header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 60px;
  height: 100px; /* 加高 */
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(16px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: center;
  flex-shrink: 0;
  width: 200px;
}
.header-phone {
  font-size: 14px;
  color: #888;
  letter-spacing: 0.5px;
}

.header-center {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
}
.brand-wrapper {
  display: flex;
  align-items: baseline;
  gap: 8px;
}
.brand-name {
  font-size: 32px; /* 加大 */
  font-weight: 600;
  color: #1a2a3a;
  letter-spacing: 6px;
}
.brand-sub {
  font-size: 20px; /* 加大 */
  font-weight: 300;
  color: #999;
  letter-spacing: 3px;
}
.brand-sub-en {
  font-size: 14px; /* 加大 */
  letter-spacing: 6px;
  color: #bbb;
  font-weight: 300;
}

.header-right {
  display: flex;
  align-items: center;
  flex-shrink: 0;
  width: 200px;
  justify-content: flex-end;
}

.login-btn {
  background: transparent;
  border: 1.5px solid #1a2a3a;
  border-radius: 24px;
  padding: 10px 28px;
  color: #1a2a3a;
  font-size: 13px;
  font-weight: 500;
  transition: all 0.25s ease;
  display: flex;
  align-items: center;
  gap: 8px;
  letter-spacing: 0.5px;
}
.login-btn:hover {
  background: #1a2a3a;
  color: #fff;
}

/* ===== 区块通用 ===== */
.section {
  max-width: 1300px;
  margin: 0 auto;
  padding: 64px 40px;
}
.section:not(:last-child) {
  border-bottom: 1px solid #eee;
}

.section-label {
  font-size: 12px;
  letter-spacing: 3px;
  color: #c9a96e;
  text-transform: uppercase;
  font-weight: 500;
}
.section-title {
  font-size: 28px;
  font-weight: 300;
  color: #1a2a3a;
  margin: 8px 0 16px;
  letter-spacing: 2px;
}
.section-header {
  text-align: center;
  margin-bottom: 48px;
}
.text-body {
  font-size: 15px;
  line-height: 1.8;
  color: #555;
}

/* ===== 1. 外景轮播 ===== */
.hero-section {
  padding: 0;
  max-width: 100%;
  position: relative;
}
.hero-section :deep(.el-carousel) {
  height: 640px;
}
.hero-section :deep(.el-carousel__container) {
  height: 640px;
}
.hero-section :deep(.el-image) {
  height: 640px;
  display: block;
  width: 100%;
}

/* ===== 2. 地理位置 ===== */
.location-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 48px;
  align-items: center;
}
.location-text {
  padding-right: 20px;
}
.location-info-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin: 20px 0 24px;
}
.info-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  color: #555;
}
.info-icon {
  font-size: 18px;
  width: 28px;
}
.promo-banner {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  background: linear-gradient(135deg, #c9a96e, #b8943e);
  color: #fff;
  padding: 10px 20px;
  border-radius: 30px;
  font-size: 13px;
  font-weight: 500;
  letter-spacing: 0.5px;
  box-shadow: 0 4px 16px rgba(201, 169, 110, 0.35);
  margin-top: 8px;
}
.promo-icon {
  font-size: 16px;
}
.map-container {
  width: 100%;
  height: 320px;
  border-radius: 12px;
  overflow: hidden;
  background: #eaeaea;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
}
.bm-view {
  width: 100%;
  height: 100%;
}

/* ===== 3. 空间探索（轮播） ===== */
.explore-carousel {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.06);
}
.explore-carousel :deep(.el-carousel__container) {
  height: 360px;
}

.explore-item {
  display: grid;
  grid-template-columns: 1fr 1fr;
  height: 100%;
  background: #fff;
}
.explore-image {
  height: 100%;
  overflow: hidden;
}
.explore-image .el-image {
  width: 100%;
  height: 100%;
  display: block;
}
.explore-text {
  padding: 40px 36px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  background: #faf8f6;
}
.explore-number {
  font-size: 13px;
  letter-spacing: 2px;
  color: #c9a96e;
  font-weight: 500;
}
.explore-title {
  font-size: 20px;
  font-weight: 500;
  color: #1a2a3a;
  margin: 8px 0 12px;
  letter-spacing: 1px;
}
.explore-desc {
  font-size: 14px;
  line-height: 1.8;
  color: #777;
  margin-bottom: 16px;
}

/* ===== 4. 内部设施 ===== */
.facilities-section {
  padding: 64px 40px 80px;
}
.facilities-section .section-header {
  margin-bottom: 48px;
}
.facilities-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 32px;
}

.facility-card {
  background: transparent;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: none;
}

.facility-carousel-wrapper {
  width: 100%;
  overflow: hidden;
}
.facility-carousel {
  width: 100%;
}
.facility-carousel :deep(.el-carousel__container) {
  height: 450px;
}
.facility-carousel :deep(.el-image) {
  width: 100%;
  height: 450px;
  display: block;
}
.facility-carousel :deep(.el-carousel__arrow) {
  width: 32px;
  height: 32px;
  border-radius: 50%;
}
.facility-carousel :deep(.el-carousel__arrow--left) {
  left: 8px;
}
.facility-carousel :deep(.el-carousel__arrow--right) {
  right: 8px;
}

.facility-info {
  padding: 20px 20px 24px;
  background: transparent;
}
.facility-name {
  font-size: 18px;
  font-weight: 500;
  color: #1a2a3a;
  margin: 4px 0 8px;
}
.facility-desc {
  font-size: 13px;
  line-height: 1.8;
  color: #777;
  margin: 0;
}

/* ===== 5. 菜肴精选 ===== */
.dish-scroll-wrapper {
  width: 100%;
  overflow: hidden;
  position: relative;
}

.dish-scroll {
  display: flex;
  gap: 20px;
  overflow-x: auto;
  padding: 8px 4px 16px 4px;
  scroll-behavior: smooth;
  -webkit-overflow-scrolling: touch;
}
.dish-scroll::-webkit-scrollbar {
  height: 6px;
}
.dish-scroll::-webkit-scrollbar-track {
  background: #e8e8e8;
  border-radius: 4px;
}
.dish-scroll::-webkit-scrollbar-thumb {
  background: #c9a96e;
  border-radius: 4px;
}

.dish-item {
  flex: 0 0 auto;
  width: 180px;
  text-align: center;
}
.dish-item .el-image {
  border-radius: 8px;
  display: block;
}
.dish-name {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #2c2c2c;
  margin-top: 8px;
}

/* ===== 底部 ===== */
.footer {
  text-align: center;
  padding: 32px 40px 28px;
  font-size: 12px;
  color: #bbb;
  letter-spacing: 1px;
  border-top: 1px solid #eee;
  margin-top: 12px;
  display: flex;
  justify-content: center;
  gap: 40px;
}
.footer-phone {
  color: #999;
}

/* ===== 登录弹窗 ===== */
.login-dialog :deep(.el-dialog) {
  border-radius: 14px;
}
.login-dialog :deep(.el-dialog__header) {
  padding: 24px 24px 0;
}
.login-dialog :deep(.el-dialog__title) {
  font-size: 18px;
  font-weight: 500;
  color: #1a2a3a;
}
.login-dialog :deep(.el-dialog__body) {
  padding: 20px 24px 12px;
}
.login-dialog :deep(.el-dialog__footer) {
  display: none;
}
.login-dialog :deep(.el-input__wrapper) {
  border-radius: 8px;
}
.login-dialog :deep(.el-input__inner) {
  font-size: 14px;
}
.dialog-footer-hint {
  text-align: center;
  font-size: 12px;
  color: #bbb;
  padding: 6px 0 12px;
}

/* ===== 登录弹窗按钮 ===== */
.login-dialog :deep(.el-button--primary) {
  background-color: #1a2a3a !important;
  border-color: #1a2a3a !important;
}
.login-dialog :deep(.el-button--primary:hover) {
  background-color: #2a3a4a !important;
  border-color: #2a3a4a !important;
}
.login-dialog :deep(.el-button--primary:active) {
  background-color: #0a1a2a !important;
  border-color: #0a1a2a !important;
}

/* ===== 响应式 ===== */
@media (max-width: 1200px) {
  .facilities-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 24px;
  }
  .facility-carousel :deep(.el-carousel__container) {
    height: 320px;
  }
  .facility-carousel :deep(.el-image) {
    height: 320px;
  }
}

@media (max-width: 1024px) {
  .location-content {
    grid-template-columns: 1fr;
    gap: 32px;
  }
  .location-text {
    padding-right: 0;
  }
  .section {
    padding: 48px 24px;
  }
  .facilities-section {
    padding: 48px 24px 64px;
  }
  .header {
    padding: 0 32px;
    height: 80px;
  }
  .brand-name {
    font-size: 26px;
    letter-spacing: 4px;
  }
  .brand-sub {
    font-size: 16px;
  }
  .brand-sub-en {
    font-size: 12px;
    letter-spacing: 4px;
  }
  .header-phone {
    font-size: 12px;
  }
  .login-btn {
    padding: 8px 20px;
    font-size: 12px;
  }
  .header-left {
    width: 150px;
  }
  .header-right {
    width: 150px;
  }
}

@media (max-width: 768px) {
  .header {
    padding: 0 16px;
    height: 64px;
  }
  .header-left {
    width: auto;
  }
  .header-phone {
    font-size: 11px;
    display: none;
  }
  .brand-name {
    font-size: 18px;
    letter-spacing: 2px;
  }
  .brand-sub {
    font-size: 13px;
    display: none;
  }
  .brand-sub-en {
    font-size: 10px;
    letter-spacing: 2px;
  }
  .login-btn {
    padding: 6px 14px;
    font-size: 12px;
  }
  .login-btn span {
    display: none;
  }
  .header-right {
    width: auto;
  }
  .section {
    padding: 32px 16px;
  }
  .facilities-section {
    padding: 32px 16px 48px;
  }
  .facilities-section .section-header {
    margin-bottom: 32px;
  }
  .hero-section :deep(.el-carousel) {
    height: 320px;
  }
  .hero-section :deep(.el-carousel__container) {
    height: 320px;
  }
  .hero-section :deep(.el-image) {
    height: 320px;
  }
  .facilities-grid {
    grid-template-columns: 1fr 1fr;
    gap: 16px;
  }
  .facility-carousel :deep(.el-carousel__container) {
    height: 240px;
  }
  .facility-carousel :deep(.el-image) {
    height: 240px;
  }
  .facility-info {
    padding: 12px 14px 16px;
  }
  .facility-name {
    font-size: 15px;
  }
  .facility-desc {
    font-size: 12px;
    line-height: 1.6;
  }
  .explore-item {
    grid-template-columns: 1fr;
    grid-template-rows: 1fr 1fr;
  }
  .explore-carousel :deep(.el-carousel__container) {
    height: 500px;
  }
  .explore-text {
    padding: 20px 16px;
  }
  .explore-title {
    font-size: 17px;
  }
  .dish-item {
    width: 140px;
  }
  .dish-item .el-image {
    height: 140px !important;
    width: 140px !important;
  }
  .location-info-grid {
    gap: 8px;
  }
  .promo-banner {
    font-size: 12px;
    padding: 8px 16px;
    flex-wrap: wrap;
  }
  .map-container {
    height: 240px;
  }
  .footer {
    flex-direction: column;
    gap: 8px;
    padding: 24px 16px;
  }
}

@media (max-width: 480px) {
  .facilities-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  .facility-carousel :deep(.el-carousel__container) {
    height: 220px;
  }
  .facility-carousel :deep(.el-image) {
    height: 220px;
  }
}
</style>
