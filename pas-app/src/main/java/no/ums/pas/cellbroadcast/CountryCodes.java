package no.ums.pas.cellbroadcast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;


public enum CountryCodes {//implements ActionListener {
    INSTANCE;

    private final Map<String, CCode> codesById = new LinkedHashMap<String, CCode>();

    CountryCodes() {
        InputStream is = getClass().getResourceAsStream("countrycodes.csv");
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        try {
            while ((line = br.readLine()) != null) {
                String[]  row = line.split("\t");
                String sz_ccode, sz_cname, sz_cshort, sz_cvisible;
                sz_ccode = (row.length > 0) ? row[0] : "-2";
                sz_cname = (row.length > 1) ? row[1].replace("\"", "") : "Unknown";
                sz_cshort = (row.length > 2) ? row[2] : "Unknown";
                sz_cvisible = (row.length > 3) ? row[3] : "0";
                codesById.put(sz_ccode, new CCode(sz_ccode, sz_cname, sz_cshort, sz_cvisible));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read country codes", e);
        }

    }


    public static CCode getCountryByCCode(String ccode) {
        return (INSTANCE.codesById.containsKey(ccode)) ? INSTANCE.codesById.get(ccode) : new CCode(ccode, "CCode Not found [" + ccode + "]", "N/A", "0");
    }

    public static Iterable<CCode> getCountryCodes() {
        return INSTANCE.codesById.values();
    }

}