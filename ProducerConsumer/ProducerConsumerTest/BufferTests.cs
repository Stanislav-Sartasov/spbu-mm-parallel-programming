using System.Collections.Generic;
using NUnit.Framework;
using ProducerConsumer;

namespace ProducerConsumerTest
{
    public class BufferTests
    {
        private IBuffer<string> _buffer;

        [SetUp]
        public void TestSetUp()
        {
            _buffer = new ListWithLock<string>();
        }

        [Test]
        public void EmptyBufferTest()
        {
            Assert.False(_buffer.TryGet(out var data));
        }

        [Test]
        public void BufferWithOneItemTest()
        {
            var testString = "test";
            _buffer.Add(testString);
            Assert.True(_buffer.TryGet(out var data));
            Assert.True(data.Equals(testString));
            Assert.False(_buffer.TryGet(out data));
        }

        [Test]
        public void BufferWithMultipleItemsTest()
        {
            var testList = new List<string>() {"test1", "test2", "test3"};
            foreach (var item in testList)
            {
                _buffer.Add(item);
            }

            while (_buffer.TryGet(out var data))
            {
                Assert.True(testList.Contains(data));
            }
            
            Assert.True(_buffer.IsEmpty());
        }
    }
}