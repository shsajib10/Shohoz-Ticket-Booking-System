import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ShohozLogin extends JFrame implements MouseListener, ActionListener {
    JLabel namelbl, passlbl, acc, imglbl;
    JTextField nametxtfld;
    JPasswordField passfld;
    JButton lginbuton, sinbuton, backbuton;
    Color bgclr, lblclr, buttonColor;
    Font lblfont;
    JPanel panel;
    ImageIcon img;

    public ShohozLogin() {

        this.setSize(850, 650);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        bgclr = new Color(179, 255, 214);
        lblclr = new Color(231, 255, 212);
        buttonColor = new Color(18, 186, 37);
        lblfont = new Font("Quicksand", Font.BOLD, 15);

        panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(bgclr);

        namelbl = new JLabel("Name: ");
        namelbl.setBounds(450, 100, 90, 50);
        namelbl.setForeground(Color.BLACK);
        namelbl.setFont(lblfont);
        panel.add(namelbl);

        nametxtfld = new JTextField();
        nametxtfld.setBounds(550, 100, 150, 40);
        panel.add(nametxtfld);

        passlbl = new JLabel("Password: ");
        passlbl.setBounds(450, 150, 90, 50);
        passlbl.setForeground(Color.BLACK);
        passlbl.setFont(lblfont);
        panel.add(passlbl);

        passfld = new JPasswordField();
        passfld.setBounds(550, 150, 150, 40);
        passfld.setEchoChar('*');
        panel.add(passfld);

        lginbuton = new JButton("Log in");
        lginbuton.setBounds(450, 250, 100, 30);
        lginbuton.setFont(lblfont);
        lginbuton.setForeground(Color.BLACK);
        lginbuton.setBackground(buttonColor);
        lginbuton.addMouseListener(this);
        lginbuton.addActionListener(this);
        panel.add(lginbuton);

        backbuton = new JButton("Exit");
        backbuton.setBounds(650, 250, 100, 30);
        backbuton.setFont(lblfont);
        backbuton.setForeground(Color.BLACK);
        backbuton.setBackground(buttonColor);
        backbuton.addMouseListener(this);
        backbuton.addActionListener(this);
        panel.add(backbuton);

        acc = new JLabel("Don't have an account?");
        acc.setBounds(450, 300, 200, 50);
        acc.setForeground(Color.BLACK);
        acc.setFont(lblfont);
        panel.add(acc);

        sinbuton = new JButton("Sign up");
        sinbuton.setBounds(650, 310, 100, 30);
        sinbuton.setFont(lblfont);
        sinbuton.setForeground(Color.BLACK);
        sinbuton.setBackground(buttonColor);
        sinbuton.addMouseListener(this);
        sinbuton.addActionListener(this);
        panel.add(sinbuton);

        img = new ImageIcon("shohoz_logo_new.png");
        imglbl = new JLabel(img);
        imglbl.setBounds(10, 10, 178, 38);
        panel.add(imglbl);

        this.add(panel);
    }

    public void mouseClicked(MouseEvent me) {
    }

    public void mousePressed(MouseEvent me) {
    }

    public void mouseReleased(MouseEvent me) {
    }

    public void mouseEntered(MouseEvent me) {
        JButton btn = (JButton) me.getSource();
        btn.setBackground(Color.GREEN);
        btn.setForeground(Color.WHITE);
    }

    public void mouseExited(MouseEvent me) {
        JButton btn = (JButton) me.getSource();
        btn.setBackground(buttonColor);
        btn.setForeground(Color.BLACK);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == backbuton) {
            System.exit(0);
        } else if (ae.getSource() == lginbuton) {
            String s1 = nametxtfld.getText();
            String s2 = new String(passfld.getPassword());
            if (s1.isEmpty() || s2.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.");
                return;
            }
            ShohozAccount a1 = new ShohozAccount(s1, s2);
            if (a1.getShohozAccount(s1, s2)) {
                new ShohozHome().setVisible(true);
                this.setVisible(false);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Username or Password");
            }
        } else if (ae.getSource() == sinbuton) {
            this.setVisible(false);
            new ShohozRegistration().setVisible(true);
        }
    }

    public static void main(String[] args) {
        ShohozLogin s1 = new ShohozLogin();
        s1.setVisible(true);
    }

}
