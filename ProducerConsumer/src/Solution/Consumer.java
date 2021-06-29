package Solution;

public class Consumer extends Thread{
    private int count = 0;
    Monitor monitor;
    ProducerConsumer producerConsumer;
    Consumer(Monitor monitor, ProducerConsumer producerConsumer){
        this.monitor = monitor;
        this.producerConsumer = producerConsumer;
    }
    @Override
    public void run() {
        while (!producerConsumer.getThreadFlag()){
            if(count < 3) {
                count++;
                monitor.get();
            }else {
                try {
                    Thread.sleep(1000);
                }catch (InterruptedException e) {
                }
                count = 0;
            }
        }
        System.out.println("Solution.Consumer закончил работу: " + currentThread().getId());
    }
}
