import java.util.List;

public class AnalogClock extends Clock {
    private final List<ClockHand> hands;

    public AnalogClock(City city) {
        super(city);
        hands = List.of(
                new HourHand();
                new MinuteHand();
                new SecondHand();
        );
    }


    @Override
    public void toSvg (String filePath) {
        if(filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("Wrong file path!");
        }
        try {
            FileWriter fw = new FileWriter(filePath);
            fw.write(<svg width="200" height="200" viewBox="-100 -100 200 200">
                    <rect x="-100" y="-100" width="200" height="200" fill="#CD803D" />

                    <circle r="55" stroke="#FCCE7B" stroke-width="10" fill="white" />

                    <circle
            r="45"
            stroke="#B6705F"
            stroke-width="6"
            stroke-dasharray="6 17.56194490192345"
            stroke-dashoffset="3"
            fill="none"
                    /> + this.HourHand.toSvg() + this.MinuteHand.toSvg() + this.SecondHand.toSvg() + "<svg/>";
        }
    }
}
