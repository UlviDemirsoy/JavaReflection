<template>
  <Transition
    name="fade-pop"
    mode="out-in"
    appear
  >
    <div v-if="collection" :key="collection" class="relative bg-gray-50 min-h-screen py-8">
      <div class="flex flex-col items-center mb-6">
        <h1 class="text-3xl font-extrabold tracking-tight text-blue-700 mb-4">Generic Crud App</h1>
      </div>
      <div class="max-w-5xl mx-auto rounded-2xl shadow-xl bg-white p-6">
        <CollectionTable 
          :collection="collection" 
          :refresh-key="refreshKey"
          @edit="handleEdit"
        >
          <template #add-button>
            <button class="btn-primary ml-4" @click="openCreateModal">+ Add</button>
          </template>
        </CollectionTable>
      </div>
      <div v-if="loading" class="absolute inset-0 flex items-center justify-center bg-white/60 z-20">
        <span class="inline-block w-10 h-10 border-4 border-blue-500 border-t-transparent rounded-full animate-spin"></span>
      </div>
      <DynamicFormModal
        :open="showModal"
        :collection="collection"
        :edit-item="editItem"
        :is-edit-mode="isEditMode"
        @close="closeModal"
        @created="handleCreated"
        @updated="handleUpdated"
      />
    </div>
    <div v-else key="empty" class="bg-gray-50 min-h-screen flex flex-col items-center justify-center">
      <h1 class="text-3xl font-extrabold tracking-tight text-blue-700 mb-4">Generic Crud App</h1>
      <p class="text-gray-500">Choose a collection...</p>
    </div>
  </Transition>
</template>

<script setup lang="ts">
import CollectionTable from '../components/ui/CollectionTable.vue'
import DynamicFormModal from '../components/ui/DynamicFormModal.vue'
import { defineProps, ref, computed, onMounted } from 'vue'
import { useCollectionData } from '../composables/useCollectionData'

const props = defineProps<{ collection: string | null }>()
const { loading } = useCollectionData(computed(() => props.collection))

const showModal = ref(false)
const isEditMode = ref(false)
const editItem = ref<any>(null)
const refreshKey = ref(0)

function openCreateModal() {
  isEditMode.value = false
  editItem.value = null
  showModal.value = true
}

function handleEdit(item: any) {
  isEditMode.value = true
  editItem.value = item
  showModal.value = true
}

function closeModal() {
  showModal.value = false
  isEditMode.value = false
  editItem.value = null
}

function handleCreated() {
  closeModal()
  // Refresh the table by changing key
  refreshKey.value++
}

function handleUpdated() {
  closeModal()
  // Refresh the table by changing key
  refreshKey.value++
}

onMounted(() => {
  document.title = 'Generic Crud App'
})
</script>

<style scoped>
.fade-pop-enter-active,
.fade-pop-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
.fade-pop-enter-from,
.fade-pop-leave-to {
  opacity: 0;
  transform: scale(0.96);
}
.fade-pop-enter-to,
.fade-pop-leave-from {
  opacity: 1;
  transform: scale(1);
}
</style>
