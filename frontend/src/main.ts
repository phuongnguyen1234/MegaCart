import { createApp } from "vue";
import { createPinia } from "pinia";
import App from "./App.vue";
import router from "./router";
import "./assets/styles/main.css"; // Không đổi
import { useDanhMucStore } from "./store/danhmuc.store";

const app = createApp(App);
const pinia = createPinia();

app.use(pinia);
// Lấy store sau khi đã use(pinia)
const danhMucStore = useDanhMucStore();
danhMucStore.fetchMenuDanhMuc(); // Tải dữ liệu danh mục ngay khi ứng dụng khởi động
app.use(router);
app.mount("#app");
