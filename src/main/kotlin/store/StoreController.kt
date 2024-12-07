package store

import java.io.FileReader

class StoreController {
    private val outputView = OutputView()
    private val inputView = InputView()

    fun run() {
        outputView.printStart()
        val initProducts = readProducts()
        val initPromotion = readPromotions()
        val store = Store(initProducts, initPromotion)
        outputView.printProducts(store.getProducts())
        val purchaseProducts = inputView.readPurchaseProducts(store.getProducts())
        val buyProductResults = purchaseProducts.map { purchaseProduct ->
            if (store.isPromotionProduct(purchaseProduct)) {
                val applyProduct = store.getApplyProduct(purchaseProduct)
                if (applyProduct.count > 0) {
                    inputView.readIsAddApplyProduct(applyProduct)
                }
            }
        }
    }

    private fun readProducts(): List<Product> {
        val products = FileReader("src/main/resources/products.md").readLines().drop(1).map { productLine ->
            val (name, price, quantity, promotion) = productLine.split(",")
            Product(name, price.toInt(), quantity.toInt(), promotion.validateNull())
        }.toMutableList()
        val onlyPromotionProduct =
            products.filter { product -> product.promotion != null && products.count { it.name == product.name } == 1 }
        onlyPromotionProduct.forEach { product -> products.add(product.copy(quantity = 0)) }
        return products.groupBy { it.name }.flatMap { (_, value) -> value.sortedByDescending { it.promotion } }
    }

    private fun readPromotions(): List<Promotion> {
        return FileReader("src/main/resources/promotions.md").readLines().drop(1).map { promotionLine ->
            val (name, buyInput, getInput, startDate, endDate) = promotionLine.split(",")
            Promotion(name, buyInput.toInt(), getInput.toInt(), startDate, endDate)
        }
    }

    private fun String.validateNull(): String? {
        if (this == "null") return null
        return this
    }
}