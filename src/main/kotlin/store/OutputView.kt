package store

import java.text.DecimalFormat

class OutputView {
    fun printStart() {
        println("안녕하세요. W편의점입니다.")
    }

    fun printProducts(products: List<Product>) {
        println("현재 보유하고 있는 상품입니다.\n")
        products.forEach { product ->
            println("- ${product.name} ${product.price.wonFormat()}원 ${product.getQuantity.emptyQuantityMessage()} ${product.promotion ?: ""}")
        }
    }

    fun printReceipt(results: List<PurchaseResult>) {
        val membershipDiscount = 0
        val totalDiscount = results.sumOf { results.sumOf { it.applyCount } } - membershipDiscount
        println("===========W 편의점=============")
        println(" 상품명		수량	금액")
        results.forEach { result ->
            println(" ${result.requestProduct.name}		${result.requestProduct.count} 	${result.totalPrice}")
        }
        println("====== === == 증    정 === === === === =")
        results.forEach { result ->
            println("${result.requestProduct.name}        ${result.applyCount}")
        }
        println("====== === === === === === === === ===")
        println("총구매액        ${results.sumOf { it.requestProduct.count }}    ${results.sumOf { it.totalPrice }.wonFormat()}")
        println("행사할인        - ${results.sumOf { it.applyPrice }.wonFormat()}")
        println("멤버십할인 - 3, 000")
        println(" 내실돈             ${(results.sumOf { it.totalPrice } - totalDiscount).wonFormat()}")
    }

    private fun Int.wonFormat(): String {
        val formatter = DecimalFormat("#,###")
        return formatter.format(this)
    }

    private fun Int.emptyQuantityMessage(): String {
        if (this == 0) return "재고 없음"
        return this.toString() + "개"
    }
}