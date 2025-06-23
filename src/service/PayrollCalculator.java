package service;

import dao.AttendanceDAO;
import dao.CompensationDetailsDAO;
import dao.EmployeeDAO;
import dao.GovernmentContributionsDAO;
import dao.LeaveRequestDAO;
import dao.OvertimeDAO;
import dao.PositionDAO;
import model.Attendance;
import model.CompensationDetails;
import model.Employee;
import model.GovernmentContributions;
import model.LeaveRequest;
import model.Overtime;
import model.Payroll;
import model.Position;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Enhanced PayrollCalculator service with improved error handling, 
 * better calculations, and comprehensive payroll processing
 * 
 * @author rejoice
 */
public class PayrollCalculator {
    
    private static final Logger LOGGER = Logger.getLogger(PayrollCalculator.class.getName());
    
    // Constants for payroll calculations
    private static final int STANDARD_WORKING_DAYS_PER_MONTH = 22;
    private static final int STANDARD_WORKING_HOURS_PER_DAY = 8;
    private static final double OVERTIME_RATE_MULTIPLIER = 1.25;
    private static final LocalTime STANDARD_LOGIN_TIME = LocalTime.of(8, 0);
    private static final LocalTime LATE_THRESHOLD_TIME = LocalTime.of(8, 15);
    private static final LocalTime STANDARD_LOGOUT_TIME = LocalTime.of(17, 0);
    
    // DAO instances
    private final EmployeeDAO employeeDAO;
    private final AttendanceDAO attendanceDAO;
    private final LeaveRequestDAO leaveDAO;
    private final OvertimeDAO overtimeDAO;
    private final GovernmentContributionsDAO govDAO;
    private final CompensationDetailsDAO compDAO;
    private final PositionDAO positionDAO;
    
    public PayrollCalculator() {
        this.employeeDAO = new EmployeeDAO();
        this.attendanceDAO = new AttendanceDAO();
        this.leaveDAO = new LeaveRequestDAO();
        this.overtimeDAO = new OvertimeDAO();
        this.govDAO = new GovernmentContributionsDAO();
        this.compDAO = new CompensationDetailsDAO();
        this.positionDAO = new PositionDAO();
    }
    
    /**
     * Calculate comprehensive payroll for an employee within a specific period
     * 
     * @param employeeId The employee ID
     * @param periodStart Start date of payroll period
     * @param periodEnd End date of payroll period
     * @return Calculated Payroll object
     * @throws PayrollCalculationException if calculation fails
     */
    public Payroll calculatePayroll(int employeeId, LocalDate periodStart, LocalDate periodEnd) 
            throws PayrollCalculationException {
        
        try {
            validateInputs(employeeId, periodStart, periodEnd);
            
            // Get employee information
            Employee employee = getEmployeeWithValidation(employeeId);
            Position position = getPositionWithValidation(employee.getPositionId());
            
            // Initialize payroll object
            Payroll payroll = new Payroll(employeeId, Date.valueOf(periodStart), Date.valueOf(periodEnd));
            
            // Set basic salary information
            double monthlySalary = position.getMonthlySalary();
            double dailyRate = calculateDailyRate(monthlySalary);
            
            payroll.setMonthlyRate(monthlySalary);
            payroll.setDailyRate(dailyRate);
            
            // Calculate attendance-based earnings
            calculateAttendanceBasedEarnings(payroll, employeeId, periodStart, periodEnd, dailyRate);
            
            // Calculate overtime earnings
            calculateOvertimeEarnings(payroll, employeeId, periodStart, periodEnd, dailyRate);
            
            // Calculate allowances and benefits
            calculateAllowancesAndBenefits(payroll, employeeId);
            
            // Calculate time-based deductions
            calculateTimeBasedDeductions(payroll, employeeId, periodStart, periodEnd, dailyRate);
            
            // Calculate government contributions and tax
            calculateGovernmentContributionsAndTax(payroll, employeeId, monthlySalary);
            
            // Final calculations
            payroll.calculateGrossPay();
            payroll.calculateTotalDeductions();
            payroll.calculateNetPay();
            
            // Validate final payroll
            validatePayroll(payroll);
            
            LOGGER.info(String.format("Payroll calculated successfully for employee %d", employeeId));
            return payroll;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, String.format("Failed to calculate payroll for employee %d", employeeId), e);
            throw new PayrollCalculationException("Failed to calculate payroll: " + e.getMessage(), e);
        }
    }
    
    /**
     * Calculate daily rate from monthly salary
     */
    private double calculateDailyRate(double monthlySalary) {
        return monthlySalary / STANDARD_WORKING_DAYS_PER_MONTH;
    }
    
    /**
     * Calculate hourly rate from daily rate
     */
    private double calculateHourlyRate(double dailyRate) {
        return dailyRate / STANDARD_WORKING_HOURS_PER_DAY;
    }
    
    /**
     * Calculate attendance-based earnings
     */
    private void calculateAttendanceBasedEarnings(Payroll payroll, int employeeId, 
            LocalDate periodStart, LocalDate periodEnd, double dailyRate) {
        
        List<Attendance> attendanceList = attendanceDAO.getAttendanceByEmployeeIdBetweenDates(
                employeeId, periodStart, periodEnd);
        
        int daysWorked = attendanceList.size();
        payroll.setDaysWorked(daysWorked);
        
        // Calculate basic pay
        double basicPay = daysWorked * dailyRate;
        payroll.setGrossEarnings(basicPay);
        
        LOGGER.info(String.format("Employee %d worked %d days, basic pay: %.2f", 
                employeeId, daysWorked, basicPay));
    }
    
    /**
     * Calculate overtime earnings
     */
    private void calculateOvertimeEarnings(Payroll payroll, int employeeId, 
            LocalDate periodStart, LocalDate periodEnd, double dailyRate) {
        
        List<Overtime> overtimeList = overtimeDAO.getOvertimeByEmployeeIdAndDateRange(
                employeeId, periodStart, periodEnd);
        
        double totalOvertimeHours = overtimeList.stream()
                .mapToDouble(Overtime::getHours)
                .sum();
        
        double hourlyRate = calculateHourlyRate(dailyRate);
        double overtimePay = totalOvertimeHours * hourlyRate * OVERTIME_RATE_MULTIPLIER;
        
        payroll.setTotalOvertimeHours(totalOvertimeHours);
        payroll.setOvertimePay(overtimePay);
        
        LOGGER.info(String.format("Employee %d overtime: %.2f hours, pay: %.2f", 
                employeeId, totalOvertimeHours, overtimePay));
    }
    
    /**
     * Calculate allowances and benefits
     */
    private void calculateAllowancesAndBenefits(Payroll payroll, int employeeId) {
        CompensationDetails comp = compDAO.getCompensationDetailsByEmployeeId(employeeId);
        
        if (comp != null) {
            payroll.setRiceSubsidy(comp.getRiceSubsidy());
            payroll.setPhoneAllowance(comp.getPhoneAllowance());
            payroll.setClothingAllowance(comp.getClothingAllowance());
            
            LOGGER.info(String.format("Employee %d allowances - Rice: %.2f, Phone: %.2f, Clothing: %.2f",
                    employeeId, comp.getRiceSubsidy(), comp.getPhoneAllowance(), comp.getClothingAllowance()));
        } else {
            // Set default values if no compensation details found
            payroll.setRiceSubsidy(0.0);
            payroll.setPhoneAllowance(0.0);
            payroll.setClothingAllowance(0.0);
            
            LOGGER.warning(String.format("No compensation details found for employee %d", employeeId));
        }
    }
    
    /**
     * Calculate time-based deductions (late, undertime, unpaid leave)
     */
    private void calculateTimeBasedDeductions(Payroll payroll, int employeeId, 
            LocalDate periodStart, LocalDate periodEnd, double dailyRate) {
        
        // Get attendance records for deduction calculations
        List<Attendance> attendanceList = attendanceDAO.getAttendanceByEmployeeIdBetweenDates(
                employeeId, periodStart, periodEnd);
        
        double lateDeduction = calculateLateDeduction(attendanceList, dailyRate);
        double undertimeDeduction = calculateUndertimeDeduction(attendanceList, dailyRate);
        
        payroll.setLateDeduction(lateDeduction);
        payroll.setUndertimeDeduction(undertimeDeduction);
        
        // Calculate unpaid leave deduction
        List<LeaveRequest> unpaidLeaves = leaveDAO.getApprovedLeavesByEmployeeIdAndDateRange(
                employeeId, periodStart, periodEnd);
        
        int unpaidLeaveCount = (int) unpaidLeaves.stream()
                .filter(leave -> "Unpaid".equalsIgnoreCase(leave.getLeaveType()))
                .count();
        
        double unpaidLeaveDeduction = unpaidLeaveCount * dailyRate;
        
        payroll.setUnpaidLeaveCount(unpaidLeaveCount);
        payroll.setUnpaidLeaveDeduction(unpaidLeaveDeduction);
        
        LOGGER.info(String.format("Employee %d deductions - Late: %.2f, Undertime: %.2f, Unpaid Leave: %.2f",
                employeeId, lateDeduction, undertimeDeduction, unpaidLeaveDeduction));
    }
    
    /**
     * Calculate government contributions and tax
     */
    private void calculateGovernmentContributionsAndTax(Payroll payroll, int employeeId, double monthlySalary) {
        GovernmentContributions gov = govDAO.getByEmployeeId(employeeId);
        
        if (gov != null) {
            payroll.setSss(gov.getSss());
            payroll.setPhilhealth(gov.getPhilhealth());
            payroll.setPagibig(gov.getPagibig());
            
            // Calculate tax based on salary (simplified calculation)
            double tax = calculateIncomeTax(monthlySalary);
            payroll.setTax(tax);
            
            LOGGER.info(String.format("Employee %d contributions - SSS: %.2f, PhilHealth: %.2f, Pag-IBIG: %.2f, Tax: %.2f",
                    employeeId, gov.getSss(), gov.getPhilhealth(), gov.getPagibig(), tax));
        } else {
            // Set default values and calculate basic contributions
            double sss = calculateSSSContribution(monthlySalary);
            double philhealth = calculatePhilHealthContribution(monthlySalary);
            double pagibig = calculatePagIBIGContribution(monthlySalary);
            double tax = calculateIncomeTax(monthlySalary);
            
            payroll.setSss(sss);
            payroll.setPhilhealth(philhealth);
            payroll.setPagibig(pagibig);
            payroll.setTax(tax);
            
            LOGGER.warning(String.format("No government contributions found for employee %d, using calculated values", employeeId));
        }
    }
    
    /**
     * Enhanced late deduction calculation with proper time handling
     */
    private double calculateLateDeduction(List<Attendance> attendanceList, double dailyRate) {
        double totalLateDeduction = 0.0;
        double hourlyRate = calculateHourlyRate(dailyRate);
        
        for (Attendance attendance : attendanceList) {
            if (attendance.getLoginTime() != null) {
                LocalTime loginTime = attendance.getLoginTime().toLocalTime();
                
                if (loginTime.isAfter(LATE_THRESHOLD_TIME)) {
                    long minutesLate = ChronoUnit.MINUTES.between(STANDARD_LOGIN_TIME, loginTime);
                    double hoursLate = minutesLate / 60.0;
                    totalLateDeduction += hoursLate * hourlyRate;
                }
            }
        }
        
        return totalLateDeduction;
    }
    
    /**
     * Enhanced undertime deduction calculation
     */
    private double calculateUndertimeDeduction(List<Attendance> attendanceList, double dailyRate) {
        double totalUndertimeDeduction = 0.0;
        double hourlyRate = calculateHourlyRate(dailyRate);
        
        for (Attendance attendance : attendanceList) {
            if (attendance.getLogoutTime() != null) {
                LocalTime logoutTime = attendance.getLogoutTime().toLocalTime();
                
                if (logoutTime.isBefore(STANDARD_LOGOUT_TIME)) {
                    long minutesShort = ChronoUnit.MINUTES.between(logoutTime, STANDARD_LOGOUT_TIME);
                    double hoursShort = minutesShort / 60.0;
                    totalUndertimeDeduction += hoursShort * hourlyRate;
                }
            }
        }
        
        return totalUndertimeDeduction;
    }
    
    /**
     * Calculate SSS contribution based on salary bracket
     */
    private double calculateSSSContribution(double monthlySalary) {
        // Simplified SSS calculation - you should implement actual SSS table
        if (monthlySalary <= 4000) return 180.00;
        if (monthlySalary <= 4750) return 202.50;
        if (monthlySalary <= 5500) return 225.00;
        if (monthlySalary <= 6250) return 247.50;
        if (monthlySalary <= 7000) return 270.00;
        if (monthlySalary <= 7750) return 292.50;
        if (monthlySalary <= 8500) return 315.00;
        if (monthlySalary <= 9250) return 337.50;
        if (monthlySalary <= 10000) return 360.00;
        // For higher salaries, use maximum contribution
        return 1125.00; // Maximum SSS contribution
    }
    
    /**
     * Calculate PhilHealth contribution
     */
    private double calculatePhilHealthContribution(double monthlySalary) {
        // Simplified PhilHealth calculation - implement actual rates
        double rate = 0.045; // 4.5% of basic salary (shared between employer and employee)
        double employeeShare = (monthlySalary * rate) / 2;
        double maxContribution = 5000.00; // Maximum monthly contribution
        return Math.min(employeeShare, maxContribution);
    }
    
    /**
     * Calculate Pag-IBIG contribution
     */
    private double calculatePagIBIGContribution(double monthlySalary) {
        // Pag-IBIG contribution rates
        if (monthlySalary <= 1500) {
            return monthlySalary * 0.01; // 1%
        } else {
            return monthlySalary * 0.02; // 2%
        }
    }
    
    /**
     * Calculate income tax using simplified TRAIN law brackets
     */
    private double calculateIncomeTax(double monthlySalary) {
        double annualSalary = monthlySalary * 12;
        double annualTax = 0.0;
        
        // Simplified TRAIN law tax brackets
        if (annualSalary <= 250000) {
            annualTax = 0.0; // Tax-free
        } else if (annualSalary <= 400000) {
            annualTax = (annualSalary - 250000) * 0.15;
        } else if (annualSalary <= 800000) {
            annualTax = 22500 + (annualSalary - 400000) * 0.20;
        } else if (annualSalary <= 2000000) {
            annualTax = 102500 + (annualSalary - 800000) * 0.25;
        } else if (annualSalary <= 8000000) {
            annualTax = 402500 + (annualSalary - 2000000) * 0.30;
        } else {
            annualTax = 2202500 + (annualSalary - 8000000) * 0.35;
        }
        
        return annualTax / 12; // Monthly tax
    }
    
    /**
     * Validate input parameters
     */
    private void validateInputs(int employeeId, LocalDate periodStart, LocalDate periodEnd) 
            throws PayrollCalculationException {
        if (employeeId <= 0) {
            throw new PayrollCalculationException("Invalid employee ID");
        }
        if (periodStart == null || periodEnd == null) {
            throw new PayrollCalculationException("Period dates cannot be null");
        }
        if (periodEnd.isBefore(periodStart)) {
            throw new PayrollCalculationException("Period end cannot be before period start");
        }
    }
    
    /**
     * Get employee with validation
     */
    private Employee getEmployeeWithValidation(int employeeId) throws PayrollCalculationException {
        Employee employee = employeeDAO.getEmployeeById(employeeId);
        if (employee == null) {
            throw new PayrollCalculationException("Employee not found with ID: " + employeeId);
        }
        return employee;
    }
    
    /**
     * Get position with validation
     */
    private Position getPositionWithValidation(int positionId) throws PayrollCalculationException {
        Position position = positionDAO.getPositionById(positionId);
        if (position == null) {
            throw new PayrollCalculationException("Position not found with ID: " + positionId);
        }
        return position;
    }
    
    /**
     * Validate calculated payroll
     */
    private void validatePayroll(Payroll payroll) throws PayrollCalculationException {
        if (!payroll.isValid()) {
            throw new PayrollCalculationException("Invalid payroll calculation result");
        }
        if (payroll.getNetPay() < 0) {
            LOGGER.warning(String.format("Negative net pay detected for employee %d: %.2f", 
                    payroll.getEmployeeId(), payroll.getNetPay()));
        }
    }
    
    /**
     * Custom exception for payroll calculation errors
     */
    public static class PayrollCalculationException extends Exception {
        public PayrollCalculationException(String message) {
            super(message);
        }
        
        public PayrollCalculationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}