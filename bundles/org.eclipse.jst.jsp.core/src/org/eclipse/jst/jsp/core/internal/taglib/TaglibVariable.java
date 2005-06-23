package org.eclipse.jst.jsp.core.internal.taglib;


/**
 * Contains info about a TaglibVariable: classname, variablename.
 * @author pavery
 */
public class TaglibVariable {
    
	private String fVarClass = null;
	private String fVarName = null;
	private final String ENDL = "\n";
	/**
	 * 
	 */
	public TaglibVariable(String varClass, String varName) {
		setVarClass(varClass);
		setVarName(varName);
	}
	/**
	 * @return Returns the fVarClass.
	 */
	public final String getVarClass() {
		return fVarClass;
	}
	/**
	 * @param varClass The fVarClass to set.
	 */
	public final void setVarClass(String varClass) {
		fVarClass = varClass;
	}
	/**
	 * @return Returns the fVarName.
	 */
	public final String getVarName() {
		return fVarName;
	}
	/**
	 * @param varName The fVarName to set.
	 */
	public final void setVarName(String varName) {
		fVarName = varName;
	}
	
	/**
	 * Convenience method.
	 * @return
	 */
	public final String getDeclarationString() {
		return getVarClass() + " " + getVarName() + " = null;" + ENDL; //$NON-NLS-1$ //$NON-NLS-2$
	}
}
