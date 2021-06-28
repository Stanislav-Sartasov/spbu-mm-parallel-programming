using System;

namespace ThreadPool
{
    class Program
    {
        private static void Main(string[] args)
        {
            using var pool = new MyThreadPool(8);

            var primaryTask = pool.Enqueue(() =>
            {
                Console.WriteLine($"Task 1 executed. ThreadId: {Environment.CurrentManagedThreadId}");
                return "Hello ";
            });

            var taskContinue = primaryTask.ContinueWith(result =>
            {
                Console.WriteLine($"Task 1 continuation executed. ThreadId: {Environment.CurrentManagedThreadId}");

                var subTask = pool.Enqueue(() =>
                {
                    Console.WriteLine($"Subtask in Task 1 executed. ThreadId: {Environment.CurrentManagedThreadId}");
                    return "pool";
                });

                var subTaskContinue = subTask.ContinueWith(subResult =>
                {
                    Console.WriteLine(
                        $"Subtask continuation in Task 1 executed. ThreadId: {Environment.CurrentManagedThreadId}");
                    return "!";
                });

                return result + subTask.Result + subTaskContinue.Result;
            });

            pool.Enqueue(taskContinue);

            Console.WriteLine(taskContinue.Result);
        }
    }
}