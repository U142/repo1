package no.ums.pas.localization.countrycodes;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import no.ums.pas.localization.Localization;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/***
 * 
 * @author Modda
 *
 * Reads countrycodes/country/short from lang file.
 * Formatted in lang-file: common_cc_info_1	= "USA"	"UT"	0
 */
public enum CountryCodes {
	INSTANCE;
	
    private final Map<String, CCode> codesById = Maps.newHashMap();

    CountryCodes() {
        try {
            /*final List<String> lines = Resources.readLines(Resources.getResource(getClass(), "countrycodes.csv"), Charsets.ISO_8859_1);
            for (CCode code : Lists.transform(lines, CCode.PARSE)) {
                codesById.put(code.getCCode(), code);
            }*/
    		String filter = "common_cc_info_";
    		Map<String, String> map = Localization.valuesStartingWith(filter);
    		//convert all to ccode objects
    		List<String> listCC = Lists.newArrayList();
    		for(Entry<String,String> en : map.entrySet())
    		{
    			//Make a formatted CC String
    			listCC.add(en.getKey().substring(en.getKey().lastIndexOf('_')+1) + "\t" + en.getValue());
    		}
            for (CCode code : Lists.transform(listCC, CCode.PARSE)) {
                codesById.put(code.getCCode(), code);
            }

        } catch (Exception e) {
            throw new IllegalStateException("Failed to read country codes from language file", e);
        }
    }

    public static CCode getCountryByCCode(final String ccode) {
    	final String fixedCode = (ccode.startsWith("00")) ? ccode.substring(2) : ccode;
        return (INSTANCE.codesById.containsKey(fixedCode)) ? INSTANCE.codesById.get(fixedCode) : new CCode(ccode, "CCode Not found [" + ccode + "]", "N/A", false);
    }

    public static Iterable<CCode> getCountryCodes() {
        return INSTANCE.codesById.values();
    }

}
