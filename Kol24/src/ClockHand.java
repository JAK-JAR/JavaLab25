import java.time.LocalTime;

public abstract class ClockHand {
    LocalTime time;

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String toSvg(LocalTime time) {
        double minuteAnle = time.getMinute() * 6.0;
        double hourAngle = (time.getHour() % 12) * 30.0 + (time.getMinute() * 0.5);
        return String.format(
                "<g stroke=\"#5f4c6c\" stroke-linecap=\"round\">\n" +
                        "  <!-- wskazówka godzinowa -->\n" +
                        "  <line x1=\"0\" y1=\"0\" x2=\"0\" y2=\"-20\" stroke-width=\"8\"\n" +
                        "        transform=\"rotate(%.3f)\" />\n" +
                        "  <!-- wskazówka minutowa -->\n" +
                        "  <line x1=\"0\" y1=\"0\" x2=\"0\" y2=\"-35\" stroke-width=\"6\"\n" +
                        "        transform=\"rotate(%.3f)\" />\n" +
                        "</g> ",
                hourAngle,
                minuteAngle
        );
    }
}
