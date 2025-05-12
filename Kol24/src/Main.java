//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        DigitalClock newClock = new DigitalClock(DigitalClock.timeFormat.format24, 11, 34, 0);
        System.out.println(newClock.toString());


    }
}