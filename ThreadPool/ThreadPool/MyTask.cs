using System;
using System.Collections.Generic;
using System.Threading;

namespace ThreadPool
{
    public class MyTask<TResult> : IMyTask<TResult>
    {
        private readonly Func<TResult> _func;
        private volatile bool _isCompleted;
        private readonly object _lock = new();
        private readonly List<Exception> _exceptions = new();
        private TResult _result;

        public MyTask(Func<TResult> func)
        {
            _func = func ?? throw new ArgumentNullException(nameof(func), "Task cannot execute null");
        }

        public bool IsCompleted => _isCompleted;

        public TResult Result
        {
            get
            {
                Execute();
                if (_exceptions.Count > 0)
                {
                    throw new AggregateException(_exceptions);
                }

                return _result;
            }
        }

        public void Execute()
        {
            lock (_lock)
            {
                if (_isCompleted) return;
                try
                {
                    _result = _func.Invoke();
                }
                catch (AggregateException aggEx)
                {
                    _exceptions.AddRange(aggEx.InnerExceptions);
                }
                catch (Exception e)
                {
                    _exceptions.Add(e);
                }
                finally
                {
                    _isCompleted = true;
                }
            }
        }

        public IMyTask<TNewResult> ContinueWith<TNewResult>(Func<TResult, TNewResult> func)
        {
            return new MyTask<TNewResult>(() => func(Result));
        }
    }
}