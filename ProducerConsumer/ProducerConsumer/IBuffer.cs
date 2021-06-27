namespace ProducerConsumer
{
    public interface IBuffer<T>
    {
        void Add(T value);

        bool TryGet(out T value);

        bool IsEmpty();
    }
}