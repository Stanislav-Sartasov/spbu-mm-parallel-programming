package spbu.mm.parallel.programming

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.lang.IllegalStateException

class TestsSecondTask {
    @Test
    fun `test task result`() {
        val pool = ThreadPool(DEFAULT_THREADS_NUM)
        val task = pool.enqueue { 0 }
        pool.dispose()
        Assertions.assertEquals(0, task.result)
    }

    @Test
    fun `test task submission to disposed ThreadPool`() {
        val pool = ThreadPool(DEFAULT_THREADS_NUM)
        pool.dispose()
        Assertions.assertThrows(IllegalStateException::class.java) { pool.enqueue { 0 } }
    }

    @Test
    fun `test continueWith tasks (number of tasks is greater than number of workers)`() {
        val pool = ThreadPool(DEFAULT_THREADS_NUM)
        val n = 10

        val start = pool.enqueue { 0 }
        val results = generateSequence(start) { task ->
            task.continueWith {
                Thread.sleep(500)
                it + 1
            }
        }.take(n).map { it.result }.toList()
        pool.dispose()
        Assertions.assertEquals(List(n) { it }, results)
    }

    @Test
    fun `test number of alive threads in ThreadPool before and after disposal`() {
        val pool = ThreadPool(DEFAULT_THREADS_NUM)
        repeat(10) { pool.enqueue { it } }

        Assertions.assertEquals(DEFAULT_THREADS_NUM, pool.aliveWorkersCount)
        pool.dispose()
        Assertions.assertEquals(0, pool.aliveWorkersCount)
    }

    @Test
    fun `test MyTask execution exception using ContinueWith`() {
        val pool = ThreadPool(DEFAULT_THREADS_NUM)
        val exceptionMsg = "Something went wrong"
        val task = pool.enqueue { error(exceptionMsg) }
        val nextTask = pool.enqueue<Any>(task.continueWith { it })
        Assertions.assertThrows(AggregateException::class.java) { nextTask.result }
        try {
            nextTask.result
        } catch (ex: AggregateException) {
            Assertions.assertEquals(exceptionMsg, ex.message)
        }
        pool.dispose()
    }

    companion object {
        private const val DEFAULT_THREADS_NUM = 8
    }
}
