<script setup lang="ts">
import Sidebar from './components/ui/Sidebar.vue'
import CollectionView from './views/CollectionView.vue'
import { ref, onMounted } from 'vue'

const selectedCollection = ref<string | null>(null)
const showSplash = ref(true)

onMounted(() => {
  setTimeout(() => {
    showSplash.value = false
  }, 1000)
})
</script>

<template>
  <div>
    <transition name="fade-splash">
      <div v-if="showSplash" class="fixed inset-0 z-50 flex items-center justify-center bg-white">
        <img src="/acegames-logo.png" alt="Ace Games Logo" class="w-40 h-auto animate-bounce" />
      </div>
    </transition>
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
.fade-splash-enter-active, .fade-splash-leave-active {
  transition: opacity 0.7s cubic-bezier(0.4,0,0.2,1);
}
.fade-splash-enter-from, .fade-splash-leave-to {
  opacity: 0;
}
.fade-splash-enter-to, .fade-splash-leave-from {
  opacity: 1;
}
</style>
