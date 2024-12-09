package ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import models.Student;
import utils.DataManager;

public class StudentPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField tfStudentID, tfName, tfAge, tfEmail;
    private JButton btnAdd, btnUpdate, btnDelete;

    public StudentPanel() {
        setLayout(new BorderLayout());

        String[] columnNames = {"Mã SV", "Tên", "Tuổi", "Email"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);

        loadStudents();

        JPanel inputPanel = new JPanel(new GridLayout(5, 2));
        tfStudentID = new JTextField();
        tfName = new JTextField();
        tfAge = new JTextField(); // Age không có trong model, hiển thị trống
        tfEmail = new JTextField();

        inputPanel.add(new JLabel("Mã sinh viên:"));
        inputPanel.add(tfStudentID);
        inputPanel.add(new JLabel("Tên:"));
        inputPanel.add(tfName);
        inputPanel.add(new JLabel("Tuổi:"));
        inputPanel.add(tfAge);
        inputPanel.add(new JLabel("Email:"));
        inputPanel.add(tfEmail);

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

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                tfStudentID.setText(tableModel.getValueAt(selectedRow, 0).toString());
                tfName.setText(tableModel.getValueAt(selectedRow, 1).toString());
                // Tuổi trong bảng để trống
                tfAge.setText(tableModel.getValueAt(selectedRow, 2).toString());
                tfEmail.setText(tableModel.getValueAt(selectedRow, 3).toString());
            }
        });
    }

    private void loadStudents() {
        tableModel.setRowCount(0);
        for (Student s : DataManager.studentList) {
            tableModel.addRow(new Object[]{s.getID(), s.getName(), "", s.getEmail()});
        }
    }

    private void addStudent() {
        String studentID = tfStudentID.getText().trim();
        String name = tfName.getText().trim();
        String email = tfEmail.getText().trim();

        if (studentID.isEmpty() || name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin (trừ tuổi không bắt buộc).");
            return;
        }

        for (Student s : DataManager.studentList) {
            if (s.getID().equals(studentID)) {
                JOptionPane.showMessageDialog(this, "Mã sinh viên đã tồn tại.");
                return;
            }
        }

        // tạo student mới với remainingCredits mặc định là 30
        Student student = new Student(studentID, name, email, 30);
        DataManager.studentList.add(student);
        tableModel.addRow(new Object[]{studentID, name, "", email});
        DataManager.saveData();
        clearFields();
    }

    private void updateStudent() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            String studentID = tfStudentID.getText().trim();
            String name = tfName.getText().trim();
            String email = tfEmail.getText().trim();

            if (studentID.isEmpty() || name.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin (trừ tuổi không bắt buộc).");
                return;
            }

            for (int i = 0; i < DataManager.studentList.size(); i++) {
                if (i != selectedRow && DataManager.studentList.get(i).getID().equals(studentID)) {
                    JOptionPane.showMessageDialog(this, "Mã sinh viên đã tồn tại.");
                    return;
                }
            }

            Student student = DataManager.studentList.get(selectedRow);
            student.setID(studentID);
            student.setName(name);
            student.setEmail(email);

            tableModel.setValueAt(studentID, selectedRow, 0);
            tableModel.setValueAt(name, selectedRow, 1);
            tableModel.setValueAt("", selectedRow, 2);
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
