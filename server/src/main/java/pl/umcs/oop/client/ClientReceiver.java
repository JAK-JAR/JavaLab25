package pl.umcs.oop.client;

import javafx.application.Platform;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientReceiver extends Thread {
    private final TextArea chatArea;
    private final ListView<String> userList;
    private final Socket socket;

    public ClientReceiver(TextArea chatArea, ListView<String> userList, Socket socket) {
        this.chatArea = chatArea;
        this.userList = userList;
        this.socket = socket;
    }

    private void handleMessage(String message) {
        if (message.startsWith("LOGIN")) {
            String[] parts = message.split(" ");
            Platform.runLater(() -> userList.getItems().add(parts[1]));
        } else {
            Platform.runLater(() -> chatArea.appendText(message+"\n"));
        }
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()
                    )
            );

            String message;
            while ((message = in.readLine()) != null) {
                handleMessage(message);
            }

        } catch (IOException e) {
            chatArea.appendText("Błąd połączenia: "+e.getMessage());
        }
    }

}