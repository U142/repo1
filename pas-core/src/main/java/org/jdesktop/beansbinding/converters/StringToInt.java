package org.jdesktop.beansbinding.converters;

import org.jdesktop.beansbinding.Converter;

public class StringToInt extends Converter<String, Integer> {
    @Override
    public Integer convertForward(String in) {
        return (in == null) ? 0 : Integer.valueOf(in);
    }

    @Override
    public String convertReverse(Integer out) {
        return (out == null) ? null : out.toString();
    }

}
