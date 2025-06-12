package pl.umcs.oop.breakoutgame;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Ball extends GraphicsItem {
    private Point2D moveVector;
    private double velocity;

    public Ball() {
        width = height = 0.03;
        x = 0.5 - width / 2;
        y = 0.5;
        moveVector = new Point2D(1, -1).normalize();
        velocity = 0.5;
    }

    public void setPosition(double cx, double yTop) {
        x = (cx / canvasWidth) - width / 2;
        y = yTop / canvasHeight - height;
    }

    public void updatePosition(double seconds) {
        x += moveVector.getX() * velocity * seconds;
        y += moveVector.getY() * velocity * seconds;
    }

    public void bounceHorizontally() {
        moveVector = new Point2D(-moveVector.getX(), moveVector.getY());
    }

    public void bounceVertically() {
        moveVector = new Point2D(moveVector.getX(), -moveVector.getY());
    }

    public void bounceFromPaddle(double offset) {
        double factor = offset / (width * canvasWidth);
        moveVector = new Point2D(factor, -1).normalize();
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.fillOval(x, y, width, height);

        gc.setFill(Color.RED);
        gc.fillText(String.format("(%.1f, %.1f)", x, y), x, y);
    }
    public double getTopY() {
        return y;
    }

    public double getBottomY() {
        return y + height;
    }
    public double getCenterX() {
        return x + width / 2;
    }

    public double getCenterY() {
        return y + height / 2;
    }

    public Point2D getTop() { return new Point2D(getCenterX(), getY()); }
    public Point2D getBottom() { return new Point2D(getCenterX(), getY() + getHeight()); }
    public Point2D getLeft() { return new Point2D(getX(), getY() + getHeight() / 2); }
    public Point2D getRight() { return new Point2D(getX() + getWidth(), getY() + getHeight() / 2); }
}
