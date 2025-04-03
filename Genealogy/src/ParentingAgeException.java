public class ParentingAgeException extends Exception{
    private Person parent, child;
    public ParentingAgeException(Person parent, Person child) {
        super(parent+" is less 15yo or dead at birth od :"+child);
        this.parent = parent;
        this.child = child;
    }
    public Person getParent() {
        return parent;
    }
    public Person getChild() {
        return child;
    }
}
