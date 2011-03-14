package org.jdesktop.beansbinding;

public abstract class Converter<S, T> {
    private static final Converter<?,?> NOOP = new Converter<Object, Object>() {
        @Override
        public Object convertForward(Object in) {
            return in;
        }

        @Override
        public Object convertReverse(Object out) {
            return out;
        }
    };

    @SuppressWarnings("unchecked")
    public static <IN, OUT> Converter<IN, OUT> noop() {
        return (Converter<IN, OUT>) NOOP;
    }

    public abstract T convertForward(S in);
	public abstract S convertReverse(T out);

}
