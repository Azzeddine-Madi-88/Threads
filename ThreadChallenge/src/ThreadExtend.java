import java.util.concurrent.TimeUnit;

public class ThreadExtend extends Thread{
    @Override
    public void run() {
        Thread.currentThread().setName("Even");
        int i = 0;
        try {
            for(int j = 1; j < 20; j++) {
                if( j % 2 == 0 && i < 5) {
                    TimeUnit.SECONDS.sleep(1);
                    System.out.println(j);
                    i++;
                }
                else if(i >= 5) return;
            }
        } catch (InterruptedException e) {
            System.out.println("Whoops " + Thread.currentThread().getName() + " has been interrupted");
            Thread.currentThread().interrupt();
        }

    }
}
