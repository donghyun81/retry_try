package store

import camp.nextstep.edu.missionutils.Console

class InputView {
    fun readPurchaseProducts(stock: List<Product>): List<PurchaseProduct> {
        println("구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])")
        val purchaseProductsInput = Console.readLine().split(",")
        return purchaseProductsInput.map { input ->
            require(input.first() == '[' && input.last() == ']') { "[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요." }
            val purchaseProduct = input.removeSurrounding("[", "]").split("-")
            require(purchaseProduct.size == 2) { "[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요." }
            val (name, countInput) = purchaseProduct
            val count = requireNotNull(countInput.toIntOrNull()) { "[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요." }
            require(stock.any { it.name == name }) { "[ERROR] 존재하지 않는 상품입니다. 다시 입력해 주세요." }
            require(stock.filter { it.name == name }
                .sumOf { it.quantity } >= count) { "[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요." }
            PurchaseProduct(name, count)
        }
    }

    fun readIsAddApply(purchaseProduct: PurchaseProduct): Boolean {
        println("현재 ${purchaseProduct.name}은(는) 1개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)")
        val isAddApply = Console.readLine()
        require(isAddApply == "Y" || isAddApply == "N") { "[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요." }
        return isAddApply == "Y"
    }

    fun readIsExclude(purchaseProduct: PurchaseProduct): Boolean {
        println("현재 ${purchaseProduct.name} ${purchaseProduct.count}개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)")
        val isExclude = Console.readLine()
        require(isExclude == "Y" || isExclude == "N") { "[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요." }
        return isExclude == "Y"
    }

    fun readIsMembership(): Boolean {
        println("멤버십 할인을 받으시겠습니까? (Y/N)")
        val isExclude = Console.readLine()
        require(isExclude == "Y" || isExclude == "N") { "[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요." }
        return isExclude == "Y"
    }

    fun readIsRetry(): Boolean {
        println("감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)\n")
        val isRetry = Console.readLine()
        require(isRetry == "Y" || isRetry == "N") { "[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요." }
        return isRetry == "Y"
    }
}