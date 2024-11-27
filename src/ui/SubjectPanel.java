package ui;

import models.Subject;
import utils.DataManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class SubjectPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField tfSubjectID, tfTitle, tfCredit;
    private JButton btnAdd, btnUpdate, btnDelete;

    public SubjectPanel() {
        setLayout(new BorderLayout());

        // Tạo bảng hiển thị môn học
        String[] columnNames = {"Mã MH", "Tên môn học", "Số tín chỉ"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);

        // Tải dữ liệu từ DataManager vào bảng
        loadSubjects();

        // Panel nhập liệu
        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        tfSubjectID = new JTextField();
        tfTitle = new JTextField();
        tfCredit = new JTextField();

        inputPanel.add(new JLabel("Mã môn học:"));
        inputPanel.add(tfSubjectID);
        inputPanel.add(new JLabel("Tên môn học:"));
        inputPanel.add(tfTitle);
        inputPanel.add(new JLabel("Số tín chỉ (1-5):"));
        inputPanel.add(tfCredit);

        // Nút thao tác
        btnAdd = new JButton("Thêm");
        btnUpdate = new JButton("Sửa");
        btnDelete = new JButton("Xóa");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);

        btnAdd.addActionListener(e -> addSubject());
        btnUpdate.addActionListener(e -> updateSubject());
        btnDelete.addActionListener(e -> deleteSubject());

        // Thêm các thành phần vào panel
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);

        // Sự kiện khi chọn một hàng trong bảng
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                tfSubjectID.setText(tableModel.getValueAt(selectedRow, 0).toString());
                tfTitle.setText(tableModel.getValueAt(selectedRow, 1).toString());
                tfCredit.setText(tableModel.getValueAt(selectedRow, 2).toString());
            }
        });
    }

    private void loadSubjects() {
        tableModel.setRowCount(0);
        for (Subject s : DataManager.subjectList) {
            tableModel.addRow(new Object[]{s.getSubjectID(), s.getTitle(), s.getCredit()});
        }
    }

    private void addSubject() {
        String subjectID = tfSubjectID.getText().trim();
        String title = tfTitle.getText().trim();
        String creditStr = tfCredit.getText().trim();

        if (subjectID.isEmpty() || title.isEmpty() || creditStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        int credit;
        try {
            credit = Integer.parseInt(creditStr);
            if (credit < 1 || credit > 5) {
                JOptionPane.showMessageDialog(this, "Số tín chỉ phải từ 1 đến 5.");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số tín chỉ phải là số nguyên.");
            return;
        }

        // Kiểm tra mã môn học đã tồn tại
        for (Subject s : DataManager.subjectList) {
            if (s.getSubjectID().equals(subjectID)) {
                JOptionPane.showMessageDialog(this, "Mã môn học đã tồn tại.");
                return;
            }
        }

        Subject subject = new Subject(subjectID, title, credit);
        DataManager.subjectList.add(subject);
        tableModel.addRow(new Object[]{subjectID, title, credit});
        DataManager.saveData();
        clearFields();
    }

    private void updateSubject() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            String subjectID = tfSubjectID.getText().trim();
            String title = tfTitle.getText().trim();
            String creditStr = tfCredit.getText().trim();

            if (subjectID.isEmpty() || title.isEmpty() || creditStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.");
                return;
            }

            int credit;
            try {
                credit = Integer.parseInt(creditStr);
                if (credit < 1 || credit > 5) {
                    JOptionPane.showMessageDialog(this, "Số tín chỉ phải từ 1 đến 5.");
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Số tín chỉ phải là số nguyên.");
                return;
            }

            Subject subject = DataManager.subjectList.get(selectedRow);
            subject.setSubjectID(subjectID);
            subject.setTitle(title);
            subject.setCredit(credit);

            tableModel.setValueAt(subjectID, selectedRow, 0);
            tableModel.setValueAt(title, selectedRow, 1);
            tableModel.setValueAt(credit, selectedRow, 2);
            DataManager.saveData();
            clearFields();
        }
    }

    private void deleteSubject() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            Subject subject = DataManager.subjectList.get(selectedRow);
            if (!subject.getClassSections().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không thể xóa môn học đang có lớp học.");
                return;
            }
            DataManager.subjectList.remove(selectedRow);
            tableModel.removeRow(selectedRow);
            DataManager.saveData();
            clearFields();
        }
    }

    private void clearFields() {
        tfSubjectID.setText("");
        tfTitle.setText("");
        tfCredit.setText("");
    }
}
