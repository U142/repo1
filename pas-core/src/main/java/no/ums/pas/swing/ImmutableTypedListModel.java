package no.ums.pas.swing;

import com.google.common.collect.ImmutableList;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public class ImmutableTypedListModel<T> extends AbstractTypedListModel<T, ImmutableList<T>> {

    @SuppressWarnings("unchecked")
    private static final ImmutableTypedListModel EMPTY = new ImmutableTypedListModel();

    @SuppressWarnings("unchecked")
    public static <T> ImmutableTypedListModel<T> empty() {
        return (ImmutableTypedListModel<T>) EMPTY;
    }

    public ImmutableTypedListModel(ImmutableList<T> container) {
        super(container);
    }

    public ImmutableTypedListModel(Iterable<T> container) {
        this(ImmutableList.copyOf(container));
    }

    public ImmutableTypedListModel(T ... values) {
        this(ImmutableList.copyOf(values));
    }

    @Override
    protected int indexOf(ImmutableList<T> container, T source) {
        return container.indexOf(source);
    }

    @Override
    protected boolean contains(ImmutableList<T> container, T value) {
        return container.contains(value);
    }

    @Override
    protected T elementAt(ImmutableList<T> container, int index) {
        return container.get(index);
    }

    @Override
    protected int sizeOf(ImmutableList<T> container) {
        return container.size();
    }
}
