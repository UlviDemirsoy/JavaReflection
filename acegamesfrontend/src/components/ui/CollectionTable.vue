<template>
  <div v-if="!schema">
    <p class="text-gray-500">Select a collection.</p>
  </div>
  <div v-else>
    <h2 class="text-2xl font-bold mb-4 text-center">{{ schema.collection }}</h2>
    <div class="overflow-x-auto">
      <Table class="min-w-[900px] w-full rounded-xl shadow border border-gray-200">
        <TableHeader>
          <TableRow>
            <TableHead v-for="key in fieldKeys" :key="key" class="text-center sticky top-0 bg-white z-10 px-4 py-3 text-base font-semibold border-b border-gray-200">
              {{ key }}
            </TableHead>
            <TableHead class="text-center sticky top-0 bg-white z-10 px-4 py-3 text-base font-semibold border-b border-gray-200">Actions</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          <TableRow v-for="item in items" :key="item._id" class="hover:bg-accent transition">
            <TableCell v-for="key in fieldKeys" :key="key" class="text-center px-4 py-2 border-b border-gray-100">
              {{ renderCell(item[key], schema.fields[key]) }}
            </TableCell>
            <TableCell class="text-center px-4 py-2 border-b border-gray-100">
              <div class="flex gap-2 justify-center">
                <Button variant="secondary" size="sm" @click="$emit('edit', item)">Edit</Button>
              </div>
            </TableCell>
          </TableRow>
        </TableBody>
      </Table>
    </div>
    <div v-if="loading" class="text-xs text-gray-500 mt-2">Loading...</div>
    <div v-if="error" class="text-xs text-red-500 mt-2">{{ error }}</div>
  </div>
</template>

<script setup lang="ts">
import { computed, toRef } from 'vue'
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
