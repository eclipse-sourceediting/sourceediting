/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
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
package org.eclipse.wst.sse.core.internal.text;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IDocumentPartitioningListener;
import org.eclipse.jface.text.IPositionUpdater;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TypedRegion;
import org.eclipse.wst.sse.core.internal.Logger;
import org.eclipse.wst.sse.core.internal.NotImplementedException;
import org.eclipse.wst.sse.core.internal.encoding.EncodingMemento;
import org.eclipse.wst.sse.core.internal.ltk.parser.RegionParser;
import org.eclipse.wst.sse.core.internal.provisional.events.IModelAboutToBeChangedListener;
import org.eclipse.wst.sse.core.internal.provisional.events.IStructuredDocumentListener;
import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegionList;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredTextReParser;
import org.eclipse.wst.sse.core.internal.undo.IStructuredTextUndoManager;


/**
 * Purely a dummy "marker" instance for StructuredDocumentRegions which are
 * created temorarily in the course of re-parsing. Primarily a place holder,
 * but can be needed to get text from.
 */
public class MinimalDocument implements IStructuredDocument {
	private SubSetTextStore data;

	/**
	 * Marked private to be sure never created without data being initialized.
	 *  
	 */
	private MinimalDocument() {
		super();
	}

	public MinimalDocument(SubSetTextStore initialContents) {
		this();
		data = initialContents;
	}

	public void addDocumentAboutToChangeListener(IModelAboutToBeChangedListener listener) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void addDocumentChangedListener(IStructuredDocumentListener listener) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void addDocumentChangingListener(IStructuredDocumentListener listener) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void addDocumentListener(IDocumentListener listener) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void addDocumentPartitioningListener(IDocumentPartitioningListener listener) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void addPosition(Position position) throws BadLocationException {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void addPosition(String category, Position position) throws BadLocationException, BadPositionCategoryException {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void addPositionCategory(String category) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void addPositionUpdater(IPositionUpdater updater) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void addPrenotifiedDocumentListener(IDocumentListener documentAdapter) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void clearReadOnly(int startOffset, int length) {
		// TODO: this is called from notifier loop inappropriately
		//	throw new NotImplementedException("intentionally not implemented");
	}

	public int computeIndexInCategory(String category, int offset) throws BadLocationException, BadPositionCategoryException {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public int computeNumberOfLines(String text) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public ITypedRegion[] computePartitioning(int offset, int length) throws BadLocationException {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public boolean containsPosition(String category, int offset, int length) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public boolean containsPositionCategory(String category) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public boolean containsReadOnly(int startOffset, int length) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void fireNewDocument(Object requester) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public String get() {
		String result = null;
		result = data.get(0, data.getLength());
		return result;
	}

	public String get(int offset, int length) throws BadLocationException {
		String result = null;
		try {
			result = data.get(offset, length);
		} catch (StringIndexOutOfBoundsException e) {
			throw new BadLocationException("offset: " + offset + " length: " + length + "\ndocument length: " + data.getLength());
		}
		return result;
	}

	public Object getAdapter(Class adapter) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public char getChar(int offset) throws BadLocationException {
		return data.get(offset);
	}

	public String getContentType(int offset) throws BadLocationException {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public IDocumentPartitioner getDocumentPartitioner() {
		// temp fix
		return null;
		//		throw new NotImplementedException("intentionally not implemented");
	}

	public EncodingMemento getEncodingMemento() {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public IStructuredDocumentRegion getFirstStructuredDocumentRegion() {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public IStructuredDocumentRegion getLastStructuredDocumentRegion() {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public String[] getLegalContentTypes() {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public String[] getLegalLineDelimiters() {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public int getLength() {
		return data.getLength();
	}

	public String getPreferedLineDelimiter() {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public String getLineDelimiter(int line) throws BadLocationException {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public IRegion getLineInformation(int line) throws BadLocationException {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public IRegion getLineInformationOfOffset(int offset) throws BadLocationException {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public int getLineLength(int line) throws BadLocationException {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public int getLineOffset(int line) throws BadLocationException {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public int getLineOfOffset(int offset) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public int getNumberOfLines() {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public int getNumberOfLines(int offset, int length) throws BadLocationException {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public RegionParser getParser() {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public ITypedRegion getPartition(int offset) throws BadLocationException {
		Logger.log(Logger.WARNING, "An instance of MinimalDocument was asked for its partition, sometime indicating a deleted region was being accessed."); //$NON-NLS-1$
		return new TypedRegion(0,0, "undefined"); //$NON-NLS-1$
		//throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public String[] getPositionCategories() {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public Position[] getPositions(String category) throws BadPositionCategoryException {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public IPositionUpdater[] getPositionUpdaters() {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public IStructuredDocumentRegion getRegionAtCharacterOffset(int offset) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public IStructuredDocumentRegionList getRegionList() {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public IStructuredTextReParser getReParser() {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public String getText() {
		return data.get(0, data.getLength());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.text.IStructuredDocument#getUndoManager()
	 */
	public IStructuredTextUndoManager getUndoManager() {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void insertPositionUpdater(IPositionUpdater updater, int index) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void makeReadOnly(int startOffset, int length) {
		// TODO: this is called from notifier loop inappropriately
		//	throw new NotImplementedException("intentionally not implemented");
	}

	public IStructuredDocument newInstance() {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocumentExtension#registerPostNotificationReplace(org.eclipse.jface.text.IDocumentListener,
	 *      org.eclipse.jface.text.IDocumentExtension.IReplace)
	 */
	public void registerPostNotificationReplace(IDocumentListener owner, IReplace replace) throws UnsupportedOperationException {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$		
	}

	public void removeDocumentAboutToChangeListener(IModelAboutToBeChangedListener listener) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void removeDocumentChangedListener(IStructuredDocumentListener listener) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void removeDocumentChangingListener(IStructuredDocumentListener listener) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void removeDocumentListener(IDocumentListener listener) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void removeDocumentPartitioningListener(IDocumentPartitioningListener listener) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void removePosition(Position position) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void removePosition(String category, Position position) throws BadPositionCategoryException {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void removePositionCategory(String category) throws BadPositionCategoryException {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void removePositionUpdater(IPositionUpdater updater) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void removePrenotifiedDocumentListener(IDocumentListener documentAdapter) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void replace(int offset, int length, String text) throws BadLocationException {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
		//		data.replace(offset, length, text);
	}

	public StructuredDocumentEvent replaceText(Object source, int oldStart, int replacementLength, String requestedChange) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.text.IStructuredDocument#replaceText(java.lang.Object,
	 *      int, int, java.lang.String, boolean)
	 */
	public StructuredDocumentEvent replaceText(Object source, int oldStart, int replacementLength, String requestedChange, boolean ignoreReadOnlySetting) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocumentExtension#resumePostNotificationProcessing()
	 */
	public void resumePostNotificationProcessing() {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public int search(int startOffset, String findString, boolean forwardSearch, boolean caseSensitive, boolean wholeWord) throws BadLocationException {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void set(String text) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
		//		data.set(text);
	}

	public void setDocumentPartitioner(IDocumentPartitioner partitioner) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void setEncodingMemento(EncodingMemento encodingMemento) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void setPreferredLineDelimiter(String delimiter) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public StructuredDocumentEvent setText(Object requester, String allText) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.text.IStructuredDocument#setUndoManager(org.eclipse.wst.sse.core.undo.StructuredTextUndoManager)
	 */
	public void setUndoManager(IStructuredTextUndoManager undoManager) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocumentExtension#startSequentialRewrite(boolean)
	 */
	public void startSequentialRewrite(boolean normalize) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocumentExtension#stopPostNotificationProcessing()
	 */
	public void stopPostNotificationProcessing() {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocumentExtension#stopSequentialRewrite()
	 */
	public void stopSequentialRewrite() {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public String getLineDelimiter() {
		return null;
	}

	public String getPreferredLineDelimiter() {
		return null;
	}

	public void setLineDelimiter(String delimiter) {
		
	}
	
	public IStructuredDocumentRegion[] getStructuredDocumentRegions() {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}
	
	public IStructuredDocumentRegion[] getStructuredDocumentRegions(int start, int length) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}
}
