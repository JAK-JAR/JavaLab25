
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
//import java.util.TreeSet;

public class Person implements Comparable<Person>, Serializable {
    private final String name, surname;
    private final LocalDate birth;
    private final LocalDate death;
    private final Set<Person> children; //= new TreeSet<>();

    public Person(String name, String surname, LocalDate birth, LocalDate death) throws NegativeLifespanException {
        this.name = name;
        this.surname = surname;
        this.birth = birth;
        this.death = death;
        this.children = new HashSet<>();
        if (death != null && birth.isAfter(death)){
            throw new NegativeLifespanException(this);
        }
    }

    public static Person fromCsvline(String csvLine) throws NegativeLifespanException {
        String[] elements = csvLine.split(",", -1);
        String[] fullName = elements[0].split(" ", 2);
        LocalDate birth = LocalDate.parse(elements[1], DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendValue(ChronoField.DAY_OF_MONTH, 2)
                .appendLiteral('.')
                .appendValue(ChronoField.MONTH_OF_YEAR, 2)
                .appendLiteral('.')
                .appendValue(ChronoField.YEAR, 4)
                .toFormatter();
        LocalDate death;
        try {
            death = LocalDate.parse(elements[2], formatter);
        } catch (DateTimeParseException e) {
            death = null;
        }

        return new Person(fullName[0], fullName[1], birth, death);
    }

    public static List<Person> fromCsv(String csvFileName) throws AmbiguousPersonException {
        Map<String, Person> family = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFileName))) {
            br.readLine();      // ignorujemy pierwszą linię (nagłówek)
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    Person readPerson = fromCsvline(line);
                    if (family.containsKey(readPerson.getFullName())){
                        throw new AmbiguousPersonException(readPerson.getFullName());
                    }
                    family.put(readPerson.getFullName(), readPerson);
                    // dodawanie dzieci
                    String[] elements = line.split(",", -1);
                    Person parentA = family.get(elements[3]);
                    Person parentB = family.get(elements[4]);
                    if(parentA != null) {
                        try {
                            parentA.adopt(readPerson);
                        } catch (ParentingAgeException e){
                            System.out.println(e.getMessage());
                            System.out.println("Are you sure you want to adopt? [Y/n(default)]");
                            Scanner sc = new Scanner(System.in);
                            if (sc.nextLine().equalsIgnoreCase("Y")) {
                                e.getParent().children.add(e.getChild());
                            }
                        }
                    }
                    if(parentB != null) {
                        try {
                            parentB.adopt(readPerson);
                        } catch (ParentingAgeException e){
                            System.out.println(e.getMessage());
                            System.out.println("Are you sure you want to adopt? [Y/n(default)]");
                            Scanner sc = new Scanner(System.in);
                            if (sc.nextLine().equalsIgnoreCase("Y")) {
                                e.getParent().children.add(e.getChild());
                            }
                        }

                    }
                } catch (NegativeLifespanException e) {
                    // po prostu ignorujemy linię i nie dodajemy nic do listy
                    // nie ma potrzeby przerywania wczytywania całego pliku
                    System.err.println(e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return family.values().stream().toList();
    }

    public static void toBinaryFile(List<Person> personList, String fileName) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))){
            out.writeObject(personList);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static List<Person> fromBinaryFile(String fileName) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))){
            Object o = in.readObject();
            return (List<Person>) o;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
        }
        return new ArrayList<>();
    }

    public boolean adopt(Person p) throws ParentingAgeException {
        if (this == p)
            return false;
        if (this.birth.until(p.birth).getYears() < 15 ||
                (this.death != null && this.death.isBefore(p.birth))) {
            throw new ParentingAgeException(this, p);
        }
        return children.add(p);
    }

    public Person getYoungestChild() {
        if(children.isEmpty())
            return null;

        return Collections.max(children);
//        Person youngest = null;
//        for (Person child : children) {
//            if (youngest == null || child.birth.isAfter(youngest.birth)) {
//                youngest = child;
//            }
//        }
//        return youngest;
    }

    public List<Person> getChildren() {
        return children.stream().sorted().toList();
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", birth=" + birth +
                ", death=" + death +
                '}';
    }

    @Override
    public int compareTo(Person o) {
        return this.birth.compareTo(o.birth);
    }

    public String getFullName() {
        return name + ' ' + surname;
    }

    public String getUMLObject() {
        return "object \"" + getFullName() + "\" {\n" +
                "birth="+birth+"\n" +
                (death==null ? "" : "death="+death+"\n") +
                "}\n";
    }

    public static String umlFromList(List<Person> personList,
                                     Function<String, String> postProcess,
                                     Predicate<Person> condition) {
        StringBuilder umlData = new StringBuilder();
        for (Person p : personList) {
            String umlPerson = p.getUMLObject();
            if (condition.test(p)){
                umlPerson = postProcess.apply(umlPerson);
            }
            umlData.append(umlPerson);
        }
        for (Person p : personList) {
            for (Person child : p.getChildren()){
                umlData.append('\"').append(child.getFullName()).append('\"')
                        .append(" --> ")
                        .append('\"').append(p.getFullName()).append('\"')
                        .append("\n");
            }
        }

        return umlData.toString();
    }

    // z4
    public static List<Person> selectSurnames(List<Person> from, String substring){
//        List<Person> result = new ArrayList<>();
//        for (Person p : from) {
//            if (p.surname.toLowerCase().contains(substring.toLowerCase())){
//                result.add(p);
//            }
//        }
//        return result;
        return from.stream().filter(
                p -> p.surname.toLowerCase().contains(substring.toLowerCase())
        ).collect(Collectors.toList());
    }
    // z5
    public static List<Person> sortedByBirth(List<Person> from) {
        return from.stream()
                .sorted()
                .collect(Collectors.toList());
    }
    // z6
    public static List<Person> selectDeceased(List<Person> from) {
        return from.stream()
                .filter(p -> p.death != null)
                .sorted(Comparator.comparingLong(p -> ChronoUnit.DAYS.between(p.death, p.birth)))
                .toList();
    }
    // z7
    public static Person getOldestAlive(List<Person> from) {
        return from.stream().filter(person -> person.death == null)
                .min(Comparator.comparing(a -> a.birth))
                .orElse(null);  // zwróci osobę a jeżeli nie ma to null

    }



}
