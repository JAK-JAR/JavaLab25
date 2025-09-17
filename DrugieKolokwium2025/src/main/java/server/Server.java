package server;

import Game.Duel;
import Game.Player;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Server {
    private final int port;
    private List<ClientHandler> clients;
    private Database database;

    public Server(int port) {
        this.port = port;
    }

    public Database getDatabase() {
        return database;
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    public void listen() {
        while(true) {
            ClientHandler handler = null;
            try (ServerSocket socket = new ServerSocket(port)) {
                Socket clientSocket = socket.accept();
                handler = new ClientHandler(this, clientSocket);
                clients.add(handler);
                new Thread(handler).start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                removeClient(handler);
            }
        }
    }

    public static void sendMessage(String message, Socket socket) {
        try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            writer.write(message);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void challengeToDuel(ClientHandler challenger, String chalengeeLogin) {
        boolean ok = false;
        for(ClientHandler client : clients) {
            if(client.getLogin().equals(chalengeeLogin)) {
                startDuel(challenger, client);
                ok = true;
                break;
            }
        }
        if(!ok) {
            sendMessage("User with login " + chalengeeLogin + " not found!", challenger.getSocket());
        }
    }

    private void startDuel(ClientHandler challenger, ClientHandler challengee) {
        if(challenger.isInDuel()) {
            sendMessage("You are already in a duel!", challenger.getSocket());
            return;
        }
        if(challengee.isInDuel()) {
            sendMessage("User " + challengee.getLogin() + " is already in a duel!", challenger.getSocket());
            return;
        }
        if(challenger == challengee) {
            sendMessage("You cannot duel yourself!", challenger.getSocket());
            return;
        }
        else {
            Duel duel = new Duel(challenger, challengee);
            sendMessage("Duel started!", challenger.getSocket());
            sendMessage("Duel started!", challengee.getSocket());
            duel.setOnEnd(() -> {
                Duel.Result result = duel.evaluate();
                if (result == null) {
                    sendMessage("Duel ended in a draw!", challenger.getSocket());
                    sendMessage("Duel ended in a draw!", challengee.getSocket());
                } else {
                    if (result.winner() == challenger) {
                        result.winner().setLogin(challenger.getLogin());
                    } else {
                        result.winner().setLogin(challengee.getLogin());
                    }
                    sendMessage(result.winner().getLogin() + " is the winner!", challengee.getSocket());
                    sendMessage(result.winner().getLogin() + " is the winner!", challenger.getSocket());
                }
            });
        }
    }
}
