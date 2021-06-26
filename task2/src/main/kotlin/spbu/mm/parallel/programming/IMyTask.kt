package spbu.mm.parallel.programming

interface IMyTask<TResult> {
    val isCompleted: Boolean
    val result: TResult

    fun execute()
    fun <TNewResult> continueWith(func: (TResult) -> TNewResult): IMyTask<TNewResult>
}
