public class zad1 {
    public static void main(String[] args)
    {
        Point p = new Point();
        p.x = 5.0;
        p.y = 7.5;

        System.out.println(p);
        System.out.println(p.toSVG());

        Point p2 = p.translated(2.0, 3.5);
        System.out.println(p);
        System.out.println(p2);

        Segment segment = new Segment();
        segment.a=p;
        segment.b=p2;
        System.out.println(segment.lenght());
    }
}
