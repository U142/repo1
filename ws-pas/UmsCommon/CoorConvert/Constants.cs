using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace com.ums.UmsCommon.CoorConvert
{

    public class Ellipsoid
    {
	    public Ellipsoid(int Id, String name, double radius, double ecc)
	    {
		    id = Id; ellipsoidName = name; 
		    EquatorialRadius = radius; eccentricitySquared = ecc;
	    }

	    public int id;
        public String ellipsoidName;
        public double EquatorialRadius;
        public double eccentricitySquared;  

    };

    public class Constants
    {

        public const double PI = 3.14159265;
        public const double FOURTHPI = PI / 4;
        public const double deg2rad = PI / 180;
        public const double rad2deg = 180.0 / PI;

        public static Ellipsoid [] ellipsoid = 
        {//  id, Ellipsoid name, Equatorial Radius, square of eccentricity	
	        new Ellipsoid( -1, "Placeholder", 0, 0),//placeholder only, To allow array indices to match id numbers
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
	        new Ellipsoid( 23, "WGS-84", 6378137, 0.00669438)
        };
    }
}
