import { defineStore } from 'pinia'
import { ref, computed, watch } from 'vue'
import { showToast } from 'vant'

export const useCartStore = defineStore('cart', () => {
  const savedCart = localStorage.getItem('cola_cart')
  const cartList = ref<any[]>(savedCart ? JSON.parse(savedCart) : [])
  
  // 👇 新增：用于暂存“立即购买”的单件商品
  const buyNowItem = ref<any>(null)

  // 设置立即购买商品
  const setBuyNowItem = (item: any) => {
    buyNowItem.value = item
  }

  // 添加购物车
  const addToCart = (product: any, sku: any) => {
    const skuId = sku.selectedSkuComb.id
    const existingItem = cartList.value.find((item: any) => item.skuId === skuId)
    
    const cartItem = {
      id: product.id,
      skuId: skuId,
      name: product.title,
      price: sku.selectedSkuComb.price,
      img: sku.selectedSkuComb.imgUrl || product.images[0],
      desc: sku.selectedSkuComb.s1_name ? `${sku.selectedSkuComb.s1_name} / ${sku.selectedSkuComb.s2_name}` : '默认规格',
      count: sku.selectedNum,
      checked: true
    }

    if (existingItem) {
      existingItem.count += sku.selectedNum
    } else {
      cartList.value.push(cartItem)
    }
    showToast('已加入购物车')
  }

  const removeItems = (ids: any[]) => {
    cartList.value = cartList.value.filter((item: any) => !ids.includes(item.skuId))
  }

  const totalPrice = computed(() => {
    return cartList.value
      .filter((item: any) => item.checked)
      .reduce((sum: number, item: any) => sum + item.price * item.count, 0)
  })

  const totalCount = computed(() => cartList.value.length)

  watch(cartList, (newVal) => {
    localStorage.setItem('cola_cart', JSON.stringify(newVal))
  }, { deep: true })

  return { cartList, buyNowItem, setBuyNowItem, addToCart, removeItems, totalPrice, totalCount }
})