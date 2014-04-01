package no.ums.pas.core.logon;

import org.jdesktop.beansbinding.Converter;

public class IntegerToObject extends Converter<Integer,Object>{

	@Override
	public Object convertForward(Integer in) {
		 return (in == null) ? null : new DeptCategory(in,""+in);
	}

	@Override
	public Integer convertReverse(Object out) {
		return (out == null) ? 0 : ((DeptCategory)out).getValue();
	}
}