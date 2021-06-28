using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading;

namespace ThreadPool
{
    public partial class MyThreadPool
    {
        private class Worker
        {
            private readonly Thread _thread;
            public int Id => _thread.ManagedThreadId;
            public bool IsAlive => _thread.IsAlive;
            public void Start() => _thread.Start();
            public void Join() => _thread.Join();

            public Worker(MyThreadPool pool)
            {
                _thread = new Thread(() =>
                {
                    while (pool.RunTask())
                    {
                    }
                });
            }
        }

        private class WorkerService
        {
            public List<Worker> Workers = new();
            public Dictionary<int, DequeForSteal<Action>> WorkersQueuesDict = new();
            public int NumOfAliveWorkers => Workers.Count(worker => worker.IsAlive);

            public void AddWorker(Worker worker)
            {
                Workers.Add(worker);
                WorkersQueuesDict.Add(worker.Id, new DequeForSteal<Action>());
            }

            public void StartAllWorkers()
            {
                foreach (var worker in Workers)
                {
                    worker.Start();
                }
            }

            public bool HasWorkerWithId(int workerId)
            {
                return WorkersQueuesDict.ContainsKey(workerId);
            }

            public void AddTaskForWorker<TResult>(int workerId, IMyTask<TResult> task)
            {
                WorkersQueuesDict[workerId].Push(task.Execute);
            }

            public bool TryGetNewTaskForWorker(int workerId, out Action newAction)
            {
                var result = WorkersQueuesDict[workerId].TryPopFirst(out var task);
                newAction = task;
                return result;
            }

            public bool TryGetTaskForSteal(out Action actionToSteal)
            {
                var workerQueue = ChooseRandomWorkerQueue();
                var result = workerQueue.TryPopLast(out var task);
                actionToSteal = task;
                return result;
            }

            public void JoinAllWorkers()
            {
                foreach (var worker in Workers)
                {
                    worker.Join();
                }
            }

            private DequeForSteal<Action> ChooseRandomWorkerQueue() =>
                WorkersQueuesDict.ElementAt(_rnd.Next(0, Workers.Count)).Value;

            private readonly Random _rnd = new();
        }
    }
}