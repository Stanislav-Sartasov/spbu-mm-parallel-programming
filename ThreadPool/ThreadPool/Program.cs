using System;
using System.Collections.Generic;
using ThreadPool.Task;
using ThreadPool.Pool;
using System.Threading;
using System.Diagnostics;

namespace ThreadPool
{
    class Program
    {
        static void Main(string[] args)
        {
            var task1 = TaskFactory.Instance.RunSimpleTask(() => { Thread.Sleep(1000); return 1; });
            var task2 = task1.ContinueWith((x) => { Thread.Sleep(1000); return x + 1; });
            var task3 = task1.ContinueWith((x) => { Thread.Sleep(1000); return x + 1; });
  
            Console.WriteLine("{0}\n{1}\n{2}", task1.Result, task2.Result, task3.Result);

            Console.ReadKey();
        }
    }
}
