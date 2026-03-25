/**
 * 性能监控工具
 * 用于监控图片加载性能和整体页面性能
 */

class PerformanceMonitor {
  constructor() {
    this.metrics = {
      imageLoads: [],
      pageLoads: [],
      errors: []
    }
    this.observers = []
    this.isMonitoring = false
  }

  /**
   * 开始监控
   */
  start() {
    if (this.isMonitoring) return
    this.isMonitoring = true

    // 监控图片加载性能
    this.monitorImagePerformance()
    
    // 监控页面性能
    this.monitorPagePerformance()
    
    // 监控网络状况
    this.monitorNetworkStatus()

    console.log('[PerformanceMonitor] Started monitoring')
  }

  /**
   * 停止监控
   */
  stop() {
    this.isMonitoring = false
    this.observers.forEach(obs => obs.disconnect())
    this.observers = []
    console.log('[PerformanceMonitor] Stopped monitoring')
  }

  /**
   * 监控图片加载性能
   */
  monitorImagePerformance() {
    if (!window.PerformanceObserver) return

    const observer = new PerformanceObserver((list) => {
      for (const entry of list.getEntries()) {
        if (entry.initiatorType === 'img' || entry.name.match(/\.(jpg|jpeg|png|gif|webp|avif)$/i)) {
          const metric = {
            url: entry.name,
            duration: entry.duration,
            startTime: entry.startTime,
            loadTime: entry.responseEnd - entry.startTime,
            size: entry.transferSize,
            timestamp: Date.now()
          }
          
          this.metrics.imageLoads.push(metric)
          
          // 如果加载时间超过3秒，记录为慢加载
          if (entry.duration > 3000) {
            console.warn('[PerformanceMonitor] Slow image load:', metric)
          }
        }
      }
    })

    observer.observe({ entryTypes: ['resource'] })
    this.observers.push(observer)
  }

  /**
   * 监控页面性能
   */
  monitorPagePerformance() {
    // 使用 Navigation Timing API
    window.addEventListener('load', () => {
      setTimeout(() => {
        const navEntry = performance.getEntriesByType('navigation')[0]
        if (navEntry) {
          const pageMetric = {
            domContentLoaded: navEntry.domContentLoadedEventEnd - navEntry.startTime,
            loadComplete: navEntry.loadEventEnd - navEntry.startTime,
            firstByte: navEntry.responseStart - navEntry.startTime,
            timestamp: Date.now()
          }
          this.metrics.pageLoads.push(pageMetric)
          console.log('[PerformanceMonitor] Page load metrics:', pageMetric)
        }
      }, 0)
    })
  }

  /**
   * 监控网络状况
   */
  monitorNetworkStatus() {
    if ('connection' in navigator) {
      const connection = navigator.connection
      
      const logNetworkStatus = () => {
        console.log('[PerformanceMonitor] Network status:', {
          effectiveType: connection.effectiveType,
          downlink: connection.downlink,
          rtt: connection.rtt,
          saveData: connection.saveData
        })
      }

      logNetworkStatus()
      connection.addEventListener('change', logNetworkStatus)
    }
  }

  /**
   * 获取图片加载统计
   */
  getImageStats() {
    const loads = this.metrics.imageLoads
    if (loads.length === 0) return null

    const durations = loads.map(l => l.duration)
    const totalSize = loads.reduce((sum, l) => sum + (l.size || 0), 0)

    return {
      count: loads.length,
      avgDuration: durations.reduce((a, b) => a + b, 0) / durations.length,
      maxDuration: Math.max(...durations),
      minDuration: Math.min(...durations),
      totalSize: totalSize,
      slowLoads: loads.filter(l => l.duration > 3000).length,
      // 3G网络标准：加载时间<=3秒的图片占比
      meet3GStandard: (loads.filter(l => l.duration <= 3000).length / loads.length * 100).toFixed(2) + '%'
    }
  }

  /**
   * 获取性能报告
   */
  getReport() {
    return {
      imageStats: this.getImageStats(),
      pageLoads: this.metrics.pageLoads,
      errorCount: this.metrics.errors.length,
      timestamp: Date.now()
    }
  }

  /**
   * 清空指标
   */
  clear() {
    this.metrics = {
      imageLoads: [],
      pageLoads: [],
      errors: []
    }
  }

  /**
   * 模拟3G网络环境进行测试
   */
  async simulate3GTest(imageUrls) {
    console.log('[PerformanceMonitor] Starting 3G simulation test...')
    
    const results = []
    const startTime = performance.now()
    
    for (const url of imageUrls) {
      const imgStart = performance.now()
      
      try {
        await new Promise((resolve, reject) => {
          const img = new Image()
          img.onload = () => {
            const duration = performance.now() - imgStart
            results.push({ url, duration, success: true })
            resolve()
          }
          img.onerror = () => {
            const duration = performance.now() - imgStart
            results.push({ url, duration, success: false })
            reject()
          }
          img.src = url
        })
      } catch (e) {
        // 图片加载失败
      }
    }
    
    const totalDuration = performance.now() - startTime
    const successfulLoads = results.filter(r => r.success)
    const avgDuration = successfulLoads.length > 0 
      ? successfulLoads.reduce((sum, r) => sum + r.duration, 0) / successfulLoads.length 
      : 0
    
    const report = {
      totalImages: imageUrls.length,
      successfulLoads: successfulLoads.length,
      failedLoads: results.length - successfulLoads.length,
      avgLoadTime: avgDuration.toFixed(2) + 'ms',
      totalTime: totalDuration.toFixed(2) + 'ms',
      meet3GStandard: successfulLoads.every(r => r.duration <= 3000),
      details: results
    }
    
    console.log('[PerformanceMonitor] 3G Test Results:', report)
    return report
  }
}

// 导出单例
export const performanceMonitor = new PerformanceMonitor()

export default performanceMonitor
