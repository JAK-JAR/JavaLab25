import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Person implements Comparable<Person>
{
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private final Set<Person> children;

    public Person(String firstName, String lastName, LocalDate birthDate)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.children = new HashSet<>();
    }
    @Override
    public String toString() {
        return "Person{" +
                "name='" + firstName + '\'' +
                ", surname='" + lastName + '\'' +
                ", birth=" + birthDate +
                ", children=" + children +
                '}';
    }

    public boolean adopt(Person child)
    {
        if (this == child)
            return false;

        return children.add(child);
    }

    public Person getYoungestChild(Person adult)
    {
        if(children.isEmpty())
            return null;

        return Collections.max(children);
    }

    public List<Person> getChildren()
    {
        return children.stream().sorted().toList();
    }

    @Override
    public int compareTo(Person other)
    {
        return this.birthDate.compareTo(other.birthDate);
    }

    public String getFullName()
    {
        return firstName + ' ' + lastName;
    }
}
