package store

import java.text.DecimalFormat

class OutputView {

    fun printStart() {
        println("안녕하세요. W편의점입니다.\n")
    }

    fun printStocks(products: List<Product>) {
        products.forEach { product ->
            println("- ${product.name} ${product.price.wonFormat()}원 ${product.quantity.emptyStock()} ${product.promotion ?: ""}")
        }
    }

    fun printReceipt(purchaseResults: List<PurchaseResult>, memberShipDiscount: Int) {
        println("===========W 편의점=============")
        println("상품명\t\t수량\t금액")
        purchaseResults.forEach { purchaseResult ->
            println("${purchaseResult.purchaseProduct.name}\t\t${purchaseResult.purchaseProduct.count} \t${purchaseResult.totalPrice.wonFormat()}")
        }
        println("===========증\t정=============")
        purchaseResults.forEach { purchaseResult ->
            if (purchaseResult.applyCount > 0) {
                println("${purchaseResult.purchaseProduct.name}\t\t${purchaseResult.applyCount}")
            }
        }
        val totalPrice = purchaseResults.sumOf { it.totalPrice }
        val eventDiscount = purchaseResults.sumOf { it.applyDiscount }
        println("==============================")
        println("총구매액\t\t${purchaseResults.sumOf { it.purchaseProduct.count }}\t${purchaseResults.sumOf { it.totalPrice }.wonFormat()}")
        println("행사할인\t\t\t-${eventDiscount.wonFormat()}")
        println("멤버십할인\t\t\t-${memberShipDiscount.wonFormat()}")
        println("내실돈\t\t\t ${(totalPrice - eventDiscount - memberShipDiscount).wonFormat()}")
    }

    private fun Int.wonFormat(): String {
        val formatter = DecimalFormat("#,###")
        return formatter.format(this)
    }

    private fun Int.emptyStock(): String {
        if (this == 0) return "재고 없음"
        return this.toString() + "개"
    }
}