import java.time.LocalTime;

public class HourHand extends ClockHand {
    public LocalTime time;

    @Override
    public void setTime(LocalTime time) {
        this.time = time;
    }

    @Override
    public String toSvg() {
        double angle = (time.getHour() % 12) * 30.0 + (time.getMinute() * 0.5);
        return String.format(
                "<line x1=\"0\" y1=\"0\" x2=\"0\" y2=\"-%.1f\" " +
                        "stroke=\"black\" stroke-width=\"8\" stroke-linecap=\"round\" " +
                        "transform=\"rotate(%.3f)\" />",
                40.0, angle
        );
    }
}
