import java.util.Scanner;

public class DigitalClock extends Clock {
    public int hours, minutes, seconds;
    public boolean isAM;
    public enum timeFormat {
        format24,
        format12
    }
    private timeFormat format;

    public DigitalClock(timeFormat format, int hours, int minutes, int seconds) {
        if(format == timeFormat.format24) {
            if(hours < 0 || hours > 23 ||
               minutes < 0 || minutes > 59 ||
               seconds < 0 || seconds > 59) {
                throw new IllegalArgumentException("Invalid values for time!");
            } else {
                this.hours = hours;
                this.minutes = minutes;
                this.seconds = seconds;
            }
        } else if(format == timeFormat.format12) {
            if(hours < 1 || hours > 12 ||
               minutes < 0 || minutes > 59 ||
               seconds < 0 || seconds > 59) {
                throw new IllegalArgumentException("Invalid values for time!");
            } else {
                System.out.println("Is it AM? (y/n)");
                Scanner scanner = new Scanner(System.in);
                if(scanner.next().equals("y")) {
                    isAM = true;
                } else {
                    isAM = false;
                }
                this.hours = hours;
                this.minutes = minutes;
                this.seconds = seconds;
            }
        }
            this.format = format;
    }


    @Override
    public String toString() {
        if(format == timeFormat.format24) {
            return super.toString();
        }
        else {
            if(isAM) {
                return String.format("%d:%02d:%02d AM", hours, minutes, seconds);
            }
            else {
                return String.format("%d:%02d:%02d PM", hours, minutes, seconds);
            }
        }
    }

    @Override
    public void toSvg(String filePath) {

    }
}
