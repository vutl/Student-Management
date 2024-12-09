package ui;

import java.awt.*;
import javax.swing.*;
import models.Student;
import models.Teacher;
import utils.DataManager;

public class MainFrame extends JFrame {
    private JTabbedPane tabbedPane;
    private LoginPanel loginPanel;
    private StudentPanel studentPanel;
    private SubjectPanel subjectPanel;
    private TeacherManagementPanel teacherManagementPanel;

    public MainFrame() {
        setTitle("Quản lý Học kì");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Gọi loadData để nạp dữ liệu từ file
        DataManager.loadData();

        tabbedPane = new JTabbedPane();

        // Khởi tạo các panel
        loginPanel = new LoginPanel(this);
        studentPanel = new StudentPanel();
        subjectPanel = new SubjectPanel();
        teacherManagementPanel = new TeacherManagementPanel();

        // Thêm vào tabbedPane
        tabbedPane.addTab("Đăng nhập", loginPanel);
        tabbedPane.addTab("Sinh viên", studentPanel);
        tabbedPane.addTab("Môn học", subjectPanel);
        tabbedPane.addTab("Quản lý giáo viên", teacherManagementPanel);

        add(tabbedPane, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Hiển thị StudentInteractionPanel sau khi sinh viên đăng nhập
    public void showStudentInteractionPanel(Student student) {
        tabbedPane.removeAll();
        StudentInteractionPanel sip = new StudentInteractionPanel(student);

        // Chỉ thêm StudentInteractionPanel
        tabbedPane.addTab("Sinh viên", sip);
        addLogoutTab();
        tabbedPane.setSelectedComponent(sip);
    }

    // Hiển thị TeacherInteractionPanel sau khi giáo viên đăng nhập
    public void showTeacherInteractionPanel(Teacher teacher) {
        tabbedPane.removeAll();
        TeacherInteractionPanel tip = new TeacherInteractionPanel(teacher);

        // Chỉ thêm TeacherInteractionPanel
        tabbedPane.addTab("Giáo viên", tip);
        addLogoutTab();
        tabbedPane.setSelectedComponent(tip);
    }

    private void addLogoutTab() {
        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.addActionListener(e -> showLoginTab());

        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.add(btnLogout);

        tabbedPane.addTab("Đăng xuất", logoutPanel);
    }

    public void showLoginTab() {
        tabbedPane.removeAll();
        DataManager.currentLoggedInID = null;
    
        // Khởi tạo lại các panel như ban đầu
        loginPanel = new LoginPanel(this);
        studentPanel = new StudentPanel();
        subjectPanel = new SubjectPanel();
        teacherManagementPanel = new TeacherManagementPanel();
    
        // Thêm lại tất cả các tab như lúc mới mở app
        tabbedPane.addTab("Đăng nhập", loginPanel);
        tabbedPane.addTab("Sinh viên", studentPanel);
        tabbedPane.addTab("Môn học", subjectPanel);
        tabbedPane.addTab("Quản lý giáo viên", teacherManagementPanel);
    
        tabbedPane.setSelectedIndex(0); // Chọn tab Đăng nhập
    }
    

    public static void main(String[] args) {
        new MainFrame();
    }
}
