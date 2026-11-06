/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Maintenance_Request_System;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author John Manuel
 */
public class DBConnection {
    
    public static Connection connect() {

        try {
            String url = "jdbc:mysql://localhost:3306/maintenance_request_system";
            String username = "root";
            String password = "";

            Connection conn = DriverManager.getConnection(url, username, password);

            return conn;

        } catch (SQLException e) {

            System.out.println("Database connection failed: " + e.getMessage());
            return null;
        }
    }
}
