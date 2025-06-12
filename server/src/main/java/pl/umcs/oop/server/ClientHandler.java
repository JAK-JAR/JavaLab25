package pl.umcs.oop.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final List<ClientHandler> clients;
    private PrintWriter out;
    private BufferedReader in;
    private String login;

    public ClientHandler(Socket socket, List<ClientHandler> clients) {
        this.socket = socket;
        this.clients = clients;
    }

    private void broadcast(String message) {
        for (ClientHandler c : clients) {
            c.sendMessage(message);
        }
    }

    private void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    private ClientHandler getClientByLogin(String login) {
        for (ClientHandler c : clients) {
            if (c.login != null && c.login.equals(login)) {
                return c;
            }
        }
        return null;
    }
    private void listOnlineUsers() {
        StringBuilder sb = new StringBuilder("Online: ");
        for (ClientHandler c : clients) {
            if (c.login != null) {
                sb.append(c.login).append(" ");
            }
        }
        sendMessage(sb.toString());
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            sendMessage("Witaj! Podaj swój login:");
            this.login = in.readLine();

            if (login == null || login.isBlank()) {
                sendMessage("Login nie może być pusty.");
                socket.close();
                return;
            }

            broadcast(login + " dołączył do czatu.");

            String message;
            while ((message = in.readLine()) != null) {
                if (message.equals("/online")) {
                    listOnlineUsers();
                } else if (message.startsWith("/w ")) {
                    String[] parts = message.split(" ", 3);
                    if (parts.length < 3) {
                        sendMessage("Użycie: /w login wiadomość");
                    } else {
                        String recipient = parts[1];
                        String privateMsg = parts[2];
                        ClientHandler receiver = getClientByLogin(recipient);
                        if (receiver != null) {
                            receiver.sendMessage("(priv) " + login + ": " + privateMsg);
                        } else {
                            sendMessage("Użytkownik " + recipient + " nie jest zalogowany.");
                        }
                    }
                } else {
                    broadcast(login + ": " + message);
                }
            }
        } catch (IOException e) {
            System.err.println("Błąd komunikacji z " + login + ": " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {}
            clients.remove(this);
            if (login != null) {
                broadcast(login + " opuścił czat.");
            }
        }
    }

}