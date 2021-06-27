using System;
using System.Threading;
using System.Threading.Tasks;

namespace ProducerConsumer
{
    public class Producer<T>
    {
        public Producer(IBuffer<T> bufferList, Func<T> produce, CancellationToken cToken, int sleepTime = 500)
        {
            Task.Factory.StartNew(() =>
                {
                    while (!cToken.IsCancellationRequested)
                    {
                        bufferList.Add(produce());
                        Thread.Sleep(sleepTime);
                    }
                }, cToken, TaskCreationOptions.LongRunning, TaskScheduler.Default
            );
        }
    }
}