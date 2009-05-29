using System;
using System.Collections.Generic;
using System.Text;
using System.IO;

namespace UMSAlertiX
{
    class UMSAlertiXConfig
    {
        public string GetConfigValue(string szField)
        {
            szField = szField + "=";
            string szValue = "";
            string szTemp = "";

            try
            {
                StreamReader fConfig = File.OpenText(System.AppDomain.CurrentDomain.BaseDirectory + System.AppDomain.CurrentDomain.FriendlyName.Replace(".exe", ".cfg"));
                while (fConfig.Peek() > -1)
                {
                    szTemp = fConfig.ReadLine();
                    if (szTemp.Length > 1)																	// sjekk for tomme linjer
                        if (szTemp[0].ToString() != "#")													// sjekk for kommentar
                            if (szTemp.Length >= szField.Length)											// sjekk at linja er lenger enn søkestrengen
                                if (szTemp.Substring(0, szField.Length).ToLower() == szField.ToLower())		// søk etter streng
                                {
                                    szValue = szTemp.Substring(szField.Length);
                                    break;																// funnet, hopp ut av loop
                                }
                }
                fConfig.Close();
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }

            return szValue;
        }
    }
}
