package no.ums.pas.core.mainui;

import no.ums.pas.PAS;
import no.ums.pas.core.defines.SearchPanelResults;
import no.ums.pas.core.popupmenus.PUOpenStatus;
import no.ums.pas.status.LBASEND;
import no.ums.pas.status.StatusListObject;
import no.ums.pas.ums.tools.TextFormat;

import javax.swing.ListSelectionModel;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;


public class SearchPanelStatusList extends SearchPanelResults {
	
	public static final long serialVersionUID = 1;

	private PAS m_pas;
	private OpenStatusFrame m_statusframe;
	private PUOpenStatus m_popup;
	public OpenStatusFrame get_statusframe() { return m_statusframe; }
	private int n_refno_column = 1;
	
	public PAS get_pas() { return m_pas; }
	
	public SearchPanelStatusList(PAS pas, OpenStatusFrame statusframe, String [] sz_columns, int [] n_width)
	{
		super(sz_columns, n_width, null, new Dimension(800, 200), ListSelectionModel.SINGLE_SELECTION);
		m_statusframe = statusframe;
		m_popup = new PUOpenStatus(pas, PAS.l("common_open"), this);
	}
	protected void valuesChanged() { }

	protected void start_search()
	{
	}
	protected void onMouseLClick(int n_row, int n_col, Object [] rowcontent, Point p)
	{
	}
	protected void onMouseLDblClick(int n_row, int n_col, Object [] rowcontent, Point p)
	{
		//Point mouse = this.getMousePosition();
		//m_popup.pop(this, mouse, (StatusListObject)rowcontent[2]);
		//openStatus(true, ((StatusListObject)rowcontent[2]).get_project().get_projectpk(), -1);
		openStatus(true, (StatusListObject)rowcontent[2], -1);
		//get_statusframe().get_controller().retrieve_statusitems(get_statusframe(), new Integer((String)rowcontent[2]).intValue(), true /*init*/);
			
		//get_statusframe().get_controller().retrieve_statusitems(get_statusframe(), ((StatusListObject)rowcontent[2]).get_project().get_projectpk(), ((StatusListObject)rowcontent[2]).get_refno(), true /*init*/);
	}
	protected void onMouseRClick(int n_row, int n_col, Object [] rowcontent, Point p)
	{
		m_tbl.getSelectionModel().setSelectionInterval(n_row, n_row);
		Point mouse = this.getMousePosition();
		m_popup.pop(this, mouse, (StatusListObject)rowcontent[2]);
	}
	protected void onMouseRDblClick(int n_row, int n_col, Object [] rowcontent, Point p)
	{
	}
	public void fill(ArrayList<StatusListObject> arr_statuslist) {
		String sz_statustext = "";
		for(int i=0; i < arr_statuslist.size(); i++) {
			StatusListObject obj = (StatusListObject)arr_statuslist.get(i);
			if(obj.get_type()==4 || obj.get_type()==5)//LBA or TAS
				sz_statustext = LBASEND.LBASTATUSTEXT(obj.get_sendingstatus());
			else
				sz_statustext = TextFormat.get_statustext_from_code(obj.get_sendingstatus(), obj.get_altjmp());
			Object sz_visible[] = { obj.get_project().get_projectname(), obj.get_deptid(), obj, obj.getChannel(), Integer.toString(obj.get_totitem()), obj.get_groupdesc(),
					TextFormat.format_date(obj.get_createdate()), TextFormat.format_time(obj.get_createtime(),4), 
					obj.get_sendingname(), sz_statustext/*new Integer(obj.get_sendingstatus()).toString()*/ };
			this.insert_row(sz_visible, -1);
		}
	}

	void onDownloadFinished()
	{
		
	}
	public void openStatus(final boolean b_project, final StatusListObject proj, final int n_refno) {
		new Thread("Open status list thread")
		{
			public void run()
			{
				try
				{
					m_statusframe.setVisible(false);
					PAS.pasplugin.onCloseProject();
					PAS.pasplugin.onOpenProject(proj.get_project(), -1);
					PAS.get_pas().close_active_project(true, false);
					get_statusframe().get_controller().retrieve_statusitems(get_statusframe(), proj.get_project().get_projectpk(), n_refno, true /*init*/);
					//PAS.get_pas().get_eastcontent().flip_to(EastContent.PANEL_STATUS_LIST);
				}
				catch(Exception e)
				{
					
				}
			}
		}.start();
	}
	public void openStatus(final boolean b_project, final String sz_projectpk, final int n_refno) {
		new Thread("Open status list thread")
		{
			public void run()
			{
				try
				{
					m_statusframe.setVisible(false);
					PAS.get_pas().close_active_project(true, false);
					get_statusframe().get_controller().retrieve_statusitems(get_statusframe(), (b_project ? sz_projectpk : "-1"), n_refno, true /*init*/);
				}
				catch(Exception e)
				{
					
				}
			}
		}.start();
		
	}
	public boolean is_cell_editable(int row, int col) {
		return is_cell_editable(col);
	}	
/*    public void set_custom_cellrenderer(TableColumn column, int n_col)
    {
    	if(n_col==0 || n_col==6)
    		column.setCellRenderer(new TblCellColor());
    }*/	
}