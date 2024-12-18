package ui;

import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import models.ClassSection;
import models.ClassSession;
import models.Student;
import models.Subject;
import models.Teacher;
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
    private JButton btnSetMidterm, btnSetFinal, btnFinishClass;
    private JButton btnAddSession, btnViewSessions, btnMarkAttendance;

    public TeacherInteractionPanel(Teacher teacher) {
        this.teacher = teacher;
        setLayout(new BorderLayout(10, 10));

        cbClassSection = new JComboBox<>();

        JPanel createClassPanel = new JPanel(new GridLayout(4, 2, 5, 5));
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
        createClassPanel.add(new JLabel(""));
        createClassPanel.add(btnCreateClass);
        // Bạn có thể thêm các trường khác nếu cần

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
        btnAddSession = new JButton("Thêm buổi học");
        btnViewSessions = new JButton("Xem buổi học");
        btnMarkAttendance = new JButton("Điểm danh");
        sessionButtonPanel.add(btnAddSession);
        sessionButtonPanel.add(btnViewSessions);
        sessionButtonPanel.add(btnMarkAttendance);

        studentSessionPanel.add(sessionButtonPanel, BorderLayout.SOUTH);

        btnAddSession.addActionListener(e -> showAddSessionDialog());
        btnViewSessions.addActionListener(e -> viewSessions());
        btnMarkAttendance.addActionListener(e -> markAttendance());

        // Nút nhập điểm
        JPanel gradePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        btnSetMidterm = new JButton("Nhập điểm Midterm");
        btnSetFinal = new JButton("Nhập điểm Final");
        btnFinishClass = new JButton("Kết thúc lớp học");
        gradePanel.add(btnSetMidterm);
        gradePanel.add(btnSetFinal);
        gradePanel.add(btnFinishClass);

        btnSetMidterm.addActionListener(e -> setMidtermScore());
        btnSetFinal.addActionListener(e -> setFinalScore());
        btnFinishClass.addActionListener(e -> finishClass());

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
        cs.setFinished(false);
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
        updateButtonsState();
    }

    private void updateButtonsState() {
        ClassSection cs = (ClassSection) cbClassSection.getSelectedItem();
        if (cs == null) {
            btnSetMidterm.setEnabled(false);
            btnSetFinal.setEnabled(false);
            btnFinishClass.setEnabled(false);
            btnAddSession.setEnabled(false);
            btnMarkAttendance.setEnabled(false);
            return;
        }

        int sessionCount = cs.getClassSessions().size();
        // Midterm: >=4 buổi
        btnSetMidterm.setEnabled(!cs.isFinished() && sessionCount >= 4);
        // Final: >=14 buổi
        btnSetFinal.setEnabled(!cs.isFinished() && sessionCount >= 14);

        // Kết thúc lớp học: từ buổi 14 trở đi
        btnFinishClass.setEnabled(!cs.isFinished() && sessionCount >=14);

        // Không cho thêm buổi nếu đã đủ 15 buổi hoặc lớp đã kết thúc
        btnAddSession.setEnabled(!cs.isFinished() && sessionCount < 15);

        // Cho phép điểm danh nếu đã có ít nhất một buổi học
        btnMarkAttendance.setEnabled(!cs.isFinished() && sessionCount > 0);
    }

    private void showAddSessionDialog() {
        ClassSection cs = (ClassSection) cbClassSection.getSelectedItem();
        if (cs == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp học trước.");
            return;
        }

        if (cs.isFinished()) {
            JOptionPane.showMessageDialog(this, "Lớp đã kết thúc, không thể thêm buổi.");
            return;
        }

        if (cs.getClassSessions().size() >= 15) {
            JOptionPane.showMessageDialog(this, "Đã đủ 15 buổi, không thể thêm buổi mới.");
            return;
        }

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

            for (ClassSession session : cs.getClassSessions()) {
                if (session.getSessionID().equals(sessionID)) {
                    JOptionPane.showMessageDialog(dialog, "Mã buổi học đã tồn tại trong lớp.");
                    return;
                }
            }

            if (!isTimeSlotAvailable(cs, startTime, endTime)) {
                JOptionPane.showMessageDialog(dialog, "Buổi học bị trùng hoặc quá gần hoặc trùng lịch với lớp khác của sinh viên.");
                return;
            }

            ClassSession newSession = new ClassSession(sessionID, startTime, endTime);
            cs.addClassSession(newSession);
            // Sắp xếp buổi học theo thời gian
            cs.getClassSessions().sort(Comparator.comparing(ClassSession::getStartTime));

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
        if (!endTime.isAfter(startTime)) {
            JOptionPane.showMessageDialog(this, "Thời gian kết thúc phải sau thời gian bắt đầu.");
            return false;
        }

        long minutes = Duration.between(startTime, endTime).toMinutes();
        if (minutes < 150 || minutes > 210) { // 2h30p = 150 phút, 3h30p = 210 phút
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
        // Kiểm tra trùng hoặc quá gần (dưới 3 tiếng)
        for (ClassSession existing : cs.getClassSessions()) {
            LocalDateTime exStart = existing.getStartTime();
            LocalDateTime exEnd = existing.getEndTime();

            boolean overlap = newStart.isBefore(exEnd) && newEnd.isAfter(exStart);
            if (overlap) return false;

            // Check gap >=180 phút
            if (!exStart.isBefore(newEnd)) {
                long diff = Duration.between(newEnd, exStart).toMinutes();
                if (diff < 180) return false;
            }

            if (!newStart.isBefore(exEnd)) {
                long diff = Duration.between(exEnd, newStart).toMinutes();
                if (diff < 180) return false;
            }
        }

        // Kiểm tra xem sinh viên trong lớp này có bị trùng giờ với buổi học khác mà họ tham gia hay không
        for (Student st : cs.getEnrolledStudents()) {
            for (ClassSection otherCs : st.getEnrolledClasses()) {
                if (otherCs == cs) continue;
                for (ClassSession otherSession : otherCs.getClassSessions()) {
                    LocalDateTime oStart = otherSession.getStartTime();
                    LocalDateTime oEnd = otherSession.getEndTime();

                    boolean overlap = newStart.isBefore(oEnd) && newEnd.isAfter(oStart);
                    if (overlap) {
                        return false;
                    }
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

        // Loại bỏ chức năng double-click
        // Thay vào đó, người dùng có thể chọn một buổi học và nhấn nút "Điểm danh" để mở bảng điểm danh
        // Tuy nhiên, chúng ta đã thêm nút "Điểm danh" nên có thể loại bỏ phần này
        // Nếu muốn giữ chức năng xem buổi học, chỉ cần hiển thị danh sách

        JOptionPane.showMessageDialog(this, new JScrollPane(sessionTable), "Danh sách buổi học", JOptionPane.INFORMATION_MESSAGE);
    }

    private void markAttendance() {
        ClassSection cs = (ClassSection) cbClassSection.getSelectedItem();
        if (cs == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp học trước.");
            return;
        }

        // Hiển thị danh sách buổi học và cho phép chọn buổi học để điểm danh
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

        // Cho phép người dùng chọn một buổi học từ danh sách
        int selectedOption = JOptionPane.showConfirmDialog(this, new JScrollPane(sessionTable), "Chọn buổi học để điểm danh", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (selectedOption == JOptionPane.OK_OPTION) {
            int selectedRow = sessionTable.getSelectedRow();
            if (selectedRow >= 0) {
                String sessionID = (String) sessionTableModel.getValueAt(selectedRow, 0);
                ClassSession selectedSession = DataManager.findClassSession(cs, sessionID);
                if (selectedSession != null) {
                    JFrame sessionFrame = new JFrame("Điểm danh - Buổi " + sessionID);
                    sessionFrame.setSize(600, 400);
                    sessionFrame.setLocationRelativeTo(this);
                    sessionFrame.add(new ClassSessionPanel(cs, selectedSession));
                    sessionFrame.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy buổi học.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn buổi học để điểm danh.");
            }
        }
    }

    private void setMidtermScore() {
        ClassSection cs = (ClassSection) cbClassSection.getSelectedItem();
        if (cs == null) return;
        if (cs.getClassSessions().size() < 4 || cs.isFinished()) {
            JOptionPane.showMessageDialog(this, "Không thể nhập điểm midterm (cần >=4 buổi và lớp chưa kết thúc).");
            return;
        }

        showGradeDialog(cs, true);
    }

    private void setFinalScore() {
        ClassSection cs = (ClassSection) cbClassSection.getSelectedItem();
        if (cs == null) return;
        if (cs.getClassSessions().size() < 14 || cs.isFinished()) {
            JOptionPane.showMessageDialog(this, "Không thể nhập điểm final (cần >=14 buổi và lớp chưa kết thúc).");
            return;
        }

        showGradeDialog(cs, false);
    }

    private void showGradeDialog(ClassSection cs, boolean midterm) {
        JTable tempTable = new JTable();
        DefaultTableModel tempModel = new DefaultTableModel(new String[]{"Mã SV","Tên"},0);
        tempTable.setModel(tempModel);
        for (Student s : cs.getEnrolledStudents()) {
            // Nếu vắng >3 buổi => fail luôn, không nhập điểm
            int absences=0;
            for (ClassSession session: cs.getClassSessions()) {
                Boolean p = session.getAttendanceRecords().get(s.getID());
                if (p == null || !p) absences++;
            }
            if (absences>3) {
                continue; // Vắng >3 không cho nhập điểm
            }

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

                // Chưa tính passed trừ khi kết thúc lớp
                st.setGradeForClass(cs.getClassCode(), currentMid, currentFinal, st.isPassed(cs.getClassCode()));
                DataManager.saveData();
                JOptionPane.showMessageDialog(this, "Nhập điểm thành công.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Điểm phải là số.");
            }
        }
    }

    private void finishClass() {
        ClassSection cs = (ClassSection) cbClassSection.getSelectedItem();
        if (cs == null) return;
        if (cs.isFinished()) {
            JOptionPane.showMessageDialog(this, "Lớp đã kết thúc rồi.");
            return;
        }

        if (cs.getClassSessions().size() < 14) {
            JOptionPane.showMessageDialog(this, "Phải có ít nhất 14 buổi để kết thúc lớp.");
            return;
        }

        // Khi kết thúc lớp, tính điểm
        cs.setFinished(true);
        DataManager.calculateGrades(cs);

        // Hiển thị bảng thông tin lớp học: điểm danh, số buổi, điểm, trượt/pass
        StringBuilder sb = new StringBuilder("Kết quả lớp: " + cs.getClassCode() + "\n");
        sb.append("Số buổi: ").append(cs.getClassSessions().size()).append("\n");
        for (Student s : cs.getEnrolledStudents()) {
            int absences=0;
            for (ClassSession session : cs.getClassSessions()) {
                Boolean p = session.getAttendanceRecords().get(s.getID());
                if (p != null && !p) absences++;
            }
            double mid = s.getMidterm(cs.getClassCode());
            double fin = s.getFinal(cs.getClassCode());
            boolean pass = s.isPassed(cs.getClassCode());
            sb.append("SV: ").append(s.getID()).append(", Absences: ").append(absences)
                    .append(", Midterm: ").append(mid >=0 ? mid : "N/A")
                    .append(", Final: ").append(fin >=0 ? fin : "N/A")
                    .append(", Passed: ").append(pass).append("\n");
        }

        JOptionPane.showMessageDialog(this, sb.toString(), "Kết thúc lớp học", JOptionPane.INFORMATION_MESSAGE);
        DataManager.saveData();
        loadEnrolledStudents(); // Refresh danh sách
        updateButtonsState();
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
            // Kiểm tra tham gia >=3 buổi?
            int participated =0;
            for (ClassSession session: cs.getClassSessions()) {
                Boolean p = session.getAttendanceRecords().get(s.getID());
                if (p != null && p) participated++;
            }
            if (participated>=3) continue; // Không cho vào bảng nếu không drop được
            tempModel.addRow(new Object[]{s.getID(), s.getName(), s.getEmail()});
        }

        int result = JOptionPane.showConfirmDialog(this, new JScrollPane(tempTable), "Chọn sinh viên để xóa", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            int selectedRow = tempTable.getSelectedRow();
            if (selectedRow >= 0) {
                String studentID = (String) tempModel.getValueAt(selectedRow, 0);
                Student student = DataManager.findStudentByID(studentID);
                if (student != null) {
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
