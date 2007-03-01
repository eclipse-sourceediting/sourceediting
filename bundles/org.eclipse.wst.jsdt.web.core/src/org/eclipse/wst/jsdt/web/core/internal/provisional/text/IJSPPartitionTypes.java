package org.eclipse.wst.jsdt.web.core.internal.provisional.text;

/**
 * This interface is not intended to be implemented. It defines the partition
 * types for JSP. Clients should reference the partition type Strings defined
 * here directly.
 * 
 * @deprecated use
 *             org.eclipse.wst.jsdt.web.core.internal.provisional.text.IJSPPartitions
 */
@Deprecated
public interface IJSPPartitionTypes {

	String JSP_DEFAULT = "org.eclipse.wst.jsdt.web.DEFAULT_JSP"; //$NON-NLS-1$
	String JSP_COMMENT = "org.eclipse.wst.jsdt.web.JSP_COMMENT"; //$NON-NLS-1$

	String JSP_SCRIPT_PREFIX = "org.eclipse.wst.jsdt.web.SCRIPT."; //$NON-NLS-1$
	String JSP_CONTENT_DELIMITER = JSP_SCRIPT_PREFIX + "DELIMITER"; //$NON-NLS-1$
	String JSP_CONTENT_JAVA = JSP_SCRIPT_PREFIX + "JAVA"; //$NON-NLS-1$
	String JSP_CONTENT_JAVASCRIPT = JSP_SCRIPT_PREFIX + "JAVASCRIPT"; //$NON-NLS-1$
	String JSP_DEFAULT_EL = JSP_SCRIPT_PREFIX + "JSP_EL"; //$NON-NLS-1$
	String JSP_DEFAULT_EL2 = JSP_SCRIPT_PREFIX + "JSP_EL2"; //$NON-NLS-1$

	String JSP_DIRECTIVE = "org.eclipse.wst.jsdt.web.JSP_DIRECTIVE"; //$NON-NLS-1$
}
