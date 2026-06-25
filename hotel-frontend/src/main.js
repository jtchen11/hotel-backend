import { createPinia } from 'pinia';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import * as ElementPlusIconsVue from '@element-plus/icons-vue';
import App from './App.vue';
import router from './router';
import '@/styles/global.css'
import { createApp } from 'vue'
import BaiduMap from 'vue-baidu-map-next'

const app = createApp(App)

const pinia = createPinia();
app.use(pinia);
app.use(router);
app.use(ElementPlus);
app.use(BaiduMap, {
    ak: 'GOsmZvgGnJS1fom7aqE2QMRrya07qlzW'
});

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component);
}

app.mount('#app');