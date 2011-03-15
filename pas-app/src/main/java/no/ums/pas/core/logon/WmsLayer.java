package no.ums.pas.core.logon;

public class WmsLayer {
	public WmsLayer(String name, boolean checked)
	{
		this.layername = name;
		this.checked = checked;
	}
	public String layername;
	public Boolean checked;
	@Override
	public String toString()
	{
		return layername + "=" + checked.toString();
	}
	@Override
	public int hashCode() {
		return layername.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		return (obj==null ? false : this.hashCode()==obj.hashCode());
	}
}
