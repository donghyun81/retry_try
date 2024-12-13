package store

class StoreService(private val products: List<Product>, private val promotions: List<Promotion>) {
    fun getProducts() = products.toList()
}