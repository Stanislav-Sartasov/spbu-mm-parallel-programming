package spbu.mm.parallel.programming

import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.random.Random

class ThreadPool(numThreads: Int) {
    private val taskQueue: ConcurrentLinkedQueue<IMyTask<*>> = ConcurrentLinkedQueue()
    private val workerRegistry: WorkerRegistry = WorkerRegistry()
    private val lock = object {}

    var disposed: Boolean = false
        private set

    private val shouldProceed: Boolean
        get() = !disposed

    val aliveWorkersCount: Int
        get() = workerRegistry.aliveWorkersCount

    init {
        repeat(numThreads) {
            workerRegistry.addWorker(Worker(this))
        }
    }

    private class Worker(val pool: ThreadPool) {
        private val workerThread = Thread { while (pool.runTask()) continue }

        val id: Long
            get() = workerThread.id

        val isAlive: Boolean
            get() = workerThread.isAlive

        fun start() = workerThread.start()
        fun join() = workerThread.join()
    }

    private inner class WorkerRegistry(
        val workers: ArrayList<Worker> = ArrayList(),
        val workerId2Tasks: HashMap<Long, ConcurrentLinkedDeque<IMyTask<*>>> = HashMap()
    ) {
        val aliveWorkersCount: Int
            get() = workers.count { it.isAlive }

        fun addWorker(worker: Worker) {
            workers.add(worker)
            workerId2Tasks[worker.id] = ConcurrentLinkedDeque()
            worker.start()
        }

        fun hasWorker(id: Long): Boolean = workerId2Tasks.containsKey(id)
        fun pushTask(id: Long, task: IMyTask<*>) = workerId2Tasks[id]!!.push(task)
        fun getWorkToSteal() = workerId2Tasks[Random.nextLong(0L, workers.size.toLong())]?.pollFirst()
        fun taskForId(id: Long) = workerId2Tasks[id]?.pollLast()
        fun joinAll() = workers.forEach { it.join() }
    }

    fun <TResult> enqueue(task: IMyTask<TResult>): IMyTask<TResult> {
        if (disposed) error("Current ThreadPool was already disposed")

        val workerId = Thread.currentThread().id
        if (workerRegistry.hasWorker(workerId)) {
            workerRegistry.pushTask(workerId, task)
        } else {
            taskQueue.add(task)
        }
        return task
    }

    fun <TResult> enqueue(func: () -> TResult): IMyTask<TResult> {
        return enqueue(MyTask(func))
    }

    private fun runTask(): Boolean {
        val workerId = Thread.currentThread().id
        whenNotNull(workerRegistry.taskForId(workerId)) {
            it.execute()
            return true
        }

        whenNotNull(taskQueue.poll()) {
            it.execute()
            return true
        }

        whenNotNull(workerRegistry.getWorkToSteal()) {
            it.execute()
            return true
        }

        return shouldProceed
    }

    private fun joinAll() = workerRegistry.joinAll()

    fun dispose() {
        synchronized(lock) {
            if (disposed) return
            disposed = true
            joinAll()
        }
    }

    companion object {
        private inline fun <T : Any, R> whenNotNull(input: T?, callback: (T) -> R): R? {
            return input?.let(callback)
        }
    }
}
