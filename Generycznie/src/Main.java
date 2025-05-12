public class Main {
    public static void main(String[] args) {
        CustomList<Integer> list = new CustomList<>();
        list.addFirst(1);
        list.addLast(2);
        System.out.println(list.get(1));
    }
}