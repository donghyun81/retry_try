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

    fun buyProducts(requestProduct: RequestProduct): PurchaseResult {
        var currentPurchaseProduct = requestProduct.count
        products.filter { requestProduct.name == it.name }.forEach { product ->
            val buyCount = product.buyQuantity(currentPurchaseProduct)
            currentPurchaseProduct -= buyCount
        }
        val product = findProduct(requestProduct.name) ?: throw IllegalArgumentException()
        val totalPrice = requestProduct.count * product.price
        return PurchaseResult(requestProduct, totalPrice)
    }

    private fun findProduct(name: String) = products.find { name == it.name }

    private fun findPromotion(product: Product) = promotions.find { product.promotion == it.name }
}