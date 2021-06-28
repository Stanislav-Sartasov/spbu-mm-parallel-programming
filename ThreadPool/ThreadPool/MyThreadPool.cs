using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Linq;
using System.Threading;

namespace ThreadPool
{
    public partial class MyThreadPool : IDisposable
    {
        private volatile bool _isDisposed;
        private readonly WorkerService _workerService = new();
        private readonly ConcurrentQueue<Action> _poolTaskQueue = new();
        private readonly object _disposeLock = new();

        public int NumOfAliveWorkers => _workerService.NumOfAliveWorkers;

        public MyThreadPool(int threadsNum)
        {
            for (int i = 0; i < threadsNum; i++)
            {
                var worker = new Worker(this);
                _workerService.AddWorker(worker);
            }

            _workerService.StartAllWorkers();
        }

        public IMyTask<TResult> Enqueue<TResult>(IMyTask<TResult> task)
        {
            if (_isDisposed)
            {
                throw new ObjectDisposedException("Current instance of MyThreadPool was already disposed");
            }

            var currentWorkerId = Environment.CurrentManagedThreadId;
            if (_workerService.HasWorkerWithId(currentWorkerId))
            {
                _workerService.AddTaskForWorker(currentWorkerId, task);
            }
            else
            {
                _poolTaskQueue.Enqueue(task.Execute);
            }

            return task;
        }

        public IMyTask<TResult> Enqueue<TResult>(Func<TResult> func) => Enqueue(new MyTask<TResult>(func));

        public bool RunTask()
        {
            var workerId = Environment.CurrentManagedThreadId;
            if (_workerService.TryGetNewTaskForWorker(workerId, out var currentWorkerTask))
            {
                currentWorkerTask();
                return true;
            }

            if (_poolTaskQueue.TryDequeue(out var poolTask))
            {
                poolTask();
                return true;
            }

            if (_workerService.TryGetTaskForSteal(out var stolenAction))
            {
                stolenAction();
                return true;
            }

            return !_isDisposed;
        }

        public void Dispose()
        {
            lock (_disposeLock)
            {
                if (_isDisposed)
                    return;
                _isDisposed = true;
                _workerService.JoinAllWorkers();
            }
        }
    }
}