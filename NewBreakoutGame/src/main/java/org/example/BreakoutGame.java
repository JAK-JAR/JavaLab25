package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class BreakoutGame extends JPanel implements ActionListener, KeyListener {
    // Ustawienia gry
    private static final int SCREEN_WIDTH = 800;   // Szerokość okna gry
    private static final int SCREEN_HEIGHT = 600;  // Wysokość okna gry
    private static final int PLAYER_SIZE = 50;     // Rozmiar gracza (kwadrat)
    private static final int ENEMY_SIZE = 30;      // Rozmiar przeciwnika
    private static final int PLAYER_SPEED = 5;     // Prędkość poruszania gracza
    private static final int MAX_ENEMIES = 10;     // Maksymalna liczba przeciwników
    private static final int DELAY = 16;           // Opóźnienie timeru (~60 FPS)

    // Zmienne gry
    private Rectangle player;                      // Gracz (prostokąt)
    private ArrayList<Rectangle> enemies;          // Lista przeciwników
    private ArrayList<Point> enemyVelocities;      // Prędkości przeciwników
    private boolean[] keysPressed;                 // Śledzenie wciśniętych klawiszy
    private Random random;                         // Generator liczb losowych
    private Timer timer;                           // Timer aktualizacji gry
    private boolean gameOver;                      // Stan gry

    public BreakoutGame() {
        // Inicjalizacja zmiennych
        keysPressed = new boolean[4]; // 0: góra, 1: dół, 2: lewo, 3: prawo
        random = new Random();
        gameOver = false;

        // Utwórz gracza na środku ekranu
        player = new Rectangle(
                SCREEN_WIDTH / 2 - PLAYER_SIZE / 2,
                SCREEN_HEIGHT / 2 - PLAYER_SIZE / 2,
                PLAYER_SIZE,
                PLAYER_SIZE
        );

        // Inicjalizacja list przeciwników
        enemies = new ArrayList<>();
        enemyVelocities = new ArrayList<>();

        // Konfiguracja panelu
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        // Uruchom timer gry
        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Rysuj gracza (zielony kwadrat)
        g.setColor(Color.GREEN);
        g.fillRect(player.x, player.y, player.width, player.height);

        // Rysuj przeciwników (czerwone kwadraty)
        g.setColor(Color.RED);
        for (Rectangle enemy : enemies) {
            g.fillRect(enemy.x, enemy.y, enemy.width, enemy.height);
        }

        // Wyświetl komunikat końca gry
        if (gameOver) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            String text = "GAME OVER!";
            int textWidth = g.getFontMetrics().stringWidth(text);
            g.drawString(text, SCREEN_WIDTH / 2 - textWidth / 2, SCREEN_HEIGHT / 2);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            movePlayer();    // Aktualizuj pozycję gracza
            moveEnemies();   // Aktualizuj pozycje przeciwników
            spawnEnemies();  // Twórz nowych przeciwników
            checkCollisions(); // Sprawdzaj kolizje
        }
        repaint(); // Odśwież ekran
    }

    private void movePlayer() {
        // Poruszaj graczem na podstawie wciśniętych klawiszy
        if (keysPressed[0] && player.y > 0) { // Góra
            player.y -= PLAYER_SPEED;
        }
        if (keysPressed[1] && player.y < SCREEN_HEIGHT - player.height) { // Dół
            player.y += PLAYER_SPEED;
        }
        if (keysPressed[2] && player.x > 0) { // Lewo
            player.x -= PLAYER_SPEED;
        }
        if (keysPressed[3] && player.x < SCREEN_WIDTH - player.width) { // Prawo
            player.x += PLAYER_SPEED;
        }
    }

    private void spawnEnemies() {
        // Twórz nowych przeciwników jeśli jest ich mniej niż MAX_ENEMIES
        if (enemies.size() < MAX_ENEMIES && random.nextInt(100) < 5) {
            // Losowa pozycja na górze ekranu
            int x = random.nextInt(SCREEN_WIDTH - ENEMY_SIZE);
            enemies.add(new Rectangle(x, -ENEMY_SIZE, ENEMY_SIZE, ENEMY_SIZE));

            // Losowa prędkość (1-4 piksele/klatkę)
            int speedY = 1 + random.nextInt(4);
            enemyVelocities.add(new Point(0, speedY));
        }
    }

    private void moveEnemies() {
        // Poruszaj wszystkich przeciwników
        for (int i = 0; i < enemies.size(); i++) {
            Rectangle enemy = enemies.get(i);
            Point velocity = enemyVelocities.get(i);

            enemy.y += velocity.y;

            // Usuń przeciwników którzy zeszli z ekranu
            if (enemy.y > SCREEN_HEIGHT) {
                enemies.remove(i);
                enemyVelocities.remove(i);
                i--;
            }
        }
    }

    private void checkCollisions() {
        // Sprawdzaj kolizje gracza z przeciwnikami
        for (Rectangle enemy : enemies) {
            if (player.intersects(enemy)) {
                gameOver = true;
                timer.stop();
                break;
            }
        }
    }

    // Implementacja KeyListener
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_UP) keysPressed[0] = true;
        if (key == KeyEvent.VK_DOWN) keysPressed[1] = true;
        if (key == KeyEvent.VK_LEFT) keysPressed[2] = true;
        if (key == KeyEvent.VK_RIGHT) keysPressed[3] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_UP) keysPressed[0] = false;
        if (key == KeyEvent.VK_DOWN) keysPressed[1] = false;
        if (key == KeyEvent.VK_LEFT) keysPressed[2] = false;
        if (key == KeyEvent.VK_RIGHT) keysPressed[3] = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    // Uruchomienie gry
    public static void main(String[] args) {
        JFrame frame = new JFrame("Avoidance Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(new BreakoutGame());  
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}