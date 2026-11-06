/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Maintenance_Request_System;

import java.sql.*;
import javax.swing.table.DefaultTableModel;


/**
 *
 * @author John Manuel
 */

public class userDao {
    
    
    


    // Holds the account information returned after login
    public static class UserAccount {
        
        private final int user_id;
        private final String user_fname;
        private final String user_email;
        private final String user_pass;
        private final String role;
        private final java.sql.Timestamp created_at; 

        public UserAccount(int user_id, String user_fname, String user_email, String user_pass, String role, Timestamp created_at) {
            this.user_id = user_id;
            this.user_fname = user_fname;
            this.user_email = user_email;
            this.user_pass = user_pass;
            this.role = role;
            this.created_at = created_at;
        }

        public int getUserId() {
            return user_id;
        }

        public String getFname() {
            return user_fname;
        }

        public String getEmail() {
            return user_email;
        }
        public String getPass() {
            return user_pass;
        }

        public String getRole() {
            return role;
        }
        public Timestamp getCreatedAt() {
            return created_at;
        }
    }

    // Checks the login credentials
    public UserAccount login(String email, String password)
            throws SQLException {

        String sql = "SELECT user_id, user_fname, user_email, user_pass, role, created_at "
                   + "FROM tbl_users "
                   + "WHERE user_email = ? AND user_pass = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, email);
            pst.setString(2, password);

            try (ResultSet rs = pst.executeQuery()) {

                if (rs.next()) {
                    return new UserAccount(
                            rs.getInt("user_id"),
                            rs.getString("user_fname"),
                            rs.getString("user_email"),
                            rs.getString("user_pass"),
                            rs.getString("role"),
                            rs.getTimestamp("created_at")
                            
                            //int user_id, String user_fname, String user_email, String user_pass, String role, Timestamp created_at
                    );
                }
            }
        }

        // No matching account
        return null;
    }      
public boolean signup(String firstName, String lastName, String middleName,
                      String departmentName, String yearLevel, String section,
                      String email, String password) throws SQLException {

    String checkSql = "SELECT user_id FROM tbl_users WHERE user_email = ?";

    String userSql = "INSERT INTO tbl_users (user_fname, user_email, user_pass, role) "
                   + "VALUES (?, ?, ?, ?)";

    String profileSql = "INSERT INTO student_profiles "
                      + "(user_id, first_name, last_name, middle_name, department, year_level, section) "
                      + "VALUES (?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = DBConnection.connect()) {

        try (PreparedStatement checkPst = conn.prepareStatement(checkSql)) {
            checkPst.setString(1, email);
            try (ResultSet rs = checkPst.executeQuery()) {
                if (rs.next()) {
                    return false;               // email is taken
                }
            }
        }

        conn.setAutoCommit(false);              // save both rows or neither
        try {
            int newUserId;
            try (PreparedStatement userPst =
                     conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                userPst.setString(1, firstName + " " + lastName);
                userPst.setString(2, email);
                userPst.setString(3, password);
                userPst.setString(4, "student");
                userPst.executeUpdate();

                try (ResultSet keys = userPst.getGeneratedKeys()) {
                    keys.next();
                    newUserId = keys.getInt(1);
                }
            }

            try (PreparedStatement profilePst = conn.prepareStatement(profileSql)) {
                profilePst.setInt(1, newUserId);
                profilePst.setString(2, firstName);
                profilePst.setString(3, lastName);
                profilePst.setString(4, middleName);
                profilePst.setString(5, departmentName);
                profilePst.setString(6, yearLevel);
                profilePst.setString(7, section);
                profilePst.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
    }
}
                
public void loadUserRequests(DefaultTableModel model) throws SQLException {

    String sql = "SELECT u.user_id, "
               + "COALESCE(TRIM(CONCAT(sp.last_name, ', ', sp.first_name, ' ', "
               + "COALESCE(sp.middle_name, ''))), u.user_fname) AS full_name, "
               + "u.user_email, u.created_at, u.role "
               + "FROM tbl_users u "
               + "LEFT JOIN student_profiles sp ON sp.user_id = u.user_id "
               + "ORDER BY u.user_id";

    model.setRowCount(0);

    try (Connection conn = DBConnection.connect();
         PreparedStatement pst = conn.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {

        while (rs.next()) {
            model.addRow(new Object[] {
                rs.getInt("user_id"),
                rs.getString("full_name"),
                rs.getString("user_email"),
                rs.getTimestamp("created_at"),
                rs.getString("role"),
                "View"
            });
        }
    }
}

// Holds the admin's extra details
public static class AdminProfile {
    private final String office;
    private final String position;
    private final String contactNo;

    public AdminProfile(String office, String position, String contactNo) {
        this.office = office;
        this.position = position;
        this.contactNo = contactNo;
    }
    public String getOffice()    { return office; }
    public String getPosition()  { return position; }
    public String getContactNo() { return contactNo; }
}

    public AdminProfile getAdminProfile(int userId) throws SQLException {

    String sql = "SELECT office, position, contact_no FROM admin_profiles WHERE user_id = ?";

    try (Connection conn = DBConnection.connect();
         PreparedStatement pst = conn.prepareStatement(sql)) {

        pst.setInt(1, userId);

        try (ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                return new AdminProfile(
                        rs.getString("office"),
                        rs.getString("position"),
                        rs.getString("contact_no"));
            }
        }
    }
    return null;
}

public void saveAdminProfile(int userId, String fname, String email, String pass,
                             String office, String position) throws SQLException {

    String userSql = "UPDATE tbl_users SET user_fname = ?, user_email = ?, user_pass = ? "
                   + "WHERE user_id = ?";

    String profileSql = "INSERT INTO admin_profiles (user_id, office, position) "
                      + "VALUES (?, ?, ?) "
                      + "ON DUPLICATE KEY UPDATE office = VALUES(office), position = VALUES(position)";

    try (Connection conn = DBConnection.connect()) {

        conn.setAutoCommit(false);
        try {
            try (PreparedStatement userPst = conn.prepareStatement(userSql)) {
                userPst.setString(1, fname);
                userPst.setString(2, email);
                userPst.setString(3, pass);
                userPst.setInt(4, userId);
                userPst.executeUpdate();
            }

            try (PreparedStatement profilePst = conn.prepareStatement(profileSql)) {
                profilePst.setInt(1, userId);
                profilePst.setString(2, office);
                profilePst.setString(3, position);
                profilePst.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
    }
}

// ---------------- STUDENT PROFILE ----------------

// Returns {department, year level, section} or null
public String[] getStudentProfile(int userId) throws SQLException {

    String sql = "SELECT department, year_level, section FROM student_profiles WHERE user_id = ?";

    try (Connection conn = DBConnection.connect();
         PreparedStatement pst = conn.prepareStatement(sql)) {

        pst.setInt(1, userId);

        try (ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                return new String[] {
                    rs.getString("department"),
                    rs.getString("year_level"),
                    rs.getString("section")
                };
            }
        }
    }
    return null;
}

public void saveStudentProfile(int userId, String fname, String email, String pass,
                               String department, String yearLevel, String section) throws SQLException {

    String userSql = "UPDATE tbl_users SET user_fname = ?, user_email = ?, user_pass = ? WHERE user_id = ?";
    String profileSql = "UPDATE student_profiles SET department = ?, year_level = ?, section = ? WHERE user_id = ?";

    try (Connection conn = DBConnection.connect()) {

        conn.setAutoCommit(false);
        try {
            try (PreparedStatement pst = conn.prepareStatement(userSql)) {
                pst.setString(1, fname);
                pst.setString(2, email);
                pst.setString(3, pass);
                pst.setInt(4, userId);
                pst.executeUpdate();
            }

            try (PreparedStatement pst = conn.prepareStatement(profileSql)) {
                pst.setString(1, department);
                pst.setString(2, yearLevel);
                pst.setString(3, section);
                pst.setInt(4, userId);
                pst.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
    }
}

// ---------------- STAFF PROFILE ----------------

// Returns {team, specialization, availability, contact no} or null
public String[] getStaffProfile(int userId) throws SQLException {

    String sql = "SELECT team, specialization, availability, contact_no FROM staff_profiles WHERE user_id = ?";

    try (Connection conn = DBConnection.connect();
         PreparedStatement pst = conn.prepareStatement(sql)) {

        pst.setInt(1, userId);

        try (ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                return new String[] {
                    rs.getString("team"),
                    rs.getString("specialization"),
                    rs.getString("availability"),
                    rs.getString("contact_no")
                };
            }
        }
    }
    return null;
}

public void saveStaffProfile(int userId, String fname, String email, String pass,
                             String team, String specialization, String availability,
                             String contactNo) throws SQLException {

    String userSql = "UPDATE tbl_users SET user_fname = ?, user_email = ?, user_pass = ? WHERE user_id = ?";

    String profileSql = "INSERT INTO staff_profiles (user_id, team, specialization, availability, contact_no) "
                      + "VALUES (?, ?, ?, ?, ?) "
                      + "ON DUPLICATE KEY UPDATE team = VALUES(team), specialization = VALUES(specialization), "
                      + "availability = VALUES(availability), contact_no = VALUES(contact_no)";

    try (Connection conn = DBConnection.connect()) {

        conn.setAutoCommit(false);
        try {
            try (PreparedStatement pst = conn.prepareStatement(userSql)) {
                pst.setString(1, fname);
                pst.setString(2, email);
                pst.setString(3, pass);
                pst.setInt(4, userId);
                pst.executeUpdate();
            }

            try (PreparedStatement pst = conn.prepareStatement(profileSql)) {
                pst.setInt(1, userId);
                pst.setString(2, team);
                pst.setString(3, specialization);
                pst.setString(4, availability);
                pst.setString(5, contactNo);
                pst.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
    }
}
}
        

        

        

    
    

