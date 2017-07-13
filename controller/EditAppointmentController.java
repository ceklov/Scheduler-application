package scheduler.controller;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import scheduler.DAO.AppointmentDAO;
import scheduler.DateHandler;
import scheduler.Scheduler;
import scheduler.model.Appointment;
import scheduler.model.Customer;

public class EditAppointmentController {

    @FXML private TextField appointmentCustomerTextField;
    @FXML private TextField appointmentTitleTextField;
    @FXML private DatePicker appointmentDatePicker;
    @FXML private TextField appointmentStartTextField;
    @FXML private TextField appointmentEndTextField;

    private Stage editAppointmentStage;
    
    private Customer customer;
    private Appointment appointment;
    private int appointmentId;
    private int appointmentIndex;
    
    @FXML
    public void initialize() {

    } 
    
    @FXML
    private void clickedSave() {
        if (isInputValid()) {
            
            if (appointment == null) { //CE: namely, we are creating a new appointment
            
                Appointment newAppointment = new Appointment();
                setAppointmentValues(newAppointment);

                try {
                    AppointmentDAO.addAppointment(newAppointment);
                } catch (SQLException e) {
                    System.out.println("Could not add appointment");
                    e.printStackTrace();
                }
                
                Scheduler.appointmentData.add(newAppointment);
                editAppointmentStage.close();
            } else { //CE: namely, we are editing an existing appointment
                
                setAppointmentValues(appointment);
                
                try {
                    AppointmentDAO.updateAppointment(appointment);
                } catch (SQLException e) {
                    System.out.println("Could not add appointment");
                    e.printStackTrace();
                }
                
            Scheduler.appointmentData.set(Scheduler.appointmentData.indexOf(appointment), appointment);
            editAppointmentStage.close();
            }
        }
    }
    
    @FXML
    private void clickedCancel() {
        editAppointmentStage.close();
    }

    public void setStage(Stage editAppointmentStage) {
        this.editAppointmentStage = editAppointmentStage;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;      
        appointmentCustomerTextField.setText(customer.getCustomerName());
    }

    private boolean isInputValid() { //CE: must also check for schedule overlap!!! and business hours!!!
        String errorMessage = "";
        
        if ((appointmentTitleTextField.getText() == null) || (appointmentTitleTextField.getText().length() == 0)) {
            errorMessage += "Invalid title\n";
        }
        if (appointmentDatePicker.getValue() == null) {
            errorMessage += "Invalid date\n";
        }
        if ((appointmentStartTextField.getText() == null) || (appointmentStartTextField.getText().length() == 0)) {
            errorMessage += "Invalid start time\n";
        }
        if ((appointmentEndTextField.getText() == null) || (appointmentEndTextField.getText().length() == 0)) {
            errorMessage += "Invalid end time\n";
        }
        
        if (errorMessage.length() != 0) {
           Alert alert = new Alert(Alert.AlertType.ERROR);
           alert.setTitle("Invalid appointment information");
           alert.setHeaderText(null);
           alert.setContentText(errorMessage);
           
           alert.showAndWait();
           errorMessage = "";
           return false;
        }

        //CE: perform more checks after we know the fields are not empty
        LocalTime openingTime = LocalTime.of(8,0);
        LocalTime closingTime = LocalTime.of(17,0);
        
        LocalDate date = null;
        LocalDateTime startDateTime = null;
        LocalTime startTime = null;
        LocalTime endTime = null;
        
        try {
             date = appointmentDatePicker.getValue();
        } catch (DateTimeParseException e) {
             errorMessage += "Could not read date\n";
        }

        try {
            startTime = DateHandler.parseTime(appointmentStartTextField.getText());
        } catch (DateTimeParseException e) {
            errorMessage += "Could not read start time\n";
        }
        
        if (date != null && startTime != null) {
            startDateTime = LocalDateTime.of(date, startTime);
        }
        
        if (startDateTime != null && startDateTime.isBefore(LocalDateTime.now())) {
            errorMessage += "Appointment must be in the future\n";
        }
        
        try {
            startTime = DateHandler.parseTime(appointmentStartTextField.getText());
            if (startTime.isBefore(openingTime)) {
                errorMessage += "Start time is before business hours\n";
            }
        } catch (DateTimeParseException e) {
             errorMessage += "Could not parse start time\n";
        }
        
        try {
            endTime = DateHandler.parseTime(appointmentEndTextField.getText());
            if (endTime.isAfter(closingTime)) {
                errorMessage += "End time is after business hours\n";
            }
        } catch (DateTimeParseException e) {
             errorMessage += "Could not parse end time\n";
         }
        
        if (startTime != null && endTime != null) {
            if (startTime.isAfter(endTime)) {
                errorMessage += "Start time must precede end time\n";
            }
        }
        
        if (startTime != null && endTime != null) {
            ObservableList<Appointment> appointments = FXCollections.observableArrayList(Scheduler.appointmentData);
            ZonedDateTime appointmentStart = DateHandler.convertToGMT(appointmentDatePicker.getValue(), DateHandler.parseTime(appointmentStartTextField.getText()));
        ZonedDateTime appointmentEnd = DateHandler.convertToGMT(appointmentDatePicker.getValue(), DateHandler.parseTime(appointmentEndTextField.getText())).minusSeconds(1);
            boolean shouldBreak = false;
            for (Appointment appt : appointments) {
                if ( appointmentStart.isAfter(appt.getAppointmentStart()) && appointmentStart.isBefore(appt.getAppointmentEnd())) {
                    errorMessage += "Start time overlaps with another appointment\n";
                    shouldBreak = true;
                }
                if ( appointmentEnd.isAfter(appt.getAppointmentStart()) && appointmentEnd.isBefore(appt.getAppointmentEnd())) {
                    errorMessage += "End time overlaps with another appointment\n";
                    shouldBreak = true;
                }
                if (shouldBreak == true) {
                    break;
                }
            }
        }
        
        if (errorMessage.length() == 0) {
            return true;
        } else {
           Alert alert = new Alert(Alert.AlertType.ERROR);
           alert.setTitle("Invalid appointment information");
           alert.setHeaderText(null);
           alert.setContentText(errorMessage);
           
           alert.showAndWait();
           errorMessage = "";
           return false;
        }
    }
    
    public void setAppointment(Appointment appointment) {
        if (appointment != null) {
            this.appointment = appointment;
            this.appointmentId = appointment.getAppointmentID();
            this.appointmentIndex = Scheduler.appointmentData.indexOf(appointment);
            
            System.out.println("Appointment: " + appointment.getAppointmentTitle() + ", Id: " + appointmentId + ", Index: " + appointmentIndex);
            
            appointmentTitleTextField.setText(appointment.getAppointmentTitle());
            
            // CE: the convertToLDT method should convert the zoned time to the proper system's time
            LocalDateTime ldt = DateHandler.convertToLDT(appointment.getAppointmentStart());
            appointmentDatePicker.setValue(ldt.toLocalDate());

             String start = DateHandler.formatTime(DateHandler.convertToLDT(appointment.getAppointmentStart()).toLocalTime());
            appointmentStartTextField.setText(start);
            String end = DateHandler.formatTime(DateHandler.convertToLDT(appointment.getAppointmentEnd()).toLocalTime());
            appointmentEndTextField.setText(end);
            
        }
    }
    
    private void setAppointmentValues(Appointment newAppointment) {
        //CE: get LocalDateTime for start and end of appointment, then convert to ZonedDateTime in UTC/GMT for storage
        ZonedDateTime appointmentStart = DateHandler.convertToGMT(appointmentDatePicker.getValue(), DateHandler.parseTime(appointmentStartTextField.getText()));
        ZonedDateTime appointmentEnd = DateHandler.convertToGMT(appointmentDatePicker.getValue(), DateHandler.parseTime(appointmentEndTextField.getText())).minusSeconds(1); //CE: subtract so as to avoid time overlap

        newAppointment.setAppointmentStart(appointmentStart);
        newAppointment.setAppointmentEnd(appointmentEnd);
        newAppointment.setAppointmentTitle(appointmentTitleTextField.getText());
        newAppointment.setAppointmentLocation(DateHandler.getDefaultLocation());
        newAppointment.setAppointmentCustomerID(customer.getCustomerID());

        //CE: we use the newly constructed ZonedDateTimes rather than the entered fields to ensure validity of dates and times
        LocalDateTime ldt1 = DateHandler.convertToLDT(appointmentStart);
        String appointmentDate = DateHandler.formatDate(ldt1.toLocalDate());          
        String appointmentStartTime = DateHandler.formatTime(ldt1.toLocalTime());

        LocalDateTime ldt2 = DateHandler.convertToLDT(appointmentEnd);
        String appointmentEndTime = DateHandler.formatTime(ldt2.toLocalTime());

        newAppointment.setAppointmentDateProperty(appointmentDate);
        newAppointment.setAppointmentStartProperty(appointmentStartTime);
        newAppointment.setAppointmentEndProperty(appointmentEndTime);
        newAppointment.setAppointmentCustomerNameProperty(customer.getCustomerName());
    }
    
}
