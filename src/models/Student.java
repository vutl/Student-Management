package models;

import java.util.ArrayList;
import java.util.List;

public class Student implements PersonInterface {
    private String studentID;
    private String name;
    private int age;
    private String email;
    private int remainingCredits;
    private List<ClassSection> enrolledClasses;

    public Student(String studentID, String name, int age, String email, int remainingCredits) {
        this.studentID = studentID;
        this.name = name;
        this.age = age;
        this.email = email;
        this.remainingCredits = remainingCredits;
        this.enrolledClasses = new ArrayList<>();
    }

    // Getter và Setter cho studentID
    @Override
    public String getID() {
        return studentID;
    }

    @Override
    public void setID(String studentID) {
        this.studentID = studentID;
    }

    // Getter và Setter cho name
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    // Getter và Setter cho email
    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    // Getter và Setter cho age
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    // Getter và Setter cho remainingCredits
    public int getRemainingCredits() {
        return remainingCredits;
    }

    public void setRemainingCredits(int remainingCredits) {
        this.remainingCredits = remainingCredits;
    }

    // Getter và Setter cho enrolledClasses
    public List<ClassSection> getEnrolledClasses() {
        return enrolledClasses;
    }

    public void setEnrolledClasses(List<ClassSection> enrolledClasses) {
        this.enrolledClasses = enrolledClasses;
    }

    public void addClass(ClassSection classSection) {
        this.enrolledClasses.add(classSection);
    }

    public void removeClass(ClassSection classSection) {
        this.enrolledClasses.remove(classSection);
    }

    @Override
    public String toString() {
        return name + " (" + studentID + ")";
    }
}
