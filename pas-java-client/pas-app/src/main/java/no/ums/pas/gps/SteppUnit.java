package no.ums.pas.gps;

public class SteppUnit extends Object {
	private String _sz_objectpk;
	private String _sz_id;
	private String _sz_hardware;
	private String _sz_firmware;
	private String _sz_imei;
	private String _sz_localip;
	private String _sz_cmdversion;
	private double _f_battery = 0;
	private boolean _b_gpsfix = false;
	private boolean _b_connected = false;
	private boolean _b_isverified = false;
	
	public void set_objectpk(String sz_objectpk) { _sz_objectpk = sz_objectpk; }
	public void set_id(String sz_id) { _sz_id = sz_id; }
	public void set_hardware(String sz_hw) { _sz_hardware = sz_hw; }
	public void set_firmware(String sz_fw) { _sz_firmware = sz_fw; }
	public void set_imei(String sz_imei) { _sz_imei = sz_imei; }
	public void set_localip(String sz_localip) { _sz_localip = sz_localip; }
	public void set_cmdversion(String sz_cmd) { _sz_cmdversion = sz_cmd; }
	public void set_battery(double d) { _f_battery = d; }
	public double get_battery() { return _f_battery; }
	public void set_gpsfix(boolean b) { _b_gpsfix = b; }
	
	public String get_objectpk() { return _sz_objectpk; }
	public String get_id() { return _sz_id; }
	public String get_hardware() { return _sz_hardware; }
	public String get_firmware() { return _sz_firmware; }
	public String get_imei() { return _sz_imei; }
	public String get_localip() { return _sz_localip; }
	public String get_cmdversion() { return _sz_cmdversion; }
	public boolean get_gpsfix() { return _b_gpsfix; }
	
	public void set_connected(boolean b) { _b_connected = b; }
	public boolean get_connected() { return _b_connected; }
	public void set_verified() { _b_isverified = true; }
	public boolean get_verified() { return _b_isverified; }

	public SteppUnit() {
	}
	public boolean verify() {
		if(get_imei().length()>0)
			return true;
		else
			return false;
	}
}