import java.time.LocalTime;

public abstract class ClockHand {
    public LocalTime time;

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public abstract String toSvg();

}
