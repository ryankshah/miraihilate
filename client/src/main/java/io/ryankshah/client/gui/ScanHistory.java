package io.ryankshah.client.gui;

import io.ryankshah.Client;
import io.ryankshah.util.database.DBHelper;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class ScanHistory extends JFrame
{
    private ScanEntry entries[];
    private JList entryList;
    private JTextArea scanResult;

    public ScanHistory() {
        setTitle("Scan History");
        setSize(new Dimension(600, 400));

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(1,2));

        // TODO: Add list on left side
        JPanel resultListPanel = new JPanel();
        TitledBorder resultListBorder = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Recent Scan");
        resultListPanel.setBorder(resultListBorder);
        resultListPanel.setLayout(new GridLayout(1, 2));

        ArrayList<ScanEntry> seList = getResultIDList();
        entries = new ScanEntry[seList.size()];
        int i = 0;
        for(ScanEntry e : seList) {
            entries[i++] = e;
        }
        System.out.println(Arrays.toString(entries));

        entryList = new JList(entries);
        entryList.setCellRenderer(new ScanEntryRenderer());
        entryList.setVisibleRowCount(4);
        JScrollPane pane = new JScrollPane(entryList);
        resultListPanel.add(pane);
        add(resultListPanel);

        // Add scan result view area
        JPanel resultViewPanel = new JPanel();
        TitledBorder resultViewBorder = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Result View");
        resultViewPanel.setBorder(resultViewBorder);
        resultViewPanel.setLayout(new GridLayout(1, 2));
        scanResult = new JTextArea();
        scanResult.setEditable(false);
        //updateRecentScan();
        JScrollPane scroll = new JScrollPane(
                scanResult,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        resultViewPanel.add(scroll);
        add(resultViewPanel);

        setLocationRelativeTo(null);
        setResizable(false);
    }

    public ArrayList<ScanEntry> getResultIDList() {
        Connection con = DBHelper.getDatabaseConnection();
        PreparedStatement stmt = null;
        ArrayList<ScanEntry> resultList = new ArrayList<>();
        try {
            String query = "SELECT id, start_timestamp FROM scan_logs WHERE user_uuid = ?";
            stmt = con.prepareStatement(query);
            stmt.setString(1, Client.user.getUUID().toString());
            ResultSet results = stmt.executeQuery();

            // While a result exists
            while(results.next()) {
                int scanID = results.getInt("id");
                String startTimestamp = results.getString("start_timestamp");
                resultList.add(new ScanEntry(scanID, startTimestamp));
            }

            // Closing up the connection
            if(stmt != null)
                stmt.close();
            if(con != null)
                con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultList;
    }

    public void retrieveScanResult() {

    }
}