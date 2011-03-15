package no.ums.pas.pluginbase.defaults;


import no.ums.pas.PAS;
import no.ums.pas.core.defines.SearchPanelResults.TableList;
import no.ums.pas.core.mainui.address_search.AddressSearchPanel;
import no.ums.pas.core.mainui.address_search.SearchPanelResultsAddrSearch;
import no.ums.pas.core.mainui.address_search.SearchPanelVals;
import no.ums.pas.core.mainui.address_search.AddressSearchDlg.AddressSearchListItem;
import no.ums.pas.core.ws.vars;
import no.ums.pas.localization.Localization;
import no.ums.pas.pluginbase.AbstractPasScriptingInterface;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.pas.GABTYPE;
import no.ums.ws.pas.ObjectFactory;
import no.ums.ws.pas.Pasws;
import no.ums.ws.pas.UGabResult;
import no.ums.ws.pas.UGabSearchParams;
import no.ums.ws.pas.UGabSearchResultList;

import javax.swing.table.TableColumnModel;
import javax.xml.namespace.QName;
import java.awt.Dimension;
import java.awt.event.ActionListener;

public class DefaultAddressSearch implements AbstractPasScriptingInterface.AddressSearch
{
	
	
	@Override
	public SearchPanelResultsAddrSearch onCreateSearchPanelResultsAddrSearch(AddressSearchPanel panel, ActionListener callback)
			throws Exception {
		System.out.println("onCreateSearchPanelResultsAddrSearch");
        String[] sz_columns  = {Localization.l("adrsearch_dlg_hit"),
                Localization.l("adrsearch_dlg_address"),
                Localization.l("adrsearch_dlg_region"),
                Localization.l("common_lon"),
                Localization.l("common_lat"),};//"icon"
		int[] n_width = { 30, 200, 100, 50, 50 }; //, 16 };
		return new SearchPanelResultsAddrSearch(panel, sz_columns, n_width, new Dimension(800, 200), callback);
	}

	@Override
	public SearchPanelVals onCreateSearchPanelVals(AddressSearchPanel panel) throws Exception {
		System.out.println("onCreateSearchPanelVals");
		return new SearchPanelVals(panel);
	}

	@Override
	public UGabSearchResultList onExecSearch(SearchPanelVals vals) throws Exception {
		String sz_address	= vals.get_address();
		String sz_no		= vals.get_number();
		String sz_postno	= vals.get_postno();
		String sz_postarea	= vals.get_postarea();
		String sz_region	= vals.get_region();
		String sz_country	= vals.get_country();
		
		
		return onExecSearch(sz_address, sz_no, sz_postno, sz_postarea, sz_region, sz_country);
	}
	public UGabSearchResultList onExecSearch(String sz_address, String sz_no, String sz_postno, String sz_postarea, String sz_region, String sz_country) throws Exception
	{
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
		params.setSzAddress(sz_address==null?"":sz_address);
		params.setSzCountry(sz_language);
		params.setSzLanguage(sz_language);
		params.setSzNo(sz_no==null?"":sz_no);
		params.setSzPostarea(sz_postarea==null?"":sz_postarea);
		params.setSzPostno(sz_postno==null?"":sz_postno);
		params.setSzPwd("MSG");
		params.setSzRegion(sz_region==null?"":sz_region);
		params.setSzUid("UMS");
		ULOGONINFO logoninfo = new ULOGONINFO();
		logoninfo.setSessionid(PAS.get_pas().get_userinfo().get_sessionid());
		logoninfo.setLUserpk(new Long(PAS.get_pas().get_userinfo().get_userpk()));
		logoninfo.setSzPassword(PAS.get_pas().get_userinfo().get_passwd());
		logoninfo.setLComppk(PAS.get_pas().get_userinfo().get_comppk());
		logoninfo.setLDeptpk(PAS.get_pas().get_userinfo().get_current_department().get_deptpk());
		
		java.net.URL wsdl;
		try
		{			
			wsdl = new java.net.URL(vars.WSDL_PAS);//PAS.get_pas().get_sitename() + "/ExecAlert/WS/PAS.asmx?WSDL"); 
			QName service = new QName("http://ums.no/ws/pas/", "pasws");
			Pasws myService = new Pasws(wsdl, service);
			UGabSearchResultList response = myService.getPaswsSoap12().gabSearch(params, logoninfo);
			if(response.isBHaserror())
			{
                no.ums.pas.ums.errorhandling.Error.getError().addError(Localization.l("common_error"), response.getSzErrortext(), new Exception(response.getSzExceptiontext()), 1);
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
			System.out.println("Resulttype="+result.getType().toString());
			TableColumnModel tcm = list.getJTable().getColumnModel();
			if(result.getType().equals(GABTYPE.HOUSE))
			{
                tcm.getColumn(1).setHeaderValue(Localization.l("adrsearch_dlg_address"));
                tcm.getColumn(2).setHeaderValue(Localization.l("adrsearch_dlg_region"));
			}
			else if(result.getType().equals(GABTYPE.STREET))
			{
                tcm.getColumn(1).setHeaderValue(Localization.l("adrsearch_dlg_streetname"));
                tcm.getColumn(2).setHeaderValue(Localization.l("adrsearch_dlg_region"));
			}
			else if(result.getType().equals(GABTYPE.POST))
			{
                tcm.getColumn(1).setHeaderValue(Localization.l("adrsearch_dlg_postno"));
                tcm.getColumn(2).setHeaderValue(Localization.l("adrsearch_dlg_region"));
			}
			else if(result.getType().equals(GABTYPE.REGION))
			{
                tcm.getColumn(1).setHeaderValue(Localization.l("adrsearch_dlg_region"));
                tcm.getColumn(2).setHeaderValue(Localization.l("adrsearch_dlg_region"));
			}
			list.getJTable().getTableHeader().repaint();
			
			Object[] obj_insert = { result.getMatch(), result.getName(), result.getRegion(), new Float(result.getLon()).toString(), new Float(result.getLat()).toString() }; //, m_icon_goto };
			list.insert_row(obj_insert, -1);
		}

		return true;
	}
	
}
