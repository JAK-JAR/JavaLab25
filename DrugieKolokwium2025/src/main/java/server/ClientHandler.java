package server;

import Game.Gesture;
import Game.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Player implements Runnable {
    private final Server server;
    private final Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private String login;
    private String password;

    public void sendMessage(String message) {
        writer.println(message);
    }

    public String getLogin() {
        return login;
    }

    public ClientHandler(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            writer = new PrintWriter(socket.getOutputStream(), true);
            this.reader = reader;

            writer.println("Login: ");
            String providedLogin = reader.readLine();
            writer.println("Password: ");
            String providedPassword = reader.readLine();

            boolean ok = server.getDatabase().authenticate(providedLogin, providedPassword);
            if(!ok) {
                writer.println("Login or password is incorrect!");
                this.server.removeClient(this);
            }
            else {
                this.login = providedLogin;
                this.password = providedPassword;
                writer.println("Welcome, " + login + "!");
                String message;
                while((message = reader.readLine()) != null) {
                    if(this.isInDuel()) {
                        Gesture gesture = Gesture.fromString(message);
                        if(gesture != null) {
                            this.makeGesture(gesture);
                        }
                    }
                    this.server.challengeToDuel(this, message);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            server.removeClient(this);
        }
    }

    public Socket getSocket() {
        return socket;
    }
}
