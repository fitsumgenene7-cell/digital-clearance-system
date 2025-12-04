package org.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class StudentDashboardController {

    @FXML private Label studentNameLabel;
    @FXML private Label studentIdLabel;
    @FXML private Label clearanceStatusLabel;
    @FXML private TableView<OfficeStatus> statusTable;
    @FXML private TableColumn<OfficeStatus, String> officeColumn;
    @FXML private TableColumn<OfficeStatus, String> statusColumn;
    @FXML private TableColumn<OfficeStatus, String> reasonColumn;
    @FXML private TableColumn<OfficeStatus, String> dateColumn;
    @FXML private VBox warningsBox;

    private String studentId;
    private String studentName;

    public void initializeStudent(String studentId, String studentName) {
        this.studentId = studentId;
        this.studentName = studentName;
        
        studentNameLabel.setText("Welcome, " + studentName + "!");
        studentIdLabel.setText("ID: " + studentId);
        
        initializeTable();
        loadClearanceStatus();
        loadWarnings();
    }

    private void initializeTable() {
        officeColumn.setCellValueFactory(new PropertyValueFactory<>("officeName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        reasonColumn.setCellValueFactory(new PropertyValueFactory<>("reason"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("formattedDate"));
        
        // Set column widths
        officeColumn.setPrefWidth(150);
        statusColumn.setPrefWidth(100);
        reasonColumn.setPrefWidth(250);
        dateColumn.setPrefWidth(150);
    }

    private void loadClearanceStatus() {
        try (Connection conn = DB.getConnection()) {
            // Get all offices
            List<OfficeStatus> statusList = new ArrayList<>();
            
            String officesSql = "SELECT office_name FROM offices";
            PreparedStatement officesStmt = conn.prepareStatement(officesSql);
            ResultSet officesRs = officesStmt.executeQuery();
            
            while (officesRs.next()) {
                String officeName = officesRs.getString("office_name");
                OfficeStatus status = getOfficeStatus(conn, officeName);
                statusList.add(status);
            }
            
            // Update table
            statusTable.getItems().setAll(statusList);
            
            // Calculate overall clearance status
            calculateOverallStatus(statusList);
            
        } catch (SQLException e) {
            showAlert("Error", "Error loading clearance status: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private OfficeStatus getOfficeStatus(Connection conn, String officeName) throws SQLException {
        String sql = "SELECT status, reason, created_at FROM penalties WHERE student_id = ? AND office_name = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, studentId);
        pstmt.setString(2, officeName);
        ResultSet rs = pstmt.executeQuery();
        
        if (rs.next()) {
            return new OfficeStatus(
                officeName,
                rs.getString("status"),
                rs.getString("reason"),
                rs.getString("created_at")
            );
        } else {
            // Default status for each office
            String defaultStatus = "PENDING";
            String defaultReason = "Not yet reviewed";
            
            // Special case for Dormitory
            if ("Dormitory".equals(officeName)) {
                defaultStatus = "DENIED";
                defaultReason = "Not cleared by dormitory (Default)";
            }
            
            return new OfficeStatus(officeName, defaultStatus, defaultReason, "");
        }
    }

    private void calculateOverallStatus(List<OfficeStatus> statusList) {
        boolean allApproved = true;
        boolean hasDenied = false;
        boolean hasWarning = false;
        
        for (OfficeStatus status : statusList) {
            if ("DENIED".equals(status.getStatus())) {
                hasDenied = true;
                break;
            } else if ("WARNING".equals(status.getStatus())) {
                hasWarning = true;
            } else if (!"APPROVED".equals(status.getStatus())) {
                allApproved = false;
            }
        }
        
        String overallStatus;
        String statusStyle;
        
        if (hasDenied) {
            overallStatus = "CLEARANCE DENIED";
            statusStyle = "-fx-text-fill: red; -fx-font-weight: bold;";
        } else if (hasWarning) {
            overallStatus = "HAS WARNINGS";
            statusStyle = "-fx-text-fill: orange; -fx-font-weight: bold;";
        } else if (allApproved) {
            overallStatus = "CLEARED";
            statusStyle = "-fx-text-fill: green; -fx-font-weight: bold;";
        } else {
            overallStatus = "IN PROGRESS";
            statusStyle = "-fx-text-fill: blue; -fx-font-weight: bold;";
        }
        
        clearanceStatusLabel.setText(overallStatus);
        clearanceStatusLabel.setStyle(statusStyle);
    }

    private void loadWarnings() {
        warningsBox.getChildren().clear();
        
        try (Connection conn = DB.getConnection()) {
            String sql = "SELECT office_name, reason, created_at FROM penalties " +
                        "WHERE student_id = ? AND status = 'WARNING' " +
                        "ORDER BY created_at DESC";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            int warningCount = 0;
            while (rs.next()) {
                warningCount++;
                String officeName = rs.getString("office_name");
                String reason = rs.getString("reason");
                String date = rs.getString("created_at");
                
                Label warningLabel = new Label(
                    "⚠️ " + officeName + ": " + reason + " (" + date + ")"
                );
                warningLabel.setWrapText(true);
                warningLabel.setStyle("-fx-text-fill: orange; -fx-padding: 5px;");
                warningsBox.getChildren().add(warningLabel);
            }
            
            if (warningCount == 0) {
                Label noWarningsLabel = new Label("No warnings issued.");
                noWarningsLabel.setStyle("-fx-text-fill: green; -fx-padding: 5px;");
                warningsBox.getChildren().add(noWarningsLabel);
            }
            
        } catch (SQLException e) {
            Label errorLabel = new Label("Error loading warnings: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red; -fx-padding: 5px;");
            warningsBox.getChildren().add(errorLabel);
        }
    }

    @FXML
    private void handleRefresh() {
        loadClearanceStatus();
        loadWarnings();
        showAlert("Success", "Dashboard refreshed!");
    }

    @FXML
    private void handleViewProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/student-profile.fxml"));
            Parent root = loader.load();
            
            StudentProfileController controller = loader.getController();
            controller.initializeProfile(studentId, studentName);
            
            Stage stage = (Stage) studentNameLabel.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 500));
            stage.setTitle("Student Profile - Digital Clearance");
            
        } catch (Exception e) {
            showAlert("Error", "Error loading profile: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePrintClearance() {
        // Generate clearance summary
        StringBuilder summary = new StringBuilder();
        summary.append("=== CLEARANCE SUMMARY ===\n");
        summary.append("Student: ").append(studentName).append("\n");
        summary.append("ID: ").append(studentId).append("\n");
        summary.append("Date: ").append(LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n\n");
        summary.append("OFFICE STATUS:\n");
        
        for (OfficeStatus status : statusTable.getItems()) {
            summary.append(String.format("%-20s: %-10s - %s\n", 
                status.getOfficeName(), 
                status.getStatus(), 
                status.getReason()));
        }
        
        summary.append("\nOVERALL STATUS: ").append(clearanceStatusLabel.getText());
        
        // Show in alert (in real app, this could print or save to file)
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Clearance Summary");
        alert.setHeaderText("Your Clearance Status");
        alert.setContentText(summary.toString());
        alert.showAndWait();
    }

    @FXML
    private void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/student-login.fxml"));
            Stage stage = (Stage) studentNameLabel.getScene().getWindow();
            stage.setScene(new Scene(root, 400, 400));
            stage.setTitle("Student Login - Digital Clearance");
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

    // Model class for table data
    public static class OfficeStatus {
        private String officeName;
        private String status;
        private String reason;
        private String date;
        private String formattedDate;

        public OfficeStatus(String officeName, String status, String reason, String date) {
            this.officeName = officeName;
            this.status = status;
            this.reason = reason;
            this.date = date;
            this.formattedDate = formatDate(date);
        }

        private String formatDate(String dateStr) {
            if (dateStr == null || dateStr.isEmpty()) {
                return "N/A";
            }
            try {
                // Format date string for display
                return dateStr.replace("T", " ").substring(0, Math.min(16, dateStr.length()));
            } catch (Exception e) {
                return dateStr;
            }
        }

        public String getOfficeName() { return officeName; }
        public String getStatus() { return status; }
        public String getReason() { return reason; }
        public String getDate() { return date; }
        public String getFormattedDate() { return formattedDate; }
    }
}