using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.IO;
using System.Xml;
using com.ums.UmsCommon;

namespace com.ums.PAS.Weather
{
    public class UWeatherSearch
    {
        public double lon;
        public double lat;
        public int forecasts;
        public int interval;
        public long date;
    }
    public class UWeatherReportResults
    {
        public List<UWeatherResult> results = new List<UWeatherResult>();
    }
    public class UWeatherResult
    {
        public String source;
        public float height;
        public double lon;
        public double lat;
        public long datetime;
        public long localtime;
        public float winddirection;
        public float windspeed;
        public int cloudcover;
        public int symbol;
        public float temperature;
        public float temperaturemax;
        public float temperaturemin;
    }
    public class UWeatherReport
    {
        public UWeatherReportResults GetWeatherReport(ref UWeatherSearch w)
        {
            UWeatherReportResults res = new UWeatherReportResults();
            try
            {
                if (w.date <= 0) //current date
                {
                    String date = UCommon.UGetDateNow();
                    date += String.Format("{0:HH}0000", DateTime.UtcNow.ToLocalTime());
                    w.date = long.Parse(date);//UCommon.UGetDateTimeNow().
                }

                String sz_params = String.Format(UCommon.UGlobalizationInfo,
                    "lo={0}&"+
                    "la={1}&"+
                    "date={2}&"+
                    "forecasts={3}&"+
                    "interval={4}",
                    w.lon,
                    w.lat,
                    w.date,
                    w.forecasts,
                    w.interval);
                String szUrl = String.Format("{0}{1}", UCommon.USETTINGS.sz_url_weather_forecast, sz_params);
                HttpWebRequest req = (HttpWebRequest)WebRequest.Create(szUrl);
                HttpWebResponse response = (HttpWebResponse)req.GetResponse();
                StreamReader sr = new StreamReader(response.GetResponseStream());
                XmlDocument doc = new XmlDocument();
                String sz = sr.ReadToEnd();
                doc.LoadXml(sz);
                //XmlElement el = doc.GetElementById("pointforecast");
                XmlNodeList nl = doc.GetElementsByTagName("pointforecast");
                for (int i = 0; i < nl.Count; i++)
                {
                    XmlNode forecast = nl.Item(i);
                    UWeatherResult wr = new UWeatherResult();
                    XmlNodeList nl_forecast = forecast.ChildNodes;
                    for (int j = 0; j < nl_forecast.Count; j++)
                    {
                        XmlNode child = nl_forecast.Item(j);
                        if (child.Name.Equals("heigth"))
                            wr.height = float.Parse(child.FirstChild.Value.Replace('.', ','));
                        else if (child.Name.Equals("datetime"))
                            wr.datetime = long.Parse(child.FirstChild.Value);
                        else if (child.Name.Equals("source"))
                            wr.source = child.FirstChild.Value;
                        else if (child.Name.Equals("lon"))
                            wr.lon = double.Parse(child.FirstChild.Value.Replace('.', ','));
                        else if (child.Name.Equals("lat"))
                            wr.lat = double.Parse(child.FirstChild.Value.Replace('.', ','));
                        else if (child.Name.Equals("localtime"))
                            wr.localtime = long.Parse(child.FirstChild.Value);
                        else if (child.Name.Equals("winddirection"))
                            wr.winddirection = float.Parse(child.FirstChild.Value.Replace('.', ','));
                        else if (child.Name.Equals("windspeed"))
                            wr.windspeed = float.Parse(child.FirstChild.Value.Replace('.', ','));
                        else if (child.Name.Equals("cloudcover"))
                            wr.cloudcover = int.Parse(child.FirstChild.Value);
                        else if (child.Name.Equals("symbol"))
                            wr.symbol = int.Parse(child.FirstChild.Value);
                        else if (child.Name.Equals("temperature"))
                            wr.temperature = float.Parse(child.FirstChild.Value.Replace('.', ','));
                        else if (child.Name.Equals("maxT"))
                            wr.temperaturemax = float.Parse(child.FirstChild.Value.Replace('.', ','));
                        else if (child.Name.Equals("temperaturemin"))
                            wr.temperaturemin = float.Parse(child.FirstChild.Value.Replace('.', ','));
                    }

                    res.results.Add(wr);
                }
                /*XmlNodeList nl = el.ChildNodes;
                for (int i = 0; i < nl.Count; i++)
                {
                    XmlNode node = nl.Item(i);
                    UWeatherResult wr = new UWeatherResult();
                    wr.localtime = node.
                    res.results.Add(wr);
                }*/

            }
            catch (Exception e)
            {
                throw new UWeatherReportException(e.Message);
            }
            return res;
        }
    }

}
