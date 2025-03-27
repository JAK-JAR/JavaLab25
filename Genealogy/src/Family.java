import java.util.*;

public class Family
{
    private final Map<String, List<Person>> family;

    public Family()
    {
        family = new HashMap<>();
    }

    public void add(Person... people)
    {
        for(Person p : people)
        {
            if (!family.containsKey(p.getFullName()))
            {
                family.put(p.getFullName(), new ArrayList<>());
            }
            family.get(p.getFullName()).add(p);
        }
    }

    public List<Person> get(String key)
    {
        return family.getOrDefault(key, new ArrayList<>()).stream().sorted().toList();
    }
}
