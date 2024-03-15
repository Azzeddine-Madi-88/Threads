import java.util.Arrays;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        int numbersLength = 100_000_000;
        var numbers = new Random().longs(numbersLength, 1, numbersLength).toArray();
        long delta = 0;
        long deltaSum = 0;
        int iterations = 25;

        for(int i = 0; i < iterations; i++) {
            long start = System.nanoTime();
            double averageSerial = Arrays.stream(numbers).average().orElseThrow();
            long elapsedSerial = System.nanoTime() - start;
            System.out.printf("Ave =  %.2f , elapsed = %d nanos or %.2f ms%n", averageSerial, elapsedSerial, elapsedSerial / 1000000.0);
            start = System.nanoTime();
            double newSum = Arrays.stream(numbers)
                    .parallel()
                    .average()
                    .orElseThrow();
            long elapsedParallel = System.nanoTime() - start;
            System.out.printf("Ave =  %.2f , elapsed = %d nanos or %.2f ms%n", averageSerial, elapsedParallel, elapsedParallel / 1000000.0);

            deltaSum += delta = elapsedSerial - elapsedParallel;

            System.out.println("Delta = " + delta / 1000000.0);
            System.out.println("***************************************************");
        }

        System.out.println("Delta average = " +  (deltaSum / iterations) / 1000000.0);


    }
}