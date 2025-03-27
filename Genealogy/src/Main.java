import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args)
    {
        List<Person> people = new ArrayList<>();
        people.add(new Person("Jakub", "Jarocki", LocalDate.of(2004, 4, 23)));
        people.add(new Person("Malgorzata", "Ferens", LocalDate.of(2005, 9, 10)));
    }
}