using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using com.ums.pas.cap.CapServiceReference;
using com.ums.UmsParm;
using com.ums.UmsCommon;
using com.ums.pas.cap;


namespace com.ums.PAS.cap
{
    public class Cap
    {
        public class CapFieldsNotSpecifiedException : Exception
        {
            public CapFieldsNotSpecifiedException(String s)
                : base("No CAP fields are specified for this sending\n" + s)
            {
            }
        }

        public void SendCap(UMAPSENDING mapSending)
        {
            try
            {
                if (mapSending.capFields == null)
                {
                    throw new CapFieldsNotSpecifiedException("Unable to publish CAP");
                }
                CapProxy proxy = new CapProxy();
                proxy.PublishCap(ConvertMapsendingToCap(mapSending));
            }
            catch (Exception e)
            {
                ULog.error(e.Message);
            }
        }

        protected String GenerateCapReferences(UMAPSENDING m)
        {
            if (m.b_resend)
            {
                return String.Format("{0},{1},{2}",
                    GenerateCapSender(m),
                    GenerateCapIdentifier(m.n_projectpk, m.n_resend_refno),
                    "Here we need sent timestamp of the original message");
            }
            return null;
        }


        protected String GenerateCapIdentifier(long lProjectPk, long lRefno)
        {
            return String.Format("{0}.{1}", lProjectPk, lRefno);
        }

        protected String GenerateCapSender(UMAPSENDING m)
        {
            return String.Format("{0}.{1}", "pas.ums.no", m.logoninfo.sz_compid);
        }

        protected String GenerateCapDatetime(DateTime dt)
        {
            return "";
        }
        protected String GetCompanyName(int lCompPk)
        {
            PASUmsDb db = new PASUmsDb();
            return db.getCompanyName(lCompPk);
        }
        protected String GetProjectName(long lProjectPk)
        {
            PASUmsDb db = new PASUmsDb();
            BBPROJECT proj = new BBPROJECT();
            db.GetProjectInfo(lProjectPk, ref proj);
            return proj.sz_name;
        }


        public alert ConvertMapsendingToCap(UMAPSENDING mapSending)
        {
            alert capAlert = new alert();
            bool b_voice_active = false;
            bool b_sms_active = false;
            bool b_lba_active = false;
            bool b_tas_active = false;
            if (mapSending.doSendSMS() && ((mapSending.n_addresstypes & (long)ADRTYPES.FIXED_COMPANY_ALT_SMS) > 0 ||
                (mapSending.n_addresstypes & (long)ADRTYPES.FIXED_PRIVATE_ALT_SMS) > 0 ||
                (mapSending.n_addresstypes & (long)ADRTYPES.SMS_COMPANY) > 0 ||
                (mapSending.n_addresstypes & (long)ADRTYPES.SMS_COMPANY_ALT_FIXED) > 0 ||
                (mapSending.n_addresstypes & (long)ADRTYPES.SMS_PRIVATE) > 0 ||
                (mapSending.n_addresstypes & (long)ADRTYPES.SMS_PRIVATE_ALT_FIXED) > 0))
            {
                b_sms_active = true;
            }
            if (mapSending.doSendVoice() && ((mapSending.n_addresstypes & (long)ADRTYPES.FIXED_COMPANY_ALT_SMS) > 0 ||
                (mapSending.n_addresstypes & (long)ADRTYPES.FIXED_PRIVATE_ALT_SMS) > 0 ||

                (mapSending.n_addresstypes & (long)ADRTYPES.FIXED_COMPANY) > 0 ||
                (mapSending.n_addresstypes & (long)ADRTYPES.FIXED_PRIVATE) > 0 ||

                (mapSending.n_addresstypes & (long)ADRTYPES.SMS_COMPANY_ALT_FIXED) > 0 ||
                (mapSending.n_addresstypes & (long)ADRTYPES.SMS_PRIVATE_ALT_FIXED) > 0 ||

                (mapSending.n_addresstypes & (long)ADRTYPES.MOBILE_COMPANY) > 0 ||
                (mapSending.n_addresstypes & (long)ADRTYPES.MOBILE_PRIVATE) > 0 ||

                (mapSending.n_addresstypes & (long)ADRTYPES.FIXED_COMPANY_AND_MOBILE) > 0 ||
                (mapSending.n_addresstypes & (long)ADRTYPES.FIXED_PRIVATE_AND_MOBILE) > 0 ||

                (mapSending.n_addresstypes & (long)ADRTYPES.MOBILE_PRIVATE_AND_FIXED) > 0 ||
                (mapSending.n_addresstypes & (long)ADRTYPES.MOBILE_COMPANY_AND_FIXED) > 0))
            {
                b_voice_active = true;
            }
            if ((mapSending.n_addresstypes & (long)ADRTYPES.LBA_TEXT) > 0)
            {
                b_lba_active = true;
            }
            if ((mapSending.n_addresstypes & (long)ADRTYPES.SENDTO_TAS_SMS) > 0)
            {
                b_tas_active = true;
            }

            alert capAlertField = new alert();
            capAlert.senderField = String.Format(GenerateCapSender(mapSending));


            capAlert.statusField = mapSending.capFields.status; //alertStatus.Actual;


            capAlert.sentField = DateTime.Now;

            capAlert.identifierField = GenerateCapIdentifier(mapSending.n_projectpk, mapSending.n_refno); //mapSending.n_projectpk + "." + mapSending.n_refno.ToString();

            capAlert.scopeField = mapSending.capFields.scope; //alertScope.Public;

            capAlert.incidentsField = String.Format("\"{0}\"", mapSending.sz_sendingname);

            capAlert.noteField = mapSending.capFields.note; //"Formål og omfang av hendelse. Må eventuelt spesifiseres i PAS"; //String.Format("SMS={0} Voice={1} LocationBasedAlert={2}", mapSending.doSendSMS(), mapSending.doSendVoice(), mapSending.m_lba!=null);

            //capAlert.referencesField = "Kan her referere til tidligere CAP/PAS-meldinger (f.eks. ved resend? eller flere alerts i samme PAS-prosjekt/event?)";
            capAlert.referencesField = GenerateCapReferences(mapSending);


            capAlert.sourceField = mapSending.capFields.source; // "Startet av operatør eller dings, dette må eventuelt legges inn i PAS-sendeobjekt";

            capAlert.msgTypeField = (mapSending.b_resend ? alertMsgType.Update : alertMsgType.Alert);


            capAlert.restrictionField = mapSending.capFields.restriction; //"Hvis Scope=restricted skal denne vise til hvem den er ment for";

            capAlert.addressesField = null;

            capAlert.codeField = mapSending.capFields.code;

            alertInfo setAlertInfo = new alertInfo();
            capAlert.infoField = new alertInfo[1];
            capAlert.infoField[0] = new alertInfo(); //set in struct

            alertInfoArea setAlertInfoArea = new alertInfoArea();
            if (mapSending.GetType().Equals(typeof(UPOLYGONSENDING)))
            {
                UPOLYGONSENDING polygon = (UPOLYGONSENDING)mapSending;
                HashSet<String> hashPolygon = new HashSet<string>();
                setAlertInfoArea.areaDescField = "Polygon";
                String polyString = String.Empty;
                int iCount = -1;
                while(iCount <= polygon.polygonpoints.Length)
                {
                    int id = ++iCount % polygon.polygonpoints.Length;
                    UMapPoint p = polygon.polygonpoints[id];
                    polyString += String.Format(UCommon.UGlobalizationInfo, "{0},{1} ", Math.Round(p.lat, 4), Math.Round(p.lon,4));
                }
                hashPolygon.Add(polyString);
                setAlertInfoArea.polygonField = hashPolygon.ToArray();
            }
            else if (mapSending.GetType().Equals(typeof(UELLIPSESENDING)))
            {
                UELLIPSESENDING eSending = (UELLIPSESENDING)mapSending;
                HashSet<String> hashPolygon = new HashSet<string>();
                setAlertInfoArea.areaDescField = "Elliptical Polygon";
                double[,] polyOut = new double[36, 2];
                UCommon.ConvertEllipseToPolygon(eSending.ellipse.center.lon,
                                                eSending.ellipse.center.lat,
                                                eSending.ellipse.center.lon + eSending.ellipse.radius.lon,
                                                eSending.ellipse.center.lat + eSending.ellipse.radius.lat,
                                                polyOut.GetLength(0),
                                                360,
                                                ref polyOut);
                String polyString = String.Empty;
                int iCount = -1;
                while (iCount < polyOut.GetLength(0))
                {
                    int id = ++iCount % polyOut.GetLength(0);
                    polyString += String.Format(UCommon.UGlobalizationInfo, "{0},{1} ", Math.Round(polyOut[id,1], 4), Math.Round(polyOut[id,0], 4));
                }
                hashPolygon.Add(polyString);
                setAlertInfoArea.polygonField = hashPolygon.ToArray();
            }
            else if (mapSending.GetType().Equals(typeof(UTASSENDING)))
            {
                UTASSENDING tas = (UTASSENDING)mapSending;
                String szCountry = String.Empty;
                foreach (ULBACOUNTRY c in tas.countrylist)
                {
                    szCountry += c.sz_name + ",";
                }
                setAlertInfoArea.areaDescField = String.Format("{0} {1}", "Tourist Alert to ", szCountry);
            }
            else if (mapSending.GetType().Equals(typeof(UGISSENDING)))
            {
                UGISSENDING gis = (UGISSENDING)mapSending;
                setAlertInfoArea.areaDescField = "GIS Import";
            }
            else if (mapSending.GetType().Equals(typeof(UMUNICIPALSENDING)))
            {
                UMUNICIPALSENDING mun = (UMUNICIPALSENDING)mapSending;
                setAlertInfoArea.areaDescField = "Municipal";
                HashSet<alertInfoAreaGeocode> codes = new HashSet<alertInfoAreaGeocode>();
                foreach (UMunicipalDef m in mun.municipals)
                {
                    alertInfoAreaGeocode geocode = new alertInfoAreaGeocode();
                    geocode.valueField = m.sz_municipalid;
                    geocode.valueNameField = "MUNICIPALID";
                    codes.Add(geocode);
                }
                setAlertInfoArea.geocodeField = codes.ToArray();
            }


            setAlertInfo.categoryField = mapSending.capFields.category;
            alertInfoEventCode eventCode = new alertInfoEventCode();
            HashSet<alertInfoEventCode> eventCodes = new HashSet<alertInfoEventCode>();
            //addCustomEvent(ref eventCodes, "SYSTEM.SPESIFIKKE.VARIABLER", "Her kan vi legge inn mer info om selve sendingen");
            addCustomEvent(ref eventCodes, "PAS.SMS.SENDTO", b_sms_active);
            addCustomEvent(ref eventCodes, "PAS.VOICE.SENDTO", b_voice_active);
            addCustomEvent(ref eventCodes, "PAS.LBA.SENDTO", b_lba_active);
            addCustomEvent(ref eventCodes, "PAS.TAS.SENDTO", b_tas_active);
            addCustomEvent(ref eventCodes, "PAS.SCHEDULED_DATE_TIME", DateTime.UtcNow);
            ADRTYPES types = (ADRTYPES)mapSending.n_addresstypes;
            addCustomEvent(ref eventCodes, "PAS.ADDRESSTYPES", types);

            if (b_voice_active)
            {
                addCustomEvent(ref eventCodes, "PAS.VOICE.VALIDITY_DAYS", mapSending.n_validity);
                addCustomEvent(ref eventCodes, "PAS.VOICE.ORIGIN_CALLER", mapSending.oadc.sz_number);
            }
            if (b_sms_active)
            {
                addCustomEvent(ref eventCodes, "PAS.SMS.ORIGIN_CALLER", mapSending.sz_sms_oadc);
                addCustomEvent(ref eventCodes, "PAS.SMS.MESSAGE_CONTENT", mapSending.sz_sms_message);
            }
            if (b_voice_active && mapSending.n_dynvoc>0)
            {
                HashSet<alertInfoResource> hashResources = new HashSet<alertInfoResource>();
                for(int i=0; i < mapSending.n_dynvoc; i++)
                {
                    String wavUri = String.Format("{0}audiofiles/v{1}_{2}.wav", UCommon.UPATHS.sz_url_vb, mapSending.n_refno, i + 1);
                    addResource(ref hashResources, String.Format("Audio file #{0}", i + 1), "audio/x-wav", wavUri, 0);
                }
                setAlertInfo.resourceField = hashResources.ToArray();
            }

            setAlertInfo.eventCodeField = eventCodes.ToArray();

            setAlertInfo.eventField = GetProjectName(mapSending.n_projectpk); //"EventNavn - kan bruke PAS Eventnavn her?";

            setAlertInfo.urgencyField = mapSending.capFields.urgency; //alertInfoUrgency.Immediate; //MÅ SETTES MANUELT

            setAlertInfo.certaintyField = mapSending.capFields.certainty; // alertInfoCertainty.Observed; //MÅ SETTES MANUELT

            setAlertInfo.severityField = mapSending.capFields.severity; // alertInfoSeverity.Moderate; //MÅ SETTES MANUELT

            setAlertInfo.senderNameField = GetCompanyName(mapSending.logoninfo.l_comppk); //String.Format("{0}", "F.eks. bruke company-name her, skal være human readable avsendernavn");

            setAlertInfo.headlineField = mapSending.capFields.headline; //"Kortfattet overskrift (f.eks. til SMS, max 160 tegn). Kopi av SMS-melding hvis SMS er satt som kanal?";

            setAlertInfo.descriptionField = mapSending.capFields.description; //"Beskrivelse av hendelsen, kan f.eks. bruke description i PARM til dette. Vi har ikke støtte for description i ad-hoc-sendinger";

            setAlertInfo.instructionField = mapSending.capFields.instruction; //"Råd til mottakere av alert. Dette må eventuelt være nytt tekstfelt i PAS-sendegrensesnitt";

            setAlertInfo.webField = mapSending.capFields.web; //"Hvis mer info om hendelsen via WEB. Kan være referanse til nettavis/UD e.l. Må eventuelt inn i PAS-sendegrensesnitt";

            setAlertInfo.contactField = mapSending.capFields.contact; //"Kontaktinfo for oppfølgning - nytt felt PAS-sendegrensesnitt, tror ikke vi skal bruke avsender for sms/voice her";

            setAlertInfo.languageField = mapSending.capFields.language; //"no-NO";


            setAlertInfo.responseTypeField = mapSending.capFields.responseType;

            setAlertInfoArea.altitudeField = 0;
            setAlertInfoArea.altitudeFieldSpecified = false;
            setAlertInfoArea.ceilingField = 100;
            setAlertInfoArea.ceilingFieldSpecified = false;

            setAlertInfo.areaField = new alertInfoArea[1];
            setAlertInfo.areaField[0] = setAlertInfoArea; //set in struct
            capAlert.infoField = new alertInfo[1];
            capAlert.infoField[0] = setAlertInfo; //set in struct

            /*HashSet<alertInfoParameter> infoParams = new HashSet<alertInfoParameter>();
            addCustomParameter(ref infoParams, "TILLEGGSPARAMETRE", "Kan legge inn diverse ekstra info i tillegg til Event Parametre");

            setAlertInfo.parameterField = infoParams.ToArray();

            HashSet<alertInfoResource> infoResources = new HashSet<alertInfoResource>();
            alertInfoResource resource = new alertInfoResource();
            resource.resourceDescField = "Kartbilde av varslet område";
            resource.mimeTypeField = "image/png";
            resource.derefUriField = "";
            resource.sizeField = "20000";
            resource.uriField = String.Format("https://secure.ums.no/pas/alert_images/{0}.png", mapSending.n_refno);
            resource.digestField = "";
            infoResources.Add(resource);
            resource = new alertInfoResource();
            resource.resourceDescField = "Lydfil fra PAS";
            resource.mimeTypeField = "audio/x-wav";
            resource.derefUriField = "";
            resource.sizeField = "10000";
            resource.uriField = String.Format("https://secure.ums.no/pas/audio/{0}_1.wav", mapSending.n_refno);
            resource.digestField = "";
            infoResources.Add(resource);

            setAlertInfo.resourceField = infoResources.ToArray();*/

            setAlertInfo.effectiveField = DateTime.Now;
            setAlertInfo.onsetField = DateTime.Now;
            setAlertInfo.expiresField = DateTime.Now + new TimeSpan(24, 0, 0);
            setAlertInfo.audienceField = mapSending.capFields.audience; //"Spesifiser hvis alert er ment for et spesifikt publikum. F.eks. asmatikere/bevegelseshemmede? Eventuelt nytt PAS-sendefelt";
            return capAlert;
        }

        private void addResource(ref HashSet<alertInfoResource> coll, 
                                String desc, 
                                String mimeType,
                                String uri,
                                int size)
        {
            alertInfoResource resource = new alertInfoResource();
            resource.mimeTypeField = mimeType;
            resource.sizeField = size.ToString();
            resource.uriField = uri;
            resource.resourceDescField = desc;
            coll.Add(resource);
        }

        private void addCustomParameter(ref HashSet<alertInfoParameter> coll, String paramName, object value)
        {
            alertInfoParameter param = new alertInfoParameter();
            param.valueNameField = paramName;
            param.valueField = value.ToString();
            coll.Add(param);
        }

        private void addCustomEvent(ref HashSet<alertInfoEventCode> coll, String paramName, object value)
        {
            alertInfoEventCode param = new alertInfoEventCode();
            param.valueNameField = paramName;
            param.valueField = value.ToString();
            coll.Add(param);
        }
    }

}
