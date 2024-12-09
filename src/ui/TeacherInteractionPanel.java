package ui;

import models.*;
import utils.DataManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;

public class TeacherInteractionPanel extends JPanel {
    private Teacher teacher;
    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField tfClassCode;
    private JComboBox<Subject> cbSubject;
    private JButton btnCreateClass, btnRemoveStudent, btnViewSessions, btnLogout;

    private JComboBox<ClassSection> cbClassSection;
    private JTable studentTable;
    private DefaultTableModel studentTableModel;

    public TeacherInteractionPanel(Teacher teacher) {
        this.teacher = teacher;
        setLayout(new BorderLayout(10, 10)); // Thêm khoảng cách giữa các thành phần

        // Panel tạo lớp mới
        JPanel createClassPanel = new JPanel(new GridLayout(2, 3, 5, 5)); // Thay đổi GridLayout để phù hợp
        tfClassCode = new JTextField();
        cbSubject = new JComboBox<>();
        for (Subject s : DataManager.subjectList) {
            cbSubject.addItem(s);
        }
        btnCreateClass = new JButton("Tạo lớp");

        createClassPanel.add(new JLabel("Mã lớp:"));
        createClassPanel.add(tfClassCode);
        createClassPanel.add(new JLabel()); // Khoảng trống
        createClassPanel.add(new JLabel("Chọn môn học:"));
        createClassPanel.add(cbSubject);
        createClassPanel.add(btnCreateClass);

        btnCreateClass.addActionListener(e -> createClass());

        // Bảng hiển thị các lớp đang dạy
        String[] columnNames = {"Mã lớp", "Môn học", "Giáo viên"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        loadTeachingClasses();

        // Panel chọn lớp để xem sinh viên và buổi học
        JPanel classSelectionPanel = new JPanel(new BorderLayout(5, 5));
        cbClassSection = new JComboBox<>(); // Khởi tạo trước
        loadTeachingClassesComboBox();
        cbClassSection.addActionListener(e -> loadEnrolledStudents());

        // Bảng hiển thị sinh viên trong lớp
        String[] studentColumnNames = {"Mã SV", "Tên", "Email"};
        studentTableModel = new DefaultTableModel(studentColumnNames, 0);
        studentTable = new JTable(studentTableModel);

        // Panel hiển thị sinh viên và các buổi học
        JPanel studentSessionPanel = new JPanel(new BorderLayout(5, 5));

        // Bảng hiển thị sinh viên
        JScrollPane studentScrollPane = new JScrollPane(studentTable);
        studentSessionPanel.add(studentScrollPane, BorderLayout.CENTER);

        // Panel thêm buổi học và xem buổi học
        JPanel sessionButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton btnAddSession = new JButton("Thêm buổi học");
        JButton btnViewSessions = new JButton("Xem buổi học");
        sessionButtonPanel.add(btnAddSession);
        sessionButtonPanel.add(btnViewSessions);

        studentSessionPanel.add(sessionButtonPanel, BorderLayout.SOUTH);

        btnAddSession.addActionListener(e -> addSession());
        btnViewSessions.addActionListener(e -> viewSessions());

        classSelectionPanel.add(new JLabel("Chọn lớp:"), BorderLayout.NORTH);
        classSelectionPanel.add(cbClassSection, BorderLayout.CENTER);
        classSelectionPanel.add(studentSessionPanel, BorderLayout.SOUTH);

        // Nút xóa sinh viên và đăng xuất
        JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnRemoveStudent = new JButton("Xóa sinh viên");
        btnLogout = new JButton("Đăng xuất");
        bottomButtonPanel.add(btnRemoveStudent);
        bottomButtonPanel.add(btnLogout);

        btnRemoveStudent.addActionListener(e -> removeStudent());
        btnLogout.addActionListener(e -> logout());

        // Thêm các thành phần vào giao diện
        add(createClassPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(classSelectionPanel, BorderLayout.SOUTH);
        add(bottomButtonPanel, BorderLayout.PAGE_END);
    }

    private void createClass() {
        String classCode = tfClassCode.getText().trim();
        Subject subject = (Subject) cbSubject.getSelectedItem();

        if (classCode.isEmpty() || subject == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.");
            return;
        }
        if (DataManager.findClassSectionByCode(classCode) != null) {
            JOptionPane.showMessageDialog(this, "Mã lớp đã tồn tại.");
            return;
        }

        // Số tín chỉ của lớp bằng số tín chỉ của môn học
        int credit = subject.getCredit();

        ClassSection cs = new ClassSection(classCode, subject, teacher, credit);
        teacher.addClass(cs);
        subject.addClassSection(cs);
        DataManager.classSectionList.add(cs);
        DataManager.saveData();
        loadTeachingClasses();
        JOptionPane.showMessageDialog(this, "Tạo lớp thành công.");
        clearFields();
    }

    private void loadTeachingClasses() {
        tableModel.setRowCount(0);
        cbClassSection.removeAllItems();
        for (ClassSection cs : teacher.getTeachingClasses()) {
            tableModel.addRow(new Object[]{cs.getClassCode(), cs.getSubject().getTitle(), cs.getTeacher().getName()});
            cbClassSection.addItem(cs);
        }
    }

    private void loadTeachingClassesComboBox() {
        cbClassSection.removeAllItems();
        for (ClassSection cs : teacher.getTeachingClasses()) {
            cbClassSection.addItem(cs);
        }
    }

    private void loadEnrolledStudents() {
        ClassSection cs = (ClassSection) cbClassSection.getSelectedItem();
        if (cs != null) {
            // Tải danh sách sinh viên
            studentTableModel.setRowCount(0);
            for (Student s : cs.getEnrolledStudents()) {
                studentTableModel.addRow(new Object[]{s.getID(), s.getName(), s.getEmail()});
            }
        }
    }

    private void addSession() {
        ClassSection cs = (ClassSection) cbClassSection.getSelectedItem();
        if (cs == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp học trước.");
            return;
        }

        JTextField tfSessionID = new JTextField();
        JTextField tfStartTime = new JTextField(); // Định dạng: yyyy-MM-dd HH:mm
        JTextField tfEndTime = new JTextField();   // Định dạng: yyyy-MM-dd HH:mm

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.add(new JLabel("Mã buổi học:"));
        panel.add(tfSessionID);
        panel.add(new JLabel("Thời gian bắt đầu (yyyy-MM-dd HH:mm):"));
        panel.add(tfStartTime);
        panel.add(new JLabel("Thời gian kết thúc (yyyy-MM-dd HH:mm):"));
        panel.add(tfEndTime);

        int result = JOptionPane.showConfirmDialog(this, panel, "Thêm buổi học", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String sessionID = tfSessionID.getText().trim();
            String startStr = tfStartTime.getText().trim();
            String endStr = tfEndTime.getText().trim();

            if (sessionID.isEmpty() || startStr.isEmpty() || endStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin buổi học.");
                return;
            }

            try {
                LocalDateTime startTime = LocalDateTime.parse(startStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                LocalDateTime endTime = LocalDateTime.parse(endStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

                // Kiểm tra giờ học hợp lệ
                int startHour = startTime.getHour();
                int endHour = endTime.getHour();
                if (startHour < 6 || (startHour == 17 && startTime.getMinute() > 0) || startHour > 17 ||
                        endHour < 6 || (endHour == 17 && endTime.getMinute() > 0) || endHour > 17) {
                    JOptionPane.showMessageDialog(this, "Giờ học phải từ 6:00 đến 17:00.");
                    return;
                }

                // Kiểm tra độ dài buổi học (2h30p - 3h30p)
                long minutes = java.time.Duration.between(startTime, endTime).toMinutes();
                if (minutes < 150 || minutes > 210) {
                    JOptionPane.showMessageDialog(this, "Thời gian buổi học phải từ 2h30p đến 3h30p.");
                    return;
                }

                // Kiểm tra mã buổi học đã tồn tại trong lớp
                for (ClassSession session : cs.getClassSessions()) {
                    if (session.getSessionID().equals(sessionID)) {
                        JOptionPane.showMessageDialog(this, "Mã buổi học đã tồn tại trong lớp.");
                        return;
                    }
                }

                ClassSession session = new ClassSession(sessionID, startTime, endTime);
                cs.addClassSession(session);
                DataManager.saveData();
                JOptionPane.showMessageDialog(this, "Thêm buổi học thành công.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Định dạng thời gian không hợp lệ.");
            }
        }
    }

    private void viewSessions() {
        ClassSection cs = (ClassSection) cbClassSection.getSelectedItem();
        if (cs == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp học trước.");
            return;
        }

        // Tạo bảng để hiển thị các buổi học
        String[] sessionColumnNames = {"Mã buổi học", "Thời gian bắt đầu", "Thời gian kết thúc"};
        DefaultTableModel sessionTableModel = new DefaultTableModel(sessionColumnNames, 0);
        JTable sessionTable = new JTable(sessionTableModel);

        for (ClassSession session : cs.getClassSessions()) {
            sessionTableModel.addRow(new Object[]{
                    session.getSessionID(),
                    session.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    session.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            });
        }

        // Tạo dialog để chọn buổi học
        int selectedOption = JOptionPane.showConfirmDialog(this, new JScrollPane(sessionTable), "Chọn buổi học để xem điểm danh", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (selectedOption == JOptionPane.OK_OPTION) {
            int selectedRow = sessionTable.getSelectedRow();
            if (selectedRow >= 0) {
                String sessionID = (String) sessionTableModel.getValueAt(selectedRow, 0);
                ClassSession session = findSessionByID(cs, sessionID);
                if (session != null) {
                    // Mở cửa sổ điểm danh
                    JFrame sessionFrame = new JFrame("Điểm danh - Buổi " + sessionID);
                    sessionFrame.setSize(600, 400);
                    sessionFrame.setLocationRelativeTo(null);
                    sessionFrame.add(new ClassSessionPanel(cs, session));
                    sessionFrame.setVisible(true);
                }
            }
        }
    }

    private ClassSession findSessionByID(ClassSection cs, String sessionID) {
        for (ClassSession session : cs.getClassSessions()) {
            if (session.getSessionID().equals(sessionID)) {
                return session;
            }
        }
        return null;
    }

    private void removeStudent() {
        ClassSection cs = (ClassSection) cbClassSection.getSelectedItem();
        if (cs == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp học.");
            return;
        }

        JTable tempTable = new JTable();
        DefaultTableModel tempModel = new DefaultTableModel(new String[]{"Mã SV", "Tên", "Email"}, 0);
        tempTable.setModel(tempModel);
        for (Student s : cs.getEnrolledStudents()) {
            tempModel.addRow(new Object[]{s.getID(), s.getName(), s.getEmail()});
        }

        int result = JOptionPane.showConfirmDialog(this, new JScrollPane(tempTable), "Chọn sinh viên để xóa", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            int selectedRow = tempTable.getSelectedRow();
            if (selectedRow >= 0) {
                String studentID = (String) tempModel.getValueAt(selectedRow, 0);
                Student student = DataManager.findStudentByID(studentID);
                if (student != null) {
                    cs.removeStudent(student);
                    student.removeClass(cs);
                    // Hoàn lại số tín chỉ cho sinh viên
                    student.setRemainingCredits(student.getRemainingCredits() + cs.getCredit());
                    DataManager.saveData();
                    JOptionPane.showMessageDialog(this, "Đã xóa sinh viên khỏi lớp.");
                    loadEnrolledStudents();
                }
            }
        }
    }

    private void logout() {
        mainFrame.showLoginTab();
    }

    private void clearFields() {
        tfClassCode.setText("");
        cbSubject.setSelectedIndex(0);
    }
}
