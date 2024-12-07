package store

import camp.nextstep.edu.missionutils.Console

class InputView {
    fun readPurchaseProducts(products: List<Product>): List<RequestProduct> {
        println("구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])")
        val purchaseProductInput = Console.readLine().split(",")
        println()
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
            require(stock.isEmpty().not()) { "[ERROR] 존재하지 않는 상품입니다. 다시 입력해 주세요." }
            require(purchaseProduct.count <= stock.sumOf { it.getQuantity() }) { "[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요." }
        }
        return purchaseProducts
    }

    fun readIsAddApplyProduct(applyProduct: RequestProduct): Boolean {
        println("현재 ${applyProduct.name}은(는) ${applyProduct.count}개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)")
        val isApplyProductInput = Console.readLine()
        println()
        require(isApplyProductInput == "Y" || isApplyProductInput == "N")
        return isApplyProductInput == "Y"
    }

    fun readIsExcludeProduct(excludeProduct: RequestProduct): Boolean {
        println("현재 ${excludeProduct.name} ${excludeProduct.count}개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)")
        val isExcludeProduct = Console.readLine()
        println()
        require(isExcludeProduct == "Y" || isExcludeProduct == "N")
        return isExcludeProduct == "N"
    }

    fun readIsMembership(): Boolean {
        println("멤버십 할인을 받으시겠습니까? (Y/N)")
        val isMemberShip = Console.readLine()
        require(isMemberShip == "Y" || isMemberShip == "N")
        return isMemberShip == "Y"
    }

    fun readIsRetryPurchase(): Boolean {
        println("감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)")
        val isRetryPurchase = Console.readLine()
        require(isRetryPurchase == "Y" || isRetryPurchase == "N")
        return isRetryPurchase == "Y"
    }
}