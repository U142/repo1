package no.ums.pas.area.server;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.area.voobjects.AreaVO;
import no.ums.pas.core.Variables;
import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.core.ws.WSThread;
import no.ums.pas.core.ws.vars;
import no.ums.pas.importer.gis.GISList;
import no.ums.pas.importer.gis.GISRecord;
import no.ums.pas.importer.gis.GISRecordProperty;
import no.ums.pas.maps.defines.EllipseStruct;
import no.ums.pas.maps.defines.GISShape;
import no.ums.pas.maps.defines.MapPoint;
import no.ums.pas.maps.defines.MapPointLL;
import no.ums.pas.maps.defines.PolygonStruct;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.common.parm.ArrayOfUGisImportResultLine;
import no.ums.ws.common.parm.PAALERT;
import no.ums.ws.common.parm.PARMOPERATION;
import no.ums.ws.common.parm.UEllipse;
import no.ums.ws.common.parm.UGeminiStreet;
import no.ums.ws.common.parm.UGisImportResultLine;
import no.ums.ws.common.parm.UPolygon;
import no.ums.ws.common.parm.UPolypoint;
import no.ums.ws.common.parm.UShape;
import no.ums.ws.parm.admin.ArrayOfPAALERT;
import no.ums.ws.parm.admin.ParmAdmin;
import no.ums.ws.parm.admin.UPAALERTRESTULT;
import no.ums.ws.pas.ArrayOfUGisImportLine;
import no.ums.ws.pas.ArrayOfUGisImportPropertyLine;
import no.ums.ws.pas.Pasws;
import no.ums.ws.pas.UGisImportLine;
import no.ums.ws.pas.UGisImportList;
import no.ums.ws.pas.UGisImportPropertyLine;
import no.ums.ws.pas.UGisImportPropertyList;
import no.ums.ws.pas.UGisImportResultsByStreetId;


/**
 * @author sachinn
 */
public class AreaServerCon extends Thread {

	private static final Log log = UmsLog.getLogger(AreaServerCon.class);
	
	private AreaVO alert = null;
	private String returndPk = "";
	private static ArrayList<AreaVO> areaList;
	private String operation = "";
	
	public  ArrayList<AreaVO> getAreaList() {
		return areaList;
	}
	
	public void execute(AreaVO alert,String operation) //throws Exception
	{
		this.alert = alert;
		this.operation = operation;
		start();
	}

	public void run() {
		if("fetch".equals(operation))
		{
			fetchAreaList();
			Variables.setAreaList(areaList);
		}
		else
		{
			executeWsArea();
			if("insert".equals(operation))
				Variables.getAreaList().add(alert);
			if("update".equals(operation))
			{
				boolean result = Variables.getAreaList().remove(alert);
				Variables.getAreaList().add(alert);
			}
			if("delete".equals(operation))
			{
				fetchAreaList();
				Variables.setAreaList(areaList);
			}
			PAS.get_pas().actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_predefined_areas_changed"));
		}
	}
	
	private void executeWsArea()
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
			PAALERT save = new PAALERT();
			String par = alert.getParent();
			String pk = alert.getAlertpk();
			if (par == null)
				par = "0";
			if (pk == null)
				pk = "0";
			if (par.length() > 0 && par.startsWith("e"))
				par = par.substring(1);
			if (pk.length() > 0 && pk.startsWith("a"))
				pk = pk.substring(1);
			String ts = alert.getTimestamp();
			if (ts == null || ts.length() == 0)
				ts = "0";

			save.setFLocked(alert.getLocked());
			save.setLAddresstypes(alert.getAddresstypes());
			save.setLAlertpk(new Long(pk));
			save.setLParent(par);
			save.setLProfilepk(alert.getProfilepk());
			save.setLSchedpk(alert.getSchedpk());
			save.setLTemppk(new Long(pk));
			save.setLTimestamp(ts);
			save.setLValidity(alert.getValidity());
			save.setNExpiry(alert.get_LBAExpiry());
			save.setNMaxchannels(alert.getMaxChannels());
			if (alert.getArea() != null)
				save.setSzAreaid(alert.getArea().get_id());
			save.setSzDescription(alert.getDescription());
			save.setSzName(alert.getName());
			save.setSzOadc(alert.getOadc());

			save.setSzSmsMessage(alert.get_sms_message());
			save.setSzSmsOadc(alert.get_sms_oadc());

			save.setSzSmsOadc("");
			save.setSzSmsMessage("");
			save.setLSchedpk("0");

			if (alert.getOperation().equals("insert"))
				save.setParmop(PARMOPERATION.INSERT);
			else if (alert.getOperation().equals("update"))
				save.setParmop(PARMOPERATION.UPDATE);
			else if (alert.getOperation().equals("delete"))
				save.setParmop(PARMOPERATION.DELETE);
			else {
				throw new Exception("Unknown operation " + alert.getOperation());
			}
			UShape shape = null;
			UShape lbashape = null;
			if (alert.getM_shape().getClass().equals(PolygonStruct.class)) {
				PolygonStruct from = alert.getM_shape().typecast_polygon();
				shape = new UPolygon();
				UPolygon poly = (UPolygon) shape;
				List<UPolypoint> points = new ArrayList<UPolypoint>();
				for (int i = 0; i < from.get_size(); i++) {
					UPolypoint p = new UPolypoint();
					p.setLat(from.get_coor_lat(i));
					p.setLon(from.get_coor_lon(i));
					points.add(p);
				}
				poly.getPolypoint().addAll(points);
				// poly.setMArrayPolypoints(points);
			} else if (alert.getM_shape().getClass()
					.equals(EllipseStruct.class)) {
				EllipseStruct from = alert.getM_shape().typecast_ellipse();
				shape = new UEllipse();
				UEllipse ell = (UEllipse) shape;
				ell.setLat(from.get_center().get_lat());
				ell.setLon(from.get_center().get_lon());
				ell.setX(Math.abs(from.get_corner().get_lon()
						- from.get_center().get_lon()));
				ell.setY(Math.abs(from.get_corner().get_lat()
						- from.get_center().get_lat()));

			} else if (alert.getM_shape().getClass().equals(GISShape.class)) {
				shape = new UGeminiStreet();
				GISShape from = alert.getM_shape().typecast_gis();
				UGeminiStreet g = (UGeminiStreet) shape;
				ArrayOfUGisImportResultLine arr = new ArrayOfUGisImportResultLine();
				for (int i = 0; i < from.get_gislist().size(); i++) {
					GISRecord fromline = from.get_gislist().get(i);
					UGisImportResultLine line = new UGisImportResultLine();
					line.setBIsvalid(true);
					line.setMunicipalid(fromline.get_municipal()!=null ? fromline.get_municipal() : "");
					line.setStreetid(fromline.get_streetid() !=null ? fromline.get_streetid() : "");
					line.setHouseno(fromline.get_houseno() !=null ? fromline.get_houseno() : "");
					line.setLetter(fromline.get_letter() !=null ? fromline.get_letter() : "");
					line.setNamefilter1(fromline.get_name1() !=null ? fromline.get_name1() : "");
					line.setNamefilter2(fromline.get_name2() != null ? fromline.get_name2() : "");
					line.setNLinenumber(fromline.get_lineno());
					line.setApartmentid("");
					line.setGnr("");
					line.setBnr("");
					line.setFnr("");
					line.setSnr("");
					if(fromline instanceof GISRecordProperty)
					{
						GISRecordProperty propertyLine= (GISRecordProperty)fromline;
						line.setMunicipalid(propertyLine.get_municipal() != null ? propertyLine.get_municipal() : "");
						line.setGnr(propertyLine.getM_sz_gnr() != null ? propertyLine.getM_sz_gnr() : "");
						line.setBnr(propertyLine.getM_sz_bnr() != null ? propertyLine.getM_sz_bnr() : "");
						line.setFnr(propertyLine.getM_sz_fnr() != null ? propertyLine.getM_sz_fnr() : "");
						line.setSnr(propertyLine.getM_sz_snr() != null ? propertyLine.getM_sz_snr() : "");
						line.setNamefilter1(propertyLine.get_name1() != null ? propertyLine.get_name1() : "");
						line.setNamefilter2(propertyLine.get_name2() != null ? propertyLine.get_name2() : "");
						line.setNLinenumber(propertyLine.get_lineno());
						line.setPropertyField(true);
					}
					arr.getUGisImportResultLine().add(line);
				}
				g.setLinelist(arr);

			}

			shape.setColAlpha(alert.getM_shape().get_fill_color().getAlpha());
			shape.setColBlue(alert.getM_shape().get_fill_color().getBlue());
			shape.setColGreen(alert.getM_shape().get_fill_color().getGreen());
			shape.setColRed(alert.getM_shape().get_fill_color().getRed());

			save.setMShape(shape);
			save.setMLbaShape(lbashape);

			URL wsdl = new URL(vars.WSDL_PARMADMIN); // PAS.get_pas().get_sitename()
														// +
														// "/ExecAlert/WS/ParmAdmin.asmx?WSDL");
			// URL wsdl = new URL("http://localhost/WS/ParmAdmin.asmx?WSDL");
			QName service = new QName("http://ums.no/ws/parm/admin/",
					"ParmAdmin");
			UPAALERTRESTULT res = new ParmAdmin(wsdl, service)
					.getParmAdminSoap12().execAreaUpdate(logon, save);

			if (alert.getOperation().equals("delete")) {
				// XmlWriter writer = new XmlWriter();
				// writer.deleteFromTree(alert.getPk());
			}

			returndPk = "" + res.getPk();
			if (res.getPk() > 0) {
				// alert.setPk("a" + Long.toString(res.getPk()));
				alert.setPk("" + Long.toString(res.getPk()));
				alert.setTempPk("-1");
			}

		} catch (Exception err) {
			Error.getError().addError("MainController",
					"Exception saving Alert", err, Error.SEVERITY_ERROR);
		}
	
	}

	private void fetchAreaList() {

//		log.debug("calling webservice to fetch areas list from backend");
		areaList = new ArrayList<AreaVO>();
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

			URL wsdl = new URL(vars.WSDL_PARMADMIN); // PAS.get_pas().get_sitename()
														// +
														// "/ExecAlert/WS/ParmAdmin.asmx?WSDL");
			// URL wsdl = new URL("http://localhost/WS/ParmAdmin.asmx?WSDL");
			QName service = new QName("http://ums.no/ws/parm/admin/",
					"ParmAdmin");
			ArrayOfPAALERT res = new ParmAdmin(wsdl, service)
					.getParmAdminSoap12().getAreas(logon, 0);

			// res.getPAALERT();1212

			int col_r, col_g, col_b, col_a;
			for (PAALERT alert : res.getPAALERT()) {
				AreaVO area = new AreaVO();
				area.setPk("" + alert.getLAlertpk());
				area.setParent("" + alert.getLParent());
				area.setName("" + alert.getSzName());
				area.setDescription(alert.getSzDescription());
				UShape mshape = alert.getMShape();

				if (mshape instanceof UPolygon) {
//					log.debug("it's a polygon shape");
					UPolygon polyShape = (UPolygon) mshape;

					PolygonStruct shape = new PolygonStruct(PAS.get_pas()
							.get_mapsize());
					shape.shapeName = area.getName();
					col_r = (int) polyShape.getColRed();
					col_g = (int) polyShape.getColGreen();
					col_b = (int) polyShape.getColBlue();
					col_a = (int) polyShape.getColAlpha();
					Color col = new Color(col_r, col_g, col_b, col_a);
					shape.set_fill_color(col);
//					log.debug("polyShape.getPolypoint().size()="
//							+ polyShape.getPolypoint().size());
					for (UPolypoint polyPoint : polyShape.getPolypoint()) {
						shape.add_coor(polyPoint.getLon(), polyPoint.getLat());
					}
					if (polyShape.getPolypoint().size() > 0)
						area.submitShape(shape);
				} else if (mshape instanceof UEllipse) {
//					log.debug("it's an ellipse shape");
					UEllipse polyShape = (UEllipse) mshape;

					EllipseStruct shape = new EllipseStruct();
					shape.shapeName = area.getName();

					col_r = (int) polyShape.getColRed();
					col_g = (int) polyShape.getColGreen();
					col_b = (int) polyShape.getColBlue();
					col_a = (int) polyShape.getColAlpha();
					Color col = new Color(col_r, col_g, col_b, col_a);
					shape.set_fill_color(col);

					shape.set_ellipse_center(Variables.getNavigation(),
							new MapPoint(Variables.getNavigation(),
									new MapPointLL(polyShape.getLon(),
											polyShape.getLat())));
					shape.set_ellipse_corner(
							Variables.getNavigation(),
							new MapPoint(Variables.getNavigation(),
									new MapPointLL(polyShape.getX(), polyShape
											.getY())));
					area.submitShape(shape);
				}
				else if (mshape instanceof UGeminiStreet) {

					UGeminiStreet streetShape = (UGeminiStreet) mshape;
					GISList gisList = fetchAddressForImportedPredefinedArea(streetShape.getLinelist().getUGisImportResultLine());
					//gisList.fill(streetShape.getLinelist().getUGisImportResultLine());

					GISShape shape = new GISShape(gisList);

					shape.shapeName = area.getName();

					col_r = (int) streetShape.getColRed();
					col_g = (int) streetShape.getColGreen();
					col_b = (int) streetShape.getColBlue();
					col_a = (int) streetShape.getColAlpha();
					Color col = new Color(col_r, col_g, col_b, col_a);
					shape.set_fill_color(col);

					area.submitShape(shape);
				}

				if(keySet.contains(area.getParent()))
				{
					keySet.add(area.getAlertpk());
					areaList.add(area);
				}
			}
		} catch (Exception err) {
			Error.getError().addError("MainAreaController",
					"Exception fetching area list", err, Error.SEVERITY_ERROR);
		}
		keySet.clear();
	}

	public GISList fetchAddressForImportedPredefinedArea(List<UGisImportResultLine> lines)
	{
		GISList gisList = null;

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
	        URL wsdl = new URL(vars.WSDL_PAS); //PAS.get_pas().get_sitename() + "/ExecAlert/WS/Pas.asmx?WSDL");
	        QName service = new QName("http://ums.no/ws/pas/", "pasws");
	        UGisImportResultsByStreetId res = null;

	        boolean propertyImport = false;

	        //to determine the type of address import
            if(lines!=null && lines.size()>0)
            {
            	no.ums.ws.common.parm.UGisImportResultLine line = lines.get(0);
            	if(line.isPropertyField())
            		propertyImport=true;
            }

            if(propertyImport)
            {
            	ArrayOfUGisImportPropertyLine importPropertyLines = new ArrayOfUGisImportPropertyLine();
                UGisImportPropertyList propertySearch = new UGisImportPropertyList();
                propertySearch.setList(importPropertyLines);
                propertySearch.setDETAILTHRESHOLDLINES(PAS.get_pas().get_settings().getGisDownloadDetailThreshold());
                propertySearch.setSKIPLINES(0);

            	for (no.ums.ws.common.parm.UGisImportResultLine rl : lines) {
	        		UGisImportPropertyLine resline = new UGisImportPropertyLine();
                        resline.setMunicipalid(rl.getMunicipalid());
                        resline.setGnr(rl.getGnr());
                        resline.setBnr(rl.getBnr());
                        resline.setFnr(rl.getFnr());
                        resline.setSnr(rl.getSnr());
                        resline.setApartmentid(rl.getApartmentid());
                        resline.setNamefilter1(rl.getNamefilter1());
                        resline.setNamefilter2(rl.getNamefilter2());
                        importPropertyLines.getUGisImportPropertyLine().add(resline);
            	}

            	res = new Pasws(wsdl, service).getPaswsSoap12().getGisByPropertyIdV3(logon, propertySearch);
                gisList = new GISList();
                gisList.fillProperty(res);
            }
            else
            {
            	ArrayOfUGisImportLine importStreetLines = new ArrayOfUGisImportLine();
                UGisImportList streetSearch = new UGisImportList();
                streetSearch.setList(importStreetLines);
                streetSearch.setDETAILTHRESHOLDLINES(PAS.get_pas().get_settings().getGisDownloadDetailThreshold());
                streetSearch.setSKIPLINES(0);

            	for (no.ums.ws.common.parm.UGisImportResultLine rl : lines) {
	        		UGisImportLine resline = new UGisImportLine();
	        		resline.setMunicipalid(rl.getMunicipalid());
                    resline.setStreetid(rl.getStreetid());
                    resline.setHouseno(rl.getHouseno());
                    resline.setLetter(rl.getLetter());
                    resline.setApartmentid(rl.getApartmentid());
                    resline.setNamefilter1(rl.getNamefilter1());
                    resline.setNamefilter2(rl.getNamefilter2());
                    importStreetLines.getUGisImportLine().add(resline);
            	}

            	res = new Pasws(wsdl, service).getPaswsSoap12().getGisByStreetIdV2(logon, streetSearch);
            	gisList = new GISList();
            	gisList.fill(res);
            }
        }
        catch(Exception e)
        {
        	log.warn(e.getMessage(), e);
        }

        return gisList;
	}
}