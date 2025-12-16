package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;       // For FXMLLoader
import javafx.scene.Parent;         // For Parent
import javafx.scene.Scene;          // For Scene
import javafx.stage.Stage;          // For Stage


public class StudentProfileController {

    @FXML private Label titleLabel;
    @FXML private TextField studentIdField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField sexField;
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;

    private String studentId;

    public void initializeProfile(String studentId, String studentName) {
        this.studentId = studentId;
        
        titleLabel.setText("Profile - " + studentName);
        loadStudentData();
    }

    private void loadStudentData() {
        try (Connection conn = DB.getConnection()) {
            String sql = "SELECT * FROM students WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, studentId);
            var rs = pstmt.executeQuery();
            
            if (rs.next()) {
                studentIdField.setText(rs.getString("id"));
                firstNameField.setText(rs.getString("first_name"));
                lastNameField.setText(rs.getString("last_name"));
                sexField.setText(rs.getString("sex"));
            }
            
        } catch (Exception e) {
            showAlert("Error", "Error loading profile: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdatePassword() {
        String currentPassword = currentPasswordField.getText().trim();
        String newPassword = newPasswordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Error", "Please fill all password fields");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showAlert("Error", "New passwords do not match");
            return;
        }

        if (newPassword.length() < 4) {
            showAlert("Error", "Password must be at least 4 characters");
            return;
        }

        try (Connection conn = DB.getConnection()) {
            // Verify current password
            String verifySql = "SELECT password FROM students WHERE id = ?";
            PreparedStatement verifyStmt = conn.prepareStatement(verifySql);
            verifyStmt.setString(1, studentId);
            var rs = verifyStmt.executeQuery();
            
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                
                if (!storedPassword.equals(currentPassword)) {
                    showAlert("Error", "Current password is incorrect");
                    return;
                }
                
                // Update password
                String updateSql = "UPDATE students SET password = ? WHERE id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setString(1, newPassword);
                updateStmt.setString(2, studentId);
                
                int rowsAffected = updateStmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    showAlert("Success", "Password updated successfully!");
                    currentPasswordField.clear();
                    newPasswordField.clear();
                    confirmPasswordField.clear();
                }
            }
            
        } catch (Exception e) {
            showAlert("Error", "Error updating password: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/student-dashboard.fxml")
            );
            Parent root = loader.load();

            // Pass student data back
            StudentDashboardController controller = loader.getController();
            controller.initializeStudent(studentId,
                    firstNameField.getText() + " " + lastNameField.getText());

            Stage stage = (Stage) titleLabel.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 650));
            stage.setTitle("Student Dashboard - Digital Clearance");

        } catch (Exception e) {
            showAlert("Error", "Failed to go back: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
