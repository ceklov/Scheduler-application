package scheduler.DAO;

import com.mysql.jdbc.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import scheduler.DBHandler;
import scheduler.DateHandler;
import scheduler.Scheduler;
import scheduler.model.Appointment;

public class AppointmentDAO {
         
     public static void addAppointment(Appointment appointment) throws SQLException {
         String createDate = DateHandler.format(LocalDateTime.now());
                 
         String statement = "INSERT INTO appointment ( "
                 + "appointmentId, "
                 + "customerId, "
                 + "title, "
                 + "description, "
                 + "location, "
                 + "contact, "
                 + "url, "
                 + "start, "
                 + "end, "
                 + "createDate, "
                 + "createdBy, "
                 + "lastUpdate, "
                 + "lastUpdateBy ) VALUES ( "
                 + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
         
        try (Connection conn = DBHandler.getConnection();           
            PreparedStatement ps = conn.prepareStatement(statement) ) {
                
             ps.setString(1, String.valueOf(appointment.getAppointmentID()));
             ps.setString(2, String.valueOf(appointment.getAppointmentCustomerID()));
             ps.setString(3, appointment.getAppointmentTitle());
             ps.setString(4, "");
             ps.setString(5, appointment.getAppointmentLocation());
             ps.setString(6, "");
             ps.setString(7, "");
             //CE: first convert the returned ZonedDateTime to LocalDateTime because the database will not accept zones, then convert it to a string
             ps.setString(8, DateHandler.format(DateHandler.convertToLDTinGMT(appointment.getAppointmentStart()))); //CE: may be a problem with Date object types
             ps.setString(9, DateHandler.format(DateHandler.convertToLDTinGMT(appointment.getAppointmentEnd()))); //CE: here too
             ps.setString(10, createDate);
             ps.setString(11, Scheduler.currentUser);
             ps.setString(12, createDate);
             ps.setString(13, Scheduler.currentUser);

             ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("Problem in addAppointment()");
            e.printStackTrace();
        }        
     }
     
     public static void loadAppointmentData() throws SQLException {
         
         ArrayList<String[]> appointmentValues = getAppointmentValues();
         
         for (int i = 0; i < appointmentValues.size(); i++) {
             Appointment appointment = new Appointment();
             
             String[] appointmentArray = appointmentValues.get(i);

             int customerID = Integer.parseInt(appointmentArray[1]);
             appointment.setAppointmentID(Integer.parseInt(appointmentArray[0]));
             appointment.setAppointmentCustomerID(customerID);
             appointment.setAppointmentTitle(appointmentArray[2]);
             appointment.setAppointmentLocation(appointmentArray[3]);
             //CE: first get the String from the ArrayList, then parse it as a LocalDateTime, finally add zone
             String start = appointmentArray[4].substring(0, appointmentArray[4].length() - 2);
             String end = appointmentArray[5].substring(0, appointmentArray[5].length() - 2);
             ZonedDateTime startZDT = DateHandler.formatZDT(start);
             ZonedDateTime endZDT = DateHandler.formatZDT(end);
             
             appointment.setAppointmentStart(startZDT);
             appointment.setAppointmentEnd(endZDT);
             
             LocalDateTime ldt1 = DateHandler.convertToLDT(startZDT);
             String appointmentDate = DateHandler.formatDate(ldt1.toLocalDate());          
             String appointmentStartTime = DateHandler.formatTime(ldt1.toLocalTime());
            
             LocalDateTime ldt2 = DateHandler.convertToLDT(endZDT);
             String appointmentEndTime = DateHandler.formatTime(ldt2.toLocalTime());
             
             appointment.setAppointmentDateProperty(appointmentDate);
             appointment.setAppointmentStartProperty(appointmentStartTime);
             appointment.setAppointmentEndProperty(appointmentEndTime);
             appointment.setAppointmentCustomerNameProperty(CustomerDAO.getCustomerName(customerID));
             
             Scheduler.appointmentData.add(appointment);
         }

     }

    private static ArrayList<String[]> getAppointmentValues() throws SQLException {
        
        ArrayList<String[]> appointmentValues = new ArrayList<>();
        
        try (Connection conn = DBHandler.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT appointmentId, customerId, title, location, start, end FROM appointment") ) {
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String[] array = {String.valueOf(rs.getInt("appointmentId")), String.valueOf(rs.getInt("customerId")), rs.getString("title"), rs.getString("location"), rs.getString("start"), rs.getString("end")};
                    appointmentValues.add(array);
                }
            }
        } catch (Exception e) {
            System.out.println("Problem in getAppointmentValues()");
        }
        return appointmentValues;   
    }
    
    public static void deleteAppointment(Appointment appointment) throws SQLException {
        
        try (Connection conn = DBHandler.getConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM appointment WHERE appointmentId = ?") ) {  
            ps.setString(1, String.valueOf(appointment.getAppointmentID()));
            ps.executeUpdate();
        }
    }

    public static void updateAppointment(Appointment appointment) throws SQLException {

        try (Connection conn = DBHandler.getConnection();
            PreparedStatement ps = conn.prepareStatement("UPDATE appointment SET title = ?, location = ?, start = ?, end = ?, lastUpdate = ?, lastUpdateBy = ? WHERE appointmentId = ?") ) {

            ps.setString(1, appointment.getAppointmentTitle());
            ps.setString(2, appointment.getAppointmentLocation());
            ps.setString(3, DateHandler.format(DateHandler.convertToLDTinGMT(appointment.getAppointmentStart())));
            ps.setString(4, DateHandler.format(DateHandler.convertToLDTinGMT(appointment.getAppointmentEnd())));
            ps.setString(5, DateHandler.format(LocalDateTime.now()));
            ps.setString(6, Scheduler.currentUser);
            ps.setString(7, String.valueOf(appointment.getAppointmentID()));
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("Problem in updateAppointment()");
        }
    }
     
}
