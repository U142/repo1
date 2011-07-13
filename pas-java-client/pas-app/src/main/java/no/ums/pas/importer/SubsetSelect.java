package no.ums.pas.importer;


import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.core.Variables;
import no.ums.pas.core.defines.SearchPanelResults;
import no.ums.pas.maps.MapFrame.MapMode;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.send.SendObject;
import no.ums.pas.ums.tools.StdTextLabel;
import no.ums.pas.ums.tools.Utils;

import javax.swing.JDialog;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;


/*manually select a subset from e.g. a SOSI file (FLATE)*/

public class SubsetSelect extends JDialog implements WindowListener {

    private static final Log log = UmsLog.getLogger(SubsetSelect.class);
    
	public static final long serialVersionUID = 1;



	SubsetSelectPanel m_panel;
	protected StdTextLabel m_lbl_info;
	protected ShapeStruct currentSelection = null;
	
	protected void close() {
		this.setVisible(false);
		if(currentSelection!=null)
		{
			PAS.pasplugin.removeShapeToPaint(currentSelection);
		}
	}
	
	public SubsetSelect(String [] sz_columns, int [] n_width, boolean [] b_editable, Dimension dim, ActionListener callback, java.util.List<SendObject> sendings) {
		super(PAS.get_pas(), "Select subset", true);
		this.setModal(true);
		this.addWindowListener(this);
		this.setAlwaysOnTop(true);
		m_lbl_info = new StdTextLabel("", new Dimension(dim.width, 300));
		
		m_panel = new SubsetSelectPanel(sz_columns, n_width, b_editable, new Dimension(dim.width, dim.height-300), callback, sendings);

		//m_panel.get_table().setFont(getFont().deriveFont(1));
		m_panel.setSize(dim.width, dim.height-300);
		this.add(m_panel, BorderLayout.NORTH);
		//this.add(lbl_ting, BorderLayout.NORTH); // Dette måtte til for å få tilbake bilde, forstår ikke hvorfor
		//m_panel.setSize(new Dimension(dim.width, dim.height-300));
		//m_panel.setBorder(BorderFactory.createRaisedBevelBorder());
		this.add(m_lbl_info, BorderLayout.SOUTH);
		Dimension ul = Utils.screendlg_upperleft(dim);
		setBounds(ul.width, ul.height, dim.width, dim.height);
		Point p = no.ums.pas.ums.tools.Utils.get_dlg_location_centered(200, 500);
		p.setLocation(p.x, p.y+PAS.get_pas().getHeight()/4);
		setLocation(p);
		setVisible(true);
	}
	
	public SubsetSelect(String [] sz_columns, int [] n_width, boolean [] b_editable, Dimension dim, ActionListener callback, ShapeStruct [] sendings) {
		super(PAS.get_pas(), "Select subset", true);
		this.setModal(true);
		this.addWindowListener(this);
		m_lbl_info = new StdTextLabel("", new Dimension(dim.width, 300));
		
		m_panel = new SubsetSelectPanel(sz_columns, n_width, b_editable, new Dimension(50,50), callback, sendings);
		this.add(m_panel, BorderLayout.NORTH);
		this.add(m_lbl_info, BorderLayout.SOUTH);
		Dimension ul = Utils.screendlg_upperleft(dim);
		setBounds(ul.width, ul.height, dim.width, dim.height);
		Point p = no.ums.pas.ums.tools.Utils.get_dlg_location_centered(200, 500);
		p.setLocation(p.x, p.y+PAS.get_pas().getHeight()/4);
		setLocation(p);
		setVisible(true);
		
	}
	
	protected void doResize()
	{
		int w = getWidth();
		int h = getHeight();
		//setPreferredSize(new Dimension(getWidth(), getHeight()));
		m_panel.setPreferredSize(new Dimension(getWidth(), getHeight()-300));
		//m_panel.m_tbl.setPreferredSize(new Dimension(getWidth(), getHeight()-300));
		m_lbl_info.setPreferredSize(new Dimension(getWidth(), 300));	
		m_panel.revalidate();
		
	}


	public class SubsetSelectPanel extends SearchPanelResults {
		public static final long serialVersionUID = 1;
		ActionListener m_callback;
		java.util.List<SendObject> m_sendings = null;
		ShapeStruct [] m_shapes = null;
		
		@Override
		public void componentResized(ComponentEvent e) {
			doResize();
			super.componentResized(e);
		}

		public SubsetSelectPanel(String [] sz_columns, int [] n_width, boolean [] b_editable, Dimension dim, ActionListener callback, java.util.List<SendObject> sendings) {
			super(sz_columns, n_width, b_editable, dim, ListSelectionModel.SINGLE_SELECTION);
			m_callback = callback;
			m_sendings = sendings;
			start_search();
		}
		public SubsetSelectPanel(String [] sz_columns, int [] n_width, boolean [] b_editable, Dimension dim, ActionListener callback, ShapeStruct [] sendings) {
			super(sz_columns, n_width, b_editable, dim, ListSelectionModel.SINGLE_SELECTION);
			m_callback = callback;
			m_shapes = sendings;
			setPreferredSize(dim);
			start_search();
		}
		
		
		protected void start_search() {
			if(m_sendings!=null)
			{
				for(int i=0; i < m_sendings.size(); i++) {
					if(m_sendings.get(i)!=null) {
						if(((SendObject)m_sendings.get(i)).get_sendproperties().get_sendingname()!=null &&
								((SendObject)m_sendings.get(i)).get_sendproperties().get_sendingname().length()>=0) {
							Object [] data = { m_sendings.get(i), ((SendObject)m_sendings.get(i)).get_sendproperties().get_sendingname() };
							this.insert_row(data, -1);
						} else {
							Object [] data = { m_sendings.get(i), "[No name]" };
							this.insert_row(data, -1);
						}
					}
				}
			}
			else if(m_shapes!=null)
			{
				for(int i=0; i < m_shapes.length; i++)
				{
					Object [] data = { m_shapes[i], m_shapes[i].shapeName };
					this.insert_row(data, -1);
					//PAS.pasplugin.addShapeToPaint(m_shapes[i]);
				}
			}
		}
	
		protected void onMouseLClick(int n_row, int n_col, Object[] rowcontent, Point p) {
			//goto area and preview polygon
			 //ShapeStruct shape = (ShapeStruct)((SendObject)rowcontent[0]).get_sendproperties().typecast_poly().get_shapestruct();
			ShapeStruct shape = null;
			SendObject sendobject = null;
			PAS.pasplugin.removeShapeToPaint(currentSelection);
			if(rowcontent[0] instanceof ShapeStruct)
			{
				shape = (ShapeStruct)rowcontent[0];
			}
			else if(rowcontent[0] instanceof SendObject)
			{
				sendobject = (SendObject)rowcontent[0];
				shape = sendobject.get_sendproperties().get_shapestruct();
			}
			currentSelection = shape;
			PAS.pasplugin.addShapeToPaint(currentSelection);
			if(m_sendings!=null)
			{				
				try {
					String sz_desc = sendobject.get_sendproperties().get_description();
					m_lbl_info.setText(sz_desc);
					PAS.pasplugin.addShapeToPaint(shape);
					PAS.get_pas().actionPerformed(new ActionEvent(shape.calc_bounds(), ActionEvent.ACTION_PERFORMED, "act_map_goto_area"));
				} catch(Exception err) {
					log.debug(err.getMessage());
				}
			}
			else if(m_shapes!=null)
			{
				try
				{
					//ShapeStruct shape = (ShapeStruct)rowcontent[0];
					//PAS.get_pas().get_parmcontroller().addShapeToDrawQueue(shape);
					m_lbl_info.setText(shape.shapeName);
					PAS.get_pas().actionPerformed(new ActionEvent(shape.calc_bounds(), ActionEvent.ACTION_PERFORMED, "act_map_goto_area"));
					
				}
				catch(Exception e)
				{
				}
			}
			
		}
	
		protected void onMouseLDblClick(int n_row, int n_col, Object[] rowcontent, Point p) {
			ShapeStruct shape = null;
			SendObject sendobject = null;
			if(rowcontent[0] instanceof ShapeStruct)
			{
				shape = (ShapeStruct)rowcontent[0];
			}
			else if(rowcontent[0] instanceof SendObject)
			{
				sendobject = (SendObject)rowcontent[0];
				shape = sendobject.get_sendproperties().get_shapestruct();
			}

			if(m_sendings!=null)
			{
				try {
					PAS.get_pas().get_mappane().set_active_shape(shape);
					PAS.get_pas().get_parmcontroller().updateShape(shape);
					Variables.getMapFrame().setPaintModeBasedOnActiveShape(false);

					PAS.get_pas().actionPerformed(new ActionEvent(shape.calc_bounds(), ActionEvent.ACTION_PERFORMED, "act_map_goto_area"));
				} catch(Exception err) {
					
				}
			}
			else if(m_shapes!=null)
			{
				try
				{
					//PAS.get_pas().get_parmcontroller().clearDrawQueue();
					//PAS.get_pas().get_parmcontroller().addShapeToDrawQueue(shape);
					//PAS.get_pas().get_parmcontroller().updateShape(shape);
				}
				catch(Exception e)
				{
					
				}
				try
				{
					//PAS.get_pas().get_parmcontroller().addShapeToDrawQueue(shape);
					//PAS.get_pas().get_mappane().set_active_shape(shape);
					Variables.getMapFrame().set_active_shape(shape);
					m_callback.actionPerformed(new ActionEvent(shape, ActionEvent.ACTION_PERFORMED, "act_set_shape"));
					PAS.get_pas().actionPerformed(new ActionEvent(shape.calc_bounds(), ActionEvent.ACTION_PERFORMED, "act_map_goto_area"));
					m_callback.actionPerformed(new ActionEvent(shape, ActionEvent.ACTION_PERFORMED, "act_sending_selected"));
				}
				catch(Exception e)
				{
					
				}
			}
			//currentSelection = null; //don't clear the shape from drawqueue
			close();
		}
	
		protected void onMouseRClick(int n_row, int n_col, Object[] rowcontent, Point p) {
			
		}
	
		protected void onMouseRDblClick(int n_row, int n_col, Object[] rowcontent, Point p) {
			
		}
	
		protected void valuesChanged() {
			
		}
	
		public boolean is_cell_editable(int row, int col) {
			return false;
		}
		
	}


	@Override
	public void windowOpened(WindowEvent e) {
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		close();
	}

	@Override
	public void windowClosed(WindowEvent e) {
		close();
	}

	@Override
	public void windowIconified(WindowEvent e) {
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		
	}
	
	
}