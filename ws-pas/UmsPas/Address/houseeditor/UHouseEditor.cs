using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using com.ums.UmsCommon;
using com.ums.PAS.Database;

namespace com.ums.PAS.Address
{
    public class UHouseEditor 
    {
        public enum HOUSEEDITOR_OPERATION
        {
            INSERT = 0,
            UPDATE = 1,
            DELETE = 2,
        }
        public UHouseEditor(ref ULOGONINFO logon, ref UAddress adr, HOUSEEDITOR_OPERATION operation)
        {
            try
            {
                
                UAdrDb db = new UAdrDb(logon.sz_stdcc, 60, logon.l_deptpk);
                //db.CheckLogon(ref logon);
                switch (operation)
                {
                    case HOUSEEDITOR_OPERATION.INSERT:
                        db.InsertInhabitant(ref adr, ref logon);
                        break;
                    case HOUSEEDITOR_OPERATION.UPDATE:
                        db.UpdateInhabitant(ref adr, ref logon);
                        break;
                    case HOUSEEDITOR_OPERATION.DELETE:
                        db.DeleteInhabitant(ref adr, ref logon);
                        break;
                }
            }
            catch (Exception)
            {
                throw;
            }

        }
    }
}
