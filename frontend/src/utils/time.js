/**
 * 全局时间格式化函数
 * @param {string|Date} time - 时间值
 * @param {string} format - 格式化模式，默认 'YYYY-MM-DD HH:mm:ss'
 * @returns {string} 格式化后的时间字符串
 */
export function formatTime(time, format = 'YYYY-MM-DD HH:mm:ss') {
  if (!time) return '-'
  
  let date
  if (typeof time === 'string') {
    date = new Date(time.replace(/-/g, '/'))
  } else if (time instanceof Date) {
    date = time
  } else {
    return '-'
  }
  
  if (isNaN(date.getTime())) return '-'
  
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  
  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds)
}

/**
 * 格式化为日期（不含时间）
 */
export function formatDate(time) {
  return formatTime(time, 'YYYY-MM-DD')
}

/**
 * 格式化为简短时间
 */
export function formatShortTime(time) {
  return formatTime(time, 'MM-DD HH:mm')
}

/**
 * 获取相对时间描述
 */
export function formatRelativeTime(time) {
  if (!time) return '-'
  
  let date
  if (typeof time === 'string') {
    date = new Date(time.replace(/-/g, '/'))
  } else if (time instanceof Date) {
    date = time
  } else {
    return '-'
  }
  
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  
  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour
  const week = 7 * day
  const month = 30 * day
  
  if (diff < minute) {
    return '刚刚'
  } else if (diff < hour) {
    return Math.floor(diff / minute) + '分钟前'
  } else if (diff < day) {
    return Math.floor(diff / hour) + '小时前'
  } else if (diff < week) {
    return Math.floor(diff / day) + '天前'
  } else if (diff < month) {
    return Math.floor(diff / week) + '周前'
  } else {
    return formatDate(time)
  }
}
