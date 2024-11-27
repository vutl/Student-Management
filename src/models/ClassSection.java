package models;

import java.util.ArrayList;
import java.util.List;

public class ClassSection {
    private String classCode;
    private Subject subject;
    private Teacher teacher;
    private int credit;
    private List<Student> enrolledStudents;

    public ClassSection(String classCode, Subject subject, Teacher teacher, int credit) {
        this.classCode = classCode;
        this.subject = subject;
        this.teacher = teacher;
        this.credit = credit;
        this.enrolledStudents = new ArrayList<>();
    }

    // Getter và Setter cho classCode
    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    // Getter và Setter cho subject
    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    // Getter và Setter cho teacher
    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    // Getter và Setter cho credit
    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    // Getter và Setter cho enrolledStudents
    public List<Student> getEnrolledStudents() {
        return enrolledStudents;
    }

    public void setEnrolledStudents(List<Student> enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
    }

    public void addStudent(Student student) {
        this.enrolledStudents.add(student);
    }

    public void removeStudent(Student student) {
        this.enrolledStudents.remove(student);
    }

    @Override
    public String toString() {
        return classCode + " - " + subject.getTitle() + " - " + teacher.getName();
    }
}
