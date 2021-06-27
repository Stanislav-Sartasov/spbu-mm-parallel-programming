using System.Collections.Generic;
using System.Linq;

namespace ProducerConsumer
{
    public class ListWithLock<T> : IBuffer<T>
    {
        private readonly object _lock = new();
        private readonly List<T> _list = new();

        public void Add(T value)
        {
            lock (_lock)
            {
                _list.Add(value);
            }
        }

        public bool TryGet(out T value)
        {
            lock (_lock)
            {
                if (_list.Count > 0)
                {
                    var lastIndex = _list.Count - 1;
                    value = _list[lastIndex];
                    _list.RemoveAt(lastIndex);
                    return true;
                }
                value = default;
                return false;
            }
        }

        public bool IsEmpty()
        {
            lock (_lock)
            {
                return _list.Count == 0;
            }
        }
    }
}