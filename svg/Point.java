import java.util.Locale;

public class Point {
    private double x;
    private double y;

    public Point(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public Point()
    {
       
    }

    public Point(Point oldPoint)
    {
        this.x = oldPoint.x;
        this.y = oldPoint.y;
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

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public void setX(double x)
    {
        this.x = x;
    }

    public void setY(double y)
    {
        this.y = y;
    }
    
}

