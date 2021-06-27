using System;
using System.Collections.Generic;
using System.Text;
using ThreadPool.Task;
using System.Threading;

namespace ThreadPool.Pool
{
    public class TaskScheduler
    {
        private readonly CancellationToken _token;
        private readonly Thread _Scheduler;
        private readonly List<MyTask> _TaskQueue;
        private readonly object _Lock = new object();

        private ThreadWorker _worker;
        private MyTask _task;

        public TaskScheduler()
        {
            _TaskQueue = new List<MyTask>();
            _token = WorkerPool.Instance.GetToken();
            _task = null;
            _worker = null;
            _Scheduler = new Thread(Schedule) { IsBackground = true };
            _Scheduler.Start();
        }

        public void Enqueue(MyTask task)
        {
            lock(_Lock)
            {
                _TaskQueue.Add(task);
            }
        }

        public int GetQueueCount()
        {
            lock (_Lock)
            {
                return _TaskQueue.Count;
            }
        }

        private void Schedule()
        {
            while (!_token.IsCancellationRequested)
            {
                if (_task is null)
                {
                    lock (_Lock)
                    {
                        _task = _TaskQueue.Find(task => task.IsReady);
                    }
                }

                if (!(_task is null))
                {
                    if (_worker is null)
                    {
                        WorkerPool.Instance.TryGetWorker(out _worker);
                    }

                    if (!(_worker is null))
                    {
                        lock (_Lock)
                        {
                            _TaskQueue.Remove(_task);
                        }
                        _worker.ExecuteTask(_task);
                        _worker = null;
                        _task = null;
                    }
                }

                Thread.Sleep(10);
            }
        }
    }
}
