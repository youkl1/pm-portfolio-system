import axios from 'axios'

// 创建axios实例
const apiClient = axios.create({
  baseURL: 'http://localhost:8080',
  withCredentials: true
})

// 添加响应拦截器处理未登录情况和错误日志
apiClient.interceptors.response.use(
  response => response,
  error => {
    if (error.response && error.response.status === 401) {
      // 未登录，跳转到登录页面
      localStorage.removeItem('userInfo')
      window.location.reload()
    } else {
      // 打印详细的错误日志
      console.error('API请求错误:', {
        url: error.config?.url,
        method: error.config?.method,
        status: error.response?.status,
        statusText: error.response?.statusText,
        data: error.response?.data,
        message: error.message,
        stack: error.stack
      })
    }
    return Promise.reject(error)
  }
)

export default apiClient
