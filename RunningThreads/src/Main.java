import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        System.out.println("Main thread running");
        try {
            System.out.println("Main thread paused for one second");
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Thread thread = new Thread(() -> {
            String tname = Thread.currentThread().getName();
            System.out.println(tname + " thread running");
            try {
                System.out.println(tname + " should take 10 dots to run");
                for(int i = 1; i <= 10; i++) {
                    System.out.print(" . ");
                    TimeUnit.MILLISECONDS.sleep(500);
                }
                System.out.println("\n" + tname + " completed.");
            } catch (InterruptedException e) {
                System.out.println("\nWhoops!! " + tname + " has been interrupted");
                Thread.currentThread().interrupt();
            }
        });

        Thread installThread = new Thread(() -> {
           try {
               for(int i = 0; i < 3; i++){
                   TimeUnit.MILLISECONDS.sleep(250);
                   System.out.println("Installation Step " + (i + 1 ) + " is completed");
               }
           } catch(InterruptedException e) {
               e.printStackTrace();
           }
        }, "InstallThread");

        Thread threadMonitor = new Thread(() -> {
            long now = System.currentTimeMillis();
            while (thread.isAlive()) {
                try {
                    Thread.sleep(8000);
                    if(System.currentTimeMillis() - now > 2000) {
                        thread.interrupt();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }, "threadMonitor");

        System.out.println(thread.getName() + " starting");
        thread.start();
        threadMonitor.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if(!thread.isInterrupted()) {
            installThread.start();
        } else {
            System.out.println("Previous thread was interrupted, " + installThread.getName() + " can't run");
        }

    }
}