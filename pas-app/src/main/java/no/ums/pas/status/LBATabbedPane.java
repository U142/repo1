package no.ums.pas.status;

import no.ums.pas.core.defines.SearchPanelResults;
import no.ums.pas.localization.Localization;

import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

//import org.jvnet.substance.SubstanceTabbedPaneUI;


public class LBATabbedPane extends JTabbedPane implements ComponentListener, ChangeListener
{
	public static final long serialVersionUID = 1;
	private LBAListCC m_listcc;
	private LBAListCell m_listcell;
	//private ArrayList<LBAListCC> m_operators = new ArrayList<LBAListCC>();
	private Hashtable<Integer, LBAListCC> m_tab_hash = new Hashtable<Integer, LBAListCC>();
	
	public LBAListCC getListCC() { return m_listcc; }
	public LBAListCell getListCell() { return m_listcell; }
	final String [] colscc = new String[] {Localization.l("main_status_lba_country"), Localization.l("common_countrycode_short"), Localization.l("main_status_queue"), Localization.l("main_status_delivered"), Localization.l("main_status_failed"), Localization.l("main_status_expired"), Localization.l("main_status_subscribers"), Localization.l("common_progress")};
    final int [] widthcc = new int[] { 100, 40, 70, 70, 70, 70, 70, 100 };

	final String [] colscell = new String [] {Localization.l("main_status_lba_cellid"), Localization.l("main_status_queue"), Localization.l("main_status_delivered"), Localization.l("main_status_failed"), Localization.l("common_progress")};
    final int [] widthcell = new int[] { 120, 30, 30, 30, 200 };
	final LBATabbedPane tabbedpane = this;
	ActionListener callback = null;
	public void setCallback(ActionListener l)
	{
		callback = l;
	}
	
	public LBATabbedPane()
	{
		try
		{
			//this.setUI(new SubstanceTabbedPaneUI());
			m_listcc = new LBAListCC(colscc, widthcc, new Dimension(200,100));
			m_listcell = new LBAListCell(colscell, widthcell, new Dimension(200, 100));
            addTab(m_listcc, Localization.l("main_status_lba_nationalities"));
			this.addComponentListener(this);
			this.addChangeListener(this);
			//addTab(m_listcell, PAS.l("main_status_lba_nationalities"));
		}
		catch(Exception e)
		{
			
		}
		//this.addTab("Mobile Cells", null, m_listcell);
		//getTabComponentAt(1).setEnabled(false); //disable CELL info
	}
	
	protected void addTab(final SearchPanelResults list, final String name)
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
		
				tabbedpane.addTab(name, null, list);
				tabbedpane.addComponentListener(tabbedpane);
			}
		});		
	}
	
	public void clear()
	{
		m_listcc.clear();
		m_listcell.clear();
	}
	
	public void UpdateData(LBASEND o)
	{
		for(int i=0; i < o.hist_cc.size(); i++)
		{
			m_listcc.SetRow(o.hist_cc.get(i).getListRow(), 0);
		}
	}
	
	private void UpdateData(ArrayList<LBASEND.LBAHISTCC> cc, LBAListCC list)
	{
		for(int i=0; i < cc.size(); i++)
		{
			list.SetRow(cc.get(i).getListRow(), 0);
		}		
	}
	
	
	public void UpdateDataByOperator(ArrayList<LBASEND> operators)
	{
		//show separate tabs by operators
		for(int i=0; i < operators.size(); i++)
		{
			LBAListCC cclist = null;
			if(m_tab_hash.containsKey(operators.get(i).l_operator)) //update
			{
				cclist = m_tab_hash.get(operators.get(i).l_operator);
			}
			else
			{
				cclist = new LBAListCC(colscc, widthcc, new Dimension(200,100));
				cclist.operator = operators.get(i).l_operator;
				m_tab_hash.put(operators.get(i).l_operator, cclist);
				addTab(cclist, operators.get(i).sz_operator);
			}
			UpdateData(operators.get(i).hist_cc, cclist);//operators.get(i).hist_cc, cclist);
		}
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		
	}

	@Override
	public void componentMoved(ComponentEvent e) {		
	}

	@Override
	public void componentResized(ComponentEvent e) {
		int w = getWidth();
		int h = getHeight();
		//setPreferredSize(new Dimension(getWidth(), getHeight()));
		m_listcc.setPreferredSize(new Dimension(w-10, h-30));
		revalidate();
		m_listcc.revalidate();
	}

	@Override
	public void componentShown(ComponentEvent e) {	
	}
	Component last_selected_component = null;
	@Override
	public void stateChanged(ChangeEvent e) {
		JTabbedPane tabSource = (JTabbedPane) e.getSource();
		Component tab = tabSource.getSelectedComponent();
		if(tab.equals(last_selected_component))
			return;
		last_selected_component = tab;
		int operator = -1;
		if(m_tab_hash.containsValue(tab))
		{
			Enumeration<LBAListCC> cclist = m_tab_hash.elements();
			while(cclist.hasMoreElements())
			{
				operator = cclist.nextElement().operator;
				if(operator>=0)
				{
					if(m_tab_hash.get(operator).equals(tab))
					{
						//System.out.println("Show operator " + operator);
						break;
					}
				}
			}
		}
		/*if(operator==-1)
			System.out.println("Show all operators");*/
		if(callback!=null)
			//ask StatusSending to filter LBA status information
			callback.actionPerformed(new ActionEvent(new Integer(operator), ActionEvent.ACTION_PERFORMED, "act_lba_filter_by_operator"));
		
	}

}