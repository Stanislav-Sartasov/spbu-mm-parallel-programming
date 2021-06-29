using System;
using System.Collections.Generic;
using System.Text;
using ThreadPool.Task;
using System.Threading;

namespace ThreadPool.Pool
{
    public class TaskScheduler : IDisposable
    {
        private readonly CancellationToken _token;
        private readonly Thread _Scheduler;
        private readonly List<MyTask> _TaskQueue;
        private readonly List<ManualResetEvent> _taskCompleted;
        private readonly object _Lock = new object();
        private readonly AutoResetEvent _newTask;

        private ThreadWorker _worker;
        private MyTask _task;

        public TaskScheduler()
        {
            _TaskQueue = new List<MyTask>();
            _taskCompleted = new List<ManualResetEvent>();
            _token = WorkerPool.Instance.GetToken();
            _newTask = new AutoResetEvent(false);
            _task = null;
            _worker = null;
            _Scheduler = new Thread(Schedule) { IsBackground = true };
            _Scheduler.Start();
        }

        public ManualResetEvent Enqueue(MyTask task)
        {
            var taskCompleted = new ManualResetEvent(false);

            lock(_Lock)
            {
                _TaskQueue.Add(task);
                _taskCompleted.Add(taskCompleted);
            }

            if (task.IsReady)
            {
                _newTask.Set();
            }

            return taskCompleted;
        }

        public void NewTask()
        {
            _newTask.Set();
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
                _newTask.WaitOne();

                while (!WorkerPool.Instance.TryGetWorker(out _worker)) { }
                
                lock (_Lock)
                {
                    _task = _TaskQueue.Find(task => task.IsReady);
                    _TaskQueue.Remove(_task);
                }
                
                _worker.ExecuteTask(_task);

                if (_TaskQueue.Find(task => task.IsReady) != null)
                {
                    _newTask.Set();
                }
            }
        }

        public void Dispose()
        {
            _taskCompleted.ForEach(evnt => evnt.Dispose());
            _newTask.Dispose();
        }
    }
}
