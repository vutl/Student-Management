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
    public static String currentLoggedInID = null;

    private static final DateTimeFormatter SESSION_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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
                // Nếu có 5 phần tử thì phần tử thứ 5 là finished
                boolean finished = false;
                if (parts.length == 5) {
                    finished = Boolean.parseBoolean(parts[4]);
                }
                if (parts.length >= 4) {
                    String classCode = parts[0];
                    String subjectID = parts[1];
                    String teacherID = parts[2];
                    int credit = Integer.parseInt(parts[3]);

                    Subject subject = findSubjectByID(subjectID);
                    Teacher teacher = findTeacherByID(teacherID);
                    if (subject != null && teacher != null) {
                        ClassSection cs = new ClassSection(classCode, subject, teacher, credit);
                        cs.setFinished(finished);
                        classSectionList.add(cs);
                        subject.addClassSection(cs);
                        teacher.addClass(cs);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Không thể tải dữ liệu lớp học: " + e.getMessage());
        }

        loadClassSessions();
        loadAttendance();
        loadGrades();
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

    private static void loadAttendance() {
        File file = new File("attendance.txt");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String classCode = parts[0];
                    String sessionID = parts[1];
                    String studentID = parts[2];
                    boolean present = Boolean.parseBoolean(parts[3]);

                    ClassSection cs = findClassSectionByCode(classCode);
                    if (cs != null) {
                        ClassSession session = findClassSession(cs, sessionID);
                        if (session != null) {
                            session.markAttendance(studentID, present);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Không thể tải dữ liệu attendance: " + e.getMessage());
        }
    }

    private static void loadGrades() {
        File file = new File("grades.txt");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String studentID = parts[0];
                    String classCode = parts[1];
                    double midterm = Double.parseDouble(parts[2]);
                    double fin = Double.parseDouble(parts[3]);
                    boolean passed = Boolean.parseBoolean(parts[4]);

                    Student st = findStudentByID(studentID);
                    ClassSection cs = findClassSectionByCode(classCode);
                    if (st != null && cs != null) {
                        st.setGradeForClass(cs.getClassCode(), midterm, fin, passed);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Không thể tải dữ liệu điểm: " + e.getMessage());
        }
    }

    public static void calculateGrades(ClassSection cs) {
        // Khi kết thúc lớp học mới tính pass/fail
        // Nếu lớp không đủ 14 hoặc 15 buổi, cứ tính theo số buổi thực tế
        int totalSessions = cs.getClassSessions().size();
        for (Student s : cs.getEnrolledStudents()) {
            int absences = 0;
            for (ClassSession session : cs.getClassSessions()) {
                Boolean isPresent = session.getAttendanceRecords().get(s.getID());
                if (isPresent == null || !isPresent) absences++;
            }
            double midterm = s.getMidterm(cs.getClassCode());
            double fin = s.getFinal(cs.getClassCode());
            boolean passed;
            // Nếu vắng >3 buổi => fail ngay
            if (absences > 3) {
                passed = false;
            } else {
                // Nếu chưa có đủ điểm midterm/final, coi như 0
                if (midterm <0) midterm=0;
                if (fin<0) fin=0;
                double finalGrade = midterm*0.3 + fin*0.7;
                passed = finalGrade >=70;
            }
            s.setGradeForClass(cs.getClassCode(), midterm, fin, passed);
        }
        saveData();
    }

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
                bw.write(cs.getClassCode() + "," + cs.getSubject().getSubjectID() + "," + cs.getTeacher().getID() + "," + cs.getCredit() + "," + cs.isFinished());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Không thể lưu dữ liệu lớp học: " + e.getMessage());
        }

        saveClassSessions();
        saveAttendance();
        saveGrades();
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

    private static void saveAttendance() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("attendance.txt"))) {
            for (ClassSection cs : classSectionList) {
                for (ClassSession session : cs.getClassSessions()) {
                    for (var entry : session.getAttendanceRecords().entrySet()) {
                        String studentID = entry.getKey();
                        boolean present = entry.getValue();
                        String line = cs.getClassCode() + "," + session.getSessionID() + "," + studentID + "," + present;
                        bw.write(line);
                        bw.newLine();
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Không thể lưu dữ liệu attendance: " + e.getMessage());
        }
    }

    private static void saveGrades() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("grades.txt"))) {
            for (Student s : studentList) {
                for (var entry : s.getGrades().entrySet()) {
                    String classCode = entry.getKey();
                    Student.GradeInfo info = entry.getValue();
                    String line = s.getID() + "," + classCode + "," + info.midterm + "," + info.finalExam + "," + info.passed;
                    bw.write(line);
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Không thể lưu dữ liệu điểm: " + e.getMessage());
        }
    }

    public static ClassSession findClassSession(ClassSection cs, String sessionID) {
        for (ClassSession session : cs.getClassSessions()) {
            if (session.getSessionID().equals(sessionID)) {
                return session;
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

    public static Teacher findTeacherByID(String id) {
        for (Teacher t : teacherList) {
            if (t.getID().equals(id)) {
                return t;
            }
        }
        return null;
    }

    public static Subject findSubjectByID(String id) {
        for (Subject s : subjectList) {
            if (s.getSubjectID().equals(id)) {
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
