import { ref, watch } from 'vue'
import type { Ref } from 'vue'
import api from '../lib/api'

export function useCollectionData(collection: Ref<string | null>) {
  const schema = ref<any>(null)
  const items = ref<any[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  const fetchData = async () => {
    if (!collection.value) return
    loading.value = true
    error.value = null
    try {
      const [schemaRes, dataRes] = await Promise.all([
        api.get(`/schema/${collection.value}`),
        api.get(`/content/${collection.value}`)
      ])
      console.log('Schema:', schemaRes.data)
      console.log('Items:', dataRes.data)
      schema.value = schemaRes.data
      items.value = dataRes.data
    } catch (e: any) {
      console.error('API Hatası:', e)
      error.value = e.message || 'Bir hata oluştu'
    } finally {
      loading.value = false
    }
  }

  watch(collection, fetchData, { immediate: true })

  return { schema, items, loading, error }
}
