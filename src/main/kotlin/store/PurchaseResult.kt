package store

data class PurchaseResult(
    val requestProduct: RequestProduct,
    val applyCount: Int,
    val totalPrice: Int,
    val discountPrice: Int = 0
)