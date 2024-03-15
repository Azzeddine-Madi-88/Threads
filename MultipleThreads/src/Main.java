import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        StopWatch greenWatch = new StopWatch(TimeUnit.SECONDS);
        StopWatch purpleWatch = new StopWatch(TimeUnit.SECONDS);
        StopWatch redWatch = new StopWatch(TimeUnit.SECONDS);
        Thread green = new Thread(greenWatch::countDown, ThreadColour.ANSI_GREEN.name());
        Thread purple = new Thread(() -> purpleWatch.countDown(7), ThreadColour.ANSI_PURPLE.name());
        Thread red = new Thread(redWatch::countDown, ThreadColour.ANSI_RED.name());
        green.start();
        purple.start();
        red.start();
    }
}

class StopWatch {
    private final TimeUnit timeUnit;

    public StopWatch(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public void countDown() {
        countDown(5);
    }

    public void countDown(int unitCount) {
        String threadName = Thread.currentThread().getName();
        ThreadColour threadColour = ThreadColour.ANSI_RESET;
        try {
            threadColour = ThreadColour.valueOf(threadName);
        } catch (IllegalArgumentException e) {
            //Ignore
        }
        String colour = threadColour.colour();
       for(int i = unitCount; i > 0; i--) {
           try {
                timeUnit.sleep(1);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
           System.out.printf("%s%s Thread : i = %d%n", colour, threadName, i);
       }
    }
}