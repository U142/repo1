package no.ums.pas.swing.dynamictree;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import javax.annotation.Nullable;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author Ståle Undheim <su@ums.no>
 */
public abstract class LazyTreeFilteredSelectionModel<T> extends DefaultTreeSelectionModel {

    private final LazyTreeModel<T> treeModel;

    public LazyTreeFilteredSelectionModel(LazyTreeModel<T> treeModel) {
        this.treeModel = treeModel;
    }

    @Override
    public void setSelectionPaths(TreePath[] pPaths) {
        super.setSelectionPaths(filter(pPaths));
    }

    @Override
    public void addSelectionPaths(TreePath[] paths) {
        super.addSelectionPaths(filter(paths));
    }

    private TreePath[] filter(TreePath[] paths) {
        final Collection<TreePath> filtered = Collections2.filter(Arrays.asList(paths), new Predicate<TreePath>() {
            @Override
            public boolean apply(@Nullable TreePath input) {
                return filter(treeModel.getNodeValue(input.getLastPathComponent()));
            }
        });
        return filtered.toArray(new TreePath[filtered.size()]);
    }

    protected abstract boolean filter(T nodeValue);
}
