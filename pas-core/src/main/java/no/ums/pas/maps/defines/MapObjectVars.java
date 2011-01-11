package no.ums.pas.maps.defines;

public class MapObjectVars {
	public static final int FILTER_TYPE_MANUFACTURERS_ = 0;
	public static final int FILTER_TYPE_USERTYPE_ = 1;
	public static final int FILTER_TYPE_DYNAMIC_ = 2;
	public static final int FILTER_TYPE_ONLINE_STATUS_ = 3;
	public static final int FILTER_TYPE_STATUS_ = 4;
	public static final int FILTER_TYPE_NAME_ = 5;
	
	public static final String GPS_UNIT_MANUFACTURERS_ [][] = new String[][] {
																		new String[] { "0", "1", "2" },
																		new String[] { "Not Specified", "Falcom", "Lommy" },
																		new String[] { "1", "2", "4" }
																	};
	//public static final String GPS_UNIT_USAGE_ [] = new String[] { "Mobile", "Static" };
	public static final String GPS_UNIT_USERTYPE_ [][] = new String[][] { 
																		new String[] { "0", "1", "2", "3" }, //, "4", "5" },
																		new String[] { "Universal", "Person", "Dog", "Car" }, //, "P.O.I.", "Shelter" },
																		new String[] { "1", "2", "4", "8" }, //, "16", "32" }
																	 	};

	public static final String GPS_UNIT_SETUP_RETRIEVEMODE_ [][] = new String[][] { 
																	new String[] { "0", "1" }, 
																	new String[] { "Push", "Pull" }, 
																	new String[] { "1", "2" }
																   };
	public static final String GPS_UNIT_SETUP_INTERVALMODE_ [][] = new String[][] { 
																	new String[] { "0", "1" }, 
																	new String[] { "Distance", "Time" },
																	new String[] { "1", "2" }
																   };
	public static final String GPS_UNIT_EVENTS_ [][] = new String[][] {
																	new String[] { "0", "1", "2", "3", "4" },
																	new String[] { "Entering geofence", "Leaving geofence", "Battery low", "Door open/close", "Ignition on/off" },
																	new String[] { "1", "2", "4", "8", "16" }
													   				};

    public static final String GPS_UNIT_DYNAMIC_ [][] = new String[][] {
																	new String[] { "0", "1" },
																	new String[] { "Static", "Dynamic" },
																	new String[] { "1", "2" }
																	};
	public static final String GPS_UNIT_ONLINESTATUS_ [][] = new String[][] {
																	new String[] { "0", "1" },
																	new String[] { "Offline", "Online" },
																	new String[] { "1", "2" }
																	};
	public static final String GPS_UNIT_CARRIER_STATUS_ [][] 	= new String[][] {
																	new String[] { "0", "1", "2" },
																	new String[] { "Off duty", "Available", "Occupied" },
																	new String[] { "1", "2", "4" }
																	};
		
}
