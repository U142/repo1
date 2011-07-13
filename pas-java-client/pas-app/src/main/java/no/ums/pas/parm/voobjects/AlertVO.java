package no.ums.pas.parm.voobjects;

import no.ums.pas.cellbroadcast.Area;
import no.ums.pas.cellbroadcast.CBMessage;
import no.ums.pas.localization.Localization;
import no.ums.pas.maps.defines.Navigation;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.parm.map.MapPanel;
import no.ums.pas.send.SendController;
import no.ums.pas.send.SendObject;
import no.ums.pas.send.SendProperties;

import java.awt.datatransfer.DataFlavor;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AlertVO extends ParmVO implements Cloneable {
	
	public static String LBAStatusToString(int status)
	{
		String ret = "";
		switch(status)
		{
		case -3: //sent to lba server
            return Localization.l("main_parmtab_lba_area_creation_sent_to_lbaserver");
            case -2: //failed
                return Localization.l("main_parmtab_lba_area_creation_failed");
            case -1: //processing
                return Localization.l("main_parmtab_lba_area_creation_processing");
            case 0: //ok finished
                return Localization.l("main_parmtab_lba_area_creation_finished");
            default:
                ret = Localization.l("common_unknown_status");
			break;
		}
		return ret;
	}

	
	//download area generation status from each operator to be displayed in GUI
	public class LBAOperator
	{
		public LBAOperator(long l_alertpk, int l_operator, int l_status, String sz_operatorname, long l_areaid, String sz_status)
		{
			this.l_alertpk = l_alertpk;
			this.l_operator = l_operator;
			this.l_status = l_status;
			this.sz_status = sz_status;
			this.sz_operatorname = sz_operatorname;
			this.l_areaid = l_areaid;
		}
		public void update(int l_operator, int l_status, String sz_operatorname, long l_areaid, String sz_status)
		{
			this.l_operator = l_operator;
			this.l_status = l_status;
			this.sz_status = sz_status;
			this.sz_operatorname = sz_operatorname;
			this.l_areaid = l_areaid;			
		}
		public void update(LBAOperator u)
		{
			this.l_operator = u.l_operator;
			this.l_status = u.l_status;
			this.sz_status = u.sz_status;
			this.sz_operatorname = u.sz_operatorname;
			this.l_areaid = u.l_areaid;			
		}
		public long l_alertpk;
		public int l_operator;
		public int l_status;
		public String sz_status;
		public String sz_operatorname;
		public long l_areaid;
	}
	
	public class ArrayLBAOperator extends ArrayList<LBAOperator>
	{
		Hashtable<Integer, Integer> hash = new Hashtable<Integer, Integer>(); //contains l_operator and arrayindex
		@Override
		public boolean add(LBAOperator op)
		{
			if(hash.containsKey(op.l_operator))//update
			{
				LBAOperator u = get((Integer)hash.get(op.l_operator));
				u.update(op);
			}
			else //new record
			{
				super.add(op);
				hash.put(op.l_operator, super.size()-1);
			}
			return true;
		}
		public LBAOperator get(int i)
		throws IndexOutOfBoundsException
		{
			try
			{
				return (LBAOperator)super.get(i);
			}
			catch(IndexOutOfBoundsException e)
			{
				throw e;
			}
		}
	}

	private String strAlertpk;
	private String strParent;
	private String strName;
	private String strDescription;
	private int iProfilepk;
	private String strSchedpk;
	private String strOadc;
	private int iValidity;
	private int iAddresstypes;
	private String strTimestamp;
	private SendObject sendObj;
	private String tempPk;
	private int locked;
	private String[] strLocalLanguage;
	private String[] strInternationalLanguage;
	private ArrayList<Object> CBMessages;
	private String strCBOadc;
	private String m_sz_sms_oadc;
	public String get_sms_oadc() { return m_sz_sms_oadc; }
	public void set_sms_oadc(String sms_oadc) { m_sz_sms_oadc = sms_oadc; }
	private String m_sz_sms_message;
	public String get_sms_message() { return m_sz_sms_message; }
	public void set_sms_message(String sms_message) { m_sz_sms_message = sms_message; }
	private Area area;
	private String sz_lba_areaid; //New type of area-id for LBA
	private int l_lba_expiry; // Expiry time in minutes for LBA
	public int get_LBAExpiry() { return l_lba_expiry; }
	protected boolean b_size_dirty = true;
	private int m_n_maxchannels;
	private int m_n_requesttype;
	protected ArrayLBAOperator m_operator_status = new ArrayLBAOperator();
	public void UpdateLbaOperator(LBAOperator l)
	{
		m_operator_status.add(l);
	}
	
	@Override
	public AlertVO clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		AlertVO a = (AlertVO)super.clone();
		if(CBMessages != null) {
			a.CBMessages = (ArrayList<Object>)CBMessages.clone();
			a.CBMessages.clear();
			for(int i=0;i<this.CBMessages.size();++i)
				a.CBMessages.add(((CBMessage)this.CBMessages.get(i)).clone());
		}
		return a;
	}
	public ArrayLBAOperator getOperators() { return m_operator_status; }
	
	public AlertVO() {
		//fjåått
	}
	public SendObject getSendObject(){
		return sendObj;
	}
	public String getPk() { return strAlertpk; }
	public void setPk(String s) { strAlertpk = s; }
	
	public static final DataFlavor TREE_PATH_FLAVOR = new DataFlavor(AlertVO.class, "Alert");
	DataFlavor flavors[] = { TREE_PATH_FLAVOR };
	public synchronized DataFlavor[] getTransferDataFlavors() {
		return flavors;
	}
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return (flavor.match(TREE_PATH_FLAVOR));
	}
	public AlertVO(String alertpk, String parent, String name, int addresstypes, MapPanel map, Navigation nav) {
		strAlertpk = alertpk;
		strParent = parent;
		strName = name;
		iAddresstypes = addresstypes;
		this.m_shape = null;
		
		sendObj = new SendObject("New sending", SendProperties.SENDING_TYPE_POLYGON_, 0, 
				map, nav);
	}
	
	public AlertVO(String alertpk, String parent, String name, String description, int profilepk, String schedpk, String oadc, int validity,
			int addresstypes, String timestamp, ShapeStruct shape) {
		strAlertpk = alertpk;
		strParent = parent;
		strName = name;
		strDescription = description;
		iProfilepk = profilepk;
		strSchedpk = schedpk;
		strOadc = oadc;
		iValidity = validity;
		iAddresstypes = addresstypes;
		strTimestamp = timestamp;
		this.m_shape = shape;
		if(this.m_shape!=null)
		{
			this.m_shape.shapeName = strName;
		}
	}
	
	public AlertVO(String alertpk, String parent, String name, String description, int profilepk, String schedpk, String oadc, int validity,
			int addresstypes, String timestamp, ShapeStruct shape, String[] local_language, String[] international_language, Area area, String strCBOadc, ArrayList<Object> CBMessages) {
		this(alertpk, parent, name, description, profilepk, schedpk, oadc, validity,
			addresstypes, timestamp, shape);
		this.strLocalLanguage = local_language;
		this.strInternationalLanguage = international_language;
		this.strCBOadc = strCBOadc;
		this.area = area;
		this.CBMessages = CBMessages;
	}
	
	public AlertVO(String strAlertpk, String strParent, String strName, int addresstypes) {
		super();
		this.strAlertpk = strAlertpk;
		this.strParent = strParent;
		this.strName = strName;
		iAddresstypes = addresstypes;
	}
	
	public void submitShape(ShapeStruct s) {
		m_shape = s;
	}
	public ShapeStruct getShape() {
		return m_shape;
	}
	
	public String getAlertpk(){ return strAlertpk; }
	
	public String getParent(){ return strParent; }
	
	public void setParent(String parent){ strParent = parent; }
	
	public String getName() { 
		//if(hasValidAreaIDFromCellVision())
			return strName;
		//else
		//	return strName + " [Waiting for Location Based Area]";
	}
	
	public void setName(String name){ strName = name; }
	
	public String getDescription(){ return strDescription; }
		
	public int getLocked() {
		return locked;
	}
	public void setLocked(int locked) {
		this.locked = locked;
	}
	public void setDescription(String description){ strDescription = description; }
	
	public int getProfilepk(){ return iProfilepk; }
	
	public void setProfilepk(int profilepk){ iProfilepk = profilepk; } 
	
	public String getSchedpk(){ return strSchedpk; }
	
	public void setSchedpk(String schedpk){ strSchedpk = schedpk; }
	
	public String getOadc(){ return strOadc; }
	
	public void setOadc(String oadc){ strOadc = oadc; }
		
	public int getValidity(){ return iValidity; }
	
	public void setValidity(int validity){ iValidity = validity; }
	
	public int getMaxChannels() { return m_n_maxchannels; }
	
	public void setMaxChannels(int n) { m_n_maxchannels = n; }
	
	public int getRequestType() { return m_n_requesttype; }
	
	public void setRequestType(int n) { m_n_requesttype = n; }
	
	public int getAddresstypes(){ return iAddresstypes; }
	
	public void setAddresstypes(int addresstypes){ iAddresstypes = addresstypes; }
	
	public String getTimestamp(){ return strTimestamp; }
	
	public void setTimestamp(String timestamp){ strTimestamp = timestamp; }
	
	public void setLocalLanguage(String[] local_language) { strLocalLanguage = local_language; }
	
	public String[] getLocalLanguage() { return strLocalLanguage; }
	
	public void setInternationalLanguage(String[] international_language) { strInternationalLanguage = international_language; }
	
	public String[] getInternationalLanguage() { return strInternationalLanguage; }
	
	public void setArea(Area area) { this.area = area; }
	
	public Area getArea() { return area; }
	
	public void setCBMessages(ArrayList<Object> messages) { this.CBMessages = messages; }
	
	public ArrayList<Object> getCBMessages() { return CBMessages; }
	
	public void setSMSOadc(String smsoadc) { m_sz_sms_oadc = smsoadc; }
	public void setSMSMessage(String smsmessage) { m_sz_sms_message=smsmessage; }
	
	public boolean hasSMSTemplate() { 
		Pattern start = Pattern.compile("\\[");
		Pattern end = Pattern.compile("\\]");
		boolean start_found = false;
		boolean end_found = false;
		
		Matcher m = start.matcher(m_sz_sms_message);
		while(m.find())
			start_found = true;
		
		m = end.matcher(m_sz_sms_message);
		while(m.find())
			end_found = true;
		
		if(start_found && end_found)
			return true;
		else
			return false;
	}
	
	public String getLBAAreaID() { return sz_lba_areaid; }
	public void setLBAAreaID(String sz) { 
			sz_lba_areaid = sz;
	}
	public boolean hasValidAreaIDFromCellVision() {
		//check if alert has addresstypes with LBA sending AND also has a valid areaid from CellVision
		if((getAddresstypes() & SendController.SENDTO_CELL_BROADCAST_TEXT) == SendController.SENDTO_CELL_BROADCAST_TEXT ||
			(getAddresstypes() & SendController.SENDTO_CELL_BROADCAST_VOICE) == SendController.SENDTO_CELL_BROADCAST_VOICE)
		{
			if(getLBAAreaID()==null)
				return false;
			if(getLBAAreaID().equals("-1") || getLBAAreaID().equals("-2") || getLBAAreaID().equals("0"))
				return false;
			return true;
		}
		return true;
		
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AlertVO alertVO = (AlertVO) o;

        return !(strAlertpk != null ? !strAlertpk.equals(alertVO.strAlertpk) : alertVO.strAlertpk != null);

    }

    @Override
    public int hashCode() {
        return strAlertpk != null ? strAlertpk.hashCode() : 0;
    }

    public String toString() {
		String ret;
		if(getLocked() == 1) {
			ret = strName + " [Locked]";
		}
		else
			ret = strName;
		if(!hasValidAreaIDFromCellVision())
		{
			if(sz_lba_areaid!=null)
			{
				if(sz_lba_areaid.equals("-2"))
					ret += " [LBA ERROR]";
				else if(sz_lba_areaid.equals("0"))
					ret += " [Waiting for LBA area to be generated]";
					
				b_size_dirty = true;
			}			
		}
		return ret;
	}

	public void setStrAlertpk(String strAlertpk) {
		this.strAlertpk = strAlertpk;
	}
	public String getTempPk() {
		return tempPk;
	}
	public void setTempPk(String tempPk) {
		this.tempPk = tempPk;
	}
	public String getCBOadc() {
		return strCBOadc;
	}
	public void setCBOadc(String strCBOadc) {
		this.strCBOadc = strCBOadc;
	}
	public void setLBAExpiry(int minutes) {
		this.l_lba_expiry = minutes;
	}
	
	@Override
	public boolean hasValidPk() {
		if(getPk().startsWith("a"))
			return true;
		return false;
	}
}
