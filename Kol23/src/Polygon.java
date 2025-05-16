import java.util.ArrayList;
import java.util.List;

public class Polygon {
    private List<Point> points;
    boolean port;

    public Polygon(List<Point> points, boolean port) {
        List<Point> newPoints = new ArrayList<Point>();
        boolean isPort = port;
        this.points = newPoints;
        this.port = isPort;
    }

    public Polygon() {
    }

    public boolean inside(Point p) {
        int counter = 0;
        double a, b, d, x, x1, y1, x2, y2;

        for (int i = 0; i < this.points.size(); i++) {
            x1 = points.get(i).x();
            x2 = points.get((i + 1) % points.size()).x();
            y1 = points.get(i).y();
            y2 = points.get((i + 1) % points.size()).y();
            if (y1 > y2) {
                double tmpx = x1, tmpy = y1;
                x1 = x2;
                y1 = y2;
                x2 = tmpx;
                y2 = tmpy;
            }
            if (y1 <= p.y() && p.y() < y2) {
                d = x2 - x1;
                if (d == 0) {
                    x = x1;
                } else {
                    a = (y2 - y1) / d;
                    b = y1 - a * x1;
                    x = (p.y() - b) / a;
                    if (x < p.x()) {
                        counter++;
                    }
                }
            }
        }
        return (counter % 2) != 0;
    }
}
