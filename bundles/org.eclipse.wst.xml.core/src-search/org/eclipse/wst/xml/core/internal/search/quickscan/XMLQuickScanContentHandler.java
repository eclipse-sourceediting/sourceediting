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

package org.eclipse.wst.xml.core.internal.search.quickscan;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.eclipse.wst.common.core.search.document.ComponentDeclarationEntry;
import org.eclipse.wst.common.core.search.document.ComponentReferenceEntry;
import org.eclipse.wst.common.core.search.document.FileReferenceEntry;
import org.eclipse.wst.common.core.search.document.SearchDocument;
import org.eclipse.wst.common.core.search.pattern.QualifiedName;
import org.eclipse.wst.common.core.search.pattern.SearchPattern;
import org.eclipse.wst.xml.core.internal.search.XMLComponentDeclarationPattern;
import org.eclipse.wst.xml.core.internal.search.XMLComponentReferencePattern;
import org.eclipse.wst.xml.core.internal.search.impl.IXMLSearchConstants;
import org.eclipse.wst.xml.core.internal.search.matching.PatternMatcher;
import org.eclipse.wst.xml.core.internal.search.matching.SAXSearchElement;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class is a SAX content handler, it should be recycled before scanning a file for the new SearchPattern.
 *
 */
public class XMLQuickScanContentHandler extends DefaultHandler
{
	private Map namespaceMap = new HashMap(); // Map of string prefix keys and namespace
	private String targetNamespace = ""; //$NON-NLS-1$
	
	private SearchPattern pattern;
	private SearchDocument document;  // we'll add useful entries in the search document as we parsing
	private SAXSearchElement searchElement = new SAXSearchElement();

	private boolean hasMatch = false;
	private Stack currentPath = new Stack();
	private PatternMatcher matcher;
	
	public static final String XMLSCHEMA_NAMESPACE = "http://www.w3.org/2001/XMLSchema"; //$NON-NLS-1$
  public static final String WSDL_NAMESPACE = "http://schemas.xmlsoap.org/wsdl/"; //$NON-NLS-1$

	
	public XMLQuickScanContentHandler(PatternMatcher matcher, SearchPattern pattern) {
		super();
		this.pattern = pattern;
		this.matcher = matcher;
	}
	
	public XMLQuickScanContentHandler(SearchDocument document, PatternMatcher matcher, SearchPattern pattern) {
		super();
		this.pattern = pattern;
		this.matcher = matcher;
		this.document = document;
	}
	
	public XMLQuickScanContentHandler() {
		super();
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException
	{	
		// Search for targetNamespace if we haven't encountered it yet.
		if (targetNamespace.equals("")) //$NON-NLS-1$
		{
			int nAttributes = attributes.getLength();
			for (int i = 0; i < nAttributes; i++)
			{
				if ("targetNamespace".equals(attributes.getQName(i))) //$NON-NLS-1$
				{
					targetNamespace = attributes.getValue(i);
					break;
				}
			}
		}
		
		// collect link info
    
    // TODO This code should be refactored to delegate the responsibility to
    // detect links between files to the search providers/contributors.
    // The current code only handles the XSD and WSDL cases. 
    
		if("import".equals(localName) && namespaceMatches(uri)){ //$NON-NLS-1$
			FileReferenceEntry documentEntry = new FileReferenceEntry();
			documentEntry.setCategory(IXMLSearchConstants.REF);
			documentEntry.setKey("import"); //$NON-NLS-1$
			String namespace = attributes.getValue("namespace"); //$NON-NLS-1$
			String location = attributes.getValue(getLocationAttributeName(uri)); //$NON-NLS-1$
			documentEntry.setPublicIdentifier(namespace);
			documentEntry.setRelativeFilePath(location);            
			document.putEntry(documentEntry);
		}
		if(("redefine".equals(localName)|| "include".equals(localName)) && //$NON-NLS-1$ //$NON-NLS-2$
				namespaceMatches(uri)){
			FileReferenceEntry documentEntry = new FileReferenceEntry();
			documentEntry.setCategory(IXMLSearchConstants.REF);
			documentEntry.setKey("include"); //$NON-NLS-1$
			String location = attributes.getValue(getLocationAttributeName(uri)); //$NON-NLS-1$
			documentEntry.setPublicIdentifier(uri);
			documentEntry.setRelativeFilePath(location);
			document.putEntry(documentEntry);
		}
		
		
        // issue (cs) you may want to try perf measurements to compate reusing the same
        // instance of a SAXSearchElement instead of newing one each time 
		//XMLSearchPattern.SAXSearchElement searchElement = new XMLSearchPattern.SAXSearchElement();
		searchElement.setElementName(localName);
		searchElement.setElementNamespace(uri);
		searchElement.setAttributes(attributes);
		searchElement.setNamespaceMap(namespaceMap);
		searchElement.setTargetNamespace(targetNamespace);
		if (currentPath.size() > 0)
		{
		  String parentName = (String)currentPath.peek();
		  searchElement.setParentName(parentName);
		}			
	

		if(matcher != null){
			if(matcher.matches(pattern, searchElement)){
				hasMatch = true;
				if(pattern instanceof XMLComponentReferencePattern){
					ComponentReferenceEntry documentEntry = new ComponentReferenceEntry();
					documentEntry.setCategory(IXMLSearchConstants.COMPONENT_REF);
					QualifiedName name = new QualifiedName(uri, localName);
					documentEntry.setKey(name.toString());
					documentEntry.setName(name);
					document.putEntry(documentEntry);
				}
				else if(pattern instanceof XMLComponentDeclarationPattern){
					ComponentDeclarationEntry documentEntry = new ComponentDeclarationEntry();
					documentEntry.setCategory(IXMLSearchConstants.COMPONENT_DECL);
                    QualifiedName name = new QualifiedName(targetNamespace, attributes.getValue("name")); //$NON-NLS-1$
					QualifiedName metaName = new QualifiedName(uri, localName);                    
					documentEntry.setKey(name.toString());
                    documentEntry.setName(name);
					documentEntry.setMetaName(metaName);
					document.putEntry(documentEntry);
				}
			}
		}
		currentPath.push(localName); //$NON-NLS-1$		
	}

  private String getLocationAttributeName(String uri)
  {
    if (XMLSCHEMA_NAMESPACE.equals(uri))
    {
      return "schemaLocation";
    }
    
    else if (WSDL_NAMESPACE.equals(uri))
    {
      return "location";
    }
    
    return "";
  }

  private boolean namespaceMatches(String uri)
  {
    return XMLSCHEMA_NAMESPACE.equals(uri) ||
          WSDL_NAMESPACE.equals(uri);
  }
	
	public void endElement(String uri, String localName, String qName)
			throws SAXException
	{
		currentPath.pop();
	}

	/**
	 * Callback for SAX parser
	 * 
	 * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String,
	 *      java.lang.String)
	 */
	public void startPrefixMapping(String arg0, String arg1)
			throws SAXException
	{
		if (arg0 != null && arg0.length() > 0)
		{
			this.namespaceMap.put(arg0, arg1);
		}
	}

	public String getTargetNamespace() {
		return targetNamespace;
	}

	public void setTargetNamespace(String targetNamespace) {
		this.targetNamespace = targetNamespace;
	}

	public boolean hasMatch() {
		return hasMatch;
	}

	
}