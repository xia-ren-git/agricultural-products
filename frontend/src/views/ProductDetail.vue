<template>
  <div class="detail-container">
    <el-card v-loading="loading">
      <div class="product-detail" v-if="product">
        <div class="product-image">
          <img :src="getImageUrl(product.image)" />
        </div>
        <div class="product-info">
          <h1 class="product-name">{{ product.name }}</h1>
          <p class="product-desc">{{ product.description }}</p>
          <div class="product-meta">
            <div class="meta-item">
              <span class="label">价格：</span>
              <span class="price">¥{{ product.price }}</span>
            </div>
            <div class="meta-item">
              <span class="label">库存：</span>
              <span>{{ product.stock }} {{ product.unit }}</span>
            </div>
            <div class="meta-item">
              <span class="label">销量：</span>
              <span>{{ product.sales }}</span>
            </div>
            <div class="meta-item">
              <span class="label">产地：</span>
              <span>{{ product.origin }}</span>
            </div>
            <div class="meta-item">
              <span class="label">分类：</span>
              <span>{{ product.categoryName }}</span>
            </div>
          </div>
          <div class="product-rating">
            <el-rate v-model="avgRating" disabled show-score text-color="#ff9900" />
            <span class="rating-count">({{ reviewCount }}条评价)</span>
          </div>
          <div class="product-actions">
            <div class="quantity-selector">
              <span class="label">数量：</span>
              <el-input-number v-model="quantity" :min="1" :max="product.stock" />
            </div>
            <div class="action-buttons">
              <el-button 
                type="primary" 
                size="large" 
                :loading="addingToCart"
                :disabled="!product || product.stock <= 0"
                @click="handleAddToCart"
              >
                <el-icon><ShoppingCart /></el-icon>
                {{ !product || product.stock <= 0 ? '已售罄' : '加入购物车' }}
              </el-button>
              <el-button 
                :type="isFavorite ? 'danger' : 'default'" 
                size="large" 
                @click="handleToggleFavorite"
              >
                <el-icon><Star /></el-icon>
                {{ isFavorite ? '取消收藏' : '收藏' }}
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <el-card class="review-card">
      <template #header>
        <div class="card-header">
          <span>商品评价</span>
          <div class="header-actions">
            <el-select v-model="reviewSort" size="small" style="width: 120px; margin-right: 10px;">
              <el-option label="最新" value="newest" />
              <el-option label="最热" value="hottest" />
            </el-select>
            <el-button type="primary" @click="showReviewDialog" v-if="canReview">发表评价</el-button>
          </div>
        </div>
      </template>

      <div class="review-list" v-if="sortedReviews.length > 0">
        <div v-for="review in sortedReviews" :key="review.id" class="review-item">
          <div class="review-header">
            <el-avatar :src="getImageUrl(review.userAvatar)" :size="40">{{ review.userName }}</el-avatar>
            <div class="review-info">
              <div class="user-name">{{ review.userName }}</div>
              <el-rate v-model="review.rating" disabled />
            </div>
            <div class="review-time">{{ formatTime(review.createTime) }}</div>
            <div class="review-actions" v-if="isReviewOwner(review)">
              <el-button type="primary" link size="small" @click="editReview(review)">编辑</el-button>
              <el-button type="danger" link size="small" @click="deleteReviewItem(review)">删除</el-button>
            </div>
          </div>
          <div class="review-content">{{ review.content }}</div>
          <div class="review-images" v-if="review.images">
            <el-image
              v-for="(img, index) in review.images.split(',')"
              :key="index"
              :src="getImageUrl(img)"
              :preview-src-list="review.images.split(',').map(i => getImageUrl(i))"
              fit="cover"
              class="review-image"
            />
          </div>
          <div class="review-footer">
            <div class="like-btn" @click="handleToggleLike(review)">
              <el-icon :class="{ liked: review.isLiked }"><Pointer /></el-icon>
              <span>{{ review.likeCount || 0 }}</span>
            </div>
            <div class="reply-btn" @click="showReplyDialog(review)">
              <el-icon><ChatDotRound /></el-icon>
              <span>回复</span>
            </div>
          </div>
          
          <div class="replies" v-if="review.replies && review.replies.length > 0">
            <div v-for="reply in review.replies" :key="reply.id" class="reply-item">
              <div class="reply-header">
                <el-avatar :src="getImageUrl(reply.userAvatar)" :size="28">{{ reply.userName }}</el-avatar>
                <span class="reply-user">{{ reply.userName }}</span>
                <span class="reply-time">{{ formatTime(reply.createTime) }}</span>
                <div class="reply-actions" v-if="isReviewOwner(reply)">
                  <el-button type="danger" link size="small" @click="deleteReviewItem(reply)">删除</el-button>
                </div>
              </div>
              <div class="reply-content">{{ reply.content }}</div>
              <div class="reply-footer">
                <div class="like-btn small" @click="handleToggleLike(reply)">
                  <el-icon :class="{ liked: reply.isLiked }"><Pointer /></el-icon>
                  <span>{{ reply.likeCount || 0 }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无评价" />
    </el-card>

    <el-dialog v-model="reviewDialogVisible" :title="isEditMode ? '编辑评价' : '发表评价'" width="500px">
      <el-form :model="reviewForm" label-width="80px">
        <el-form-item label="评分" v-if="!isEditMode || reviewForm.rating > 0">
          <el-rate v-model="reviewForm.rating" show-text />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input 
            v-model="reviewForm.content" 
            type="textarea" 
            :rows="4" 
            placeholder="请输入评价内容（最多500字）" 
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="图片">
          <el-upload
            v-model:file-list="reviewImageList"
            action="/api/upload"
            list-type="picture-card"
            :headers="uploadHeaders"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :on-remove="handleUploadRemove"
            :on-preview="handlePreview"
            :limit="3"
            accept="image/*"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">最多上传3张图片</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReview" :loading="submitting">{{ isEditMode ? '保存' : '提交' }}</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="replyDialogVisible" title="回复评论" width="450px">
      <el-form :model="replyForm" label-width="60px">
        <el-form-item label="内容">
          <el-input 
            v-model="replyForm.content" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入回复内容（最多500字）"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="replyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReply" :loading="submitting">回复</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ShoppingCart, Star, Pointer, ChatDotRound, Plus } from '@element-plus/icons-vue'
import { getProductById } from '@/api/product'
import { addToFavorite, removeFromFavorite, checkFavorite } from '@/api/favorite'
import { getProductReviews, addReview, replyReview, updateReview, deleteReview as deleteReviewApi, toggleReviewLike, getReviewStats } from '@/api/review'
import { formatTime } from '@/utils/time'
import { useUserStore } from '@/store/user'
import { useCartStore } from '@/store/cart'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const loading = ref(false)
const product = ref(null)
const quantity = ref(1)
const isFavorite = ref(false)
const reviews = ref([])
const avgRating = ref(0)
const reviewCount = ref(0)
const reviewDialogVisible = ref(false)
const replyDialogVisible = ref(false)
const isEditMode = ref(false)
const editingReviewId = ref(null)
const addingToCart = ref(false)
const submitting = ref(false)
const reviewSort = ref('newest')
const reviewImageList = ref([])

const uploadHeaders = computed(() => ({
  Authorization: userStore.token ? `Bearer ${userStore.token}` : ''
}))

const reviewForm = reactive({
  rating: 5,
  content: '',
  images: ''
})

const replyForm = reactive({
  parentId: null,
  content: ''
})

const canReview = computed(() => {
  return userStore.userInfo && userStore.userInfo.username !== 'admin'
})

const isReviewOwner = (review) => {
  return userStore.userInfo && review.userId === userStore.userInfo.id
}

const getImageUrl = (url) => {
  if (!url) return '/placeholder.jpg'
  if (url.startsWith('http')) return url
  return 'http://localhost:8080' + url
}

const sortedReviews = computed(() => {
  const list = [...reviews.value]
  if (reviewSort.value === 'hottest') {
    return list.sort((a, b) => (b.likeCount || 0) - (a.likeCount || 0))
  }
  return list
})

const loadProduct = async () => {
  loading.value = true
  try {
    const res = await getProductById(route.params.id)
    product.value = res.data
  } catch (error) {
    console.error(error)
    ElMessage.error('加载商品失败')
  } finally {
    loading.value = false
  }
}

const loadFavoriteStatus = async () => {
  if (!userStore.userInfo) return
  try {
    const res = await checkFavorite(route.params.id)
    isFavorite.value = res.data.isFavorite
  } catch (error) {
    console.error(error)
  }
}

const loadReviews = async () => {
  try {
    const res = await getProductReviews(route.params.id)
    reviews.value = res.data || []
  } catch (error) {
    console.error(error)
  }
}

const loadReviewStats = async () => {
  try {
    const res = await getReviewStats(route.params.id)
    avgRating.value = res.data.avgRating
    reviewCount.value = res.data.count
  } catch (error) {
    console.error(error)
  }
}

const handleAddToCart = async () => {
  if (!product.value) return
  
  if (product.value.stock <= 0) {
    ElMessage.warning('商品已售罄')
    return
  }
  
  if (quantity.value > product.value.stock) {
    ElMessage.warning(`库存不足，最多可购买${product.value.stock}件`)
    return
  }
  
  addingToCart.value = true
  try {
    const success = await cartStore.addToCart(product.value, quantity.value)
    if (success) {
      ElMessage.success('已加入购物车')
    } else {
      ElMessage.error('加入购物车失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('加入购物车失败')
  } finally {
    addingToCart.value = false
  }
}

const handleToggleFavorite = async () => {
  if (!userStore.userInfo) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    if (isFavorite.value) {
      await removeFromFavorite(product.value.id)
      ElMessage.success('已取消收藏')
    } else {
      await addToFavorite(product.value.id)
      ElMessage.success('收藏成功')
    }
    isFavorite.value = !isFavorite.value
  } catch (error) {
    console.error(error)
    ElMessage.error('操作失败')
  }
}

const showReviewDialog = () => {
  isEditMode.value = false
  editingReviewId.value = null
  reviewForm.rating = 5
  reviewForm.content = ''
  reviewForm.images = ''
  reviewImageList.value = []
  reviewDialogVisible.value = true
}

const editReview = (review) => {
  isEditMode.value = true
  editingReviewId.value = review.id
  reviewForm.rating = review.rating
  reviewForm.content = review.content
  reviewForm.images = review.images || ''
  if (review.images) {
    reviewImageList.value = review.images.split(',').filter(Boolean).map((url, index) => ({
      name: `image-${index}`,
      url: getImageUrl(url)
    }))
  } else {
    reviewImageList.value = []
  }
  reviewDialogVisible.value = true
}

const showReplyDialog = (review) => {
  if (!userStore.userInfo) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  replyForm.parentId = review.id
  replyForm.content = ''
  replyDialogVisible.value = true
}

const handleUploadSuccess = (response, file) => {
  if (response.code === 200 && response.data && response.data.url) {
    const images = reviewForm.images ? reviewForm.images.split(',').filter(Boolean) : []
    images.push(response.data.url)
    reviewForm.images = images.join(',')
    // 设置完整URL以便预览显示
    file.url = getImageUrl(response.data.url)
  } else {
    ElMessage.error(response.message || '上传失败')
    const index = reviewImageList.value.findIndex(f => f.uid === file.uid)
    if (index > -1) {
      reviewImageList.value.splice(index, 1)
    }
  }
}

const handleUploadError = (error, file) => {
  ElMessage.error('图片上传失败，请重试')
  const index = reviewImageList.value.findIndex(f => f.uid === file.uid)
  if (index > -1) {
    reviewImageList.value.splice(index, 1)
  }
}

const handlePreview = (file) => {
  if (file.url) {
    window.open(getImageUrl(file.url), '_blank')
  }
}

const handleUploadRemove = (file) => {
  let imageToRemove = ''
  if (file.response && file.response.data && file.response.data.url) {
    imageToRemove = file.response.data.url
  } else if (file.url) {
    // 处理编辑时已上传的图片，file.url可能是完整URL，需要提取相对路径
    imageToRemove = file.url.replace('http://localhost:8080', '')
  }
  if (imageToRemove) {
    const images = reviewForm.images ? reviewForm.images.split(',').filter(Boolean) : []
    const index = images.findIndex(img => img === imageToRemove)
    if (index > -1) {
      images.splice(index, 1)
      reviewForm.images = images.join(',')
    }
  }
}

const deleteReviewItem = async (review) => {
  try {
    await ElMessageBox.confirm('确定要删除该评价吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteReviewApi(review.id)
    ElMessage.success('删除成功')
    loadReviews()
    loadReviewStats()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleToggleLike = async (review) => {
  if (!userStore.userInfo) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    const res = await toggleReviewLike(review.id)
    review.isLiked = res.data.isLiked
    review.likeCount = res.data.likeCount
  } catch (error) {
    console.error(error)
    ElMessage.error('操作失败')
  }
}

const submitReview = async () => {
  if (!reviewForm.content || reviewForm.content.trim().length === 0) {
    ElMessage.warning('请输入评价内容')
    return
  }
  if (reviewForm.content.length > 500) {
    ElMessage.warning('评价内容不能超过500字')
    return
  }
  
  submitting.value = true
  try {
    if (isEditMode.value) {
      await updateReview(editingReviewId.value, {
        rating: reviewForm.rating,
        content: reviewForm.content,
        images: reviewForm.images
      })
      ElMessage.success('修改成功')
    } else {
      await addReview({
        productId: product.value.id,
        rating: reviewForm.rating,
        content: reviewForm.content,
        images: reviewForm.images
      })
      ElMessage.success('评价成功')
    }
    reviewDialogVisible.value = false
    loadReviews()
    loadReviewStats()
  } catch (error) {
    console.error(error)
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

const submitReply = async () => {
  if (!replyForm.content || replyForm.content.trim().length === 0) {
    ElMessage.warning('请输入回复内容')
    return
  }
  
  submitting.value = true
  try {
    await replyReview({
      parentId: replyForm.parentId,
      content: replyForm.content
    })
    ElMessage.success('回复成功')
    replyDialogVisible.value = false
    loadReviews()
  } catch (error) {
    console.error(error)
    ElMessage.error('回复失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadProduct()
  loadFavoriteStatus()
  loadReviews()
  loadReviewStats()
  cartStore.init()
})
</script>

<style scoped>
.detail-container {
  padding: 20px;
}

.product-detail {
  display: flex;
  gap: 40px;
}

.product-image {
  flex: 0 0 400px;
}

.product-image img {
  width: 100%;
  height: 400px;
  object-fit: cover;
}

.product-info {
  flex: 1;
}

.product-name {
  font-size: 24px;
  margin: 0 0 15px;
}

.product-desc {
  color: #666;
  margin-bottom: 20px;
}

.product-meta {
  background: #f5f5f5;
  padding: 15px;
  margin-bottom: 20px;
}

.meta-item {
  margin-bottom: 10px;
}

.meta-item:last-child {
  margin-bottom: 0;
}

.meta-item .label {
  color: #999;
  margin-right: 10px;
}

.meta-item .price {
  font-size: 24px;
  color: #f56c6c;
  font-weight: bold;
}

.product-rating {
  margin-bottom: 20px;
}

.rating-count {
  margin-left: 10px;
  color: #999;
}

.product-actions {
  margin-top: 20px;
}

.quantity-selector {
  margin-bottom: 15px;
}

.quantity-selector .label {
  margin-right: 10px;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.review-card {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  align-items: center;
}

.review-item {
  padding: 15px 0;
  border-bottom: 1px solid #eee;
}

.review-item:last-child {
  border-bottom: none;
}

.review-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.review-info {
  flex: 1;
  margin-left: 10px;
}

.user-name {
  font-weight: bold;
  margin-bottom: 5px;
}

.review-time {
  color: #999;
  font-size: 12px;
}

.review-actions {
  margin-left: 15px;
}

.review-content {
  margin-bottom: 10px;
  line-height: 1.6;
}

.review-images {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}

.review-image {
  width: 80px;
  height: 80px;
}

.review-footer {
  display: flex;
  gap: 20px;
  margin-top: 10px;
}

.like-btn, .reply-btn {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
  color: #666;
  font-size: 14px;
}

.like-btn:hover, .reply-btn:hover {
  color: #409eff;
}

.like-btn .liked {
  color: #f56c6c;
}

.like-btn.small {
  font-size: 12px;
}

.replies {
  margin-top: 15px;
  margin-left: 50px;
  padding-left: 15px;
  border-left: 2px solid #eee;
}

.reply-item {
  padding: 10px 0;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 5px;
}

.reply-user {
  font-weight: 500;
  font-size: 13px;
}

.reply-time {
  color: #999;
  font-size: 11px;
}

.reply-actions {
  margin-left: auto;
}

.reply-content {
  font-size: 13px;
  color: #333;
  line-height: 1.5;
}

.reply-footer {
  margin-top: 5px;
}

.upload-tip {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}
</style>
