/*
 * Created by JFormDesigner on Mon Feb 07 09:52:21 CET 2011
 */

package no.ums.pas.parm2.views;

import java.awt.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.border.*;

/**
 * @author StÃ¥le Undheim
 */
public class FolderPanel {
    public FolderPanel() {
        initComponents();

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - StÃ¥le Undheim
        JPanel panel1 = new JPanel();
        JPanel objectInfo = new JPanel();
        JLabel label9 = new JLabel();
        JLabel label10 = new JLabel();
        JTextField textField9 = new JTextField();
        JLabel label11 = new JLabel();
        JTextField textField10 = new JTextField();
        JLabel label12 = new JLabel();
        JTextField textField11 = new JTextField();
        JLabel label13 = new JLabel();
        JTextField textField12 = new JTextField();
        JComboBox comboBox1 = new JComboBox();
        JTextArea description = new JTextArea();
        JScrollPane scrollPane1 = new JScrollPane();
        textArea2 = new JTextArea();
        JButton button1 = new JButton();
        JToggleButton toggleButton1 = new JToggleButton();
        JTextField name = new JTextField();
        JLabel categoryIcon = new JLabel();
        JPanel panel6 = new JPanel();
        JTextField textField15 = new JTextField();
        JButton button4 = new JButton();
        JScrollPane scrollPane3 = new JScrollPane();
        objectList = new JList();

        //======== panel1 ========
        {

            // JFormDesigner evaluation mark
            panel1.setBorder(new javax.swing.border.CompoundBorder(
                new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
                    "JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
                    javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
                    java.awt.Color.red), panel1.getBorder())); panel1.addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});


            //======== objectInfo ========
            {
                objectInfo.setBorder(new TitledBorder("Object Information Input"));

                //---- label9 ----
                label9.setText("Category: ");

                //---- label10 ----
                label10.setText("Address:");

                //---- label11 ----
                label11.setText("Zip code: ");

                //---- label12 ----
                label12.setText("Place: ");

                //---- label13 ----
                label13.setText("Phone: ");

                GroupLayout objectInfoLayout = new GroupLayout(objectInfo);
                objectInfo.setLayout(objectInfoLayout);
                objectInfoLayout.setHorizontalGroup(
                    objectInfoLayout.createParallelGroup()
                        .addGroup(objectInfoLayout.createSequentialGroup()
                            .addGroup(objectInfoLayout.createParallelGroup()
                                .addComponent(label10, GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
                                .addGroup(objectInfoLayout.createSequentialGroup()
                                    .addGroup(objectInfoLayout.createParallelGroup()
                                        .addComponent(label9, GroupLayout.PREFERRED_SIZE, 134, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label11, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label12, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(label13))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(objectInfoLayout.createParallelGroup()
                                        .addComponent(textField10, GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
                                        .addComponent(textField11, GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
                                        .addComponent(textField12, GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
                                        .addComponent(textField9, GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
                                        .addComponent(comboBox1, 0, 289, Short.MAX_VALUE))))
                            .addContainerGap())
                );
                objectInfoLayout.setVerticalGroup(
                    objectInfoLayout.createParallelGroup()
                        .addGroup(objectInfoLayout.createSequentialGroup()
                            .addGroup(objectInfoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(label9, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
                                .addComponent(comboBox1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(objectInfoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(label10)
                                .addComponent(textField9, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(objectInfoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(label11, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
                                .addComponent(textField12, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(objectInfoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(label12, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
                                .addComponent(textField11, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(objectInfoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(label13)
                                .addComponent(textField10, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)))
                );
            }

            //---- description ----
            description.setText("After given an object necessary information, please mark the position on the map.");
            description.setLineWrap(true);
            description.setBackground(new Color(214, 217, 223));
            description.setWrapStyleWord(true);
            description.setEditable(false);
            description.setBorder(new TitledBorder("Set polygon position"));

            //======== scrollPane1 ========
            {
                scrollPane1.setBorder(new TitledBorder("Additional description"));

                //---- textArea2 ----
                textArea2.setBackground(Color.white);
                scrollPane1.setViewportView(textArea2);
            }

            //---- button1 ----
            button1.setText("Save Changes");

            //---- toggleButton1 ----
            toggleButton1.setText("Edit area");

            //======== panel6 ========
            {
                panel6.setBorder(new TitledBorder("Content"));

                //---- button4 ----
                button4.setText("Add Object");

                //======== scrollPane3 ========
                {
                    scrollPane3.setViewportView(objectList);
                }

                GroupLayout panel6Layout = new GroupLayout(panel6);
                panel6.setLayout(panel6Layout);
                panel6Layout.setHorizontalGroup(
                    panel6Layout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, panel6Layout.createSequentialGroup()
                            .addComponent(textField15, GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(button4))
                        .addComponent(scrollPane3, GroupLayout.DEFAULT_SIZE, 447, Short.MAX_VALUE)
                );
                panel6Layout.setVerticalGroup(
                    panel6Layout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, panel6Layout.createSequentialGroup()
                            .addComponent(scrollPane3, GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(panel6Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(textField15, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(button4)))
                );
            }

            GroupLayout panel1Layout = new GroupLayout(panel1);
            panel1.setLayout(panel1Layout);
            panel1Layout.setHorizontalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addComponent(objectInfo, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(GroupLayout.Alignment.LEADING, panel1Layout.createSequentialGroup()
                                .addComponent(categoryIcon, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(name, GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE))
                            .addComponent(description, GroupLayout.Alignment.LEADING)
                            .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 463, Short.MAX_VALUE)
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(toggleButton1)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 261, Short.MAX_VALUE)
                                .addComponent(button1)))
                        .addContainerGap())
                    .addComponent(panel6, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            );
            panel1Layout.setVerticalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(name, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(categoryIcon, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(objectInfo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(description, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(button1)
                            .addComponent(toggleButton1))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panel6, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - StÃ¥le Undheim
    private JTextArea textArea2;
    private JList objectList;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
