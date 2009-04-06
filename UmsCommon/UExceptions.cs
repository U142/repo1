using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace com.ums.UmsCommon
{
    public class UException : Exception
    {
        public UException(String s)
            : base(s)
        {

        }
    }

    public class UEmptySMSMessageException : UException
    {
        public UEmptySMSMessageException()
            : base("No text in SMS message")
        {
        }
    }
    public class UEmptySMSOadcException : UException
    {
        public UEmptySMSOadcException()
            : base("No text in SMS OADC field")
        {
        }
    }

    public class ULogonFailedException : UException
    {
        public ULogonFailedException()
            : base("Logon failed")
        {

        }
    }
    public class USendingFailedException : UException
    {
        public USendingFailedException()
            : base("Sending failed (Aborting)")
        {

        }
    }
    public class USendingPartiallyFailedException : UException
    {
        public USendingPartiallyFailedException()
            : base("Sending failed partially")
        {
        }
    }

    public class UFileNotFoundException : UException
    {
        public UFileNotFoundException(String s)
            : base(s)
        {

        }
    }
    public class UFileCopyException : UException
    {
        public UFileCopyException(String s)
            : base(s)
        {
        }
    }
    public class UFileDeleteException : UException
    {
        public UFileDeleteException(String s)
            : base(s)
        {

        }
    }
    public class UVerifyAlertException : UException
    {
        public UVerifyAlertException(String s)
            : base(s)
        {

        }
    }
    public class URefnoException : UException
    {
        public URefnoException()
            : base("Error retrieving a refno for sending.")
        {
        }
    }
    public class UXmlParseException : UException
    {
        public UXmlParseException(String s)
            : base(s)
        {
        }
    }
    public class UDbConnectionException : UException
    {
        public UDbConnectionException()
            : base("No database connection")
        {
        }
    }
    public class UDbQueryException : UException
    {
        public UDbQueryException(String s)
            : base(s)
        {
        }
    }
    public class UDbNoDataException : UException
    {
        public UDbNoDataException(String s)
            : base("No record(s) returned. " + s)
        {

        }
    }
    public class USendingTypeNotSupportedException : UException
    {
        public USendingTypeNotSupportedException(String s)
            : base(s)
        {

        }
    }
    public class UParsePolygonException : UException
    {
        public UParsePolygonException(String s)
            : base(s)
        {

        }
    }
    public class UParseEllipseException : UException
    {
        public UParseEllipseException(String s)
            : base(s)
        {

        }
    }
    public class UFileWriteException : UException
    {
        public UFileWriteException(String s)
            : base(s)
        {
        }
    }
    public class UProfileNotSupportedException : UException
    {
        public UProfileNotSupportedException(long l_profilepk, String s)
            : base(String.Format("profilepk={0} is not supported.{1}", l_profilepk, s))
        {
        }
    }
    public class UMapLoadFailedException : UException
    {
        public UMapLoadFailedException(Exception e)
            : base(e.Message)
        {
        }
    }
   
}
