package pl.umcs.oop.breakoutgame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Paddle extends GraphicsItem {
    public Paddle() {
        width = 0.2;
        height = 0.03;
        x = 0.4;
        y = 0.95;
    }

    public void moveTo(double mouseX) {
        x = (mouseX / canvasWidth) - width / 2;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.BLUE);
        gc.fillRect(getX(), getY(), getWidth(), getHeight());
    }
}
