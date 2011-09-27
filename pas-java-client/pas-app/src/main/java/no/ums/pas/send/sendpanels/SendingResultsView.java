/*
 * Created by JFormDesigner on Wed Sep 07 13:24:36 CEST 2011
 */

package no.ums.pas.send.sendpanels;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import no.ums.pas.core.dataexchange.soap.SoapExecAlert.SnapAlertResults;

import org.jdesktop.beansbinding.*;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;

/**
 * @author User #3
 */
public class SendingResultsView extends JDialog {
	
	public interface ISendingResultsUpdate {
		public void onYes();
		public void onNo();
	}
	
	private ISendingResultsUpdate callback;
	private SendingResultsController controller;
	
	public SendingResultsView(Component owner, boolean bStatusNotOpened) {
		initComponents();
		controller = new SendingResultsController();
		sendingResultsModel1.setStatusNotOpened(bStatusNotOpened);
	}
	
	public boolean getAnswer(SnapAlertResults res) {
		controller.init(sendingResultsModel1, res);
		this.setVisible(true);
		return sendingResultsModel1.getAnswer();
	}
	
	public ISendingResultsUpdate getCallback() {
		return callback;
	}

	public JScrollPane getScrollPane1() {
		return scrollPane1;
	}


	/*public SendingResultsView(Dialog owner) {
		super(owner);
		initComponents();
	}*/

	public JButton getBtnYes() {
		return btnYes;
	}

	public JButton getBtnNo() {
		return btnNo;
	}

	public JButton getBtnOk() {
		return btnOk;
	}

	public JLabel getLblOpenStatus() {
		return lblOpenStatus;
	}

	private void btnYesActionPerformed(ActionEvent e) {
		// TODO add your code here
		sendingResultsModel1.setYesOption();
		this.dispose();
	}

	private void btnNoActionPerformed(ActionEvent e) {
		// TODO add your code here
		sendingResultsModel1.setNoOption();
		this.dispose();
	}

	private void btnOkActionPerformed(ActionEvent e) {
		// TODO add your code here
		sendingResultsModel1.setOkOption();
		this.dispose();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		ResourceBundle bundle = ResourceBundle.getBundle("no.ums.pas.localization.lang");
		btnYes = new JButton();
		btnNo = new JButton();
		lblOpenStatus = new JLabel();
		scrollPane1 = new JScrollPane();
		lblResult = new JLabel();
		btnOk = new JButton();
		sendingResultsModel1 = new SendingResultsModel();

		//======== this ========
		setTitle(bundle.getString("quicksend_dlg_results"));
		setModal(true);
		setResizable(false);
		setAlwaysOnTop(true);
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

		//======== scrollPane1 ========
		{

			//---- lblResult ----
			lblResult.setVerticalAlignment(SwingConstants.TOP);
			scrollPane1.setViewportView(lblResult);
		}

		//---- btnOk ----
		btnOk.setText(bundle.getString("common_ok"));
		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnOkActionPerformed(e);
			}
		});

		GroupLayout contentPaneLayout = new GroupLayout(contentPane);
		contentPane.setLayout(contentPaneLayout);
		contentPaneLayout.setHorizontalGroup(
			contentPaneLayout.createParallelGroup()
				.addGroup(contentPaneLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(contentPaneLayout.createParallelGroup()
						.addGroup(contentPaneLayout.createSequentialGroup()
							.addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE)
							.addContainerGap())
						.addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
							.addComponent(lblOpenStatus)
							.addGap(129, 129, 129))
						.addGroup(contentPaneLayout.createSequentialGroup()
							.addComponent(btnYes, GroupLayout.PREFERRED_SIZE, 114, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 159, Short.MAX_VALUE)
							.addComponent(btnNo, GroupLayout.PREFERRED_SIZE, 114, GroupLayout.PREFERRED_SIZE)
							.addContainerGap())
						.addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
							.addComponent(btnOk, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)
							.addGap(153, 153, 153))))
		);
		contentPaneLayout.setVerticalGroup(
			contentPaneLayout.createParallelGroup()
				.addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
					.addGap(18, 18, 18)
					.addComponent(lblOpenStatus)
					.addGap(18, 18, 18)
					.addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(btnNo)
						.addComponent(btnYes))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(btnOk)
					.addGap(30, 30, 30))
		);
		pack();
		setLocationRelativeTo(getOwner());

		//---- bindings ----
		bindingGroup = new BindingGroup();
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			sendingResultsModel1, BeanProperty.create("resText"),
			lblResult, BeanProperty.create("text")));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			sendingResultsModel1, BeanProperty.create("statusNotOpened"),
			lblOpenStatus, BeanProperty.create("visible")));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			sendingResultsModel1, BeanProperty.create("statusNotOpened"),
			btnNo, BeanProperty.create("visible")));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			sendingResultsModel1, BeanProperty.create("statusOpened"),
			btnOk, BeanProperty.create("visible")));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			sendingResultsModel1, BeanProperty.create("statusNotOpened"),
			btnYes, BeanProperty.create("visible")));
		bindingGroup.bind();
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JButton btnYes;
	private JButton btnNo;
	private JLabel lblOpenStatus;
	private JScrollPane scrollPane1;
	private JLabel lblResult;
	private JButton btnOk;
	private SendingResultsModel sendingResultsModel1;
	private BindingGroup bindingGroup;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
