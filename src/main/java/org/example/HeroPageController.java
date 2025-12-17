package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HeroPageController {

    @FXML
    private void goToStudentLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/student-login.fxml"));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 400, 400));
            stage.setTitle("Student Login - Digital Clearance");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToOfficeLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/office-login.fxml"));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 400, 400));
            stage.setTitle("Office Login - Digital Clearance");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}