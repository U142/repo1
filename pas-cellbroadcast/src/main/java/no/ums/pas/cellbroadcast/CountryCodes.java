package no.ums.pas.cellbroadcast;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.LineProcessor;
import com.google.common.io.Resources;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public enum CountryCodes {
    INSTANCE;

    private final Map<String, CCode> codesById;

    CountryCodes() {
        try {
            final List<String> lines = Resources.readLines(Resources.getResource(getClass(), "countrycodes.csv"), Charsets.ISO_8859_1);
            codesById = Maps.uniqueIndex(Lists.transform(lines, CCode.PARSE), new Function<CCode, String>() {
                @Override
                public String apply(@Nullable CCode input) {
                    return input.getCCode();
                }
            });
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read country codes", e);
        }
    }

    public static CCode getCountryByCCode(final String ccode) {
    	final String fixedCode = (ccode.startsWith("00")) ? ccode.substring(2) : ccode;
        return (INSTANCE.codesById.containsKey(fixedCode)) ? INSTANCE.codesById.get(fixedCode) : new CCode(ccode, "CCode Not found [" + ccode + "]", "N/A", "0");
    }

    public static Iterable<CCode> getCountryCodes() {
        return INSTANCE.codesById.values();
    }

}