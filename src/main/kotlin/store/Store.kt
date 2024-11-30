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

    fun isPromotion(purchaseProduct: PurchaseProduct): Boolean {
        val product = products.find { purchaseProduct.name == it.name } ?: return false
        val promotion = promotions.find { product.promotion == it.name } ?: return false
        val currentDate = DateTimes.now().toLocalDate()
        val startDate = LocalDate.parse(promotion.startDate)
        val endDate = LocalDate.parse(promotion.endDate)
        return currentDate in startDate..endDate
    }
}