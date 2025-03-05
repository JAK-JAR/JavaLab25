public class Segment {
    public Point a;
    public Point b;

    public Segment(Point a, Point b)
    {
        this.a = a;
        this.b = b;
    }
    
    public double lenght()
    {
        return Math.hypot(a.x-b.x, a.y-b.y);
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
