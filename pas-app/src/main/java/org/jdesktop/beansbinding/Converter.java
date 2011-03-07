package org.jdesktop.beansbinding;

public abstract class Converter<S, T> {
	public abstract T convertForward(S in);
	public abstract S convertReverse(T out);
	public static Object defaultConvert(Object in, Object out)
	{
		return out.toString();
	}
	
}
