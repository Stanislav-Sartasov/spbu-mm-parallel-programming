package spbu.mm.parallel.programming

fun main() {
    val taskQueue = TaskContainer<Int>()
    val producers = createCollectionAndStart(PRODUCER_NUM) { Producer(taskQueue, { it }, order = it) }
    val consumers = createCollectionAndStart(CONSUMER_NUM) {
        Consumer(taskQueue, { res -> println("Consumer $it: $res") }, order = it)
    }

    System.`in`.read()
    println("Sending interruption commands to producers/consumers...")

    producers.interruptAll()
    consumers.interruptAll()
}
