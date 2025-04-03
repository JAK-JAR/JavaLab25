import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class Person implements Comparable<Person>, Serializable
{

    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private final Set<Person> children;
    private final LocalDate deathDate;

    public Person(String firstName, String lastName, LocalDate birthDate, LocalDate deathDate) throws NegativeLifespanException
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.children = new HashSet<>();
        this.deathDate = deathDate;
        if(deathDate != null && birthDate.isAfter(deathDate))
        {
            throw new NegativeLifespanException(this);
        }
    }

    public static Person fromCsvLine(String csvLine) throws NegativeLifespanException
    {
        String[] elements = csvLine.split(",", 5);
        String[] fullName = elements[0].split(" ", 2);
        LocalDate birthDate = LocalDate.parse(elements[1], DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        LocalDate deathDate;
        try
        {
            deathDate = LocalDate.parse(elements[2], DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException e)
        {
            deathDate = null;
        }
        return new Person(fullName[0], fullName[1], birthDate, deathDate);
    }

    public static List<Person> fromCsv(String csvLine)
    {
        Map<String, Person> people = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvLine))){
            br.readLine();
            String line;
            while ((line = br.readLine()) != null){
                try {
                    Person readPerson = fromCsvLine(line);
                    if(people.containsKey(readPerson.getFullName())) {
                        throw new AmbiguousPersonException(readPerson.getFullName());
                    }
                    people.put(readPerson.getFullName(), readPerson);

                    String[] elements = line.split(",", -1);
                    Person parentA = people.get(elements[3]);
                    Person parentB = people.get(elements[4]);
                    if(parentA != null) {
                        try {
                            parentA.adopt(readPerson);
                        } catch (ParentingAgeException e) {
                            System.out.println(e.getMessage());
                            System.out.println("Are you sure you want to adopt? [Y/N]");
                            Scanner scanner = new Scanner(System.in);
                            if(scanner.nextLine().equals("Y")) {
                                e.getParent().children.add(e.getChild());
                            }
                        }
                    }
                    if(parentB != null) {
                        try {
                            parentB.adopt(readPerson);
                        } catch (ParentingAgeException e)
                        {
                            System.out.println(e.getMessage());
                            System.out.println("Are you sure you want to adopt? [Y/N]");
                            Scanner scanner = new Scanner(System.in);
                            if(scanner.nextLine().equals("Y")) {
                                e.getParent().children.add(e.getChild());
                            }
                        }
                    }

                } catch (NegativeLifespanException e)
                {
                    System.err.println(e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return people.values().stream().toList();
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + firstName + '\'' +
                ", surname='" + lastName + '\'' +
                ", birth=" + birthDate +
                ", death=" + deathDate +
                ", children=" + children +
                '}';
    }

    public static void toBinaryFile(List<Person> personList, String fileName){
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(personList);
        } catch (IOException e){
            System.err.println(e.getMessage());
        }
    }

    public static List<Person> fromBinaryFile(String fileName){
        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))){
            Object o = in.readObject();
            return (List<Person>) o;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
        }
        return new ArrayList<>();
    }

    public boolean adopt(Person child) throws ParentingAgeException {
        if (this == child)
            return false;

        if (this.birthDate.until(child.birthDate).getYears() < 15 || (this.deathDate != null && this.deathDate.isAfter(child.birthDate)))
        {
            throw new ParentingAgeException(this, child);
        }

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

    public LocalDate getDeathDate() {
        return deathDate;
    }
}
