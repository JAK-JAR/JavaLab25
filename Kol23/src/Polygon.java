import java.util.ArrayList;
import java.util.List;

public class Polygon {
    private List<Point> points;

    public Polygon(List<Point> points) {
        List<Point> newPoints = new ArrayList<Point>();
        for(Point p : points) {
            newPoints.add(p);
        }
        this.points = newPoints;
    }
}
