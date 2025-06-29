<template>
  <div class="p-6">
    <Card>
      <CardHeader>
        <CardTitle>Data Seeding with Reflection</CardTitle>
        <CardDescription>
          Generate sample data for your domain models using Java reflection
        </CardDescription>
      </CardHeader>
      
      <CardContent class="space-y-6">
        <!-- Available Classes -->
        <div>
          <h3 class="text-lg font-semibold mb-3">Available Classes</h3>
          <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-2">
            <div
              v-for="className in availableClasses"
              :key="className"
              class="p-3 border rounded-lg cursor-pointer hover:bg-gray-50 transition-colors"
              :class="selectedClasses.includes(className) ? 'bg-blue-50 border-blue-200' : 'bg-white'"
              @click="toggleClassSelection(className)"
            >
              <div class="font-medium text-sm">{{ className }}</div>
              <div class="text-xs text-gray-500">
                {{ getSeededCount(className) }} records
              </div>
            </div>
          </div>
        </div>

        <!-- Seeding Controls -->
        <div class="space-y-4">
          <div class="flex items-center gap-4">
            <Label for="countPerClass">Records per class:</Label>
            <Input
              id="countPerClass"
              v-model.number="countPerClass"
              type="number"
              min="1"
              max="20"
              class="w-24"
            />
          </div>

          <div class="flex gap-2">
            <Button
              @click="seedSelectedClasses"
              :disabled="selectedClasses.length === 0 || isSeeding"
              variant="default"
            >
              <span v-if="isSeeding">Seeding...</span>
              <span v-else>Seed Selected Classes ({{ selectedClasses.length }})</span>
            </Button>
            
            <Button
              @click="seedAllClasses"
              :disabled="isSeeding"
              variant="outline"
            >
              Seed All Classes
            </Button>
            
            <Button
              @click="clearAllData"
              :disabled="isClearing"
              variant="destructive"
            >
              <span v-if="isClearing">Clearing...</span>
              <span v-else>Clear All Data</span>
            </Button>
          </div>
        </div>

        <!-- Statistics -->
        <div v-if="statistics" class="bg-gray-50 p-4 rounded-lg">
          <h3 class="text-lg font-semibold mb-3">Seeding Statistics</h3>
          <div class="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
            <div>
              <div class="font-medium">Total Classes</div>
              <div class="text-2xl font-bold text-blue-600">{{ statistics.totalClasses }}</div>
            </div>
            <div>
              <div class="font-medium">Seeded Classes</div>
              <div class="text-2xl font-bold text-green-600">{{ statistics.seededClasses }}</div>
            </div>
            <div>
              <div class="font-medium">Total Records</div>
              <div class="text-2xl font-bold text-purple-600">{{ statistics.totalRecords }}</div>
            </div>
            <div>
              <div class="font-medium">Avg per Class</div>
              <div class="text-2xl font-bold text-orange-600">
                {{ statistics.seededClasses > 0 ? Math.round(statistics.totalRecords / statistics.seededClasses) : 0 }}
              </div>
            </div>
          </div>
        </div>

        <!-- Results -->
        <div v-if="lastResult" class="bg-green-50 border border-green-200 p-4 rounded-lg">
          <h3 class="text-lg font-semibold text-green-800 mb-2">Last Operation Result</h3>
          <div class="text-green-700">
            <p><strong>Message:</strong> {{ lastResult.message }}</p>
            <p v-if="lastResult.totalClasses"><strong>Classes Processed:</strong> {{ lastResult.totalClasses }}</p>
            <p v-if="lastResult.recordsPerClass"><strong>Records per Class:</strong> {{ lastResult.recordsPerClass }}</p>
          </div>
        </div>

        <!-- Error -->
        <Alert v-if="error" variant="destructive">
          <AlertTitle>Error</AlertTitle>
          <AlertDescription>{{ error }}</AlertDescription>
        </Alert>
      </CardContent>
    </Card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { seedingApi } from '@/lib/api'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'

interface SeedingStatistics {
  totalClasses: number
  seededClasses: number
  totalRecords: number
  availableClasses: string[]
  seededClassesList: string[]
  recordsPerClass: Record<string, number>
}

const availableClasses = ref<string[]>([])
const selectedClasses = ref<string[]>([])
const countPerClass = ref(3)
const statistics = ref<SeedingStatistics | null>(null)
const lastResult = ref<any>(null)
const error = ref<string>('')
const isSeeding = ref(false)
const isClearing = ref(false)

const loadAvailableClasses = async () => {
  try {
    const response = await seedingApi.getAvailableClasses()
    availableClasses.value = response.data
  } catch (err) {
    error.value = 'Failed to load available classes'
    console.error(err)
  }
}

const loadStatistics = async () => {
  try {
    const response = await seedingApi.getStatistics()
    statistics.value = response.data
  } catch (err) {
    console.error('Failed to load statistics:', err)
  }
}

const toggleClassSelection = (className: string) => {
  const index = selectedClasses.value.indexOf(className)
  if (index > -1) {
    selectedClasses.value.splice(index, 1)
  } else {
    selectedClasses.value.push(className)
  }
}

const getSeededCount = (className: string) => {
  if (!statistics.value?.recordsPerClass) return 0
  return statistics.value.recordsPerClass[className] || 0
}

const seedSelectedClasses = async () => {
  if (selectedClasses.value.length === 0) return
  
  isSeeding.value = true
  error.value = ''
  
  try {
    const response = await seedingApi.seedClasses(selectedClasses.value, countPerClass.value)
    lastResult.value = response.data
    await loadStatistics()
  } catch (err: any) {
    error.value = err.response?.data?.message || 'Failed to seed data'
    console.error(err)
  } finally {
    isSeeding.value = false
  }
}

const seedAllClasses = async () => {
  isSeeding.value = true
  error.value = ''
  
  try {
    const response = await seedingApi.seedAll(countPerClass.value)
    lastResult.value = response.data
    await loadStatistics()
  } catch (err: any) {
    error.value = err.response?.data?.message || 'Failed to seed all data'
    console.error(err)
  } finally {
    isSeeding.value = false
  }
}

const clearAllData = async () => {
  isClearing.value = true
  error.value = ''
  
  try {
    await seedingApi.clearAllSeededData()
    lastResult.value = { message: 'All seeded data cleared successfully' }
    await loadStatistics()
  } catch (err: any) {
    error.value = err.response?.data?.message || 'Failed to clear data'
    console.error(err)
  } finally {
    isClearing.value = false
  }
}

onMounted(async () => {
  await loadAvailableClasses()
  await loadStatistics()
})
</script> 