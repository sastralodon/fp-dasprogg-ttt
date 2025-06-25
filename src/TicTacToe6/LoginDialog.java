package TicTacToe6;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginDialog extends JDialog {
    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JLabel lbMessage;
    private boolean succeeded;

    public LoginDialog(Frame parent) {
        super(parent, "Welcome to Tic Tac Toe 🎮", true);

        // Panel utama
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Gambar logo (opsional, kalau punya logo game)
        JLabel logo = new JLabel(new ImageIcon("images/logo.png")); // ubah sesuai gambar kamu
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(logo, BorderLayout.NORTH);

        // Panel form input
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        formPanel.add(new JLabel("Username:"));
        tfUsername = new JTextField(20);
        formPanel.add(tfUsername);

        formPanel.add(new JLabel("Password:"));
        pfPassword = new JPasswordField(20);
        formPanel.add(pfPassword);

        lbMessage = new JLabel(" ");
        lbMessage.setForeground(Color.RED);
        formPanel.add(lbMessage);

        panel.add(formPanel, BorderLayout.CENTER);

        // Tombol login
        JPanel buttonPanel = new JPanel();
        JButton btnLogin = new JButton("Login");
        btnLogin.setBackground(new Color(64, 154, 225));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(btnLogin);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Action tombol login
        btnLogin.addActionListener(e -> {
            String user = tfUsername.getText().trim();
            String pass = new String(pfPassword.getPassword());
            try {
                if (authenticate(user, pass)) {
                    succeeded = true;
                    dispose();
                } else {
                    lbMessage.setText("Username atau password salah!");
                    pfPassword.setText("");
                    succeeded = false;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(LoginDialog.this,
                        "Koneksi ke database gagal!", "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        setContentPane(panel);
        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    private boolean authenticate(String username, String password) throws ClassNotFoundException {
        String host = "mysql-1d2252ab-maryunani52-0a0e.d.aivencloud.com";
        String port = "24911";
        String dbName = "tictactoedb";
        String dbUser = "avnadmin";
        String dbPass = "AVNS_7r_uUr9IcdVJJaOIlJQ";

        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://" + host + ":" + port + "/" + dbName + "?sslmode=require", dbUser, dbPass);
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT password FROM user_game WHERE user_name = ?")) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String correctPass = rs.getString("password");
                return password.equals(correctPass);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isSucceeded() {
        return succeeded;
    }
}
