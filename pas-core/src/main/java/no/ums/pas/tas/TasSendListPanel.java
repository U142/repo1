package no.ums.pas.tas;

import no.ums.pas.PAS;
import no.ums.pas.core.defines.DefaultPanel;
import no.ums.pas.core.defines.tree.TreeRenderer;
import no.ums.pas.core.defines.tree.TreeTable;
import no.ums.pas.core.defines.tree.UMSTree;
import no.ums.pas.core.defines.tree.UMSTreeNode;
import no.ums.pas.tas.treenodes.CommonTASListItem;
import no.ums.pas.tas.treenodes.CountryListItem;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Hashtable;




public class TasSendListPanel extends DefaultPanel implements ComponentListener
{
	JScrollPane scroller;
	CountryList list;
	ActionListener callback;
	Hashtable<String, CountryListItem> hash_tree = new Hashtable<String, CountryListItem>();
	public TasSendListPanel(ActionListener callback)
	{
		super();
		addComponentListener(this);
		setVisible(false);
		this.callback = callback;
		String [] cols = new String [] { PAS.l("common_country"), PAS.l("touristscount") };
		int [] width = new int [] { 150, 50 };
		boolean [] edit = new boolean [] { false, false };
		Dimension dim = new Dimension(300, 100);
		//list = new CountryList(cols, width, edit, dim);
		list = new CountryList();
		scroller = new JScrollPane(list);
		add_controls();
		init();
		this.setBorder(no.ums.pas.ums.tools.TextFormat.CreateStdBorder(PAS.l("main_tas_sendlist")));
	}
	
	public void CancelSendlist()
	{
		
		setVisible(false);
	}
	
	public void addCountry(CountryListItem i)
	{
		CountryListItem newitem = (CountryListItem)i.clone();
		hash_tree.put(newitem.getCountry().getSzIso().trim(), newitem);
		newitem.Search("");
		list.top.add(newitem);
		list.model.nodeStructureChanged(list.top);
		i.setAddedToSendList(true);
		newitem.setAddedToSendList(true);
		this.setBorder(no.ums.pas.ums.tools.TextFormat.CreateStdBorder(PAS.l("main_tas_sendlist") + " " + list.model.getChildCount(list.top) + " " + PAS.l("common_countries")));
	}
	public void removeCountry(CountryListItem i)
	{
		//CountryListItem newitem = (CountryListItem)i.clone();
		CountryListItem toremove = hash_tree.get(i.getCountry().getSzIso().trim());
		if(toremove==null)
			return;
		hash_tree.remove(i.getCountry().getSzIso().trim());
		list.top.remove(toremove);
		list.model.nodeStructureChanged(list.top);
		/*list.top.remove(toremove);
		list.model.nodeStructureChanged(list.top);
		i.setAddedToSendList(false);
		toremove.setAddedToSendList(false);*/
		callback.actionPerformed(new ActionEvent(i, ActionEvent.ACTION_PERFORMED, "act_removed_from_sendlist"));
		this.setBorder(no.ums.pas.ums.tools.TextFormat.CreateStdBorder(PAS.l("main_tas_sendlist") + " " + list.model.getChildCount(list.top) + " " + PAS.l("common_countries")));
		if(hash_tree.size()==0)
			callback.actionPerformed(new ActionEvent(i, ActionEvent.ACTION_PERFORMED, "act_sendlist_cleared"));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if("act_remove_selected_country".equals(e.getActionCommand()))
		{
			UMSTreeNode node = list.getSelectedNode();
			if(node==null)
				return;
			if(node.getClass().equals(CountryListItem.class))
				removeCountry((CountryListItem)node);
		}
	}

	@Override
	public void add_controls() {
		set_gridconst(0, inc_panels(), 1, 1);
		add(scroller, m_gridconst);
	}

	@Override
	public void init() {
	}
	
	@Override
	public void componentResized(ComponentEvent e) {
		if(getWidth()<=0 || getHeight()<=0)
			return;
		int w = getWidth();
		int h = getHeight();
		scroller.setPreferredSize(new Dimension(w-20, h-30));
		scroller.revalidate();
		super.componentResized(e);
	}
	
	public class CountryList extends UMSTree
	{
		public CountryList()
		{
			super(TasSendListPanel.this, 5000);
			JMenuItem remove = new JMenuItem("main_tas_remove");
			remove.addActionListener(TasSendListPanel.this);
			remove.setActionCommand("act_remove_selected_country");
			this.popup.add(remove);
		}

		@Override
		public UMSTreeNode getSelectedNode() {
			return (CountryListItem)selected_node;
		}

		@Override
		public void InitRenderer() {
			try
			{
				final String [] cols = new String [] {"", PAS.l("main_tas_panel_table_heading_tourists"), PAS.l("main_tas_panel_table_heading_updated") };
				final int [] width = new int [] { 250, 80, 150 };
				final boolean [] b_editable = new boolean [] { false, false, false };
	
				TreeTable table = new TreeTable(cols, width, b_editable, new Dimension(300, 100))
				{
					@Override
					public void set_custom_cellrenderer(TableColumn column, final int n_col) {
						column.setCellRenderer(new DefaultTableCellRenderer() {
							public static final long serialVersionUID = 1;
						    public Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
						        Component renderer =  super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);    //---
				        		renderer.setFont(new Font(UIManager.getString("Common.Fontface"), Font.BOLD, 11));
					        	if(n_col==2)
					        	{
					        		CommonTASListItem item = (CommonTASListItem)m_tbl_list.getValueAt(row, 0);
					        		
					        		TableDateFormatter date = (TableDateFormatter)m_tbl_list.getValueAt(row, n_col);
					        		renderer.setForeground(item.getOutdatedColor());
					        	}
					        	else if(n_col==0)
					        	{
					        		CountryListItem item = (CountryListItem)m_tbl_list.getValueAt(row, 0);
					        		return item.lbl;
					        	}
					        	else
					        	{
					        		renderer.setForeground(Color.black);
					        	}

						        return renderer;
						    }
						});
					}
				};
				cr = new TreeRenderer(this, table) 
				{
					@Override
					public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
							boolean leaf, int row, boolean hasFocus){
						//super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
						if(node.getClass().equals(CountryListItem.class))
						{
							CountryListItem c = (CountryListItem) value;
							lbl.setText(c.getCountry().getSzName());
							lbl.setFont(new Font(UIManager.getString("Common.Fontface"), Font.BOLD, 14));
							Object [] data = new Object [] { c, c.getCountry().getNTouristcount()+"", m_tbl_list.newTableDateFormatter(c.getCountry().getNOldestupdate())/*UMS.Tools.TextFormat.format_datetime(c.getNOldestupdate()) */};
							boolean [] update = new boolean[] { true, true, true };
							m_tbl_list.edit_row(data, 0, update);
							return m_tbl_list.m_tbl;

						}
						else
							this.setText(node.toString());
						return this;
					}
					@Override
					protected void initTable(TreeTable table)
					{
						String [] data = new String [] { " ", " ", " " };
						table.m_tbl.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
						table.insert_row(data, 0);
						table.m_tbl.setBackground(new Color(0,0,0,0));
						table.m_tbl.setOpaque(false);
						table.m_tbl.getTableHeader().setBackground(new Color(0,0,0,0));
						table.m_tbl.getTableHeader().setResizingAllowed(true);
						table.m_tbl.getTableHeader().setOpaque(false);
						table.m_tbl.setIntercellSpacing(new Dimension(0,0));
						table.m_tbl.setShowGrid(false);
						table.revalidate();
						super.initTable(table);
						m_tbl_list.m_tbl.setShowGrid(true);
					}
					int colwidths [] = new int [] { 250, 80, 50 };
					
					@Override
					public void setPreferredSize(Dimension d)
					{
						TableColumn col1 = m_tbl_list.m_tbl.getColumnModel().getColumn(0);
						TableColumn col2 = m_tbl_list.m_tbl.getColumnModel().getColumn(1);
						TableColumn col3 = m_tbl_list.m_tbl.getColumnModel().getColumn(2);
						col1.setWidth(colwidths[0]);
						col2.setWidth(colwidths[1]);
						int w = totalwidth;//getWidth();
						col3.setWidth(w-colwidths[0]-colwidths[1]);
						col1.setResizable(false);
						col2.setResizable(false);
						col3.setResizable(false);
					}
				};
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			
		}
	}

	/*public class CountryList extends SearchPanelResults
	{
		public CountryList(String [] sz_columns, int [] n_width, boolean [] b_editable, Dimension dim)
		{
			super(sz_columns, n_width, b_editable, dim);
		}

		@Override
		public boolean is_cell_editable(int row, int col) {
			return false;
		}

		@Override
		protected void onMouseLClick(int n_row, int n_col, Object[] rowcontent,
				Point p) {
			
		}

		@Override
		protected void onMouseLDblClick(int n_row, int n_col,
				Object[] rowcontent, Point p) {
			
		}

		@Override
		protected void onMouseRClick(int n_row, int n_col, Object[] rowcontent,
				Point p) {
			
		}

		@Override
		protected void onMouseRDblClick(int n_row, int n_col,
				Object[] rowcontent, Point p) {
			
		}

		@Override
		protected void start_search() {
			
		}

		@Override
		protected void valuesChanged() {
			
		}
		
	}*/
}