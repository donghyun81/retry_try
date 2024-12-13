package store

import camp.nextstep.edu.missionutils.Console

class InputView {
    fun readPurchaseProducts(products: List<Product>): List<RequestProduct> {
        println(Guide.PURCHASE.message)
        val purchaseProductInput = Console.readLine().split(DEFAULT_DELIMITERS)
        println()
        val confirmedPurchaseProductInput = confirmPurchaseProduct(purchaseProductInput)
        val purchaseProducts = getPurchaseProducts(confirmedPurchaseProductInput)
        validatePurChaseProducts(products, purchaseProducts)
        return purchaseProducts
    }

    private fun confirmPurchaseProduct(purchaseProductInput: List<String>): List<String> {
        return purchaseProductInput.map { input ->
            require(input.first() == PURCHASE_PREFIX && input.last() == PURCHASE_SUFFIX) { Error.FORM.getMessage() }
            input.removeSurrounding("[", "]")
        }
    }

    private fun getPurchaseProducts(confirmedPurchaseProductInput: List<String>): List<RequestProduct> {
        return confirmedPurchaseProductInput.map { input ->
            require(input.split(PURCHASE_DELIMITERS).size == 2) { Error.FORM.getMessage() }
            val (name, countInput) = input.split(PURCHASE_DELIMITERS)
            val count = requireNotNull(countInput.toIntOrNull()) { Error.FORM.getMessage() }
            RequestProduct(name, count)
        }.groupBy { it.name }.map { (name, product) ->
            val totalCount = product.sumOf { it.count }
            RequestProduct(name, totalCount)
        }
    }

    private fun validatePurChaseProducts(products: List<Product>, purchaseProducts: List<RequestProduct>) {
        purchaseProducts.forEach { purchaseProduct ->
            val stock = products.filter { purchaseProduct.name == it.name }
            require(stock.isEmpty().not()) { Error.EMPTY_PRODUCT.getMessage() }
            require(purchaseProduct.count <= stock.sumOf { it.quantity }) { Error.OVER_QUANTITY.getMessage() }
        }
    }

    fun readIsAddApplyProduct(applyProduct: RequestProduct): Boolean {
        println(Guide.ADD_APPLY_PROMOTION_FORM.message.format(applyProduct.name, applyProduct.count))
        val isApplyProductInput = Console.readLine()
        println()
        require(isApplyProductInput == ACCEPT || isApplyProductInput == REJECT) { Error.OTHER.getMessage() }
        return isApplyProductInput == ACCEPT
    }

    fun readIsExcludeProduct(excludeProduct: RequestProduct): Boolean {
        println(Guide.EXCLUDE_PROMOTION_FORM.message.format(excludeProduct.name, excludeProduct.count))
        val isExcludeProduct = Console.readLine()
        println()
        require(isExcludeProduct == ACCEPT || isExcludeProduct == REJECT) { Error.OTHER.getMessage() }
        return isExcludeProduct == REJECT
    }

    fun readIsMembership(): Boolean {
        println(Guide.MEMBERSHIP.message)
        val isMemberShip = Console.readLine()
        require(isMemberShip == ACCEPT || isMemberShip == REJECT) { Error.OTHER.getMessage() }
        return isMemberShip == ACCEPT
    }

    fun readIsRetryPurchase(): Boolean {
        println(Guide.RETRY.message)
        val isRetryPurchase = Console.readLine()
        require(isRetryPurchase == ACCEPT || isRetryPurchase == REJECT) { Error.OTHER.getMessage() }
        return isRetryPurchase == ACCEPT
    }

    companion object {
        private const val ACCEPT = "Y"
        private const val REJECT = "N"
        private const val PURCHASE_DELIMITERS = "-"
        private const val PURCHASE_PREFIX = '['
        private const val PURCHASE_SUFFIX = ']'
    }
}