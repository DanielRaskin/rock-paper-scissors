package rps;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Exchanger;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Server
 */
public class Server {
    public static int PORT = 23;
    public static final int MAX_NUMBER_OF_CONNECTIONS = 50;
    public static final int TIMEOUT = 60000;

    private AtomicInteger numberOfConnections = new AtomicInteger(0);
    private Exchanger<Player> playerExchanger = new Exchanger<>();
    private Set<String> nicks = ConcurrentHashMap.newKeySet();

    // start the server
    public void start() {
        // use virtual threads
        var executor = Executors.newVirtualThreadPerTaskExecutor();
        try (var serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started");
            while (true) {
                // start new user session in a virtual thread
                executor.execute(new UserSession(this, serverSocket.accept()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // count live connections
    public boolean newConnection() {
        var count = numberOfConnections.getAndUpdate(i -> ((i < MAX_NUMBER_OF_CONNECTIONS) ? i + 1 : i));
        return count < MAX_NUMBER_OF_CONNECTIONS;
    }

    // create new player - returns created new player or null if nick is already used
    public Player newPlayer(String nick) {
        Player player, opponent;
        // nick should be unique for current players
        if (nicks.add(nick)) {
            try {
                player = new Player(nick);
                // find opponent
                opponent = playerExchanger.exchange(player);
            } catch (InterruptedException e) {
                nicks.remove(nick);
                throw new RuntimeException(e);
            }
            player.setOpponent(opponent);
            return player;
        } else {
            // return null if nick is already used
            return null;
        }
    }

    // release player then player is leaving
    public void releasePlayer(Player player) {
        numberOfConnections.decrementAndGet();
        if (player != null) {
            player.finish();
            // release nick
            nicks.remove(player.getNick());
        }
    }
}
