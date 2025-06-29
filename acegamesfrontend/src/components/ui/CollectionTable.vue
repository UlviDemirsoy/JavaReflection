<template>
  <div v-if="!schema">
    <p class="text-gray-500">Select a collection.</p>
  </div>
  <div v-else>
    <div class="flex flex-col items-center mb-8">
      <h2 class="text-3xl font-extrabold text-blue-700 mb-2 tracking-tight capitalize">{{ schema.collection }}</h2>
    </div>
    <div class="flex flex-row justify-between items-center mb-4 max-w-5xl mx-auto gap-4">
      <input
        v-model="filterText"
        type="text"
        placeholder="Filter..."
        class="border border-gray-300 rounded-lg px-4 py-2 text-base w-72 focus:outline-none focus:ring-2 focus:ring-blue-400 shadow-sm bg-white"
      />
      <slot name="add-button"></slot>
    </div>
    <div class="max-w-5xl mx-auto rounded-2xl shadow-xl bg-white p-6">
      <div class="overflow-x-auto">
        <Table class="min-w-[900px] w-full">
          <TableHeader>
            <TableRow>
              <TableHead v-for="key in fieldKeys" :key="key" class="text-center sticky top-0 bg-white z-20 px-5 py-4 text-lg font-extrabold border-b border-gray-200 uppercase tracking-wide shadow-md text-blue-700">
                {{ key.replace(/_/g, ' ').replace(/\b\w/g, l => l.toUpperCase()) }}
              </TableHead>
              <TableHead class="text-center sticky top-0 bg-white z-20 px-5 py-4 text-lg font-extrabold border-b border-gray-200 uppercase tracking-wide shadow-md text-blue-700">Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            <template v-if="paginatedItems.length">
              <TableRow
                v-for="(item, idx) in paginatedItems"
                :key="item._id"
                :class="[
                  idx % 2 === 0 ? 'bg-gray-50' : 'bg-white',
                  'hover:bg-blue-50 transition'
                ]"
              >
                <TableCell v-for="key in fieldKeys" :key="key" class="text-center px-5 py-3 border-b border-gray-100 rounded-lg max-w-xs truncate group relative">
                  <span v-if="key.toLowerCase().includes('id') && typeof item[key] === 'string'" class="truncate block cursor-pointer" :title="item[key]">
                    {{ item[key] }}
                  </span>
                  <span v-else-if="typeof item[key] === 'object' && item[key] !== null">
                    <span class="inline-block bg-blue-100 text-blue-700 text-xs px-2 py-1 rounded-full cursor-pointer" :title="JSON.stringify(item[key])">
                      Object
                    </span>
                  </span>
                  <span v-else>
                    {{ renderCell(item[key], schema.fields[key]) }}
                  </span>
                </TableCell>
                <TableCell class="text-center px-5 py-3 border-b border-gray-100 rounded-lg">
                  <div class="flex gap-2 justify-center">
                    <Button variant="secondary" size="sm" @click="$emit('edit', item)">Edit</Button>
                  </div>
                </TableCell>
              </TableRow>
            </template>
            <template v-else>
              <TableRow>
                <TableCell :colspan="fieldKeys.length + 1" class="text-center py-8 text-gray-400 text-lg">
                  <div class="flex flex-col items-center gap-2">
                    <svg width="48" height="48" fill="none" viewBox="0 0 24 24"><path fill="#60a5fa" d="M12 2a10 10 0 100 20 10 10 0 000-20zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/></svg>
                    <span>No data found. Try adjusting your filter or add a new item.</span>
                  </div>
                </TableCell>
              </TableRow>
            </template>
          </TableBody>
        </Table>
      </div>
      <!-- Pagination Controls -->
      <div class="flex flex-row items-center justify-between mt-6">
        <div class="flex items-center gap-2">
          <button
            class="px-3 py-1 rounded-md border border-gray-300 bg-white text-gray-700 hover:bg-blue-50 disabled:opacity-50"
            :disabled="currentPage === 1"
            @click="currentPage--"
          >
            Previous
          </button>
          <span class="mx-2 text-base">Page {{ currentPage }} of {{ totalPages }}</span>
          <button
            class="px-3 py-1 rounded-md border border-gray-300 bg-white text-gray-700 hover:bg-blue-50 disabled:opacity-50"
            :disabled="currentPage === totalPages || totalPages === 0"
            @click="currentPage++"
          >
            Next
          </button>
        </div>
        <div class="flex items-center gap-2">
          <label for="pageSize" class="text-base text-gray-600">Rows per page:</label>
          <select
            id="pageSize"
            v-model.number="pageSize"
            class="border border-gray-300 rounded-md px-2 py-1 text-base focus:outline-none focus:ring-2 focus:ring-blue-400"
          >
            <option :value="10">10</option>
            <option :value="20">20</option>
            <option :value="50">50</option>
          </select>
        </div>
      </div>
    </div>
    <div v-if="loading" class="text-xs text-gray-500 mt-2">Loading...</div>
    <div v-if="error" class="text-xs text-red-500 mt-2">{{ error }}</div>
  </div>
</template>

<script setup lang="ts">
import { computed, toRef, ref, watch } from 'vue'
import { useCollectionData } from '../../composables/useCollectionData'
import Button from '../../components/ui/button/Button.vue'
import { Table, TableHeader, TableBody, TableRow, TableHead, TableCell } from '../../components/ui/table'

const props = defineProps<{ 
  collection: string | null,
  refreshKey?: number 
}>()
const emit = defineEmits(['edit'])
const collectionRef = toRef(props, 'collection')
const refreshKeyRef = toRef(props, 'refreshKey')
const { schema, items, loading, error } = useCollectionData(collectionRef, refreshKeyRef)

const fieldKeys = computed(() => schema.value ? Object.keys(schema.value.fields) : [])

const filterText = ref('')

const pageSize = ref(10)
const currentPage = ref(1)

const filteredItems = computed(() => {
  if (!filterText.value) return items.value
  const lower = filterText.value.toLowerCase()
  return items.value.filter(item =>
    fieldKeys.value.some(key => {
      const val = item[key]
      return typeof val === 'string' && val.toLowerCase().includes(lower)
    })
  )
})

const totalPages = computed(() => Math.ceil(filteredItems.value.length / pageSize.value) || 1)

const paginatedItems = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredItems.value.slice(start, start + pageSize.value)
})

watch([pageSize, filteredItems], () => {
  currentPage.value = 1
})

console.log('schema.value:', schema.value)
console.log('fieldKeys:', fieldKeys.value)
console.log('items:', items.value)

function renderCell(value: any, fieldDef: any) {
  if (fieldDef.type === 'Date' && typeof value === 'number') {
    return new Date(value).toISOString().slice(0, 10)
  }
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
