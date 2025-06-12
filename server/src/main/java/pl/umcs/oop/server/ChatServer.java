package pl.umcs.oop.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {
    private static final int PORT = 12345;
    private static final List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private static final ExecutorService threadPool = Executors.newCachedThreadPool();
    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(PORT)){
            System.out.println("Serwer uruchomiony na porcie "+PORT);
            while (true) {
                System.out.println("Oczekiwanie na połączenie...");
                Socket socket = serverSocket.accept();
                System.out.println("Połączono "+socket);
                ClientHandler clientHandler = new ClientHandler(socket, clients);
                clients.add(clientHandler);
                threadPool.submit(clientHandler);
            }
        } catch (IOException e) {
            System.err.println("Błąd serwera: " + e.getMessage());
        } finally {
            threadPool.shutdown();
        }

    }
}