import java.util.Locale;

public class Style 
{
    public String fillColor;
    public String strokeColor;
    public double strokeWidth;

    public Style(String fillColor, String strokeColor, double strokeWidth) 
    {
        this.fillColor = fillColor;
        this.strokeColor = strokeColor;
        this.strokeWidth = strokeWidth;
    }

    public String toSvg()
    {
        return String.format(Locale.ENGLISH, "style=\"fill:%s;stroke:%s;stroke-width:%f\"", fillColor, strokeColor, strokeWidth);
    }

}
