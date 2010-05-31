package no.ums.pas.tas.treenodes;

import javax.swing.tree.DefaultTreeModel;

import no.ums.pas.core.defines.tree.UMSTreeNode;
import no.ums.pas.tas.TasPanel;
import no.ums.ws.pas.tas.UTASREQUESTRESULTS;

public class RequestLogItem extends CommonTASListItem implements Comparable<UMSTreeNode>
{
	public long n_clitimestamp_updated = 0;
	UTASREQUESTRESULTS res;
	public RequestLogItem(UTASREQUESTRESULTS r, DefaultTreeModel model)
	{
		super(r.getNRequestpk(), r, model);
		res = r;
		this.setUserObject(res);
		n_clitimestamp_updated = no.ums.pas.ums.tools.Utils.get_current_datetime();
	}
	public void Update(UTASREQUESTRESULTS r)
	{
		this.res = r;
		n_clitimestamp_updated = no.ums.pas.ums.tools.Utils.get_current_datetime();
	}
	public long GetLocalAgeSec()
	{
		return no.ums.pas.ums.tools.TextFormat.datetime_diff_seconds(n_clitimestamp_updated);
	}
	public long GetServerAgeSec()
	{
		return no.ums.pas.ums.tools.TextFormat.datetime_diff_seconds(res.getNTimestamp(), TasPanel.SERVER_CLOCK);
		//return UMS.Tools.TextFormat.datetime_diff_seconds(res.getNTimestamp());
	}
	public UTASREQUESTRESULTS getTasResult()
	{
		return res;
	}
	@Override
	public int compareTo(UMSTreeNode o1) {
		//return this.toString().toUpperCase().compareTo(o1.toString().toUpperCase());
		//return getCountry().getSzName().toUpperCase().compareTo(o1.getCountry().getSzName().toUpperCase());
		//return (new Integer(getTasResult().getNRequestpk()).compareTo(o1.getTasResult().getNRequestpk()));
		RequestLogItem i1 = (RequestLogItem)o1;
		return (getTasResult().getNTimestamp()>i1.getTasResult().getNTimestamp() ? -1 : 1);
		
	}
}