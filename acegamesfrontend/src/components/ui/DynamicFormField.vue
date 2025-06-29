<template>
  <div v-if="field.type === 'Object'">
    <div v-for="(subField, subKey) in field.fields" :key="subKey" class="mb-4">
      <Label class="block text-sm font-semibold mb-1">{{ subKey }}</Label>
      <DynamicFormField
        :field="subField"
        v-model="modelValue[subKey]"
        :enumOptions="enumOptions"
        :referenceOptions="referenceOptions"
      />
    </div>
  </div>
  <div v-else-if="field.type === 'Array'">
    <div v-for="(item, idx) in modelValue" :key="idx" class="mb-4 p-3 border rounded-lg bg-gray-50">
      <DynamicFormField
        :field="field.items"
        v-model="modelValue[idx]"
        :enumOptions="enumOptions"
        :referenceOptions="referenceOptions"
      />
      <Button type="button" variant="secondary" class="w-full mt-2 text-base font-semibold py-2" @click="modelValue.splice(idx, 1)">Delete</Button>
    </div>
    <Button type="button" variant="secondary" class="w-full mt-2 text-base font-semibold py-2" @click="addArrayItem">
      + Add
    </Button>
  </div>
  <div v-else-if="field.reference && referenceOptions[field.reference]">
    <Select v-model="modelValue" class="w-full text-base py-2">
      <SelectTrigger class="w-full text-base py-2">
        {{ getReferenceDisplayValue(field.reference, modelValue) || 'Select' }}
      </SelectTrigger>
      <SelectContent>
        <SelectItem
          v-for="item in referenceOptions[field.reference].filter(opt => opt._id)"
          :key="item._id"
          :value="item._id"
        >
          {{ item.name || item._id }}
        </SelectItem>
      </SelectContent>
    </Select>
  </div>
  <div v-else-if="field.type === 'String'">
    <Input v-model="modelValue" placeholder="Enter text" class="w-full text-base py-2" />
  </div>
  <div v-else-if="field.type === 'Number'">
    <Input v-model.number="modelValue" type="number" placeholder="Enter number" class="w-full text-base py-2" />
  </div>
  <div v-else-if="field.type === 'Date'">
    <Input v-model="modelValue" type="date" class="w-full text-base py-2" />
  </div>
  <div v-else-if="field.type === 'Boolean'">
    <Checkbox v-model="modelValue" class="w-full" />
  </div>
  <div v-else-if="field.type === 'Enum'">
    <Select v-model="modelValue" class="w-full text-base py-2">
      <SelectTrigger class="w-full text-base py-2">
        {{ modelValue || 'Select' }}
      </SelectTrigger>
      <SelectContent>
        <SelectItem v-for="option in enumOptions[field.enumName] || []" :key="option" :value="option">
          {{ option }}
        </SelectItem>
      </SelectContent>
    </Select>
  </div>
  <div v-else class="text-gray-400 italic text-xs">({{ field.type }})</div>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick } from 'vue'
import Input from '../../components/ui/input/Input.vue'
import Button from '../../components/ui/button/Button.vue'
import Select from '../../components/ui/select/Select.vue'
import { SelectTrigger, SelectContent, SelectItem } from '../../components/ui/select'
import Checkbox from './checkbox/Checkbox.vue'
import Label from '../../components/ui/label/Label.vue'
import DynamicFormField from './DynamicFormField.vue'

const props = defineProps<{ 
  field: any, 
  modelValue: any, 
  enumOptions?: Record<string, string[]>,
  referenceOptions?: Record<string, any[]>
}>()
const emit = defineEmits(['update:modelValue'])

const modelValue = computed({
  get: () => props.modelValue,
  set: v => emit('update:modelValue', v)
})

const enumOptions = computed<Record<string, string[]>>(() => {
  if (Array.isArray(props.enumOptions)) {
    // Convert array to object with a default key
    return { default: props.enumOptions }
  }
  return props.enumOptions ?? {}
})

const referenceOptions = computed<Record<string, any[]>>(() => {
  return props.referenceOptions ?? {}
})

function getReferenceDisplayValue(reference: string, value: any): string {
  if (!value || !referenceOptions.value[reference]) return ''
  const item = referenceOptions.value[reference].find(opt => opt._id === value)
  return item ? (item.name || item._id) : ''
}

function addArrayItem() {
  if (!Array.isArray(modelValue.value)) {
    emit('update:modelValue', [])
    nextTick(() => {
      modelValue.value.push(props.field.items.type === 'Object' ? {} : '')
    })
  } else {
    modelValue.value.push(props.field.items.type === 'Object' ? {} : '')
  }
}
</script> 