package Solution;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
 public class ProducerConsumer {
    //Получаем количество P и C
    private final int numberMan;
    private final int numberCon;
    public ProducerConsumer(int numberMan, int numberCon){
        this.numberMan = numberMan;
        this.numberCon = numberCon;
    }
    //Флаг для остановки работы P и C
    private boolean threadsStop = false;
    public boolean getThreadFlag(){
        return threadsStop;
    }
    public void setThreadFlag(){
        threadsStop = true;
    }
    private  final ArrayList<Thread> allThreads = new ArrayList<>();
    public ArrayList<Thread> getAllThreads(){
        return  allThreads;
    }
    public void startProgram() {
        //Слушатель нажатия клавиши
        JFrame frame = new JFrame();
        frame.setVisible(true);
        frame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                setThreadFlag();
            }
        });

        Monitor monitor = new Monitor(this);
        for(int i = 0; i < numberMan; i++){
            Producer m = new Producer(monitor,this);
            m.start();
            allThreads.add(m);
        }
        for(int i = 0; i < numberCon; i++){
            Consumer c = new Consumer(monitor,this);
            c.start();
            allThreads.add(c);
        }
    }
}
