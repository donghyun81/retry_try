package store

import java.io.FileReader

class StoreController {
    private val outputView = OutputView()
    private val inputView = InputView()

    fun run() {
        outputView.printStart()
        val products = getProducts()
        val promotion = getPromotions()
        val store = Store(products, promotion)
        outputView.printProducts(store.getProducts())
        val purchaseProducts = inputView.inputPurchaseProduct(store.getProducts())
    }

    private fun getProducts(): List<Product> {
        val products = FileReader("src/main/resources/products.md").readLines().drop(1).map { productsInput ->
            createProduct(productsInput)
        }
        return products
    }

    private fun createProduct(productsInput: String): Product {
        val (name, priceInput, quantityInput, promotion) = productsInput.split(",")
        val price = requireNotNull(priceInput.toIntOrNull()) { "[ERROR] 파일 형식이 잘못되었습니다." }
        val quantity = requireNotNull(quantityInput.toIntOrNull()) { "[ERROR] 파일 형식이 잘못되었습니다." }
        return Product(name, price, quantity, promotion)
    }

    private fun getPromotions(): List<Promotion> {
        val promotions = FileReader("src/main/resources/promotions.md").readLines().drop(1).map { productsInput ->
            createPromotion(productsInput)
        }
        return promotions
    }

    private fun createPromotion(productsInput: String): Promotion {
        val (name, buyInput, getInput, startDate, endDate) = productsInput.split(",")
        val buy = requireNotNull(buyInput.toIntOrNull()) { "[ERROR] 파일 형식이 잘못되었습니다." }
        val get = requireNotNull(getInput.toIntOrNull()) { "[ERROR] 파일 형식이 잘못되었습니다." }
        return Promotion(name, buy, get, startDate, endDate)
    }
}