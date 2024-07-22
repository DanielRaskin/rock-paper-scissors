package rps;

import static rps.Result.*;

/**
 * Game move
 */
public enum Move {
    // every move contains game results for all possible opponent moves
    ROCK(new Result[] {DRAW, LOSS, WIN}),
    PAPER(new Result[] {WIN, DRAW, LOSS}),
    SCISSORS(new Result[] {LOSS, WIN, DRAW});

    private Result[] results;

    Move(Result[] results) {
        this.results = results;
    }

    // result of game for this move and given opponent move
    public Result result(Move otherMove) {
        return results[otherMove.ordinal()];
    }
}
