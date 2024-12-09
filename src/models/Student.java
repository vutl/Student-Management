package models;

import java.util.ArrayList;
import java.util.List;

public class Student {
    private String ID;
    private String name;
    private String email;
    private List<ClassSection> enrolledClasses;
    private int remainingCredits;
    private List<String> passedSubjects;
    private List<String> failedSubjects;

    public Student(String ID, String name, String email, int remainingCredits) {
        this.ID = ID;
        this.name = name;
        this.email = email;
        this.remainingCredits = remainingCredits;
        this.enrolledClasses = new ArrayList<>();
        this.passedSubjects = new ArrayList<>();
        this.failedSubjects = new ArrayList<>();
    }

    // Getters và Setters
    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public List<ClassSection> getEnrolledClasses() {
        return enrolledClasses;
    }

    public void addClass(ClassSection cs) {
        enrolledClasses.add(cs);
    }

    public void removeClass(ClassSection cs) {
        enrolledClasses.remove(cs);
    }

    public int getRemainingCredits() {
        return remainingCredits;
    }

    public void setRemainingCredits(int remainingCredits) {
        this.remainingCredits = remainingCredits;
    }

    public List<String> getPassedSubjects() {
        return passedSubjects;
    }

    public List<String> getFailedSubjects() {
        return failedSubjects;
    }

    @Override
    public String toString() {
        return name + " (" + ID + ")";
    }
}
