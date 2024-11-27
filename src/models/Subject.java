package models;

import java.util.ArrayList;
import java.util.List;

public class Subject {
    private String subjectID;
    private String title;
    private int credit;
    private List<ClassSection> classSections;

    public Subject(String subjectID, String title, int credit) {
        this.subjectID = subjectID;
        this.title = title;
        this.credit = credit;
        this.classSections = new ArrayList<>();
    }

    // Getter và Setter cho subjectID
    public String getSubjectID() {
        return subjectID;
    }

    public void setSubjectID(String subjectID) {
        this.subjectID = subjectID;
    }

    // Getter và Setter cho title
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Getter và Setter cho credit
    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    // Getter và Setter cho classSections
    public List<ClassSection> getClassSections() {
        return classSections;
    }

    public void setClassSections(List<ClassSection> classSections) {
        this.classSections = classSections;
    }

    public void addClassSection(ClassSection classSection) {
        this.classSections.add(classSection);
    }

    public void removeClassSection(ClassSection classSection) {
        this.classSections.remove(classSection);
    }

    @Override
    public String toString() {
        return title + " (" + subjectID + ")";
    }
}
