package store

import java.io.FileReader

class StoreController {
    private val outputView = OutputView()

    fun run() {
        outputView.printStart()
        val products = readProducts()
        outputView.printProducts(products)
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

    private fun String.validateNull(): String? {
        if (this == "null") return null
        return this
    }
}