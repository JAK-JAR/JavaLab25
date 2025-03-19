public class Text extends Shape {
    private String text;
    private Point position;
    private double textLength;
    private double fontSize;

    public Text(String text, Point position, double textLength, double fontSize, Style style) {
        super(style);
        this.text = text;
        this.position = position;
        this.textLength = textLength;
        this.fontSize = fontSize;
    }

    @Override
    public String toSvg() {
        return String.format(
            "<text x=\"%f\" y=\"%f\" textLength=\"%f\" font-size=\"%f\" style=\"%s\">%s</text>",
            position.getX(), position.getY(), textLength, fontSize, style, text
        );
    }

    @Override
    public BoundingBox boundingBox() {
        return new BoundingBox(position.getX(), position.getY(), textLength, fontSize);
    }

}
