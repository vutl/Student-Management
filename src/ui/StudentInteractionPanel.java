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
    private JButton btnRegister, btnCancel, btnLogout;

    public StudentInteractionPanel(Student student) {
        this.student = student;
        setLayout(new BorderLayout());

        // Bảng hiển thị các lớp đã đăng ký
        String[] columnNames = {"Mã lớp", "Môn học", "Giáo viên"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        loadRegisteredClasses();

        // Panel chọn môn học và lớp
        JPanel selectionPanel = new JPanel(new GridLayout(4, 2));
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
        selectionPanel.add(lblTeacherName);
        selectionPanel.add(lblTotalStudents);
        selectionPanel.add(lblRemainingCredits);

        // Nút đăng ký và hủy đăng ký
        btnRegister = new JButton("Đăng ký");
        btnCancel = new JButton("Hủy đăng ký");
        btnLogout = new JButton("Đăng xuất");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnRegister);
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnLogout);

        btnRegister.addActionListener(e -> registerClass());
        btnCancel.addActionListener(e -> cancelClass());
        btnLogout.addActionListener(e -> logout());

        // Thêm các thành phần vào giao diện
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(selectionPanel, BorderLayout.NORTH);
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
            lblTeacherName.setText("Giáo viên: " + cs.getTeacher().getName());
            lblTotalStudents.setText("Tổng số sinh viên: " + cs.getEnrolledStudents().size());
        } else {
            lblTeacherName.setText("Giáo viên: ");
            lblTotalStudents.setText("Tổng số sinh viên: ");
        }
    }

    private void registerClass() {
        ClassSection cs = (ClassSection) cbClassSection.getSelectedItem();
        if (cs != null) {
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
                student.removeClass(cs);
                cs.removeStudent(student);
                student.setRemainingCredits(student.getRemainingCredits() + cs.getCredit());
                loadRegisteredClasses();
                JOptionPane.showMessageDialog(this, "Hủy đăng ký thành công.");
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
