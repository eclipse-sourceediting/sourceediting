/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
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
package org.eclipse.wst.sse.core.internal.text.rules;



import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.wst.sse.core.internal.ltk.parser.IBlockedStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.parser.ForeignRegion;
import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentRegionsReplacedEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredTextPartitioner;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionCollection;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.core.text.IStructuredPartitions;


/**
 * Base Document partitioner for StructuredDocuments. BLOCK_TEXT ITextRegions
 * have a partition type of BLOCK or BLOCK:TAGNAME if a surrounding tagname
 * was recorded.
 * 
 * Subclasses should synchronize access to <code>internalReusedTempInstance</code> using the lock
 * <code>PARTITION_LOCK</code>.
 */
public class StructuredTextPartitioner implements IDocumentPartitioner, IStructuredTextPartitioner {

	static class CachedComputedPartitions {
		int fLength;
		int fOffset;
		ITypedRegion[] fPartitions;
		boolean isInValid;

		CachedComputedPartitions(int offset, int length, ITypedRegion[] partitions) {
			fOffset = offset;
			fLength = length;
			fPartitions = partitions;
			isInValid = true;
		}
	}

	private CachedComputedPartitions cachedPartitions = new CachedComputedPartitions(-1, -1, null);
	protected String[] fSupportedTypes = null;
	protected IStructuredTypedRegion internalReusedTempInstance = new SimpleStructuredTypedRegion(0, 0, IStructuredPartitions.DEFAULT_PARTITION);
	protected IStructuredDocument fStructuredDocument;

	protected final Object PARTITION_LOCK = new Object();

	/**
	 * StructuredTextPartitioner constructor comment.
	 */
	public StructuredTextPartitioner() {
		super();
	}

	/**
	 * Returns the partitioning of the given range of the connected document.
	 * There must be a document connected to this partitioner.
	 * 
	 * Note: this shouldn't be called directly by clients, unless they control
	 * the threading that includes modifications to the document. Otherwise
	 * the document could be modified while partitions are being computed. We
	 * advise that clients use the computePartitions API directly from the
	 * document, so they won't have to worry about that.
	 * 
	 * @param offset
	 *            the offset of the range of interest
	 * @param length
	 *            the length of the range of interest
	 * @return the partitioning of the range
	 */
	public ITypedRegion[] computePartitioning(int offset, int length) {
		if (fStructuredDocument == null) {
			throw new IllegalStateException("document partitioner is not connected"); //$NON-NLS-1$
		}
		ITypedRegion[] results = null;

		synchronized (cachedPartitions) {
			if ((!cachedPartitions.isInValid) && (offset == cachedPartitions.fOffset) && (length == cachedPartitions.fLength))
				results = cachedPartitions.fPartitions;
		}

		if (results == null) {
			if (length == 0) {
				results = new ITypedRegion[]{getPartition(offset)};
			} else {
				List list = new ArrayList();
				int endPos = offset + length;
				if (endPos > fStructuredDocument.getLength()) {
					// This can occur if the model instance is being
					// changed
					// and everyone's not yet up to date
					return new ITypedRegion[]{createPartition(offset, length, getUnknown())};
				}
				int currentPos = offset;
				IStructuredTypedRegion previousPartition = null;
				while (currentPos < endPos) {
					IStructuredTypedRegion partition = null;
					synchronized (PARTITION_LOCK) {
						internalGetPartition(currentPos, false);
						currentPos += internalReusedTempInstance.getLength();
						
						// check if this partition just continues last one
						// (type is the same),
						// if so, just extend length of last one, not need to
						// create new
						// instance.
						if (previousPartition != null && internalReusedTempInstance.getType().equals(previousPartition.getType())) {
							// same partition type
							previousPartition.setLength(previousPartition.getLength() + internalReusedTempInstance.getLength());
						}
						else {
							// not the same, so add to list
							partition = createNewPartitionInstance();
						}
					}
					if (partition != null) {
						list.add(partition);
						// and make current, previous
						previousPartition = partition;
					}
				}
				results = new ITypedRegion[list.size()];
				list.toArray(results);
			}
			if (results.length > 0) {
				// truncate returned results to requested range
				if (results[0].getOffset() < offset && results[0] instanceof IStructuredRegion) {
					((IStructuredRegion) results[0]).setOffset(offset);
				}
				int lastEnd = results[results.length - 1].getOffset() + results[results.length - 1].getLength();
				if (lastEnd > offset + length && results[results.length - 1] instanceof IStructuredRegion) {
					((IStructuredRegion) results[results.length - 1]).setLength(offset + length - results[results.length - 1].getOffset());
				}
			}
			synchronized (cachedPartitions) {
				cachedPartitions.fLength = length;
				cachedPartitions.fOffset = offset;
				cachedPartitions.fPartitions = results;
				cachedPartitions.isInValid = false;
			}
		}
		return results;
	}

	private void invalidatePartitionCache() {
		synchronized (cachedPartitions) {
			cachedPartitions.isInValid = true;
		}
	}

	/**
	 * Connects the document to the partitioner, i.e. indicates the begin of
	 * the usage of the receiver as partitioner of the given document.
	 */
	public synchronized void connect(IDocument document) {
		if (document instanceof IStructuredDocument) {
			invalidatePartitionCache();
			this.fStructuredDocument = (IStructuredDocument) document;
		} else {
			throw new IllegalArgumentException("This class and API are for Structured Documents only"); //$NON-NLS-1$
		}
	}

	/**
	 * Determines if the given ITextRegionContainer itself contains another
	 * ITextRegionContainer
	 * 
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

	private IStructuredTypedRegion createNewPartitionInstance() {
		synchronized (PARTITION_LOCK) {
			return new SimpleStructuredTypedRegion(internalReusedTempInstance.getOffset(), internalReusedTempInstance.getLength(), internalReusedTempInstance.getType());
		}
	}

	/**
	 * Creates the concrete partition from the given values. Returns a new
	 * instance for each call.
	 * 
	 * Subclasses may override.
	 * 
	 * @param offset
	 * @param length
	 * @param type
	 * @return ITypedRegion
	 * 
	 * TODO: should be protected
	 */
	public IStructuredTypedRegion createPartition(int offset, int length, String type) {
		return new SimpleStructuredTypedRegion(offset, length, type);
	}

	/**
	 * Disconnects the document from the partitioner, i.e. indicates the end
	 * of the usage of the receiver as partitioner of the given document.
	 * 
	 * @see org.eclipse.jface.text.IDocumentPartitioner#disconnect()
	 */
	public synchronized void disconnect() {
		invalidatePartitionCache();
		this.fStructuredDocument = null;
	}

	/**
	 * Informs about a forthcoming document change.
	 * 
	 * @see org.eclipse.jface.text.IDocumentPartitioner#documentAboutToBeChanged(DocumentEvent)
	 */
	public void documentAboutToBeChanged(DocumentEvent event) {
		invalidatePartitionCache();
	}

	/**
	 * The document has been changed. The partitioner updates the set of
	 * regions and returns whether the structure of the document partitioning
	 * has been changed, i.e. whether partitions have been added or removed.
	 * 
	 * @see org.eclipse.jface.text.IDocumentPartitioner#documentChanged(DocumentEvent)
	 */
	public boolean documentChanged(DocumentEvent event) {
		boolean result = false;
		if (event instanceof StructuredDocumentRegionsReplacedEvent) {
			// partitions don't always change while document regions do,
			// but that's the only "quick check" we have.
			// I'm not sure if something more sophisticated will be needed
			// in the future. (dmw, 02/18/04).
			result = true;
		}
		return result;
	}

	protected boolean doParserSpecificCheck(int offset, boolean partitionFound, IStructuredDocumentRegion sdRegion, IStructuredDocumentRegion previousStructuredDocumentRegion, ITextRegion next, ITextRegion previousStart) {
		// this (conceptually) abstract method is not concerned with
		// specific region types
		return false;
	}

	/**
	 * Returns the content type of the partition containing the given
	 * character position of the given document. The document has previously
	 * been connected to the partitioner.
	 * 
	 * @see org.eclipse.jface.text.IDocumentPartitioner#getContentType(int)
	 */
	public String getContentType(int offset) {
		return getPartition(offset).getType();
	}

	/**
	 * To be used by default!
	 */
	public String getDefaultPartitionType() {

		return IStructuredPartitions.DEFAULT_PARTITION;
	}

	/**
	 * Returns the set of all possible content types the partitioner supports.
	 * I.e. Any result delivered by this partitioner may not contain a content
	 * type which would not be included in this method's result.
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
	 * Returns the partition containing the given character position of the
	 * given document. The document has previously been connected to the
	 * partitioner.
	 * 
	 * Note: this shouldn't be called directly by clients, unless they control
	 * the threading that includes modifications to the document. Otherwise
	 * the document could be modified while partitions are being computed. We
	 * advise that clients use the getPartition API directly from the
	 * document, so they won't have to worry about that.
	 * 
	 * 
	 * 
	 * @see org.eclipse.jface.text.IDocumentPartitioner#getPartition(int)
	 */
	public ITypedRegion getPartition(int offset) {
		internalGetPartition(offset, true);
		return createNewPartitionInstance();
	}

	protected String getPartitionFromBlockedText(ITextRegion region, int offset, String result) {
		// parser sensitive code was moved to subclass for quick transition
		// this (conceptually) abstract version isn't concerned with blocked
		// text

		return result;
	}

	protected String getPartitionType(ForeignRegion region, int offset) {
		String tagname = region.getSurroundingTag();
		String result = null;
		if (tagname != null) {
			result = "BLOCK:" + tagname.toUpperCase(Locale.ENGLISH); //$NON-NLS-1$
		} else {
			result = "BLOCK"; //$NON-NLS-1$
		}
		return result;
	}


	protected String getPartitionType(IBlockedStructuredDocumentRegion blockedStructuredDocumentRegion, int offset) {
		String result = null;
		ITextRegionList regions = blockedStructuredDocumentRegion.getRegions();

		// regions should never be null, or hold zero regions, but just in
		// case...
		if (regions != null && regions.size() > 0) {
			if (regions.size() == 1) {
				// if only one, then its a "pure" blocked note.
				// if more than one, then must contain some embedded region
				// container
				ITextRegion blockedRegion = regions.get(0);
				// double check for code safefy, though should always be true
				if (blockedRegion instanceof ForeignRegion) {
					result = getPartitionType((ForeignRegion) blockedRegion, offset);
				}
			} else {
				// must have some embedded region container, so we'll make
				// sure we'll get the appropriate one
				result = getReleventRegionType(blockedStructuredDocumentRegion, offset);
			}
		}
		return result;
	}

	/**
	 * Method getPartitionType.
	 * 
	 * @param region
	 * @return String
	 */
	private String getPartitionType(ITextRegion region) {
		// if it get's to this "raw" level, then
		// must be default.
		return getDefaultPartitionType();
	}

	/**
	 * Returns the partition based on region type. This basically maps from
	 * one region-type space to another, higher level, region-type space.
	 * 
	 * @param region
	 * @param offset
	 * @return String
	 */
	public String getPartitionType(ITextRegion region, int offset) {
		String result = getDefaultPartitionType();
		//		if (region instanceof ContextRegionContainer) {
		//			result = getPartitionType((ITextRegionContainer) region, offset);
		//		} else {
		if (region instanceof ITextRegionContainer) {
			result = getPartitionType((ITextRegionContainer) region, offset);
		}

		result = getPartitionFromBlockedText(region, offset, result);

		return result;

	}

	/**
	 * Similar to method with 'ITextRegion' as argument, except for
	 * RegionContainers, if it has embedded regions, then we need to drill
	 * down and return DocumentPartition based on "lowest level" region type.
	 * For example, in <body id=" <%= object.getID() %>" > The text between
	 * <%= and %> would be a "java region" not an "HTML region".
	 */
	protected String getPartitionType(ITextRegionContainer region, int offset) {
		// TODO this method needs to be 'cleaned up' after refactoring
		// its instanceof logic seems messed up now.
		String result = null;
		if (region != null) {
			ITextRegion coreRegion = region;
			if (coreRegion instanceof ITextRegionContainer) {
				result = getPartitionType((ITextRegionContainer) coreRegion, ((ITextRegionContainer) coreRegion).getRegions(), offset);
			} else {
				result = getPartitionType(region);
			}
		} else {
			result = getPartitionType((ITextRegion) region, offset);
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

	/**
	 * Computes the partition type for the zero-length partition between a
	 * start tag and end tag with the given name regions.
	 * 
	 * @param previousStartTagNameRegion
	 * @param nextEndTagNameRegion
	 * @return String
	 */
	public String getPartitionTypeBetween(IStructuredDocumentRegion previousNode, IStructuredDocumentRegion nextNode) {
		return getDefaultPartitionType();
	}

	/**
	 * Return the ITextRegion at the given offset. For most cases, this will
	 * be the flatNode itself. Should it contain an embedded
	 * ITextRegionContainer, will return the internal region at the offset
	 * 
	 * 
	 * @param flatNode
	 * @param offset
	 * @return ITextRegion
	 */
	private String getReleventRegionType(IStructuredDocumentRegion flatNode, int offset) {
		//		* Note: the original form of this method -- which returned "deep"
		// region, isn't that
		//		* useful, after doing parent elimination refactoring,
		//		* since once the deep region is returned, its hard to get its text
		// or offset without
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
		} else {
			resultRegion = flatNode;
		}
		return resultRegion.getType();
	}

	/**
	 * To be used, instead of default, when there is some thing surprising
	 * about are attempt to partition
	 */
	protected String getUnknown() {
		return IStructuredPartitions.UNKNOWN_PARTITION;
	}

	/**
	 * to be abstract eventually
	 */
	protected void initLegalContentTypes() {
		fSupportedTypes = new String[]{IStructuredPartitions.DEFAULT_PARTITION, IStructuredPartitions.UNKNOWN_PARTITION};
	}

	/**
	 * Returns the partition containing the given character position of the
	 * given document. The document has previously been connected to the
	 * partitioner. If the checkBetween parameter is true, an offset between a
	 * start and end tag will return a zero-length region.
	 */
	private void internalGetPartition(int offset, boolean checkBetween) {
		if (fStructuredDocument == null) {
			throw new IllegalStateException("document partitioner is not connected"); //$NON-NLS-1$
		}

		boolean partitionFound = false;
		int docLength = fStructuredDocument.getLength();
		// get document region type and map to partition type :
		// Note: a partion can be smaller than a flatnode, if that flatnode
		// contains a region container.
		// That's why we need to get "relevent region".
		IStructuredDocumentRegion structuredDocumentRegion = fStructuredDocument.getRegionAtCharacterOffset(offset);
		// flatNode is null if empty document
		// this is king of a "normal case" for empty document
		if (structuredDocumentRegion == null) {
			if (docLength == 0) {
				/*
				 * In order to prevent infinite error loops, this partition
				 * must never have a zero length unless the document is also
				 * zero length
				 */
				setInternalPartition(offset, 0, getDefaultPartitionType());
				partitionFound = true;
			}
			else {
				/*
				 * This case is "unusual". When would region be null, and
				 * document longer than 0. I think this means something's "out
				 * of sync". And we may want to "flag" that fact and just
				 * return one big region of 'unknown', instead of one
				 * character at a time.
				 */
				setInternalPartition(offset, 1, getUnknown());
				partitionFound = true;
			}
		}	
		else if (checkBetween) {
			// dmw: minimizes out to the first if test above
			//			if (structuredDocumentRegion == null && docLength == 0) {
			//				// known special case for an empty document
			//				setInternalPartition(offset, 0, getDefault());
			//				partitionFound = true;
			//			}
			//			else
			if (structuredDocumentRegion.getStartOffset() == offset) {
				IStructuredDocumentRegion previousStructuredDocumentRegion = structuredDocumentRegion.getPrevious();
				if (previousStructuredDocumentRegion != null) {
					ITextRegion next = structuredDocumentRegion.getRegionAtCharacterOffset(offset);
					ITextRegion previousStart = previousStructuredDocumentRegion.getRegionAtCharacterOffset(previousStructuredDocumentRegion.getStartOffset());
					partitionFound = doParserSpecificCheck(offset, partitionFound, structuredDocumentRegion, previousStructuredDocumentRegion, next, previousStart);
				}
			}
		}

		if (!partitionFound && structuredDocumentRegion != null) {
			/* We want the actual ITextRegion and not a possible ITextRegionCollection that
			 * could be returned by IStructuredDocumentRegion#getRegionAtCharacterOffset
			 * This allows for correct syntax highlighting and content assist.
			 */
			ITextRegion resultRegion = getDeepRegionAtCharacterOffset(structuredDocumentRegion, offset);
			partitionFound = isDocumentRegionBasedPartition(structuredDocumentRegion, resultRegion, offset);
			if (!partitionFound) {
				if (resultRegion != null) {
					String type = getPartitionType(resultRegion, offset);
					setInternalPartition(offset, resultRegion.getLength(), type);
				} else {
					// can happen at EOF
					// https://bugs.eclipse.org/bugs/show_bug.cgi?id=224886
					// The unknown type was causing problems with content assist in JSP documents
					setInternalPartition(offset, 1, getDefaultPartitionType());
				}
			}
		}
	}
	
	/**
	 * <p>Unlike {@link IStructuredDocumentRegion#getRegionAtCharacterOffset(int)} this will dig
	 * into <code>ITextRegionCollection</code> to find the region containing the given offset</p>
	 * 
	 * @param region the containing region of the given <code>offset</code>
	 * @param offset to the overall offset in the document.
	 * @return the <code>ITextRegion</code> containing the given <code>offset</code>, will never be
	 * a <code>ITextRegionCollextion</code>
	 */
	private ITextRegion getDeepRegionAtCharacterOffset(IStructuredDocumentRegion region, int offset) {
		ITextRegion text = region.getRegionAtCharacterOffset(offset);
		while (text instanceof ITextRegionCollection) {
			text = ((ITextRegionCollection) text).getRegionAtCharacterOffset(offset);
		}
		return text;
	}

	/**
	 * Provides for a per-StructuredDocumentRegion override selecting the
	 * partition type using more than just a single ITextRegion.
	 * 
	 * @param structuredDocumentRegion
	 *            the StructuredDocumentRegion
	 * @param containedChildRegion
	 *            an ITextRegion within the given StructuredDocumentRegion
	 *            that would normally determine the partition type by itself
	 * @param offset
	 *            the document offset
	 * @return true if the partition type will be overridden, false to
	 *         continue normal processing
	 */
	protected boolean isDocumentRegionBasedPartition(IStructuredDocumentRegion structuredDocumentRegion, ITextRegion containedChildRegion, int offset) {
		return false;
	}

	public IDocumentPartitioner newInstance() {
		return new StructuredTextPartitioner();
	}

	protected void setInternalPartition(int offset, int length, String type) {
		synchronized (PARTITION_LOCK) {
			internalReusedTempInstance.setOffset(offset);
			internalReusedTempInstance.setLength(length);
			internalReusedTempInstance.setType(type);
		}
	}

}
