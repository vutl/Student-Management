package ui;

import models.*;
import utils.DataManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class StudentInteractionPanel extends JPanel {
    private Student student;
    private JTable table;
    private DefaultTableModel tableModel;

    private JComboBox<Subject> cbSubject;
    private JComboBox<ClassSection> cbClassSection;
    private JLabel lblTeacherName;
    private JLabel lblTotalStudents;
    private JLabel lblRemainingCredits;
    private JButton btnRegister, btnCancel, btnViewGrades, btnLogout;

    public StudentInteractionPanel(Student student) {
        this.student = student;
        setLayout(new BorderLayout(10, 10)); // Thêm khoảng cách giữa các thành phần

        // Panel chọn môn học và lớp
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

        // Bảng hiển thị các lớp đã đăng ký
        String[] columnNames = {"Mã lớp", "Môn học", "Giáo viên"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        loadRegisteredClasses();

        // Nút đăng ký, hủy đăng ký, xem điểm và đăng xuất
        btnRegister = new JButton("Đăng ký");
        btnCancel = new JButton("Hủy đăng ký");
        btnViewGrades = new JButton("Xem điểm và trạng thái môn học");
        btnLogout = new JButton("Đăng xuất");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(btnRegister);
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnViewGrades);
        buttonPanel.add(btnLogout);

        btnRegister.addActionListener(e -> registerClass());
        btnCancel.addActionListener(e -> cancelClass());
        btnViewGrades.addActionListener(e -> viewGrades());
        btnLogout.addActionListener(e -> logout());

        // Thêm các thành phần vào giao diện
        add(selectionPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Tải danh sách lớp cho môn học đầu tiên
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
                cbClassSection.addItem(cs);
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
            // Kiểm tra không tham gia nhiều hơn 1 lớp cùng môn học
            for (ClassSection enrolled : student.getEnrolledClasses()) {
                if (enrolled.getSubject().getSubjectID().equals(cs.getSubject().getSubjectID())) {
                    JOptionPane.showMessageDialog(this, "Bạn đã tham gia một lớp cùng môn học này.");
                    return;
                }
            }

            if (!student.getEnrolledClasses().contains(cs)) {
                // Kiểm tra số tín chỉ
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

    private void cancelClass() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            String classCode = tableModel.getValueAt(selectedRow, 0).toString();
            ClassSection cs = DataManager.findClassSectionByCode(classCode);
            if (cs != null) {
                // Kiểm tra số buổi đã tham gia >=3, không cho phép hủy đăng ký
                int attendanceCount = 0;
                for (ClassSession session : cs.getClassSessions()) {
                    if (session.getAttendanceRecords().getOrDefault(student.getID(), false)) {
                        attendanceCount++;
                    }
                }
                if (attendanceCount >= 3) {
                    JOptionPane.showMessageDialog(this, "Bạn đã tham gia đủ buổi học, không thể hủy đăng ký.");
                    return;
                }

                student.removeClass(cs);
                cs.removeStudent(student);
                // Hoàn lại số tín chỉ cho sinh viên
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
        StringBuilder sb = new StringBuilder();
        sb.append("Môn học đã qua:\n");
        for (String subjectID : student.getPassedSubjects()) {
            Subject subject = DataManager.findSubjectByID(subjectID);
            if (subject != null) {
                sb.append(subject.getTitle()).append("\n");
            }
        }
        sb.append("\nMôn học đã trượt:\n");
        for (String subjectID : student.getFailedSubjects()) {
            Subject subject = DataManager.findSubjectByID(subjectID);
            if (subject != null) {
                sb.append(subject.getTitle()).append("\n");
            }
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "Điểm và Trạng thái môn học", JOptionPane.INFORMATION_MESSAGE);
    }

    private void logout() {
        MainFrame mainFrame = (MainFrame) SwingUtilities.getWindowAncestor(this);
        mainFrame.showLoginTab();
    }
}
