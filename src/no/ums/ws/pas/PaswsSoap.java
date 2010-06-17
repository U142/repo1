
package no.ums.ws.pas;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebService(name = "paswsSoap", targetNamespace = "http://ums.no/ws/pas/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface PaswsSoap {


    /**
     * 
     * @return
     *     returns no.ums.ws.pas.Imports
     */
    @WebMethod(operationName = "_no_use", action = "http://ums.no/ws/pas/_no_use")
    @WebResult(name = "_no_useResult", targetNamespace = "http://ums.no/ws/pas/")
    @RequestWrapper(localName = "_no_use", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.NoUse")
    @ResponseWrapper(localName = "_no_useResponse", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.NoUseResponse")
    public Imports noUse();

    /**
     * 
     * @param l
     * @return
     *     returns no.ums.ws.pas.UPASLOGON
     */
    @WebMethod(operationName = "PasLogon", action = "http://ums.no/ws/pas/PasLogon")
    @WebResult(name = "PasLogonResult", targetNamespace = "http://ums.no/ws/pas/")
    @RequestWrapper(localName = "PasLogon", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.PasLogon")
    @ResponseWrapper(localName = "PasLogonResponse", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.PasLogonResponse")
    public UPASLOGON pasLogon(
        @WebParam(name = "l", targetNamespace = "http://ums.no/ws/pas/")
        ULOGONINFO l);

    /**
     * 
     * @param l
     * @return
     *     returns boolean
     */
    @WebMethod(operationName = "PasLogoff", action = "http://ums.no/ws/pas/PasLogoff")
    @WebResult(name = "PasLogoffResult", targetNamespace = "http://ums.no/ws/pas/")
    @RequestWrapper(localName = "PasLogoff", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.PasLogoff")
    @ResponseWrapper(localName = "PasLogoffResponse", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.PasLogoffResponse")
    public boolean pasLogoff(
        @WebParam(name = "l", targetNamespace = "http://ums.no/ws/pas/")
        ULOGONINFO l);

    /**
     * 
     * @param ui
     * @param l
     * @return
     *     returns boolean
     */
    @WebMethod(operationName = "SavePasUiSettings", action = "http://ums.no/ws/pas/SavePasUiSettings")
    @WebResult(name = "SavePasUiSettingsResult", targetNamespace = "http://ums.no/ws/pas/")
    @RequestWrapper(localName = "SavePasUiSettings", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.SavePasUiSettings")
    @ResponseWrapper(localName = "SavePasUiSettingsResponse", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.SavePasUiSettingsResponse")
    public boolean savePasUiSettings(
        @WebParam(name = "l", targetNamespace = "http://ums.no/ws/pas/")
        ULOGONINFO l,
        @WebParam(name = "ui", targetNamespace = "http://ums.no/ws/pas/")
        UPASUISETTINGS ui);

    /**
     * 
     * @param l
     * @return
     *     returns no.ums.ws.pas.UPASUISETTINGS
     */
    @WebMethod(operationName = "LoadLanguageAndVisuals", action = "http://ums.no/ws/pas/LoadLanguageAndVisuals")
    @WebResult(name = "LoadLanguageAndVisualsResult", targetNamespace = "http://ums.no/ws/pas/")
    @RequestWrapper(localName = "LoadLanguageAndVisuals", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.LoadLanguageAndVisuals")
    @ResponseWrapper(localName = "LoadLanguageAndVisualsResponse", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.LoadLanguageAndVisualsResponse")
    public UPASUISETTINGS loadLanguageAndVisuals(
        @WebParam(name = "l", targetNamespace = "http://ums.no/ws/pas/")
        ULOGONINFO l);

    /**
     * 
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "Powerup", action = "http://ums.no/ws/pas/Powerup")
    @WebResult(name = "PowerupResult", targetNamespace = "http://ums.no/ws/pas/")
    @RequestWrapper(localName = "Powerup", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.Powerup")
    @ResponseWrapper(localName = "PowerupResponse", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.PowerupResponse")
    public String powerup();

    /**
     * 
     * @param request
     * @return
     *     returns no.ums.ws.pas.UPASMap
     */
    @WebMethod(operationName = "GetMap", action = "http://ums.no/ws/pas/GetMap")
    @WebResult(name = "GetMapResult", targetNamespace = "http://ums.no/ws/pas/")
    @RequestWrapper(localName = "GetMap", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.GetMap")
    @ResponseWrapper(localName = "GetMapResponse", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.GetMapResponse")
    public UPASMap getMap(
        @WebParam(name = "request", targetNamespace = "http://ums.no/ws/pas/")
        UMapInfo request);

    /**
     * 
     * @param param
     * @param logon
     * @return
     *     returns no.ums.ws.pas.UAddressList
     */
    @WebMethod(operationName = "GetAddressListByQuality", action = "http://ums.no/ws/pas/GetAddressListByQuality")
    @WebResult(name = "GetAddressListByQualityResult", targetNamespace = "http://ums.no/ws/pas/")
    @RequestWrapper(localName = "GetAddressListByQuality", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.GetAddressListByQuality")
    @ResponseWrapper(localName = "GetAddressListByQualityResponse", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.GetAddressListByQualityResponse")
    public UAddressList getAddressListByQuality(
        @WebParam(name = "logon", targetNamespace = "http://ums.no/ws/pas/")
        ULOGONINFO logon,
        @WebParam(name = "param", targetNamespace = "http://ums.no/ws/pas/")
        UMapAddressParamsByQuality param);

    /**
     * 
     * @param search
     * @param logon
     * @return
     *     returns no.ums.ws.pas.UGisImportResultsByStreetId
     */
    @WebMethod(operationName = "GetGisByStreetId", action = "http://ums.no/ws/pas/GetGisByStreetId")
    @WebResult(name = "GetGisByStreetIdResult", targetNamespace = "http://ums.no/ws/pas/")
    @RequestWrapper(localName = "GetGisByStreetId", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.GetGisByStreetId")
    @ResponseWrapper(localName = "GetGisByStreetIdResponse", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.GetGisByStreetIdResponse")
    public UGisImportResultsByStreetId getGisByStreetId(
        @WebParam(name = "logon", targetNamespace = "http://ums.no/ws/pas/")
        ULOGONINFO logon,
        @WebParam(name = "search", targetNamespace = "http://ums.no/ws/pas/")
        UGisImportParamsByStreetId search);

    /**
     * 
     * @param jobType
     * @param l
     * @return
     *     returns no.ums.ws.pas.PercentResult
     */
    @WebMethod(operationName = "GetProgress", action = "http://ums.no/ws/pas/GetProgress")
    @WebResult(name = "GetProgressResult", targetNamespace = "http://ums.no/ws/pas/")
    @RequestWrapper(localName = "GetProgress", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.GetProgress")
    @ResponseWrapper(localName = "GetProgressResponse", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.GetProgressResponse")
    public PercentResult getProgress(
        @WebParam(name = "l", targetNamespace = "http://ums.no/ws/pas/")
        ULOGONINFO l,
        @WebParam(name = "jobType", targetNamespace = "http://ums.no/ws/pas/")
        ProgressJobType jobType);

    /**
     * 
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "GetCurrentJobs", action = "http://ums.no/ws/pas/GetCurrentJobs")
    @WebResult(name = "GetCurrentJobsResult", targetNamespace = "http://ums.no/ws/pas/")
    @RequestWrapper(localName = "GetCurrentJobs", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.GetCurrentJobs")
    @ResponseWrapper(localName = "GetCurrentJobsResponse", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.GetCurrentJobsResponse")
    public String getCurrentJobs();

    /**
     * 
     * @param l
     * @return
     *     returns byte[]
     */
    @WebMethod(operationName = "GetSendSettings", action = "http://ums.no/ws/pas/GetSendSettings")
    @WebResult(name = "GetSendSettingsResult", targetNamespace = "http://ums.no/ws/pas/")
    @RequestWrapper(localName = "GetSendSettings", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.GetSendSettings")
    @ResponseWrapper(localName = "GetSendSettingsResponse", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.GetSendSettingsResponse")
    public byte[] getSendSettings(
        @WebParam(name = "l", targetNamespace = "http://ums.no/ws/pas/")
        ULOGONINFO l);

    /**
     * 
     * @param request
     * @return
     *     returns no.ums.ws.pas.UPASMap
     */
    @WebMethod(operationName = "GetMapOverlay", action = "http://ums.no/ws/pas/GetMapOverlay")
    @WebResult(name = "GetMapOverlayResult", targetNamespace = "http://ums.no/ws/pas/")
    @RequestWrapper(localName = "GetMapOverlay", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.GetMapOverlay")
    @ResponseWrapper(localName = "GetMapOverlayResponse", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.GetMapOverlayResponse")
    public UPASMap getMapOverlay(
        @WebParam(name = "request", targetNamespace = "http://ums.no/ws/pas/")
        UMapInfoLayerCellVision request);

    /**
     * 
     * @param logoninfo
     * @return
     *     returns long
     */
    @WebMethod(operationName = "GetRefno", action = "http://ums.no/ws/pas/GetRefno")
    @WebResult(name = "GetRefnoResult", targetNamespace = "http://ums.no/ws/pas/")
    @RequestWrapper(localName = "GetRefno", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.GetRefno")
    @ResponseWrapper(localName = "GetRefnoResponse", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.GetRefnoResponse")
    public long getRefno(
        @WebParam(name = "logoninfo", targetNamespace = "http://ums.no/ws/pas/")
        ULOGONINFO logoninfo);

    /**
     * 
     * @param logoninfo
     * @param maxalloc
     * @return
     *     returns no.ums.ws.pas.UMAXALLOC
     */
    @WebMethod(operationName = "SetMaxAlloc", action = "http://ums.no/ws/pas/SetMaxAlloc")
    @WebResult(name = "SetMaxAllocResult", targetNamespace = "http://ums.no/ws/pas/")
    @RequestWrapper(localName = "SetMaxAlloc", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.SetMaxAlloc")
    @ResponseWrapper(localName = "SetMaxAllocResponse", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.SetMaxAllocResponse")
    public UMAXALLOC setMaxAlloc(
        @WebParam(name = "logoninfo", targetNamespace = "http://ums.no/ws/pas/")
        ULOGONINFO logoninfo,
        @WebParam(name = "maxalloc", targetNamespace = "http://ums.no/ws/pas/")
        UMAXALLOC maxalloc);

    /**
     * 
     * @param operation
     * @param adr
     * @param logoninfo
     * @return
     *     returns no.ums.ws.pas.UAddress
     */
    @WebMethod(operationName = "HouseEditor", action = "http://ums.no/ws/pas/HouseEditor")
    @WebResult(name = "HouseEditorResult", targetNamespace = "http://ums.no/ws/pas/")
    @RequestWrapper(localName = "HouseEditor", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.HouseEditor")
    @ResponseWrapper(localName = "HouseEditorResponse", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.HouseEditorResponse")
    public UAddress houseEditor(
        @WebParam(name = "adr", targetNamespace = "http://ums.no/ws/pas/")
        UAddress adr,
        @WebParam(name = "logoninfo", targetNamespace = "http://ums.no/ws/pas/")
        ULOGONINFO logoninfo,
        @WebParam(name = "operation", targetNamespace = "http://ums.no/ws/pas/")
        HOUSEEDITOROPERATION operation);

    /**
     * 
     * @param searchparams
     * @param logoninfo
     * @return
     *     returns no.ums.ws.pas.UAddressList
     */
    @WebMethod(operationName = "GetAddressList", action = "http://ums.no/ws/pas/GetAddressList")
    @WebResult(name = "GetAddressListResult", targetNamespace = "http://ums.no/ws/pas/")
    @RequestWrapper(localName = "GetAddressList", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.GetAddressList")
    @ResponseWrapper(localName = "GetAddressListResponse", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.GetAddressListResponse")
    public UAddressList getAddressList(
        @WebParam(name = "searchparams", targetNamespace = "http://ums.no/ws/pas/")
        UMapAddressParams searchparams,
        @WebParam(name = "logoninfo", targetNamespace = "http://ums.no/ws/pas/")
        ULOGONINFO logoninfo);

    /**
     * 
     * @param p
     * @param logon
     * @return
     *     returns no.ums.ws.pas.UGabResultFromPoint
     */
    @WebMethod(operationName = "GetNearestGABFromPoint", action = "http://ums.no/ws/pas/GetNearestGABFromPoint")
    @WebResult(name = "GetNearestGABFromPointResult", targetNamespace = "http://ums.no/ws/pas/")
    @RequestWrapper(localName = "GetNearestGABFromPoint", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.GetNearestGABFromPoint")
    @ResponseWrapper(localName = "GetNearestGABFromPointResponse", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.GetNearestGABFromPointResponse")
    public UGabResultFromPoint getNearestGABFromPoint(
        @WebParam(name = "logon", targetNamespace = "http://ums.no/ws/pas/")
        ULOGONINFO logon,
        @WebParam(name = "p", targetNamespace = "http://ums.no/ws/pas/")
        UMapPoint p);

    /**
     * 
     * @param logoninfo
     * @param param
     * @return
     *     returns no.ums.ws.pas.UAddressList
     */
    @WebMethod(operationName = "GetNearestInhabitantsFromPoint", action = "http://ums.no/ws/pas/GetNearestInhabitantsFromPoint")
    @WebResult(name = "GetNearestInhabitantsFromPointResult", targetNamespace = "http://ums.no/ws/pas/")
    @RequestWrapper(localName = "GetNearestInhabitantsFromPoint", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.GetNearestInhabitantsFromPoint")
    @ResponseWrapper(localName = "GetNearestInhabitantsFromPointResponse", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.GetNearestInhabitantsFromPointResponse")
    public UAddressList getNearestInhabitantsFromPoint(
        @WebParam(name = "param", targetNamespace = "http://ums.no/ws/pas/")
        UMapDistanceParams param,
        @WebParam(name = "logoninfo", targetNamespace = "http://ums.no/ws/pas/")
        ULOGONINFO logoninfo);

    /**
     * 
     * @param searchparams
     * @param logoninfo
     * @return
     *     returns no.ums.ws.pas.UGabSearchResultList
     */
    @WebMethod(operationName = "GabSearch", action = "http://ums.no/ws/pas/GabSearch")
    @WebResult(name = "GabSearchResult", targetNamespace = "http://ums.no/ws/pas/")
    @RequestWrapper(localName = "GabSearch", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.GabSearch")
    @ResponseWrapper(localName = "GabSearchResponse", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.GabSearchResponse")
    public UGabSearchResultList gabSearch(
        @WebParam(name = "searchparams", targetNamespace = "http://ums.no/ws/pas/")
        UGabSearchParams searchparams,
        @WebParam(name = "logoninfo", targetNamespace = "http://ums.no/ws/pas/")
        ULOGONINFO logoninfo);

    /**
     * 
     * @param logon
     * @param req
     * @return
     *     returns no.ums.ws.pas.UCONVERTTTSRESPONSE
     */
    @WebMethod(operationName = "ConvertTTS", action = "http://ums.no/ws/pas/ConvertTTS")
    @WebResult(name = "ConvertTTSResult", targetNamespace = "http://ums.no/ws/pas/")
    @RequestWrapper(localName = "ConvertTTS", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.ConvertTTS")
    @ResponseWrapper(localName = "ConvertTTSResponse", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.ConvertTTSResponse")
    public UCONVERTTTSRESPONSE convertTTS(
        @WebParam(name = "logon", targetNamespace = "http://ums.no/ws/pas/")
        ULOGONINFO logon,
        @WebParam(name = "req", targetNamespace = "http://ums.no/ws/pas/")
        UCONVERTTTSREQUEST req);

    /**
     * 
     * @param logon
     * @param req
     * @return
     *     returns no.ums.ws.pas.AUDIORESPONSE
     */
    @WebMethod(operationName = "UPostAudio", action = "http://ums.no/ws/pas/UPostAudio")
    @WebResult(name = "UPostAudioResult", targetNamespace = "http://ums.no/ws/pas/")
    @RequestWrapper(localName = "UPostAudio", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.UPostAudio")
    @ResponseWrapper(localName = "UPostAudioResponse", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.UPostAudioResponse")
    public AUDIORESPONSE uPostAudio(
        @WebParam(name = "logon", targetNamespace = "http://ums.no/ws/pas/")
        ULOGONINFO logon,
        @WebParam(name = "req", targetNamespace = "http://ums.no/ws/pas/")
        AUDIOREQUEST req);

    /**
     * 
     * @param logon
     * @param req
     * @return
     *     returns no.ums.ws.pas.UPROJECTRESPONSE
     */
    @WebMethod(operationName = "UCreateProject", action = "http://ums.no/ws/pas/UCreateProject")
    @WebResult(name = "UCreateProjectResult", targetNamespace = "http://ums.no/ws/pas/")
    @RequestWrapper(localName = "UCreateProject", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.UCreateProject")
    @ResponseWrapper(localName = "UCreateProjectResponse", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.UCreateProjectResponse")
    public UPROJECTRESPONSE uCreateProject(
        @WebParam(name = "logon", targetNamespace = "http://ums.no/ws/pas/")
        ULOGONINFO logon,
        @WebParam(name = "req", targetNamespace = "http://ums.no/ws/pas/")
        UPROJECTREQUEST req);

    /**
     * 
     * @return
     *     returns no.ums.ws.pas.REFNORESPONSE
     */
    @WebMethod(operationName = "URefno", action = "http://ums.no/ws/pas/URefno")
    @WebResult(name = "URefnoResult", targetNamespace = "http://ums.no/ws/pas/")
    @RequestWrapper(localName = "URefno", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.URefno")
    @ResponseWrapper(localName = "URefnoResponse", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.URefnoResponse")
    public REFNORESPONSE uRefno();

    /**
     * 
     * @param s
     * @param l
     * @return
     *     returns no.ums.ws.pas.UWeatherReportResults
     */
    @WebMethod(operationName = "GetWeatherReport", action = "http://ums.no/ws/pas/GetWeatherReport")
    @WebResult(name = "GetWeatherReportResult", targetNamespace = "http://ums.no/ws/pas/")
    @RequestWrapper(localName = "GetWeatherReport", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.GetWeatherReport")
    @ResponseWrapper(localName = "GetWeatherReportResponse", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.GetWeatherReportResponse")
    public UWeatherReportResults getWeatherReport(
        @WebParam(name = "l", targetNamespace = "http://ums.no/ws/pas/")
        ULOGONINFO l,
        @WebParam(name = "s", targetNamespace = "http://ums.no/ws/pas/")
        UWeatherSearch s);

    /**
     * 
     * @param logon
     * @param msg
     * @return
     *     returns no.ums.ws.pas.UBBMESSAGE
     */
    @WebMethod(operationName = "InsertMessageLibrary", action = "http://ums.no/ws/pas/InsertMessageLibrary")
    @WebResult(name = "InsertMessageLibraryResult", targetNamespace = "http://ums.no/ws/pas/")
    @RequestWrapper(localName = "InsertMessageLibrary", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.InsertMessageLibrary")
    @ResponseWrapper(localName = "InsertMessageLibraryResponse", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.InsertMessageLibraryResponse")
    public UBBMESSAGE insertMessageLibrary(
        @WebParam(name = "logon", targetNamespace = "http://ums.no/ws/pas/")
        ULOGONINFO logon,
        @WebParam(name = "msg", targetNamespace = "http://ums.no/ws/pas/")
        UBBMESSAGE msg);

    /**
     * 
     * @param logon
     * @param msg
     * @return
     *     returns no.ums.ws.pas.UBBMESSAGE
     */
    @WebMethod(operationName = "DeleteMessageLibrary", action = "http://ums.no/ws/pas/DeleteMessageLibrary")
    @WebResult(name = "DeleteMessageLibraryResult", targetNamespace = "http://ums.no/ws/pas/")
    @RequestWrapper(localName = "DeleteMessageLibrary", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.DeleteMessageLibrary")
    @ResponseWrapper(localName = "DeleteMessageLibraryResponse", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.DeleteMessageLibraryResponse")
    public UBBMESSAGE deleteMessageLibrary(
        @WebParam(name = "logon", targetNamespace = "http://ums.no/ws/pas/")
        ULOGONINFO logon,
        @WebParam(name = "msg", targetNamespace = "http://ums.no/ws/pas/")
        UBBMESSAGE msg);

    /**
     * 
     * @param logon
     * @param filter
     * @return
     *     returns no.ums.ws.pas.UBBMESSAGELIST
     */
    @WebMethod(operationName = "GetMessageLibrary", action = "http://ums.no/ws/pas/GetMessageLibrary")
    @WebResult(name = "GetMessageLibraryResult", targetNamespace = "http://ums.no/ws/pas/")
    @RequestWrapper(localName = "GetMessageLibrary", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.GetMessageLibrary")
    @ResponseWrapper(localName = "GetMessageLibraryResponse", targetNamespace = "http://ums.no/ws/pas/", className = "no.ums.ws.pas.GetMessageLibraryResponse")
    public UBBMESSAGELIST getMessageLibrary(
        @WebParam(name = "logon", targetNamespace = "http://ums.no/ws/pas/")
        ULOGONINFO logon,
        @WebParam(name = "filter", targetNamespace = "http://ums.no/ws/pas/")
        UBBMESSAGELISTFILTER filter);

}
