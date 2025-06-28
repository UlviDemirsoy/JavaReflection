<template>
  <div v-if="!schema">
    <p class="text-gray-500">Bir koleksiyon seçin.</p>
  </div>
  <div v-else>
    <h2 class="text-2xl font-bold mb-4">{{ schema.collection }}</h2>
    <table class="min-w-full border">
      <thead>
        <tr>
          <th v-for="key in fieldKeys" :key="key" class="px-2 py-1 border-b">
            {{ key }}
          </th>
          <th class="px-2 py-1 border-b">İşlem</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in items" :key="item._id">
          <td v-for="key in fieldKeys" :key="key" class="px-2 py-1 border-b">
            {{ renderCell(item[key], schema.fields[key]) }}
          </td>
          <td class="px-2 py-1 border-b">
            <button class="btn-secondary" @click="$emit('edit', item)">Düzenle</button>
          </td>
        </tr>
      </tbody>
    </table>
    <div v-if="loading" class="text-xs text-gray-500 mt-2">Yükleniyor...</div>
    <div v-if="error" class="text-xs text-red-500 mt-2">{{ error }}</div>
  </div>
</template>

<script setup lang="ts">
import { computed, toRef } from 'vue'
import { useCollectionData } from '../../composables/useCollectionData'

const props = defineProps<{ collection: string | null }>()
const emit = defineEmits(['edit'])
const collectionRef = toRef(props, 'collection')
const { schema, items, loading, error } = useCollectionData(collectionRef)

const fieldKeys = computed(() => schema.value ? Object.keys(schema.value.fields) : [])

console.log('schema.value:', schema.value)
console.log('fieldKeys:', fieldKeys.value)
console.log('items:', items.value)

function renderCell(value: any, fieldDef: any) {
  if (fieldDef.type === 'Object' && value) {
    return JSON.stringify(value)
  }
  if (fieldDef.type === 'Array' && Array.isArray(value)) {
    return value.map(v => typeof v === 'object' ? '[object Object]' : v).join(', ')
  }
  if (fieldDef.type === 'Boolean') {
    return value ? '✔️' : '❌'
  }
  return value ?? '-'
}
</script>
