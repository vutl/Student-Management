package ui;

import javax.swing.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("Ứng dụng Quản lý Sinh viên");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Tạo tab panel
        JTabbedPane tabbedPane = new JTabbedPane();

        // Thêm tab Sinh viên để quản lý sinh viên
        tabbedPane.addTab("Sinh viên", new StudentPanel());

        // Thêm tab Giáo viên để quản lý giáo viên
        tabbedPane.addTab("Giáo viên", new TeacherManagementPanel());

        // Thêm tab Môn học để quản lý môn học
        tabbedPane.addTab("Môn học", new SubjectPanel());

        // Thêm tab Đăng ký để sinh viên hoặc giáo viên đăng nhập
        tabbedPane.addTab("Đăng ký", new LoginPanel(this));

        // Thêm tab vào frame
        add(tabbedPane);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
