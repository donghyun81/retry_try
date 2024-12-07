package store

import camp.nextstep.edu.missionutils.Console

class InputView {
    fun readPurchaseProducts(products: List<Product>): List<RequestProduct> {
        println("구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])")
        val purchaseProductInput = Console.readLine().split(",")
        val confirmedPurchaseProductInput = purchaseProductInput.map { input ->
            require(input.first() == '[' && input.last() == ']') { "[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요." }
            input.removeSurrounding("[", "]")
        }
        val purchaseProducts = confirmedPurchaseProductInput.map { input ->
            require(input.split("-").size == 2) { "[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요." }
            val (name, countInput) = input.split("-")
            val count = requireNotNull(countInput.toIntOrNull()) { "[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요." }
            RequestProduct(name, count)
        }.groupBy { it.name }.map { (name, product) ->
            val totalCount = product.sumOf { it.count }
            RequestProduct(name, totalCount)
        }
        purchaseProducts.forEach { purchaseProduct ->
            val stock = products.filter { purchaseProduct.name == it.name }
            require(stock.isEmpty()) { "[ERROR] 존재하지 않는 상품입니다. 다시 입력해 주세요." }
            require(purchaseProduct.count <= stock.sumOf { it.quantity }) { "[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요." }
        }
        return purchaseProducts
    }
}