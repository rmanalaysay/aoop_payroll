/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dao.AttendanceDAO;
import dao.CompensationDetailsDAO;
import dao.EmployeeDAO;
import dao.GovernmentContributionsDAO;
import dao.LeaveRequestDAO;
import dao.OvertimeDAO;
import dao.PositionDAO;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import model.Attendance;
import model.CompensationDetails;
import model.Employee;
import model.GovernmentContributions;
import model.LeaveRequest;
import model.Overtime;
import model.Payroll;
import model.Position;

/**
 *
 * @author rejoice
 */
public class PayrollCalculator {

    public Payroll calculatePayroll(int employeeId, LocalDate periodStart, LocalDate periodEnd) {
        EmployeeDAO employeeDAO = new EmployeeDAO();
        AttendanceDAO attendanceDAO = new AttendanceDAO();
        LeaveRequestDAO leaveDAO = new LeaveRequestDAO();
        OvertimeDAO overtimeDAO = new OvertimeDAO();
        GovernmentContributionsDAO govDAO = new GovernmentContributionsDAO();
        CompensationDetailsDAO compDAO = new CompensationDetailsDAO();

        Employee employee = employeeDAO.getEmployeeById(employeeId);
        if (employee == null) {
            throw new RuntimeException("Employee not found.");
        }

        // Basic salary
        Position position = new PositionDAO().getPositionById(employee.getPositionId());
        double monthlySalary = position.getMonthlySalary();
        double dailyRate = monthlySalary / 22.0; // Assuming 22 work days

        // Attendance
        List<Attendance> attendanceList = attendanceDAO.getAttendanceByEmployeeIdBetweenDates(employeeId, periodStart, periodEnd);
        int daysWorked = attendanceList.size();

        // Leave
        List<LeaveRequest> leaves = leaveDAO.getApprovedLeavesByEmployeeIdAndDateRange(employeeId, periodStart, periodEnd);
        int unpaidLeaveCount = (int) leaves.stream().filter(l -> l.getLeaveType().equalsIgnoreCase("Unpaid")).count();

        // Overtime
        List<Overtime> overtimeList = overtimeDAO.getOvertimeByEmployeeIdAndDateRange(employeeId, periodStart, periodEnd);
        double totalOvertimeHours = overtimeList.stream().mapToDouble(Overtime::getHours).sum();
        double overtimePay = totalOvertimeHours * (dailyRate / 8 * 1.25); // 125% of hourly rate

        // Compensation details
        CompensationDetails comp = compDAO.getCompensationDetailsByEmployeeId(employeeId);
        double riceSubsidy = comp.getRiceSubsidy();
        double phoneAllowance = comp.getPhoneAllowance();
        double clothingAllowance = comp.getClothingAllowance();

        // Deductions
        double lateDeduction = calculateLateDeduction(attendanceList);
        double undertimeDeduction = calculateUndertimeDeduction(attendanceList);
        double unpaidLeaveDeduction = unpaidLeaveCount * dailyRate;

        // Government contributions
        GovernmentContributions gov = govDAO.getByEmployeeId(employeeId);
        double sss = gov.getSss();
        double philhealth = gov.getPhilhealth();
        double pagibig = gov.getPagibig();
        double tax = gov.getTin(); // You can replace this with actual tax computation

        // Totals
        double grossEarnings = (daysWorked * dailyRate) + overtimePay + riceSubsidy + phoneAllowance + clothingAllowance;
        double totalDeductions = lateDeduction + undertimeDeduction + unpaidLeaveDeduction + sss + philhealth + pagibig + tax;
        double netPay = grossEarnings - totalDeductions;

        // Prepare and return the Payroll object
        Payroll payroll = new Payroll();
        payroll.setEmployeeId(employeeId);
        payroll.setStartDate(Date.valueOf(periodStart));
        payroll.setEndDate(Date.valueOf(periodEnd));
        payroll.setGrossEarnings(grossEarnings);
        payroll.setTotalDeductions(totalDeductions);
        payroll.setNetPay(netPay);

        // Add breakdown for future payslip use if needed
        payroll.setLateDeduction(lateDeduction);
        payroll.setUndertimeDeduction(undertimeDeduction);
        payroll.setUnpaidLeaveDeduction(unpaidLeaveDeduction);
        payroll.setOvertimePay(overtimePay);
        payroll.setRiceSubsidy(riceSubsidy);
        payroll.setPhoneAllowance(phoneAllowance);
        payroll.setClothingAllowance(clothingAllowance);

        payroll.setSss(sss);
        payroll.setPhilhealth(philhealth);
        payroll.setPagibig(pagibig);
        payroll.setTax(tax);

        return payroll;
    }

        private double calculateLateDeduction(List<Attendance> records) {
        double total = 0.0;
        for (Attendance att : records) {
            if (att.getLoginTime().after(Time.valueOf("08:15:00"))) {
                long diff = att.getLoginTime().getTime() - Time.valueOf("08:00:00").getTime();
                double minutesLate = diff / (1000.0 * 60);
                total += (minutesLate / 60) * (500.0 / 8); // Assuming dailyRate ₱500
            }
        }
        return total;
    }

    private double calculateUndertimeDeduction(List<Attendance> records) {
        double total = 0.0;
        for (Attendance att : records) {
            if (att.getLogoutTime().before(Time.valueOf("17:00:00"))) {
                long diff = Time.valueOf("17:00:00").getTime() - att.getLogoutTime().getTime();
                double minutesShort = diff / (1000.0 * 60);
                total += (minutesShort / 60) * (500.0 / 8); // Assuming dailyRate ₱500
            }
        }
        return total;
    }
}