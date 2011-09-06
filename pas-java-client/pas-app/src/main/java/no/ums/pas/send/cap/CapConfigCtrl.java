package no.ums.pas.send.cap;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import org.joda.time.DateTime;

import no.ums.pas.send.cap.CapConfigView.IController;
import no.ums.ws.common.parm.AlertInfoCategory;
import no.ums.ws.common.parm.AlertInfoResponseType;
import no.ums.ws.common.parm.AlertInfoSeverity;
import no.ums.ws.common.parm.AlertInfoUrgency;
import no.ums.ws.common.parm.AlertScope;
import no.ums.ws.common.parm.AlertStatus;
import no.ums.ws.common.parm.ArrayOfAlertInfoCategory;
import no.ums.ws.common.parm.ArrayOfAlertInfoResponseType;
import no.ums.ws.common.parm.ArrayOfString;
import no.ums.ws.common.parm.UMapSendingCapFields;


public class CapConfigCtrl implements IController {

	public void initialize(final CapConfigModel model)
	{
		model.addPropertyChangeListener("dasiojasi", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if(model.restriction.equals("fosdjk"))
				{
					
				}
			}
		});
	}

	@Override
	public UMapSendingCapFields onOk(CapConfigModel model) {
		UMapSendingCapFields ret = new UMapSendingCapFields();
		ret.setAudience(model.getAudience());
		ret.setCategory(new ArrayOfAlertInfoCategory());
		if(model.getCategory()!=null)
			ret.getCategory().getAlertInfoCategory().addAll(model.getCategory());
		ret.setCertainty(model.getCertainty());
		ret.setCode(new ArrayOfString());
		if(model.getCode()!=null)
			ret.getCode().getString().addAll(model.getCodes());
		ret.setContact(model.getContact());
		ret.setDescription(model.getDescription());
		
		ret.setHeadline(model.getHeadline());
		ret.setInstruction(model.getInstruction());
		ret.setLanguage(model.getLanguage());
		ret.setNote(model.getNote());
		ret.setResponseType(new ArrayOfAlertInfoResponseType());
		if(model.getResponseType()!=null)
			ret.getResponseType().getAlertInfoResponseType().addAll(model.getResponseType());
		ret.setRestriction(model.getRestriction());
		ret.setScope(model.getScope());
		ret.setSeverity(model.getSeverity());
		ret.setSource(model.getSource());
		ret.setStatus(model.getStatus());
		ret.setUrgency(model.getUrgency());
		ret.setWeb(model.getWeb());
		
		return ret;
	}

	@Override
	public void updateModel(UMapSendingCapFields cap,
			CapConfigModel capConfigModel) {
		if(cap==null)
			return;
		capConfigModel.setAudience(cap.getAudience());
		capConfigModel.setCategory(cap.getCategory()!=null ? cap.getCategory().getAlertInfoCategory() : capConfigModel.getCategory());
		capConfigModel.setCertainty(cap.getCertainty());
		capConfigModel.setCodes(cap.getCode()!=null ? cap.getCode().getString() : new ArrayList<String>());
		capConfigModel.setContact(cap.getContact());
		capConfigModel.setDescription(cap.getDescription());
		capConfigModel.setHeadline(cap.getHeadline());
		capConfigModel.setInstruction(cap.getInstruction());
		capConfigModel.setLanguage(cap.getLanguage());
		capConfigModel.setNote(cap.getNote());
		capConfigModel.setResponseType(cap.getResponseType()!=null ? cap.getResponseType().getAlertInfoResponseType() : new ArrayList<AlertInfoResponseType>());
		capConfigModel.setRestriction(cap.getRestriction());
		capConfigModel.setScope(cap.getScope()!=null ? cap.getScope() : AlertScope.PUBLIC);
		capConfigModel.setSeverity(cap.getSeverity()!=null ? cap.getSeverity() : AlertInfoSeverity.UNKNOWN);
		capConfigModel.setSource(cap.getSource());
		capConfigModel.setStatus(cap.getStatus()!=null ? cap.getStatus() : AlertStatus.ACTUAL);
		capConfigModel.setUrgency(cap.getUrgency()!=null ? cap.getUrgency() : AlertInfoUrgency.IMMEDIATE);
		capConfigModel.setWeb(cap.getWeb());
	}

	@Override
	public void addCode(CapConfigModel capConfigModel) {
		//read code from model and add to list
		String code = capConfigModel.getCode();
		
		//check if already in list, if so, select the item and reset code-text
		if(capConfigModel.getCodes().contains(code))
		{
			capConfigModel.setSelectedCodes(Arrays.asList(code));
		}
		else
		{
			List<String> codes = new ArrayList<String>(capConfigModel.getCodes());
			codes.add(code);
			capConfigModel.setCodes(codes);
		}
		capConfigModel.setCode(""); //reset code-text
		checkForBtnEnable(capConfigModel);
	}

	@Override
	public void removeSelectedCodes(CapConfigModel capConfigModel) {
		if(capConfigModel.getSelectedCodes()!=null)
		{
			List<String> codes = new ArrayList<String>(capConfigModel.getCodes());
			codes.removeAll(capConfigModel.getSelectedCodes());
			capConfigModel.setCodes(codes);
		}
		capConfigModel.setSelectedCodes(new ArrayList<String>());
		checkForBtnEnable(capConfigModel);
	}

	@Override
	public void removeAllCodes(CapConfigModel capConfigModel) {
		capConfigModel.setCodes(new ArrayList<String>());
		capConfigModel.setSelectedCodes(new ArrayList<String>());
		checkForBtnEnable(capConfigModel);
	}

	@Override
	public void codeSelectionChanged(CapConfigModel capConfigModel) {
		checkForBtnEnable(capConfigModel);
	}

	private void checkForBtnEnable(CapConfigModel capConfigModel)
	{
		capConfigModel.setEnableClearAllCodes(capConfigModel.getCodes().size()>0);	
		capConfigModel.setEnableClearCode(capConfigModel.getSelectedCodes()!=null && capConfigModel.getSelectedCodes().size()>0);
		capConfigModel.setEnableOk(true);
	}


	
	
}
