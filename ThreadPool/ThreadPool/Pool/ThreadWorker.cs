using System;
using System.Collections.Generic;
using System.Text;
using System.Threading;
using ThreadPool.Task;

namespace ThreadPool.Pool
{
    public class ThreadWorker : IDisposable
    {
        public bool IsWorking {get; set;}
        private CancellationToken _token;
        private Thread _thread;
        private readonly AutoResetEvent _NewTask;
        private MyTask _task;

        public ThreadWorker(CancellationToken token)
        {
            IsWorking = false;
            _token = token;
            _NewTask = new AutoResetEvent(false);
            _thread = new Thread(Runtime) { IsBackground = true };
            _thread.Start();
        }

        public void ExecuteTask(MyTask task)
        {
            _task = task;
            _NewTask.Set();
        }

        public void Awake()
        {
            _NewTask.Set();
        }

        private void Runtime()
        {
            Console.WriteLine("Thread {0} started", Thread.GetCurrentProcessorId());
            while (true)
            {
                _NewTask.WaitOne();

                if (_token.IsCancellationRequested)
                {
                    break;
                }

                try
                {
                    _task.Run();
                    Console.WriteLine("Thread {0} processed the task {1}", Thread.GetCurrentProcessorId(), _task.ID);
                }
                catch (Exception exc)
                {
                    Console.WriteLine("Error occured: " + exc.Message);
                }
                finally
                {
                    IsWorking = false;
                }
            }
        }

        public void Dispose()
        {
            _NewTask.Dispose();
        }
    }
}
