/*
* Copyright (c) 2002 IBM Corporation and others.
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
package org.eclipse.wst.sse.core.text.rules;



import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.wst.sse.core.events.StructuredDocumentRegionsReplacedEvent;
import org.eclipse.wst.sse.core.internal.parser.ForeignRegion;
import org.eclipse.wst.sse.core.parser.IBlockedStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.core.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.text.ITextRegionList;
import org.eclipse.wst.sse.core.text.SimpleStructuredTypedRegion;
import org.eclipse.wst.sse.core.text.StructuredRegion;
import org.eclipse.wst.sse.core.text.StructuredTypedRegion;


/**
 * Base Document partitioner for StructuredDocuments.  BLOCK_TEXT ITextRegions
 * have a partition type of BLOCK or BLOCK:TAGNAME if a surrounding tagname was 
 * recorded.
 */
public class StructuredTextPartitioner implements IDocumentPartitioner {

	public final static String ST_DEFAULT_PARTITION = "org.eclipse.wst.sse.ui.ST_DEFAULT"; //$NON-NLS-1$
	public final static String ST_UNKNOWN_PARTITION = "org.eclipse.wst.sse.UNKNOWN_PARTITION_TYPE"; //$NON-NLS-1$
	protected String[] fSupportedTypes = null;
	protected IStructuredDocument structuredDocument;
	protected StructuredTypedRegion internalReusedTempInstance = new SimpleStructuredTypedRegion(0, 0, ST_DEFAULT_PARTITION);
	private CachedComputedPartitions cachedPartitions = new CachedComputedPartitions(-1, -1, null);

	class CachedComputedPartitions {
		int fOffset;
		int fLength;
		ITypedRegion[] fPartitions;
		boolean isInValid;

		CachedComputedPartitions(int offset, int length, ITypedRegion[] partitions) {
			fOffset = offset;
			fLength = length;
			fPartitions = partitions;
			isInValid = true;
		}
	}

	/**
	 * StructuredTextPartitioner constructor comment.
	 */
	public StructuredTextPartitioner() {
		super();
	}

	/**
	 * Returns the partitioning of the given range of the connected
	 * document. There must be a document connected to this partitioner.
	 *
	 * @param offset the offset of the range of interest
	 * @param length the length of the range of interest
	 * @return the partitioning of the range
	 */
	synchronized public ITypedRegion[] computePartitioning(int offset, int length) {
		if (structuredDocument == null) {
			throw new IllegalStateException("document partitioner is not connected"); //$NON-NLS-1$
		}
		ITypedRegion[] results = null;
		if ((!cachedPartitions.isInValid) && (offset == cachedPartitions.fOffset) && (length == cachedPartitions.fLength)) {
			results = cachedPartitions.fPartitions;
		}
		else {

			List list = new ArrayList();
			if (length == 0) {
				list.add(getPartition(offset));
			}
			else {
				int currentPos = offset;
				int endPos = offset + length;
				if (endPos > structuredDocument.getLength()) {
					// this can occur if the model instance is being changed and everyone's
					// not yet up to date
					return new ITypedRegion[]{createPartition(offset, length, getUnknown())};
				}
				StructuredTypedRegion previousPartition = null;
				while (currentPos < endPos) {
					internalGetPartition(currentPos, false);
					currentPos += internalReusedTempInstance.getLength();
					// check if this partition just continues last one (type is the same),
					// if so, just extend length of last one, not need to create new 
					// instance.
					if (previousPartition != null && internalReusedTempInstance.getType().equals(previousPartition.getType())) {
						// same partition type
						previousPartition.setLength(previousPartition.getLength() + internalReusedTempInstance.getLength());
					}
					else {
						// not the same, so add to list
						StructuredTypedRegion partition = createNewPartitionInstance();
						list.add(partition);
						// and make current, previous
						previousPartition = partition;
					}
				}
			}
			results = new ITypedRegion[list.size()];
			list.toArray(results);
			if (results.length > 0) {
				// truncate returned results to requested range
				if (results[0].getOffset() < offset && results[0] instanceof StructuredRegion) {
					((StructuredRegion) results[0]).setOffset(offset);
				}
				int lastEnd = results[results.length - 1].getOffset() + results[results.length - 1].getLength();
				if (lastEnd > offset + length && results[results.length - 1] instanceof StructuredRegion) {
					((StructuredRegion) results[results.length - 1]).setLength(offset + length - results[results.length - 1].getOffset());
				}
			}
			cachedPartitions.fLength = length;
			cachedPartitions.fOffset = offset;
			cachedPartitions.fPartitions = results;
			cachedPartitions.isInValid = false;
		}
		return results;
	}

	/**
	 * Connects the document to the partitioner, i.e. indicates the
	 * begin of the usage of the receiver as partitioner of the
	 * given document.
	 */
	public void connect(IDocument document) {
		if (document instanceof IStructuredDocument) {
			this.structuredDocument = (IStructuredDocument) document;
		}
		else {
			throw new IllegalArgumentException("This class and API are for StructuredDocuments only"); //$NON-NLS-1$
		}
	}

	/**
	 * Creates the concrete partition from the given values.  Returns a new
	 * instance for each call.
	 * 
	 * Subclasses may override.
	 * 
	 * @param offset
	 * @param length
	 * @param type
	 * @return ITypedRegion
	 */
	public StructuredTypedRegion createPartition(int offset, int length, String type) {
		return new SimpleStructuredTypedRegion(offset, length, type);
	}

	protected void setInternalPartition(int offset, int length, String type) {
		internalReusedTempInstance.setOffset(offset);
		internalReusedTempInstance.setLength(length);
		internalReusedTempInstance.setType(type);
	}

	/** 
	 * Disconnects the document from the partitioner, i.e. indicates the end of
	 * the usage of the receiver as partitioner of the given document.
	 *
	 * @see org.eclipse.jface.text.IDocumentPartitioner#disconnect()
	 */
	public void disconnect() {
		this.structuredDocument = null;
	}

	/**
	 * Informs about a forthcoming document change.
	 * 
	 * @see org.eclipse.jface.text.IDocumentPartitioner#documentAboutToBeChanged(DocumentEvent)
	 */
	public void documentAboutToBeChanged(DocumentEvent event) {
		cachedPartitions.isInValid = true;
	}

	/**
	 * The document has been changed. The partitioner updates 
	 * the set of regions and returns whether the structure of the
	 * document partitioning has been changed, i.e. whether partitions
	 * have been added or removed.
	 * 
	 * @see org.eclipse.jface.text.IDocumentPartitioner#documentChanged(DocumentEvent)
	 */
	public boolean documentChanged(DocumentEvent event) {
		boolean result = false;
		if (event instanceof StructuredDocumentRegionsReplacedEvent) {
			// partitions don't always change with document regions do, 
			// but that's the only "quick check" we have. 
			// I'm not sure if something more sophisticated will be needed 
			// in the future. (dmw, 02/18/04).
			result = true;
		}
		return result;
	}

	/**
	 * Returns the content type of the partition containing the
	 * given character position of the given document. The document
	 * has previously been connected to the partitioner.
	 * 
	 * @see org.eclipse.jface.text.IDocumentPartitioner#getContentType(int)
	 */
	public String getContentType(int offset) {
		return getPartition(offset).getType();
	}

	/**
	 * Returns the set of all possible content types the partitoner supports.
	 * I.e. Any result delivered by this partitioner may not contain a content type
	 * which would not be included in this method's result.
	 * 
	 * @see org.eclipse.jface.text.IDocumentPartitioner#getLegalContentTypes()
	 */
	public java.lang.String[] getLegalContentTypes() {
		if (fSupportedTypes == null) {
			initLegalContentTypes();
		}
		return fSupportedTypes;
	}

	/**
	 * to be abstract eventually 
	 */
	protected void initLegalContentTypes() {
		fSupportedTypes = new String[]{ST_DEFAULT_PARTITION, ST_UNKNOWN_PARTITION};
	}

	/**
	 * Returns the partition containing the given character position of
	 * the given document. The document has previously been connected to the
	 * partitioner.
	 * 
	 * @see org.eclipse.jface.text.IDocumentPartitioner#getPartition(int)
	 */
	public ITypedRegion getPartition(int offset) {
		internalGetPartition(offset, true);
		return createNewPartitionInstance();
	}

	private StructuredTypedRegion createNewPartitionInstance() {
		return new SimpleStructuredTypedRegion(internalReusedTempInstance.getOffset(), internalReusedTempInstance.getLength(), internalReusedTempInstance.getType());
	}

	/**
	 * Returns the partition containing the given character position of
	 * the given document. The document has previously been connected to the
	 * partitioner.  If the checkBetween parameter is true, an offset between a
	 * start and end tag will return a zero-length region.
	 */
	synchronized private void internalGetPartition(int offset, boolean checkBetween) {
		if (structuredDocument == null) {
			throw new IllegalStateException("document partitioner is not connected"); //$NON-NLS-1$
		}

		boolean partitionFound = false;
		// get flatnode type and map to partition type :
		// Note: a partion can be smaller than a flatnode, if that flatnode
		// contains a region container.
		// That's why we need to get "relevent region".  
		IStructuredDocumentRegion structuredDocumentRegion = structuredDocument.getRegionAtCharacterOffset(offset);
		// flatNode is null if empty document
		// this is king of a "normal case" for empty document
		if (structuredDocumentRegion == null && structuredDocument.getLength() == 0) {
			// in order to prevent infinite error loops, this partition must never have a zero length
			setInternalPartition(offset, 0, getDefault());
			partitionFound = true;
		}
		else if (structuredDocumentRegion == null && structuredDocument.getLength() != 0) {
			// this case is "unusual". When would region be null, and document longer
			// than 0. I think this means somethings "out of sync". And we may want 
			// to "flag" that fact and just return one big region of 'unknown', instead
			// of one character at a time.
			setInternalPartition(offset, 1, getUnknown());
			partitionFound = true;
		}
		else if (checkBetween) {
			if (structuredDocumentRegion == null && structuredDocument.getLength() == 0) {
				// known special case for an empty document
				setInternalPartition(offset, 0, getDefault());
				partitionFound = true;
			}
			else if (structuredDocumentRegion.getStartOffset() == offset) {
				IStructuredDocumentRegion previousStructuredDocumentRegion = structuredDocumentRegion.getPrevious();
				if (previousStructuredDocumentRegion != null) {
					ITextRegion next = structuredDocumentRegion.getRegionAtCharacterOffset(offset);
					ITextRegion previousStart = previousStructuredDocumentRegion.getRegionAtCharacterOffset(previousStructuredDocumentRegion.getStartOffset());
					partitionFound = doParserSpecificCheck(offset, partitionFound, structuredDocumentRegion, previousStructuredDocumentRegion, next, previousStart);
				}
			}
		}
		else if (structuredDocumentRegion == null) {
			setInternalPartition(offset, 0, getDefault());
			partitionFound = true;
		}
		if (!partitionFound) {
			ITextRegion resultRegion = structuredDocumentRegion.getRegionAtCharacterOffset(offset);
			partitionFound = isDocumentRegionBasedPartition(structuredDocumentRegion, resultRegion, offset);
			if (!partitionFound) {
				// Note: this new logic doesn't handle container regions inside of 
				// container regions ... may need to make this first clause
				// a recursive method
				if (resultRegion != null && resultRegion instanceof ITextRegionContainer) {
					ITextRegionContainer containerRegion = (ITextRegionContainer) resultRegion;
					// then need to "drill down" for relevent region and relevent offset
					ITextRegion deepRegion = containerRegion.getRegionAtCharacterOffset(offset);
					int endOffset = containerRegion.getEndOffset(deepRegion);
					String type = getPartitionType(deepRegion, endOffset);
					setInternalPartition(offset, endOffset - offset, type);
				}
				else {
					if (resultRegion != null) {
						String type = getPartitionType(resultRegion, offset);
						setInternalPartition(offset, structuredDocumentRegion.getEndOffset(resultRegion) - offset, type);
					}
					else {
						// can happen at EOF
						setInternalPartition(offset, 1, getUnknown());
					}
				}
			}
		}
	}

	/**
	 * Provides for a per-StructuredDocumentRegion override selecting the partition type using more than
	 * just a single ITextRegion.
	 * 
	 * @param structuredDocumentRegion the StructuredDocumentRegion
	 * @param containedChildRegion an ITextRegion within the given StructuredDocumentRegion that would
	 *                             normally determine the partition type by itself
	 * @param offset the document offset
	 * @return true if the partition type will be overridden, false to continue normal processing
	 */
	protected boolean isDocumentRegionBasedPartition(IStructuredDocumentRegion structuredDocumentRegion, ITextRegion containedChildRegion, int offset) {
		return false;
	}

	protected boolean doParserSpecificCheck(int offset, boolean partitionFound, IStructuredDocumentRegion sdRegion, IStructuredDocumentRegion previousStructuredDocumentRegion, ITextRegion next, ITextRegion previousStart) {
		// this (conceptually) abstract method is not concerned with
		// specific region types
		return false;
	}

	/**
	 * Return the ITextRegion at the given offset.  For most cases, this will
	 * be the flatNode itself.  Should it contain an embedded ITextRegionContainer,
	 * will return the internal region at the offset
	 * 
	 * 
	 * @param flatNode
	 * @param offset
	 * @return ITextRegion
	 */
	private String getReleventRegionType(IStructuredDocumentRegion flatNode, int offset) {
		//		* Note: the original form of this method -- which returned "deep" region, isn't that 
		//		* useful, after doing parent elimination refactoring, 
		//		* since once the deep region is returned, its hard to get its text or offset without
		//		* proper parent.
		ITextRegion resultRegion = null;
		if (containsEmbeddedRegion(flatNode)) {
			resultRegion = flatNode.getRegionAtCharacterOffset(offset);
			if (resultRegion instanceof ITextRegionContainer) {
				resultRegion = flatNode.getRegionAtCharacterOffset(offset);
				ITextRegionList regions = ((ITextRegionContainer) resultRegion).getRegions();
				for (int i = 0; i < regions.size(); i++) {
					ITextRegion region = regions.get(i);
					if (flatNode.getStartOffset(region) <= offset && offset < flatNode.getEndOffset(region)) {
						resultRegion = region;
						break;
					}
				}
			}
		}
		else {
			resultRegion = flatNode;
		}
		return resultRegion.getType();
	}

	/**
	 * Determines if the given ITextRegionContainer itself contains another ITextRegionContainer
	 * @param ITextRegionContainer
	 * @return boolean
	 */
	protected boolean containsEmbeddedRegion(IStructuredDocumentRegion container) {
		boolean containsEmbeddedRegion = false;

		ITextRegionList regions = container.getRegions();
		for (int i = 0; i < regions.size(); i++) {
			ITextRegion region = regions.get(i);
			if (region instanceof ITextRegionContainer) {
				containsEmbeddedRegion = true;
				break;
			}
		}
		return containsEmbeddedRegion;
	}

	/**
	 * Returns the partition based on region type.  This basically maps from one
	 * region-type space to another, higher level, region-type space.
	 * 
	 * @param region
	 * @param offset
	 * @return String
	 */
	protected String getPartitionType(ITextRegion region, int offset) {
		String result = getDefault();
		//		if (region instanceof ContextRegionContainer) {
		//			result = getPartitionType((ITextRegionContainer) region, offset);
		//		} else {
		if (region instanceof ITextRegionContainer) {
			result = getPartitionType((ITextRegionContainer) region, offset);
		}

		result = getPartitionFromBlockedText(region, offset, result);

		return result;

	}

	protected String getPartitionFromBlockedText(ITextRegion region, int offset, String result) {
		// parser sensitive code was moved to subclass for quick transition
		// this (conceptually) abstract version isn't concerned with blocked text  

		return result;
	}

	/**
	 * To be used, instead of default, when there is some thing
	 * surprising about are attempt to partition
	 */
	protected String getUnknown() {
		return ST_UNKNOWN_PARTITION;
	}

	/**
	 * Similar to method with 'ITextRegion' as argument, except for 
	 * RegionContainers, if it has embedded regions, then we need 
	 * to drill down and return DocumentPartition based on "lowest level" 
	 * region type. For example, in 
	 * <body id="<%= object.getID() %>" >
	 * The text between <%= and %> would be a "java region" not 
	 * an "HTML region".
	 */
	protected String getPartitionType(ITextRegionContainer region, int offset) {
		// TODO this method needs to be 'cleaned up' after refactoring
		// its instanceof logic seems messed up now.
		String result = null;
		if (region != null) {
			ITextRegion coreRegion = region;
			if (coreRegion instanceof ITextRegionContainer) {
				result = getPartitionType((ITextRegionContainer) coreRegion, ((ITextRegionContainer) coreRegion).getRegions(), offset);
			}
			else {
				result = getPartitionType(region);
			}
		}
		else {
			result = getPartitionType((ITextRegion) region, offset);
		}

		return result;
	}

	/**
	 * Computes the partition type for the zero-length partition between a start
	 * tag and end tag with the given name regions.
	 * 
	 * @param previousStartTagNameRegion
	 * @param nextEndTagNameRegion
	 * @return String
	 */
	protected String getPartitionTypeBetween(IStructuredDocumentRegion previousNode, ITextRegion previousStartTagNameRegion, IStructuredDocumentRegion nextNode, ITextRegion nextEndTagNameRegion) {
		return getDefault();
	}

	/**
	 * Method getPartitionType.
	 * @param region
	 * @return String
	 */
	private String getPartitionType(ITextRegion region) {
		// if it get's to this "raw" level, then 
		// must be default.
		return getDefault();
	}

	/**
	 * To be used by default!
	 */
	public String getDefault() {

		return ST_DEFAULT_PARTITION;
	}

	protected String getPartitionType(ForeignRegion region, int offset) {
		String tagname = region.getSurroundingTag();
		String result = null;
		if(result != null) {
			result = "BLOCK:" + tagname.toUpperCase(Locale.ENGLISH); //$NON-NLS-1$
		}
		else {
			result = "BLOCK"; //$NON-NLS-1$
		}
		return result;
	}

	/**
	 */
	protected String getPartitionType(IBlockedStructuredDocumentRegion blockedStructuredDocumentRegion, int offset) {
		String result = null;
		ITextRegionList regions = blockedStructuredDocumentRegion.getRegions();

		// regions should never be null, or hold zero regions, but just in case...
		if (regions != null && regions.size() > 0) {
			if (regions.size() == 1) {
				// if only one, then its a "pure" blocked note.
				// if more than one, then must contain some embedded region container
				ITextRegion blockedRegion = regions.get(0);
				// double check for code safefy, though should always be true
				if (blockedRegion instanceof ForeignRegion) {
					result = getPartitionType((ForeignRegion) blockedRegion, offset);
				}
			}
			else {
				// must have some embedded region container, so we'll make sure we'll get the appropriate one
				result = getReleventRegionType(blockedStructuredDocumentRegion, offset);
			}
		}
		return result;
	}

	private String getPartitionType(ITextRegionContainer coreRegion, ITextRegionList regions, int offset) {
		String result = null;
		for (int i = 0; i < regions.size(); i++) {
			ITextRegion region = regions.get(i);
			if (coreRegion.containsOffset(region, offset)) {
				result = getPartitionType(region, offset);
				break;
			}
		}
		return result;
	}

	public IDocumentPartitioner newInstance() {
		return new StructuredTextPartitioner();
	}

}
