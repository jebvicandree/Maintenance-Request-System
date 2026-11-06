package Maintenance_Request_System;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import javax.swing.table.DefaultTableModel;

/**
 * RequestDAO = ALL the database code for maintenance requests and staff tasks.
 * The screens (forms) never write SQL. They just call a method from here.
 *
 * Request status flow:
 *   Pending -> Assigned -> In Progress -> Completed
 *   Pending -> Declined            (admin)
 *   Pending -> Cancelled           (student)
 */
public class RequestDAO {

    // ------------------------------------------------------------
    // A small box that holds the details of ONE request
    // ------------------------------------------------------------
    public static class RequestInfo {
        public int id;
        public String requesterName = "";
        public String requesterRole = "";
        public String department = "";
        public String issue = "";
        public String location = "";
        public String category = "";
        public String description = "";
        public String priority;        // can be null
        public String status = "";
        public String photoPath;       // can be null
        public String declineReason;   // can be null
        public String dateSubmitted = "";
    }

    // Opens the database. Gives a clear message if MySQL is not running.
    private Connection connect() throws SQLException {
        Connection conn = DBConnection.connect();
        if (conn == null) {
            throw new SQLException("Cannot connect to the database. Is MySQL running?");
        }
        return conn;
    }

    // ============================================================
    // STUDENT
    // ============================================================

    // Saves a new request (status becomes "Pending" automatically)
    public void addRequest(int userId, String issue, String location, String category,
                           String description, String photoPath, String priority) throws SQLException {

        String sql = "INSERT INTO maintenance_requests "
                   + "(user_id, issue, location, category, description, photo_path, priority) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, userId);
            pst.setString(2, issue);
            pst.setString(3, location);
            pst.setString(4, category);
            pst.setString(5, description);
            pst.setString(6, photoPath);   // null is OK (photo is optional)
            pst.setString(7, priority);
            pst.executeUpdate();
        }
    }

    // Fills the student's "My Requests" table. status = null means show ALL.
    public void loadStudentRequests(DefaultTableModel model, int userId, String status) throws SQLException {

        String sql = "SELECT request_id, issue, location, category, "
                   + "DATE_FORMAT(date_submitted, '%b %d, %Y') AS submitted, status "
                   + "FROM maintenance_requests WHERE user_id = ? ";
        if (status != null) {
            sql += "AND status = ? ";
        }
        sql += "ORDER BY request_id DESC";

        model.setRowCount(0);   // clear old rows

        try (Connection conn = connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, userId);
            if (status != null) {
                pst.setString(2, status);
            }

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[] {
                        rs.getInt("request_id"),
                        rs.getString("issue"),
                        rs.getString("location"),
                        rs.getString("category"),
                        rs.getString("submitted"),
                        rs.getString("status"),
                        "See Details"
                    });
                }
            }
        }
    }

    // Student cancels a request. Only works while it is still Pending.
    // Returns true if it was cancelled.
    public boolean cancelRequest(int requestId) throws SQLException {

        String sql = "UPDATE maintenance_requests SET status = 'Cancelled' "
                   + "WHERE request_id = ? AND status = 'Pending'";

        try (Connection conn = connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, requestId);
            return pst.executeUpdate() > 0;
        }
    }

    // ============================================================
    // SHARED: details of one request (used by student, admin, staff)
    // ============================================================
    public RequestInfo getRequest(int requestId) throws SQLException {

        String sql = "SELECT r.request_id, r.issue, r.location, r.category, r.description, "
                   + "r.priority, r.status, r.photo_path, r.decline_reason, "
                   + "DATE_FORMAT(r.date_submitted, '%b %d, %Y') AS submitted, "
                   + "u.user_fname, u.role, sp.department "
                   + "FROM maintenance_requests r "
                   + "JOIN tbl_users u ON u.user_id = r.user_id "
                   + "LEFT JOIN student_profiles sp ON sp.user_id = r.user_id "
                   + "WHERE r.request_id = ?";

        try (Connection conn = connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, requestId);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    RequestInfo info = new RequestInfo();
                    info.id = rs.getInt("request_id");
                    info.requesterName = rs.getString("user_fname");
                    info.requesterRole = rs.getString("role");
                    info.department = rs.getString("department") == null ? "" : rs.getString("department");
                    info.issue = rs.getString("issue");
                    info.location = rs.getString("location");
                    info.category = rs.getString("category");
                    info.description = rs.getString("description") == null ? "" : rs.getString("description");
                    info.priority = rs.getString("priority");
                    info.status = rs.getString("status");
                    info.photoPath = rs.getString("photo_path");
                    info.declineReason = rs.getString("decline_reason");
                    info.dateSubmitted = rs.getString("submitted");
                    return info;
                }
            }
        }
        return null;   // request not found
    }

    // ============================================================
    // ADMIN
    // ============================================================

    // Fills the admin's "Manage Requests" table. status = null means show ALL.
    public void loadAllRequests(DefaultTableModel model, String status) throws SQLException {

        String sql = "SELECT r.request_id, u.user_fname, r.issue, r.location, r.category, "
                   + "DATE_FORMAT(r.date_submitted, '%b %d, %Y') AS submitted, r.status "
                   + "FROM maintenance_requests r "
                   + "JOIN tbl_users u ON u.user_id = r.user_id ";
        if (status != null) {
            sql += "WHERE r.status = ? ";
        }
        sql += "ORDER BY r.request_id DESC";

        model.setRowCount(0);

        try (Connection conn = connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            if (status != null) {
                pst.setString(1, status);
            }

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[] {
                        rs.getInt("request_id"),
                        rs.getString("user_fname"),
                        rs.getString("issue"),
                        rs.getString("location"),
                        rs.getString("category"),
                        rs.getString("submitted"),
                        rs.getString("status"),
                        "See Details"
                    });
                }
            }
        }
    }
    // Admin accepts a pending request. Only works while it is still Pending.
    public boolean approveRequest(int requestId, int adminId) throws SQLException {

        String sql = "UPDATE maintenance_requests "
                   + "SET status = 'Approved', reviewed_by = ?, reviewed_at = NOW() "
                   + "WHERE request_id = ? AND status = 'Pending'";

        try (Connection conn = connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, adminId);
            pst.setInt(2, requestId);
            return pst.executeUpdate() > 0;
        }
    }
    
    // Admin declines a request and writes the reason
    public void declineRequest(int requestId, int adminId, String reason) throws SQLException {

        String sql = "UPDATE maintenance_requests "
                   + "SET status = 'Declined', decline_reason = ?, reviewed_by = ?, reviewed_at = NOW() "
                   + "WHERE request_id = ?";

        try (Connection conn = connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, reason);
            pst.setInt(2, adminId);
            pst.setInt(3, requestId);
            pst.executeUpdate();
        }
    }

    // Admin assigns a request to a staff member.
    // Two things must happen together: update the request AND create the task.
    public void assignRequest(int requestId, int staffId, int adminId, String priority,
                              LocalDate scheduleDate, LocalDate targetDate) throws SQLException {

        String requestSql = "UPDATE maintenance_requests "
                          + "SET status = 'Assigned', priority = ?, reviewed_by = ?, reviewed_at = NOW() "
                          + "WHERE request_id = ?";

        String taskSql = "INSERT INTO staff_tasks "
                       + "(request_id, staff_id, assigned_by, schedule_date, target_date) "
                       + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = connect()) {

            conn.setAutoCommit(false);   // save both changes or neither
            try {
                try (PreparedStatement pst = conn.prepareStatement(requestSql)) {
                    pst.setString(1, priority);
                    pst.setInt(2, adminId);
                    pst.setInt(3, requestId);
                    pst.executeUpdate();
                }

                try (PreparedStatement pst = conn.prepareStatement(taskSql)) {
                    pst.setInt(1, requestId);
                    pst.setInt(2, staffId);
                    pst.setInt(3, adminId);
                    pst.setDate(4, Date.valueOf(scheduleDate));
                    pst.setDate(5, Date.valueOf(targetDate));
                    pst.executeUpdate();
                }

                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    // ============================================================
    // STAFF
    // ============================================================

    // Fills the staff's task table. taskStatus = null means show ALL.
    public void loadStaffTasks(DefaultTableModel model, int staffId, String taskStatus) throws SQLException {

        String sql = "SELECT r.request_id, u.user_fname, r.issue, r.location, r.category, "
                   + "DATE_FORMAT(r.date_submitted, '%b %d, %Y') AS submitted, t.task_status "
                   + "FROM staff_tasks t "
                   + "JOIN maintenance_requests r ON r.request_id = t.request_id "
                   + "JOIN tbl_users u ON u.user_id = r.user_id "
                   + "WHERE t.staff_id = ? ";
        if (taskStatus != null) {
            sql += "AND t.task_status = ? ";
        }
        sql += "ORDER BY t.task_id DESC";

        model.setRowCount(0);

        try (Connection conn = connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, staffId);
            if (taskStatus != null) {
                pst.setString(2, taskStatus);
            }

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[] {
                        rs.getInt("request_id"),
                        rs.getString("user_fname"),
                        rs.getString("issue"),
                        rs.getString("location"),
                        rs.getString("category"),
                        rs.getString("submitted"),
                        rs.getString("task_status"),
                        "See Details"
                    });
                }
            }
        }
    }

    // Returns {task status, date assigned, target date} for one task, or null
    public String[] getTaskInfo(int requestId, int staffId) throws SQLException {

        String sql = "SELECT task_status, "
                   + "DATE_FORMAT(date_assigned, '%b %d, %Y') AS assigned, "
                   + "COALESCE(DATE_FORMAT(target_date, '%b %d, %Y'), '-') AS target "
                   + "FROM staff_tasks WHERE request_id = ? AND staff_id = ? "
                   + "ORDER BY task_id DESC LIMIT 1";

        try (Connection conn = connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, requestId);
            pst.setInt(2, staffId);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new String[] {
                        rs.getString("task_status"),
                        rs.getString("assigned"),
                        rs.getString("target")
                    };
                }
            }
        }
        return null;
    }

    // Staff changes the task status ("In Progress" or "Completed").
    // The request's own status is updated too, so the student sees it.
    public void updateTaskStatus(int requestId, int staffId, String newStatus) throws SQLException {

        String doneDate = "Completed".equals(newStatus) ? "NOW()" : "NULL";

        String taskSql = "UPDATE staff_tasks SET task_status = ?, date_completed = " + doneDate + " "
                       + "WHERE request_id = ? AND staff_id = ?";

        String requestSql = "UPDATE maintenance_requests SET status = ? WHERE request_id = ?";

        try (Connection conn = connect()) {

            conn.setAutoCommit(false);
            try {
                try (PreparedStatement pst = conn.prepareStatement(taskSql)) {
                    pst.setString(1, newStatus);
                    pst.setInt(2, requestId);
                    pst.setInt(3, staffId);
                    pst.executeUpdate();
                }

                try (PreparedStatement pst = conn.prepareStatement(requestSql)) {
                    pst.setString(1, newStatus);
                    pst.setInt(2, requestId);
                    pst.executeUpdate();
                }

                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    // ============================================================
    // DASHBOARD NUMBERS
    // ============================================================

    // How many requests per status, e.g. {"Pending"=3, "Completed"=5}
    // userId = 0 means count everyone's requests (admin)
    public Map<String, Integer> getRequestCounts(int userId) throws SQLException {

        String sql = "SELECT status, COUNT(*) FROM maintenance_requests "
                   + (userId > 0 ? "WHERE user_id = ? " : "")
                   + "GROUP BY status";

        try (Connection conn = connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            if (userId > 0) {
                pst.setInt(1, userId);
            }
            return readCounts(pst);
        }
    }

    // How many tasks per status for one staff member
    public Map<String, Integer> getTaskCounts(int staffId) throws SQLException {

        String sql = "SELECT task_status, COUNT(*) FROM staff_tasks "
                   + "WHERE staff_id = ? GROUP BY task_status";

        try (Connection conn = connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, staffId);
            return readCounts(pst);
        }
    }

    // Adds up all the numbers in a counts map
    public static int total(Map<String, Integer> counts) {
        int sum = 0;
        for (int n : counts.values()) {
            sum += n;
        }
        return sum;
    }

    // Helper: runs the query and puts the results into a Map
    private Map<String, Integer> readCounts(PreparedStatement pst) throws SQLException {
        Map<String, Integer> counts = new HashMap<>();
        try (ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                counts.put(rs.getString(1), rs.getInt(2));
            }
        }
        return counts;
    }
        // ============================================================
    // EXTRAS: dashboard tables, staff cards, admin numbers
    // ============================================================

    // Latest requests for the ADMIN dashboard table (6 columns)
    public void loadRecentRequests(DefaultTableModel model, int limit) throws SQLException {

        String sql = "SELECT request_id, issue, location, category, "
                   + "DATE_FORMAT(date_submitted, '%b %d, %Y') AS submitted, status "
                   + "FROM maintenance_requests ORDER BY request_id DESC LIMIT ?";

        model.setRowCount(0);

        try (Connection conn = connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, limit);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[] {
                        rs.getInt("request_id"), rs.getString("issue"), rs.getString("location"),
                        rs.getString("category"), rs.getString("submitted"), rs.getString("status")
                    });
                }
            }
        }
    }

    // Latest tasks for the STAFF dashboard table (6 columns)
    public void loadRecentTasks(DefaultTableModel model, int staffId, int limit) throws SQLException {

        String sql = "SELECT r.request_id, r.issue, r.location, r.category, "
                   + "DATE_FORMAT(r.date_submitted, '%b %d, %Y') AS submitted, t.task_status "
                   + "FROM staff_tasks t JOIN maintenance_requests r ON r.request_id = t.request_id "
                   + "WHERE t.staff_id = ? ORDER BY t.task_id DESC LIMIT ?";

        model.setRowCount(0);

        try (Connection conn = connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, staffId);
            pst.setInt(2, limit);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[] {
                        rs.getInt("request_id"), rs.getString("issue"), rs.getString("location"),
                        rs.getString("category"), rs.getString("submitted"), rs.getString("task_status")
                    });
                }
            }
        }
    }

    // One entry per staff member: {id, name, email, team, task count, date added}
    public java.util.List<String[]> getStaffList() throws SQLException {

        String sql = "SELECT u.user_id, u.user_fname, u.user_email, "
                   + "COALESCE(sp.team, 'No team yet') AS team, "
                   + "(SELECT COUNT(*) FROM staff_tasks t WHERE t.staff_id = u.user_id) AS tasks, "
                   + "DATE_FORMAT(u.created_at, '%b %d, %Y') AS added "
                   + "FROM tbl_users u LEFT JOIN staff_profiles sp ON sp.user_id = u.user_id "
                   + "WHERE u.role = 'staff' ORDER BY u.user_fname";

        java.util.List<String[]> list = new java.util.ArrayList<>();

        try (Connection conn = connect();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                list.add(new String[] {
                    String.valueOf(rs.getInt("user_id")),
                    rs.getString("user_fname"),
                    rs.getString("user_email"),
                    rs.getString("team"),
                    String.valueOf(rs.getInt("tasks")),
                    rs.getString("added")
                });
            }
        }
        return list;
    }

    // How many requests one admin reviewed, per status
    public Map<String, Integer> getReviewedCounts(int adminId) throws SQLException {

        String sql = "SELECT status, COUNT(*) FROM maintenance_requests "
                   + "WHERE reviewed_by = ? GROUP BY status";

        try (Connection conn = connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, adminId);
            return readCounts(pst);
        }
    }
        // ============================================================
    // REPORTS: numbers for the Reports & Analytics charts
    // ============================================================

    // Requests per category, e.g. {"Electrical"=4, "Plumbing"=2}
    public Map<String, Integer> getCategoryCounts() throws SQLException {

        String sql = "SELECT category, COUNT(*) FROM maintenance_requests GROUP BY category";

        try (Connection conn = connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            return readCounts(pst);
        }
    }

    // Completed tasks in the last 4 blocks of 7 days.
    // Index 0 = the last 7 days, 1 = the 7 days before that, ... 3 = oldest.
    public int[] getCompletedPerWeek() throws SQLException {

        String sql = "SELECT FLOOR(DATEDIFF(CURDATE(), DATE(date_completed)) / 7) AS wk, COUNT(*) "
                   + "FROM staff_tasks "
                   + "WHERE task_status = 'Completed' AND date_completed IS NOT NULL "
                   + "AND date_completed >= DATE_SUB(CURDATE(), INTERVAL 28 DAY) "
                   + "GROUP BY wk";

        int[] weeks = new int[4];

        try (Connection conn = connect();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                int wk = rs.getInt(1);
                if (wk >= 0 && wk < 4) {
                    weeks[wk] = rs.getInt(2);
                }
            }
        }
        return weeks;
    }
}
