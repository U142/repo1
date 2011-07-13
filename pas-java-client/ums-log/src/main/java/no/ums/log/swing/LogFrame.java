/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * LogFrame2.java
 *
 * Created on Jan 27, 2011, 1:39:22 PM
 */
package no.ums.log.swing;

import no.ums.log.Log;
import no.ums.log.UmsLog;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.LayoutStyle;
import javax.swing.ListCellRenderer;
import javax.swing.TransferHandler;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 *
 * @author staaleu
 */
public class LogFrame extends javax.swing.JFrame {

    private static final Log log = UmsLog.getLogger(LogFrame.class);
    private static final ListDataListener DATA_LISTENER = new ListDataListener() {

        @Override
        public void intervalAdded(ListDataEvent e) {
            contentsChanged(e);
        }

        @Override
        public void intervalRemoved(ListDataEvent e) {
            // No need to do anything when items are removed.
        }

        @Override
        public void contentsChanged(ListDataEvent e) {
            for (int i=e.getIndex0(); i<=e.getIndex1(); i++) {
                // Only show the frame if hidden and we get a severe message.
                if (!Holder.INSTANCE.isVisible() && LogRecordCollector.MODEL.is(Level.SEVERE, i)) {
                    EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            Holder.INSTANCE.setVisible(true);
                        }
                    });
                }
            }
        }

    };

    private boolean scrollEnabled = true;

    public void setSendMailEnabled(boolean enabled) {
        btnSendMail.setEnabled(enabled);
    }

    /** Creates new form LogFrame2 */
    private void windowClosed() {
        //LogRecordCollector.MODEL.clear();
    }

    private void btnSendMailActionPerformed() {
        if(LogRecordCollector.sendMail())
        {
        	LogRecordCollector.MODEL.clear(); //clear all records sent
        }
    }

    private void btnClearActionPerformed() {
        LogRecordCollector.MODEL.clear();
    }

    public LogFrame() {
        initComponents();
        jComboBox1.setRenderer(new ListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Level level = (Level) value;
                JLabel label = new JLabel(level.getLocalizedName());
                label.setForeground(LogSwingUtil.toColor(level));
                return label;
            }
        });
        jComboBox1.setSelectedItem(LogRecordCollector.MODEL.getLevel());
        jComboBox1.setModel(LogSwingUtil.LEVEL_MODEL);
        jComboBox1.setSelectedItem(Level.SEVERE);

        jList1.setCellRenderer(LogSwingUtil.LOG_RECORD_RENDERER);
        jList1.setModel(LogRecordCollector.MODEL);
        jScrollPane1.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {

            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                scrollEnabled = (e.getValue() + jScrollPane1.getHeight() - e.getAdjustable().getMaximum()) > -15;
            }
        });
        jList1.setTransferHandler(new TransferHandler() {

            @Override
            public void exportToClipboard(final JComponent comp, final Clipboard clip, final int action) throws IllegalStateException {
                if ((action == COPY || action == MOVE)) {

                    Transferable t = createTransferable(comp);
                    if (t != null) {
                        try {
                            clip.setContents(t, null);
                            exportDone(comp, t, action);
                            return;
                        } catch (IllegalStateException ise) {
                            exportDone(comp, t, NONE);
                            throw ise;
                        }
                    }
                }

                exportDone(comp, null, NONE);
            }

            @Override
            protected Transferable createTransferable(final JComponent c) {
                final StringWriter sw = new StringWriter();
                writeSelected(new PrintWriter(sw));
                return new StringSelection(sw.toString());
            }
        });
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jSplitPane1 = new JSplitPane();
        jPanel2 = new JPanel();
        jScrollPane1 = new JScrollPane();
        jList1 = new JList();
        btnClose = new JButton();
        btnSave = new JButton();
        filterLabel = new JLabel();
        jComboBox1 = new JComboBox();
        btnSendMail = new JButton();
        btnClear = new JButton();
        logRecordDetail1 = new LogRecordDetail();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                LogFrame.this.windowClosed();
            }
        });
        Container contentPane = getContentPane();

        //======== jSplitPane1 ========
        {
            jSplitPane1.setBorder(new EtchedBorder());
            jSplitPane1.setDividerLocation(500);
            jSplitPane1.setResizeWeight(1.0);

            //======== jPanel2 ========
            {

                //======== jScrollPane1 ========
                {

                    //---- jList1 ----
                    jList1.addListSelectionListener(new ListSelectionListener() {
                        @Override
                        public void valueChanged(ListSelectionEvent e) {
                            jList1ValueChanged(e);
                        }
                    });
                    jList1.addComponentListener(new ComponentAdapter() {
                        @Override
                        public void componentResized(ComponentEvent e) {
                            jList1ComponentResized(e);
                        }
                    });
                    jScrollPane1.setViewportView(jList1);
                }

                //---- btnClose ----
                btnClose.setText("Close");
                btnClose.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        closeButtonActionPerformed(e);
                    }
                });

                //---- btnSave ----
                btnSave.setText("Save");
                btnSave.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        saveButtonActionPerformed(e);
                    }
                });

                //---- filterLabel ----
                filterLabel.setText("Filter:");

                //---- jComboBox1 ----
                jComboBox1.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        jComboBox1ActionPerformed(e);
                    }
                });

                //---- btnSendMail ----
                btnSendMail.setText("Send Mail");
                btnSendMail.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnSendMailActionPerformed();
                    }
                });

                //---- btnClear ----
                btnClear.setText("Clear");
                btnClear.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnClearActionPerformed();
                    }
                });

                GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
                jPanel2.setLayout(jPanel2Layout);
                jPanel2Layout.setHorizontalGroup(
                    jPanel2Layout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(filterLabel)
                            .addGap(18, 18, 18)
                            .addComponent(jComboBox1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 60, Short.MAX_VALUE)
                            .addComponent(btnClear)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(btnSendMail)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(btnSave)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(btnClose)
                            .addContainerGap())
                        .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 498, Short.MAX_VALUE)
                );
                jPanel2Layout.setVerticalGroup(
                    jPanel2Layout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                            .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 415, Short.MAX_VALUE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(btnClose)
                                .addComponent(btnSave)
                                .addComponent(filterLabel)
                                .addComponent(jComboBox1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnSendMail)
                                .addComponent(btnClear))
                            .addContainerGap())
                );
            }
            jSplitPane1.setLeftComponent(jPanel2);
            jSplitPane1.setRightComponent(logRecordDetail1);
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addComponent(jSplitPane1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 803, Short.MAX_VALUE)
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addComponent(jSplitPane1, GroupLayout.DEFAULT_SIZE, 464, Short.MAX_VALUE)
        );
        pack();
        setLocationRelativeTo(getOwner());
    }// </editor-fold>//GEN-END:initComponents

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("UmsLog-"+LogSwingUtil.formatDateTime(System.currentTimeMillis())+".txt"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                final PrintWriter writer = new PrintWriter(fileChooser.getSelectedFile());
                writeSelected(writer);
                writer.close();
            } catch (FileNotFoundException e) {
                log.error("Failed to save to file %s", fileChooser.getSelectedFile(), e);
            }
        }
    }//GEN-LAST:event_saveButtonActionPerformed

    private void writeSelected(final PrintWriter writer) {
        //noinspection deprecation
        for (final Object o : jList1.getSelectedValues()) {
            final LogRecord logRecord = (LogRecord) o;
            writer.printf("%tF %tT [%-7s] (%s) %s\n", logRecord.getMillis(), logRecord.getMillis(), logRecord.getLevel().getName(), logRecord.getLoggerName(), logRecord.getMessage());
            //noinspection ThrowableResultOfMethodCallIgnored
            if (logRecord.getThrown() != null) {
                //noinspection ThrowableResultOfMethodCallIgnored
                logRecord.getThrown().printStackTrace(writer);
            }
        }
    }

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        dispose();
    }//GEN-LAST:event_closeButtonActionPerformed

    private void jList1ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList1ValueChanged
        logRecordDetail1.setLogRecord((LogRecord) jList1.getSelectedValue());
    }//GEN-LAST:event_jList1ValueChanged

    private void jList1ComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jList1ComponentResized
        if (scrollEnabled) {
            jScrollPane1.getVerticalScrollBar().setValue(jScrollPane1.getVerticalScrollBar().getMaximum());
        }
    }//GEN-LAST:event_jList1ComponentResized

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        LogRecordCollector.MODEL.setLevel((Level) jComboBox1.getSelectedItem());
    }//GEN-LAST:event_jComboBox1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        LogRecordCollector.install(null, true);
        UmsLog.getLogger(LogFrame.class).debug("A Test message", new Exception("Test exception"));
        UmsLog.getLogger(LogFrame.class).debug("A Test message", new Exception("Test exception"));
        UmsLog.getLogger(LogFrame.class).debug("A Test message", new Exception("Test exception"));
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                final LogFrame logFrame2 = new LogFrame();
                logFrame2.setVisible(true);
            }
        });
    }

    /**
     * Static lazy initialization off a single LogFrame.
     *
     * @see <a href="http://blog.crazybob.org/2007/01/lazy-loading-singletons.html">blog.crazybob.org/2007/01/lazy-loading-singletons.html</a>
     * @author staaleu
     */
    static class Holder {
        static LogFrame INSTANCE = new LogFrame();
    }

    public static void install() {
        LogRecordCollector.MODEL.addListDataListener(DATA_LISTENER);
    }

    public static void remove() {
        LogRecordCollector.MODEL.removeListDataListener(DATA_LISTENER);
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Holder.INSTANCE.setVisible(false);
            }
        });
    }

    public static LogFrame getInstance() {
        return Holder.INSTANCE;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JSplitPane jSplitPane1;
    private JPanel jPanel2;
    private JScrollPane jScrollPane1;
    private JList jList1;
    private JButton btnClose;
    private JButton btnSave;
    private JLabel filterLabel;
    private JComboBox jComboBox1;
    private JButton btnSendMail;
    private JButton btnClear;
    private LogRecordDetail logRecordDetail1;
    // End of variables declaration//GEN-END:variables
}
