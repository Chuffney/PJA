package consumer;

import list.Student;

public class MaxFinder extends Finder {
    @Override
    public void accept(Student student) {
        if (matchingStudent == null || student.s() > matchingStudent.s())
            matchingStudent = student;
    }
}
