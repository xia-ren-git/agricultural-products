<template>
  <el-dialog
    v-model="visible"
    :show-close="false"
    class="image-preview-dialog"
    @close="handleClose"
  >
    <div class="preview-container" ref="previewContainer">
      <div class="preview-header">
        <span class="preview-title">{{ currentIndex + 1 }} / {{ imageList.length }}</span>
        <div class="preview-controls">
          <el-button-group>
            <el-button @click="zoomIn" :disabled="scale >= 3">
              <el-icon><ZoomIn /></el-icon>
            </el-button>
            <el-button @click="zoomOut" :disabled="scale <= 1">
              <el-icon><ZoomOut /></el-icon>
            </el-button>
            <el-button @click="resetZoom">
              <el-icon><RefreshRight /></el-icon>
            </el-button>
          </el-button-group>
          <el-button-group>
            <el-button @click="rotate(-90)">
              <el-icon><RefreshLeft /></el-icon>
            </el-button>
            <el-button @click="rotate(90)">
              <el-icon><RefreshRight /></el-icon>
            </el-button>
          </el-button-group>
        </div>
      </div>
      <div
        class="preview-image-wrapper"
        @wheel.prevent="handleWheel"
        @mousedown="handleMouseDown"
        @mousemove="handleMouseMove"
        @mouseup="handleMouseUp"
        @mouseleave="handleMouseUp"
      >
        <img
          ref="previewImage"
          :src="currentImage"
          :style="imageStyle"
          class="preview-image"
          @error="handleImageError"
          draggable="false"
        />
      </div>
      <div class="preview-hint">
        <span>拖动移动图片 | 滚轮缩放(1-3倍) | 双指缩放(移动端)</span>
      </div>
    </div>

    <template #footer v-if="imageList.length > 1">
      <div class="preview-footer">
        <el-button
          :disabled="currentIndex === 0"
          @click="prev"
        >
          <el-icon><ArrowLeft /></el-icon>
          上一张
        </el-button>
        <div class="preview-thumbnails">
          <div
            v-for="(img, index) in imageList"
            :key="index"
            :class="['thumbnail', { active: index === currentIndex }]"
            @click="goTo(index)"
          >
            <img :src="getThumbUrl(img)" />
          </div>
        </div>
        <el-button
          :disabled="currentIndex === imageList.length - 1"
          @click="next"
        >
          下一张
          <el-icon><ArrowRight /></el-icon>
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { 
  ZoomIn, ZoomOut, RefreshLeft, RefreshRight,
  ArrowLeft, ArrowRight
} from '@element-plus/icons-vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  imageList: {
    type: Array,
    default: () => []
  },
  initialIndex: {
    type: Number,
    default: 0
  }
})

const emit = defineEmits(['update:modelValue', 'close'])

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const currentIndex = ref(props.initialIndex)
const scale = ref(1)
const rotation = ref(0)
const translateX = ref(0)
const translateY = ref(0)
const isDragging = ref(false)
const dragStartX = ref(0)
const dragStartY = ref(0)

const previewContainer = ref(null)
const previewImage = ref(null)

const currentImage = computed(() => {
  return props.imageList[currentIndex.value] || ''
})

const imageStyle = computed(() => ({
  transform: `translate(${translateX.value}px, ${translateY.value}px) scale(${scale.value}) rotate(${rotation.value}deg)`,
  transition: isDragging.value ? 'none' : 'transform 0.3s ease'
}))

watch(() => props.initialIndex, (val) => {
  currentIndex.value = val
  resetZoom()
})

const getThumbUrl = (url) => {
  if (!url) return '/placeholder.jpg'
  if (url.startsWith('http')) return url
  return `http://localhost:8080${url}`
}

const handleClose = () => {
  emit('close')
  resetZoom()
}

const resetZoom = () => {
  scale.value = 1
  rotation.value = 0
  translateX.value = 0
  translateY.value = 0
}

const zoomIn = () => {
  if (scale.value < 3) {
    scale.value = Math.min(3, scale.value + 0.5)
  }
}

const zoomOut = () => {
  if (scale.value > 1) {
    scale.value = Math.max(1, scale.value - 0.5)
  }
}

const rotate = (deg) => {
  rotation.value += deg
}

const handleWheel = (e) => {
  if (e.deltaY < 0) {
    zoomIn()
  } else {
    zoomOut()
  }
}

const handleMouseDown = (e) => {
  if (scale.value > 1) {
    isDragging.value = true
    dragStartX.value = e.clientX - translateX.value
    dragStartY.value = e.clientY - translateY.value
  }
}

const handleMouseMove = (e) => {
  if (isDragging.value) {
    translateX.value = e.clientX - dragStartX.value
    translateY.value = e.clientY - dragStartY.value
  }
}

const handleMouseUp = () => {
  isDragging.value = false
}

const handleImageError = (e) => {
  console.error('[Image Preview Error]', {
    url: currentImage.value,
    timestamp: new Date().toISOString()
  })
}

const prev = () => {
  if (currentIndex.value > 0) {
    currentIndex.value--
    resetZoom()
  }
}

const next = () => {
  if (currentIndex.value < props.imageList.length - 1) {
    currentIndex.value++
    resetZoom()
  }
}

const goTo = (index) => {
  currentIndex.value = index
  resetZoom()
}

defineExpose({
  prev,
  next,
  goTo,
  resetZoom
})
</script>

<style scoped>
.image-preview-dialog {
  max-width: 90vw;
}

.preview-container {
  display: flex;
  flex-direction: column;
  height: 70vh;
}

.preview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}

.preview-title {
  font-size: 14px;
  color: #666;
}

.preview-controls {
  display: flex;
  gap: 10px;
}

.preview-image-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  cursor: grab;
  background: #000;
  position: relative;
}

.preview-image-wrapper:active {
  cursor: grabbing;
}

.preview-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
  user-select: none;
}

.preview-hint {
  text-align: center;
  padding: 10px;
  color: #999;
  font-size: 12px;
}

.preview-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
}

.preview-thumbnails {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding: 5px;
}

.thumbnail {
  width: 50px;
  height: 50px;
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
  opacity: 0.6;
  transition: all 0.2s;
}

.thumbnail:hover {
  opacity: 1;
}

.thumbnail.active {
  border-color: #409eff;
  opacity: 1;
}

.thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

@media (max-width: 768px) {
  .preview-header {
    flex-direction: column;
    gap: 10px;
  }

  .preview-controls {
    flex-wrap: wrap;
    justify-content: center;
  }

  .preview-footer {
    flex-direction: column;
  }

  .preview-thumbnails {
    order: -1;
    width: 100%;
    justify-content: center;
  }
}
</style>
