/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Maintenance_Request_System;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.util.Map;

/**
 *
 * @author pc
 */
public class AdminDashboard extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(AdminDashboard.class.getName());

    
    public AdminDashboard() {
        initComponents();
        
        
        // CALL OUT PIECHARTTT
        setupPieChart();
        //CALL OUT LIEN CHART
        setupLineChart();
        
        //COMBOBOX
        addItems();
        
        setupSearchPlaceholder(recTxt);
        setupSearchPlaceholder(txtSearch);
        
        
        //SEARCH BAR FOR REQUESTS
        recTxt.addActionListener(e -> recSearch.doClick());
        //SEARCH BAR FOR USERS
        txtSearch.addActionListener(e -> btnSearch.doClick());
        
        
        //DASHBOARD BUTTON FUNCTIONS SWITCHING TABS
        makeClickable(dashboardBtn, dashboardlbl, 0);
        makeClickable(recBtn, reclbl, 1);
        makeClickable(usersBtn, userlbl, 2);
        makeClickable(staffBtn, stafflbl, 3);
        makeClickable(profileBtn, profilelbl, 4);
        makeClickable(logout, logoutlbl,-1);
        makeClickable(reportsBtn, reportslbl, 5);
        
        //profile editing
        setProfileEditing(false);
        //table buttons
        showUserRequests();
        showUserCounts();
        setupActionButtons();
        
        buttons2.setText("PENDING");
        buttons1.addActionListener(e -> loadRequests(null));
        buttons2.addActionListener(e -> loadRequests("Pending"));
        buttons3.addActionListener(e -> loadRequests("Declined"));
        buttons4.addActionListener(e -> loadRequests("Completed"));
                whiteBtn1.addActionListener(e -> {
            if (staffPage > 0) {
                staffPage--;
                showStaffCards();
            }
        });
        buttons9.addActionListener(e -> {
            if ((staffPage + 1) * 4 < staffList.size()) {
                staffPage++;
                showStaffCards();
            }
        });
    }
    private userDao.UserAccount account;     // the logged-in user

    private final RequestDAO requestDao = new RequestDAO();
    
    public AdminDashboard(userDao.UserAccount account) {
    this();
    this.account = account;

    name.setText(account.getFname());
    jLabel18.setText(Helper.todayText());

    // The 4 dashboard cards become: Assigned / Pending / Declined / Completed
    jLabel13.setText("PENDING");        // was "APPROVED"
    jLabel17.setText("COMPLETED");      // was "REVIEWED"

    loadProfile();
    refreshDashboard();
    loadStaffCards();
}

private void showUserRequests() {
    DefaultTableModel model = (DefaultTableModel) tblUsers.getModel();
    try {
        new userDao().loadUserRequests(model);
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        e.printStackTrace();
    }
}
    // Reloads the requests table, the 4 cards and both pie charts
// Reloads the requests table, the 4 cards and both pie charts
private void refreshDashboard() {
    loadRequests(null);

    try {
        Map<String, Integer> c = requestDao.getRequestCounts(0);   // 0 = everyone's requests
        requestDao.loadRecentRequests((DefaultTableModel) jTable1.getModel(), 10);
        int pending = c.getOrDefault("Pending", 0);
        int approved = c.getOrDefault("Approved", 0);               // NEW
        int assigned = c.getOrDefault("Assigned", 0);
        int inProgress = c.getOrDefault("In Progress", 0);
        int declined = c.getOrDefault("Declined", 0);
        int completed = c.getOrDefault("Completed", 0);

        jLabel28.setText(String.valueOf(assigned));
        jLabel29.setText(String.valueOf(pending));
        jLabel30.setText(String.valueOf(declined));
        jLabel31.setText(String.valueOf(completed));

        // Reports tab pie chart
        customPieChart1.clearData();
        addSlice(customPieChart1, "Pending", pending, new Color(253, 242, 217));
        addSlice(customPieChart1, "Approved", approved, new Color(204, 238, 222));   // NEW
        addSlice(customPieChart1, "Assigned", assigned, new Color(216, 224, 248));
        addSlice(customPieChart1, "In Progress", inProgress, new Color(230, 204, 255));
        addSlice(customPieChart1, "Declined", declined, new Color(253, 203, 203));
        addSlice(customPieChart1, "Completed", completed, new Color(203, 252, 203));
        customPieChart1.setChartTitle("Request Progress Overview");

        // Dashboard pie chart
        customPieChart2.clearData();
        addSlice(customPieChart2, "Accepted", approved + assigned + inProgress + completed, new Color(203, 252, 203));   // CHANGED
        addSlice(customPieChart2, "Declined", declined, new Color(253, 203, 203));
        customPieChart2.setChartTitle("Request Approval Status");

    } catch (SQLException e) {
        Helper.showError(this, e);
    }
}

// Only adds a pie slice when the number is more than 0
private void addSlice(CustomPieChart chart, String label, int value, Color color) {
    if (value > 0) {
        chart.addData(label, value, color);
    }
}

// status = null means ALL
private void loadRequests(String status) {
    try {
        requestDao.loadAllRequests((DefaultTableModel) tblRequests.getModel(), status);
    } catch (SQLException e) {
        Helper.showError(this, e);
    }
}
    private java.util.List<String[]> staffList = new java.util.ArrayList<>();
    private int staffPage = 0;      // 0 = first 4 staff, 1 = next 4, ...

    private void loadStaffCards() {
        try {
            staffList = requestDao.getStaffList();
            staffPage = 0;
            showStaffCards();
        } catch (SQLException e) {
            Helper.showError(this, e);
        }
    }

    // Fills the 4 cards with the staff of the current page
    private void showStaffCards() {
        java.awt.Component[] cards = { roundCorners15, roundCorners16, roundCorners17, roundCorners18 };
        JLabel[] names  = { jLabel38, jLabel47, jLabel56, jLabel65 };
        JLabel[] ids    = { jLabel39, jLabel48, jLabel57, jLabel66 };
        JLabel[] teams  = { jLabel40, jLabel49, jLabel58, jLabel67 };
        JLabel[] emails = { jLabel36, jLabel45, jLabel54, jLabel63 };
        JLabel[] tasks  = { jLabel37, jLabel46, jLabel55, jLabel64 };

        for (int i = 0; i < 4; i++) {
            int index = staffPage * 4 + i;

            if (index >= staffList.size()) {
                cards[i].setVisible(false);          // no staff for this card
                continue;
            }

            String[] s = staffList.get(index);       // {id, name, email, team, tasks, date added}
            cards[i].setVisible(true);
            names[i].setText(s[1]);
            ids[i].setText("Staff ID: " + s[0]);
            teams[i].setText(s[3]);
            emails[i].setText(s[2]);
            tasks[i].setText("Assigned Tasks: " + s[4]);
        }
    }

    // cardNumber is 0, 1, 2 or 3
    private void openStaffDetails(int cardNumber) {
        String[] s = staffList.get(staffPage * 4 + cardNumber);

        StaffDetails d = new StaffDetails(this, true);
        d.loadStaff(Integer.parseInt(s[0]), s[1], s[2], s[5]);
        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }
    
// Counts the roles in the Users table for the 3 cards
private void showUserCounts() {
    DefaultTableModel model = (DefaultTableModel) tblUsers.getModel();
    int staff = 0;
    int admins = 0;

    for (int i = 0; i < model.getRowCount(); i++) {
        String role = String.valueOf(model.getValueAt(i, 4));
        if (role.equals("staff")) {
            staff++;
        } else if (role.equals("admin")) {
            admins++;
        }
    }

    jLabel32.setText(String.valueOf(model.getRowCount()));   // total users
    jLabel33.setText(String.valueOf(staff));
    jLabel34.setText(String.valueOf(admins));
}

private void loadProfile() {
    ProfileName.setText(account.getFname());
    ProfileEmail.setText(account.getEmail());
    ProfilePass.setText(account.getPass());

    try {
        userDao.AdminProfile p = new userDao().getAdminProfile(account.getUserId());
        if (p != null) {
            officeUnit.setText(p.getOffice());
            adminpos.setSelectedItem(p.getPosition());
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        e.printStackTrace();
    }
}
    
   
    private void setupLineChart() {

    CustomeLineChart chart = new CustomeLineChart();

    chart.setTitle("Completed Tasks");
    chart.setXAxisTitle("Week");
    chart.setYAxisTitle("Tasks");

    chart.addData("Week 1", 5);
    chart.addData("Week 2", 8);
    chart.addData("Week 3", 6);
    chart.addData("Week 4", 10);

    reportPanel.setLayout(new java.awt.BorderLayout());
    reportPanel.add(chart, java.awt.BorderLayout.CENTER);

    reportPanel.revalidate();
    reportPanel.repaint();
    }//PIECHARTTT
   private void setupPieChart() {

    customPieChart1.clearData();

    customPieChart1.addData(
        "Assigned",
        5,
        new Color(216,224,248)
    );

    customPieChart1.addData(
        "Approved",
        3,
        new Color(203,252,203)
    );

    customPieChart1.addData(
        "Declined",
        7,
        new Color(253,203,203)
    );
    customPieChart1.addData(
        "Reviewed",
        7,
        new Color(230,204,255)
    );

    customPieChart1.setChartTitle(
        "Request Progress Overview"
    );
    
    customPieChart2.addData(
        "Approved",
        5,
        new Color(203,252,203)
    );

    customPieChart2.addData(
        "Declined",
        3,
        new Color(253,203,203)
    );
    customPieChart2.setChartTitle(
        "Request Approval Status"
    );

 
}
    //MAKE SEARCCH BAR TEXT GREY
    private void setupSearchPlaceholder(javax.swing.JTextField field) {

    String placeholder = "Search";

    field.setText(placeholder);
    field.setForeground(java.awt.Color.GRAY);

    field.addFocusListener(new java.awt.event.FocusAdapter() {

        @Override
        public void focusGained(java.awt.event.FocusEvent evt) {
            if (field.getText().equals(placeholder)) {
                field.setText("");
                field.setForeground(java.awt.Color.BLACK);
            }
        }

        @Override
        public void focusLost(java.awt.event.FocusEvent evt) {
            if (field.getText().trim().isEmpty()) {
                field.setText(placeholder);
                field.setForeground(java.awt.Color.GRAY);
            }
        }
    });
}
    
    private void setupActionButtons() {

        //tblRequests
    int actionColumn = -1;

    javax.swing.table.TableModel model =
            tblRequests.getModel();

    // Find the Action column
    for (int i = 0; i < model.getColumnCount(); i++) {
        if ("Action".equals(model.getColumnName(i))) {
            actionColumn = i;
            break;
        }
    }

    // Add the Action column if missing
    if (actionColumn == -1) {
        ((javax.swing.table.DefaultTableModel) model)
                .addColumn("Action");

        actionColumn = model.getColumnCount() - 1;
    }

    tblRequests.getColumnModel()
            .getColumn(actionColumn)
            .setCellRenderer(new DetailsButtonRenderer());

    tblRequests.getColumnModel()
            .getColumn(actionColumn)
            .setCellEditor(new DetailsButtonEditor());

    tblRequests.getColumnModel()
            .getColumn(actionColumn)
            .setPreferredWidth(100);

    tblRequests.setRowHeight(35);
    
    
    
    //tblUsers

    
    int userActionColumn = -1;

    javax.swing.table.TableModel userModel =
            tblUsers.getModel();

    // Find the Action column
    for (int i = 0; i < userModel.getColumnCount(); i++) {
        if ("View Details".equals(userModel.getColumnName(i))) {
            userActionColumn = i;
            break;
        }
    }

    // Add Action column if missing
    if (userActionColumn == -1) {
        ((javax.swing.table.DefaultTableModel) userModel)
                .addColumn("Action");

        userActionColumn = userModel.getColumnCount() - 1;
    }

    // Apply the same button design
    tblUsers.getColumnModel()
            .getColumn(userActionColumn)
            .setCellRenderer(new DetailsButtonRenderer());

    tblUsers.getColumnModel()
            .getColumn(userActionColumn)
            .setCellEditor(new DetailsButtonEditor());

    tblUsers.getColumnModel()
            .getColumn(userActionColumn)
            .setPreferredWidth(100);

    tblUsers.setRowHeight(35);

    
}
    // ==========================================
// SEE DETAILS BUTTON RENDERER
// ==========================================
private class DetailsButtonRenderer extends JButton
        implements javax.swing.table.TableCellRenderer {

    private final Color normalColor = new Color(44, 163, 132);

    public DetailsButtonRenderer() {
        setText("See Details");
        setForeground(Color.WHITE);
        setFont(new Font("SansSerif", Font.BOLD, 12));

        setBorderPainted(false);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);

        setHorizontalAlignment(SwingConstants.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2.setColor(normalColor);

        g2.fillRoundRect(
            3, 3,
            getWidth() - 6,
            getHeight() - 6,
            30, 30
        );

        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column) {

        return this;
    }
}


// ==========================================
// SEE DETAILS BUTTON EDITOR
// ==========================================
private class DetailsButtonEditor
        extends javax.swing.AbstractCellEditor
        implements javax.swing.table.TableCellEditor {

    private final Color normalColor = new Color(44, 163, 132);
    private final Color clickedColor = new Color(105, 205, 173);

    private final JButton button = new JButton("See Details") {

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(getBackground());

            g2.fillRoundRect(
                3, 3,
                getWidth() - 6,
                getHeight() - 6,
                30, 30
            );

            g2.dispose();
            super.paintComponent(g);
        }
    };

    private int modelRow;
    private JTable currentTable;

    DetailsButtonEditor() {

        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));

        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);

        button.setBackground(normalColor);
        button.setHorizontalAlignment(SwingConstants.CENTER);

        button.addActionListener(e -> {

            int selectedRow = modelRow;

            button.setBackground(clickedColor);

            fireEditingStopped();

            showDetails(currentTable, selectedRow);
        });
    }

    @Override
    public Component getTableCellEditorComponent(
            JTable table,
            Object value,
            boolean isSelected,
            int row,
            int column) {

        modelRow = table.convertRowIndexToModel(row);
        currentTable = table;

        button.setBackground(normalColor);

        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return "See Details";
    }
}


// ==========================================
// OPEN REQUEST DETAILS
// ==========================================
private void showDetails(JTable table, int row) {
    DefaultTableModel model = (DefaultTableModel) table.getModel();
    int id = (int) model.getValueAt(row, 0);

        if (table == tblRequests) {
        // a maintenance request
        String status = String.valueOf(model.getValueAt(row, 6));   // "Status" column

        // Pending = not accepted yet, so show the accept/decline window first
        if (status.equals("Pending")) {
            ApproveDeclinedWindow review = new ApproveDeclinedWindow(this, true, id, account.getUserId());
            review.setLocationRelativeTo(this);
            review.setVisible(true);

            if (!review.isApproved()) {       // declined or closed the window
                refreshDashboard();
                return;
            }
        }

        // Already accepted (or just accepted) -> assign staff
        RequestDetails dialog = new RequestDetails(this, true, id, account.getUserId());
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        refreshDashboard();                       // numbers may have changed
    } else {
        // a user in the Users table
        String role = String.valueOf(model.getValueAt(row, 4));
        String userName = String.valueOf(model.getValueAt(row, 1));
        String email = String.valueOf(model.getValueAt(row, 2));
        String date = String.valueOf(model.getValueAt(row, 3));

        if (role.equals("staff")) {
            StaffDetails d = new StaffDetails(this, true);
            d.loadStaff(id, userName, email, date);
            d.setLocationRelativeTo(this);
            d.setVisible(true);
        } else if (role.equals("admin")) {
            AdminDetails d = new AdminDetails(this, true);
            d.loadAdmin(id, userName, email, date);
            d.setLocationRelativeTo(this);
            d.setVisible(true);
        } else {
            UserDetails d = new UserDetails(this, true);
            d.loadStudent(id, userName, email, date);
            d.setLocationRelativeTo(this);
            d.setVisible(true);
        }
    }
}


        //DASHBOARD DESIGN MENU
        private JPanel selectedPanel = null;
        private JLabel selectedLabel = null;

        private void makeClickable(JPanel panel, JLabel label, int tabIndex) {

    Color defaultColor = new Color(29, 111, 90);
    Color hoverColor = new Color(55, 137, 114);
    Color selectedColor = Color.WHITE;
    Color selectedFont = new Color(29, 111, 90);

    panel.setBackground(defaultColor);
    label.setForeground(Color.WHITE);

    panel.setCursor(new java.awt.Cursor(
            java.awt.Cursor.HAND_CURSOR));

    panel.addMouseListener(new java.awt.event.MouseAdapter() {

        @Override
        public void mouseEntered(java.awt.event.MouseEvent e) {
            if (selectedPanel != panel) {
                panel.setBackground(hoverColor);
            }
        }

        @Override
        public void mouseExited(java.awt.event.MouseEvent e) {
            if (selectedPanel != panel) {
                panel.setBackground(defaultColor);
                label.setForeground(Color.WHITE);
            }
        
        }
        @Override
        public void mouseClicked(java.awt.event.MouseEvent e) {

            // Reset the previous selected panel
            if (selectedPanel != null) {
                selectedPanel.setBackground(defaultColor);
                selectedLabel.setForeground(Color.WHITE);
            }

            // Set the new selected panel
            selectedPanel = panel;
            selectedLabel = label;

            panel.setBackground(selectedColor);
            label.setForeground(selectedFont);

            // Logout
            if (tabIndex == -1) {
                login loginPage = new login();
                loginPage.setVisible(true);
                dispose();
                return;
            }
            // Switch to the selected tab
            pages.setSelectedIndex(tabIndex);
            
        }
    });
     

        pages.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
    @Override
    protected void paintTabArea(
            java.awt.Graphics g,
            int tabPlacement,
            int selectedIndex) {
        // Hide tab headers
    }

    @Override
    protected int calculateTabAreaHeight(
            int tabPlacement,
            int horizRunCount,
            int maxTabHeight) {
        return 0;
        }
});
}
        private String originalName;
        private String originalEmail;
        private String originalPassword;
        private String originalSection;
        
        private void setProfileEditing(boolean editing) {

    // Allow typing only when editing
    ProfileName.setEditable(editing);
    ProfileEmail.setEditable(editing);
    ProfilePass.setEditable(editing);
    officeUnit.setEditable(editing);

    // Show the correct buttons
    editBtn.setVisible(!editing);
    saveBtn.setVisible(editing);
    savecancelBtn.setVisible(editing);
}
        private void addItems() {
    admindep.setModel(new javax.swing.DefaultComboBoxModel<>(
        new String[] {
            "Facilities Management",
            "Operations Management",
            "Information Technology",
            "Property Management"
        }));
    adminpos.setModel(new javax.swing.DefaultComboBoxModel<>(
        new String[] {
            "Maintenance Administrator",
            "Facilities Administrator",
            "Maintenance Supervisor",
            "Facilities Mananger",
            "IT Administrator"
        }));
        
}


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        menu = new Designs.RoundCorners();
        dashboardBtn = new Designs.RoundCorners();
        dashboardlbl = new javax.swing.JLabel();
        recBtn = new Designs.RoundCorners();
        reclbl = new javax.swing.JLabel();
        usersBtn = new Designs.RoundCorners();
        userlbl = new javax.swing.JLabel();
        staffBtn = new Designs.RoundCorners();
        stafflbl = new javax.swing.JLabel();
        profileBtn = new Designs.RoundCorners();
        profilelbl = new javax.swing.JLabel();
        logout = new Designs.RoundCorners();
        logoutlbl = new javax.swing.JLabel();
        reportsBtn = new Designs.RoundCorners();
        reportslbl = new javax.swing.JLabel();
        header = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        pages = new javax.swing.JTabbedPane();
        dashboard = new javax.swing.JPanel();
        roundCorners8 = new Designs.RoundCorners();
        roundCorners9 = new Designs.RoundCorners();
        jLabel8 = new javax.swing.JLabel();
        name = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        roundCorners10 = new Designs.RoundCorners();
        jLabel11 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        circularLabel2 = new Maintenance_Request_System.CircularLabel();
        roundCorners11 = new Designs.RoundCorners();
        jLabel13 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        circularLabel3 = new Maintenance_Request_System.CircularLabel();
        roundCorners12 = new Designs.RoundCorners();
        jLabel14 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        circularLabel4 = new Maintenance_Request_System.CircularLabel();
        roundCorners13 = new Designs.RoundCorners();
        jLabel17 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        circularLabel5 = new Maintenance_Request_System.CircularLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new Designs.DesignTable();
        customPieChart2 = new Maintenance_Request_System.CustomPieChart();
        manage_rec = new Designs.WhiteGreyP();
        roundCorners14 = new Designs.RoundCorners();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblRequests = new Designs.DesignTable();
        buttons1 = new Designs.Buttons();
        buttons2 = new Designs.Buttons();
        buttons3 = new Designs.Buttons();
        buttons4 = new Designs.Buttons();
        recTxt = new Maintenance_Request_System.RoundedSearchField();
        recSearch = new Designs.Buttons();
        manage_users = new Designs.WhiteGreyP();
        roundCorners2 = new Designs.RoundCorners();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        roundCorners3 = new Designs.RoundCorners();
        jLabel5 = new javax.swing.JLabel();
        circularLabel6 = new Maintenance_Request_System.CircularLabel();
        jLabel32 = new javax.swing.JLabel();
        roundCorners4 = new Designs.RoundCorners();
        jLabel6 = new javax.swing.JLabel();
        circularLabel7 = new Maintenance_Request_System.CircularLabel();
        jLabel33 = new javax.swing.JLabel();
        roundCorners5 = new Designs.RoundCorners();
        jLabel19 = new javax.swing.JLabel();
        circularLabel8 = new Maintenance_Request_System.CircularLabel();
        jLabel34 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblUsers = new Designs.DesignTable();
        txtSearch = new Maintenance_Request_System.RoundedSearchField();
        btnSearch = new Designs.Buttons();
        staffs = new Designs.WhiteGreyP();
        roundCorners15 = new Designs.RoundCorners();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jPanel2 = new Designs.GreenP();
        circularLabel10 = new Maintenance_Request_System.CircularLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        buttons5 = new Designs.Buttons();
        jLabel12 = new javax.swing.JLabel();
        roundCorners16 = new Designs.RoundCorners();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jPanel3 = new Designs.GreenP();
        circularLabel11 = new Maintenance_Request_System.CircularLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        buttons6 = new Designs.Buttons();
        roundCorners17 = new Designs.RoundCorners();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        jPanel4 = new Designs.GreenP();
        circularLabel12 = new Maintenance_Request_System.CircularLabel();
        jLabel56 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        buttons7 = new Designs.Buttons();
        roundCorners18 = new Designs.RoundCorners();
        jLabel59 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        jPanel5 = new Designs.GreenP();
        circularLabel13 = new Maintenance_Request_System.CircularLabel();
        jLabel65 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        buttons8 = new Designs.Buttons();
        buttons9 = new Designs.Buttons();
        whiteBtn1 = new Designs.WhiteBtn();
        profile = new Designs.WhiteGreyP();
        roundCorners1 = new Designs.RoundCorners();
        circularLabel1 = new Maintenance_Request_System.CircularLabel();
        uploadBtn = new Designs.Buttons();
        ProfileName = new Maintenance_Request_System.RoundedSearchField();
        ProfileEmail = new Maintenance_Request_System.RoundedSearchField();
        officeUnit = new Maintenance_Request_System.RoundedSearchField();
        editBtn = new Designs.Buttons();
        saveBtn = new Designs.Buttons();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        savecancelBtn = new Designs.WhiteBtn();
        whiteBtn2 = new Designs.WhiteBtn();
        admindep = new Maintenance_Request_System.RoundedComboBox();
        jLabel68 = new javax.swing.JLabel();
        adminpos = new Maintenance_Request_System.RoundedComboBox();
        jLabel69 = new javax.swing.JLabel();
        ProfilePass = new Maintenance_Request_System.RoundedPasswordField();
        jLabel10 = new javax.swing.JLabel();
        reports = new javax.swing.JPanel();
        roundCorners7 = new Designs.RoundCorners();
        jLabel15 = new javax.swing.JLabel();
        customPieChart1 = new Maintenance_Request_System.CustomPieChart();
        customBarChart1 = new Maintenance_Request_System.CustomBarChart();
        reportPanel = new Maintenance_Request_System.CustomeLineChart();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        menu.setBackground(new java.awt.Color(29, 111, 90));

        dashboardBtn.setBackground(new java.awt.Color(29, 111, 90));

        dashboardlbl.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        dashboardlbl.setForeground(new java.awt.Color(255, 255, 255));
        dashboardlbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        dashboardlbl.setText("DASHBOARD");

        javax.swing.GroupLayout dashboardBtnLayout = new javax.swing.GroupLayout(dashboardBtn);
        dashboardBtn.setLayout(dashboardBtnLayout);
        dashboardBtnLayout.setHorizontalGroup(
            dashboardBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dashboardBtnLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(dashboardlbl, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        dashboardBtnLayout.setVerticalGroup(
            dashboardBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dashboardBtnLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(dashboardlbl, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                .addContainerGap())
        );

        recBtn.setBackground(new java.awt.Color(29, 111, 90));

        reclbl.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        reclbl.setForeground(new java.awt.Color(255, 255, 255));
        reclbl.setText("MANAGE REQUESTS");

        javax.swing.GroupLayout recBtnLayout = new javax.swing.GroupLayout(recBtn);
        recBtn.setLayout(recBtnLayout);
        recBtnLayout.setHorizontalGroup(
            recBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(recBtnLayout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addComponent(reclbl)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        recBtnLayout.setVerticalGroup(
            recBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, recBtnLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(reclbl, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                .addContainerGap())
        );

        usersBtn.setBackground(new java.awt.Color(29, 111, 90));

        userlbl.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        userlbl.setForeground(new java.awt.Color(255, 255, 255));
        userlbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        userlbl.setText("USER MANAGEMENT");

        javax.swing.GroupLayout usersBtnLayout = new javax.swing.GroupLayout(usersBtn);
        usersBtn.setLayout(usersBtnLayout);
        usersBtnLayout.setHorizontalGroup(
            usersBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(usersBtnLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(userlbl, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        usersBtnLayout.setVerticalGroup(
            usersBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, usersBtnLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(userlbl, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                .addContainerGap())
        );

        staffBtn.setBackground(new java.awt.Color(29, 111, 90));

        stafflbl.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        stafflbl.setForeground(new java.awt.Color(255, 255, 255));
        stafflbl.setText("STAFF MANAGEMENT");

        javax.swing.GroupLayout staffBtnLayout = new javax.swing.GroupLayout(staffBtn);
        staffBtn.setLayout(staffBtnLayout);
        staffBtnLayout.setHorizontalGroup(
            staffBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(staffBtnLayout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(stafflbl)
                .addContainerGap(41, Short.MAX_VALUE))
        );
        staffBtnLayout.setVerticalGroup(
            staffBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, staffBtnLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(stafflbl, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                .addContainerGap())
        );

        profileBtn.setBackground(new java.awt.Color(29, 111, 90));

        profilelbl.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        profilelbl.setForeground(new java.awt.Color(255, 255, 255));
        profilelbl.setText("PROFILE");

        javax.swing.GroupLayout profileBtnLayout = new javax.swing.GroupLayout(profileBtn);
        profileBtn.setLayout(profileBtnLayout);
        profileBtnLayout.setHorizontalGroup(
            profileBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(profileBtnLayout.createSequentialGroup()
                .addGap(79, 79, 79)
                .addComponent(profilelbl)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        profileBtnLayout.setVerticalGroup(
            profileBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(profileBtnLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(profilelbl, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        logoutlbl.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        logoutlbl.setText("LOGOUT");

        javax.swing.GroupLayout logoutLayout = new javax.swing.GroupLayout(logout);
        logout.setLayout(logoutLayout);
        logoutLayout.setHorizontalGroup(
            logoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(logoutLayout.createSequentialGroup()
                .addGap(79, 79, 79)
                .addComponent(logoutlbl)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        logoutLayout.setVerticalGroup(
            logoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(logoutLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(logoutlbl)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        reportsBtn.setBackground(new java.awt.Color(29, 111, 90));

        reportslbl.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        reportslbl.setForeground(new java.awt.Color(255, 255, 255));
        reportslbl.setText("REPORTS & ANALYTICS");

        javax.swing.GroupLayout reportsBtnLayout = new javax.swing.GroupLayout(reportsBtn);
        reportsBtn.setLayout(reportsBtnLayout);
        reportsBtnLayout.setHorizontalGroup(
            reportsBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(reportsBtnLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(reportslbl, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        reportsBtnLayout.setVerticalGroup(
            reportsBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, reportsBtnLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(reportslbl, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout menuLayout = new javax.swing.GroupLayout(menu);
        menu.setLayout(menuLayout);
        menuLayout.setHorizontalGroup(
            menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dashboardBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(recBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(usersBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(staffBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(profileBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(logout, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(reportsBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        menuLayout.setVerticalGroup(
            menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuLayout.createSequentialGroup()
                .addGap(109, 109, 109)
                .addComponent(dashboardBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(recBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(usersBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(staffBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(reportsBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(profileBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(logout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50))
        );

        header.setBackground(new java.awt.Color(255, 255, 255));

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SystemImages/title.logo.png"))); // NOI18N

        javax.swing.GroupLayout headerLayout = new javax.swing.GroupLayout(header);
        header.setLayout(headerLayout);
        headerLayout.setHorizontalGroup(
            headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 413, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        headerLayout.setVerticalGroup(
            headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(8, Short.MAX_VALUE))
        );

        pages.setBackground(new java.awt.Color(255, 255, 255));

        dashboard.setBackground(new java.awt.Color(249, 248, 255));

        roundCorners8.setBackground(new java.awt.Color(249, 248, 255));

        roundCorners9.setBackground(new java.awt.Color(204, 238, 222));

        jLabel8.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(29, 111, 90));
        jLabel8.setText("WELCOME,");

        name.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        name.setForeground(new java.awt.Color(29, 111, 90));
        name.setText("ADMIN");

        jLabel9.setFont(new java.awt.Font("Segoe UI Semibold", 2, 12)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(29, 111, 90));
        jLabel9.setText("Today is");

        jLabel18.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(29, 111, 90));
        jLabel18.setText("DATE");

        javax.swing.GroupLayout roundCorners9Layout = new javax.swing.GroupLayout(roundCorners9);
        roundCorners9.setLayout(roundCorners9Layout);
        roundCorners9Layout.setHorizontalGroup(
            roundCorners9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners9Layout.createSequentialGroup()
                .addGap(85, 85, 85)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(name, javax.swing.GroupLayout.PREFERRED_SIZE, 419, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(178, 178, 178)
                .addGroup(roundCorners9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(176, Short.MAX_VALUE))
        );
        roundCorners9Layout.setVerticalGroup(
            roundCorners9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundCorners9Layout.createSequentialGroup()
                .addContainerGap(19, Short.MAX_VALUE)
                .addGroup(roundCorners9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(name, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(17, 17, 17))
            .addGroup(roundCorners9Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel18)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        roundCorners10.setBackground(new java.awt.Color(216, 224, 248));

        jLabel11.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("ASSIGNED");

        jLabel28.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setText("0");

        circularLabel2.setBackground(new java.awt.Color(141, 155, 228));
        circularLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SystemImages/list.logo.png"))); // NOI18N

        javax.swing.GroupLayout roundCorners10Layout = new javax.swing.GroupLayout(roundCorners10);
        roundCorners10.setLayout(roundCorners10Layout);
        roundCorners10Layout.setHorizontalGroup(
            roundCorners10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners10Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(circularLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(roundCorners10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundCorners10Layout.createSequentialGroup()
                        .addGap(69, 69, 69)
                        .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(roundCorners10Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        roundCorners10Layout.setVerticalGroup(
            roundCorners10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners10Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(roundCorners10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(circularLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(roundCorners10Layout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel28)))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        roundCorners11.setBackground(new java.awt.Color(203, 252, 203));

        jLabel13.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("APPROVED");

        jLabel29.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setText("0");

        circularLabel3.setBackground(new java.awt.Color(120, 234, 135));
        circularLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SystemImages/approved.png"))); // NOI18N

        javax.swing.GroupLayout roundCorners11Layout = new javax.swing.GroupLayout(roundCorners11);
        roundCorners11.setLayout(roundCorners11Layout);
        roundCorners11Layout.setHorizontalGroup(
            roundCorners11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners11Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(circularLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(roundCorners11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundCorners11Layout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(roundCorners11Layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(55, Short.MAX_VALUE))
        );
        roundCorners11Layout.setVerticalGroup(
            roundCorners11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners11Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(roundCorners11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(circularLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(roundCorners11Layout.createSequentialGroup()
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel29)))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        roundCorners12.setBackground(new java.awt.Color(253, 203, 203));

        jLabel14.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("DECLINED");

        jLabel30.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setText("0");

        circularLabel4.setBackground(new java.awt.Color(245, 134, 134));
        circularLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SystemImages/decline.png"))); // NOI18N

        javax.swing.GroupLayout roundCorners12Layout = new javax.swing.GroupLayout(roundCorners12);
        roundCorners12.setLayout(roundCorners12Layout);
        roundCorners12Layout.setHorizontalGroup(
            roundCorners12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundCorners12Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(circularLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(roundCorners12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundCorners12Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(jLabel14))
                    .addGroup(roundCorners12Layout.createSequentialGroup()
                        .addGap(55, 55, 55)
                        .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(80, Short.MAX_VALUE))
        );
        roundCorners12Layout.setVerticalGroup(
            roundCorners12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners12Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(roundCorners12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(circularLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(roundCorners12Layout.createSequentialGroup()
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel30)))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        roundCorners13.setBackground(new java.awt.Color(230, 204, 255));

        jLabel17.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("REVIEWED");

        jLabel31.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setText("0");

        circularLabel5.setBackground(new java.awt.Color(185, 160, 220));
        circularLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SystemImages/completed.icon.png"))); // NOI18N

        javax.swing.GroupLayout roundCorners13Layout = new javax.swing.GroupLayout(roundCorners13);
        roundCorners13.setLayout(roundCorners13Layout);
        roundCorners13Layout.setHorizontalGroup(
            roundCorners13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners13Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(circularLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(roundCorners13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundCorners13Layout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(roundCorners13Layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(51, Short.MAX_VALUE))
        );
        roundCorners13Layout.setVerticalGroup(
            roundCorners13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners13Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(roundCorners13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(circularLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(roundCorners13Layout.createSequentialGroup()
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel31)))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Issue/Problem", "Location", "Category", "Date Submitted", "Status"
            }
        ));
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(7);
        }

        javax.swing.GroupLayout customPieChart2Layout = new javax.swing.GroupLayout(customPieChart2);
        customPieChart2.setLayout(customPieChart2Layout);
        customPieChart2Layout.setHorizontalGroup(
            customPieChart2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        customPieChart2Layout.setVerticalGroup(
            customPieChart2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 368, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout roundCorners8Layout = new javax.swing.GroupLayout(roundCorners8);
        roundCorners8.setLayout(roundCorners8Layout);
        roundCorners8Layout.setHorizontalGroup(
            roundCorners8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(roundCorners8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundCorners8Layout.createSequentialGroup()
                        .addGroup(roundCorners8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(roundCorners8Layout.createSequentialGroup()
                                .addComponent(roundCorners10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(roundCorners11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(roundCorners12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(roundCorners13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(roundCorners8Layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 888, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(28, 28, 28)
                                .addComponent(customPieChart2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(34, 34, 34))
                    .addGroup(roundCorners8Layout.createSequentialGroup()
                        .addComponent(roundCorners9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        roundCorners8Layout.setVerticalGroup(
            roundCorners8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(roundCorners9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(roundCorners8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(roundCorners10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(roundCorners11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(roundCorners12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(roundCorners13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                .addGroup(roundCorners8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(customPieChart2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17))
        );

        javax.swing.GroupLayout dashboardLayout = new javax.swing.GroupLayout(dashboard);
        dashboard.setLayout(dashboardLayout);
        dashboardLayout.setHorizontalGroup(
            dashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(roundCorners8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        dashboardLayout.setVerticalGroup(
            dashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(roundCorners8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pages.addTab("tab1", dashboard);

        manage_rec.setBackground(new java.awt.Color(255, 255, 255));

        roundCorners14.setBackground(new java.awt.Color(204, 238, 222));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(29, 111, 90));
        jLabel1.setText("MANAGE ALL REQUESTS");

        jLabel2.setFont(new java.awt.Font("Segoe UI Semibold", 2, 12)); // NOI18N
        jLabel2.setText("View and manage all maintenance requets submitted by users");

        javax.swing.GroupLayout roundCorners14Layout = new javax.swing.GroupLayout(roundCorners14);
        roundCorners14.setLayout(roundCorners14Layout);
        roundCorners14Layout.setHorizontalGroup(
            roundCorners14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners14Layout.createSequentialGroup()
                .addGap(106, 106, 106)
                .addGroup(roundCorners14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(648, Short.MAX_VALUE))
        );
        roundCorners14Layout.setVerticalGroup(
            roundCorners14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners14Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        tblRequests.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Requester", "Issue/Problem", "Location", "Category", "Date Submitted", "Status", "Action"
            }
        ));
        jScrollPane2.setViewportView(tblRequests);
        if (tblRequests.getColumnModel().getColumnCount() > 0) {
            tblRequests.getColumnModel().getColumn(0).setPreferredWidth(10);
            tblRequests.getColumnModel().getColumn(1).setPreferredWidth(100);
            tblRequests.getColumnModel().getColumn(5).setPreferredWidth(100);
        }

        buttons1.setText("ALL");
        buttons1.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N

        buttons2.setText("ASSIGNED");
        buttons2.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N

        buttons3.setText("DECLINED");
        buttons3.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N

        buttons4.setText("COMPLETED");
        buttons4.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N

        recTxt.setBackground(new java.awt.Color(236, 236, 236));
        recTxt.setText("search");

        recSearch.setText("Search");
        recSearch.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        recSearch.addActionListener(this::recSearchActionPerformed);

        javax.swing.GroupLayout manage_recLayout = new javax.swing.GroupLayout(manage_rec);
        manage_rec.setLayout(manage_recLayout);
        manage_recLayout.setHorizontalGroup(
            manage_recLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(manage_recLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(manage_recLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(roundCorners14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, manage_recLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(manage_recLayout.createSequentialGroup()
                            .addComponent(recTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(recSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(buttons1, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(12, 12, 12)
                            .addComponent(buttons2, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(buttons3, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(buttons4, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(111, 111, 111))
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 1001, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(31, Short.MAX_VALUE))
        );
        manage_recLayout.setVerticalGroup(
            manage_recLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(manage_recLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(roundCorners14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addGroup(manage_recLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttons1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttons2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttons3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttons4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(recTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(recSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(46, Short.MAX_VALUE))
        );

        pages.addTab("tab2", manage_rec);

        manage_users.setBackground(new java.awt.Color(255, 255, 255));

        roundCorners2.setBackground(new java.awt.Color(204, 238, 222));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(29, 111, 90));
        jLabel3.setText("USER MANAGEMENT");

        jLabel4.setFont(new java.awt.Font("Segoe UI Semibold", 2, 12)); // NOI18N
        jLabel4.setText("Manage registered users and their accounts");

        javax.swing.GroupLayout roundCorners2Layout = new javax.swing.GroupLayout(roundCorners2);
        roundCorners2.setLayout(roundCorners2Layout);
        roundCorners2Layout.setHorizontalGroup(
            roundCorners2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners2Layout.createSequentialGroup()
                .addGap(79, 79, 79)
                .addGroup(roundCorners2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        roundCorners2Layout.setVerticalGroup(
            roundCorners2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners2Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        roundCorners3.setBackground(new java.awt.Color(216, 224, 248));

        jLabel5.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        jLabel5.setText("TOTAL USERS");

        circularLabel6.setBackground(new java.awt.Color(141, 155, 228));
        circularLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SystemImages/users.png"))); // NOI18N

        jLabel32.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel32.setText("0");

        javax.swing.GroupLayout roundCorners3Layout = new javax.swing.GroupLayout(roundCorners3);
        roundCorners3.setLayout(roundCorners3Layout);
        roundCorners3Layout.setHorizontalGroup(
            roundCorners3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners3Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(circularLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(roundCorners3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundCorners3Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(roundCorners3Layout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(69, Short.MAX_VALUE))
        );
        roundCorners3Layout.setVerticalGroup(
            roundCorners3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners3Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel32)
                .addContainerGap(25, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundCorners3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(circularLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );

        roundCorners4.setBackground(new java.awt.Color(253, 242, 217));

        jLabel6.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        jLabel6.setText("MAINTENANCE STAFF");

        circularLabel7.setBackground(new java.awt.Color(236, 206, 155));
        circularLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SystemImages/staff.png"))); // NOI18N

        jLabel33.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel33.setText("0");

        javax.swing.GroupLayout roundCorners4Layout = new javax.swing.GroupLayout(roundCorners4);
        roundCorners4.setLayout(roundCorners4Layout);
        roundCorners4Layout.setHorizontalGroup(
            roundCorners4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundCorners4Layout.createSequentialGroup()
                .addContainerGap(31, Short.MAX_VALUE)
                .addComponent(circularLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(roundCorners4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundCorners4Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6))
                    .addGroup(roundCorners4Layout.createSequentialGroup()
                        .addGap(73, 73, 73)
                        .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(30, 30, 30))
        );
        roundCorners4Layout.setVerticalGroup(
            roundCorners4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners4Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel33)
                .addContainerGap(26, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundCorners4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(circularLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );

        roundCorners5.setBackground(new java.awt.Color(203, 252, 203));
        roundCorners5.setToolTipText("");

        jLabel19.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("ADMIN");

        circularLabel8.setBackground(new java.awt.Color(120, 234, 135));
        circularLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SystemImages/admin.png"))); // NOI18N

        jLabel34.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setText("0");

        javax.swing.GroupLayout roundCorners5Layout = new javax.swing.GroupLayout(roundCorners5);
        roundCorners5.setLayout(roundCorners5Layout);
        roundCorners5Layout.setHorizontalGroup(
            roundCorners5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundCorners5Layout.createSequentialGroup()
                .addContainerGap(37, Short.MAX_VALUE)
                .addComponent(circularLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(roundCorners5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundCorners5Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(roundCorners5Layout.createSequentialGroup()
                        .addGap(65, 65, 65)
                        .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(52, 52, 52))
        );
        roundCorners5Layout.setVerticalGroup(
            roundCorners5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners5Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel34)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundCorners5Layout.createSequentialGroup()
                .addContainerGap(27, Short.MAX_VALUE)
                .addComponent(circularLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );

        tblUsers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Full name", "Email", "Date Registered", "Role", "View Details"
            }
        ));
        jScrollPane3.setViewportView(tblUsers);

        txtSearch.setBackground(new java.awt.Color(236, 236, 236));
        txtSearch.setText("search");
        txtSearch.addActionListener(this::txtSearchActionPerformed);

        btnSearch.setText("Search");
        btnSearch.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        btnSearch.addActionListener(this::btnSearchActionPerformed);

        javax.swing.GroupLayout manage_usersLayout = new javax.swing.GroupLayout(manage_users);
        manage_users.setLayout(manage_usersLayout);
        manage_usersLayout.setHorizontalGroup(
            manage_usersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, manage_usersLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(roundCorners2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(manage_usersLayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(manage_usersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 874, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(manage_usersLayout.createSequentialGroup()
                        .addGroup(manage_usersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtSearch, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(roundCorners3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(manage_usersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(manage_usersLayout.createSequentialGroup()
                                .addGap(32, 32, 32)
                                .addComponent(roundCorners4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(38, 38, 38)
                                .addComponent(roundCorners5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(manage_usersLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(186, Short.MAX_VALUE))
        );
        manage_usersLayout.setVerticalGroup(
            manage_usersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(manage_usersLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(roundCorners2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(manage_usersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(roundCorners3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(roundCorners4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(roundCorners5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(manage_usersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                    .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 361, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pages.addTab("tab3", manage_users);

        staffs.setBackground(new java.awt.Color(249, 248, 255));

        roundCorners15.setBackground(new java.awt.Color(255, 255, 255));

        jLabel21.setText("Maintenance Department Team");

        jLabel22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SystemImages/smolEmail.png"))); // NOI18N

        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SystemImages/smolLoc.png"))); // NOI18N

        jLabel35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SystemImages/smolTask.png"))); // NOI18N

        jLabel36.setText("Email");

        jLabel37.setText("Assigned Tasks: ");

        jPanel2.setBackground(new java.awt.Color(153, 255, 204));

        circularLabel10.setBackground(new java.awt.Color(255, 255, 255));
        circularLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SystemImages/staff.png"))); // NOI18N

        jLabel38.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(255, 255, 255));
        jLabel38.setText("NAME");

        jLabel39.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        jLabel39.setForeground(new java.awt.Color(255, 255, 255));
        jLabel39.setText("STAFF ID");

        jLabel40.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        jLabel40.setForeground(new java.awt.Color(255, 255, 255));
        jLabel40.setText("Department");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(circularLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(circularLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel38)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel39)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel40)))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        buttons5.setText("See Details");
        buttons5.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        buttons5.addActionListener(this::buttons5ActionPerformed);

        javax.swing.GroupLayout roundCorners15Layout = new javax.swing.GroupLayout(roundCorners15);
        roundCorners15.setLayout(roundCorners15Layout);
        roundCorners15Layout.setHorizontalGroup(
            roundCorners15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners15Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(roundCorners15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel27)
                    .addComponent(jLabel22)
                    .addComponent(jLabel35))
                .addGap(18, 18, 18)
                .addGroup(roundCorners15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundCorners15Layout.createSequentialGroup()
                        .addGroup(roundCorners15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(roundCorners15Layout.createSequentialGroup()
                        .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(buttons5, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35))))
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        roundCorners15Layout.setVerticalGroup(
            roundCorners15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners15Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(roundCorners15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel36, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(12, 12, 12)
                .addGroup(roundCorners15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(roundCorners15Layout.createSequentialGroup()
                        .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel35)
                        .addGap(45, 45, 45))
                    .addGroup(roundCorners15Layout.createSequentialGroup()
                        .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(roundCorners15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(roundCorners15Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(roundCorners15Layout.createSequentialGroup()
                                .addGap(37, 37, 37)
                                .addComponent(buttons5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(24, 24, 24))))
        );

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(29, 111, 90));
        jLabel12.setText("STAFF MANAGEMENT");

        roundCorners16.setBackground(new java.awt.Color(255, 255, 255));

        jLabel41.setText("Maintenance Department Team");

        jLabel42.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SystemImages/smolEmail.png"))); // NOI18N

        jLabel43.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SystemImages/smolLoc.png"))); // NOI18N

        jLabel44.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SystemImages/smolTask.png"))); // NOI18N

        jLabel45.setText("Email");

        jLabel46.setText("Assigned Tasks: ");

        jPanel3.setBackground(new java.awt.Color(153, 255, 204));

        circularLabel11.setBackground(new java.awt.Color(255, 255, 255));
        circularLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SystemImages/staff.png"))); // NOI18N

        jLabel47.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        jLabel47.setForeground(new java.awt.Color(255, 255, 255));
        jLabel47.setText("NAME");

        jLabel48.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        jLabel48.setForeground(new java.awt.Color(255, 255, 255));
        jLabel48.setText("STAFF ID");

        jLabel49.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        jLabel49.setForeground(new java.awt.Color(255, 255, 255));
        jLabel49.setText("Department");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(circularLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(circularLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel47)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel48)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel49)))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        buttons6.setText("See Details");
        buttons6.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        buttons6.addActionListener(this::buttons6ActionPerformed);

        javax.swing.GroupLayout roundCorners16Layout = new javax.swing.GroupLayout(roundCorners16);
        roundCorners16.setLayout(roundCorners16Layout);
        roundCorners16Layout.setHorizontalGroup(
            roundCorners16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners16Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(roundCorners16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel43)
                    .addComponent(jLabel42)
                    .addComponent(jLabel44))
                .addGap(18, 18, 18)
                .addGroup(roundCorners16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundCorners16Layout.createSequentialGroup()
                        .addGroup(roundCorners16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(roundCorners16Layout.createSequentialGroup()
                        .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 71, Short.MAX_VALUE)
                        .addComponent(buttons6, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35))))
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        roundCorners16Layout.setVerticalGroup(
            roundCorners16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners16Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(roundCorners16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel45, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                    .addComponent(jLabel42, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE))
                .addGap(12, 12, 12)
                .addGroup(roundCorners16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(roundCorners16Layout.createSequentialGroup()
                        .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel44)
                        .addGap(45, 45, 45))
                    .addGroup(roundCorners16Layout.createSequentialGroup()
                        .addComponent(jLabel41, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                        .addGroup(roundCorners16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(roundCorners16Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(roundCorners16Layout.createSequentialGroup()
                                .addGap(37, 37, 37)
                                .addComponent(buttons6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(24, 24, 24))))
        );

        roundCorners17.setBackground(new java.awt.Color(255, 255, 255));

        jLabel50.setText("Maintenance Department Team");

        jLabel51.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SystemImages/smolEmail.png"))); // NOI18N

        jLabel52.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SystemImages/smolLoc.png"))); // NOI18N

        jLabel53.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SystemImages/smolTask.png"))); // NOI18N

        jLabel54.setText("Email");

        jLabel55.setText("Assigned Tasks: ");

        jPanel4.setBackground(new java.awt.Color(153, 255, 204));

        circularLabel12.setBackground(new java.awt.Color(255, 255, 255));
        circularLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SystemImages/staff.png"))); // NOI18N

        jLabel56.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        jLabel56.setForeground(new java.awt.Color(255, 255, 255));
        jLabel56.setText("NAME");

        jLabel57.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        jLabel57.setForeground(new java.awt.Color(255, 255, 255));
        jLabel57.setText("STAFF ID");

        jLabel58.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        jLabel58.setForeground(new java.awt.Color(255, 255, 255));
        jLabel58.setText("Department");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(circularLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel58, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(circularLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel56)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel57)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel58)))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        buttons7.setText("See Details");
        buttons7.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        buttons7.addActionListener(this::buttons7ActionPerformed);

        javax.swing.GroupLayout roundCorners17Layout = new javax.swing.GroupLayout(roundCorners17);
        roundCorners17.setLayout(roundCorners17Layout);
        roundCorners17Layout.setHorizontalGroup(
            roundCorners17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners17Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(roundCorners17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel52)
                    .addComponent(jLabel51)
                    .addComponent(jLabel53))
                .addGap(18, 18, 18)
                .addGroup(roundCorners17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundCorners17Layout.createSequentialGroup()
                        .addGroup(roundCorners17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(roundCorners17Layout.createSequentialGroup()
                        .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(buttons7, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35))))
            .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        roundCorners17Layout.setVerticalGroup(
            roundCorners17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners17Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(roundCorners17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel54, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
                    .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(12, 12, 12)
                .addGroup(roundCorners17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(roundCorners17Layout.createSequentialGroup()
                        .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel53)
                        .addGap(45, 45, 45))
                    .addGroup(roundCorners17Layout.createSequentialGroup()
                        .addComponent(jLabel50, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(roundCorners17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(roundCorners17Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(roundCorners17Layout.createSequentialGroup()
                                .addGap(37, 37, 37)
                                .addComponent(buttons7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(24, 24, 24))))
        );

        roundCorners18.setBackground(new java.awt.Color(255, 255, 255));

        jLabel59.setText("Maintenance Department Team");

        jLabel60.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SystemImages/smolEmail.png"))); // NOI18N

        jLabel61.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SystemImages/smolLoc.png"))); // NOI18N

        jLabel62.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SystemImages/smolTask.png"))); // NOI18N

        jLabel63.setText("Email");

        jLabel64.setText("Assigned Tasks: ");

        jPanel5.setBackground(new java.awt.Color(153, 255, 204));

        circularLabel13.setBackground(new java.awt.Color(255, 255, 255));
        circularLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SystemImages/staff.png"))); // NOI18N

        jLabel65.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        jLabel65.setForeground(new java.awt.Color(255, 255, 255));
        jLabel65.setText("NAME");

        jLabel66.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        jLabel66.setForeground(new java.awt.Color(255, 255, 255));
        jLabel66.setText("STAFF ID");

        jLabel67.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        jLabel67.setForeground(new java.awt.Color(255, 255, 255));
        jLabel67.setText("Department");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(circularLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel65, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel66, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel67, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(circularLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel65)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel66)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel67)))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        buttons8.setText("See Details");
        buttons8.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        buttons8.addActionListener(this::buttons8ActionPerformed);

        javax.swing.GroupLayout roundCorners18Layout = new javax.swing.GroupLayout(roundCorners18);
        roundCorners18.setLayout(roundCorners18Layout);
        roundCorners18Layout.setHorizontalGroup(
            roundCorners18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners18Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(roundCorners18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel61)
                    .addComponent(jLabel60)
                    .addComponent(jLabel62))
                .addGap(18, 18, 18)
                .addGroup(roundCorners18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundCorners18Layout.createSequentialGroup()
                        .addGroup(roundCorners18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel59, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel63, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(roundCorners18Layout.createSequentialGroup()
                        .addComponent(jLabel64, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 71, Short.MAX_VALUE)
                        .addComponent(buttons8, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35))))
            .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        roundCorners18Layout.setVerticalGroup(
            roundCorners18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners18Layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(roundCorners18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel63, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel60, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12)
                .addGroup(roundCorners18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(roundCorners18Layout.createSequentialGroup()
                        .addComponent(jLabel61, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel62)
                        .addGap(45, 45, 45))
                    .addGroup(roundCorners18Layout.createSequentialGroup()
                        .addComponent(jLabel59, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(roundCorners18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(roundCorners18Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel64, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(roundCorners18Layout.createSequentialGroup()
                                .addGap(37, 37, 37)
                                .addComponent(buttons8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(24, 24, 24))))
        );

        buttons9.setText(">");
        buttons9.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        whiteBtn1.setText("<");
        whiteBtn1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        javax.swing.GroupLayout staffsLayout = new javax.swing.GroupLayout(staffs);
        staffs.setLayout(staffsLayout);
        staffsLayout.setHorizontalGroup(
            staffsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(staffsLayout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addGroup(staffsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 409, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(staffsLayout.createSequentialGroup()
                        .addGroup(staffsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(roundCorners16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(roundCorners15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(50, 50, 50)
                        .addGroup(staffsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(roundCorners18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(roundCorners17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(155, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, staffsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(whiteBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(buttons9, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33))
        );
        staffsLayout.setVerticalGroup(
            staffsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(staffsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(staffsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(roundCorners15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(roundCorners17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31)
                .addGroup(staffsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(roundCorners18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(roundCorners16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(staffsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttons9, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(whiteBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pages.addTab("tab4", staffs);

        profile.setBackground(new java.awt.Color(255, 255, 255));

        roundCorners1.setBackground(new java.awt.Color(203, 231, 222));

        circularLabel1.setBackground(new java.awt.Color(255, 255, 255));
        circularLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SystemImages/uploadIMG.png"))); // NOI18N

        uploadBtn.setText("Upload Photo");
        uploadBtn.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N

        ProfileName.setText("Name");

        ProfileEmail.setText("Email");

        officeUnit.setText("Section");

        editBtn.setText("Edit");
        editBtn.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        editBtn.addActionListener(this::editBtnActionPerformed);

        saveBtn.setText("Save Changes");
        saveBtn.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        saveBtn.addActionListener(this::saveBtnActionPerformed);

        jLabel23.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel23.setText("Name");

        jLabel24.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel24.setText("Email");

        jLabel25.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel25.setText("Password");

        jLabel26.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel26.setText("Office/Unit");

        savecancelBtn.setText("Cancel");
        savecancelBtn.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        savecancelBtn.addActionListener(this::savecancelBtnActionPerformed);

        whiteBtn2.setText("Cancel");
        whiteBtn2.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N

        jLabel68.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel68.setText("Department");

        jLabel69.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel69.setText("Position");

        ProfilePass.setText("roundedPasswordField1");

        javax.swing.GroupLayout roundCorners1Layout = new javax.swing.GroupLayout(roundCorners1);
        roundCorners1.setLayout(roundCorners1Layout);
        roundCorners1Layout.setHorizontalGroup(
            roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners1Layout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundCorners1Layout.createSequentialGroup()
                        .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, roundCorners1Layout.createSequentialGroup()
                                .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(officeUnit, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 485, Short.MAX_VALUE)
                                .addComponent(saveBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(15, 15, 15)
                                .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(editBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                                    .addComponent(savecancelBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, roundCorners1Layout.createSequentialGroup()
                                .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(ProfileEmail, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                                        .addComponent(ProfileName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(114, 114, 114)
                                .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel69, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(admindep, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                                        .addComponent(jLabel68, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(adminpos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(31, 31, 31))
                    .addGroup(roundCorners1Layout.createSequentialGroup()
                        .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(roundCorners1Layout.createSequentialGroup()
                                .addComponent(circularLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(77, 77, 77)
                                .addComponent(uploadBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(whiteBtn2, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(ProfilePass, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        roundCorners1Layout.setVerticalGroup(
            roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners1Layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(uploadBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(whiteBtn2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(circularLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(49, 49, 49)
                .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(jLabel68))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(admindep, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ProfileName, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE))
                .addGap(11, 11, 11)
                .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(jLabel69))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(adminpos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ProfileEmail, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jLabel25)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ProfilePass, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(roundCorners1Layout.createSequentialGroup()
                        .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(saveBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(savecancelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addComponent(editBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(roundCorners1Layout.createSequentialGroup()
                        .addComponent(jLabel26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(officeUnit, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)))
                .addContainerGap(32, Short.MAX_VALUE))
        );

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(29, 111, 90));
        jLabel10.setText("ADMIN INFORMATION DETAILS");

        javax.swing.GroupLayout profileLayout = new javax.swing.GroupLayout(profile);
        profile.setLayout(profileLayout);
        profileLayout.setHorizontalGroup(
            profileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(profileLayout.createSequentialGroup()
                .addGroup(profileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(profileLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(roundCorners1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(profileLayout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 409, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(62, Short.MAX_VALUE))
        );
        profileLayout.setVerticalGroup(
            profileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, profileLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(roundCorners1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(50, Short.MAX_VALUE))
        );

        pages.addTab("tab5", profile);

        reports.setBackground(new java.awt.Color(249, 248, 255));

        roundCorners7.setBackground(new java.awt.Color(204, 238, 222));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(29, 111, 90));
        jLabel15.setText("MAINTENANCE MANAGEMENT ANALYTICS");

        javax.swing.GroupLayout roundCorners7Layout = new javax.swing.GroupLayout(roundCorners7);
        roundCorners7.setLayout(roundCorners7Layout);
        roundCorners7Layout.setHorizontalGroup(
            roundCorners7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners7Layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 414, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(652, Short.MAX_VALUE))
        );
        roundCorners7Layout.setVerticalGroup(
            roundCorners7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners7Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout customPieChart1Layout = new javax.swing.GroupLayout(customPieChart1);
        customPieChart1.setLayout(customPieChart1Layout);
        customPieChart1Layout.setHorizontalGroup(
            customPieChart1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 420, Short.MAX_VALUE)
        );
        customPieChart1Layout.setVerticalGroup(
            customPieChart1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 272, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout customBarChart1Layout = new javax.swing.GroupLayout(customBarChart1);
        customBarChart1.setLayout(customBarChart1Layout);
        customBarChart1Layout.setHorizontalGroup(
            customBarChart1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        customBarChart1Layout.setVerticalGroup(
            customBarChart1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 215, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout reportPanelLayout = new javax.swing.GroupLayout(reportPanel);
        reportPanel.setLayout(reportPanelLayout);
        reportPanelLayout.setHorizontalGroup(
            reportPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        reportPanelLayout.setVerticalGroup(
            reportPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 482, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout reportsLayout = new javax.swing.GroupLayout(reports);
        reports.setLayout(reportsLayout);
        reportsLayout.setHorizontalGroup(
            reportsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, reportsLayout.createSequentialGroup()
                .addGroup(reportsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, reportsLayout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(reportsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(customPieChart1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(customBarChart1, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE))
                        .addGap(41, 41, 41)
                        .addComponent(reportPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, reportsLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(roundCorners7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(28, 28, 28))
        );
        reportsLayout.setVerticalGroup(
            reportsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(reportsLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(roundCorners7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addGroup(reportsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(reportsLayout.createSequentialGroup()
                        .addComponent(customPieChart1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(customBarChart1, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(reportPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 482, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(44, Short.MAX_VALUE))
        );

        pages.addTab("tab6", reports);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(menu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(header, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pages)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(menu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(header, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pages, javax.swing.GroupLayout.PREFERRED_SIZE, 716, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        // TODO add your handling code here:
        String keyword = txtSearch.getText().trim();

javax.swing.table.TableRowSorter<javax.swing.table.TableModel> sorter =
    (javax.swing.table.TableRowSorter<javax.swing.table.TableModel>)
    tblUsers.getRowSorter();

if (sorter == null) {
    sorter = new javax.swing.table.TableRowSorter<>(
        tblUsers.getModel()
    );

    tblUsers.setRowSorter(sorter);
}

if (keyword.isEmpty() || keyword.equals("Search")) {
    sorter.setRowFilter(null);
} else {
    sorter.setRowFilter(
        javax.swing.RowFilter.regexFilter(
            "(?i)" + java.util.regex.Pattern.quote(keyword)
        )
    );
}
    }//GEN-LAST:event_btnSearchActionPerformed

    private void recSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_recSearchActionPerformed
        // TODO add your handling code here:
        String keyword = recTxt.getText().trim();

javax.swing.table.TableRowSorter<javax.swing.table.TableModel> sorter =
    (javax.swing.table.TableRowSorter<javax.swing.table.TableModel>)
    tblRequests.getRowSorter();

if (sorter == null) {
    sorter = new javax.swing.table.TableRowSorter<>(
        tblRequests.getModel()
    );

    tblRequests.setRowSorter(sorter);
}

if (keyword.isEmpty() || keyword.equals("Search")) {
    sorter.setRowFilter(null);
} else {
    sorter.setRowFilter(
        javax.swing.RowFilter.regexFilter(
            "(?i)" + java.util.regex.Pattern.quote(keyword)
        )
    );
}
    }//GEN-LAST:event_recSearchActionPerformed

    private void editBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editBtnActionPerformed
       

    // Save the current values
    originalName = ProfileName.getText();
    originalEmail = ProfileEmail.getText();
    originalPassword = ProfilePass.getText();
    originalSection = officeUnit.getText();

    // Enable editing
    setProfileEditing(true);

    }//GEN-LAST:event_editBtnActionPerformed

    private void saveBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveBtnActionPerformed
    String name = ProfileName.getText().trim();
    String email = ProfileEmail.getText().trim();
    String pass = ProfilePass.getText();
    String office = officeUnit.getText().trim();
    String position = adminpos.getSelectedItem().toString();

    if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
    JOptionPane.showMessageDialog(this, "Name, email and password cannot be empty.");
    return;
}

    try {
    new userDao().saveAdminProfile(account.getUserId(), name, email, pass, office, position);

    setProfileEditing(false);
    JOptionPane.showMessageDialog(this, "Changes saved successfully!");

    } catch (SQLException e) {
    JOptionPane.showMessageDialog(this, "Could not save: " + e.getMessage());
    e.printStackTrace();
}

    }//GEN-LAST:event_saveBtnActionPerformed

    private void savecancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_savecancelBtnActionPerformed
        // TODO add your handling code here:
         // Restore the original values
    ProfileName.setText(originalName);
    ProfileEmail.setText(originalEmail);
    ProfilePass.setText(originalPassword);
    officeUnit.setText(originalSection);

    // Return to read-only mode
    setProfileEditing(false);
    }//GEN-LAST:event_savecancelBtnActionPerformed

    private void buttons5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttons5ActionPerformed
        // TODO add your handling code here:
        openStaffDetails(0);

    }//GEN-LAST:event_buttons5ActionPerformed

    private void buttons6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttons6ActionPerformed
        // TODO add your handling code here:
        openStaffDetails(1);
    }//GEN-LAST:event_buttons6ActionPerformed

    private void buttons7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttons7ActionPerformed
        // TODO add your handling code here:
        openStaffDetails(2);
    }//GEN-LAST:event_buttons7ActionPerformed

    private void buttons8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttons8ActionPerformed
        // TODO add your handling code here:
        openStaffDetails(3);
    }//GEN-LAST:event_buttons8ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new AdminDashboard().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Maintenance_Request_System.RoundedSearchField ProfileEmail;
    private Maintenance_Request_System.RoundedSearchField ProfileName;
    private Maintenance_Request_System.RoundedPasswordField ProfilePass;
    private Maintenance_Request_System.RoundedComboBox admindep;
    private Maintenance_Request_System.RoundedComboBox adminpos;
    private Designs.Buttons btnSearch;
    private Designs.Buttons buttons1;
    private Designs.Buttons buttons2;
    private Designs.Buttons buttons3;
    private Designs.Buttons buttons4;
    private Designs.Buttons buttons5;
    private Designs.Buttons buttons6;
    private Designs.Buttons buttons7;
    private Designs.Buttons buttons8;
    private Designs.Buttons buttons9;
    private Maintenance_Request_System.CircularLabel circularLabel1;
    private Maintenance_Request_System.CircularLabel circularLabel10;
    private Maintenance_Request_System.CircularLabel circularLabel11;
    private Maintenance_Request_System.CircularLabel circularLabel12;
    private Maintenance_Request_System.CircularLabel circularLabel13;
    private Maintenance_Request_System.CircularLabel circularLabel2;
    private Maintenance_Request_System.CircularLabel circularLabel3;
    private Maintenance_Request_System.CircularLabel circularLabel4;
    private Maintenance_Request_System.CircularLabel circularLabel5;
    private Maintenance_Request_System.CircularLabel circularLabel6;
    private Maintenance_Request_System.CircularLabel circularLabel7;
    private Maintenance_Request_System.CircularLabel circularLabel8;
    private Maintenance_Request_System.CustomBarChart customBarChart1;
    private Maintenance_Request_System.CustomPieChart customPieChart1;
    private Maintenance_Request_System.CustomPieChart customPieChart2;
    private javax.swing.JPanel dashboard;
    private Designs.RoundCorners dashboardBtn;
    private javax.swing.JLabel dashboardlbl;
    private Designs.Buttons editBtn;
    private javax.swing.JPanel header;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private Designs.RoundCorners logout;
    private javax.swing.JLabel logoutlbl;
    private javax.swing.JPanel manage_rec;
    private javax.swing.JPanel manage_users;
    private Designs.RoundCorners menu;
    private javax.swing.JLabel name;
    private Maintenance_Request_System.RoundedSearchField officeUnit;
    private javax.swing.JTabbedPane pages;
    private javax.swing.JPanel profile;
    private Designs.RoundCorners profileBtn;
    private javax.swing.JLabel profilelbl;
    private Designs.RoundCorners recBtn;
    private Designs.Buttons recSearch;
    private Maintenance_Request_System.RoundedSearchField recTxt;
    private javax.swing.JLabel reclbl;
    private Maintenance_Request_System.CustomeLineChart reportPanel;
    private javax.swing.JPanel reports;
    private Designs.RoundCorners reportsBtn;
    private javax.swing.JLabel reportslbl;
    private Designs.RoundCorners roundCorners1;
    private Designs.RoundCorners roundCorners10;
    private Designs.RoundCorners roundCorners11;
    private Designs.RoundCorners roundCorners12;
    private Designs.RoundCorners roundCorners13;
    private Designs.RoundCorners roundCorners14;
    private Designs.RoundCorners roundCorners15;
    private Designs.RoundCorners roundCorners16;
    private Designs.RoundCorners roundCorners17;
    private Designs.RoundCorners roundCorners18;
    private Designs.RoundCorners roundCorners2;
    private Designs.RoundCorners roundCorners3;
    private Designs.RoundCorners roundCorners4;
    private Designs.RoundCorners roundCorners5;
    private Designs.RoundCorners roundCorners7;
    private Designs.RoundCorners roundCorners8;
    private Designs.RoundCorners roundCorners9;
    private Designs.Buttons saveBtn;
    private Designs.WhiteBtn savecancelBtn;
    private Designs.RoundCorners staffBtn;
    private javax.swing.JLabel stafflbl;
    private javax.swing.JPanel staffs;
    private javax.swing.JTable tblRequests;
    private javax.swing.JTable tblUsers;
    private Maintenance_Request_System.RoundedSearchField txtSearch;
    private Designs.Buttons uploadBtn;
    private javax.swing.JLabel userlbl;
    private Designs.RoundCorners usersBtn;
    private Designs.WhiteBtn whiteBtn1;
    private Designs.WhiteBtn whiteBtn2;
    // End of variables declaration//GEN-END:variables
}
