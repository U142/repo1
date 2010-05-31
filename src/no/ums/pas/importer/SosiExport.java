package no.ums.pas.importer;


import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;

import no.ums.pas.PAS;
import no.ums.pas.core.storage.StorageController;
import no.ums.pas.maps.defines.CommonFunc;
import no.ums.pas.maps.defines.NavStruct;
import no.ums.pas.maps.defines.PolygonStruct;
import no.ums.pas.maps.defines.ShapeStruct;
import no.ums.pas.parm.voobjects.*;
import no.ums.pas.send.SendObject;
import no.ums.pas.ums.tools.CoorConverter;
import no.ums.pas.ums.tools.FilePicker;
import no.ums.pas.ums.tools.CoorConverter.LLCoor;
import no.ums.pas.ums.tools.CoorConverter.UTMCoor;

public class SosiExport
{
	private AlertVO [] alert;
	private File file;
	public static final String MIME_TYPE_SOSI_ = ".sos";
	public static final String[][] FILE_FILTERS_ = new String[][] {
		{ "SOSI files", MIME_TYPE_SOSI_ }
	};
	public SosiExport(AlertVO [] alert)
	{
		this.alert = alert;
		FilePicker picker = new FilePicker(PAS.get_pas(), PAS.get_pas().get_lookandfeel(), 
				StorageController.StorageElements.get_path(StorageController.PATH_HOME_), 
				"Choose file", FILE_FILTERS_, FilePicker.MODE_SAVE_);
		file = picker.getSelectedFile();
		
		try
		{
			//if(file.canWrite())
			{
				file.createNewFile();
				write();
			}
		}
		catch(Exception e)
		{
			no.ums.pas.ums.errorhandling.Error.getError().addError("File error", "Unable to create file", e, 1);
		}
	}
	
	protected void write()
	{
		try
		{
			double unit = 0.01;
			UTMCoor utm;
			CoorConverter coor = new CoorConverter();
			int utmzone = 32;
			int sosikoor = 32;
			
			LLCoor origo_ll = coor.newLLCoor(0, 0);
			LLCoor maxne_ll = coor.newLLCoor(0, 0);
			LLCoor minne_ll = coor.newLLCoor(0, 0);
			UTMCoor ORIGO = coor.newUTMCoor(0, 0, "32V");
			UTMCoor MAXNE = coor.newUTMCoor(0, 0, "32V");
			UTMCoor MINNE = coor.newUTMCoor(0, 0, "32V");
			ArrayList<ShapeStruct> polygons = new ArrayList<ShapeStruct>();
			for(int i=0; i < alert.length; i++) {
				polygons.add(alert[i].getShape());
			}
			try {
				NavStruct nav = CommonFunc.calc_bounds(polygons.toArray());
				origo_ll.set_lat((nav._ubo + nav._bbo) / 2);
				origo_ll.set_lon((nav._rbo + nav._lbo) / 2);
				String sone = "32V";
				ORIGO = coor.LL2UTM(23, origo_ll.get_lat(), origo_ll.get_lon(), 0, 0, sone, 0);
				sone = ORIGO.sz_zone;
				String temp = sone.substring(0, 2);
				utmzone = Integer.parseInt(temp);
				
				maxne_ll.set_lat(nav._ubo);
				maxne_ll.set_lon(nav._lbo);
				minne_ll.set_lat(nav._bbo);
				minne_ll.set_lon(nav._rbo);
				MAXNE = coor.LL2UTM(23, maxne_ll.get_lat(), maxne_ll.get_lon(), 0, 0, ORIGO.sz_zone, utmzone);
				MINNE = coor.LL2UTM(23, minne_ll.get_lat(), minne_ll.get_lon(), 0, 0, ORIGO.sz_zone, utmzone);
			} catch(Exception err) {
				err.printStackTrace();
			}
			
			switch(utmzone)
			{
			case 31:
				sosikoor = 21;
				break;
			case 32:
				sosikoor = 22;
				break;
			case 33:
				sosikoor = 23;
				break;
			case 34:
				sosikoor = 24;
				break;
			case 35:
				sosikoor = 25;
				break;
			case 36:
				sosikoor = 26;
				break;
			}
			
			
			String sz_curdate = no.ums.pas.ums.tools.Utils.get_current_date_formatted();
			FileWriter writer = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(writer);
			PrintWriter w = new PrintWriter(bw);
			w.println(SosiFile.FIELD_HODE_);
			w.println(SosiFile.FIELD_TEGNSETT_ + " ANSI");
			w.println("...KOORDSYS " + sosikoor);
			w.println(SosiFile.FIELD_ENHET_ + " " + unit);
			w.println(SosiFile.FIELD_ORIGO_ + " 0 0"); // + (long)(ORIGO.f_northing) + " " + (long)(ORIGO.f_easting));
			w.println(SosiFile.FIELD_OMRAADE_);
			w.println(SosiFile.FIELD_MIN_NE_ + " " + (long)(MINNE.f_northing) + " " + (long)(MINNE.f_easting));
			w.println(SosiFile.FIELD_MAX_NE_ + " " + (long)(MAXNE.f_northing) + " " + (long)(MAXNE.f_easting));
			w.println(SosiFile.FIELD_SOSI_VERSJON_ + " 3.4");
			w.println(SosiFile.FIELD_SOSI_NIVAA_ + " 4");
			w.println(SosiFile.FIELD_EIER_ + " \"" + PAS.get_pas().get_userinfo().get_compid() + "\"");
			w.println(SosiFile.FIELD_PRODUSENT_ + " \"" + PAS.get_pas().get_userinfo().get_compid() + "\"");
			w.println("!!!!!!!!!!!!!!!!!!!!!!!!!");
			w.println("!!!!!!!!!!!!!!!!!!!!!!!!!");
			w.println("!!!!!!!!!!!!!!!!!!!!!!!!!");
			for(int i=0; i < alert.length; i++)
			{
				PolygonStruct poly = alert[i].getShape().typecast_polygon();
				w.println(SosiFile.FIELD_FLATE_ + " " + (i+1) + ":");
				w.println(SosiFile.FIELD_OBJTYPE_ + " Areal");
				//w.println(SosiFile.FIELD_FTEMA_ + " ");
				w.println(SosiFile.FIELD_DIGDATO_ + " " + sz_curdate);
				w.println(SosiFile.FIELD_KVALITET_ + " 82");
				//w.println("..KOMM");
				//w.println("..ID");
				w.println(SosiFile.FIELD_NAME_ + " \"" + alert[i].getName() + "\"");
				w.println(SosiFile.FIELD_INFORMASJON_ + " \"Exported from UMS PAS\"");
				w.println(SosiFile.FIELD_REF_ + " :" + (i+1));
				w.println(SosiFile.FIELD_NE_);
				utm = coor.LL2UTM(23, poly.calc_bounds()._ubo, poly.calc_bounds()._lbo, 0, 0, "", 0);
				w.println((long)(utm.f_northing) + " " + (long)(utm.f_northing));
			}
			w.println("!!!!!!!!!!!!!!!!!!!!!!!!!");
			for(int i=0; i < alert.length; i++)
			{
				PolygonStruct poly = alert[i].getShape().typecast_polygon();
				w.println(SosiFile.FIELD_KURVE_ + " " + (i+1) + ":" );
				w.println(SosiFile.FIELD_OBJTYPE_ + " PASAlert");
				w.println(SosiFile.FIELD_LTEMA_ + " ");
				w.println(SosiFile.FIELD_DIGDATO_ + " " + sz_curdate);
				w.println(SosiFile.FIELD_KVALITET_ + " 82");
				//w.println("..KOMM");
				w.println(SosiFile.FIELD_INFORMASJON_ + " \"Exported from UMS PAS\"");
				w.println(SosiFile.FIELD_NE_);
				utm = coor.LL2UTM(23, poly.calc_bounds()._ubo, poly.calc_bounds()._lbo, 0, 0, "", 0);
				w.println((long)(utm.f_northing/unit) + " " + (long)(utm.f_easting/unit));
				w.println(SosiFile.FIELD_NE_);
				for(int p = 0; p < poly.get_size(); p++)
				{
					//w.println((long)(poly.get_coor_lon(p)) + " " + (long)(poly.get_coor_lat(p)) + " " + (p+1==poly.get_size() ? "...KP 1" : ""));
					utm = coor.LL2UTM(23, poly.get_coor_lat(p), poly.get_coor_lon(p), 0, 0, ORIGO.sz_zone, utmzone);
					w.println((long)(utm.f_northing/unit) + " " + (long)(utm.f_easting/unit));
					
				}
				
			}
			
			
			w.close();
		}
		catch(Exception e)
		{
			no.ums.pas.ums.errorhandling.Error.getError().addError("File error", "Unable to write to file", e, 1);			
		}
		
		
	}
	
}