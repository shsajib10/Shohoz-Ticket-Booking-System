import java.io.*;
import java.util.*;

public class ShohozAccount {
    private String username;
    private String userpass;

    private File myfile;
    private FileWriter fwrite;
    private Scanner sc;

    public ShohozAccount() {}

    public ShohozAccount(String username, String userpass) {
        this.username = username;
        this.userpass = userpass;
    }

    public void setusername(String username) {
        this.username = username;
    }

    public void setuserpass(String userpass) {
        this.userpass = userpass;
    }

    public String getusername() {
        return username;
    }

    public String getuserpass() {
        return userpass;
    }

    public void addShohozAccount() {
        try {
            myfile = new File("./UserData.txt");
            myfile.createNewFile();

            // Check if username already exists
            if (isUsernameTaken(username)) {
                System.out.println("Username already exists. Please choose a different one.");
                return;
            }

            fwrite = new FileWriter(myfile, true);
            fwrite.write(getusername() + "\t" + getuserpass() + "\n");
            fwrite.flush();
            fwrite.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public boolean isUsernameTaken(String username) {
        File file = new File("./UserData.txt");
        if (!file.exists()) {
            return false;
        }
        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                String[] value = line.split("\t");
                if (value.length == 2 && value[0].trim().equalsIgnoreCase(username.trim())) {
                    return true;
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return false;
    }

    public boolean getShohozAccount(String username, String userpass) {
        try {
            myfile = new File("./UserData.txt");
            sc = new Scanner(myfile);
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                String[] value = line.split("\t");
                if (value.length == 2) {
                    if (value[0].trim().equals(username.trim()) && value[1].trim().equals(userpass.trim())) {
                        sc.close();
                        return true;
                    }
                }
            }
            sc.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return false;
    }
}
