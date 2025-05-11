import java.util.Collection;
import java.util.Comparator;

public class CollectionComparator<T extends Collection<? extends Number>> implements Comparator<T> {

    @Override
    public int compare(T o1, T o2) {
        double sum1 = o1.stream().mapToDouble(Number::doubleValue).sum();
        double sum2 = o2.stream().mapToDouble(Number::doubleValue).sum();
        System.out.println(sum1+ " vs "+sum2);
        return Double.compare(sum1, sum2);
    }
}
