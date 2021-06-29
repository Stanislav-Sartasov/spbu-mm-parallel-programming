package Solution;

import java.util.ArrayList;
public class Monitor {
    private volatile ArrayList<Integer> arraInts = new ArrayList<>();
    private final ProducerConsumer producerConsumer;
    Monitor(ProducerConsumer producerConsumer) {
        this.producerConsumer = producerConsumer;
    }

    public synchronized void get(){
          while (arraInts.size() == 0 && !producerConsumer.getThreadFlag()){
              try {
                  wait(1000);
              }catch (InterruptedException e){

              }
          }
          notify();

          if(arraInts.size() != 0 && !producerConsumer.getThreadFlag())
          arraInts.remove(0);
    }
    public synchronized void set(){
        if (!producerConsumer.getThreadFlag())
        arraInts.add((int)(Math.random()*10));
    }
}