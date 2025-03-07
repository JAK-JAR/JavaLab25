import java.util.Arrays;
import java.util.Locale;

public class Polygon 
{
    private Point[] points;
    
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

    public Polygon(Point[] points)
    {
        this.points = new Point[points.length];
        for(int i = 0; i < points.length; i++)
        {
            this.points[i] = new Point(points[i]);
        }
    }

    public String toString()
    {
        return "Polygon{" + "points=" + Arrays.toString(points) + '}';
    }

    public String toSvg()
    {
        String pointsString = "";
        for(Point p : points)
        {
            pointsString += p.getX() + "," + p.getY() + " ";
        }
        return String.format(Locale.ENGLISH, "<polygon points=\"%s\" style=\"fill:lime;stroke:purple;stroke-width:3\" />", pointsString);
    }

    public BoundingBox()
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
        return new BoundingBox(xMin, yMin, xMax-xMin, yMax-yMin);
    }

}
