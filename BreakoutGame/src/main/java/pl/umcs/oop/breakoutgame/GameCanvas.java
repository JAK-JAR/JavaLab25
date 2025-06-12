package pl.umcs.oop.breakoutgame;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameCanvas extends Canvas {
    private final GraphicsContext gc;
    private final Paddle paddle;
    private final Ball ball;
    private final List<Brick> bricks;
    private boolean gameStarted = false;
    private long lastTime;

    public GameCanvas(double width, double height) {
        super(width, height);
        gc = getGraphicsContext2D();
        GraphicsItem.setCanvasSize(width, height);
        this.paddle = new Paddle();
        this.ball = new Ball();
        this.bricks = new ArrayList<>();

        setOnMouseMoved((MouseEvent e) -> paddle.moveTo(e.getX()));
        setOnMouseClicked(e -> {
            if (!gameStarted) {
                gameStarted = true;
                lastTime = System.nanoTime();
            }
        });

        loadLevel();
    }

    public void startGame() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double delta = (now - lastTime) / 1e9;
                lastTime = now;
                drawFrame(delta);
            }
        };
        timer.start();
    }

    private void drawFrame(double delta) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, getWidth(), getHeight());

        if (gameStarted) {
            ball.updatePosition(delta);

            if (shouldBallBounceHorizontally()) ball.bounceHorizontally();
            if (shouldBallBounceVertically()) ball.bounceVertically();
            if (shouldBallBounceFromPaddle()) {
                double relative = ball.getCenterX() - paddle.getCenterX();
                ball.bounceFromPaddle(relative);
            }

            Iterator<Brick> iter = bricks.iterator();
            while (iter.hasNext()) {
                Brick b = iter.next();
                Brick.CrushType crush = b.checkCollision(ball.getTop(), ball.getBottom(), ball.getLeft(), ball.getRight());
                if (crush != Brick.CrushType.NoCrush) {
                    if (crush == Brick.CrushType.HorizontalCrush) ball.bounceHorizontally();
                    else ball.bounceVertically();
                    iter.remove();
                    break;
                }
            }
        } else {
            ball.setPosition(paddle.getCenterX(), paddle.getY() - ball.getHeight());
        }

        paddle.draw(gc);
        ball.draw(gc);
        for (Brick b : bricks) b.draw(gc);
    }

    private boolean shouldBallBounceHorizontally() {
        return ball.getX() <= 0 || (ball.getX() + ball.getWidth()) >= getWidth();
    }

    private boolean shouldBallBounceVertically() {
        return ball.getY() <= 0;
    }

    private boolean shouldBallBounceFromPaddle() {
        // Użyj getY() na punkcie
        return ball.getBottom().getY() >= paddle.getY() &&
                ball.getX() + ball.getWidth() / 2 >= paddle.getX() &&
                ball.getX() + ball.getWidth() / 2 <= paddle.getX() + paddle.getWidth();
    }

    private void loadLevel() {
        Brick.setGrid(20, 10);
        for (int row = 2; row <= 7; row++) {
            Color color = Color.hsb((row - 2) * 60, 1.0, 1.0);
            for (int col = 0; col < 10; col++) {
                bricks.add(new Brick(col, row, color));
            }
        }
    }
}
