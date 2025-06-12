package pl.umcs.oop.breakoutgame;

import javafx.scene.canvas.GraphicsContext;

public abstract class GraphicsItem {
    protected static double canvasWidth, canvasHeight;
    protected double x, y, width, height;

    public static void setCanvasSize(double w, double h) {
        canvasWidth = w;
        canvasHeight = h;
    }

    public double getX() { return x * canvasWidth; }
    public double getY() { return y * canvasHeight; }
    public double getWidth() { return width * canvasWidth; }
    public double getHeight() { return height * canvasHeight; }

    public double getCenterX() { return getX() + getWidth() / 2; }

    public abstract void draw(GraphicsContext gc);
}
