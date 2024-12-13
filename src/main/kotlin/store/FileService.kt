package store

import java.io.FileReader

class FileService {

    fun readProducts(): List<Product> {
        val products = FileReader("src/main/resources/products.md").readLines().drop(1).map { productFile ->
            val (name, priceInput, quantityInput, promotion) = productFile.split(",")
            Product(name, priceInput.toInt(), quantityInput.toInt(), promotion.nullOrPromotion())
        }.toMutableList()
        val onlyPromotionStock =
            products.filter { product -> product.promotion != null && products.count { it.name == product.name } == 1 }
        onlyPromotionStock.forEach { stock -> products.add(stock.copy(quantity = 0)) }
        return products.groupBy { it.name }.flatMap { (_, product) -> product.sortedByDescending { it.promotion } }
    }

    private fun String.nullOrPromotion(): String? {
        if (this == "null") return null
        return this
    }
}