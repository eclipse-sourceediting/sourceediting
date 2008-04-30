/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.core.internal.provisional.text;

/**
*
* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*/
public interface IJSPPartitionTypes {
	String JSP_COMMENT = "org.eclipse.wst.jsdt.web.JSP_COMMENT"; //$NON-NLS-1$
	String JSP_CONTENT_DELIMITER = IJSPPartitionTypes.JSP_SCRIPT_PREFIX + "DELIMITER"; //$NON-NLS-1$
	String JSP_CONTENT_JAVA = IJSPPartitionTypes.JSP_SCRIPT_PREFIX + "JAVA"; //$NON-NLS-1$
	String JSP_CONTENT_JAVASCRIPT = IJSPPartitionTypes.JSP_SCRIPT_PREFIX + "JAVASCRIPT"; //$NON-NLS-1$
	String JSP_DEFAULT = "org.eclipse.wst.jsdt.web.DEFAULT_JSP"; //$NON-NLS-1$
	String JSP_DEFAULT_EL = IJSPPartitionTypes.JSP_SCRIPT_PREFIX + "JSP_EL"; //$NON-NLS-1$
	String JSP_DEFAULT_EL2 = IJSPPartitionTypes.JSP_SCRIPT_PREFIX + "JSP_EL2"; //$NON-NLS-1$
	String JSP_DIRECTIVE = "org.eclipse.wst.jsdt.web.JSP_DIRECTIVE"; //$NON-NLS-1$
	String JSP_SCRIPT_PREFIX = "org.eclipse.wst.jsdt.web.SCRIPT."; //$NON-NLS-1$
}
