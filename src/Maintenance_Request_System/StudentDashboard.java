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
import java.sql.SQLException;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

public class StudentDashboard extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(StudentDashboard.class.getName());

    /**
     * Creates new form StudentDashboard
     */
    public StudentDashboard() {
        initComponents();
        setupSearchPlaceholder(recTxt);
        
        
        
        //SEARCH BAR FOR REQUESTS
        recTxt.addActionListener(e -> recSearch.doClick());
        //SEARCH BAR FOR USERS
       
        //COMBOBOX
        addItems();
        
        
        //DASHBOARD BUTTON FUNCTIONS SWITCHING TABS
        makeClickable(dashboardBtn, dashboardlbl, 0);
        makeClickable(recBtn, reclbl, 1);
        makeClickable(usersBtn, userlbl, 2);
        makeClickable(profileBtn, pfplbl, 3);
       
        makeClickable(logout, logoutlbl, -1);
        
        setProfileEditing(false);
        
        
        setupActionButtons();
        
        submit.addActionListener(e -> submitRequest());
        photoUP.addActionListener(e -> choosePhoto());
        buttons8.addActionListener(e -> pages.setSelectedIndex(2));   // "NEW REQUEST" button

        // filter buttons on the "My Requests" page
        buttons1.addActionListener(e -> loadRequests(null));
        buttons2.addActionListener(e -> loadRequests("Pending"));
        buttons3.addActionListener(e -> loadRequests("In Progress"));
        buttons4.addActionListener(e -> loadRequests("Completed"));
        
        
    }
        private userDao.UserAccount account;                 // the logged-in student
        private final RequestDAO requestDao = new RequestDAO();
        private String selectedPhotoPath = null;             // photo chosen for a new request

        public StudentDashboard(userDao.UserAccount account) {
        this();                                          // build the screen first
        this.account = account;

        name.setText(account.getFname());
        jLabel18.setText(Helper.todayText());
        fname.setText(account.getFname());
        fname.setEditable(false);

        loadProfile();
        refreshAll();
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
// OPEN REQUEST DETAILS
// ==========================================
    private void showRequestDetails(int row) {
        int requestId = (int) tblRequests.getModel().getValueAt(row, 0);

        ViewDetails dialog = new ViewDetails(this, true, requestId);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        refreshAll();     // the student may have cancelled the request
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
        //PROFILE PAGE
        private String originalName;
        private String originalEmail;
        private String originalPassword;
        private String originalSection;
        
        private void setProfileEditing(boolean editing) {

    // Allow typing only when editing
    ProfileName.setEditable(editing);
    ProfileEmail.setEditable(editing);
    ProfilePass.setEditable(editing);
    ProfileSec.setEditable(editing);
    depart.setEnabled(editing);
    grade.setEnabled(editing);

    // Show the correct buttons
    editBtn.setVisible(!editing);
    saveBtn.setVisible(editing);
    savecancelBtn.setVisible(editing);
    }
        private void addItems() {
    categ.setModel(new javax.swing.DefaultComboBoxModel<>(
        new String[] {
            "Electrical",
            "Plumbing",
            "Furniture",
            "Air Conditioning",
            "Doors and Locks",
            "Windows",
            "Ceiling and Roofing",
            "Walls/Floors",
            "Computer Eequipments",
            "Laboratory Equipement",
            "Restroom Facilities",
            "Drainange/Sewage",
            "Canteen Equipement",
            "Structural Areas",
            "Fire safety Equipment",
            "Classroom Equipment",
            "Others"
        }
    ));

    loc.setModel(new javax.swing.DefaultComboBoxModel<>(
        new String[] {
            "Main Building",
            "Administration Office",
            "Library",
            "CompLab 1",
            "ComLab 2",
            "Registrar's Office",
            "Classroom",
            "Guidance Office",
            "Auditorium",
            "Canteen/Cafeteria",
            "Restroom",
            "Staircase",
            "Lobby/Reception Area",
            "Clinic/Infirmary",
            "Others"
            
        }
    ));

        grade.setModel(new javax.swing.DefaultComboBoxModel<>(Helper.YEAR_LEVELS));
        depart.setModel(new javax.swing.DefaultComboBoxModel<>(Helper.DEPARTMENTS));
        strDprt.setModel(new javax.swing.DefaultComboBoxModel<>(Helper.DEPARTMENTS));
        roundedComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "LOW", "MEDIUM", "HIGH" }));
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
        usersBtn = new Designs.RoundCorners();
        userlbl = new javax.swing.JLabel();
        profileBtn = new Designs.RoundCorners();
        pfplbl = new javax.swing.JLabel();
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
        circularLabel3 = new Maintenance_Request_System.CircularLabel();
        jLabel31 = new javax.swing.JLabel();
        roundCorners11 = new Designs.RoundCorners();
        jLabel13 = new javax.swing.JLabel();
        circularLabel4 = new Maintenance_Request_System.CircularLabel();
        jLabel32 = new javax.swing.JLabel();
        roundCorners12 = new Designs.RoundCorners();
        jLabel14 = new javax.swing.JLabel();
        circularLabel5 = new Maintenance_Request_System.CircularLabel();
        jLabel33 = new javax.swing.JLabel();
        roundCorners13 = new Designs.RoundCorners();
        jLabel17 = new javax.swing.JLabel();
        circularLabel6 = new Maintenance_Request_System.CircularLabel();
        jLabel34 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new Designs.DesignTable();
        roundCorners4 = new Designs.RoundCorners();
        circularLabel1 = new Maintenance_Request_System.CircularLabel();
        jLabel22 = new javax.swing.JLabel();
        buttons8 = new Designs.Buttons();
        jLabel27 = new javax.swing.JLabel();
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
        submitReq = new Designs.WhiteGreyP();
        roundCorners3 = new Designs.RoundCorners();
        issue = new Maintenance_Request_System.RoundedSearchField();
        loc = new Maintenance_Request_System.RoundedComboBox();
        categ = new Maintenance_Request_System.RoundedComboBox();
        desc = new Maintenance_Request_System.RoundedTextArea();
        imglbl = new javax.swing.JLabel();
        submit = new Designs.Buttons();
        photoUP = new Designs.Buttons();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        fname = new Maintenance_Request_System.RoundedSearchField();
        jLabel3 = new javax.swing.JLabel();
        grSec = new Maintenance_Request_System.RoundedSearchField();
        jLabel10 = new javax.swing.JLabel();
        strDprt = new Maintenance_Request_System.RoundedComboBox();
        jLabel12 = new javax.swing.JLabel();
        cancelReq = new Designs.WhiteBtn();
        roundedComboBox1 = new Maintenance_Request_System.RoundedComboBox();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        roundCorners2 = new Designs.RoundCorners();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        profile = new Designs.WhiteGreyP();
        roundCorners1 = new Designs.RoundCorners();
        profilelbl = new Maintenance_Request_System.CircularLabel();
        uploadBtn = new Designs.Buttons();
        ProfileName = new Maintenance_Request_System.RoundedSearchField();
        ProfileEmail = new Maintenance_Request_System.RoundedSearchField();
        ProfileSec = new Maintenance_Request_System.RoundedSearchField();
        editBtn = new Designs.Buttons();
        saveBtn = new Designs.Buttons();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        grade = new Maintenance_Request_System.RoundedComboBox();
        depart = new Maintenance_Request_System.RoundedComboBox();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        savecancelBtn = new Designs.WhiteBtn();
        whiteBtn1 = new Designs.WhiteBtn();
        ProfilePass = new Maintenance_Request_System.RoundedPasswordField();
        jLabel30 = new javax.swing.JLabel();

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
                .addComponent(dashboardlbl, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
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
        reclbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        reclbl.setText("MY REQUESTS");

        javax.swing.GroupLayout recBtnLayout = new javax.swing.GroupLayout(recBtn);
        recBtn.setLayout(recBtnLayout);
        recBtnLayout.setHorizontalGroup(
            recBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(recBtnLayout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addComponent(reclbl, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
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
        userlbl.setText("SUBMIT REQUESTS");

        javax.swing.GroupLayout usersBtnLayout = new javax.swing.GroupLayout(usersBtn);
        usersBtn.setLayout(usersBtnLayout);
        usersBtnLayout.setHorizontalGroup(
            usersBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, usersBtnLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(userlbl)
                .addGap(47, 47, 47))
        );
        usersBtnLayout.setVerticalGroup(
            usersBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, usersBtnLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(userlbl, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                .addContainerGap())
        );

        profileBtn.setBackground(new java.awt.Color(29, 111, 90));

        pfplbl.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        pfplbl.setForeground(new java.awt.Color(255, 255, 255));
        pfplbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pfplbl.setText("PROFILE");

        javax.swing.GroupLayout profileBtnLayout = new javax.swing.GroupLayout(profileBtn);
        profileBtn.setLayout(profileBtnLayout);
        profileBtnLayout.setHorizontalGroup(
            profileBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(profileBtnLayout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(pfplbl, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(42, Short.MAX_VALUE))
        );
        profileBtnLayout.setVerticalGroup(
            profileBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, profileBtnLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pfplbl, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                .addContainerGap())
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
                    .addComponent(usersBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(usersBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        dashboard.setBackground(new java.awt.Color(255, 255, 255));

        roundCorners8.setBackground(new java.awt.Color(249, 248, 255));

        roundCorners9.setBackground(new java.awt.Color(204, 238, 222));

        jLabel8.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(29, 111, 90));
        jLabel8.setText("WELCOME,");

        name.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        name.setForeground(new java.awt.Color(29, 111, 90));
        name.setText("USER");

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
                .addContainerGap(174, Short.MAX_VALUE))
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
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel18)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        roundCorners10.setBackground(new java.awt.Color(203, 252, 203));

        jLabel11.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        jLabel11.setText("TOTAL REQUESTS");

        circularLabel3.setBackground(new java.awt.Color(120, 234, 135));
        circularLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SystemImages/list.logo.png"))); // NOI18N

        jLabel31.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setText("0");

        javax.swing.GroupLayout roundCorners10Layout = new javax.swing.GroupLayout(roundCorners10);
        roundCorners10.setLayout(roundCorners10Layout);
        roundCorners10Layout.setHorizontalGroup(
            roundCorners10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners10Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(circularLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(roundCorners10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundCorners10Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel11))
                    .addGroup(roundCorners10Layout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(40, Short.MAX_VALUE))
        );
        roundCorners10Layout.setVerticalGroup(
            roundCorners10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners10Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(roundCorners10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(circularLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(roundCorners10Layout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel31)))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        roundCorners11.setBackground(new java.awt.Color(253, 242, 217));

        jLabel13.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("PENDING");

        circularLabel4.setBackground(new java.awt.Color(236, 206, 155));
        circularLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SystemImages/pending.logo.png"))); // NOI18N

        jLabel32.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel32.setText("0");

        javax.swing.GroupLayout roundCorners11Layout = new javax.swing.GroupLayout(roundCorners11);
        roundCorners11.setLayout(roundCorners11Layout);
        roundCorners11Layout.setHorizontalGroup(
            roundCorners11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners11Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(circularLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(roundCorners11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundCorners11Layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addComponent(jLabel13))
                    .addGroup(roundCorners11Layout.createSequentialGroup()
                        .addGap(58, 58, 58)
                        .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(70, Short.MAX_VALUE))
        );
        roundCorners11Layout.setVerticalGroup(
            roundCorners11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners11Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(roundCorners11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(circularLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(roundCorners11Layout.createSequentialGroup()
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel32)))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        roundCorners12.setBackground(new java.awt.Color(230, 204, 255));

        jLabel14.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("IN PROGRESS");

        circularLabel5.setBackground(new java.awt.Color(185, 160, 220));
        circularLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SystemImages/progress.icon.png"))); // NOI18N

        jLabel33.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel33.setText("0");

        javax.swing.GroupLayout roundCorners12Layout = new javax.swing.GroupLayout(roundCorners12);
        roundCorners12.setLayout(roundCorners12Layout);
        roundCorners12Layout.setHorizontalGroup(
            roundCorners12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundCorners12Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(circularLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(roundCorners12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundCorners12Layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(jLabel14))
                    .addGroup(roundCorners12Layout.createSequentialGroup()
                        .addGap(56, 56, 56)
                        .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(44, Short.MAX_VALUE))
        );
        roundCorners12Layout.setVerticalGroup(
            roundCorners12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners12Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(roundCorners12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(circularLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(roundCorners12Layout.createSequentialGroup()
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel33)))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        roundCorners13.setBackground(new java.awt.Color(216, 224, 248));

        jLabel17.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("COMPLETED");

        circularLabel6.setBackground(new java.awt.Color(141, 155, 228));
        circularLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SystemImages/completed.icon.png"))); // NOI18N

        jLabel34.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setText("0");

        javax.swing.GroupLayout roundCorners13Layout = new javax.swing.GroupLayout(roundCorners13);
        roundCorners13.setLayout(roundCorners13Layout);
        roundCorners13Layout.setHorizontalGroup(
            roundCorners13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners13Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(circularLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(roundCorners13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundCorners13Layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(jLabel17))
                    .addGroup(roundCorners13Layout.createSequentialGroup()
                        .addGap(57, 57, 57)
                        .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(37, Short.MAX_VALUE))
        );
        roundCorners13Layout.setVerticalGroup(
            roundCorners13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners13Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(roundCorners13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(circularLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(roundCorners13Layout.createSequentialGroup()
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel34)))
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

        roundCorners4.setBackground(new java.awt.Color(204, 238, 222));

        circularLabel1.setBackground(new java.awt.Color(255, 255, 255));
        circularLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SystemImages/addR.logo.png"))); // NOI18N

        jLabel22.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("SUBMIT A REQUEST");

        buttons8.setText("NEW REQUEST");
        buttons8.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N

        jLabel27.setFont(new java.awt.Font("Verdana", 2, 12)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(29, 73, 62));
        jLabel27.setText("<html>Report a maintenance concern and let our team help keep our campus safe and comfortable</html>");

        javax.swing.GroupLayout roundCorners4Layout = new javax.swing.GroupLayout(roundCorners4);
        roundCorners4.setLayout(roundCorners4Layout);
        roundCorners4Layout.setHorizontalGroup(
            roundCorners4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners4Layout.createSequentialGroup()
                .addContainerGap(42, Short.MAX_VALUE)
                .addGroup(roundCorners4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(roundCorners4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundCorners4Layout.createSequentialGroup()
                            .addComponent(circularLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(75, 75, 75))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundCorners4Layout.createSequentialGroup()
                            .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(37, 37, 37)))
                    .addComponent(buttons8, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        roundCorners4Layout.setVerticalGroup(
            roundCorners4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners4Layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addComponent(circularLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(buttons8, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(35, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout roundCorners8Layout = new javax.swing.GroupLayout(roundCorners8);
        roundCorners8.setLayout(roundCorners8Layout);
        roundCorners8Layout.setHorizontalGroup(
            roundCorners8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners8Layout.createSequentialGroup()
                .addContainerGap()
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
                            .addGroup(roundCorners8Layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 850, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(27, 27, 27)
                                .addComponent(roundCorners4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 9, Short.MAX_VALUE)))
                .addContainerGap())
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                .addGroup(roundCorners8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(roundCorners4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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

        roundCorners14.setBackground(new java.awt.Color(204, 238, 222));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(29, 111, 90));
        jLabel1.setText("MY REQUESTS");

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
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Issue/Problem", "Location", "Category", "Date Submitted", "Status", "Action"
            }
        ));
        jScrollPane2.setViewportView(tblRequests);

        buttons1.setText("ALL");
        buttons1.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N

        buttons2.setText("PENDING");
        buttons2.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N

        buttons3.setText("IN PROGRESS");
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
                .addContainerGap(44, Short.MAX_VALUE))
        );

        pages.addTab("tab2", manage_rec);

        submitReq.setBackground(new java.awt.Color(255, 255, 255));

        roundCorners3.setBackground(new java.awt.Color(216, 248, 232));

        issue.addActionListener(this::issueActionPerformed);

        imglbl.setBackground(new java.awt.Color(255, 255, 255));
        imglbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        imglbl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SystemImages/uploadIMG.png"))); // NOI18N
        imglbl.setOpaque(true);

        submit.setText("Submit Request");
        submit.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N

        photoUP.setText("Upload Photo");
        photoUP.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N

        jLabel4.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel4.setText("Issue/Problem*");

        jLabel5.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel5.setText("Location* (Building, Office, Rooms)");

        jLabel6.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel6.setText("Category*");

        jLabel19.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel19.setText("Description*");

        jLabel3.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel3.setText("Full Name*");

        jLabel10.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel10.setText("Grade/Section*");

        jLabel12.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel12.setText("Strand/Department*");

        cancelReq.setText("Cancel");
        cancelReq.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        cancelReq.addActionListener(this::cancelReqActionPerformed);

        jLabel35.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel35.setText("Attach Photo* (Optional)");

        jLabel36.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel36.setText("Priority*");

        javax.swing.GroupLayout roundCorners3Layout = new javax.swing.GroupLayout(roundCorners3);
        roundCorners3.setLayout(roundCorners3Layout);
        roundCorners3Layout.setHorizontalGroup(
            roundCorners3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners3Layout.createSequentialGroup()
                .addGap(71, 71, 71)
                .addGroup(roundCorners3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(roundCorners3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(issue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(fname, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(grSec, javax.swing.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE))
                    .addComponent(strDprt, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(roundCorners3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundCorners3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(cancelReq, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(submit, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38))
                    .addGroup(roundCorners3Layout.createSequentialGroup()
                        .addGap(72, 72, 72)
                        .addGroup(roundCorners3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(categ, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(loc, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(desc, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 73, Short.MAX_VALUE)
                        .addGroup(roundCorners3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundCorners3Layout.createSequentialGroup()
                                .addComponent(imglbl, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(58, 58, 58))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundCorners3Layout.createSequentialGroup()
                                .addComponent(photoUP, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(120, 120, 120))
                            .addGroup(roundCorners3Layout.createSequentialGroup()
                                .addGroup(roundCorners3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(roundedComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap())))))
        );
        roundCorners3Layout.setVerticalGroup(
            roundCorners3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners3Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(roundCorners3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundCorners3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(issue, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(88, 88, 88))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundCorners3Layout.createSequentialGroup()
                        .addGroup(roundCorners3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(roundCorners3Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(fname, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(roundCorners3Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(loc, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                        .addGroup(roundCorners3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(roundCorners3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(grSec, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(categ, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(48, 48, 48)
                        .addGroup(roundCorners3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(jLabel19))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(roundCorners3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(desc, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(strDprt, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(100, Short.MAX_VALUE))
                    .addGroup(roundCorners3Layout.createSequentialGroup()
                        .addComponent(jLabel35)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(imglbl, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(photoUP, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(jLabel36)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(roundedComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(roundCorners3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(submit, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cancelReq, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(30, 30, 30))))
        );

        roundCorners2.setBackground(new java.awt.Color(204, 238, 222));

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(29, 111, 90));
        jLabel20.setText("REQUEST MAINTENANCE");

        jLabel21.setFont(new java.awt.Font("Segoe UI Semibold", 2, 12)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(60, 63, 86));
        jLabel21.setText("Fill out the form below to submit a maintenance request");

        javax.swing.GroupLayout roundCorners2Layout = new javax.swing.GroupLayout(roundCorners2);
        roundCorners2.setLayout(roundCorners2Layout);
        roundCorners2Layout.setHorizontalGroup(
            roundCorners2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners2Layout.createSequentialGroup()
                .addGap(63, 63, 63)
                .addGroup(roundCorners2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 308, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        roundCorners2Layout.setVerticalGroup(
            roundCorners2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners2Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel21)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout submitReqLayout = new javax.swing.GroupLayout(submitReq);
        submitReq.setLayout(submitReqLayout);
        submitReqLayout.setHorizontalGroup(
            submitReqLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, submitReqLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(roundCorners2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(submitReqLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(roundCorners3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(48, Short.MAX_VALUE))
        );
        submitReqLayout.setVerticalGroup(
            submitReqLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(submitReqLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(roundCorners2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(roundCorners3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        pages.addTab("tab4", submitReq);

        roundCorners1.setBackground(new java.awt.Color(203, 231, 222));

        profilelbl.setBackground(new java.awt.Color(255, 255, 255));
        profilelbl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SystemImages/uploadIMG.png"))); // NOI18N

        uploadBtn.setText("Upload Photo");
        uploadBtn.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N

        ProfileName.setText("Name");

        ProfileEmail.setText("Email");

        ProfileSec.setText("Section");

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
        jLabel26.setText("Section");

        jLabel28.setFont(new java.awt.Font("Segoe UI Semibold", 1, 12)); // NOI18N
        jLabel28.setText("Grade/Year");

        jLabel29.setFont(new java.awt.Font("Segoe UI Semibold", 1, 12)); // NOI18N
        jLabel29.setText("Strand/Department");

        savecancelBtn.setText("Cancel");
        savecancelBtn.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        savecancelBtn.addActionListener(this::savecancelBtnActionPerformed);

        whiteBtn1.setText("Delete");
        whiteBtn1.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N

        javax.swing.GroupLayout roundCorners1Layout = new javax.swing.GroupLayout(roundCorners1);
        roundCorners1.setLayout(roundCorners1Layout);
        roundCorners1Layout.setHorizontalGroup(
            roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundCorners1Layout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundCorners1Layout.createSequentialGroup()
                        .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(roundCorners1Layout.createSequentialGroup()
                        .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(roundCorners1Layout.createSequentialGroup()
                                .addComponent(profilelbl, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(77, 77, 77)
                                .addComponent(uploadBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(whiteBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(roundCorners1Layout.createSequentialGroup()
                                .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(ProfileName, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ProfileEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(125, 125, 125)
                                .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(grade, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(depart, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel28, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel29, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(315, 315, Short.MAX_VALUE))
                    .addGroup(roundCorners1Layout.createSequentialGroup()
                        .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(ProfilePass, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(ProfileSec, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE))
                            .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(saveBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(editBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                            .addComponent(savecancelBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                    .addComponent(profilelbl, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(49, 49, 49)
                .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(jLabel28))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ProfileName, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(grade, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(11, 11, 11)
                .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel29))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(roundCorners1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(depart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ProfileEmail, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                        .addComponent(ProfileSec, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)))
                .addContainerGap(35, Short.MAX_VALUE))
        );

        jLabel30.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(29, 111, 90));
        jLabel30.setText("STUDENT INFORMATION");

        javax.swing.GroupLayout profileLayout = new javax.swing.GroupLayout(profile);
        profile.setLayout(profileLayout);
        profileLayout.setHorizontalGroup(
            profileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(profileLayout.createSequentialGroup()
                .addGroup(profileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(profileLayout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 343, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(profileLayout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(roundCorners1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(59, Short.MAX_VALUE))
        );
        profileLayout.setVerticalGroup(
            profileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, profileLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(roundCorners1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(51, Short.MAX_VALUE))
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
    // ---------- LOADING DATA ----------

    private void refreshAll() {
        loadRequests(null);
        showCounts();

        // The dashboard's small table has 6 columns. The 7th value ("See Details") is dropped automatically.
        try {
            requestDao.loadStudentRequests((DefaultTableModel) jTable1.getModel(), account.getUserId(), null);
        } catch (SQLException e) {
            Helper.showError(this, e);
        }
    }

    // status = null means ALL
    private void loadRequests(String status) {
        try {
            requestDao.loadStudentRequests((DefaultTableModel) tblRequests.getModel(),
                                           account.getUserId(), status);
        } catch (SQLException e) {
            Helper.showError(this, e);
        }
    }

    private void showCounts() {
        try {
            Map<String, Integer> counts = requestDao.getRequestCounts(account.getUserId());

            jLabel31.setText(String.valueOf(RequestDAO.total(counts)));          // total
            jLabel32.setText(String.valueOf(counts.getOrDefault("Pending", 0)));
            jLabel33.setText(String.valueOf(counts.getOrDefault("In Progress", 0)));
            jLabel34.setText(String.valueOf(counts.getOrDefault("Completed", 0)));
        } catch (SQLException e) {
            Helper.showError(this, e);
        }
    }

    private void loadProfile() {
        ProfileName.setText(account.getFname());
        ProfileEmail.setText(account.getEmail());
        ProfilePass.setText(account.getPass());

        try {
            String[] p = new userDao().getStudentProfile(account.getUserId());   // {department, year, section}
            if (p != null) {
                depart.setSelectedItem(p[0]);
                grade.setSelectedItem(p[1]);
                ProfileSec.setText(p[2]);

                // the read-only info on the Submit Request form
                strDprt.setSelectedItem(p[0]);
                grSec.setText(p[1] + " - " + p[2]);
            }
        } catch (SQLException e) {
            Helper.showError(this, e);
        }

        grSec.setEditable(false);
        strDprt.setEnabled(false);
    }

    // ---------- SUBMIT A REQUEST ----------

    private void choosePhoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "jpeg", "png"));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedPhotoPath = chooser.getSelectedFile().getAbsolutePath();
            imglbl.setIcon(Helper.scaledIcon(selectedPhotoPath, 240, 195));
        }
    }

    private void submitRequest() {
        String issueText = issue.getText().trim();
        String descText = desc.getText().trim();

        if (issueText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the issue/problem.");
            return;
        }
        if (descText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a description.");
            return;
        }

        try {
            requestDao.addRequest(
                    account.getUserId(),
                    issueText,
                    loc.getSelectedItem().toString(),
                    categ.getSelectedItem().toString(),
                    descText,
                    selectedPhotoPath,
                    roundedComboBox1.getSelectedItem().toString());

            JOptionPane.showMessageDialog(this, "Request submitted successfully!");
            clearForm();
            refreshAll();
            pages.setSelectedIndex(1);      // go to "My Requests"

        } catch (SQLException e) {
            Helper.showError(this, e);
        }
    }

    private void clearForm() {
        issue.setText("");
        desc.setText("");
        selectedPhotoPath = null;
        imglbl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SystemImages/uploadIMG.png")));
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
        originalSection = ProfileSec.getText();

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
            new userDao().saveStudentProfile(account.getUserId(), newName, newEmail, newPass,
                    depart.getSelectedItem().toString(),
                    grade.getSelectedItem().toString(),
                    ProfileSec.getText().trim());

            setProfileEditing(false);
            name.setText(newName);
            JOptionPane.showMessageDialog(this, "Changes saved successfully!");

        } catch (SQLException e) {
            Helper.showError(this, e);
        }
    
    }//GEN-LAST:event_saveBtnActionPerformed

    private void issueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_issueActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_issueActionPerformed

    private void savecancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_savecancelBtnActionPerformed
        // TODO add your handling code here:
         // Restore the original values
        ProfileName.setText(originalName);
        ProfileEmail.setText(originalEmail);
        ProfilePass.setText(originalPassword);
        ProfileSec.setText(originalSection);

        // Return to read-only mode
        setProfileEditing(false);
    }//GEN-LAST:event_savecancelBtnActionPerformed

    private void cancelReqActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelReqActionPerformed
        // TODO add your handling code here:
        
        clearForm();
    }//GEN-LAST:event_cancelReqActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new StudentDashboard().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Maintenance_Request_System.RoundedSearchField ProfileEmail;
    private Maintenance_Request_System.RoundedSearchField ProfileName;
    private Maintenance_Request_System.RoundedPasswordField ProfilePass;
    private Maintenance_Request_System.RoundedSearchField ProfileSec;
    private Designs.Buttons buttons1;
    private Designs.Buttons buttons2;
    private Designs.Buttons buttons3;
    private Designs.Buttons buttons4;
    private Designs.Buttons buttons8;
    private Designs.WhiteBtn cancelReq;
    private Maintenance_Request_System.RoundedComboBox categ;
    private Maintenance_Request_System.CircularLabel circularLabel1;
    private Maintenance_Request_System.CircularLabel circularLabel3;
    private Maintenance_Request_System.CircularLabel circularLabel4;
    private Maintenance_Request_System.CircularLabel circularLabel5;
    private Maintenance_Request_System.CircularLabel circularLabel6;
    private javax.swing.JPanel dashboard;
    private Designs.RoundCorners dashboardBtn;
    private javax.swing.JLabel dashboardlbl;
    private Maintenance_Request_System.RoundedComboBox depart;
    private Maintenance_Request_System.RoundedTextArea desc;
    private Designs.Buttons editBtn;
    private Maintenance_Request_System.RoundedSearchField fname;
    private Maintenance_Request_System.RoundedSearchField grSec;
    private Maintenance_Request_System.RoundedComboBox grade;
    private javax.swing.JPanel header;
    private javax.swing.JLabel imglbl;
    private Maintenance_Request_System.RoundedSearchField issue;
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
    private javax.swing.JLabel jLabel20;
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
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private Maintenance_Request_System.RoundedComboBox loc;
    private Designs.RoundCorners logout;
    private javax.swing.JLabel logoutlbl;
    private javax.swing.JPanel manage_rec;
    private Designs.RoundCorners menu;
    private javax.swing.JLabel name;
    private javax.swing.JTabbedPane pages;
    private javax.swing.JLabel pfplbl;
    private Designs.Buttons photoUP;
    private javax.swing.JPanel profile;
    private Designs.RoundCorners profileBtn;
    private Maintenance_Request_System.CircularLabel profilelbl;
    private Designs.RoundCorners recBtn;
    private Designs.Buttons recSearch;
    private Maintenance_Request_System.RoundedSearchField recTxt;
    private javax.swing.JLabel reclbl;
    private Designs.RoundCorners roundCorners1;
    private Designs.RoundCorners roundCorners10;
    private Designs.RoundCorners roundCorners11;
    private Designs.RoundCorners roundCorners12;
    private Designs.RoundCorners roundCorners13;
    private Designs.RoundCorners roundCorners14;
    private Designs.RoundCorners roundCorners2;
    private Designs.RoundCorners roundCorners3;
    private Designs.RoundCorners roundCorners4;
    private Designs.RoundCorners roundCorners8;
    private Designs.RoundCorners roundCorners9;
    private Maintenance_Request_System.RoundedComboBox roundedComboBox1;
    private Designs.Buttons saveBtn;
    private Designs.WhiteBtn savecancelBtn;
    private Maintenance_Request_System.RoundedComboBox strDprt;
    private Designs.Buttons submit;
    private javax.swing.JPanel submitReq;
    private javax.swing.JTable tblRequests;
    private Designs.Buttons uploadBtn;
    private javax.swing.JLabel userlbl;
    private Designs.RoundCorners usersBtn;
    private Designs.WhiteBtn whiteBtn1;
    // End of variables declaration//GEN-END:variables
}
