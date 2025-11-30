package org.example;

import java.time.LocalDateTime;

public class Penalty {
    private String studentId;
    private String studentName;
    private String officeName;
    private String status;
    private String reason;
    private LocalDateTime createdAt;

    // Constructors
    public Penalty() {}

    public Penalty(String studentId, String studentName, String officeName, String status, String reason, LocalDateTime createdAt) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.officeName = officeName;
        this.status = status;
        this.reason = reason;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getOfficeName() { return officeName; }
    public void setOfficeName(String officeName) { this.officeName = officeName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}