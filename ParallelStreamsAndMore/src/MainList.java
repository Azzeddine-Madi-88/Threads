import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Stream;

public class MainList {
    public static void main(String[] args) {

        var threadMap = new ConcurrentSkipListMap<String, Long>();
        var persons = Stream.generate(Person::new)
                .limit(10000)
                .parallel()
                .peek(p -> {
                    var threadName = Thread.currentThread().getName()
                            .replace("ForkJoinPool.commonPool-worker-", "thread_");
                   threadMap.merge(threadName, 1L, Long::sum);
                })
                .toArray(Person[]::new);
        System.out.println("Total = " + persons.length);

        threadMap.entrySet().forEach(System.out::println);
        long total = 0;
        for(var num : threadMap.values()) {
            total += num;
        }
        System.out.println("Total number of Thread: " + total);
    }
}
