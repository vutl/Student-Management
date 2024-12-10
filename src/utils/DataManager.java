package utils;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import models.*;

public class DataManager {
    public static List<Teacher> teacherList = new ArrayList<>();
    public static List<Student> studentList = new ArrayList<>();
    public static List<Subject> subjectList = new ArrayList<>();
    public static List<ClassSection> classSectionList = new ArrayList<>();
    public static String currentLoggedInID = null; // Biến lưu trữ ID hiện tại

    private static final DateTimeFormatter SESSION_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Phương thức loadData() để tải dữ liệu từ file
    public static void loadData() {
        teacherList.clear();
        studentList.clear();
        subjectList.clear();
        classSectionList.clear();

        // Load Teachers
        try (BufferedReader br = new BufferedReader(new FileReader("teachers.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    Teacher teacher = new Teacher(parts[0], parts[1], parts[2]);
                    teacherList.add(teacher);
                }
            }
        } catch (IOException e) {
            System.out.println("Không thể tải dữ liệu giáo viên: " + e.getMessage());
        }

        // Load Students
        try (BufferedReader br = new BufferedReader(new FileReader("students.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    Student student = new Student(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]));
                    studentList.add(student);
                }
            }
        } catch (IOException e) {
            System.out.println("Không thể tải dữ liệu sinh viên: " + e.getMessage());
        }

        // Load Subjects
        try (BufferedReader br = new BufferedReader(new FileReader("subjects.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    Subject subject = new Subject(parts[0], parts[1], Integer.parseInt(parts[2]));
                    subjectList.add(subject);
                }
            }
        } catch (IOException e) {
            System.out.println("Không thể tải dữ liệu môn học: " + e.getMessage());
        }

        // Load ClassSections
        try (BufferedReader br = new BufferedReader(new FileReader("classes.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String classCode = parts[0];
                    String subjectID = parts[1];
                    String teacherID = parts[2];
                    int credit = Integer.parseInt(parts[3]);

                    Subject subject = findSubjectByID(subjectID);
                    Teacher teacher = findTeacherByID(teacherID);
                    if (subject != null && teacher != null) {
                        ClassSection cs = new ClassSection(classCode, subject, teacher, credit);
                        classSectionList.add(cs);
                        subject.addClassSection(cs);
                        teacher.addClass(cs);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Không thể tải dữ liệu lớp học: " + e.getMessage());
        }

        // Load ClassSessions
        loadClassSessions();
    }

    private static void loadClassSessions() {
        File file = new File("class_sessions.txt");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String classCode = parts[0];
                    String sessionID = parts[1];
                    LocalDateTime startTime = LocalDateTime.parse(parts[2], SESSION_FORMATTER);
                    LocalDateTime endTime = LocalDateTime.parse(parts[3], SESSION_FORMATTER);

                    ClassSection cs = findClassSectionByCode(classCode);
                    if (cs != null) {
                        ClassSession session = new ClassSession(sessionID, startTime, endTime);
                        cs.addClassSession(session);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Không thể tải dữ liệu buổi học: " + e.getMessage());
        }
    }

    public static void calculateGrades() {
        for (ClassSection cs : classSectionList) {
            for (Student s : cs.getEnrolledStudents()) {
                int absentCount = 0;
                for (ClassSession session : cs.getClassSessions()) {
                    Boolean isPresent = session.getAttendanceRecords().get(s.getID());
                    if (isPresent != null && !isPresent) {
                        absentCount++;
                    }
                }

                // Quy định: Trượt nếu vắng mặt >= 5 buổi
                if (absentCount >= 5) {
                    s.getFailedSubjects().add(cs.getSubject().getSubjectID());
                } else {
                    s.getPassedSubjects().add(cs.getSubject().getSubjectID());
                }
            }
        }
        saveData(); // Lưu dữ liệu sau khi tính điểm
    }

    // Phương thức saveData() để lưu dữ liệu vào file
    public static void saveData() {
        // Save Teachers
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("teachers.txt"))) {
            for (Teacher t : teacherList) {
                bw.write(t.getID() + "," + t.getName() + "," + t.getEmail());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Không thể lưu dữ liệu giáo viên: " + e.getMessage());
        }

        // Save Students
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("students.txt"))) {
            for (Student s : studentList) {
                bw.write(s.getID() + "," + s.getName() + "," + s.getEmail() + "," + s.getRemainingCredits());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Không thể lưu dữ liệu sinh viên: " + e.getMessage());
        }

        // Save Subjects
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("subjects.txt"))) {
            for (Subject s : subjectList) {
                bw.write(s.getSubjectID() + "," + s.getTitle() + "," + s.getCredit());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Không thể lưu dữ liệu môn học: " + e.getMessage());
        }

        // Save ClassSections
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("classes.txt"))) {
            for (ClassSection cs : classSectionList) {
                bw.write(cs.getClassCode() + "," + cs.getSubject().getSubjectID() + "," + cs.getTeacher().getID() + "," + cs.getCredit());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Không thể lưu dữ liệu lớp học: " + e.getMessage());
        }

        // Save ClassSessions
        saveClassSessions();
    }

    private static void saveClassSessions() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("class_sessions.txt"))) {
            for (ClassSection cs : classSectionList) {
                for (ClassSession session : cs.getClassSessions()) {
                    String line = cs.getClassCode() + "," + session.getSessionID() + ","
                            + session.getStartTime().format(SESSION_FORMATTER) + ","
                            + session.getEndTime().format(SESSION_FORMATTER);
                    bw.write(line);
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Không thể lưu dữ liệu buổi học: " + e.getMessage());
        }
    }

    // Phương thức tìm sinh viên theo ID
    public static Student findStudentByID(String id) {
        for (Student s : studentList) {
            if (s.getID().equals(id)) {
                return s;
            }
        }
        return null;
    }

    // Phương thức tìm giáo viên theo ID
    public static Teacher findTeacherByID(String id) {
        for (Teacher t : teacherList) {
            if (t.getID().equals(id)) {
                return t;
            }
        }
        return null;
    }

    // Phương thức tìm môn học theo ID
    public static Subject findSubjectByID(String id) {
        for (Subject s : subjectList) {
            if (s.getSubjectID().equals(id)) {
                return s;
            }
        }
        return null;
    }

    // Bổ sung phương thức tìm ClassSection theo mã lớp
    public static ClassSection findClassSectionByCode(String code) {
        for (ClassSection cs : classSectionList) {
            if (cs.getClassCode().equals(code)) {
                return cs;
            }
        }
        return null;
    }
}
