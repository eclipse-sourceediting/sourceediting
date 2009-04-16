/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.text;


/**
 * This interface is not intended to be implemented.
 * It defines the partition types for JSP.
 * Clients should reference the partition type Strings defined here directly.
 * 
 * @since 1.1
 */
public interface IJSPPartitions {

	String JSP_DEFAULT = "org.eclipse.jst.jsp.DEFAULT_JSP"; //$NON-NLS-1$
	String JSP_COMMENT = "org.eclipse.jst.jsp.JSP_COMMENT"; //$NON-NLS-1$
	
	String JSP_SCRIPT_PREFIX = "org.eclipse.jst.jsp.SCRIPT."; //$NON-NLS-1$
	String JSP_CONTENT_DELIMITER = JSP_SCRIPT_PREFIX + "DELIMITER"; //$NON-NLS-1$
	String JSP_CONTENT_JAVA = JSP_SCRIPT_PREFIX + "JAVA"; //$NON-NLS-1$
	String JSP_CONTENT_JAVASCRIPT = JSP_SCRIPT_PREFIX + "JAVASCRIPT"; //$NON-NLS-1$
	String JSP_DEFAULT_EL = JSP_SCRIPT_PREFIX + "JSP_EL"; //$NON-NLS-1$
	String JSP_DEFAULT_EL2 = JSP_SCRIPT_PREFIX + "JSP_EL2"; //$NON-NLS-1$
	
	String JSP_DIRECTIVE = "org.eclipse.jst.jsp.JSP_DIRECTIVE"; //$NON-NLS-1$
}
