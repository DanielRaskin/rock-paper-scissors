package rps;

/**
 * Utility methods
 */
public class Util {
    // string to move
    public static Move convertUserInputToMove(String input) {
        return switch (input.trim().toUpperCase()) {
            case "КАМЕНЬ", "1" -> Move.ROCK;
            case "НОЖНИЦЫ", "2" -> Move.SCISSORS;
            case "БУМАГА", "3" -> Move.PAPER;
            default -> null;
        };
    }

    // move to string
    public static String convertMoveToUserOutput(Move move) {
        return switch (move) {
            case Move.ROCK -> "КАМЕНЬ";
            case Move.SCISSORS -> "НОЖНИЦЫ";
            case Move.PAPER -> "БУМАГА";
        };
    }
}
