package spbu.mm.parallel.programming

class Producer<T>(
    private val taskContainer: TaskContainer<T>,
    private val produce: () -> T,
    private val sleepTimer: Long = SLEEP_TIME,
    order: Int = 0
) : Thread(order.toString()) {
    override fun run() {
        try {
            while (!isInterrupted) {
                taskContainer.add(produce())
                sleep(sleepTimer)
            }
        } catch (ex: InterruptedException) {
            println("Producer №$name was interrupted")
        }
    }
}
