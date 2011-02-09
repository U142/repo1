using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Web.Services.Protocols;

namespace com.ums.UmsCommon
{
    public class UException : Exception
    {
        public UException(String s)
            : base(s)
        {

        }
    }

    public class UServerDeniedAddressDatabaseException : UException
    {
        public UServerDeniedAddressDatabaseException()
            : base("Server denied use of any address databases")
        {

        }
    }

    public class UCBDurationNotSpecifiedException : UException
    {
        public UCBDurationNotSpecifiedException()
            : base("Duration parameter is not specified in LBAPARAMETERS")
        {

        }
    }

    public class UFileSerializationException : UException
    {
        String filename;
        public UFileSerializationException(String filename, Exception e)
            : base("Failed to serialize file " + filename + "\nCAUSE: "+ e.Message)
        {
            this.filename = filename;
        }
    }

    public class UFilePublishException : UException
    {
        String source_filename, dest_filename;
        public UFilePublishException(String source_filename, String dest_filename, Exception e)
            : base("Failed to publish file " + dest_filename + " to " + source_filename + "\nCAUSE: " + e.Message)
        {
            this.source_filename = source_filename;
            this.dest_filename = dest_filename;
        }
    }

    public class UCBSimulationException : UException
    {
        public UCBSimulationException(Exception e)
            : base("Failed to execute CB simulation sending\nCAUSE: " + e.Message)
        {
        }
            
    }

    public class USessionExpiredException : UException
    {
        public long seconds;
        public USessionExpiredException(long seconds)
            : base("Session expired after " + seconds + " seconds")
            //: base("Session expired after " + seconds + " seconds")
        {
            this.seconds = seconds;
        }

    }

    public class USessionDeletedException : USessionExpiredException
    {
        public USessionDeletedException(long seconds)
            : base(seconds)
        {
        }
    }

    public class USessionDoesNotExsistException : UException
    {
        public USessionDoesNotExsistException()
            : base("Session does not exsist")
        {
        }
    }

    public class UOneTimeKeyNotValidException : UException
    {
        public UOneTimeKeyNotValidException()
            : base("One time key is no longer valid")
        {
        }
    }

    public class UMalformedDateTimeException : UException
    {
        public UMalformedDateTimeException()
            : base("Error creating valid date-time")
        {
        }
    }

    public class UNoCountryCodeSpecifiedException : UException
    {
        public UNoCountryCodeSpecifiedException()
            : base("No Country Code was specified")
        {
        }
    }

    public class UXmlShapeException : UException
    {
        public UXmlShapeException(String s)
            : base("Error parsing XML Shape. " + s)
        {
        }
    }

    public class ULbaCellCoverageWmsServerNotRegisteredException : UException
    {
        public ULbaCellCoverageWmsServerNotRegisteredException(String sz_operator)
            : base(String.Format("LBA Cell coverage server not registered for \"{0}\"", sz_operator))
        {
        }
    }
    public class ULbaCellCoverageJobIdNotFoundException : UException
    {
        public ULbaCellCoverageJobIdNotFoundException(String sz_jobid)
            : base(String.Format("LBA Cell coverage, JobId not found \"{0}\"", sz_jobid))
        {
        }
    }
    public class ULbaCellCoverageJobNotPreparedException : UException
    {
        public ULbaCellCoverageJobNotPreparedException(String sz_operator)
            : base(String.Format("LBA Cell coverage, Job not yet prepared"))
        {
        }
    }

    public class UNoAccessOperatorsException : UException
    {
        public UNoAccessOperatorsException()
            : base("No access to any operators")
        {
        }
    }

    public class UNoCellBroadcastChannelAssignedForCompanyException : UException
    {
        public UNoCellBroadcastChannelAssignedForCompanyException()
            : base("No CB-channel assigned for company")
        {
        }
    }

    public class UGetCellBroadcastChannelException : UException
    {
        public UGetCellBroadcastChannelException()
            : base("Error retrieving CB-channel")
        {
        }

    }

    public class UProjectIsMarkedAsFinishedException : UException
    {
        public UProjectIsMarkedAsFinishedException()
            : base("The project is marked as ended")
        {
        }
    }
    public class UEventIsMarkedAsFinishedException : UException
    {
        public UEventIsMarkedAsFinishedException()
            : base("The Event is marked as ended")
        {
        }
    }

    public class ULBANoOperatorsReadyForConfirmCancel : UException
    {
        public ULBANoOperatorsReadyForConfirmCancel()
            : base("No operators are ready to confirm or cancel LBA sending")
        {
        }
    }

    public class ULBACouldNotPublishConfirmCancelFile : UException
    {
        public ULBACouldNotPublishConfirmCancelFile()
            : base("Could not publish the confirm or cancel file")
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
    public class UTestSendingUnavailableException : UException
    {
        public UTestSendingUnavailableException(String s)
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
    public class UParseStreetidException : UException
    {
        public UParseStreetidException(String s)
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
    public class UProfileDoesNotExistException : UException
    {
        public UProfileDoesNotExistException(long l_profilepk)
            : base(String.Format("profilepk={0} does not exist", l_profilepk))
        {
        }
    }
    public class UMapLoadFailedException : UException
    {
        public UMapLoadFailedException(Exception e, String bounds)
            : base(bounds + "\n" + e.Message)
        {
        }
    }
    public class UMapOverlayFailedException : UException
    {
        public UMapOverlayFailedException(String s, String sz_operator, String sz_jobid)
            : base(String.Format("{0}\n JobId={1}\n Operator={2}", s, sz_jobid, sz_operator))
        {

        }
    }
    public class UWeatherReportException : UException
    {
        public UWeatherReportException(String s)
            : base(s)
        {
        }
    }
    public class UNoStatusReadRightsException : UException
    {
        public UNoStatusReadRightsException(String s)
            : base(s)
        {
        }
    }
   

}
