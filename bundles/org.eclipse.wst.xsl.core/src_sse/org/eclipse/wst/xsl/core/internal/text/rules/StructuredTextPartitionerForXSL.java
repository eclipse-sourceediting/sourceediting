/*******************************************************************************
 * Copyright (c) 2008 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.xsl.core.internal.text.rules;

import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredTextPartitioner;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xml.core.internal.text.rules.StructuredTextPartitionerForXML;
import org.eclipse.wst.xml.core.text.IXMLPartitions;
import org.eclipse.wst.xsl.core.internal.text.IXSLPartitions;

/**
 * Contains information specific to setting up Structured Document Partions 
 * in XSL documents. 
 * @author dcarver
 *
 */
public class StructuredTextPartitionerForXSL extends StructuredTextPartitionerForXML implements IStructuredTextPartitioner {

	private final static String[] configuredContentTypes = new String[]{IXMLPartitions.XML_DEFAULT, IXMLPartitions.XML_CDATA, IXMLPartitions.XML_PI, IXMLPartitions.XML_DECLARATION, IXMLPartitions.XML_COMMENT, IXMLPartitions.DTD_SUBSET, IXSLPartitions.XSL_XPATH};

	/**
	 * The StructuredTextPartitionerForXSL adds the necessary
	 * Partition types to help Identify potential XPath areas.
	 * This is also used for Line Styling and Content Assistance.
	 */
	public StructuredTextPartitionerForXSL() {
		super();
	}

	@Override
	public String getPartitionType(ITextRegion region, int offset) {
		String result = null;
		if (region.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
					result = IXSLPartitions.XSL_XPATH;
		} else {
			result = super.getPartitionType(region, offset);
		}
		return result;
	}
	
	public static String[] getConfiguredContentTypes() {
		return configuredContentTypes.clone();
	}
	
	@Override
	public IDocumentPartitioner newInstance() {
		StructuredTextPartitionerForXSL instance = new StructuredTextPartitionerForXSL();
		return instance;
	}
	

	
}
