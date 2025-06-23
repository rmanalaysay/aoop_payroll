package dao;

import model.Deduction;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DeductionDAO {

    public void addDeduction(Deduction deduction) throws SQLException {
        String sql = "INSERT INTO deductions (employee_id, type, amount, description) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, deduction.getEmployeeId());
            stmt.setString(2, deduction.getType());
            stmt.setDouble(3, deduction.getAmount());
            stmt.setString(4, deduction.getDescription());
            stmt.executeUpdate();
        }
    }

    public List<Deduction> getDeductionsByEmployeeId(int employeeId) throws SQLException {
        String sql = "SELECT * FROM deductions WHERE employee_id = ?";
        List<Deduction> deductions = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Deduction d = new Deduction(
                        rs.getInt("employee_id"),
                        rs.getString("type"),
                        rs.getDouble("amount"),
                        rs.getString("description")
                );
                d.setDeductionId(rs.getInt("deduction_id"));
                deductions.add(d);
            }
        }

        return deductions;
    }

    // Optionally: updateDeduction(), deleteDeduction(), etc.
}
