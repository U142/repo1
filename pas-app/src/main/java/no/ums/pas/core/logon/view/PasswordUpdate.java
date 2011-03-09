/*
 * Created by JFormDesigner on Mon Mar 07 15:02:12 CET 2011
 */

package no.ums.pas.core.logon.view;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.border.*;
import org.jdesktop.beansbinding.*;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import no.ums.pas.PAS;

/**
 * @author User #2
 */
public class PasswordUpdate extends JDialog {
	PasswordUpdateComplete callback;
	public PasswordUpdate(PasswordUpdateComplete callback) {
		this.callback = callback;
		initComponents();
		updatePasswordStrengthColor();
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
		public void onAfterValidation(PasswordResult result, PasswordUpdateModel bean);
	}
	
	public enum PasswordResult
	{
		OK,
		WRONG_PASSWORD,
		PASSWORD_MISMATCH,
		PASSWORD_EMPTY,
	}
	
	public enum PasswordStrength
	{
		TOO_WEAK,
		WEAK,
		MEDIUM,
		STRONG,
	}
	

	private void btnOkActionPerformed(ActionEvent e) {
		callback.onAfterValidation(callback.onValidation(passwordModel1), passwordModel1);
	}

	private void btnCancelActionPerformed(ActionEvent e) {
		callback.onCancel(passwordModel1);
	}

	private void txtStrengthPropertyChange(PropertyChangeEvent e) {
	}

	private void txtStrengthKeyTyped(KeyEvent e) {
	}
	private void txtNewPasswordPropertyChange(PropertyChangeEvent e) {
	}

	private void txtNewPasswordKeyTyped(KeyEvent e) {
	}

	public JTextField getTxtStrength() {
		return txtStrength;
	}

	private void txtNewPasswordKeyReleased(KeyEvent e) {
		updatePasswordStrengthColor();
	}
	
	private void updatePasswordStrengthColor()
	{
		Color c;
		switch(passwordModel1.updatePasswordStrength())
		{
		case WEAK:
			c = new Color(255, 255, 150);
			btnOk.setEnabled(false);
			break;
		case MEDIUM:
			c = new Color(150, 200, 255);
			btnOk.setEnabled(true);
			break;
		case STRONG:
			c = new Color(150, 255, 150);
			btnOk.setEnabled(true);
			break;
		case TOO_WEAK:
		default:
			c = new Color(255, 150, 150);
			btnOk.setEnabled(false);
			break;
		}
		lblStrengthCol.setBackground(c);
	}

	private void thisKeyPressed(KeyEvent e) {
		btnOk.doClick();
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
		txtStrength = new JTextField();
		lblStrengthCol = new JTextField();
		passwordModel1 = new PasswordUpdateModel();

		//======== this ========
		setAlwaysOnTop(true);
		setResizable(false);
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				thisKeyPressed(e);
			}
		});
		Container contentPane = getContentPane();
		getRootPane().setDefaultButton(btnOk);

		//---- lblOldPassword ----
		lblOldPassword.setText("text");
		lblOldPassword.setText(PAS.l("mainmenu_update_password_old"));

		//---- lblNewPassword ----
		lblNewPassword.setText("text");
		lblNewPassword.setText(PAS.l("mainmenu_update_password_new"));

		//---- lblRepeatNewPassword ----
		lblRepeatNewPassword.setText("text");
		lblRepeatNewPassword.setText(PAS.l("mainmenu_update_password_repeat"));

		//---- txtNewPassword ----
		txtNewPassword.addPropertyChangeListener("text", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				txtNewPasswordPropertyChange(e);
			}
		});
		txtNewPassword.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				txtNewPasswordKeyReleased(e);
			}
			@Override
			public void keyTyped(KeyEvent e) {
				txtNewPasswordKeyTyped(e);
			}
		});

		//---- btnOk ----
		btnOk.setText("text");
		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnOkActionPerformed(e);
			}
		});
		btnOk.setText(PAS.l("common_ok"));

		//---- btnCancel ----
		btnCancel.setText("text");
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnCancelActionPerformed(e);
			}
		});
		btnCancel.setText(PAS.l("common_cancel"));

		//---- txtStrength ----
		txtStrength.setEditable(false);
		txtStrength.setBackground(SystemColor.control);
		txtStrength.setBorder(null);
		txtStrength.setRequestFocusEnabled(false);
		txtStrength.setFocusable(false);
		txtStrength.addPropertyChangeListener("text", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				txtStrengthPropertyChange(e);
			}
		});
		txtStrength.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				txtStrengthKeyTyped(e);
			}
		});

		//---- lblStrengthCol ----
		lblStrengthCol.setFocusable(false);

		GroupLayout contentPaneLayout = new GroupLayout(contentPane);
		contentPane.setLayout(contentPaneLayout);
		contentPaneLayout.setHorizontalGroup(
			contentPaneLayout.createParallelGroup()
				.addGroup(contentPaneLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addGroup(contentPaneLayout.createSequentialGroup()
							.addComponent(btnOk, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(btnCancel))
						.addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
							.addGroup(GroupLayout.Alignment.LEADING, contentPaneLayout.createSequentialGroup()
								.addComponent(lblRepeatNewPassword, GroupLayout.PREFERRED_SIZE, 53, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(txtRepeatNewPassword))
							.addGroup(GroupLayout.Alignment.LEADING, contentPaneLayout.createSequentialGroup()
								.addComponent(lblOldPassword, GroupLayout.PREFERRED_SIZE, 131, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(txtOldPassword))
							.addGroup(GroupLayout.Alignment.LEADING, contentPaneLayout.createSequentialGroup()
								.addComponent(lblNewPassword, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(txtNewPassword, GroupLayout.PREFERRED_SIZE, 252, GroupLayout.PREFERRED_SIZE))))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(lblStrengthCol, GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(txtStrength, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE)
					.addGap(1, 1, 1))
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
					.addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE, false)
						.addComponent(lblNewPassword)
						.addComponent(txtNewPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(txtStrength, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblStrengthCol, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(lblRepeatNewPassword)
						.addComponent(txtRepeatNewPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18, 18, 18)
					.addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(btnOk)
						.addComponent(btnCancel))
					.addGap(27, 27, 27))
		);
		contentPaneLayout.linkSize(SwingConstants.VERTICAL, new Component[] {txtNewPassword, txtStrength});
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
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			passwordModel1, BeanProperty.create("strength"),
			txtStrength, BeanProperty.create("text")));
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
	private JTextField txtStrength;
	private JTextField lblStrengthCol;
	private PasswordUpdateModel passwordModel1;
	private BindingGroup bindingGroup;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
