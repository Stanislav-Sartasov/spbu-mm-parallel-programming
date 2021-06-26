package spbu.mm.parallel.programming

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TestsFirstTask {
    private fun Thread.interruptTimeout(timeOut: Long) {
        val start = System.currentTimeMillis()
        while (System.currentTimeMillis() - start < timeOut) continue
        this.interrupt()
    }

    private fun checkTimeout(timeOut: Long, action: () -> Boolean) {
        val start = System.currentTimeMillis()
        while (System.currentTimeMillis() - start < timeOut) continue
        return Assertions.assertTrue(action())
    }

    @Test
    fun `test empty producer`() {
        val taskContainer = TaskContainer<Any>()
        var initialized = false
        val consumer = Consumer(taskContainer, { initialized = true }).also { it.start() }
        consumer.interruptTimeout(1000L)
        Assertions.assertTrue(!initialized && taskContainer.isEmpty())
    }

    @Test
    fun `test empty consumer`() {
        val taskContainer = TaskContainer<Any>()
        val producer = Producer(taskContainer, { 0 }).also { it.start() }
        producer.interruptTimeout(5000L)
        Assertions.assertFalse(taskContainer.isEmpty())
    }

    @Test
    fun `test producer-consumer`() {
        val taskContainer = TaskContainer<Boolean>()
        var initialized = false
        val producer = Producer(taskContainer, { false }).also { it.start() }
        val consumer = Consumer(taskContainer, { if (!it) initialized = true }).also { it.start() }
        checkTimeout(10000L) { initialized }
    }
}