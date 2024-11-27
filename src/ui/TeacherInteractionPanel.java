package ui;

import models.*;
import utils.DataManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class TeacherInteractionPanel extends JPanel {
    private Teacher teacher;
    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField tfClassCode, tfCredit;
    private JComboBox<Subject> cbSubject;
    private JButton btnCreateClass, btnRemoveStudent, btnLogout;

    private JComboBox<ClassSection> cbClassSection;

    public TeacherInteractionPanel(Teacher teacher) {
        this.teacher = teacher;
        setLayout(new BorderLayout());

        // Bảng hiển thị sinh viên trong lớp
        String[] columnNames = {"Mã SV", "Tên", "Email"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);

        // Panel tạo lớp mới
        JPanel createClassPanel = new JPanel(new GridLayout(4, 2));
        tfClassCode = new JTextField();
        cbSubject = new JComboBox<>();
        for (Subject s : DataManager.subjectList) {
            cbSubject.addItem(s);
        }
        tfCredit = new JTextField();
        btnCreateClass = new JButton("Tạo lớp");

        createClassPanel.add(new JLabel("Mã lớp:"));
        createClassPanel.add(tfClassCode);
        createClassPanel.add(new JLabel("Chọn môn học:"));
        createClassPanel.add(cbSubject);
        createClassPanel.add(new JLabel("Số tín chỉ:"));
        createClassPanel.add(tfCredit);
        createClassPanel.add(new JLabel());
        createClassPanel.add(btnCreateClass);

        btnCreateClass.addActionListener(e -> createClass());

        // Panel chọn lớp để xem sinh viên
        JPanel classSelectionPanel = new JPanel(new FlowLayout());
        cbClassSection = new JComboBox<>();
        loadTeachingClasses();
        cbClassSection.addActionListener(e -> loadEnrolledStudents());

        btnRemoveStudent = new JButton("Xóa sinh viên");
        btnRemoveStudent.addActionListener(e -> removeStudent());

        btnLogout = new JButton("Đăng xuất");
        btnLogout.addActionListener(e -> logout());

        classSelectionPanel.add(new JLabel("Chọn lớp:"));
        classSelectionPanel.add(cbClassSection);
        classSelectionPanel.add(btnRemoveStudent);
        classSelectionPanel.add(btnLogout);

        // Thêm các thành phần vào giao diện
        add(createClassPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(classSelectionPanel, BorderLayout.SOUTH);
    }

    private void createClass() {
        String classCode = tfClassCode.getText().trim();
        Subject subject = (Subject) cbSubject.getSelectedItem();
        int credit;
        try {
            credit = Integer.parseInt(tfCredit.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số tín chỉ phải là số nguyên.");
            return;
        }

        if (classCode.isEmpty() || subject == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.");
            return;
        }
        if (DataManager.findClassSectionByCode(classCode) != null) {
            JOptionPane.showMessageDialog(this, "Mã lớp đã tồn tại.");
            return;
        }

        // Kiểm tra số tín chỉ của lớp có khớp với môn học không
        if (credit != subject.getCredit()) {
            JOptionPane.showMessageDialog(this, "Số tín chỉ của lớp phải khớp với môn học.");
            return;
        }

        ClassSection cs = new ClassSection(classCode, subject, teacher, credit);
        teacher.addClass(cs);
        subject.addClassSection(cs);
        DataManager.classSectionList.add(cs);
        DataManager.saveData();
        loadTeachingClasses();
        JOptionPane.showMessageDialog(this, "Tạo lớp thành công.");
    }

    private void loadTeachingClasses() {
        cbClassSection.removeAllItems();
        for (ClassSection cs : teacher.getTeachingClasses()) {
            cbClassSection.addItem(cs);
        }
    }

    private void loadEnrolledStudents() {
        tableModel.setRowCount(0);
        ClassSection cs = (ClassSection) cbClassSection.getSelectedItem();
        if (cs != null) {
            for (Student s : cs.getEnrolledStudents()) {
                tableModel.addRow(new Object[]{s.getID(), s.getName(), s.getEmail()});
            }
        }
    }

    private void removeStudent() {
        int selectedRow = table.getSelectedRow();
        ClassSection cs = (ClassSection) cbClassSection.getSelectedItem();
        if (selectedRow >= 0 && cs != null) {
            String studentID = tableModel.getValueAt(selectedRow, 0).toString();
            Student student = DataManager.findStudentByID(studentID);
            if (student != null) {
                cs.removeStudent(student);
                student.removeClass(cs);
                // Hoàn lại số tín chỉ cho sinh viên
                student.setRemainingCredits(student.getRemainingCredits() + cs.getCredit());
                loadEnrolledStudents();
                JOptionPane.showMessageDialog(this, "Đã xóa sinh viên khỏi lớp.");
                DataManager.saveData();
            }
        }
    }

    private void logout() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        topFrame.setContentPane(new LoginPanel(topFrame));
        topFrame.revalidate();
    }
}
