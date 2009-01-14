using System;
using System.Data;
using System.Configuration;
using System.Linq;
using System.Web;
using System.Web.Security;
using System.Web.UI;
using System.Web.UI.HtmlControls;
using System.Web.UI.WebControls;
using System.Web.UI.WebControls.WebParts;
using System.Xml.Linq;

using System.Collections;
namespace com.ums.ws.vb.VB
{
    public class Pincode
    {
        public ArrayList generate_filenames(string pin)
        {
            ArrayList filenames = new ArrayList();

            for (int i = 0; i < pin.Length; i++)
                filenames.Add("\\\\195.119.0.167\\Backbone\\SndLib\\IN\\Num\\no\\000" + pin.Substring(i,1) + ".raw");

            return filenames;
        }
    }
}
