using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using com.ums.UmsCommon;
using com.ums.db;

namespace com.ums.ws.session
{
    public class SessionValidator
    {

        private readonly string _sharedSecret;

        public SessionValidator(string sharedSecret)
        {
            _sharedSecret = sharedSecret;
        }

        public bool IsValid(string sessionId, long deptPk)
        {
            if (IsTrusted(sessionId))
            {
                return true;
            }
            else
            {
                return Db.Session.Get<BbUserSession>(sessionId) != null;
            }
        }

        public bool IsTrusted(string sessionId)
        {
            var split = sessionId.IndexOf('.');
            if (split != -1)
            {
                return sessionId.Substring(split + 1).Equals(Helpers.CreateSHA512Hash(sessionId.Substring(0, split) + _sharedSecret));
            }
            else
            {
                return false;
            }
        }

        public string CreateTrusted()
        {
            var guid = Guid.NewGuid().ToString();
            return guid + "." + Helpers.CreateSHA512Hash(guid + _sharedSecret);
        }
    }
}