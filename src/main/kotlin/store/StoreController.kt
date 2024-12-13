package store

class StoreController {
    private val outputView = OutputView()
    private val fileService = FileService()

    fun run() {
        outputView.printStart()
        val products = fileService.readProducts()
        val promotion = fileService.readPromotions()
        val storeService = StoreService(products, promotion)
        outputView.printStocks(storeService.getProducts())
    }
}