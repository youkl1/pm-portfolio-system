// API配置文件
const config = {
  // API基础URL
  apiBaseUrl: process.env.NODE_ENV === 'production' ? 'http://8.148.235.131:8080' : 'http://localhost:8080'
}

export default config
