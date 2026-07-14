import { createRouter, createWebHistory } from "vue-router";
import { useUserStore } from "@/store/user";
import { ROLES, ROLE_HOME_MAP } from "@/constants/roles";

// 定义各角色的首页路径
const roleHomeMap = ROLE_HOME_MAP;

const routes = [
  {
    path: "/login",
    name: "Login",
    component: () => import("@/views/Login.vue"),
  },
  {
    path: "/",
    redirect: "/login",
  },
  {
    path: "/reception",
    name: "Reception",
    component: () => import("@/components/Layout.vue"),
    meta: { roles: [ROLES.RECEPTION] },
    children: [
      { path: "", redirect: "/reception/dashboard", name: "ReceptionIndex" },
      {
        path: "dashboard",
        component: () => import("@/views/reception/Dashboard.vue"),
        meta: { title: "前台首页" },
      },
      {
        path: "checkin",
        component: () => import("@/views/reception/Checkin.vue"),
        meta: { title: "入住办理" },
      },
      {
        path: "groupCheckin",
        component: () => import("@/views/reception/GroupCheckin.vue"),
        meta: { title: "团体预订" },
      },
      {
        path: "roomStatus",
        component: () => import("@/views/reception/RoomStatus.vue"),
        meta: { title: "房态图" },
      },
      {
        path: "booking",
        component: () => import("@/views/reception/Booking.vue"),
        meta: { title: "创建预订" },
      },
      {
        path: "messages",
        component: () => import("@/views/reception/Messages.vue"),
        meta: { title: "留言管理" },
      },
    ],
  },
  {
    path: "/waiter",
    name: "Waiter",
    component: () => import("@/components/Layout.vue"),
    meta: { roles: [ROLES.WAITER] },
    children: [
      { path: "", redirect: "/waiter/dashboard", name: "WaiterIndex" },
      {
        path: "dashboard",
        component: () => import("@/views/waiter/Dashboard.vue"),
        meta: { title: "服务首页" },
      },
      {
        path: "menu",
        component: () => import("@/views/waiter/Menu.vue"),
        meta: { title: "菜单点菜" },
      },
      {
        path: "ktv",
        component: () => import("@/views/waiter/Ktv.vue"),
        meta: { title: "KTV管理" },
      },
      {
        path: "unsettled",
        component: () => import("@/views/waiter/UnsettledBill.vue"),
      },
      {
        path: "stock",
        component: () => import("@/views/waiter/Stock.vue"),
        meta: { title: "库房管理" },
      },
    ],
  },
  {
    path: "/finance",
    name: "Finance",
    component: () => import("@/components/Layout.vue"),
    meta: { roles: [ROLES.FINANCE] },
    children: [
      { path: "", redirect: "/finance/dashboard", name: "FinanceIndex" },
      {
        path: "dashboard",
        component: () => import("@/views/finance/Dashboard.vue"),
        meta: { title: "财务首页" },
      },
      {
        path: "bill",
        component: () => import("@/views/finance/GuestBill.vue"),
        meta: { title: "客人账单" },
      },
      {
        path: "employee",
        component: () => import("@/views/finance/Employee.vue"),
        meta: { title: "员工管理" },
      },
      {
        path: "attendance",
        component: () => import("@/views/finance/Attendance.vue"),
        meta: { title: "考勤管理" },
      },
      {
        path: "salary",
        component: () => import("@/views/finance/Salary.vue"),
        meta: { title: "工资管理" },
      },
    ],
  },
  {
    path: "/gm",
    name: "Gm",
    component: () => import("@/components/Layout.vue"),
    meta: { roles: [ROLES.GM] },
    children: [
      { path: "", redirect: "/gm/statistics", name: "GmIndex" },
      {
        path: "statistics",
        component: () => import("@/views/gm/Statistics.vue"),
        meta: { title: "统计图表" },
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// 路由守卫：检查登录 + 角色权限

// decode JWT payload to check expiration
function isTokenExpired(token) {
  try {
    // JWT uses base64url, convert to base64 first
    const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
    const payload = JSON.parse(atob(base64));
    return payload.exp * 1000 < Date.now();
  } catch {
    return true;
  }
}

router.beforeEach((to, from, next) => {
  const userStore = useUserStore();
  const token = userStore.token;
  const role = userStore.userInfo.role;

  // redirect if token expired
  if (token && isTokenExpired(token)) {
    userStore.logout();
    next('/login');
    return;
  }

  if (to.path === '/login') {
    if (token) {
      const homePath = roleHomeMap[role] || '/login';
      next(homePath);
    } else {
      next();
    }
    return;
  }

  if (!token) {
    next('/login');
    return;
  }

  const requiredRoles = to.matched.flatMap(record => record.meta.roles || []);
  if (requiredRoles.length > 0 && !requiredRoles.includes(role)) {
    next('/login');
    return;
  }

  next();
});

export default router;
