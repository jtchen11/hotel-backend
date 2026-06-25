import { createRouter, createWebHistory } from 'vue-router';
import { useUserStore } from '@/store/user';

// 定义各角色的首页路径
const roleHomeMap = {
    '前台接待员': '/reception',
    '营业服务员': '/waiter',
    '财务管理员': '/finance',
    '总经理': '/gm',
};

const routes = [
    { path: '/login', name: 'Login', component: () => import('@/views/Login.vue') },
    {
        path: '/',
        redirect: '/login',
    },
    {
        path: '/reception',
        name: 'Reception',
        component: () => import('@/components/Layout.vue'),
        meta: { roles: ['前台接待员'] },
        children: [
            { path: '', redirect: '/reception/dashboard' },
            { path: 'dashboard', component: () => import('@/views/reception/Dashboard.vue'), meta: { title: '前台首页' } },
            { path: 'checkin', component: () => import('@/views/reception/Checkin.vue'), meta: { title: '散客预订' } },
            { path: 'groupCheckin', component: () => import('@/views/reception/GroupCheckin.vue'), meta: { title: '团体预订' } },
            { path: 'roomStatus', component: () => import('@/views/reception/RoomStatus.vue'), meta: { title: '房态图' } },
            { path: 'messages', component: () => import('@/views/reception/Messages.vue'), meta: { title: '留言管理' } },
        ],
    },
    {
        path: '/waiter',
        name: 'Waiter',
        component: () => import('@/components/Layout.vue'),
        meta: { roles: ['营业服务员'] },
        children: [
            { path: '', redirect: '/waiter/dashboard' },
            { path: 'dashboard', component: () => import('@/views/waiter/Dashboard.vue'), meta: { title: '服务首页' } },
            { path: 'menu', component: () => import('@/views/waiter/Menu.vue'), meta: { title: '菜单点菜' } },
            { path: 'ktv', component: () => import('@/views/waiter/Ktv.vue'), meta: { title: 'KTV管理' } },
            { path: 'unsettled', component: () => import('@/views/waiter/UnsettledBill.vue') },
            { path: 'stock', component: () => import('@/views/waiter/Stock.vue'), meta: { title: '库房管理' } },
        ],
    },
    {
        path: '/finance',
        name: 'Finance',
        component: () => import('@/components/Layout.vue'),
        meta: { roles: ['财务管理员'] },
        children: [
            { path: '', redirect: '/finance/dashboard' },
            { path: 'dashboard', component: () => import('@/views/finance/Dashboard.vue'), meta: { title: '财务首页' } },
            { path: 'bill', component: () => import('@/views/finance/GuestBill.vue'), meta: { title: '客人账单' } },
            { path: 'employee', component: () => import('@/views/finance/Employee.vue'), meta: { title: '员工管理' } },
            { path: 'attendance', component: () => import('@/views/finance/Attendance.vue'), meta: { title: '考勤管理' } },
            { path: 'salary', component: () => import('@/views/finance/Salary.vue'), meta: { title: '工资管理' } },
        ],
    },
    {
        path: '/gm',
        name: 'Gm',
        component: () => import('@/components/Layout.vue'),
        meta: { roles: ['总经理'] },
        children: [
            { path: '', redirect: '/gm/statistics' },
            { path: 'statistics', component: () => import('@/views/gm/Statistics.vue'), meta: { title: '统计图表' } },
        ],
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

// 路由守卫：检查登录 + 角色权限
router.beforeEach((to, from, next) => {
    const userStore = useUserStore();
    const token = userStore.token;
    const role = userStore.userInfo.role;

    if (to.path === '/login') {
        if (token) {
            // 已登录，跳转到对应主页
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

    // 检查角色权限
    if (to.meta.roles && !to.meta.roles.includes(role)) {
        next('/login');
        return;
    }

    next();
});

export default router;