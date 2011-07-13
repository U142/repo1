
package no.ums.pas.ums.tools;

class CoorConst {
	
	final static Ellipsoid ellipsoid[] = new Ellipsoid[]
	{//  id, Ellipsoid name, Equatorial Radius, square of eccentricity	
		new Ellipsoid( -1, "Placeholder", 0, 0.0),//placeholder only, To allow array indices to match id numbers
		new Ellipsoid( 1, "Airy", 6377563, 0.00667054),
		new Ellipsoid( 2, "Australian National", 6378160, 0.006694542),
		new Ellipsoid( 3, "Bessel 1841", 6377397, 0.006674372),
		new Ellipsoid( 4, "Bessel 1841 (Nambia) ", 6377484, 0.006674372),
		new Ellipsoid( 5, "Clarke 1866", 6378206, 0.006768658),
		new Ellipsoid( 6, "Clarke 1880", 6378249, 0.006803511),
		new Ellipsoid( 7, "Everest", 6377276, 0.006637847),
		new Ellipsoid( 8, "Fischer 1960 (Mercury) ", 6378166, 0.006693422),
		new Ellipsoid( 9, "Fischer 1968", 6378150, 0.006693422),
		new Ellipsoid( 10, "GRS 1967", 6378160, 0.006694605),
		new Ellipsoid( 11, "GRS 1980", 6378137, 0.00669438),
		new Ellipsoid( 12, "Helmert 1906", 6378200, 0.006693422),
		new Ellipsoid( 13, "Hough", 6378270, 0.00672267),
		new Ellipsoid( 14, "International", 6378388, 0.00672267),
		new Ellipsoid( 15, "Krassovsky", 6378245, 0.006693422),
		new Ellipsoid( 16, "Modified Airy", 6377340, 0.00667054),
		new Ellipsoid( 17, "Modified Everest", 6377304, 0.006637847),
		new Ellipsoid( 18, "Modified Fischer 1960", 6378155, 0.006693422),
		new Ellipsoid( 19, "South American 1969", 6378160, 0.006694542),
		new Ellipsoid( 20, "WGS 60", 6378165, 0.006693422),
		new Ellipsoid( 21, "WGS 66", 6378145, 0.006694542),
		new Ellipsoid( 22, "WGS-72", 6378135, 0.006694318),
		new Ellipsoid( 23, "WGS-84", 6378137, 0.00669438),
		new Ellipsoid( 24, "Bessel RT90", 1500102, 0.006674372)
//	739.845 -0.000010037483 
	};
}
class Ellipsoid {
	public int m_n_id;
	public String m_sz_name;
	public int EquatorialRadius;
	public double eccentricitySquared;
	Ellipsoid(int n_id, String sz_name, int n_eqrad, double f_sq) {
		m_n_id = n_id;
		m_sz_name = sz_name;
		EquatorialRadius = n_eqrad;
		eccentricitySquared = f_sq;
	}
}





public class CoorConverter {
    public static final double deg2rad = Math.PI / 180;
	public static final double rad2deg = 180.0 / Math.PI;

	public LLCoor newLLCoor(double lon, double lat) { return new LLCoor(lon, lat); }
	public UTMCoor newUTMCoor(double n, double e, String z) { return new UTMCoor(n, e, z); }
	public class LLCoor {
		private double m_lon;
		private double m_lat;
		public double get_lon() { return m_lon; }
		public double get_lat() { return m_lat; }
		public void set_lon(double l) { m_lon = l; }
		public void set_lat(double l) { m_lat = l; }
		public LLCoor() {
			
		}
		public LLCoor(double lon, double lat) {
			m_lon = lon;
			m_lat = lat;
		}
	}
	public class UTMCoor {
		public UTMCoor(double n, double e, String z) {
			f_northing = n;
			f_easting = e;
			sz_zone = z;
		}
		public double f_northing;
		public double f_easting;
		public String sz_zone;
		
		public double getNorthing() { return f_northing; }
		public double getEasting() { return f_easting; }
		public String getZone() { return sz_zone; }
	}	
	public class Correction {
		public Double phi;
		public Double lambda;
	}

	
	public CoorConverter() {
	}
	public char UTMLetterDesignator(double Lat)
	{
	//	This routine determines the correct UTM letter designator for the given latitude
	//	returns 'Z' if latitude is outside the UTM limits of 84N to 80S
		//Written by Chuck Gantz- chuck.gantz@globalstar.com
		char LetterDesignator;
	
		if((84 >= Lat) && (Lat >= 72)) LetterDesignator = 'X';
		else if((72 > Lat) && (Lat >= 64)) LetterDesignator = 'W';
		else if((64 > Lat) && (Lat >= 56)) LetterDesignator = 'V';
		else if((56 > Lat) && (Lat >= 48)) LetterDesignator = 'U';
		else if((48 > Lat) && (Lat >= 40)) LetterDesignator = 'T';
		else if((40 > Lat) && (Lat >= 32)) LetterDesignator = 'S';
		else if((32 > Lat) && (Lat >= 24)) LetterDesignator = 'R';
		else if((24 > Lat) && (Lat >= 16)) LetterDesignator = 'Q';
		else if((16 > Lat) && (Lat >= 8)) LetterDesignator = 'P';
		else if(( 8 > Lat) && (Lat >= 0)) LetterDesignator = 'N';
		else if(( 0 > Lat) && (Lat >= -8)) LetterDesignator = 'M';
		else if((-8> Lat) && (Lat >= -16)) LetterDesignator = 'L';
		else if((-16 > Lat) && (Lat >= -24)) LetterDesignator = 'K';
		else if((-24 > Lat) && (Lat >= -32)) LetterDesignator = 'J';
		else if((-32 > Lat) && (Lat >= -40)) LetterDesignator = 'H';
		else if((-40 > Lat) && (Lat >= -48)) LetterDesignator = 'G';
		else if((-48 > Lat) && (Lat >= -56)) LetterDesignator = 'F';
		else if((-56 > Lat) && (Lat >= -64)) LetterDesignator = 'E';
		else if((-64 > Lat) && (Lat >= -72)) LetterDesignator = 'D';
		else if((-72 > Lat) && (Lat >= -80)) LetterDesignator = 'C';
		else LetterDesignator = 'Z'; //This is here as an error flag to show that the Latitude is outside the UTM limits
	
		return LetterDesignator;
	}
	
	
	public UTMCoor LL2UTM(int ReferenceEllipsoid, double Lat, double Long)
	{
	//converts lat/long to UTM coords.  Equations from USGS Bulletin 1532 
	//East Longitudes are positive, West longitudes are negative. 
	//North latitudes are positive, South latitudes are negative
	//Lat and Long are in decimal degrees
	    double UTMNorthing;
        double UTMEasting;
        String UTMZone;
		double a = CoorConst.ellipsoid[ReferenceEllipsoid].EquatorialRadius;
		double eccSquared = CoorConst.ellipsoid[ReferenceEllipsoid].eccentricitySquared;
		double k0 = 0.9996;
	
		double LongOrigin;
		double eccPrimeSquared;
		double N, T, C, A, M;
		
	//Make sure the longitude is between -180.00 .. 179.9
		double LongTemp = (Long+180)-(int)((Long+180.0)/360.0)*360.0-180.0; // -180.00 .. 179.9;
	
		double LatRad = Lat*CoorConverter.deg2rad;
		double LongRad = LongTemp*CoorConverter.deg2rad;
		double LongOriginRad;
		int    ZoneNumber;
	
		ZoneNumber = (int)((LongTemp + 180) / 6 + 1);
	 
		if( Lat >= 56.0 && Lat < 64.0 && LongTemp >= 3.0 && LongTemp < 12.0 )
			ZoneNumber = 32;
	
	 // Special zones for Svalbard
		if( Lat >= 72.0 && Lat < 84.0 ) 
		{
		  if(      LongTemp >= 0.0  && LongTemp <  9.0 ) ZoneNumber = 31;
		  else if( LongTemp >= 9.0  && LongTemp < 21.0 ) ZoneNumber = 33;
		  else if( LongTemp >= 21.0 && LongTemp < 33.0 ) ZoneNumber = 35;
		  else if( LongTemp >= 33.0 && LongTemp < 42.0 ) ZoneNumber = 37;
		 }
		LongOrigin = (ZoneNumber - 1)*6 - 180 + 3;  //+3 puts origin in middle of zone
		LongOriginRad = LongOrigin * CoorConverter.deg2rad;
	
		//compute the UTM Zone from the latitude and longitude
		//sprintf(UTMZone, "%d%c", ZoneNumber, UTMLetterDesignator(Lat));
		UTMZone = Integer.toString(ZoneNumber) + UTMLetterDesignator(Lat);
	
		eccPrimeSquared = (eccSquared)/(1-eccSquared);
	
		N = a/Math.sqrt(1-eccSquared*Math.sin(LatRad)*Math.sin(LatRad));
		T = Math.tan(LatRad)*Math.tan(LatRad);
		C = eccPrimeSquared*Math.cos(LatRad)*Math.cos(LatRad);
		A = Math.cos(LatRad)*(LongRad-LongOriginRad);
	
		M = a*((1	- eccSquared/4		- 3*eccSquared*eccSquared/64	- 5*eccSquared*eccSquared*eccSquared/256)*LatRad 
					- (3*eccSquared/8	+ 3*eccSquared*eccSquared/32	+ 45*eccSquared*eccSquared*eccSquared/1024)*Math.sin(2*LatRad)
										+ (15*eccSquared*eccSquared/256 + 45*eccSquared*eccSquared*eccSquared/1024)*Math.sin(4*LatRad) 
										- (35*eccSquared*eccSquared*eccSquared/3072)*Math.sin(6*LatRad));
		
		UTMEasting = (double)(k0*N*(A+(1-T+C)*A*A*A/6
						+ (5-18*T+T*T+72*C-58*eccPrimeSquared)*A*A*A*A*A/120)
						+ 500000.0);
	
		UTMNorthing = (double)(k0*(M+N*Math.tan(LatRad)*(A*A/2+(5-T+9*C+4*C*C)*A*A*A*A/24
					 + (61-58*T+T*T+600*C-330*eccPrimeSquared)*A*A*A*A*A*A/720)));
		if(Lat < 0)
			UTMNorthing += 10000000.0; //10000000 meter offset for southern hemisphere
		return new UTMCoor(UTMNorthing, UTMEasting, UTMZone);
	}	

	public LLCoor UTM2LL(int ReferenceEllipsoid, double UTMNorthing, double UTMEasting, String UTMZone)
	{
		//converts UTM coords to lat/long.  Equations from USGS Bulletin 1532 
		//East Longitudes are positive, West longitudes are negative. 
		//North latitudes are positive, South latitudes are negative
		//Lat and Long are in decimal degrees. 
		//Written by Chuck Gantz- chuck.gantz@globalstar.com
		double Lat, Long;
		double k0 = 0.9996;
		double a = CoorConst.ellipsoid[ReferenceEllipsoid].EquatorialRadius;
		double eccSquared = CoorConst.ellipsoid[ReferenceEllipsoid].eccentricitySquared;
		double eccPrimeSquared;
		double e1 = (1-Math.sqrt(1-eccSquared))/(1+Math.sqrt(1-eccSquared));
		double N1, T1, C1, R1, D, M;
		double LongOrigin;
		double mu, phi1, phi1Rad;
		double x, y;
		int ZoneNumber;
		char ZoneLetter;

        x = UTMEasting - 500000.0; //remove 500,000 meter offset for longitude
		y = UTMNorthing;
		
		//ZoneNumber = strtoul(UTMZone, ZoneLetter, 10);
		ZoneNumber = Integer.parseInt(UTMZone.substring(0, 2));//strtoul(UTMZone, ZoneLetter, 10);
		ZoneLetter = UTMZone.charAt(2);
		if((ZoneLetter - 'N') >= 0) {

        }
        else
		{
            y -= 10000000.0;//remove 10,000,000 meter offset used for southern hemisphere
		}
		
		LongOrigin = (ZoneNumber - 1)*6 - 180 + 3;  //+3 puts origin in middle of zone
		
		eccPrimeSquared = (eccSquared)/(1-eccSquared);
		
		M = y / k0;
		mu = M/(a*(1-eccSquared/4-3*eccSquared*eccSquared/64-5*eccSquared*eccSquared*eccSquared/256));
		
		phi1Rad = mu	+ (3*e1/2-27*e1*e1*e1/32)*Math.sin(2*mu) 
					+ (21*e1*e1/16-55*e1*e1*e1*e1/32)*Math.sin(4*mu)
					+(151*e1*e1*e1/96)*Math.sin(6*mu);

		N1 = a/Math.sqrt(1-eccSquared*Math.sin(phi1Rad)*Math.sin(phi1Rad));
		T1 = Math.tan(phi1Rad)*Math.tan(phi1Rad);
		C1 = eccPrimeSquared*Math.cos(phi1Rad)*Math.cos(phi1Rad);
		R1 = a*(1-eccSquared)/Math.pow(1-eccSquared*Math.sin(phi1Rad)*Math.sin(phi1Rad), 1.5);
		D = x/(N1*k0);
		
		Lat = phi1Rad - (N1*Math.tan(phi1Rad)/R1)*(D*D/2-(5+3*T1+10*C1-4*C1*C1-9*eccPrimeSquared)*D*D*D*D/24
						+(61+90*T1+298*C1+45*T1*T1-252*eccPrimeSquared-3*C1*C1)*D*D*D*D*D*D/720);
		Lat = Lat * CoorConverter.rad2deg;
		
		Long = (D-(1+2*T1+C1)*D*D*D/6+(5-2*C1+28*T1-3*C1*C1+8*eccPrimeSquared+24*T1*T1)
						*D*D*D*D*D/120)/Math.cos(phi1Rad);
		Long = LongOrigin + Long * CoorConverter.rad2deg;
		return new LLCoor(Long, Lat);
	}
	
	//RD <-> WGS
    // constants
    private static long x0 = 155000;
    private static long y0 = 463000;
    private static double k = 0.9999079;
    private static double bigr = 6382644.571;
    private static double m = 0.003773954;
    private static double n = 1.000475857;
    private static double lambda0 = 0.094032038;
    private static double phi0 = 0.910296727;
    private static double l0 = 0.094032038;
    private static double b0 = 0.909684757;
    private static double e = 0.081696831;
    private static double a = 6377397.155;

    public UTMCoor wgs84_to_rd(double Lat, double Long)
    {
        // correction to lat/lon?
        Double phicor = 0.0, lamcor = 0.0; 
        Correction corr = correction(Lat, Long);
        phicor = corr.phi;
        lamcor = corr.lambda;
        double phibes = Lat - phicor;
        double lambes = Long - lamcor;

        // convert to RD
        double phi = phibes / 180 * Math.PI;
        double lambda = lambes / 180 * Math.PI;
        double qprime = Math.log(Math.tan(phi / 2 + Math.PI / 4));
        double dq = e / 2 * Math.log((e * Math.sin(phi) + 1) / (1 - e * Math.sin(phi)));
        double q = qprime - dq;
        double w = n * q + m;
        double b = Math.atan(Math.exp(w)) * 2 - Math.PI / 2;
        double dl = n * (lambda - lambda0);
        double d_1 = Math.sin((b - b0) / 2);
        double d_2 = Math.sin(dl / 2);
        double s2psihalf = d_1 * d_1 + d_2 * d_2 * Math.cos(b) * Math.cos(b0);
        double cpsihalf = Math.sqrt(1 - s2psihalf);
        double spsihalf = Math.sqrt(s2psihalf);
        double tpsihalf = spsihalf / cpsihalf;
        double spsi = spsihalf * 2 * cpsihalf;
        double cpsi = 1 - s2psihalf * 2;
        double sa = Math.sin(dl) * Math.cos(b) / spsi;
        double ca = (Math.sin(b) - Math.sin(b0) * cpsi) / (Math.cos(b0) * spsi);
        double r = k * 2 * bigr * tpsihalf;
        Double X, Y;
        X = r * sa + x0;
        Y = r * ca + y0;
        UTMCoor utm = new UTMCoor(X, Y, "");
        return utm;
    }

    public LLCoor rd_to_wgs84(double X, double Y)
    {
        // convert to WGS84
        double d_1 = X - x0;
        double d_2 = Y - y0;
        double r = Math.sqrt(d_1 * d_1 + d_2 * d_2);
        double sa; if (r != 0) sa = d_1 / r; else sa = 0;
        double ca; if (r != 0) ca = d_2 / r; else ca = 0;
        double psi = Math.atan2(r, k * 2 * bigr) * 2;
        double cpsi = Math.cos(psi);
        double spsi = Math.sin(psi);
        double sb = ca * Math.cos(b0) * spsi + Math.sin(b0) * cpsi;
        double cb = Math.sqrt(1 - sb * sb);
        double b = Math.acos(cb);
        double sdl = sa * spsi / cb;
        double dl = Math.asin(sdl);
        double lambda = dl / n + lambda0;
        double w = Math.log(Math.tan(b / 2 + Math.PI / 4));
        double q = (w - m) / n;
        double phiprime = Math.atan(Math.exp(q)) * 2 - Math.PI / 2;
        double dq, phi; phi = phiprime;
        for (int i = 0; i < 4; i++) // adjust dq and phi 4 times
        {
            dq = e / 2 * Math.log((e * Math.sin(phi) + 1) / (1 - e * Math.sin(phi)));
            phi = Math.atan(Math.exp(q + dq)) * 2 - Math.PI / 2;
        }
        lambda = lambda / Math.PI * 180;
        phi = phi / Math.PI * 180;

        // correction to lat/lon?
        Double phicor = 0.0, lamcor = 0.0; 
        Correction corr = correction(phi, lambda);
        phicor = corr.phi;
        lamcor = corr.lambda;
        Double Lat,Long;
        Lat = phi + phicor;
        Long = lambda + lamcor;
        LLCoor ll = new LLCoor(Long, Lat);
        return ll;
    }

    private Correction correction(double phi, double lambda)
    {
        double dphi = phi - 52;
        double dlam = lambda - 5;
        Correction corr = new Correction();
        corr.phi = (-96.862 - dphi * 11.714 - dlam * 0.125) * 0.00001;
        corr.lambda = (dphi * 0.329 - 37.902 - dlam * 14.667) * 0.00001;
        return corr;
    }
    
    
    public class RdCoordinate
    {
    	public double x;
    	public double y;
    }
    public class BesselCoordinate
    {
    	public double x;
    	public double y;
    	public double lambda;
    	public double phi;
    }
    public class WGS84Coordinate
    {
    	public WGS84Coordinate()
    	{
    		a = 0;
    		b = 0;
    	}
    	public WGS84Coordinate(double lat, double lon)
    	{
    		a = lon;
    		b = lat;
    	}
    	public double a;
    	public double b;
    }
	/*private static Double x0 = 1.55e5;
	private static Double y0 = 4.63e5;
	private static Double k = .9999079;
	private static Double bigr = 6382644.571;
	private static Double m = .003773953832;
	private static Double n = 1.00047585668;
	private static Double lambda0 = Math.PI * .029931327161111111;
	private static Double b0 = Math.PI * .28956165138333334;
	private static Double e = .08169683122;*/
    
    //public class RdWgsConverter {

    	/**
    	 * converts a RD coordinate into a WGS84 coordinate. Approximate
    	 * transformation has a 50 cm accuracy.
    	 * 
    	 * @param rdCoordinate
    	 *            RD coordinate.
    	 * @return WG@84 coordinate.
    	 */
    	public WGS84Coordinate rd2wgs84(RdCoordinate rdCoordinate) {
    		BesselCoordinate besselCoordinate = rd2bessel(rdCoordinate);
    		return bessel2wgs84(besselCoordinate);
    	}

    	/**
    	 * converts a WGS84 coordinate into a RD coordinate.
    	 * 
    	 * @param wgsCoordinate
    	 *            WGS84 coordinate.
    	 * @return RD coordinate.
    	 */
    	public RdCoordinate wgs842rd_(WGS84Coordinate wgsCoordinate) {
    		BesselCoordinate besselCoordinate = wgs842bessel(wgsCoordinate);
    		return bessel2rd(besselCoordinate);
    	}

    	// ------------------------------------------------------------------------
    	// input is x,y in RD output is phi,lambda on the Bessel ellipsoid
    	// ------------------------------------------------------------------------
    	private BesselCoordinate rd2bessel(RdCoordinate rdCoordinate) {
    		BesselCoordinate besselCoordinate = new BesselCoordinate();

    		Double d__1, d__2;
    		Double cpsi, spsi, phiprime, b;
    		Integer i;
    		Double q, r, w, ca, cb, dl, sa, sb;
    		Double dq, sdl, psi;

    		d__1 = rdCoordinate.x - x0;
    		d__2 = rdCoordinate.y - y0;
    		r = Math.sqrt(d__1 * d__1 + d__2 * d__2);
    		if (r != 0.) {
    			sa = (rdCoordinate.x - x0) / r;
    			ca = (rdCoordinate.y - y0) / r;
    		} else {
    			sa = 0.;
    			ca = 0.;
    		}
    		psi = Math.atan2(r, k * 2. * bigr) * 2.;
    		cpsi = Math.cos(psi);
    		spsi = Math.sin(psi);
    		sb = ca * Math.cos(b0) * spsi + Math.sin(b0) * cpsi;
    		d__1 = sb;
    		cb = Math.sqrt(1. - d__1 * d__1);
    		b = Math.acos(cb);
    		sdl = sa * spsi / cb;
    		dl = Math.asin(sdl);
    		besselCoordinate.lambda = dl / n + lambda0;
    		w = Math.log(Math.tan(b / 2. + Math.PI / 4.));
    		q = (w - m) / n;
    		phiprime = Math.atan(Math.exp(q)) * 2. - Math.PI / 2.;

    		for (i = 1; i <= 4; ++i) {
    			dq = e
    					/ 2.
    					* Math.log((e * Math.sin(phiprime) + 1.)
    							/ (1. - e * Math.sin(phiprime)));
    			besselCoordinate.phi = Math.atan(Math.exp(q + dq)) * 2. - Math.PI / 2.;
    			phiprime = besselCoordinate.phi;
    		}

    		besselCoordinate.lambda = besselCoordinate.lambda / Math.PI * 180.;
    		besselCoordinate.phi = besselCoordinate.phi / Math.PI * 180.;
    		return besselCoordinate;
    	}

    	// ------------------------------------------------------------------------
    	// input is phi,lambda on the Bessel ellipsoid, output is x,y in RD
    	// ------------------------------------------------------------------------
    	private RdCoordinate bessel2rd(BesselCoordinate besselCoordinate) {
    		RdCoordinate rdCoordinate = new RdCoordinate();
    		Double d__1, d__2;
    		Double cpsi, cpsihalf, spsi, spsihalf, tpsihalf, b, s2psihalf, q, r, w, ca, lambda, dl, sa, dq, qprime, phi;

    		phi = besselCoordinate.phi / 180. * Math.PI;
    		lambda = besselCoordinate.lambda / 180. * Math.PI;
    		qprime = Math.log(Math.tan(phi / 2. + Math.PI / 4.));
    		dq = e / 2.
    				* Math.log((e * Math.sin(phi) + 1.) / (1. - e * Math.sin(phi)));
    		q = qprime - dq;
    		w = n * q + m;
    		b = Math.atan(Math.exp(w)) * 2. - Math.PI / 2.;
    		dl = n * (lambda - lambda0);
    		d__1 = Math.sin((b - b0) / 2.);
    		d__2 = Math.sin(dl / 2.);
    		s2psihalf = d__1 * d__1 + d__2 * d__2 * Math.cos(b) * Math.cos(b0);
    		cpsihalf = Math.sqrt(1. - s2psihalf);
    		spsihalf = Math.sqrt(s2psihalf);
    		tpsihalf = spsihalf / cpsihalf;
    		spsi = spsihalf * 2. * cpsihalf;
    		cpsi = 1. - s2psihalf * 2.;
    		sa = Math.sin(dl) * Math.cos(b) / spsi;
    		ca = (Math.sin(b) - Math.sin(b0) * cpsi) / (Math.cos(b0) * spsi);
    		r = k * 2. * bigr * tpsihalf;
    		rdCoordinate.x = r * sa + x0;
    		rdCoordinate.y = r * ca + y0;
    		return rdCoordinate;
    	}

    	private WGS84Coordinate bessel2wgs84(BesselCoordinate besselCoordinate) {
    		Double dlam, dphi, lamcor, phicor;
    		dphi = besselCoordinate.phi - 52.;
    		dlam = besselCoordinate.lambda - 5.;
    		phicor = (-96.862 - dphi * (float) 11.714 - dlam * .125) * 1e-5;
    		lamcor = (dphi * (float) .329 - 37.902 - dlam * 14.667) * 1e-5;

    		WGS84Coordinate wgsCoordinate = new WGS84Coordinate();
    		wgsCoordinate.a = besselCoordinate.phi + phicor;
    		wgsCoordinate.b = besselCoordinate.lambda + lamcor;
    		return wgsCoordinate;
    	}

    	/**
    	 * converts a WGS84 coordinate into a bessel coordinate.
    	 * 
    	 * @param wgsCoordinate
    	 *            A WGS84 coordinate.
    	 * @return A Bessel coordinate.
    	 */
    	private BesselCoordinate wgs842bessel(WGS84Coordinate wgsCoordinate) {
    		BesselCoordinate besselCoordinate = new BesselCoordinate();
    		Double dlam, dphi, lamcor, phicor;
    		dphi = wgsCoordinate.a - 52.;
    		dlam = wgsCoordinate.b - 5.;
    		phicor = (-96.862 - dphi * (float) 11.714 - dlam * .125) * 1e-5;
    		lamcor = (dphi * (float) .329 - 37.902 - dlam * 14.667) * 1e-5;
    		besselCoordinate.phi = wgsCoordinate.a - phicor;
    		besselCoordinate.lambda = wgsCoordinate.b - lamcor;
    		return besselCoordinate;
    	}
    //}
}