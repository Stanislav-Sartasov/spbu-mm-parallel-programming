using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading;

namespace ProducerConsumer
{
    internal static class Program
    {
        private const int ProducersNum = 6;
        private const int ConsumersNum = 4;

        private static Random _rnd = new();

        static void Main(string[] args)
        {
            using var cTokenSource = new CancellationTokenSource();
            var cToken = cTokenSource.Token;

            var listWithLock = new ListWithLock<List<string>>();
            var producers = new List<Producer<List<string>>>(ProducersNum);
            var consumers = new List<Consumer<List<string>>>(ConsumersNum);

            var words = new List<string>() {"apple", "orange", "mango", "banana", "lemon", "kiwi"};

            for (int i = 0; i < ProducersNum; i++)
            {
                int wordsCount = i + 1;
                producers.Add(new Producer<List<string>>(listWithLock,
                    () => words.OrderBy(x => _rnd.Next()).Take(wordsCount).ToList(), cToken));
            }

            for (int i = 0; i < ConsumersNum; i++)
            {
                int consumerNum = i + 1;
                consumers.Add(new Consumer<List<string>>(listWithLock,
                    data => Console.WriteLine($"Consumer {consumerNum} took: {string.Join(", ", data)}"), cToken));
            }

            Console.WriteLine("Press any key to stop...");
            Console.ReadKey();

            Console.WriteLine("\nShutting down processes...");
            cTokenSource.Cancel();
        }
    }
}