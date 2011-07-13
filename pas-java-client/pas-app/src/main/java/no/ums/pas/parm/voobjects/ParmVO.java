package no.ums.pas.parm.voobjects;

import no.ums.pas.maps.defines.ShapeStruct;

import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;


public abstract class ParmVO implements Transferable, Cloneable {
	@Override
	protected ParmVO clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return (ParmVO)super.clone();
	}
	protected ShapeStruct m_shape = null;

	public ShapeStruct getM_shape() {
		return m_shape;
	}

	public void setM_shape(ShapeStruct shape) {
		this.m_shape = shape;
	}

	public ParmVO() {
		
	}
	private String strOperation=null;

	public abstract String getPk();
	public void setOperation(String strOperation) { this.strOperation = strOperation; }
	public String getOperation() { return strOperation; }

	private boolean bDragOver = false;
	public void setDragOver(boolean b) { bDragOver = b; }
	public boolean getDragOver() { return bDragOver; }

	DefaultMutableTreeNode path;
	public void setPath(DefaultMutableTreeNode p) {
		path = p;
	}
	public abstract boolean hasValidPk();
	public DefaultMutableTreeNode getPath() { return path; }

	public synchronized Object getTransferData(DataFlavor flavor)
	throws UnsupportedFlavorException, IOException {
    //if (isDataFlavorSupported(flavor)) {
      return (Object) this;
    //} else {
    //  throw new UnsupportedFlavorException(flavor);
    //}
	}
	
}