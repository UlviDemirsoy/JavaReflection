<template>
  <aside class="w-64 bg-gray-100 h-screen p-4">
    <h2 class="font-bold mb-4">Koleksiyonlar</h2>
    <ul>
      <li
        v-for="col in collections"
        :key="col.collection"
        class="mb-2"
      >
        <button
          class="w-full text-left px-2 py-2 rounded-lg mb-2 transition font-semibold"
          :class="col.collection === selected
            ? 'bg-blue-600 text-black shadow'
            : 'bg-white text-gray-800 hover:bg-blue-100'"
          @click="$emit('select', col.collection)"
        >
          {{ col.collection }}
        </button>
      </li>
    </ul>
    <div v-if="loading" class="text-xs text-gray-500 mt-4">Yükleniyor...</div>
    <div v-if="error" class="text-xs text-red-500 mt-4">{{ error }}</div>
  </aside>
</template>

<script setup lang="ts">
import { useCollections } from '../../composables/useCollections';
import { ref, watch } from 'vue'

const { collections, loading, error } = useCollections()
const props = defineProps<{ selected: string | null }>()
</script>
