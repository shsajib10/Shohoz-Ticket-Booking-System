import java.awt.event.*;
import javax.swing.*;

public class ShohozRegistration extends JFrame implements ActionListener {
    JPanel panel;
    JLabel namlbl, passlbl, acclbl;
    JTextField namfld;
    JPasswordField passfld;
    JButton signupbtn, backbtn, logiButton;
    boolean registrationSuccess = false;

    public ShohozRegistration() {
        this.setSize(850, 650);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new JPanel();
        panel.setLayout(null);

        namlbl = new JLabel("Name");
        namlbl.setBounds(200, 100, 100, 50);
        panel.add(namlbl);

        namfld = new JTextField();
        namfld.setBounds(350, 100, 200, 50);
        panel.add(namfld);

        passlbl = new JLabel("Password");
        passlbl.setBounds(200, 170, 100, 50);
        panel.add(passlbl);

        passfld = new JPasswordField();
        passfld.setBounds(350, 170, 200, 50);
        panel.add(passfld);

        signupbtn = new JButton("SignUp");
        signupbtn.setBounds(250, 250, 100, 30);
        signupbtn.addActionListener(this);
        panel.add(signupbtn);

        backbtn = new JButton("Back");
        backbtn.setBounds(400, 250, 100, 30);
        backbtn.addActionListener(this);
        panel.add(backbtn);

        acclbl = new JLabel("Already have an account?");
        acclbl.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 17));
        acclbl.setBounds(200, 350, 250, 50);
        panel.add(acclbl);

        logiButton = new JButton("Login");
        logiButton.setBounds(400, 360, 100, 30);
        logiButton.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 16));
        logiButton.addActionListener(this);
        panel.add(logiButton);

        this.add(panel);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == backbtn) {
            this.setVisible(false);
            new ShohozLogin().setVisible(true);
        } else if (ae.getSource() == signupbtn) {
            String username = namfld.getText();
            String password = new String(passfld.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Fill up all fields.");
                return;
            }

            if (!username.matches("^[a-zA-Z0-9]{3,20}$")) {
                JOptionPane.showMessageDialog(null, "Username must be 3-20 alphanumeric characters.");
                return;
            }

            if (password.length() < 6) {
                JOptionPane.showMessageDialog(null, "Password must be at least 6 characters.");
                return;
            }

            ShohozAccount a1 = new ShohozAccount(username, password);
            if (a1.isUsernameTaken(username)) {
                JOptionPane.showMessageDialog(null, "Username already exists. Try another user name.");
                return;
            }

            a1.addShohozAccount();
            registrationSuccess = true;
            JOptionPane.showMessageDialog(null, "Registration Successful! Please click Login to proceed.");
        } else if (ae.getSource() == logiButton) {
            if (registrationSuccess) {
                this.setVisible(false);
                new ShohozLogin().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "Please register first before logging in.");
            }
        }
    }

    public static void main(String[] args) {
        ShohozRegistration s1 = new ShohozRegistration();
        s1.setVisible(true);
    }
}
