using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace pasinvoke
{
    public class ParmVO
    {
        private string sz_pk;
        private string sz_name;
        public ParmVO(string pk, string name)
        {
            sz_pk = pk;
            sz_name = name;
        }
        public string pk
        {
            get { return sz_pk; }
            set { sz_pk = value; }
        }
        public string name
        {
            get { return sz_name; }
            set { sz_name = value; }
        }
        public override string ToString()
        {
            return sz_name;
        }
    }
}
