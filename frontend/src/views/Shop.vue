<template>
  <div class="shop-container">
    <el-card>
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="搜索商品" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="searchForm.categoryId" placeholder="全部分类" clearable style="width: 140px">
            <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格区间">
          <el-input-number v-model="searchForm.minPrice" :min="0" :precision="2" placeholder="最低价" style="width: 140px" />
          <span class="price-separator">-</span>
          <el-input-number v-model="searchForm.maxPrice" :min="0" :precision="2" placeholder="最高价" style="width: 140px" />
        </el-form-item>
        <el-form-item label="排序">
          <el-select v-model="searchForm.orderBy" placeholder="默认排序" style="width: 140px">
            <el-option label="最新上架" value="create_time" />
            <el-option label="销量优先" value="sales" />
            <el-option label="价格升序" value="price_asc" />
            <el-option label="价格降序" value="price_desc" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <div class="product-stats" v-if="total > 0">
      <span>共找到 <em>{{ total }}</em> 件商品</span>
    </div>

    <div
      class="product-grid"
      v-loading="loading"
      element-loading-text="加载商品中..."
    >
      <article
        v-for="product in products"
        :key="product.id"
        class="product-card"
        @click="goToDetail(product.id)"
      >
        <div class="product-image-wrapper">
          <ProductImage
            :src="product.image"
            :alt="product.name"
            :height="220"
            :lazy="true"
            :widths="[320, 640, 960]"
            :quality="80"
            sizes="(max-width: 768px) 50vw, (max-width: 1400px) 33vw, 260px"
            @click="handleImageClick(product)"
          />
          <div class="product-badge" v-if="product.sales > 100">热销</div>
          <div class="product-overlay">
            <el-button type="primary" size="small" @click.stop="handleQuickView(product)">
              <el-icon><View /></el-icon>
              快速查看
            </el-button>
          </div>
        </div>
        <div class="product-info">
          <h3 class="product-name" :title="product.name">{{ product.name }}</h3>
          <p class="product-desc" :title="product.description">{{ product.description || '暂无描述' }}</p>
          <div class="product-meta">
            <div class="product-price-wrapper">
              <span class="product-price">¥{{ product.price }}</span>
              <span class="product-unit">/{{ product.unit || '件' }}</span>
            </div>
            <span class="product-sales">已售{{ product.sales || 0 }}</span>
          </div>
          <div class="product-actions">
            <el-tag v-if="product.stock <= 0" type="danger" size="small">已售罄</el-tag>
            <el-tag v-else-if="product.stock < 10" type="warning" size="small">仅剩{{ product.stock }}</el-tag>
            <el-button
              v-if="product.status === 1 && product.stock > 0"
              type="primary"
              size="small"
              :loading="addingToCart[product.id]"
              @click.stop="handleAddToCart(product)"
            >
              <el-icon><ShoppingCart /></el-icon>
              加入购物车
            </el-button>
            <el-button
              v-else-if="product.status !== 1"
              type="info"
              size="small"
              disabled
            >
              商品已下架
            </el-button>
          </div>
        </div>
      </article>
    </div>

    <el-empty v-if="!loading && products.length === 0" description="暂无商品" />

    <div class="pagination-wrapper" v-if="total > 0">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[12, 24, 48]"
        :background="true"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>

    <ImagePreview
      v-model="previewVisible"
      :image-list="previewImages"
      :initial-index="previewIndex"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Refresh, ShoppingCart, View } from '@element-plus/icons-vue'
import { searchProducts } from '@/api/product'
import { getCategoryList } from '@/api/category'
import { useCartStore } from '@/store/cart'
import { useUserStore } from '@/store/user'
import { smartPreload, imageUtils } from '@/utils/imageUtils'
import { performanceMonitor } from '@/utils/performanceMonitor'
import ProductImage from '@/components/ProductImage.vue'
import ImagePreview from '@/components/ImagePreview.vue'

const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()

const loading = ref(false)
const products = ref([])
const categories = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(12)
const addingToCart = ref({})
const previewVisible = ref(false)
const previewImages = ref([])
const previewIndex = ref(0)

const searchForm = reactive({
  keyword: '',
  categoryId: null,
  minPrice: null,
  maxPrice: null,
  orderBy: 'create_time'
})

const loadCategories = async () => {
  try {
    const res = await getCategoryList()
    categories.value = res.data || []
  } catch (error) {
    console.error('[Shop] Failed to load categories:', error)
  }
}

const loadProducts = async () => {
  loading.value = true
  try {
    const res = await searchProducts({
      keyword: searchForm.keyword,
      categoryId: searchForm.categoryId,
      minPrice: searchForm.minPrice,
      maxPrice: searchForm.maxPrice,
      orderBy: searchForm.orderBy,
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })
    products.value = res.data.list || []
    total.value = res.data.total || 0

    preloadNearbyImages()
  } catch (error) {
    console.error('[Shop] Failed to load products:', error)
    ElMessage.error('加载商品失败')
  } finally {
    loading.value = false
  }
}

const preloadNearbyImages = () => {
  const nextProducts = products.value.slice(0, 4)
  const urls = nextProducts
    .map(p => {
      if (!p.image) return null
      return imageUtils.getImageUrl(p.image, { width: 640, quality: 80 })
    })
    .filter(Boolean)

  if (urls.length > 0) {
    smartPreload(urls, {
      concurrency: 2,
      onProgress: (loaded, total) => {
        console.log(`[Shop] Preloaded ${loaded}/${total} images`)
      }
    })
  }
}

const handleSearch = () => {
  if (searchForm.minPrice && searchForm.maxPrice && searchForm.minPrice > searchForm.maxPrice) {
    ElMessage.warning('最低价不能大于最高价')
    return
  }
  pageNum.value = 1
  loadProducts()
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.categoryId = null
  searchForm.minPrice = null
  searchForm.maxPrice = null
  searchForm.orderBy = 'create_time'
  pageNum.value = 1
  loadProducts()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  pageNum.value = 1
  loadProducts()
}

const handlePageChange = (page) => {
  pageNum.value = page
  loadProducts()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const goToDetail = (id) => {
  router.push(`/product/${id}`)
}

const handleImageClick = (product) => {
  if (product.image) {
    previewImages.value = [product.image]
    previewIndex.value = 0
    previewVisible.value = true
  }
}

const handleQuickView = (product) => {
  handleImageClick(product)
}

const handleAddToCart = async (product) => {
  if (product.stock <= 0) {
    ElMessage.warning('商品已售罄')
    return
  }

  addingToCart.value[product.id] = true

  try {
    const success = await cartStore.addToCart(product, 1)
    if (success) {
      ElMessage.success('已加入购物车')
    } else {
      ElMessage.error('加入购物车失败')
    }
  } catch (error) {
    console.error('[Shop] Failed to add to cart:', error)
    ElMessage.error('加入购物车失败')
  } finally {
    addingToCart.value[product.id] = false
  }
}

onMounted(() => {
  loadCategories()
  loadProducts()
  cartStore.init()
  
  // 启动性能监控
  performanceMonitor.start()
  
  // 页面卸载时输出性能报告
  window.addEventListener('beforeunload', () => {
    const report = performanceMonitor.getReport()
    console.log('[Shop] Performance Report:', report)
  })
})
</script>

<style scoped>
.shop-container {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}

.price-separator {
  margin: 0 8px;
  color: #999;
}

.product-stats {
  margin-bottom: 15px;
  padding: 10px 15px;
  background: #f5f7fa;
  border-radius: 4px;
  font-size: 14px;
  color: #666;
}

.product-stats em {
  color: #409eff;
  font-style: normal;
  font-weight: bold;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.product-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  border: 1px solid #eee;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.product-image-wrapper {
  position: relative;
  overflow: hidden;
}

.product-badge {
  position: absolute;
  top: 10px;
  left: 10px;
  padding: 4px 10px;
  background: linear-gradient(135deg, #ff6b6b, #ff4757);
  color: #fff;
  font-size: 12px;
  border-radius: 4px;
  z-index: 3;
}

.product-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
  z-index: 2;
}

.product-card:hover .product-overlay {
  opacity: 1;
}

.product-info {
  padding: 15px;
}

.product-name {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #333;
}

.product-desc {
  font-size: 12px;
  color: #999;
  margin: 0 0 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.product-price-wrapper {
  display: flex;
  align-items: baseline;
}

.product-price {
  font-size: 20px;
  font-weight: bold;
  color: #f56c6c;
}

.product-unit {
  font-size: 12px;
  color: #999;
  margin-left: 2px;
}

.product-sales {
  font-size: 12px;
  color: #999;
}

.product-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

@media (max-width: 768px) {
  .product-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
  }

  .product-info {
    padding: 10px;
  }

  .product-name {
    font-size: 14px;
  }

  .product-price {
    font-size: 16px;
  }
}

@media (min-width: 1400px) {
  .product-grid {
    grid-template-columns: repeat(5, 1fr);
  }
}
</style>
