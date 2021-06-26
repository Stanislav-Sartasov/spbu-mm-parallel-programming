package spbu.mm.parallel.programming

import kotlin.collections.ArrayList

class AggregateException(val exceptions: List<Exception>, message: String? = exceptions[0].message) : Exception(message, exceptions[0])

class MyTask<TResult>(val func: () -> TResult) : IMyTask<TResult> {
    @Volatile
    private var _isCompleted = false
    override val isCompleted: Boolean
        get() = _isCompleted

    private var _result: TResult? = null
    override val result: TResult
        get() {
            execute()
            if (exceptions.isNotEmpty()) throw AggregateException(exceptions)
            _isCompleted = true
            return _result!!
        }

    private val lock = object {}
    private val exceptions: ArrayList<Exception> = ArrayList()

    override fun <TNewResult> continueWith(func: (TResult) -> TNewResult): IMyTask<TNewResult> {
        return MyTask { func(result) }
    }

    override fun execute() {
        synchronized(lock) {
            if (_isCompleted) return
            try {
                _result = func()
            } catch (ex: AggregateException) {
                exceptions.addAll(ex.exceptions)
            } catch (ex: Exception) {
                exceptions.add(ex)
            } finally {
                _isCompleted = true
            }
        }
    }
}
