package Solution;

import java.util.ArrayList;
public class Monitor {
    private volatile ArrayList<Integer> arraInts = new ArrayList<>();
    private final ProducerConsumer producerConsumer;
    Monitor(ProducerConsumer producerConsumer){
        this.producerConsumer = producerConsumer;
    }

    public synchronized int criticalSection(String operation) {
        if (operation.equals("get") && producerConsumer.getThreadFlag()){
          if(arraInts.size() == 0) {
              return 1;
          }
            arraInts.remove(0);
            return 0;
        }
        else if (operation.equals("set") && producerConsumer.getThreadFlag()){
            arraInts.add((int)(Math.random()*10));
            return 0;
        }else {
        return 2;
        }
    }
}