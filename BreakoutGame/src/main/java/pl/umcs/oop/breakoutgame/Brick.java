package pl.umcs.oop.breakoutgame;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Brick extends GraphicsItem {
    private static int gridRows, gridCols;
    private final Color color;

    public enum CrushType { NoCrush, HorizontalCrush, VerticalCrush }

    public static void setGrid(int rows, int cols) {
        gridRows = rows;
        gridCols = cols;
    }

    public Brick(int col, int row, Color color) {
        this.color = color;
        this.x = (double) col / gridCols;
        this.y = (double) row / gridRows;
        this.width = 1.0 / gridCols;
        this.height = 1.0 / gridRows;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(color);
        gc.fillRect(getX(), getY(), getWidth(), getHeight());
        gc.setStroke(Color.GRAY);
        gc.strokeRect(getX(), getY(), getWidth(), getHeight());
    }

    public CrushType checkCollision(Point2D top, Point2D bottom, Point2D left, Point2D right) {
        if (right.getX() < x ||
                left.getX() > x + width ||
                bottom.getY() < y ||
                top.getY() > y + height) {
            return CrushType.NoCrush;
        }

        if (left.getX() <= x || right.getX() >= x + width) {
            return CrushType.HorizontalCrush;
        }
        return CrushType.VerticalCrush;
    }

    private boolean contains(Point2D p) {
        return p.getX() >= getX() && p.getX() <= getX() + getWidth()
                && p.getY() >= getY() && p.getY() <= getY() + getHeight();
    }
}
