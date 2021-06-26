package spbu.mm.parallel.programming

class TaskContainer<T> {
    private val lock = object {}
    private val tasks: ArrayList<T> = ArrayList()

    fun add(task: T) = synchronized(lock) { tasks.add(task) }

    fun getFirstOrNull(): T? {
        return synchronized(lock) {
            tasks.takeIf(ArrayList<T>::isNotEmpty)?.removeAt(0)
        }
    }

    fun isEmpty(): Boolean = synchronized(lock) { tasks.isEmpty() }
}