package ui;

import javax.swing.*;
import models.Student;
import models.Teacher;
import utils.DataManager;

public class MainFrame extends JFrame {
    private JTabbedPane tabbedPane;
    private LoginPanel loginPanel;
    private AdminLoginPanel adminLoginPanel;
    private StudentPanel studentPanel;
    private SubjectPanel subjectPanel;
    private TeacherManagementPanel teacherManagementPanel;

    private JPanel adminLogoutPanel;
    private JButton btnAdminLogout;

    public MainFrame() {
        setTitle("Quản lý Học kì");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        DataManager.loadData();

        tabbedPane = new JTabbedPane();

        loginPanel = new LoginPanel(this);
        adminLoginPanel = new AdminLoginPanel(this);
        studentPanel = new StudentPanel();
        subjectPanel = new SubjectPanel();
        teacherManagementPanel = new TeacherManagementPanel();

        tabbedPane.addTab("Đăng nhập", loginPanel);
        tabbedPane.addTab("Admin Login", adminLoginPanel);

        tabbedPane.addTab("Sinh viên", studentPanel);
        tabbedPane.addTab("Môn học", subjectPanel);
        tabbedPane.addTab("Quản lý giáo viên", teacherManagementPanel);

        // Admin logout panel
        adminLogoutPanel = new JPanel();
        btnAdminLogout = new JButton("Đăng xuất Admin");
        btnAdminLogout.addActionListener(e->adminLogout());
        adminLogoutPanel.add(btnAdminLogout);

        // Add Admin Logout tab
        tabbedPane.addTab("Admin Logout", adminLogoutPanel);

        setAdminTabsVisible(false);
        setAdminLogoutTabVisible(false);

        add(tabbedPane);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void showStudentInteractionPanel(Student student) {
        tabbedPane.removeAll();
        tabbedPane.add("Sinh viên Tương tác", new StudentInteractionPanel(student));
    }

    public void showTeacherInteractionPanel(Teacher teacher) {
        tabbedPane.removeAll();
        tabbedPane.add("Giáo viên Tương tác", new TeacherInteractionPanel(teacher));
    }

    public void showLoginTab() {
        tabbedPane.removeAll();

        loginPanel = new LoginPanel(this);
        adminLoginPanel = new AdminLoginPanel(this);
        studentPanel = new StudentPanel();
        subjectPanel = new SubjectPanel();
        teacherManagementPanel = new TeacherManagementPanel();

        adminLogoutPanel = new JPanel();
        btnAdminLogout = new JButton("Đăng xuất Admin");
        btnAdminLogout.addActionListener(e->adminLogout());
        adminLogoutPanel.add(btnAdminLogout);

        tabbedPane.addTab("Đăng nhập", loginPanel);
        tabbedPane.addTab("Admin Login", adminLoginPanel);
        tabbedPane.addTab("Sinh viên", studentPanel);
        tabbedPane.addTab("Môn học", subjectPanel);
        tabbedPane.addTab("Quản lý giáo viên", teacherManagementPanel);
        tabbedPane.addTab("Admin Logout", adminLogoutPanel);

        setAdminTabsVisible(false);
        setAdminLogoutTabVisible(false);

        tabbedPane.setSelectedIndex(0);
    }

    public void setAdminTabsVisible(boolean visible) {
        // 0=Đăng nhập,1=Admin Login,2=Sinh viên,3=Môn học,4=Quản lý giáo viên,5=Admin Logout
        tabbedPane.setEnabledAt(2, visible);
        tabbedPane.setEnabledAt(3, visible);
        tabbedPane.setEnabledAt(4, visible);
    }

    public void setAdminLogoutTabVisible(boolean visible) {
        // Admin logout tab ở index 5
        tabbedPane.setEnabledAt(5, visible);
    }

    public void adminLoggedIn() {
        setAdminTabsVisible(true);
        setAdminLogoutTabVisible(true);
        tabbedPane.setSelectedIndex(2);
    }

    public void adminLogout() {
        // Quay lại trạng thái ban đầu
        showLoginTab();
    }
}
