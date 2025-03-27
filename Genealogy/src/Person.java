import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Person
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
        return firstName + " " + lastName + " (" + birthDate + ")";
    }

    public boolean adopt(Person child)
    {
        if (this == child)
        {
            return false;
        }
        return children.add(child);
    }

    public Person getYoungestChild(Person adult)
    {
        if(children.isEmpty())
            return null;

        Person youngest = null;
        for(Person c : children)
        {
            if(youngest == null || c.birthDate.isAfter(youngest.birthDate))
            {
                youngest = c;
            }
        }
        return youngest;
    }




}
