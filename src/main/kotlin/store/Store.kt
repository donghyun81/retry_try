package store

import camp.nextstep.edu.missionutils.DateTimes
import java.time.LocalDate

class Store(private val products: List<Product>, private val promotions: List<Promotion>) {

    fun getProducts() = products.toList()

    fun isPromotionProduct(purchaseProduct: RequestProduct): Boolean {
        val promotionStock = products.find { it.promotion != null && purchaseProduct.name == it.name } ?: return false
        val promotion = promotions.find { it.name == promotionStock.promotion } ?: return false
        val currentDate = DateTimes.now().toLocalDate()
        val startDate = LocalDate.parse(promotion.startDate)
        val endDate = LocalDate.parse(promotion.endDate)
        return currentDate in startDate..endDate
    }

    fun getAddApplyProduct(purchaseProduct: RequestProduct): RequestProduct {
        val promotionStock =
            products.find { product -> purchaseProduct.name == product.name }
                ?: return purchaseProduct.copy(count = INIT_NUMBER)
        val promotion =
            promotions.find { it.name == promotionStock.promotion } ?: return purchaseProduct.copy(count = INIT_NUMBER)
        val totalEventCount = promotion.buy + promotion.get
        val remainPromotionCount = purchaseProduct.count % totalEventCount
        if (remainPromotionCount >= promotion.buy) {
            return purchaseProduct.copy(count = totalEventCount - remainPromotionCount)
        }
        return purchaseProduct.copy(count = INIT_NUMBER)
    }

    fun getExcludeProduct(purchaseProduct: RequestProduct): RequestProduct {
        val promotionStock =
            products.find { product -> purchaseProduct.name == product.name }
                ?: return purchaseProduct.copy(count = INIT_NUMBER)
        val promotion =
            promotions.find { it.name == promotionStock.promotion } ?: return purchaseProduct.copy(count = INIT_NUMBER)
        val totalEventCount = promotion.buy + promotion.get
        val promotionCount =
            if (promotionStock.getQuantity() >= purchaseProduct.count) purchaseProduct.count.div(totalEventCount) * totalEventCount
            else promotionStock.getQuantity().div(totalEventCount) * totalEventCount
        if (purchaseProduct.count > promotionStock.getQuantity()) return purchaseProduct.copy(count = purchaseProduct.count - promotionCount)
        return purchaseProduct.copy(count = INIT_NUMBER)
    }

    fun buyProduct(purchaseProduct: RequestProduct): PurchaseResult {
        val stocks = products.filter { it.name == purchaseProduct.name }
        val promotionCount = getPromotionCount(purchaseProduct)
        val applyCount = getApplyCount(purchaseProduct)
        var currentBuyCount = purchaseProduct.count
        stocks.forEach { product ->
            val buyCount = product.buyProduct(currentBuyCount)
            currentBuyCount -= buyCount
        }
        return PurchaseResult(purchaseProduct, applyCount, stocks.first().price, promotionCount)
    }

    private fun getApplyCount(purchaseProduct: RequestProduct): Int {
        if (isPromotionProduct(purchaseProduct).not()) return 0
        val promotionStock =
            products.find { product -> purchaseProduct.name == product.name && product.promotion != null }
                ?: return INIT_NUMBER
        val promotion = promotions.find { it.name == promotionStock.promotion } ?: return INIT_NUMBER
        val totalEventCount = promotion.buy + promotion.get
        if (purchaseProduct.count >= promotionStock.getQuantity()) return (promotionStock.getQuantity() / totalEventCount) * promotion.get
        return (purchaseProduct.count / totalEventCount) * promotion.get
    }

    private fun getPromotionCount(purchaseProduct: RequestProduct): Int {
        if (isPromotionProduct(purchaseProduct).not()) return 0
        val promotionStock =
            products.find { product -> purchaseProduct.name == product.name && product.promotion != null }
                ?: return INIT_NUMBER
        val promotion = promotions.find { it.name == promotionStock.promotion } ?: return INIT_NUMBER
        val totalEventCount = promotion.buy + promotion.get
        if (purchaseProduct.count >= promotionStock.getQuantity()) return getApplyCount(purchaseProduct) * totalEventCount
        return getApplyCount(purchaseProduct) * totalEventCount
    }
}