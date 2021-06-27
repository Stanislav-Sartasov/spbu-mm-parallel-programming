package Solution;

public class Producer extends Thread{
    private int count = 0;
    Monitor monitor;
    ProducerConsumer producerConsumer;
    Producer(Monitor monitor, ProducerConsumer producerConsumer){
        this.monitor = monitor;
        this.producerConsumer = producerConsumer;
    }

    @Override
    public void run() {
        while (!producerConsumer.getThreadFlag()){
            if(count < 3) {
                waitOrSet(monitor.criticalSection("set"));
            }else {
                try {
                    Thread.sleep(1000);
                }catch (InterruptedException e) {
                }
                count = 0;
            }
        }
        System.out.println("Solution.Producer закончил работу: " + currentThread().getId());
    }

    private void waitOrSet(int check){
        switch (check){
            case 0:count++;
            case 1:try {  Thread.sleep(1000);}catch (InterruptedException e){}
        }
    }
}
