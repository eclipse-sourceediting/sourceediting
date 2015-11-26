/*******************************************************************************
 * Copyright (c) 2005, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 *   Jens Lukowski/Innoopract - initial renaming/restructuring
 * 
 * /
 *******************************************************************************/
package org.eclipse.wst.css.ui.tests;

import junit.framework.TestCase;

import org.eclipse.wst.css.core.internal.provisional.contenttype.ContentTypeIdForCSS;
import org.eclipse.wst.css.core.text.ICSSPartitions;
import org.eclipse.wst.css.ui.StructuredTextViewerConfigurationCSS;
import org.eclipse.wst.css.ui.internal.text.hover.CSSColorHover;
import org.eclipse.wst.css.ui.views.contentoutline.CSSContentOutlineConfiguration;
import org.eclipse.wst.css.ui.views.properties.CSSPropertySheetConfiguration;
import org.eclipse.wst.sse.ui.internal.ExtendedConfigurationBuilder;

/**
 * Tests retrieving editor contributions for css content type
 */
public class TestEditorConfigurationCSS extends TestCase {
	public void testGetSourceViewerConfiguration() {
		Object o = ExtendedConfigurationBuilder.getInstance().getConfiguration(ExtendedConfigurationBuilder.SOURCEVIEWERCONFIGURATION, ContentTypeIdForCSS.ContentTypeID_CSS);
		assertNotNull("no source viewer configuration for " + ContentTypeIdForCSS.ContentTypeID_CSS, o);
		// check for over-qualified subclasses
		assertEquals("unexpected source viewer configuration for " + ContentTypeIdForCSS.ContentTypeID_CSS, o.getClass(), StructuredTextViewerConfigurationCSS.class);
	}

	public void testGetContentOutlineViewerConfiguration() {
		Object o = ExtendedConfigurationBuilder.getInstance().getConfiguration(ExtendedConfigurationBuilder.CONTENTOUTLINECONFIGURATION, ContentTypeIdForCSS.ContentTypeID_CSS);
		assertNotNull("no content outline configuration for " + ContentTypeIdForCSS.ContentTypeID_CSS, o);
		// check for over-qualified subclasses
		assertEquals("unexpected content outline configuration for " + ContentTypeIdForCSS.ContentTypeID_CSS, o.getClass(), CSSContentOutlineConfiguration.class);
	}

	public void testGetPropertySheetConfiguration() {
		Object o = ExtendedConfigurationBuilder.getInstance().getConfiguration(ExtendedConfigurationBuilder.PROPERTYSHEETCONFIGURATION, ContentTypeIdForCSS.ContentTypeID_CSS);
		assertNotNull("no property sheet configuration for " + ContentTypeIdForCSS.ContentTypeID_CSS, o);
		// check for over-qualified subclasses
		assertEquals("unexpected property sheet configuration for " + ContentTypeIdForCSS.ContentTypeID_CSS, o.getClass(), CSSPropertySheetConfiguration.class);
	}
	
	public void testGetDocumentationTextHover() {
		Object o = ExtendedConfigurationBuilder.getInstance().getConfiguration(ExtendedConfigurationBuilder.DOCUMENTATIONTEXTHOVER, ICSSPartitions.STYLE);
		assertEquals("unexpected documentation text hover for " + ICSSPartitions.STYLE, o.getClass(), CSSColorHover.class);
	}
}
