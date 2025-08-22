import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import path from "path";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      // Cấu hình alias '@' để trỏ đến thư mục 'src'
      "@": path.resolve(__dirname, "./src"),
    },
  },
  server: {
    proxy: {
      // Bất kỳ request nào bắt đầu bằng '/api' sẽ được chuyển tiếp
      "/api": {
        // Địa chỉ của backend Spring Boot của bạn.
        // Hãy đảm bảo port 8080 là chính xác.
        target: "http://localhost:8080",

        // Cần thiết để backend nhận đúng host header
        changeOrigin: true,

        // Không cần rewrite path vì cả frontend và backend đều dùng '/api'
        // Nếu backend không có tiền tố /api, bạn sẽ cần dòng sau:
        // rewrite: (path) => path.replace(/^\/api/, ''),
      },
    },
  },
});
