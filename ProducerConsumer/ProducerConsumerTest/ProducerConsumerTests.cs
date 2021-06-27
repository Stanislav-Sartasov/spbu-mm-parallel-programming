using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Threading;
using NUnit.Framework;
using ProducerConsumer;

namespace ProducerConsumerTest
{
    public class ProducerConsumerTests
    {
        [Test]
        public void NoProducersTest()
        {
            var dataRecieved = false;

            using var cTokenSource = new CancellationTokenSource();
            var cToken = cTokenSource.Token;
            var bufferList = new ListWithLock<string>();
            var consumers = new List<Consumer<string>>()
            {
                new(bufferList, data => dataRecieved = true, cToken)
            };

            cTokenSource.CancelAfter(7000);
            Thread.Sleep(3000);

            Assert.False(dataRecieved);
            Assert.True(bufferList.IsEmpty());
        }


        [Test]
        public void NoConsumersTest()
        {
            using var cTokenSource = new CancellationTokenSource();
            var cToken = cTokenSource.Token;
            var bufferList = new ListWithLock<string>();
            var producers = new List<Producer<string>>()
            {
                new(bufferList, () => "test1", cToken),
                new(bufferList, () => "test2", cToken),
                new(bufferList, () => "test3", cToken),
            };

            cTokenSource.CancelAfter(7000);
            Thread.Sleep(3000);

            Assert.False(bufferList.IsEmpty());
        }

        [Test]
        public void CancellationTest()
        {
            using var producerCTokenSource = new CancellationTokenSource();
            using var consumerCTokenSource = new CancellationTokenSource();
            var producerCToken = producerCTokenSource.Token;
            var consumerCToken = consumerCTokenSource.Token;

            var bufferList = new ListWithLock<string>();
            var producers = new List<Producer<string>>()
            {
                new(bufferList, () => "test1", producerCToken),
                new(bufferList, () => "test2", producerCToken),
            };
            var consumers = new List<Consumer<string>>()
            {
                new(bufferList, data => Thread.Sleep(0), consumerCToken),
                new(bufferList, data => Thread.Sleep(0), consumerCToken)
            };

            Thread.Sleep(5000);

            producerCTokenSource.Cancel();

            Assert.True(WaitWithTimeout(() => bufferList.IsEmpty(), 10000));
        }

        [Test]
        public void FullProducerConsumerTest()
        {
            using var cTokenSource = new CancellationTokenSource();
            var cToken = cTokenSource.Token;
            var testList = new List<string>() {"test1", "test2"};

            var recievedData1 = new ConcurrentQueue<string>();
            var recievedData2 = new ConcurrentQueue<string>();

            var bufferList = new ListWithLock<string>();
            var producers = new List<Producer<string>>()
            {
                new(bufferList, () => testList[0], cToken),
                new(bufferList, () => testList[1], cToken),
            };
            var consumers = new List<Consumer<string>>()
            {
                new(bufferList, data => recievedData1.Enqueue(data), cToken),
                new(bufferList, data => recievedData2.Enqueue(data), cToken)
            };

            WaitWithTimeout(
                () => recievedData1.Distinct().ToList().Equals(testList) &&
                      recievedData2.Distinct().ToList().Equals(testList), 20000);
        }

        private static bool WaitWithTimeout(Func<bool> statement, int timeout)
        {
            var sw = new Stopwatch();
            sw.Start();
            while (sw.ElapsedMilliseconds < timeout)
            {
                if (statement())
                    return true;
            }

            sw.Stop();
            return false;
        }
    }
}