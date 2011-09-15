/*******************************************************************************
 * Copyright (c) 2004, 2011 IBM Corporation and others.
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
import org.eclipse.wst.xml.core.internal.search.matching.SAXSearchElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ibm.icu.util.StringTokenizer;

public class XMLSearchPattern extends SearchPattern{
	
	private String elementNamespace = null;
	private String elementName = null;
	private String attributeName = null;
	private String searchName = null;
	private String searchNamespace = null;
	private String parentName = null;
    private int depth = -1;
    private SAXSearchElement element = null;
    private Element domElement = null;

    public XMLSearchPattern(String elementNamespace, String parentElementName,String elementName, String attributeName) {
    	this(elementNamespace, parentElementName, elementName, attributeName, -1);
    }
	
	public XMLSearchPattern(String elementNamespace, String elementName, String attributeName) {
       this(elementNamespace, null, elementName, attributeName, -1);
    }
    
    public XMLSearchPattern(String elementNamespace, String elementName, String attributeName, int depth) {
    	this(elementNamespace, null, elementName, attributeName, depth);       
    }
           
    private XMLSearchPattern(String elementNamespace, String parentElementName, String elementName, String attributeName, int depth) {
        super();
        this.attributeName = attributeName;
        this.elementName = elementName;
        this.parentName = parentElementName;
        this.elementNamespace = elementNamespace;
        this.depth = depth;
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

	public void setSearchElement(SAXSearchElement element) {
		this.element = element;
	}

	public void setSearchElement(Element element) {
		domElement = element;
	}

	public boolean matches(XMLSearchPattern pattern) {
		if (pattern.searchName == null)
			return false;
		if ("*".equals(searchName) && "*".equals(searchNamespace)) { //$NON-NLS-1$ //$NON-NLS-2$
			return true;
		}

		final StringTokenizer tokenizer = new StringTokenizer(pattern.searchName);
		 while (tokenizer.hasMoreTokens()) {
			 final String token = tokenizer.nextToken();
			 int n = token.indexOf(":"); //$NON-NLS-1$
			 String name = token;
			 String namespace = pattern.searchNamespace; // accept the default
			 if(n > 0) {
				 final String prefix = token.substring(0, n);
				 name = token.substring(n+1);
				 namespace = pattern.element != null ? (String)pattern.element.getNamespaceMap().get(prefix) : computeNamespaceForPrefix(pattern.domElement, prefix);
			 }

			 if (namespace == null) {
				 if (name.equals(searchName) || "*".equals(searchName))
					 return true;
			 }
			 else {
				 if ((namespace.equals(searchNamespace) || "*".equals(searchNamespace)) && name.equals(searchName))
					 return true;
			 }
		 }
		 return false;
	}

	protected String computeNamespaceForPrefix(Element element, String prefix)
	{
	  String result = null;
	  for (Node node = element; node != null; node = node.getParentNode())
	  {
		if (node.getNodeType() == Node.ELEMENT_NODE)
		{
		  Element e = (Element)node;
		  String attributeName = (prefix != null && prefix.length() > 0) ? ("xmlns:" + prefix) : "xmlns";  //$NON-NLS-1$ //$NON-NLS-2$
		  result = e.hasAttribute(attributeName) ? e.getAttribute(attributeName) : null;
		  if (result != null && result.length() > 0)
		  {
			 break;  
		  }	  
		}	
	  }	  
	  return result;
	}

  public int getDepth()
  {
    return depth;
  }

  public void setDepth(int depth)
  {
    this.depth = depth;
  }

public String getParentName() {
	return parentName;
}

public void setParentName(String parentName) {
	this.parentName = parentName;
}
	

	
}