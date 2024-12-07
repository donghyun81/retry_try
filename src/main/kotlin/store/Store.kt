package store

class Store(private val products: List<Product>, private val promotion: List<Promotion>) {

    fun getProducts() = products.toList()

    fun getApplyProduct(purchaseProduct: RequestProduct): RequestProduct {
        val promotionStock =
            products.find { product -> purchaseProduct.name == product.name } ?: return purchaseProduct.copy(count = 0)
        val promotion = promotion.find { it.name == promotionStock.name } ?: return purchaseProduct.copy(count = 0)
        val totalEventCount = promotion.buy + promotion.get
        val remainPromotionCount = purchaseProduct.count % totalEventCount
        if (remainPromotionCount >= promotion.buy) {
            return purchaseProduct.copy(count = totalEventCount - remainPromotionCount)
        }
        return purchaseProduct.copy(count = 0)
    }
}