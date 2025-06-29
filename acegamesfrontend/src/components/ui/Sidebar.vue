<template>
  <aside class="w-64 bg-gray-100 h-screen p-4">
    <h2 class="font-bold mb-4">Schemas</h2>
    <div class="flex flex-col gap-2">
      <Button
        v-for="col in collections"
        :key="col.collection"
        variant="ghost"
        class="w-full justify-start"
        :class="col.collection === selected ? 'bg-blue-600 text-black shadow' : ''"
        @click="$emit('select', col.collection)"
      >
        {{ col.collection }}
      </Button>
    </div>
    <div v-if="loading" class="text-xs text-gray-500 mt-4">Yükleniyor...</div>
    <div v-if="error" class="text-xs text-red-500 mt-4">{{ error }}</div>
  </aside>
</template>

<script setup lang="ts">
import Button from '../../components/ui/button/Button.vue'
import { useCollections } from '../../composables/useCollections'
import { ref } from 'vue'

const { collections, loading, error } = useCollections()
const props = defineProps<{ selected: string | null }>()
</script>
