package rps;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

// server test
public class ServerTest {
    @Test
    public void testServer() throws Exception {
        Server server = new Server();
        var firstLatch = new CountDownLatch(2);
        var executor = Executors.newVirtualThreadPerTaskExecutor();
        var result = new AtomicBoolean(true);
        // John and Jack should be opponents
        executor.execute(() -> {
            if (! server.newPlayer("John").getOpponentNick().equals("Jack")) {
                result.set(false);
            };
            firstLatch.countDown();
        });
        executor.execute(() -> {
            if (! server.newPlayer("Jack").getOpponentNick().equals("John")) {
                result.set(false);
            }
            firstLatch.countDown();
        });
        firstLatch.await();
        var secondLatch = new CountDownLatch(1);
        executor.execute(() -> {
            // name Jack shouldn't be allowed because is already used
            if (server.newPlayer("Jack") != null) {
                result.set(false);
            }
            secondLatch.countDown();
        });
        secondLatch.await();
        assert result.get();
    }
}
