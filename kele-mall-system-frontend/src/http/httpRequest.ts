import axios, { type AxiosInstance, type AxiosResponse, type InternalAxiosRequestConfig, AxiosError } from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

// 1. 定义后端返回的数据结构 (根据你的实际情况调整)
// 这样在组件里用 res.data 就能有提示了
interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

const service: AxiosInstance = axios.create({
  baseURL: 'http://localhost:8080', 
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
})

// ==========================================
// 请求拦截器
// ==========================================
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem('cola_admin_token')
    if (token) {
      // 确保 headers 对象存在
      config.headers = config.headers || {}
      config.headers.Authorization = token
    }
    return config
  },
  (error: AxiosError) => {
    return Promise.reject(error)
  }
)

// ==========================================
// 响应拦截器
// ==========================================
service.interceptors.response.use(
  (response: AxiosResponse) => {
    // 这里的 res 类型是 ApiResponse
    const res = response.data as ApiResponse
    
    // 业务逻辑错误处理 (code !== 200)
    if (res.code !== 200) {
      ElMessage.error(res.message || '系统未知错误')
      
      // 401: Token 过期或未登录
      if (res.code === 401) {
        localStorage.removeItem('cola_admin_token')
        router.replace('/login')
      }
      
      // 抛出错误，中断后续代码执行
      return Promise.reject(new Error(res.message || 'Error'))
    } else {
      // 业务成功，直接返回 res (包含 code, message, data)
      return res as any 
    }
  },
  (error: any) => {
    let message = '网络连接失败'
    // 处理 HTTP 状态码错误 (如 404, 500)
    if (error.response) {
      switch (error.response.status) {
        case 401:
          message = '未授权，请重新登录'
          localStorage.removeItem('cola_admin_token')
          router.replace('/login')
          break
        case 403:
          message = '拒绝访问'
          break
        case 404:
          message = '请求资源不存在'
          break
        case 500:
          message = '服务器内部错误'
          break
        default:
          message = `连接错误 ${error.response.status}`
      }
    }
    // 防止重复弹窗 (可选优化: 可以加一个防抖)
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

// ==========================================
// 导出通用方法 (增加泛型 T 支持)
// ==========================================

/**
 * GET 请求
 * @param url 请求地址
 * @param params 查询参数
 * @returns Promise<ApiResponse<T>>
 */
export const doGet = <T = any>(url: string, params?: any): Promise<ApiResponse<T>> => {
  return service.get(url, { params })
}

/**
 * POST 请求
 * @param url 请求地址
 * @param data Body 参数
 * @returns Promise<ApiResponse<T>>
 */
export const doPost = <T = any>(url: string, data?: any): Promise<ApiResponse<T>> => {
  return service.post(url, data)
}

/**
 * PUT 请求
 */
export const doPut = <T = any>(url: string, data?: any): Promise<ApiResponse<T>> => {
  return service.put(url, data)
}

/**
 * DELETE 请求
 */
export const doDelete = <T = any>(url: string, params?: any): Promise<ApiResponse<T>> => {
  return service.delete(url, { params })
}

export default service