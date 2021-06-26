using System;
using System.Collections.Generic;
using System.Threading;

namespace ProducersConsumersProgram.Objects
{
    public class Consumer
    {
        private int id;
        object mutex;
        Queue<Application> queue;
        bool producersStopped;
        
        public Consumer(int id, Object mutex, Queue<Application> queue, bool producersStopped)
        {
            this.mutex = mutex;
            this.queue = queue;
            this.producersStopped = producersStopped;
            this.id = id;
        }
        
        public  void Consume()
        {
            while (true)
            {
                var application = Dequeue();
                if (application == null)
                    break;
                Console.WriteLine($"Consumer№{id} processing application № {application.Id}");
                Thread.Sleep(application.TimeToProcess);
                Console.WriteLine($"Consumer№{id} finished application № {application.Id}");
                
                Thread.Sleep(2000);
            }
        }
        public Application Dequeue()
        {
            lock (mutex)
            {
                while (queue.Count == 0 && !producersStopped)
                    Monitor.Wait(mutex);

                if (queue.Count == 0)
                    return null;

                return queue.Dequeue();
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