/**
 * 
 */
package org.eclipse.wst.jsdt.web.core.internal.modelhandler;

/**
 * @author childsb
 * 
 */
public interface IWebDocumentChangeListener extends IWebResourceChangedListener {
	public static final int BORING = 0;
	public static final int DIRTY_DOC = 1;
	public static final int DIRTY_MODEL = 2;
	
	public int getIntrestLevelAtOffset(int documentOffset);
}
