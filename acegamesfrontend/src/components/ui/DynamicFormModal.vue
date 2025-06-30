<template>
  <div v-if="open" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm">
    <div class="bg-white rounded-2xl shadow-2xl p-10 w-full max-w-3xl relative max-h-[90vh] overflow-y-auto border border-gray-200">
      <button class="absolute top-6 right-6 text-gray-400 hover:text-gray-700 text-3xl font-bold focus:outline-none focus:ring-2 focus:ring-blue-500" @click="$emit('close')">&times;</button>
      <div class="mb-8 pb-2 border-b border-gray-100 flex items-center justify-center">
        <h2 class="text-3xl font-extrabold text-center tracking-tight">{{ isEditMode ? (schema?.displayName || collection) + ' Edit' : 'Add New ' + (schema?.displayName || collection) }}</h2>
      </div>
      <form @submit.prevent="handleSubmit" class="space-y-6">
        <template v-for="(field, key) in schema?.fields" :key="key">
          <div v-if="String(key) !== '_id'" class="space-y-2">
            <Label class="block font-semibold text-base">{{ key }}</Label>
            <DynamicFormField
              :field="field"
              v-model="form[key]"
              :enumOptions="enumOptions"
              :referenceOptions="referenceOptions"
            />
          </div>
        </template>
        <div class="flex gap-4 pt-6">
          <Button 
            type="button" 
            variant="outline" 
            class="flex-1 text-lg font-semibold py-3" 
            @click="$emit('close')"
          >
            Cancel
          </Button>
          <Button 
            type="submit" 
            variant="default" 
            class="flex-1 text-lg font-semibold py-3 text-black bg-blue-500 hover:bg-blue-600"
          >
            {{ isEditMode ? 'Update' : 'Save' }}
          </Button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import api from '../../lib/api'
import Button from '../../components/ui/button/Button.vue'
import Label from '../../components/ui/label/Label.vue'
import DynamicFormField from './DynamicFormField.vue'

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
  
  form.value = {}
  for (const key in res.data.fields) {
    if (key !== '_id') {
      const field = res.data.fields[key]
      if (field.type === 'Array') {
        form.value[key] = []
      } else if (field.type === 'Object') {
        form.value[key] = {}
      } else if (field.type === 'Boolean') {
        form.value[key] = false
      } else {
        form.value[key] = ''
      }
    }
  }
  
  await fetchReferenceOptions(res.data.fields)
  
  if (props.isEditMode && props.editItem) {
    for (const key in schema.value.fields) {
      if (key !== '_id' && props.editItem[key] !== undefined) {
        const field = schema.value.fields[key]
        
        if (field.type === 'Date' && typeof props.editItem[key] === 'number') {
          const date = new Date(props.editItem[key])
          form.value[key] = date.toISOString().slice(0, 10)
        } else if (field.reference && props.editItem[key]) {
          if (typeof props.editItem[key] === 'object' && props.editItem[key]._id) {
            form.value[key] = props.editItem[key]._id
          } else {
            form.value[key] = props.editItem[key]
          }
        } else {
          form.value[key] = JSON.parse(JSON.stringify(props.editItem[key]))
        }
      }
    }
  }
}

async function fetchReferenceOptions(fields: any) {
  const references = new Set<string>()
  
  for (const key in fields) {
    const field = fields[key]
    if (field.reference) {
      references.add(field.reference)
    } else if (field.type === 'Object' && field.fields) {
        await fetchReferenceOptions(field.fields)
    } else if (field.type === 'Array' && field.items) {
      if (field.items.reference) {
        references.add(field.items.reference)
      } else if (field.items.type === 'Object' && field.items.fields) {
        await fetchReferenceOptions(field.items.fields)
      }
    }
  }
  
  for (const reference of references) {
    if (!referenceOptions.value[reference]) {
      try {
        const res = await api.get(`/content/${reference.toLowerCase()}`)
        referenceOptions.value = { ...referenceOptions.value, [reference]: res.data }
      } catch (error) {
        console.error(`Failed to fetch reference ${reference}:`, error)
        referenceOptions.value = { ...referenceOptions.value, [reference]: [] }
      }
    }
  }
}

async function fetchEnums() {
  try {
    const res = await api.get('/schema/enums')
    enumOptions.value = res.data
  } catch (error) {
    console.error('Failed to fetch enums:', error)
    enumOptions.value = {}
  }
}

function convertDatesToTimestamps(obj: any, schemaFields: any): any {
  const result = { ...obj }
  
  for (const key in schemaFields) {
    if (key === '_id') continue
    
    const field = schemaFields[key]
    const value = result[key]
    
    if (field.type === 'Date' && typeof value === 'string' && value) {
      result[key] = new Date(value).getTime()
    } else if (field.type === 'Object' && value && field.fields) {
      result[key] = convertDatesToTimestamps(value, field.fields)
    } else if (field.type === 'Array' && Array.isArray(value) && field.items) {
      result[key] = value.map(item => {
        if (field.items.type === 'Object' && field.items.fields) {
          return convertDatesToTimestamps(item, field.items.fields)
        } else if (field.items.type === 'Date' && typeof item === 'string' && item) {
          return new Date(item).getTime()
        }
        return item
      })
    }
  }
  
  return result
}

async function handleSubmit() {
  if (!props.collection || !schema.value) return
  
  try {
    const dataToSubmit = convertDatesToTimestamps(form.value, schema.value.fields)
    
    if (props.isEditMode && props.editItem && props.editItem._id) {
      // Update
      await api.put(`/content/${props.collection}/${props.editItem._id}`, dataToSubmit)
      emit('updated')
    } else {
      // Create
      await api.post(`/content/${props.collection}`, dataToSubmit)
      emit('created')
    }
    
    emit('close')
  } catch (error) {
    console.error('Submit error:', error)
  }
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
</script> 