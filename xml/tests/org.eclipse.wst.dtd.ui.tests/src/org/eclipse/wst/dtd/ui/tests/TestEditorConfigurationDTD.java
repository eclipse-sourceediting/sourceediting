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
package org.eclipse.wst.dtd.ui.tests;

import junit.framework.TestCase;

import org.eclipse.wst.dtd.core.internal.provisional.contenttype.ContentTypeIdForDTD;
import org.eclipse.wst.dtd.core.text.IDTDPartitions;
import org.eclipse.wst.dtd.ui.StructuredTextViewerConfigurationDTD;
import org.eclipse.wst.dtd.ui.views.contentoutline.DTDContentOutlineConfiguration;
import org.eclipse.wst.sse.ui.internal.ExtendedConfigurationBuilder;

/**
 * Tests retrieving editor contributions for dtd content type
 */
public class TestEditorConfigurationDTD extends TestCase {
	public void testGetSourceViewerConfiguration() {
		Object o = ExtendedConfigurationBuilder.getInstance().getConfiguration(ExtendedConfigurationBuilder.SOURCEVIEWERCONFIGURATION, ContentTypeIdForDTD.ContentTypeID_DTD);
		assertNotNull("no source viewer configuration for " + ContentTypeIdForDTD.ContentTypeID_DTD, o);
		// check for over-qualified subclasses
		assertEquals("unexpected source viewer configuration for " + ContentTypeIdForDTD.ContentTypeID_DTD, o.getClass(), StructuredTextViewerConfigurationDTD.class);
	}

	public void testGetContentOutlineViewerConfiguration() {
		Object o = ExtendedConfigurationBuilder.getInstance().getConfiguration(ExtendedConfigurationBuilder.CONTENTOUTLINECONFIGURATION, ContentTypeIdForDTD.ContentTypeID_DTD);
		assertNotNull("no content outline configuration for " + ContentTypeIdForDTD.ContentTypeID_DTD, o);
		// check for over-qualified subclasses
		assertEquals("unexpected content outline configuration for " + ContentTypeIdForDTD.ContentTypeID_DTD, o.getClass(), DTDContentOutlineConfiguration.class);
	}
	
	public void testGetDocumentationTextHover() {
		Object o = ExtendedConfigurationBuilder.getInstance().getConfiguration(ExtendedConfigurationBuilder.DOCUMENTATIONTEXTHOVER, IDTDPartitions.DTD_DEFAULT);
		assertNull("unexpected documentation text hover for " + IDTDPartitions.DTD_DEFAULT, o);
	}
}
