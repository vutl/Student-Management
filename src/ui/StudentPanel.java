package ui;

import models.Student;
import utils.DataManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class StudentPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField tfStudentID, tfName, tfAge, tfEmail;
    private JButton btnAdd, btnUpdate, btnDelete;

    public StudentPanel() {
        setLayout(new BorderLayout());

        // Tạo bảng hiển thị sinh viên
        String[] columnNames = {"Mã SV", "Tên", "Tuổi", "Email"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);

        // Tải dữ liệu từ DataManager vào bảng
        loadStudents();

        // Panel nhập liệu
        JPanel inputPanel = new JPanel(new GridLayout(5, 2));
        tfStudentID = new JTextField();
        tfName = new JTextField();
        tfAge = new JTextField();
        tfEmail = new JTextField();

        inputPanel.add(new JLabel("Mã sinh viên:"));
        inputPanel.add(tfStudentID);
        inputPanel.add(new JLabel("Tên:"));
        inputPanel.add(tfName);
        inputPanel.add(new JLabel("Tuổi:"));
        inputPanel.add(tfAge);
        inputPanel.add(new JLabel("Email:"));
        inputPanel.add(tfEmail);

        // Nút thao tác
        btnAdd = new JButton("Thêm");
        btnUpdate = new JButton("Sửa");
        btnDelete = new JButton("Xóa");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);

        btnAdd.addActionListener(e -> addStudent());
        btnUpdate.addActionListener(e -> updateStudent());
        btnDelete.addActionListener(e -> deleteStudent());

        // Thêm các thành phần vào panel
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);

        // Sự kiện khi chọn một hàng trong bảng
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                tfStudentID.setText(tableModel.getValueAt(selectedRow, 0).toString());
                tfName.setText(tableModel.getValueAt(selectedRow, 1).toString());
                tfAge.setText(tableModel.getValueAt(selectedRow, 2).toString());
                tfEmail.setText(tableModel.getValueAt(selectedRow, 3).toString());
            }
        });
    }

    private void loadStudents() {
        tableModel.setRowCount(0);
        for (Student s : DataManager.studentList) {
            tableModel.addRow(new Object[]{s.getID(), s.getName(), s.getAge(), s.getEmail()});
        }
    }

    private void addStudent() {
        String studentID = tfStudentID.getText().trim();
        String name = tfName.getText().trim();
        String ageStr = tfAge.getText().trim();
        String email = tfEmail.getText().trim();

        if (studentID.isEmpty() || name.isEmpty() || ageStr.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Tuổi phải là số nguyên.");
            return;
        }

        // Kiểm tra mã sinh viên đã tồn tại
        for (Student s : DataManager.studentList) {
            if (s.getID().equals(studentID)) {
                JOptionPane.showMessageDialog(this, "Mã sinh viên đã tồn tại.");
                return;
            }
        }

        int defaultCredits = 20; // Số tín chỉ cố định cho sinh viên mới
        Student student = new Student(studentID, name, age, email, defaultCredits);
        DataManager.studentList.add(student);
        tableModel.addRow(new Object[]{studentID, name, age, email});
        DataManager.saveData();
        clearFields();
    }

    private void updateStudent() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            String studentID = tfStudentID.getText().trim();
            String name = tfName.getText().trim();
            String ageStr = tfAge.getText().trim();
            String email = tfEmail.getText().trim();

            if (studentID.isEmpty() || name.isEmpty() || ageStr.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.");
                return;
            }

            int age;
            try {
                age = Integer.parseInt(ageStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Tuổi phải là số nguyên.");
                return;
            }

            Student student = DataManager.studentList.get(selectedRow);
            student.setID(studentID);
            student.setName(name);
            student.setAge(age);
            student.setEmail(email);
            // Không thay đổi số tín chỉ của sinh viên khi cập nhật thông tin

            tableModel.setValueAt(studentID, selectedRow, 0);
            tableModel.setValueAt(name, selectedRow, 1);
            tableModel.setValueAt(age, selectedRow, 2);
            tableModel.setValueAt(email, selectedRow, 3);
            DataManager.saveData();
            clearFields();
        }
    }

    private void deleteStudent() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            Student student = DataManager.studentList.get(selectedRow);
            if (!student.getEnrolledClasses().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không thể xóa sinh viên đang đăng ký lớp học.");
                return;
            }
            DataManager.studentList.remove(selectedRow);
            tableModel.removeRow(selectedRow);
            DataManager.saveData();
            clearFields();
        }
    }

    private void clearFields() {
        tfStudentID.setText("");
        tfName.setText("");
        tfAge.setText("");
        tfEmail.setText("");
    }
}
