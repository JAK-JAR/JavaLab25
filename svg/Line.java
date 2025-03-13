public class Line 
{
    private Point a;
    private Point b;

    public Line(Point a, Point b) {
        this.a = a;
        this.b = b;
    }

    public Point getA() {
        return a;
    }

    public Point getB() {
        return b;
    }

    public double length() {
        return Math.hypot(a.getX() - b.getX(), a.getY() - b.getY());
    }

    // Metoda zwracająca prostopadły odcinek o tej samej długości
    public Line perpendicular() {
        double dx = b.getX() - a.getX();
        double dy = b.getY() - a.getY();

        Point c = new Point(b.getX() - dy, b.getY() + dx);
        Point d = new Point(a.getX() - dy, a.getY() + dx);

        return new Line(c, d);
    }

    // Przeciążona metoda zwracająca prostopadły odcinek o zadanej długości 
    public Line perpendicular(double length) {
        double dx = b.getX() - a.getX();
        double dy = b.getY() - a.getY();
        double scale = length / Math.hypot(dx, dy);

        dx *= scale;
        dy *= scale;

        Point c = new Point(b.getX() - dy, b.getY() + dx);
        Point d = new Point(a.getX() - dy, a.getY() + dx);

        return new Line(c, d);
    }
    
}