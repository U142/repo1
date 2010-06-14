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

        private static void PrintHelp()
        {
            Console.WriteLine("# List of commands:");
            Console.WriteLine("#");
            Console.WriteLine("#   h, ?\tthis page");
            Console.WriteLine("#   ctrl+t\trun self test");
            Console.WriteLine("#   ctrl+m\tmodify current self test");
            Console.WriteLine("#   ctrl+k\tkill current self test");
            Console.WriteLine("#   ctrl+c\tstop parser");
        }
        public static void KeyReader()
        {
            while (CBServer.running)
            {
                if (Console.KeyAvailable)
                {
                    ConsoleKeyInfo key = Console.ReadKey(true);

                    if (key.KeyChar == '?')
                        PrintHelp();
                    else
                        switch (key.Key)
                        {
                            case ConsoleKey.Q:
                                Console.WriteLine("# Use ctrl+c to quit");
                                break;
                            case ConsoleKey.T:
                                if (key.Modifiers == ConsoleModifiers.Control)
                                    test.Selftest.NewAlert();
                                else
                                    Console.WriteLine("# Use ctrl+t to run test");
                                break;
                            case ConsoleKey.K:
                                if (key.Modifiers == ConsoleModifiers.Control)
                                    test.Selftest.KillAlert();
                                else
                                    Console.WriteLine("# Use ctrl+k to kill current test");
                                break;
                            case ConsoleKey.M:
                                if (key.Modifiers == ConsoleModifiers.Control)
                                    test.Selftest.UpdateAlert();
                                else
                                    Console.WriteLine("# Use ctrl+m to modify current test");
                                break;
                            case ConsoleKey.H:
                            case ConsoleKey.Help:
                                PrintHelp();
                                break;
                            default:
                                Console.WriteLine("# Unknown command '{0}'. Press 'h' or '?' for a list of available commands.", key.Key.ToString().ToLower());
                                break;
                        }
                }
                else
                {
                    Thread.Sleep(100);
                }
            }
            Log.WriteLog("Stopped keyreader thread", 9);
        }
    }
}
