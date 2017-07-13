package scheduler.controller;

import java.sql.SQLException;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import scheduler.DAO.CustomerDAO;
import scheduler.Scheduler;
import scheduler.model.Customer;

public class SchedulerMainController {
    
    private Scheduler scheduler;

    @FXML private Button scheduleAppointmentButton;
    @FXML private Button appointmentsWeekButton;
    @FXML private Button appointmentsMonthButton;
    @FXML private Button addCustomerButton;
    @FXML private Button editCustomerButton;
    @FXML private Button deleteCustomerButton; //CE: will actually render the customer inactive and remove from view
    
    @FXML private Button report1;
    @FXML private Button report2;
    @FXML private Button report3;

    @FXML private TableView<Customer> schedulerMainTable;
    @FXML private TableColumn<Customer, String> customerNameColumn;
    @FXML private TableColumn<Customer, String> customerAddressColumn;
    @FXML private TableColumn<Customer, String> customerCityStateColumn;
    @FXML private TableColumn<Customer, String> customerZipColumn;
    @FXML private TableColumn<Customer, String> customerCountryColumn;
    @FXML private TableColumn<Customer, String> customerPhoneColumn;
    
    @FXML private void initialize() {
        customerNameColumn.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());
        customerAddressColumn.setCellValueFactory(cellData -> cellData.getValue().customerAddressProperty());
        customerCityStateColumn.setCellValueFactory(cellData -> cellData.getValue().customerCityStateProperty());
        customerZipColumn.setCellValueFactory(cellData -> cellData.getValue().customerZipProperty()); 
        customerCountryColumn.setCellValueFactory(cellData -> cellData.getValue().customerCountryProperty());   
        customerPhoneColumn.setCellValueFactory(cellData -> cellData.getValue().customerPhoneProperty());    }    
    
    @FXML
    private void clickedScheduleAppointment() {
        Customer selectedCustomer = schedulerMainTable.getSelectionModel().getSelectedItem();
        
        if (selectedCustomer == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(scheduler.getPrimaryStage());
            alert.setTitle("No customer selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a customer from the table.");
            
            alert.showAndWait();
        } else scheduler.showEditAppointment(selectedCustomer);
    }
    
    @FXML
    private void clickedAppointmentsWeek() {
        scheduler.showAppointmentsByWeek();
    }
    
    @FXML
    private void clickedAppointmentsMonth() {
        scheduler.showAppointmentsByMonth();
    }
    
    @FXML
    private void clickedAddCustomer() {
        scheduler.showAddCustomer();
    }
    
    @FXML
    private void clickedEditCustomer() {
        Customer selectedCustomer = schedulerMainTable.getSelectionModel().getSelectedItem();
        
        if (selectedCustomer == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(scheduler.getPrimaryStage());
            alert.setTitle("No customer selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a customer from the table.");
            
            alert.showAndWait();
        } else scheduler.showEditCustomer(selectedCustomer, true);
    }
    
    @FXML
    private void clickedDeleteCustomer() {
        int selectedIndex = schedulerMainTable.getSelectionModel().getSelectedIndex();
        
        if (selectedIndex >= 0) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete this customer?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                Customer selectedCustomer = (Customer) schedulerMainTable.getSelectionModel().getSelectedItem();
                try {
                    CustomerDAO.deleteCustomer(selectedCustomer);
                    Scheduler.customerData.remove(selectedCustomer);
                } catch (SQLException e) {
                    System.out.println("Could not delete " + selectedCustomer.getCustomerName());
                    e.printStackTrace();
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(scheduler.getPrimaryStage());
            alert.setTitle("No customer selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a customer from the table.");
            
            alert.showAndWait();
        } 
    }
    
    @FXML
    private void clickedTypeReport() {
        scheduler.showTypeReport();
    }
    
    @FXML
    private void clickedCustomerReport() {
        Customer selectedCustomer = schedulerMainTable.getSelectionModel().getSelectedItem();
        
        if (selectedCustomer == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(scheduler.getPrimaryStage());
            alert.setTitle("No customer selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a customer from the table.");
            
            alert.showAndWait();
        } else scheduler.showCustomerReport(selectedCustomer);
    }
    
    @FXML
    private void clickedLocationReport() {
        scheduler.showLocationReport();
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
        schedulerMainTable.setItems(scheduler.getCustomerData());
        
    }
    
}
