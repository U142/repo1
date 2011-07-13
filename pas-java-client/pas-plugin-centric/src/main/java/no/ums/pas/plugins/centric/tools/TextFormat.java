package no.ums.pas.plugins.centric.tools;

public final class TextFormat {
	
	public synchronized static final String format_date(long n)
	{
		String s = new Long(n).toString();
		return format_date(s);
	}
	
	public synchronized static final String format_datetime(String sz_dt /*expect yyyymmddhhmmss*/) {
		if(sz_dt.length()<14)
			return "N/A";
		return format_date(sz_dt.substring(0, 8)) + " " + format_time(sz_dt.substring(8, 14), 6);
	}
	
	public synchronized static final String format_time(String sz_time, int n_size)
	{
		String sz_ret = new String();
		if(n_size == 2) //hour only
		{
			sz_ret = sz_time+":00";
		}
		else if(n_size == 4)
		{
			sz_time = padding(sz_time, '0', 4);
			sz_ret = sz_time.substring(0,2) + ":" + sz_time.substring(2,4);
		}
		else if(n_size == 6)
		{
			sz_ret = format_time6(sz_time);
		}
		else
			return "N/A";
		return sz_ret;
	}
	
	private synchronized static final String format_time6(String sz_time)
	{
		String sz_ret;
		sz_ret = padding(sz_time, '0', 6);
		return sz_ret.substring(0,2) + ":" + sz_ret.substring(2,4) + ":" + sz_ret.substring(4,6);
	}
	
	public synchronized static final String padding(String sz_str, char c_padding, int n_total_length)
	{
		String sz_ret = sz_str;
		if(sz_str.length() > n_total_length)
			return sz_str.substring(0, n_total_length);
		for(int i=0; i < n_total_length - sz_str.length(); i++)
			sz_ret = c_padding + sz_ret;
		//PAS.get_pas().add_event(sz_ret);
		return sz_ret;
	}
	
	public synchronized static final String format_date(String sz_date)
	{
		String sz_ret;
		if(sz_date.length() == 6) {
			sz_ret = sz_date.substring(4,6) + "-" + sz_date.substring(0,4);
		}
		else if(sz_date.length() >= 8)
		{
			sz_ret = sz_date.substring(6,8) + "-" + sz_date.substring(4,6) + "-" + sz_date.substring(0, 4); 
		}
		else
			return "N/A";
		return sz_ret;
	}
}
