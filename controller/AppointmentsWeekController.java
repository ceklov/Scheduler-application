package scheduler.controller;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import scheduler.DAO.AppointmentDAO;
import scheduler.DAO.CustomerDAO;
import scheduler.DateHandler;
import scheduler.Scheduler;
import scheduler.model.Appointment;
import scheduler.model.Customer;

public class AppointmentsWeekController {
    
    Stage appointmentsWeekStage;
    private Scheduler scheduler;
             
    ZoneId zoneID = DateHandler.getDefaultZoneId();
    final ZonedDateTime NOW = ZonedDateTime.now();
    ZonedDateTime now = ZonedDateTime.now();
    ZonedDateTime oneWeekFromNow = now.plus(Period.ofDays(7));
    Duration week = Duration.ofDays(6);

    @FXML private Button appointmentWeekBackButton;
    @FXML private Button previousWeekButton;
    @FXML private Button nextWeekButton;
    @FXML private Button viewCustomerButton;
    
    @FXML private TextField weekOfTextField;

    @FXML private TableView appointmentWeeksTable;
    @FXML private TableColumn<Appointment, String> appointmentWeekDateColumn;
    @FXML private TableColumn<Appointment, String> appointmentWeekStartColumn; //CE: or should be DateTime after being converted to local?
    @FXML private TableColumn<Appointment, String> appointmentWeekEndColumn;
    @FXML private TableColumn<Appointment, String> appointmentWeekTitleColumn;
    @FXML private TableColumn<Appointment, String> appointmentWeekCustomerColumn; //CE: cause problems because actually from a Customer?
    @FXML private TableColumn<Appointment, String> appointmentWeekLocationColumn;
    
    @FXML
    public void initialize() {
        appointmentWeekDateColumn.setCellValueFactory(cellData -> cellData.getValue().appointmentDateProperty());
        appointmentWeekStartColumn.setCellValueFactory(cellData -> cellData.getValue().appointmentStartProperty());
        appointmentWeekEndColumn.setCellValueFactory(cellData -> cellData.getValue().appointmentEndProperty());
        appointmentWeekTitleColumn.setCellValueFactory(cellData -> cellData.getValue().appointmentTitleProperty()); 
        appointmentWeekCustomerColumn.setCellValueFactory(cellData -> cellData.getValue().appointmentCustomerNameProperty());   
        appointmentWeekLocationColumn.setCellValueFactory(cellData -> cellData.getValue().appointmentLocationProperty());    
    }          
    
    @FXML
    private void clickedBack() {
        appointmentsWeekStage.close();
    }
    
    @FXML
    private void clickedPreviousWeek() {
        oneWeekFromNow = now; 
        now = now.minus(Period.ofDays(7));
        showAppointments(scheduler);
    }
    
    @FXML
    private void clickedNextWeek() {  
        now = oneWeekFromNow;
        oneWeekFromNow = oneWeekFromNow.plus(Period.ofDays(7));
        showAppointments(scheduler);
    }

    public void setStage(Stage appointmentsWeekStage) {
        this.appointmentsWeekStage = appointmentsWeekStage;
    }
    
    public void showAppointments(Scheduler scheduler) {
        if (now.isBefore(NOW) || now.isEqual(NOW)) { 
            previousWeekButton.setVisible(false);
        } else { previousWeekButton.setVisible(true); }
        
        Comparator<Appointment> appointmentComparator = Comparator.comparing(x -> x.getAppointmentStart().truncatedTo(ChronoUnit.MINUTES));
        
        if (this.scheduler == null) { this.scheduler = scheduler; }
                
        ObservableList<Appointment> filteredAppointments = FXCollections.observableArrayList();
        scheduler.getAppointmentData().stream()
                .filter(x -> x.getAppointmentStart().isAfter(now) && x.getAppointmentStart().isBefore(oneWeekFromNow))
                .sorted(appointmentComparator)
                .forEach(x -> filteredAppointments.add(x));
        
        appointmentWeeksTable.setItems(filteredAppointments);
        weekOfTextField.setText("Week of " + DateHandler.MONTH_DAY_PATTERN.format(now));
    }
    
    @FXML
    private void clickedEditAppointment() {
        Appointment selectedAppointment = (Appointment) appointmentWeeksTable.getSelectionModel().getSelectedItem();
        
        if (selectedAppointment == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(scheduler.getPrimaryStage());
            alert.setTitle("No appointment selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select an appointment from the table.");
            
            alert.showAndWait();
        } else {
            Customer selectedCustomer;
            try {
                selectedCustomer = CustomerDAO.getCustomer(selectedAppointment.getAppointmentCustomerID());
                scheduler.showEditAppointment(selectedAppointment, selectedCustomer);

            } catch (SQLException e) {
                System.out.println("Could not open appointment for editing");
            } 

        }
        showAppointments(scheduler);
    }
    
    @FXML
    private void clickedDeleteAppointment() {
        Appointment selectedAppointment = (Appointment) appointmentWeeksTable.getSelectionModel().getSelectedItem();
        
        if (selectedAppointment == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(scheduler.getPrimaryStage());
            alert.setTitle("No appointment selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select an appointment from the table.");
            
            alert.showAndWait();
        } else {
            try {
                AppointmentDAO.deleteAppointment(selectedAppointment);
            } catch (SQLException e) {
                System.out.println("Could not open appointment for editing");
            }          
            Scheduler.appointmentData.remove(selectedAppointment);
        }
        showAppointments(scheduler);
    }
    
    @FXML
    private void clickedViewCustomer() {
        Appointment selectedAppointment = (Appointment) appointmentWeeksTable.getSelectionModel().getSelectedItem();
        Customer customer = null;
        
        if (selectedAppointment == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(scheduler.getPrimaryStage());
            alert.setTitle("No item selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select an item from the table.");
            
            alert.showAndWait();
        } else {
            try {
                customer = CustomerDAO.getCustomer(selectedAppointment.getAppointmentCustomerID());
            } catch (SQLException e) {
                System.out.println("Could not open customer information");
            }          
            scheduler.showEditCustomer(customer, false);
        }
    }
}
