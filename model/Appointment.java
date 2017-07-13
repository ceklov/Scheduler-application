package scheduler.model;

import java.time.ZonedDateTime;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Appointment {
    
    private int appointmentID;
    private ZonedDateTime appointmentStart;
    private ZonedDateTime appointmentEnd;
    private StringProperty appointmentTitle;
    private StringProperty appointmentLocation;       
    private int appointmentCustomerID; //CE: will be used as a foreign key for the database
    
    //CE: whereas the above instance variables are only set when an appointment is added/edited, the following instance variables are also set when an appointments screen is opened so as to display values in the user's local time
    private StringProperty appointmentCustomerName;
    private StringProperty appointmentDate;
    private StringProperty appointmentStartProperty;
    private StringProperty appointmentEndProperty;
    
    private static int appointmentCounter = 0;
    
    public Appointment(ZonedDateTime appointmentStart, ZonedDateTime appointmentEnd, String appointmentTitle, String appointmentLocation, String appointmentDate, String appointmentStartProperty, String appointmentEndProperty, int appointmentCustomerID, String appointmentCustomerName) {
        this.appointmentID = ++appointmentCounter;
        this.appointmentStart = appointmentStart;
        this.appointmentEnd = appointmentEnd;
        this.appointmentTitle = new SimpleStringProperty(appointmentTitle);
        this.appointmentLocation = new SimpleStringProperty(appointmentLocation);
        
        this.appointmentDate = new SimpleStringProperty(appointmentDate);
        this.appointmentStartProperty = new SimpleStringProperty(appointmentStartProperty);
        this.appointmentEndProperty = new SimpleStringProperty(appointmentEndProperty);
        this.appointmentCustomerID = appointmentCustomerID;
        this.appointmentCustomerName = new SimpleStringProperty(appointmentCustomerName);
    }
    
    public Appointment() {
        this(null, null, "appointment" + appointmentCounter, null, null, null, null, -1, null);
    }
 
    public int getAppointmentID() {
        return appointmentID;
    }
    
    public void setAppointmentID(int appointmentID) {
        this.appointmentID = appointmentID;
    }  
    
    public ZonedDateTime getAppointmentStart() {
        return appointmentStart;
    }

    public void setAppointmentStart(ZonedDateTime appointmentStart) {
        this.appointmentStart = appointmentStart;
    }

    public ZonedDateTime getAppointmentEnd() {
        return appointmentEnd;
    }

    public void setAppointmentEnd(ZonedDateTime appointmentEnd) {
        this.appointmentEnd = appointmentEnd;
    }
        
    public StringProperty appointmentTitleProperty() {
        return appointmentTitle;
    }
    
    public String getAppointmentTitle() {
        return appointmentTitle.get();
    }

    public void setAppointmentTitle(String appointmentTitle) {
        this.appointmentTitle.set(appointmentTitle);
    }
    
    public String getAppointmentLocation() {
        return appointmentLocation.get();
    }

    public StringProperty appointmentLocationProperty() {
        return appointmentLocation;
    }

    public void setAppointmentLocation(String appointmentLocation) {
        this.appointmentLocation.set(appointmentLocation);
    }
 
    public StringProperty appointmentDateProperty() {
        return appointmentDate;
    }
    
    public void setAppointmentDateProperty(String date) {
        this.appointmentDate.set(date);
    }

    public StringProperty appointmentStartProperty() {
        return appointmentStartProperty;
    }
    
    public void setAppointmentStartProperty(String start) {
        this.appointmentStartProperty.set(start);
    }
    
    public StringProperty appointmentEndProperty() {
        return appointmentEndProperty;
    }
    
    public void setAppointmentEndProperty(String end) {
        this.appointmentEndProperty.set(end);
    }
        
    public int getAppointmentCustomerID() {
        return appointmentCustomerID;
    }

    public void setAppointmentCustomerID(int appointmentCustomerID) {
        this.appointmentCustomerID = appointmentCustomerID;
    }
    
    public StringProperty appointmentCustomerNameProperty() {
        return appointmentCustomerName;
    }
    
    public String getAppointmentCustomerName() {
        return appointmentCustomerName.get();
    }
    
    public void setAppointmentCustomerNameProperty(String customerName) {
        this.appointmentCustomerName.set(customerName);
    }
        
}
