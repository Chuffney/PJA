package list;

import consumer.Consumer;

import java.util.Iterator;

public class SortedDoublyLinkedList implements Iterable<Student> {
    private Node head = null;
    private Node tail = null;
    private int size = 0;
    /**
     * Whether the Nodes are ordered ascending (1) or descending (-1)
     */
    private int direction = 1;

    public void add(Student student) {
        size++;
        Node newNode = new Node(student);
        if (head == null) {
            head = newNode;
            tail = newNode;
            return;
        }

        insertNode(newNode);
    }

    public void reverse() {
        reverseHelper(head);

        Node originalHead = head;
        head = tail;
        tail = originalHead;

        direction = -direction;
    }

    private void reverseHelper(Node iterator) {
        mirrorNode(iterator);

        if (iterator.previous == null)
            return;

        reverseHelper(iterator.previous);
    }

    private static void mirrorNode(Node node) {
        Node tmp = node.next;
        node.next = node.previous;
        node.previous = tmp;
    }

    private void insertNode(Node newNode) {
        if (newNode.compareTo(head) * direction <= 0) { //special case if newNode is to become a new head...
            head.previous = newNode;
            newNode.next = head;
            head = newNode;
            return;
        }
        if (newNode.compareTo(tail) * direction >= 0) { //...or a new tail
            tail.next = newNode;
            newNode.previous = tail;
            tail = newNode;
            return;
        }

        Node iterator = head;
        while (iterator.next != null && newNode.compareTo(iterator.next) * direction > 0)   //find such node that newNode goes after it
            iterator = iterator.next;

        newNode.next = iterator.next;
        iterator.next = newNode;
        newNode.previous = iterator;
        if (newNode.next != null)
            newNode.next.previous = newNode;
    }

    public void removeAt(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds for list of size " + size);

        if (index == 0) {
            removeFirst();
            return;
        }
        if (index == size - 1) {
            removeLast();
            return;
        }
        if (removeCommon()) return;

        Node iterator = head;
        for (int i = 0; i < index; i++)
            iterator = iterator.next;

        iterator.previous.next = iterator.next;
        iterator.next.previous = iterator.previous;
    }

    public void removeFirst() {
        if (removeCommon()) return;

        head = head.next;
        head.previous = null;
    }

    public void removeLast() {
        if (removeCommon()) return;

        tail = tail.previous;
        tail.next = null;
    }

    /**
     * Checks for edge cases common in every .remove...() function
     * @return <b>true</b> if there is nothing more to be done and the calling function can return
     */
    private boolean removeCommon() {
        if (size == 0)
            throw new RemovalFromEmptyListException();

        size--;
        if (size == 0) {
            head = null;
            tail = null;
            return true;
        }
        return false;
    }

    public int size() {
        return size;
    }

    public void supplyTo(Consumer studentConsumer) {
        for (Student student : this) {
            studentConsumer.accept(student);
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        boolean first = true;
        for (Student student : this) {
            if (!first)
                stringBuilder.append("->");
            stringBuilder.append('(').append(student).append(')');
            first = false;
        }
        return stringBuilder.toString();
    }

    @Override
    public Iterator<Student> iterator() {
        return new Iterator<>() {
            private Node iterator = head;

            @Override
            public boolean hasNext() {
                return iterator != null;
            }

            @Override
            public Student next() {
                Student returnedStudent = iterator.student;
                iterator = iterator.next;
                return returnedStudent;
            }
        };
    }

    static class Node implements Comparable<Node> {
        Student student;
        Node next = null;
        Node previous = null;

        private Node(Student student) {
            this.student = student;
        }

        @Override
        public int compareTo(Node other) {
            Student otherStudent = other.student;
            if (student.surname().compareTo(otherStudent.surname()) != 0)
                return student.surname().compareTo(otherStudent.surname());

            else if (student.name().compareTo(otherStudent.name()) != 0)
                return student.name().compareTo(otherStudent.name());

            else return student.s() - otherStudent.s();
        }

        @Override
        public String toString() {
            return student.toString();
        }
    }
}
