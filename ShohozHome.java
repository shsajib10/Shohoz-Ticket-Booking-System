import java.awt.Color;
import java.awt.event.*;
import javax.swing.*;

public class ShohozHome extends JFrame implements ActionListener {
    JLabel welcomeLabel,imglbl;
    JButton BusButton, TrainButton, AirTickButton, LogoutButton;
    JPanel panel;
    ImageIcon img;
    Color homecolor;

    public ShohozHome() {
        super("Shohoz Home");
        this.setSize(1000, 600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        homecolor=new Color(0x555879);

        panel = new JPanel();
        panel.setLayout(null);
        panel.setOpaque(true);
        panel.setBackground(homecolor);

       

        ImageIcon busIcon = new ImageIcon("icons/bus.png");
        ImageIcon trainIcon = new ImageIcon("icons/train.png");
        ImageIcon airIcon = new ImageIcon("icons/air.png");
        ImageIcon logoutIcon = new ImageIcon("icons/logout.png");

        welcomeLabel = new JLabel("Welcome to Shohoz");
        welcomeLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 28));
        welcomeLabel.setBounds(370, 30, 400, 50);
        panel.add(welcomeLabel);

        // Ticket buttons in one row
        BusButton = new JButton("Bus Ticket", busIcon);
        BusButton.setBounds(150, 150, 200, 80);
        BusButton.addActionListener(this);
        panel.add(BusButton);

        TrainButton = new JButton("Train Ticket", trainIcon);
        TrainButton.setBounds(400, 150, 200, 80);
        TrainButton.addActionListener(this);
        panel.add(TrainButton);

        AirTickButton = new JButton("Air Ticket", airIcon);
        AirTickButton.setBounds(650, 150, 200, 80);
        AirTickButton.addActionListener(this);
        panel.add(AirTickButton);

        // Logout button below, centered and larger
        LogoutButton = new JButton("Logout", logoutIcon);
        LogoutButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 20));
        LogoutButton.setBounds(400, 300, 200, 60);
        LogoutButton.addActionListener(this);
        panel.add(LogoutButton);

        this.add(panel);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == BusButton) {
            this.setVisible(false);
            new BusBooking().setVisible(true);
        } else if (ae.getSource() == TrainButton) {
            this.setVisible(false);
            new TrainBooking().setVisible(true);
        } else if (ae.getSource() == AirTickButton) {
            this.setVisible(false);
            new PlaneBooking().setVisible(true);
        } else if (ae.getSource() == LogoutButton) {
            this.setVisible(false);
            JOptionPane.showMessageDialog(this, "Logging out...");
            new ShohozLogin().setVisible(true);
        }

        


       
    }

    public static void main(String[] args) {
        ShohozHome s1 = new ShohozHome();
        s1.setVisible(true);
    }
}
