package ui;

import models.Teacher;
import utils.DataManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class TeacherManagementPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField tfTeacherID, tfName, tfDepartment, tfEmail;
    private JButton btnAdd, btnUpdate, btnDelete;

    public TeacherManagementPanel() {
        setLayout(new BorderLayout());

        // Tạo bảng hiển thị giáo viên
        String[] columnNames = {"Mã GV", "Tên", "Khoa", "Email"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);

        // Tải dữ liệu từ DataManager vào bảng
        loadTeachers();

        // Panel nhập liệu
        JPanel inputPanel = new JPanel(new GridLayout(5, 2));
        tfTeacherID = new JTextField();
        tfName = new JTextField();
        tfDepartment = new JTextField();
        tfEmail = new JTextField();

        inputPanel.add(new JLabel("Mã giáo viên:"));
        inputPanel.add(tfTeacherID);
        inputPanel.add(new JLabel("Tên:"));
        inputPanel.add(tfName);
        inputPanel.add(new JLabel("Khoa:"));
        inputPanel.add(tfDepartment);
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

        btnAdd.addActionListener(e -> addTeacher());
        btnUpdate.addActionListener(e -> updateTeacher());
        btnDelete.addActionListener(e -> deleteTeacher());

        // Thêm các thành phần vào panel
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);

        // Sự kiện khi chọn một hàng trong bảng
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                tfTeacherID.setText(tableModel.getValueAt(selectedRow, 0).toString());
                tfName.setText(tableModel.getValueAt(selectedRow, 1).toString());
                tfDepartment.setText(tableModel.getValueAt(selectedRow, 2).toString());
                tfEmail.setText(tableModel.getValueAt(selectedRow, 3).toString());
            }
        });
    }

    private void loadTeachers() {
        tableModel.setRowCount(0);
        for (Teacher t : DataManager.teacherList) {
            tableModel.addRow(new Object[]{t.getID(), t.getName(), t.getDepartment(), t.getEmail()});
        }
    }

    private void addTeacher() {
        String teacherID = tfTeacherID.getText().trim();
        String name = tfName.getText().trim();
        String department = tfDepartment.getText().trim();
        String email = tfEmail.getText().trim();

        if (teacherID.isEmpty() || name.isEmpty() || department.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        // Kiểm tra mã giáo viên đã tồn tại
        for (Teacher t : DataManager.teacherList) {
            if (t.getID().equals(teacherID)) {
                JOptionPane.showMessageDialog(this, "Mã giáo viên đã tồn tại.");
                return;
            }
        }

        Teacher teacher = new Teacher(teacherID, name, department, email);
        DataManager.teacherList.add(teacher);
        tableModel.addRow(new Object[]{teacherID, name, department, email});
        DataManager.saveData();
        clearFields();
    }

    private void updateTeacher() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            String teacherID = tfTeacherID.getText().trim();
            String name = tfName.getText().trim();
            String department = tfDepartment.getText().trim();
            String email = tfEmail.getText().trim();

            if (teacherID.isEmpty() || name.isEmpty() || department.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.");
                return;
            }

            Teacher teacher = DataManager.teacherList.get(selectedRow);
            teacher.setID(teacherID);
            teacher.setName(name);
            teacher.setDepartment(department);
            teacher.setEmail(email);

            tableModel.setValueAt(teacherID, selectedRow, 0);
            tableModel.setValueAt(name, selectedRow, 1);
            tableModel.setValueAt(department, selectedRow, 2);
            tableModel.setValueAt(email, selectedRow, 3);
            DataManager.saveData();
            clearFields();
        }
    }

    private void deleteTeacher() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            Teacher teacher = DataManager.teacherList.get(selectedRow);
            if (!teacher.getTeachingClasses().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không thể xóa giáo viên đang có lớp học.");
                return;
            }
            DataManager.teacherList.remove(selectedRow);
            tableModel.removeRow(selectedRow);
            DataManager.saveData();
            clearFields();
        }
    }

    private void clearFields() {
        tfTeacherID.setText("");
        tfName.setText("");
        tfDepartment.setText("");
        tfEmail.setText("");
    }
}
