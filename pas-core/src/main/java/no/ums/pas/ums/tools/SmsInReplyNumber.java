package no.ums.pas.ums.tools;

public class SmsInReplyNumber {
	
	private String sz_replynumber;
	private int l_activerefno;
	private long l_timestamp;
	
	public SmsInReplyNumber(String sz_replynumber, int l_activerefno, long l_timestamp){
		this.sz_replynumber = sz_replynumber;
		this.l_activerefno = l_activerefno;
		this.l_timestamp = l_timestamp;
	}

	public String get_replynumber() {
		return sz_replynumber;
	}

	public void set_replynumber(String sz_replynumber) {
		this.sz_replynumber = sz_replynumber;
	}

	public int get_activerefno() {
		return l_activerefno;
	}

	public void set_activerefno(int l_activerefno) {
		this.l_activerefno = l_activerefno;
	}

	public long get_timestamp() {
		return l_timestamp;
	}

	public void set_timestamp(long l_timestamp) {
		this.l_timestamp = l_timestamp;
	}

	@Override
	public String toString() {
		if(l_activerefno > 0)
			return "+" + sz_replynumber + " Active " + no.ums.pas.ums.tools.TextFormat.format_datetime(l_timestamp);
		else 
			return "+" + sz_replynumber + " Inactive";
	}

}
