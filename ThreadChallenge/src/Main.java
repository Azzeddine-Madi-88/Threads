import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
            Thread odd = new Thread(() -> {
                try {
                    int i = 0;
                    for(int j = 1; j < 20; j++) {
                        if( j % 2 == 1 && i < 5) {
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
            }, "Odd");

        ThreadExtend even = new ThreadExtend();

        even.start();
        odd.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}