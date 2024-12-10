package store

fun <T> retryInput(runInput: () -> T): T {
    while (true) {
        try {
            return runInput()
        } catch (e: IllegalArgumentException) {
            println(e.message)
        }
    }
}

fun retryPurchase(isRetry: () -> Boolean) {
    while (true) {
        val retry = isRetry()
        if (retry.not()) break
    }
}