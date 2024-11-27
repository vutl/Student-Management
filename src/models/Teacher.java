package models;

import java.util.ArrayList;
import java.util.List;

public class Teacher implements PersonInterface {
    private String teacherID;
    private String name;
    private String department;
    private String email;
    private List<ClassSection> teachingClasses;

    public Teacher(String teacherID, String name, String department, String email) {
        this.teacherID = teacherID;
        this.name = name;
        this.department = department;
        this.email = email;
        this.teachingClasses = new ArrayList<>();
    }

    // Getter và Setter cho teacherID
    @Override
    public String getID() {
        return teacherID;
    }

    @Override
    public void setID(String teacherID) {
        this.teacherID = teacherID;
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

    // Getter và Setter cho department
    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    // Getter và Setter cho teachingClasses
    public List<ClassSection> getTeachingClasses() {
        return teachingClasses;
    }

    public void setTeachingClasses(List<ClassSection> teachingClasses) {
        this.teachingClasses = teachingClasses;
    }

    public void addClass(ClassSection classSection) {
        this.teachingClasses.add(classSection);
    }

    public void removeClass(ClassSection classSection) {
        this.teachingClasses.remove(classSection);
    }

    @Override
    public String toString() {
        return name + " (" + teacherID + ")";
    }
}
