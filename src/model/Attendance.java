package model;

import java.sql.Date;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Model class representing employee attendance
 * @author rejoice
 */
public class Attendance {
    private int attendanceId;
    private int employeeId;
    private Date date;
    private Time loginTime;
    private Time logoutTime;

    // Constructors
    public Attendance() {}

    public Attendance(int employeeId, Date date, Time loginTime, Time logoutTime) {
        setEmployeeId(employeeId);
        setDate(date);
        setLoginTime(loginTime);
        setLogoutTime(logoutTime);
    }

    // Getters and Setters with validation
    public int getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(int attendanceId) {
        this.attendanceId = attendanceId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        if (employeeId <= 0) {
            throw new IllegalArgumentException("Employee ID must be positive");
        }
        this.employeeId = employeeId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        this.date = date;
    }

    public Time getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Time loginTime) {
        this.loginTime = loginTime;
    }

    public Time getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(Time logoutTime) {
        if (loginTime != null && logoutTime != null && logoutTime.before(loginTime)) {
            throw new IllegalArgumentException("Logout time cannot be before login time");
        }
        this.logoutTime = logoutTime;
    }

    // Utility methods
    public Duration getWorkDuration() {
        if (loginTime == null || logoutTime == null) {
            return Duration.ZERO;
        }
        LocalTime login = loginTime.toLocalTime();
        LocalTime logout = logoutTime.toLocalTime();
        return Duration.between(login, logout);
    }

    public double getWorkHours() {
        return getWorkDuration().toMinutes() / 60.0;
    }

    public boolean isFullDay() {
        return getWorkDuration().toHours() >= 8;
    }

    public boolean isPresent() {
        return loginTime != null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Attendance that = (Attendance) obj;
        return attendanceId == that.attendanceId &&
               employeeId == that.employeeId &&
               Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attendanceId, employeeId, date);
    }

    @Override
    public String toString() {
        return "Attendance{" +
                "attendanceId=" + attendanceId +
                ", employeeId=" + employeeId +
                ", date=" + date +
                ", loginTime=" + loginTime +
                ", logoutTime=" + logoutTime +
                '}';
    }
}