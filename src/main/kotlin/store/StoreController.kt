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
        retryPurchase {
            outputView.printProducts(store.getProducts())
            val purchaseProducts = retryInput { inputView.inputPurchaseProduct(store.getProducts()) }
            val purchaseResults = purchaseProducts.map { purchaseProduct ->
                val processedPurchaseProduct = getPromotionPurchaseProduct(purchaseProduct, store)
                store.buyProducts(processedPurchaseProduct)
            }
            val isMemberShip = retryInput { inputView.inputIsMemberShip() }
            outputView.printReceipt(purchaseResults, isMemberShip)
            retryInput { inputView.inputIsRetry()}
        }
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

    private fun getPromotionPurchaseProduct(purchaseProduct: RequestProduct, store: Store): RequestProduct {
        if (store.isPromotion(purchaseProduct)) {
            val addApplyProduct = store.getAddApplyProduct(purchaseProduct)
            val excludeProduct = store.getExcludePromotionProduct(purchaseProduct)
            if (addApplyProduct.count > 0) {
                return getAddApplyProduct(purchaseProduct, addApplyProduct)
            }
            if (excludeProduct.count > 0) {
                return getExcludeProduct(purchaseProduct, excludeProduct)
            }
        }
        return purchaseProduct
    }

    private fun getAddApplyProduct(purchaseProduct: RequestProduct, addApplyProduct: RequestProduct): RequestProduct {
        val isAddApplyProduct = retryInput { inputView.inputIsAddApplyProduct(addApplyProduct) }
        if (isAddApplyProduct) return purchaseProduct.copy(count = purchaseProduct.count + addApplyProduct.count)
        return purchaseProduct
    }

    private fun getExcludeProduct(purchaseProduct: RequestProduct, excludeProduct: RequestProduct): RequestProduct {
        val isExcludeProduct = retryInput { inputView.inputIsExcludeProduct(excludeProduct) }
        if (isExcludeProduct) return purchaseProduct.copy(count = purchaseProduct.count - excludeProduct.count)
        return purchaseProduct
    }
}