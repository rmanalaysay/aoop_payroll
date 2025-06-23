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
import model.Position;
import java.sql.*;
import java.util.*;

public class PositionDAO {
    public Position getPositionById(int positionId) {
        String query = "SELECT * FROM position WHERE position_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, positionId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Position p = new Position();
                p.setPositionId(rs.getInt("position_id"));
                p.setPositionName(rs.getString("position_name"));
                return p;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
