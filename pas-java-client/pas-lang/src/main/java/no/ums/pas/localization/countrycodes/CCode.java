package no.ums.pas.localization.countrycodes;
import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.base.Splitter;
import no.ums.log.Log;
import no.ums.log.UmsLog;

import java.util.Iterator;

public class CCode implements Cloneable {

    private static final Log log = UmsLog.getLogger(CCode.class);

    @Override
	public CCode clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return (CCode)super.clone();
	}

	public static final Function<String, CCode> PARSE = new Function<String, CCode>() {
        @Override
        public CCode apply(String input) { //@Nullable 
            Iterator<String> line = Splitter
                    .on(';')
                    .trimResults(CharMatcher.is('"')) // Remove quotes
                    .split(input).iterator(); //Preconditions.checkNotNull(input, "input is null")
            String cCode = line.hasNext() ? line.next() : "-2";
            String cName = line.hasNext() ? line.next() : "Unknown";
            String cShort = line.hasNext() ? line.next() : "Unknown";
            boolean visible = line.hasNext() && line.next().equals("1");
            return new CCode(cCode, cName, cShort, visible);
        }
    };

    private final String sz_ccode;
    private final String sz_country;
    private final String sz_short;
    private final int n_ccode;
    private final boolean b_visible;

    public boolean isVisible() {
        return b_visible;
    }

    public String getCCode() {
        return sz_ccode;
    }

    public String getCountry() {
        return sz_country;
    }

    public String getShort() {
        return sz_short;
    }

    public int getNCCode() {
        return n_ccode;
    }

    public String toString() {
        return getCountry();
    }

    public String GetCcodeAndCountry() {
        return getCCode() + " " + getCountry();
    }

    public CCode(String code, String country, String sh, boolean visible) {
        sz_ccode = code;
        sz_country = country;
        sz_short = sh;
        int ccode;
        try {
            ccode = Integer.parseInt(code);
        } catch (NumberFormatException ne) {
            ccode = 0;
            log.warn("Could not parse CC: " + code);
        }
        n_ccode = ccode;
        b_visible = visible;
    }

}