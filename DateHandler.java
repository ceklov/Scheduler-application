package scheduler;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateHandler {
    
     private static final DateTimeFormatter DATE_TIME_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
     private static final DateTimeFormatter DATE_PATTERN = DateTimeFormatter.ofPattern("MM/dd/yyyy");
     private static final DateTimeFormatter TIME_PATTERN = DateTimeFormatter.ofPattern("HH:mm");
     
     public static final DateTimeFormatter MONTH_DAY_PATTERN = DateTimeFormatter.ofPattern("MMMM dd");
     
     private static final ZoneId NY_ZONE = ZoneId.of("America/New_York");
     private static final ZoneId PHOENIX_ZONE = ZoneId.of("America/Phoenix");
     private static final ZoneId LONDON_ZONE = ZoneId.of("Europe/London");
     
     public static final ZoneId GMT_ZONE = ZoneId.of("GMT");

     //public static String convertTo/FromLocalTime()
     
     public static String format(LocalDateTime dateTime) {
         if (dateTime == null) {
             return null;
         } else return DATE_TIME_PATTERN.format(dateTime);
     }
     
     public static String formatTime(LocalTime time) {
         if (time == null) {
             return null;
         } else return TIME_PATTERN.format(time);
     }
     
     public static String formatDate(LocalDate date) {
         if (date == null) {
             return null;
         } else return DATE_PATTERN.format(date);
     }
     
     public static LocalDateTime parse(String dateTimeString) {
         try {
             return DATE_TIME_PATTERN.parse(dateTimeString, LocalDateTime::from);
         } catch (DateTimeParseException e) {
             e.printStackTrace();
             return null;
         }
     }
     
     public static LocalTime parseTime(String timeString) {
         return TIME_PATTERN.parse(timeString, LocalTime::from);
     }
     
     public static LocalDateTime convert(Timestamp timestamp) {
         return LocalDateTime.ofInstant(timestamp.toInstant(), ZoneOffset.ofHours(0));
     }
     
     public static ZonedDateTime convertToGMT(LocalDate appointmentDate, LocalTime localTime) {
         ZoneId zoneID = getDefaultZoneId();
         ZonedDateTime zonedDateTime = null;
         ZonedDateTime appointmentDateTime = null;
         
         /*CE: because I am not in one of the three zones described in this assignment, I wrote (but commented out) the code to default to Phoenix for testing
         if (!zoneID.equals(DateHandler.NY_ZONE) && !zoneID.equals(DateHandler.PHOENIX_ZONE) && !zoneID.equals(DateHandler.LONDON_ZONE)) {
             zonedDateTime = ZonedDateTime.of(appointmentDate, localTime, DateHandler.PHOENIX_ZONE);
             appointmentDateTime = zonedDateTime.withZoneSameInstant(DateHandler.GMT_ZONE);
         } else {    */     
             zonedDateTime = ZonedDateTime.of(appointmentDate, localTime, getDefaultZoneId());
             appointmentDateTime = zonedDateTime.withZoneSameInstant(DateHandler.GMT_ZONE);
         //}
         return appointmentDateTime;
     }
     
     public static ZonedDateTime formatZDT(String appointment) {
         return ZonedDateTime.of(parse(appointment), DateHandler.GMT_ZONE);
     }
     
     /*CE: public static ZonedDateTime convertZDTToDefault(ZonedDateTime appointmentZDTInGMT, ZoneId zoneID) {
         ZonedDateTime appointmentZDTInDefault = appointmentZDTInGMT.withZoneSameInstant(zoneID);
         System.out.println(appointmentZDTInGMT + " converted to " + appointmentZDTInDefault);
         return appointmentZDTInDefault;
     }*/
     
     public static LocalDateTime convertToLDTinGMT(ZonedDateTime zonedAppointment) {
         Instant instant = zonedAppointment.toInstant();
         LocalDateTime appointment = LocalDateTime.ofInstant(instant, GMT_ZONE); //CE: because all ZonedDateTimes are stored in GMT/UTC zone
         return appointment;
     }
     
     public static LocalDateTime convertToLDT(ZonedDateTime zonedAppointment) {
         Instant instant = zonedAppointment.toInstant();
         ZoneId zoneID = getDefaultZoneId();
         LocalDateTime appointment = null;
         
         /*CE: if (!zoneID.equals(DateHandler.NY_ZONE) && !zoneID.equals(DateHandler.PHOENIX_ZONE) && !zoneID.equals(DateHandler.LONDON_ZONE)) {
             appointment = LocalDateTime.ofInstant(instant, PHOENIX_ZONE); //CE: because I have chosen Phoenix for testing and I am not in any of the three required timezones
         } else {*/
             appointment = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
         //}
         return appointment;
     }
     
     public static ZoneId getDefaultZoneId() {
         ZoneId zoneID = ZoneId.systemDefault();
         
         /*CE: if (!zoneID.equals(DateHandler.NY_ZONE) && !zoneID.equals(DateHandler.PHOENIX_ZONE) && !zoneID.equals(DateHandler.LONDON_ZONE)) {
             zoneID = DateHandler.PHOENIX_ZONE;
         }*/
         return zoneID;
     }

     public static String getDefaultLocation() {
         ZoneId zoneID = getDefaultZoneId();
         String defaultLocation = null;

         if (!zoneID.equals(DateHandler.NY_ZONE) && !zoneID.equals(DateHandler.PHOENIX_ZONE) && !zoneID.equals(DateHandler.LONDON_ZONE)) {
             defaultLocation = "Phoenix";
         } else if (zoneID.equals(DateHandler.NY_ZONE)) {         
             defaultLocation = "New York";
         } else if (zoneID.equals(DateHandler.PHOENIX_ZONE)) {         
             defaultLocation = "Phoenix";
         } else if (zoneID.equals(DateHandler.LONDON_ZONE)) {         
             defaultLocation = "London";
         }
         return defaultLocation;
     }
}
