package utils;

import models.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    public static List<Student> studentList = new ArrayList<>();
    public static List<Teacher> teacherList = new ArrayList<>();
    public static List<Subject> subjectList = new ArrayList<>();
    public static List<ClassSection> classSectionList = new ArrayList<>();

    // Phương thức để lưu dữ liệu vào file
    public static void saveData() {
        saveTeachers();
        saveStudents();
        saveSubjects();
        saveClassSections();
    }

    private static void saveTeachers() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("teachers.txt"))) {
            for (Teacher t : teacherList) {
                bw.write(t.getID() + "," + t.getName() + "," + t.getDepartment() + "," + t.getEmail());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveStudents() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("students.txt"))) {
            for (Student s : studentList) {
                bw.write(s.getID() + "," + s.getName() + "," + s.getAge() + "," + s.getEmail() + "," + s.getRemainingCredits());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveSubjects() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("subjects.txt"))) {
            for (Subject s : subjectList) {
                bw.write(s.getSubjectID() + "," + s.getTitle() + "," + s.getCredit());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveClassSections() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("classes.txt"))) {
            for (ClassSection cs : classSectionList) {
                bw.write(cs.getClassCode() + "," + cs.getSubject().getSubjectID() + "," + cs.getTeacher().getID() + "," + cs.getCredit());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Phương thức để tải dữ liệu từ file
    public static void loadData() {
        loadTeachers();      // Load teachers trước để có thể liên kết với classes
        loadSubjects();      // Load subjects trước khi load classes
        loadStudents();      // Load students
        loadClassSections(); // Load classes
    }

    private static void loadTeachers() {
        File file = new File("teachers.txt");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            teacherList.clear();
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 4) {
                    Teacher t = new Teacher(data[0], data[1], data[2], data[3]);
                    teacherList.add(t);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadStudents() {
        File file = new File("students.txt");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            studentList.clear();
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 4) {
                    // Đặt remainingCredits là 20
                    Student s = new Student(data[0], data[1], Integer.parseInt(data[2]), data[3], 20);
                    studentList.add(s);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void loadSubjects() {
        File file = new File("subjects.txt");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            subjectList.clear();
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 3) {
                    Subject s = new Subject(data[0], data[1], Integer.parseInt(data[2]));
                    subjectList.add(s);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadClassSections() {
        File file = new File("classes.txt");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            classSectionList.clear();
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 4) {
                    Subject subject = findSubjectByID(data[1]);
                    Teacher teacher = findTeacherByID(data[2]);
                    int credit = Integer.parseInt(data[3]);
                    if (subject != null && teacher != null) {
                        ClassSection cs = new ClassSection(data[0], subject, teacher, credit);
                        classSectionList.add(cs);
                        subject.addClassSection(cs);
                        teacher.addClass(cs);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Các phương thức hỗ trợ
    public static Subject findSubjectByID(String id) {
        for (Subject s : subjectList) {
            if (s.getSubjectID().equals(id)) {
                return s;
            }
        }
        return null;
    }

    public static Teacher findTeacherByID(String id) {
        for (Teacher t : teacherList) {
            if (t.getID().equals(id)) {
                return t;
            }
        }
        return null;
    }

    public static Student findStudentByID(String id) {
        for (Student s : studentList) {
            if (s.getID().equals(id)) {
                return s;
            }
        }
        return null;
    }

    public static ClassSection findClassSectionByCode(String code) {
        for (ClassSection cs : classSectionList) {
            if (cs.getClassCode().equals(code)) {
                return cs;
            }
        }
        return null;
    }
}
