package scheduler;   

import java.time.ZonedDateTime;
import javafx.scene.control.Alert;
import scheduler.model.Appointment;

public class AlertHandler {
    
    public static void setAlertData() {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime fifteenMinutesFromNow = now.plusMinutes(15);
        
        Scheduler.appointmentData.stream()
                .filter(x -> x.getAppointmentStart().isAfter(now))
                .filter(x -> x.getAppointmentStart().isBefore(fifteenMinutesFromNow))
                .forEach(x -> {
                    getAlert(x).showAndWait();
                });
    }
        
    public static Alert getAlert(Appointment appointment) {  
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Upcoming Appointment");
        alert.setHeaderText("There is an appointment beginning very soon:");
        alert.setContentText("Customer: " + appointment.getAppointmentCustomerName() +
                "\nType/Title: " + appointment.getAppointmentTitle() +
                "\nStart: (Local) " + DateHandler.convertToLDT(appointment.getAppointmentStart()) +
                "\nEnd: (Local) " + DateHandler.convertToLDT(appointment.getAppointmentEnd()) +
                "\nLocation: " + appointment.getAppointmentLocation());
        return alert;
    }
    
}
