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
package org.eclipse.wst.css.ui.tests;

import junit.framework.TestCase;

import org.eclipse.wst.css.core.internal.provisional.contenttype.ContentTypeIdForCSS;
import org.eclipse.wst.css.ui.StructuredTextViewerConfigurationCSS;
import org.eclipse.wst.css.ui.views.contentoutline.CSSContentOutlineConfiguration;
import org.eclipse.wst.css.ui.views.properties.CSSPropertySheetConfiguration;
import org.eclipse.wst.sse.ui.internal.ExtendedConfigurationBuilder;

/**
 * Tests retrieving editor contributions for css content type
 */
public class TestEditorConfigurationCSS extends TestCase {
	public void testGetSourceViewerConfiguration() {
		Object o = ExtendedConfigurationBuilder.getInstance().getConfiguration(ExtendedConfigurationBuilder.SOURCEVIEWERCONFIGURATION, ContentTypeIdForCSS.ContentTypeID_CSS);
		assertTrue("unexpected source viewer configuration for " + ContentTypeIdForCSS.ContentTypeID_CSS, (o instanceof StructuredTextViewerConfigurationCSS));
	}

	public void testGetContentOutlineViewerConfiguration() {
		Object o = ExtendedConfigurationBuilder.getInstance().getConfiguration(ExtendedConfigurationBuilder.CONTENTOUTLINECONFIGURATION, ContentTypeIdForCSS.ContentTypeID_CSS);
		assertTrue("unexpected content outline viewer configuration for " + ContentTypeIdForCSS.ContentTypeID_CSS, (o instanceof CSSContentOutlineConfiguration));
	}
	
	public void testGetPropertySheetConfiguration() {
		Object o = ExtendedConfigurationBuilder.getInstance().getConfiguration(ExtendedConfigurationBuilder.PROPERTYSHEETCONFIGURATION, ContentTypeIdForCSS.ContentTypeID_CSS);
		assertTrue("unexpected property sheet viewer configuration for " + ContentTypeIdForCSS.ContentTypeID_CSS, (o instanceof CSSPropertySheetConfiguration));
	}
}
