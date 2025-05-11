import java.util.List;

public class Util {
    public static <E> List<E> selectClass(List<E> list, Class<?> clazz){
        return list.stream().filter(clazz::isInstance).toList();
    }

    public static <T extends Comparable<T>> long countInRange(List<T> list, T min, T max){
        RangePredicate<T> predicate = new RangePredicate<T>(min, max);
        return list.stream().filter(predicate).count();
    }
}