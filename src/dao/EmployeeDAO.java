/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

/**
 *
 * @author rejoice
 */

import util.DBConnection;
import model.Employee;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmployeeDAO {
    private static final Logger LOGGER = Logger.getLogger(EmployeeDAO.class.getName());

    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT * FROM employee ORDER BY last_name, first_name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Employee e = mapResultSetToEmployee(rs);
                employees.add(e);
            }

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error fetching all employees", ex);
            throw new RuntimeException("Failed to fetch employees", ex);
        }

        return employees;
    }

    public Employee getEmployeeById(int employeeId) {
        String query = "SELECT * FROM employee WHERE employee_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToEmployee(rs);
            }

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error fetching employee with ID: " + employeeId, ex);
            throw new RuntimeException("Failed to fetch employee", ex);
        }

        return null;
    }

    public boolean insertEmployee(Employee e) {
        String sql = "INSERT INTO employee (last_name, first_name, birthdate, address, contact_info, " +
                    "sss_number, philhealth_number, pagibig_number, tin_number, employment_status_id, " +
                    "position_id, supervisor_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, e.getLastName());
            stmt.setString(2, e.getFirstName());
            stmt.setDate(3, e.getBirthdate() != null ? Date.valueOf(e.getBirthdate()) : null);
            stmt.setString(4, e.getAddress());
            stmt.setString(5, e.getContactInfo());
            stmt.setString(6, e.getSssNumber());
            stmt.setString(7, e.getPhilhealthNumber());
            stmt.setString(8, e.getPagibigNumber());
            stmt.setString(9, e.getTinNumber());
            stmt.setInt(10, e.getEmploymentStatusId());
            stmt.setInt(11, e.getPositionId());
            stmt.setInt(12, e.getSupervisorId());

            int result = stmt.executeUpdate();
            
            if (result > 0) {
                // Set the generated employee ID
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    e.setEmployeeId(keys.getInt(1));
                }
                return true;
            }

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error inserting employee", ex);
            throw new RuntimeException("Failed to insert employee", ex);
        }

        return false;
    }

    public boolean updateEmployee(Employee e) {
        String sql = "UPDATE employee SET last_name=?, first_name=?, birthdate=?, address=?, " +
                    "contact_info=?, sss_number=?, philhealth_number=?, pagibig_number=?, " +
                    "tin_number=?, employment_status_id=?, position_id=?, supervisor_id=? " +
                    "WHERE employee_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, e.getLastName());
            stmt.setString(2, e.getFirstName());
            stmt.setDate(3, e.getBirthdate() != null ? Date.valueOf(e.getBirthdate()) : null);
            stmt.setString(4, e.getAddress());
            stmt.setString(5, e.getContactInfo());
            stmt.setString(6, e.getSssNumber());
            stmt.setString(7, e.getPhilhealthNumber());
            stmt.setString(8, e.getPagibigNumber());
            stmt.setString(9, e.getTinNumber());
            stmt.setInt(10, e.getEmploymentStatusId());
            stmt.setInt(11, e.getPositionId());
            stmt.setInt(12, e.getSupervisorId());
            stmt.setInt(13, e.getEmployeeId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error updating employee with ID: " + e.getEmployeeId(), ex);
            throw new RuntimeException("Failed to update employee", ex);
        }
    }

    public boolean deleteEmployee(int employeeId) {
        String sql = "DELETE FROM employee WHERE employee_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, employeeId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error deleting employee with ID: " + employeeId, ex);
            throw new RuntimeException("Failed to delete employee", ex);
        }
    }

    public List<Employee> getEmployeesByStatus(int statusId) {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT * FROM employee WHERE employment_status_id = ? ORDER BY last_name, first_name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, statusId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Employee e = mapResultSetToEmployee(rs);
                employees.add(e);
            }

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error fetching employees by status: " + statusId, ex);
            throw new RuntimeException("Failed to fetch employees by status", ex);
        }

        return employees;
    }

    private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        Employee e = new Employee();
        e.setEmployeeId(rs.getInt("employee_id"));
        e.setLastName(rs.getString("last_name"));
        e.setFirstName(rs.getString("first_name"));
        
        Date birthdate = rs.getDate("birthdate");
        if (birthdate != null) {
            e.setBirthdate(birthdate.toLocalDate());
        }
        
        e.setAddress(rs.getString("address"));
        e.setContactInfo(rs.getString("contact_info"));
        e.setSssNumber(rs.getString("sss_number"));
        e.setPhilhealthNumber(rs.getString("philhealth_number"));
        e.setPagibigNumber(rs.getString("pagibig_number"));
        e.setTinNumber(rs.getString("tin_number"));
        e.setEmploymentStatusId(rs.getInt("employment_status_id"));
        e.setPositionId(rs.getInt("position_id"));
        e.setSupervisorId(rs.getInt("supervisor_id"));

        return e;
    }
}

