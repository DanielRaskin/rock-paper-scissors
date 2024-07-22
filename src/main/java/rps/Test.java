package rps;

import java.io.*;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

/**
 * Simple load test
 */
public class Test {
    private static final int PLAYERS_COUNT = 50;
    private final Random RANDOM_GENERATOR = new Random();
    private final CountDownLatch latch = new CountDownLatch(PLAYERS_COUNT);

    // run load test
    public static void main(String[] args) throws Exception {
        new Test().runTest();
    }

    public void runTest() throws Exception {
        var executor = Executors.newVirtualThreadPerTaskExecutor();
        for (int i = 0; i < PLAYERS_COUNT; i++) {
            // create 50 players
            var number = i;
            executor.execute(() -> emulateUserSession(number));
        }
        // wait all players finish
        latch.await();
    }

    private void emulateUserSession(int number) {
        try (var socket = new Socket("127.0.0.1", 23); var resultWriter = new FileWriter("result" + number + ".txt")) {
            var socketWriter = new PrintWriter(socket.getOutputStream(), true);
            var socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            var result = new StringBuilder();
            var input = "Player_" + number; // this is the nick
            String oldInput = null;
            // while connection is live
            while (gameServerResponse(socketReader, result, oldInput)) {
                socketWriter.println(input);
                oldInput = input;
                // do the move
                input = switch (Math.abs(RANDOM_GENERATOR.nextInt()) % 3) {
                    case 0 -> "камень";
                    case 1 -> "ножницы";
                    case 2 -> "бумага";
                    default -> null;
                };
            }
            // save game log to the file
            resultWriter.write(result.toString());
            resultWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            // count down then player finish
            latch.countDown();
        }
    }

    // get response from a server, returns true if connection is still live, false if not
    private boolean gameServerResponse(Reader socketReader, StringBuilder result, String oldInput) throws IOException {
        var textFromSocket = new StringBuilder();
        int symbol;
        try {
            // wait for the input
            symbol = socketReader.read();
        } catch (IOException e) {
            return false;
        }
        // return false if connection is actually closed
        if (symbol == -1) {
            return false;
        } else {
            try {
                // wait for a while to let server prepare all it's output
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // save first symbol
            textFromSocket.append((char)symbol);

            // read all server output
            while (socketReader.ready()) {
                textFromSocket.append((char) (socketReader.read()));
            }
            // save our input and server output
            if (oldInput != null) {
                result.append(oldInput).append('\n').append(textFromSocket.toString());
            } else {
                result.append(textFromSocket.toString());
            }
            // connection is live, return true
            return true;
        }
    }
}
