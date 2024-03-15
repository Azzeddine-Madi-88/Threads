import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class MessageRepository {
    private String message;
    private boolean hasMessage = false;

    private final Lock lock = new ReentrantLock();
    public String read()  {
        if(lock.tryLock()) {
            try {
                while(!hasMessage) {
                    // Wait for Message
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                hasMessage = false;
            } finally {
                lock.unlock();
            }
        } else {
            System.out.println("** read blocked " + lock);
            hasMessage = false;
        }
        return message;
    }

    public  void write(String message)  {
        try {
            if(lock.tryLock(3, TimeUnit.SECONDS)) {
                try {
                    while (hasMessage) {
                        // Wait till the Consumer read the message
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    hasMessage = true;
                }
                finally {
                    lock.unlock();
                }
            }
            else {
                System.out.println("** write blocked " + lock);
                hasMessage = true;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.message = message;
    }
}

class MessageWriter implements Runnable {

    private MessageRepository outGoingMessage;
    private final String text = """
            Humpty Dumpty sat on a wall
            Humpty Dumpty had a great fall,
            All the king's horses and all the king's men,
            Couldn't put Humpty together again.""";

    public MessageWriter(MessageRepository outGoingMessage) {
        this.outGoingMessage = outGoingMessage;
    }

    @Override
    public void run() {
        Random random = new Random();
        String[] lines = text.split("\n");
        for(int i = 0; i < lines.length; i++) {

            try {
                outGoingMessage.write(lines[i]);
                Thread.sleep(random.nextInt(500, 2000));

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        outGoingMessage.write("Finished");
    }
}

class MessageReader implements Runnable {
    private MessageRepository inComingMessage;
    public MessageReader(MessageRepository inComingMessage) {
        this.inComingMessage = inComingMessage;
    }

    @Override
    public void run() {
        Random random = new Random();
        String message = "";
        do {
            try {
                Thread.sleep(random.nextInt(500, 2000));
                message = inComingMessage.read();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(message);
        } while (!message.equals("Finished"));

    }
}
public class Main {
    public static void main(String[] args) {
        MessageRepository messageRepository = new MessageRepository();
        Thread writer = new Thread(new MessageWriter(messageRepository), "Writer");
        Thread reader = new Thread(new MessageReader(messageRepository), "Reader");

        writer.setUncaughtExceptionHandler((thread, exc) -> {
            System.out.println("Writer had exception: " + exc);
            if(reader.isAlive()) {
                System.out.println("Going to interrupt the reader");
                reader.interrupt();
            }
        });
        reader.setUncaughtExceptionHandler((thread, exc) -> {
            System.out.println("Reader had exception: " + exc);
            if(writer.isAlive()) {
                System.out.println("Going to interrupt the writer");
                writer.interrupt();
            }
        });
        reader.start();
        writer.start();
    }
}