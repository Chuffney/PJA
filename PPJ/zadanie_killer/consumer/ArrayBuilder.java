package consumer;

import list.Student;

import java.util.Arrays;

public class ArrayBuilder extends Consumer {
    private static final int DEFAULT_CAPACITY = 16;

    private Student[] array = new Student[DEFAULT_CAPACITY];
    private int lastIndex = 0;

    public Student[] getStudents() {
        return Arrays.copyOf(array, lastIndex);
    }

    @Override
    public void accept(Student student) {
        if (lastIndex == array.length)
            array = Arrays.copyOf(array, array.length * 2);

        array[lastIndex] = student;
        lastIndex++;
    }
}
