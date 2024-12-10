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

    public MainFrame() {
        setTitle("Quản lý Học kì");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Tải dữ liệu
        DataManager.loadData();

        tabbedPane = new JTabbedPane();

        // Khởi tạo panel
        loginPanel = new LoginPanel(this);
        adminLoginPanel = new AdminLoginPanel(this);
        studentPanel = new StudentPanel();
        subjectPanel = new SubjectPanel();
        teacherManagementPanel = new TeacherManagementPanel();

        // Thêm tab
        tabbedPane.addTab("Đăng nhập", loginPanel);
        tabbedPane.addTab("Admin Login", adminLoginPanel);

        // Thêm các tab quản lý nhưng ẩn (hoặc disable) cho đến khi admin đăng nhập
        // Ban đầu ta không add 3 tab này, sau khi admin login mới add
        // Hoặc add sẵn nhưng disable:
        // add sẵn, disable:
        tabbedPane.addTab("Sinh viên", studentPanel);
        tabbedPane.addTab("Môn học", subjectPanel);
        tabbedPane.addTab("Quản lý giáo viên", teacherManagementPanel);

        // Admin chưa login => ẩn 3 tab
        setAdminTabsVisible(false);

        add(tabbedPane);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Hiển thị StudentInteractionPanel sau khi sinh viên đăng nhập
    public void showStudentInteractionPanel(Student student) {
        // Xóa tất cả các tab, chỉ để tab StudentInteractionPanel
        // Thay đổi logic: Khi sinh viên login, chỉ cần hiển thị 1 tab StudentInteractionPanel
        tabbedPane.removeAll();
        tabbedPane.add("Sinh viên Tương tác", new StudentInteractionPanel(student));
    }

    // Hiển thị TeacherInteractionPanel sau khi giáo viên đăng nhập
    public void showTeacherInteractionPanel(Teacher teacher) {
        // Xóa tất cả, chỉ TeacherInteractionPanel
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

        tabbedPane.addTab("Đăng nhập", loginPanel);
        tabbedPane.addTab("Admin Login", adminLoginPanel);

        tabbedPane.addTab("Sinh viên", studentPanel);
        tabbedPane.addTab("Môn học", subjectPanel);
        tabbedPane.addTab("Quản lý giáo viên", teacherManagementPanel);

        setAdminTabsVisible(false);

        tabbedPane.setSelectedIndex(0);
    }

    public void setAdminTabsVisible(boolean visible) {
        // visible=true => enable 3 tab (Sinh viên, Môn học, Quản lý giáo viên)
        // visible=false => disable
        // Tab thứ tự: 0=Đăng nhập, 1=Admin Login, 2=Sinh viên, 3=Môn học, 4=Quản lý giáo viên
        tabbedPane.setEnabledAt(2, visible);
        tabbedPane.setEnabledAt(3, visible);
        tabbedPane.setEnabledAt(4, visible);
    }

    public void adminLoggedIn() {
        // Admin login success
        setAdminTabsVisible(true);
        tabbedPane.setSelectedIndex(2); // Chuyển sang tab "Sinh viên" hoặc tùy ý
    }
}
