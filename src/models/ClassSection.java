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

    public ClassSection(String classCode, Subject subject, Teacher teacher, int credit) {
        this.classCode = classCode;
        this.subject = subject;
        this.teacher = teacher;
        this.credit = credit;
        this.enrolledStudents = new ArrayList<>();
        this.classSessions = new ArrayList<>();
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

    @Override
    public String toString() {
        return classCode + " - " + subject.getTitle();
    }
}
