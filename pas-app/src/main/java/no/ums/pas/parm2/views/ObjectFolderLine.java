/*
 * Created by JFormDesigner on Mon Feb 07 11:53:29 CET 2011
 */

package no.ums.pas.parm2.views;

import java.awt.event.*;
import javax.swing.plaf.*;

import no.ums.pas.parm2.ParmUiService;
import no.ums.ws.parm.PaObject;

import javax.swing.*;
import java.awt.*;

/**
 * @author StÃ¥le Undheim
 */
public class ObjectFolderLine extends JPanel {

    private final PaObject paObject;
    private final ParmUiService parmUiService;

    public ObjectFolderLine(PaObject paObject, final ParmUiService parmUiService) {
        this.paObject = paObject;
        this.parmUiService = parmUiService;
        initComponents();
    }

    private void deleteButtonActionPerformed(ActionEvent e) {
        parmUiService.deletePaObject(paObject);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - StÃ¥le Undheim
        categoryIcon = new JLabel(ResourceHelper.getIcon(paObject.getCategorypk(), 32, 32));
        nameLabel = new JLabel(paObject.getName());
        button1 = new JButton();
        button2 = new JButton();

        //======== this ========
        setBackground(Color.white);

        // JFormDesigner evaluation mark
        setBorder(new javax.swing.border.CompoundBorder(
            new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
                "JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
                java.awt.Color.red), getBorder())); addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});


        //---- categoryIcon ----
        categoryIcon.setBackground(Color.white);

        //---- nameLabel ----
        nameLabel.setVerticalAlignment(SwingConstants.BOTTOM);
        nameLabel.setBackground(Color.white);

        //---- button1 ----
        button1.setText("View");

        //---- button2 ----
        button2.setText("Delete");

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(categoryIcon, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(nameLabel, GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(button1)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(button2)
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(nameLabel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                        .addGroup(GroupLayout.Alignment.LEADING, layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addComponent(categoryIcon, GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(button2)
                                .addComponent(button1, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE))))
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - StÃ¥le Undheim
    private JLabel categoryIcon;
    private JLabel nameLabel;
    private JButton button1;
    private JButton button2;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
