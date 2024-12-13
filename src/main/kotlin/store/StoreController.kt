package store

class StoreController {
    private val outputView = OutputView()
    private val inputView = InputView()
    private val fileService = FileService()

    fun run() {
        outputView.printStart()
        val products = fileService.readProducts()
        val promotion = fileService.readPromotions()
        val storeService = StoreService(products, promotion)
        while (true) {
            outputView.printStocks(storeService.getProducts())
            val purchaseProducts = retryInput { inputView.readPurchaseProducts(storeService.getProducts()) }
            val purchaseResult = purchaseProducts.map { purchaseProduct ->
                val completePurchaseProduct =
                    if (storeService.isPromotion(purchaseProduct)) getPromotionPurchaseProduct(
                        storeService,
                        purchaseProduct
                    ) else purchaseProduct
                storeService.getPurchaseResult(completePurchaseProduct)
            }
            val membershipDiscount = getMembershipDiscount(purchaseResult)
            outputView.printReceipt(purchaseResult, membershipDiscount)
            if (inputView.readIsRetry().not()) break
        }
    }

    fun getPromotionPurchaseProduct(storeService: StoreService, purchaseProduct: PurchaseProduct): PurchaseProduct {
        val applyStock = storeService.getAddApply(purchaseProduct)
        if (applyStock.count > 0) return getAddApply(applyStock, purchaseProduct)
        val excludeStock = storeService.getExcludeStock(purchaseProduct)
        if (excludeStock.count > 0) return getExcludeStock(excludeStock, purchaseProduct)
        return purchaseProduct
    }

    fun getAddApply(applyStock: PurchaseProduct, purchaseProduct: PurchaseProduct): PurchaseProduct {
        val isAddApply = retryInput { inputView.readIsAddApply(applyStock) }
        if (isAddApply) return purchaseProduct.copy(count = purchaseProduct.count + applyStock.count)
        return purchaseProduct.copy()
    }

    fun getExcludeStock(excludeStock: PurchaseProduct, purchaseProduct: PurchaseProduct): PurchaseProduct {
        val isExclude = retryInput { inputView.readIsExclude(excludeStock) }
        if (isExclude.not()) return purchaseProduct.copy(count = purchaseProduct.count - excludeStock.count)
        return purchaseProduct.copy()
    }

    fun getMembershipDiscount(purchaseResults: List<PurchaseResult>): Int {
        val totalPrice = purchaseResults.sumOf { it.totalPrice }
        val promotionPrice = purchaseResults.sumOf { it.promotionPrice }
        val isMembership = retryInput { inputView.readIsMembership() }
        if (isMembership) return ((totalPrice - promotionPrice) * 0.3).toInt().coerceAtMost(8000)
        return 0
    }
}