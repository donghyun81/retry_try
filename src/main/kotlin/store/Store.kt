package store

class Store(initProducts: List<Product>, private val promotion: List<Promotion>) {

    private var products = initProducts.toMutableList()

    init {
        initProducts()
    }

    private fun initProducts() {
        products.replaceAll { product ->
            if (product.promotion == "null") product.copy(promotion = null)
            else product
        }
        val productOnlyPromotion =
            products.filter { product -> product.promotion != null && containsSameName(product.name) }
        productOnlyPromotion.forEach { product ->
            products.add(product.copy(quantity = 0, promotion = null))
        }
        products =
            products.groupBy { it.name }.flatMap { (_, products) -> products.sortedByDescending { it.promotion } }
                .toMutableList()
    }

    private fun containsSameName(name: String) = products.count { it.name == name } == 0

}