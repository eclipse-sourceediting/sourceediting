/**
 * 
 */
package org.eclipse.wst.jsdt.web.core.internal.java;

import java.util.List;

import org.eclipse.wst.jsdt.core.ICompilationUnit;
import org.eclipse.wst.jsdt.core.IJavaElement;
import org.eclipse.wst.jsdt.core.WorkingCopyOwner;

/**
 * @author childsb
 * 
 */
public interface IJsTranslation {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#fixupMangledName(java.lang.String)
	 */
	public abstract String fixupMangledName(String displayString);
	
	public abstract IJavaElement[] getAllElementsInJsRange(int jspStart, int jspEnd);
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#getCompilationUnit()
	 */
	public abstract ICompilationUnit getCompilationUnit();
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#getElementsFromJspRange(int,
	 *      int)
	 */
	public abstract IJavaElement[] getElementsFromJsRange(int jspStart, int jspEnd);
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#getJspText()
	 */
	public abstract String getHtmlText();
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#isUseBean(int)
	 */
	// public boolean isUseBean(int javaOffset) {
	// System.out.println("REMOVE JSPTranslation.isUseBean(int javaOffset)");
	// return false;
	// }
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#getJavaPath()
	 */
	public abstract String getJavaPath();
	
	public abstract IJavaElement getJsElementAtOffset(int htmlstart);
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.IJSPTranslation#getJavaText()
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#getJavaText()
	 */
	public abstract String getJsText();
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#getProblems()
	 */
	public abstract List getProblems();
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#getWorkingCopyOwner()
	 */
	public abstract WorkingCopyOwner getWorkingCopyOwner();
	
	public abstract boolean ifOffsetInImportNode(int offset);
	
	//public abstract boolean isOffsetInScriptNode(int offset);
	
	public abstract void reconcileCompilationUnit();
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#release()
	 */
	public abstract void release();
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation_Interface#setProblemCollectingActive(boolean)
	 */
	public abstract void setProblemCollectingActive(boolean collect);
}