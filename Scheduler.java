package scheduler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import scheduler.DAO.AppointmentDAO;
import scheduler.DAO.CustomerDAO;
import scheduler.controller.AppointmentsMonthController;
import scheduler.controller.AppointmentsWeekController;
import scheduler.controller.CustomerReportController;
import scheduler.controller.EditAppointmentController;
import scheduler.controller.EditCustomerController;
import scheduler.controller.LocationReportController;
import scheduler.controller.LoginController;
import scheduler.controller.SchedulerMainController;
import scheduler.controller.TypeReportController;
import scheduler.model.Appointment;
import scheduler.model.Customer;

public class Scheduler extends Application {
    
    private Stage primaryStage;
    private BorderPane rootLayout;
    
    public static ObservableList<Customer> customerData = FXCollections.observableArrayList();
    public static ObservableList<Appointment> appointmentData = FXCollections.observableArrayList();
    
    public static String currentUser = null;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Scheduler Application");

        /* CE: for at least one user to log in*/
        try {
            DebugLoader.loadDebugUser();
        } catch (SQLException e) {
            System.out.println("Could not add test user");
        }
        
        startRootLayout();

        showLogin();
        System.out.println("Logged in as: " + currentUser);
        
        if (currentUser != null) {        
            try {
                CustomerDAO.loadCustomerData();
                AppointmentDAO.loadAppointmentData();
                AlertHandler.setAlertData();
            } catch (SQLException e) {
                System.out.println("Could not load customer data");
                e.printStackTrace();
            }
        }
        
        if (currentUser != null) {
            showSchedulerMain();
        } else System.exit(0);
        
    }

    public void startRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
        
            loader.setLocation(Scheduler.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);    
            primaryStage.show();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    public void showSchedulerMain() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Scheduler.class.getResource("view/SchedulerMain.fxml"));
            AnchorPane schedulerAnchorPane = (AnchorPane) loader.load();
            rootLayout.setCenter(schedulerAnchorPane);
            SchedulerMainController controller = loader.getController();
            controller.setScheduler(this);
        } catch (IOException e) {
            e.printStackTrace();
        }      
    }
    
    private void showLogin() {        
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Scheduler.class.getResource("view/Login.fxml"));
            AnchorPane loginAnchorPane = (AnchorPane) loader.load();
            
            Stage loginStage = new Stage();
            loginStage.setTitle("Login");
            loginStage.initModality(Modality.WINDOW_MODAL);
            loginStage.initOwner(primaryStage);
            
            Scene scene = new Scene(loginAnchorPane);
            loginStage.setScene(scene);
            
            LoginController controller = loader.getController();
            controller.setStage(loginStage);
            
            loginStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
    
    public void showAppointmentsByWeek() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Scheduler.class.getResource("view/AppointmentsWeek.fxml"));
            AnchorPane pane = (AnchorPane) loader.load();
            
            Stage appointmentsWeekStage = new Stage();
            appointmentsWeekStage.setTitle("Appointments (Week)");
            appointmentsWeekStage.initModality(Modality.WINDOW_MODAL);
            appointmentsWeekStage.initOwner(primaryStage);
            
            Scene scene = new Scene(pane);
            appointmentsWeekStage.setScene(scene);
            
            AppointmentsWeekController controller = loader.getController();
            controller.setStage(appointmentsWeekStage);
            controller.showAppointments(this);
            
            appointmentsWeekStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
    
    public void showAppointmentsByMonth() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Scheduler.class.getResource("view/AppointmentsMonth.fxml"));
            AnchorPane pane = (AnchorPane) loader.load();
            
            Stage appointmentsMonthStage = new Stage();
            appointmentsMonthStage.setTitle("Appointments (Month)");
            appointmentsMonthStage.initModality(Modality.WINDOW_MODAL);
            appointmentsMonthStage.initOwner(primaryStage);
            
            Scene scene = new Scene(pane);
            appointmentsMonthStage.setScene(scene);
            
            AppointmentsMonthController controller = loader.getController();
            controller.setStage(appointmentsMonthStage);
            controller.showAppointments(this);
            
            appointmentsMonthStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
    
    public void showEditAppointment(Customer customer) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Scheduler.class.getResource("view/EditAppointment.fxml"));
            AnchorPane pane = (AnchorPane) loader.load();
            
            Stage editAppointmentStage = new Stage();
            editAppointmentStage.setTitle("Appointment");
            editAppointmentStage.initModality(Modality.WINDOW_MODAL);
            editAppointmentStage.initOwner(primaryStage);
            
            Scene scene = new Scene(pane);
            editAppointmentStage.setScene(scene);
            
            EditAppointmentController controller = loader.getController();
            controller.setStage(editAppointmentStage);
            controller.setCustomer(customer);
            
            editAppointmentStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
    
    public void showEditAppointment(Appointment appointment, Customer customer) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Scheduler.class.getResource("view/EditAppointment.fxml"));
            AnchorPane pane = (AnchorPane) loader.load();
            
            Stage editAppointmentStage = new Stage();
            editAppointmentStage.setTitle("Appointment");
            editAppointmentStage.initModality(Modality.WINDOW_MODAL);
            editAppointmentStage.initOwner(primaryStage);
            
            Scene scene = new Scene(pane);
            editAppointmentStage.setScene(scene);
            
            EditAppointmentController controller = loader.getController();
            controller.setStage(editAppointmentStage);
            controller.setAppointment(appointment);
            controller.setCustomer(customer);
            
            editAppointmentStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
    
    public void showEditCustomer(Customer customer, boolean showEdit) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Scheduler.class.getResource("view/EditCustomer.fxml"));
            AnchorPane pane = (AnchorPane) loader.load();
            
            Stage editCustomerStage = new Stage();
            editCustomerStage.setTitle("Customer");
            editCustomerStage.initModality(Modality.WINDOW_MODAL);
            editCustomerStage.initOwner(primaryStage);
            
            Scene scene = new Scene(pane);
            editCustomerStage.setScene(scene);
            
            EditCustomerController controller = loader.getController();
            controller.setStage(editCustomerStage);
            controller.setEditView(showEdit);
            controller.setCustomer(customer);
            
            editCustomerStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
    
    public void showTypeReport() {
        String appointmentType = "";
        
        Dialog dialog = new TextInputDialog("Type");
        dialog.setTitle("Choose appointment type");
        dialog.setHeaderText("Enter a type to produce a report");
        
        Optional<String> choice = dialog.showAndWait();
        if (choice.isPresent()) {
            appointmentType = choice.get();
        }
        
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Scheduler.class.getResource("view/TypeReport.fxml"));
            AnchorPane pane = (AnchorPane) loader.load();
            
            Stage typeReportStage = new Stage();
            typeReportStage.setTitle("Type Report");
            typeReportStage.initModality(Modality.WINDOW_MODAL);
            typeReportStage.initOwner(primaryStage);
            
            Scene scene = new Scene(pane);
            typeReportStage.setScene(scene);
            
            TypeReportController controller = loader.getController();
            controller.setStage(typeReportStage);
            controller.setAppointmentType(appointmentType);
            controller.showAppointments(this);
            
            typeReportStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
    
    public void showCustomerReport(Customer customer) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Scheduler.class.getResource("view/CustomerReport.fxml"));
            AnchorPane pane = (AnchorPane) loader.load();
            
            Stage customerReportStage = new Stage();
            customerReportStage.setTitle("Customer Report");
            customerReportStage.initModality(Modality.WINDOW_MODAL);
            customerReportStage.initOwner(primaryStage);
            
            Scene scene = new Scene(pane);
            customerReportStage.setScene(scene);
            
            CustomerReportController controller = loader.getController();
            controller.setStage(customerReportStage);
            controller.setCustomer(customer);
            controller.showAppointments(this);
            
            customerReportStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
    
    public void showLocationReport() {
        String location = "";
        
        Dialog dialog = new TextInputDialog("Location");
        dialog.setTitle("Choose location");
        dialog.setHeaderText("Enter a location to produce a report");
        
        Optional<String> choice = dialog.showAndWait();
        if (choice.isPresent()) {
            location = choice.get();
        }
        
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Scheduler.class.getResource("view/LocationReport.fxml"));
            AnchorPane pane = (AnchorPane) loader.load();
            
            Stage locationReportStage = new Stage();
            locationReportStage.setTitle("Location Report");
            locationReportStage.initModality(Modality.WINDOW_MODAL);
            locationReportStage.initOwner(primaryStage);
            
            Scene scene = new Scene(pane);
            locationReportStage.setScene(scene);
            
            LocationReportController controller = loader.getController();
            controller.setStage(locationReportStage);
            controller.setLocation(location);
            controller.showAppointments(this);
            
            locationReportStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
    
    public void showAddCustomer() {
        showEditCustomer(null, true);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
    public ObservableList<Customer> getCustomerData() {
        return customerData;
    }
    
    public ObservableList<Appointment> getAppointmentData() {
        return appointmentData;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
}
