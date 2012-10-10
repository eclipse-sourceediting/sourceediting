/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.html.core.tests.contenttype;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;

import org.eclipse.wst.html.core.internal.contenttype.HTMLResourceEncodingDetector;

import junit.framework.TestCase;
/**
 * 
 * Create due to bug: https://bugs.eclipse.org/bugs/show_bug.cgi?id=371430
 *
 */
public class TestHTMLResourceEncodingDetector extends TestCase {

	HTMLResourceEncodingDetector htmlResouceEncodingDetector;
	private static final String XMLDeclContent_UTF8= "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
	private static final String XMLDeclContent_ISO= "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>";
	private static final String XHTMLContent_HTML_AS_ROOTELEMENT = "<html xmlns=\"http://www.w3.org/1999/xhtml\">";
	private static final String XHTMLContent_NOT_HTML_AS_ROOTELEMENT= "<ui:composition attribut=43 xmlns=\"http://www.w3.org/1999/xhtml\">";
	private static final String METAContent = "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\" />";
	
	
	public TestHTMLResourceEncodingDetector() {
		this("HTML Resource Encoding Detector");
	}

	public TestHTMLResourceEncodingDetector(String string) {
		super(string);
	}

	
	protected void setUp() throws Exception {
		htmlResouceEncodingDetector = new HTMLResourceEncodingDetector();
	}
	
	
	private String getContentType(String content) throws IOException{
		Reader contentReader = new CharArrayReader(content.toCharArray());
		htmlResouceEncodingDetector.set(contentReader);
		return htmlResouceEncodingDetector.getEncoding();
	}
	
	
	public void testXMLDecl() throws IOException{
		String contentType=getContentType(XMLDeclContent_UTF8+XHTMLContent_HTML_AS_ROOTELEMENT+METAContent);		
		assertEquals("UTF-8", contentType);
		
		contentType=getContentType(XMLDeclContent_ISO+XHTMLContent_HTML_AS_ROOTELEMENT+METAContent);	
		assertEquals("ISO-8859-1", contentType);
		
		contentType=getContentType(XMLDeclContent_UTF8+XHTMLContent_NOT_HTML_AS_ROOTELEMENT+METAContent);		
		assertEquals("UTF-8", contentType);
		
		contentType=getContentType (XMLDeclContent_ISO + XHTMLContent_NOT_HTML_AS_ROOTELEMENT);
		assertEquals("ISO-8859-1", contentType);
	}
	
	public void testMetaDecl() throws IOException{
		String contentType = getContentType(XHTMLContent_HTML_AS_ROOTELEMENT+METAContent);
		assertEquals("ISO-8859-1", contentType);
		
		contentType = getContentType(XHTMLContent_NOT_HTML_AS_ROOTELEMENT+METAContent);
		assertEquals("ISO-8859-1", contentType);
	}

	
	public void testXHTML() throws IOException{
		String contentType = getContentType(XHTMLContent_HTML_AS_ROOTELEMENT);
		assertEquals("UTF-8", contentType);
		
		contentType = getContentType(XHTMLContent_NOT_HTML_AS_ROOTELEMENT);
		assertEquals("UTF-8", contentType);
	}		
}
