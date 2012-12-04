package no.ums.pas.send;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import no.ums.pas.PAS;
import no.ums.pas.PasApplication;
import no.ums.pas.ums.tools.Utils;
import no.ums.ws.common.UDATAFILTER;
import no.ums.ws.common.ULOGONINFO;
import no.ums.ws.common.parm.UPASLOGON;
import no.ums.ws.pas.ArrayOfUGisImportLine;
import no.ums.ws.pas.ArrayOfUGisImportResultLine;
import no.ums.ws.pas.UGisImportLine;
import no.ums.ws.pas.UGisImportList;
import no.ums.ws.pas.UGisImportParamsByStreetId;
import no.ums.ws.pas.UGisImportResultLine;
import no.ums.ws.pas.UGisImportResultsByStreetId;

public class GisImport {

	public static void main(String[] args) {
		PasApplication pasApp = PasApplication.init("http://localhost:8080/WS/");
		
		
		try {
			ULOGONINFO logoninfo = new ULOGONINFO();
			logoninfo.setOnetimekey(pasApp.getPaswsSoap().getOneTimeKey());
			logoninfo.setSzCompid("ums");
			logoninfo.setSzUserid("rwtest");
			logoninfo.setSzPassword(Utils.encrypt("wilhelmsen123"));
			UPASLOGON logon = pasApp.getPaswsSoap().pasLogon(logoninfo);
			logoninfo.setSessionid(logon.getSessionid());
			logoninfo.setLDeptpk(logon.getDepartments().getUDEPARTMENT().get(0).getLDeptpk());
			logoninfo.setLUserpk(logon.getLUserpk());
			logoninfo.setLComppk(logon.getLComppk());
			logoninfo.setSzStdcc("0047");
			
			UGisImportParamsByStreetId search = new UGisImportParamsByStreetId();
			search.setCOLHOUSENO(2);
			search.setCOLLETTER(3);
			search.setCOLMUNICIPAL(0);
			search.setCOLNAMEFILTER1(4);
			search.setCOLNAMEFILTER2(5);
			search.setCOLSTREETID(1);
			search.setSEPARATOR("\t");
			search.setSKIPLINES(1);
			search.setDETAILTHRESHOLDLINES(500);
			byte [] bytes = no.ums.pas.ums.tools.IO.ConvertInputStreamtoByteArray(new FileInputStream("C:\\UMS Population Alert System\\gis\\adr test1_name filter.txt"));
			search.setFILE(bytes);
			
			final FileInputStream fis = new FileInputStream("C:\\UMS Population Alert System\\gis\\adr test1_name filter.txt");
			final Reader rdr = new InputStreamReader(fis, "UTF-8");
			final StringBuffer content = new StringBuffer();
			char[] buffer = new char[4096];
			int read = 0;
			while ((read = rdr.read(buffer)) > 0) {
				content.append(buffer, 0, read);
			}
			
			
			UGisImportList search2 = new UGisImportList();
			ArrayOfUGisImportLine importlines = new ArrayOfUGisImportLine();
			search2.setList(importlines);
			search2.setDETAILTHRESHOLDLINES(500);
			search2.setSKIPLINES(1);
			final BufferedReader br = new BufferedReader(new StringReader(content.toString()));
			String temp;
			int line=0;
			UGisImportLine resline;
			while((temp = br.readLine()) != null) {
				if(line==0 && search.getSKIPLINES() == 1) {
					// isje jørr någe
				}
				else {
					resline = new UGisImportLine();
					String[] ting = temp.split(search.getSEPARATOR());
					resline.setMunicipalid(ting[0]); // getC
					resline.setStreetid(ting[1]);
					resline.setHouseno(ting[2]);
					resline.setLetter(ting[3]);
					resline.setNamefilter1(ting.length>=5?ting[4]:"");
					resline.setNamefilter2(ting.length>=6?ting[5]:"");
					importlines.getUGisImportLine().add(resline);
				}
				line++;
			}			
			
			UGisImportResultsByStreetId res = pasApp.getPaswsSoap().getGisByStreetIdV2(logoninfo, search2);
			System.out.println(res.getList().getUGisImportResultLine().size());
		}
		catch(Exception e) {
			
		}
		finally {
			pasApp.shutdown();
		}
	}
}
