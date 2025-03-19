public class zad1 {
    
    public static void main(String[] args) 
    {
        OldCode();
        polygons();
        testText();
        testElipse();
    }
    
    public static void OldCode()
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

    public static void polygons()
    {
        Point[] points = new Point[3];
        points[0] = new Point(10,50);
        points[1] = new Point(100,200);
        points[2] = new Point(150,350);

        Polygon polygon = new Polygon(points);
        System.out.println(polygon);
    }

    public static void testText() {
        Point position = new Point(50, 50);
        Style style = new Style("fill:blue", "stroke:black", 1.0);
        Text text = new Text("Hello, SVG!", position, 100, 20, style);

        System.out.println(text.toSvg());
        System.out.println(text.boundingBox());
    }

    public static void testElipse() {
        Point middle = new Point(50, 50);
        Style style = new Style("fill:blue", "stroke:black", 1.0);
        Elipse elipse = new Elipse(middle, 40, 20, style);

        System.out.println(elipse.toSvg());
        System.out.println(elipse.boundingBox());
    }
}
