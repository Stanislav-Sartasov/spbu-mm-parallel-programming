using System.Collections.Generic;
using System.Threading;
using NUnit.Framework;
using ProducersConsumersProgram.Objects;

namespace ProducersConsumersTests
{
    public class Tests
    {
        const int producersNumber = 3;
        const int consumersNumber = 5;
        
        List<Producer> producers;
        List<Consumer> consumers;
        object mutex; 
        Queue<Application> queue;
        bool producersStopped = false;
        
        [SetUp]
        public void Setup()
        {
            producers = new List<Producer>();
            consumers = new List<Consumer>();
            mutex = new object();
            queue = new Queue<Application>();
            
        }

        [Test]
        public void NoConsumersNotEmptyQueueTest()
        {
            for (int i = 0; i < producersNumber; i++)
            {
                var producer = new Producer(i, mutex, queue, producersStopped);
                producers.Add(producer);
                var threadProducer = new Thread(producer.Produce);
                threadProducer.Start();
            }
  
            Thread.Sleep(10000);
  
            foreach (var p in producers)
            {
                p.Stop();
            }
  
            Assert.True(queue.Count > 0);

        }
        
        [Test]
        public void NoProducersEmptyQueueTest()
        {
            for (int i = 0; i < consumersNumber; i++)
            {
                var consumer = new Consumer(i, mutex, queue, producersStopped);
                consumers.Add(consumer);
                var threadConsumer = new Thread(consumer.Consume);
                threadConsumer.Start();
            }

            foreach (var c in consumers)
            {
                c.Stop();
            }
            Assert.True(queue.Count == 0);
        }
        
        [Test]
        public void ConsumersProcessedAllApplicationsTest()
        {
            for (int i = 0; i < producersNumber; i++)
            {
                var producer = new Producer(i, mutex, queue, producersStopped);
                producers.Add(producer);
                var threadProducer = new Thread(producer.Produce);
                threadProducer.Start();
            }
            
            foreach (var p in producers)
            {
                p.Stop();
            }

            for (int i = 0; i < consumersNumber; i++)
            {
                var consumer = new Consumer(i, mutex, queue, producersStopped);
                consumers.Add(consumer);
                var threadConsumer = new Thread(consumer.Consume);
                threadConsumer.Start();
            }
            
            foreach (var c in consumers)
            {
                c.Stop();
            }
  
            Assert.True(queue.Count == 0);
        }
    }
}