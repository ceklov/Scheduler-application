package scheduler.controller;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.Period;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import scheduler.DateHandler;
import scheduler.Scheduler;
import scheduler.model.Appointment;

public class TypeReportController {
    
    private Stage editCustomerStage;
    private String appointmentType;
    private Scheduler scheduler;
    
    @FXML private TableView appointmentTable;
    @FXML private TableColumn<Appointment, String> dateColumn;
    @FXML private TableColumn<Appointment, String> startColumn;
    @FXML private TableColumn<Appointment, String> endColumn;
    @FXML private TableColumn<Appointment, String> titleColumn;
    @FXML private TableColumn<Appointment, String> customerColumn;
    @FXML private TableColumn<Appointment, String> locationColumn;
    
    @FXML private Button previousMonthButton;
    @FXML private Button nextMonthButton;
    
    @FXML private TextField monthOfTextField;
    
    private final Month CURRENT_MONTH = ZonedDateTime.now().getMonth();
    private final int CURRENT_YEAR = ZonedDateTime.now().getYear();
    private final LocalDateTime FIRST_DAY_OF_CURRENT_MONTH_ZDT = LocalDateTime.of(CURRENT_YEAR, CURRENT_MONTH, 1, 0, 0, 0);
    private final ZonedDateTime FIRST_DAY_OF_CURRENT_MONTH = ZonedDateTime.of(FIRST_DAY_OF_CURRENT_MONTH_ZDT, DateHandler.getDefaultZoneId());
    private ZonedDateTime now = ZonedDateTime.of(FIRST_DAY_OF_CURRENT_MONTH_ZDT, DateHandler.getDefaultZoneId());
    private Month currentMonth = FIRST_DAY_OF_CURRENT_MONTH.getMonth();
    private Period oneMonth = Period.ofMonths(1);

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
    
    public void setAppointmentType(String appointmentType) {
        this.appointmentType = appointmentType;
    }
    
    public void showAppointments(Scheduler scheduler) {
        if (now.isBefore(FIRST_DAY_OF_CURRENT_MONTH) || now.isEqual(FIRST_DAY_OF_CURRENT_MONTH)) { 
            previousMonthButton.setVisible(false);
        } else { previousMonthButton.setVisible(true); }
        
        Comparator<Appointment> appointmentComparator = Comparator.comparing(x -> x.getAppointmentStart().truncatedTo(ChronoUnit.MINUTES));

        
        if (this.scheduler == null) { this.scheduler = scheduler; }
                
        ObservableList<Appointment> filteredAppointments = FXCollections.observableArrayList();
        scheduler.getAppointmentData().stream()
                .filter(x -> x.getAppointmentTitle().equals(appointmentType) && x.getAppointmentStart().getMonth().equals(currentMonth))
                .sorted(appointmentComparator)
                .forEach(x -> filteredAppointments.add(x));
        
        appointmentTable.setItems(filteredAppointments);
        monthOfTextField.setText(String.valueOf(currentMonth));
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Report");
        alert.setHeaderText("Report for " + appointmentType);
        alert.setContentText("There are " + filteredAppointments.size() + " " + appointmentType + " appointments in " + currentMonth);
        alert.showAndWait();
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
