package consumer;

import list.Student;

public class MinFinder extends Finder {
    @Override
    public void accept(Student student) {
        if (matchingStudent == null || student.s() < matchingStudent.s())
            matchingStudent = student;
    }
}
