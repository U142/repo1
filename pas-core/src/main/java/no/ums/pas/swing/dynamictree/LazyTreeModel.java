package no.ums.pas.swing.dynamictree;

import no.ums.log.Log;
import no.ums.log.UmsLog;

import javax.annotation.Nonnull;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Implementation of a TreeModel that loads values on request.
 *
 * This treemodel needs to be attached as a TreeWillExpandListener to it's target tree.
 * It uses a {@link TreeNodeFactory} to fetch the content off the tree.
 *
 * @author Ståle Undheim <su@ums.no>
 */
public final class LazyTreeModel<T> implements TreeModel, TreeWillExpandListener {

    private static final Log log = UmsLog.getLogger(LazyTreeModel.class);

    private class LazyTreeNode extends DefaultMutableTreeNode {

        private T initObject;

        LazyTreeNode(T userObject) {
            super(userObject);
        }

        @Override
        @SuppressWarnings("unchecked")
        public T getUserObject() {
            return (T) super.getUserObject();
        }

        @Override
        public boolean isLeaf() {
            return factory.isLeaf(getUserObject());
        }

        @Override
        @SuppressWarnings("unchecked")
        public LazyTreeNode getChildAt(int index) {
            return (LazyTreeNode) super.getChildAt(index);    //To change body of overridden methods use File | Settings | File Templates.
        }
    }

    private final DefaultTreeModel delegate;
    private final LazyTreeNode root;
    private final TreeNodeFactory<T> factory;

    private Map<Object, LazyTreeNode> nodeMap = new HashMap<Object, LazyTreeNode>();

    public LazyTreeModel(@Nonnull T rootValue, TreeNodeFactory<T> factory) {
        this.factory = factory;
        this.root = createNode(rootValue);
        this.delegate = new DefaultTreeModel(root);
        populateNode(root);
    }

    public TreePath getPathTo(T value) {
        if (nodeMap.containsKey(factory.identity(value))) {
            return new TreePath(nodeMap.get(factory.identity(value)).getPath());
        }
        return null;
    }

    public void expired(final T value) {
        if (nodeMap.containsKey(factory.identity(value))) {
            LazyTreeNode lazyTreeNode = nodeMap.get(factory.identity(value));
            lazyTreeNode.setUserObject(value);
            if (!lazyTreeNode.isLeaf()) {
                populateNode(lazyTreeNode);
            }
        }
    }

    private LazyTreeNode createNode(T value) {
        final LazyTreeNode node;
        Object id = factory.identity(value);
        if (nodeMap.containsKey(id)) {
            node = nodeMap.get(id);
        } else {
            node = new LazyTreeNode(value);
            nodeMap.put(id, node);
        }
        node.setUserObject(value);
        return node;
    }

    @SuppressWarnings("unchecked")
    public T getNodeValue(Object node) {
        return ((LazyTreeNode) node).getUserObject();
    }

    @Override
    public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
        @SuppressWarnings("unchecked")
        final LazyTreeNode node = (LazyTreeNode) event.getPath().getLastPathComponent();
        populateNode(node);
    }

    private void populateNode(LazyTreeNode node) {
        if (node.initObject != node.getUserObject()) {
            synchronized (root) {
                while (node.getChildCount() > 0) {
                    delegate.removeNodeFromParent(node.getChildAt(node.getChildCount()-1));
                }
                if (!node.isLeaf()) {
                    for (T child : factory.getChildren(node.getUserObject())) {
                        delegate.insertNodeInto(createNode(child), node, node.getChildCount());
                    }
                }
                node.initObject = node.getUserObject();
            }
        }
    }

    @Override
    public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
        // Noop
    }

    @Override
    public Object getRoot() {
        return delegate.getRoot();
    }

    @Override
    public Object getChild(Object parent, int index) {
        return delegate.getChild(parent, index);
    }

    @Override
    public int getChildCount(Object parent) {
        return delegate.getChildCount(parent);
    }

    @Override
    public boolean isLeaf(Object node) {
        return factory.isLeaf(getNodeValue(node));
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        delegate.valueForPathChanged(path, newValue);
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return delegate.getIndexOfChild(parent, child);
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        delegate.addTreeModelListener(l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        delegate.removeTreeModelListener(l);
    }
}
