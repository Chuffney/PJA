package list;

public record Student(String name, String surname, int s) {
    @Override
    public String toString() {
        return name + " " + surname + " " + s;
    }
}
