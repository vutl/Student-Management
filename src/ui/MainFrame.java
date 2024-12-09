package ui;

import utils.DataManager;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private JTabbedPane tabbedPane;
    private LoginPanel loginPanel;
    private StudentInteractionPanel studentPanel;
    private TeacherInteractionPanel teacherPanel;
    private SubjectPanel subjectPanel;

    public MainFrame() {
        setTitle("Quản lý Học kì");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Tạo tab panel
        tabbedPane = new JTabbedPane();

        // Khởi tạo các panel nhưng không thêm vào tabbedPane ngay
        subjectPanel = new SubjectPanel();

        // Tạo và thêm tab Đăng nhập đầu tiên
        loginPanel = new LoginPanel(this); // Pass the MainFrame to LoginPanel
        tabbedPane.addTab("Đăng nhập", loginPanel);

        // Thêm tab vào frame (chỉ có Đăng nhập lúc đầu)
        add(tabbedPane);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Phương thức để hiển thị các tab sau khi đăng nhập
    public void showTabsAfterLogin(boolean isTeacher) {
        // Xóa tất cả các tab hiện tại
        tabbedPane.removeAll();

        if (isTeacher) {
            // Lấy đối tượng Teacher từ DataManager
            TeacherInteractionPanel teacherPanel = new TeacherInteractionPanel(DataManager.findTeacherByID(DataManager.currentLoggedInID));
            tabbedPane.addTab("Giáo viên", teacherPanel);
            tabbedPane.addTab("Môn học", subjectPanel);
        } else {
            // Lấy đối tượng Student từ DataManager
            StudentInteractionPanel studentPanel = new StudentInteractionPanel(DataManager.findStudentByID(DataManager.currentLoggedInID));
            tabbedPane.addTab("Sinh viên", studentPanel);
            tabbedPane.addTab("Môn học", subjectPanel);
        }

        // Thêm tab "Đăng xuất"
        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.addActionListener(e -> showLoginTab());

        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.add(btnLogout);

        tabbedPane.addTab("Đăng xuất", logoutPanel);
        tabbedPane.setSelectedIndex(0); // Chọn tab đầu tiên sau khi đăng nhập
    }

    // Phương thức để quay lại giao diện đăng nhập sau khi đăng xuất
    public void showLoginTab() {
        // Xóa tất cả các tab hiện tại
        tabbedPane.removeAll();

        // Reset currentLoggedInID
        DataManager.currentLoggedInID = null;

        // Tạo lại LoginPanel với MainFrame hiện tại
        loginPanel = new LoginPanel(this);
        tabbedPane.addTab("Đăng nhập", loginPanel);
        tabbedPane.setSelectedIndex(0);
    }
}
