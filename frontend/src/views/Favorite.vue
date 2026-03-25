<template>
  <div class="favorite-container">
    <el-card>
      <template #header>
        <span>我的收藏</span>
      </template>

      <el-empty v-if="favorites.length === 0" description="暂无收藏">
        <el-button type="primary" @click="router.push('/shop')">去购物</el-button>
      </el-empty>

      <div class="favorite-list" v-loading="loading" v-else>
        <div class="favorite-grid">
          <div v-for="item in favorites" :key="item.id" class="favorite-item">
            <div class="product-image" @click="goToDetail(item.productId)">
              <img :src="getImageUrl(item.productImage)" />
            </div>
            <div class="product-info">
              <h4 class="product-name" @click="goToDetail(item.productId)">{{ item.productName }}</h4>
              <div class="product-meta">
                <span class="price">¥{{ item.price }}</span>
                <el-tag v-if="item.productStatus !== 1" type="danger" size="small">已下架</el-tag>
                <el-tag v-else type="success" size="small">在售</el-tag>
              </div>
              <div class="product-actions">
                <el-button 
                  type="primary" 
                  size="small" 
                  :loading="addingToCart[item.productId]"
                  :disabled="item.productStatus !== 1"
                  @click="handleAddToCart(item)"
                >
                  加入购物车
                </el-button>
                <el-button type="danger" size="small" @click="removeFavorite(item)">
                  取消收藏
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFavoriteList, removeFromFavorite } from '@/api/favorite'
import { useCartStore } from '@/store/cart'

const router = useRouter()
const cartStore = useCartStore()
const loading = ref(false)
const favorites = ref([])
const addingToCart = ref({})

const loadFavorites = async () => {
  loading.value = true
  try {
    const res = await getFavoriteList()
    favorites.value = res.data || []
  } catch (error) {
    console.error(error)
    ElMessage.error('加载收藏列表失败')
  } finally {
    loading.value = false
  }
}

const getImageUrl = (url) => {
  if (!url) return '/placeholder.jpg'
  if (url.startsWith('http')) return url
  return 'http://localhost:8080' + url
}

const goToDetail = (productId) => {
  router.push(`/product/${productId}`)
}

const handleAddToCart = async (item) => {
  if (item.productStatus !== 1) {
    ElMessage.warning('该商品已下架')
    return
  }
  
  addingToCart.value[item.productId] = true
  try {
    const success = await cartStore.addToCart({
      id: item.productId,
      name: item.productName,
      image: item.productImage,
      price: item.price,
      status: item.productStatus,
      stock: 999
    }, 1)
    if (success) {
      ElMessage.success('已加入购物车')
    } else {
      ElMessage.error('加入购物车失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('加入购物车失败')
  } finally {
    addingToCart.value[item.productId] = false
  }
}

const removeFavorite = async (item) => {
  try {
    await ElMessageBox.confirm('确定要取消收藏该商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await removeFromFavorite(item.productId)
    ElMessage.success('已取消收藏')
    loadFavorites()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
      ElMessage.error('操作失败')
    }
  }
}

onMounted(() => {
  loadFavorites()
  cartStore.init()
})
</script>

<style scoped>
.favorite-container {
  padding: 20px;
}

.favorite-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.favorite-item {
  border: 1px solid #eee;
  border-radius: 4px;
  overflow: hidden;
}

.product-image {
  cursor: pointer;
}

.product-image img {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.product-info {
  padding: 15px;
}

.product-name {
  font-size: 16px;
  margin: 0 0 10px;
  cursor: pointer;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-name:hover {
  color: #409eff;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.price {
  font-size: 18px;
  color: #f56c6c;
  font-weight: bold;
}

.product-actions {
  display: flex;
  gap: 10px;
}
</style>
