package no.ums.pas.core;

import java.awt.Dimension;

import no.ums.pas.Draw;
import no.ums.pas.ParmController;
import no.ums.pas.core.controllers.StatusController;
import no.ums.pas.core.logon.Settings;
import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.maps.MapFrame;
import no.ums.pas.maps.defines.Navigation;
import no.ums.pas.send.SendController;

public class variables {
	
	public static UserInfo USERINFO = null;
	public static SendController SENDCONTROLLER = null;
	public static ParmController PARMCONTROLLER = null;
	public static Draw DRAW = null;
	public static Navigation NAVIGATION = null;
	public static MapFrame MAPPANE = null;
	public static Settings SETTINGS = null;
	public static StatusController STATUSCONTROLLER = null;
	public static Dimension MINMAPDIMENSIONS = new Dimension(100,100);
	public static float MAPZOOMSPEED = 0.25f; 
}
