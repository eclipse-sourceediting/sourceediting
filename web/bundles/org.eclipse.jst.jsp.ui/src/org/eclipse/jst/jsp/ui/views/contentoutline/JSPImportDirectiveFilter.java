/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.views.contentoutline;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.JSP20TLDNames;
import org.eclipse.jst.jsp.core.internal.provisional.JSP11Namespace;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class JSPImportDirectiveFilter extends ViewerFilter {


	/*
	 * @see ViewerFilter
	 */
	public boolean select(Viewer viewer, Object parent, Object element) {
		if (element instanceof Node){
			Node node = (Node)element;
			if (node.getNodeName().equalsIgnoreCase(JSP11Namespace.ElementName.DIRECTIVE_PAGE)){
					final NamedNodeMap nodeMap = node.getAttributes();
					for (int i=0;i <nodeMap.getLength();i++){
						final Node attr = nodeMap.item(i);
						if (attr != null && JSP20TLDNames.IMPORT.equalsIgnoreCase(attr.getNodeName()))
							return false;
					}
			}
		}
		return true;
	}
}
