package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import models.ClassSection;
import models.Subject;
import utils.DataManager;

public class SubjectPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField tfSubjectID, tfTitle, tfCredit;
    private JButton btnAdd, btnRemove, btnUpdate;

    public SubjectPanel() {
        setLayout(new BorderLayout(10, 10));

        JPanel addPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        tfSubjectID = new JTextField();
        tfTitle = new JTextField();
        tfCredit = new JTextField();
        btnAdd = new JButton("Thêm môn học");
        btnUpdate = new JButton("Sửa môn học");

        addPanel.add(new JLabel("Mã môn học:"));
        addPanel.add(tfSubjectID);
        addPanel.add(new JLabel("Tên môn học:"));
        addPanel.add(tfTitle);
        addPanel.add(new JLabel("Số tín chỉ (1-5):"));
        addPanel.add(tfCredit);
        addPanel.add(btnAdd);
        addPanel.add(btnUpdate);

        btnAdd.addActionListener(e -> addSubject());
        btnUpdate.addActionListener(e -> updateSubject());

        String[] columnNames = {"Mã môn học", "Tên môn học", "Số tín chỉ"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        loadSubjects();

        btnRemove = new JButton("Xóa môn học");
        btnRemove.addActionListener(e -> removeSubject());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(btnRemove);

        add(addPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Khi chọn một dòng trong bảng, điền thông tin vào các trường để sửa
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    String subjectID = (String) tableModel.getValueAt(selectedRow, 0);
                    String title = (String) tableModel.getValueAt(selectedRow, 1);
                    int credit = (int) tableModel.getValueAt(selectedRow, 2);

                    tfSubjectID.setText(subjectID);
                    tfTitle.setText(title);
                    tfCredit.setText(String.valueOf(credit));
                }
            }
        });
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
            // Giới hạn số tín chỉ 1 đến 5
            if (credit < 1 || credit > 5) {
                JOptionPane.showMessageDialog(this, "Số tín chỉ phải nằm trong khoảng từ 1 đến 5.");
                return;
            }

            Subject subject = new Subject(subjectID, title, credit);
            DataManager.subjectList.add(subject);
            DataManager.saveData();
            loadSubjects();
            JOptionPane.showMessageDialog(this, "Thêm môn học thành công.");
            clearFields();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số tín chỉ phải là số.");
        }
    }

    private void updateSubject() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn môn học để cập nhật.");
            return;
        }

        String subjectID = tfSubjectID.getText().trim();
        String title = tfTitle.getText().trim();
        String creditStr = tfCredit.getText().trim();

        if (subjectID.isEmpty() || title.isEmpty() || creditStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        try {
            int credit = Integer.parseInt(creditStr);
            if (credit < 1 || credit > 5) {
                JOptionPane.showMessageDialog(this, "Số tín chỉ phải nằm trong khoảng từ 1 đến 5.");
                return;
            }

            // Kiểm tra mã môn học trùng lặp (trừ chính nó)
            for (int i = 0; i < DataManager.subjectList.size(); i++) {
                if (i != selectedRow && DataManager.subjectList.get(i).getSubjectID().equals(subjectID)) {
                    JOptionPane.showMessageDialog(this, "Mã môn học đã tồn tại.");
                    return;
                }
            }

            Subject subject = DataManager.subjectList.get(selectedRow);
            subject.setSubjectID(subjectID);
            subject.setTitle(title);
            subject.setCredit(credit);

            DataManager.saveData();
            loadSubjects();
            JOptionPane.showMessageDialog(this, "Cập nhật môn học thành công.");
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
                for (ClassSection cs : DataManager.classSectionList) {
                    if (cs.getSubject().getSubjectID().equals(subjectID)) {
                        hasClass = true;
                        break;
                    }
                }
                if (hasClass) {
                    JOptionPane.showMessageDialog(this, "Môn học này đang được sử dụng trong các lớp học.");
                    return;
                }

                DataManager.subjectList.remove(subject);
                DataManager.saveData();
                loadSubjects();
                JOptionPane.showMessageDialog(this, "Đã xóa môn học.");
                clearFields();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn môn học để xóa.");
        }
    }

    private void loadSubjects() {
        tableModel.setRowCount(0);
        for (Subject s : DataManager.subjectList) {
            tableModel.addRow(new Object[]{s.getSubjectID(), s.getTitle(), s.getCredit()});
        }
    }

    private Subject findSubjectByID(String id) {
        for (Subject s : DataManager.subjectList) {
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
