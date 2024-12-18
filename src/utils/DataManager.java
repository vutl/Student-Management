package utils;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
        // Format: ID,Name,Department,Email
        try (BufferedReader br = new BufferedReader(new FileReader("teachers.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    Teacher teacher = new Teacher(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim());
                    if (!teacherList.contains(teacher)) { // Prevent duplicates
                        teacherList.add(teacher);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Không thể tải dữ liệu giáo viên: " + e.getMessage());
        }

        // Load Students
        // Format: ID,Name,Age,Email,RemainingCredits
        try (BufferedReader br = new BufferedReader(new FileReader("students.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    String studentID = parts[0].trim();
                    String name = parts[1].trim();
                    int age = Integer.parseInt(parts[2].trim());
                    String email = parts[3].trim();
                    int remainingCredits = Integer.parseInt(parts[4].trim());
                    Student student = new Student(studentID, name, age, email, remainingCredits);
                    if (!studentList.contains(student)) { // Prevent duplicates
                        studentList.add(student);
                    }
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
                    String subjectID = parts[0].trim();
                    String title = parts[1].trim();
                    int credit = Integer.parseInt(parts[2].trim());
                    Subject subject = new Subject(subjectID, title, credit);
                    if (!subjectList.contains(subject)) { // Prevent duplicates
                        subjectList.add(subject);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Không thể tải dữ liệu môn học: " + e.getMessage());
        }

        // Load ClassSections
        // Format: classCode,subjectID,teacherID,credit,finished,studentIDs (separated by |)
        try (BufferedReader br = new BufferedReader(new FileReader("classes.txt"))) {
            String line;
            Set<String> loadedClassCodes = new HashSet<>(); // To track duplicates
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 4) continue; // Invalid line
                String classCode = parts[0].trim();
                if (loadedClassCodes.contains(classCode)) {
                    // Duplicate classCode, skip
                    continue;
                }
                loadedClassCodes.add(classCode);

                String subjectID = parts[1].trim();
                String teacherID = parts[2].trim();
                int credit = Integer.parseInt(parts[3].trim());
                boolean finished = false;
                String studentIDsStr = "";
                if (parts.length >=5) {
                    finished = Boolean.parseBoolean(parts[4].trim());
                }
                if (parts.length >=6) {
                    studentIDsStr = parts[5].trim();
                }

                Subject subject = findSubjectByID(subjectID);
                Teacher teacher = findTeacherByID(teacherID);
                if (subject != null && teacher != null) {
                    ClassSection cs = new ClassSection(classCode, subject, teacher, credit);
                    cs.setFinished(finished);
                    classSectionList.add(cs);
                    if (!subject.getClassSections().contains(cs)) {
                        subject.addClassSection(cs);
                    }
                    if (!teacher.getTeachingClasses().contains(cs)) {
                        teacher.addClass(cs);
                    }

                    // Thêm sinh viên vào lớp
                    if (!studentIDsStr.isEmpty()) {
                        String[] studentIDs = studentIDsStr.split("\\|");
                        for (String studentID : studentIDs) {
                            studentID = studentID.trim();
                            Student student = findStudentByID(studentID);
                            if (student != null) {
                                if (!cs.getEnrolledStudents().contains(student)) {
                                    cs.addStudent(student);
                                }
                                if (!student.getEnrolledClasses().contains(cs)) {
                                    student.addClass(cs);
                                }
                            }
                        }
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
                    String classCode = parts[0].trim();
                    String sessionID = parts[1].trim();
                    LocalDateTime startTime = LocalDateTime.parse(parts[2].trim(), SESSION_FORMATTER);
                    LocalDateTime endTime = LocalDateTime.parse(parts[3].trim(), SESSION_FORMATTER);

                    ClassSection cs = findClassSectionByCode(classCode);
                    if (cs != null) {
                        // Prevent adding duplicate ClassSession
                        boolean exists = false;
                        for (ClassSession existingSession : cs.getClassSessions()) {
                            if (existingSession.getSessionID().equals(sessionID)) {
                                exists = true;
                                break;
                            }
                        }
                        if (!exists) {
                            ClassSession session = new ClassSession(sessionID, startTime, endTime);
                            cs.addClassSession(session);
                        }
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
                    String classCode = parts[0].trim();
                    String sessionID = parts[1].trim();
                    String studentID = parts[2].trim();
                    boolean present = Boolean.parseBoolean(parts[3].trim());

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
                    String studentID = parts[0].trim();
                    String classCode = parts[1].trim();
                    double midterm = Double.parseDouble(parts[2].trim());
                    double fin = Double.parseDouble(parts[3].trim());
                    boolean passed = Boolean.parseBoolean(parts[4].trim());

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
            if (absences > 3) {
                passed = false;
            } else {
                if (midterm <0) midterm=0;
                if (fin <0) fin=0;
                double finalGrade = midterm*0.3 + fin*0.7;
                passed = finalGrade >=70;
            }
            s.setGradeForClass(cs.getClassCode(), midterm, fin, passed);
        }
        saveData();
    }

    public static void saveData() {
        // Save Teachers: ID,Name,Department,Email
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("teachers.txt", false))) { // Overwrite
            for (Teacher t : teacherList) {
                bw.write(t.getID() + "," + t.getName() + "," + t.getDepartment() + "," + t.getEmail());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Không thể lưu dữ liệu giáo viên: " + e.getMessage());
        }

        // Save Students: ID,Name,Age,Email,RemainingCredits
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("students.txt", false))) { // Overwrite
            for (Student s : studentList) {
                bw.write(s.getID() + "," + s.getName() + "," + s.getAge() + "," + s.getEmail() + "," + s.getRemainingCredits());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Không thể lưu dữ liệu sinh viên: " + e.getMessage());
        }

        // Save Subjects
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("subjects.txt", false))) { // Overwrite
            for (Subject s : subjectList) {
                bw.write(s.getSubjectID() + "," + s.getTitle() + "," + s.getCredit());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Không thể lưu dữ liệu môn học: " + e.getMessage());
        }

        // Save ClassSections: classCode,subjectID,teacherID,credit,finished,studentIDs (separated by |)
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("classes.txt", false))) { // Overwrite
            for (ClassSection cs : classSectionList) {
                StringBuilder sb = new StringBuilder();
                sb.append(cs.getClassCode()).append(",")
                        .append(cs.getSubject().getSubjectID()).append(",")
                        .append(cs.getTeacher().getID()).append(",")
                        .append(cs.getCredit()).append(",")
                        .append(cs.isFinished()).append(",");

                // Append student IDs
                List<Student> enrolled = cs.getEnrolledStudents();
                for (int i = 0; i < enrolled.size(); i++) {
                    sb.append(enrolled.get(i).getID());
                    if (i != enrolled.size() -1) sb.append("|");
                }

                bw.write(sb.toString());
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
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("class_sessions.txt", false))) { // Overwrite
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
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("attendance.txt", false))) { // Overwrite
            for (ClassSection cs : classSectionList) {
                for (ClassSession session : cs.getClassSessions()) {
                    for (Map.Entry<String, Boolean> entry : session.getAttendanceRecords().entrySet()) {
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
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("grades.txt", false))) { // Overwrite
            for (Student s : studentList) {
                for (Map.Entry<String, Student.GradeInfo> entry : s.getGrades().entrySet()) {
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
