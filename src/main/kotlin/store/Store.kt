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

    fun getApplyProduct(purchaseProduct: RequestProduct): RequestProduct {
        val promotionStock =
            products.find { product -> purchaseProduct.name == product.name } ?: return purchaseProduct.copy(count = 0)
        val promotion = promotions.find { it.name == promotionStock.name } ?: return purchaseProduct.copy(count = 0)
        val totalEventCount = promotion.buy + promotion.get
        val remainPromotionCount = purchaseProduct.count % totalEventCount
        if (remainPromotionCount >= promotion.buy) {
            return purchaseProduct.copy(count = totalEventCount - remainPromotionCount)
        }
        return purchaseProduct.copy(count = 0)
    }
}