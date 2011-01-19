package no.ums.pas.status;

import no.ums.pas.core.ws.WSMaxAlloc;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.ws.pas.UMAXALLOC;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;


public class StatusSendingList extends ArrayList<StatusSending> implements ActionListener {
	public static final long serialVersionUID = 1;
	ActionListener m_statuspanel;
	
	public StatusSendingList() {
		super();
	}
	public void add_sending(StatusSending sending, ActionListener callback) {
		if(!update_sending(sending)) {
			try
			{
				sending.set_sendinglist(this);
				add(sending);
				sending.init_ui();
				m_statuspanel = callback;
				callback.actionPerformed(new ActionEvent(sending, ActionEvent.ACTION_PERFORMED, "act_add_sending"));
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	public void clear() {
		for(int i=0; i < this.size(); i++) {
			m_statuspanel.actionPerformed(new ActionEvent(get_sending(i), ActionEvent.ACTION_PERFORMED, "act_rem_sending"));
			get_sending(i).destroy_ui();
		}
		super.clear();
	}
	public StatusSending get_sending(int idx) {
		return (StatusSending)get(idx);
	}
	protected boolean update_sending(StatusSending sending) {
		StatusSending current;
		for(int i=0; i < size(); i++) {
			current = get_sending(i);
			if(current.get_refno() == sending.get_refno()) {
				current.set_values(sending);
				return true;
			}
		}
		return false;
	}
	/*public boolean updateCellBroadcast(StatusCellBroadcast cell) {
		StatusSending current;
		for(int i=0; i < size(); i++) {
			current = get_sending(i);
			if(current.get_refno() == cell.get_parent_refno()) {
				current.setCellBroadcast(cell);
				return true;
			}
		}
		return false;
	}*/
	
	public void ClearAllLBA()
	{
		for(int i=0; i < size(); i++)
		{
			final StatusSending current = get_sending(i);
			current.ResetLbaByOperator();
		}
	}
	public boolean updateLBA_2_0(LBASEND lba)
	{
		final LBASEND templba = lba;
		for(int i=0; i < size(); i++)
		{
			final StatusSending current = get_sending(i); //find correct parent sending
			if(current.get_refno() == lba.n_parentrefno)
			{
				try
				{
					if(templba.languages!=null)
						current.setLbaLanguages(templba.languages);
					current.addLbaOperator(templba);
				}
				catch(Exception e)
				{
					
				}
				//lba.CalcStatistics();
				//return true;
				break;
			}
		}
		
		//current.setLBA(templba);
		return false;

	}
	
	public void finalizeLBA_2_0()
	{
		//all LBA info is set, now calc totals
		for(int i=0; i < size(); i++)
		{
			final StatusSending current = get_sending(i);
			current.CalcLbaTotalsFromOperators();
		}		
	}
	public boolean updateLBA(LBASEND lba)
	{
		final LBASEND templba = lba;
		for(int i=0; i < size(); i++)
		{
			final StatusSending current = get_sending(i); //find correct parent sending
			if(current.get_refno() == lba.n_parentrefno)
			{
				try
				{
					//SwingUtilities.invokeAndWait(new Runnable() 
					//{
					//	public void run()
						{
							current.setLBA(templba);
							
						}
					//});
				}
				catch(Exception e)
				{
					
				}
				//lba.CalcStatistics();
				return true;
			}
		}
		return false;
	}
	public void set_maxalloc(String sz_projectpk, int n_refno, int n_maxalloc, ActionListener callback) {
		try {
			/*HttpPostForm form = new HttpPostForm(PAS.get_pas().get_sitename() + "PAS_set_maxalloc.asp");
			form.setParameter("l_projectpk", sz_projectpk);
			form.setParameter("l_refno", new Integer(n_refno).toString());
			form.setParameter("l_maxalloc", new Integer(n_maxalloc).toString());
			
			XMLSetMaxAlloc xml = new XMLSetMaxAlloc(form, Thread.NORM_PRIORITY, callback, "act_max_alloc_set");
			xml.start();*/
			UMAXALLOC max = new UMAXALLOC();
			max.setNMaxalloc(n_maxalloc);
			max.setNProjectpk(Long.parseLong(sz_projectpk));
			max.setNRefno(n_refno);
			new WSMaxAlloc(max, callback, "act_max_alloc_set").start();
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			Error.getError().addError("StatusSendingList","Exception in set_maxalloc",e,1);
		}
		
	}
	public void actionPerformed(ActionEvent e) {
	}
}