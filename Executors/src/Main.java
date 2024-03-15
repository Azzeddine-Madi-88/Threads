import java.util.List;
import java.util.concurrent.*;

class ColorThreadFactory implements ThreadFactory {

    private String threadName;
    private int colourValue = 1;

    public ColorThreadFactory(ThreadColor colour) {
        this.threadName = colour.name();
    }

    public ColorThreadFactory() {

    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        String name = threadName;
        if(name == null) {
            name = ThreadColor.values()[colourValue].name();
        }
        if(++colourValue > (ThreadColor.values().length - 1)) {
            colourValue = 1;
        }
        thread.setName(name);
        return thread;
    }
}
public class Main {
    public static void main(String[] args) {
        var multiExecutor = Executors.newCachedThreadPool();
        try {
            List<Callable<Integer>> taskList = List.of(
                    () -> Main.sum(1, 10, 1 , "red"),
                    () -> Main.sum(10, 100, 10, "blue"),
                    () -> Main.sum(2, 20, 2, "green")
            );
            try {
                var results = multiExecutor.invokeAny(taskList);
                //for(var result : results) {
                    System.out.println(results);
                //}
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        } finally {
            multiExecutor.shutdown();
        }
    }
    public static void cashedmain(String[] args) {
        var multiExecutor = Executors.newCachedThreadPool();
        try {
            Future<Integer> futureRed = multiExecutor.submit(() -> Main.sum(1, 10, 1 , "red"));
            Future<Integer> futureBlue = multiExecutor.submit(() -> Main.sum(10, 100, 10, "blue"));
            Future<Integer> futureGreen = multiExecutor.submit(() -> Main.sum(2, 20, 2, "green"));


                try {
                    System.out.println("Future Red " + futureRed.get(500, TimeUnit.MILLISECONDS));
                    System.out.println("Future Blue " + futureBlue.get(500, TimeUnit.MILLISECONDS));
                    System.out.println("Future Green " + futureGreen.get(500, TimeUnit.MILLISECONDS));
                } catch (InterruptedException | TimeoutException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }


        } finally {
            multiExecutor.shutdown();
        }
    }
    public static void fixedmain(String[] args) {
        int count = 6;
        var multiExecutor = Executors.newFixedThreadPool(3, new ColorThreadFactory());
        for(int i = 0; i < count; i++) {
            multiExecutor.execute(Main::countDown);
        }
        multiExecutor.shutdown();

    }
    public static void singlemain(String[] args) {
        boolean isDone = false;
        var blueExecutor = Executors.newSingleThreadExecutor(new ColorThreadFactory(ThreadColor.ANSI_BLUE));
        blueExecutor.execute(Main::countDown);
        blueExecutor.shutdown();
        try {
            isDone = blueExecutor.awaitTermination(500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if(isDone) {
            System.out.println("Blue finished, starting Red");
            var redExecutor = Executors.newSingleThreadExecutor(new ColorThreadFactory(ThreadColor.ANSI_RED));
            redExecutor.execute(Main::countDown);
            redExecutor.shutdown();
            try {
                isDone = redExecutor.awaitTermination(500, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if(isDone) {
                System.out.println("Red finished, starting Yellow");
                var yellowExecutor = Executors.newSingleThreadExecutor(new ColorThreadFactory(ThreadColor.ANSI_YELLOW));
                yellowExecutor.execute(Main::countDown);
                yellowExecutor.shutdown();
                try {
                    isDone = yellowExecutor.awaitTermination(500, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if(isDone) System.out.println("All processes completed");

    }
    public static void notmain(String[] args) {
        Thread blue = new Thread(Main::countDown, ThreadColor.ANSI_BLUE.name());
        Thread red = new Thread(Main::countDown, "ANSI_RED");
        Thread yellow = new Thread(Main::countDown, "ANSI_YELLOW");

        blue.start();

        try {
            blue.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        yellow.start();

        try {
            yellow.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        red.start();

        try {
            red.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void countDown() {
        String threadName = Thread.currentThread().getName();
        var threadColour = ThreadColor.ANSI_RESET;
        try {
            threadColour = ThreadColor.valueOf(threadName.toUpperCase());
        } catch (IllegalArgumentException ignore) {
            // User may pass a bad colour
        }

        String colour = threadColour.color();
        for(int i = 20; i >= 0; i--) {
            System.out.println(colour + " " + threadName.replace("ANSI_", "") + " " + i);
        }
    }

    private static int sum(int start, int end, int delta, String colourString) {
        var threadColour = ThreadColor.ANSI_RESET;
        try {
            threadColour = ThreadColor.valueOf("ANSI_" + colourString.toUpperCase());
        } catch (IllegalArgumentException ignore) {
            // Bad colour passed
        }
        String colour = threadColour.color();
        int sum = 0;
        for(int i = start; i <= end; i += delta) {
            sum += i;
        }
        System.out.println(colour + Thread.currentThread().getName() + ", " + colourString + " " + sum);
        return sum;
    }
}