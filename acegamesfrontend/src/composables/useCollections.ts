import { ref, onMounted } from 'vue'
import api from '../lib/api'

export function useCollections() {
  const collections = ref<any[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  const fetchCollections = async () => {
    loading.value = true
    error.value = null
    try {
      const res = await api.get('/schema')
      collections.value = res.data
    } catch (e: any) {
      error.value = e.message || 'Bir hata oluştu'
    } finally {
      loading.value = false
    }
  }

  onMounted(fetchCollections)

  return { collections, loading, error, fetchCollections }
}
