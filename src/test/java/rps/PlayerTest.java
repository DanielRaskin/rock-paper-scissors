package rps;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static rps.Move.*;
import static rps.Result.*;

// Player test
public class PlayerTest {
    @Test
    public void testWin() throws Exception {
        testPlay(SCISSORS, PAPER, WIN, LOSS);
    }

    @Test
    public void testDraw() throws Exception {
        testPlay(ROCK, ROCK, DRAW, DRAW);
    }

    private void testPlay(Move moveOfJohn, Move moveOfJack, Result resultOfJohn, Result resultOfJack) throws Exception {
        var playerJohn = new Player("John");
        var playerJack = new Player("Jack");
        playerJack.setOpponent(playerJohn);
        playerJohn.setOpponent(playerJack);
        var latch = new CountDownLatch(2);
        var executor = Executors.newVirtualThreadPerTaskExecutor();
        var result = new AtomicBoolean(true);
        executor.execute(() -> {
            if (playerJohn.play(moveOfJohn) != resultOfJohn) {
                result.set(false);
            }
            latch.countDown();
        });
        executor.execute(() -> {
            if (playerJack.play(moveOfJack) != resultOfJack) {
                result.set(false);
            }
            latch.countDown();
        });
        latch.await();
        assert result.get();
        assert playerJohn.getRound() == playerJack.getRound();
        assert playerJohn.getRound() == ((resultOfJohn == DRAW) ? 2 : 1);
    }
}
