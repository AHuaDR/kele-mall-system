/// <reference types="vite/client" />

// 这段代码告诉 TypeScript 如何处理 .vue 文件
declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}

/// <reference types="vite/client" />

declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}

// 👇👇👇 新增这段代码，手动声明 WangEditor 模块 👇👇👇
declare module '@wangeditor/editor-for-vue' {
  import { DefineComponent } from 'vue'
  export const Editor: DefineComponent<{}, {}, any>
  export const Toolbar: DefineComponent<{}, {}, any>
}