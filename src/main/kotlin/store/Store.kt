package store

class Store(private val products: List<Product>, private val promotion: List<Promotion>) {

    fun getProducts() = products.toList()
}