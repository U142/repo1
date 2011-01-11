package no.ums.pas.tas.treenodes;

import no.ums.pas.core.defines.tree.UMSTreeNode;
import no.ums.ws.pas.tas.ULBACONTINENT;

import javax.swing.tree.DefaultTreeModel;

public class ContinentListItem extends CommonTASListItem
{
	ULBACONTINENT continent;
	public ULBACONTINENT getContinent() { return continent; }
	public ContinentListItem(ULBACONTINENT c, DefaultTreeModel model)
	{
		super(c.getLContinentpk(), c, model);
		continent = c;
		this.setUserObject(c);
		//initTable();
	}
	@Override
	public String toString()
	{
		return continent.getSzName();
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContinentListItem that = (ContinentListItem) o;

        return !(continent != null ? !continent.getSzShort().equals(that.continent.getSzShort()) : that.continent != null);

    }

    @Override
    public int hashCode() {
        return continent != null ? continent.getSzShort().hashCode() : 0;
    }

    public boolean hasChanged(ULBACONTINENT c)
	{
		if(getContinent().getNLastupdate()!=c.getNLastupdate())
			return true;
		//if(getContinent().getNTouristcount()!=c.getNTouristcount())
		//	return true;
		if(!getContinent().getSzName().equals(c.getSzName()))
			return true;
		if(!getContinent().getSzShort().equals(c.getSzShort()))
			return true;
		return false;
	}
	public boolean Update(ULBACONTINENT c)
	{
		boolean b_changed = false;
		if(hasChanged(c))
		{
			//int n = getContinent().getNTouristcount();
			//getContinent().setNTouristcount(c.getNTouristcount());
			getContinent().setSzName(c.getSzName());
			getContinent().setNLastupdate(c.getNLastupdate());
			getContinent().setSzShort(c.getSzShort());
			b_changed = true;
		}
		getContinent().setWeightpoint(c.getWeightpoint());
		getContinent().setBounds(c.getBounds());
		if(b_changed)
		{
			setCountInProgress(false);
			model.nodeChanged(this);
		}
		return b_changed;
	}
	@Override
	public int compareTo(UMSTreeNode o) {
		if(o.getClass().equals(ContinentListItem.class))
		{
			return this.getContinent().getSzName().compareToIgnoreCase(((ContinentListItem)o).getContinent().getSzName());
		}
		return 0;
	}
	
}