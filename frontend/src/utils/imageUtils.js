/**
 * 图片工具模块 - 性能优化版本
 * 提供图片加载、优化和交互功能
 */

// 图片配置
const IMAGE_CONFIG = {
  placeholder: '/placeholder.jpg',
  errorImage: '/error-image.png',
  retryCount: 3,
  retryDelay: 1000,
  preloadDistance: 1.5,
  thumbnailQuality: 80,
  fullQuality: 90,
  // 响应式断点
  breakpoints: {
    sm: 640,
    md: 768,
    lg: 1024,
    xl: 1280,
    '2xl': 1536
  },
  // 支持的现代图片格式（按优先级排序）
  modernFormats: ['webp', 'avif'],
  // 格式转换质量
  conversionQuality: 85
}

// 检测浏览器支持的图片格式
const formatSupport = {
  webp: false,
  avif: false,
  initialized: false
}

// 初始化格式支持检测
function detectFormatSupport() {
  if (formatSupport.initialized) return
  
  const canvas = document.createElement('canvas')
  canvas.width = 1
  canvas.height = 1
  
  // 检测 WebP 支持
  formatSupport.webp = canvas.toDataURL('image/webp').indexOf('data:image/webp') === 0
  
  // 检测 AVIF 支持（通过创建Image对象测试）
  const avifImage = new Image()
  avifImage.src = 'data:image/avif;base64,AAAAIGZ0eXBhdmlmAAAAAGF2aWZtaWYxbWlhZk1BMUIAAADybWV0YQAAAAAAAAAoaGRscgAAAAAAAAAAcGljdAAAAAAAAAAAAAAAAGxpYmF2aWYAAAAADnBpdG0AAAAAAAEAAAAeaWxvYwAAAABEAAABAAEAAAABAAABGgAAAB0AAAAoaWluZgAAAAAAAQAAABppbmZlAgAAAAABAABhdjAxQ29sb3IAAAAAamlwcnAAAABLaXBjbwAAABRpc3BlAAAAAAAAAAIAAAACAAAAEHBpeGkAAAAAAwgICAAAAAxhdjFDgQ0MAAAAABNjb2xybmNseAACAAIAAYAAAAAXaXBtYQAAAAAAAAABAAEEAQKDBAAAACVtZGF0EgAKCBgANogQEAwgMg8f8D///8WfhwB8+ErK42A='
  avifImage.onload = () => { formatSupport.avif = true }
  avifImage.onerror = () => { formatSupport.avif = false }
  
  formatSupport.initialized = true
}

// 在模块加载时检测
if (typeof window !== 'undefined') {
  detectFormatSupport()
}

/**
 * 获取最佳图片格式
 * @returns {string} 推荐的图片格式后缀
 */
function getBestImageFormat() {
  if (!formatSupport.initialized) detectFormatSupport()
  if (formatSupport.avif) return 'avif'
  if (formatSupport.webp) return 'webp'
  return ''
}

/**
 * 生成响应式图片URL（支持格式转换和尺寸适配）
 * @param {string} url - 原始图片URL
 * @param {Object} options - 配置选项
 * @returns {string} 优化后的图片URL
 */
function generateResponsiveUrl(url, options = {}) {
  if (!url || url.startsWith('data:')) return url
  
  const { width, height, format, quality = IMAGE_CONFIG.thumbnailQuality } = options
  
  // 构建查询参数
  const params = new URLSearchParams()
  
  if (width) params.append('w', width)
  if (height) params.append('h', height)
  if (quality) params.append('q', quality)
  
  // 自动选择最佳格式
  const targetFormat = format || getBestImageFormat()
  if (targetFormat) params.append('f', targetFormat)
  
  const queryString = params.toString()
  if (!queryString) return url
  
  const separator = url.includes('?') ? '&' : '?'
  return `${url}${separator}${queryString}`
}

/**
 * 根据容器宽度获取最佳图片尺寸
 * @param {number} containerWidth - 容器宽度
 * @returns {number} 推荐的图片宽度
 */
function getOptimalWidth(containerWidth) {
  const widths = [320, 640, 750, 828, 1080, 1200, 1920, 2048, 3840]
  return widths.find(w => w >= containerWidth) || widths[widths.length - 1]
}

/**
 * 生成 srcset 属性值
 * @param {string} url - 原始图片URL
 * @param {Array<number>} widths - 宽度数组
 * @returns {string} srcset 字符串
 */
function generateSrcset(url, widths = [320, 640, 960, 1280, 1920]) {
  if (!url || url.startsWith('data:')) return ''
  
  return widths
    .map(w => `${generateResponsiveUrl(url, { width: w, quality: IMAGE_CONFIG.thumbnailQuality })} ${w}w`)
    .join(', ')
}

/**
 * 带超时的图片加载
 * @param {string} url - 图片URL
 * @param {number} timeout - 超时时间（毫秒）
 * @returns {Promise<HTMLImageElement>}
 */
function loadImageWithTimeout(url, timeout = 10000) {
  return new Promise((resolve, reject) => {
    const img = new Image()
    let timeoutId
    
    const cleanup = () => {
      clearTimeout(timeoutId)
      img.onload = null
      img.onerror = null
    }
    
    img.onload = () => {
      cleanup()
      resolve(img)
    }
    
    img.onerror = () => {
      cleanup()
      reject(new Error(`Failed to load image: ${url}`))
    }
    
    timeoutId = setTimeout(() => {
      cleanup()
      reject(new Error(`Image load timeout: ${url}`))
    }, timeout)
    
    img.src = url
  })
}

/**
 * 使用 Intersection Observer 的增强版懒加载
 * @param {Function} callback - 图片进入视口时的回调
 * @param {Object} options - 配置选项
 * @returns {Object} { observe, unobserve, destroy, pause, resume }
 */
export function useLazyLoad(callback, options = {}) {
  const {
    root = null,
    rootMargin = '50px 0px',
    threshold = 0.01,
    triggerOnce = true
  } = options

  let observer = null
  let isPaused = false
  const observedElements = new Set()

  const createObserver = () => {
    if (!('IntersectionObserver' in window)) {
      console.warn('[LazyLoad] IntersectionObserver not supported, images will load immediately')
      return null
    }

    return new IntersectionObserver((entries) => {
      if (isPaused) return
      
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          const element = entry.target
          
          // 使用 requestIdleCallback 在浏览器空闲时加载
          const loadImage = () => callback(element)
          
          if ('requestIdleCallback' in window) {
            requestIdleCallback(loadImage, { timeout: 100 })
          } else {
            // 使用 setTimeout 降级
            setTimeout(loadImage, 0)
          }
          
          if (triggerOnce) {
            observer.unobserve(element)
            observedElements.delete(element)
          }
        }
      })
    }, { root, rootMargin, threshold })
  }

  observer = createObserver()

  return {
    observe(element) {
      if (observer && element) {
        observer.observe(element)
        observedElements.add(element)
      } else if (element) {
        // 降级处理：立即加载
        callback(element)
      }
    },
    unobserve(element) {
      if (observer && element) {
        observer.unobserve(element)
        observedElements.delete(element)
      }
    },
    destroy() {
      if (observer) {
        observer.disconnect()
        observer = null
      }
      observedElements.clear()
    },
    pause() {
      isPaused = true
    },
    resume() {
      isPaused = false
    },
    // 预加载即将进入视口的图片
    preloadUpcoming(count = 2) {
      const elements = Array.from(observedElements).slice(0, count)
      elements.forEach(el => callback(el))
    }
  }
}

/**
 * 智能预加载 - 根据网络状况调整策略
 * @param {string[]} urls - 图片URL数组
 * @param {Object} options - 配置选项
 * @returns {Promise<HTMLImageElement[]>}
 */
export function smartPreload(urls, options = {}) {
  const {
    concurrency = 3,
    priority = 'auto', // 'high', 'low', 'auto'
    onProgress,
    signal // AbortSignal
  } = options

  // 根据网络状况调整并发数
  let effectiveConcurrency = concurrency
  if (typeof navigator !== 'undefined' && navigator.connection) {
    const conn = navigator.connection
    if (conn.effectiveType === '2g') effectiveConcurrency = 1
    else if (conn.effectiveType === '3g') effectiveConcurrency = 2
    else if (conn.effectiveType === '4g') effectiveConcurrency = 4
    
    // 如果开启了省流量模式，减少预加载
    if (conn.saveData) effectiveConcurrency = 1
  }

  let loaded = 0
  const results = []
  const queue = [...urls]
  const inProgress = new Set()

  return new Promise((resolve) => {
    const processNext = async () => {
      if (signal?.aborted) {
        resolve(results)
        return
      }

      if (queue.length === 0 && inProgress.size === 0) {
        resolve(results)
        return
      }

      while (inProgress.size < effectiveConcurrency && queue.length > 0) {
        const url = queue.shift()
        const index = urls.indexOf(url)
        
        inProgress.add(url)
        
        try {
          const img = await loadImageWithTimeout(url, 15000)
          results[index] = { success: true, img, url }
        } catch (error) {
          results[index] = { success: false, error, url }
        } finally {
          inProgress.delete(url)
          loaded++
          if (onProgress) {
            onProgress(loaded, urls.length, results[index])
          }
          processNext()
        }
      }
    }

    processNext()
  })
}

/**
 * 图片加载性能监控
 */
export const imagePerformance = {
  metrics: [],
  
  record(url, duration, success) {
    this.metrics.push({
      url,
      duration,
      success,
      timestamp: Date.now()
    })
    
    // 只保留最近100条记录
    if (this.metrics.length > 100) {
      this.metrics.shift()
    }
  },
  
  getStats() {
    const successful = this.metrics.filter(m => m.success)
    const failed = this.metrics.filter(m => !m.success)
    
    return {
      total: this.metrics.length,
      successCount: successful.length,
      failCount: failed.length,
      avgLoadTime: successful.length > 0 
        ? successful.reduce((sum, m) => sum + m.duration, 0) / successful.length 
        : 0,
      successRate: this.metrics.length > 0 
        ? (successful.length / this.metrics.length * 100).toFixed(2) + '%'
        : '0%'
    }
  },
  
  clear() {
    this.metrics = []
  }
}

/**
 * 增强版图片工具对象
 */
export const imageUtils = {
  IMAGE_CONFIG,
  formatSupport,
  
  /**
   * 获取处理后的图片URL
   * @param {string} url - 原始图片URL
   * @param {Object} options - 配置选项
   * @returns {string} 处理后的图片URL
   */
  getImageUrl(url, options = {}) {
    if (!url) return IMAGE_CONFIG.placeholder
    if (url.startsWith('http') || url.startsWith('data:')) return url
    
    const baseUrl = `http://localhost:8080${url}`
    return generateResponsiveUrl(baseUrl, options)
  },

  /**
   * 获取响应式图片配置（srcset 和 sizes）
   * @param {string} url - 原始图片URL
   * @param {Object} options - 配置选项
   * @returns {Object} { src, srcset, sizes }
   */
  getResponsiveImage(url, options = {}) {
    if (!url) {
      return { src: IMAGE_CONFIG.placeholder, srcset: '', sizes: '' }
    }
    
    const { sizes = '100vw', widths = [320, 640, 960, 1280, 1920] } = options
    const baseUrl = url.startsWith('http') ? url : `http://localhost:8080${url}`
    
    return {
      src: generateResponsiveUrl(baseUrl, { width: widths[1], quality: IMAGE_CONFIG.thumbnailQuality }),
      srcset: generateSrcset(baseUrl, widths),
      sizes
    }
  },

  /**
   * 检测浏览器支持的图片格式
   * @returns {Object} 格式支持状态
   */
  getSupportedFormats() {
    if (!formatSupport.initialized) detectFormatSupport()
    return { ...formatSupport }
  },

  /**
   * 处理图片加载错误
   * @param {Event} event - 错误事件
   * @param {HTMLImageElement} img - 图片元素
   */
  handleImageError(event, img) {
    console.error('[Image Error]', {
      url: img.src,
      error: event?.type,
      timestamp: new Date().toISOString()
    })
    
    // 尝试使用原始格式
    if (img.src.includes('?f=webp') || img.src.includes('?f=avif')) {
      img.src = img.src.replace(/\?f=(webp|avif)/, '')
    } else {
      img.src = IMAGE_CONFIG.errorImage || IMAGE_CONFIG.placeholder
    }
    img.onerror = null
  },

  /**
   * 带重试和性能监控的图片加载
   * @param {string} url - 图片URL
   * @param {HTMLImageElement} img - 图片元素
   * @param {number} retries - 剩余重试次数
   */
  loadImageWithRetry(url, img, retries = IMAGE_CONFIG.retryCount) {
    const startTime = performance.now()
    
    img.onload = () => {
      const duration = performance.now() - startTime
      imagePerformance.record(url, duration, true)
      console.log('[Image Loaded]', { url, duration: `${duration.toFixed(2)}ms` })
    }
    
    img.onerror = (event) => {
      if (retries > 0) {
        const delay = IMAGE_CONFIG.retryDelay * (IMAGE_CONFIG.retryCount - retries + 1)
        console.warn(`[Image Retry] ${retries} attempts left, retrying in ${delay}ms`, { url })
        setTimeout(() => {
          // 添加时间戳避免缓存
          const retryUrl = `${url}${url.includes('?') ? '&' : '?'}_t=${Date.now()}`
          img.src = retryUrl
          this.loadImageWithRetry(url, img, retries - 1)
        }, delay)
      } else {
        const duration = performance.now() - startTime
        imagePerformance.record(url, duration, false)
        this.handleImageError(event, img)
      }
    }
  },

  /**
   * 批量加载图片（带并发控制）
   * @param {string[]} urls - 图片URL数组
   * @param {Object} options - 配置选项
   */
  async loadImages(urls, options = {}) {
    return smartPreload(urls, options)
  },

  /**
   * 获取性能统计
   */
  getPerformanceStats() {
    return imagePerformance.getStats()
  }
}

/**
 * 预加载单张图片（兼容旧版本）
 * @param {string} url - 图片URL
 * @returns {Promise<HTMLImageElement>}
 */
export function preloadImage(url) {
  return loadImageWithTimeout(url, 10000)
}

/**
 * 预加载多张图片（兼容旧版本）
 * @param {string[]} urls - 图片URL数组
 * @param {Function} onProgress - 进度回调
 * @returns {Promise<HTMLImageElement[]>}
 */
export function preloadImages(urls, onProgress) {
  return smartPreload(urls, { onProgress, concurrency: 3 })
}

export default {
  IMAGE_CONFIG,
  imageUtils,
  useLazyLoad,
  preloadImage,
  preloadImages,
  smartPreload,
  imagePerformance
}
