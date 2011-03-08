/*
 * Created by JFormDesigner on Mon Mar 07 15:02:12 CET 2011
 */

package no.ums.pas.core.logon.view;

import java.awt.*;
import java.awt.Component;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import org.jdesktop.beansbinding.*;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;

/**
 * @author User #2
 */
public class PasswordUpdate extends JDialog {
	PasswordUpdateComplete callback;
	public PasswordUpdate(PasswordUpdateComplete callback) {
		this.callback = callback;
		initComponents();
	}
	
	public interface PasswordUpdateComplete
	{
		/**
		 * user clicks Save. Validate values
		 * @param model
		 * @return
		 */
		public PasswordResult onValidation(PasswordUpdateModel model);
		public void onCancel(PasswordUpdateModel model);
		/**
		 * after onValidation
		 * @param result
		 */
		public void onAfterValidation(PasswordResult result);
	}
	
	public enum PasswordResult
	{
		OK,
		WRONG_PASSWORD,
		PASSWORD_MISMATCH,
		PASSWORD_EMPTY,
	}
	

	private void btnOkActionPerformed(ActionEvent e) {
		callback.onAfterValidation(callback.onValidation(passwordModel1));
	}

	private void btnCancelActionPerformed(ActionEvent e) {
		callback.onCancel(passwordModel1);
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		lblOldPassword = new JLabel();
		lblNewPassword = new JLabel();
		lblRepeatNewPassword = new JLabel();
		txtOldPassword = new JPasswordField();
		txtNewPassword = new JPasswordField();
		txtRepeatNewPassword = new JPasswordField();
		btnOk = new JButton();
		btnCancel = new JButton();
		passwordModel1 = new PasswordUpdateModel();

		//======== this ========
		setAlwaysOnTop(true);
		setResizable(false);
		Container contentPane = getContentPane();

		//---- lblOldPassword ----
		lblOldPassword.setText("text");

		//---- lblNewPassword ----
		lblNewPassword.setText("text");

		//---- lblRepeatNewPassword ----
		lblRepeatNewPassword.setText("text");

		//---- btnOk ----
		btnOk.setText("text");
		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnOkActionPerformed(e);
			}
		});

		//---- btnCancel ----
		btnCancel.setText("text");
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnCancelActionPerformed(e);
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
							.addComponent(lblNewPassword, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
							.addComponent(txtNewPassword, GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE))
						.addGroup(contentPaneLayout.createSequentialGroup()
							.addComponent(lblRepeatNewPassword, GroupLayout.PREFERRED_SIZE, 53, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
							.addComponent(txtRepeatNewPassword, GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE))
						.addGroup(contentPaneLayout.createSequentialGroup()
							.addComponent(lblOldPassword, GroupLayout.PREFERRED_SIZE, 131, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
							.addComponent(txtOldPassword, GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE))
						.addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
							.addComponent(btnOk, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(btnCancel)))
					.addContainerGap())
		);
		contentPaneLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {lblNewPassword, lblOldPassword, lblRepeatNewPassword});
		contentPaneLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {btnCancel, btnOk});
		contentPaneLayout.setVerticalGroup(
			contentPaneLayout.createParallelGroup()
				.addGroup(contentPaneLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(lblOldPassword)
						.addComponent(txtOldPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(lblNewPassword)
						.addComponent(txtNewPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(lblRepeatNewPassword)
						.addComponent(txtRepeatNewPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
					.addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(btnOk)
						.addComponent(btnCancel))
					.addContainerGap())
		);
		pack();
		setLocationRelativeTo(getOwner());

		//---- bindings ----
		bindingGroup = new BindingGroup();
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			passwordModel1, BeanProperty.create("oldpassword"),
			txtOldPassword, BeanProperty.create("text")));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			passwordModel1, BeanProperty.create("newpassword"),
			txtNewPassword, BeanProperty.create("text")));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			passwordModel1, BeanProperty.create("repeatnewpassword"),
			txtRepeatNewPassword, BeanProperty.create("text")));
		bindingGroup.bind();
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JLabel lblOldPassword;
	private JLabel lblNewPassword;
	private JLabel lblRepeatNewPassword;
	private JPasswordField txtOldPassword;
	private JPasswordField txtNewPassword;
	private JPasswordField txtRepeatNewPassword;
	private JButton btnOk;
	private JButton btnCancel;
	private PasswordUpdateModel passwordModel1;
	private BindingGroup bindingGroup;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
