package org.jdesktop.beansbinding;

public abstract class Converter<S, T> {
    private static final Converter<?, ?> NOOP = new Converter<Object, Object>() {
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
    /**
     * Creates a noop converter implicitly cast to the desired types.
     */
    public static <IN, OUT> Converter<IN, OUT> noop() {
        return (Converter<IN, OUT>) NOOP;
    }

    /**
     * Convert from a source type to the target type
     *
     * @param in value to convert from source to target
     * @return the target instance of the source value
     */
    public abstract T convertForward(S in);

    /**
     * Converts an instance from target to source.
     *
     * @param out value to convert to source
     * @return source value for the out value.
     */
    public abstract S convertReverse(T out);

}
