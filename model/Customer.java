package scheduler.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Customer {
    
    private int customerID;
    private StringProperty customerName;
    private String customerPassword;

    private StringProperty customerAddress;
    private StringProperty customerCityState;
    private StringProperty customerZip;
    private StringProperty customerCountry;
    private StringProperty customerPhone;
    private int customerActive;
    
    private static int customerCounter = 0; //CE: may need to find highest customer ID in database and set counter to it

    public Customer(String customerName, String customerPassword, String customerAddress, String customerCityState, String customerZip, String customerCountry, String customerPhone, int customerActive) {
        this.customerID = ++customerCounter;
        this.customerName = new SimpleStringProperty(customerName);
        this.customerPassword = customerPassword;
        this.customerAddress = new SimpleStringProperty(customerAddress);
        this.customerCityState = new SimpleStringProperty(customerCityState);
        this.customerZip = new SimpleStringProperty(customerZip);
        this.customerCountry = new SimpleStringProperty(customerCountry);
        this.customerPhone = new SimpleStringProperty(customerPhone);
        this.customerActive = customerActive;
    }

    public Customer() {
        this("customer" + customerCounter, null, null, null, null, null, null, 0);
    }

    public String getCustomerPassword() {
        return customerPassword;
    }

    public void setCustomerPassword(String customerPassword) {
        this.customerPassword = customerPassword;
    }
    
    public StringProperty customerCityStateProperty() {
        return customerCityState;
    }

    public String getCustomerCityState() {
        return customerCityState.get();
    }
    
    public void setCustomerCityState(String customerCityState) {
        this.customerCityState.set(customerCityState);
    }

    public StringProperty customerZipProperty() {
        return customerZip;
    }
    
    public String getCustomerZip() {
        return customerZip.get();
    }

    public void setCustomerZip(String customerZip) {
        this.customerZip.set(customerZip);
    }

    public StringProperty customerCountryProperty() {
        return customerCountry;
    }
    
    public String getCustomerCountry() {
        return customerCountry.get();
    }

    public void setCustomerCountry(String customerCountry) {
        this.customerCountry.set(customerCountry);
    }

    public static int getCustomerCounter() {
        return customerCounter;
    }

    public static void setCustomerCounter(int customerCounter) {
        Customer.customerCounter = customerCounter;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }
    
    public void setCustomerID() {
        this.customerID = ++customerCounter;
    }

    public StringProperty customerNameProperty() {
        return customerName;
    }
    
    public String getCustomerName() {
        return customerName.get();
    }

    public void setCustomerName(String customerName) {
        this.customerName.set(customerName);
    }

    public StringProperty customerAddressProperty() {
        return customerAddress;
    }

    public String getCustomerAddress() {
        return customerAddress.get();
    }
    
    public void setCustomerAddress(String customerAddress) {
        this.customerAddress.set(customerAddress);
    }

    public StringProperty customerPhoneProperty() {
        return customerPhone;
    }
    
    public String getCustomerPhone() {
        return customerPhone.get();
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone.set(customerPhone);
    }
    
    public int getCustomerActive() {
        return customerActive;
    }

    public void setCustomerActive(int customerActive) {
        this.customerActive = customerActive;
    }

}
