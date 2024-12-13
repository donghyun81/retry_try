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

    fun getPurchaseResult(purchaseProduct: PurchaseProduct): PurchaseResult {
        var currentPurchaseCount = purchaseProduct.count
        val stocks = products.filter { it.name == purchaseProduct.name }
        stocks.forEach { stock ->
            currentPurchaseCount -= buyProduct(stock, currentPurchaseCount)
        }
        return PurchaseResult(purchaseProduct, purchaseProduct.count * stocks[0].price)
    }

    private fun buyProduct(stock: Product, buyCount: Int): Int {
        val stockIndex = products.indexOfFirst { stock == it }
        products[stockIndex] = stock.copy(quantity = stock.quantity - buyCount.coerceAtMost(stock.quantity))
        return buyCount.coerceAtMost(stock.quantity)
    }
}