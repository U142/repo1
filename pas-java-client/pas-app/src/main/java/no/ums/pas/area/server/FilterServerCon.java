package no.ums.pas.area.server;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.area.voobjects.AddressFilterInfoVO;
import no.ums.pas.core.Variables;
import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.core.ws.GISFilterList;
import no.ums.pas.core.ws.WSThread;
import no.ums.pas.core.ws.vars;
import no.ums.pas.importer.gis.GISList;
import no.ums.pas.maps.defines.GISShape;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.ws.addressfilters.AddressAssociatedWithFilter;
import no.ums.ws.addressfilters.AddressFilterInfo;
import no.ums.ws.addressfilters.ArrayOfAddressAssociatedWithFilter;
import no.ums.ws.addressfilters.ArrayOfAddressFilterInfo;
import no.ums.ws.addressfilters.ArrayOfUAddress;
import no.ums.ws.addressfilters.ExecUpdateAddressFilter;
import no.ums.ws.addressfilters.FILTEROPERATION;
import no.ums.ws.addressfilters.UAddress;
import no.ums.ws.addressfilters.UAddressList;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.common.parm.UGisImportResultLine;
import no.ums.ws.pas.ArrayOfUGisImportLine;
import no.ums.ws.pas.ArrayOfUGisImportPropertyLine;
import no.ums.ws.pas.Pasws;
import no.ums.ws.pas.UGisImportLine;
import no.ums.ws.pas.UGisImportList;
import no.ums.ws.pas.UGisImportPropertyLine;
import no.ums.ws.pas.UGisImportPropertyList;
import no.ums.ws.pas.UGisImportResultsByStreetId;


/**
 * @author abhinava
 */
public class FilterServerCon extends Thread {

	private static final Log log = UmsLog.getLogger(FilterServerCon.class);
	
	private AddressFilterInfoVO filter = null;
	private String returndPk = "";
	private static ArrayList<AddressFilterInfoVO> filterList;
	private String operation = "";
	
	public  ArrayList<AddressFilterInfoVO> getFilterList() {
		return filterList;
	}
	
	public void execute(AddressFilterInfoVO filter,String operation) //throws Exception
	{
		this.filter = filter;
		this.operation = operation;
		start();
	}

	public void run() {
		if("fetch".equals(operation))
		{
			fetchFilterList();
			Variables.setFilterList(filterList);
		}
		else
		{
			executeWsFilter();
			if("insert".equals(operation))
				Variables.getFilterList().add(filter);
			if("update".equals(operation))
			{
				boolean result = Variables.getFilterList().remove(filter);
				Variables.getFilterList().add(filter);
			}
			if("delete".equals(operation))
			{
				fetchFilterList();
				Variables.setFilterList(filterList);
			}
			PAS.get_pas().actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_predefined_filters_changed"));
		}
	}
	
	private void executeWsFilter()
	{
		try {
			// Kjør ws kall med event vo
			ULOGONINFO logon = new ULOGONINFO();
			logon.setSzUserid(PAS.get_pas().get_userinfo().get_userid());
			logon.setSzCompid(PAS.get_pas().get_userinfo().get_compid());
			logon.setSzPassword(PAS.get_pas().get_userinfo().get_passwd());
			logon.setLComppk(PAS.get_pas().get_userinfo().get_comppk());
			logon.setLDeptpk(PAS.get_pas().get_userinfo()
					.get_current_department().get_deptpk());
			logon.setLUserpk(new Long(PAS.get_pas().get_userinfo().get_userpk()));
			String tester;
			//PAALERT save = new PAALERT();
			ExecUpdateAddressFilter save = new ExecUpdateAddressFilter();
			AddressFilterInfo info = new AddressFilterInfo();
			
			String par = filter.getParent();
			String pk = filter.getAlertpk();
			if (par == null)
				par = "0";
			if (pk == null)
				pk = "0";
			if (par.length() > 0 && par.startsWith("e"))
				par = par.substring(1);
			if (pk.length() > 0 && pk.startsWith("a"))
				pk = pk.substring(1);
			String ts = filter.getTimestamp();
			if (ts == null || ts.length() == 0)
				ts = "0";
       //	info.setAddressForFilterlist((ArrayOfAddressAssociatedWithFilter) filter.getAddressForFilterlist());
			info.setAddressType(filter.getAddressType());
			info.setLastupdatedDate(filter.getCreatTime());
			info.setDeptId(String.valueOf(filter.getDeptId()));
			info.setDescription(filter.getDescription());
			info.setFilterName(filter.getFilterName());
			info.setFilterOp(filter.getfilterOp());
			info.setFilterId(filter.getFilterId());
			if (!filter.getOperation().equals("delete")){
			GISFilterList gisList= filter.getGisFilterList();
			List<AddressAssociatedWithFilter> list= gisList.subList(0, gisList.size());
			info.setAddressForFilterlist(new ArrayOfAddressAssociatedWithFilter());
            for(Iterator<AddressAssociatedWithFilter> adr = list.iterator();
                  adr.hasNext();)
			   {
                info.getAddressForFilterlist().getAddressAssociatedWithFilter().add(adr.next());
			   }
			   }

			if (filter.getOperation().equals("insert"))
				save.setOperation(FILTEROPERATION.INSERT); 
				
			else if (filter.getOperation().equals("update"))
				save.setOperation(FILTEROPERATION.UPDATE);
			else if (filter.getOperation().equals("delete"))
				save.setOperation(FILTEROPERATION.DELETE);
			else {
				throw new Exception("Unknown operation " + filter.getOperation());
			}
            log.info("---------------Filter Operation started:"+save.getOperation()+"---------------");
			URL wsdl = new URL(vars.WSDL_ADDRESSFILTERS); // PAS.get_pas().get_sitename()
            QName service = new QName("http://ums.no/ws/addressfilters/",
					"AddressFilters");
			AddressFilterInfo res = new no.ums.ws.addressfilters.AddressFilters(wsdl, service)
					.getAddressFiltersSoap().execUpdateAddressFilter(save.getOperation(), logon, info);
			 log.info("---------------Filter Operation Completed:"+save.getOperation()+"---------------"+res.getFilterId());
			 filter.setFilterId(res.getFilterId());
			 filter.setCreationTime(res.getLastupdatedDate().toString());
			 GISShape m_shape= new GISShape(fetchAddressForImportedPredefinedFilters(res),true);
             m_shape.setM_gisfilterlist(fetchAddressForImportedPredefinedFilters(res));
             filter.submitShape(m_shape);
           } catch (Exception err) {
			Error.getError().addError("MainController",
					"Exception saving Alert", err, Error.SEVERITY_ERROR);
		}
	
	}
     private void fetchFilterList() {

//		log.debug("calling webservice to fetch filter list from backend");
		filterList = new ArrayList<AddressFilterInfoVO>();
		Set<String> keySet = new HashSet<String>();
        keySet.add("0");
		try {
			ULOGONINFO logon = new ULOGONINFO();
			logon.setSzUserid(PAS.get_pas().get_userinfo().get_userid());
			logon.setSzCompid(PAS.get_pas().get_userinfo().get_compid());
			logon.setSzPassword(PAS.get_pas().get_userinfo().get_passwd());
			logon.setLComppk(PAS.get_pas().get_userinfo().get_comppk());
			logon.setLDeptpk(PAS.get_pas().get_userinfo()
					.get_current_department().get_deptpk());
			logon.setLUserpk(new Long(PAS.get_pas().get_userinfo().get_userpk()));

			//URL wsdl = new URL(vars.WSDL_ADDRESSFILTERS); // PAS.get_pas().get_sitename()
														// +
														// "/ExecAlert/WS/ParmAdmin.asmx?WSDL");
			log.info("Filter Operation fetch all started");
			URL wsdl = new URL(vars.WSDL_ADDRESSFILTERS);
			QName service = new QName("http://ums.no/ws/addressfilters/",
					"AddressFilters");
			ArrayOfAddressFilterInfo res = new  no.ums.ws.addressfilters.AddressFilters(wsdl, service)
					.getAddressFiltersSoap().getListofAddressFilter(logon);
               for (AddressFilterInfo filterObj : res.getAddressFilterInfo()) {
                GISShape m_shape= new GISShape(fetchAddressForImportedPredefinedFilters(filterObj),true);
			    m_shape.setM_gisfilterlist(fetchAddressForImportedPredefinedFilters(filterObj));
                AddressFilterInfoVO filterVo = new AddressFilterInfoVO();
				filterVo.setFilterId(filterObj.getFilterId());
				filterVo.setFilterName(filterObj.getFilterName());
				filterVo.setDescription(filterObj.getDescription());
				filterVo.setCreationTime(filterObj.getLastupdatedDate().toString());
				filterVo.setDeptId(filterObj.getDeptId());
				filterVo.submitShape(m_shape);
			    filterList.add(filterVo);
           }
			log.info("---------------Filter Operation fetch all completed successfully---------------");
		} catch (Exception err) {
			System.out.println(err.toString());
			Error.getError().addError("MainFilterController",
					"Exception fetching Filter list", err, Error.SEVERITY_ERROR);
		}
		keySet.clear();
	}
    public no.ums.pas.importer.gis.GISFilterList fetchAddressForImportedPredefinedFilters(AddressFilterInfo Filter)
    {
        no.ums.pas.importer.gis.GISFilterList gisFilterList =new no.ums.pas.importer.gis.GISFilterList();
        ULOGONINFO logon = new ULOGONINFO();
        UserInfo u = PAS.get_pas().get_userinfo();
        logon.setLComppk(u.get_comppk());
        logon.setLDeptpk(u.get_current_department().get_deptpk());
        logon.setLUserpk(new Long(u.get_userpk()));
        logon.setSzCompid(u.get_compid());
        logon.setSzDeptid(u.get_current_department().get_deptid());
        logon.setSzPassword(u.get_passwd());
        logon.setSzStdcc(u.get_current_department().get_stdcc());
        logon.setLDeptpk(u.get_current_department().get_deptpk());
        logon.setSessionid(u.get_sessionid());
        logon.setJobid(WSThread.GenJobId());
        try
        {
            URL wsdl = new URL(vars.WSDL_ADDRESSFILTERS);      //PAS.get_pas().get_sitename() + "/ExecAlert/WS/Pas.asmx?WSDL");
            QName service = new QName("http://ums.no/ws/addressfilters/", "AddressFilters");

          //  UAddress address = new UAddress();
	      //  ArrayOfUAddress importaddresslines = new ArrayOfUAddress();
           UAddressList res = new  no.ums.ws.addressfilters.AddressFilters(wsdl, service)
            .getAddressFiltersSoap().getActualAddressesOfFilter(logon, Filter);

            if(res!=null){
                gisFilterList.fill(res);
            }
}
        catch(Exception e)
        {
            log.warn(e.getMessage(), e);
        }
        return  gisFilterList;
    }
}