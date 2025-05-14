import java.util.ArrayList;
import java.util.List;

public class Land extends Polygon {
        private List<Point> points;

        public Polygon(List<Point> points) {
            List<Point> newPoints = new ArrayList<Point>();
            for(Point p : points) {
                newPoints.add(p);
            }
            this.points = newPoints;
        }

}
