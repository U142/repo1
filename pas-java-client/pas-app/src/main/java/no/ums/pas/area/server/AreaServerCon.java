package no.ums.pas.area.server;

import java.awt.Color;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.PAS;
import no.ums.pas.area.voobjects.AreaVO;
import no.ums.pas.core.Variables;
import no.ums.pas.core.ws.vars;
import no.ums.pas.importer.gis.GISRecord;
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
					line.setMunicipalid(fromline.get_municipal());
					line.setStreetid(fromline.get_streetid());
					line.setHouseno(fromline.get_houseno());
					line.setLetter(fromline.get_letter());
					line.setNamefilter1(fromline.get_name1());
					line.setNamefilter2(fromline.get_name2());
					line.setNLinenumber(fromline.get_lineno());
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
				areaList.add(area);
			}
		} catch (Exception err) {
			Error.getError().addError("MainAreaController",
					"Exception fetching area list", err, Error.SEVERITY_ERROR);
		}
	}

}
