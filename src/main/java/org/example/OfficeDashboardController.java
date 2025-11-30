package org.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OfficeDashboardController {

    @FXML private Label officeTitle;
    @FXML private Label welcomeLabel;
    @FXML private TextField searchField;
    @FXML private Label studentInfoLabel;
    @FXML private VBox reasonSection;
    @FXML private TextArea reasonTextArea;
    @FXML private Button denyButton;

    private String currentOfficeName;
    private String currentStudentId;

    public void initializeOffice(String officeName) {
        this.currentOfficeName = officeName;
        officeTitle.setText(officeName + " Dashboard");
        welcomeLabel.setText("Welcome, " + officeName + " Administrator!");
        studentInfoLabel.setText("No student selected");
        reasonSection.setVisible(false);

        // Show deny button only for Dormitory
        denyButton.setVisible("Dormitory".equals(officeName));
    }

    @FXML
    private void handleSearch() {
        String studentId = searchField.getText().trim();
        if (studentId.isEmpty()) {
            showAlert("Error", "Please enter a student ID");
            return;
        }

        searchStudent(studentId);
    }

    private void searchStudent(String studentId) {
        try (Connection conn = DB.getConnection()) {
            // Search for student
            String studentSql = "SELECT * FROM students WHERE id = ?";
            PreparedStatement studentStmt = conn.prepareStatement(studentSql);
            studentStmt.setString(1, studentId);
            ResultSet studentRs = studentStmt.executeQuery();

            if (studentRs.next()) {
                currentStudentId = studentId;
                String studentName = studentRs.getString("first_name") + " " + studentRs.getString("last_name");

                // Check current status with this office
                String status = getStudentStatus(conn, studentId);

                // Display student information
                studentInfoLabel.setText(
                        "Student: " + studentName + " (" + studentId + ")\n" +
                                "Status: " + status + "\n" +
                                "Office: " + currentOfficeName
                );

                showAlert("Student Found",
                        "Student: " + studentName + "\n" +
                                "ID: " + studentId + "\n" +
                                "Current Status: " + status + "\n\n" +
                                "You can now use the action buttons to manage this student's clearance.");

            } else {
                currentStudentId = null;
                studentInfoLabel.setText("Student not found");
                showAlert("Not Found", "Student with ID " + studentId + " not found");
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Error searching for student: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getStudentStatus(Connection conn, String studentId) throws SQLException {
        String statusSql = "SELECT status, reason FROM penalties WHERE student_id = ? AND office_name = ?";
        PreparedStatement statusStmt = conn.prepareStatement(statusSql);
        statusStmt.setString(1, studentId);
        statusStmt.setString(2, currentOfficeName);
        ResultSet statusRs = statusStmt.executeQuery();

        if (statusRs.next()) {
            String status = statusRs.getString("status");
            String reason = statusRs.getString("reason");
            return status + " - " + reason;
        } else {
            // Default status based on office type
            if ("Dormitory".equals(currentOfficeName)) {
                return "DENIED - Not cleared by dormitory (Default)";
            } else {
                return "APPROVED - No issues (Default)";
            }
        }
    }

    @FXML
    private void handleIssueWarning() {
        if (currentStudentId == null) {
            showAlert("Error", "Please search for a student first");
            return;
        }
        reasonSection.setVisible(true);
    }

    @FXML
    private void handleSubmitWarning() {
        String reason = reasonTextArea.getText().trim();
        if (reason.isEmpty()) {
            showAlert("Error", "Please enter a reason for the warning");
            return;
        }

        try (Connection conn = DB.getConnection()) {
            String sql = """
                INSERT OR REPLACE INTO penalties (student_id, office_name, status, reason) 
                VALUES (?, ?, 'WARNING', ?)
            """;

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, currentStudentId);
            pstmt.setString(2, currentOfficeName);
            pstmt.setString(3, reason);

            pstmt.executeUpdate();

            showAlert("Success", "Warning issued to student " + currentStudentId);
            reasonSection.setVisible(false);
            reasonTextArea.clear();

            // Refresh student info
            searchStudent(currentStudentId);

        } catch (SQLException e) {
            showAlert("Error", "Error issuing warning: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleApproveStudent() {
        if (currentStudentId == null) {
            showAlert("Error", "Please search for a student first");
            return;
        }

        try (Connection conn = DB.getConnection()) {
            String sql = """
                INSERT OR REPLACE INTO penalties (student_id, office_name, status, reason) 
                VALUES (?, ?, 'APPROVED', ?)
            """;

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, currentStudentId);
            pstmt.setString(2, currentOfficeName);

            // Different reason based on office
            if ("Dormitory".equals(currentOfficeName)) {
                pstmt.setString(3, "Cleared by dormitory chief");
            } else {
                pstmt.setString(3, "Cleared by office");
            }

            pstmt.executeUpdate();

            showAlert("Success", "Student " + currentStudentId + " approved by " + currentOfficeName + "!");

            // Refresh student info
            searchStudent(currentStudentId);

        } catch (SQLException e) {
            showAlert("Error", "Error approving student: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDenyClearance() {
        // This should only be available for Dormitory
        if (!"Dormitory".equals(currentOfficeName)) {
            showAlert("Error", "Only Dormitory office can deny clearance");
            return;
        }

        if (currentStudentId == null) {
            showAlert("Error", "Please search for a student first");
            return;
        }

        try (Connection conn = DB.getConnection()) {
            String sql = """
                INSERT OR REPLACE INTO penalties (student_id, office_name, status, reason) 
                VALUES (?, ?, 'DENIED', ?)
            """;

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, currentStudentId);
            pstmt.setString(2, currentOfficeName);
            pstmt.setString(3, "Not cleared by dormitory");

            pstmt.executeUpdate();

            showAlert("Notice", "Clearance denied for student " + currentStudentId);

            // Refresh student info
            searchStudent(currentStudentId);

        } catch (SQLException e) {
            showAlert("Error", "Error denying clearance: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClearWarning() {
        if (currentStudentId == null) {
            showAlert("Error", "Please search for a student first");
            return;
        }

        try (Connection conn = DB.getConnection()) {
            String sql = "DELETE FROM penalties WHERE student_id = ? AND office_name = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, currentStudentId);
            pstmt.setString(2, currentOfficeName);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                showAlert("Success", "Warning cleared for student " + currentStudentId);
            } else {
                showAlert("Info", "No warning found to clear for this student");
            }

            // Refresh student info
            searchStudent(currentStudentId);

        } catch (SQLException e) {
            showAlert("Error", "Error clearing warning: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/office-login.fxml"));
            Stage stage = (Stage) officeTitle.getScene().getWindow();
            stage.setScene(new Scene(root, 400, 400));
            stage.setTitle("Office Login - Digital Clearance");
        } catch (Exception e) {
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