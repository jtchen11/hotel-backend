<template>
  <el-container style="height: 100vh">
    <el-aside width="220px" style="background-color: #304156">
      <div class="logo">饭店管理系统</div>
      <el-menu
        :default-active="$route.path"
        class="el-menu-vertical"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <el-menu-item v-for="item in menus" :key="item.path" :index="item.path">
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.title }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header
        style="
          background-color: #fff;
          border-bottom: 1px solid #e6e6e6;
          display: flex;
          justify-content: space-between;
          align-items: center;
        "
      >
        <span
          >欢迎，{{ userStore.userInfo.empName }} ({{
            userStore.userInfo.role
          }})</span
        >
        <el-button type="danger" size="small" @click="logout"
          >退出登录</el-button
        >
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
  if (role === "前台接待员") {
    return [
      { path: "/reception/dashboard", title: "首页", icon: House },
      { path: "/reception/checkin", title: "散客预订", icon: User },
      { path: "/reception/groupCheckin", title: "团体预订", icon: User },
      { path: "/reception/roomStatus", title: "房态图", icon: House },
      { path: "/reception/messages", title: "留言管理", icon: Edit },
    ];
  } else if (role === "营业服务员") {
    return [
      { path: "/waiter/dashboard", title: "首页", icon: House },
      { path: "/waiter/menu", title: "菜单点菜", icon: Goods },
      { path: "/waiter/ktv", title: "KTV管理", icon: Service },
      { path: "/waiter/unsettled", title: "历史账单", icon: Ticket },
      { path: "/waiter/stock", title: "库房管理", icon: Edit },
    ];
  } else if (role === "财务管理员") {
    return [
      { path: "/finance/dashboard", title: "首页", icon: House },
      { path: "/finance/bill", title: "客人账单", icon: Money }, // 新增
      { path: "/finance/employee", title: "员工管理", icon: House },
      { path: "/finance/attendance", title: "考勤管理", icon: Money },
      { path: "/finance/salary", title: "工资管理", icon: Ticket },
    ];
  } else if (role === "总经理") {
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
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: white;
  font-size: 18px;
  font-weight: bold;
  background-color: #263445;
}
.el-menu-vertical:not(.el-menu--collapse) {
  width: 220px;
}
.el-aside {
  background-color: #304156;
}
.el-header {
  background-color: #ffffff;
  border-bottom: 1px solid #e6e9ef;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
}
.el-main {
  background-color: #f5f7fa;
  padding: 24px;
}
</style>
