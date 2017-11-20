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
package org.eclipse.wst.xml.ui.tests;

import junit.framework.TestCase;

import org.eclipse.wst.sse.ui.internal.ExtendedConfigurationBuilder;
import org.eclipse.wst.xml.core.internal.provisional.contenttype.ContentTypeIdForXML;
import org.eclipse.wst.xml.core.text.IXMLPartitions;
import org.eclipse.wst.xml.ui.StructuredTextViewerConfigurationXML;
import org.eclipse.wst.xml.ui.internal.doubleclick.XMLDoubleClickStrategy;
import org.eclipse.wst.xml.ui.internal.taginfo.XMLTagInfoHoverProcessor;
import org.eclipse.wst.xml.ui.views.contentoutline.XMLContentOutlineConfiguration;
import org.eclipse.wst.xml.ui.views.properties.XMLPropertySheetConfiguration;

/**
 * Tests retrieving editor contributions for xml content type
 */
public class TestEditorConfigurationXML extends TestCase {
	public void testGetSourceViewerConfiguration() {
		Object o = ExtendedConfigurationBuilder.getInstance().getConfiguration(ExtendedConfigurationBuilder.SOURCEVIEWERCONFIGURATION, ContentTypeIdForXML.ContentTypeID_XML);
		assertNotNull("no source viewer configuration for " + ContentTypeIdForXML.ContentTypeID_XML, o);
		// check for over-qualified subclasses
		assertEquals("unexpected source viewer configuration for " + ContentTypeIdForXML.ContentTypeID_XML, StructuredTextViewerConfigurationXML.class, o.getClass());
	}

	public void testGetContentOutlineViewerConfiguration() {
		Object o = ExtendedConfigurationBuilder.getInstance().getConfiguration(ExtendedConfigurationBuilder.CONTENTOUTLINECONFIGURATION, ContentTypeIdForXML.ContentTypeID_XML);
		assertNotNull("no content outline viewer configuration for " + ContentTypeIdForXML.ContentTypeID_XML, o);
		// check for over-qualified subclasses
		assertEquals("unexpected content outline viewer configuration for " + ContentTypeIdForXML.ContentTypeID_XML, XMLContentOutlineConfiguration.class, o.getClass());
	}

	public void testGetPropertySheetConfiguration() {
		Object o = ExtendedConfigurationBuilder.getInstance().getConfiguration(ExtendedConfigurationBuilder.PROPERTYSHEETCONFIGURATION, ContentTypeIdForXML.ContentTypeID_XML);
		assertNotNull("no property sheet configuration for " + ContentTypeIdForXML.ContentTypeID_XML, o);
		// check for over-qualified subclasses
		assertEquals("unexpected property sheet configuration for " + ContentTypeIdForXML.ContentTypeID_XML, XMLPropertySheetConfiguration.class, o.getClass());
	}
	
	public void testGetDocumentationTextHover() {
		Object[] hovers = ExtendedConfigurationBuilder.getInstance().getConfigurations(ExtendedConfigurationBuilder.DOCUMENTATIONTEXTHOVER, IXMLPartitions.XML_DEFAULT).toArray();
		assertTrue("no documentation text hover for " + IXMLPartitions.XML_DEFAULT, hovers.length > 0);
//		// check for over-qualified subclasses
//		assertEquals("unexpected documentation text hover for " + IXMLPartitions.XML_DEFAULT, o.getClass(), XMLTagInfoHoverProcessor.class);
		Class required = XMLTagInfoHoverProcessor.class;
		boolean requiredFound = false;
		for (int i = 0; i < hovers.length; i++) {
			if(required.equals(hovers[i].getClass()))
				requiredFound = true;
		}
		assertTrue(required.getName() + " not loaded", requiredFound);
	}
	
	public void testGetDoubleClickStrategy() {
		Object o = ExtendedConfigurationBuilder.getInstance().getConfiguration(ExtendedConfigurationBuilder.DOUBLECLICKSTRATEGY, IXMLPartitions.XML_DEFAULT);
		assertNotNull("no doubleclick strategy for " + IXMLPartitions.XML_DEFAULT, o);
		// check for over-qualified subclasses
		assertEquals("unexpected doubleclick strategy for " + IXMLPartitions.XML_DEFAULT, XMLDoubleClickStrategy.class, o.getClass());
	}
}
