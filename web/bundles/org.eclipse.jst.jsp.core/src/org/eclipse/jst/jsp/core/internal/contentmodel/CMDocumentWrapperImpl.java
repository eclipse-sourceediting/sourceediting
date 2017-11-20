/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.contentmodel;



import java.util.Hashtable;
import java.util.Iterator;

import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamespace;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.provisional.contentmodel.CMNodeWrapper;

public class CMDocumentWrapperImpl implements CMDocument, CMNodeWrapper {

	class CMNamedNodeMapImpl implements CMNamedNodeMap {

		protected Hashtable table = new Hashtable();

		public CMNamedNodeMapImpl() {
			super();
		}

		Hashtable getHashtable() {
			return table;
		}

		public int getLength() {
			return table.size();
		}

		public CMNode getNamedItem(String name) {
			return (CMNode) table.get(name);
		}

		public CMNode item(int index) {
			Object result = null;
			int size = table.size();
			if (index < size) {
				Iterator values = iterator();
				for (int i = 0; i <= index; i++) {
					result = values.next();
				}
			}
			return (CMNode) result;
		}

		public Iterator iterator() {
			return table.values().iterator();
		}

		public void setNamedItem(String name, CMNode aNode) {
			if (name != null && aNode != null)
				table.put(name, aNode);
		}
	}

	public class CMNamespaceImpl implements CMNamespace {
		public String getNodeName() {
			return CMDocumentWrapperImpl.this.getURI();
		}

		public int getNodeType() {
			return CMNode.NAME_SPACE;
		}

		public String getPrefix() {
			return CMDocumentWrapperImpl.this.getPrefix();
		}

		public Object getProperty(String property) {
			return null;
		}

		public String getURI() {
			return CMDocumentWrapperImpl.this.getURI();
		}

		public boolean supports(String feature) {
			return false;
		}
	}

	private CMDocument fDocument;
	private CMNamedNodeMap fElements = null;
	private CMNamedNodeMap fEntities = null;
	private CMNamespace fNamespace = new CMNamespaceImpl();
	private String fPrefix;
	private String fURI;

	public CMDocumentWrapperImpl(String newURI, String newPrefix, CMDocument tld) {
		fURI = newURI;
		fPrefix = newPrefix;
		fDocument = tld;
	}

	/**
	 * 
	 * @return org.eclipse.wst.xml.core.internal.contentmodel.CMDocument
	 */
	public CMDocument getDocument() {
		return fDocument;
	}

	/**
	 * getElements method
	 * @return CMNamedNodeMap
	 *
	 * Returns CMNamedNodeMap of ElementDeclaration
	 */
	public CMNamedNodeMap getElements() {
		if (fElements == null) {
			int length = getDocument().getElements().getLength();
			CMNamedNodeMapImpl elements = new CMNamedNodeMapImpl();
			for (int i = 0; i < length; i++) {
				CMElementDeclaration ed = new CMElementDeclarationWrapperImpl(fPrefix, (CMElementDeclaration) getDocument().getElements().item(i));
				elements.setNamedItem(ed.getNodeName(), ed);
			}
			fElements = elements;
		}
		return fElements;
	}

	/**
	 * getEntities method
	 * @return CMNamedNodeMap
	 *
	 * Returns CMNamedNodeMap of EntityDeclaration
	 */
	public CMNamedNodeMap getEntities() {
		if (fEntities == null) {
			fEntities = getDocument().getEntities();
		}
		return fEntities;
	}

	/**
	 * getNamespace method
	 * @return CMNamespace
	 */
	public CMNamespace getNamespace() {
		return fNamespace;
	}

	/**
	 * getNodeName method
	 * @return java.lang.String
	 */
	public String getNodeName() {
		return getDocument().getNodeName();
	}

	/**
	 * getNodeType method
	 * @return int
	 *
	 * Returns one of :
	 *
	 */
	public int getNodeType() {
		return getDocument().getNodeType();
	}

	public CMNode getOriginNode() {
		return fDocument;
	}

	/**
	 * 
	 * @return java.lang.String
	 */
	public String getPrefix() {
		return fPrefix;
	}

	/**
	 * getProperty method
	 * @return java.lang.Object
	 *
	 * Returns the object property desciped by the propertyName
	 *
	 */
	public Object getProperty(String propertyName) {
		return getDocument().getProperty(propertyName);
	}

	/**
	 * 
	 * @return java.lang.String
	 */
	public String getURI() {
		return fURI;
	}

	/**
	 * supports method
	 * @return boolean
	 *
	 * Returns true if the CMNode supports a specified property
	 *
	 */
	public boolean supports(String propertyName) {
		return getDocument().supports(propertyName);
	}
}
