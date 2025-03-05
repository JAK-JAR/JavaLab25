
import java.util.Locale;

public class Point {
    public double x;
    public double y;

    public Point(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    
    public String toString()
    {
        return "X = " + x + ", Y = " + y;
    }

    public String toSVG()
    {
        return String.format(Locale.ENGLISH,"<circle r=\"5\" cx=\"%f\" cy=\"%f\" fill=\"red\" />", x, y);
    }

    public void translate(double dx, double dy)
    {
        x += dx;
        y += dy;
    }

    public Point translated(double dx, double dy)
    {
        Point p= new Point(x, y);
        p.x = x + dx;
        p.y = y + dy;
        return p;
    }

}

