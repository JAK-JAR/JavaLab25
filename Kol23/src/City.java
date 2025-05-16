import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class City extends Polygon {
    private String name;
    public final Point center;
    private List<Point> points;
    public boolean port;
    public Set<Resource.Type> resources = new HashSet<>();

    public String getName() {
        return name;
    }

    public City(Point center, String name, double wallLength, boolean port) {
        List<Point> newPoints = new ArrayList<Point>();
            this.center = center;
            this.name = name;
            Point p1 = new Point(center.x() + wallLength / 2, center.y() + wallLength / 2);
            Point p2 = new Point(center.x() - wallLength / 2, center.y() - wallLength / 2);
            Point p3 = new Point(center.x() + wallLength / 2, center.y() - wallLength / 2);
            Point p4 = new Point(center.x() - wallLength / 2, center.y() + wallLength / 2);
            newPoints.add(p1);
            newPoints.add(p2);
            newPoints.add(p3);
            newPoints.add(p4);

            this.port = port;
            this.points = newPoints;
    }

    public void addResourcesInRange(List<Resource> resources, double range) {
        double length;
        for(Resource r : resources) {
            length = Math.sqrt(Math.pow(r.point.x()-this.center.x(), 2) + Math.pow(r.point.y()-this.center.y(), 2));
            if(length <= range) {
                if(r.getType() == Resource.Type.Fish) {
                    if(this.port) {
                        this.resources.add(r.getType());
                    } else throw new IllegalArgumentException("This city has no port!");
                } else
                    this.resources.add(r.getType());
            }
        }
    }
}
