package no.ums.pas.tas.treenodes;

import no.ums.pas.core.defines.tree.UMSTreeNode;
import no.ums.pas.tas.TasHelpers;
import no.ums.ws.common.ArrayOfUTOURISTCOUNT;
import no.ums.ws.common.ULBACOUNTRY;
import no.ums.ws.common.UTOURISTCOUNT;

import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeModel;
import java.awt.Color;
import java.awt.Font;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

public class CountryListItem extends CommonTASListItem implements Comparable<UMSTreeNode>
{
	@Override
	public int compareTo(UMSTreeNode o1) {
		//return this.toString().toUpperCase().compareTo(o1.toString().toUpperCase());
		if(o1.getClass().equals(CountryListItem.class))
		{
			CountryListItem i1 = (CountryListItem)o1;
			return getCountry().getSzName().toUpperCase().compareTo(i1.getCountry().getSzName().toUpperCase());
		}
		return 0;
	}
	public ContinentListItem parent = null;
	public JTextArea lbl = null;
	ULBACOUNTRY country;
	public ULBACOUNTRY getCountry() { return country; }
	public CountryListItem(ULBACOUNTRY c, ContinentListItem parent, DefaultTreeModel model)
	{
		super(c.getLCc(), c, model);
		country = c;
		this.setUserObject(c);
		this.parent = parent;
		//initTable();
	}
	@Override
	public Color getOutdatedColor()
	{
		return TasHelpers.getOutdatedColor(this.country);
	}
	@Override
	public String toString()
	{
		return country.getSzName();
	}
	@Override
	public boolean canRunCountRequest()
	{
		return !getCountInProgress();
	}
	
	@Override
	public void setAddedToSendList(boolean b) {
		if(b)
		{
			lbl.setFont(new Font(UIManager.getString("Common.Fontface"), Font.BOLD, 11));
		}
		else
			lbl.setFont(new Font(UIManager.getString("Common.Fontface"), Font.PLAIN, 11));
		super.setAddedToSendList(b);
		model.nodeChanged(this);
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CountryListItem that = (CountryListItem) o;

        return !(country != null ? !country.getSzIso().equals(that.country.getSzIso()) : that.country != null);

    }

    @Override
    public int hashCode() {
        return country != null ? country.getSzIso().hashCode() : 0;
    }

    public boolean hasChanged(ULBACOUNTRY c)
	{
		if(getCountry().getNTouristcount()!=c.getNTouristcount())
		{
			return true;
		}
		if(!getCountry().getSzIso().equals(c.getSzIso().trim()))
			return true;
		if(!getCountry().getSzName().equals(c.getSzName()))
			return true;
		if(getCountry().getNNewestupdate()!=c.getNNewestupdate())
		{
			return true;
		}
		if(getCountry().getNOldestupdate()!=c.getNOldestupdate())
			return true;
		return false;
	}
	public void setVisible(boolean b)
	{
		boolean b_changed = false;
		if(b!=this.b_visible)
		{
			b_changed = true;
			this.b_visible = b;
		}			
		if(b_changed)
			model.nodeChanged(this);
	}
	public boolean Update(ULBACOUNTRY c)
	{
		boolean b_changed = false;
		if(hasChanged(c))
		{
			//getCountry().setNTouristcount(c.getNTouristcount()); //(int)(Math.random() * 1001)); //country.getNTouristcount());
			getCountry().setSzName(c.getSzName());
			getCountry().setSzIso(c.getSzIso().trim());
			// All this to find the oldest timestamp
			Hashtable<Integer, Long> timestamp = new Hashtable<Integer, Long>();
			
			for(int i = 0;i<getCountry().getOperators().getUTOURISTCOUNT().size();++i) {
				timestamp.put(getCountry().getOperators().getUTOURISTCOUNT().get(i).getLOperator(),getCountry().getOperators().getUTOURISTCOUNT().get(i).getLLastupdate());
			}
			
			for(int i = 0;i<c.getOperators().getUTOURISTCOUNT().size();++i) {
				timestamp.put(c.getOperators().getUTOURISTCOUNT().get(i).getLOperator(),c.getOperators().getUTOURISTCOUNT().get(i).getLLastupdate());
			}
			
			long newtimestamp = c.getNOldestupdate();
			long tmp_timestamp;
			
			Enumeration e = timestamp.elements();
			while(e.hasMoreElements()) {
				Object key = e.nextElement();
				tmp_timestamp = Long.parseLong(key.toString());
				if(newtimestamp > tmp_timestamp)
					newtimestamp = tmp_timestamp;
			}
							
			getCountry().setNOldestupdate(newtimestamp);
			getCountry().setNNewestupdate(c.getNNewestupdate());
			b_changed = true;
		}
		List<UTOURISTCOUNT> newlist = c.getOperators().getUTOURISTCOUNT();
		List<UTOURISTCOUNT> oldlist = getCountry().getOperators().getUTOURISTCOUNT();
		ArrayOfUTOURISTCOUNT merged = new ArrayOfUTOURISTCOUNT();
		merged.getUTOURISTCOUNT().addAll(oldlist);
		int count = 0;
		for(int newop=0; newop < newlist.size(); newop++)
		{
			UTOURISTCOUNT tc = newlist.get(newop);
			boolean b_newrecord = true;
			//find matching old record?
			for(int oldop=0; oldop < merged.getUTOURISTCOUNT().size(); oldop++)
			{
				UTOURISTCOUNT temp = merged.getUTOURISTCOUNT().get(oldop);
				if(temp.getLOperator()==tc.getLOperator()) //the same operator
				{
					//merged.getUTOURISTCOUNT().add(temp);
					temp.setLLastupdate(tc.getLLastupdate());
					temp.setLOperator(tc.getLOperator());
					temp.setLTouristcount(tc.getLTouristcount());
					temp.setSzOperator(tc.getSzOperator());
					b_newrecord = false; //this is an update
					count+=temp.getLTouristcount();
					break;
				}
			}
			if(b_newrecord)
			{
				merged.getUTOURISTCOUNT().add(tc);
				count+=tc.getLTouristcount();
			}
		}
		count = 0;
		for(int i=0; i < merged.getUTOURISTCOUNT().size(); i++)
			count += merged.getUTOURISTCOUNT().get(i).getLTouristcount();
		getCountry().setNTouristcount(count);
		getCountry().setOperators(merged);
		getCountry().setWeightpoint(c.getWeightpoint());
		getCountry().setBounds(c.getBounds());

		if(b_changed)
		{
			setCountInProgress(false);
			model.nodeChanged(this);
		}
		return b_changed;

	}
}