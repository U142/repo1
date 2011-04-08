package no.ums.pas.importer;

import no.ums.pas.PAS;
import no.ums.pas.core.Variables;
import no.ums.pas.maps.defines.NavStruct;
import no.ums.pas.maps.defines.PolygonStruct;
import no.ums.pas.send.SendObject;
import no.ums.pas.send.SendProperties;
import no.ums.pas.ums.errorhandling.Error;
import no.ums.pas.ums.tools.CoorConverter;
import no.ums.pas.ums.tools.TextFormat;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;



public class SosiFile extends Object {
	class SosiFieldSet extends Object {
		private int m_n_line;
		private String m_sz_fieldname;
		private int m_n_fieldid;
		private ArrayList <String>m_dataset = new ArrayList<String>();
		private boolean m_b_isknown = true;
		
		public int id() { return m_n_fieldid; }
		public String name() { return m_sz_fieldname; }
		public int line() { return m_n_line; }
		public ArrayList<String> data() { return m_dataset; }
		public boolean isKnown() { return m_b_isknown; }
		public void setIsCoor() {
			if(!isKnown()) {
				m_b_isknown = true;
				m_n_fieldid = 999;
				m_sz_fieldname = "Coor";
			}
		}
		
		SosiFieldSet(String sz_fieldname, int n_fieldid, int n_line, ArrayList<String> data) {
			if(sz_fieldname.equals("")) {
				sz_fieldname = "Unknown";
				m_b_isknown = false;
			}
			m_n_fieldid = n_fieldid;
			m_sz_fieldname = sz_fieldname;
			m_n_line = n_line;
			m_dataset = data;
		}
		public String toString() {
			//sz_ret = "line: " + m_n_line + " fieldid=" + id() + " fieldname: " + name() + " data:";
			/*for(int i=0; i < data().size(); i++) {
				sz_ret += " " + data().get(i).toString();
			}*/
			String sz_ret = null;
			return sz_ret;
		}
	}
	class SosiField extends Object {
		private int m_n_fieldid;
		private String m_sz_fieldname;
		public int id() { return m_n_fieldid; }
		public String name() { return m_sz_fieldname; }
		SosiField(int n_id, String sz_name) {
			m_n_fieldid = n_id;
			m_sz_fieldname = sz_name;
		}
	}
	public static final String FIELD_HODE_		= ".HODE";
	public static final String FIELD_TEGNSETT_	= "..TEGNSETT";
	public static final String FIELD_KOORDSYS_	= "...KOORDSYS";
	public static final String FIELD_ENHET_		= "...ENHET";
	public static final String FIELD_ORIGO_		= "...ORIGO-NØ";
	public static final String FIELD_OMRAADE_	= "..OMRÅDE";
	public static final String FIELD_MIN_NE_	= "...MIN-NØ";
	public static final String FIELD_MAX_NE_	= "...MAX-NØ";
	public static final String FIELD_SOSI_VERSJON_	= "..SOSI-VERSJON";
	public static final String FIELD_SOSI_NIVAA_	= "..SOSI-NIVÅ";
	public static final String FIELD_EIER_		= "..EIER";
	public static final String FIELD_PRODUSENT_	= "..PRODUSENT";
	public static final String FIELD_KURVE_		= ".KURVE";
	public static final String FIELD_OBJTYPE_	= "..OBJTYPE";
	public static final String FIELD_LTEMA_		= "..LTEMA";
	public static final String FIELD_DIGDATO_	= "..DIGDATO";
	public static final String FIELD_KVALITET_	= "..KVALITET";
	public static final String FIELD_NAME_		= "..NAVN";
	public static final String FIELD_INFORMASJON_ = "..INFORMASJON";
	public static final String FIELD_NE_		= "..NØ";
	public static final String FIELD_FLATE_		= ".FLATE";
	public static final String FIELD_REF_		= "..REF"; //..REF :1
	public static final String FIELD_SLUTT_		= ".SLUTT";
	public static final String FIELD_FTEMA_		= "..FTEMA";
	
	public SosiField[] m_sosifields = new SosiField [] {
			new SosiField(1, FIELD_HODE_),
			new SosiField(2, FIELD_TEGNSETT_),
			new SosiField(3, FIELD_KOORDSYS_),
			new SosiField(4, FIELD_ENHET_),
			new SosiField(5, FIELD_ORIGO_),
			new SosiField(6, FIELD_OMRAADE_), 
			new SosiField(7, FIELD_MIN_NE_),
			new SosiField(8, FIELD_MAX_NE_),
			new SosiField(9, FIELD_SOSI_VERSJON_),
			new SosiField(10, FIELD_SOSI_NIVAA_),
			new SosiField(11, FIELD_EIER_),
			new SosiField(12, FIELD_PRODUSENT_),
			new SosiField(13, FIELD_KURVE_),
			new SosiField(14, FIELD_OBJTYPE_),
			new SosiField(15, FIELD_LTEMA_),
			new SosiField(16, FIELD_DIGDATO_),
			new SosiField(17, FIELD_KVALITET_),
			new SosiField(18, FIELD_INFORMASJON_),
			new SosiField(19, FIELD_NE_),
			new SosiField(20, FIELD_FLATE_),
			new SosiField(21, FIELD_NAME_),
			new SosiField(22, FIELD_REF_),
			new SosiField(23, FIELD_SLUTT_)
	};
	public SosiField [] fields() { return m_sosifields; }
	private SosiParser m_parser;
	private SendObject m_sendobj;
	
	protected boolean set_value(int n_fieldid, ArrayList<String> data) {
		try {
		String sz_param1, sz_param2;

		if(data==null)
			return false;
		if(data.size()<1)
			return false;
		//String sz_data = data.get(1).toString();
		String sz_data = "";
		try {
			if(data.size() >= 2) {
				sz_data = data.get(1).toString();
				sz_data = sz_data.trim();
			} else {
				sz_data = data.get(0).toString();
				sz_data = sz_data.trim();
			}
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			Error.getError().addError("SosiFile","Exception in set_value",e,1);
		}
		//PAS.get_pas().add_event("set_value (" + n_fieldid + ")" + data.toString());
		switch(n_fieldid) {
			case 1:
				break;
			case 2:
				break;
			case 3:
				m_n_coorsys = new Integer(sz_data).intValue();
				/*if(m_n_coorsys==22) {
					set_utmzone("32V");
				} else
					set_utmzone("33V");*/
				switch(m_n_coorsys) {
				case 21:
				case 31:
					set_utmzone("31V");
					set_coorsystype(COORSYS_UTM_);
					break;
				case 22:
				case 32:
					set_utmzone("32V");
					set_coorsystype(COORSYS_UTM_);
					break;
				case 23:
				case 33:
					set_utmzone("33V");
					set_coorsystype(COORSYS_UTM_);
					break;
				case 24:
				case 34:
					set_utmzone("34V");
					set_coorsystype(COORSYS_UTM_);
					break;
				case 25:
				case 35:
					set_utmzone("35V");
					set_coorsystype(COORSYS_UTM_);
					break;
				case 26:
				case 36:
					set_utmzone("36V");
					set_coorsystype(COORSYS_UTM_);
					break;
				case 50: //ED50 Geografisk, ingen projeksjon
					set_utmzone("0");
					set_coorsystype(COORSYS_LL_);
					break;
				case 72: //WGS72
					set_utmzone("0");
					set_coorsystype(COORSYS_LL_);
					break;
				case 84: //EUREF89/WGS84
					set_utmzone("0");
					set_coorsystype(COORSYS_LL_);
					break;
				case 87: //ED87
					set_utmzone("0");
					set_coorsystype(COORSYS_LL_);
					break;
					
					
				}
				//PAS.get_pas().add_event("UTM Zone " + get_utmzone());
				break;
			case 4:
				m_f_units = new Double(sz_data).doubleValue();
				break;
			case 5:
				try {
					set_origin(new Point(new Integer(data.get(1).toString()).intValue(), new Integer(data.get(2).toString()).intValue()));
				}catch(Exception e) {
					set_origin(new Point(0, 0));
				}
				//PAS.get_pas().add_event("origin = " + m_p_origin.x + "/" + m_p_origin.y);
				break;
			case 6:
				break;
			case 7:
				try {
					if(data.size() >= 4) {
						sz_param1 = data.get(2).toString();
						sz_param2 = data.get(3).toString();
					} else {
						if(data.size()>=3)
						{
							sz_param1 = data.get(1).toString();
							sz_param2 = data.get(2).toString();
						}
						else
							return false;
					}
					System.out.println(sz_param1 + " " + sz_param2);
					if(get_coorsystype()==COORSYS_UTM_)
					{
						CoorConverter.LLCoor min = new CoorConverter().UTM2LL(23, new Double(sz_param1).doubleValue(), new Double(sz_param2).doubleValue(), get_utmzone());
						get_bounding()._lbo = min.get_lon();
						get_bounding()._ubo = min.get_lat();
					}
					else if(get_coorsystype()==COORSYS_LL_)
					{
						get_bounding()._lbo = new Double(sz_param2).doubleValue();
						get_bounding()._ubo = new Double(sz_param1).doubleValue();
					}
				} catch(Exception e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					Error.getError().addError("SosiFile","Exception in set_value",e,1);
				}
				break;
			case 8:
				//String sz_param1, sz_param2, sz_param3;
				try {
					if(data.size() >= 4) {
						sz_param1 = data.get(2).toString();
						sz_param2 = data.get(3).toString();
					} else {
						if(data.size()>=3)
						{
							sz_param1 = data.get(1).toString();
							sz_param2 = data.get(2).toString();
						}
						else
							return false;
					}
					System.out.println(sz_param1 + " " + sz_param2);
					if(get_coorsystype()==COORSYS_UTM_)
					{
						CoorConverter.LLCoor max = new CoorConverter().UTM2LL(23, new Double(sz_param1).doubleValue(), new Double(sz_param2).doubleValue(), "32V");
						get_bounding()._rbo = max.get_lon();
						get_bounding()._bbo = max.get_lat();
					}
					else if(get_coorsystype()==COORSYS_LL_)
					{
						get_bounding()._rbo = new Double(sz_param2).doubleValue();
						get_bounding()._bbo = new Double(sz_param1).doubleValue();
					}
				} catch(Exception e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					Error.getError().addError("SosiFile","Exception in set_value",e,1);
				}
				break;
			case 9:
				try {
					m_f_sosiversion = new Double(sz_data).doubleValue();
				} catch(Exception e) { }//Error.getError().addError("SosiFile","Exception in set_value",e,1); }
				break;
			case 10:
				try {
					m_n_sosilevel = new Integer(sz_data).intValue();
				} catch(Exception e) { } //Error.getError().addError("SosiFile","Exception in set_value",e,1); }
				break;
			case 11:
				m_sz_owner = sz_data;
				break;
			case 12:
				m_sz_manufacturer = sz_data;
				break;
			case 13:
				break;
			case 14:
				//m_sz_objecttype = sz_data;
				m_arr_flater.get_current_flate().set_objecttype(sz_data);
				break;
			case 15:
				m_arr_flater.get_current_flate().set_ltema(sz_data);
				break;
			case 16:
				m_arr_flater.get_current_flate().set_createdate(sz_data);
				break;
			case 17:
				int n_quality = new Integer(sz_data).intValue();
				m_arr_flater.get_current_flate().set_quality(n_quality);
				break;
			case 18:
				//m_sz_information = sz_data;
				//set_information(sz_data);
				m_arr_flater.get_current_flate().set_information(sz_data);
				break;
			case 19:
				//north east
				break;
			case 20:
				//flate
				int n_flateid = new Integer(sz_data.substring(0, sz_data.length()-1)).intValue();
				m_arr_flater.add_flate(n_flateid);
				break;
			case 21:
				//name
				try {
					m_arr_flater.get_current_flate().set_name(sz_data);
				} catch(Exception e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					Error.getError().addError("SosiFile","Exception in set_value",e,1);
				}
				break;
			case 22:
				try {
					m_arr_flater.get_current_flate().set_ref(new Integer(sz_data.substring(1)).intValue());
				} catch(Exception e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
					Error.getError().addError("SosiFile","Exception in set_value",e,1);
				}
				break;
			case 23: //slutt
				break;
			case 999: //coor
				//PAS.get_pas().add_event(data.get(0).toString() + " " + data.get(1).toString());
				double d_x, d_y;
				try {
					d_x = get_origin().x + (new Double(data.get(0).toString()).doubleValue()) * get_units();
					d_y = get_origin().y + (new Double(data.get(1).toString()).doubleValue()) * get_units();
					//PAS.get_pas().add_event("d_x = " + d_x + "  d_y = " + d_y);
					add_polypoint(d_x, d_y, get_coorsystype()); //(get_coorsys()==84 ? COORSYS_LL_ : COORSYS_UTM_));
				} catch(Exception e) {
					//PAS.get_pas().add_event("error d_x, d_y", e);
					Error.getError().addError("SosiFile","Exception in set_value",e,1);
				}
				break;
				
		}
		return true;
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			Error.getError().addError("SosiFile","Exception in set_value",e,1);
			return false;
		}
	}	
	
	public static final int COORSYS_LL_ = 1;
	public static final int COORSYS_UTM_ = 2;
	
	public boolean parse(URL url, ActionListener callback, String action) {
		m_parser = new SosiParser(url, callback);
		m_parser.set_callback(callback);
		m_parser.set_action(action);
		m_parser.set_object(this);
		m_parser.begin_parsing("");
		return true;
	}
	
	public boolean parse(File f, ActionListener callback, String action) {
		m_parser = new SosiParser(f, callback);
		m_parser.set_callback(callback);
		m_parser.set_action(action);
		m_parser.set_object(this);
		m_parser.begin_parsing("");
		return true;
	}
	/*private void onDownloadFinished(ActionPolygonLoaded e) {
		
	}*/
	public String toString() {
		//String sz_ret = null;
		/*int n_fields [] = new int[] { 6, 7, 8, 11, 14, 16, 17, 18 };
		sz_ret = "Polygon: " + get_polygon().get_size() + " points\n";
		for(int i = 0; i < n_fields.length; i++) {
			sz_ret += fields()[n_fields[i]].name() + ": " + m_parser.(n_fields[i]) + "\n";
		}*/
		/*sz_ret=  "<html><table>";
		sz_ret+= "<tr><td><b>Name: </b></td><td>" + m_arr_flater.get_current_flate().get_name() + "</td></tr>";
		sz_ret+= "<tr><td><b>Polygon:</b></td><td>" + m_arr_flater.get_current_flate().get_polygon().get_size() + " points</td></tr>";
		sz_ret+= "<tr><td><b>Created:</b></td><td>" + TextFormat.format_date(m_arr_flater.get_current_flate().get_createdate()) + "</td></tr>";
		sz_ret+= "<tr><td><b>Owner:</b></td><td>" + get_owner() + "</td></tr>";
		//sz_ret+= "<b>Coor-sys:    </b></td><td>" + get_coorsys() + "</td></tr>";
		sz_ret+= "<tr><td><b>UTM Zone:</b></td><td>" + get_utmzone() + "</td></tr>";
		sz_ret+= "<tr><td><b>Object:</b></td><td>" + m_arr_flater.get_current_flate().get_objecttype() + "</td></tr>";
		sz_ret+= "<tr><td><b>Information:</b></td><td>" + m_arr_flater.get_current_flate().get_information() + "</td></tr>";
		sz_ret+= "</table></html>";
		return sz_ret;*/
		return getFlateInformation(get_flater().m_n_current_index);

	}
	
	public String getFlateInformation(int n_index) {
		String sz_ret = "";
		if(n_index<0 || n_index>=get_flater().size())
			return "N/A";
		FlateFields flate = (FlateFields)get_flater().get(n_index);
		sz_ret=  "<html><table>";
		sz_ret+= "<tr><td><b>Name: </b></td><td>" + flate.get_name() + "</td></tr>";
		//sz_ret+= "<tr><td><b>Polygon:</b></td><td>" + flate.get_polygon().get_size() + " points</td></tr>";
		try {
			sz_ret+= "<tr><td><b>Created:</b></td><td>" + TextFormat.format_date(flate.get_createdate()) + "</td></tr>";
		} catch(Exception e) {
			
		}
		sz_ret+= "<tr><td><b>Owner:</b></td><td>" + get_owner() + "</td></tr>";
		//sz_ret+= "<b>Coor-sys:    </b></td><td>" + get_coorsys() + "</td></tr>";
		sz_ret+= "<tr><td><b>UTM Zone:</b></td><td>" + get_utmzone() + "</td></tr>";
		sz_ret+= "<tr><td><b>Object:</b></td><td>" + flate.get_objecttype() + "</td></tr>";
		sz_ret+= "<tr><td><b>Information:</b></td><td>" + flate.get_information() + "</td></tr>";
		sz_ret+= "</table></html>";
		return sz_ret;
	}
	public class SosiParser extends PolygonParser {
		ArrayList <SosiFieldSet>m_lineobjects = new ArrayList<SosiFieldSet>();
		protected ActionListener m_importpolygon_callback;
		public SosiParser(File f, ActionListener callback) {
			super(f, callback);
			m_importpolygon_callback = callback;
		}
		public SosiParser(URL url, ActionListener callback) {
			super(url, callback);
			openUrlStream();
			m_importpolygon_callback = callback;
		}
		public void begin() {
			begin_parsing("");
		}
		public boolean parse() {
			create_values();
			traverse();
			//PAS.get_pas().add_event("polypoints = " + get_polygon().get_size());
			
			//PAS.get_pas().add_event("Parsing finished");
			//PAS.get_pas().add_event("Goto " + get_bounding().toString());
			//Variables.NAVIGATION.gotoMap(get_bounding());
			
			return true;
		}
		public boolean create_values() {
			for(int i=0; i < lines().size(); i++) {
				ArrayList<String> arr = split(lines().get(i).toString(), " ");
				//PAS.get_pas().add_event("'" + arr.get(0).toString() + "'");
				if(arr==null || arr.size()==0)
					continue;
				int fieldno = recognize_field(arr.get(0).toString());
				String sz_fieldname;
				int n_fieldid = -1;
				if(fieldno >= 0) {
					n_fieldid = fields()[fieldno].id();
					sz_fieldname = arr.get(0).toString();
					//PAS.get_pas().add_event("Field recognized : " + sz_fieldname);
				}
				else {
					n_fieldid = -1;
					sz_fieldname = "";
				}
				SosiFieldSet linedata = new SosiFieldSet(sz_fieldname, n_fieldid, i, arr);
				//PAS.get_pas().add_event(linedata.toString());
				m_lineobjects.add(linedata);
			}
			return true;
		}
		private int recognize_field(String sz_field) {
			for(int i=0; i < fields().length; i++) {
				if(sz_field.indexOf(fields()[i].name()) >= 0) {
					return i;
				}
			}
			return -1;
		}
		public boolean traverse() {
			boolean b_expectcoor = false;
			boolean b_coorsadded = false;
			int n_coorstarts = 0;
			boolean b_kurve = false;
			int n_kurvecount = 0;
			for(int i=0; i < m_lineobjects.size(); i++) {
				SosiFieldSet obj = (SosiFieldSet)m_lineobjects.get(i);
				//if(obj.isKnown()) {
					if(b_expectcoor /*&& !b_coorsadded*/) {
						obj.setIsCoor();
						//PAS.get_pas().add_event("COOR " + obj.data().get(0).toString() + " / " + obj.data().get(1).toString());
					}
					try {
						//PAS.get_pas().add_event("set_value " + obj.id() + "=" + obj.data());
						set_value(obj.id(), obj.data());
					} catch(Exception e) {
						//PAS.get_pas().add_event("ERROR setting " + obj.m_sz_fieldname, e);
						Error.getError().addError("SosiFile","Exception in traverse",e,1);
					}
					if(obj.id() == 20 || obj.id() == 23) {
						b_kurve = false;
						b_expectcoor = false;
					}
					if(obj.id() == 13) {
						if(n_kurvecount >= 0) { //new
							try {
								String s = obj.data().get(1).toString();
								int n_kurvenr = new Integer(s.substring(0, s.length()-1)).intValue();
								get_flater().add_flate(n_kurvenr);
								m_sendobj = new SendObject("Imported kurve " + n_kurvenr, SendProperties.SENDING_TYPE_POLYGON_, n_kurvenr, PAS.get_pas().get_sendcontroller(), Variables.getNavigation());
								int n_flate = m_arr_flater.set_active_flate_by_kurve_ref(n_kurvenr);
								if(n_flate==-1) {
									//FLATE information not yet registrered
								}
								System.out.println("Found flate " + n_flate + " for curve " + n_kurvenr);
									
								m_sendobj.get_sendproperties().set_shapestruct((PolygonStruct)get_flater().get_current_flate().get_polygon());
								m_sendobj.get_sendproperties().set_sendingname(get_flater().get_current_flate().get_name(), getFlateInformation(n_flate));
								get_callback().actionPerformed(new ActionEvent(m_sendobj, ActionEvent.ACTION_PERFORMED, "act_importsending_found"));
							 
								b_expectcoor = false;
								b_coorsadded = false;
								n_coorstarts = 0;
							} catch(Exception err) {
								System.out.println(err.getMessage());
								err.printStackTrace();
								Error.getError().addError("SosiFile","Exception in traverse",err,1);
								return false;
							}
						}
						b_kurve = true;
						n_kurvecount++;
					}
					if(obj.id() == 19 && b_kurve) { //NØ
						n_coorstarts++;
						if(n_coorstarts==1)// && !b_coorsadded)
						{
							//check two lines forward is another ..NØ
							b_expectcoor = true;
							try
							{
								SosiFieldSet test = (SosiFieldSet)m_lineobjects.get(i+2);
								if(test.id()==19)
									b_expectcoor = false;
							}
							catch(Exception e)
							{
								
							}
						}
						else if(n_coorstarts==2)
							b_expectcoor = true;
						//PAS.get_pas().add_event("b_expectcoor=true at line " + obj.line());
						//PAS.get_pas().add_event(
					}
					else {
						if(obj.id() == 13 || obj.id() == 20) {
							b_expectcoor = false;
							b_coorsadded = true;
						}
					}
				//}
			}
			callback();
			return true;
		}
	}
	
	public void callback() {
		for(FlateFields ff : get_flater())
		{
			if(ff.get_polygon()!=null)
				ff.get_polygon().finalizeShape();
		}
		m_parser.get_callback().actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "act_sosi_parsing_complete"));
	}
	/*
.FLATE 1:
..OBJTYPE FlomAreal
..FTEMA 3280
..KOMM 1524
..DIGDATO 20060131
..KVALITET 82
..ID 15241001
..NAVN "Norddal"
..INFORMASJON "Digitalisert på skjerm etter plott"
..REF :6
..NØ
	 */

	//*** COMMON HEADER ***//
	private int m_n_coorsys; //...KOORDSYS 22
	private int m_n_coorsystype; //UTM OR LL
	private double m_f_units; //...ENHET 0.01
	private Point m_p_origin; //...ORIGO-NØ 0 0
	private NavStruct m_bounding; //...MIN-NØ 6883943 388410
								  //...MAX-NØ 6885939 391058
	private double m_f_sosiversion; //..SOSI-VERSJON  3.4
	private int m_n_sosilevel; //..SOSI-NIVÅ 4
	private String m_sz_owner; //..EIER "Møre og Romsdal Fylke"
	private String m_sz_manufacturer; //..PRODUSENT "Møre og Romsdal Fylke"
	private int m_n_ellipsoid = 23;
	private String m_sz_utmzone = "32V";
	private FlateArray m_arr_flater = new FlateArray();
	public FlateArray get_flater() { return m_arr_flater; }
	
	public class FlateArray extends ArrayList<FlateFields> {
		public static final long serialVersionUID = 1;
		private int m_n_current_index = -1;

		public FlateArray() {
			super();
		}
		
		public void add_flate(int n_flateid) {
			try {
				this.add(new FlateFields(n_flateid));
				m_n_current_index++;
			} catch(Exception e) { Error.getError().addError("SosiFile","Exception in add_flate",e,1); }
		}
		public FlateFields get_flate(int n) {
			return (FlateFields)get(n);
		}
		public FlateFields get_current_flate() {
			return (FlateFields)get(m_n_current_index);
		}
		public int set_active_flate_by_kurve_ref(int ref) {
			for(int i=0; i < size(); i++) {
				int id=get_flate(i).get_ref();//get_flateid();
				if(id == ref) {
					m_n_current_index = i;
					return m_n_current_index;
					//return get_flate(m_n_current_index).get_flateid();
				}
			}
			return -1;
		}
	}
	
	public class FlateFields extends Object {
		public FlateFields(int n_flateid) {
			super();
			m_n_flateid = n_flateid;
			m_polygon = new PolygonStruct(PAS.get_pas().get_mappane().get_dimension());

		}
		//*** FIELDS ***//
		private int m_n_flateid = 0;
		private String m_sz_objecttype = ""; //..OBJTYPE FlomArealGrense
		private String m_sz_ltema; //..LTEMA 3280
		private String m_sz_createdate; //..DIGDATO 20060117
		private int m_n_quality; //..KVALITET 82
		private String m_sz_information; //..INFORMASJON "Digitalisert på skjerm etter plott"
		private String m_sz_name = "";
		private int m_n_ref = -1;
		private Point m_p_northeast; //..NØ
									 //688593817 38871428 ...KP 1
									 //..NØ
		public void set_flateid(int n) { m_n_flateid = n;}
		public void set_objecttype(String s) { m_sz_objecttype = s; }
		public void set_ltema(String s) { m_sz_ltema = s; }
		public void set_createdate(String s) { m_sz_createdate = s; }
		public void set_quality(int n) { m_n_quality = n; }
		public void set_information(String s) { m_sz_information = s; }
		public void set_northeast(Point p) { m_p_northeast = p; }
		public void set_name(String s) { m_sz_name = s; }
		public void set_ref(int n) { m_n_ref = n; }

		public int get_flateid() { return m_n_flateid; }
		public String get_objecttype() { return m_sz_objecttype; }
		public String get_ltema() { return m_sz_ltema; }
		public String get_createdate() { return m_sz_createdate; }
		public int get_quality() { return m_n_quality; }
		public String get_information() { return m_sz_information; }
		public Point get_northeast() { return m_p_northeast; }
		public String get_name() { return m_sz_name; }
		public int get_ref() { return m_n_ref; }

		private PolygonStruct m_polygon;
		public PolygonStruct get_polygon() { return m_polygon; }
	}
	
	
	public SosiFile() {
		m_bounding = new NavStruct();
		m_p_origin = new Point(0,0);
	}
	public void set_coorsys(int n) { m_n_coorsys = n; }
	public void set_coorsystype(int n) { m_n_coorsystype = n; } //LL OR UTM
	public void set_units(double d) { m_f_units = d; }
	public void set_origin(Point p) { m_p_origin = p; }
	public void set_bounding(NavStruct nav) { m_bounding = nav; }
	public void set_sosiversion(double d) { m_f_sosiversion = d; }
	public void set_sosilevel(int n) { m_n_sosilevel = n; }
	public void set_owner(String s) { m_sz_owner = s; }
	public void set_ellipsoid(int n) { m_n_ellipsoid = n; }
	public void set_utmzone(String s) { m_sz_utmzone = s; }
	public void set_manufacturer(String s) { m_sz_manufacturer = s; }

	
	public int get_coorsys() { return m_n_coorsys; }
	public int get_coorsystype() { return m_n_coorsystype; }
	public double get_units() { return m_f_units; }
	public Point get_origin() { return m_p_origin; }
	public NavStruct get_bounding() { return m_bounding; }
	public double get_sosiversion() { return m_f_sosiversion; }
	public int get_sosilevel() { return m_n_sosilevel; }
	public String get_owner() { return m_sz_owner; }
	public String get_manufacturer() { return m_sz_manufacturer; }
	public int get_ellipsoid() { return m_n_ellipsoid; }
	public String get_utmzone() { return m_sz_utmzone; }

	public void add_polypoint(double x, double y, int COORSYS) {
		switch(COORSYS) {
			case COORSYS_LL_:
				get_flater().get_current_flate().get_polygon().add_coor(new Double(x), new Double(y), true, false);
				break;
			case COORSYS_UTM_:
				CoorConverter.LLCoor ll = new CoorConverter().UTM2LL(get_ellipsoid(), x, y, get_utmzone());
				get_flater().get_current_flate().get_polygon().add_coor(new Double(ll.get_lon()), new Double(ll.get_lat()), true, false);
				//PAS.get_pas().add_event("lon = " + ll.get_lon() + "  lat = " + ll.get_lat());
				break;
		}
	}
	
}