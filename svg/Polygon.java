import java.util.Arrays;
import java.util.Locale;

public class Polygon 
{
    private Point[] points;
    
    public Polygon(Point[] points)
    {
        this.points = points;
    }

    public String toString()
    {
        return "Polygon{" + "points=" + Arrays.toString(points) + '}';
    }

    public String toSVG()
    {
        String pointsString = "";
        for(Point p : points)
        {
            pointsString += p.getX() + "," + p.getY() + " ";
        }
        return String.format(Locale.ENGLISH, "<polygon points=\"%s\" style=\"fill:lime;stroke:purple;stroke-width:3\" />", pointsString);
    }
    
}
