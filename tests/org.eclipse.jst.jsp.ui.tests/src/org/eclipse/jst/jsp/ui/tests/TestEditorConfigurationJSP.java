/*
 * Copyright (c) 2005 IBM Corporation and others.
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
package org.eclipse.jst.jsp.ui.tests;

import junit.framework.TestCase;

import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;
import org.eclipse.jst.jsp.ui.internal.provisional.StructuredTextViewerConfigurationJSP;
import org.eclipse.jst.jsp.ui.internal.views.contentoutline.JSPContentOutlineConfiguration;
import org.eclipse.wst.sse.ui.internal.ExtendedConfigurationBuilder;
import org.eclipse.wst.xml.ui.views.properties.XMLPropertySheetConfiguration;

/**
 * Tests retrieving editor contributions for jsp content type
 */
public class TestEditorConfigurationJSP extends TestCase {
	public void testGetSourceViewerConfiguration() {
		Object o = ExtendedConfigurationBuilder.getInstance().getConfiguration(ExtendedConfigurationBuilder.SOURCEVIEWERCONFIGURATION, ContentTypeIdForJSP.ContentTypeID_JSP);
		assertTrue("unexpected source viewer configuration for " + ContentTypeIdForJSP.ContentTypeID_JSP, (o instanceof StructuredTextViewerConfigurationJSP));
	}

	public void testGetContentOutlineViewerConfiguration() {
		Object o = ExtendedConfigurationBuilder.getInstance().getConfiguration(ExtendedConfigurationBuilder.CONTENTOUTLINECONFIGURATION, ContentTypeIdForJSP.ContentTypeID_JSP);
		assertTrue("unexpected content outline viewer configuration for " + ContentTypeIdForJSP.ContentTypeID_JSP, (o instanceof JSPContentOutlineConfiguration));
	}

	public void testGetPropertySheetConfiguration() {
		Object o = ExtendedConfigurationBuilder.getInstance().getConfiguration(ExtendedConfigurationBuilder.PROPERTYSHEETCONFIGURATION, ContentTypeIdForJSP.ContentTypeID_JSP);
		assertTrue("unexpected property sheet viewer configuration for " + ContentTypeIdForJSP.ContentTypeID_JSP, (o instanceof XMLPropertySheetConfiguration));
	}
}
