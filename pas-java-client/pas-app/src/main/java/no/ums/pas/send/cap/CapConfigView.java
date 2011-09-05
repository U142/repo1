/*
 * Created by JFormDesigner on Thu Aug 04 13:28:03 CEST 2011
 */

package no.ums.pas.send.cap;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import no.ums.ws.common.parm.UMapSendingCapFields;

import org.jdesktop.beansbinding.*;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.swingbinding.*;

/**
 * @author User #2
 */
public class CapConfigView extends JDialog {
	public CapConfigView(IController c) {
		super();
		controller = c;
		initComponents();
		controller.initialize(capConfigModel);
	}
	public CapConfigView()
	{
		this(new CapConfigCtrl());
	}
	
	public void setValues(UMapSendingCapFields cap)
	{
		controller.updateModel(cap, capConfigModel);
	}
	
	
	public void initialize(CapConfigModel model)
	{
		
	}

	private void okButtonActionPerformed(ActionEvent e) {
		result = controller.onOk(capConfigModel);
		dispose();
	}
	private void cancelButtonActionPerformed(ActionEvent e) {
	}
	
	private final IController controller;
	
	public interface IController 
	{
		
		public UMapSendingCapFields onOk(CapConfigModel capConfigModel);
		public void updateModel(UMapSendingCapFields cap,
				CapConfigModel capConfigModel);
		public void initialize(CapConfigModel model);
		public void addCode(CapConfigModel capConfigModel);
		public void removeSelectedCodes(CapConfigModel capConfigModel);
		public void removeAllCodes(CapConfigModel capConfigModel);
		public void codeSelectionChanged(CapConfigModel capConfigModel);
	}
	
	private UMapSendingCapFields result;
	
	public UMapSendingCapFields edit(UMapSendingCapFields cap) {
		if(cap==null)
			cap = new UMapSendingCapFields();
		this.result = cap;
		//initialize model from input
		controller.updateModel(result, capConfigModel);
		//edit model
		setVisible(true);
		return result;
	}

	private void txtCodeKeyPressed(KeyEvent e) {
		if(e.getKeyCode()==KeyEvent.VK_ENTER)
		{
			controller.addCode(capConfigModel);
		}
	}

	private void btnAddCodeActionPerformed(ActionEvent e) {
		controller.addCode(capConfigModel);
	}

	private void btnRemoveCodeActionPerformed(ActionEvent e) {
		controller.removeSelectedCodes(capConfigModel);
	}

	private void btnCodeClearActionPerformed(ActionEvent e) {
		controller.removeAllCodes(capConfigModel);
	}

	private void listCodesValueChanged(ListSelectionEvent e) {
		controller.codeSelectionChanged(capConfigModel);
	}



	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		ResourceBundle bundle = ResourceBundle.getBundle("no.ums.pas.localization.lang");
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		lblStatus = new JLabel();
		comboStatus = new JComboBox();
		panelAlertInfo = new JPanel();
		lblCategory = new JLabel();
		lblUrgency = new JLabel();
		comboUrgency = new JComboBox();
		scrollPane2 = new JScrollPane();
		listCategory = new JList();
		lblCertainty = new JLabel();
		comboCertainty = new JComboBox();
		lblSeverity = new JLabel();
		comboSeverity = new JComboBox();
		lblHeadline = new JLabel();
		scrollPane6 = new JScrollPane();
		txtHeadline = new JTextArea();
		lblDescription = new JLabel();
		scrollPane3 = new JScrollPane();
		txtDescription = new JTextArea();
		lblInstruction = new JLabel();
		txtInstruction = new JTextField();
		lblWeb = new JLabel();
		txtWeb = new JTextField();
		Contact = new JLabel();
		txtContact = new JTextField();
		lblResponseType = new JLabel();
		scrollPane4 = new JScrollPane();
		listResponsetype = new JList();
		lblAudience = new JLabel();
		txtAudience = new JTextField();
		panelAlert = new JPanel();
		lblSource = new JLabel();
		txtSource = new JTextField();
		lblScope = new JLabel();
		comboScope = new JComboBox();
		txtRestriction = new JTextField();
		lblRestriction = new JLabel();
		lblNote = new JLabel();
		scrollPane1 = new JScrollPane();
		txtNote = new JTextArea();
		lblCode = new JLabel();
		txtCode = new JTextField();
		btnAddCode = new JButton();
		scrollPane5 = new JScrollPane();
		listCodes = new JList();
		btnRemoveCode = new JButton();
		btnCodeClear = new JButton();
		buttonBar = new JPanel();
		okButton = new JButton();
		cancelButton = new JButton();
		capConfigModel = new CapConfigModel();

		//======== this ========
		setTitle(bundle.getString("CapConfigView.Title"));
		setModal(true);
		Container contentPane = getContentPane();

		//======== dialogPane ========
		{
			dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{

				//---- lblStatus ----
				lblStatus.setText(bundle.getString("CapConfigView.lblStatus.text"));
				lblStatus.setToolTipText(bundle.getString("CapConfigView.comboStatus.toolTipText"));
				lblStatus.setVisible(false);

				//---- comboStatus ----
				comboStatus.setToolTipText(bundle.getString("CapConfigView.comboStatus.toolTipText"));
				comboStatus.setVisible(false);

				//======== panelAlertInfo ========
				{

					//---- lblCategory ----
					lblCategory.setText(bundle.getString("CapConfigView.lblCategory.text"));
					lblCategory.setToolTipText(bundle.getString("CapConfigView.listCategory.toolTipText"));
					lblCategory.setForeground(Color.red);

					//---- lblUrgency ----
					lblUrgency.setText(bundle.getString("CapConfigView.lblUrgency.text"));
					lblUrgency.setToolTipText(bundle.getString("CapConfigView.comboUrgency.toolTipText"));
					lblUrgency.setForeground(Color.red);

					//---- comboUrgency ----
					comboUrgency.setToolTipText(bundle.getString("CapConfigView.comboUrgency.toolTipText"));

					//======== scrollPane2 ========
					{

						//---- listCategory ----
						listCategory.setToolTipText(bundle.getString("CapConfigView.listCategory.toolTipText"));
						scrollPane2.setViewportView(listCategory);
					}

					//---- lblCertainty ----
					lblCertainty.setText(bundle.getString("CapConfigView.lblCertainty.text"));
					lblCertainty.setToolTipText(bundle.getString("CapConfigView.comboCertainty.toolTipText"));
					lblCertainty.setForeground(Color.red);

					//---- comboCertainty ----
					comboCertainty.setToolTipText(bundle.getString("CapConfigView.comboCertainty.toolTipText"));

					//---- lblSeverity ----
					lblSeverity.setText(bundle.getString("CapConfigView.lblSeverity.text"));
					lblSeverity.setToolTipText(bundle.getString("CapConfigView.comboSeverity.toolTipText"));
					lblSeverity.setForeground(Color.red);

					//---- comboSeverity ----
					comboSeverity.setToolTipText(bundle.getString("CapConfigView.comboSeverity.toolTipText"));

					//---- lblHeadline ----
					lblHeadline.setText(bundle.getString("CapConfigView.lblHeadline.text"));
					lblHeadline.setToolTipText(bundle.getString("CapConfigView.txtHeadline.toolTipText"));

					//======== scrollPane6 ========
					{

						//---- txtHeadline ----
						txtHeadline.setToolTipText(bundle.getString("CapConfigView.txtHeadline.toolTipText"));
						scrollPane6.setViewportView(txtHeadline);
					}

					//---- lblDescription ----
					lblDescription.setText(bundle.getString("CapConfigView.lblDescription.text"));
					lblDescription.setToolTipText(bundle.getString("CapConfigView.txtDescription.toolTipText"));

					//======== scrollPane3 ========
					{

						//---- txtDescription ----
						txtDescription.setToolTipText(bundle.getString("CapConfigView.txtDescription.toolTipText"));
						scrollPane3.setViewportView(txtDescription);
					}

					//---- lblInstruction ----
					lblInstruction.setText(bundle.getString("CapConfigView.lblInstruction.text"));
					lblInstruction.setToolTipText(bundle.getString("CapConfigView.txtInstruction.toolTipText"));

					//---- txtInstruction ----
					txtInstruction.setToolTipText(bundle.getString("CapConfigView.txtInstruction.toolTipText"));

					//---- lblWeb ----
					lblWeb.setText(bundle.getString("CapConfigView.lblWeb.text"));
					lblWeb.setToolTipText(bundle.getString("CapConfigView.txtWeb.toolTipText"));

					//---- txtWeb ----
					txtWeb.setToolTipText(bundle.getString("CapConfigView.txtWeb.toolTipText"));

					//---- Contact ----
					Contact.setText(bundle.getString("CapConfigView.Contact.text"));
					Contact.setToolTipText(bundle.getString("CapConfigView.txtContact.toolTipText"));

					//---- txtContact ----
					txtContact.setToolTipText(bundle.getString("CapConfigView.txtContact.toolTipText"));

					//---- lblResponseType ----
					lblResponseType.setText(bundle.getString("CapConfigView.lblResponseType.text"));
					lblResponseType.setToolTipText(bundle.getString("CapConfigView.listResponsetype.toolTipText"));

					//======== scrollPane4 ========
					{

						//---- listResponsetype ----
						listResponsetype.setToolTipText(bundle.getString("CapConfigView.listResponsetype.toolTipText"));
						scrollPane4.setViewportView(listResponsetype);
					}

					//---- lblAudience ----
					lblAudience.setText(bundle.getString("CapConfigView.lblAudience.text"));
					lblAudience.setToolTipText(bundle.getString("CapConfigView.txtAudience.toolTipText"));

					//---- txtAudience ----
					txtAudience.setToolTipText(bundle.getString("CapConfigView.txtAudience.toolTipText"));

					GroupLayout panelAlertInfoLayout = new GroupLayout(panelAlertInfo);
					panelAlertInfo.setLayout(panelAlertInfoLayout);
					panelAlertInfoLayout.setHorizontalGroup(
						panelAlertInfoLayout.createParallelGroup()
							.addGroup(GroupLayout.Alignment.TRAILING, panelAlertInfoLayout.createSequentialGroup()
								.addGroup(panelAlertInfoLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
									.addGroup(panelAlertInfoLayout.createSequentialGroup()
										.addContainerGap()
										.addComponent(lblHeadline, GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE))
									.addGroup(GroupLayout.Alignment.LEADING, panelAlertInfoLayout.createSequentialGroup()
										.addContainerGap()
										.addComponent(lblResponseType, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
									.addGroup(GroupLayout.Alignment.LEADING, panelAlertInfoLayout.createSequentialGroup()
										.addContainerGap()
										.addComponent(lblAudience, GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE))
									.addGroup(GroupLayout.Alignment.LEADING, panelAlertInfoLayout.createSequentialGroup()
										.addContainerGap()
										.addGroup(panelAlertInfoLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
											.addComponent(lblWeb, GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
											.addComponent(lblInstruction, GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
											.addComponent(Contact, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)))
									.addGroup(GroupLayout.Alignment.LEADING, panelAlertInfoLayout.createSequentialGroup()
										.addGap(10, 10, 10)
										.addGroup(panelAlertInfoLayout.createParallelGroup()
											.addComponent(lblUrgency, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
											.addComponent(lblCertainty, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
											.addComponent(lblSeverity, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
											.addComponent(lblDescription, GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
											.addComponent(lblCategory, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE))))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(panelAlertInfoLayout.createParallelGroup()
									.addComponent(scrollPane6, GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)
									.addComponent(txtAudience, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)
									.addComponent(scrollPane4, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)
									.addComponent(txtWeb, GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)
									.addComponent(txtContact, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)
									.addComponent(txtInstruction, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)
									.addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)
									.addGroup(panelAlertInfoLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
										.addComponent(comboSeverity, GroupLayout.Alignment.LEADING, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(comboCertainty, GroupLayout.Alignment.LEADING, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(comboUrgency, GroupLayout.Alignment.LEADING, 0, 141, Short.MAX_VALUE))
									.addComponent(scrollPane3, GroupLayout.PREFERRED_SIZE, 362, GroupLayout.PREFERRED_SIZE))
								.addContainerGap())
					);
					panelAlertInfoLayout.setVerticalGroup(
						panelAlertInfoLayout.createParallelGroup()
							.addGroup(panelAlertInfoLayout.createSequentialGroup()
								.addGroup(panelAlertInfoLayout.createParallelGroup()
									.addComponent(scrollPane2, GroupLayout.PREFERRED_SIZE, 126, GroupLayout.PREFERRED_SIZE)
									.addComponent(lblCategory))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(panelAlertInfoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(comboUrgency, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(lblUrgency))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(panelAlertInfoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(lblCertainty)
									.addComponent(comboCertainty, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(panelAlertInfoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(lblSeverity)
									.addComponent(comboSeverity, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(panelAlertInfoLayout.createParallelGroup()
									.addComponent(lblHeadline)
									.addComponent(scrollPane6, GroupLayout.PREFERRED_SIZE, 79, GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(panelAlertInfoLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
									.addComponent(lblDescription, GroupLayout.Alignment.LEADING)
									.addComponent(scrollPane3, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGroup(panelAlertInfoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(lblInstruction)
									.addComponent(txtInstruction, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(panelAlertInfoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(lblWeb)
									.addComponent(txtWeb, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(panelAlertInfoLayout.createParallelGroup()
									.addComponent(txtContact, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(Contact))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(panelAlertInfoLayout.createParallelGroup()
									.addComponent(scrollPane4, GroupLayout.PREFERRED_SIZE, 119, GroupLayout.PREFERRED_SIZE)
									.addComponent(lblResponseType))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(panelAlertInfoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(txtAudience, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(lblAudience))
								.addContainerGap())
					);
					panelAlertInfoLayout.linkSize(SwingConstants.VERTICAL, new Component[] {scrollPane3, scrollPane6});
				}

				//======== panelAlert ========
				{

					//---- lblSource ----
					lblSource.setText(bundle.getString("CapConfigView.lblSource.text"));
					lblSource.setToolTipText(bundle.getString("CapConfigView.txtSource.toolTipText"));
					lblSource.setForeground(Color.red);

					//---- txtSource ----
					txtSource.setToolTipText(bundle.getString("CapConfigView.txtSource.toolTipText"));

					//---- lblScope ----
					lblScope.setText(bundle.getString("CapConfigView.lblScope.text"));
					lblScope.setToolTipText(bundle.getString("CapConfigView.comboScope.toolTipText"));
					lblScope.setForeground(Color.red);

					//---- comboScope ----
					comboScope.setToolTipText(bundle.getString("CapConfigView.comboScope.toolTipText"));

					//---- txtRestriction ----
					txtRestriction.setToolTipText(bundle.getString("CapConfigView.txtRestriction.toolTipText"));

					//---- lblRestriction ----
					lblRestriction.setText(bundle.getString("CapConfigView.lblRestriction.text"));
					lblRestriction.setToolTipText(bundle.getString("CapConfigView.txtRestriction.toolTipText"));

					//---- lblNote ----
					lblNote.setText(bundle.getString("CapConfigView.lblNote.text"));
					lblNote.setToolTipText(bundle.getString("CapConfigView.txtNote.toolTipText"));

					//======== scrollPane1 ========
					{

						//---- txtNote ----
						txtNote.setToolTipText(bundle.getString("CapConfigView.txtNote.toolTipText"));
						scrollPane1.setViewportView(txtNote);
					}

					//---- lblCode ----
					lblCode.setText(bundle.getString("CapConfigView.lblCode.text"));
					lblCode.setToolTipText(bundle.getString("CapConfigView.txtCode.toolTipText"));

					//---- txtCode ----
					txtCode.setToolTipText(bundle.getString("CapConfigView.txtCode.toolTipText"));
					txtCode.addKeyListener(new KeyAdapter() {
						@Override
						public void keyPressed(KeyEvent e) {
							txtCodeKeyPressed(e);
						}
					});

					//---- btnAddCode ----
					btnAddCode.setText(bundle.getString("common_add"));
					btnAddCode.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							btnAddCodeActionPerformed(e);
						}
					});

					//======== scrollPane5 ========
					{

						//---- listCodes ----
						listCodes.setToolTipText(bundle.getString("CapConfigView.txtCode.toolTipText"));
						listCodes.addListSelectionListener(new ListSelectionListener() {
							@Override
							public void valueChanged(ListSelectionEvent e) {
								listCodesValueChanged(e);
							}
						});
						scrollPane5.setViewportView(listCodes);
					}

					//---- btnRemoveCode ----
					btnRemoveCode.setText(bundle.getString("common_remove"));
					btnRemoveCode.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							btnRemoveCodeActionPerformed(e);
						}
					});

					//---- btnCodeClear ----
					btnCodeClear.setText(bundle.getString("common_clear"));
					btnCodeClear.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							btnCodeClearActionPerformed(e);
						}
					});

					GroupLayout panelAlertLayout = new GroupLayout(panelAlert);
					panelAlert.setLayout(panelAlertLayout);
					panelAlertLayout.setHorizontalGroup(
						panelAlertLayout.createParallelGroup()
							.addGroup(panelAlertLayout.createSequentialGroup()
								.addGroup(panelAlertLayout.createParallelGroup()
									.addGroup(panelAlertLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
										.addComponent(lblNote, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(lblRestriction, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(lblScope, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(lblSource, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE))
									.addComponent(lblCode, GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(panelAlertLayout.createParallelGroup()
									.addGroup(panelAlertLayout.createSequentialGroup()
										.addComponent(btnRemoveCode)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(btnCodeClear, GroupLayout.PREFERRED_SIZE, 107, GroupLayout.PREFERRED_SIZE)
										.addContainerGap())
									.addGroup(GroupLayout.Alignment.TRAILING, panelAlertLayout.createSequentialGroup()
										.addGroup(panelAlertLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
											.addComponent(scrollPane5, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
											.addGroup(GroupLayout.Alignment.LEADING, panelAlertLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
												.addComponent(txtRestriction)
												.addComponent(comboScope, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(txtSource, GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
												.addComponent(scrollPane1))
											.addGroup(GroupLayout.Alignment.LEADING, panelAlertLayout.createSequentialGroup()
												.addComponent(txtCode, GroupLayout.PREFERRED_SIZE, 169, GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(btnAddCode, GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE)))
										.addGap(45, 45, 45))))
					);
					panelAlertLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {btnCodeClear, btnRemoveCode});
					panelAlertLayout.setVerticalGroup(
						panelAlertLayout.createParallelGroup()
							.addGroup(panelAlertLayout.createSequentialGroup()
								.addGroup(panelAlertLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(lblSource)
									.addComponent(txtSource, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(panelAlertLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(lblScope)
									.addComponent(comboScope, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(panelAlertLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(lblRestriction)
									.addComponent(txtRestriction, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(panelAlertLayout.createParallelGroup()
									.addComponent(lblNote)
									.addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 106, GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(panelAlertLayout.createParallelGroup()
									.addGroup(panelAlertLayout.createSequentialGroup()
										.addGap(11, 11, 11)
										.addComponent(lblCode))
									.addGroup(panelAlertLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(txtCode, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(btnAddCode)))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(scrollPane5, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(panelAlertLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(btnRemoveCode)
									.addComponent(btnCodeClear))
								.addContainerGap(223, Short.MAX_VALUE))
					);
				}

				GroupLayout contentPanelLayout = new GroupLayout(contentPanel);
				contentPanel.setLayout(contentPanelLayout);
				contentPanelLayout.setHorizontalGroup(
					contentPanelLayout.createParallelGroup()
						.addGroup(contentPanelLayout.createSequentialGroup()
							.addContainerGap()
							.addGroup(contentPanelLayout.createParallelGroup()
								.addGroup(contentPanelLayout.createSequentialGroup()
									.addComponent(lblStatus, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
									.addGap(376, 376, 376))
								.addGroup(contentPanelLayout.createSequentialGroup()
									.addComponent(panelAlert, GroupLayout.PREFERRED_SIZE, 369, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)))
							.addComponent(panelAlertInfo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(26, 26, 26)
							.addComponent(comboStatus, 0, 0, Short.MAX_VALUE)
							.addGap(282, 282, 282))
				);
				contentPanelLayout.setVerticalGroup(
					contentPanelLayout.createParallelGroup()
						.addGroup(contentPanelLayout.createSequentialGroup()
							.addGroup(contentPanelLayout.createParallelGroup()
								.addGroup(contentPanelLayout.createSequentialGroup()
									.addGap(635, 635, 635)
									.addGroup(contentPanelLayout.createParallelGroup()
										.addComponent(lblStatus)
										.addComponent(comboStatus, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
								.addComponent(panelAlertInfo, GroupLayout.PREFERRED_SIZE, 637, GroupLayout.PREFERRED_SIZE)
								.addComponent(panelAlert, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addContainerGap(21, Short.MAX_VALUE))
				);
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));

				//---- okButton ----
				okButton.setText("OK");
				okButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						okButtonActionPerformed(e);
					}
				});

				//---- cancelButton ----
				cancelButton.setText("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						cancelButtonActionPerformed(e);
					}
				});

				GroupLayout buttonBarLayout = new GroupLayout(buttonBar);
				buttonBar.setLayout(buttonBarLayout);
				buttonBarLayout.setHorizontalGroup(
					buttonBarLayout.createParallelGroup()
						.addGroup(GroupLayout.Alignment.TRAILING, buttonBarLayout.createSequentialGroup()
							.addContainerGap(672, Short.MAX_VALUE)
							.addComponent(okButton, GroupLayout.PREFERRED_SIZE, 82, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
							.addContainerGap())
				);
				buttonBarLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {cancelButton, okButton});
				buttonBarLayout.setVerticalGroup(
					buttonBarLayout.createParallelGroup()
						.addGroup(buttonBarLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(cancelButton)
							.addComponent(okButton))
				);
			}
			dialogPane.add(buttonBar, BorderLayout.SOUTH);
		}

		GroupLayout contentPaneLayout = new GroupLayout(contentPane);
		contentPane.setLayout(contentPaneLayout);
		contentPaneLayout.setHorizontalGroup(
			contentPaneLayout.createParallelGroup()
				.addComponent(dialogPane, GroupLayout.PREFERRED_SIZE, 879, GroupLayout.PREFERRED_SIZE)
		);
		contentPaneLayout.setVerticalGroup(
			contentPaneLayout.createParallelGroup()
				.addComponent(dialogPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
		);
		pack();
		setLocationRelativeTo(getOwner());

		//---- bindings ----
		bindingGroup = new BindingGroup();
		bindingGroup.addBinding(SwingBindings.createJComboBoxBinding(UpdateStrategy.READ,
			capConfigModel, (BeanProperty) BeanProperty.create("listStatus"), comboStatus));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			capConfigModel, BeanProperty.create("status"),
			comboStatus, BeanProperty.create("selectedItem")));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			capConfigModel, BeanProperty.create("note"),
			txtNote, BeanProperty.create("text")));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			capConfigModel, BeanProperty.create("source"),
			txtSource, BeanProperty.create("text")));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			capConfigModel, BeanProperty.create("restriction"),
			txtRestriction, BeanProperty.create("text")));
		bindingGroup.addBinding(SwingBindings.createJComboBoxBinding(UpdateStrategy.READ,
			capConfigModel, (BeanProperty) BeanProperty.create("listInfoUrgency"), comboUrgency));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			capConfigModel, BeanProperty.create("urgency"),
			comboUrgency, BeanProperty.create("selectedItem")));
		bindingGroup.addBinding(SwingBindings.createJListBinding(UpdateStrategy.READ,
			capConfigModel, (BeanProperty) BeanProperty.create("listInfoCateogry"), listCategory));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			capConfigModel, BeanProperty.create("category"),
			listCategory, BeanProperty.create("selectedElements")));
		bindingGroup.addBinding(SwingBindings.createJComboBoxBinding(UpdateStrategy.READ,
			capConfigModel, (BeanProperty) BeanProperty.create("listInfoCertainty"), comboCertainty));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			capConfigModel, BeanProperty.create("certainty"),
			comboCertainty, BeanProperty.create("selectedItem")));
		bindingGroup.addBinding(SwingBindings.createJComboBoxBinding(UpdateStrategy.READ,
			capConfigModel, (BeanProperty) BeanProperty.create("listInfoSeverity"), comboSeverity));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			capConfigModel, BeanProperty.create("severity"),
			comboSeverity, BeanProperty.create("selectedItem")));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			capConfigModel, BeanProperty.create("headline"),
			txtHeadline, BeanProperty.create("text")));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			capConfigModel, BeanProperty.create("description"),
			txtDescription, BeanProperty.create("text")));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			capConfigModel, BeanProperty.create("instruction"),
			txtInstruction, BeanProperty.create("text")));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			capConfigModel, BeanProperty.create("web"),
			txtWeb, BeanProperty.create("text")));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			capConfigModel, BeanProperty.create("contact"),
			txtContact, BeanProperty.create("text")));
		bindingGroup.addBinding(SwingBindings.createJListBinding(UpdateStrategy.READ,
			capConfigModel, (BeanProperty) BeanProperty.create("listInfoResponseType"), listResponsetype));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			capConfigModel, BeanProperty.create("responseType"),
			listResponsetype, BeanProperty.create("selectedElements")));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			capConfigModel, BeanProperty.create("audience"),
			txtAudience, BeanProperty.create("text")));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			capConfigModel, BeanProperty.create("enableRestriction"),
			txtRestriction, BeanProperty.create("enabled")));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			capConfigModel, BeanProperty.create("enableOk"),
			okButton, BeanProperty.create("enabled")));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			capConfigModel, BeanProperty.create("code"),
			txtCode, BeanProperty.create("text")));
		bindingGroup.addBinding(SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE,
			capConfigModel, (BeanProperty) BeanProperty.create("codes"), listCodes));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			capConfigModel, BeanProperty.create("selectedCodes"),
			listCodes, BeanProperty.create("selectedElements")));
		bindingGroup.addBinding(SwingBindings.createJComboBoxBinding(UpdateStrategy.READ,
			capConfigModel, (BeanProperty) BeanProperty.create("listScope"), comboScope));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			capConfigModel, BeanProperty.create("scope"),
			comboScope, BeanProperty.create("selectedItem")));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			capConfigModel, BeanProperty.create("enableClearCode"),
			btnRemoveCode, BeanProperty.create("enabled")));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			capConfigModel, BeanProperty.create("enableClearAllCodes"),
			btnCodeClear, BeanProperty.create("enabled")));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			capConfigModel, BeanProperty.create("enableSource"),
			txtSource, BeanProperty.create("enabled")));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			capConfigModel, BeanProperty.create("enableAddCode"),
			btnAddCode, BeanProperty.create("enabled")));
		bindingGroup.bind();
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JPanel dialogPane;
	private JPanel contentPanel;
	private JLabel lblStatus;
	private JComboBox comboStatus;
	private JPanel panelAlertInfo;
	private JLabel lblCategory;
	private JLabel lblUrgency;
	private JComboBox comboUrgency;
	private JScrollPane scrollPane2;
	private JList listCategory;
	private JLabel lblCertainty;
	private JComboBox comboCertainty;
	private JLabel lblSeverity;
	private JComboBox comboSeverity;
	private JLabel lblHeadline;
	private JScrollPane scrollPane6;
	private JTextArea txtHeadline;
	private JLabel lblDescription;
	private JScrollPane scrollPane3;
	private JTextArea txtDescription;
	private JLabel lblInstruction;
	private JTextField txtInstruction;
	private JLabel lblWeb;
	private JTextField txtWeb;
	private JLabel Contact;
	private JTextField txtContact;
	private JLabel lblResponseType;
	private JScrollPane scrollPane4;
	private JList listResponsetype;
	private JLabel lblAudience;
	private JTextField txtAudience;
	private JPanel panelAlert;
	private JLabel lblSource;
	private JTextField txtSource;
	private JLabel lblScope;
	private JComboBox comboScope;
	private JTextField txtRestriction;
	private JLabel lblRestriction;
	private JLabel lblNote;
	private JScrollPane scrollPane1;
	private JTextArea txtNote;
	private JLabel lblCode;
	private JTextField txtCode;
	private JButton btnAddCode;
	private JScrollPane scrollPane5;
	private JList listCodes;
	private JButton btnRemoveCode;
	private JButton btnCodeClear;
	private JPanel buttonBar;
	private JButton okButton;
	private JButton cancelButton;
	private CapConfigModel capConfigModel;
	private BindingGroup bindingGroup;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
	
}
