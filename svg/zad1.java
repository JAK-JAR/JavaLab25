public class zad1 {
    public static void main(String[] args)
    {
        Point p = new Point(5,7);

        System.out.println(p);
        System.out.println(p.toSVG());

        Point p2 = p.translated(2.0, 3.5);
        System.out.println(p);
        System.out.println(p2);

        Segment segment = new Segment(p,p2);
        System.out.println(segment.lenght());

        Segment[] segments = new Segment[3];

        segments[0] = new Segment(new Point(1,1), new Point(5,4));
        segments[1] = new Segment(new Point(3,4), new Point(4,6));
        segments[2] = new Segment(new Point(6,8), new Point(8,11));

        int number = 0;
        for(Segment s : segments)
        {
            System.out.println("Lenght of " + number + " segment is: " + s.lenght());
            number++;
        }

        Segment LongestSegment = Segment.FindLongestSegment(segments);
        System.out.println("Longest segment lenght: " + LongestSegment.lenght());

    }
}
