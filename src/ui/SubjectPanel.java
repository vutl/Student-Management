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
    private JButton btnAdd, btnRemove;

    public SubjectPanel() {
        setLayout(new BorderLayout(10, 10));

        // Panel thêm môn học mới
        JPanel addPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        tfSubjectID = new JTextField();
        tfTitle = new JTextField();
        tfCredit = new JTextField();
        btnAdd = new JButton("Thêm môn học");

        addPanel.add(new JLabel("Mã môn học:"));
        addPanel.add(tfSubjectID);
        addPanel.add(new JLabel("Tên môn học:"));
        addPanel.add(tfTitle);
        addPanel.add(new JLabel("Số tín chỉ:"));
        addPanel.add(tfCredit);
        addPanel.add(new JLabel());
        addPanel.add(btnAdd);

        btnAdd.addActionListener(e -> addSubject());

        // Bảng hiển thị môn học
        String[] columnNames = {"Mã môn học", "Tên môn học", "Số tín chỉ"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        loadSubjects();

        // Nút xóa môn học
        btnRemove = new JButton("Xóa môn học");
        btnRemove.addActionListener(e -> removeSubject());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(btnRemove);

        add(addPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void addSubject() {
        String subjectID = tfSubjectID.getText().trim();
        String title = tfTitle.getText().trim();
        String creditStr = tfCredit.getText().trim();

        if (subjectID.isEmpty() || title.isEmpty() || creditStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        if (findSubjectByID(subjectID) != null) {
            JOptionPane.showMessageDialog(this, "Mã môn học đã tồn tại.");
            return;
        }

        try {
            int credit = Integer.parseInt(creditStr);
            Subject subject = new Subject(subjectID, title, credit);
            subjectList.add(subject);
            DataManager.saveData();
            loadSubjects();
            JOptionPane.showMessageDialog(this, "Thêm môn học thành công.");
            clearFields();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số tín chỉ phải là số.");
        }
    }

    private void removeSubject() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            String subjectID = (String) tableModel.getValueAt(selectedRow, 0);
            Subject subject = findSubjectByID(subjectID);
            if (subject != null) {
                // Kiểm tra xem môn học có lớp học nào không
                boolean hasClass = false;
                for (ClassSection cs : classSectionList) {
                    if (cs.getSubject().getSubjectID().equals(subjectID)) {
                        hasClass = true;
                        break;
                    }
                }
                if (hasClass) {
                    JOptionPane.showMessageDialog(this, "Môn học này đang được sử dụng trong các lớp học.");
                    return;
                }

                subjectList.remove(subject);
                DataManager.saveData();
                loadSubjects();
                JOptionPane.showMessageDialog(this, "Đã xóa môn học.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn môn học để xóa.");
        }
    }

    private void loadSubjects() {
        tableModel.setRowCount(0);
        for (Subject s : subjectList) {
            tableModel.addRow(new Object[]{s.getSubjectID(), s.getTitle(), s.getCredit()});
        }
    }

    private Subject findSubjectByID(String id) {
        for (Subject s : subjectList) {
            if (s.getSubjectID().equals(id)) {
                return s;
            }
        }
        return null;
    }

    private void clearFields() {
        tfSubjectID.setText("");
        tfTitle.setText("");
        tfCredit.setText("");
    }
}
