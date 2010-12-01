using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using com.ums.UmsCommon.CoorConvert;
using System.Reflection;
using System.Text.RegularExpressions;

namespace pas_cb_server
{
    class Tools
    {
        static readonly Regex GSM_Alphabet_Regex = new Regex("[^a-zA-Z0-9 .Δ_ΦΓΛΩΠΨΣΘΞ@£$¥èéùìòÇØøÅåÆæßÉÄÖÑÜ§¿äöñüà+,/:;<=>?¡|^€{}*!#¤%&'()\r\n\\\\\\[\\]\"~-]");
        static readonly Regex GSM_Extended_Regex = new Regex("[|^€{}\\[\\]~\\\\]");

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

        // console info/input handling
        private static void PrintHelp()
        {
            Console.WriteLine("# List of commands:");
            Console.WriteLine("#");
            Console.WriteLine("#   h, ?\tthis page");
            Console.WriteLine("#   i\t\tforce status check (all active alerts)");
            Console.WriteLine("#   ctrl+t\trun self test");
            Console.WriteLine("#   ctrl+p\trun PLMN self test");
            Console.WriteLine("#   ctrl+m\tmodify current self test");
            Console.WriteLine("#   ctrl+k\tkill current self test");
            Console.WriteLine("#   ctrl+c\tstop parser");
            Console.WriteLine("#   p\t\tpause/resume parser thread");
            Console.WriteLine(String.Format("\r\nAssembly: {0}", Assembly.GetAssembly(typeof(CBServer)).FullName));
            Console.WriteLine("Parser thread is {0}", CBServer.parserpaused ? "paused" : "running");
        }
        public static void KeyReaderThread()
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
                                    cb_test.CBSelftest.NewAlert();
                                else
                                    Console.WriteLine("# Use ctrl+t to run test");
                                break;
                            case ConsoleKey.P:
                                if (key.Modifiers == ConsoleModifiers.Control)
                                    cb_test.CBSelftest.NewAlertPLMN();
                                else
                                {
                                    //Console.WriteLine("# Use ctrl+p to run PLMN test");
                                    // Pause parser thread
                                    Console.WriteLine("{0} parser thread.", CBServer.parserpaused ? "Starting" : "Pausing");
                                    CBServer.parserpaused = !CBServer.parserpaused;
                                }
                                break;
                            case ConsoleKey.K:
                                if (key.Modifiers == ConsoleModifiers.Control)
                                    cb_test.CBSelftest.KillAlert();
                                else
                                    Console.WriteLine("# Use ctrl+k to kill current test");
                                break;
                            case ConsoleKey.M:
                                if (key.Modifiers == ConsoleModifiers.Control)
                                    cb_test.CBSelftest.UpdateAlert();
                                else
                                    Console.WriteLine("# Use ctrl+m to modify current test");
                                break;
                            case ConsoleKey.I:
                                Log.WriteLog(String.Format("Checking status for all active messages."), 0);
                                CBStatus.CheckStatus();
                                break;
                            case ConsoleKey.H:
                            case ConsoleKey.Help:
                                PrintHelp();
                                break;
                            case ConsoleKey.MediaNext:
                            case ConsoleKey.MediaPlay:
                            case ConsoleKey.MediaPrevious:
                            case ConsoleKey.MediaStop:
                            case ConsoleKey.LaunchApp1:
                            case ConsoleKey.LaunchApp2:
                            case ConsoleKey.LaunchMail:
                            case ConsoleKey.LaunchMediaSelect:
                            case ConsoleKey.Applications:
                            case ConsoleKey.LeftWindows:
                            case ConsoleKey.RightWindows:
                            case ConsoleKey.LeftArrow:
                            case ConsoleKey.RightArrow:
                            case ConsoleKey.UpArrow:
                            case ConsoleKey.DownArrow:
                                // just ignore these keys
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
            Interlocked.Decrement(ref Settings.threads);
        }

        // gsm 7bit conversion
        public static string decodegsm(byte[] encoded)
        {
            List<byte> decoded = new List<byte>();

            int i_shift = 0;

            byte b_add = 0;
            byte b_base = 0;
            byte b_result = 0;

            foreach (byte enc in encoded)
            {
                // for every 7th shifts, add the adder byte before adding the next byte
                if (i_shift == 7)
                {
                    decoded.Add(b_add);

                    // reset shift and add
                    i_shift = 0;
                    b_add = 0;
                }

                // get base byte, shifted to the left and masked with 0fffffff (since it's 7 bits, not 8 bits)
                b_base = (byte)((enc << i_shift) & 0x7f);
                b_result = (byte)(b_base + b_add);

                decoded.Add(b_result);

                i_shift++;
                // get next adder byte which is based on this encoded byte (the 1st, 8th, 15th etc. are 0)
                b_add = (byte)((enc >> (8 - i_shift)));
            }

            return Encoding.ASCII.GetString(decoded.ToArray());
        }
        public static byte[] encodegsm(string decoded)
        {
            List<byte> encoded = new List<byte>();
            //byte[] b_decoded = Encoding.ASCII.GetBytes(decoded);
            byte[] b_decoded = GSMChar(decoded);

            int i_shift = 0;
            int i_pos = 0;

            byte b_add = 0;
            byte b_base = 0;
            byte b_result = 0;

            for (i_pos = 0; i_pos < b_decoded.Length; i_pos++)
            {
                b_add = 0; // init
                if ((i_pos + 1) < b_decoded.Length)
                    b_add = (byte)(b_decoded[i_pos + 1] << (7 - i_shift)); // get adder value from next byte

                if (i_shift < 7)
                {
                    // get base byte, shifted to the right and masked with 0fffffff (since it's 7 bits, not 8 bits)
                    b_base = (byte)((b_decoded[i_pos] >> i_shift) & 0x7f);
                    b_result = (byte)(b_base + b_add);

                    encoded.Add(b_result);
                    i_shift++;
                }
                else
                {
                    i_shift = 0;
                }
            }

            return encoded.ToArray();
        }
        private static byte[] GSMChar(string PlainText)
        {
            List<byte> GSMOutput = new List<byte>();
            // ` is not a conversion, just a untranslatable letter
            string strGSMTable = "";
            strGSMTable += "@£$¥èéùìòÇ\nØø\rÅå";
            strGSMTable += "Δ_ΦΓΛΩΠΨΣΘΞ`ÆæßÉ";
            strGSMTable += " !\"#¤%&'()*=,-./";
            strGSMTable += "0123456789:;<=>?";
            strGSMTable += "¡ABCDEFGHIJKLMNO";
            strGSMTable += "PQRSTUVWXYZÄÖÑÜ§";
            strGSMTable += "¿abcdefghijklmno";
            strGSMTable += "pqrstuvwxyzäöñüà";

            string strExtendedTable = "";
            strExtendedTable += "````````````````";
            strExtendedTable += "````^```````````";
            strExtendedTable += "````````{}`````\\";
            strExtendedTable += "````````````[~]`";
            strExtendedTable += "|```````````````";
            strExtendedTable += "````````````````";
            strExtendedTable += "`````€``````````";
            strExtendedTable += "````````````````";

            //string strGSMOutput = "";
            foreach (char cPlainText in PlainText.ToCharArray())
            {
                int intGSMTable = strGSMTable.IndexOf(cPlainText);
                if (intGSMTable != -1)
                {
                    GSMOutput.Add((byte)intGSMTable);
                    //strGSMOutput += intGSMTable.ToString("X2");
                    continue;
                }
                int intExtendedTable = strExtendedTable.IndexOf(cPlainText);
                if (intExtendedTable != -1)
                {
                    GSMOutput.Add(27);
                    GSMOutput.Add((byte)intExtendedTable);
                    //strGSMOutput += (27).ToString("X2");
                    //strGSMOutput += intExtendedTable.ToString("X2");
                    continue;
                }
                // not found, add ?
                GSMOutput.Add(63);
            }

            while(GSMOutput.Count<93)
                GSMOutput.Add(13); // pad with \r

            return GSMOutput.ToArray();
        }
        // message splitting (for pages)
        public static List<string> SplitMessage(string msgtext, int maxmsglength, int maxsplitoffset)
        {
            int lStartPos = 0;
            bool bDone = false;
            string szTmpMess = "";
            List<string> tmp = new List<string>();
            List<string> ret = new List<string>();
            char[] whitespace = new char[] { ' ', '.', ',', '-' };

            if (msgtext.Length + GSM_Extended_Regex.Matches(msgtext).Count > maxmsglength) // må splittes
            {
                while (!bDone)
                {
                    bool lookForWhitespace = false;
                    szTmpMess = msgtext.Substring(lStartPos);

                    // klipp opp meldingen
                    while (szTmpMess.Length + GSM_Extended_Regex.Matches(szTmpMess).Count > maxmsglength)
                    {
                        szTmpMess = szTmpMess.Remove(szTmpMess.Length - 1, 1);
                        if (maxsplitoffset > 0) // ignorere søk på whitespace hvis maxsplitoffset er 0
                            lookForWhitespace = true;
                    }
                    // søk etter første whitespace innenfor maxsplitoffset
                    if (lookForWhitespace && ((szTmpMess.Length - szTmpMess.LastIndexOfAny(whitespace)) < maxsplitoffset))
                        szTmpMess = szTmpMess.Remove(szTmpMess.LastIndexOfAny(whitespace));

                    if (maxsplitoffset > 0) // Trim hvis splitting skjer på whitespace
                        tmp.Add(szTmpMess.Trim());
                    else // ikke Trim hvis splittingen skjer på maxlength og ignorerer maxsplitoffset
                        tmp.Add(szTmpMess);

                    lStartPos += szTmpMess.Length;

                    if (lStartPos == msgtext.Length)
                        bDone = true;
                }
            }

            if (tmp.Count > 1)
            {
                foreach (string msg in tmp)
                {
                    ret.Add(msg);
                }
            }
            else
            {
                ret.Add(msgtext);
            }

            return ret;
        }
        public static string CleanMessage(string msgtext)
        {
            return GSM_Alphabet_Regex.Replace(msgtext, "?");
        }

        // convert t-mobile message number to correct endian
        public static byte[] GetBytes(int i)
        {
            if (BitConverter.IsLittleEndian)
                return BitConverter.GetBytes(i).Reverse().ToArray();
            else
                return BitConverter.GetBytes(i);
        }
        public static int ToInt32(byte[] b, int i)
        {
            if(BitConverter.IsLittleEndian)
                return BitConverter.ToInt32(b.Reverse().ToArray(), i);
            else
                return BitConverter.ToInt32(b, i);
        }
    }
}
