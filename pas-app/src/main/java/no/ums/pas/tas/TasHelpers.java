package no.ums.pas.tas;

import no.ums.pas.PAS;
import no.ums.ws.common.ULBACOUNTRY;
import no.ums.ws.common.UTOURISTCOUNT;

import java.awt.*;

public class TasHelpers
{
	/** UI helpers*/
	public static Color getOutdatedColor(ULBACOUNTRY c)
	{
		if(getCountIsOutdated(c))
			return Color.red;
		else
			return Color.black;
	}
	public static Color getOutdatedColor(UTOURISTCOUNT c)
	{
		if(getCountIsOutdated(c))
			return Color.red;
		else
			return Color.black;
	}
	public static String getOutdatedColorHex(ULBACOUNTRY c)
	{
		return "#" + Integer.toHexString(getOutdatedColor(c).getRGB()).substring(2);
	}
	public static String getOutdatedColorHex(UTOURISTCOUNT c)
	{
		return "#" + Integer.toHexString(getOutdatedColor(c).getRGB()).substring(2);		
	}
	
	public static long getTimeDiff(ULBACOUNTRY c)
	{
		return no.ums.pas.ums.tools.TextFormat.datetime_diff_minutes(c.getNOldestupdate(), TasPanel.SERVER_CLOCK);			
	}
	
	public static long getTimeDiff(UTOURISTCOUNT c)
	{
		return no.ums.pas.ums.tools.TextFormat.datetime_diff_minutes(c.getLLastupdate(), TasPanel.SERVER_CLOCK);
	}
	
	public static boolean getCountIsOutdated(ULBACOUNTRY c)
	{
		try
		{
			long diff = getTimeDiff(c);
			return (diff >= TasPanel.TAS_ADRCOUNT_TIMESTAMP_EXPIRED_SECONDS/60.0);
		}
		catch(Exception e)
		{
			return true;
		}
	}
	public static boolean getCountIsOutdated(UTOURISTCOUNT c)
	{
		try
		{
			long diff = getTimeDiff(c);
			return (diff >= TasPanel.TAS_ADRCOUNT_TIMESTAMP_EXPIRED_SECONDS/60.0);
		}
		catch(Exception e)
		{
			return true;
		}
	}
	public static String getOutdatedText(ULBACOUNTRY c)
	{
		String ret = "";
		long diff = getTimeDiff(c);
		ret = getDateText(diff);
		return ret;
	}
	public static String getOutdatedText(UTOURISTCOUNT c)
	{
		String ret = "";
		long diff = getTimeDiff(c);
		ret = getDateText(diff);
		return ret;		
	}
	
	public static String getDateText(long diff)
	{
		String ret = "";
		if(diff>60*24)
			ret += diff /60/24 + " " + PAS.l("common_days_maybe") + " " + PAS.l("common_ago");
		else if(diff>60)
			ret += diff /60 + " " + PAS.l("common_hours_maybe") + " " + PAS.l("common_ago");
		else
			ret += diff + " " + PAS.l("common_minutes_maybe") + " " + PAS.l("common_ago");
		return ret;
	}
	
	public static Color getInformationColor()
	{
		return new Color(255, 255, 255, 198);
	}
	public static Color getSimulationColor()
	{
		return new Color(200, 200, 0, 198);
	}
	public static Color getLiveColor()
	{
		return new Color(128, 0, 0, 198);
	}
	public static Color getInformationTextColor()
	{
		return new Color(0, 0, 0, 255);
	}
	public static Color getSimulationTextColor()
	{
		return new Color(0, 0, 0, 255);
	}
	public static Color getLiveTextColor()
	{
		return new Color(255, 255, 255, 255);
	}

}