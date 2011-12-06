package no.ums.pas;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import no.ums.pas.cellbroadcast.CCode;
import no.ums.pas.localization.Localization;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class CCFromLangFileTest {
	
	@Test
	public void testGetAllCCFromLangFile()
	{
		Map<String, CCode> codesById = Maps.newHashMap();
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
        Assert.assertNotNull(codesById);
	}
}
