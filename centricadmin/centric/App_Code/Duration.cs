using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

/// <summary>
/// Summary description for Duration
/// </summary>
public class Duration
{
    private int l_duration, l_interval, l_repetition;

	public Duration()
	{
		//
		// TODO: Add constructor logic here
		//
	}

    public Duration(int l_duration, int l_interval, int l_repetition)
    {
        this.l_duration = l_duration;
        this.l_interval = l_interval;
        this.l_repetition = l_repetition;
    }

    public override string ToString()
    {
        return convert(l_duration);
    }

    private string convert(int l_duration)
    {
        if (l_duration < 60)
        {
            return l_duration.ToString() + " minute(s)";
        }
        else if (l_duration >= 1440)
        {
            int d = l_duration / 1440;
            int h = (l_duration - (d * 1440))/ 60;
            int m = l_duration - ((d * 1440) + h * 60);
            return d.ToString() + " day(s)" + (h>0?" " + h + " hour(s)":"") + (m>0?" " + m + " minute(s)":"");
        }
        else if(l_duration >= 60) {
            int h = l_duration/60;
            int m = l_duration - (h * 60);
            return h.ToString() + " hour(s)" + (m>0?" " + m + " minute(s)":"");
        }

        return "";
    }



}
