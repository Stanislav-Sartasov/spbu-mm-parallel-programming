using System;
using Xunit;
using ThreadPool.Task;
using ThreadPool.Pool;
using System.Threading;

namespace ThreadPool
{
    public class UnitTest1
    {
        [Fact]
        public void RunTaskTest()
        {
            using (WorkerPool.Instance)
            {
                var task = TaskFactory.Instance.RunSimpleTask(() => 1);
                Assert.Equal(1, task.Result);
            }
        }

        [Fact]
        public void RunManyTaskTest()
        {
            using (WorkerPool.Instance)
            {
                // default number of threads is 5
                TaskFactory.Instance.RunSimpleTask(() => { Thread.Sleep(1000); return 1; });
                TaskFactory.Instance.RunSimpleTask(() => { Thread.Sleep(1000); return 1; });
                TaskFactory.Instance.RunSimpleTask(() => { Thread.Sleep(1000); return 1; });
                TaskFactory.Instance.RunSimpleTask(() => { Thread.Sleep(1000); return 1; });
                TaskFactory.Instance.RunSimpleTask(() => { Thread.Sleep(1000); return 1; });
                TaskFactory.Instance.RunSimpleTask(() => { Thread.Sleep(1000); return 1; });
                Thread.Sleep(200);
                Assert.Equal(1, TaskFactory.Instance.GetScheduler().GetQueueCount());
            }
        }

        [Fact]
        public void StartTaskTest()
        {
            using (WorkerPool.Instance)
            {
                var scheduler = new TaskScheduler();
                var task = new SimpleTask<int>(() => 1, scheduler);
                Assert.False(task.IsCompleted);
                task.Start();
                Thread.Sleep(100);
                Assert.True(task.IsCompleted);
            }
        }

        [Fact]
        public void ResultWaitTest()
        {
            using (WorkerPool.Instance)
            {
                var task = TaskFactory.Instance.RunSimpleTask(() => { Thread.Sleep(1000); return 1; });
                Assert.Equal(1, task.Result);
            }
        }

        [Fact]
        public void ContinueTest()
        {
            using (WorkerPool.Instance)
            {
                var task = TaskFactory.Instance.RunSimpleTask(() => { Thread.Sleep(500); return 1; });
                var task2 = task.ContinueWith((x) => { Thread.Sleep(500); return x + 1; });
                Assert.Equal(2, task2.Result);
            }
        }

        [Fact]
        public void MultipleContinueTest1()
        {
            using (WorkerPool.Instance)
            {
                var task = TaskFactory.Instance.RunSimpleTask(() => { Thread.Sleep(500); return 1; });
                var task2 = task.ContinueWith((x) => { Thread.Sleep(500); return x + 1; });
                var task3 = task.ContinueWith((x) => { Thread.Sleep(500); return x + 1; });
                Assert.Equal(2, task2.Result);
                Assert.Equal(2, task3.Result);
            }
        }

        [Fact]
        public void MultipleContinueTest2()
        {
            using (WorkerPool.Instance)
            {
                var task = TaskFactory.Instance.RunSimpleTask(() => { Thread.Sleep(500); return 1; });
                var task2 = task.ContinueWith((x) => { Thread.Sleep(500); return x + 1; });
                var task3 = task2.ContinueWith((x) => { Thread.Sleep(500); return x + 1; });
                Assert.Equal(2, task2.Result);
                Assert.Equal(3, task3.Result);
            }
        }

        [Fact]
        public void MultipleContinueTest3()
        {
            using (WorkerPool.Instance)
            {
                var task = TaskFactory.Instance.RunSimpleTask(() => { Thread.Sleep(500); return 1; });
                var task3 = task.ContinueWith((x) => { Thread.Sleep(500); return x + 1; }).ContinueWith((x) => { Thread.Sleep(500); return x + 1; });
                Assert.Equal(3, task3.Result);
            }
        }//*/
    }
}
