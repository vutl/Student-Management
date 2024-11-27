package ui;

import models.*;
import utils.DataManager;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {
    private JFrame parentFrame;
    private JTextField tfID;
    private JButton btnLogin;

    public LoginPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
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
            // Đăng nhập với tư cách sinh viên
            JOptionPane.showMessageDialog(this, "Đăng nhập thành công với tư cách sinh viên.");
            parentFrame.setContentPane(new StudentInteractionPanel(student));
            parentFrame.revalidate();
        } else {
            Teacher teacher = DataManager.findTeacherByID(id);
            if (teacher != null) {
                // Đăng nhập với tư cách giáo viên
                JOptionPane.showMessageDialog(this, "Đăng nhập thành công với tư cách giáo viên.");
                parentFrame.setContentPane(new TeacherInteractionPanel(teacher));
                parentFrame.revalidate();
            } else {
                JOptionPane.showMessageDialog(this, "ID không tồn tại.");
            }
        }
    }
}
