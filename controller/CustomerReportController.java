package scheduler.controller;

import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import scheduler.Scheduler;
import scheduler.model.Appointment;
import scheduler.model.Customer;

public class CustomerReportController {
    
    private Stage editCustomerStage;
    private Customer customer;
    private Scheduler scheduler;
    
    @FXML private TableView appointmentTable;
    @FXML private TableColumn<Appointment, String> dateColumn;
    @FXML private TableColumn<Appointment, String> startColumn;
    @FXML private TableColumn<Appointment, String> endColumn;
    @FXML private TableColumn<Appointment, String> titleColumn;
    @FXML private TableColumn<Appointment, String> customerColumn;
    @FXML private TableColumn<Appointment, String> locationColumn;

    public void initialize() {
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().appointmentDateProperty());
        startColumn.setCellValueFactory(cellData -> cellData.getValue().appointmentStartProperty());
        endColumn.setCellValueFactory(cellData -> cellData.getValue().appointmentEndProperty());
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().appointmentTitleProperty()); 
        customerColumn.setCellValueFactory(cellData -> cellData.getValue().appointmentCustomerNameProperty());   
        locationColumn.setCellValueFactory(cellData -> cellData.getValue().appointmentLocationProperty());
        
    }    
    
    public void setStage(Stage editCustomerStage) {
        this.editCustomerStage = editCustomerStage;
    }
    
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
    public void showAppointments(Scheduler scheduler) {

        Comparator<Appointment> appointmentComparator = Comparator.comparing(x -> x.getAppointmentStart().truncatedTo(ChronoUnit.MINUTES));
        
        if (this.scheduler == null) { this.scheduler = scheduler; }
                
        ObservableList<Appointment> filteredAppointments = FXCollections.observableArrayList();
        scheduler.getAppointmentData().stream()
                .filter(x -> x.getAppointmentCustomerID() == customer.getCustomerID())
                .sorted(appointmentComparator)
                .forEach(x -> filteredAppointments.add(x));
        
        appointmentTable.setItems(filteredAppointments);
    }
    
}
