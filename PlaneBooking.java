
    import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PlaneBooking extends JFrame implements ActionListener{
     JLabel fromLabel, toLabel, classLabel, ticketLabel, imglbl;
     JComboBox<String> fromCombo, toCombo;
     JRadioButton acButton, nonAcButton;
     JTextField ticketField;
     JButton bookButton,backButton;
     JTextArea outputArea;
     JPanel panel;
    ImageIcon img;
    Color textareaclr,lblbg,brgred;

    private final String[] cities = {"Dhaka", "London", "Madrid", "Paris", "Delhi"};
    private final int[][] nonAcPrices = {
        {0, 500, 550, 600, 650},
        {500, 0, 700, 750, 800},
        {550, 700, 0, 850, 900},
        {600, 750, 850, 0, 950},
        {650, 800, 900, 950, 0}
    };
    private final int[][] acPrices = {
        {0, 800, 850, 900, 950},
        {800, 0, 1000, 1050, 1100},
        {850, 1000, 0, 1150, 1200},
        {900, 1050, 1150, 0, 1250},
        {950, 1100, 1200, 1250, 0}
    };

    public PlaneBooking() {
        super("Air Ticket Booking System");
        setSize(850, 650);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textareaclr=new Color(244, 231, 225);
        lblbg=new Color(82, 53, 123);
        brgred=new Color(238, 75, 43);


        panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(0, 0, 850, 650);

        
        

        // "From City" label and combo box
        fromLabel = new JLabel("From City:");
        fromLabel.setBounds(250, 170, 100, 30);
        fromLabel.setOpaque(true);
        fromLabel.setBackground(lblbg);
        fromLabel.setForeground(Color.white);
        panel.add(fromLabel);

        fromCombo = new JComboBox<>(cities);
        fromCombo.setBounds(370, 170, 200, 30);
        panel.add(fromCombo);

        // "To City" label and combo box
        toLabel = new JLabel("To City:");
        toLabel.setBounds(250, 220, 100, 30);
        toLabel.setOpaque(true);
        toLabel.setBackground(lblbg);
        toLabel.setForeground(Color.white);
        panel.add(toLabel);

        toCombo = new JComboBox<>(cities);
        toCombo.setBounds(370, 220, 200, 30);
        panel.add(toCombo);

        // "Class" label and radio buttons
        classLabel = new JLabel("Class:");
        classLabel.setBounds(250, 270, 100, 30);
        classLabel.setOpaque(true);
        classLabel.setBackground(lblbg);
        classLabel.setForeground(Color.white);
        panel.add(classLabel);

        acButton = new JRadioButton("Economy");
        acButton.setBounds(370, 270, 90, 30);
        panel.add(acButton);

        nonAcButton = new JRadioButton("Business", true);
        nonAcButton.setBounds(460, 270, 100, 30);
        panel.add(nonAcButton);

        ButtonGroup classGroup = new ButtonGroup();
        classGroup.add(acButton);
        classGroup.add(nonAcButton);

        // Ticket input
        ticketLabel = new JLabel("Number of Tickets:");
        ticketLabel.setBounds(250, 320, 125, 30);
        ticketLabel.setOpaque(true);
        ticketLabel.setBackground(lblbg);
        ticketLabel.setForeground(Color.white);
        panel.add(ticketLabel);

        ticketField = new JTextField();
        ticketField.setBounds(395, 320, 200, 30);
        panel.add(ticketField);

        // Book button
        bookButton = new JButton("Book Ticket");
        bookButton.setBounds(280, 370, 130, 35);
        bookButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int fromIndex = fromCombo.getSelectedIndex();
                int toIndex = toCombo.getSelectedIndex();

                if (fromIndex == toIndex) {
                    JOptionPane.showMessageDialog(PlaneBooking.this, "Please select different cities for From and To.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    int quantity = Integer.parseInt(ticketField.getText());
                    if (quantity <= 0) {
                        JOptionPane.showMessageDialog(PlaneBooking.this, "Number of tickets must be greater than zero.", "Input Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    int price;
                    String travelClass;

                    if (acButton.isSelected()) {
                        price = nonAcPrices[fromIndex][toIndex];
                        travelClass = "Economy";
                    } else {
                        price = acPrices[fromIndex][toIndex];
                        travelClass = "Business";
                    }

                    int total = price * quantity;

                    String details = "Booking Details:\n" +
                            "From: " + cities[fromIndex] + "\n" +
                            "To: " + cities[toIndex] + "\n" +
                            "Class: " + travelClass + "\n" +
                            "Tickets: " + quantity + "\n" +
                            "Total Price: " + total + " BDT\n";

                    outputArea.setText(details);

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(PlaneBooking.this, "Please enter a valid number of tickets.", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });// using named ActionListener 

        panel.add(bookButton);

        JButton deleteButton = new JButton("Delete Ticket");
        deleteButton.setBounds(430, 370, 140, 35);
        deleteButton.setBackground(Color.RED);
        deleteButton.setForeground(Color.black);
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ticketField.setText("");
                outputArea.setText("");
            }
        });
        
        
        panel.add(deleteButton);

        // Output area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setBackground(textareaclr);
        
        //read only
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBounds(200, 420, 450, 150);
        
        panel.add(scrollPane);


        backButton = new JButton("Back");
        backButton.setBounds(750, 580, 100, 30);
        panel.add(backButton);
        backButton.addActionListener(this);
    

    


        img=new ImageIcon("Plane.jpeg");
		imglbl=new JLabel(img);
		imglbl.setBounds(0,0,850,650);
		panel.add(imglbl);




        add(panel);

        
    }


    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == backButton) {
            this.setVisible(false);
            new ShohozHome().setVisible(true);
        }
    }

    
   

    public static void main(String[] args) {
      
                new PlaneBooking().setVisible(true);            
            
        
            
       
    }
}




