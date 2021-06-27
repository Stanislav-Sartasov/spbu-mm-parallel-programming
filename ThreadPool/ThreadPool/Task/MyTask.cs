using System;
using System.Collections.Generic;
using System.Text;
using System.Threading;
using ThreadPool.Pool;

namespace ThreadPool.Task
{
    public abstract class MyTask 
    {
        protected TaskScheduler _taskScheduler;

        public bool IsCompleted { get; protected set; }
        public bool IsReady { get; set; }
        public string ID { get; private set; }
        protected bool _wasStarted;

        protected List<MyTask> _nextTasks;

        public MyTask()
        {
            IsCompleted = false;
            IsReady = false;
            _wasStarted = false;
            _nextTasks = new List<MyTask>();

            Random rnd = new Random();
            ID = string.Format("#{0}", rnd.Next(10000000, 99999999));
        }

        public void Start()
        {
            if (!_wasStarted)
            {
                _wasStarted = true;
                _taskScheduler.Enqueue(this);
            }
        }

        public void Wait()
        {
            if (!_wasStarted)
            {
                return;
            }

            while (!IsCompleted)
            {
                Thread.Sleep(100);
            }
        }

        internal abstract void Run();
    }

    public abstract class MyTask<TResult> : MyTask 
    {
        protected TResult _result;
        public TResult Result {
            get 
            {
                if (!_wasStarted)
                {
                    Start();
                }

                if (!IsCompleted)
                {
                    Wait();
                }

                return _result;
            }
            protected set 
            {
                _result = value;
                IsCompleted = true;
            } 
        }

        public MyTask<TNewResult> ContinueWith<TNewResult>(Func<TResult, TNewResult> next)
        {
            MyTask<TNewResult> task = new ChainedTask<TResult, TNewResult>(this, next, _taskScheduler);
            _nextTasks.Add(task);
            if (IsCompleted)
            {
                task.IsReady = true;
            }
            task.Start();
            return task;
        }
    }

    public class SimpleTask<TResult> : MyTask<TResult>
    {
        private Func<TResult> Task;

        public SimpleTask(Func<TResult> func, TaskScheduler taskScheduler)
            : base()
        {
            Task = func;
            IsReady = true;
            _taskScheduler = taskScheduler;
        }

        internal override void Run()
        {
            Result = Task();

            _nextTasks.ForEach(task => task.IsReady = true);
        }
    }

    public class ChainedTask<TInput, TResult> : MyTask<TResult>
    {
        private Func<TInput, TResult> Task;

        private MyTask<TInput> _prevTask;

        public ChainedTask(MyTask<TInput> prevTask, Func<TInput, TResult> func, TaskScheduler taskScheduler)
            : base()
        {
            Task = func;
            _prevTask = prevTask;
            _taskScheduler = taskScheduler;
        }
    
        internal override void Run()
        {
            Result = Task(_prevTask.Result);

            _nextTasks.ForEach(task => task.IsReady = true);
        }
    }
}
