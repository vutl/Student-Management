package models;

import java.util.ArrayList;
import java.util.List;

public class Teacher implements PersonInterface {
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

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public void setID(String id) {
        this.ID = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
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
