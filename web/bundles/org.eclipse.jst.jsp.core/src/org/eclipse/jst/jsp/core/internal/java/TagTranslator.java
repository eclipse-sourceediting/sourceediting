/*******************************************************************************
 * Copyright (c) 2007, 2021 IBM Corporation and others.
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
		String rootPackage = fServletAPIDescriptor.getRootPackage();

		fClassHeader = "public class _TagHandler extends "; //$NON-NLS-1$
		fClassname = "_TagHandler"; //$NON-NLS-1$

		fImplicitImports = "import " + rootPackage + ".*;" + ENDL + //$NON-NLS-1$
					"import "+ rootPackage + ".http.*;" + ENDL + //$NON-NLS-1$
					"import " + rootPackage + ".jsp.*;" + ENDL + ENDL; //$NON-NLS-1$

		fServiceHeader = "public void doTag() throws JspException, java.io.IOException, IllegalStateException, SkipPageException {" + //$NON-NLS-1$
					rootPackage + ".http.HttpServletResponse response = null;" + ENDL + //$NON-NLS-1$
					rootPackage + ".http.HttpServletRequest request = null;" + ENDL + //$NON-NLS-1$
					"JspContext jspContext = getJspContext();" + ENDL + //$NON-NLS-1$
					rootPackage + ".ServletContext application = null;" + ENDL + //$NON-NLS-1$
					rootPackage + ".jsp.JspWriter out = null;" + ENDL + //$NON-NLS-1$
					rootPackage + ".ServletConfig config = null;" + ENDL; //$NON-NLS-1$ 

		fSuperclass = rootPackage + ".jsp.tagext.SimpleTagSupport"; //$NON-NLS-1$

		fContext = "getJspContext()"; //$NON-NLS-1$
		fSession = "((PageContext)" + fContext + ").getSession();"; //$NON-NLS-1$ //$NON-NLS-2$
	}
}
