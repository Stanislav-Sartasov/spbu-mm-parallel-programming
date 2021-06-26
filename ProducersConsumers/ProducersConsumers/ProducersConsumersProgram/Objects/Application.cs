using System;

namespace ProducersConsumersProgram.Objects
{
    public class Application
    {
        public int Id { get; }
        
        public int TimeToProcess { get; }
        public Application(int id)
        {
            this.Id = id;
            this.TimeToProcess =  new Random().Next(1000, 10000);
        }
        
    }
}