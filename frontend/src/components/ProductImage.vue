<template>
  <figure class="product-image-container" :style="containerStyle">
    <!-- 骨架屏 -->
    <div
      v-if="showSkeleton"
      class="image-skeleton"
      :style="skeletonStyle"
    >
      <div class="skeleton-shimmer"></div>
      <div class="skeleton-placeholder">
        <el-icon :size="skeletonIconSize"><Picture /></el-icon>
        <span>{{ loadingText }}</span>
      </div>
    </div>
    
    <!-- 响应式图片 -->
    <picture v-show="!error">
      <!-- AVIF 格式（最优） -->
      <source
        v-if="supportsAvif && srcsetAvif && actualSrc"
        :srcset="srcsetAvif"
        :sizes="sizes"
        type="image/avif"
      >
      <!-- WebP 格式 -->
      <source
        v-if="supportsWebp && srcsetWebp && actualSrc"
        :srcset="srcsetWebp"
        :sizes="sizes"
        type="image/webp"
      >
      <!-- 回退到原始格式 -->
      <img
        ref="imgRef"
        :src="actualSrc"
        :srcset="actualSrcset"
        :sizes="sizes"
        :alt="alt"
        class="product-image"
        :class="{ loaded: imageLoaded }"
        :loading="nativeLazy ? 'lazy' : 'eager'"
        decoding="async"
        @load="handleLoad"
        @error="handleError"
        @click="$emit('click', $event)"
        @touchstart="handleTouchStart"
        @touchmove="handleTouchMove"
        @touchend="handleTouchEnd"
      >
    </picture>
    
    <!-- 错误状态 -->
    <div
      v-if="error"
      class="image-error"
      :style="errorStyle"
    >
      <el-icon :size="40"><Picture /></el-icon>
      <span>{{ errorText }}</span>
      <el-button size="small" @click="retry" v-if="retryCount < maxRetries">
        {{ retryButtonText }}
      </el-button>
    </div>
    
    <!-- 图片标题 -->
    <figcaption v-if="showCaption && caption" class="image-caption">
      {{ caption }}
    </figcaption>
  </figure>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { Picture } from '@element-plus/icons-vue'
import { imageUtils, useLazyLoad } from '@/utils/imageUtils'

// 1x1 透明 GIF 占位图
const TRANSPARENT_PLACEHOLDER = 'data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7'

const props = defineProps({
  src: {
    type: String,
    default: ''
  },
  alt: {
    type: String,
    default: '商品图片'
  },
  width: {
    type: [String, Number],
    default: '100%'
  },
  height: {
    type: [String, Number],
    default: 200
  },
  lazy: {
    type: Boolean,
    default: true
  },
  nativeLazy: {
    type: Boolean,
    default: true
  },
  showCaption: {
    type: Boolean,
    default: false
  },
  caption: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: '/placeholder.jpg'
  },
  sizes: {
    type: String,
    default: '(max-width: 640px) 100vw, (max-width: 1024px) 50vw, 33vw'
  },
  widths: {
    type: Array,
    default: () => [320, 640, 960, 1280]
  },
  quality: {
    type: Number,
    default: 80
  },
  loadingText: {
    type: String,
    default: '加载中...'
  },
  errorText: {
    type: String,
    default: '图片加载失败'
  },
  retryButtonText: {
    type: String,
    default: '点击重试'
  }
})

const emit = defineEmits(['click', 'load', 'error', 'retry'])

const imgRef = ref(null)
const loading = ref(true)
const error = ref(false)
const imageLoaded = ref(false)
const retryCount = ref(0)
const maxRetries = 3
const retryDelay = 1000
const lazyLoaded = ref(false)

const formatSupport = ref({ webp: false, avif: false })

const containerStyle = computed(() => ({
  width: typeof props.width === 'number' ? `${props.width}px` : props.width,
  height: typeof props.height === 'number' ? `${props.height}px` : props.height
}))

const skeletonStyle = computed(() => ({
  width: '100%',
  height: '100%'
}))

const skeletonIconSize = computed(() => {
  const h = typeof props.height === 'number' ? props.height : 200
  return Math.min(40, Math.max(24, h / 5))
})

const errorStyle = computed(() => ({
  width: '100%',
  height: '100%'
}))

const showSkeleton = computed(() => loading.value && !error.value)

const supportsWebp = computed(() => formatSupport.value.webp)
const supportsAvif = computed(() => formatSupport.value.avif)

const responsiveConfig = computed(() => {
  return imageUtils.getResponsiveImage(props.src, {
    sizes: props.sizes,
    widths: props.widths,
    quality: props.quality
  })
})

// 实际显示的src
const actualSrc = computed(() => {
  if (error.value && retryCount.value >= maxRetries) {
    return props.placeholder
  }
  
  // 懒加载模式下，初始使用透明占位图
  if (props.lazy && !lazyLoaded.value) {
    return TRANSPARENT_PLACEHOLDER
  }
  
  return responsiveConfig.value.src
})

// 实际显示的srcset
const actualSrcset = computed(() => {
  if (error.value || (props.lazy && !lazyLoaded.value)) {
    return ''
  }
  return responsiveConfig.value.srcset
})

const srcsetWebp = computed(() => {
  if (!props.src || error.value) return ''
  const baseUrl = props.src.startsWith('http') ? props.src : `http://localhost:8080${props.src}`
  return imageUtils.getResponsiveImage(props.src, {
    widths: props.widths,
    quality: props.quality
  }).srcset.split(', ').map(s => {
    const [url, width] = s.split(' ')
    return `${url}${url.includes('?') ? '&' : '?'}f=webp ${width}`
  }).join(', ')
})

const srcsetAvif = computed(() => {
  if (!props.src || error.value) return ''
  const baseUrl = props.src.startsWith('http') ? props.src : `http://localhost:8080${props.src}`
  return imageUtils.getResponsiveImage(props.src, {
    widths: props.widths,
    quality: props.quality
  }).srcset.split(', ').map(s => {
    const [url, width] = s.split(' ')
    return `${url}${url.includes('?') ? '&' : '?'}f=avif ${width}`
  }).join(', ')
})

let lazyLoadHandler = null

const handleLoad = (event) => {
  // 忽略透明占位图的加载事件
  if (event.target.src === TRANSPARENT_PLACEHOLDER) {
    return
  }
  
  loading.value = false
  error.value = false
  imageLoaded.value = true
  emit('load', event)
}

const handleError = (event) => {
  // 忽略透明占位图的错误事件
  if (event.target.src === TRANSPARENT_PLACEHOLDER) {
    return
  }
  
  const imgElement = event.target
  console.error('[ProductImage Error]', {
    src: props.src,
    actualSrc: imgElement?.src,
    naturalWidth: imgElement?.naturalWidth,
    naturalHeight: imgElement?.naturalHeight,
    retryCount: retryCount.value,
    timestamp: new Date().toISOString()
  })

  if (retryCount.value < maxRetries) {
    retryCount.value++
    const delay = retryDelay * retryCount.value
    console.warn(`[ProductImage] Retrying (${retryCount.value}/${maxRetries}) in ${delay}ms`)
    setTimeout(() => {
      if (imgRef.value) {
        const retryUrl = `${responsiveConfig.value.src}${responsiveConfig.value.src.includes('?') ? '&' : '?'}_t=${Date.now()}`
        imgRef.value.src = retryUrl
      }
    }, delay)
  } else {
    loading.value = false
    error.value = true
    emit('error', event)
  }
}

const retry = () => {
  retryCount.value = 0
  error.value = false
  loading.value = true
  imageLoaded.value = false
  lazyLoaded.value = false
  
  if (props.lazy && lazyLoadHandler && imgRef.value) {
    lazyLoadHandler.observe(imgRef.value)
  } else if (imgRef.value) {
    imgRef.value.src = responsiveConfig.value.src
  }
  
  emit('retry')
}

let touchStartX = 0
let touchStartY = 0
let initialDistance = 0
let initialScale = 1

const handleTouchStart = (e) => {
  if (e.touches.length === 2) {
    const touch1 = e.touches[0]
    const touch2 = e.touches[1]
    initialDistance = Math.hypot(
      touch2.clientX - touch1.clientX,
      touch2.clientY - touch1.clientY
    )
    initialScale = 1
  } else if (e.touches.length === 1) {
    touchStartX = e.touches[0].clientX
    touchStartY = e.touches[0].clientY
  }
}

const handleTouchMove = (e) => {
  if (e.touches.length === 2) {
    e.preventDefault()
    const touch1 = e.touches[0]
    const touch2 = e.touches[1]
    const currentDistance = Math.hypot(
      touch2.clientX - touch1.clientX,
      touch2.clientY - touch1.clientY
    )
    const scale = (currentDistance / initialDistance) * initialScale
    const clampedScale = Math.min(3, Math.max(1, scale))
    
    if (imgRef.value) {
      imgRef.value.style.transform = `scale(${clampedScale})`
    }
  }
}

const handleTouchEnd = () => {
  initialDistance = 0
  initialScale = 1
  if (imgRef.value) {
    imgRef.value.style.transform = 'scale(1)'
  }
}

const detectFormatSupport = () => {
  const canvas = document.createElement('canvas')
  canvas.width = 1
  canvas.height = 1
  
  formatSupport.value.webp = canvas.toDataURL('image/webp').indexOf('data:image/webp') === 0
  
  const avifImage = new Image()
  avifImage.onload = () => { formatSupport.value.avif = true }
  avifImage.onerror = () => { formatSupport.value.avif = false }
  avifImage.src = 'data:image/avif;base64,AAAAIGZ0eXBhdmlmAAAAAGF2aWZtaWYxbWlhZk1BMUIAAADybWV0YQAAAAAAAAAoaGRscgAAAAAAAAAAcGljdAAAAAAAAAAAAAAAAGxpYmF2aWYAAAAADnBpdG0AAAAAAAEAAAAeaWxvYwAAAABEAAABAAEAAAABAAABGgAAAB0AAAAoaWluZgAAAAAAAQAAABppbmZlAgAAAAABAABhdjAxQ29sb3IAAAAAamlwcnAAAABLaXBjbwAAABRpc3BlAAAAAAAAAAIAAAACAAAAEHBpeGkAAAAAAwgICAAAAAxhdjFDgQ0MAAAAABNjb2xybmNseAACAAIAAYAAAAAXaXBtYQAAAAAAAAABAAEEAQKDBAAAACVtZGF0EgAKCBgANogQEAwgMg8f8D///8WfhwB8+ErK42A='
}

// 加载真实图片
const loadRealImage = () => {
  if (lazyLoaded.value) return
  
  lazyLoaded.value = true
  
  // 直接设置img元素的src
  if (imgRef.value) {
    imgRef.value.src = responsiveConfig.value.src
  }
}

onMounted(() => {
  detectFormatSupport()
  
  if (props.lazy && imgRef.value) {
    lazyLoadHandler = useLazyLoad(
      (element) => {
        loadRealImage()
      },
      {
        rootMargin: '100px 0px',
        threshold: 0.01
      }
    )
    lazyLoadHandler.observe(imgRef.value)
  } else if (imgRef.value) {
    // 非懒加载模式：立即加载
    lazyLoaded.value = true
    imgRef.value.src = responsiveConfig.value.src
  }
})

onUnmounted(() => {
  if (lazyLoadHandler) {
    lazyLoadHandler.destroy()
  }
})

watch(() => props.src, (newSrc, oldSrc) => {
  if (newSrc === oldSrc) return
  
  loading.value = true
  error.value = false
  imageLoaded.value = false
  retryCount.value = 0
  lazyLoaded.value = false
  
  if (imgRef.value) {
    imgRef.value.style.transform = 'scale(1)'
    
    if (!props.lazy) {
      lazyLoaded.value = true
      imgRef.value.src = responsiveConfig.value.src
    } else if (lazyLoadHandler) {
      lazyLoadHandler.unobserve(imgRef.value)
      lazyLoadHandler.observe(imgRef.value)
    }
  }
})
</script>

<style scoped>
.product-image-container {
  margin: 0;
  padding: 0;
  position: relative;
  overflow: hidden;
  background: #f5f5f5;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  opacity: 0;
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.product-image.loaded {
  opacity: 1;
}

.image-skeleton {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  z-index: 1;
}

@keyframes shimmer {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}

.skeleton-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: #ccc;
  z-index: 1;
}

.skeleton-placeholder span {
  font-size: 12px;
}

.image-error {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background: #f5f5f5;
  color: #999;
  z-index: 2;
}

.image-error span {
  font-size: 12px;
}

.image-caption {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 8px;
  background: rgba(0, 0, 0, 0.6);
  color: #fff;
  font-size: 12px;
  text-align: center;
}

@media (prefers-reduced-motion: reduce) {
  .product-image {
    transition: none;
  }
  
  .image-skeleton {
    animation: none;
    background: #f0f0f0;
  }
}

@media print {
  .image-skeleton,
  .image-error {
    display: none;
  }
  
  .product-image {
    opacity: 1;
  }
}
</style>
