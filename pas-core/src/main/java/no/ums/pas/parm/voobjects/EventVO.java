package no.ums.pas.parm.voobjects;

import no.ums.pas.maps.defines.UnknownShape;

import java.awt.datatransfer.DataFlavor;
import java.util.ArrayList;


public class EventVO extends ParmVO {
	
	private String eventPk;
	private String name;
	private String description;
	private String parent;
	private ArrayList<Object> alertListe;
	private CategoryVO catVO;
	private String categorypk;
	private String timestamp;
	private String tempPk;
	private double[] epicentre = { 0.0, 0.0 };
	
	public String getTempPk() {
		return tempPk;
	}

	public void setTempPk(String tempPk) {
		this.tempPk = tempPk;
	}
	public String getPk() { return eventPk; }
	
	public static final DataFlavor TREE_PATH_FLAVOR = new DataFlavor(AlertVO.class, "Alert");
	DataFlavor flavors[] = { TREE_PATH_FLAVOR };

	public synchronized DataFlavor[] getTransferDataFlavors() {
		return flavors;
	}
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return (flavor.match(TREE_PATH_FLAVOR));
	}


	public EventVO() {
		this.name = "";
		this.description = "";
		this.parent = "";
		this.alertListe = new ArrayList<Object>();
		this.catVO = null;
		this.categorypk = "";
		this.eventPk = "";
		this.timestamp = "";
		m_shape = new UnknownShape();
	}
	
	public EventVO(String eventPk, String parent, String name, String description, ArrayList<Object> alertListe, CategoryVO catVO, String categorypk, String timestamp) {
		super();
		this.name = name;
		this.description = description;
		this.parent = parent;
		this.alertListe = alertListe;
		this.catVO = catVO;
		this.categorypk = categorypk;
		this.eventPk = eventPk;
		this.timestamp = timestamp;
	}
	public EventVO(String eventPk, String parent, String name, String description, CategoryVO catVO, String timestamp) {
		super();
		this.name = name;
		this.description = description;
		this.parent = parent;
		this.catVO = catVO;
		this.eventPk = eventPk;
		this.timestamp = timestamp;
		alertListe = new ArrayList<Object>();
	}
	public EventVO(String eventPk, String parent, String name, String description, String categorypk, String timestamp) {
		super();
		this.name = name;
		this.description = description;
		this.parent = parent;
		this.categorypk = categorypk;
		this.eventPk = eventPk;
		this.timestamp = timestamp;
		alertListe = new ArrayList<Object>();
	}
	public EventVO(String eventPk, String parent, String name, String description, String timestamp) {
		super();
		this.name = name;
		this.description = description;
		this.parent = parent;
		this.eventPk = eventPk;
		this.timestamp = timestamp;
		alertListe = new ArrayList<Object>();
	}
	public ArrayList<Object> getAlertListe() { 
		return alertListe; 
	}	
	public void setAlertListe(ArrayList<Object> alertListe) { 
		this.alertListe = alertListe; 
	}	
	public String getCategorypk() { 
		return categorypk;
	}	
	public void setCategorypk(String categorypk) {
		this.categorypk = categorypk;
	}
	public CategoryVO getCatVO() {
		return catVO;
	}
	public void setCatVO(CategoryVO catVO) {
		this.catVO = catVO;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getEventPk() {
		return eventPk;
	}
	public void setEventPk(String eventPk) {
		this.eventPk = eventPk;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getParentpk() {
		return parent;
	}
	public void setParentpk(String parent) {
		this.parent = parent;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EventVO eventVO = (EventVO) o;

        return !(eventPk != null ? !eventPk.equals(eventVO.eventPk) : eventVO.eventPk != null);

    }

    @Override
    public int hashCode() {
        return eventPk != null ? eventPk.hashCode() : 0;
    }

    public void addAlerts(AlertVO ao){
		if(!alertListe.contains(ao))
			alertListe.add(ao);
		else
			System.out.println("Alert already exists!");
	}
	public void removeAlerts(AlertVO ao){
		if(alertListe.contains(ao))
			alertListe.remove(ao);
		else
			System.out.println("Alert does not exist!");
	}
	public String toString(){
		return name;
	}

	public double getEpicentreX() {
		return epicentre[0];
	}

	public void setEpicentreX(double epicentre) {
		this.epicentre[0] = epicentre;
	}
	
	public double getEpicentreY() {
		return epicentre[1];
	}
	
	public void setEpicentreY(double epicentre) {
		this.epicentre[1] = epicentre;
	}

	@Override
	public boolean hasValidPk() {
		if(getPk().startsWith("e"))
			return true;
		return false;
	}
}
