/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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
import org.eclipse.jface.text.BadPartitioningException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DocumentPartitioningChangedEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IPositionUpdater;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ISynchronizable;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.wst.common.encoding.EncodingMemento;
import org.eclipse.wst.sse.core.events.NewModelEvent;
import org.eclipse.wst.sse.core.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.parser.RegionParser;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegionList;
import org.eclipse.wst.sse.core.text.IStructuredTextReParser;
import org.eclipse.wst.sse.core.undo.IStructuredTextUndoManager;


/**
 * 
 * @deprecated to be removed
 */

public class SynchronizedStructuredDocument extends BasicStructuredDocument implements ISynchronizable {
	private final Object fInternalLockObject = new byte[0];
	private Object fLockObject;

	/**
	 *  
	 */
	public SynchronizedStructuredDocument() {
		super();
	}

	/**
	 * @param parser
	 */
	public SynchronizedStructuredDocument(RegionParser parser) {
		super(parser);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocument#addPosition(org.eclipse.jface.text.Position)
	 */
	public void addPosition(Position position) throws BadLocationException {
		synchronized (getLockObject()) {
			super.addPosition(position);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocument#addPosition(java.lang.String,
	 *      org.eclipse.jface.text.Position)
	 */
	public void addPosition(String category, Position position) throws BadLocationException, BadPositionCategoryException {
		synchronized (getLockObject()) {
			super.addPosition(category, position);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocument#addPositionCategory(java.lang.String)
	 */
	public void addPositionCategory(String category) {
		synchronized (getLockObject()) {
			super.addPositionCategory(category);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocument#addPositionUpdater(org.eclipse.jface.text.IPositionUpdater)
	 */
	public void addPositionUpdater(IPositionUpdater updater) {
		synchronized (getLockObject()) {
			super.addPositionUpdater(updater);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.CharSequence#charAt(int)
	 */
	public char charAt(int arg0) {
		synchronized (getLockObject()) {
			return super.charAt(arg0);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.text.IStructuredDocument#clearReadOnly(int, int)
	 */
	public void clearReadOnly(int startOffset, int length) {
		synchronized (getLockObject()) {
			super.clearReadOnly(startOffset, length);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocument#computeIndexInCategory(java.lang.String,
	 *      int)
	 */
	public int computeIndexInCategory(String category, int offset) throws BadPositionCategoryException, BadLocationException {
		synchronized (getLockObject()) {
			return super.computeIndexInCategory(category, offset);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocument#computeNumberOfLines(java.lang.String)
	 */
	public int computeNumberOfLines(String text) {
		synchronized (getLockObject()) {
			return super.computeNumberOfLines(text);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocument#computePartitioning(int, int)
	 */
	public ITypedRegion[] computePartitioning(int offset, int length) throws BadLocationException {
		synchronized (getLockObject()) {
			return super.computePartitioning(offset, length);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocumentExtension3#computePartitioning(java.lang.String,
	 *      int, int, boolean)
	 */
	public ITypedRegion[] computePartitioning(String partitioning, int offset, int length, boolean includeZeroLengthPartitions) throws BadLocationException, BadPartitioningException {
		synchronized (getLockObject()) {
			return super.computePartitioning(partitioning, offset, length, includeZeroLengthPartitions);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocument#containsPosition(java.lang.String,
	 *      int, int)
	 */
	public boolean containsPosition(String category, int offset, int length) {
		synchronized (getLockObject()) {
			return super.containsPosition(category, offset, length);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocument#containsPositionCategory(java.lang.String)
	 */
	public boolean containsPositionCategory(String category) {
		synchronized (getLockObject()) {
			return super.containsPositionCategory(category);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.text.IStructuredDocument#containsReadOnly(int,
	 *      int)
	 */
	public boolean containsReadOnly(int startOffset, int length) {
		synchronized (getLockObject()) {
			return super.containsReadOnly(startOffset, length);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.internal.text.BasicStructuredDocument#fireDocumentPartitioningChanged(org.eclipse.jface.text.DocumentPartitioningChangedEvent)
	 */
	protected void fireDocumentPartitioningChanged(DocumentPartitioningChangedEvent event) {
		synchronized (getLockObject()) {
			super.fireDocumentPartitioningChanged(event);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocument#get()
	 */
	public String get() {
		synchronized (getLockObject()) {
			return super.get();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocument#get(int, int)
	 */
	public String get(int offset, int length) {
		synchronized (getLockObject()) {
			return super.get(offset, length);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		synchronized (getLockObject()) {
			return super.getAdapter(adapter);
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.internal.text.BasicStructuredDocument#getCachedDocumentRegion()
	 */
	IStructuredDocumentRegion getCachedDocumentRegion() {
		synchronized (getLockObject()) {
			return super.getCachedDocumentRegion();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocument#getChar(int)
	 */
	public char getChar(int pos) throws BadLocationException {
		synchronized (getLockObject()) {
			return super.getChar(pos);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocument#getContentType(int)
	 */
	public String getContentType(int offset) throws BadLocationException {
		synchronized (getLockObject()) {
			return super.getContentType(offset);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocumentExtension3#getContentType(java.lang.String,
	 *      int, boolean)
	 */
	public String getContentType(String partitioning, int offset, boolean preferOpenPartitions) throws BadLocationException, BadPartitioningException {
		synchronized (getLockObject()) {
			return super.getContentType(partitioning, offset, preferOpenPartitions);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocument#getDocumentPartitioner()
	 */
	public IDocumentPartitioner getDocumentPartitioner() {
		synchronized (getLockObject()) {
			return super.getDocumentPartitioner();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocumentExtension3#getDocumentPartitioner(java.lang.String)
	 */
	public IDocumentPartitioner getDocumentPartitioner(String partitioning) {
		synchronized (getLockObject()) {
			return super.getDocumentPartitioner(partitioning);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.document.IEncodedDocument#getEncodingMemento()
	 */
	public EncodingMemento getEncodingMemento() {
		synchronized (getLockObject()) {
			return super.getEncodingMemento();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.text.IStructuredDocument#getFirstStructuredDocumentRegion()
	 */
	public IStructuredDocumentRegion getFirstStructuredDocumentRegion() {
		synchronized (getLockObject()) {
			return super.getFirstStructuredDocumentRegion();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.text.IStructuredDocument#getLastStructuredDocumentRegion()
	 */
	public IStructuredDocumentRegion getLastStructuredDocumentRegion() {
		synchronized (getLockObject()) {
			return super.getLastStructuredDocumentRegion();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocument#getLegalContentTypes()
	 */
	public String[] getLegalContentTypes() {
		synchronized (getLockObject()) {
			return super.getLegalContentTypes();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocumentExtension3#getLegalContentTypes(java.lang.String)
	 */
	public String[] getLegalContentTypes(String partitioning) throws BadPartitioningException {
		synchronized (getLockObject()) {
			return super.getLegalContentTypes(partitioning);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocument#getLegalLineDelimiters()
	 */
	public String[] getLegalLineDelimiters() {
		synchronized (getLockObject()) {
			return super.getLegalLineDelimiters();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocument#getLength()
	 */
	public int getLength() {
		synchronized (getLockObject()) {
			return super.getLength();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.document.IEncodedDocument#getLineDelimiter()
	 */
	public String getLineDelimiter() {
		synchronized (getLockObject()) {
			return super.getLineDelimiter();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocument#getLineDelimiter(int)
	 */
	public String getLineDelimiter(int line) throws BadLocationException {
		synchronized (getLockObject()) {
			return super.getLineDelimiter(line);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocument#getLineInformation(int)
	 */
	public IRegion getLineInformation(int line) throws BadLocationException {
		synchronized (getLockObject()) {
			return super.getLineInformation(line);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocument#getLineInformationOfOffset(int)
	 */
	public IRegion getLineInformationOfOffset(int offset) throws BadLocationException {
		synchronized (getLockObject()) {
			return super.getLineInformationOfOffset(offset);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocument#getLineLength(int)
	 */
	public int getLineLength(int line) throws BadLocationException {
		synchronized (getLockObject()) {
			return super.getLineLength(line);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocument#getLineOffset(int)
	 */
	public int getLineOffset(int line) throws BadLocationException {
		synchronized (getLockObject()) {
			return super.getLineOffset(line);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocument#getLineOfOffset(int)
	 */
	public int getLineOfOffset(int offset) {
		synchronized (getLockObject()) {
			return super.getLineOfOffset(offset);
		}
	}

	/*
	 * @see org.eclipse.jface.text.ISynchronizable#getLockObject()
	 */
	public synchronized Object getLockObject() {
		return fLockObject == null ? fInternalLockObject : fLockObject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocument#getNumberOfLines()
	 */
	public int getNumberOfLines() {
		synchronized (getLockObject()) {
			return super.getNumberOfLines();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocument#getNumberOfLines(int, int)
	 */
	public int getNumberOfLines(int offset, int length) throws BadLocationException {
		synchronized (getLockObject()) {
			return super.getNumberOfLines(offset, length);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.text.IStructuredDocument#getParser()
	 */
	public RegionParser getParser() {
		synchronized (getLockObject()) {
			return super.getParser();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocument#getPartition(int)
	 */
	public ITypedRegion getPartition(int offset) throws BadLocationException {
		synchronized (getLockObject()) {
			return super.getPartition(offset);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocumentExtension3#getPartition(java.lang.String,
	 *      int, boolean)
	 */
	public ITypedRegion getPartition(String partitioning, int offset, boolean preferOpenPartitions) throws BadLocationException, BadPartitioningException {
		synchronized (getLockObject()) {
			return super.getPartition(partitioning, offset, preferOpenPartitions);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocumentExtension3#getPartitionings()
	 */
	public String[] getPartitionings() {
		synchronized (getLockObject()) {
			return super.getPartitionings();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocument#getPositionCategories()
	 */
	public String[] getPositionCategories() {
		synchronized (getLockObject()) {
			return super.getPositionCategories();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocument#getPositions(java.lang.String)
	 */
	public Position[] getPositions(String category) throws BadPositionCategoryException {
		synchronized (getLockObject()) {
			return super.getPositions(category);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocument#getPositionUpdaters()
	 */
	public IPositionUpdater[] getPositionUpdaters() {
		synchronized (getLockObject()) {
			return super.getPositionUpdaters();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.text.IStructuredDocument#getRegionAtCharacterOffset(int)
	 */
	public IStructuredDocumentRegion getRegionAtCharacterOffset(int offset) {
		synchronized (getLockObject()) {
			return super.getRegionAtCharacterOffset(offset);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.text.IStructuredDocument#getRegionList()
	 */
	public IStructuredDocumentRegionList getRegionList() {
		synchronized (getLockObject()) {
			return super.getRegionList();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.text.IStructuredDocument#getReParser()
	 */
	public IStructuredTextReParser getReParser() {
		synchronized (getLockObject()) {
			return super.getReParser();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.text.IStructuredDocument#getText()
	 */
	public String getText() {
		synchronized (getLockObject()) {
			return super.getText();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.text.IStructuredDocument#getUndoManager()
	 */
	public IStructuredTextUndoManager getUndoManager() {
		synchronized (getLockObject()) {
			return super.getUndoManager();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.internal.text.BasicStructuredDocument#initializeFirstAndLastDocumentRegion()
	 */
	void initializeFirstAndLastDocumentRegion() {
		synchronized (getLockObject()) {
			super.initializeFirstAndLastDocumentRegion();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocument#insertPositionUpdater(org.eclipse.jface.text.IPositionUpdater,
	 *      int)
	 */
	public void insertPositionUpdater(IPositionUpdater updater, int index) {
		synchronized (getLockObject()) {
			super.insertPositionUpdater(updater, index);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.CharSequence#length()
	 */
	public int length() {
		synchronized (getLockObject()) {
			return super.length();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.text.IStructuredDocument#makeReadOnly(int, int)
	 */
	public void makeReadOnly(int startOffset, int length) {
		synchronized (getLockObject()) {
			super.makeReadOnly(startOffset, length);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.text.IStructuredDocument#newInstance()
	 */
	public IStructuredDocument newInstance() {
		synchronized (getLockObject()) {
			return super.newInstance();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocumentExtension#registerPostNotificationReplace(org.eclipse.jface.text.IDocumentListener,
	 *      org.eclipse.jface.text.IDocumentExtension.IReplace)
	 */
	public void registerPostNotificationReplace(IDocumentListener owner, IReplace replace) {
		synchronized (getLockObject()) {
			super.registerPostNotificationReplace(owner, replace);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocument#removePosition(org.eclipse.jface.text.Position)
	 */
	public void removePosition(Position position) {
		synchronized (getLockObject()) {
			super.removePosition(position);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocument#removePosition(java.lang.String,
	 *      org.eclipse.jface.text.Position)
	 */
	public void removePosition(String category, Position position) throws BadPositionCategoryException {
		synchronized (getLockObject()) {
			super.removePosition(category, position);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocument#removePositionCategory(java.lang.String)
	 */
	public void removePositionCategory(String category) throws BadPositionCategoryException {
		synchronized (getLockObject()) {
			super.removePositionCategory(category);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocument#replace(int, int,
	 *      java.lang.String)
	 */
	public void replace(int pos, int length, String string) throws BadLocationException {
		synchronized (getLockObject()) {
			super.replace(pos, length, string);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.text.IStructuredDocument#replaceText(java.lang.Object,
	 *      int, int, java.lang.String)
	 */
	public StructuredDocumentEvent replaceText(Object requester, int start, int replacementLength, String changes) {
		synchronized (getLockObject()) {
			return super.replaceText(requester, start, replacementLength, changes);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.text.IStructuredDocument#replaceText(java.lang.Object,
	 *      int, int, java.lang.String, boolean)
	 */
	public StructuredDocumentEvent replaceText(Object requester, int start, int replacementLength, String changes, boolean ignoreReadOnlySettings) {
		synchronized (getLockObject()) {
			return super.replaceText(requester, start, replacementLength, changes, ignoreReadOnlySettings);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.internal.text.BasicStructuredDocument#resetParser(int,
	 *      int)
	 */
	void resetParser(int startOffset, int endOffset) {
		synchronized (getLockObject()) {
			super.resetParser(startOffset, endOffset);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocumentExtension#resumePostNotificationProcessing()
	 */
	public void resumePostNotificationProcessing() {
		synchronized (getLockObject()) {
			super.resumePostNotificationProcessing();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocument#search(int, java.lang.String,
	 *      boolean, boolean, boolean)
	 */
	public int search(int startPosition, String findString, boolean forwardSearch, boolean caseSensitive, boolean wholeWord) throws BadLocationException {
		synchronized (getLockObject()) {
			return super.search(startPosition, findString, forwardSearch, caseSensitive, wholeWord);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocument#set(java.lang.String)
	 */
	public void set(String string) {
		synchronized (getLockObject()) {
			super.set(string);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.internal.text.BasicStructuredDocument#setCachedDocumentRegion(org.eclipse.wst.sse.core.text.IStructuredDocumentRegion)
	 */
	public void setCachedDocumentRegion(IStructuredDocumentRegion structuredRegion) {
		synchronized (getLockObject()) {
			super.setCachedDocumentRegion(structuredRegion);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocument#setDocumentPartitioner(org.eclipse.jface.text.IDocumentPartitioner)
	 */
	public void setDocumentPartitioner(IDocumentPartitioner partitioner) {
		synchronized (getLockObject()) {
			super.setDocumentPartitioner(partitioner);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocumentExtension3#setDocumentPartitioner(java.lang.String,
	 *      org.eclipse.jface.text.IDocumentPartitioner)
	 */
	public void setDocumentPartitioner(String partitioning, IDocumentPartitioner partitioner) {
		synchronized (getLockObject()) {
			super.setDocumentPartitioner(partitioning, partitioner);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.document.IEncodedDocument#setEncodingMemento(org.eclipse.wst.common.encoding.EncodingMemento)
	 */
	public void setEncodingMemento(EncodingMemento encodingMemento) {
		synchronized (getLockObject()) {
			super.setEncodingMemento(encodingMemento);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.internal.text.BasicStructuredDocument#setFirstDocumentRegion(org.eclipse.wst.sse.core.text.IStructuredDocumentRegion)
	 */
	void setFirstDocumentRegion(IStructuredDocumentRegion region) {
		synchronized (getLockObject()) {
			super.setFirstDocumentRegion(region);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.internal.text.BasicStructuredDocument#setLastDocumentRegion(org.eclipse.wst.sse.core.text.IStructuredDocumentRegion)
	 */
	void setLastDocumentRegion(IStructuredDocumentRegion region) {
		synchronized (getLockObject()) {
			super.setLastDocumentRegion(region);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.document.IEncodedDocument#setLineDelimiter(java.lang.String)
	 */
	public void setLineDelimiter(String delimiter) {
		synchronized (getLockObject()) {
			super.setLineDelimiter(delimiter);
		}
	}

	/*
	 * @see org.eclipse.jface.text.ISynchronizable#setLockObject(java.lang.Object)
	 */
	public synchronized void setLockObject(Object lockObject) {
		// TODO_future: Note, technically, we should have an array of "ordered
		// lock objects",
		// or some way for clients to add and remove. We're assuming once set,
		// its
		// permenant or nearly so, and or that clients know how to set one at
		// a time. If a client tries to set the lock object, and its already
		// set,
		// a runtime exception is thrown, since this could prove "fatal" to
		// clients
		// thinking they had the lock, when they didn't.
		if (fLockObject == null) {
			// all is ok, can assign new one.
			fLockObject = lockObject;
		} else {
			// lock object is in use, so throw execption,
			// unless argument is null.
			if (lockObject == null) {
				fLockObject = lockObject;
			} else {
				throw new IllegalStateException("Program flow error: attempt to set structured document lock object when it was already set");
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.internal.text.BasicStructuredDocument#setParser(org.eclipse.wst.sse.core.parser.RegionParser)
	 */
	public void setParser(RegionParser newParser) {
		synchronized (getLockObject()) {
			super.setParser(newParser);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.internal.text.BasicStructuredDocument#setReParser(org.eclipse.wst.sse.core.text.IStructuredTextReParser)
	 */
	public void setReParser(IStructuredTextReParser newReParser) {
		synchronized (getLockObject()) {
			super.setReParser(newReParser);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.text.IStructuredDocument#setText(java.lang.Object,
	 *      java.lang.String)
	 */
	public NewModelEvent setText(Object requester, String theString) {
		synchronized (getLockObject()) {
			return super.setText(requester, theString);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.text.IStructuredDocument#setUndoManager(org.eclipse.wst.sse.core.undo.IStructuredTextUndoManager)
	 */
	public void setUndoManager(IStructuredTextUndoManager undoManager) {
		synchronized (getLockObject()) {
			super.setUndoManager(undoManager);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocumentExtension#startSequentialRewrite(boolean)
	 */
	public void startSequentialRewrite(boolean normalized) {
		synchronized (getLockObject()) {
			super.startSequentialRewrite(normalized);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocumentExtension#stopPostNotificationProcessing()
	 */
	public void stopPostNotificationProcessing() {
		synchronized (getLockObject()) {
			super.stopPostNotificationProcessing();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocumentExtension#stopSequentialRewrite()
	 */
	public void stopSequentialRewrite() {
		synchronized (getLockObject()) {
			super.stopSequentialRewrite();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.CharSequence#subSequence(int, int)
	 */
	public CharSequence subSequence(int arg0, int arg1) {
		synchronized (getLockObject()) {
			return super.subSequence(arg0, arg1);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.internal.text.BasicStructuredDocument#updateDocumentData(int,
	 *      int, java.lang.String)
	 */
	public void updateDocumentData(int start, int lengthToReplace, String changes) {
		synchronized (getLockObject()) {
			super.updateDocumentData(start, lengthToReplace, changes);
		}
	}
}
