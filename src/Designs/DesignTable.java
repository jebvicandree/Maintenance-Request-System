/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Designs;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;

public class DesignTable extends JTable {
    
public DesignTable() {
    // Table background
    setBackground(Color.WHITE);
    setForeground(new Color(45, 55, 72));

    // Font
    setFont(new Font("Segoe UI", Font.PLAIN, 13));

    // Row height
    setRowHeight(40);

    // Grid lines
    setShowGrid(true);
    setGridColor(new Color(230, 235, 240));

    // Selected row
    setSelectionBackground(new Color(220, 240, 231));
    setSelectionForeground(new Color(255,255,255));

    // Selection behavior
    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    // Fill available viewport space
    setFillsViewportHeight(true);

    // Header design
    JTableHeader header = getTableHeader();

    header.setBackground(new Color(29,111,90));
    header.setForeground(new Color(29,111,90));
    header.setFont(new Font("Segoe UI", Font.BOLD, 14));
    header.setPreferredSize(new java.awt.Dimension(0, 40));

    // Prevent columns from being rearranged
    header.setReorderingAllowed(false);
}


}
