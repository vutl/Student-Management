package ui;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import models.*;
import utils.DataManager;

public class StudentInteractionPanel extends JPanel {
    private Student student;
    private JTable table;
    private DefaultTableModel tableModel;

    private JComboBox<Subject> cbSubject;
    private JComboBox<ClassSection> cbClassSection;
    private JLabel lblTeacherName;
    private JLabel lblTotalStudents;
    private JLabel lblRemainingCredits;
    private JButton btnRegister, btnCancel, btnViewGrades, btnViewAttendance, btnLogout;

    public StudentInteractionPanel(Student student) {
        this.student = student;
        setLayout(new BorderLayout(10, 10));

        JPanel selectionPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        cbSubject = new JComboBox<>();
        for (Subject s : DataManager.subjectList) {
            cbSubject.addItem(s);
        }
        cbSubject.addActionListener(e -> loadClassSections());

        cbClassSection = new JComboBox<>();
        cbClassSection.addActionListener(e -> updateClassInfo());

        lblTeacherName = new JLabel("Giáo viên: ");
        lblTotalStudents = new JLabel("Tổng số sinh viên: ");
        lblRemainingCredits = new JLabel("Số tín chỉ còn lại: " + student.getRemainingCredits());

        selectionPanel.add(new JLabel("Chọn môn học:"));
        selectionPanel.add(cbSubject);
        selectionPanel.add(new JLabel("Chọn lớp:"));
        selectionPanel.add(cbClassSection);
        selectionPanel.add(new JLabel("Giáo viên:"));
        selectionPanel.add(lblTeacherName);
        selectionPanel.add(new JLabel("Tổng số sinh viên:"));
        selectionPanel.add(lblTotalStudents);
        selectionPanel.add(new JLabel("Số tín chỉ còn lại:"));
        selectionPanel.add(lblRemainingCredits);

        String[] columnNames = {"Mã lớp", "Môn học", "Giáo viên"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        loadRegisteredClasses();

        btnRegister = new JButton("Đăng ký");
        btnCancel = new JButton("Hủy đăng ký");
        btnViewGrades = new JButton("Xem điểm");
        btnViewAttendance = new JButton("Xem Attendance");
        btnLogout = new JButton("Đăng xuất");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(btnRegister);
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnViewGrades);
        buttonPanel.add(btnViewAttendance);
        buttonPanel.add(btnLogout);

        btnRegister.addActionListener(e -> registerClass());
        btnCancel.addActionListener(e -> cancelClass());
        btnViewGrades.addActionListener(e -> viewGrades());
        btnViewAttendance.addActionListener(e -> viewAttendance());
        btnLogout.addActionListener(e -> logout());

        add(selectionPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        if (cbSubject.getItemCount() > 0) {
            loadClassSections();
        }
    }

    private void loadRegisteredClasses() {
        tableModel.setRowCount(0);
        for (ClassSection cs : student.getEnrolledClasses()) {
            tableModel.addRow(new Object[]{cs.getClassCode(), cs.getSubject().getTitle(), cs.getTeacher().getName()});
        }
        lblRemainingCredits.setText("Số tín chỉ còn lại: " + student.getRemainingCredits());
    }

    private void loadClassSections() {
        cbClassSection.removeAllItems();
        Subject selectedSubject = (Subject) cbSubject.getSelectedItem();
        if (selectedSubject != null) {
            for (ClassSection cs : selectedSubject.getClassSections()) {
                // Kiểm tra sinh viên đã tham gia lớp nào của môn này chưa
                boolean alreadyEnrolled = false;
                for (ClassSection enrolledCs : student.getEnrolledClasses()) {
                    if (enrolledCs.getSubject().getSubjectID().equals(selectedSubject.getSubjectID())) {
                        alreadyEnrolled = true;
                        break;
                    }
                }
                if (!alreadyEnrolled) {
                    cbClassSection.addItem(cs);
                }
            }
        }
    }

    private void updateClassInfo() {
        ClassSection cs = (ClassSection) cbClassSection.getSelectedItem();
        if (cs != null) {
            lblTeacherName.setText(cs.getTeacher().getName());
            lblTotalStudents.setText(String.valueOf(cs.getEnrolledStudents().size()));
        } else {
            lblTeacherName.setText("Giáo viên: ");
            lblTotalStudents.setText("Tổng số sinh viên: ");
        }
    }

    private void registerClass() {
        ClassSection cs = (ClassSection) cbClassSection.getSelectedItem();
        if (cs != null) {
            // Kiểm tra lớp đã kết thúc hoặc >=4 buổi => không đăng ký
            if (cs.isFinished()) {
                JOptionPane.showMessageDialog(this, "Lớp đã kết thúc, không thể đăng ký.");
                return;
            }
            if (cs.getClassSessions().size() >=4) {
                JOptionPane.showMessageDialog(this, "Lớp đã có >=4 buổi, không thể đăng ký nữa.");
                return;
            }

            // Kiểm tra sinh viên không học 2 lớp cùng môn đã làm ở loadClassSections()
            // Kiểm tra trùng lịch buổi học:
            if (!checkNoTimeConflict(cs)) {
                JOptionPane.showMessageDialog(this, "Bạn bị trùng giờ với lớp khác bạn đang học.");
                return;
            }

            if (!student.getEnrolledClasses().contains(cs)) {
                if (student.getRemainingCredits() >= cs.getCredit()) {
                    student.addClass(cs);
                    cs.addStudent(student);
                    student.setRemainingCredits(student.getRemainingCredits() - cs.getCredit());
                    loadRegisteredClasses();
                    JOptionPane.showMessageDialog(this, "Đăng ký thành công.");
                    DataManager.saveData();
                } else {
                    JOptionPane.showMessageDialog(this, "Không đủ số tín chỉ để đăng ký lớp này.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Bạn đã đăng ký lớp này rồi.");
            }
        }
    }

    private boolean checkNoTimeConflict(ClassSection newClass) {
        // Lấy tất cả buổi học của newClass, so sánh với buổi học của tất cả lớp student đang học
        for (ClassSection enrolled : student.getEnrolledClasses()) {
            for (ClassSession es : enrolled.getClassSessions()) {
                LocalDateTime eStart = es.getStartTime();
                LocalDateTime eEnd = es.getEndTime();
                for (ClassSession ns : newClass.getClassSessions()) {
                    LocalDateTime nStart = ns.getStartTime();
                    LocalDateTime nEnd = ns.getEndTime();

                    boolean overlap = nStart.isBefore(eEnd) && nEnd.isAfter(eStart);
                    if (overlap) return false;
                }
            }
        }
        return true;
    }

    private void cancelClass() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            String classCode = tableModel.getValueAt(selectedRow, 0).toString();
            ClassSection cs = DataManager.findClassSectionByCode(classCode);
            if (cs != null) {
                int participated = 0;
                for (ClassSession session : cs.getClassSessions()) {
                    Boolean present = session.getAttendanceRecords().get(student.getID());
                    if (present != null && present) participated++;
                }
                if (participated >= 3) {
                    JOptionPane.showMessageDialog(this, "Bạn đã tham gia >=3 buổi, không thể hủy đăng ký.");
                    return;
                }

                student.removeClass(cs);
                cs.removeStudent(student);
                student.setRemainingCredits(student.getRemainingCredits() + cs.getCredit());
                loadRegisteredClasses();
                JOptionPane.showMessageDialog(this, "Hủy đăng ký thành công.");
                DataManager.saveData();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp để hủy đăng ký.");
        }
    }

    private void viewGrades() {
        StringBuilder sb = new StringBuilder("Bảng điểm:\n");
        for (ClassSection cs : student.getEnrolledClasses()) {
            String classCode = cs.getClassCode();
            double mid = student.getMidterm(classCode);
            double fin = student.getFinal(classCode);
            boolean passed = student.isPassed(classCode);
            if (mid >=0 && fin>=0) {
                double finalGrade = mid*0.3 + fin*0.7;
                sb.append("Lớp: ").append(classCode)
                        .append(", Midterm: ").append(mid>=0?mid:"N/A")
                        .append(", Final: ").append(fin>=0?fin:"N/A")
                        .append(", FinalGrade: ").append(finalGrade)
                        .append(", Passed: ").append(passed).append("\n");
            } else {
                sb.append("Lớp: ").append(classCode).append(" - Chưa đủ dữ liệu điểm.\n");
            }
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "Điểm", JOptionPane.INFORMATION_MESSAGE);
    }

    private void viewAttendance() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp trong bảng bên dưới để xem attendance.");
            return;
        }
        String classCode = tableModel.getValueAt(selectedRow, 0).toString();
        ClassSection cs = DataManager.findClassSectionByCode(classCode);
        if (cs == null) return;

        String[] col = {"Mã buổi", "Bắt đầu", "Kết thúc", "Có mặt?"};
        DefaultTableModel attModel = new DefaultTableModel(col,0);
        JTable attTable = new JTable(attModel);

        java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (ClassSession session : cs.getClassSessions()) {
            Boolean present = session.getAttendanceRecords().get(student.getID());
            String presentStr = (present != null && present) ? "Có" : "Vắng";
            attModel.addRow(new Object[]{
                    session.getSessionID(),
                    session.getStartTime().format(fmt),
                    session.getEndTime().format(fmt),
                    presentStr
            });
        }

        JOptionPane.showMessageDialog(this, new JScrollPane(attTable), "Attendance: " + classCode, JOptionPane.INFORMATION_MESSAGE);
    }

    private void logout() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame instanceof MainFrame) {
            ((MainFrame) frame).showLoginTab();
        }
    }
}
