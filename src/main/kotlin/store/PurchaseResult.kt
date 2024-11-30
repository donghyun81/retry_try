package store

data class PurchaseResult(
    val requestProduct: RequestProduct,
    val totalPrice: Int,
    val applyCount: Int = 0,
    val applyPrice: Int = 0
)