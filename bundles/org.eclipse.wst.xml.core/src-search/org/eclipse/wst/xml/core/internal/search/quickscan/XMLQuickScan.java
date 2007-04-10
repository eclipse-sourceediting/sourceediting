/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.search.quickscan;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.eclipse.wst.common.core.search.document.SearchDocument;
import org.eclipse.wst.common.core.search.pattern.SearchPattern;
import org.eclipse.wst.xml.core.internal.search.matching.PatternMatcher;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * 
 */
public class XMLQuickScan
{
    /*
	public static String getTargetNamespace(String fullFilePath)
	{
		XMLQuickScanContentHandler handler = new XMLQuickScanContentHandler();
		parseFile(fullFilePath, handler);
		return handler.getTargetNamespace();
	}*/
	
	/*
	 * Returns information about matches encountered based on the criteria
	 * provided.
	 *
	public static boolean hasMatch(String fullFilePath, PatternMatcher matcher, SearchPattern pattern)
	{
		XMLQuickScanContentHandler handler = new XMLQuickScanContentHandler(matcher, pattern);
		parseFile(fullFilePath, handler);
		return handler.hasMatch();
	}*/
	
	public static boolean populateSearchDocument(SearchDocument document, PatternMatcher matcher, SearchPattern pattern)
	{
		XMLQuickScanContentHandler handler = new XMLQuickScanContentHandler(document, matcher, pattern);
		parseFile(document.getPath(), handler);
		return handler.hasMatch();
	}

    private static XMLReader reader;
    private static XMLReader getOrCreateReader()
    {
       if (reader == null)
       {
         try
         {
          SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
          reader = parser.getXMLReader();  
          reader.setFeature("http://xml.org/sax/features/namespaces", true); //$NON-NLS-1$
          reader.setErrorHandler(new InternalErrorHandler());          
         }
         catch (Exception e)
         {           
         }
       } 
       return reader;
    }
    
    static class InternalErrorHandler implements ErrorHandler
    {
      public void error(SAXParseException exception) throws SAXException
      {          
      }
      
      public void fatalError(SAXParseException exception) throws SAXException
      {
      }
      public void warning(SAXParseException exception) throws SAXException
      {        
      }
    }
    
	private synchronized static void parseFile(String fullFilePath,
			XMLQuickScanContentHandler handler)
	{
		FileInputStream inputStream = null;
		try
		{            
			inputStream = new FileInputStream(new File(fullFilePath));
			XMLReader reader = getOrCreateReader();
            reader.setContentHandler(handler);
			//System.out.println("parseFile" + reader + " (" +  fullFilePath + ")");			
			reader.parse(new InputSource(inputStream));
		} catch (Exception e)
		{
			// skip the file
		} 
		finally{
			if(inputStream != null){
				try {
					inputStream.close();
				} catch (IOException e) {
					// can not do much 
				}
			}
			
		}
	}
}
