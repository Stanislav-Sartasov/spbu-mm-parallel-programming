using System;
using System.Threading;
using System.Threading.Tasks;

namespace ProducerConsumer
{
    public class Consumer<T>
    {
        public Consumer(IBuffer<T> bufferList, Action<T> consume, CancellationToken cToken, int sleepTime = 500)
        {
            Task.Factory.StartNew(() =>
                {
                    while (!cToken.IsCancellationRequested)
                    {
                        if (bufferList.TryGet(out T data))
                        {
                            consume(data);
                        }
                        Thread.Sleep(sleepTime);
                    }
                }, cToken, TaskCreationOptions.LongRunning, TaskScheduler.Default
            );
        }
    }
}