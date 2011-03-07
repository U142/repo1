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

    public static enum GpsUnitManufacturers {
        UNKNOWN(0, "Not Specified", 1),
        FALCOM(1, "Falcom", 2),
        LOMMY(2, "Lommy", 4);

        private final int id;
        private final String name;
        private final int filter;

        GpsUnitManufacturers(int id, String name, int filter) {
            this.id = id;
            this.name = name;
            this.filter = filter;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getFilter() {
            return filter;
        }

        public static String[] getNames() {
            final String[] names = new String[values().length];
            for (int i = 0; i < values().length; i++) {
                names[i] = values()[i].getName();
            }
            return names;
        }
    }


    public static enum GpsUnitUsertype {
        UNIVERSAL(0, "Universal", 1),
        PERSON(1, "Person", 2),
        DOG(2, "Dog", 4),
        CAR(3, "Car", 8);

        private final int id;
        private final String name;
        private final int filter;

        GpsUnitUsertype(int id, String name, int filter) {
            this.id = id;
            this.name = name;
            this.filter = filter;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getFilter() {
            return filter;
        }

        public static String[] getNames() {
            final String[] names = new String[values().length];
            for (int i = 0; i < values().length; i++) {
                names[i] = values()[i].getName();
            }
            return names;
        }
    }

    public static enum GpsUnitDynamic {
        STATIC(false, "Static", 1),
        DYNAMIC(true, "Dynamic", 2);

        private final boolean id;
        private final String name;
        private final int filter;

        GpsUnitDynamic(boolean id, String name, int filter) {
            this.id = id;
            this.name = name;
            this.filter = filter;
        }

        public boolean getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getFilter() {
            return filter;
        }
    }

    public static enum GpsUnitOnlineStatus {
        OFFLINE(false, "Offline", 1),
        ONLINE(true, "Online", 2);

        private final boolean id;
        private final String name;
        private final int filter;

        GpsUnitOnlineStatus(boolean id, String name, int filter) {
            this.id = id;
            this.name = name;
            this.filter = filter;
        }

        public boolean getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getFilter() {
            return filter;
        }
    }

    public static enum GpsUnitCarrierStatus {
        OFF_DUTY(0, "Off duty", 1),
        AVAILABLE(1, "Available", 2),
        OCCUPIED(2, "Occupied", 4);

        private final int id;
        private final String name;
        private final int filter;

        GpsUnitCarrierStatus(int id, String name, int filter) {
            this.id = id;
            this.name = name;
            this.filter = filter;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getFilter() {
            return filter;
        }
    }
		
}
