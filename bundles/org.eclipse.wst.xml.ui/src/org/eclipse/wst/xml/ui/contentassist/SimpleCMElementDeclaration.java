/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.xml.ui.contentassist;



public class SimpleCMElementDeclaration implements org.eclipse.wst.contentmodel.CMElementDeclaration {

	String fNodeName;

	/**
	 * SimpleCMELementDeclaration constructor comment.
	 */
	public SimpleCMElementDeclaration() {
		super();
	}

	public SimpleCMElementDeclaration(String nodeName) {
		super();
		setNodeName(nodeName);
	}

	/**
	 * getAttributes method
	 * @return CMNamedNodeMap
	 *
	 * Returns CMNamedNodeMap of AttributeDeclaration
	 */
	public org.eclipse.wst.contentmodel.CMNamedNodeMap getAttributes() {
		return null;
	}

	/**
	 * getCMContent method
	 * @return CMContent
	 *
	 * Returns the root node of this element's content model.
	 * This can be an CMElementDeclaration or a CMGroup
	 */
	public org.eclipse.wst.contentmodel.CMContent getContent() {
		return null;
	}

	/**
	 * getContentType method
	 * @return int
	 *
	 * Returns one of :
	 * ANY, EMPTY, ELEMENT, MIXED, PCDATA, CDATA.
	 */
	public int getContentType() {
		return 0;
	}

	/**
	 * getDataType method
	 * @return java.lang.String
	 */
	public org.eclipse.wst.contentmodel.CMDataType getDataType() {
		return null;
	}

	/**
	 * getElementName method
	 * @return java.lang.String
	 */
	public String getElementName() {
		return null;
	}

	/**
	 * getLocalElements method
	 * @return CMNamedNodeMap
	 *
	 * Returns a list of locally defined elements.
	 */
	public org.eclipse.wst.contentmodel.CMNamedNodeMap getLocalElements() {
		return null;
	}

	/**
	 * getMaxOccur method
	 * @return int
	 *
	 * If -1, it's UNBOUNDED.
	 */
	public int getMaxOccur() {
		return 0;
	}

	/**
	 * getMinOccur method
	 * @return int
	 *
	 * If 0, it's OPTIONAL.
	 * If 1, it's REQUIRED.
	 */
	public int getMinOccur() {
		return 0;
	}

	/**
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getNodeName() {
		return fNodeName;
	}

	/**
	 * getNodeType method
	 * @return int
	 *
	 * Returns one of :
	 *
	 */
	public int getNodeType() {
		return 0;
	}

	/**
	 * getProperty method
	 * @return java.lang.Object
	 *
	 * Returns the object property desciped by the propertyName
	 *
	 */
	public Object getProperty(String propertyName) {
		return null;
	}

	/**
	 * 
	 * @param newNodeName java.lang.String
	 */
	public void setNodeName(java.lang.String newNodeName) {
		fNodeName = newNodeName;
	}

	/**
	 * supports method
	 * @return boolean
	 *
	 * Returns true if the CMNode supports a specified property
	 *
	 */
	public boolean supports(String propertyName) {
		return false;
	}
}
