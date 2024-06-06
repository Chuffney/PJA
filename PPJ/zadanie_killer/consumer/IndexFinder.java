package consumer;

import list.Student;

public class IndexFinder extends Finder {
    private final int targetIndex;

    public IndexFinder(int index) {
        this.targetIndex = index;
    }

    public Student getTrackedStudent() {
        return matchingStudent;
    }

    @Override
    public void accept(Student student) {
        if (student.s() == targetIndex) {
            matchingStudent = student;
        }
    }
}
