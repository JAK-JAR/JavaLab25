public class Segment {
    private Point a;
    private Point b;

    public Segment(Point a, Point b)
    {
        this.a = new Point(a);
        this.b = new Point(b);
    }
    
    public double lenght()
    {
        return Math.hypot(a.getX()-b.getX(), a.getY()-b.getY());
    }

    public static Segment FindLongestSegment(Segment[] segments)
    {
        Segment Longest = segments[0];
        
        for(Segment s : segments)
        {
            if(s.lenght() > Longest.lenght())
            {
                Longest = s;
            }
        }
        return Longest;
    }
    
}
