package store

import camp.nextstep.edu.missionutils.DateTimes
import java.time.LocalDate

class Store(initProducts: List<Product>, private val promotions: List<Promotion>) {

    private var products = initProducts.toMutableList()

    init {
        initProducts()
    }

    private fun initProducts() {
        products.replaceAll { product ->
            if (product.promotion == "null") product.copy(promotion = null)
            else product
        }
        val productOnlyPromotion =
            products.filter { product -> product.promotion != null && containsSameName(product.name) }
        productOnlyPromotion.forEach { product ->
            products.add(product.copy(quantity = 0, promotion = null))
        }
        products =
            products.groupBy { it.name }.flatMap { (_, products) -> products.sortedByDescending { it.promotion } }
                .toMutableList()
    }

    private fun containsSameName(name: String) = products.count { it.name == name } == 1

    fun getProducts() = products.toList()

    fun isPromotion(requestProduct: RequestProduct): Boolean {
        val product = findProduct(requestProduct.name) ?: return false
        val promotion = findPromotion(product) ?: return false
        val currentDate = DateTimes.now().toLocalDate()
        val startDate = LocalDate.parse(promotion.startDate)
        val endDate = LocalDate.parse(promotion.endDate)
        return currentDate in startDate..endDate
    }

    fun getAddApplyProduct(purchaseProduct: RequestProduct): RequestProduct {
        val product = findProduct(purchaseProduct.name) ?: throw IllegalArgumentException()
        val promotion = findPromotion(product) ?: throw IllegalArgumentException()
        if (product.getQuantity() <= purchaseProduct.count) return purchaseProduct.copy(count = 0)
        val totalEventCount = promotion.get + promotion.buy
        val excludeCount = purchaseProduct.count % (totalEventCount)
        val addApplyCount = if (excludeCount >= promotion.get) totalEventCount - excludeCount else 0
        return RequestProduct(purchaseProduct.name, addApplyCount)
    }

    fun getExcludePromotionProduct(purchaseProduct: RequestProduct): RequestProduct {
        val product = findProduct(purchaseProduct.name) ?: throw IllegalArgumentException()
        val promotion = findPromotion(product) ?: throw IllegalArgumentException()
        if (product.getQuantity() >= purchaseProduct.count) return purchaseProduct.copy(count = 0)
        val totalEventCount = promotion.get + promotion.buy
        val promotionCount = product.getQuantity().div(totalEventCount) * totalEventCount
        return purchaseProduct.copy(count = purchaseProduct.count - promotionCount)
    }

    private fun getApplyCount(purchaseProduct: RequestProduct): Int {
        if (isPromotion(purchaseProduct).not()) return 0
        val product = findProduct(purchaseProduct.name) ?: throw IllegalArgumentException()
        val promotion = findPromotion(product) ?: return 0
        if (purchaseProduct.count >= product.getQuantity()) return product.getQuantity()
            .div(promotion.get + promotion.buy) * promotion.get
        return purchaseProduct.count.div(promotion.get + promotion.buy) * promotion.get
    }

    private fun getPromotionPrice(applyCount: Int, product: Product): Int {
        val promotion = findPromotion(product) ?: return 0
        val totalEventCount = promotion.get + promotion.buy
        return (applyCount * totalEventCount) * product.price
    }

    fun buyProducts(requestProduct: RequestProduct): PurchaseResult {
        var currentPurchaseProduct = requestProduct.count
        val applyCount = getApplyCount(requestProduct)
        products.filter { requestProduct.name == it.name }.forEach { product ->
            val buyCount = product.buyQuantity(currentPurchaseProduct)
            currentPurchaseProduct -= buyCount
        }
        val product = findProduct(requestProduct.name) ?: throw IllegalArgumentException()
        val totalPrice = requestProduct.count * product.price
        val promotionPrice = getPromotionPrice(applyCount, product)
        return PurchaseResult(requestProduct, totalPrice, applyCount, promotionPrice, applyCount * product.price)
    }

    private fun findProduct(name: String) = products.find { name == it.name }

    private fun findPromotion(product: Product) = promotions.find { product.promotion == it.name }
}