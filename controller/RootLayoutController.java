package scheduler.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class RootLayoutController {

    @FXML
    public void initialize() {
        // TODO
    }   

    @FXML
    private void handleClose(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    private void handleAbout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Scheduler Application for C195");
        alert.setContentText("Created by Cody Eklov\nceklov@wgu.edu");
        alert.show();
    }
    
}
