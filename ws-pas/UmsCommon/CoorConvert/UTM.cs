using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace com.ums.UmsCommon.CoorConvert
{
    public class UTM
    {
        public static double rad2deg = 180.0 / Math.PI;
        public static void UTM2LL(int ReferenceEllipsoid, double UTMNorthing, double UTMEasting, string UTMZone, char ZoneLetter,
			          ref double Lat, ref double Long )
        {
        //converts UTM coords to lat/long.  Equations from USGS Bulletin 1532 
        //East Longitudes are positive, West longitudes are negative. 
        //North latitudes are positive, South latitudes are negative
        //Lat and Long are in decimal degrees. 
	        //Written by Chuck Gantz- chuck.gantz@globalstar.com
            
	        double k0 = 0.9996;
	        double a = Constants.ellipsoid[ReferenceEllipsoid].EquatorialRadius;
            double eccSquared = Constants.ellipsoid[ReferenceEllipsoid].eccentricitySquared;
	        double eccPrimeSquared;
            double e1 = (1 - Math.Sqrt(1 - eccSquared)) / (1 + Math.Sqrt(1 - eccSquared));
	        double N1, T1, C1, R1, D, M;
	        double LongOrigin;
	        double mu, phi1, phi1Rad;
	        double x, y;
	        int ZoneNumber;
	        //char ZoneLetter;

            x = UTMEasting - 500000.0; //remove 500,000 meter offset for longitude
	        y = UTMNorthing;


            ZoneNumber = int.Parse(UTMZone);//strtoul(UTMZone, &ZoneLetter, 10);
            if ((ZoneLetter - 'N') < 0)
            {
                y -= 10000000.0; //remove 10,000,000 meter offset used for southern hemisphere
            }

            LongOrigin = (ZoneNumber - 1.0)*6.0 - 180.0 + 3.0;  //+3 puts origin in middle of zone

	        eccPrimeSquared = (eccSquared)/(1.0-eccSquared);

	        M = y / k0;
	        mu = M/(a*(1.0-eccSquared/4.0-3.0*eccSquared*eccSquared/64.0-5.0*eccSquared*eccSquared*eccSquared/256.0));

	        phi1Rad = mu	+ (3.0*e1/2.0-27.0*e1*e1*e1/32.0)*Math.Sin(2.0*mu) 
				        + (21.0*e1*e1/16.0-55.0*e1*e1*e1*e1/32.0)*Math.Sin(4.0*mu)
				        +(151.0*e1*e1*e1/96.0)*Math.Sin(6.0*mu);
	        phi1 = phi1Rad*rad2deg;

            N1 = a / Math.Sqrt(1 - eccSquared * Math.Sin(phi1Rad) * Math.Sin(phi1Rad));
            T1 = Math.Tan(phi1Rad) * Math.Tan(phi1Rad);
            C1 = eccPrimeSquared * Math.Cos(phi1Rad) * Math.Cos(phi1Rad);
            R1 = a * (1 - eccSquared) / Math.Pow(1 - eccSquared * Math.Sin(phi1Rad) * Math.Sin(phi1Rad), 1.5);
	        D = x/(N1*k0);

            Lat = phi1Rad - (N1 * Math.Tan(phi1Rad) / R1) * (D * D / 2.0 - (5.0 + 3.0 * T1 + 10.0 * C1 - 4.0 * C1 * C1 - 9.0 * eccPrimeSquared) * D * D * D * D / 24
					        +(61.0+90.0*T1+298.0*C1+45.0*T1*T1-252.0*eccPrimeSquared-3.0*C1*C1)*D*D*D*D*D*D/720.0);
	        Lat = Lat * rad2deg;

	        Long = (D-(1.0+2.0*T1+C1)*D*D*D/6.0+(5.0-2.0*C1+28*T1-3.0*C1*C1+8.0*eccPrimeSquared+24.0*T1*T1)
                            * D * D * D * D * D / 120.0) / Math.Cos(phi1Rad);
	        Long = LongOrigin + Long * rad2deg;

        }

        char UTMLetterDesignator(double Lat)
        {
        //This routine determines the correct UTM letter designator for the given latitude
        //returns 'Z' if latitude is outside the UTM limits of 84N to 80S
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

        public void LL2UTM(int ReferenceEllipsoid, double Lat, double Long, int WantedZone,
                     ref double UTMNorthing /*out*/, ref double UTMEasting/*out*/, ref String UTMZone/*out*/)
        {
        //converts lat/long to UTM coords.  Equations from USGS Bulletin 1532 
        //East Longitudes are positive, West longitudes are negative. 
        //North latitudes are positive, South latitudes are negative
        //Lat and Long are in decimal degrees
	        //Written by Chuck Gantz- chuck.gantz@globalstar.com

	        double a = Constants.ellipsoid[ReferenceEllipsoid].EquatorialRadius;
            double eccSquared = Constants.ellipsoid[ReferenceEllipsoid].eccentricitySquared;
	        double k0 = 0.9996;

	        double LongOrigin;
	        double eccPrimeSquared;
	        double N, T, C, A, M;
        	
        //Make sure the longitude is between -180.00 .. 179.9
	        double LongTemp = (Long+180)-(int)((Long+180)/360)*360-180; // -180.00 .. 179.9;

            double LatRad = Lat * Constants.deg2rad;
            double LongRad = LongTemp * Constants.deg2rad;
	        double LongOriginRad;
	        int    ZoneNumber;

	        ZoneNumber = (int)((LongTemp + 180)/6) + 1;
          
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
            if (WantedZone != 0)
                ZoneNumber = WantedZone;
	        LongOrigin = (ZoneNumber - 1)*6 - 180 + 3;  //+3 puts origin in middle of zone
	        LongOriginRad = LongOrigin * Constants.deg2rad;

	        //compute the UTM Zone from the latitude and longitude
	        //sprintf(UTMZone, "%d%c", ZoneNumber, UTMLetterDesignator(Lat));
            UTMZone = String.Format("{0}{1}", ZoneNumber, UTMLetterDesignator(Lat));

	        eccPrimeSquared = (eccSquared)/(1-eccSquared);

	        N = a/Math.Sqrt(1-eccSquared*Math.Sin(LatRad)*Math.Sin(LatRad));
	        T = Math.Tan(LatRad)*Math.Tan(LatRad);
	        C = eccPrimeSquared*Math.Cos(LatRad)*Math.Cos(LatRad);
	        A = Math.Cos(LatRad)*(LongRad-LongOriginRad);

	        M = a*((1	- eccSquared/4		- 3*eccSquared*eccSquared/64	- 5*eccSquared*eccSquared*eccSquared/256)*LatRad 
				        - (3*eccSquared/8	+ 3*eccSquared*eccSquared/32	+ 45*eccSquared*eccSquared*eccSquared/1024)*Math.Sin(2*LatRad)
									        + (15*eccSquared*eccSquared/256 + 45*eccSquared*eccSquared*eccSquared/1024)*Math.Sin(4*LatRad) 
									        - (35*eccSquared*eccSquared*eccSquared/3072)*Math.Sin(6*LatRad));
        	
	        UTMEasting = (double)(k0*N*(A+(1-T+C)*A*A*A/6
					        + (5-18*T+T*T+72*C-58*eccPrimeSquared)*A*A*A*A*A/120)
					        + 500000.0);

	        UTMNorthing = (double)(k0*(M+N*Math.Tan(LatRad)*(A*A/2+(5-T+9*C+4*C*C)*A*A*A*A/24
				         + (61-58*T+T*T+600*C-330*eccPrimeSquared)*A*A*A*A*A*A/720)));
	        if(Lat < 0)
		        UTMNorthing += 10000000.0; //10000000 meter offset for southern hemisphere
        }
    }
}
