public class Segment {
    public Point a;
    public Point b;
    
    public double lenght()
    {
        return Math.sqrt(Math.hypot(a.x-b.x, a.y-b.y));
    }

}
