using System;
using System.Collections.Generic;
using System.Text;
using ThreadPool.Pool;

namespace ThreadPool.Task
{
    public class TaskFactory
    {
        private static readonly object _syncRoot = new object();
        private static TaskFactory _instance;
        public static TaskFactory Instance
        {
            get
            {
                if (_instance is null)
                {
                    lock (_syncRoot)
                    {
                        if (_instance is null)
                        {
                            _instance = new TaskFactory();
                        }
                    }
                }

                return _instance;
            }
            
        }

        private readonly TaskScheduler _taskScheduler;

        private TaskFactory ()
        {
            _taskScheduler = new TaskScheduler();
        }

        public TaskScheduler GetScheduler()
        {
            return _taskScheduler;
        }

        public MyTask<TResult> RunSimpleTask<TResult>(Func<TResult> func)
        {
            var task = new SimpleTask<TResult>(func, _taskScheduler);
            task.Start();

            return task;
        }

        public MyTask<TNewResult> RunChainedTask<TInput, TNewResult>(MyTask<TInput> PrevTask, Func<TInput, TNewResult> func)
        {
            var task = PrevTask.ContinueWith<TNewResult>(func);
            task.Start();

            return task;
        }

        public static void Dispose()
        {
            _instance = null;
        }
    }
}
