<script setup lang="ts">
import Sidebar from './components/ui/Sidebar.vue'
import CollectionView from './views/CollectionView.vue'
import { ref } from 'vue'

const selectedCollection = ref<string | null>(null)
</script>

<template>
  <div>
    <aside class="fixed top-0 left-0 h-screen w-64 bg-gray-100 shadow-md z-10">
      <Sidebar :selected="selectedCollection" @select="selectedCollection = $event" />
    </aside>
    <main class="ml-64 p-8 min-h-screen">
      <CollectionView :collection="selectedCollection" />
    </main>
  </div>
</template>

<style scoped>
.logo {
  height: 6em;
  padding: 1.5em;
  will-change: filter;
  transition: filter 300ms;
}
.logo:hover {
  filter: drop-shadow(0 0 2em #646cffaa);
}
.logo.vue:hover {
  filter: drop-shadow(0 0 2em #42b883aa);
}
</style>
