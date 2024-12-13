package store

import camp.nextstep.edu.missionutils.DateTimes
import java.time.LocalDate

class Store(initProducts: List<Product>, private val promotions: List<Promotion>) {

    private val products = initProducts.toMutableList()

    fun getProducts() = products.map { it.copy() }

    fun isPromotionProduct(purchaseProduct: RequestProduct): Boolean {
        val promotionStock = products.find { it.promotion != null && purchaseProduct.name == it.name } ?: return false
        val promotion = promotions.find { it.name == promotionStock.promotion } ?: return false
        val currentDate = DateTimes.now().toLocalDate()
        val startDate = LocalDate.parse(promotion.startDate)
        val endDate = LocalDate.parse(promotion.endDate)
        return currentDate in startDate..endDate
    }

    fun getAddApplyProduct(purchaseProduct: RequestProduct): RequestProduct {
        val promotionStock = findPromotionStock(purchaseProduct) ?: return purchaseProduct.copy(count = INIT_NUMBER)
        val promotion = findPromotion(promotionStock) ?: return purchaseProduct.copy(count = INIT_NUMBER)
        val totalEventCount = promotion.buy + promotion.get
        val remainPromotionCount = purchaseProduct.count % totalEventCount
        val addApplyCount = totalEventCount - remainPromotionCount
        if (purchaseProduct.count + addApplyCount > promotionStock.quantity) return purchaseProduct.copy(count = INIT_NUMBER)
        if (remainPromotionCount >= promotion.buy) return purchaseProduct.copy(count = totalEventCount - remainPromotionCount)
        return purchaseProduct.copy(count = INIT_NUMBER)
    }

    fun getExcludeProduct(purchaseProduct: RequestProduct): RequestProduct {
        val promotionStock = findPromotionStock(purchaseProduct) ?: return purchaseProduct.copy(count = INIT_NUMBER)
        val promotion = findPromotion(promotionStock) ?: return purchaseProduct.copy(count = INIT_NUMBER)
        val totalEventCount = promotion.buy + promotion.get
        val promotionCount =
            if (promotionStock.quantity >= purchaseProduct.count) purchaseProduct.count.div(totalEventCount) * totalEventCount
            else promotionStock.quantity.div(totalEventCount) * totalEventCount
        if (purchaseProduct.count >= promotionStock.quantity) return purchaseProduct.copy(count = purchaseProduct.count - promotionCount)
        return purchaseProduct.copy(count = INIT_NUMBER)
    }

    fun getPurchaseResult(purchaseProduct: RequestProduct): PurchaseResult {
        val stocks = products.filter { it.name == purchaseProduct.name }
        val promotionCount = getPromotionCount(purchaseProduct)
        val applyCount = getApplyCount(purchaseProduct)
        var currentBuyCount = purchaseProduct.count
        stocks.forEach { product ->
            val buyCount = buyProduct(product, currentBuyCount)
            currentBuyCount -= buyCount
        }
        return PurchaseResult(purchaseProduct, applyCount, stocks.first().price, promotionCount)
    }

    private fun buyProduct(product: Product, count: Int): Int {
        val productIndex = products.indexOfFirst { it == product }
        val currentProduct = products[productIndex]
        val buyCount = count.coerceAtMost(currentProduct.quantity)
        products[productIndex] = currentProduct.copy(quantity = currentProduct.quantity - buyCount)
        return buyCount
    }

    private fun getApplyCount(purchaseProduct: RequestProduct): Int {
        if (isPromotionProduct(purchaseProduct).not()) return 0
        val promotionStock =
            products.find { product -> purchaseProduct.name == product.name && product.promotion != null }
                ?: return INIT_NUMBER
        val promotion = promotions.find { it.name == promotionStock.promotion } ?: return INIT_NUMBER
        val totalEventCount = promotion.buy + promotion.get
        if (purchaseProduct.count >= promotionStock.quantity) return (promotionStock.quantity / totalEventCount) * promotion.get
        return (purchaseProduct.count / totalEventCount) * promotion.get
    }

    private fun getPromotionCount(purchaseProduct: RequestProduct): Int {
        if (isPromotionProduct(purchaseProduct).not()) return 0
        val promotionStock = findPromotionStock(purchaseProduct) ?: return INIT_NUMBER
        val promotion = findPromotion(promotionStock) ?: return INIT_NUMBER
        val totalEventCount = promotion.buy + promotion.get
        if (purchaseProduct.count >= promotionStock.quantity) return getApplyCount(purchaseProduct) * totalEventCount
        return getApplyCount(purchaseProduct) * totalEventCount
    }

    private fun findPromotionStock(purchaseProduct: RequestProduct) =
        products.find { product -> purchaseProduct.name == product.name && product.promotion != null }


    private fun findPromotion(promotionStock: Product) = promotions.find { it.name == promotionStock.promotion }
}