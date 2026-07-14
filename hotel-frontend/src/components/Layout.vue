<template>
  <el-container style="height: 100vh">
    <el-aside width="220px">
      <div class="logo">CPLee Hotel</div>
      <el-menu
        :default-active="$route.path"
        class="el-menu-vertical"
        background-color="#1a1a2e"
        text-color="#bfcbd9"
        active-text-color="#c8a27a"
        router
      >
        <el-menu-item v-for="item in menus" :key="item.path" :index="item.path">
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.title }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header>
        <div class="header-left">
          <span class="header-greeting">欢迎，{{ userStore.userInfo.empName }}</span>
          <el-tag size="small" effect="plain" style="margin-left:8px">{{ userStore.userInfo.role }}</el-tag>
        </div>
        <el-button class="logout-btn" size="small" @click="logout">退出登录</el-button>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/store/user";
import { ROLES } from "@/constants/roles";
import {
  House,
  User,
  Edit,
  Ticket,
  PieChart,
  Goods,
  Service,
  Money,
  DataAnalysis,
} from "@element-plus/icons-vue";

const router = useRouter();
const userStore = useUserStore();

// 根据角色动态生成菜单
const menus = computed(() => {
  const role = userStore.userInfo.role;
  if (role === ROLES.RECEPTION) {
    return [
      { path: "/reception/dashboard", title: "首页", icon: House },
      { path: "/reception/booking", title: "创建预订", icon: Edit },
      { path: "/reception/checkin", title: "入住办理", icon: User },
      { path: "/reception/roomStatus", title: "房态图", icon: House },
      { path: "/reception/messages", title: "留言管理", icon: Edit },
    ];
  } else if (role === ROLES.WAITER) {
    return [
      { path: "/waiter/dashboard", title: "首页", icon: House },
      { path: "/waiter/menu", title: "菜单点菜", icon: Goods },
      { path: "/waiter/ktv", title: "KTV管理", icon: Service },
      { path: "/waiter/unsettled", title: "历史账单", icon: Ticket },
      { path: "/waiter/stock", title: "库房管理", icon: Edit },
    ];
  } else if (role === ROLES.FINANCE) {
    return [
      { path: "/finance/dashboard", title: "首页", icon: House },
      { path: "/finance/bill", title: "客人账单", icon: Money }, // 新增
      { path: "/finance/employee", title: "员工管理", icon: House },
      { path: "/finance/attendance", title: "考勤管理", icon: Money },
      { path: "/finance/salary", title: "工资管理", icon: Ticket },
    ];
  } else if (role === ROLES.GM) {
    return [{ path: "/gm/statistics", title: "统计图表", icon: PieChart }];
  }
  return [];
});

const logout = () => {
  userStore.logout();
  router.push("/login");
};
</script>

<style scoped>
.logo {
  height: 64px;
  line-height: 64px;
  text-align: center;
  color: #c8a27a;
  font-size: 20px;
  font-weight: 600;
  letter-spacing: 2px;
  background-color: #151528;
  border-bottom: 1px solid rgba(200,162,122,0.15);
}
.el-menu-vertical:not(.el-menu--collapse) {
  width: 220px;
}
.el-aside {
  background-color: #1a1a2e;
}
.el-header {
  background-color: #ffffff;
  border-bottom: 1px solid #e6e9ef;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 56px;
}
.header-left {
  display: flex;
  align-items: center;
}
.header-greeting {
  font-size: 14px;
  color: #333;
}
.logout-btn {
  color: #999;
  border-color: #ddd;
}
.logout-btn:hover {
  color: #c8a27a;
  border-color: #c8a27a;
}
.el-main {
  background-color: #f5f5f5;
  padding: 24px;
}
/* 菜单激活项金色 */
.el-menu-item.is-active {
  background-color: rgba(200,162,122,0.1) !important;
  border-right: 3px solid #c8a27a;
}
</style>
