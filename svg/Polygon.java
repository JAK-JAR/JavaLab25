import java.util.Arrays;
import java.util.Locale;

public class Polygon extends Shape
{
    private Point[] points;
    private Style style;
    
    // public Polygon(Point[] points)
    // {
    //     this.points = points;
    // }

    // public Polygon(Point[] points)
    // {
    //     this.points = new Point[points.length];
    //     for(int i = 0; i < points.length; i++)
    //     {
    //         this.points[i] = points[i];
    //     }
    // }

    public Polygon(Point[] points, Style style)
    {
        super(style);
        this.points = new Point[points.length];
        for (int i = 0; i < points.length; i++) {
            this.points[i] = new Point(points[i]);
        }
    }

    public Polygon(Point[] points)
    {
        this(points, new Style("none", "black", 1.0));
    }

    @Override
    public String toString()
    {
        return "Polygon{" + "points=" + Arrays.toString(points) + '}';
    }

    @Override
    public String toSvg()
    {
        StringBuilder pointsString = new StringBuilder();
        for(Point p : points)
        {
            pointsString.append(p.getX()).append(",").append(p.getY()).append(" ");
        }
        return String.format(Locale.ENGLISH, "<polygon points=\"%s\" style=\"fill:lime;stroke:purple;stroke-width:3\" />", pointsString.toString().trim(), style.toSvg());
    }

    public BoundingBox boundingBox()
    {
        double xMin = this.points[0].getX();
        double xMax = this.points[0].getX();
        double yMin = this.points[0].getY();
        double yMax = this.points[0].getY();

        for(int i = 1; i<points.length; i++)
        {
            xMin = Math.min(xMin, points[i].getX());
            xMax = Math.max(xMax, points[i].getX());
            yMin = Math.min(yMin, points[i].getY());
            yMax = Math.max(yMax, points[i].getY());
        }
        return new BoundingBox(xMin, yMin, xMax - xMin, yMax - yMin);
    }
    
    public static Polygon square(Line line, Style style) 
    {
        Point a = line.getA();
        Point b = line.getB();

        double dx = b.getX() - a.getX();
        double dy = b.getY() - a.getY();

        Point c = new Point(b.getX() - dy, b.getY() + dx);
        Point d = new Point(a.getX() - dy, a.getY() + dx);

        Point[] points = { a, b, c, d, a };
        return new Polygon(points, style);
    }
}
