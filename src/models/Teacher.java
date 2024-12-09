package models;

import java.util.ArrayList;
import java.util.List;

public class Teacher {
    private String ID;
    private String name;
    private String email;
    private List<ClassSection> teachingClasses;

    public Teacher(String ID, String name, String email) {
        this.ID = ID;
        this.name = name;
        this.email = email;
        this.teachingClasses = new ArrayList<>();
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

    public List<ClassSection> getTeachingClasses() {
        return teachingClasses;
    }

    public void addClass(ClassSection cs) {
        teachingClasses.add(cs);
    }

    public void removeClass(ClassSection cs) {
        teachingClasses.remove(cs);
    }

    @Override
    public String toString() {
        return name + " (" + ID + ")";
    }
}
