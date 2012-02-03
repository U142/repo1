/*
 * Created by JFormDesigner on Thu Feb 02 14:06:31 CET 2012
 */

package no.ums.pas.plugins.centric.address_search;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import no.ums.pas.PAS;
import no.ums.pas.core.mainui.address_search.*;
import no.ums.pas.core.mainui.address_search.AddressSearchDlg.AddressSearchListItem;
import no.ums.pas.core.mainui.address_search.AddressSearchDlg.IAddressSearch;
import no.ums.pas.localization.Localization;
import no.ums.pas.ums.tools.*;
import org.jdesktop.beansbinding.*;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.swingbinding.*;

/**
 * @author User #2
 */
public class CentricAddressSearchDlg extends JFrame {

	private IAddressSearch callback;

	public CentricAddressSearchDlg(IAddressSearch callback, JComponent parent) {
		this.callback = callback;
		initComponents();
		getRootPane().setDefaultButton(btnSearch);
		this.setLocationRelativeTo(parent);
		try
		{
			this.setIconImage(PAS.get_pas().getIconImage());
		}
		catch(Exception e)
		{
			//if icon is not set, ignore. Only for test purposes
		}
	}


	private void btnSearchActionPerformed(ActionEvent e) {
		callback.onSearch(addressSearchModel1);
	}

	private void tblResultsMouseClicked(MouseEvent e) {
		if(e.getClickCount()>=2)
		{
			int selRow = tblResults.getSelectedRow();
			callback.onAddressSelect((AddressSearchListItem)tblResults.getValueAt(selRow, 1));
		}
	}

	public JButton getBtnSearch() {
		return btnSearch;
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		ResourceBundle bundle = ResourceBundle.getBundle("no.ums.pas.localization.lang");
		lblAddress = new JLabel();
		txtAddress = new JTextField();
		txtNumber = new JTextField();
		lblPostCode = new JLabel();
		txtPostCode = new StdIntegerArea();
		txtPlace = new JTextField();
		lblRegion = new JLabel();
		txtRegion = new JTextField();
		btnSearch = new JButton();
		scrollPane1 = new JScrollPane();
		tblResults = new JTable();
		addressSearchModel1 = new AddressSearchModel();

		//======== this ========
		Container contentPane = getContentPane();

		//---- lblAddress ----
		lblAddress.setText(bundle.getString("CentricAddressSearchDlg.lblAddress.text"));

		//---- lblPostCode ----
		lblPostCode.setText(bundle.getString("CentricAddressSearchDlg.lblPostCode.text"));

		//---- lblRegion ----
		lblRegion.setText(bundle.getString("CentricAddressSearchDlg.lblRegion.text"));

		//---- btnSearch ----
		btnSearch.setText(bundle.getString("CentricAddressSearchDlg.btnSearch.text"));
		btnSearch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnSearchActionPerformed(e);
			}
		});

		//======== scrollPane1 ========
		{

			//---- tblResults ----
			tblResults.setModel(new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
					"Hit", "Address", "Region", "Lon", "Lat"
				}
			) {
				boolean[] columnEditable = new boolean[] {
					false, false, false, false, false
				};
				@Override
				public boolean isCellEditable(int rowIndex, int columnIndex) {
					return columnEditable[columnIndex];
				}
			});
			{
				TableColumnModel cm = tblResults.getColumnModel();
				cm.getColumn(0).setResizable(false);
				cm.getColumn(3).setResizable(false);
				cm.getColumn(4).setResizable(false);
			}
			tblResults.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			tblResults.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					tblResultsMouseClicked(e);
					tblResultsMouseClicked(e);
				}
			});
			scrollPane1.setViewportView(tblResults);
		}

		GroupLayout contentPaneLayout = new GroupLayout(contentPane);
		contentPane.setLayout(contentPaneLayout);
		contentPaneLayout.setHorizontalGroup(
			contentPaneLayout.createParallelGroup()
				.addGroup(contentPaneLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(contentPaneLayout.createParallelGroup()
						.addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
						.addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
							.addGroup(contentPaneLayout.createSequentialGroup()
								.addComponent(lblAddress, GroupLayout.PREFERRED_SIZE, 133, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(txtAddress, GroupLayout.PREFERRED_SIZE, 256, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(txtNumber, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE))
							.addGroup(contentPaneLayout.createSequentialGroup()
								.addComponent(lblPostCode, GroupLayout.PREFERRED_SIZE, 133, GroupLayout.PREFERRED_SIZE)
								.addGap(5, 5, 5)
								.addComponent(txtPostCode, GroupLayout.PREFERRED_SIZE, 56, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(txtPlace, GroupLayout.PREFERRED_SIZE, 249, GroupLayout.PREFERRED_SIZE))
							.addGroup(contentPaneLayout.createSequentialGroup()
								.addComponent(lblRegion, GroupLayout.PREFERRED_SIZE, 133, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(txtRegion)))
						.addComponent(btnSearch, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 124, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		contentPaneLayout.setVerticalGroup(
			contentPaneLayout.createParallelGroup()
				.addGroup(contentPaneLayout.createSequentialGroup()
					.addGap(30, 30, 30)
					.addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(lblAddress)
						.addComponent(txtAddress, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(txtNumber, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(contentPaneLayout.createParallelGroup()
						.addGroup(contentPaneLayout.createSequentialGroup()
							.addGap(3, 3, 3)
							.addComponent(lblPostCode))
						.addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(txtPostCode, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
							.addComponent(txtPlace, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(lblRegion)
						.addComponent(txtRegion, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(btnSearch)
					.addContainerGap())
		);
		pack();
		setLocationRelativeTo(getOwner());

		//---- bindings ----
		bindingGroup = new BindingGroup();
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			addressSearchModel1, BeanProperty.create("address"),
			txtAddress, BeanProperty.create("text")));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			addressSearchModel1, BeanProperty.create("house"),
			txtNumber, BeanProperty.create("text")));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			addressSearchModel1, BeanProperty.create("postno"),
			txtPostCode, BeanProperty.create("text")));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			addressSearchModel1, BeanProperty.create("place"),
			txtPlace, BeanProperty.create("text")));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			addressSearchModel1, BeanProperty.create("region"),
			txtRegion, BeanProperty.create("text")));
		bindingGroup.bind();
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JLabel lblAddress;
	private JTextField txtAddress;
	private JTextField txtNumber;
	private JLabel lblPostCode;
	private StdIntegerArea txtPostCode;
	private JTextField txtPlace;
	private JLabel lblRegion;
	private JTextField txtRegion;
	private JButton btnSearch;
	private JScrollPane scrollPane1;
	private JTable tblResults;
	private AddressSearchModel addressSearchModel1;
	private BindingGroup bindingGroup;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
