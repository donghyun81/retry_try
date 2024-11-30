package store

import camp.nextstep.edu.missionutils.Console

class InputView {
    fun inputPurchaseProduct(products: List<Product>): List<RequestProduct> {
        println("구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])")
        val productsInput = Console.readLine().split(",")
        val requestProducts = productsInput.map { productInput ->
            require(productInput.startsWith("[") && productInput.endsWith("]")) { "[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요." }
            require(productInput.split("-").size == 2) { "[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요." }
            val (name, countInput) = productInput.removeSurrounding("[", "]").split("-")
            val count = requireNotNull(countInput.toIntOrNull()) { "[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요." }
            require(products.count { it.name == name } != 0) { "[ERROR] 존재하지 않는 상품입니다. 다시 입력해 주세요." }
            require(products.filter { it.name == name }
                .sumOf { it.getQuantity } >= count) { "[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요." }
            RequestProduct(name, count)
        }
        return requestProducts.groupBy { it.name }.map { (name, purchaseProducts) ->
            RequestProduct(name, purchaseProducts.sumOf { it.count })
        }
    }

    fun inputIsAddApplyProduct(requestProduct: RequestProduct): Boolean {
        println("현재 ${requestProduct.name}은(는) ${requestProduct.count}개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)\n")
        val isAddApplyProductInput = Console.readLine()
        require(isAddApplyProductInput == "Y" || isAddApplyProductInput == "N") { "[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요." }
        return isAddApplyProductInput == "Y"
    }

    fun inputIsMemberShip(): Boolean {
        println("멤버십 할인을 받으시겠습니까? (Y/N)")
        val isMembershipInput = Console.readLine()
        require(isMembershipInput == "Y" || isMembershipInput == "N") { "[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요." }
        return isMembershipInput == "Y"
    }
}