package ui;

import javax.swing.*;
import java.awt.*;
import models.Student;
import models.Teacher;

public class AdminLoginPanel extends JPanel {
    private MainFrame mainFrame;
    private JTextField tfUser;
    private JPasswordField pfPass;
    private JButton btnLogin;

    public AdminLoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel lblUser = new JLabel("Admin User:");
        tfUser = new JTextField(15);
        JLabel lblPass = new JLabel("Admin Pass:");
        pfPass = new JPasswordField(15);
        btnLogin = new JButton("Đăng nhập Admin");

        gbc.insets = new Insets(5,5,5,5);
        gbc.gridx=0; gbc.gridy=0;
        add(lblUser,gbc);
        gbc.gridy=1;
        add(tfUser,gbc);
        gbc.gridy=2;
        add(lblPass,gbc);
        gbc.gridy=3;
        add(pfPass,gbc);
        gbc.gridy=4;
        add(btnLogin,gbc);

        btnLogin.addActionListener(e->login());
    }

    private void login() {
        String user = tfUser.getText().trim();
        String pass = new String(pfPass.getPassword());

        if (user.equals("admin") && pass.equals("123")) {
            JOptionPane.showMessageDialog(this, "Admin đăng nhập thành công.");
            mainFrame.adminLoggedIn();
        } else {
            JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu admin.");
        }
    }
}
