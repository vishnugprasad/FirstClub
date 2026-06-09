import axios from 'axios'

const apiBaseUrl = import.meta.env.VITE_API_URL
  || (import.meta.env.VITE_API_HOST
    ? `https://${import.meta.env.VITE_API_HOST}/api/v1`
    : 'http://localhost:8080/api/v1')

const api = axios.create({
  baseURL: apiBaseUrl,
  timeout: 10000,
})

export const errorMessage = (error) =>
  error.response?.data?.message || error.message || 'Something went wrong'

export default api
