/*
 * Created by JFormDesigner on Fri May 09 18:11:49 IST 2014
 */

package no.ums.pas.area;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.area.AreaController.AreaSource;
import no.ums.pas.area.FilterController.FilterSource;
import no.ums.pas.importer.ImportAddressFile;
import no.ums.pas.importer.gis.PreviewFrame;
import no.ums.pas.maps.defines.Navigation;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.ums.tools.Col;

/**
 * @author abhinava
 */
public class FilterUI extends JFrame implements ActionListener, WindowListener{
	
	private static final Log log = UmsLog.getLogger(FilterUI.class);
	
	private Col m_default_color = new Col(new Color(1.0f, 0.0f, 0.0f, 0.2f), new Color(1.0f, 0.0f, 0.0f, 0.9f));
	private Navigation m_navigation = null;
	private FilterController FilterController; 
	protected Navigation get_navigation() { return m_navigation; }
	private ShapeStruct shape = null;
	private PreviewFrame m_gis_preview = null;
	public FilterUI(FilterController FilterController,String title,Navigation nav) {
		this.FilterController = FilterController;
		m_navigation = nav;
		
		this.setResizable(false);
		this.setTitle(title);
//		removeMinMaxClose(this);
		initComponents();
		initExtra();
		
		this.setVisible(true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(this);
	}
	
	public JButton getBtnSave() {
		return btnSave;
	}
	
	public JButton getBtnCancel() {
		return btnCancel;
	}
	
	/*public JToggleButton getTbPolygon() {
		return tbPolygon;
	}*/

	/*public JToggleButton getTbEllipse() {
		return tbEllipse;
	}*/
	
	public JToggleButton getTbImportPoly() {
		return tbImportPoly;
	}

	public JTextField getTxtName() {
		return txtName;
	}
	
	public JTextArea getTxtDescription() {
		return txtDescription;
	}
	
	public void setErrorMessage(String message)
	{
		lblErrorMessage.setText(message);
	}
	
	public void clearErrorMessage()
	{
		lblErrorMessage.setText("");
	}
	
	public void removeMinMaxClose(Component comp)  
	  {  
	    if(comp instanceof AbstractButton)  
	    {  
	      comp.getParent().remove(comp);  
	    }  
	    if (comp instanceof Container)  
	    {  
	      Component[] comps = ((Container)comp).getComponents();  
	      for(int x = 0, y = comps.length; x < y; x++)  
	      {  
	        removeMinMaxClose(comps[x]);  
	      }  
	    }  
	  }  
	
	private void initExtra() {
		txtName.setDocument(new JTextFieldLimit(50));
		//max 50 chars allowed as area name
		txtName.addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent arg0) {
				FilterController.actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_area_name_changed"));
				if(FilterController.getSource().equals(FilterSource.LIBRARY))
					updateShapeName();
			}
			
			@Override
			public void keyTyped(KeyEvent arg0) {
			}
			
			@Override
			public void keyPressed(KeyEvent arg0) {
			}
		});
		
		//tbPolygon.setActionCommand("act_sendingtype_polygon");
		//tbEllipse.setActionCommand("act_sendingtype_ellipse");
		tbImportPoly.setActionCommand("act_preview_gislist");
		//tbPolygon.addActionListener(this);
		//tbEllipse.addActionListener(this);
		tbImportPoly.addActionListener(this);
		ButtonGroup btnGroupAutoShape = new ButtonGroup();
		//btnGroupAutoShape.add(tbPolygon);
		//btnGroupAutoShape.add(tbEllipse);
		btnGroupAutoShape.add(tbImportPoly);
//from before commented		tbImportPoly.setVisible(false);
		clearErrorMessage();
		updateShapeName();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		/*if(e.getActionCommand().equals("act_sendingtype_polygon"))
		{
			if(FilterController.getSource().equals(AreaSource.NEW_ALERT) || FilterController.getSource().equals(AreaSource.STATUS))
				return;
			log.debug("inside FilterUI act_sendingtype_polygon event called");
			shape = new PolygonStruct(get_navigation().getDimension());
//			shape.finalizeShape();
			FilterController.setEditShape(shape);
			updateShapeName();
			FilterController.actionPerformed(e);
		}
		else if(e.getActionCommand().equals("act_sendingtype_ellipse") || FilterController.getSource().equals(AreaSource.STATUS))
		{
			if(FilterController.getSource().equals(AreaSource.NEW_ALERT))
				return;
			log.debug("inside FilterUI act_sendingtype_ellipse event called");
			shape = new EllipseStruct();
//			shape.finalizeShape();
			FilterController.setEditShape(shape);
			updateShapeName();
			FilterController.actionPerformed(e);
		}*/
		if(e.getActionCommand().equals("act_preview_gislist"))
		{
			if(FilterController.getSource().equals(AreaSource.NEW_ALERT) || FilterController.getSource().equals(AreaSource.STATUS))
				return;
			
			new ImportAddressFile(FilterController, "act_polygon_imported", false, PAS.get_pas());
			
			FilterController.actionPerformed(e);
		}

	}
	
	private void updateShapeName()
	{
		if(PAS.get_pas().getPredefinedFilterController().get_shape()!=null)
		{
			PAS.get_pas().getPredefinedFilterController().get_shape().shapeName = (getTxtName().getText() == null) ? "" : getTxtName().getText();
			PAS.get_pas().kickRepaint();
		}
	}
	
	private void close()
	{
		//FilterController.actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_cancel_predefined_area"));
	}
	
	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		lblErrorMessage = new JLabel();
		lblName = new JLabel();
		txtName = new JTextField();
		lblMandatorySymbol1 = new JLabel();
		lblEmpty1 = new JLabel();
		lblDescription = new JLabel();
		scrollPane1 = new JScrollPane();
		txtDescription = new JTextArea();
		lblAreaDefinition = new JLabel();
		panel1 = new JPanel();
		tbPolygon = new JToggleButton();
		tbEllipse = new JToggleButton();
		tbImportPoly = new JToggleButton();
		panel3 = new JPanel();
		label1 = new JLabel();
		lblMandatoryText = new JLabel();
		panel2 = new JPanel();
		btnSave = new JButton();
		btnCancel = new JButton();

		//======== this ========
		Container contentPane = getContentPane();
		contentPane.setLayout(new GridBagLayout());
		((GridBagLayout)contentPane.getLayout()).columnWidths = new int[] {40, 84, 286, 40, 72, 0};
		((GridBagLayout)contentPane.getLayout()).rowHeights = new int[] {42, 36, 0, 69, 53, 35, 0, 23, 0};
		((GridBagLayout)contentPane.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};
		((GridBagLayout)contentPane.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

		//---- lblErrorMessage ----
		lblErrorMessage.setText("error message goes here");
		lblErrorMessage.setForeground(Color.red);
		contentPane.add(lblErrorMessage, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
			GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL,
			new Insets(0, 0, 5, 5), 0, 0));

		//---- lblName ----
		lblName.setText("Name:");
		contentPane.add(lblName, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 5), 0, 0));
		contentPane.add(txtName, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 5), 0, 0));

		//---- lblMandatorySymbol1 ----
		lblMandatorySymbol1.setText("*");
		lblMandatorySymbol1.setForeground(Color.red);
		contentPane.add(lblMandatorySymbol1, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 5), 0, 0));
		contentPane.add(lblEmpty1, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 5), 0, 0));

		//---- lblDescription ----
		lblDescription.setText("Description:");
		contentPane.add(lblDescription, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 5), 0, 0));

		//======== scrollPane1 ========
		{
			scrollPane1.setViewportView(txtDescription);
		}
		contentPane.add(scrollPane1, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 5), 0, 0));

		//---- lblAreaDefinition ----
		/*lblAreaDefinition.setText("Area Definition:");
		contentPane.add(lblAreaDefinition, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 5), 0, 0));*/

		//======== panel1 ========
		{
			panel1.setLayout(new GridBagLayout());
			((GridBagLayout)panel1.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
			((GridBagLayout)panel1.getLayout()).rowHeights = new int[] {0, 0};
			((GridBagLayout)panel1.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};
			((GridBagLayout)panel1.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

			//---- tbPolygon ----
		/*	tbPolygon.setIcon(new ImageIcon(getClass().getResource("/no/ums/pas/icons/send_polygon_24.png")));
			panel1.add(tbPolygon, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 5), 0, 0));
*/
			//---- tbEllipse ----
			/*tbEllipse.setIcon(new ImageIcon(getClass().getResource("/no/ums/pas/icons/send_ellipse_24.png")));
			panel1.add(tbEllipse, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 5), 0, 0));*/

			//---- tbImportPoly ----
			tbImportPoly.setIcon(new ImageIcon(getClass().getResource("/no/ums/pas/icons/folder2_24.png")));
			panel1.add(tbImportPoly, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));
		}
		contentPane.add(panel1, new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0,
			GridBagConstraints.WEST, GridBagConstraints.NONE,
			new Insets(0, 0, 5, 5), 0, 0));

		//======== panel3 ========
		{
			panel3.setLayout(new GridBagLayout());
			((GridBagLayout)panel3.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
			((GridBagLayout)panel3.getLayout()).rowHeights = new int[] {0, 0};
			((GridBagLayout)panel3.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 1.0E-4};
			((GridBagLayout)panel3.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

			//---- label1 ----
			label1.setText("*");
			label1.setForeground(Color.red);
			panel3.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 5), 0, 0));

			//---- lblMandatoryText ----
			lblMandatoryText.setText("= Mandatory Input");
			panel3.add(lblMandatoryText, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 5), 0, 0));
		}
		contentPane.add(panel3, new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 5), 0, 0));

		//======== panel2 ========
		{
			panel2.setLayout(new GridBagLayout());
			((GridBagLayout)panel2.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
			((GridBagLayout)panel2.getLayout()).rowHeights = new int[] {0, 0};
			((GridBagLayout)panel2.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0E-4};
			((GridBagLayout)panel2.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

			//---- btnSave ----
			btnSave.setText("Save");
			panel2.add(btnSave, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 5), 0, 0));

			//---- btnCancel ----
			btnCancel.setText("Cancel");
			panel2.add(btnCancel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 5), 0, 0));
		}
		contentPane.add(panel2, new GridBagConstraints(2, 6, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 5), 0, 0));
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JLabel lblErrorMessage;
	private JLabel lblName;
	private JTextField txtName;
	private JLabel lblMandatorySymbol1;
	private JLabel lblEmpty1;
	private JLabel lblDescription;
	private JScrollPane scrollPane1;
	private JTextArea txtDescription;
	private JLabel lblAreaDefinition;
	private JPanel panel1;
	private JToggleButton tbPolygon;
	private JToggleButton tbEllipse;
	private JToggleButton tbImportPoly;
	private JPanel panel3;
	private JLabel label1;
	private JLabel lblMandatoryText;
	private JPanel panel2;
	private JButton btnSave;
	private JButton btnCancel;
	private boolean enableButton=false;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
	
	public void enableSave(boolean enablesave) {
		enableButton=true;
	}
	
	
	@Override
	public void windowActivated(WindowEvent arg0) {
		log.info("Save Button Enabled?....:"+enableButton);
		btnSave.setEnabled(enableButton);
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		close();
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		btnSave.setEnabled(enableButton);
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		btnSave.setEnabled(enableButton);
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		btnSave.setEnabled(enableButton);
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		btnSave.setEnabled(enableButton);
	}
}
