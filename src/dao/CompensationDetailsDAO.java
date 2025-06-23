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
import model.CompensationDetails;
import java.sql.*;

public class CompensationDetailsDAO {
    public CompensationDetails getById(int id) {
        String query = "SELECT * FROM compensation_details WHERE compensation_details_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                CompensationDetails c = new CompensationDetails();
                c.setCompId(rs.getInt("compensation_details_id"));
                c.setRiceSubsidy(rs.getDouble("rice_subsidy"));
                c.setPhoneAllowance(rs.getDouble("phone_allowance"));
                c.setClothingAllowance(rs.getDouble("clothing_allowance"));
                return c;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public CompensationDetails getCompensationDetailsByEmployeeId(int employeeId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}