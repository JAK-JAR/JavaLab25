package pl.umcs.oop.drugiekolokwium2023;

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
import javax.imageio.ImageIO;

public class Server {
    private static int radius = 1; // Domyślny promień

    public static void main(String[] args) {
        initializeDatabase();
        createImagesDirectory();

        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("Server started on port 5000");

            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                    handleClient(clientSocket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try {
            // 1. Odbierz plik od klienta
            String fileName = saveReceivedFile(clientSocket);

            // 2. Zapisz plik z timestampem
            BufferedImage image = ImageIO.read(new File(fileName));
            if (image == null) {
                System.err.println("Invalid PNG file received");
                return;
            }

            // 3. Pobierz rozmiar filtra z interfejsu użytkownika
            showRadiusDialog();

            // 4. Przetwórz obraz algorytmem box blur
            long startTime = System.currentTimeMillis();
            BufferedImage blurredImage = blurImage(image, radius);
            long delay = System.currentTimeMillis() - startTime;

            // 5. Zapisz do bazy danych
            saveToDatabase(fileName, radius, delay);

            // 6. Wyślij przetworzony obraz z powrotem do klienta
            sendImage(blurredImage, clientSocket);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 1. Odbieranie pliku od klienta
    private static String saveReceivedFile(Socket socket) throws IOException {
        DataInputStream input = new DataInputStream(socket.getInputStream());

        // Generuj unikalną nazwę pliku
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "images/" + timestamp + ".png";

        // Odbierz długość pliku
        long fileSize = input.readLong();

        // Zapisz plik
        try (FileOutputStream output = new FileOutputStream(fileName)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            long totalRead = 0;

            while (totalRead < fileSize) {
                bytesRead = input.read(buffer, 0, (int) Math.min(buffer.length, fileSize - totalRead));
                if (bytesRead == -1) break;
                output.write(buffer, 0, bytesRead);
                totalRead += bytesRead;
            }
        }
        System.out.println("File saved: " + fileName);
        return fileName;
    }

    // 2. Utwórz katalog images jeśli nie istnieje
    private static void createImagesDirectory() {
        File dir = new File("images");
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    // 3. Interfejs użytkownika do ustawienia promienia
    private static void showRadiusDialog() {
        JFrame frame = new JFrame("Blur Radius Settings");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 150);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

        JLabel label = new JLabel("Current radius: 1");
        JSlider slider = new JSlider(1, 15, 1);
        slider.setMajorTickSpacing(2);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);

        // Ustaw tylko nieparzyste wartości
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int value = slider.getValue();
                if (value % 2 == 0) {
                    slider.setValue(value + 1);
                } else {
                    radius = value;
                    label.setText("Current radius: " + radius);
                }
            }
        });

        frame.add(label);
        frame.add(slider);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Czekaj na zamknięcie okna
        while (frame.isVisible()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // 4. Algorytm box blur zrównoleglony
    private static BufferedImage blurImage(BufferedImage original, int radius) {
        int width = original.getWidth();
        int height = original.getHeight();
        BufferedImage blurred = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int processors = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(processors);

        int segmentHeight = height / processors;

        for (int i = 0; i < processors; i++) {
            final int startY = i * segmentHeight;
            final int endY = (i == processors - 1) ? height : startY + segmentHeight;

            executor.submit(() -> {
                for (int y = startY; y < endY; y++) {
                    for (int x = 0; x < width; x++) {
                        int[] pixel = applyBoxBlur(original, x, y, radius, width, height);
                        blurred.setRGB(x, y, (pixel[0] << 16) | (pixel[1] << 8) | pixel[2]);
                    }
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return blurred;
    }

    private static int[] applyBoxBlur(BufferedImage image, int x, int y, int radius, int width, int height) {
        int size = 2 * radius + 1;
        int totalPixels = 0;
        int r = 0, g = 0, b = 0;

        for (int dy = -radius; dy <= radius; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                int nx = Math.min(Math.max(x + dx, 0), width - 1);
                int ny = Math.min(Math.max(y + dy, 0), height - 1);

                int rgb = image.getRGB(nx, ny);
                r += (rgb >> 16) & 0xFF;
                g += (rgb >> 8) & 0xFF;
                b += rgb & 0xFF;
                totalPixels++;
            }
        }

        return new int[]{
                r / totalPixels,
                g / totalPixels,
                b / totalPixels
        };
    }

    // 5. Inicjalizacja bazy danych
    private static void initializeDatabase() {
        String dbPath = "images/index.db";

        if (!Files.exists(Paths.get(dbPath))) {
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath)) {
                String sql = "CREATE TABLE transformations ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "path TEXT NOT NULL,"
                        + "size INTEGER NOT NULL,"
                        + "delay INTEGER NOT NULL)";
                conn.createStatement().execute(sql);
                System.out.println("Database created");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 5. Zapisz do bazy danych
    private static void saveToDatabase(String filePath, int size, long delay) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:images/index.db")) {
            String sql = "INSERT INTO transformations (path, size, delay) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, filePath);
                pstmt.setInt(2, size);
                pstmt.setLong(3, delay);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 6. Wyślij przetworzony obraz do klienta
    private static void sendImage(BufferedImage image, Socket socket) throws IOException {
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] imageData = baos.toByteArray();

        output.writeLong(imageData.length);
        output.write(imageData);
        output.flush();
        System.out.println("Processed image sent to client");
    }
}