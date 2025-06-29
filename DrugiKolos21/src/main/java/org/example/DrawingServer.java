package org.example;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Główna klasa serwera rysującego odcinki.
 * Łączy funkcjonalność serwera sieciowego z interfejsem graficznym JavaFX.
 */
public class DrawingServer extends Application {
    // Stałe konfiguracyjne
    private static final int PORT = 6000;
    private static final int CANVAS_SIZE = 500;

    // Elementy GUI
    private Canvas canvas; // płótno do rysowania
    private GraphicsContext gc; // kontekst graficzny do rysowania
    private Label statusLabel; // etykieta z informacją o przesunięciu

    // Stan aplikacji
    private double offsetX = 0; // przesunięcie w osi X
    private double offsetY = 0; // przesunięcie w osi Y

    // Dane graficzne
    private final List<LineSegment> allSegments = new ArrayList<>(); // lista narysowanych odcinków
    private final Map<Socket, Color> clientColors = new HashMap<>(); // mapowanie kolorow do klientów

    /**
     * Metoda startowa JavaFX - inicjalizacja interfejsu użytkownika.
     */
    @Override
    public void start(Stage stage) {
        // Inicjalizacja płótna
        canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
        gc = canvas.getGraphicsContext2D();

        // Wypełnienie płótna białym kolorem
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, CANVAS_SIZE, CANVAS_SIZE);

        // Inicjalizacja etykiety statusu
        statusLabel = new Label("Przesunięcie: (0, 0)");

        // Ustawienie układu interfejsu
        VBox root = new VBox(statusLabel, canvas);
        Scene scene = new Scene(root);

        // Rejestracja obsługi klawiszy
        scene.setOnKeyPressed(this::handleKeyPress);

        // Konfiguracja i pokazanie okna
        stage.setScene(scene);
        stage.setTitle("Rysowanie odcinków");
        stage.show();

        // Uruchamianie serwera sieciowego w osobnym wątku
        new Thread(this::startNetworkServer).start();
    }

    /**
     * Uruchamia serwer sieciowy nasłuchujący na zdefiniowanym porcie.
     */
    private void startNetworkServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Serwer sieciowy uruchomiony na porcie " + PORT);

            while(true) {
                // Akceptuj nowe połączenie klienta
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nowy klient: " + clientSocket);

                // Uruchom obsuługe klienta w nowym wątku
                new Thread(() -> handleClient(clientSocket)).start();
            }
        }  catch (IOException e) {
            System.out.println("Błąd serwera: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Obsługuje pojedyńczego klienta
    private void handleClient(Socket clientSocket) {
    try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
        Color currentColor = Color.BLACK; // aktualny kolor, domyślnie czarny
        clientColors.put(clientSocket, currentColor); // dodaj kolor do mapy

        String inputLine;
        // Czytaj linie od klienta
        while ((inputLine = in.readLine()) != null) {
            if (inputLine.startsWith("#")) {
                // Funkcja obsługująca zmianę koloru
                handleColorMessage(clientSocket, inputLine);
            } else {
                // Funkcja obsługująca zmianę położenia
                handleCoordinatesMessage(clientSocket, inputLine);
            }
        }
        
    } catch (IOException e) {
        System.out.println("Błąd podczas obsługi klienta: " + e.getMessage());
    } finally {
        // Sprzątanie po rozłączonym serwerze
        clientColors.remove(clientSocket);
        try {
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("Błąd przy zamykaniu giazda: " + e.getMessage());
        }
        System.out.println("Klient rozłączony: " + clientSocket);
        }
    }
    /**
     * Obsługuje wiadomość z kolorem.
     *
     * @param clientSocket Gniazdo klienta
     * @param colorHex Kod koloru w formacie heksadecymalnym (np. #FF0000)
     */
    private void handleColorMessage(Socket clientSocket, String colorHex) {
        try {
            Color color = Color.web(colorHex);
            clientColors.put(clientSocket, color);
            System.out.println("Klient " + clientSocket + " zmienił kolor na " + colorHex);
        } catch (IllegalArgumentException e) {
            System.out.println("Nieprawidłowy kod koloru: " + colorHex);
        }
    }

    /**
     * Obsługuje wiadomość z współrzędnymi odcinka.
     *
     * @param clientSocket Gniazdo klienta
     * @param coordinates Cztery liczby zmiennoprzecinkowe oddzielone spacjami
     */
    private void handleCoordinatesMessage(Socket clientSocket, String coordinates) {
        try {
            String[] coords = coordinates.split(" ");
            if(coords.length != 4) {
                System.out.println("Nieprawidłowa ilość współrzędnych: " + coordinates);
                return;
            }

            // Parsowanie współrzędnych
            double x1 = Double.parseDouble(coords[0]);
            double y1 = Double.parseDouble(coords[1]);
            double x2 = Double.parseDouble(coords[2]);
            double y2 = Double.parseDouble(coords[3]);

            // Poprany kolor dla klienta, domyślnie czarny
            Color color = clientColors.getOrDefault(clientSocket, Color.BLACK);

            // Utworzenie nowego docinka
            LineSegment segment = new LineSegment(x1, y1, x2, y2, color);
            allSegments.add(segment);

            // Rysowanie na płótnie w wątku
            Platform.runLater(() ->  {
                gc.setStroke(segment.color);
                gc.strokeLine(
                        segment.x1 + offsetX, segment.y1 + offsetY,
                        segment.x2 + offsetX, segment.y2 + offsetY
                );
            });
            System.out.println("Dodano odcinek: " + segment);

        } catch (NumberFormatException e) {
            System.err.println("Nieprawdiłowy format współrzędnych: " + coordinates);
        }
    }

    /**
     * Obsługuje naciśnięcie klawisza (strzałki).
     *
     * @param event Zdarzenie naciśnięcia klawisza
     */
    private void handleKeyPress(KeyEvent event) {
        switch (event.getCode()) {
            case UP:
                offsetY += 1;
                break;
            case DOWN:
                offsetY -= 1;
                break;
            case LEFT:
                offsetX += 1;
                break;
            case RIGHT:
                offsetX -= 1;
                break;
            default:
                return;
        }

        // Aktualizuj etykietę statusu
        statusLabel.setText(String.format("Przesunięcie: (%.0f, %.0f)", offsetX, offsetY));

        // Przerysuj płótno z nowym przesunięciem
        Platform.runLater(this::redrawCanvas);
    }

    // Przerysowuje płótno razem z przesunięciem
    private void redrawCanvas() {
        // Wypełnij białym tłem
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, CANVAS_SIZE, CANVAS_SIZE);

        // Rysowwanie odcinków z przesunięciem
        for (LineSegment segment : allSegments) {
            gc.setStroke(segment.color);
            gc.strokeLine(
                    segment.x1 + offsetX, segment.y1 + offsetY,
                    segment.x2 + offsetX, segment.y2 + offsetY
            );
        }
    }

    // Klasa reprezentująca odcinek
    private static class LineSegment {
        final double x1, y1, x2, y2;
        final Color color;

        LineSegment(double x1, double y1, double x2, double y2, Color color) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.color = color;
        }

        @Override
        public String toString() {
            return String.format("(%.1f,%.1f)-(%.1f,%.1f) %s", x1, y1, x2, y2, color);
        }
    }

    // Punkt wejścia aplikacji
    public static void main(String[] args) {
        launch(args);
    }
}