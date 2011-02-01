package no.ums.pas.cellbroadcast;

import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;

import javax.annotation.Nullable;
import java.util.Iterator;

public class CCode {

    public static final Function<String, CCode> PARSE = new Function<String, CCode>() {
        @Override
        public CCode apply(@Nullable String input) {
            Iterator<String> line = Splitter
                    .on('\t')
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
        n_ccode = Integer.parseInt(code);
        b_visible = visible;
    }

}