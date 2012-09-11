/*
 * Created by JFormDesigner on Mon Mar 14 10:12:58 CET 2011
 */

package no.ums.pas.core.mainui.address_search;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.localization.Localization;
import no.ums.pas.ums.tools.StdIntegerArea;
import no.ums.ws.pas.GABTYPE;
import no.ums.ws.pas.UGabResult;
import no.ums.ws.pas.UGabSearchResultList;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author User #3
 */
public class AddressSearchDlg extends JFrame {

    private static final Log log = UmsLog.getLogger(AddressSearchDlg.class);
	
	private IAddressSearch callback;
	public AddressSearchDlg(IAddressSearch callback, JComponent parent) {
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
        txtAddress.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) { }

            @Override
            public void keyPressed(KeyEvent e) { 
            	cut(e, txtAddress);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                copyPaste(e, txtAddress);
            }
        });
        txtNumber.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) { }

            @Override
            public void keyPressed(KeyEvent e) {
            	cut(e, txtNumber);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                copyPaste(e, txtNumber);
            }
        });
        txtPlace.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) { }

            @Override
            public void keyPressed(KeyEvent e) { 
            	cut(e, txtPlace);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                copyPaste(e, txtPlace);
            }
        });
        txtPostCode.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) { }

            @Override
            public void keyPressed(KeyEvent e) {
            	cut(e, txtPostCode);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                copyPaste(e, txtPostCode);
            }
        });
        txtRegion.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) { }

            @Override
            public void keyPressed(KeyEvent e) {
            	cut(e,txtRegion);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                copyPaste(e, txtRegion);
            }
        });
	}

    private void copyPaste(KeyEvent e, JTextField textField) {
        if((e.getKeyCode() == KeyEvent.VK_V) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable clipData = clipboard.getContents(clipboard);
            try {
                int start = textField.getSelectionStart();
                int end = textField.getSelectionEnd();
                String startContent = textField.getText().substring(0,start);
                String endContent = textField.getText().substring(end);
                textField.setText(new StringBuilder().append(startContent).append((String) (clipData.getTransferData(DataFlavor.stringFlavor))).append(endContent).toString());
                textField.setCaretPosition(start + ((String)(clipData.getTransferData(DataFlavor.stringFlavor))).length());
            } catch (UnsupportedFlavorException e1) {
                log.warn("Trying to paste unsupported type: " + e1.getMessage());
                e1.printStackTrace();
            } catch (IOException e1) {
                log.warn("Exception while trying to paste: " + e1.getMessage());
                e1.printStackTrace();
            }
        }
        if((e.getKeyCode() == KeyEvent.VK_C) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            int start = textField.getSelectionStart();
            int end = textField.getSelectionEnd();
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection selection = new StringSelection(textField.getText().substring(start,end));
            clipboard.setContents(selection, null);
        }
        /*
        if((e.getKeyCode() == KeyEvent.VK_X) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            int start = textField.getSelectionStart();
            int end = textField.getSelectionEnd();
            String startContent = textField.getText().substring(0,start);
            String endContent = textField.getText().substring(end);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection selection = new StringSelection(textField.getText().substring(start,end));
            clipboard.setContents(selection, null);
            textField.setText(startContent + endContent);
            textField.setCaretPosition(start);
        }*/
    }

    private void cut(KeyEvent e, JTextField textField) {
        if((e.getKeyCode() == KeyEvent.VK_X) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            int start = textField.getSelectionStart();
            int end = textField.getSelectionEnd();
            String startContent = textField.getText().substring(0,start);
            String endContent = textField.getText().substring(end);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection selection = new StringSelection(textField.getText().substring(start,end));
            clipboard.setContents(selection, null);
            textField.setText(startContent + endContent);
            textField.setCaretPosition(start);
        }
    }
    
    public AddressSearchCountry newAddressSearchCountry(int cc, String country)
	{
		return new AddressSearchCountry(cc, country);
	}

	public void setCountries(List<AddressSearchCountry> countries, int defaultCountry)
	{
		cbxCountry.removeAllItems();
		AddressSearchCountry def = null;
		for(AddressSearchCountry s : countries)
		{
			cbxCountry.addItem(s);
			if(s.n_cc==defaultCountry)
				def = s;
		}
		cbxCountry.setSelectedItem(def);
		//addressSearchModel1.setCountry(def);
	}
	public void setDefaultCountry(AddressSearchCountry defaultCountry)
	{
		cbxCountry.setSelectedItem(defaultCountry);
	}
	
	public class AddressSearchListHeader
	{
		private String hit;
		private String adr;
		private String region;
		private String lon;
		private String lat;
		public String getHit() {
			return hit;
		}
		public void setHit(String hit) {
			this.hit = hit;
		}
		public String getAdr() {
			return adr;
		}
		public void setAdr(String adr) {
			this.adr = adr;
		}
		public String getRegion() {
			return region;
		}
		public void setRegion(String region) {
			this.region = region;
		}
		public String getLon() {
			return lon;
		}
		public void setLon(String lon) {
			this.lon = lon;
		}
		public String getLat() {
			return lat;
		}
		public void setLat(String lat) {
			this.lat = lat;
		}
	}
	
	public class AddressSearchListItem
	{
		private float hit;
		private String adr;
		private String region;
		private double lon;
		private double lat;
		private GABTYPE type;
		
		public GABTYPE getType() {
			return type;
		}
		public void setType(GABTYPE type) {
			this.type = type;
		}
		public float getHit() {
			return hit;
		}
		public void setHit(float hit) {
			this.hit = hit;
		}
		public String getAdr() {
			return adr;
		}
		public void setAdr(String adr) {
			this.adr = adr;
		}
		public String getRegion() {
			return region;
		}
		public void setRegion(String region) {
			this.region = region;
		}
		public double getLon() {
			return lon;
		}
		public void setLon(double lon) {
			this.lon = lon;
		}
		public double getLat() {
			return lat;
		}
		public void setLat(double lat) {
			this.lat = lat;
		}
		
		public String toString()
		{
			return getAdr();
		}
		
		public Object [] toArray()
		{
			return new Object [] { hit, adr, region, lon, lat };
		}
	}
	
	public interface IAddressSearch
	{
		public boolean onSearch(AddressSearchModel m);
		public boolean onAddressSelect(AddressSearchListItem s);
	}

	private void btnSearchActionPerformed(ActionEvent e) {
		callback.onSearch(addressSearchModel1);
	}
	
	private void setTableHeaders(UGabSearchResultList results)
	{
		

	}
	
	public void fillResults(UGabSearchResultList results)
	{
		//setTableHeaders();
		if(tblResults!=null) {
			DefaultTableModel dtm = ((DefaultTableModel)tblResults.getModel());
			for(int i=dtm.getRowCount()-1;i>=0;i--)
				((DefaultTableModel)tblResults.getModel()).removeRow(i);
		}
		java.util.Iterator<UGabResult> it = results.getList().getUGabResult().iterator();
		while(it.hasNext())
		{			
			UGabResult result = it.next();
			AddressSearchListItem li = new AddressSearchListItem();
			li.setAdr(result.getName());
			li.setHit(result.getMatch());
			li.setLat(result.getLat());
			li.setLon(result.getLon());
			li.setRegion(result.getRegion());
			li.setType(result.getType());

			TableColumnModel tcm = tblResults.getColumnModel();
			if(result.getType().equals(GABTYPE.HOUSE))
			{
				tblResults.getColumnModel().getColumn(1).setHeaderValue(Localization.l("adrsearch_dlg_address"));
				tblResults.getColumnModel().getColumn(2).setHeaderValue(Localization.l("adrsearch_dlg_region"));
			}
			else if(result.getType().equals(GABTYPE.STREET))
			{
				tblResults.getColumnModel().getColumn(1).setHeaderValue(Localization.l("adrsearch_dlg_streetname"));
				tblResults.getColumnModel().getColumn(2).setHeaderValue(Localization.l("adrsearch_dlg_region"));
			}
			else if(result.getType().equals(GABTYPE.POST))
			{
				tblResults.getColumnModel().getColumn(1).setHeaderValue(Localization.l("adrsearch_dlg_postno"));
				tblResults.getColumnModel().getColumn(2).setHeaderValue(Localization.l("adrsearch_dlg_region"));
			}
			else if(result.getType().equals(GABTYPE.REGION))
			{
				tblResults.getColumnModel().getColumn(1).setHeaderValue(Localization.l("adrsearch_dlg_region"));
				tblResults.getColumnModel().getColumn(2).setHeaderValue(Localization.l("adrsearch_dlg_region"));
			}
			tblResults.getColumnModel().getColumn(0).setHeaderValue(Localization.l("adrsearch_dlg_hit"));
			tblResults.getColumnModel().getColumn(3).setHeaderValue(Localization.l("common_lon"));
			tblResults.getColumnModel().getColumn(4).setHeaderValue(Localization.l("common_lat"));
			tblResults.getTableHeader().repaint();
			
			//Object[] obj_insert = { result.getMatch(), result.getName(), result.getRegion(), new Float(result.getLon()).toString(), new Float(result.getLat()).toString() }; //, m_icon_goto };
			Object [] obj_insert = { li.getHit(), li, li.getRegion(), li.getLon(), li.getLat() };
			//list.getModel().insert_row(obj_insert, -1);
			((DefaultTableModel)tblResults.getModel()).addRow(obj_insert);
		}
		//for(AddressSearchListItem item : l) {
		//	((DefaultTableModel)tblResults.getModel()).addRow(item.toArray());
			/*tblResults.getModel().setValueAt(item.hit, -1, 0);
			tblResults.getModel().setValueAt(item.adr, -1, 1);
			tblResults.getModel().setValueAt(item.region, -1, 2);
			tblResults.getModel().setValueAt(item.lon, -1, 3);
			tblResults.getModel().setValueAt(item.lat, -1, 4);*/			
//		}
	}

	private void tblResultsMouseClicked(MouseEvent e) {
		if(e.getClickCount()>=2)
		{
			int selRow = tblResults.getSelectedRow();
			//AddressSearchListItem sel = new AddressSearchListItem();
			//sel.hit = (Float)tblResults.getValueAt(selRow, 0);
			//sel.lon = Float.parseFloat(tblResults.getValueAt(selRow, 3).toString());
			//sel.lat = Float.parseFloat(tblResults.getValueAt(selRow, 4).toString());
			
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
		txtNumber = new JTextField();
		txtPlace = new JTextField();
		txtAddress = new JTextField();
		txtPostCode = new StdIntegerArea();
		txtRegion = new JTextField();
		lblPostCode = new JLabel();
		lblRegion = new JLabel();
		lblCountry = new JLabel();
		btnSearch = new JButton();
		scrollPane1 = new JScrollPane();
		tblResults = new JTable();
		cbxCountry = new JComboBox();
		addressSearchModel1 = new AddressSearchModel();

		//======== this ========
		setTitle(bundle.getString("adrsearch_dlg_title"));
		Container contentPane = getContentPane();

		//---- lblAddress ----
		lblAddress.setText(bundle.getString("adrsearch_dlg_address_and_house"));

		//---- lblPostCode ----
		lblPostCode.setText(bundle.getString("adrsearch_dlg_postcode_place"));

		//---- lblRegion ----
		lblRegion.setText(bundle.getString("adrsearch_dlg_region"));

		//---- lblCountry ----
		lblCountry.setText(bundle.getString("adrsearch_dlg_country"));

		//---- btnSearch ----
		btnSearch.setText(bundle.getString("common_search"));
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
						.addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
						.addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
							.addGroup(contentPaneLayout.createParallelGroup()
								.addComponent(lblPostCode, GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
								.addComponent(lblCountry, GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
								.addComponent(lblRegion, GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
								.addComponent(lblAddress, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE))
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addGroup(contentPaneLayout.createParallelGroup()
								.addGroup(contentPaneLayout.createSequentialGroup()
									.addComponent(txtPostCode, GroupLayout.PREFERRED_SIZE, 56, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(txtPlace, GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE))
								.addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
									.addComponent(txtAddress, GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(txtNumber, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE))
								.addComponent(txtRegion, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)
								.addComponent(cbxCountry, GroupLayout.PREFERRED_SIZE, 159, GroupLayout.PREFERRED_SIZE)))
						.addComponent(btnSearch, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 124, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		contentPaneLayout.setVerticalGroup(
			contentPaneLayout.createParallelGroup()
				.addGroup(contentPaneLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(lblAddress)
						.addComponent(txtAddress, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(txtNumber, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(txtPlace, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblPostCode)
						.addComponent(txtPostCode, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(txtRegion, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblRegion))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(lblCountry)
						.addComponent(cbxCountry, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(13, 13, 13)
					.addComponent(btnSearch)
					.addGap(18, 18, 18)
					.addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
					.addContainerGap())
		);
		contentPaneLayout.linkSize(SwingConstants.VERTICAL, new Component[] {txtPlace, txtPostCode});
		pack();
		setLocationRelativeTo(getOwner());

		//---- bindings ----
		bindingGroup = new BindingGroup();
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
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			addressSearchModel1, BeanProperty.create("country"),
			cbxCountry, BeanProperty.create("selectedItem")));
		bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
			addressSearchModel1, BeanProperty.create("address"),
			txtAddress, BeanProperty.create("text")));
		bindingGroup.bind();
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JLabel lblAddress;
	private JTextField txtNumber;
	private JTextField txtPlace;
	private JTextField txtAddress;
	private StdIntegerArea txtPostCode;
	private JTextField txtRegion;
	private JLabel lblPostCode;
	private JLabel lblRegion;
	private JLabel lblCountry;
	private JButton btnSearch;
	private JScrollPane scrollPane1;
	private JTable tblResults;
	private JComboBox cbxCountry;
	private AddressSearchModel addressSearchModel1;
	private BindingGroup bindingGroup;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
