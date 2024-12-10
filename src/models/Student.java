package models;

import java.util.*;

public class Student implements PersonInterface {
    private String ID;
    private String name;
    private String email;
    private List<ClassSection> enrolledClasses;
    private int remainingCredits;
    private List<String> passedSubjects;
    private List<String> failedSubjects;

    // Lưu điểm cho mỗi classCode
    public static class GradeInfo {
        public double midterm = -1;
        public double finalExam = -1;
        public boolean passed = false;
    }

    private Map<String, GradeInfo> grades; // key = classCode

    public Student(String ID, String name, String email, int remainingCredits) {
        this.ID = ID;
        this.name = name;
        this.email = email;
        this.remainingCredits = remainingCredits;
        this.enrolledClasses = new ArrayList<>();
        this.passedSubjects = new ArrayList<>();
        this.failedSubjects = new ArrayList<>();
        this.grades = new HashMap<>();
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

    public List<ClassSection> getEnrolledClasses() {
        return enrolledClasses;
    }

    public void addClass(ClassSection cs) {
        enrolledClasses.add(cs);
    }

    public void removeClass(ClassSection cs) {
        enrolledClasses.remove(cs);
        grades.remove(cs.getClassCode());
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

    // Thiết lập điểm cho một lớp
    public void setGradeForClass(String classCode, double midterm, double finalExam, boolean passed) {
        GradeInfo info = grades.getOrDefault(classCode, new GradeInfo());
        info.midterm = midterm;
        info.finalExam = finalExam;
        info.passed = passed;
        grades.put(classCode, info);

        // Cập nhật passedSubjects và failedSubjects nếu đủ 15 buổi
        ClassSection cs = null;
        for (ClassSection c : getEnrolledClasses()) {
            if (c.getClassCode().equals(classCode)) {
                cs = c;
                break;
            }
        }
        if (cs != null && cs.getClassSessions().size() == 15) {
            // Đủ 15 buổi
            String subjectID = cs.getSubject().getSubjectID();
            if (passed) {
                if (!passedSubjects.contains(subjectID)) passedSubjects.add(subjectID);
                failedSubjects.remove(subjectID);
            } else {
                if (!failedSubjects.contains(subjectID)) failedSubjects.add(subjectID);
                passedSubjects.remove(subjectID);
            }
        }
    }

    public double getMidterm(String classCode) {
        GradeInfo info = grades.get(classCode);
        if (info == null) return -1;
        return info.midterm;
    }

    public double getFinal(String classCode) {
        GradeInfo info = grades.get(classCode);
        if (info == null) return -1;
        return info.finalExam;
    }

    public boolean isPassed(String classCode) {
        GradeInfo info = grades.get(classCode);
        if (info == null) return false;
        return info.passed;
    }

    public Map<String, GradeInfo> getGrades() {
        return grades;
    }

    @Override
    public String toString() {
        return name + " (" + ID + ")";
    }
}
