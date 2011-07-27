using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace pasinvoke
{
    public class UserInfo
    {
        private string sz_user;
        private string sz_company;
        private int l_department;

        public string User
        {
            get { return sz_user; }
            set { sz_user = value; }
        }
        public string Company
        {
            get { return sz_company; }
            set { sz_company = value; }
        }
        public int Department
        {
            get { return l_department; }
            set { l_department = value; }
        }
    }
}
