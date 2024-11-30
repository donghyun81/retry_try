package store

import java.io.FileReader

class StoreController {
    private val outputView = OutputView()

    fun run() {
        outputView.printStart()
        val products = getProducts()
    }

    private fun getProducts(): List<Product> {
        val file = FileReader("src/main/resources/products.md").readLines().drop(1).map { productsInput ->
            createProduct(productsInput)
        }
        return file
    }

    private fun createProduct(productsInput: String): Product {
        val (name, priceInput, quantityInput, promotion) = productsInput.split(",")
        val price = requireNotNull(priceInput.toIntOrNull()) { "[ERROR] 파일 형식이 잘못되었습니다." }
        val quantity = requireNotNull(quantityInput.toIntOrNull()) { "[ERROR] 파일 형식이 잘못되었습니다." }
        return Product(name, price, quantity, promotion)
    }
}