/*
 * Created by JFormDesigner on Wed Sep 07 13:24:36 CEST 2011
 */

package no.ums.pas.send.sendpanels;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

/**
 * @author User #3
 */
public class SendingResultsView extends JDialog {
	
	public interface ISendingResultsUpdate {
		public void onYes();
		public void onNo();
	}
	
	ISendingResultsUpdate callback;
	
	public SendingResultsView(Frame owner, ISendingResultsUpdate callback) {
		super(owner);
		this.callback = callback;
		initComponents();

	}
	
	public ISendingResultsUpdate getCallback() {
		return callback;
	}

	public JScrollPane getScrollPane2() {
		return scrollPane2;
	}

	public JLabel getLblTest() {
		return lblTest;
	}

	public SendingResultsView(Frame owner) {
		super(owner);
		initComponents();
	}

	public SendingResultsView(Dialog owner) {
		super(owner);
		initComponents();
	}

	public JButton getBtnYes() {
		return btnYes;
	}

	public JButton getBtnNo() {
		return btnNo;
	}

	public JLabel getLblOpenStatus() {
		return lblOpenStatus;
	}

	private void btnYesActionPerformed(ActionEvent e) {
		// TODO add your code here
		callback.onYes();
	}

	private void btnNoActionPerformed(ActionEvent e) {
		// TODO add your code here
		callback.onNo();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		ResourceBundle bundle = ResourceBundle.getBundle("no.ums.pas.localization.lang");
		btnYes = new JButton();
		btnNo = new JButton();
		lblOpenStatus = new JLabel();
		scrollPane2 = new JScrollPane();
		lblTest = new JLabel();

		//======== this ========
		setTitle(bundle.getString("quicksend_dlg_results"));
		Container contentPane = getContentPane();

		//---- btnYes ----
		btnYes.setText(bundle.getString("common_yes"));
		btnYes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnYesActionPerformed(e);
			}
		});

		//---- btnNo ----
		btnNo.setText(bundle.getString("common_no"));
		btnNo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnNoActionPerformed(e);
			}
		});

		//---- lblOpenStatus ----
		lblOpenStatus.setText(bundle.getString("quicksend_dlg_open_status"));

		//======== scrollPane2 ========
		{
			scrollPane2.setViewportView(lblTest);
		}

		GroupLayout contentPaneLayout = new GroupLayout(contentPane);
		contentPane.setLayout(contentPaneLayout);
		contentPaneLayout.setHorizontalGroup(
			contentPaneLayout.createParallelGroup()
				.addGroup(contentPaneLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(contentPaneLayout.createParallelGroup()
						.addGroup(contentPaneLayout.createSequentialGroup()
							.addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE)
							.addContainerGap())
						.addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
							.addComponent(lblOpenStatus)
							.addGap(129, 129, 129))))
				.addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
					.addGap(107, 107, 107)
					.addComponent(btnYes, GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
					.addGap(35, 35, 35)
					.addComponent(btnNo, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
					.addGap(97, 97, 97))
		);
		contentPaneLayout.setVerticalGroup(
			contentPaneLayout.createParallelGroup()
				.addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
					.addGap(18, 18, 18)
					.addComponent(lblOpenStatus)
					.addGap(18, 18, 18)
					.addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(btnYes)
						.addComponent(btnNo))
					.addGap(56, 56, 56))
		);
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JButton btnYes;
	private JButton btnNo;
	private JLabel lblOpenStatus;
	private JScrollPane scrollPane2;
	private JLabel lblTest;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
