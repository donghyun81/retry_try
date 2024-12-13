package store

import java.text.DecimalFormat

class OutputView {

    fun printStart() {
        println("안녕하세요. W편의점입니다.\n")
    }

    //- 콜라 1,000원 10개 탄산2+1
    fun printStocks(products: List<Product>) {
        products.forEach { product ->
            println("- ${product.name} ${product.price.wonFormat()}원 ${product.quantity.emptyStock()} ${product.promotion ?: ""}")
        }
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