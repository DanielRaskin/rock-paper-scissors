package rps;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;

import static rps.Result.*;

/**
 * User session
 */
public class UserSession implements Runnable {
    private final Server server;
    private final Socket socket;

    public UserSession(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    @Override
    public void run() {
        Player player = null;
        PrintWriter writer = null;
        try (socket) {
            writer = new PrintWriter(socket.getOutputStream(), true);
            if (! server.newConnection()) {
                // server doesn't allow new connections if there are too many players
                writer.println("Невозможно начать игру, слишком много игроков. Попробуйте позже.");
                return;
            }
            socket.setSoTimeout(Server.TIMEOUT);
            var is = socket.getInputStream();
            is.skip(is.available());
            var reader = new BufferedReader(new InputStreamReader(is));
            writer.println("Привет! Добро пожаловать в игру \"Камень, ножницы, бумага\"!");
            String playerNick;
            do {
                // ask for nick till user enter unique nick
                writer.printf("Введите пожалуйста ваш ник: ");
                playerNick = reader.readLine();
                player = server.newPlayer(playerNick);
                if (player == null) {
                    writer.printf("К сожалению ник %s уже используется, выберите пожалуйста другой ник", playerNick);
                }
            } while (player == null);
            var opponentNick = player.getOpponentNick();
            writer.printf("%s, вашим соперником будет %s. Удачи!\n", playerNick, opponentNick);
            Result result;
            do {
                // new round
                if (! player.isOpponentLive()) {
                    writer.println("Извините, к сожалению ваш соперник покинул игру");
                    return;
                }
                writer.printf("Раунд %d%n", player.getRound());
                Move move;
                do {
                    // player should enter correct move
                    writer.printf("Камень (1), ножницы (2) или бумага (3)? Ваш выбор: ");
                    var input = reader.readLine();
                    move = Util.convertUserInputToMove(input);
                    if (move == null) {
                        writer.println("Некорректный ввод. Пожалуйста, попробуйте еще раз.");
                    }
                } while (move == null);
                // play with this move
                result = player.play(move);
                writer.printf("Ваш соперник выбрал %s%n", Util.convertMoveToUserOutput(player.getOpponentMove()));
                // if draw - next round
                if (result == DRAW) {
                    writer.println("Ничья! Назначен новый раунд.");
                }
            } while (result == DRAW);
            if (result == WIN) {
                writer.printf("Поздравляем %s, вы победили!%n", playerNick);
            } else {
                writer.printf("К сожалению %s вы проиграли. Победитель %s.%n", playerNick, opponentNick);
            }
            writer.printf("%s, спасибо за игру! Приходите играть еще!%n", playerNick);
        } catch (IOException e) {
            writer.println((e instanceof SocketTimeoutException) ? "Превышено время ожидания" : "Ошибка ввода-вывода");
            e.printStackTrace();
        } finally {
            server.releasePlayer(player);
        }
    }
}
