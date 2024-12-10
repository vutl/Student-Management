package models;

import java.util.ArrayList;
import java.util.List;

public class ClassSection {
    private String classCode;
    private Subject subject;
    private Teacher teacher;
    private int credit;
    private List<Student> enrolledStudents;
    private List<ClassSession> classSessions;
    private boolean finished; // Thêm thuộc tính này

    public ClassSection(String classCode, Subject subject, Teacher teacher, int credit) {
        this.classCode = classCode;
        this.subject = subject;
        this.teacher = teacher;
        this.credit = credit;
        this.enrolledStudents = new ArrayList<>();
        this.classSessions = new ArrayList<>();
        this.finished = false; // Mặc định là chưa kết thúc
    }

    public String getClassCode() {
        return classCode;
    }

    public Subject getSubject() {
        return subject;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public int getCredit() {
        return credit;
    }

    public List<Student> getEnrolledStudents() {
        return enrolledStudents;
    }

    public void addStudent(Student s) {
        enrolledStudents.add(s);
    }

    public void removeStudent(Student s) {
        enrolledStudents.remove(s);
    }

    public List<ClassSession> getClassSessions() {
        return classSessions;
    }

    public void addClassSession(ClassSession session) {
        classSessions.add(session);
    }

    // Thêm getter setter cho finished
    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    @Override
    public String toString() {
        return classCode + " - " + subject.getTitle();
    }
}
