package ui;

import java.awt.*;
import javax.swing.*;
import models.Student;
import models.Teacher;
import utils.DataManager;

public class LoginPanel extends JPanel {
    private MainFrame mainFrame;
    private JTextField tfID;
    private JButton btnLogin;

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel lblID = new JLabel("Nhập ID của bạn (Sinh viên hoặc Giáo viên):");
        tfID = new JTextField(15);
        btnLogin = new JButton("Đăng nhập");

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0; gbc.gridy = 0;
        add(lblID, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(tfID, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        add(btnLogin, gbc);

        btnLogin.addActionListener(e -> login());
    }

    private void login() {
        String id = tfID.getText().trim();
        Student student = DataManager.findStudentByID(id);
        if (student != null) {
            DataManager.currentLoggedInID = id;
            JOptionPane.showMessageDialog(this, "Đăng nhập thành công với tư cách sinh viên.");
            mainFrame.showStudentInteractionPanel(student);
        } else {
            Teacher teacher = DataManager.findTeacherByID(id);
            if (teacher != null) {
                DataManager.currentLoggedInID = id;
                JOptionPane.showMessageDialog(this, "Đăng nhập thành công với tư cách giáo viên.");
                mainFrame.showTeacherInteractionPanel(teacher);
            } else {
                JOptionPane.showMessageDialog(this, "ID không tồn tại.");
            }
        }
    }
}
