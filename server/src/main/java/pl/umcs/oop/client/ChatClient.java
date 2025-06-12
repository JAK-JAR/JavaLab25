package pl.umcs.oop.client;

import java.io.*;
import java.net.Socket;

public class ChatClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in));
            Thread receiver = new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    System.err.println("Rozłączono z serwerem.");
                }
                System.exit(0);
            });
            receiver.setDaemon(true);
            receiver.start();

            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line); // np. "Podaj login:"
                String input = consoleIn.readLine();
                out.println(input);
                break;
            }

            String input;
            while ((input = consoleIn.readLine()) != null) {
                if (input.equalsIgnoreCase("exit")) {
                    socket.close();
                    break;
                }
                out.println(input);
            }

        } catch (IOException e) {
            System.err.println("Błąd klienta: " + e.getMessage());
        }
    }
}
