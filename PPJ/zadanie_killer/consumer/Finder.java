package consumer;

import list.Student;

public abstract class Finder extends Consumer {
    protected Student matchingStudent;

    public Student getTrackedStudent() {
        return matchingStudent;
    }
}
