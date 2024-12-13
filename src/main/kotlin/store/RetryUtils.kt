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