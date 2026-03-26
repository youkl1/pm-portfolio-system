import axios from 'axios'

// 创建axios实例
const apiClient = axios.create({
  baseURL: '',
  withCredentials: true
})

// 添加响应拦截器处理未登录情况
apiClient.interceptors.response.use(
  response => response,
  error => {
    if (error.response && error.response.status === 401) {
      // 未登录，跳转到登录页面
      localStorage.removeItem('userInfo')
      window.location.reload()
    }
    return Promise.reject(error)
  }
)

export default apiClient
