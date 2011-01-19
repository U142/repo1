package no.ums.pas.core.ws;

import no.ums.pas.PAS;
import no.ums.pas.core.project.Project;
import no.ums.pas.core.Variables;
import no.ums.pas.maps.defines.*;
import no.ums.pas.status.LBASEND;
import no.ums.pas.status.StatusCode;
import no.ums.pas.status.StatusItemObject;
import no.ums.pas.status.StatusSending;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.calendarutils.DateTime;
import no.ums.ws.pas.status.ArrayOfLBACCode;
import no.ums.ws.pas.status.LBACCode;
import no.ums.ws.pas.status.LBALanguage;
import no.ums.ws.pas.status.ArrayOfLong;
import no.ums.ws.pas.status.PasStatus;
import no.ums.ws.pas.status.USMSINSTATS;
import no.ums.ws.pas.tas.ULBACOUNTRY;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

public class WSGetStatusItems extends WSThread
{
	private long sz_projectpk;
	private int sz_datefilter;
	private int sz_timefilter;
	private boolean b_automatic_update;
	private boolean b_use_loading_image = false;
	private long [] m_refno_list;
	
	public ActionListener get_callback() { return m_callback; }
	
	public WSGetStatusItems(ActionListener callback, long projectpk, int datefilter, int timefilter, long [] refno_list /*list of already downloaded sendings, no need to download shapes again*/, boolean automatic_update)
	{
		super(callback);
		b_automatic_update = automatic_update;
		sz_projectpk = projectpk;
		sz_datefilter = datefilter;
		sz_timefilter = timefilter;
		m_n_max_litem = 0;
		m_n_max_date = 0;
		m_n_max_time = 0;
		m_refno_list = refno_list;
	}

	@Override
	public void OnDownloadFinished() {
		try {
			//((OpenStatusFrame)get_parentframe()).onDownloadFinishedStatusItems();
			fire_set_datetimefilter(m_n_max_date, m_n_max_time);
			fire_update_complete();
			//Thread.sleep(50);
			//PAS.get_pas().actionPerformed(new ActionEvent("", ActionEvent.ACTION_PERFORMED, "act_center_all_polygon_sendings"));
			if(!b_automatic_update)
			{
				PAS.get_pas().get_eastcontent().get_statuspanel().m_icon_panel_main.getBoundsTing();
			}
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			Error.getError().addError("WSGetStatusItems","Exception in onDownloadFinished",e,1);
		}
		finally
		{
			if(b_use_loading_image)
				PAS.get_pas().get_mappane().SetIsLoading(false, "");			
		}
		
	}

	@Override
	public void call() throws Exception {
		if(sz_datefilter<=0 && sz_timefilter<=0)
		{
			PAS.get_pas().get_mappane().SetIsLoading(true, PAS.l("common_loading") + " " + PAS.l("mainmenu_status"));
			b_use_loading_image = true;
			set_datetimefilter(1, 1);
		}
		else
			b_use_loading_image = false;

		no.ums.ws.pas.status.ObjectFactory of = new no.ums.ws.pas.status.ObjectFactory();
		no.ums.ws.pas.status.ULOGONINFO logon = of.createULOGONINFO();
		no.ums.ws.pas.status.UStatusItemSearchParams search = of.createUStatusItemSearchParams();
		search.setLDateFilter(sz_datefilter);
		search.setLTimeFilter(sz_timefilter);
		search.setLProjectpk(sz_projectpk);
		ArrayOfLong arr = of.createArrayOfLong();
		for(int x = 0; x < m_refno_list.length; x++)
			arr.getLong().add(new Long(m_refno_list[x]));
		search.setLRefnoFilter(arr);
		
		WSFillLogoninfo.fill(logon, PAS.get_pas().get_userinfo());
		int n_retries = 0;
		int n_maxretries = 3;
		
		for(n_retries=0; n_retries < n_maxretries; n_retries++)
		{

			try
			{
				URL wsdl = new URL(vars.WSDL_PASSTATUS); //PAS.get_pas().get_sitename() + "/ExecAlert/WS/PasStatus.asmx?WSDL");
				//URL wsdl = new URL("http://localhost/ws/PasStatus.asmx?WSDL");
				QName service = new QName("http://ums.no/ws/pas/status", "PasStatus");
				ByteArrayInputStream zip_data = new ByteArrayInputStream(new PasStatus(wsdl, service).getPasStatusSoap12().getStatusItems(logon, search));
				Document doc = null;
				try{ //try gzip first
					GZIPInputStream gzip = new GZIPInputStream(zip_data);
					DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
					doc = db.parse(gzip);
				}
				catch(Exception e)
				{
					ZipInputStream zis = new ZipInputStream(zip_data);
					zis.getNextEntry();
					DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
					doc = db.parse(zis);
				}
				parseDoc(doc);
				break;			
			}
			catch(Exception e)
			{
				/*no.ums.pas.ums.errorhandling.Error.getError().addError(PAS.l("common_error"), "Error fetching status items WSGetStatusItems::run()", e, 1);
				if(n_retries+1<n_maxretries)
					no.ums.pas.ums.errorhandling.Error.getError().addError(PAS.l("common_error"), "Automatic retry will start. Failed to get statusitems", e, 1);
					*/
				if(b_use_loading_image)
					PAS.get_pas().get_mappane().SetIsLoading(false, "");
				throw e;
			}
		}
	}
	
	@Override
	protected String getErrorMessage() {
		return "Error fetching status items WSGetStatusItems::run()";
	}

	public void parseDoc(final Document doc)
	{
		/*try
		{
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run()
				{*/
	
		if(doc==null)
			return;
		
		/*
	sprintf(sz_xmltemp, "<SENDING sz_sendingname=\"%s\" l_refno=\"%d\" l_group=\"%d\" l_createdate=\"%d\" l_createtime=\"%d\" "
						"l_scheddate=\"%d\" l_schedtime=\"%d\" l_sendingstatus=\"%d\" l_comppk=\"%d\" l_deptpk=\"%d\" "
						"l_type=\"%d\" l_addresstypes=\"%d\" l_profilepk=\"%d\" l_queuestatus=\"%d\" l_totitem=\"%d\" "
						"l_proc=\"%d\" l_altjmp=\"%d\" l_alloc=\"%d\" l_maxalloc=\"%d\">",
						info->_sz_sendingname, info->_n_refno, info->_n_group, info->_n_createdate, info->_n_createtime,
						info->_n_scheddate, info->_n_schedtime, info->_n_sendingstatus, info->_n_companypk,
						info->_n_deptpk, info->_n_type, info->_n_addresstypes, info->_n_profilepk, info->_n_queuestatus,
						info->_n_totitem, info->_n_processed, info->_n_altjmp, info->_n_alloc, info->_n_maxalloc);

		*/
		
		NodeList sendings = doc.getElementsByTagName("SENDINGS");
		if(sendings!=null)
		{
			try {
				String sz_projectpk = sendings.item(0).getAttributes().getNamedItem("l_projectpk").getNodeValue();
				String sz_name = sendings.item(0).getAttributes().getNamedItem("sz_name").getNodeValue();
				String sz_created = sendings.item(0).getAttributes().getNamedItem("l_created").getNodeValue();
				String sz_updated = sendings.item(0).getAttributes().getNamedItem("l_updated").getNodeValue();
				String n_deptpk = sendings.item(0).getAttributes().getNamedItem("l_deptpk").getNodeValue();
				String l_sendings = sendings.item(0).getAttributes().getNamedItem("l_count").getNodeValue();
				Project p = new Project();
				p.set_projectpk(sz_projectpk);
				p.set_createtimestamp(sz_created);
				p.set_projectname(sz_name);
				p.set_updatetimestamp(sz_updated);
				p.set_saved();
				PAS.get_pas().activateProject(p);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		
		String [] sz_sendattr = { "sz_sendingname", "l_refno", "l_group", "l_createdate", "l_createtime", "l_scheddate", "l_schedtime",
								  "l_sendingstatus", "l_comppk", "l_deptpk", "l_type", "l_addresstypes", "l_profilepk", 
								  "l_queuestatus", "l_totitem", "l_proc", "l_altjmp", "l_alloc", "l_maxalloc", "sz_oadc", "l_qreftype",
								  "f_dynacall", "l_nofax", "l_linktype", "l_resendrefno", "sz_messagetext", "sz_actionprofilename",
								  "l_num_dynfiles"};
		
		
		/*parse polygon*/
		//NodeList list_polygon = doc.getElementsByTagName("POLYGON");
		NodeList list_sendings = doc.getElementsByTagName("SENDING");
		boolean b_hasvoice = false;

		if (list_sendings != null)
		{
			for(int i=0; i < list_sendings.getLength(); i++) {
				//PolygonStruct polygon = new PolygonStruct(get_pas().get_navigation(), get_pas().get_mappane().get_dimension());
				Node current = list_sendings.item(i);
				StatusSending sending;
				
				if(current!=null) {
					//fetch sendinginfo
					NamedNodeMap nnm_sendinginfo;
					String [] sz_values = new String[sz_sendattr.length];
					nnm_sendinginfo = current.getAttributes();
					for(int n_attr=0; n_attr < sz_sendattr.length; n_attr++)
					{
						try {
							if(nnm_sendinginfo.getNamedItem(sz_sendattr[n_attr])!=null)
								sz_values[n_attr] = new String(nnm_sendinginfo.getNamedItem(sz_sendattr[n_attr]).getNodeValue());
							else
								sz_values[n_attr] = "-1";
						}
						catch(Exception e)
						{
							sz_values[n_attr] = "-1";
							System.out.println(e.getMessage());
							e.printStackTrace();
							Error.getError().addError("XMLGetStatusItems","Exception in parseDoc",e,1);
						}
					}
					try {
						sending = new StatusSending(sz_values);
						if(sending.get_type()==1)
							b_hasvoice = true;
						sending.setProjectpk(new Long(sz_projectpk).toString());
					} catch(Exception e) {
						System.out.println(e.getMessage());
						e.printStackTrace();
						Error.getError().addError("XMLGetStatusItems","Exception in parseDoc",e,1);
						continue;
					}
					
					if(sending.get_type()==5 || sending.get_type()==4)
					{
						NodeList list_smsstats = current.getChildNodes();
						try
						{
							if(list_smsstats!=null)
							{
								for(int stat=0; stat < list_smsstats.getLength(); stat++)
								{
									Node node_stat = list_smsstats.item(stat);
									if(node_stat.getNodeName().equals("SMSINSTATS"))
									{
										//System.out.println("SMS in stats found");
										NodeList list_answers = node_stat.getChildNodes();
										for(int answ = 0; answ < list_answers.getLength(); answ++)	
										{
											Node node_answer = list_answers.item(answ);
											/*if(node_answer.getNodeName().equals("ANSWER"))
											{
												System.out.println("Answer found");
											}*/
											try
											{
												String refno = node_answer.getAttributes().getNamedItem("l_refno").getNodeValue();
												String answercode = node_answer.getAttributes().getNamedItem("l_answercode").getNodeValue();
												String desc = node_answer.getAttributes().getNamedItem("sz_description").getNodeValue();
												String count = node_answer.getAttributes().getNamedItem("l_count").getNodeValue();
												//System.out.println("answer refno " + refno + " " + answercode + " " + desc);
												USMSINSTATS _smsstat = new USMSINSTATS();
												_smsstat.setLRefno(new Integer(refno).intValue());
												_smsstat.setLAnswercode(new Integer(answercode).intValue());
												_smsstat.setSzDescription(desc);
												_smsstat.setLCount(new Integer(count).intValue());
												sending.addSmsInStats(_smsstat);
											
											}
											catch(Exception e)
											{
												
											}
										}
									}
								}
							}
						}
						catch(Exception e)
						{
							
						}
					}
					switch(sending.get_group()) {
						case 3:
							sending.set_shape(new PolygonStruct(PAS.get_pas().get_mappane().get_dimension()));
							NodeList list_points = current.getChildNodes(); //polygon
							if(list_points!=null) {
								String[] sz_itemattr = { "lon", "lat" };
								Node node_point;
								NamedNodeMap nnm_point;
								Double f_lon, f_lat;
								//System.out.println("Polypoints = " + list_points.getLength());
								
								for(int n_items=0; n_items < list_points.getLength(); n_items++)
								{
									f_lon = null;
									f_lat = null;
									node_point = list_points.item(n_items);
									nnm_point = node_point.getAttributes();
									//get_pas().add_event("Parsing polygon");
									if(node_point==null || nnm_point==null)
										continue;
									try {
										//get_pas().add_event("Poly: " + nnm_point.getNamedItem(sz_itemattr[0]).getNodeValue() + " / " + nnm_point.getNamedItem(sz_itemattr[1]).getNodeValue());
										if(nnm_point.getNamedItem(sz_itemattr[0])!=null)
											f_lon = new Double(nnm_point.getNamedItem(sz_itemattr[0]).getNodeValue());
										else
											f_lon = null;
										if(nnm_point.getNamedItem(sz_itemattr[1])!=null)
											f_lat = new Double(nnm_point.getNamedItem(sz_itemattr[1]).getNodeValue());
										else
											f_lat = null;
										if(f_lon!=null && f_lat!=null)
										{
											//System.out.println(f_lon + " , " + f_lat);
											sending.get_polygon().add_coor(f_lon, f_lat);
										}
									}
									catch(Exception e)
									{
										System.out.println(e.getMessage());
										e.printStackTrace();
										Error.getError().addError("XMLGetStatusItems","Exception in parseDoc",e,1);
									}
								}
								//m_arr_polygons.add(m_polygon);
							}
							break;
						case 4: //gemini
						{
							boolean b = true;
							double lbo = 0, rbo = 0, ubo = 0, bbo = 0;
							Node node = null;
							GISShape shape = new GISShape(null);
							//GISShape shape = new GISShape();
							NodeList nl = current.getChildNodes();
							for(int x = 0; x < nl.getLength(); x++)
							{
								node = nl.item(x);
								if(node==null)
									b = false;
								else
									b = true;
								if(b)
								{
									if(node.getNodeName().equals("BOUNDS"))
									{
										String [] sz_itemattr = { "lbo", "rbo", "ubo", "bbo" };
										NamedNodeMap nnm_bounds = node.getAttributes();
										if(node==null || nnm_bounds==null)
											break;
										lbo = new Double(nnm_bounds.getNamedItem(sz_itemattr[0]).getNodeValue());
										rbo = new Double(nnm_bounds.getNamedItem(sz_itemattr[1]).getNodeValue());
										ubo = new Double(nnm_bounds.getNamedItem(sz_itemattr[2]).getNodeValue());
										bbo = new Double(nnm_bounds.getNamedItem(sz_itemattr[3]).getNodeValue());
									}
								}
							}
							if(lbo!=0 && rbo!=0 && ubo!=0 && bbo!=0)
								shape.SetBounds(lbo, rbo, ubo, bbo);
							sending.set_shape(shape);

							break;
						}
						case 5: //TAS country
						{
							double lbo = 0, rbo = 0, ubo = 0, bbo = 0;
							Node node = null;
							try
							{
								TasStruct shape = new TasStruct();
								boolean b = true;
								NodeList nl = current.getChildNodes();
								for(int x = 0; x < nl.getLength(); x++)
								{
									node = nl.item(x);
									if(node==null)
									{
										b = false;
									}
									else
										b = true;
									if(b)
									{
										if(node.getNodeName().equals("TASCOUNTRIES"))
										{
											NodeList nl_mun = node.getChildNodes();
											for(int mun = 0; mun < nl_mun.getLength(); mun++)
											{
												Node node_municipal = nl_mun.item(mun);
												if(node_municipal!=null)
												{
													NamedNodeMap nnm_mun = node_municipal.getAttributes();
													if(nnm_mun!=null)
													{
														//Municipal municipal = new Municipal(nnm_mun.getNamedItem("id").getNodeValue(), nnm_mun.getNamedItem("name").getNodeValue());
														ULBACOUNTRY country = new ULBACOUNTRY();
														country.setSzIso(nnm_mun.getNamedItem("iso").getNodeValue());
														country.setSzName(nnm_mun.getNamedItem("name").getNodeValue());
														country.setLCc(new Integer(nnm_mun.getNamedItem("cc").getNodeValue()));
														country.setLIsoNumeric(new Integer(nnm_mun.getNamedItem("iso-n").getNodeValue()));
														shape.AddCountry(country, true);
													}
												}
											}
										}
										else if(node.getNodeName().equals("BOUNDS"))
										{
											String [] sz_itemattr = { "lbo", "rbo", "ubo", "bbo" };
											NamedNodeMap nnm_bounds = node.getAttributes();
											if(node==null || nnm_bounds==null)
												break;
											lbo = new Double(nnm_bounds.getNamedItem(sz_itemattr[0]).getNodeValue());
											rbo = new Double(nnm_bounds.getNamedItem(sz_itemattr[1]).getNodeValue());
											ubo = new Double(nnm_bounds.getNamedItem(sz_itemattr[2]).getNodeValue());
											bbo = new Double(nnm_bounds.getNamedItem(sz_itemattr[3]).getNodeValue());
										}
									}
								}
								//if(lbo!=0 && rbo!=0 && ubo!=0 && bbo!=0) // Fjernet denne pga resend fører til -9999.0,-9999.0,-9999.0,-9999.0 og ødelegger statusoversikten
									shape.SetBounds(lbo, rbo, ubo, bbo);
								sending.set_shape(shape);

							}
							catch(Exception e)
							{
								
							}
							break;
						}
						case 9: //MUNICIPAL
						{
							double lbo = 0, rbo = 0, ubo = 0, bbo = 0;
							Node node = null;
							try
							{
								MunicipalStruct shape = new MunicipalStruct();
								boolean b = true;
								//node = current.getFirstChild().getNextSibling();
								NodeList nl = current.getChildNodes();
								for(int x = 0; x < nl.getLength(); x++)
								{
									node = nl.item(x);
									if(node==null)
									{
										b = false;
									}
									else
										b = true;
									if(b)
									{
										if(node.getNodeName().equals("MUNICIPALLIST"))
										{
											NodeList nl_mun = node.getChildNodes();
											for(int mun = 0; mun < nl_mun.getLength(); mun++)
											{
												Node node_municipal = nl_mun.item(mun);
												if(node_municipal!=null)
												{
													NamedNodeMap nnm_mun = node_municipal.getAttributes();
													if(nnm_mun!=null)
													{
														Municipal municipal = new Municipal(nnm_mun.getNamedItem("id").getNodeValue(), nnm_mun.getNamedItem("name").getNodeValue());
														shape.AddMunicipal(municipal, true);
													}
												}
											}
										}
										else if(node.getNodeName().equals("BOUNDS"))
										{
											String [] sz_itemattr = { "lbo", "rbo", "ubo", "bbo" };
											NamedNodeMap nnm_bounds = node.getAttributes();
											if(node==null || nnm_bounds==null)
												break;
											lbo = new Double(nnm_bounds.getNamedItem(sz_itemattr[0]).getNodeValue());
											rbo = new Double(nnm_bounds.getNamedItem(sz_itemattr[1]).getNodeValue());
											ubo = new Double(nnm_bounds.getNamedItem(sz_itemattr[2]).getNodeValue());
											bbo = new Double(nnm_bounds.getNamedItem(sz_itemattr[3]).getNodeValue());
										}
									}
								}
								if(lbo!=0 && rbo!=0 && ubo!=0 && bbo!=0)
									shape.SetBounds(lbo, rbo, ubo, bbo);
								sending.set_shape(shape);
							}
							catch(Exception e)
							{
								node = null;
							}
							
							break;
						}
						case 8: //ELLIPSE
							//NodeList list_ellipse = current.getFirstChild(); //getChildNodes(); //should be one ellipse node
							//if(list_ellipse!=null) 
							{
								
								String [] sz_itemattr = { "f_centerx", "f_centery", "f_radiusx", "f_radiusy" };
								Node node = null;
								try
								{
									node = current.getFirstChild().getNextSibling();
									while(node!=null)
									{
										if(node==null)
											break;
										if(node.getNodeName().equals("ELLIPSE"))
											break;
										node = node.getNextSibling();//current.getFirstChild().getNextSibling();										
									}
								}
								catch(Exception e)
								{
									node = null;
									//continue;
								}
								if(node!=null)
								{
									NamedNodeMap nnm_ellipse;
									nnm_ellipse = node.getAttributes();
									if(node==null || nnm_ellipse==null)
										break;
									try {
										Double f_centerx, f_centery, f_radiusx, f_radiusy;
										if(nnm_ellipse.getNamedItem(sz_itemattr[0])!=null)
											f_centerx = new Double(nnm_ellipse.getNamedItem(sz_itemattr[0]).getNodeValue());
										else
											f_centerx = null;
										if(nnm_ellipse.getNamedItem(sz_itemattr[1])!=null)
											f_centery = new Double(nnm_ellipse.getNamedItem(sz_itemattr[1]).getNodeValue());
										else
											f_centery = null;
										if(nnm_ellipse.getNamedItem(sz_itemattr[2])!=null)
											f_radiusx = new Double(nnm_ellipse.getNamedItem(sz_itemattr[2]).getNodeValue());
										else
											f_radiusx = null;
										if(nnm_ellipse.getNamedItem(sz_itemattr[3])!=null)
											f_radiusy = new Double(nnm_ellipse.getNamedItem(sz_itemattr[3]).getNodeValue());
										else
											f_radiusy = null;
										
										EllipseStruct e = new EllipseStruct();
										//MapPoint p_center = new MapPoint(Variables.NAVIGATION, new MapPointLL(f_centerx.doubleValue(), f_centery.doubleValue()));
										//MapPoint p_corner = new MapPoint(Variables.NAVIGATION, new MapPointLL(f_centerx.doubleValue() + f_radiusx.doubleValue(), f_centery.doubleValue() + f_radiusy.doubleValue()));
										MapPoint p_center = new MapPoint(Variables.getNavigation(), new MapPointLL(f_centerx==null?0.0:f_centerx.doubleValue(), f_centery==null?0.0:f_centery.doubleValue()));
										MapPoint p_corner = new MapPoint(Variables.getNavigation(), new MapPointLL((f_centerx==null?0.0:f_centerx.doubleValue()) + (f_radiusx==null?0.0:f_radiusx.doubleValue()), (f_centery==null?0.0:f_centery.doubleValue()) + (f_radiusy==null?0.0:f_radiusy.doubleValue())));
										e.set_ellipse(Variables.getNavigation(), p_center, p_corner);
										sending.set_shape(e);
									} catch(Exception e)  {
										Error.getError().addError("XMLGetStatusItems", "Exception in parseDoc", e, 1);
									}
								}
							}
							
							break;
							
					}
					try {
						//System.out.println("sending " + sending.get_refno() + " with " + sending.get_polygon().get_size() + " points");
						get_callback().actionPerformed(new ActionEvent(sending, ActionEvent.ACTION_PERFORMED, "act_add_sending"));
						//get_pas().get_statuscontroller().add_sending(sending);
					} catch(Exception e) {
						System.out.println(e.getMessage());
						e.printStackTrace();
						Error.getError().addError("XMLGetStatusItems","Exception in parseDoc",e,1);
					}
				}
			}
		}
		
		list_sendings = doc.getElementsByTagName("LBASEND");
		NodeList list_cbstatus;
		
		String [] sz_lbasend_attr = { "l_parentrefno", "l_cbtype", "l_status", "l_response", "l_items", "l_proc", "l_retries", "l_requesttype", "sz_jobid", "sz_areaid", "f_simulation", "l_operator", "sz_operator" };
		String [] sz_lbasend_vals = new String[sz_lbasend_attr.length];
		String [] sz_lbahistcc_attr = { "sz_ccode", "l_delivered", "l_expired", "l_failed", "l_unknown", "l_submitted", "l_queued", "l_subscribers", "l_operator" };
		String [] sz_lbatext_attr = { "name", "oadc", "text" };
		String [] sz_lbatextcc_attr = { "cc" };
		
		List<LBASEND> lbasendlist = new ArrayList<LBASEND>();
		if(list_sendings!=null)
		{
			LBASEND lbasend = null;
			for(int i=0; i < list_sendings.getLength(); i++)
			{
				//PARSE LBASEND ATTRIBUTES
				Node current = list_sendings.item(i);
				if(current!=null)
				{
					NamedNodeMap nnm_lbasend = current.getAttributes();
					for(int n_attr = 0; n_attr < sz_lbasend_attr.length; n_attr++)
					{
						try
						{
							if(nnm_lbasend.getNamedItem(sz_lbasend_attr[n_attr])!=null)
								sz_lbasend_vals[n_attr] = new String(nnm_lbasend.getNamedItem(sz_lbasend_attr[n_attr]).getNodeValue());
						}
						catch(Exception e)
						{
							sz_lbasend_vals[n_attr] = "0";
						}
					}
					lbasend = new LBASEND(sz_lbasend_vals);
					lbasendlist.add(lbasend);
					
					//PARSE LBAHISTCC/CELL/TIMESTAMP/LBALANGUAGES records
					NodeList nl_lbahist = current.getChildNodes();
					for(int hist = 0; hist < nl_lbahist.getLength(); hist++)
					{
						Node lbahist = nl_lbahist.item(hist);
						String sz_nodetype = lbahist.getNodeName();
						NamedNodeMap nnm_lbahist = lbahist.getAttributes();
						if(sz_nodetype.equals("LBAHISTCC"))
						{
							String [] sz_lbahist_vals = new String[sz_lbahistcc_attr.length];
							
							for(int n_attr = 0; n_attr < sz_lbahistcc_attr.length; n_attr++)
							{
								try
								{
									if(nnm_lbahist.getNamedItem(sz_lbahistcc_attr[n_attr])!=null)
										sz_lbahist_vals[n_attr] = new String(nnm_lbahist.getNamedItem(sz_lbahistcc_attr[n_attr]).getNodeValue());
								}
								catch(Exception e)
								{
									sz_lbahist_vals[n_attr] = "0";
								}
							}
							lbasend.addHistCC(sz_lbahist_vals);
						}
						else if(sz_nodetype.equals("LBAHISTCELL"))
						{
							//String [] sz_lbahist_vals = new String[sz_lbahistcell_attr.length];
							//lbasend.addHistCell(sz_lbahist_vals);
						}
						else if(sz_nodetype.equals("LBASEND_TS"))
						{
							NodeList list_ts = lbahist.getChildNodes();
							for(int ts = 0; ts < list_ts.getLength(); ts++)
							{
								try
								{
									Node tsnode = list_ts.item(ts);
									NamedNodeMap nnm_ts = tsnode.getAttributes();
									/*String l_status = nnm_ts.getNamedItem("l_status").getNodeValue();
									String l_timestamp = nnm_ts.getNamedItem("l_ts").getNodeValue();*/
									String [] tsvals = new String[] { nnm_ts.getNamedItem("l_status").getNodeValue(),
											nnm_ts.getNamedItem("l_ts").getNodeValue(), nnm_ts.getNamedItem("l_operator").getNodeValue() };
									
									lbasend.addSendTS(tsvals);
								}
								catch(Exception e)
								{
									
								}
							}
						}
						else if(sz_nodetype.equals("LBALANGUAGES"))
						{
							NodeList list_lang = lbahist.getChildNodes();
							for(int la = 0; la < list_lang.getLength(); la++)
							{
								try
								{
									Node langnode = list_lang.item(la);
									NamedNodeMap nnm_lang = langnode.getAttributes();
									String [] langvals = new String [] { 
											nnm_lang.getNamedItem("textpk").getNodeValue(),
											nnm_lang.getNamedItem("name").getNodeValue(),
											nnm_lang.getNamedItem("oadc").getNodeValue(), 
											nnm_lang.getNamedItem("text").getNodeValue() 
									};
									String cclist = nnm_lang.getNamedItem("ccodelist").getNodeValue();
									LBALanguage lbalang = new LBALanguage();
									//lbalang.setLTextpk(Long.parseLong(langvals[0]));
									lbalang.setSzName(langvals[1]);
									lbalang.setSzCbOadc(langvals[2]);
									lbalang.setSzText(langvals[3]);
									lbalang.setMCcodes(new ArrayOfLBACCode());
									
									String [] ccs = cclist.split(",");
									for(int ccodes = 0; ccodes < ccs.length; ccodes++)
									{
										LBACCode lbaccode = new LBACCode();
										lbaccode.setCcode(ccs[ccodes]);
										lbalang.getMCcodes().getLBACCode().add(lbaccode);
									}
									
										
									/*NodeList list_ccodes = langnode.getChildNodes();
									
									for(int ccodes = 0; ccodes < list_ccodes.getLength(); ccodes++)
									{
										try
										{
											Node ccnode = list_ccodes.item(ccodes);
											System.out.println(ccnode.getNodeName());
											NamedNodeMap nnm_ccode = ccnode.getAttributes();
											String cc = nnm_ccode.getNamedItem("cc").getNodeValue();
											LBACCode lbaccode = new LBACCode();
											lbaccode.setCcode(cc);
											lbalang.getMCcodes().getLBACCode().add(lbaccode);
										}
										catch(Exception e)
										{
											
										}
									}*/
									lbasend.addLBALanguage(lbalang);
								}
								catch(Exception e)
								{
									//e.printStackTrace();
								}
							}
						}
					
					}
					
				
				}
			
			}
			if(lbasendlist.size()>0)
				fire_update_lbasendings(lbasendlist);
		}
		PAS.get_pas().get_statuscontroller().enableMainStatusVoice(b_hasvoice);
		if(lbasendlist.size()==0)
			PAS.get_pas().get_statuscontroller().updateLBA(lbasendlist);
			
		//<STATUS l_projectpk="500000000000184" l_dst="0" sz_ccode="0047" f_simulation="1" l_recipients="120" />
		/*String [] sz_cbstatus_attr = { "l_projectpk", "l_dst", "sz_ccode", "f_simulation", "l_recipients", "sz_oadc", "l_pri", "l_expecteditems", "l_items", "l_proc", "l_sendingstatus" };
		
		ArrayList cb_sendings = new ArrayList();
		if (list_sendings != null)
		{
			for(int i=0; i < list_sendings.getLength(); i++) {
				Node current = list_sendings.item(i);
				String sz_parent_refno = "-1";
				if(current!=null) {
					sz_parent_refno = current.getAttributes().getNamedItem("l_parentrefno").getNodeValue();
					StatusCellBroadcast cb_sending = new StatusCellBroadcast(sz_parent_refno);
					list_cbstatus = current.getChildNodes();
					for(int s=0; s < list_cbstatus.getLength(); s++)
					{
						String [] sz_values = new String[sz_cbstatus_attr.length];
						Node node_cbstatus = list_cbstatus.item(s);
						//Status tags
						NamedNodeMap nnm_cbstatus = node_cbstatus.getAttributes();
						if(nnm_cbstatus==null)
							continue;
						for(int n_attr=0; n_attr < sz_cbstatus_attr.length; n_attr++)
						{
							try {
								if(nnm_cbstatus.getNamedItem(sz_cbstatus_attr[n_attr])!=null)
									sz_values[n_attr] = new String(nnm_cbstatus.getNamedItem(sz_cbstatus_attr[n_attr]).getNodeValue());
							}
							catch(Exception e)
							{
								System.out.println(e.getMessage());
								e.printStackTrace();
								Error.getError().addError("XMLGetStatusItems","Exception in parseDoc",e,1);
							}							
						}
						cb_sending.addStatusItem(sz_values);
						cb_sendings.add(cb_sending);
						//execute statusupdate for parent refno
						
					}
				}
			}
		}
		fire_update_cbsendings(cb_sendings);*/
			
		
		/*parse header*/
		NodeList list_itemlist = doc.getElementsByTagName("STATUSITEMLIST");
		if (list_itemlist == null)
			return;
		Node current = list_itemlist.item(0);
		if(current==null)
			return;
		NamedNodeMap nnm_itemlist = current.getAttributes();
		if(nnm_itemlist==null)
			return;
		Node node_projectpk= nnm_itemlist.getNamedItem("l_projectpk");
		Node node_refno = nnm_itemlist.getNamedItem("l_refno");
		Node node_records = nnm_itemlist.getNamedItem("records");
		Node node_totitem = nnm_itemlist.getNamedItem("totitem");
		Node node_lbo = nnm_itemlist.getNamedItem("lbo");
		Node node_rbo = nnm_itemlist.getNamedItem("rbo");
		Node node_ubo = nnm_itemlist.getNamedItem("ubo");
		Node node_bbo = nnm_itemlist.getNamedItem("bbo");
		Node node_parsing = nnm_itemlist.getNamedItem("parsing");
		Node node_queue = nnm_itemlist.getNamedItem("queue");
		Node node_sending = nnm_itemlist.getNamedItem("sending");
		
		
		if(node_refno==null || node_records==null || node_totitem==null || node_parsing==null || node_queue==null || node_sending == null)
			return;
			
		String sz_records, sz_parsing, sz_queue, sz_sending;
		//sz_refno	= node_refno.getNodeValue();
		sz_records	= node_records.getNodeValue();
		//sz_totitem	= node_totitem.getNodeValue();
		sz_parsing	= node_parsing.getNodeValue();
		sz_queue	= node_queue.getNodeValue();
		sz_sending	= node_sending.getNodeValue();
		set_parsing(new Integer(sz_parsing).intValue());
		set_queue(new Integer(sz_queue).intValue());
		set_sending(new Integer(sz_sending).intValue());

		//get_pas().add_event("Inflating and parsing status items for refno " + sz_refno + " (" + sz_records + " records)");

		//get_pas().add_event("lbo: " + node_lbo.getNodeValue() + " , rbo: " + node_rbo.getNodeValue() + " , ubo: " + node_ubo.getNodeValue() + " , bbo: " + node_bbo.getNodeValue());

		int n_records = new Integer(sz_records).intValue();
		
		Node node_item;
		NamedNodeMap nnm_items;
		NodeList list_items = current.getChildNodes();
		String[] sz_values;
		/*try {
			NavStruct nav_init = new NavStruct(new Double(node_lbo.getNodeValue()).doubleValue(), new Double(node_rbo.getNodeValue()).doubleValue(),
					   new Double(node_ubo.getNodeValue()).doubleValue(), new Double(node_bbo.getNodeValue()).doubleValue());
			get_callback().actionPerformed(new ActionEvent(nav_init, ActionEvent.ACTION_PERFORMED, "act_set_nav_init"));
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			Error.getError().addError("XMLGetStatusItems","Exception in parseDoc",e,1);
		}*/

	
			/*parse items*/
			list_items = current.getChildNodes();
			if(list_items==null)
				return;
	
			String[] sz_itemattr = { "l_refno", "l_item", "l_adrpk", "l_lon", "l_lat", "sz_adrname", "sz_postaddr", "sz_postno", "sz_postarea", "l_date", "l_time", "l_status", "sz_number", "l_tries", "l_channel", "l_pcid", "l_seconds", "l_changedate", "l_changetime", "l_ldate", "l_ltime" };
			
			StatusItemObject obj;			
			double f_percent = 0;
			int n_show_percent = 0;
			double f_percent_pr_item;
			int n_record_count = n_records;
			if(n_record_count > 0)
				f_percent_pr_item = ((double)30 / n_record_count);
			else
				f_percent_pr_item = 100;
			
			for(int n_items=0; n_items < list_items.getLength(); n_items++) //list_items.getLength() == sz_records
			{
				node_item = list_items.item(n_items);
				nnm_items = node_item.getAttributes();
				if(nnm_items==null)
					continue;
				//sz_values = new String[new Integer(sz_records).intValue()];
				sz_values = new String[sz_itemattr.length + 1];
				//sz_values[0] = sz_refno;
				for(int n_attr=0; n_attr < sz_itemattr.length; n_attr++)
				{
					try {
						if(nnm_items.getNamedItem(sz_itemattr[n_attr])!=null)
							sz_values[n_attr] = new String(nnm_items.getNamedItem(sz_itemattr[n_attr]).getNodeValue());
						//else
						//	sz_values[n_attr] = "0";
					}
					catch(Exception e)
					{
						System.out.println(e.getMessage());
						e.printStackTrace();
						//sz_values[n_attr] = new String("0");
						Error.getError().addError("XMLGetStatusItems","Exception in parseDoc",e,1);
					}
				}
				int n_index = new Integer(sz_values[1]).intValue() - 1;
				try {
					obj = new StatusItemObject(sz_values);
					if(obj.get_item() > m_n_max_litem)
						m_n_max_litem = obj.get_item();
					fire_statusitem(obj);
					//m_statusitems.add(obj);
					//m_statusitems.set(n_index, obj);
					//MsgBox message = new  MsgBox(new Frame(""), sz_values[0] + " " + sz_values[1] + " " + sz_values[2] + " " + sz_values[3], true);
				} catch(Exception e) { 
					System.out.println(e.getMessage());
					e.printStackTrace();
					Error.getError().addError("XMLGetStatusItems","Exception in parseDoc",e,1);
				}				
			}
			fire_set_itemfilter(new Integer(m_n_max_litem));
		//}
		
		/*parse the statuscodes*/
		//get_pas().add_event("Inflating and parsing statuscodes");
		//m_statuscodes = new ArrayList();
		
		NodeList list_codelist = doc.getElementsByTagName("STATUSCODES");
		if(list_codelist==null)
			return;
		current = list_codelist.item(0);
		if(current==null)
			return;
		NodeList list_codes = current.getChildNodes();
		if(list_codes==null)
			return;
		String[] sz_codeattr = { "l_status", "sz_name", "f_userdefined" };
		StatusCode obj_status;
		//m_statuscodes.add(new StatusCode(-1000, "Parsing (no details)", true, get_parsing()));
		//m_statuscodes.add(new StatusCode(-1001, "Queue (no details)", true, get_queue()));
		//m_statuscodes.add(new StatusCode(-1002, "Sending (no details)", true, get_sending()));
		ActionEvent event_code;
		fire_event(new ActionEvent(new StatusCode(-1000, PAS.l("common_parsing") + " (" + PAS.l("common_no_details") + ")", true, get_parsing(), false), ActionEvent.ACTION_PERFORMED, "act_insert_statuscode"));
		fire_event(new ActionEvent(new StatusCode(-1001, PAS.l("common_queue") + " (" + PAS.l("common_queue") + ")", true, get_queue(), false), ActionEvent.ACTION_PERFORMED, "act_insert_statuscode"));
		fire_event(new ActionEvent(new StatusCode(-1002, PAS.l("common_sending") + " (" + PAS.l("common_sending") + ")", true, get_sending(), false), ActionEvent.ACTION_PERFORMED, "act_insert_statuscode"));
		
		for(int n_items=0; n_items < list_codes.getLength(); n_items++)
		{
			node_item = list_codes.item(n_items);
			nnm_items = node_item.getAttributes();
			if(nnm_items==null)
				continue;
			sz_values = new String[sz_codeattr.length];
			for(int n_attr=0; n_attr < sz_codeattr.length; n_attr++)
			{
				try {
					if(nnm_items.getNamedItem(sz_codeattr[n_attr])!=null)
						sz_values[n_attr] = new String(nnm_items.getNamedItem(sz_codeattr[n_attr]).getNodeValue());
				}
				catch(Exception e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					Error.getError().addError("XMLGetStatusItems","Exception in parseDoc",e,1);
				}
			}
			try {
				obj_status = new StatusCode(sz_values);
				fire_statuscode(obj_status);
				//m_statuscodes.add(obj_status);
			}
			catch(Exception e) { 
				System.out.println(e.getMessage());
				e.printStackTrace();
				Error.getError().addError("XMLGetStatusItems","Exception in parseDoc",e,1);
			}
			
		}
					//OnDownloadFinished();				
		/*		}
			});
		}
		catch(Exception e)
		{
			
		}*/
	}	
	
	int m_n_parsing = 0;
	int m_n_queue = 0;
	int m_n_sending = 0;
	
	int m_n_max_litem = 0;
	int m_n_max_date = 0;
	int m_n_max_time = 0;	
	
	//ArrayList get_statusitems() { return m_statusitems; }
	//ArrayList get_statuscodes() { return m_statuscodes; }
	//public NavStruct get_nav_init() { return m_nav_init; }
	//public PolygonStruct get_polygon() { return m_polygon; }
	void set_parsing(int n) { m_n_parsing = n; }
	void set_queue(int n) { m_n_queue = n; }
	void set_sending(int n) { m_n_sending = n; }
	public int get_parsing() { return m_n_parsing; }
	public int get_queue() { return m_n_queue; }
	public int get_sending() { return m_n_sending; }
	
	
	private void fire_statusitem(StatusItemObject obj) {
		PAS.get_pas().get_statuscontroller().getStatusItems()._add(obj);
		check_datetimefilter(obj.get_latestdate(), obj.get_latesttime());
	}
	private void fire_statuscode(StatusCode obj) {
		if(obj!=null && PAS.get_pas().get_statuscontroller().getStatusCodes() != null)
			PAS.get_pas().get_statuscontroller().getStatusCodes()._add(obj);
	}
	private void fire_update_complete() {
		PAS.get_pas().get_statuscontroller().status_update();
	}
	private void fire_set_itemfilter(Integer i) {
		PAS.get_pas().get_statuscontroller().set_item_filter(i.intValue());
	}
	private void fire_set_datetimefilter(int n_date, int n_time) {
		try {
			DateTime t = new DateTime(n_date, n_time);
			PAS.get_pas().get_statuscontroller().set_datetime_filter(t);
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			Error.getError().addError("XMLGetStatusItems","Exception in file_set_datetimefilter",e,1);
		}
	}
	
	private void fire_update_lbasendings(List<LBASEND> arr)
	{
		final List<LBASEND> array = arr;
		try
		{
			//SwingUtilities.invokeAndWait(new Runnable() {
			//	public void run()
				{
					PAS.get_pas().get_statuscontroller().updateLBA(array);
				}
			//});
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/*private void fire_update_cbsendings(ArrayList arr)
	{
		try {
			//ActionEvent e = new ActionEvent(arr, ActionEvent.ACTION_PERFORMED, "act_update_cellbroadcast");
			//fire_event(e);
			//PAS.get_pas().get_statuscontroller().updateCellBroadcast(arr);
		} catch(Exception e) {
			
		}
	}*/
	/*private void fire_set_timefilter(Integer i) {
		ActionEvent e = new ActionEvent(i, ActionEvent.ACTION_PERFORMED, "act_set_timefilter");
		fire_event(e);		
	}*/
	private void fire_event(ActionEvent e) {
		PAS.get_pas().get_statuscontroller().actionPerformed(e);		
	}
	private void check_datetimefilter(int n_date, int n_time) {
		if(((n_date > m_n_max_date) || (n_date==m_n_max_date && n_time > m_n_max_time))) 
			set_datetimefilter(n_date, n_time);
	}
	private void set_datetimefilter(int n_date, int n_time) {
		m_n_max_date = n_date;
		m_n_max_time = n_time;
		//fire_set_datetimefilter(n_date, n_time);
		//fire_set_datefilter(new Integer(n_date));
		//fire_set_timefilter(new Integer(n_time));
	}	
	
}