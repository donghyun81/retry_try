package store

import camp.nextstep.edu.missionutils.Console

class InputView {
    fun readPurchaseProducts(products: List<Product>): List<RequestProduct> {
        println("구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])")
        val purchaseProductInput = Console.readLine().split(",")
        println()
        val confirmedPurchaseProductInput = purchaseProductInput.map { input ->
            require(input.first() == '[' && input.last() == ']') { Error.FORM.getMessage() }
            input.removeSurrounding("[", "]")
        }
        val purchaseProducts = confirmedPurchaseProductInput.map { input ->
            require(input.split("-").size == 2) { Error.FORM.getMessage() }
            val (name, countInput) = input.split("-")
            val count = requireNotNull(countInput.toIntOrNull()) { Error.FORM.getMessage() }
            RequestProduct(name, count)
        }.groupBy { it.name }.map { (name, product) ->
            val totalCount = product.sumOf { it.count }
            RequestProduct(name, totalCount)
        }
        purchaseProducts.forEach { purchaseProduct ->
            val stock = products.filter { purchaseProduct.name == it.name }
            require(stock.isEmpty().not()) { Error.EMPTY_PRODUCT.getMessage() }
            require(purchaseProduct.count <= stock.sumOf { it.getQuantity() }) { Error.OVER_QUANTITY.getMessage() }
        }
        return purchaseProducts
    }

    fun readIsAddApplyProduct(applyProduct: RequestProduct): Boolean {
        println("현재 ${applyProduct.name}은(는) ${applyProduct.count}개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)")
        val isApplyProductInput = Console.readLine()
        println()
        require(isApplyProductInput == "Y" || isApplyProductInput == "N") { Error.OTHER.getMessage() }
        return isApplyProductInput == "Y"
    }

    fun readIsExcludeProduct(excludeProduct: RequestProduct): Boolean {
        println("현재 ${excludeProduct.name} ${excludeProduct.count}개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)")
        val isExcludeProduct = Console.readLine()
        println()
        require(isExcludeProduct == "Y" || isExcludeProduct == "N") { Error.OTHER.getMessage() }
        return isExcludeProduct == "N"
    }

    fun readIsMembership(): Boolean {
        println("멤버십 할인을 받으시겠습니까? (Y/N)")
        val isMemberShip = Console.readLine()
        require(isMemberShip == "Y" || isMemberShip == "N") { Error.OTHER.getMessage() }
        return isMemberShip == "Y"
    }

    fun readIsRetryPurchase(): Boolean {
        println("감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)")
        val isRetryPurchase = Console.readLine()
        require(isRetryPurchase == "Y" || isRetryPurchase == "N") { Error.OTHER.getMessage() }
        return isRetryPurchase == "Y"
    }
}