/*******************************************************************************
 * Copyright (c) 2005-2007 Orangevolt (www.orangevolt.com)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Orangevolt (www.orangevolt.com) - XSLT support
 *     Jesper Steen Moller - refactored Orangevolt XSLT support into WST
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.xpath.ui.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.w3c.dom.Text;

@Deprecated
public class DOMViewerFilter extends ViewerFilter {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers
	 * .Viewer, java.lang.Object, java.lang.Object)
	 */
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		return !((element instanceof Text) && ((((Text) element).getData())
				.trim().length() == 0));
	}
}
