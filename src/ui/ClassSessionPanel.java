package ui;

import models.*;
import utils.DataManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;

public class ClassSessionPanel extends JPanel {
    private ClassSection classSection;
    private ClassSession session;
    private JTable attendanceTable;
    private DefaultTableModel attendanceTableModel;

    private JButton btnSaveAttendance;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ClassSessionPanel(ClassSection classSection, ClassSession session) {
        this.classSection = classSection;
        this.session = session;
        setLayout(new BorderLayout());

        // Bảng hiển thị điểm danh
        String[] columnNames = {"Mã SV", "Tên", "Có mặt"};
        attendanceTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 2) return Boolean.class;
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // Chỉ cột "Có mặt" có thể chỉnh sửa
            }
        };
        attendanceTable = new JTable(attendanceTableModel);
        loadAttendance();

        // Nút lưu điểm danh
        btnSaveAttendance = new JButton("Lưu điểm danh");
        btnSaveAttendance.addActionListener(e -> saveAttendance());

        add(new JScrollPane(attendanceTable), BorderLayout.CENTER);
        add(btnSaveAttendance, BorderLayout.SOUTH);
    }

    private void loadAttendance() {
        attendanceTableModel.setRowCount(0);
        for (Student s : classSection.getEnrolledStudents()) {
            boolean isPresent = session.getAttendanceRecords().getOrDefault(s.getID(), false);
            attendanceTableModel.addRow(new Object[]{s.getID(), s.getName(), isPresent});
        }
    }

    private void saveAttendance() {
        for (int i = 0; i < attendanceTableModel.getRowCount(); i++) {
            String studentID = (String) attendanceTableModel.getValueAt(i, 0);
            Boolean isPresent = (Boolean) attendanceTableModel.getValueAt(i, 2);
            session.markAttendance(studentID, isPresent);
        }
        DataManager.saveData();
        JOptionPane.showMessageDialog(this, "Đã lưu điểm danh.");

        // Kiểm tra nếu đã hết 15 buổi học, tính điểm và cập nhật trạng thái
        if (classSection.getClassSessions().size() == 15) {
            DataManager.calculateGrades();
            JOptionPane.showMessageDialog(this, "Đã tính điểm và cập nhật trạng thái môn học.");
        }
    }
}
