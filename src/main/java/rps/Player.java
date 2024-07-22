package rps;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static rps.Result.DRAW;

/**
 * Player
 */
public class Player {
    private final String nick;
    private int round;
    private final BlockingQueue<Move> moves;
    private Player opponent;
    private Move opponentMove;
    private volatile boolean live;

    public Player(String nick) {
        this.nick = nick;
        this.moves = new ArrayBlockingQueue<Move>(1);
        this.round = 1;
        live = true;
    }

    // server calls setOpponent after creating Player object, then opponent for this object found
    void setOpponent(Player opponent) {
        this.opponent = opponent;
    }

    public int getRound() {
        return round;
    }

    // play a round
    public Result play(Move move) {
        try {
            moves.put(move);
            // get opponent move from blocking queue
            opponentMove = opponent.moves.poll(Server.TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        var result = move.result(opponentMove);
        if (result == DRAW) {
            // if this is a draw, new round
            round++;
        }
        return result;
    }

    public String getNick() {
        return nick;
    }

    public String getOpponentNick() {
        return opponent.nick;
    }

    public Move getOpponentMove() {
        return opponentMove;
    }

    public boolean isOpponentLive() {
        return opponent.live;
    }

    public void finish() {
        live = false;
    }
}
