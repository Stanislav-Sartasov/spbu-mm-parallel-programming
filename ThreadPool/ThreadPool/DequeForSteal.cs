using System.Collections.Generic;
using System.Linq;

namespace ThreadPool
{
    public partial class MyThreadPool
    {
        private class DequeForSteal<T>
        {
            private readonly object _lock = new();
            private readonly LinkedList<T> _deque = new();

            public void Push(T value)
            {
                lock (_lock)
                {
                    _deque.AddFirst(value);
                }
            }

            public bool TryPopLast(out T value)
            {
                lock (_lock)
                {
                    value = default;
                    if (_deque.Count == 0)
                        return false;

                    value = _deque.Last();
                    _deque.RemoveLast();

                    return true;
                }
            }

            public bool TryPopFirst(out T value)
            {
                lock (_lock)
                {
                    value = default;
                    if (_deque.Count == 0)
                        return false;

                    value = _deque.First();
                    _deque.RemoveFirst();

                    return true;
                }
            }
        }
    }
}