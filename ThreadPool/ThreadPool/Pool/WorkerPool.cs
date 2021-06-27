using System;
using System.Collections.Generic;
using System.Threading;
using System.Text;
using ThreadPool.Task;
using System.Linq;

namespace ThreadPool.Pool
{
    public class WorkerPool : IDisposable
    {
        private const int _nWorkers = 5;

        private static readonly object _syncRoot = new object();
        private static WorkerPool _instance;
        public static WorkerPool Instance
        {
            get
            {
                if (_instance is null)
                {
                    lock (_syncRoot)
                    {
                        if (_instance is null)
                        {
                            _instance = new WorkerPool(_nWorkers);
                        }
                    }
                }

                return _instance;
            }

        }

        private readonly CancellationTokenSource _CancelTokenSource;
        private readonly object _Lock = new object();
        private readonly List<ThreadWorker> _Pool;

        private WorkerPool(int WorkersCount)
        {
            _CancelTokenSource = new CancellationTokenSource();
            _Pool = new List<ThreadWorker>();

            for (int i = 0; i < WorkersCount; i++)
            {
                _Pool.Add(new ThreadWorker(_CancelTokenSource.Token));
            }
             
        }

        public bool TryGetWorker (out ThreadWorker worker)
        {
            int WorkerID;
            worker = null;
            bool result = false;

            if (!_CancelTokenSource.IsCancellationRequested)
            {
                lock (_Lock)
                {
                    WorkerID = _Pool.FindIndex(Worker => !Worker.IsWorking);
                    if (WorkerID != -1)
                    {
                        _Pool[WorkerID].IsWorking = true;
                        worker = _Pool[WorkerID];
                        result = true;
                    }
                }
            }
            
            return result;
        }

        public CancellationToken GetToken()
        {
            return _CancelTokenSource.Token;
        }

        public void Dispose()
        {
            Console.WriteLine("Shutting down...");

            _CancelTokenSource.Cancel();
            _CancelTokenSource.Dispose();

            _Pool.ForEach(worker => { worker.Awake(); });

            while (_Pool.Any(Worker => Worker.IsWorking))
            {
                Thread.Sleep(100);
            }

            _Pool.ForEach(worker => { worker.Dispose(); });

            TaskFactory.Dispose();
            _instance = null;
            
        }
    }
}
