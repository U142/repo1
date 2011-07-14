using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Configuration;

namespace umssettings
{
    public class _settings
    {
        // Functions for required keys (throws exception if key is missing or malformated in app.config)
        public static int GetInt(string key)
        {
            int ret;
            try
            {
                ret = int.Parse(GetString(key));
            }
            catch (FormatException e)
            {
                throw new Exception(String.Format("Could not read key \"{0}\" ({1})", key, e.Message));
            }
            catch (Exception e)
            {
                throw new Exception(e.Message);
            }
            return ret;
        }
        public static bool GetBool(string key)
        {
            bool ret;
            try
            {
                ret = bool.Parse(GetString(key));
            }
            catch (FormatException e)
            {
                throw new Exception(String.Format("Could not read key \"{0}\" ({1})", key, e.Message));
            }
            catch (Exception e)
            {
                throw new Exception(e.Message);
            }
            return ret;
        }
        public static string GetString(string key)
        {
            string ret = "";
            try
            {
                ret = ConfigurationSettings.AppSettings[key];
                if (ret.Trim() == "")
                    throw new Exception("Missing required key \"" + key + "\"");
            }
            catch (NullReferenceException)
            {
                throw new Exception("Missing required key \"" + key + "\"");
            }
            catch (Exception e)
            {
                throw new Exception(e.Message, e.InnerException);
            }
            return ret;
        }

        // functions for optional keys with default values (throws exception if key is malformated, uses default value if missing)
        public static int GetValue(string key, int default_value)
        {
            int ret = default_value;
            string temp = GetValue(key, default_value.ToString());
            try
            {
                ret = int.Parse(temp);
            }
            catch (NullReferenceException)
            {
                // value not found, using default value
            }
            catch (Exception e)
            {
                throw new Exception(String.Format("Could not read key \"{0}\" ({1})", key, e.Message));
            }
            return ret;
        }
        public static bool GetValue(string key, bool default_value)
        {
            bool ret = default_value;
            string temp = GetValue(key, default_value.ToString());
            try
            {
                ret = bool.Parse(temp);
            }
            catch (NullReferenceException)
            {
                // value not found, using default value
            }
            catch (Exception e)
            {
                throw new Exception(String.Format("Could not read key \"{0}\" ({1})", key, e.Message));
            }
            return ret;
        }
        public static string GetValue(string key, string default_value)
        {
            string ret = default_value;
            try
            {
                string temp = ConfigurationSettings.AppSettings[key];
                if (temp.Trim() != "")
                    ret = temp;
            }
            catch (NullReferenceException)
            {
                // value not found, using default value
            }
            catch (Exception e)
            {
                throw new Exception(String.Format("Could not read key \"{0}\" ({1})", key, e.Message));
            }
            return ret;
        }
    }
}
