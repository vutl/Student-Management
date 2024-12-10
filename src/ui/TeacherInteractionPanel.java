package ui;

import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import models.*;
import utils.DataManager;

public class TeacherInteractionPanel extends JPanel {
    private Teacher teacher;
    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField tfClassCode;
    private JComboBox<Subject> cbSubject;
    private JButton btnCreateClass, btnRemoveStudent, btnLogout;
    private JComboBox<ClassSection> cbClassSection;
    private JTable studentTable;
    private DefaultTableModel studentTableModel;
    private JButton btnSetMidterm, btnSetFinal;

    public TeacherInteractionPanel(Teacher teacher) {
        this.teacher = teacher;
        setLayout(new BorderLayout(10, 10));

        cbClassSection = new JComboBox<>();

        JPanel createClassPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        tfClassCode = new JTextField();
        cbSubject = new JComboBox<>();
        for (Subject s : DataManager.subjectList) {
            cbSubject.addItem(s);
        }
        btnCreateClass = new JButton("Tạo lớp");

        createClassPanel.add(new JLabel("Mã lớp:"));
        createClassPanel.add(tfClassCode);
        createClassPanel.add(new JLabel("Chọn môn học:"));
        createClassPanel.add(cbSubject);
        createClassPanel.add(new JLabel());
        createClassPanel.add(btnCreateClass);

        btnCreateClass.addActionListener(e -> createClass());

        String[] columnNames = {"Mã lớp", "Môn học", "Giáo viên"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        loadTeachingClasses();

        JPanel classSelectionPanel = new JPanel(new BorderLayout(5,5));
        loadTeachingClassesComboBox();
        cbClassSection.addActionListener(e -> loadEnrolledStudents());

        String[] studentColumnNames = {"Mã SV", "Tên", "Email"};
        studentTableModel = new DefaultTableModel(studentColumnNames, 0);
        studentTable = new JTable(studentTableModel);

        JPanel studentSessionPanel = new JPanel(new BorderLayout(5,5));
        JScrollPane studentScrollPane = new JScrollPane(studentTable);
        studentSessionPanel.add(studentScrollPane, BorderLayout.CENTER);

        JPanel sessionButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton btnAddSession = new JButton("Thêm buổi học");
        JButton btnViewSessions = new JButton("Xem buổi học");
        sessionButtonPanel.add(btnAddSession);
        sessionButtonPanel.add(btnViewSessions);

        studentSessionPanel.add(sessionButtonPanel, BorderLayout.SOUTH);

        btnAddSession.addActionListener(e -> showAddSessionDialog());
        btnViewSessions.addActionListener(e -> viewSessions());

        // Thêm nút nhập điểm
        JPanel gradePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        btnSetMidterm = new JButton("Nhập điểm Midterm");
        btnSetFinal = new JButton("Nhập điểm Final");
        gradePanel.add(btnSetMidterm);
        gradePanel.add(btnSetFinal);

        btnSetMidterm.addActionListener(e -> setMidtermScore());
        btnSetFinal.addActionListener(e -> setFinalScore());

        studentSessionPanel.add(gradePanel, BorderLayout.NORTH);

        classSelectionPanel.add(new JLabel("Chọn lớp:"), BorderLayout.NORTH);
        classSelectionPanel.add(cbClassSection, BorderLayout.CENTER);
        classSelectionPanel.add(studentSessionPanel, BorderLayout.SOUTH);

        JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnRemoveStudent = new JButton("Xóa sinh viên");
        btnLogout = new JButton("Đăng xuất");
        bottomButtonPanel.add(btnRemoveStudent);
        bottomButtonPanel.add(btnLogout);

        btnRemoveStudent.addActionListener(e -> removeStudent());
        btnLogout.addActionListener(e -> logout());

        JPanel southContainer = new JPanel(new BorderLayout(5,5));
        southContainer.add(classSelectionPanel, BorderLayout.CENTER);
        southContainer.add(bottomButtonPanel, BorderLayout.SOUTH);

        add(createClassPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(southContainer, BorderLayout.SOUTH);
    }

    private void createClass() {
        String classCode = tfClassCode.getText().trim();
        Subject subject = (Subject) cbSubject.getSelectedItem();

        if (classCode.isEmpty() || subject == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.");
            return;
        }
        if (DataManager.findClassSectionByCode(classCode) != null) {
            JOptionPane.showMessageDialog(this, "Mã lớp đã tồn tại.");
            return;
        }

        int credit = subject.getCredit();

        ClassSection cs = new ClassSection(classCode, subject, teacher, credit);
        teacher.addClass(cs);
        subject.addClassSection(cs);
        DataManager.classSectionList.add(cs);
        DataManager.saveData();
        loadTeachingClasses();
        JOptionPane.showMessageDialog(this, "Tạo lớp thành công.");
        clearFields();
    }

    private void loadTeachingClasses() {
        tableModel.setRowCount(0);
        cbClassSection.removeAllItems();
        for (ClassSection cs : teacher.getTeachingClasses()) {
            tableModel.addRow(new Object[]{cs.getClassCode(), cs.getSubject().getTitle(), cs.getTeacher().getName()});
            cbClassSection.addItem(cs);
        }
    }

    private void loadTeachingClassesComboBox() {
        cbClassSection.removeAllItems();
        for (ClassSection cs : teacher.getTeachingClasses()) {
            cbClassSection.addItem(cs);
        }
    }

    private void loadEnrolledStudents() {
        ClassSection cs = (ClassSection) cbClassSection.getSelectedItem();
        studentTableModel.setRowCount(0);
        if (cs != null) {
            for (Student s : cs.getEnrolledStudents()) {
                studentTableModel.addRow(new Object[]{s.getID(), s.getName(), s.getEmail()});
            }
        }

        // Cập nhật trạng thái nút nhập điểm
        updateGradeButtonsState();
    }

    private void updateGradeButtonsState() {
        ClassSection cs = (ClassSection) cbClassSection.getSelectedItem();
        if (cs == null) {
            btnSetMidterm.setEnabled(false);
            btnSetFinal.setEnabled(false);
            return;
        }
        int sessionCount = cs.getClassSessions().size();
        // Midterm: chỉ sau buổi thứ 4
        btnSetMidterm.setEnabled(sessionCount >= 4);
        // Final: chỉ sau buổi thứ 14
        btnSetFinal.setEnabled(sessionCount >= 14);
    }

    private void showAddSessionDialog() {
        ClassSection cs = (ClassSection) cbClassSection.getSelectedItem();
        if (cs == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp học trước.");
            return;
        }

        // Mã đã có ở trên trong câu trả lời trước, giữ nguyên
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Thêm buổi học", true);
        dialog.setLayout(new GridLayout(4, 2, 5, 5));

        JTextField tfSessionID = new JTextField();
        SpinnerDateModel startModel = new SpinnerDateModel(new Date(), null, null, java.util.Calendar.MINUTE);
        JSpinner startSpinner = new JSpinner(startModel);
        startSpinner.setEditor(new JSpinner.DateEditor(startSpinner, "yyyy-MM-dd HH:mm"));

        SpinnerDateModel endModel = new SpinnerDateModel(new Date(), null, null, java.util.Calendar.MINUTE);
        JSpinner endSpinner = new JSpinner(endModel);
        endSpinner.setEditor(new JSpinner.DateEditor(endSpinner, "yyyy-MM-dd HH:mm"));

        dialog.add(new JLabel("Mã buổi học:"));
        dialog.add(tfSessionID);
        dialog.add(new JLabel("Thời gian bắt đầu:"));
        dialog.add(startSpinner);
        dialog.add(new JLabel("Thời gian kết thúc:"));
        dialog.add(endSpinner);

        JButton btnOK = new JButton("OK");
        JButton btnCancel = new JButton("Hủy");

        dialog.add(btnOK);
        dialog.add(btnCancel);

        btnOK.addActionListener(e -> {
            String sessionID = tfSessionID.getText().trim();
            if (sessionID.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Mã buổi học không được để trống.");
                return;
            }

            Date startDate = (Date) startSpinner.getValue();
            Date endDate = (Date) endSpinner.getValue();
            LocalDateTime startTime = LocalDateTime.ofInstant(startDate.toInstant(), ZoneId.systemDefault());
            LocalDateTime endTime = LocalDateTime.ofInstant(endDate.toInstant(), ZoneId.systemDefault());

            if (!validateSessionTime(startTime, endTime)) {
                return;
            }

            ClassSection cs2 = (ClassSection) cbClassSection.getSelectedItem();
            for (ClassSession session : cs2.getClassSessions()) {
                if (session.getSessionID().equals(sessionID)) {
                    JOptionPane.showMessageDialog(dialog, "Mã buổi học đã tồn tại trong lớp.");
                    return;
                }
            }

            if (!isTimeSlotAvailable(cs2, startTime, endTime)) {
                JOptionPane.showMessageDialog(dialog, "Buổi học bị trùng hoặc quá gần (phải cách ít nhất 3 tiếng) với buổi học khác.");
                return;
            }

            ClassSession session = new ClassSession(sessionID, startTime, endTime);
            cs2.addClassSession(session);
            DataManager.saveData();
            JOptionPane.showMessageDialog(dialog, "Thêm buổi học thành công.");
            dialog.dispose();
            loadEnrolledStudents();
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private boolean validateSessionTime(LocalDateTime startTime, LocalDateTime endTime) {
        // Như cũ
        if (!endTime.isAfter(startTime)) {
            JOptionPane.showMessageDialog(this, "Thời gian kết thúc phải sau thời gian bắt đầu.");
            return false;
        }

        long minutes = Duration.between(startTime, endTime).toMinutes();
        if (minutes < 150 || minutes > 210) {
            JOptionPane.showMessageDialog(this, "Thời gian buổi học phải từ 2h30p đến 3h30p.");
            return false;
        }

        int startHour = startTime.getHour();
        int startMinute = startTime.getMinute();
        int endHour = endTime.getHour();
        int endMinute = endTime.getMinute();

        boolean startValid = (startHour > 6 || (startHour == 6 && startMinute >= 45)) && (startHour < 17 || (startHour == 17 && startMinute == 0));
        boolean endValid = (endHour > 6 || (endHour == 6 && endMinute >= 45)) && (endHour < 17 || (endHour == 17 && endMinute == 0));

        if (!startValid || !endValid) {
            JOptionPane.showMessageDialog(this, "Giờ học phải trong khoảng từ 6:45 đến 17:00.");
            return false;
        }

        return true;
    }

    private boolean isTimeSlotAvailable(ClassSection cs, LocalDateTime newStart, LocalDateTime newEnd) {
        for (ClassSession existing : cs.getClassSessions()) {
            LocalDateTime exStart = existing.getStartTime();
            LocalDateTime exEnd = existing.getEndTime();

            boolean overlap = newStart.isBefore(exEnd) && newEnd.isAfter(exStart);
            if (overlap) {
                return false;
            }

            // Kiểm tra khoảng cách >=180 phút
            if (!exStart.isBefore(newEnd)) {
                long diff = Duration.between(newEnd, exStart).toMinutes();
                if (diff < 180) {
                    return false;
                }
            }

            if (!newStart.isBefore(exEnd)) {
                long diff = Duration.between(exEnd, newStart).toMinutes();
                if (diff < 180) {
                    return false;
                }
            }
        }
        return true;
    }

    private void viewSessions() {
        ClassSection cs = (ClassSection) cbClassSection.getSelectedItem();
        if (cs == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp học trước.");
            return;
        }

        String[] sessionColumnNames = {"Mã buổi học", "Thời gian bắt đầu", "Thời gian kết thúc"};
        DefaultTableModel sessionTableModel = new DefaultTableModel(sessionColumnNames, 0);
        JTable sessionTable = new JTable(sessionTableModel);

        for (ClassSession session : cs.getClassSessions()) {
            sessionTableModel.addRow(new Object[]{
                    session.getSessionID(),
                    session.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    session.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            });
        }

        int selectedOption = JOptionPane.showConfirmDialog(this, new JScrollPane(sessionTable), "Chọn buổi học để xem điểm danh", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (selectedOption == JOptionPane.OK_OPTION) {
            int selectedRow = sessionTable.getSelectedRow();
            if (selectedRow >= 0) {
                String sessionID = (String) sessionTableModel.getValueAt(selectedRow, 0);
                ClassSession session = DataManager.findClassSession(cs, sessionID);
                if (session != null) {
                    JFrame sessionFrame = new JFrame("Điểm danh - Buổi " + sessionID);
                    sessionFrame.setSize(600, 400);
                    sessionFrame.setLocationRelativeTo(null);
                    sessionFrame.add(new ClassSessionPanel(cs, session));
                    sessionFrame.setVisible(true);
                }
            }
        }
    }

    private void setMidtermScore() {
        ClassSection cs = (ClassSection) cbClassSection.getSelectedItem();
        if (cs == null) return;
        if (cs.getClassSessions().size() < 4) {
            JOptionPane.showMessageDialog(this, "Chưa thể nhập điểm midterm (cần >=4 buổi).");
            return;
        }

        showGradeDialog(cs, true);
    }

    private void setFinalScore() {
        ClassSection cs = (ClassSection) cbClassSection.getSelectedItem();
        if (cs == null) return;
        if (cs.getClassSessions().size() < 14) {
            JOptionPane.showMessageDialog(this, "Chưa thể nhập điểm final (cần >=14 buổi).");
            return;
        }

        showGradeDialog(cs, false);
    }

    private void showGradeDialog(ClassSection cs, boolean midterm) {
        // Chọn sinh viên
        JTable tempTable = new JTable();
        DefaultTableModel tempModel = new DefaultTableModel(new String[]{"Mã SV","Tên"},0);
        tempTable.setModel(tempModel);
        for (Student s : cs.getEnrolledStudents()) {
            tempModel.addRow(new Object[]{s.getID(), s.getName()});
        }

        int result = JOptionPane.showConfirmDialog(this, new JScrollPane(tempTable), "Chọn sinh viên để nhập điểm", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            int selectedRow = tempTable.getSelectedRow();
            if (selectedRow < 0) return;

            String studentID = (String) tempModel.getValueAt(selectedRow, 0);
            Student st = DataManager.findStudentByID(studentID);
            if (st == null) return;

            String scoreType = midterm ? "Midterm" : "Final";
            String input = JOptionPane.showInputDialog(this, "Nhập điểm " + scoreType + " (0-100):");
            if (input == null) return;
            try {
                double score = Double.parseDouble(input);
                if (score < 0 || score > 100) {
                    JOptionPane.showMessageDialog(this, "Điểm phải từ 0-100.");
                    return;
                }

                double currentMid = st.getMidterm(cs.getClassCode());
                double currentFinal = st.getFinal(cs.getClassCode());
                if (midterm) {
                    currentMid = score;
                } else {
                    currentFinal = score;
                }

                // passed tạm thời tính sau khi đủ 15 buổi
                boolean passed = st.isPassed(cs.getClassCode());
                st.setGradeForClass(cs.getClassCode(), currentMid, currentFinal, passed);

                DataManager.saveData();
                JOptionPane.showMessageDialog(this, "Nhập điểm thành công.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Điểm phải là số.");
            }
        }
    }

    private void removeStudent() {
        ClassSection cs = (ClassSection) cbClassSection.getSelectedItem();
        if (cs == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp học.");
            return;
        }

        JTable tempTable = new JTable();
        DefaultTableModel tempModel = new DefaultTableModel(new String[]{"Mã SV", "Tên", "Email"}, 0);
        tempTable.setModel(tempModel);
        for (Student s : cs.getEnrolledStudents()) {
            tempModel.addRow(new Object[]{s.getID(), s.getName(), s.getEmail()});
        }

        int result = JOptionPane.showConfirmDialog(this, new JScrollPane(tempTable), "Chọn sinh viên để xóa", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            int selectedRow = tempTable.getSelectedRow();
            if (selectedRow >= 0) {
                String studentID = (String) tempModel.getValueAt(selectedRow, 0);
                Student student = DataManager.findStudentByID(studentID);
                if (student != null) {
                    // Check if student participated >=3 sessions?
                    int participated = 0;
                    for (ClassSession session : cs.getClassSessions()) {
                        Boolean present = session.getAttendanceRecords().get(student.getID());
                        if (present != null && present) participated++;
                    }
                    if (participated >= 3) {
                        JOptionPane.showMessageDialog(this, "Sinh viên đã tham gia >=3 buổi, không thể drop.");
                        return;
                    }

                    cs.removeStudent(student);
                    student.removeClass(cs);
                    student.setRemainingCredits(student.getRemainingCredits() + cs.getCredit());
                    DataManager.saveData();
                    JOptionPane.showMessageDialog(this, "Đã xóa sinh viên khỏi lớp.");
                    loadEnrolledStudents();
                }
            }
        }
    }

    private void logout() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame instanceof MainFrame) {
            ((MainFrame) frame).showLoginTab();
        }
    }

    private void clearFields() {
        tfClassCode.setText("");
        cbSubject.setSelectedIndex(0);
    }
}
