/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.dialogs.types.xml;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerFactory;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 */
public class XMLQuickScan {
  
	/*
	 * Returns information about matches encountered
     * based on the criteria provided. 
	 */
  public static List getTagInfo(String fullFilePath, List paths, List attributes) {
    XSDGlobalElementTypeContentHandler handler = new XSDGlobalElementTypeContentHandler();
    handler.stringTagPaths = paths;
    handler.searchAttributes = attributes;
    handler.fileLocation = fullFilePath;

    ClassLoader prevClassLoader = Thread.currentThread().getContextClassLoader();
    
    try {
        IPath path = new Path(fullFilePath);
    	FileInputStream inputStream = new FileInputStream(new File(path.toOSString()));
    	
        
//      SAXParser  sparser = SAXParserFactory.newInstance().newSAXParser();
//      XMLReader reader = sparser.getXMLReader();
        
        // Line below is a hack to get XMLReader working
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        
    	XMLReader reader = org.xml.sax.helpers.XMLReaderFactory.createXMLReader();
    	reader.setContentHandler(handler);
    	reader.parse(new InputSource(inputStream));
    }
    catch (Exception e) {
    	e.printStackTrace();
    }
    finally {
      Thread.currentThread().setContextClassLoader(prevClassLoader);
    }
    return handler.getSearchAttributeValues();
  }
  
  public static class XSDGlobalElementTypeContentHandler extends DefaultHandler {
  	protected List stringTagPaths;
  	protected List searchAttributes;
  	private List matchingTags = new ArrayList();
  	private String targetNamespace = "";
  	private String fileLocation;

  	StringBuffer currentPath = new StringBuffer();
  	
    public void startElement(String uri, String localName, String qName, Attributes attributes)  throws SAXException {
    	currentPath.append("/" + localName);
    	
    	// Search for targetNamespace if we haven't encountered it yet.
    	if (targetNamespace.equals("")) {
	        int nAttributes = attributes.getLength();
	        for (int i = 0; i < nAttributes; i++)
	        {
	          if (attributes.getLocalName(i).equals("targetNamespace"))
	          {
	            targetNamespace = attributes.getValue(i);
	            break;
	          }
	        }
    	}
    	
    	// Search for the path
    	for (int index = 0; index < stringTagPaths.size(); index++) {
    		String path = (String) stringTagPaths.get(index);
    		if (currentPath.length() == path.length() && currentPath.toString().equals(path)) {
    			// Found a path match
    			createTagInfo(attributes, (String[]) searchAttributes.get(index));
    		}
    	}
    }
    
    public void endElement(String uri, String localName, String qName) throws SAXException {
    	int slashIndex = currentPath.lastIndexOf("/");
    	currentPath.delete(slashIndex, currentPath.length());
    }
    
    /*
     * Information about a tag is stored in a TagInfo class.
     */
    private void createTagInfo(Attributes attributes, String[] attributesToSearch) {
    	XMLComponentSpecification spec = new XMLComponentSpecification(currentPath.toString());
    	
//    	tagInfo.addAttributeInfo("name", attributes.getValue("name"));
    	for (int index = 0; index < attributesToSearch.length; index++) {
    		String attrString = (String) attributesToSearch[index];
    		String value = attributes.getValue(attrString);
    		if (value != null) {
    			spec.addAttributeInfo(attrString, value);
    		}
    	}
    	spec.setTargetNamespace(targetNamespace);
    	spec.setFileLocation(fileLocation);
    	matchingTags.add(spec);
    }
    
    private int getAttributeNameIndex(String attrName) {
    	for (int index = 0; index < searchAttributes.size(); index++) {
    		if (searchAttributes.get(index).equals(attrName)) {
    			return index;
    		}
    	}
    	
    	// Not found.  We are not looking for this Attribute Name
    	return -1;
    }
        
    public List getSearchAttributeValues() {
    	return matchingTags;
    }
  }
}