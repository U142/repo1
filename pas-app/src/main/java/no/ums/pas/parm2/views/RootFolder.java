/*
 * Created by JFormDesigner on Mon Feb 07 14:48:28 CET 2011
 */

package no.ums.pas.parm2.views;

import no.ums.pas.parm2.ParmUiService;
import no.ums.ws.parm.PaObject;

import java.awt.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.border.*;

/**
 * @author StÃ¥le Undheim
 */
public class RootFolder extends JPanel {

    private final ParmUiService parmUiService;

    public RootFolder(final ParmUiService parmUiService) {
        this.parmUiService = parmUiService;
        initComponents();
        objectList.setCellRenderer(new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
                if (value instanceof PaObject) {
                    ObjectFolderLine objectFolderLine = new ObjectFolderLine((PaObject) value, parmUiService);
                    if (isSelected) {
                        objectFolderLine.setBackground(objectList.getSelectionBackground());
                        objectFolderLine.setForeground(objectList.getSelectionForeground());
                    }
                    return objectFolderLine;
                }
                return new JLabel(String.valueOf(value));  //To change body of implemented methods use File | Settings | File Templates.
            }
        });
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - StÃ¥le Undheim
        JPanel panel6 = new JPanel();
        JTextField textField15 = new JTextField();
        JButton button4 = new JButton();
        JScrollPane scrollPane3 = new JScrollPane();
        objectList = new JList(parmUiService.getRootFolders());
        button1 = new JButton();

        //======== this ========

        // JFormDesigner evaluation mark
        setBorder(new javax.swing.border.CompoundBorder(
            new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
                "JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
                java.awt.Color.red), getBorder())); addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});


        //======== panel6 ========
        {
            panel6.setBorder(new TitledBorder("Root items"));

            //---- button4 ----
            button4.setText("Add Object");

            //======== scrollPane3 ========
            {
                scrollPane3.setViewportView(objectList);
            }

            //---- button1 ----
            button1.setText("Create Folder");

            GroupLayout panel6Layout = new GroupLayout(panel6);
            panel6.setLayout(panel6Layout);
            panel6Layout.setHorizontalGroup(
                panel6Layout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, panel6Layout.createSequentialGroup()
                        .addComponent(textField15, GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button1)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button4))
                    .addComponent(scrollPane3, GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
            );
            panel6Layout.setVerticalGroup(
                panel6Layout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, panel6Layout.createSequentialGroup()
                        .addComponent(scrollPane3, GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel6Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(textField15, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(button4)
                            .addComponent(button1)))
            );
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createParallelGroup()
                    .addComponent(panel6, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createParallelGroup()
                    .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(panel6, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(0, 300, Short.MAX_VALUE)
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - StÃ¥le Undheim
    private JList objectList;
    private JButton button1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
