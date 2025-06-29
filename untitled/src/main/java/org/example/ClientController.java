package org.example;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.ResourceBundle;

// Kontroler dla interfejsu, obsługuję logikę połączenia z serwerem, odbieranie słów, filtrowanie i wyświetlanie danych
public class ClientController implements Initializable {

    // Elementy interfejsu zdefiniowane w pliku fxml
    @FXML private Label wordCountLabel;
    @FXML private TextField filterField;
    @FXML private ListView<WordEntry> wordList;

    // Kolekcje danych
    private final ObservableList<WordEntry> allWords = FXCollections.observableArrayList();
    private FilteredList<WordEntry> filteredWords;
    private SortedList<WordEntry> sortedWords;

    // Formatowanie czasu
    private final DateTimeFormatter timeFormater = DateTimeFormatter.ofPattern("HH:mm:ss");

    // Inicjalizacja kolekcji z filtrowaniem i sortowaniem, konfiguruje kolekcje danych, listę słów i mechanizm filtrowania
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicjalizacja kolekcji z filtrowaniem
        filteredWords = new FilteredList<>(allWords);
        sortedWords = new SortedList<>(filteredWords);

        // Sortowanie alfabetyczne po słowach
        sortedWords.setComparator(Comparator.comparing(WordEntry::getWord));

        // Podłączenie posortowanej listy do widoku
        wordList.setItems(sortedWords);

        // Konfiguracja formatowania komorek listy
        wordList.setCellFactory(lv -> new ListCell<WordEntry>() {
            @Override
            protected void updateItem(WordEntry item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getFormattedString());
            }
        });

        // Reakcja na zmiany w polu filtrującym
        filterField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) {
                filteredWords.setPredicate(null); // Wyłączanie filtra
            } else {
                // Filtrowanie słów
                filteredWords.setPredicate(entry -> entry.getWord().startsWith(newVal));
            }
        });

        // Inicjalizacja połączenia z serwerem
        connectToServer();
    }

    // Łączenie z serwerem w osobnym wątku
    private void connectToServer() {
        new Thread(() -> {
            try (
                    // Połączenie z serwerem
                    Socket socket = new Socket("localhost", 5000);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))
                ) {
                String word;
                while ((word = reader.readLine()) != null) {
                    final String receivedWord = word;
                    // Aktualizacja UI w wątku
                    Platform.runLater(() -> {
                        LocalTime now = LocalTime.now();
                        WordEntry entry = new WordEntry(now, receivedWord);

                        // Dodanie słowa do głównej kolekcji
                        allWords.add(entry);

                        // Aktualizacja licznika słów
                        wordCountLabel.setText(String.valueOf(allWords.size()));
                    });
                }
            } catch (IOException e) {
                System.err.println("Błąd połączenia: " + e.getMessage());
            }
        }).start();
    }

    // Klasa reprezentująca pojedynczy wpis słowa, przechowuje treść oraz czas otrzymania
    public static class WordEntry {
        private final LocalTime time;
        private final String word;

        public WordEntry(LocalTime time, String word) {
            this.time = time;
            this.word = word;
        }

        public String getWord() {
            return word;
        }

        public String getFormattedString() {
            return time.format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " " + word;
        }
    }
}
