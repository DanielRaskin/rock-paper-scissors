package rps;

import org.junit.jupiter.api.Test;

import static rps.Move.*;

// simple Util tests
public class UtilTest {
    @Test
    public void testTextToMove() {
        assert Util.convertUserInputToMove("камень ") == ROCK;
        assert Util.convertUserInputToMove("1") == ROCK;
        assert Util.convertUserInputToMove("  ножницы ") == SCISSORS;
        assert Util.convertUserInputToMove("  2 ") == SCISSORS;
        assert Util.convertUserInputToMove("   бумага         ") == PAPER;
        assert Util.convertUserInputToMove(" 3") == PAPER;
        assert Util.convertUserInputToMove("кмнь") == null;
    }

    @Test
    public void testMoveToText() {
        assert "КАМЕНЬ".equals(Util.convertMoveToUserOutput(ROCK));
        assert "НОЖНИЦЫ".equals(Util.convertMoveToUserOutput(SCISSORS));
        assert "БУМАГА".equals(Util.convertMoveToUserOutput(PAPER));
    }
}
