package store

data class Product(
    val name: String, val price: Int, private var quantity: Int, val promotion: String?
) {
    fun getQuantity() = quantity

    fun buyQuantity(count: Int): Int {
        quantity -= count.coerceAtLeast(0)
        return count.coerceAtMost(quantity)
    }
}