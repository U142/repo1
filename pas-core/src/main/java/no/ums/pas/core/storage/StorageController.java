package no.ums.pas.core.storage;

import java.io.*;

import no.ums.pas.ums.errorhandling.Error;



public class StorageController {
	public static final int PATH_HOME_ = 0;
	public static final int PATH_TEMPWAV_ = 1;
	public static final int PATH_STATUS_ = 2;
	public static final int PATH_FLEETCONTROL_ = 3;
	public static final int PATH_USERSETTINGS_ = 4;
	public static final int PATH_GISIMPORT_ = 5;
	public static final int PATH_PARM_ = 6;
	
	
	public StorageController() {
		
	}
	
	public void create_storageelements(String sz_home, String sz_tempwav, String sz_status, 
										String sz_fleetcontrol, String sz_usersettings, String sz_gisimport, 
										String sz_parm) {
		StorageElements.set_home(sz_home);
		//StorageElements.set_tempwav(StorageElements.get_home() + sz_tempwav);
		StorageElements.set_tempwav(System.getProperty("java.io.tmpdir") + sz_tempwav);
		StorageElements.set_status(StorageElements.get_home() + sz_status);
		StorageElements.set_fleetcontrol(StorageElements.get_home() + sz_fleetcontrol);
		StorageElements.set_usersettings(StorageElements.get_home() + sz_usersettings);
		StorageElements.set_gisimport(StorageElements.get_home() + sz_gisimport);
		StorageElements.set_parm(StorageElements.get_home() + sz_parm);
	}
	
	
	public static class StorageElements {
		private static String m_sz_home;
		private static String m_sz_path_tempwav;
		private static String m_sz_path_status;
		private static String m_sz_path_fleetcontrol;
		private static String m_sz_path_usersettings;
		private static String m_sz_path_gisimport;
		private static String m_sz_path_parm;
		private static String get_home() { return m_sz_home; }
		private static String get_tempwav() { return m_sz_path_tempwav; }
		private static String get_status() { return m_sz_path_status; }
		private static String get_fleetcontrol() { return m_sz_path_fleetcontrol; }
		private static String get_usersettings() { return m_sz_path_usersettings; }
		private static String get_gisimport() { return m_sz_path_gisimport; }
		private static String get_parm() { return m_sz_path_parm; }
		private static String m_sz_lasterror = null;
		private static void set_lasterror(String sz_error) { m_sz_lasterror = sz_error; }
		public static String get_lasterror() { return m_sz_lasterror; }
		protected static void set_home(String sz_home) {
			m_sz_home = sz_home;
			try {
				create_path(get_path(PATH_HOME_));
			} catch(IOException e) {
				set_lasterror("ERROR: " + e.getMessage());
				Error.getError().addError("StorageController","IOException in set_home",e,1);
			}
		}
		protected static void set_tempwav(String sz_path) { 
			m_sz_path_tempwav = sz_path;
			try {
				create_path(get_path(PATH_TEMPWAV_));
			} catch(IOException e) {
				set_lasterror("ERROR: " + e.getMessage());
				Error.getError().addError("StorageController","IOException in set_tempwav",e,1);
			}
		}
		protected static void set_status(String sz_path) { 
			m_sz_path_status = sz_path;
			try {
				create_path(get_path(PATH_STATUS_));
			} catch(IOException e) {
				set_lasterror("ERROR: " + e.getMessage());
				Error.getError().addError("StorageController","IOException in set_status",e,1);
			}
		}
		protected static void set_fleetcontrol(String sz_path) { 
			m_sz_path_fleetcontrol = sz_path;
			try {
				create_path(get_path(PATH_FLEETCONTROL_));
			} catch(IOException e) {
				set_lasterror("ERROR: " + e.getMessage());
				Error.getError().addError("StorageController","IOException in set_fleetcontrol",e,1);
			}
		}
		protected static void set_usersettings(String sz_path) { 
			m_sz_path_usersettings = sz_path; 
			try {
				create_path(get_path(PATH_USERSETTINGS_));
			} catch(IOException e) {
				set_lasterror("ERROR: " + e.getMessage());
				Error.getError().addError("StorageController","IOException in set_usersettings",e,1);
			}
		}
		protected static void set_gisimport(String sz_path) {
			m_sz_path_gisimport = sz_path;
			try {
				create_path(get_path(PATH_GISIMPORT_));
			} catch(IOException e) {
				set_lasterror("ERROR: " + e.getMessage());
				Error.getError().addError("StorageController","IOException in set_gisimport",e,1);
			}
		}
		protected static void set_parm(String sz_path) {
			m_sz_path_parm = sz_path;
			try {
				create_path(get_path(PATH_PARM_));
			} catch(IOException e) {
				set_lasterror("ERROR: " + e.getMessage());
				Error.getError().addError("StorageController","IOException in set_parm",e,1);
			}
		}
		// Satte denne fra private til public for å kunne brukes ved opprettelse av parm underkataloger
		//
		public static boolean create_path(String sz_path) throws IOException {
			boolean b_ret = false;
			try {
				b_ret = new File(sz_path).mkdirs();
			} catch(Exception e) {
				Error.getError().addError("StorageController","Exception in create_path",e,1);
				throw new IOException("Error creating directory " + sz_path);
			}
			return b_ret;
		}
		public static String get_path(int n_type) {
			if(n_type == PATH_HOME_)
				return get_home();
			else if(n_type == PATH_TEMPWAV_)
				return get_tempwav();
			else if(n_type == PATH_STATUS_)
				return get_status();
			else if(n_type == PATH_FLEETCONTROL_)
				return get_fleetcontrol();
			else if(n_type == PATH_USERSETTINGS_)
				return get_usersettings();
			else if(n_type == PATH_GISIMPORT_)
				return get_gisimport();
			else if(n_type == PATH_PARM_)
				return get_parm();
			return null;
		}
	}
	
}