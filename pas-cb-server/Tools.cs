using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using com.ums.UmsCommon.CoorConvert;

namespace pas_cb_server
{
    class Tools
    {
        // convert a coordinate point
        public static void ConvertCoordinate(PolyPoint wgs84pt, out double xcoord, out double ycoord, COORDINATESYSTEM coordtype)
        {
            UTM utm = new UTM();

            double utm_north = 0, utm_east = 0;
            string utm_zone = "";
            long X, Y;

            xcoord = ycoord = 0;

            // convert from wgs84 to COORDINATE-type
            switch (coordtype)
            {
                case COORDINATESYSTEM.RD:
                    RD.wgs84_to_rd(wgs84pt.y, wgs84pt.x, out X, out Y);
                    xcoord = (double)X;
                    ycoord = (double)Y;
                    break;
                case COORDINATESYSTEM.UTM31:
                    utm.LL2UTM(23, wgs84pt.y, wgs84pt.x, 31, ref utm_north, ref utm_east, ref utm_zone); // WGS84 to UTM31
                    xcoord = utm_east;
                    ycoord = utm_north;
                    break;
                case COORDINATESYSTEM.UTM32:
                    utm.LL2UTM(23, wgs84pt.y, wgs84pt.x, 32, ref utm_north, ref utm_east, ref utm_zone); // WGS84 to UTM31
                    xcoord = utm_east;
                    ycoord = utm_north;
                    break;
                case COORDINATESYSTEM.WGS84:
                    xcoord = wgs84pt.x;
                    ycoord = wgs84pt.y;
                    break;
            }
        }

        public static void KeyReader()
        {
            while (CBServer.running)
            {
                try
                {
                    switch (Console.ReadLine().ToLower())
                    {
                        case "h":
                        case "?":
                        case "help":
                            Console.WriteLine("# List of commands:", 1);
                            Console.WriteLine("#", 1);
                            Console.WriteLine("#   h, help\tthis page", 1);
                            Console.WriteLine("#   t, test\trun self test", 1);
                            Console.WriteLine("#   ctrl+c\tstop parser", 1);
                            break;
                        case "q":
                        case "quit":
                        case "exit":
                            Console.WriteLine("Use ctrl+c to quit");
                            break;
                        case "t":
                        case "test":
                            test.Selftest.NewAlertTest();
                            break;
                        default:
                            Console.WriteLine("# Uknown command, try \"help\" for more info.", 1);
                            break;
                    }
                }
                catch
                {
                    // Exception means program is shutting down while still reading. If so, just break while
                    break;
                }
            }
            Log.WriteLog("Stopped keyreader thread", 9);
        }
    }
}
