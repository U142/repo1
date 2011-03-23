package no.ums.pas.core.mainui;

import no.ums.pas.PAS;
import no.ums.pas.core.Variables;
import no.ums.pas.core.defines.SearchPanelResults;
import no.ums.pas.core.logon.DeptInfo;
import no.ums.pas.core.popupmenus.PUOpenStatus;
import no.ums.pas.icons.ImageFetcher;
import no.ums.pas.localization.Localization;
import no.ums.pas.status.LBASEND;
import no.ums.pas.status.StatusListObject;
import no.ums.pas.ums.tools.TextFormat;
import no.ums.ws.common.UDELETESTATUSRESPONSE;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.SystemColor;
import java.util.ArrayList;
import no.ums.pas.core.ws.WSDeleteStatus.IDeleteStatus;


public class SearchPanelStatusList extends SearchPanelResults {
	
	public static final long serialVersionUID = 1;

	private PAS m_pas;
	private OpenStatusFrame m_statusframe;
	private PUOpenStatus m_popup;
	public OpenStatusFrame get_statusframe() { return m_statusframe; }
	private final int n_refno_column = 2;
	private final int n_delete_column = 11;
	
	public PAS get_pas() { return m_pas; }
	
	public SearchPanelStatusList(PAS pas, OpenStatusFrame statusframe, String [] sz_columns, int [] n_width)
	{
		super(sz_columns, n_width, null, new Dimension(800, 200), ListSelectionModel.SINGLE_SELECTION);
		SearchPanelStatusList.this.get_table().setRowHeight(24);
		m_statusframe = statusframe;
        m_popup = new PUOpenStatus(pas, Localization.l("common_open"), this);
	}
	@Override
	public void set_custom_cellrenderer(TableColumn column, int n_col) {
		if(n_col==n_delete_column)
		{
			column.setCellRenderer(new DefaultTableCellRenderer() {
				JButton del = new JButton(ImageFetcher.getIcon("delete_16.png"));
				@Override
				public Component getTableCellRendererComponent(JTable table, Object value,
						boolean isSelected, boolean hasFocus, int row, int column) {
					if(value instanceof StatusListObject)
					{
						StatusListObject obj = (StatusListObject)value;
						if(statusMayBeDeleted(obj))
						{
							ImageIcon ico = ImageFetcher.getIcon("delete_16.png");
							setIcon(ico);
							this.setHorizontalAlignment(SwingConstants.CENTER);
						}
						else
						{
							setIcon(null);
							setText("");
						}
						this.setBackground(getBgColorForRow(row));
						this.setForeground(getFgColorForRow(row));
					}
					else
					{
						setIcon(null);
						setText("");
					}
					return this;
				}
			});
		}
		super.set_custom_cellrenderer(column, n_col);
	}

	protected void valuesChanged() { }

	protected void start_search()
	{
	}
	protected boolean statusMayBeDeleted(StatusListObject s)
	{
		//check if user is member of dept and that membership allows to delete (status>=2)
		for(DeptInfo di : Variables.getUserInfo().get_departments())
		{
			if(di.get_deptpk()==s.get_deptpk())
			{
				if(di.get_userprofile().get_status()<2)
				{
					return false;
				}
			}
		}
		boolean b_ret = false;
		switch(s.get_type())
		{
		case 1: //voice
		case 2: //sms
		case 3: //email
			if(s.get_sendingstatus() <= 0 || s.get_sendingstatus()>=7)
				b_ret = true;
			break;
		case 4: //lba
		case 5: //tas
			b_ret = LBASEND.HasFinalStatus(s.get_sendingstatus());
			break;
		}
		return b_ret;
	}
	protected void onMouseLClick(final int n_row, int n_col, final Object [] rowcontent, Point p)
	{
		if(n_col==n_delete_column)
		{
			if(rowcontent[n_delete_column] instanceof StatusListObject &&
				statusMayBeDeleted((StatusListObject)rowcontent[n_delete_column]) && JOptionPane.showConfirmDialog(this, Localization.l("common_are_you_sure"), Localization.l("common_are_you_sure"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION)
			{
				System.out.println("Delete status");
				PAS.pasplugin.onDeleteStatus((long)((StatusListObject)rowcontent[n_delete_column]).get_refno(),
						new IDeleteStatus() {							
							@Override
							public void Complete(long refno, UDELETESTATUSRESPONSE response) {
								if(response.equals(UDELETESTATUSRESPONSE.OK))
								{
									delete_row(rowcontent[n_delete_column], n_delete_column, StatusListObject.class);
								}
							}
						});
			}
		}
	}
	protected void onMouseLDblClick(int n_row, int n_col, Object [] rowcontent, Point p)
	{
		openStatus(true, (StatusListObject)rowcontent[2], -1);
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
			String sz_simulation = "Unknown";
			switch(obj.get_simulation())
			{
			case 0:
				sz_simulation = Localization.l("common_live");
				break;
			case 1:
				sz_simulation = Localization.l("common_simulated");
				break;
			case 2:
				sz_simulation = Localization.l("common_test");
				break;
			case 4:
				sz_simulation = Localization.l("common_silent");
				break;
			}
			JButton del = new JButton(ImageFetcher.getIcon("delete_24.png"));
			Object sz_visible[] = { 
					obj.get_project().get_projectname(), 
					obj.get_deptid(), 
					obj, 
					obj.getChannel(), 
					sz_simulation, 
					Integer.toString(obj.get_totitem()), 
					obj.get_groupdesc(),
					TextFormat.format_date(obj.get_createdate()), 
					TextFormat.format_time(obj.get_createtime(),4), 
					obj.get_sendingname(), 
					sz_statustext, 
					obj};
			this.insert_row(sz_visible, -1, false);
		}
		this.get_tablelist().fireTableDataChanged();
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