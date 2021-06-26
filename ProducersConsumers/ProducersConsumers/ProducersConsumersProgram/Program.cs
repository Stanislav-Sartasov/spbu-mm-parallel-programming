using System;
using System.Collections.Generic;
using System.Threading;
using ProducersConsumersProgram.Objects;

namespace ProducersConsumersProgram
{
    class Program
    {
        const int producersNumber = 3;
        const int consumersNumber = 5;
        
        static void Main(string[] args)
        {
            new Program().Run();
        }
        
        void Run()
        {
            var producers = new List<Producer>();
            var consumers = new List<Consumer>();
            var mutex = new object();
            var queue = new Queue<Application>();
            var producersStopped = false;
            Thread threadProducer; Thread threadConsumer;
            Producer producer; Consumer consumer;
          
            for (int i = 0; i < producersNumber; i++)
            {
                producer = new Producer(i, mutex, queue, producersStopped);
                producers.Add(producer);
                threadProducer = new Thread(producer.Produce);
                threadProducer.Start();
            }
          
            for (int i = 0; i < consumersNumber; i++)
            {
                consumer = new Consumer(i, mutex, queue, producersStopped);
                consumers.Add(consumer);
                threadConsumer = new Thread(consumer.Consume);
                threadConsumer.Start();
            }
            
            Thread.Sleep(2000);
            Console.WriteLine("\nPress any button to stop producing applications.\n");
            Console.ReadKey();
            Console.WriteLine("\nConsumers are finishing their job...");
          

            foreach (var p in producers)
            {
                p.Stop();
            }
          
            foreach (var c in consumers)
            {
                c.Stop();
            }
            
        }
    }
}