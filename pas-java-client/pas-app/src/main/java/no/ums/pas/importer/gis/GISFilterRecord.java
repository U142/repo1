package no.ums.pas.importer.gis;

public class GISFilterRecord  extends GISRecord{
	
	private String ApartmentId;
	private int    gnrNo;
	private int  bnrNo;
	private int  fnrNo;
	private int  snrNo;
	private int seCadId;
	private int  seVaId;
	public String getApartmentId() {
		return ApartmentId;
	}
	public int getGnrNo() {
		return gnrNo;
	}
	public int getBnrNo() {
		return bnrNo;
	}
	public int getFnrNo() {
		return fnrNo;
	}
	public int getSnrNo() {
		return snrNo;
	}
	public int getSeCadId() {
		return seCadId;
	}
	public int getSeVaId() {
		return seVaId;
	}
	
	
	
	public GISFilterRecord(String apartmentId, int gnrNo, int bnrNo, int fnrNo,
			int snrNo, int seCadId, int seVaId) {
		super();
		ApartmentId = apartmentId;
		this.gnrNo = gnrNo;
		this.bnrNo = bnrNo;
		this.fnrNo = fnrNo;
		this.snrNo = snrNo;
		this.seCadId = seCadId;
		this.seVaId = seVaId;
	}
	
    public GISFilterRecord()
    {
    	super();
    }
	
	

	
	
	
	
	
	
	
	

}
