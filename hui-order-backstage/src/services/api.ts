import axios from 'axios'

// Create a configured axios instance
const api = axios.create({
  baseURL: 'http://localhost:9100',
  headers: {
    'Content-Type': 'application/json',
  }
})

// Add request interceptor for auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

export default api
