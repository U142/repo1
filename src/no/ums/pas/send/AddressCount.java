package no.ums.pas.send;

public class AddressCount {
	private int m_n_private;
	private int m_n_company;
	private int m_n_privatemobile;
	private int m_n_companymobile;
	private int m_n_private_nonumber;
	private int m_n_company_nonumber;
	
	

	
	
	private int m_n_private_sms;
	private int m_n_company_sms;
	private int m_n_fax;
	private int m_n_duplicates;
	private int m_n_total = 0;
	private int m_n_total_by_types = 0;
	public int get_private() { return m_n_private; }
	public int get_company() { return m_n_company; }
	public int get_privatemobile() { return m_n_privatemobile; }
	public int get_companymobile() { return m_n_companymobile; }
	public int get_privatenonumber() { return m_n_private_nonumber; }
	public int get_companynonumber() { return m_n_company_nonumber; }
	public int get_privatesms() { return m_n_private_sms;}
	public int get_companysms() { return m_n_company_sms;}
	public int get_fax() { return m_n_fax; }
	public int get_total() { return m_n_total; }
	public int get_duplicates() { return m_n_duplicates; }
	public int get_total_by_types() { return m_n_total_by_types; }
	private void update() { m_n_total = get_private() + get_company() + get_privatemobile() + get_companymobile() + get_fax() + get_privatesms()  + get_companysms(); }
	public void set_private(int n) { _set_private(n); update(); }
	public void set_company(int n) { _set_company(n); update(); }
	public void set_privatemobile(int n) { _set_privatemobile(n); update(); }
	public void set_companymobile(int n) { _set_companymobile(n); update(); }
	public void set_privatenonumber(int n) { _set_privatenonumber(n); update(); }
	public void set_companynonumber(int n) { _set_companynonumber(n); update(); }
	public void set_privatesms(int n) { _set_privatesms(n); update(); }
	public void set_companysms(int n) { _set_companysms(n); update(); }
	public void set_fax(int n) { _set_fax(n); update(); }
	public void set_duplicates(int n) { _set_duplicates(n); }
	public void set_total_by_types(int n) { m_n_total_by_types = n; }
	public void inc_private(int n) { _set_private(n+get_private()); update(); }
	public void inc_company(int n) { _set_company(n+get_company()); update(); }
	public void inc_privatemobile(int n) { _set_privatemobile(n+get_privatemobile()); update();  }
	public void inc_companymobile(int n) { _set_companymobile(n+get_companymobile()); update(); }
	public void inc_privatenonumber(int n) { _set_privatenonumber(n+get_privatenonumber()); update(); }
	public void inc_companynonumber(int n) { _set_companynonumber(n+get_companynonumber()); update(); }
	public void inc_privatesms(int n) { _set_privatesms(n+get_privatesms()); update(); }
	public void inc_companysms(int n) { _set_companysms(n+get_companysms()); update(); }
	private void _set_private(int n) { m_n_private = n; }
	private void _set_company(int n) { m_n_company = n; }
	private void _set_privatemobile(int n) { m_n_privatemobile = n; }
	private void _set_companymobile(int n) { m_n_companymobile = n; }
	private void _set_privatenonumber(int n) { m_n_private_nonumber = n; }
	private void _set_companynonumber(int n) { m_n_company_nonumber = n; }
	private void _set_privatesms(int n) { m_n_private_sms = n; }
	private void _set_companysms(int n) { m_n_company_sms = n; }
	private void _set_fax(int n) { m_n_fax = n; }
	private void _set_duplicates(int n) { m_n_duplicates = n; }
	public AddressCount() {
		this(0, 0, 0, 0, 0, 0);
	}
	public AddressCount(int n_private, int n_company, int n_privmobile, int n_compmobile, 
			int n_privsms, int n_compsms, int n_privnonumber,
			int n_compnonumber, int n_fax, int n_duplicates) {
		this(n_private, n_company, n_privmobile, n_compmobile, n_privnonumber, n_compnonumber, n_fax, n_duplicates);
		_set_privatesms(n_privsms);
		_set_companysms(n_compsms);
		update();
	}
	public AddressCount(int n_private, int n_company, int n_privmobile, int n_compmobile, int n_privnonumber, int n_compnonumber) {
		this(n_private, n_company, n_privmobile, n_compmobile, n_privnonumber, n_compnonumber, 0, 0);
	}
	public AddressCount(int n_private, int n_company, int n_privmobile, int n_compmobile, int n_privnonumber,
						int n_compnonumber, int n_fax, int n_duplicates) {
		_set_private(n_private);
		_set_company(n_company);
		_set_privatemobile(n_privmobile);
		_set_companymobile(n_compmobile);
		_set_privatenonumber(n_privnonumber);
		_set_companynonumber(n_compnonumber);
		_set_fax(n_fax);
		_set_duplicates(n_duplicates);
		update();
	}
	
}
