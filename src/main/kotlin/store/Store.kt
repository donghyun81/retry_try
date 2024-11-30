package store

import camp.nextstep.edu.missionutils.DateTimes
import java.time.LocalDate
import javax.swing.text.DateFormatter

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

    private fun isPromotion(purchaseProduct: PurchaseProduct): Boolean {
        val product = findProduct(purchaseProduct.name) ?: return false
        val promotion = findPromotion(product) ?: return false
        val currentDate = DateTimes.now().toLocalDate()
        val startDate = LocalDate.parse(promotion.startDate)
        val endDate = LocalDate.parse(promotion.endDate)
        return currentDate in startDate..endDate
    }

    fun getPurchaseResult(purchaseProduct: PurchaseProduct): PurchaseResult {
        val product = findProduct(purchaseProduct.name) ?: return PurchaseResult(purchaseProduct, 0)
        val promotion = findPromotion(product) ?: return PurchaseResult(purchaseProduct, 0)
        val applyCount = purchaseProduct.count / promotion.buy
        return PurchaseResult(purchaseProduct, applyCount)
    }

    fun isOutOfStock(purchaseProduct: PurchaseProduct): Boolean {
        val product = findProduct(purchaseProduct.name) ?: return false
        return product.quantity < purchaseProduct.count
    }

    private fun findProduct(name: String) = products.find { name == it.name }

    private fun findPromotion(product: Product) = promotions.find { product.promotion == it.name }
}