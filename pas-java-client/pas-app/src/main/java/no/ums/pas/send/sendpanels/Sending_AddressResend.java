package no.ums.pas.send.sendpanels;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.core.defines.SearchPanelResults;
import no.ums.pas.core.defines.SearchPanelResults.TableList;
import no.ums.pas.localization.Localization;
import no.ums.pas.status.StatusCode;
import no.ums.pas.status.StatusCodeList;

import javax.swing.ListSelectionModel;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;



public class Sending_AddressResend extends Sending_AddressPanel implements ComponentListener {

    private static final Log log = UmsLog.getLogger(Sending_AddressResend.class);

	public static final long serialVersionUID = 1;
	
	private StatusCodeList m_statuscodes;
	private StatusCodeListView m_list;
	public StatusCodeListView get_statuscodes() { return m_list; }
	private int m_n_resend_refno;
	

	public Sending_AddressResend(SendWindow parent, StatusCodeList statuscodes, int n_refno) {
		super(null, parent);
		m_n_resend_refno = n_refno;
		m_statuscodes = statuscodes;
        String [] sz_columns = new String [] {Localization.l("main_status_code"), Localization.l("main_status_name"), Localization.l("main_sending_recipients"), "" };
		int [] n_width = new int [] { 50, 300, 50, 16 };
		boolean [] b_editable = new boolean [] { false, false, false, true };
		Dimension dim = new Dimension(500, 350);
		addComponentListener(this);
		m_list = new StatusCodeListView(sz_columns, n_width, b_editable, dim);
		m_list.start_search();
		add_controls();
	}

	public final void add_controls() {
		set_gridconst(0, inc_panels(), 1, 1);
		add(m_list, get_gridconst());
		get_statuscodes().get_table().setEnabled(false);
		init();
	}

	/*public void init() {
		add_controls();
		setVisible(true);
	}*/

	public void actionPerformed(ActionEvent e) {
		
	}
	public void exec_adrcount() {
		
	}
	
	public ArrayList<Object> get_checked() {
		TableList tl = get_statuscodes().get_tablelist();
		ArrayList<Object> resends = new ArrayList<Object>();
		for(int i=0;i<tl.getRowCount();i++) {
			Object temp = tl.getValueAt(i,3); 
			if(((Boolean)temp).booleanValue())
				resends.add(tl.getRowContent(i));
		}
		if(resends.size()==0)
			System.out.print("Resends = 0, no selected status");
		return resends;
	}
	
	
	public class StatusCodeListView extends SearchPanelResults {
		public static final long serialVersionUID = 1;
		private int m_n_objectcolumn = 0;
		private int m_n_includecolumn = 3;
		
		public StatusCodeListView(String [] sz_columns, int [] n_width, boolean [] b_editable, Dimension dim) {
			super(sz_columns, n_width, b_editable, dim, ListSelectionModel.SINGLE_SELECTION);
		}

		protected void start_search() {
			//fill list of statuscodes
			for(int i=0; i < m_statuscodes.size(); i++) {
				StatusCode item = m_statuscodes._get(i);
				if(!item.get_reserved()) { //don't include sending/pending/parsing
					Object [] obj = new Object [] { item, new String(item.get_status()==null?"":item.get_status()), new Integer(item.get_current_count()), new Boolean(false) };
					this.insert_row(obj, -1);
				}
			}
			
		}
		
		protected void checkStatus(int n_col, Object [] row) {
			if(n_col==m_n_includecolumn) {
				StatusCode item = (StatusCode)row[m_n_objectcolumn];
				try {
					if(((Boolean)row[n_col]).booleanValue()) {
						log.debug("Checked status " + item.get_code());
						parent.get_sendobject().get_sendproperties().addResendStatus(item);
					}
					else {
						log.debug("UnChecked status " + item.get_code());
						parent.get_sendobject().get_sendproperties().remResendStatus(item);
					}
				} catch(Exception e) {
					
				}
			}
		}

		protected void onMouseLClick(int n_row, int n_col, Object[] rowcontent, Point p) {
			//if(get_statuscodes().get_table().isEnabled())
				checkStatus(n_col, rowcontent);
		}

		protected void onMouseLDblClick(int n_row, int n_col, Object[] rowcontent, Point p) {
			if(get_statuscodes().get_table().isEnabled())
				checkStatus(n_col, rowcontent);			
		}

		protected void onMouseRClick(int n_row, int n_col, Object[] rowcontent, Point p) {
			
		}

		protected void onMouseRDblClick(int n_row, int n_col, Object[] rowcontent, Point p) {
			
		}

		protected void valuesChanged() {
			
		}

		public boolean is_cell_editable(int row, int col) {
			if(col == m_n_includecolumn)
				return true;
			return false;
		}
		
	}





	public void componentResized(ComponentEvent e) {
		m_list.setPreferredSize(new Dimension(getWidth(), getHeight()));
		m_list.setSize(getWidth(), getHeight()-40);
		revalidate();
	}

	public void componentMoved(ComponentEvent e) {
		
	}

	public void componentShown(ComponentEvent e) {
		
	}

	public void componentHidden(ComponentEvent e) {
		
	}
}