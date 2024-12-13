package store

data class PurchaseResult(
    val purchaseProduct: PurchaseProduct,
    val applyCount: Int,
    val totalPrice: Int,
    val applyDiscount: Int
)