/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.document;



import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;


/**
 * DocumentFragmentImpl class
 */
public class DocumentFragmentImpl extends NodeContainer implements DocumentFragment {

	/**
	 * DocumentFragmentImpl constructor
	 */
	protected DocumentFragmentImpl() {
		super();
	}

	/**
	 * DocumentFragmentImpl constructor
	 * 
	 * @param that
	 *            DocumentFragmentImpl
	 */
	protected DocumentFragmentImpl(DocumentFragmentImpl that) {
		super(that);
	}

	/**
	 * cloneNode method
	 * 
	 * @return org.w3c.dom.Node
	 * @param deep
	 *            boolean
	 */
	public Node cloneNode(boolean deep) {
		DocumentFragmentImpl cloned = new DocumentFragmentImpl(this);
		if (deep)
			cloneChildNodes(cloned, deep);
		return cloned;
	}

	/**
	 * getNodeName method
	 * 
	 * @return java.lang.String
	 */
	public String getNodeName() {
		return "#document-fragment";//$NON-NLS-1$
	}

	/**
	 * getNodeType method
	 * 
	 * @return short
	 */
	public short getNodeType() {
		return DOCUMENT_FRAGMENT_NODE;
	}
}
