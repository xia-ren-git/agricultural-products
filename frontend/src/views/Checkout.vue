<template>
  <div class="checkout-container">
    <el-card>
      <template #header>
        <span>确认订单</span>
      </template>

      <div class="checkout-content" v-loading="loading">
        <div class="section">
          <h3>收货信息</h3>
          <el-form :model="orderForm" :rules="rules" ref="formRef" label-width="100px">
            <el-form-item label="收货人" prop="receiverName">
              <el-input v-model="orderForm.receiverName" placeholder="请输入收货人姓名" />
            </el-form-item>
            <el-form-item label="联系电话" prop="receiverPhone">
              <el-input v-model="orderForm.receiverPhone" placeholder="请输入联系电话" />
            </el-form-item>
            <el-form-item label="收货地址" prop="receiverAddress">
              <el-input v-model="orderForm.receiverAddress" type="textarea" :rows="2" placeholder="请输入详细地址" />
            </el-form-item>
            <el-form-item label="备注">
              <el-input v-model="orderForm.remark" placeholder="选填，可以告诉卖家您的特殊需求" />
            </el-form-item>
          </el-form>
        </div>

        <div class="section">
          <h3>商品清单</h3>
          <el-table :data="selectedItems" border>
            <el-table-column label="商品" width="300">
              <template #default="{ row }">
                <div class="product-cell">
                  <img :src="row.productImage || '/placeholder.jpg'" class="product-img" />
                  <div class="product-info">
                    <div class="product-name">{{ row.productName }}</div>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="单价" width="120">
              <template #default="{ row }">¥{{ row.price }}</template>
            </el-table-column>
            <el-table-column label="数量" width="100">
              <template #default="{ row }">{{ row.quantity }}</template>
            </el-table-column>
            <el-table-column label="小计" width="120">
              <template #default="{ row }">
                <span class="subtotal">¥{{ (row.price * row.quantity).toFixed(2) }}</span>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <div class="section total-section">
          <div class="total-info">
            <span>共 {{ totalCount }} 件商品</span>
            <span class="total-price">
              应付金额：<em>¥{{ totalPrice.toFixed(2) }}</em>
            </span>
          </div>
        </div>

        <div class="checkout-actions">
          <el-button @click="goBack">返回购物车</el-button>
          <el-button type="primary" size="large" @click="submitOrder" :loading="submitting">
            提交订单
          </el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCartList } from '@/api/cart'
import { createOrder } from '@/api/order'

const router = useRouter()
const loading = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const selectedItems = ref([])

const orderForm = reactive({
  receiverName: '',
  receiverPhone: '',
  receiverAddress: '',
  remark: ''
})

const rules = {
  receiverName: [
    { required: true, message: '请输入收货人姓名', trigger: 'blur' }
  ],
  receiverPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  receiverAddress: [
    { required: true, message: '请输入收货地址', trigger: 'blur' }
  ]
}

const totalCount = computed(() => {
  return selectedItems.value.reduce((sum, item) => sum + item.quantity, 0)
})

const totalPrice = computed(() => {
  return selectedItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
})

const loadSelectedItems = async () => {
  loading.value = true
  try {
    const res = await getCartList()
    const allItems = res.data || []
    selectedItems.value = allItems.filter(item => item.selected === 1)
    
    if (selectedItems.value.length === 0) {
      ElMessage.warning('请先选择要购买的商品')
      router.push('/cart')
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.push('/cart')
}

const submitOrder = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const res = await createOrder(orderForm)
    ElMessage.success('订单创建成功')
    router.push('/my-order')
  } catch (error) {
    console.error(error)
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadSelectedItems()
})
</script>

<style scoped>
.checkout-container {
  padding: 20px;
}

.section {
  margin-bottom: 30px;
}

.section h3 {
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
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

.product-name {
  font-size: 14px;
}

.subtotal {
  color: #f56c6c;
  font-weight: bold;
}

.total-section {
  background: #f5f5f5;
  padding: 15px;
  border-radius: 4px;
}

.total-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.checkout-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
