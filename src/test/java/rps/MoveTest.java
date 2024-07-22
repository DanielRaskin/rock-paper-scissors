package rps;

import org.junit.jupiter.api.Test;

import static rps.Move.*;
import static rps.Result.*;

// simple rules test
public class MoveTest {
    @Test
    public void testMove() {
        assert ROCK.result(PAPER) == LOSS;
        assert ROCK.result(ROCK) == DRAW;
        assert ROCK.result(SCISSORS) == WIN;

        assert PAPER.result(PAPER) == DRAW;
        assert PAPER.result(ROCK) == WIN;
        assert PAPER.result(SCISSORS) == LOSS;

        assert SCISSORS.result(PAPER) == WIN;
        assert SCISSORS.result(ROCK) == LOSS;
        assert SCISSORS.result(SCISSORS) == DRAW;
    }
}
