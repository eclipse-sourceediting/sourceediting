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
package org.eclipse.jst.jsp.ui.tests.contentdescription;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jst.jsp.core.internal.Logger;
import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;
import org.eclipse.jst.jsp.core.internal.provisional.contenttype.IContentDescriptionForJSP;
import org.eclipse.jst.jsp.ui.tests.document.UnzippedProjectTester;

/**
 * Tests that our ContentDescriberForJSP is working as expected,
 * We check that the content description for a given input stream has all
 * of the appropriate properties.
 * (through the Platform content type framework)
 */
public class TestContentDescription extends UnzippedProjectTester {

	public void testNoXMLPIWithJSPSyntax() {
		HashMap expectedProperties = new HashMap();
		expectedProperties.put(IContentDescriptionForJSP.CONTENT_TYPE_ATTRIBUTE, "text/xml");
		expectedProperties.put(IContentDescription.CHARSET, "UTF-8");
		doTestContentDescription("/content-description/PurchaseOrder-no-XMLPI-jsp-syntax.jsp", expectedProperties);
	}
	
	public void testWithXMLPIWithJSPSyntax() {
		HashMap expectedProperties = new HashMap();
		expectedProperties.put(IContentDescriptionForJSP.CONTENT_TYPE_ATTRIBUTE, "text/xml");
		expectedProperties.put(IContentDescription.CHARSET, "UTF-8");
		doTestContentDescription("/content-description/PurchaseOrder-with-XMLPI-jsp-syntax.jsp", expectedProperties);
	}
	
	public void testWithXMLPIWithXMLSyntax() {
		HashMap expectedProperties = new HashMap();
		expectedProperties.put(IContentDescriptionForJSP.CONTENT_TYPE_ATTRIBUTE, "text/xml");
		expectedProperties.put(IContentDescription.CHARSET, "UTF-8");
		doTestContentDescription("/content-description/PurchaseOrder-with-XMLPI-xml-syntax.jsp", expectedProperties);
	}
	
	public void testJSPWithHTMLOutput() {
		HashMap expectedProperties = new HashMap();
		expectedProperties.put(IContentDescriptionForJSP.CONTENT_TYPE_ATTRIBUTE, "text/html");
		doTestContentDescription("/content-description/html.jsp", expectedProperties);
	}
	
	public void testJSPWithXHTMLOutput() {
		HashMap expectedProperties = new HashMap();
		expectedProperties.put(IContentDescriptionForJSP.CONTENT_TYPE_ATTRIBUTE, "text/xhtml");
		doTestContentDescription("/content-description/xhtml.jsp", expectedProperties);
	}
	
	public void testJSPWithXMLOutput() {
		HashMap expectedProperties = new HashMap();
		expectedProperties.put(IContentDescriptionForJSP.CONTENT_TYPE_ATTRIBUTE, "text/xml");
		doTestContentDescription("/content-description/xml.jsp", expectedProperties);
	}
	
	private void doTestContentDescription(String filePath, HashMap expectedProperties) {
		IContentDescription desc = getContentDescription(filePath);
		assertNotNull("couldn't get IContentDescription for file:[" + filePath + "]", desc);
		Object[] keys = expectedProperties.keySet().toArray();
		for (int i = 0; i < keys.length; i++) {
			
			Object expected = expectedProperties.get(keys[i]);
			Object detected = desc.getProperty( (QualifiedName)keys[i] );
			assertEquals("unexpected property value", expected, detected);
		}
	}
	
	private IContentDescription getContentDescription(String filePath) {
		if(filePath == null)
			return null;
	
		InputStream in = null;
		try {
			// workspace file
			IFile wsFile = FileBuffers.getWorkspaceFileAtLocation(new Path(filePath));
			if(wsFile != null && wsFile.exists()) {
				in = wsFile.getContents();
			}
			else {
				// external file
				File sFile = FileBuffers.getSystemFileAtLocation(new Path(filePath));
				if(sFile !=null && sFile.exists())
					in = new FileInputStream(sFile);
			}
		}
		catch (CoreException e) {
			Logger.logException(e);
		}
		catch (FileNotFoundException e) {
			Logger.logException(e);
		}
		
		assertNotNull(in);
		
		return getContentDescription(in);
	}
	
	/**
	 * Returns content description for an input stream
	 * Assumes it's JSP content.
	 * Closes the input stream when finished.
	 * 
	 * @param in
	 * @return the IContentDescription for in
	 */
	private IContentDescription getContentDescription(InputStream in) {
		
		if(in == null)
			return null;
		
		IContentDescription desc = null;
		try {
			
			IContentType contentTypeJSP = Platform.getContentTypeManager().getContentType(ContentTypeIdForJSP.ContentTypeID_JSP);
			desc = contentTypeJSP.getDescriptionFor(in, IContentDescription.ALL);
		}
		catch (IOException e) {
			Logger.logException(e);
		}
		finally {
			if(in != null) {
				try {
					in.close();
				}
				catch (IOException e) {
					Logger.logException(e);
				}
			}
		}
		return desc;
	}
	
}
