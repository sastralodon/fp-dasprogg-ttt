package TicTacToe5Login;


import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class LoginDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private boolean succeeded;

    private HashMap<String, String> users = new HashMap<>();

    public LoginDialog(Frame parent) {
        super(parent, "Login", true);
        users.put("nadya", "1234");
        users.put("admin", "admin");

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        JLabel userLabel = new JLabel("Username: ");
        JLabel passLabel = new JLabel("Password: ");
        usernameField = new JTextField();
        passwordField = new JPasswordField();

        panel.add(userLabel);
        panel.add(usernameField);
        panel.add(passLabel);
        panel.add(passwordField);

        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> {
            if (authenticate(getUsername(), getPassword())) {
                succeeded = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(LoginDialog.this,
                        "Username atau Password salah",
                        "Login",
                        JOptionPane.ERROR_MESSAGE);
                usernameField.setText("");
                passwordField.setText("");
                succeeded = false;
            }
        });

        panel.add(new JLabel());
        panel.add(loginBtn);

        getContentPane().add(panel, BorderLayout.CENTER);
        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    private boolean authenticate(String username, String password) {
        return users.containsKey(username) && users.get(username).equals(password);
    }

    public String getUsername() {
        return usernameField.getText().trim();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public boolean isSucceeded() {
        return succeeded;
    }
}
