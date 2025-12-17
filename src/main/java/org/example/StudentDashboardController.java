package org.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableCell;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    // -------------------------------------------------
    // INITIALIZATION
    // -------------------------------------------------
    public void initializeStudent(String studentId, String studentName) {
        this.studentId = studentId;
        this.studentName = studentName;

        studentNameLabel.setText("Welcome, " + studentName + "!");
        studentIdLabel.setText("ID: " + studentId);

        initializeTable();
        loadClearanceStatus();
        loadWarnings();
    }

    private void colorStatusColumn() {
        statusColumn.setCellFactory(column -> new TableCell<OfficeStatus, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                    return;
                }

                setText(status);

                switch (status) {
                    case "APPROVED":
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                        break;

                    case "WARNING":
                        setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                        break;

                    case "DENIED":
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                        break;

                    case "PENDING":
                        setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
                        break;

                    default:
                        setStyle("-fx-text-fill: black;");
                }
            }
        });
    }


    private void initializeTable() {
        officeColumn.setCellValueFactory(new PropertyValueFactory<>("officeName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        reasonColumn.setCellValueFactory(new PropertyValueFactory<>("reason"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("formattedDate"));

        officeColumn.setPrefWidth(150);
        statusColumn.setPrefWidth(100);
        reasonColumn.setPrefWidth(250);
        dateColumn.setPrefWidth(150);

        colorStatusColumn();
    }

    // -------------------------------------------------
    // LOAD DATA
    // -------------------------------------------------
    private void loadClearanceStatus() {
        try (Connection conn = DB.getConnection()) {

            List<OfficeStatus> statusList = new ArrayList<>();

            String officesSql = "SELECT office_name FROM offices";
            PreparedStatement officesStmt = conn.prepareStatement(officesSql);
            ResultSet officesRs = officesStmt.executeQuery();

            while (officesRs.next()) {
                String officeName = officesRs.getString("office_name");
                OfficeStatus status = getOfficeStatus(conn, officeName);
                statusList.add(status);
            }

            statusTable.getItems().setAll(statusList);
            calculateOverallStatus(statusList);

        } catch (SQLException e) {
            showAlert("Error", "Error loading clearance status: " + e.getMessage());
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
            String defaultStatus = "APPROVED";
            String defaultReason = "Not yet reviewed";

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
            switch (status.getStatus()) {
                case "DENIED":
                    hasDenied = true;
                    break;
                case "WARNING":
                    hasWarning = true;
                    break;
                case "APPROVED":
                    break;
                default:
                    allApproved = false;
            }
        }

        String overallStatus;
        String style;

        if (hasDenied) {
            overallStatus = "CLEARANCE DENIED";
            style = "-fx-text-fill: red; -fx-font-weight: bold;";
        } else if (hasWarning) {
            overallStatus = "HAS WARNINGS";
            style = "-fx-text-fill: orange; -fx-font-weight: bold;";
        } else if (allApproved) {
            overallStatus = "CLEARED";
            style = "-fx-text-fill: green; -fx-font-weight: bold;";
        } else {
            overallStatus = "IN PROGRESS";
            style = "-fx-text-fill: blue; -fx-font-weight: bold;";
        }

        clearanceStatusLabel.setText(overallStatus);
        clearanceStatusLabel.setStyle(style);
    }

    private void loadWarnings() {
        warningsBox.getChildren().clear();

        try (Connection conn = DB.getConnection()) {
            String sql = "SELECT office_name, reason, created_at FROM penalties " +
                    "WHERE student_id = ? AND status = 'WARNING' ORDER BY created_at DESC";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            int count = 0;

            while (rs.next()) {
                count++;
                Label warningLabel = new Label(
                        "⚠️ " + rs.getString("office_name") + ": " +
                                rs.getString("reason") + " (" + rs.getString("created_at") + ")"
                );
                warningLabel.setWrapText(true);
                warningLabel.setStyle("-fx-text-fill: orange; -fx-padding: 5px;");
                warningsBox.getChildren().add(warningLabel);
            }

            if (count == 0) {
                Label noWarnings = new Label("No warnings issued.");
                noWarnings.setStyle("-fx-text-fill: green; -fx-padding: 5px;");
                warningsBox.getChildren().add(noWarnings);
            }

        } catch (SQLException e) {
            Label error = new Label("Error loading warnings: " + e.getMessage());
            error.setStyle("-fx-text-fill: red;");
            warningsBox.getChildren().add(error);
        }
    }

    // -------------------------------------------------
    // BUTTON HANDLERS
    // -------------------------------------------------
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
        }
    }

    @FXML
    private void handlePrintClearance() {
        StringBuilder summary = new StringBuilder();

        summary.append("=== CLEARANCE SUMMARY ===\n");
        summary.append("Student: ").append(studentName).append("\n");
        summary.append("ID: ").append(studentId).append("\n\n");

        summary.append("OFFICE STATUS:\n");
        for (OfficeStatus status : statusTable.getItems()) {
            summary.append(String.format("%-20s: %-10s - %s\n",
                    status.getOfficeName(), status.getStatus(), status.getReason()));
        }

        summary.append("\nOVERALL STATUS: ").append(clearanceStatusLabel.getText());

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

    // -------------------------------------------------
    // CERTIFICATE GENERATION
    // -------------------------------------------------
    // StudentDashboardController.java

// ... (Around line 265)

    @FXML
    private void handleGenerateClearanceCertificate() {
        if (!canGenerateCertificate()) {
            showAlert("Cannot Generate Certificate",
                    "You cannot generate a clearance certificate yet.\n" +
                            "All offices must be APPROVED. No warnings or denials.");
            return;
        }

        try {
            String certificateContent = generateCertificateContent();

            // --- MODIFICATION START ---
            // 1. Create a safe version of studentId by replacing '/' with '-'
            //    to avoid "No such file or directory" error (2213/16 is invalid)
            String safeStudentId = studentId.replace("/", "-");

            // 2. Use the safeStudentId to construct the file name
            String fileName = "Clearance_Certificate_" + safeStudentId + "_" +
                    LocalDate.now().toString().replace("-", "") + ".txt";
            // --- MODIFICATION END ---

            try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
                writer.println(certificateContent);
            }

            showAlert("Clearance Certificate Generated",
                    "Certificate saved as: " + fileName);

        } catch (Exception e) {
            showAlert("Error", "Failed to generate certificate: " + e.getMessage());
        }
    }

    private boolean canGenerateCertificate() {
        for (OfficeStatus status : statusTable.getItems()) {
            if (!"APPROVED".equals(status.getStatus())) {
                return false;
            }
        }
        return clearanceStatusLabel.getText().equals("CLEARED");
    }

    private String generateCertificateContent() {
        StringBuilder cert = new StringBuilder();

        cert.append("╔════════════════════════════════════════════════════════════╗\n");
        cert.append("║                 OFFICIAL CLEARANCE CERTIFICATE             ║\n");
        cert.append("║                   DIGITAL CLEARANCE SYSTEM                 ║\n");
        cert.append("╚════════════════════════════════════════════════════════════╝\n\n");

        cert.append("CERTIFICATE NO: CLR-").append(studentId).append("-").append(LocalDate.now().getYear()).append("\n");
        cert.append("ISSUE DATE: ").append(LocalDate.now()).append("\n");
        cert.append("VALID UNTIL: ").append(LocalDate.now().plusMonths(6)).append("\n\n");

        cert.append("STUDENT NAME: ").append(studentName).append("\n");
        cert.append("STUDENT ID:   ").append(studentId).append("\n");
        cert.append("ISSUED ON:    ").append(LocalDateTime.now()).append("\n\n");

        cert.append("OFFICE STATUS:\n");
        cert.append(String.format("%-25s %-15s %-30s\n", "OFFICE", "STATUS", "REASON"));
        cert.append("--------------------------------------------------------------------\n");

        for (OfficeStatus status : statusTable.getItems()) {
            cert.append(String.format("%-25s %-15s %-30s\n",
                    status.getOfficeName(), status.getStatus(), status.getReason()));
        }

        cert.append("\nOVERALL CLEARANCE STATUS: CLEARED ✅\n\n");


        return cert.toString();
    }

    // -------------------------------------------------
    // UTILITY
    // -------------------------------------------------
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // -------------------------------------------------
    // MODEL CLASS
    // -------------------------------------------------
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
                return dateStr.replace("T", " ").substring(0, 16);
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
