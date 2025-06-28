<template>
  <div v-if="open" class="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
    <div class="bg-white rounded-xl shadow-xl p-8 w-full max-w-lg relative max-h-[90vh] overflow-y-auto">
      <button class="absolute top-4 right-4 text-gray-400 hover:text-gray-700 text-2xl" @click="$emit('close')">&times;</button>
      <h2 class="text-2xl font-bold mb-6">
        {{ isEditMode ? (schema?.displayName || collection) + ' Düzenle' : 'Yeni ' + (schema?.displayName || collection) + ' Ekle' }}
      </h2>
      <form @submit.prevent="handleSubmit" class="space-y-4">
        <template v-for="(field, key) in schema?.fields" :key="key">
          <div v-if="String(key) !== '_id'">
            <label class="block font-semibold mb-1">{{ key }}</label>
            <!-- Reference field support -->
            <select
              v-if="field.reference && referenceOptions[field.reference]"
              v-model="form[key]"
              class="w-full border rounded px-3 py-2"
            >
              <option disabled value="">Seçiniz</option>
              <option v-for="item in referenceOptions[field.reference]" :key="item._id" :value="item._id">
                {{ item.name || item._id }}
              </option>
            </select>
            <input
              v-else-if="field.type === 'String'"
              v-model="form[key]"
              type="text"
              class="w-full border rounded px-3 py-2"
            />
            <input
              v-else-if="field.type === 'Number'"
              v-model.number="form[key]"
              type="number"
              class="w-full border rounded px-3 py-2"
            />
            <input
              v-else-if="field.type === 'Boolean'"
              v-model="form[key]"
              type="checkbox"
              class="mr-2"
            />
            <select
              v-else-if="field.type === 'Enum'"
              v-model="form[key]"
              class="w-full border rounded px-3 py-2"
            >
              <option disabled value="">Seçiniz</option>
              <option v-for="option in enumOptions[field.enumName] || []" :key="option" :value="option">
                {{ option }}
              </option>
            </select>
            <!-- Array of Object support -->
            <div v-else-if="field.type === 'Array' && field.items && field.items.type === 'Object'">
              <div v-for="(item, idx) in form[key]" :key="idx" class="mb-2 p-2 border rounded">
                <div v-for="(subField, subKey) in field.items.fields" :key="subKey">
                  <label class="block text-xs">{{ subKey }}</label>
                  <input
                    v-if="subField.type === 'String'"
                    v-model="form[key][idx][subKey]"
                    type="text"
                    class="w-full border rounded px-2 py-1 mb-1"
                  />
                  <input
                    v-else-if="subField.type === 'Number'"
                    v-model.number="form[key][idx][subKey]"
                    type="number"
                    class="w-full border rounded px-2 py-1 mb-1"
                  />
                  <input
                    v-else-if="subField.type === 'Boolean'"
                    v-model="form[key][idx][subKey]"
                    type="checkbox"
                    class="mr-2"
                  />
                  <select
                    v-else-if="subField.type === 'Enum'"
                    v-model="form[key][idx][subKey]"
                    class="w-full border rounded px-2 py-1 mb-1"
                  >
                    <option disabled value="">Seçiniz</option>
                    <option v-for="option in enumOptions[subField.enumName] || []" :key="option" :value="option">
                      {{ option }}
                    </option>
                  </select>
                  <div v-else class="text-gray-400 italic text-xs">({{ subField.type }})</div>
                </div>
                <button type="button" class="btn-secondary mt-1" @click="form[key].splice(idx, 1)">Sil</button>
              </div>
              <button type="button" class="btn-primary mt-2" @click="form[key].push({})">+ Ekle</button>
            </div>
            <div v-else class="text-gray-400 italic text-sm">Bu alan için input eklenmedi ({{ field.type }})</div>
          </div>
        </template>
        <button type="submit" class="btn-primary w-full mt-4">
          {{ isEditMode ? 'Güncelle' : 'Ekle' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import api from '../../lib/api'

const props = defineProps<{ 
  open: boolean, 
  collection: string | null, 
  editItem?: any,
  isEditMode?: boolean 
}>()
const emit = defineEmits(['close', 'created', 'updated'])

const schema = ref<any>(null)
const form = ref<Record<string, any>>({})
const enumOptions = ref<Record<string, string[]>>({})
const referenceOptions = ref<Record<string, any[]>>({})

async function fetchSchema() {
  if (!props.collection) return
  const res = await api.get(`/schema/${props.collection}`)
  schema.value = res.data
  // Formu sıfırla ve enum/arraylar için uygun başlangıç değeri ata
  form.value = {}
  for (const key in res.data.fields) {
    if (key !== '_id') {
      if (res.data.fields[key].type === 'Enum') {
        form.value[key] = ''
      } else if (res.data.fields[key].type === 'Array') {
        form.value[key] = []
      } else if (res.data.fields[key].reference) {
        form.value[key] = ''
        fetchReferenceOptions(res.data.fields[key].reference)
      } else {
        form.value[key] = ''
      }
    }
  }
  // Eğer edit modundaysa, formu mevcut değerlerle doldur
  if (props.isEditMode && props.editItem) {
    for (const key in form.value) {
      if (props.editItem[key] !== undefined) {
        form.value[key] = JSON.parse(JSON.stringify(props.editItem[key]))
      }
    }
  }
}

async function fetchReferenceOptions(reference: string) {
  if (referenceOptions.value[reference]) return // önceden yüklendiyse tekrar çekme
  const res = await api.get(`/content/${reference.toLowerCase()}`)
  referenceOptions.value[reference] = res.data
}

async function fetchEnums() {
  const res = await api.get('/schema/enums')
  enumOptions.value = res.data
  console.log('Enum options:', enumOptions.value)
}

watch(() => props.collection, () => {
  if (props.open) fetchSchema()
})
watch(() => props.open, (val) => {
  if (val) {
    fetchSchema()
    fetchEnums()
  }
})
watch(() => props.editItem, () => {
  if (props.open && props.isEditMode) {
    fetchSchema()
  }
})

async function handleSubmit() {
  if (!props.collection) return
  if (props.isEditMode && props.editItem && props.editItem._id) {
    // Update
    await api.put(`/content/${props.collection}/${props.editItem._id}`, form.value)
    emit('updated')
  } else {
    // Create
    await api.post(`/content/${props.collection}`, form.value)
    emit('created')
  }
  emit('close')
}
</script> 