import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api', // Spring Boot backend
})

// Seeding API functions
export const seedingApi = {
  // Get available classes
  getAvailableClasses: () => api.get('/seeding/available-classes'),
  
  // Seed data for a specific class
  seedClass: (className: string, count: number = 5) => 
    api.post(`/seeding/seed/${className}?count=${count}`),
  
  // Seed data for multiple classes
  seedClasses: (classNames: string[], countPerClass: number = 3) => 
    api.post(`/seeding/seed/bulk?countPerClass=${countPerClass}`, classNames),
  
  // Seed all available classes
  seedAll: (countPerClass: number = 3) => 
    api.post(`/seeding/seed/all?countPerClass=${countPerClass}`),
  
  // Get seeded data for a specific class
  getSeededData: (className: string) => 
    api.get(`/seeding/data/${className}`),
  
  // Get all seeded data
  getAllSeededData: () => 
    api.get('/seeding/data'),
  
  // Get seeding statistics
  getStatistics: () => 
    api.get('/seeding/statistics'),
  
  // Clear seeded data for a specific class
  clearSeededData: (className: string) => 
    api.delete(`/seeding/data/${className}`),
  
  // Clear all seeded data
  clearAllSeededData: () => 
    api.delete('/seeding/data'),
}

export default api
