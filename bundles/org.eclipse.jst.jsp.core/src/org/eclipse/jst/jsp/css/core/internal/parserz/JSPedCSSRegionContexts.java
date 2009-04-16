/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.css.core.internal.parserz;

import org.eclipse.wst.css.core.internal.parserz.CSSRegionContexts;

public interface JSPedCSSRegionContexts extends CSSRegionContexts {	
	public static final String CSS_JSP_EXP = "CSS_JSP_EXP"; //$NON-NLS-1$
	public static final String CSS_JSP_EL = CSSRegionContexts.CSS_FOREIGN_ELEMENT; //$NON-NLS-1$
	public static final String CSS_JSP_SCRIPTLET = "CSS_JSP_SCRIPTLET"; //$NON-NLS-1$
	public static final String CSS_JSP_DIRECTIVE = "CSS_JSP_DIRECTIVE"; //$NON-NLS-1$
	public static final String CSS_JSP_DECL = "CSS_JSP_DECL"; //$NON-NLS-1$
	public static final String CSS_JSP_END = "CSS_JSP_END"; //$NON-NLS-1$
	public static final String CSS_EL_END = "CSS_EL_END"; //$NON-NLS-1$
	public static final String CSS_JSP_COMMENT_END = "CSS_JSP_COMMENT_END"; //$NON-NLS-1$
	public static final String CSS_JSP_COMMENT = "CSS_JSP_COMMENT"; //$NON-NLS-1$
}
