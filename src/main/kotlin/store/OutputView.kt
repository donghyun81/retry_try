package store

import java.text.DecimalFormat

class OutputView {
    fun printStart() {
        println(Guide.START.message)
    }

    fun printProducts(products: List<Product>) {
        println(Guide.STOCK.message)
        products.forEach { product ->
            println(
                "- ${product.name} ${product.price.wonFormat()}원 ${
                    product.getQuantity().countOrEmpty()
                } ${product.promotion ?: ""}"
            )
        }
        println()
    }

    fun printReceipt(results: List<PurchaseResult>, isMembership: Boolean) {
        println("===========W 편의점=============")
        println("상품명\t\t수량\t금액")
        results.forEach { result ->
            println("${result.purchaseProduct.name}\t\t${result.purchaseProduct.count} \t${(result.price * result.purchaseProduct.count).wonFormat()}")
        }
        println("===========증\t정=============")
        results.forEach { result ->
            if (result.applyCount > 0) {
                println("${result.purchaseProduct.name}\t\t${result.applyCount}")
            }
        }
        val totalPrice = results.sumOf { it.price * it.purchaseProduct.count }
        val applyDiscount = results.sumOf { it.applyCount * it.price }
        val membershipDiscount = if (isMembership) membershipDiscount(results) else 0
        val payment = totalPrice - applyDiscount - membershipDiscount
        println("==============================")
        println("총구매액		${results.sumOf { it.purchaseProduct.count }}	${totalPrice.wonFormat()}")
        println("행사할인			-${applyDiscount.wonFormat()}")
        println("멤버십할인			-${membershipDiscount.wonFormat()}")
        println("내실돈			 ${payment.wonFormat()}")
    }

    private fun membershipDiscount(results: List<PurchaseResult>): Int {
        val totalPrice = results.sumOf { it.price * it.purchaseProduct.count }
        val promotionPrice = results.sumOf { it.price * it.promotionCount }
        return ((totalPrice - promotionPrice) * 0.3).toInt().coerceAtMost(8000)
    }

    private fun Int.wonFormat(): String {
        val formatter = DecimalFormat("#,###")
        return formatter.format(this)
    }

    private fun Int.countOrEmpty(): String {
        if (this == 0) return "재고 없음"
        return this.toString() + "개"
    }
}