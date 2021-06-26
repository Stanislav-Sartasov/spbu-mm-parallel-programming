package spbu.mm.parallel.programming

class Consumer<T>(
    private val taskContainer: TaskContainer<T>,
    private val consume: (T) -> Unit,
    private val sleepTimer: Long = SLEEP_TIME,
    order: Int = 0
) : Thread(order.toString()) {
    override fun run() {
        try {
            while (!isInterrupted) {
                val task = taskContainer.getFirstOrNull()
                if (task != null) consume(task)
                sleep(sleepTimer)
            }
        } catch (ex: InterruptedException) {
            println("Consumer №$name was interrupted")
        }
    }
}
