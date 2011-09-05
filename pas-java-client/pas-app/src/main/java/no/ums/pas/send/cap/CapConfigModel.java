package no.ums.pas.send.cap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import no.ums.ws.common.parm.AlertInfoCategory;
import no.ums.ws.common.parm.AlertInfoCertainty;
import no.ums.ws.common.parm.AlertInfoResponseType;
import no.ums.ws.common.parm.AlertInfoSeverity;
import no.ums.ws.common.parm.AlertInfoUrgency;
import no.ums.ws.common.parm.AlertScope;
import no.ums.ws.common.parm.AlertStatus;

import org.jdesktop.beansbinding.AbstractBean;
import org.joda.time.DateTime;

public class CapConfigModel extends AbstractBean {

	private final List<AlertStatus> listStatus = Arrays.asList(AlertStatus.values());
	private final List<AlertScope> listScope = Arrays.asList(AlertScope.values());
	private final List<AlertInfoCategory> listInfoCateogry = Arrays.asList(AlertInfoCategory.values());
	private final List<AlertInfoUrgency> listInfoUrgency = Arrays.asList(AlertInfoUrgency.values());
	private final List<AlertInfoCertainty> listInfoCertainty = Arrays.asList(AlertInfoCertainty.values());
	private final List<AlertInfoSeverity> listInfoSeverity = Arrays.asList(AlertInfoSeverity.values());
	private final List<AlertInfoResponseType> listInfoResponseType = Arrays.asList(AlertInfoResponseType.values());

	
	//bindings for enable/disable fields
	boolean enableRestriction;

	public boolean getEnableRestriction() {
		return enableRestriction;
	}

	public void setEnableRestriction(boolean enableRestriction) {
		final boolean oldValue = this.enableRestriction;
		this.enableRestriction = enableRestriction;
		update("enableRestriction", oldValue, this.enableRestriction);
	}
	
	boolean enableOk;

	public boolean getEnableOk() {
		return enableOk;
	}

	public void setEnableOk(boolean enableOk) {
		final boolean oldValue = this.enableOk;
		this.enableOk = enableOk;
		update("enableOk", oldValue, this.enableOk);
	}
	
	boolean enableClearCode;

	public boolean getEnableClearCode() {
		return enableClearCode;
	}

	public void setEnableClearCode(boolean enableClearCode) {
		final boolean oldValue = this.enableClearCode;
		this.enableClearCode = enableClearCode;
		update("enableClearCode", oldValue, this.enableClearCode);
	}
	
	boolean enableClearAllCodes;

	public boolean getEnableClearAllCodes() {
		return enableClearAllCodes;
	}

	public void setEnableClearAllCodes(boolean enableClearAllCodes) {
		final boolean oldValue = this.enableClearAllCodes;
		this.enableClearAllCodes = enableClearAllCodes;
		update("enableClearAllCodes", oldValue, this.enableClearAllCodes);
	}
	
	boolean enableSource;

	public boolean getEnableSource() {
		return enableSource;
	}

	public void setEnableSource(boolean enableSource) {
		final boolean oldValue = this.enableSource;
		this.enableSource = enableSource;
		update("enableSource", oldValue, this.enableSource);
	}
	
	boolean enableAddCode;

	public boolean getEnableAddCode() {
		return enableAddCode;
	}

	public void setEnableAddCode(boolean enableAddCode) {
		final boolean oldValue = this.enableAddCode;
		this.enableAddCode = enableAddCode;
		update("enableAddCode", oldValue, this.enableAddCode);
	}
	
	
	public List<AlertInfoCategory> getListInfoCateogry() {
		return listInfoCateogry;
	}
	public List<AlertInfoUrgency> getListInfoUrgency() {
		return listInfoUrgency;
	}
	public List<AlertInfoCertainty> getListInfoCertainty() {
		return listInfoCertainty;
	}
	public List<AlertInfoSeverity> getListInfoSeverity() {
		return listInfoSeverity;
	}
	public List<AlertInfoResponseType> getListInfoResponseType() {
		return listInfoResponseType;
	}
	public List<AlertScope> getListScope() {
		return listScope;
	}
	public List<AlertStatus> getListStatus() {
		return listStatus;
	}
	
	
	
	
	public AlertStatus getStatus() {
		return status;
	}
	public void setStatus(AlertStatus status) {
		AlertStatus oldValue = this.status;
		this.status = status;
		update("status", oldValue, status);
	}
	public AlertScope getScope() {
		return scope;
	}
	public void setScope(AlertScope scope) {
		AlertScope oldValue = this.scope;
		this.scope = scope;
		update("scope", oldValue, scope);
		//special rule, if restricted, user will be able to describe restriction rules
		setEnableRestriction(scope.equals(AlertScope.RESTRICTED));
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		String oldValue = this.note;
		this.note = note;
		update("note", oldValue, note);
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		String oldValue = this.source;
		this.source = source;
		update("source", oldValue, source);
	}
	public String getRestriction() {
		return restriction;
	}
	/***
	 * To be used only if scope="Restricted"
	 * @param restriction
	 */
	public void setRestriction(String restriction) {
		String oldValue = this.restriction;
		this.restriction = restriction;
		update("restriction", oldValue, restriction);
	}
	
	public List<AlertInfoCategory> getCategory() {
		return category;
	}
	public void setCategory(List<AlertInfoCategory> category) {
		List<AlertInfoCategory> oldValue = this.category;
		this.category = category;
		update("category", oldValue, category);
	}
	public AlertInfoUrgency getUrgency() {
		return urgency;
	}
	public void setUrgency(AlertInfoUrgency urgency) {
		AlertInfoUrgency oldValue = this.urgency;
		this.urgency = urgency;
		update("urgency", oldValue, urgency);
	}
	public AlertInfoCertainty getCertainty() {
		return certainty;
	}
	public void setCertainty(AlertInfoCertainty certainty) {
		AlertInfoCertainty oldValue = this.certainty;
		this.certainty = certainty;
		update("certainty", oldValue, certainty);
	}
	public AlertInfoSeverity getSeverity() {
		return severity;
	}
	public void setSeverity(AlertInfoSeverity severity) {
		AlertInfoSeverity oldValue = this.severity;
		this.severity = severity;
		update("severity", oldValue, severity);
	}
	public String getHeadline() {
		return headline;
	}
	public void setHeadline(String headline) {
		String oldValue = this.headline;
		this.headline = headline;
		update("headline", oldValue, headline);
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		String oldValue = this.description;
		this.description = description;
		update("description", oldValue, description);
	}
	public String getInstruction() {
		return instruction;
	}
	public void setInstruction(String instruction) {
		String oldValue = this.instruction;
		this.instruction = instruction;
		update("instruction", oldValue, instruction);
	}
	public String getWeb() {
		return web;
	}
	public void setWeb(String web) {
		String oldValue = this.web;
		this.web = web;
		update("web", oldValue, web);
	}
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		String oldValue = this.contact;
		this.contact = contact;
		update("contact", oldValue, contact);
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		String oldValue = this.language;
		this.language = language;
		update("language", oldValue, language);
	}
	public List<AlertInfoResponseType> getResponseType() {
		return responseType;
	}
	public void setResponseType(List<AlertInfoResponseType> responseType) {
		List<AlertInfoResponseType> oldValue = this.responseType;
		this.responseType = responseType;
		update("responseType", oldValue, responseType);
	}
	public DateTime getEffective() {
		return effective;
	}
	public void setEffective(DateTime effective) {
		DateTime oldValue = this.effective;
		this.effective = effective;
		update("effective", oldValue, effective);
	}
	public DateTime getOnset() {
		return onset;
	}
	public void setOnset(DateTime onset) {
		DateTime oldValue = this.onset;
		this.onset = onset;
		update("onset", oldValue, onset);
	}
	public DateTime getExpires() {
		return expires;
	}
	public void setExpires(DateTime expires) {
		Object oldValue = this.expires;
		this.expires = expires;
		update("expires", oldValue, expires);
	}
	public String getAudience() {
		return audience;
	}
	
	public void setAudience(String audience) {
		Object oldValue = this.audience;
		this.audience = audience;
		update("audience", oldValue, audience);
	}
	
	String code;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		final String oldValue = this.code;
		this.code = code;
		update("code", oldValue, this.code);
		setEnableAddCode(code!=null && this.code.length()>0);
	}
	
	List<String> codes;

	public List<String> getCodes() {
		return codes;
	}

	public void setCodes(List<String> codes) {
		final List<String> oldValue = this.codes;
		this.codes = codes;
		update("codes", oldValue, this.codes);
	}
	
	List<String> selectedCodes;

	public List<String> getSelectedCodes() {
		return selectedCodes;
	}

	public void setSelectedCodes(List<String> selectedCodes) {
		final List<String> oldValue = this.selectedCodes;
		this.selectedCodes = selectedCodes;
		update("selectedCodes", oldValue, this.selectedCodes);
	}
	
	
	//Alert tag fields
	public AlertStatus status;
	public AlertScope scope;
	public String note;
	public String source;
	public String restriction;
	
	//Info tag fields
	public List<AlertInfoCategory> category = new ArrayList<AlertInfoCategory>(Arrays.asList(AlertInfoCategory.GEO)); 
	public AlertInfoUrgency urgency;
	public AlertInfoCertainty certainty;
	public AlertInfoSeverity severity;
	public String headline;
	public String description;
	public String instruction;
	public String web;
	public String contact;
	public String language = "en-US";
	public List<AlertInfoResponseType> responseType;
	public DateTime effective;
	public DateTime onset;
	public DateTime expires;
	public String audience;
	
	
}
