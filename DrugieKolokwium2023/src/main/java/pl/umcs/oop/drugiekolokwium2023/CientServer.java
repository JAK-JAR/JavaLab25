package org.example;

// Client.java
package pl.umcs.oop.drugiekolokwium2023;

import java.io.*;
import java.net.Socket;

/**
 * Aplikacja kliencka odpowiadająca za wysyłanie pliku PNG do serwera
 * oraz odbieranie od niego przetworzonej wersji.
 */
public class Client {

    /**
     * Nawiązuje połączenie z serwerem pod wskazanym adresem i portem.
     *
     * @param address Adres serwera, np. "localhost" lub IP.
     * @param port    Numer portu, na którym nasłuchuje serwer, np. 5000.
     * @return Otwarte gniazdo Socket.
     * @throws RuntimeException Jeśli nie uda się nawiązać połączenia.
     */
    public static Socket connectToServer(String address, int port) {
        try {
            return new Socket(address, port);
        } catch (IOException e) {
            throw new RuntimeException("Nie udało się połączyć z serwerem: " + address + ":" + port, e);
        }
    }

    /**
     * Wysyła plik z lokalnej ścieżki do serwera.
     * Najpierw przesyłany jest długość pliku (long), a następnie strumień bajtów.
     *
     * @param path   Ścieżka do pliku PNG do wysłania.
     * @param socket Aktywne połączenie do serwera.
     * @throws RuntimeException Jeśli wystąpi błąd podczas odczytu lub zapisu.
     */
    public static void send(String path, Socket socket) {
        try (FileInputStream input = new FileInputStream(path);
             DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {

            File file = new File(path);
            long fileSize = file.length();

            // Wysyłamy długość pliku, aby serwer wiedział, ile bajtów oczekiwać
            output.writeLong(fileSize);

            byte[] buffer = new byte[8192];
            int count;
            // Wysyłamy zawartość pliku kawałek po kawałku
            while ((count = input.read(buffer)) != -1) {
                output.write(buffer, 0, count);
            }
            output.flush();

            System.out.println("[Client] Plik wysłany, rozmiar: " + fileSize + " bajtów.");
        } catch (IOException e) {
            throw new RuntimeException("Błąd podczas wysyłania pliku: " + path, e);
        }
    }

    /**
     * Odbiera przetworzony plik od serwera i zapisuje go na dysku.
     * Najpierw odczytywana jest długość nadchodzącego pliku, a następnie strumień.
     *
     * @param socket Połączenie z serwerem.
     * @param path   Ścieżka, pod którą zapisać odebrany plik.
     * @throws RuntimeException Jeśli wystąpi błąd podczas odczytu lub zapisu.
     */
    public static void receive(Socket socket, String path) {
        try (DataInputStream input = new DataInputStream(socket.getInputStream());
             FileOutputStream output = new FileOutputStream(path)) {

            long fileSize = input.readLong();
            long received = 0;
            byte[] buffer = new byte[8192];
            int count;

            // Odbieramy dane do momentu, aż otrzymamy oczekiwaną liczbę bajtów
            while (received < fileSize && (count = input.read(buffer)) != -1) {
                output.write(buffer, 0, count);
                received += count;
            }

            System.out.println("[Client] Plik odebrany, zapisano: " + path);
        } catch (IOException e) {
            throw new RuntimeException("Błąd podczas odbierania pliku: " + path, e);
        }
    }

    /**
     * Przykładowe wywołanie klienta:
     * 1. Połącz się z serwerem.
     * 2. Wyślij plik input.png.
     * 3. Odbierz wynik jako output.png.
     */
    public static void main(String[] args) {
        Socket socket = connectToServer("localhost", 5000);
        try {
            send("input.png", socket);
            receive(socket, "output.png");
        } finally {
            try {
                socket.close();
                System.out.println("[Client] Połączenie zamknięte.");
            } catch (IOException ignored) {
            }
        }
    }
}

// Server.java
package pl.umcs.oop.drugiekolokwium2023;

import javax.imageio.ImageIO;
import javax.swing.*;
        import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.image.BufferedImage;
import java.io.*;
        import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
        import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Aplikacja serwerowa obsługująca kolejno połączenia klientów.
 * Dla każdego pliku PNG:
 * 1. Odbiera plik od klienta.
 * 2. Zapisuje z timestampem w katalogu images.
 * 3. Pozwala wybrać promień filtra (1-15, tylko nieparzyste).
 * 4. Przetwarza obraz algorytmem box blur równolegle.
 * 5. Zapisuje dane operacji do bazy SQLite index.db.
 * 6. Odsyła przetworzony plik do klienta.
 */
public class Server {
    // Promień filtra ustawiany przez GUI (domyślnie 1)
    private static int radius = 1;

    public static void main(String[] args) {
        initializeDatabase();       // Tworzy index.db z tabelą, jeśli nie istnieje
        createImagesDirectory();    // Tworzy katalog images

        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("[Server] Uruchomiony na porcie 5000");

            // Obsługuje kolejnych klientów w pętli
            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    System.out.println("[Server] Klient połączony: " + clientSocket.getInetAddress());
                    handleClient(clientSocket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cała logika obsługi pojedynczego klienta.
     */
    private static void handleClient(Socket clientSocket) throws IOException {
        // 1. Odbierz i zapisz plik
        String receivedPath = saveReceivedFile(clientSocket);

        // 2. Wczytaj obraz z dysku
        BufferedImage original = ImageIO.read(new File(receivedPath));
        if (original == null) {
            System.err.println("[Server] Otrzymany plik nie jest prawidłowym PNG");
            return; // Kończymy obsługę klienta
        }

        // 3. Pokaż slider do wyboru promienia filtra
        showRadiusDialog();

        // 4. Przetwarzanie obrazu box blur
        long start = System.currentTimeMillis();
        BufferedImage blurred = blurImage(original, radius);
        long delay = System.currentTimeMillis() - start;
        System.out.println("[Server] Czas przetwarzania: " + delay + " ms");

        // 5. Zapis do bazy danych
        saveToDatabase(receivedPath, radius, delay);

        // 6. Wyślij wynik klientowi
        sendImage(blurred, clientSocket);
    }

    /**
     * Odbiera plik od klienta i zapisuje go w images/timestamp.png.
     *
     * @return Pełna ścieżka zapisanego pliku.
     */
    private static String saveReceivedFile(Socket socket) throws IOException {
        DataInputStream in = new DataInputStream(socket.getInputStream());
        long fileSize = in.readLong();

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String path = "images/" + timestamp + ".png";

        try (FileOutputStream out = new FileOutputStream(path)) {
            byte[] buffer = new byte[8192];
            long total = 0;
            int count;
            while (total < fileSize && (count = in.read(buffer)) != -1) {
                out.write(buffer, 0, count);
                total += count;
            }
        }
        System.out.println("[Server] Plik zapisany: " + path);
        return path;
    }

    /**
     * Tworzy katalog images, jeśli nie istnieje.
     */
    private static void createImagesDirectory() {
        File dir = new File("images");
        if (!dir.exists()) {
            dir.mkdir();
            System.out.println("[Server] Utworzono katalog images");
        }
    }

    /**
     * Wyświetla modalny dialog Swing do wyboru promienia filtra od 1 do 15 (tylko nieparzyste).
     */
    private static void showRadiusDialog() {
        JSlider slider = new JSlider(1, 15, radius);
        slider.setMajorTickSpacing(2);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);

        JLabel label = new JLabel("Promień filtra: " + radius);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int v = slider.getValue();
                if (v % 2 == 0) v++; // wymuszamy nieparzyste
                slider.setValue(v);
                radius = v;
                label.setText("Promień filtra: " + radius);
            }
        });

        // Modalny dialog, blokuje wywołujący wątek
        JOptionPane.showMessageDialog(null, new Object[]{label, slider},
                "Ustaw promień filtra", JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Implementacja box blur z równoległym dzieleniem obrazu na segmenty.
     */
    private static BufferedImage blurImage(BufferedImage src, int radius) {
        int width = src.getWidth();
        int height = src.getHeight();
        BufferedImage dst = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int cores = Runtime.getRuntime().availableProcessors();
        ExecutorService pool = Executors.newFixedThreadPool(cores);
        int segment = height / cores;

        for (int i = 0; i < cores; i++) {
            final int y0 = i * segment;
            final int y1 = (i == cores - 1) ? height : y0 + segment;
            pool.submit(() -> {
                for (int y = y0; y < y1; y++) {
                    for (int x = 0; x < width; x++) {
                        int[] rgb = applyBoxBlur(src, x, y, radius, width, height);
                        int pixel = (rgb[0] << 16) | (rgb[1] << 8) | rgb[2];
                        dst.setRGB(x, y, pixel);
                    }
                }
            });
        }
        pool.shutdown();
        try {
            pool.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException ignored) {}

        return dst;
    }

    /**
     * Oblicza średnią wartość koloru (R,G,B) wokół piksela (x,y).
     */
    private static int[] applyBoxBlur(BufferedImage img, int x, int y,
                                      int radius, int w, int h) {
        int size = 2 * radius + 1;
        long r = 0, g = 0, b = 0;
        int count = 0;
        for (int dy = -radius; dy <= radius; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                int nx = Math.min(w - 1, Math.max(0, x + dx));
                int ny = Math.min(h - 1, Math.max(0, y + dy));
                int pixel = img.getRGB(nx, ny);
                r += (pixel >> 16) & 0xFF;
                g += (pixel >> 8) & 0xFF;
                b += pixel & 0xFF;
                count++;
            }
        }
        return new int[]{(int)(r/count), (int)(g/count), (int)(b/count)};
    }

    /**
     * Inicjalizuje bazę SQLite w images/index.db, jeśli nie istnieje.
     */
    private static void initializeDatabase() {
        String db = "images/index.db";
        try {
            if (!Files.exists(Paths.get(db))) {
                try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + db)) {
                    String sql = "CREATE TABLE transformations ("
                            + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + "path TEXT NOT NULL,"
                            + "size INTEGER NOT NULL,"
                            + "delay INTEGER NOT NULL)";
                    conn.createStatement().execute(sql);
                    System.out.println("[Server] Baza danych utworzona: " + db);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Zapisuje rekord o przetworzeniu do tabeli transformations.
     */
    private static void saveToDatabase(String path, int size, long delay) {
        String dbUrl = "jdbc:sqlite:images/index.db";
        String sql = "INSERT INTO transformations(path, size, delay) VALUES(?,?,?)";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, path);
            ps.setInt(2, size);
            ps.setLong(3, delay);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Wysyła przetworzony obraz do klienta (length + dane).
     */
    private static void sendImage(BufferedImage img, Socket socket) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", baos);
        byte[] data = baos.toByteArray();

        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.writeLong(data.length);
        out.write(data);
        out.flush();

        System.out.println("[Server] Wysłano przetworzony obraz, rozmiar: " + data.length + " bajtów.");
    }
}
