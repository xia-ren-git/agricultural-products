import { defineStore } from 'pinia'
import { ref, computed, watch } from 'vue'
import { useUserStore } from './user'
import { getCartList, addToCart as apiAddToCart, updateCartQuantity, updateCartSelected, updateAllCartSelected, deleteCartItem, deleteSelectedCart } from '@/api/cart'

const LOCAL_CART_KEY = 'local_cart'

export const useCartStore = defineStore('cart', () => {
  const userStore = useUserStore()
  const localCart = ref([])
  const serverCart = ref([])
  const loading = ref(false)

  const isLoggedIn = computed(() => !!userStore.token)

  const cartList = computed(() => {
    return isLoggedIn.value ? serverCart.value : localCart.value
  })

  const selectedCount = computed(() => {
    return cartList.value
      .filter(item => item.selected === 1)
      .reduce((sum, item) => sum + item.quantity, 0)
  })

  const totalPrice = computed(() => {
    return cartList.value
      .filter(item => item.selected === 1)
      .reduce((sum, item) => sum + item.price * item.quantity, 0)
  })

  const hasSelected = computed(() => selectedCount.value > 0)

  const selectAll = computed({
    get: () => cartList.value.length > 0 && cartList.value.every(item => item.selected === 1),
    set: () => {}
  })

  const loadLocalCart = () => {
    try {
      const saved = localStorage.getItem(LOCAL_CART_KEY)
      if (saved) {
        localCart.value = JSON.parse(saved)
      }
    } catch (e) {
      console.error('Failed to load local cart:', e)
      localCart.value = []
    }
  }

  const saveLocalCart = () => {
    try {
      localStorage.setItem(LOCAL_CART_KEY, JSON.stringify(localCart.value))
    } catch (e) {
      console.error('Failed to save local cart:', e)
    }
  }

  const loadServerCart = async () => {
    if (!isLoggedIn.value) return
    loading.value = true
    try {
      const res = await getCartList()
      serverCart.value = res.data || []
    } catch (e) {
      console.error('Failed to load server cart:', e)
    } finally {
      loading.value = false
    }
  }

  const syncLocalCartToServer = async () => {
    if (!isLoggedIn.value || localCart.value.length === 0) return
    
    for (const item of localCart.value) {
      try {
        await apiAddToCart({ productId: item.productId, quantity: item.quantity })
      } catch (e) {
        console.error('Failed to sync item:', e)
      }
    }
    
    localCart.value = []
    localStorage.removeItem(LOCAL_CART_KEY)
    await loadServerCart()
  }

  const addToCart = async (product, quantity = 1) => {
    if (isLoggedIn.value) {
      try {
        await apiAddToCart({ productId: product.id, quantity })
        await loadServerCart()
        return true
      } catch (e) {
        console.error('Failed to add to server cart:', e)
        return false
      }
    } else {
      const existIndex = localCart.value.findIndex(item => item.productId === product.id)
      if (existIndex > -1) {
        localCart.value[existIndex].quantity += quantity
      } else {
        localCart.value.push({
          id: Date.now(),
          productId: product.id,
          productName: product.name,
          productImage: product.image,
          price: product.price,
          productStock: product.stock,
          productStatus: product.status,
          quantity,
          selected: 1
        })
      }
      saveLocalCart()
      return true
    }
  }

  const updateQuantity = async (itemId, quantity) => {
    if (isLoggedIn.value) {
      try {
        await updateCartQuantity(itemId, quantity)
        const item = serverCart.value.find(i => i.id === itemId)
        if (item) item.quantity = quantity
      } catch (e) {
        console.error('Failed to update quantity:', e)
      }
    } else {
      const item = localCart.value.find(i => i.id === itemId)
      if (item) {
        item.quantity = Math.max(1, Math.min(quantity, item.productStock))
        saveLocalCart()
      }
    }
  }

  const updateSelected = async (itemId, selected) => {
    if (isLoggedIn.value) {
      try {
        await updateCartSelected(itemId, selected)
        const item = serverCart.value.find(i => i.id === itemId)
        if (item) item.selected = selected
      } catch (e) {
        console.error('Failed to update selected:', e)
      }
    } else {
      const item = localCart.value.find(i => i.id === itemId)
      if (item) {
        item.selected = selected
        saveLocalCart()
      }
    }
  }

  const updateAllSelected = async (selected) => {
    if (isLoggedIn.value) {
      try {
        await updateAllCartSelected(selected)
        serverCart.value.forEach(item => item.selected = selected)
      } catch (e) {
        console.error('Failed to update all selected:', e)
      }
    } else {
      localCart.value.forEach(item => item.selected = selected)
      saveLocalCart()
    }
  }

  const deleteItem = async (itemId) => {
    if (isLoggedIn.value) {
      try {
        await deleteCartItem(itemId)
        serverCart.value = serverCart.value.filter(i => i.id !== itemId)
      } catch (e) {
        console.error('Failed to delete item:', e)
      }
    } else {
      localCart.value = localCart.value.filter(i => i.id !== itemId)
      saveLocalCart()
    }
  }

  const deleteSelected = async () => {
    if (isLoggedIn.value) {
      try {
        await deleteSelectedCart()
        serverCart.value = serverCart.value.filter(i => i.selected !== 1)
      } catch (e) {
        console.error('Failed to delete selected:', e)
      }
    } else {
      localCart.value = localCart.value.filter(i => i.selected !== 1)
      saveLocalCart()
    }
  }

  const init = async () => {
    loadLocalCart()
    if (isLoggedIn.value) {
      await syncLocalCartToServer()
    }
  }

  watch(() => userStore.token, async (newToken, oldToken) => {
    if (newToken && !oldToken) {
      await syncLocalCartToServer()
    } else if (!newToken) {
      serverCart.value = []
      loadLocalCart()
    }
  })

  return {
    cartList,
    loading,
    selectedCount,
    totalPrice,
    hasSelected,
    selectAll,
    init,
    loadServerCart,
    addToCart,
    updateQuantity,
    updateSelected,
    updateAllSelected,
    deleteItem,
    deleteSelected
  }
})
