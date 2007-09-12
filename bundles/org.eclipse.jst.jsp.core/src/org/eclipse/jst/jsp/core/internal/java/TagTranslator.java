/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.java;

class TagTranslator extends JSPTranslator {

	public TagTranslator() {
		super();
	}
	
	protected void init() {
		fClassHeader = "public class _TagHandler extends "; //$NON-NLS-1$
		fClassname = "_TagHandler"; //$NON-NLS-1$

		fImplicitImports = "import javax.servlet.*;" + ENDL + //$NON-NLS-1$
					"import javax.servlet.http.*;" + ENDL + //$NON-NLS-1$
					"import javax.servlet.jsp.*;" + ENDL + ENDL; //$NON-NLS-1$

		fServiceHeader = "public void doTag() throws JspException, java.io.IOException, IllegalStateException, SkipPageException {" + //$NON-NLS-1$
					"javax.servlet.http.HttpServletResponse response = null;" + ENDL + //$NON-NLS-1$
					"javax.servlet.http.HttpServletRequest request = null;" + ENDL + //$NON-NLS-1$
					"JspContext jspContext = null;" + ENDL + //$NON-NLS-1$
					"javax.servlet.ServletContext application = null;" + ENDL + //$NON-NLS-1$
					"javax.servlet.jsp.JspWriter out = null;" + ENDL + //$NON-NLS-1$
					"javax.servlet.ServletConfig config = null;" + ENDL; //$NON-NLS-1$ 

		fSuperclass = "javax.servlet.jsp.tagext.SimpleTagSupport"; //$NON-NLS-1$
	}
}
