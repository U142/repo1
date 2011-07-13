package no.ums.pas.core.mainui;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.core.defines.SearchPanelResults;
import no.ums.pas.core.popupmenus.PUOpenStatus;
import no.ums.pas.core.project.Project;
import no.ums.pas.core.ws.WSDeleteStatus.IDeleteStatus;
import no.ums.pas.icons.ImageFetcher;
import no.ums.pas.localization.Localization;
import no.ums.pas.status.StatusListObject;
import no.ums.pas.ums.tools.TextFormat;
import no.ums.pas.ums.tools.Utils;
import no.ums.ws.common.UDeleteStatusResponse;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;


public class SearchPanelStatusList extends SearchPanelResults {

    private static final Log log = UmsLog.getLogger(SearchPanelStatusList.class);

	public static final long serialVersionUID = 1;

	private PAS m_pas;
	private OpenStatusFrame m_statusframe;
	private PUOpenStatus m_popup;
	public OpenStatusFrame get_statusframe() { return m_statusframe; }
	private final int OBJECT_COLUMN = 9;
	public static final int DELETE_COLUMN = 11;
	
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
		final ImageIcon ico_delete = ImageFetcher.getIcon("delete_16.png");
		final ImageIcon cant_delete = ImageFetcher.makeGrayscale(ico_delete);
		if(n_col==DELETE_COLUMN)
		{
			column.setCellRenderer(new DefaultTableCellRenderer() {
				//JButton del = new JButton(ImageFetcher.getIcon("delete_16.png"));
				@Override
				public Component getTableCellRendererComponent(JTable table, Object value,
						boolean isSelected, boolean hasFocus, int row, int column) {
					if(value instanceof StatusListObject)
					{
						StatusListObject obj = (StatusListObject)value;
						if(obj.statusMayBeDeleted()==UDeleteStatusResponse.OK)
						{
							//ImageIcon ico = ImageFetcher.getIcon("delete_16.png");
							setIcon(ico_delete);
							this.setHorizontalAlignment(SwingConstants.CENTER);
						}
						else
						{
							setIcon(cant_delete);
							this.setHorizontalAlignment(SwingConstants.CENTER);
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
	protected void onMouseLClick(final int n_row, int n_col, final Object [] rowcontent, Point p)
	{
		if(n_col==DELETE_COLUMN)
		{
			StatusListObject slo = ((StatusListObject)rowcontent[DELETE_COLUMN]);
			if(rowcontent[DELETE_COLUMN] instanceof StatusListObject &&
					slo.statusMayBeDeleted()==UDeleteStatusResponse.OK && JOptionPane.showConfirmDialog(this, Localization.l("main_status_delete_are_you_sure"), Localization.l("common_are_you_sure"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION)
			{
				log.debug("Delete status");
				PAS.pasplugin.onDeleteStatus((long)((StatusListObject)rowcontent[DELETE_COLUMN]).get_refno(),
						new IDeleteStatus() {	
							@Override
							public void Complete(long refno, UDeleteStatusResponse response) {
								if(response.equals(UDeleteStatusResponse.OK))
								{
									delete_row(rowcontent[DELETE_COLUMN], DELETE_COLUMN, StatusListObject.class);
								}
							}
						});
			}
		}
	}
	protected void onMouseLDblClick(int n_row, int n_col, Object [] rowcontent, Point p)
	{
		openStatus(true, (StatusListObject)rowcontent[OBJECT_COLUMN], -1);
	}
	protected void onMouseRClick(int n_row, int n_col, Object [] rowcontent, Point p)
	{
		m_tbl.getSelectionModel().setSelectionInterval(n_row, n_row);
		Point mouse = this.getMousePosition();
		m_popup.pop(this, mouse, (StatusListObject)rowcontent[OBJECT_COLUMN]);
	}
	protected void onMouseRDblClick(int n_row, int n_col, Object [] rowcontent, Point p)
	{
	}
	public void fill(ArrayList<StatusListObject> arr_statuslist) {
		String sz_statustext = "";
		for(int i=0; i < arr_statuslist.size(); i++) {
			StatusListObject obj = (StatusListObject)arr_statuslist.get(i);
			JButton del = new JButton(ImageFetcher.getIcon("delete_24.png"));
			int messagelength = 1;
			String totalitems = Integer.toString(obj.get_totitem());
			Integer.toString(obj.get_totitem());
			
			if(obj.get_type() == 2) {// SMS for å vise antall(faktisk antall sms)
				StringBuffer sb = new StringBuffer();
				sb.append(obj.get_totitem());
				sb.append(" (" + Integer.toString(obj.get_totitem() * (int)Math.ceil((double)(Utils.get_gsmsize(obj.get_messagetext())/(double)(152)))) + ")");
				totalitems = sb.toString();
			}
			
			Object sz_visible[] = { 
				obj.get_project().get_projectname(), 
				obj.get_deptid(), 
				obj.get_refno(), 
				obj.getChannel(), 
				obj.getSimulationText(), 
				totalitems, 
				obj.get_groupdesc(),
				TextFormat.format_date(obj.get_createdate()), 
				TextFormat.format_time(obj.get_createtime(),4), 
				obj, 
				obj.getStatusText(), 
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
					//PAS.pasplugin.onCloseProject();
					PAS.get_pas().askAndCloseActiveProject(new no.ums.pas.PAS.IAskCloseStatusComplete() {
						
						@Override
						public void Complete(boolean bStatusClosed) {
							if(bStatusClosed)
							{
								PAS.pasplugin.onOpenProject(proj.get_project(), -1);
								get_statusframe().get_controller().retrieve_statusitems(get_statusframe(), proj.get_project().get_projectpk(), n_refno, true /*init*/);							
							}
						}
					});
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
					PAS.get_pas().askAndCloseActiveProject(new no.ums.pas.PAS.IAskCloseStatusComplete() {
						
						@Override
						public void Complete(boolean bStatusClosed) {
							if(bStatusClosed)
							{
								m_statusframe.setVisible(false);
								//PAS.pasplugin.onCloseProject();
								Project proj = new Project();
								proj.set_projectpk(sz_projectpk);
								PAS.pasplugin.onOpenProject(proj, -1);
								get_statusframe().get_controller().retrieve_statusitems(get_statusframe(), (b_project ? sz_projectpk : "-1"), n_refno, true /*init*/);
							}							
						}
					});
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