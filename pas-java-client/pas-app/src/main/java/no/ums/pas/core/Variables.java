package no.ums.pas.core;

import no.ums.pas.Draw;
import no.ums.pas.area.voobjects.AreaVO;
import no.ums.pas.core.controllers.StatusController;
import no.ums.pas.core.logon.Settings;
import no.ums.pas.core.logon.UserInfo;
import no.ums.pas.maps.MapFrame;
import no.ums.pas.maps.defines.Navigation;
import no.ums.pas.send.SendController;

import java.awt.Dimension;
import java.util.ArrayList;

public class Variables {
	
	private static UserInfo userInfo = null;
	private static SendController sendController = null;
    private static Draw draw = null;
	private static Navigation navigation = null;
	private static MapFrame mapFrame = null;
	private static Settings settings = null;
	private static StatusController statusController = null;
	
	private static String switchOverDate = "20140602000000";
	public static String getSwitchOverDate() {
		return switchOverDate;
	}

	private static ArrayList<AreaVO> areaList;
	public static ArrayList<AreaVO> getAreaList() {
		return areaList;
	}
	public static void setAreaList(ArrayList<AreaVO> areaList) {
		Variables.areaList = areaList;
	}
	private static float alpha = 0.01f;
	
	public static float getAlpha() {
		return alpha;
	}

    public static int getZoomLevel() {
        return zoomLevel;
    }

    public static void setZoomLevel(int zoomLevel) {
        Variables.zoomLevel = zoomLevel;
    }

    private static int zoomLevel = 4;

    public static final Dimension MINMAPDIMENSIONS = new Dimension(20000,20000);
	public static final float MAPZOOMSPEED = 0.25f;

    public static UserInfo getUserInfo() {
        return userInfo;
    }

    public static void setUserInfo(UserInfo userInfo) {
        Variables.userInfo = userInfo;
    }

    public static SendController getSendController() {
        return sendController;
    }

    public static void setSendController(SendController sendController) {
        Variables.sendController = sendController;
    }

    public static Draw getDraw() {
        return draw;
    }

    public static void setDraw(Draw draw) {
        Variables.draw = draw;
    }

    public static Navigation getNavigation() {
        return navigation;
    }

    public static void setNavigation(Navigation navigation) {
        Variables.navigation = navigation;
    }

    public static MapFrame getMapFrame() {
        return mapFrame;
    }

    public static void setMapFrame(MapFrame mapFrame) {
        Variables.mapFrame = mapFrame;
    }

    public static Settings getSettings() {
        return settings;
    }

    public static void setSettings(Settings settings) {
        Variables.settings = settings;
    }

    public static StatusController getStatusController() {
        return statusController;
    }

    public static void setStatusController(StatusController statusController) {
        Variables.statusController = statusController;
    }
}
