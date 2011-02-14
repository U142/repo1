package no.ums.pas.ums.tools;

import no.ums.log.Log;
import no.ums.log.UmsLog;
import no.ums.pas.ums.errorhandling.Error;

import javax.sound.sampled.AudioPermission;
import java.io.FilePermission;
import java.security.AccessController;
import java.security.MessageDigest;
import java.security.Permission;
import java.util.ArrayList;






public class UMSSecurity {

    private static final Log log = UmsLog.getLogger(UMSSecurity.class);

    private SecurityManager m_sec;
	private UMSPermissionList m_list;
	public UMSPermissionList get_list() { return m_list; }
	public SecurityManager get_sec() { return m_sec; }
	
	public UMSSecurity() {
		//m_sec = System.getSecurityManager();
		m_list = new UMSPermissionList();
				
	}
	public void add_check(int n_permissiontype, Object param1, Object param2) {
		switch(n_permissiontype) {
			case UMSPermission.PERMISSION_FILE_:
				get_list().add_filepermission(((String)param1).toString(), ((String)param2).toString());
				break;
			case UMSPermission.PERMISSION_AUDIO_:
				get_list().add_audiopermission();
				break;
		}
	}
	
	public void check_permissions() {
		try
		{
			Permission p = null;
			for(int i=0; i < get_list().size(); i++) {
				try {
					p = get_list().getp(i).get_permission();
					//get_sec().checkPermission(p);
					AccessController.checkPermission(p);
					get_list().getp(i).set_ok(); //if no securityexception is thrown, set it to ok
				} catch(SecurityException e) {
					//securitycheck failed
					String sz_msg = "Security check failed for " + p.getName();
                    log.error("Security failed: %s", sz_msg, e);
//					Error.getError().addError("Security failed", sz_msg, e, 1);
				} catch(Exception e) {
					Error.getError().addError("Unknown security error", "Unknown security error", e, 1);
				}
			}
		}
		catch(Exception err)
		{
			
		}
	}
	
	public class UMSPermissionList extends ArrayList<UMSPermission> {
		public static final long serialVersionUID = 1;

		UMSPermissionList() {
			super();
		}
		public void add_filepermission(String param1, String param2) {
			UMSPermission perm = new UMSPermission(param1, param2);
			this.add(perm);
		}
		public void add_audiopermission() {
			UMSPermission perm = new UMSPermission();
			this.add(perm);
		}
		public UMSPermission getp(int i) {
			return (UMSPermission)this.get(i);
		}
	}
	public class UMSPermission extends Object {
		public static final int PERMISSION_FILE_ = 0;
		public static final int PERMISSION_AUDIO_ = 1;

		private int m_n_permissiontype;
		private Object m_param1;
		private Object m_param2;
		Permission m_perm;
		boolean b_ok = false;
		public void set_ok() { b_ok = true; }
		public boolean get_ok() { return b_ok; }
		public Permission get_permission() { return m_perm; }
		public int get_permissiontype() { return m_n_permissiontype; }
		UMSPermission(String sz_path, String sz_access) { //file
			m_param1 = sz_path;
			m_param2 = sz_access;
			m_perm = new FilePermission(sz_path, sz_access);
			m_n_permissiontype = PERMISSION_FILE_;
		}
		UMSPermission() {
			m_n_permissiontype = PERMISSION_AUDIO_;
			m_perm = new AudioPermission("play,record");
		}
		
	}		
	
}