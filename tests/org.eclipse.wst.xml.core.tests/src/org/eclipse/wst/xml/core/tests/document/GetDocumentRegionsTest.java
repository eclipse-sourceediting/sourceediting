/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.core.tests.document;

import junit.framework.TestCase;

import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.xml.core.internal.provisional.contenttype.ContentTypeIdForXML;

public class GetDocumentRegionsTest extends TestCase {
	
	private boolean fAlreadySetup = false;
	private IStructuredDocument fDoc = null;
	private String fText = null;
	
	protected void setUp() throws Exception {
		super.setUp();
		if(!fAlreadySetup) {
			IStructuredModel sModel = createModel(ContentTypeIdForXML.ContentTypeID_XML);
			fDoc = sModel.getStructuredDocument();
			fText = "<html><head></head><body> text </body></html>";
			fDoc.set(fText);
			fAlreadySetup = true;
		}
	}
	
	public void testGetAllRegions() {

		// all regions
		IStructuredDocumentRegion[] regions = fDoc.getStructuredDocumentRegions();
		checkRegions(regions, 7, "<html>", "</html>");
	}

	public void testExcludeEnds() {
		
		// leave off the end regions
		// <html>[<head><body> text </body>]</html>
		IStructuredDocumentRegion[] regions = fDoc.getStructuredDocumentRegions(6,fText.length() - (6+7));
		checkRegions(regions, 5, "<head>", "</body>");
	}

	public void testIncludeEnds() {
		
		// include end regions
		// <ht[ml><head></head><body> text </body></ht]ml>
		IStructuredDocumentRegion[] regions = fDoc.getStructuredDocumentRegions(3,fText.length()- (3+3));
		assertEquals(7, regions.length);
	}

	public void testFirstTwo() {
		// first 2 regions
		// <h[tml><h]ead></head>
		IStructuredDocumentRegion[] regions = fDoc.getStructuredDocumentRegions(2,6);
		checkRegions(regions, 2, "<html>", "<head>");
	}

	public void testRightSeam() {
		// right seam
		// [<html><head>]</head>
		IStructuredDocumentRegion[] regions = fDoc.getStructuredDocumentRegions(0,12);
		checkRegions(regions, 2, "<html>", "<head>");
	}

	public void testLeftSeam() {
		// left seam
		// </body>[</html>]
		IStructuredDocumentRegion[] regions = fDoc.getStructuredDocumentRegions(fText.length()-7, 7);
		checkRegions(regions, 1, "</html>", "</html>");
	}

	public void testGetLast() {
		// last region only
		// </body></html>|
		IStructuredDocumentRegion[] regions = fDoc.getStructuredDocumentRegions(fText.length(), 0);
		checkRegions(regions, 1, "</html>", "</html>");
	}

	public void testGetFirst() {
		// first region only
		// |<html><head>
		IStructuredDocumentRegion[] regions = fDoc.getStructuredDocumentRegions(0,0);
		checkRegions(regions, 1, "<html>", "<html>");
	}

	private void checkRegions(IStructuredDocumentRegion[] regions, int expectedLength, String firstText, String lastText) {
		
		//for (int i = 0; i < regions.length; i++) 
		//	System.out.println(regions[i].getText());
		
		assertEquals(expectedLength, regions.length);
		
		String text = regions[0].getText();
		assertEquals(text, firstText);
		
		text = regions[regions.length-1].getText();
		assertEquals(text, lastText);
	}
	
	private IStructuredModel createModel(String contentTypeID) {
		// create an empty model with its default factories
		IStructuredModel model = StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(contentTypeID);
		return model;
	}
}
