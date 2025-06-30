package pl.umcs.oop.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.umcs.oop.auth.TokenService;
import pl.umcs.oop.image.DatabaseService;
import pl.umcs.oop.image.ImageService;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

@Component
public class AdminServer implements Runnable {
    private static final int PORT = 8081;
    private static final String PASSWORD = "admin123";

    @Autowired private TokenService tokenService;
    @Autowired private DatabaseService databaseService;
    @Autowired private ImageService imageService;

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            // Password verification
            if (!PASSWORD.equals(in.readLine())) {
                out.println("Invalid password");
                return;
            }

            out.println("Welcome admin");
            String command;
            while ((command = in.readLine()) != null) {
                if (command.startsWith("ban ")) {
                    handleBan(command.substring(4).trim(), out);
                } else if ("video".equals(command)) {
                    out.println("Video generation not implemented");
                } else {
                    out.println("Unknown command");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleBan(String token, PrintWriter out) {
        tokenService.banToken(token);
        int count = databaseService.deleteByToken(token);
        imageService.rebuildImage();
        out.println("Banned. Removed " + count + " pixels.");
    }
}