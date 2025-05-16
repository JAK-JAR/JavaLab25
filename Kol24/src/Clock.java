import java.time.LocalDate;
import java.time.LocalTime;

public abstract class Clock {
    private int hours;
    private int minutes;
    private int seconds;
    private City city;

    public void setTime(int hours, int minutes, int seconds, City city) throws IllegalArgumentException {
        if(hours < 0 || hours > 23 ||
           minutes < 0 || minutes > 59 ||
           seconds < 0 || seconds > 59) {
            throw new IllegalArgumentException("Invalid values for time!");
        } else {
            this.hours = hours;
            this.minutes = minutes;
            this.seconds = seconds;
            this.city = city;
        }
    }

    public void setCurrentTime(){
        LocalTime now = LocalTime.now();
        this.hours = now.getHour();
        this.minutes = now.getMinute();
        this.seconds = now.getSecond();
    }

    public String toString() {
        return String.format("$02d:%02d:%02d", hours, minutes, seconds);
    }

    public void setCity(City newCity) {
        int utc = (hours - city.getTimeZone() + 24) % 24;
        hours = (utc + newCity.getTimeZone() + 24) % 24;
        this.city = newCity;
    }


    public abstract void toSvg(String filePath) throws Exception;
}
