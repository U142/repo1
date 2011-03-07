package no.ums.pas.ums.tools;

import no.ums.pas.core.controllers.StatusController;
import no.ums.pas.localization.Localization;
import no.ums.pas.send.SendProperties;
import org.jvnet.substance.SubstanceLookAndFeel;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.Font;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//substance 3.3


//substance 5.2
//import org.jvnet.substance.utils.border.SubstanceBorder;


public final class TextFormat {
	public synchronized static final Border CreateStdBorder(String text) {
		/*SubstanceBorder bor = new SubstanceBorder();
		java.awt.Color col = SubstanceLookAndFeel.getActiveColorScheme().getDarkColor();
		LineBorder line = (LineBorder)BorderFactory.createLineBorder(col, 1);
		TitledBorder border = BorderFactory.createTitledBorder(line, text, TitledBorder.CENTER, TitledBorder.CENTER, new Font("Arial", Font.BOLD, 12));
		
		return border;*/
		return CreateStdBorder(text, TitledBorder.CENTER);
		//SubstanceBorder bor = new SubstanceBorder();
		
		//return bor;
	}
	public synchronized static final Border CreateStdBorder(String text, int textalign) {
		//substance 3.3
		java.awt.Color col = SubstanceLookAndFeel.getActiveColorScheme().getDarkColor();
		
		//Substance 5.2
		//java.awt.Color col = SubstanceLookAndFeel.getCurrentSkin().getMainActiveColorScheme().getDarkColor();
		
		LineBorder line = (LineBorder)BorderFactory.createLineBorder(col, 1);
		TitledBorder border = BorderFactory.createTitledBorder(line, text, textalign, textalign, new Font("Arial", Font.BOLD, 12));
		
		return border;
		//SubstanceBorder bor = new SubstanceBorder();
		
		//return bor;
	}
	
	public static Double round(double d, int decimalPlace){
	    java.math.BigDecimal bd = new java.math.BigDecimal(Double.toString(d));
	    bd = bd.setScale(decimalPlace,java.math.BigDecimal.ROUND_HALF_UP);
	    return new Double(bd.doubleValue());
	}
	
	public synchronized static final String format_date(long n)
	{
		String s = new Long(n).toString();
		return format_date(s);
	}
	
	public synchronized static final String format_date(String sz_date)
	{
		String sz_ret = new String();
		if(sz_date.length() == 6) {
			sz_ret = sz_date.substring(4,6) + "." + sz_date.substring(0,4);
		}
		else if(sz_date.length() >= 8)
		{
			sz_ret = sz_date.substring(6,8) + "." + sz_date.substring(4,6) + "." + sz_date.substring(0, 4); 
		}
		else
			return "N/A";
		return sz_ret;
	}

	public synchronized static final long datetime_diff_minutes(long n)
	{
		try
		{
			return (long)(datetime_diff_seconds(n)/60.0);
		}
		catch(Exception e)
		{
			return 0;
		}
	}
	public synchronized static final long datetime_diff_seconds(long n)
	{
		try
		{
			long ret = -1;
			String old_date = new Long(n).toString();
			if(old_date.length()!=14)
				return ret;
			Calendar c_old = Calendar.getInstance();
			c_old.set(
					new Integer(old_date.substring(0, 4)), 
					new Integer(old_date.substring(4, 6))-1, //0-based month
					new Integer(old_date.substring(6,8)), 
					new Integer(old_date.substring(8,10)), 
					new Integer(old_date.substring(10, 12)), 
					new Integer(old_date.substring(12, 14)));
			Calendar now = Calendar.getInstance(TimeZone.getDefault());
			//c_old = Calendar.getInstance();
			ret = now.getTimeInMillis() - c_old.getTimeInMillis();
			ret = (long)(ret / 1000.0);
			return ret;		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return 0;
		}
	}
	public synchronized static final long datetime_diff_minutes(long n1, long n2)
	{
		try
		{
			return (long)(datetime_diff_seconds(n1, n2)/60.0);
		}
		catch(Exception e)
		{
			return 0;
		}
	}
	public synchronized static final long datetime_diff_seconds(long n1, long n2)
	{
		try
		{
			long ret = 0;
			String old_date = Long.toString(n1);
			if(old_date.length()!=14)
				return ret;
			String new_date = Long.toString(n2);
			if(new_date.length()!=14)
				return ret;
			Calendar c_old = Calendar.getInstance();
			Calendar c_new = Calendar.getInstance();
			c_old.set(
					new Integer(old_date.substring(0, 4)), 
					new Integer(old_date.substring(4, 6))-1, //0-based month
					new Integer(old_date.substring(6,8)), 
					new Integer(old_date.substring(8,10)), 
					new Integer(old_date.substring(10, 12)), 
					new Integer(old_date.substring(12, 14)));
			c_new.set(
					new Integer(new_date.substring(0, 4)), 
					new Integer(new_date.substring(4, 6))-1, //0-based month
					new Integer(new_date.substring(6,8)), 
					new Integer(new_date.substring(8,10)), 
					new Integer(new_date.substring(10, 12)), 
					new Integer(new_date.substring(12, 14)));
			
			ret = c_new.getTimeInMillis() - c_old.getTimeInMillis();
			ret = (long)(ret / 1000.0);
			return ret;		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return 0;
		}
	}
	public synchronized static String format_datetime(long n)
	{
		String sz = Long.toString(n);
		switch(sz.length())
		{
		case 6: //yyyymm
			sz = format_date(sz);
			break;
		case 8: //yyyymmdd
			sz = format_date(sz);
			break;
		case 10: //yyyymmddhh
			sz = format_date(sz) + " " + format_time(sz.substring(8), 2);
			break;
		case 12: //yyyymmddhhmm
			sz = format_date(sz) + " " + format_time(sz.substring(8), 4);
			break;
		case 14: //yyyymmddhhmmss
			sz = format_date(sz) + " " + format_time(sz.substring(8), 6);
			break;
		default:
            sz = Localization.l("common_na");
		}
		return sz;
	}
	public synchronized static String format_date(int n_date)
	{
		if(n_date>=0)
		{
			String sz_date = Integer.toString(n_date);
			return format_date(sz_date);
		}
		return "N/A";
	}
	public synchronized static String format_time(String sz_time, int n_size)
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
	public synchronized static String format_seconds_to_hms(int s)
	{
		if(s<=0)
			return "";
		String ret = "";
		int hours = (int)Math.floor((s / 3600.0));
		int minutes = (s / 60) % 60;
		int seconds  = s % 60;
		if(hours>0)
			ret += (hours<10 ? "0" : "") + hours;
		if(minutes > 0 || hours > 0)
			ret += (hours>0 ? ":" : "") + (minutes<10 ? "0" : "") + minutes;
		ret += (minutes>0 || hours>0 ? ":" : "") + (minutes==0 && hours==0 ? "00:" : "") + (seconds<10 ? "0" : "") + seconds;
			
		return ret;
	}
	public synchronized static String format_datetime(String sz_dt /*expect yyyymmddhhmmss*/) {
		if(sz_dt.length()<14)
			return "N/A";
		return format_date(sz_dt.substring(0, 8)) + " " + format_time(sz_dt.substring(8, 14), 6);
	}
	private synchronized static String format_time6(String sz_time)
	{
		String sz_ret;
		sz_ret = padding(sz_time, '0', 6);
		return sz_ret.substring(0,2) + ":" + sz_ret.substring(2,4) + ":" + sz_ret.substring(4,6);
	}
	public synchronized static String format_time(int n_time, int n_size)
	{
		if(n_time>=0)
			return format_time((String)(Integer.toString(n_time)), n_size);
		return "N/A";
	}
	public synchronized static String padding(String sz_str, char c_padding, int n_total_length)
	{
		String sz_ret = sz_str;
		if(sz_str.length() > n_total_length)
			return sz_str.substring(0, n_total_length);
		for(int i=0; i < n_total_length - sz_str.length(); i++)
			sz_ret = c_padding + sz_ret;
		//PAS.get_pas().add_event(sz_ret);
		return sz_ret;
	}
	public synchronized static String get_statustext_from_code(int n_status, int n_altjmp)
	{
		String sz_ret;
		if(n_altjmp==1)
			return StatusController.STATUSSTOP;
		switch(n_status)
		{
			case 1:
				sz_ret = StatusController.STATUS_1;
				break;
			case 2:
				sz_ret = StatusController.STATUS_2;
				break;
			case 3:
				sz_ret = StatusController.STATUS_3;
				break;
			case 4:
				sz_ret = StatusController.STATUS_4;
				break;
			case 5:
				sz_ret = StatusController.STATUS_5;
				break;
			case 6:
				sz_ret = StatusController.STATUS_6;
				break;
			case 7:
				sz_ret = StatusController.STATUS_7;
				break;
			case 8:
				sz_ret = StatusController.STATUS_8;
				break;
			case -2:
				sz_ret = StatusController.STATUSNOREC;
				break;
			default:
				sz_ret = StatusController.STATUSERR;
				break;
		}
		return sz_ret;
		
	}
	public synchronized static final String get_sendingtype(int n_type) {
		String sz_ret;
		switch(n_type)  {
			case SendProperties.SENDING_TYPE_POLYGON_:
                sz_ret = Localization.l("main_sending_type_polygon");
				break;
			case SendProperties.SENDING_TYPE_GEMINI_STREETCODE_:
                sz_ret = Localization.l("main_sending_type_gis_street");
				break;
			case SendProperties.SENDING_TYPE_GEMINI_GNRBNR_:
                sz_ret = Localization.l("main_sending_type_gis_gbno");
				break;
			case SendProperties.SENDING_TYPE_CIRCLE_:
                sz_ret = Localization.l("main_sending_type_ellipse");
				break;
			case SendProperties.SENDING_TYPE_MUNICIPAL_:
                sz_ret = Localization.l("main_sending_type_municipal");
				break;
			default:
                sz_ret = Localization.l("main_sending_type_unknown");
				break;
		}
		return sz_ret;
	}
	
	public synchronized static final String get_addresstypes(int n_type) {
		String ret;
		String sz_private	= "Private";
		String sz_company	= "Company";
		String sz_mobile	= "Mobile";
		switch(n_type) {
			case 0:
				ret = sz_private + " / " + sz_company; //Controller.ADR_TYPES_PRIVATE_ | Controller.ADR_TYPES_COMPANY_;
				break;
			case 1:
				ret = sz_private; //Controller.ADR_TYPES_PRIVATE_;
				break;
			case 2:
				ret = sz_company;
				break;
			case 3:
				ret = sz_mobile;
				break;
			case 4:
				ret = sz_private + " / " + sz_mobile;
				break;
			case 5:
				ret = sz_company + " / " + sz_mobile;
				break;
			case 6:
				ret = sz_private + " / " + sz_company + " / " + sz_mobile;
				break;
			default:
				ret = "Unknown";
				break;
		}
		return ret;
	}
	
	public synchronized static final String XmlEncode(String text){

	    int[] chars = {38, 60, 62, 34, 61, 39};
	    for(int i=0;i<chars.length-1;i++){
	        text = text.replaceAll(String.valueOf((char)chars[i]),
	            "&#"+chars[i]+";");
	    }
	    return text; 
	}
	
	public static int GsmStrLen(String s)
	{
		int ret = s.length();
		String gsmExt = "|^{}\\[\\]~\\\\";
		Pattern p = Pattern.compile("[" + gsmExt + "]");
		Matcher m = p.matcher(s);
		while(m.find())
		{
			ret++;
		}		
		return ret;
	}
	
	public static synchronized String GsmStrMaxLen(String original, int maxlen)
	{
		String ret = "";
		int original_len = original.length();
		int gsm_len = GsmStrLen(original);
		if(maxlen>=gsm_len) //no need to cut
			return original;
		int newlen = gsm_len;
		int totallen = 0;
		//we need to cut
		String newstr = "";
		for(int i=0; i < original_len; i++)
		{
			String s = original.substring(i, i+1);
			int charlen = GsmStrLen(s);
			if(charlen+totallen > maxlen)
				break;
			totallen += charlen;
			newstr += s;
			/*String s = original.substring(i, i+1);
			int charlen = GsmStrLen(s);
			if(charlen>1)
				newlen -= (charlen-1);
			totallen += charlen;
			if(totallen>=maxlen)
				break;*/
		}
		ret = newstr;//original.substring(0, newlen-1);
		
		return ret;
	}
	public static RegExpResult RegExpGsm(String s)
	{
		RegExpResult result = new TextFormat().new RegExpResult();
		//\u0394 = delta
		//\u03A6 = Phi
		//\u0393 = Gamma
		//\u039B = Lambda
		//\u03A9 = Omega
		//\u03A0 = Pi
		//\u03A8 = Psi
		//\u03A3 = Sigma
		//\u0398 = Theta
		//\u039E = Xi
		//http://www.unicode.org/Public/MAPPINGS/ETSI/GSM0338.TXT
		String greek = "\u0394\u03A6\u0393\u039B\u03A9\u03A0\u03A8\u03A3\u0398\u039E";
		
		//String gsm7 = "[^a-zA-Z0-9 " + greek + "\\.\\_\\@\\£\\$\\¥\\è\\é\\ù\\ì\\ò\\Ç\\Ø\\ø\\Å\\å\\Æ\\æ\\ß\\É\\Ä\\Ö\\Ñ\\Ü\\§\\¿\\ä\\ö\\ñ\\ü\\à\\+\\,\\/\\:\\;\\<\\=\\>\\?\\¡\\|\\^\\\\{\\}\\*\\!\\#\\€\\%\\&\\'\\(\\)\r\n\\\\\\[\\]\"~-]";
		String gsm7 = "[^a-zA-Z0-9 " + greek + "\\.\\_\\@\\£\\$\\¥\\è\\é\\ù\\ì\\ò\\Ç\\Ø\\ø\\Å\\å\\Æ\\æ\\ß\\É\\Ä\\Ö\\Ñ\\Ü\\§\\¿\\ä\\ö\\ñ\\ü\\à\\+\\,\\/\\:\\;\\<\\=\\>\\?\\¡\\|\\^\\\\{\\}\\*\\!\\#\\€\\%\\&\\'\\(\\)\r\n\\\\\\[\\]\"~-]";
		Pattern p = Pattern.compile("[" + gsm7 + "]");
		Matcher m = p.matcher(s);
		StringBuffer sb = new StringBuffer();
		result.valid = true;
		while(m.find())
		{
			m.appendReplacement(sb, "");
			result.valid = false;
		}
		m.appendTail(sb);
		result.resultstr = sb.toString();
		return result;
	}
	public class RegExpResult
	{
		public String resultstr = "";
		public boolean valid = true;
	}
}