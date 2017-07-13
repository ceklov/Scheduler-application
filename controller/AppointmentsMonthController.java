package scheduler.controller;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Period;
import javafx.scene.control.Button;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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

public class AppointmentsMonthController {
    
    Stage appointmentsMonthStage;
    private Scheduler scheduler;
    
    @FXML private Button appointmentMonthBackButton;
    @FXML private Button previousMonthButton;
    @FXML private Button nextMonthButton;
    @FXML private Button viewCustomerButton;
    
    @FXML private TextField monthOfTextField;

    @FXML private TableView appointmentMonthsTable;
    @FXML private TableColumn<Appointment, String> appointmentMonthDateColumn;
    @FXML private TableColumn<Appointment, String> appointmentMonthStartColumn; //CE: or should be DateTime after being converted to local?
    @FXML private TableColumn<Appointment, String> appointmentMonthEndColumn;
    @FXML private TableColumn<Appointment, String> appointmentMonthTitleColumn;
    @FXML private TableColumn<Appointment, String> appointmentMonthCustomerColumn; //CE: cause problems because actually from a Customer?
    @FXML private TableColumn<Appointment, String> appointmentMonthLocationColumn;
    


    private final Month CURRENT_MONTH = ZonedDateTime.now().getMonth();
    private final int CURRENT_YEAR = ZonedDateTime.now().getYear();
    private final LocalDateTime FIRST_DAY_OF_CURRENT_MONTH_ZDT = LocalDateTime.of(CURRENT_YEAR, CURRENT_MONTH, 1, 0, 0, 0);
    private final ZonedDateTime FIRST_DAY_OF_CURRENT_MONTH = ZonedDateTime.of(FIRST_DAY_OF_CURRENT_MONTH_ZDT, DateHandler.getDefaultZoneId());
    private ZonedDateTime now = ZonedDateTime.of(FIRST_DAY_OF_CURRENT_MONTH_ZDT, DateHandler.getDefaultZoneId());
    private Month currentMonth = FIRST_DAY_OF_CURRENT_MONTH.getMonth();
    private Period oneMonth = Period.ofMonths(1);
    
   @FXML
    public void initialize() {
        appointmentMonthDateColumn.setCellValueFactory(cellData -> cellData.getValue().appointmentDateProperty());
        appointmentMonthStartColumn.setCellValueFactory(cellData -> cellData.getValue().appointmentStartProperty());
        appointmentMonthEndColumn.setCellValueFactory(cellData -> cellData.getValue().appointmentEndProperty());
        appointmentMonthTitleColumn.setCellValueFactory(cellData -> cellData.getValue().appointmentTitleProperty()); 
        appointmentMonthCustomerColumn.setCellValueFactory(cellData -> cellData.getValue().appointmentCustomerNameProperty());   
        appointmentMonthLocationColumn.setCellValueFactory(cellData -> cellData.getValue().appointmentLocationProperty());    
    }          
    
    @FXML
    private void clickedBack() {
        appointmentsMonthStage.close();
    }

    public void setStage(Stage appointmentsMonthStage) {
        this.appointmentsMonthStage = appointmentsMonthStage;
    }
    
    public void showAppointments(Scheduler scheduler) {
        if (now.isBefore(FIRST_DAY_OF_CURRENT_MONTH) || now.isEqual(FIRST_DAY_OF_CURRENT_MONTH)) { 
            previousMonthButton.setVisible(false);
        } else { previousMonthButton.setVisible(true); }
        
        Comparator<Appointment> appointmentComparator = Comparator.comparing(x -> x.getAppointmentStart().truncatedTo(ChronoUnit.MINUTES));
        
        if (this.scheduler == null) { this.scheduler = scheduler; }
                
        ObservableList<Appointment> filteredAppointments = FXCollections.observableArrayList();
        scheduler.getAppointmentData().stream()
                .filter(x -> x.getAppointmentStart().isAfter(now) && x.getAppointmentStart().getMonth().equals(currentMonth))
                .sorted(appointmentComparator)
                .forEach(x -> filteredAppointments.add(x));
        
        appointmentMonthsTable.setItems(filteredAppointments);
        monthOfTextField.setText(String.valueOf(currentMonth));
    }
    
    @FXML
    private void clickedEditAppointment() {
        Appointment selectedAppointment = (Appointment) appointmentMonthsTable.getSelectionModel().getSelectedItem();
        
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
        Appointment selectedAppointment = (Appointment) appointmentMonthsTable.getSelectionModel().getSelectedItem();
        
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
        Appointment selectedAppointment = (Appointment) appointmentMonthsTable.getSelectionModel().getSelectedItem();
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
    
    @FXML
    private void clickedNextMonth() {
        now = now.plus(oneMonth);
        currentMonth = now.getMonth();
        showAppointments(scheduler);
    }
    
    @FXML
    private void clickedPreviousMonth() {
        now = now.minus(oneMonth);
        currentMonth = now.getMonth();
        showAppointments(scheduler);
    }
}
