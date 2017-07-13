package scheduler.controller;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import scheduler.DAO.UserDAO;
import scheduler.Scheduler;

public class LoginController {

    @FXML private TextField userTextField;
    @FXML private PasswordField passwordField;
    
    @FXML private Button loginButton;
    @FXML private Button languageButton;

    @FXML private Label userNameLabel;
    @FXML private Label passwordLabel;
    @FXML private Label headerLabel;
    @FXML private Label alertLabel;
    
    private Stage loginStage;
    private boolean validated;
    
    private final Locale enLocale = new Locale("en");
    private final Locale esLocale = new Locale("es");
        
    @FXML
    public void initialize() {
        Locale.setDefault(enLocale);
        //CE: for your efficient testing: Locale.setDefault(esLocale);
        resetText();
        //CE: I remove the button because I was informed to do so by a course mentor
        languageButton.setVisible(false);
    }
 
    @FXML
    private void clickedLogIn() {
        try {
            validated = UserDAO.validateUser(userTextField.getText(), passwordField.getText());
        } catch (SQLException e) {
            System.out.println("Could not validate user");
        }
        
        if (validated) {
            Scheduler.currentUser = userTextField.getText();
            recordLogin(Scheduler.currentUser);
            loginStage.close();
        } else {
            ResourceBundle rb = ResourceBundle.getBundle("Login", Locale.getDefault());
            alertLabel.setText(rb.getString("Incorrect"));    
        }
    }
    
    @FXML
    private void clickedLanguage() {
        if (Locale.getDefault() == enLocale) {
            Locale.setDefault(esLocale);
        } else {
            Locale.setDefault(enLocale);
        }
        System.out.println("Locale is now " + Locale.getDefault());
        resetText();
    }
    
    public void setStage(Stage loginStage) {
        this.loginStage = loginStage;
    }

    private void resetText() {
        ResourceBundle rb = ResourceBundle.getBundle("Login", Locale.getDefault());

        loginButton.setText(rb.getString("Log"));
        languageButton.setText(rb.getString("English"));
        userNameLabel.setText(rb.getString("User"));
        passwordLabel.setText(rb.getString("Password"));
        headerLabel.setText(rb.getString("Please"));
    }

    private void recordLogin(String currentUser) {
        ZonedDateTime timestamp = ZonedDateTime.now();
        System.out.println("User " + currentUser + " logged in at " + timestamp);
        
        try {
            if (!Files.exists(Paths.get("/Scheduler"))) {
                Files.createDirectory(Paths.get("/Scheduler"));
                System.out.println("Creating log folder");
            }
            Path path = Paths.get("/Scheduler/log.txt");
            if (!Files.exists(Paths.get("/Scheduler/log.txt"))) {
                try (BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("UTF-16"))) {
                    writer.write("User: " + currentUser + " @ " + timestamp + "\n\n");
                }
            } else {
                try (BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("UTF-16"), StandardOpenOption.APPEND)) {
                    String separator = System.getProperty("line.separator");
                    writer.write(separator + "User: " + currentUser + " @ " + timestamp + separator);
                }
            }
        } catch(IOException e) {
            System.out.println("Could not record in log");
        }
    }
    
}
