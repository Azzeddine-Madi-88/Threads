import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class FileWatcherExample {
    public static void main(String[] args) {
        try(WatchService watchService = FileSystems.getDefault().newWatchService()) {
            Path directory = Path.of(".");
            WatchKey watchKey = directory.register(watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE);
            boolean keepGoing = true;
            while (keepGoing) {
                try {
                    watchKey = watchService.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                List<WatchEvent<?>> events = watchKey.pollEvents();
                for(var event : events) {
                    Path context = (Path) event.context();
                    System.out.printf("Event type: %s - Context: %s%n", event.kind(), context);
                    if(context.getFileName().toString().equals("Testing.txt") && event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                        System.out.println("Watch Service shutting down");
                        watchService.close();
                        keepGoing = false;
                        break;
                    }
                }
                watchKey.reset();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
