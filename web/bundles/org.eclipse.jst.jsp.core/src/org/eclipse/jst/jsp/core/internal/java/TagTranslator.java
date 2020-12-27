/*******************************************************************************
 * Copyright (c) 2007, 2020 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
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

		fImplicitImports = "import " + fServletAPIDescriptor.getRootPackage() + ".*;" + ENDL + //$NON-NLS-1$
					"import "+ fServletAPIDescriptor.getRootPackage() + ".http.*;" + ENDL + //$NON-NLS-1$
					"import " + fServletAPIDescriptor.getRootPackage() + ".jsp.*;" + ENDL + ENDL; //$NON-NLS-1$

		fServiceHeader = "public void doTag() throws JspException, java.io.IOException, IllegalStateException, SkipPageException {" + //$NON-NLS-1$
					fServletAPIDescriptor.getRootPackage() + ".http.HttpServletResponse response = null;" + ENDL + //$NON-NLS-1$
					fServletAPIDescriptor.getRootPackage() + ".http.HttpServletRequest request = null;" + ENDL + //$NON-NLS-1$
					"JspContext jspContext = getJspContext();" + ENDL + //$NON-NLS-1$
					fServletAPIDescriptor.getRootPackage() + ".ServletContext application = null;" + ENDL + //$NON-NLS-1$
					fServletAPIDescriptor.getRootPackage() + ".jsp.JspWriter out = null;" + ENDL + //$NON-NLS-1$
					fServletAPIDescriptor.getRootPackage() + ".ServletConfig config = null;" + ENDL; //$NON-NLS-1$ 

		fSuperclass = fServletAPIDescriptor.getRootPackage() + ".jsp.tagext.SimpleTagSupport"; //$NON-NLS-1$

		fContext = "getJspContext()"; //$NON-NLS-1$
		fSession = "((PageContext)" + fContext + ").getSession();"; //$NON-NLS-1$ //$NON-NLS-2$
	}
}
