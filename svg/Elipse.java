public class Elipse extends Shape {
    private Point middle;
    private double rx;
    private double ry;
    private String style;

    public Elipse(Point middle, double rx, double ry, Style style) {
        super(style);
        this.middle = middle;
        this.rx = rx;
        this.ry = ry;
    }

    @Override
    public String toSvg() {
        return String.format(
            "<ellipse cx=\"%f\" cy=\"%f\" rx=\"%f\" ry=\"%f\" style=\"%s\" />",
            middle.getX(), middle.getY(), rx, ry, style
        );
    }

    @Override
    public BoundingBox boundingBox() 
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'boundingBox'");
    }
}