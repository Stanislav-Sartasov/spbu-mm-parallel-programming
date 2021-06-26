using System;
using System.Collections.Generic;
using System.Threading;

namespace ProducersConsumersProgram.Objects
{
    public class Producer
    {
        private int id;
        object mutex;
        Queue<Application> queue;
        bool producersStopped;

        public Producer(int id, Object mutex, Queue<Application> queue, bool producersStopped)
        {
            this.mutex = mutex;
            this.queue = queue;
            this.producersStopped = producersStopped;
            this.id = id;
        }
        
        public void Produce()
        {
            while (!producersStopped)
            {
                var r = new Random();
                var application = new Application(r.Next(1, 100));
                Enqueue(application);
                Thread.Sleep(2000);
            }
        }
        public void Enqueue(Application application)
        {
            if (application == null)
                throw new ArgumentNullException("application");
            lock (mutex)
            {
                if (producersStopped)
                    throw new InvalidOperationException("Queue already stopped");
                queue.Enqueue(application);
                Console.WriteLine($"Producer № {this.id} put the application №{application.Id}");
                Monitor.PulseAll(mutex);
            }
        }
        public void Stop()
        {
            lock (mutex)
            {
                producersStopped = true;
                Monitor.PulseAll(mutex);
            }
        }
    }
}