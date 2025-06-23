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
import model.GovernmentContributions;
import java.sql.*;

public class GovernmentContributionsDAO {
    public GovernmentContributions getById(int id) {
        String query = "SELECT * FROM government_contributions WHERE contribution_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                GovernmentContributions g = new GovernmentContributions();
                g.setContributionId(rs.getInt("contribution_id"));
                g.setSss(rs.getDouble("sss"));
                g.setPhilhealth(rs.getDouble("philhealth"));
                g.setPagibig(rs.getDouble("pagibig"));
                g.setTax(rs.getDouble("tax"));
                return g;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public GovernmentContributions getByEmployeeId(int employeeId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

