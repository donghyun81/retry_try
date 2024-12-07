package store

import java.io.FileReader

class StoreController {
    private val outputView = OutputView()
    private val inputView = InputView()

    fun run() {
        val store = initStore()
        while (true) {
            outputView.printProducts(store.getProducts())
            val purchaseProducts = retryInput { inputView.readPurchaseProducts(store.getProducts()) }
            val buyProductResults = getBuyProductsResult(purchaseProducts, store)
            val isMembership = retryInput { inputView.readIsMembership() }
            outputView.printReceipt(buyProductResults, isMembership)
            if (inputView.readIsRetryPurchase().not()) break
        }
    }

    private fun initStore(): Store {
        outputView.printStart()
        return Store(readProducts(), readPromotions())
    }

    private fun readProducts(): List<Product> {
        val products = FileReader(File.PRODUCTS.path).readLines().drop(ATTRIBUTE_LINE).map { productLine ->
            val (name, price, quantity, promotion) = productLine.split(DEFAULT_DELIMITERS)
            Product(name, price.toInt(), quantity.toInt(), promotion.validateNull())
        }.toMutableList()
        val onlyPromotionProduct =
            products.filter { product -> product.promotion != null && products.count { it.name == product.name } == ONLY_COUNT }
        onlyPromotionProduct.forEach { product -> products.add(product.copy(quantity = INIT_NUMBER)) }
        return products.groupBy { it.name }.flatMap { (_, value) -> value.sortedByDescending { it.promotion } }
    }

    private fun readPromotions(): List<Promotion> {
        return FileReader(File.PROMOTIONS.path).readLines().drop(ATTRIBUTE_LINE).map { promotionLine ->
            val (name, buyInput, getInput, startDate, endDate) = promotionLine.split(DEFAULT_DELIMITERS)
            Promotion(name, buyInput.toInt(), getInput.toInt(), startDate, endDate)
        }
    }

    private fun getPromotionPurchaseProduct(purchaseProduct: RequestProduct, store: Store): RequestProduct {
        val applyProduct = store.getAddApplyProduct(purchaseProduct)
        if (applyProduct.count > INIT_NUMBER) return getApplyPurchaseProduct(applyProduct, purchaseProduct)
        val excludeProduct = store.getExcludeProduct(purchaseProduct)
        if (excludeProduct.count > INIT_NUMBER) return getExcludePurchaseProduct(excludeProduct, purchaseProduct)
        return purchaseProduct
    }

    private fun getApplyPurchaseProduct(applyProduct: RequestProduct, purchaseProduct: RequestProduct): RequestProduct {
        val isAddApply = retryInput { inputView.readIsAddApplyProduct(applyProduct) }
        return if (isAddApply) purchaseProduct.copy(count = purchaseProduct.count + applyProduct.count) else purchaseProduct
    }

    private fun getExcludePurchaseProduct(
        excludeProduct: RequestProduct,
        purchaseProduct: RequestProduct
    ): RequestProduct {
        val isExclude = retryInput { inputView.readIsExcludeProduct(excludeProduct) }
        return if (isExclude) purchaseProduct.copy(count = purchaseProduct.count - excludeProduct.count) else purchaseProduct
    }

    private fun getBuyProductsResult(purchaseProducts: List<RequestProduct>, store: Store): List<PurchaseResult> {
        return purchaseProducts.map { purchaseProduct ->
            if (store.isPromotionProduct(purchaseProduct)) store.buyProduct(
                getPromotionPurchaseProduct(purchaseProduct, store)
            )
            else store.buyProduct(purchaseProduct)
        }
    }

    private fun String.validateNull(): String? {
        if (this == EMPTY_PROMOTION_TEXT) return null
        return this
    }

    companion object {
        private const val EMPTY_PROMOTION_TEXT = "null"
        private const val ATTRIBUTE_LINE = 1
        private const val ONLY_COUNT = 1
    }
}