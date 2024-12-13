package store

data class PurchaseResult(
    val purchaseProduct: PurchaseProduct,
    val totalPrice: Int,
    val applyCount: Int = 0,
    val applyDiscount: Int = 0
)