import axios, { type AxiosInstance, type AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const service: AxiosInstance = axios.create({
  baseURL: 'http://localhost:8080', 
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
})

service.interceptors.request.use(
  (config: any) => { // 👈 加上 : any
    const token = localStorage.getItem('cola_app_token')
    if (token) {
      config.headers.Authorization = token
    }
    return config
  },
  (error: any) => { // 👈 加上 : any
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || '系统未知错误')
      if (res.code === 401) {
        localStorage.removeItem('cola_app_token')
        router.replace('/login')
      }
      return Promise.reject(new Error(res.message || 'Error'))
    } else {
      return res
    }
  },
  (error: any) => { // 👈 加上 : any
    let message = '网络连接失败'
    if (error.response) {
      switch (error.response.status) {
        case 401:
          message = '未授权，请重新登录'
          localStorage.removeItem('cola_app_token')
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
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

// 导出方法时也加上类型，防止调用处报错
export const doGet = (url: string, params?: any) => {
  return service.get(url, { params })
}

export const doPost = (url: string, data?: any) => {
  return service.post(url, data)
}

export const doPut = (url: string, data?: any) => {
  return service.put(url, data)
}

export const doDelete = (url: string, params?: any) => {
  return service.delete(url, { params })
}

export default service