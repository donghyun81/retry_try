package store

class StoreController {
    private val outputView = OutputView()
    private val inputView = InputView()
    private val fileService = FileService()

    fun run() {
        outputView.printStart()
        val products = fileService.readProducts()
        val promotion = fileService.readPromotions()
        val storeService = StoreService(products, promotion)
        outputView.printStocks(storeService.getProducts())
        val purchaseProducts = inputView.readPurchaseProducts(storeService.getProducts())
        val purchaseResult = purchaseProducts.map { purchaseProduct -> storeService.getPurchaseResult(purchaseProduct) }
        outputView.printReceipt(purchaseResult, 0)
    }
}