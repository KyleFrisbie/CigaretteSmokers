import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MultithreadingExample {

    static int counter = 0;
    static Semaphore semaphore = new Semaphore(1);

    public static void incrementCounter(){
        try {
            semaphore.acquire();
            counter++;
            semaphore.release();
        } catch (InterruptedException ex) {
            Logger.getLogger(MultithreadingExample.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main(String[] args) {

        Thread thread1 = new Thread() {

            @Override
            public void run() {
                for (int i = 0; i < 5000; i++) {
                    incrementCounter();
                }

            }
        };


        Thread thread2 = new Thread() {

            @Override
            public void run() {
                for (int i = 0; i < 5000; i++) {
                    incrementCounter();
                }

            }
        };

        thread1.start();
        thread2.start();

        while (thread1.isAlive() || thread2.isAlive()) {
        }

        System.out.println("Counter : " + counter);


    }
}