package org.eclipse.wst.css.core.internal.provisional.contenttype;


/**
 * This class, with its one field, is a convience to provide compile-time
 * safety when refering to a contentType ID. The value of the contenttype id
 * field must match what is specified in plugin.xml file.
 */

public class ContentTypeIdForCSS {
	/**
	 * The value of the contenttype id field must match what is specified in
	 * plugin.xml file. Note: this value is intentially set with default
	 * protected method so it will not be inlined.
	 */
	public final static String ContentTypeID_CSS = getConstantString();

	/**
	 * Don't allow instantiation.
	 */
	private ContentTypeIdForCSS() {
		super();
	}
	
	static String getConstantString() {
		return "org.eclipse.wst.css.core.csssource"; //$NON-NLS-1$
	}
	

}
