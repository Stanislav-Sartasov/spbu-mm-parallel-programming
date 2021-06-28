using System;
using System.Linq;
using System.Threading;
using NUnit.Framework;
using ThreadPool;

namespace ThreadPoolTest
{
    public class ThreadPoolTests
    {
        private const int ThreadsNum = 10;

        [Test]
        public void SingleTaskTest()
        {
            using var pool = new MyThreadPool(ThreadsNum);
            var testString = "test";
            var task = pool.Enqueue(() => testString);
            
            Assert.AreEqual(testString, task.Result);
        }

        [Test]
        public void EnqueueToDisposedThreadPoolTest()
        {
            using var pool = new MyThreadPool(ThreadsNum);
            pool.Dispose();
            
            Assert.Throws<ObjectDisposedException>(() => pool.Enqueue(() => "test"));
        }

        [Test]
        public void TaskResultTest()
        {
            using var pool = new MyThreadPool(ThreadsNum);
            var testString = "test";
            var task = pool.Enqueue(() => testString += "1");

            Thread.Sleep(500);

            Assert.AreEqual("test1", testString);
            Assert.AreEqual("test1", task.Result);

            testString = "";

            Assert.AreEqual("test1", task.Result);
        }

        [Test]
        public void NumberOfThreadsAliveInPoolAfterDisposalTest()
        {
            using var pool = new MyThreadPool(ThreadsNum);
            for (int i = 0; i < 20; i++)
            {
                var task = pool.Enqueue(() => "test");
            }

            Assert.AreEqual(ThreadsNum, pool.NumOfAliveWorkers);
            
            pool.Dispose();
            
            Assert.AreEqual(0, pool.NumOfAliveWorkers);
        }

        [Test]
        public void TasksNumGreaterThanThreadsNumTest()
        {
            using var pool = new MyThreadPool(ThreadsNum);
            var tasksNum = 100000;

            var arrRange = Enumerable.Repeat(1000, tasksNum);
            var tasks = arrRange.Select(x => pool.Enqueue(() => x -= 7)).ToArray();

            while (tasks.Any(task => !task.IsCompleted)) {}

            Assert.AreEqual(tasks.Sum(task => task.Result), arrRange.Sum() - 7 * tasksNum);
        }

        [Test]
        public void ContinueWithTasksNumGreaterThanThreadsNumTest()
        {
            using var pool = new MyThreadPool(ThreadsNum);
            var tasksNum = 50;
            var arrRange = new string[tasksNum];

            var primaryTask = pool.Enqueue(() => "");
            var resultTask = arrRange.Aggregate(primaryTask,
                (contTask, _) => contTask.ContinueWith(result => { return result + "a"; }));
            pool.Enqueue(resultTask);
            
            Assert.AreEqual(resultTask.Result.Length, tasksNum);
        }

        [Test]
        public void CustomAggregationExceptionTest()
        {
            using var pool = new MyThreadPool(ThreadsNum);
            var exceptionMessage = "Test Exception";

            var primaryTask = pool.Enqueue<object>(() => throw new OverflowException(exceptionMessage));
            var secondaryTask = pool.Enqueue(primaryTask.ContinueWith(result => result));

            Assert.Throws<AggregateException>(() =>
            {
                var x = secondaryTask.Result;
            });
            try
            {
                var x = secondaryTask.Result;
            }
            catch (AggregateException e)
            {
                Assert.AreEqual(e.InnerExceptions[0].Message, exceptionMessage);
            }
        }

        [Test]
        public void TaskInTaskTest()
        {
            using var pool = new MyThreadPool(8);

            var primaryTask = pool.Enqueue(() => "Hello ");

            var taskContinue = primaryTask.ContinueWith(result =>
            {
                var subTask = pool.Enqueue(() => "pool");
                var subTaskContinue = subTask.ContinueWith(subResult => "!");
                return result + subTask.Result + subTaskContinue.Result;
            });

            pool.Enqueue(taskContinue);
            
            Assert.AreEqual("Hello pool!", taskContinue.Result);
        }
    }
}