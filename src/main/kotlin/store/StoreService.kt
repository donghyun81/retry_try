package store

import camp.nextstep.edu.missionutils.DateTimes
import java.time.LocalDate

class StoreService(initProducts: List<Product>, private val promotions: List<Promotion>) {

    private val products = initProducts.toMutableList()

    fun getProducts() = products.toList()

    fun isPromotion(purchaseProduct: PurchaseProduct): Boolean {
        val stock = products.find { purchaseProduct.name == it.name } ?: throw IllegalArgumentException()
        val promotion = promotions.find { it.name == stock.promotion } ?: return false
        val startDate = LocalDate.parse(promotion.startDate)
        val endDate = LocalDate.parse(promotion.endDate)
        val currentDate = DateTimes.now().toLocalDate()
        return currentDate in startDate..endDate
    }

    fun getAddApply(purchaseProduct: PurchaseProduct): PurchaseProduct {
        val stock = products.find { purchaseProduct.name == it.name } ?: throw IllegalArgumentException()
        val promotion = promotions.find { it.name == stock.promotion } ?: return purchaseProduct.copy()
        val eventCount = promotion.buy + promotion.get
        val remainStock = purchaseProduct.count.coerceAtMost(stock.quantity) % eventCount
        if (remainStock >= promotion.buy) return purchaseProduct.copy(count =  eventCount - remainStock)
        return purchaseProduct.copy(count = 0)
    }

    fun getExcludeStock(purchaseProduct: PurchaseProduct): PurchaseProduct {
        val stock = products.find { purchaseProduct.name == it.name } ?: throw IllegalArgumentException()
        val promotion = promotions.find { it.name == stock.promotion } ?: return purchaseProduct.copy(count = 0)
        val eventCount = promotion.buy + promotion.get
        if (purchaseProduct.count <= stock.quantity) return purchaseProduct.copy(count = 0)
        val remainStock = purchaseProduct.count.coerceAtMost(stock.quantity) % eventCount
        return purchaseProduct.copy(count = remainStock + purchaseProduct.count - stock.quantity)
    }

    fun getPurchaseResult(purchaseProduct: PurchaseProduct): PurchaseResult {
        var currentPurchaseCount = purchaseProduct.count
        val stocks = products.filter { it.name == purchaseProduct.name }
        val applyCount = getApplyCount(purchaseProduct)
        val promotionPrice = getPromotionPrice(purchaseProduct)
        stocks.forEach { stock ->
            currentPurchaseCount -= buyProduct(stock, currentPurchaseCount)
        }
        return PurchaseResult(
            purchaseProduct,
            purchaseProduct.count * stocks[0].price,
            promotionPrice,
            applyCount,
            applyCount * stocks[0].price
        )
    }

    private fun buyProduct(stock: Product, buyCount: Int): Int {
        val stockIndex = products.indexOfFirst { stock == it }
        products[stockIndex] = stock.copy(quantity = stock.quantity - buyCount.coerceAtMost(stock.quantity))
        return buyCount.coerceAtMost(stock.quantity)
    }

    private fun getPromotionPrice(purchaseProduct: PurchaseProduct): Int {
        val stock = products.find { purchaseProduct.name == it.name } ?: throw IllegalArgumentException()
        val promotion = promotions.find { it.name == stock.promotion } ?: return 0
        val totalEvent = promotion.get + promotion.buy
        return purchaseProduct.count.coerceAtMost(stock.quantity).div(totalEvent).times(totalEvent) * stock.price
    }

    private fun getApplyCount(purchaseProduct: PurchaseProduct): Int {
        val stock = products.find { purchaseProduct.name == it.name } ?: throw IllegalArgumentException()
        val promotion = promotions.find { it.name == stock.promotion } ?: return 0
        val totalEvent = promotion.get + promotion.buy
        return purchaseProduct.count.coerceAtMost(stock.quantity).div(totalEvent).times(promotion.get)
    }
}