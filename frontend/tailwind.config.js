// tailwind.config.js
const defaultTheme = require('tailwindcss/defaultTheme');

module.exports = {
  content: [
    './index.html',
    './src/**/*.{vue,js,ts,jsx,tsx}',
  ],
  theme: {
    extend: {
      colors: {
        ...defaultTheme.colors, // Thêm palette mặc định trở lại :contentReference[oaicite:11]{index=11}
      },
    },
  },
  plugins: [],
};
