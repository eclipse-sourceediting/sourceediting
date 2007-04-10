/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.text.rules;

import java.util.Locale;

import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.wst.sse.core.internal.parser.ForeignRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredTextPartitioner;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.text.rules.StructuredTextPartitioner;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xml.core.text.IXMLPartitions;

public class StructuredTextPartitionerForXML extends StructuredTextPartitioner implements IStructuredTextPartitioner {

	private final static String[] configuredContentTypes = new String[]{IXMLPartitions.XML_DEFAULT, IXMLPartitions.XML_CDATA, IXMLPartitions.XML_PI, IXMLPartitions.XML_DECLARATION, IXMLPartitions.XML_COMMENT, IXMLPartitions.DTD_SUBSET};

	public StructuredTextPartitionerForXML() {
		super();
	}

	protected void setInternalPartition(int offset, int length, String type) {
		super.setInternalPartition(offset, length, type);
	}

	protected void initLegalContentTypes() {
		fSupportedTypes = configuredContentTypes;
	}

	public String getPartitionType(ITextRegion region, int offset) {
		String result = null;
		if (region.getType() == DOMRegionContext.XML_COMMENT_TEXT)
			result = IXMLPartitions.XML_COMMENT;
		else if (region.getType() == DOMRegionContext.XML_CDATA_TEXT)
			result = IXMLPartitions.XML_CDATA;
		else if (region.getType() == DOMRegionContext.XML_PI_CONTENT) {
			/**
			 * Grammatically, it's impossible to get a PI_CONTENT region
			 * without a preceding XML_TAG_NAME region. Relying on this,
			 * extract the target processor name and create a partition type
			 * dynamically.
			 */
			IStructuredDocumentRegion docRegion = fStructuredDocument.getRegionAtCharacterOffset(offset);
			ITextRegion name = docRegion.getRegionAtCharacterOffset(docRegion.getStartOffset() + region.getStart() - 1);
			result = IXMLPartitions.PROCESSING_INSTRUCTION_PREFIX + docRegion.getText(name).toUpperCase(Locale.ENGLISH);
		}
		else if (region.getType() == DOMRegionContext.XML_PI_OPEN)
			result = IXMLPartitions.XML_PI;
		else if (region.getType() == DOMRegionContext.XML_DOCTYPE_DECLARATION)
			result = IXMLPartitions.XML_DECLARATION;
		else if (region.getType() == DOMRegionContext.XML_DOCTYPE_INTERNAL_SUBSET)
			result = IXMLPartitions.DTD_SUBSET;
		else
			result = super.getPartitionType(region, offset);
		return result;
	}

	protected String getPartitionType(ForeignRegion region, int offset) {
		// temp added just to dis-ambiguate call from subclass
		return super.getPartitionType(region, offset);
	}

	public String getPartitionTypeBetween(IStructuredDocumentRegion previousNode, IStructuredDocumentRegion nextNode) {
		return super.getPartitionTypeBetween(previousNode, nextNode);
	}

	public String getDefaultPartitionType() {
		return IXMLPartitions.XML_DEFAULT;
	}

	public IDocumentPartitioner newInstance() {
		StructuredTextPartitionerForXML instance = new StructuredTextPartitionerForXML();
		return instance;
	}

	/**
	 * @return
	 */
	public static String[] getConfiguredContentTypes() {
		return configuredContentTypes;
	}

	protected String getPartitionFromBlockedText(ITextRegion region, int offset, String result) {
		// was moved to subclass for quick transition
		String newResult = result;
		// nsd_TODO: David and I need to discuss, design, and implement this
		// for all block tags and comments
		// and make sure is part of "extensible" design of block tags
		if (region.getType() == DOMRegionContext.BLOCK_TEXT) {
			// for code safety, we'll always check instanceof, but I think
			// always true.
			if (region instanceof ForeignRegion) {
				// super is used below so won't be ambiguous
				newResult = getPartitionType((ForeignRegion) region, offset);
			}
			else if (region instanceof ForeignRegion) {
				newResult = getPartitionType((ForeignRegion) region, offset);
			}
			else {
				newResult = getUnknown();
			}
		}
		return newResult;
	}

	protected boolean doParserSpecificCheck(int offset, boolean partitionFound, IStructuredDocumentRegion sdRegion, IStructuredDocumentRegion previousStructuredDocumentRegion, ITextRegion next, ITextRegion previousStart) {
		// this was moved down to subclass of StructuredTextPartioner
		// for quick fix to transition problems. Heirarchy needs lots of
		// cleanup.
		if (previousStart != null && previousStart.getType() == DOMRegionContext.XML_TAG_OPEN && next.getType() == DOMRegionContext.XML_END_TAG_OPEN) {
			ITextRegion previousName = previousStructuredDocumentRegion.getRegionAtCharacterOffset(previousStructuredDocumentRegion.getEndOffset(previousStart));
			ITextRegion nextName = sdRegion.getRegionAtCharacterOffset(sdRegion.getEndOffset(next));
			if (previousName != null && nextName != null && previousName.getType() == DOMRegionContext.XML_TAG_NAME && nextName.getType() == DOMRegionContext.XML_TAG_NAME) {
				setInternalPartition(offset, 0, getPartitionTypeBetween(previousStructuredDocumentRegion, sdRegion));
				partitionFound = true;
			}
		}
		return partitionFound;
	}
}
