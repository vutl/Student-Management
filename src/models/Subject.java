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

    public String getSubjectID() {
        return subjectID;
    }

    public String getTitle() {
        return title;
    }

    public int getCredit() {
        return credit;
    }

    public List<ClassSection> getClassSections() {
        return classSections;
    }

    public void addClassSection(ClassSection cs) {
        classSections.add(cs);
    }

    public void removeClassSection(ClassSection cs) {
        classSections.remove(cs);
    }

    // Thêm các setter ở đây
    public void setSubjectID(String subjectID) {
        this.subjectID = subjectID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    @Override
    public String toString() {
        return title + " (" + credit + " tín chỉ)";
    }
}

