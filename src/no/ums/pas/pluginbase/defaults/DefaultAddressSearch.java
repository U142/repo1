package no.ums.pas.pluginbase.defaults;


import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.xml.namespace.QName;

import no.ums.pas.PAS;
import no.ums.pas.core.defines.SearchPanelResults.TableList;
import no.ums.pas.core.mainui.SearchFrame;
import no.ums.pas.core.mainui.SearchPanelResultsAddrSearch;
import no.ums.pas.core.mainui.SearchPanelVals;
import no.ums.pas.core.ws.vars;
import no.ums.pas.pluginbase.PasScriptingInterface;
import no.ums.ws.pas.ObjectFactory;
import no.ums.ws.pas.Pasws;
import no.ums.ws.pas.UGabResult;
import no.ums.ws.pas.UGabSearchParams;
import no.ums.ws.pas.UGabSearchResultList;
import no.ums.ws.pas.ULOGONINFO;

public class DefaultAddressSearch extends PasScriptingInterface.AddressSearch
{
	
	
	@Override
	public SearchPanelResultsAddrSearch onCreateSearchPanelResultsAddrSearch(SearchFrame frame, ActionListener callback)
			throws Exception {
		System.out.println("onCreateSearchPanelResultsAddrSearch");
        String[] sz_columns  = {PAS.l("adrsearch_dlg_hit"),
				PAS.l("adrsearch_dlg_address"),
				PAS.l("adrsearch_dlg_region"),
				PAS.l("common_lon"),
				PAS.l("common_lat"),};//"icon"
		int[] n_width = { 30, 200, 100, 50, 50 }; //, 16 };
		return new SearchPanelResultsAddrSearch(frame, sz_columns, n_width, new Dimension(800, 200), callback);
	}

	@Override
	public SearchPanelVals onCreateSearchPanelVals(SearchFrame frame) throws Exception {
		System.out.println("onCreateSearchPanelVals");
		return new SearchPanelVals(frame);
	}

	@Override
	public UGabSearchResultList onExecSearch(SearchPanelVals vals) throws Exception {
		String sz_address	= vals.get_address();
		String sz_no		= vals.get_number();
		String sz_postno	= vals.get_postno();
		String sz_postarea	= vals.get_postarea();
		String sz_region	= vals.get_region();
		String sz_country	= vals.get_country();
		String sz_language = "NO";
		if(sz_country=="Norway")
			sz_language = "NO";
		else if(sz_country=="Sweden")
			sz_language = "SE";
		else if(sz_country=="Denmark")
			sz_language = "DK";
		
		ObjectFactory factory = new ObjectFactory();
		//GabSearch searcher = factory.createGabSearch();
		//GabSearchResponse response = factory.createGabSearchResponse();
		UGabSearchParams params = factory.createUGabSearchParams();
		params.setNCount(10);
		params.setNSort(0);
		params.setSzAddress(sz_address);
		params.setSzCountry(sz_language);
		params.setSzLanguage(sz_language);
		params.setSzNo(sz_no);
		params.setSzPostarea(sz_postarea);
		params.setSzPostno(sz_postno);
		params.setSzPwd("MSG");
		params.setSzRegion(sz_region);
		params.setSzUid("UMS");
		ULOGONINFO logoninfo = factory.createULOGONINFO();
		
		java.net.URL wsdl;
		try
		{			
			wsdl = new java.net.URL(vars.WSDL_PAS);//PAS.get_pas().get_sitename() + "/ExecAlert/WS/PAS.asmx?WSDL"); 
			QName service = new QName("http://ums.no/ws/pas/", "pasws");
			Pasws myService = new Pasws(wsdl, service);
			UGabSearchResultList response = myService.getPaswsSoap12().gabSearch(params, logoninfo);
			if(response.isBHaserror())
			{
				no.ums.pas.ums.errorhandling.Error.getError().addError(PAS.l("common_error"), response.getSzErrortext(), new Exception(response.getSzExceptiontext()), 1);
				throw new Exception(response.getSzErrortext());
			}
			return response;
		} 
		catch(Exception e)
		{
			throw e;
		}
	}
	
	@Override
	public boolean onPopulateList(UGabSearchResultList results, TableList list)
	{
		if(list!=null) {
			list.clear();
		}
		java.util.Iterator it = results.getList().getUGabResult().iterator();
		while(it.hasNext())
		{
			UGabResult result = (UGabResult)it.next();
			Object[] obj_insert = { result.getMatch(), result.getName(), result.getRegion(), new Float(result.getLon()).toString(), new Float(result.getLat()).toString() }; //, m_icon_goto };
			list.insert_row(obj_insert, -1);
		}

		return true;
	}
}
