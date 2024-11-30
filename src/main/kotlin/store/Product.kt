package store

data class Product(
    val name: String, val price: Int, private var quantity: Int, val promotion: String?
) {
    val getQuantity = quantity

    fun buyQuantity(count: Int) = quantity - count
}