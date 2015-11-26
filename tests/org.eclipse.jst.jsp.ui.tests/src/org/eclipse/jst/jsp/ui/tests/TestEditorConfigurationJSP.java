/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 *   Jens Lukowski/Innoopract - initial renaming/restructuring
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.tests;

import junit.framework.TestCase;

import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;
import org.eclipse.jst.jsp.core.text.IJSPPartitions;
import org.eclipse.jst.jsp.ui.StructuredTextViewerConfigurationJSP;
import org.eclipse.jst.jsp.ui.internal.taginfo.JSPJavaJavadocHoverProcessor;
import org.eclipse.jst.jsp.ui.views.contentoutline.JSPContentOutlineConfiguration;
import org.eclipse.wst.sse.ui.internal.ExtendedConfigurationBuilder;
import org.eclipse.wst.xml.ui.views.properties.XMLPropertySheetConfiguration;

/**
 * Tests retrieving editor contributions for jsp content type
 */
public class TestEditorConfigurationJSP extends TestCase {
	public void testGetSourceViewerConfiguration() {
		Object o = ExtendedConfigurationBuilder.getInstance().getConfiguration(ExtendedConfigurationBuilder.SOURCEVIEWERCONFIGURATION, ContentTypeIdForJSP.ContentTypeID_JSP);
		assertNotNull("no source viewer configuration for " + ContentTypeIdForJSP.ContentTypeID_JSP, o);
		// check for over-qualified subclasses
		assertEquals("unexpected source viewer configuration for " + ContentTypeIdForJSP.ContentTypeID_JSP, o.getClass(), StructuredTextViewerConfigurationJSP.class);
	}

	public void testGetContentOutlineViewerConfiguration() {
		Object o = ExtendedConfigurationBuilder.getInstance().getConfiguration(ExtendedConfigurationBuilder.CONTENTOUTLINECONFIGURATION, ContentTypeIdForJSP.ContentTypeID_JSP);
		assertNotNull("no content outline viewer configuration for " + ContentTypeIdForJSP.ContentTypeID_JSP, o);
		// check for over-qualified subclasses
		assertEquals("unexpected content outline viewer configuration for " + ContentTypeIdForJSP.ContentTypeID_JSP, o.getClass(), JSPContentOutlineConfiguration.class);
	}

	public void testGetPropertySheetConfiguration() {
		Object o = ExtendedConfigurationBuilder.getInstance().getConfiguration(ExtendedConfigurationBuilder.PROPERTYSHEETCONFIGURATION, ContentTypeIdForJSP.ContentTypeID_JSP);
		assertNotNull("no property sheet configuration for " + ContentTypeIdForJSP.ContentTypeID_JSP, o);
		// check for over-qualified subclasses
		assertEquals("unexpected property sheet configuration for " + ContentTypeIdForJSP.ContentTypeID_JSP, o.getClass(), XMLPropertySheetConfiguration.class);
	}
	
	public void testGetDocumentationTextHover() {
		Object o = ExtendedConfigurationBuilder.getInstance().getConfiguration(ExtendedConfigurationBuilder.DOCUMENTATIONTEXTHOVER, IJSPPartitions.JSP_CONTENT_JAVA);
		assertNotNull("no documentation text hover processor for " + IJSPPartitions.JSP_CONTENT_JAVA, o);
		// check for over-qualified subclasses
		assertEquals("unexpected documentation text hover processor for " + IJSPPartitions.JSP_CONTENT_JAVA, o.getClass(), JSPJavaJavadocHoverProcessor.class);
	}
}
