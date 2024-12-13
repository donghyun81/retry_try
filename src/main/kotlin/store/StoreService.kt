package store

class StoreService(initProducts: List<Product>, private val promotions: List<Promotion>) {

    private val products = initProducts.toMutableList()

    fun getProducts() = products.toList()

    fun getPurchaseResult(purchaseProduct: PurchaseProduct): PurchaseResult {
        var currentPurchaseCount = purchaseProduct.count
        val stocks = products.filter { it.name == purchaseProduct.name }
        stocks.forEach { stock ->
            currentPurchaseCount -= buyProduct(stock, currentPurchaseCount)
        }
        return PurchaseResult(purchaseProduct, purchaseProduct.count * stocks[0].price)
    }

    private fun buyProduct(stock: Product, buyCount: Int): Int {
        val stockIndex = products.indexOfFirst { stock == it }
        products[stockIndex] = stock.copy(quantity = stock.quantity - buyCount.coerceAtMost(stock.quantity))
        return buyCount.coerceAtMost(stock.quantity)
    }
}