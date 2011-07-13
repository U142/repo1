/*
 * Created by JFormDesigner on Tue Jun 07 11:12:41 CEST 2011
 */

package no.ums.pas.entrypoint;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.border.*;
import org.jdesktop.beansbinding.*;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;

/**
 * @author User #1
 */
public class MailSendErrorDialog extends JDialog {

    interface Controller {
        void onOk();
        void onCancel();
        void onCopy();
    }

    public MailSendErrorDialog(Frame owner) {
        super(owner);
        initComponents();
    }

    public MailSendErrorDialog(Dialog owner) {
        super(owner);
        initComponents();
    }

    public MailSendErrorModel getModel() {
        return model;
    }

    private void cancelButtonActionPerformed() {
        dispose();
    }

    private void btnCopyActionPerformed() {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(txtMessage.getText()), null);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        ResourceBundle bundle = ResourceBundle.getBundle("no.ums.pas.localization.lang");
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        txtErrorInfo = new JTextPane();
        scrollPane1 = new JScrollPane();
        txtMessage = new JTextArea();
        buttonBar = new JPanel();
        btnCopy = new JButton();
        cancelButton = new JButton();
        model = new MailSendErrorModel();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {

                //---- txtErrorInfo ----
                txtErrorInfo.setText(bundle.getString("MailSendErrorDialog.txtErrorInfo.text"));
                txtErrorInfo.setEditable(false);

                //======== scrollPane1 ========
                {

                    //---- txtMessage ----
                    txtMessage.setEditable(false);
                    scrollPane1.setViewportView(txtMessage);
                }

                GroupLayout contentPanelLayout = new GroupLayout(contentPanel);
                contentPanel.setLayout(contentPanelLayout);
                contentPanelLayout.setHorizontalGroup(
                    contentPanelLayout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(txtErrorInfo, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 422, Short.MAX_VALUE)
                                .addComponent(scrollPane1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 422, Short.MAX_VALUE))
                            .addContainerGap())
                );
                contentPanelLayout.setVerticalGroup(
                    contentPanelLayout.createParallelGroup()
                        .addGroup(contentPanelLayout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(txtErrorInfo, GroupLayout.PREFERRED_SIZE, 51, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE)
                            .addContainerGap())
                );
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 85, 80};
                ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0};

                //---- btnCopy ----
                btnCopy.setText(bundle.getString("MailSendErrorDialog.btnCopy.text"));
                btnCopy.setMaximumSize(new Dimension(44, 27));
                btnCopy.setMinimumSize(new Dimension(44, 27));
                btnCopy.setPreferredSize(new Dimension(44, 27));
                btnCopy.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnCopyActionPerformed();
                    }
                });
                buttonBar.add(btnCopy, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setText(bundle.getString("common_cancel"));
                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cancelButtonActionPerformed();
                    }
                });
                buttonBar.add(cancelButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());

        //---- bindings ----
        bindingGroup = new BindingGroup();
        bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
            model, BeanProperty.create("text"),
            txtMessage, BeanProperty.create("text")));
        bindingGroup.bind();
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JTextPane txtErrorInfo;
    private JScrollPane scrollPane1;
    private JTextArea txtMessage;
    private JPanel buttonBar;
    private JButton btnCopy;
    private JButton cancelButton;
    private MailSendErrorModel model;
    private BindingGroup bindingGroup;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
