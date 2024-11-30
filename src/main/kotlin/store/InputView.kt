package store

import camp.nextstep.edu.missionutils.Console

class InputView {
    fun inputPurchaseProduct(products: List<Product>): List<PurchaseProduct> {
        println("구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])")
        val productsInput = Console.readLine().split(",")
        val purchaseProducts = productsInput.map { productInput ->
            require(productInput.startsWith("[") && productInput.endsWith("]")) { "[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요." }
            require(productInput.split("-").size == 2) { "[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요." }
            val (name, countInput) = productInput.split("-")
            val count = requireNotNull(countInput.toIntOrNull()) { "[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요." }
            require(products.count { it.name == name } != 0) { "[ERROR] 존재하지 않는 상품입니다. 다시 입력해 주세요." }
            require(products.filter { it.name == name }
                .sumOf { it.quantity } < count) { "[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요." }
            PurchaseProduct(name, count)
        }
        return purchaseProducts.groupBy { it.name }.map { (name, purchaseProducts) ->
            PurchaseProduct(name, purchaseProducts.sumOf { it.count })
        }
    }
}