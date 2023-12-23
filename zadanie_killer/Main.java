import consumer.ArrayBuilder;
import consumer.IndexFinder;
import consumer.MaxFinder;
import consumer.MinFinder;
import list.SortedDoublyLinkedList;
import list.Student;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        //enable assertions in VM first
        test1();
        test2();
        test3();
        test4();
        test5();
        test6();
        test7();
    }

    static void test1() {
        SortedDoublyLinkedList l = new SortedDoublyLinkedList();
        l.add(new Student("Michał", "Tomaszewski", 534));
        l.add(new Student("Sławomir", "Dańczak", 12764));
        l.add(new Student("Filip", "Kwiatkowski", 17137));

        assert l.toString().equals("(Sławomir Dańczak 12764)->(Filip Kwiatkowski 17137)->(Michał Tomaszewski 534)");
    }

    static void test2() {
        SortedDoublyLinkedList l = new SortedDoublyLinkedList();
        l.add(new Student("Michał", "Tomaszewski", 534));
        l.add(new Student("Sławomir", "Dańczak", 12764));
        l.add(new Student("Filip", "Kwiatkowski", 17137));
        l.add(new Student("Adam", "Dańczak", 1337)); // nowy element

        assert l.toString().equals("(Adam Dańczak 1337)->(Sławomir Dańczak 12764)->(Filip Kwiatkowski 17137)->(Michał Tomaszewski 534)");
    }

    static void test3() {
        SortedDoublyLinkedList l = new SortedDoublyLinkedList();
        l.add(new Student("Michał", "Tomaszewski", 534));
        l.add(new Student("Sławomir", "Dańczak", 12764));
        l.add(new Student("Filip", "Kwiatkowski", 17137));
        l.add(new Student("Adam", "Dańczak", 1337));
        assert l.toString().equals("(Adam Dańczak 1337)->(Sławomir Dańczak 12764)->(Filip Kwiatkowski 17137)->(Michał Tomaszewski 534)");
        l.reverse();
        assert l.toString().equals("(Michał Tomaszewski 534)->(Filip Kwiatkowski 17137)->(Sławomir Dańczak 12764)->(Adam Dańczak 1337)");
        l.add(new Student("Jan", "Zamoyski", 420));
        assert l.toString().equals("(Jan Zamoyski 420)->(Michał Tomaszewski 534)->(Filip Kwiatkowski 17137)->(Sławomir Dańczak 12764)->(Adam Dańczak 1337)");

    }

    static void test4() {
        SortedDoublyLinkedList l = new SortedDoublyLinkedList();
        l.add(new Student("Adam", "Dańczak", 1337));
        l.add(new Student("Sławomir", "Dańczak", 12764));
        l.add(new Student("Filip", "Kwiatkowski", 17137));
        l.add(new Student("Michał", "Tomaszewski", 534));
        l.removeAt(2);
        l.removeFirst();
        l.removeLast();

        assert l.toString().equals("(Sławomir Dańczak 12764)");
    }

    static void test5() {
        SortedDoublyLinkedList l = new SortedDoublyLinkedList();
        l.add(new Student("Filip", "Kwiatkowski", 17137));
        l.add(new Student("Michał", "Tomaszewski", 534));
        l.add(new Student("Sławomir", "Dańczak", 12764));
        ArrayBuilder builder = new ArrayBuilder();
        l.supplyTo(builder);

        assert Arrays.toString(builder.getStudents()).equals("[Sławomir Dańczak 12764, Filip Kwiatkowski 17137, Michał Tomaszewski 534]");
    }

    static void test6() {
        SortedDoublyLinkedList l = new SortedDoublyLinkedList();
        l.add(new Student("Filip", "Kwiatkowski", 11));
        l.add(new Student("Michał", "Tomaszewski", 11));
        l.add(new Student("Sławomir", "Dańczak", 999));
        IndexFinder finding11 = new IndexFinder(11);
        l.supplyTo(finding11);

        assert finding11.getTrackedStudent().toString().equals("Michał Tomaszewski 11");
    }

    static void test7() {
        SortedDoublyLinkedList l = new SortedDoublyLinkedList();
        l.add(new Student("Filip", "Kwiatkowski", 12));
        l.add(new Student("Michał", "Tomaszewski", 11));
        l.add(new Student("Sławomir", "Dańczak", 10));
        MinFinder minFinder = new MinFinder();
        MaxFinder maxFinder = new MaxFinder();
        l.supplyTo(minFinder);
        l.supplyTo(maxFinder);

        assert minFinder.getTrackedStudent().toString().equals("Sławomir Dańczak 10");
        assert maxFinder.getTrackedStudent().toString().equals("Filip Kwiatkowski 12");
    }
}
