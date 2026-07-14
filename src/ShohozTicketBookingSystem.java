import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

public class ShohozTicketBookingSystem {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame(new FileStore(Path.of("data"))).setVisible(true));
    }
}

class MainFrame extends JFrame {
    private final AuthService authService;
    private final BookingService bookingService;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel root = new JPanel(cardLayout);
    private final AuthPanel authPanel;
    private final BookingPanel bookingPanel;

    MainFrame(FileStore fileStore) {
        this.authService = new AuthService(fileStore);
        this.bookingService = new BookingService(fileStore);
        this.authPanel = new AuthPanel(this, authService);
        this.bookingPanel = new BookingPanel(this, bookingService);

        setTitle("Shohoz Ticket Booking System");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(900, 550);
        setLocationRelativeTo(null);

        root.add(authPanel, "auth");
        root.add(bookingPanel, "booking");
        add(root);
        showAuth();
    }

    void onLoginSuccess(String username) {
        bookingPanel.setActiveUser(username);
        cardLayout.show(root, "booking");
    }

    void showAuth() {
        authPanel.clearFields();
        cardLayout.show(root, "auth");
    }
}

class AuthPanel extends JPanel {
    private final MainFrame parent;
    private final AuthService authService;
    private final JTextField usernameField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();

    AuthPanel(MainFrame parent, AuthService authService) {
        this.parent = parent;
        this.authService = authService;
        setLayout(new BorderLayout(12, 12));

        JLabel header = new JLabel("Welcome to Shohoz", SwingConstants.CENTER);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 22f));
        add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(40, 200, 40, 200));
        form.add(new JLabel("Username:"));
        form.add(usernameField);
        form.add(new JLabel("Password:"));
        form.add(passwordField);

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");
        form.add(loginBtn);
        form.add(registerBtn);
        add(form, BorderLayout.CENTER);

        loginBtn.addActionListener(e -> login());
        registerBtn.addActionListener(e -> register());
    }

    private void register() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password are required.");
            return;
        }
        if (authService.register(username, password)) {
            JOptionPane.showMessageDialog(this, "Registration successful. Please login.");
            clearFields();
            return;
        }
        JOptionPane.showMessageDialog(this, "Username already exists.");
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (authService.login(username, password)) {
            parent.onLoginSuccess(username);
            return;
        }
        JOptionPane.showMessageDialog(this, "Invalid username or password.");
    }

    void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
    }
}

class BookingPanel extends JPanel {
    private static final DecimalFormat FARE_FORMAT = new DecimalFormat("#,##0.00");

    private final MainFrame parent;
    private final BookingService bookingService;
    private String activeUser;

    private final JComboBox<TicketType> ticketType = new JComboBox<>(TicketType.values());
    private final JComboBox<String> route = new JComboBox<>();
    private final JComboBox<TravelClass> travelClass = new JComboBox<>(TravelClass.values());
    private final JSpinner passengers = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
    private final JLabel fareLabel = new JLabel("Fare: 0.00 BDT");
    private final DefaultTableModel bookingModel = new DefaultTableModel(
            new String[]{"ID", "Type", "Route", "Class", "Passengers", "Total Fare"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable bookingTable = new JTable(bookingModel);

    BookingPanel(MainFrame parent, BookingService bookingService) {
        this.parent = parent;
        this.bookingService = bookingService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topBar = new JPanel(new BorderLayout());
        JLabel heading = new JLabel("Ticket Booking");
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 20f));
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> parent.showAuth());
        topBar.add(heading, BorderLayout.WEST);
        topBar.add(logoutBtn, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        JPanel bookingForm = new JPanel(new GridLayout(6, 2, 8, 8));
        bookingForm.setBorder(BorderFactory.createTitledBorder("Create Booking"));
        bookingForm.add(new JLabel("Transport Type:"));
        bookingForm.add(ticketType);
        bookingForm.add(new JLabel("Route:"));
        bookingForm.add(route);
        bookingForm.add(new JLabel("Travel Class:"));
        bookingForm.add(travelClass);
        bookingForm.add(new JLabel("Passengers:"));
        bookingForm.add(passengers);
        bookingForm.add(new JLabel("Estimated Total Fare:"));
        bookingForm.add(fareLabel);

        JButton bookBtn = new JButton("Book Ticket");
        JButton cancelBtn = new JButton("Cancel Selected Booking");
        bookingForm.add(bookBtn);
        bookingForm.add(cancelBtn);

        JPanel center = new JPanel(new GridLayout(1, 2, 10, 10));
        center.add(bookingForm);
        JScrollPane tablePane = new JScrollPane(bookingTable);
        tablePane.setBorder(BorderFactory.createTitledBorder("My Bookings"));
        center.add(tablePane);
        add(center, BorderLayout.CENTER);

        ticketType.addActionListener(e -> refreshRoutesAndFare());
        travelClass.addActionListener(e -> updateFareLabel());
        passengers.addChangeListener(e -> updateFareLabel());
        route.addActionListener(e -> updateFareLabel());

        bookBtn.addActionListener(e -> createBooking());
        cancelBtn.addActionListener(e -> cancelSelectedBooking());
    }

    void setActiveUser(String username) {
        this.activeUser = username;
        refreshRoutesAndFare();
        refreshBookings();
    }

    private void refreshRoutesAndFare() {
        route.removeAllItems();
        TicketType selectedType = (TicketType) ticketType.getSelectedItem();
        if (selectedType != null) {
            for (String routeName : bookingService.getRoutesForType(selectedType)) {
                route.addItem(routeName);
            }
        }
        updateFareLabel();
    }

    private void updateFareLabel() {
        TicketType selectedType = (TicketType) ticketType.getSelectedItem();
        String selectedRoute = (String) route.getSelectedItem();
        TravelClass selectedClass = (TravelClass) travelClass.getSelectedItem();
        int count = (int) passengers.getValue();

        double fare = bookingService.calculateFare(selectedType, selectedRoute, selectedClass, count);
        fareLabel.setText("Fare: " + FARE_FORMAT.format(fare) + " BDT");
    }

    private void createBooking() {
        if (activeUser == null) {
            return;
        }
        TicketType selectedType = (TicketType) ticketType.getSelectedItem();
        String selectedRoute = (String) route.getSelectedItem();
        TravelClass selectedClass = (TravelClass) travelClass.getSelectedItem();
        int count = (int) passengers.getValue();
        if (selectedType == null || selectedRoute == null || selectedClass == null) {
            JOptionPane.showMessageDialog(this, "Please complete the booking form.");
            return;
        }

        bookingService.createBooking(activeUser, selectedType, selectedRoute, selectedClass, count);
        refreshBookings();
        updateFareLabel();
        JOptionPane.showMessageDialog(this, "Booking successful.");
    }

    private void cancelSelectedBooking() {
        int selectedRow = bookingTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a booking from the table.");
            return;
        }
        String bookingId = bookingTable.getValueAt(selectedRow, 0).toString();
        bookingService.cancelBooking(activeUser, bookingId);
        refreshBookings();
    }

    private void refreshBookings() {
        bookingModel.setRowCount(0);
        for (Booking booking : bookingService.getBookingsForUser(activeUser)) {
            bookingModel.addRow(new Object[]{
                    booking.id(),
                    booking.ticketType(),
                    booking.route(),
                    booking.travelClass(),
                    booking.passengers(),
                    FARE_FORMAT.format(booking.fare()) + " BDT"
            });
        }
    }
}

enum TicketType {
    BUS, TRAIN, AIR
}

enum TravelClass {
    ECONOMY(1.0), BUSINESS(1.5), FIRST_CLASS(2.0);
    final double fareMultiplier;

    TravelClass(double fareMultiplier) {
        this.fareMultiplier = fareMultiplier;
    }
}

record Booking(String id, String username, TicketType ticketType, String route, TravelClass travelClass, int passengers, double fare) {
}

class AuthService {
    private final FileStore fileStore;

    AuthService(FileStore fileStore) {
        this.fileStore = fileStore;
    }

    boolean register(String username, String password) {
        Map<String, String> users = fileStore.loadUsers();
        if (users.containsKey(username)) {
            return false;
        }
        users.put(username, hash(password));
        fileStore.saveUsers(users);
        return true;
    }

    boolean login(String username, String password) {
        Map<String, String> users = fileStore.loadUsers();
        return users.getOrDefault(username, "").equals(hash(password));
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            for (byte b : hashedBytes) {
                result.append(String.format("%02x", b));
            }
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Unable to hash password.", e);
        }
    }
}

class BookingService {
    private final FileStore fileStore;
    private final Map<TicketType, LinkedHashMap<String, Integer>> baseFareByTypeAndRoute = new EnumMap<>(TicketType.class);

    BookingService(FileStore fileStore) {
        this.fileStore = fileStore;
        baseFareByTypeAndRoute.put(TicketType.BUS, new LinkedHashMap<>(Map.of(
                "Dhaka - Chattogram", 700,
                "Dhaka - Sylhet", 650,
                "Dhaka - Cox's Bazar", 1200
        )));
        baseFareByTypeAndRoute.put(TicketType.TRAIN, new LinkedHashMap<>(Map.of(
                "Dhaka - Rajshahi", 750,
                "Dhaka - Khulna", 900,
                "Dhaka - Chattogram", 850
        )));
        baseFareByTypeAndRoute.put(TicketType.AIR, new LinkedHashMap<>(Map.of(
                "Dhaka - Cox's Bazar", 4200,
                "Dhaka - Chattogram", 3500,
                "Dhaka - Sylhet", 3300
        )));
    }

    List<String> getRoutesForType(TicketType type) {
        if (type == null) {
            return List.of();
        }
        return new ArrayList<>(baseFareByTypeAndRoute.get(type).keySet());
    }

    double calculateFare(TicketType type, String route, TravelClass travelClass, int passengers) {
        if (type == null || route == null || travelClass == null || passengers < 1) {
            return 0.0;
        }
        Integer baseFare = baseFareByTypeAndRoute.get(type).get(route);
        if (baseFare == null) {
            return 0.0;
        }
        return baseFare * travelClass.fareMultiplier * passengers;
    }

    void createBooking(String username, TicketType type, String route, TravelClass travelClass, int passengers) {
        List<Booking> bookings = fileStore.loadBookings();
        double fare = calculateFare(type, route, travelClass, passengers);
        bookings.add(new Booking(
                UUID.randomUUID().toString().substring(0, 8),
                username,
                type,
                route,
                travelClass,
                passengers,
                fare
        ));
        fileStore.saveBookings(bookings);
    }

    List<Booking> getBookingsForUser(String username) {
        List<Booking> result = new ArrayList<>();
        for (Booking booking : fileStore.loadBookings()) {
            if (booking.username().equals(username)) {
                result.add(booking);
            }
        }
        return result;
    }

    void cancelBooking(String username, String bookingId) {
        List<Booking> bookings = fileStore.loadBookings();
        bookings.removeIf(booking -> booking.username().equals(username) && booking.id().equals(bookingId));
        fileStore.saveBookings(bookings);
    }
}

class FileStore {
    private final Path usersFile;
    private final Path bookingsFile;

    FileStore(Path dataDir) {
        try {
            Files.createDirectories(dataDir);
            this.usersFile = dataDir.resolve("users.txt");
            this.bookingsFile = dataDir.resolve("bookings.txt");
            if (!Files.exists(usersFile)) {
                Files.writeString(usersFile, "", StandardCharsets.UTF_8);
            }
            if (!Files.exists(bookingsFile)) {
                Files.writeString(bookingsFile, "", StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Unable to initialize storage files", e);
        }
    }

    synchronized Map<String, String> loadUsers() {
        Map<String, String> users = new HashMap<>();
        for (String line : readLines(usersFile)) {
            String[] parts = line.split("\\|", 2);
            if (parts.length == 2) {
                users.put(parts[0], parts[1]);
            }
        }
        return users;
    }

    synchronized void saveUsers(Map<String, String> users) {
        List<String> lines = new ArrayList<>();
        for (Map.Entry<String, String> entry : users.entrySet()) {
            lines.add(entry.getKey() + "|" + entry.getValue());
        }
        writeLines(usersFile, lines);
    }

    synchronized List<Booking> loadBookings() {
        List<Booking> bookings = new ArrayList<>();
        for (String line : readLines(bookingsFile)) {
            String[] parts = line.split("\\|", 7);
            if (parts.length == 7) {
                bookings.add(new Booking(
                        parts[0],
                        parts[1],
                        TicketType.valueOf(parts[2]),
                        parts[3],
                        TravelClass.valueOf(parts[4]),
                        Integer.parseInt(parts[5]),
                        Double.parseDouble(parts[6])
                ));
            }
        }
        return bookings;
    }

    synchronized void saveBookings(List<Booking> bookings) {
        List<String> lines = new ArrayList<>();
        for (Booking booking : bookings) {
            lines.add(String.join("|",
                    booking.id(),
                    booking.username(),
                    booking.ticketType().name(),
                    booking.route(),
                    booking.travelClass().name(),
                    String.valueOf(booking.passengers()),
                    String.valueOf(booking.fare()))
            );
        }
        writeLines(bookingsFile, lines);
    }

    private List<String> readLines(Path file) {
        try {
            return Files.readAllLines(file, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to read file: " + file, e);
        }
    }

    private void writeLines(Path file, List<String> lines) {
        try {
            Files.write(file, lines, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to write file: " + file, e);
        }
    }
}
