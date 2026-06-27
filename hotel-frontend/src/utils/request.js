import axios from "axios";
import { ElMessage } from "element-plus";
import router from "@/router";
import { useUserStore } from "@/store/user";

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 15000,
});

// 请求拦截器：添加 token + 打印日志（合并）
request.interceptors.request.use(
  (config) => {
    console.log("请求URL:", config.baseURL + config.url);
    console.log("baseURL:", import.meta.env.VITE_API_BASE_URL);
    const token = localStorage.getItem("token");
    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error),
);

// 响应拦截器：处理统一错误
request.interceptors.response.use(
  (response) => {
    const res = response.data;
    if (res.code !== 200) {
      ElMessage.error(res.msg || "请求失败");
      if (res.code === 401) {
        const userStore = useUserStore();
        userStore.logout(); // 会清除 token 和 userInfo
        router.push("/login");
      }
      return Promise.reject(new Error(res.msg));
    }
    return res;
  },
  (error) => {
    ElMessage.error(error.message || "网络错误");
    return Promise.reject(error);
  },
);

export default request;
import.meta.env;
