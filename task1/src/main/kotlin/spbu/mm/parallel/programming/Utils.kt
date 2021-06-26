package spbu.mm.parallel.programming

const val PRODUCER_NUM = 5
const val CONSUMER_NUM = 3
const val SLEEP_TIME = 300L

fun List<Thread>.interruptAll() = this.forEach { it.interrupt() }

fun <T : Thread> createCollectionAndStart(size: Int, init: (Int) -> T): List<T> {
    return List(size, init).onEach { it.start() }
}