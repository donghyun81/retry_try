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
        val excludeQuantity = purchaseProduct.count % (promotion.buy + promotion.get)
        val applyCount = excludeQuantity.minus(promotion.buy).coerceAtLeast(0)
        return RequestProduct(product.name, applyCount)
    }


    fun getPurchaseResult(requestProduct: RequestProduct): PurchaseResult {
        val product = findProduct(requestProduct.name) ?: throw IllegalArgumentException()
        val promotion = findPromotion(product) ?: throw IllegalArgumentException()
        val applyCount = requestProduct.count / promotion.buy
        val buyCount = requestProduct.count - applyCount
        buyPromotionProducts(requestProduct)
        return PurchaseResult(
            requestProduct,
            applyCount,
            requestProduct.count * product.price,
            applyCount * product.price
        )
    }

    fun isOutOfStock(requestProduct: RequestProduct): Boolean {
        val product = findProduct(requestProduct.name) ?: return false
        return product.getQuantity < requestProduct.count
    }

    private fun buyPromotionProducts(requestProduct: RequestProduct) {
        var currentPurchaseProduct = requestProduct.count
        products.filter { requestProduct.name == it.name }.forEach { product ->
            product.buyQuantity(currentPurchaseProduct)
            currentPurchaseProduct -= product.getQuantity
            if (currentPurchaseProduct < 0) return
        }
    }

    private fun findProduct(name: String) = products.find { name == it.name }

    private fun findPromotion(product: Product) = promotions.find { product.promotion == it.name }
}