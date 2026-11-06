/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Maintenance_Request_System;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.sql.SQLException;
import java.util.Map;
import javax.swing.table.DefaultTableModel;


/**
 *
 * @author pc
 */
public class StaffDashboard extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(StaffDashboard.class.getName());

    /**
     * Creates new form StaffDashboard
     */
    public StaffDashboard() {
        initComponents();
        
        //CALL OUT THE LINE CHARTTT TO DISPLAYYY
        setupLineChart();
        //CALL OUT THE PIECHART
        setupPieChart();
        
        //COMBOBOX
        addItems();
        
        //TABLE STUFF
        setupSearchPlaceholder(recTxt);
        //SEARCH BAR FOR REQUESTS
        recTxt.addActionListener(e -> recSearch.doClick());
        //SEARCH BAR FOR USERS
       
        
        
        //DASHBOARD BUTTON FUNCTIONS SWITCHING TABS
        makeClickable(dashboardBtn, dashboardlbl, 0);
        makeClickable(recBtn, reclbl, 1);
        makeClickable(staffBtn, stafflbl, 2);
        makeClickable(profileBtn, profilelbl, 3);
        makeClickable(logout, logoutlbl, -1);
        
        setProfileEditing(false);
        
        
        setupActionButtons();
        
        // "PENDING" here means tasks that are assigned but not accepted yet
        buttons1.addActionListener(e -> loadTasks(null));
        buttons2.addActionListener(e -> loadTasks("Assigned"));
        buttons3.addActionListener(e -> loadTasks("In Progress"));
        buttons4.addActionListener(e -> loadTasks("Completed"));
    
    }
    
    private userDao.UserAccount account;                 // the logged-in staff member
    private final RequestDAO requestDao = new RequestDAO();

    public StaffDashboard(userDao.UserAccount account) {
        this();
        this.account = account;

        name.setText(account.getFname());
        jLabel18.setText(Helper.todayText());
        jLabel26.setText("Contact No.");      // this field now saves the contact number

        loadProfile();
        refreshAll();
    }
    
    //LINECHARTTT METHOD NAMEEE
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

}
    ///PIECHARTTTT
    private void setupPieChart() {

    customPieChart1.clearData();

    customPieChart1.addData(
        "Pending",
        5,
        new Color(253,242,217)
    );

    customPieChart1.addData(
        "In Progress",
        3,
        new Color(100, 150, 255)
    );

    customPieChart1.addData(
        "Completed",
        7,
        new Color(29, 111, 90)
    );

    customPieChart1.setChartTitle(
        "Assigned Tasks"
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

            showRequestDetails(selectedRow);
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

        button.setBackground(normalColor);

        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return "See Details";
    }
}


// ==========================================
// OPEN ASSIGNED TASKS POPUP
// ==========================================
    private void showRequestDetails(int row) {
    int requestId = (int) tblRequests.getModel().getValueAt(row, 0);

    AssignedTasks dialog = new AssignedTasks(this, true, requestId, account.getUserId());
    dialog.setTitle("Assigned Tasks");
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);

    refreshAll();      // the status may have changed
}


        //DASHBOARD DESIGN
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
    staffdep.setEnabled(editing);
    staffspec.setEnabled(editing);
    staffavail.setEnabled(editing);

    // Show the correct buttons
    editBtn.setVisible(!editing);
    saveBtn.setVisible(editing);
    savecancelBtn.setVisible(editing);

    }
        private void addItems() {
    staffdep.setModel(new javax.swing.DefaultComboBoxModel<>(
        new String[] {
            "Electrical Maintenance",
            "Plumbing Maintenance",
            "General Facilities",
            "Builiding and Structural Maintenance",
            "Lab Equipment Maintenance",
            "General Maintenance Services"
        }));
    staffspec.setModel(new javax.swing.DefaultComboBoxModel<>(
        new String[] {
            "Electrical",
            "Plumbing",
            "Cleaning and Sanitation",
            "Building and Structural Repair",
            "Safety Equipment Maintenance",
            "Computer and IT Equipment"
        }));
        staffavail.setModel(new javax.swing.DefaultComboBoxModel<>(
        new String[] { "Available", "Busy", "On Leave" }));
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        menu = new Designs.RoundCorners();
        dashboardBtn = new Designs.RoundCorners();
        dashboardlbl = new javax.swing.JLabel();
        recBtn = new Designs.RoundCorners();
        reclbl = new javax.swing.JLabel();
        staffBtn = new Designs.RoundCorners();
        stafflbl = new javax.swing.JLabel();
        profileBtn = new Designs.RoundCorners();
        profilelbl = new javax.swing.JLabel();
        logout = new Designs.RoundCorners();
        logoutlbl = new javax.swing.JLabel();
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
        jLabel3 = new javax.swing.JLabel();
        circularLabel5 = new Maintenance_Request_System.CircularLabel();
        roundCorners11 = new Designs.RoundCorners();
        jLabel13 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        circularLabel3 = new Maintenance_Request_System.CircularLabel();
        roundCorners12 = new Designs.RoundCorners();
        jLabel14 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        circularLabel4 = new Maintenance_Request_System.CircularLabel();
        roundCorners13 = new Designs.RoundCorners();
        jLabel17 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        circularLabel6 = new Maintenance_Request_System.CircularLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new Designs.DesignTable();
        roundCorners2 = new Designs.RoundCorners();
        jLabel12 = new javax.swing.JLabel();
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
        reports = new javax.swing.JPanel();
        roundCorners3 = new Designs.RoundCorners();
        jLabel10 = new javax.swing.JLabel();
        customPieChart1 = new Maintenance_Request_System.CustomPieChart();
        reportPanel = new Maintenance_Request_System.CustomeLineChart();
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
        staffdep = new Maintenance_Request_System.RoundedComboBox();
        jLabel69 = new javax.swing.JLabel();
        staffspec = new Maintenance_Request_System.RoundedComboBox();
        jLabel70 = new javax.swing.JLabel();
        staffavail = new Maintenance_Request_System.RoundedComboBox();
        jLabel71 = new javax.swing.JLabel();
        whiteBtn1 = new Designs.WhiteBtn();
        savecancelBtn = new Designs.WhiteBtn();
        ProfilePass = new Maintenance_Request_System.RoundedPasswordField();
        jLabel19 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

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

        staffBtn.setBackground(new java.awt.Color(29, 111, 90));

        stafflbl.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        stafflbl.setForeground(new java.awt.Color(255, 255, 255));
        stafflbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        stafflbl.setText("REPORTS & ANALYTICS");

        javax.swing.GroupLayout staffBtnLayout = new javax.swing.GroupLayout(staffBtn);
        staffBtn.setLayout(staffBtnLayout);
        staffBtnLayout.setHorizontalGroup(
            staffBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(staffBtnLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(stafflbl, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(33, Short.MAX_VALUE))
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
                .addGap(15, 15, 15)
                .addComponent(profilelbl)
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

        javax.swing.GroupLayout menuLayout = new javax.swing.GroupLayout(menu);
        menu.setLayout(menuLayout);
        menuLayout.setHorizontalGroup(
            menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dashboardBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(recBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(staffBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(profileBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(logout, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        menuLayout.setVerticalGroup(
            menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuLayout.createSequentialGroup()
                .addGap(109, 109, 109)
                .addComponent(dashboardBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(recBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(staffBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
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

        dashboard.setBackground(new java.awt.Color(255, 255, 255));

        roundCorners8.setBackground(new java.awt.Color(249, 248, 255));

        roundCorners9.setBackground(new java.awt.Color(204, 238, 222));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(29, 111, 90));
        jLabel8.setText("WELCOME,");

        name.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        name.setForeground(new java.awt.Color(29, 111, 90));
        name.setText("STAFF");

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
                .addContainerGap(161, Short.MAX_VALUE))
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

        roundCorners10.setBackground(new java.awt.Color(203, 252, 203));

        jLabel11.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("ASSIGNED TASKS");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("0");

        circularLabel5.setBackground(new java.awt.Color(120, 234, 135));
        circularLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SystemImages/assigned.png"))); // NOI18N

        javax.swing.GroupLayout roundCorners10Layout = new javax.swing.GroupLayout(roundCorners10);
        roundCorners10.setLayout(roundCorners10Layout);
        roundCorners10Layout.setHorizontalGroup(
            roundCorners10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners10Layout.createSequentialGroup()
                .addContainerGap(27, Short.MAX_VALUE)
                .addComponent(circularLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(roundCorners10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundCorners10Layout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(19, 19, 19))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundCorners10Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(65, 65, 65))))
        );
        roundCorners10Layout.setVerticalGroup(
            roundCorners10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners10Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(roundCorners10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(circularLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(roundCorners10Layout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        roundCorners11.setBackground(new java.awt.Color(253, 242, 217));

        jLabel13.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("PENDING");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("0");

        circularLabel3.setBackground(new java.awt.Color(236, 206, 155));
        circularLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SystemImages/pending.logo.png"))); // NOI18N

        javax.swing.GroupLayout roundCorners11Layout = new javax.swing.GroupLayout(roundCorners11);
        roundCorners11.setLayout(roundCorners11Layout);
        roundCorners11Layout.setHorizontalGroup(
            roundCorners11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners11Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(circularLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(roundCorners11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundCorners11Layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(roundCorners11Layout.createSequentialGroup()
                        .addGap(51, 51, 51)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(54, Short.MAX_VALUE))
        );
        roundCorners11Layout.setVerticalGroup(
            roundCorners11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners11Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addContainerGap(25, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundCorners11Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(circularLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );

        roundCorners12.setBackground(new java.awt.Color(230, 204, 255));

        jLabel14.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("IN PROGRESS");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("0");

        circularLabel4.setBackground(new java.awt.Color(185, 160, 220));
        circularLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SystemImages/progress.icon.png"))); // NOI18N

        javax.swing.GroupLayout roundCorners12Layout = new javax.swing.GroupLayout(roundCorners12);
        roundCorners12.setLayout(roundCorners12Layout);
        roundCorners12Layout.setHorizontalGroup(
            roundCorners12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundCorners12Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(circularLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(roundCorners12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundCorners12Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(jLabel14))
                    .addGroup(roundCorners12Layout.createSequentialGroup()
                        .addGap(56, 56, 56)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(58, Short.MAX_VALUE))
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
                        .addComponent(jLabel5)))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        roundCorners13.setBackground(new java.awt.Color(216, 224, 248));

        jLabel17.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("COMPLETED");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("0");

        circularLabel6.setBackground(new java.awt.Color(141, 155, 228));
        circularLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SystemImages/completed.icon.png"))); // NOI18N

        javax.swing.GroupLayout roundCorners13Layout = new javax.swing.GroupLayout(roundCorners13);
        roundCorners13.setLayout(roundCorners13Layout);
        roundCorners13Layout.setHorizontalGroup(
            roundCorners13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners13Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(circularLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(roundCorners13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundCorners13Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(jLabel17))
                    .addGroup(roundCorners13Layout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(53, Short.MAX_VALUE))
        );
        roundCorners13Layout.setVerticalGroup(
            roundCorners13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners13Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(roundCorners13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(circularLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(roundCorners13Layout.createSequentialGroup()
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)))
                .addContainerGap(24, Short.MAX_VALUE))
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

        roundCorners2.setBackground(new java.awt.Color(204, 238, 222));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(29, 111, 90));
        jLabel12.setText("•  MY ASSIGNED TASKS");

        javax.swing.GroupLayout roundCorners2Layout = new javax.swing.GroupLayout(roundCorners2);
        roundCorners2.setLayout(roundCorners2Layout);
        roundCorners2Layout.setHorizontalGroup(
            roundCorners2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners2Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(172, Short.MAX_VALUE))
        );
        roundCorners2Layout.setVerticalGroup(
            roundCorners2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout roundCorners8Layout = new javax.swing.GroupLayout(roundCorners8);
        roundCorners8.setLayout(roundCorners8Layout);
        roundCorners8Layout.setHorizontalGroup(
            roundCorners8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(roundCorners8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundCorners8Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(roundCorners2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(roundCorners8Layout.createSequentialGroup()
                        .addGroup(roundCorners8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(roundCorners9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 850, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        roundCorners8Layout.setVerticalGroup(
            roundCorners8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(roundCorners9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(roundCorners8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(roundCorners10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(roundCorners11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(roundCorners12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(roundCorners13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(roundCorners2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
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

        buttons1.setText("ALL");

        buttons2.setText("PENDING");

        buttons3.setText("IN PROGRESS");

        buttons4.setText("COMPLETED");

        recTxt.setBackground(new java.awt.Color(236, 236, 236));
        recTxt.setText("search");

        recSearch.setText("Search");
        recSearch.addActionListener(this::recSearchActionPerformed);

        javax.swing.GroupLayout manage_recLayout = new javax.swing.GroupLayout(manage_rec);
        manage_rec.setLayout(manage_recLayout);
        manage_recLayout.setHorizontalGroup(
            manage_recLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(manage_recLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(manage_recLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(roundCorners14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 1001, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addContainerGap(56, Short.MAX_VALUE))
        );

        pages.addTab("tab2", manage_rec);

        reports.setBackground(new java.awt.Color(249, 248, 255));

        roundCorners3.setBackground(new java.awt.Color(204, 238, 222));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(29, 111, 90));
        jLabel10.setText("MAINTENANCE REPORTS & ANALYTICS");

        javax.swing.GroupLayout roundCorners3Layout = new javax.swing.GroupLayout(roundCorners3);
        roundCorners3.setLayout(roundCorners3Layout);
        roundCorners3Layout.setHorizontalGroup(
            roundCorners3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners3Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(692, Short.MAX_VALUE))
        );
        roundCorners3Layout.setVerticalGroup(
            roundCorners3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundCorners3Layout.createSequentialGroup()
                .addContainerGap(23, Short.MAX_VALUE)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );

        javax.swing.GroupLayout customPieChart1Layout = new javax.swing.GroupLayout(customPieChart1);
        customPieChart1.setLayout(customPieChart1Layout);
        customPieChart1Layout.setHorizontalGroup(
            customPieChart1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 472, Short.MAX_VALUE)
        );
        customPieChart1Layout.setVerticalGroup(
            customPieChart1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 278, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout reportPanelLayout = new javax.swing.GroupLayout(reportPanel);
        reportPanel.setLayout(reportPanelLayout);
        reportPanelLayout.setHorizontalGroup(
            reportPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        reportPanelLayout.setVerticalGroup(
            reportPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 505, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout reportsLayout = new javax.swing.GroupLayout(reports);
        reports.setLayout(reportsLayout);
        reportsLayout.setHorizontalGroup(
            reportsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(reportsLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(reportsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(reportsLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(customPieChart1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(reportPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 582, Short.MAX_VALUE)
                        .addGap(34, 34, 34))
                    .addGroup(reportsLayout.createSequentialGroup()
                        .addComponent(roundCorners3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(33, 33, 33))))
        );
        reportsLayout.setVerticalGroup(
            reportsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, reportsLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(roundCorners3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37)
                .addGroup(reportsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(customPieChart1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(reportPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 505, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(51, Short.MAX_VALUE))
        );

        pages.addTab("tab4", reports);

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

        jLabel69.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel69.setText("Department");

        jLabel70.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel70.setText("Specialization");

        jLabel71.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel71.setText("Availability");

        whiteBtn1.setText("Delete");
        whiteBtn1.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N

        savecancelBtn.setText("Cancel");
        savecancelBtn.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        savecancelBtn.addActionListener(this::savecancelBtnActionPerformed);

        javax.swing.GroupLayout roundCorners1Layout = new javax.swing.GroupLayout(roundCorners1);
        roundCorners1.setLayout(roundCorners1Layout);
        roundCorners1Layout.setHorizontalGroup(
            roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners1Layout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundCorners1Layout.createSequentialGroup()
                        .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(roundCorners1Layout.createSequentialGroup()
                                .addComponent(circularLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(77, 77, 77)
                                .addComponent(uploadBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(whiteBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(roundCorners1Layout.createSequentialGroup()
                                .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(ProfileName, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                                    .addGroup(roundCorners1Layout.createSequentialGroup()
                                        .addGap(8, 8, 8)
                                        .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(ProfileEmail, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                                    .addGroup(roundCorners1Layout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(ProfilePass, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(123, 123, 123)
                                .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(staffavail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jLabel70, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel69, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(staffdep, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                                        .addComponent(staffspec, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addComponent(jLabel71, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(317, 317, Short.MAX_VALUE))
                    .addGroup(roundCorners1Layout.createSequentialGroup()
                        .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(officeUnit, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, roundCorners1Layout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 500, Short.MAX_VALUE)
                        .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundCorners1Layout.createSequentialGroup()
                                .addComponent(saveBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(savecancelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(editBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(31, 31, 31))))
        );
        roundCorners1Layout.setVerticalGroup(
            roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners1Layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(uploadBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(whiteBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(circularLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(49, 49, 49)
                .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(jLabel69))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(staffdep, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ProfileName, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE))
                .addGap(11, 11, 11)
                .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(jLabel70))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ProfileEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(staffspec, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(jLabel71))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(staffavail, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                    .addComponent(ProfilePass, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundCorners1Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(saveBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(savecancelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addComponent(editBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(roundCorners1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel26)
                        .addGap(4, 4, 4)
                        .addComponent(officeUnit, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(37, Short.MAX_VALUE))
        );

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(29, 111, 90));
        jLabel19.setText("MAINTENANCE STAFF DETAILS");

        javax.swing.GroupLayout profileLayout = new javax.swing.GroupLayout(profile);
        profile.setLayout(profileLayout);
        profileLayout.setHorizontalGroup(
            profileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(profileLayout.createSequentialGroup()
                .addGroup(profileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(profileLayout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 392, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(profileLayout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(roundCorners1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(48, Short.MAX_VALUE))
        );
        profileLayout.setVerticalGroup(
            profileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, profileLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(roundCorners1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(50, Short.MAX_VALUE))
        );

        pages.addTab("tab5", profile);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(menu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(header, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pages)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(menu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(header, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pages))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents
    
    private void refreshAll() {
        loadTasks(null);

        try {
            Map<String, Integer> c = requestDao.getTaskCounts(account.getUserId());
            requestDao.loadRecentTasks((DefaultTableModel) jTable1.getModel(), account.getUserId(), 10);

            int pending = c.getOrDefault("Assigned", 0);
            int inProgress = c.getOrDefault("In Progress", 0);
            int completed = c.getOrDefault("Completed", 0);

            jLabel3.setText(String.valueOf(pending + inProgress + completed));   // all assigned tasks
            jLabel4.setText(String.valueOf(pending));
            jLabel5.setText(String.valueOf(inProgress));
            jLabel6.setText(String.valueOf(completed));

            // pie chart (only slices bigger than 0)
            customPieChart1.clearData();
            if (pending > 0)    customPieChart1.addData("Pending", pending, new Color(253, 242, 217));
            if (inProgress > 0) customPieChart1.addData("In Progress", inProgress, new Color(100, 150, 255));
            if (completed > 0)  customPieChart1.addData("Completed", completed, new Color(29, 111, 90));
            customPieChart1.setChartTitle("Assigned Tasks");

        } catch (SQLException e) {
            Helper.showError(this, e);
        }
    }

    // taskStatus = null means ALL
    private void loadTasks(String taskStatus) {
        try {
            requestDao.loadStaffTasks((DefaultTableModel) tblRequests.getModel(),
                                      account.getUserId(), taskStatus);
        } catch (SQLException e) {
            Helper.showError(this, e);
        }
    }

    private void loadProfile() {
        ProfileName.setText(account.getFname());
        ProfileEmail.setText(account.getEmail());
        ProfilePass.setText(account.getPass());

        try {
            // {team, specialization, availability, contact no}
            String[] p = new userDao().getStaffProfile(account.getUserId());
            if (p != null) {
                staffdep.setSelectedItem(p[0]);
                staffspec.setSelectedItem(p[1]);
                staffavail.setSelectedItem(p[2]);
                officeUnit.setText(p[3] == null ? "" : p[3]);
            }
        } catch (SQLException e) {
            Helper.showError(this, e);
        }
    }
    
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
   
        String newName = ProfileName.getText().trim();
        String newEmail = ProfileEmail.getText().trim();
        String newPass = ProfilePass.getText();

        if (newName.isEmpty() || newEmail.isEmpty() || newPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name, email and password cannot be empty.");
            return;
        }

        try {
            new userDao().saveStaffProfile(account.getUserId(), newName, newEmail, newPass,
                    staffdep.getSelectedItem().toString(),
                    staffspec.getSelectedItem().toString(),
                    staffavail.getSelectedItem().toString(),
                    officeUnit.getText().trim());

            setProfileEditing(false);
            name.setText(newName);
            JOptionPane.showMessageDialog(this, "Changes saved successfully!");

        } catch (SQLException e) {
            Helper.showError(this, e);
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
        java.awt.EventQueue.invokeLater(() -> new StaffDashboard().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Maintenance_Request_System.RoundedSearchField ProfileEmail;
    private Maintenance_Request_System.RoundedSearchField ProfileName;
    private Maintenance_Request_System.RoundedPasswordField ProfilePass;
    private Designs.Buttons buttons1;
    private Designs.Buttons buttons2;
    private Designs.Buttons buttons3;
    private Designs.Buttons buttons4;
    private Maintenance_Request_System.CircularLabel circularLabel1;
    private Maintenance_Request_System.CircularLabel circularLabel3;
    private Maintenance_Request_System.CircularLabel circularLabel4;
    private Maintenance_Request_System.CircularLabel circularLabel5;
    private Maintenance_Request_System.CircularLabel circularLabel6;
    private Maintenance_Request_System.CustomPieChart customPieChart1;
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
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private Designs.RoundCorners logout;
    private javax.swing.JLabel logoutlbl;
    private javax.swing.JPanel manage_rec;
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
    private Designs.RoundCorners roundCorners1;
    private Designs.RoundCorners roundCorners10;
    private Designs.RoundCorners roundCorners11;
    private Designs.RoundCorners roundCorners12;
    private Designs.RoundCorners roundCorners13;
    private Designs.RoundCorners roundCorners14;
    private Designs.RoundCorners roundCorners2;
    private Designs.RoundCorners roundCorners3;
    private Designs.RoundCorners roundCorners8;
    private Designs.RoundCorners roundCorners9;
    private Designs.Buttons saveBtn;
    private Designs.WhiteBtn savecancelBtn;
    private Designs.RoundCorners staffBtn;
    private Maintenance_Request_System.RoundedComboBox staffavail;
    private Maintenance_Request_System.RoundedComboBox staffdep;
    private javax.swing.JLabel stafflbl;
    private Maintenance_Request_System.RoundedComboBox staffspec;
    private javax.swing.JTable tblRequests;
    private Designs.Buttons uploadBtn;
    private Designs.WhiteBtn whiteBtn1;
    // End of variables declaration//GEN-END:variables
}
