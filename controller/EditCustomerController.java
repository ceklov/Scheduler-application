package scheduler.controller;

import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import scheduler.DAO.CustomerDAO;
import scheduler.model.Customer;
import scheduler.Scheduler;

public class EditCustomerController {
    
    @FXML private TextField customerNameTextField;
    @FXML private TextField customerAddressTextField;
    @FXML private TextField customerCityStateTextField;
    @FXML private TextField customerZipTextField;
    @FXML private TextField customerCountryTextField;
    @FXML private TextField customerPhoneTextField;
    
    @FXML private Button customerSaveButton;
    @FXML private Button customerCancelButton;
    
    private Stage editCustomerStage;

    private Customer customer;
    private int customerId; //CE: matches the .getCustomerID() and customerId, addressId, cityId, and countryId in the database
    private int customerIndex; //CE: matches the index of the customer in the ObservableList
    
    private boolean editView = true; //CE: true by default, set to false if opening customer information from an appointment view
    
    @FXML
    public void initialize() {

    } 
    
    @FXML
    private void clickedSave() {
        if (isInputValid()) {
            Customer newCustomer = new Customer();
            newCustomer.setCustomerActive(1);
            newCustomer.setCustomerName(customerNameTextField.getText());
            newCustomer.setCustomerAddress(customerAddressTextField.getText());
            newCustomer.setCustomerCityState(customerCityStateTextField.getText());
            newCustomer.setCustomerZip(customerZipTextField.getText());
            newCustomer.setCustomerCountry(customerCountryTextField.getText());
            newCustomer.setCustomerPhone(customerPhoneTextField.getText());
            
            if (customer != null) { //CE: namely, we are updating an existing customer
                newCustomer.setCustomerID(customerId);
                try {
                    CustomerDAO.updateCustomer(customerId, newCustomer);
                } catch (SQLException e) {
                        System.out.println("Could not edit customer");
                }
                Scheduler.customerData.set(customerIndex, newCustomer);
            } else { //CE: we are adding a new customer
                try {
                    CustomerDAO.addCustomer(newCustomer);
                    } catch (SQLException e) {
                        System.out.println("Could not add customer");
                }
                Scheduler.customerData.add(newCustomer);
            }
        }
        editCustomerStage.close();
    }
    
    @FXML
    private void clickedCancel() {
        editCustomerStage.close();
    }
    
    public void setStage(Stage editCustomerStage) {
        this.editCustomerStage = editCustomerStage;
    }
    
    private boolean isInputValid() {
        String errorMessage = "";
        
        if ((customerNameTextField.getText() == null) || (customerNameTextField.getText().length() == 0)) {
            errorMessage += "Invalid name\n";
        }
        if ((customerAddressTextField.getText() == null) || (customerAddressTextField.getText().length() == 0)) {
            errorMessage += "Invalid address\n";
        }
        if ((customerCityStateTextField.getText() == null) || (customerCityStateTextField.getText().length() == 0)) {
            errorMessage += "Invalid city/state\n";
        }
        if ((customerZipTextField.getText() == null) || (customerZipTextField.getText().length() == 0)) {
            errorMessage += "Invalid postal code\n";
        }
        if ((customerCountryTextField.getText() == null) || (customerCountryTextField.getText().length() == 0)) {
            errorMessage += "Invalid country\n";
        }
        if ((customerPhoneTextField.getText() == null) || (customerPhoneTextField.getText().length() == 0)) {
            errorMessage += "Invalid phone\n";
        }
        
        if (errorMessage.length() == 0) {
            return true;
        } else {
           Alert alert = new Alert(Alert.AlertType.ERROR);
           alert.setTitle("Invalid customer information");
           alert.setHeaderText(null);
           alert.setContentText(errorMessage);
           
           alert.showAndWait();
           errorMessage = "";
           return false;
        }
        
    }

    public void setCustomer(Customer customer) {
        if (customer != null) {
            this.customer = customer;
            this.customerId = customer.getCustomerID();
            this.customerIndex = Scheduler.customerData.indexOf(customer);

            System.out.println("Customer: " + customer.getCustomerName() + ", Id: " + customerId + ", Index: " + customerIndex);

            customerNameTextField.setText(customer.getCustomerName());
            customerAddressTextField.setText(customer.getCustomerAddress());
            customerCityStateTextField.setText(customer.getCustomerCityState());
            customerZipTextField.setText(customer.getCustomerZip());
            customerCountryTextField.setText(customer.getCustomerCountry());
            customerPhoneTextField.setText(customer.getCustomerPhone());
            
            if (editView == false) {
                customerSaveButton.setVisible(false);
                customerCancelButton.setVisible(false);
                customerNameTextField.setEditable(false);
                customerAddressTextField.setEditable(false);
                customerCityStateTextField.setEditable(false);
                customerZipTextField.setEditable(false);
                customerCountryTextField.setEditable(false);
                customerPhoneTextField.setEditable(false);
            }
        }
    }
    
    public void setEditView(boolean editView) {
        this.editView = editView;
    }
}
