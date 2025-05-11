public class MinuteHand {
    public LocalTime time;

    @Override
    public void setTime(LocalTime time) {
        this.time = time;
    }

    @Override
    public String toSvg() {
        double angle = time.getMinutes() * 6.0;
        return String.format(
                "<line x1=\"0\" y1=\"0\" x2=\"0\" y2=\"-%.1f\" " +
                        "stroke=\"black\" stroke-width=\"6.5\" stroke-linecap=\"round\" " +
                        "transform=\"rotate(%.3f)\" />"
                40, angle
        );
    }
}
