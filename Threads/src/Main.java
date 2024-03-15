import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        var currentThread = Thread.currentThread();
        printThreadState(currentThread);

        currentThread.setName("MainGuy");
        currentThread.setPriority(Thread.MAX_PRIORITY);
        printThreadState(currentThread);

        CustomThread customThread = new CustomThread();
        customThread.start();

//        new Thread(() -> {
//            for(int i = 1 ; i <= 5; i++) {
//                System.out.print(" 1 ");
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//        }}).start();

       Runnable runnable = () -> {
           for(int i = 1 ; i <= 8; i++) {
               System.out.print(" 2 ");
               try {
                   TimeUnit.MILLISECONDS.sleep(250);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
       };

       Thread t = new Thread(runnable);
       t.start();

        for(int i = 1; i <= 3; i++) {
            System.out.print(" 0 ");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }


    }

    public static void printThreadState(Thread thread) {
        System.out.println("---------------------------");
        System.out.println("Thread ID: " + thread.getId());
        System.out.println("Thread name: " + thread.getName());
        System.out.println("Thread priority: " + thread.getPriority());
        System.out.println("Thread state: " + thread.getState());
        System.out.println("Thread group: " + thread.getThreadGroup());
        System.out.println("Thread is alive: " + thread.isAlive());
        System.out.println("---------------------------");
    }
}