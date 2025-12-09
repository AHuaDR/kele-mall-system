/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        // 活力橙/金橘色：用于按钮、价格、选中状态
        primary: '#FF6B00', 
        // 辅助深色：用于标题
        textMain: '#1A1A1A',
        // 辅助浅色：用于副标题
        textSub: '#858b9c',
        // 高级灰背景：带一点点蓝调的灰，显白
        pageBg: '#F2F4F8',
        // 纯白卡片背景
        cardBg: '#FFFFFF',
      },
      boxShadow: {
        // 柔和的高级投影
        'soft': '0 4px 20px rgba(0, 0, 0, 0.03)',
      }
    },
  },
  plugins: [],
}