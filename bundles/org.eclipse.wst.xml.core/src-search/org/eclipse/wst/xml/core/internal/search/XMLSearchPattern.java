/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.core.internal.search;

import org.eclipse.wst.common.core.search.pattern.SearchPattern;

public class XMLSearchPattern extends SearchPattern{
	
	private String elementNamespace = null;
	private String elementName = null;
	private String attributeName = null;
	private String searchName = null;
	private String searchNamespace = null;
	
	public XMLSearchPattern(String elementNamespace, String elementName, String attributeName) {
		super();
		this.attributeName = attributeName;
		this.elementName = elementName;
		this.elementNamespace = elementNamespace;
	}
	
	public XMLSearchPattern(){
		
	}
	
	public String getAttributeName() {
		return attributeName;
	}

	public String getElementName() {
		return elementName;
	}

	public String getElementNamespace() {
		return elementNamespace;
	}

	public String getSearchName() {
		return searchName;
	}

	public String getSearchNamespace() {
		return searchNamespace;
	}

	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}

	public void setSearchNamespace(String searchNamespace) {
		this.searchNamespace = searchNamespace;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	public void setElementNamespace(String elementNamespace) {
		this.elementNamespace = elementNamespace;
	}
	

	
}