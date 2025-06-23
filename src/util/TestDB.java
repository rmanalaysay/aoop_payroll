/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

/**
 *
 * @author rejoice
 */
public class TestDB {
    public static void main(String[] args) {
        try {
            if (DBConnection.getConnection() != null) {
                System.out.println("✅ Connected to MySQL!");
            } else {
                System.out.println("❌ Connection failed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
