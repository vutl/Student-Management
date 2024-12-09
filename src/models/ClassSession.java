package models;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ClassSession {
    private String sessionID;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Map<String, Boolean> attendanceRecords; // key: Student ID, value: Present or not

    public ClassSession(String sessionID, LocalDateTime startTime, LocalDateTime endTime) {
        this.sessionID = sessionID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.attendanceRecords = new HashMap<>();
    }

    // Getters và Setters
    public String getSessionID() {
        return sessionID;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public Map<String, Boolean> getAttendanceRecords() {
        return attendanceRecords;
    }

    public void markAttendance(String studentID, boolean isPresent) {
        attendanceRecords.put(studentID, isPresent);
    }

    @Override
    public String toString() {
        return sessionID + " (" + startTime.toString() + " - " + endTime.toString() + ")";
    }
}
