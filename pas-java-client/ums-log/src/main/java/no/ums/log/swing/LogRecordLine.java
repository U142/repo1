/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * LogMessage.java
 *
 * Created on Jan 27, 2011, 10:47:02 AM
 */

package no.ums.log.swing;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Primitives;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.NavigableMap;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * @author staaleu
 */
public class LogRecordLine extends javax.swing.JPanel {



    /**
     * Creates new form LogMessage
     */
    public LogRecordLine(LogRecord logRecord, boolean selected) {
        initComponents();
        level.setForeground(LogSwingUtil.toColor(logRecord.getLevel()));
		ResourceBundle bundle = ResourceBundle.getBundle("no.ums.log.localization.lang");
        level.setText(bundle.getString("LogLevel."+logRecord.getLevel()));
        time.setText(LogSwingUtil.formatTime(logRecord.getMillis()));
        msg.setText(logRecord.getMessage());
        if (selected) {
            setBackground(UIManager.getColor("List.selectionBackground"));
        }
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        level = new JLabel();
        msg = new JLabel();
        time = new JLabel();

        //======== this ========

        //---- level ----
        level.setFont(new Font("DejaVu Sans", Font.BOLD, 13));
        level.setForeground(new Color(2, 167, 34));
        level.setText("DEBUG");

        //---- msg ----
        msg.setText("This is the message");

        //---- time ----
        time.setText("10:48:35");

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addComponent(time)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(level, GroupLayout.PREFERRED_SIZE, 76, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(msg, GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(time)
                    .addComponent(level)
                    .addComponent(msg))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JLabel level;
    private JLabel msg;
    private JLabel time;
    // End of variables declaration//GEN-END:variables

}
