<template>
  <div class="cart-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的购物车</span>
          <el-button type="danger" @click="handleDeleteSelected" :disabled="!cartStore.hasSelected">删除选中</el-button>
        </div>
      </template>

      <el-empty v-if="cartStore.cartList.length === 0" description="购物车是空的，快去选购商品吧">
        <el-button type="primary" @click="router.push('/shop')">去购物</el-button>
      </el-empty>

      <template v-else>
        <el-table :data="cartStore.cartList" v-loading="cartStore.loading">
          <el-table-column label="选择" width="60">
            <template #default="{ row }">
              <el-checkbox :model-value="row.selected === 1" @change="(val) => handleSelectItem(row, val)" />
            </template>
          </el-table-column>
          <el-table-column label="商品" width="300">
            <template #default="{ row }">
              <div class="product-cell">
                <img :src="getImageUrl(row.productImage)" class="product-img" />
                <div class="product-info">
                  <div class="product-name">{{ row.productName }}</div>
                  <el-tag v-if="row.productStatus !== 1" type="danger" size="small">已下架</el-tag>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="单价" width="120">
            <template #default="{ row }">
              <span class="price">¥{{ row.price }}</span>
            </template>
          </el-table-column>
          <el-table-column label="数量" width="150">
            <template #default="{ row }">
              <el-input-number
                :model-value="row.quantity"
                :min="1"
                :max="row.productStock"
                size="small"
                @change="(val) => handleUpdateQuantity(row, val)"
              />
            </template>
          </el-table-column>
          <el-table-column label="小计" width="120">
            <template #default="{ row }">
              <span class="subtotal">¥{{ (row.price * row.quantity).toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button type="danger" link @click="handleDeleteItem(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="cart-footer">
          <div class="select-all">
            <el-checkbox :model-value="cartStore.selectAll" @change="handleSelectAll">全选</el-checkbox>
          </div>
          <div class="total-info">
            <span>已选择 {{ cartStore.selectedCount }} 件商品</span>
            <span class="total-price">合计：<em>¥{{ cartStore.totalPrice.toFixed(2) }}</em></span>
            <el-button type="primary" size="large" @click="goToCheckout" :disabled="cartStore.selectedCount === 0">
              去结算
            </el-button>
          </div>
        </div>
      </template>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useCartStore } from '@/store/cart'

const router = useRouter()
const cartStore = useCartStore()

const getImageUrl = (url) => {
  if (!url) return '/placeholder.jpg'
  if (url.startsWith('http')) return url
  return 'http://localhost:8080' + url
}

const handleSelectItem = async (row, selected) => {
  await cartStore.updateSelected(row.id, selected ? 1 : 0)
}

const handleSelectAll = async (selected) => {
  await cartStore.updateAllSelected(selected ? 1 : 0)
}

const handleUpdateQuantity = async (row, quantity) => {
  if (quantity < 1 || quantity > row.productStock) {
    ElMessage.warning(`数量必须在1-${row.productStock}之间`)
    return
  }
  await cartStore.updateQuantity(row.id, quantity)
}

const handleDeleteItem = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await cartStore.deleteItem(row.id)
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleDeleteSelected = async () => {
  try {
    await ElMessageBox.confirm('确定要删除选中的商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await cartStore.deleteSelected()
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const goToCheckout = () => {
  router.push('/checkout')
}

onMounted(() => {
  cartStore.init()
})
</script>

<style scoped>
.cart-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-cell {
  display: flex;
  align-items: center;
}

.product-img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  margin-right: 10px;
}

.product-info {
  flex: 1;
}

.product-name {
  font-size: 14px;
  margin-bottom: 5px;
}

.price {
  color: #666;
}

.subtotal {
  color: #f56c6c;
  font-weight: bold;
}

.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.total-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.total-price {
  font-size: 14px;
}

.total-price em {
  font-size: 24px;
  color: #f56c6c;
  font-style: normal;
  font-weight: bold;
}
</style>
