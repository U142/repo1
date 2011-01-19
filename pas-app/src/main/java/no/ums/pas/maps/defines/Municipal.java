package no.ums.pas.maps.defines;

public class Municipal
{
	protected String sz_id;
	protected String sz_name;
	public String get_id() { return sz_id; }
	public String get_name() { return sz_name; }
	public Municipal(String sz_id, String sz_name)
	{
		this.sz_id = sz_id;
		this.sz_name = sz_name;
	}
}