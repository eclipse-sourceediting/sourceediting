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
import org.eclipse.wst.encoding.EncodingMemento;
import org.eclipse.wst.sse.core.events.IModelAboutToBeChangedListener;
import org.eclipse.wst.sse.core.events.IStructuredDocumentListener;
import org.eclipse.wst.sse.core.events.NewModelEvent;
import org.eclipse.wst.sse.core.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.internal.NotImplementedException;
import org.eclipse.wst.sse.core.internal.document.NullStructuredDocumentPartitioner;
import org.eclipse.wst.sse.core.parser.RegionParser;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegionList;
import org.eclipse.wst.sse.core.text.IStructuredTextReParser;


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

	public char getChar(int offset) throws BadLocationException {
		return data.get(offset);
	}

	public int getLength() {
		return data.getLength();
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
		}
		catch (StringIndexOutOfBoundsException e) {
			throw new BadLocationException();
		}
		return result;
	}

	public void set(String text) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
		//		data.set(text);
	}

	public void replace(int offset, int length, String text) throws BadLocationException {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
		//		data.replace(offset, length, text);
	}

	public String getText() {
		return data.get(0, data.getLength());
	}

	public void addDocumentListener(IDocumentListener listener) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void removeDocumentListener(IDocumentListener listener) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void addPrenotifiedDocumentListener(IDocumentListener documentAdapter) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void removePrenotifiedDocumentListener(IDocumentListener documentAdapter) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void addPositionCategory(String category) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void removePositionCategory(String category) throws BadPositionCategoryException {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public String[] getPositionCategories() {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public boolean containsPositionCategory(String category) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void addPosition(Position position) throws BadLocationException {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void removePosition(Position position) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void addPosition(String category, Position position) throws BadLocationException, BadPositionCategoryException {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void removePosition(String category, Position position) throws BadPositionCategoryException {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public Position[] getPositions(String category) throws BadPositionCategoryException {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public boolean containsPosition(String category, int offset, int length) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public int computeIndexInCategory(String category, int offset) throws BadLocationException, BadPositionCategoryException {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void addPositionUpdater(IPositionUpdater updater) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void removePositionUpdater(IPositionUpdater updater) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void insertPositionUpdater(IPositionUpdater updater, int index) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public IPositionUpdater[] getPositionUpdaters() {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public String[] getLegalContentTypes() {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public String getContentType(int offset) throws BadLocationException {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public ITypedRegion getPartition(int offset) throws BadLocationException {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public ITypedRegion[] computePartitioning(int offset, int length) throws BadLocationException {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void addDocumentPartitioningListener(IDocumentPartitioningListener listener) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void removeDocumentPartitioningListener(IDocumentPartitioningListener listener) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void setDocumentPartitioner(IDocumentPartitioner partitioner) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public IDocumentPartitioner getDocumentPartitioner() {
		// temp fix
		return new NullStructuredDocumentPartitioner();
		//		throw new NotImplementedException("intentionally not implemented");
	}

	public int getLineLength(int line) throws BadLocationException {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public int getLineOffset(int line) throws BadLocationException {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public IRegion getLineInformation(int line) throws BadLocationException {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public IRegion getLineInformationOfOffset(int offset) throws BadLocationException {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public int getNumberOfLines() {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public int getNumberOfLines(int offset, int length) throws BadLocationException {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public int computeNumberOfLines(String text) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public String[] getLegalLineDelimiters() {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public String getLineDelimiter(int line) throws BadLocationException {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public int search(int startOffset, String findString, boolean forwardSearch, boolean caseSensitive, boolean wholeWord) throws BadLocationException {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void addModelAboutToBeChangedListener(IModelAboutToBeChangedListener listener) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void addModelChangedListener(IStructuredDocumentListener listener) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void addModelChangingListener(IStructuredDocumentListener listener) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public IStructuredDocumentRegion getFirstStructuredDocumentRegion() {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public IStructuredDocumentRegion getLastStructuredDocumentRegion() {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public String getLineDelimiter() {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void setLineDelimiter(String delimiter) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public int getLineOfOffset(int offset) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public IStructuredDocumentRegion getRegionAtCharacterOffset(int offset) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public IStructuredDocumentRegionList getRegionList() {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void removeModelAboutToBeChangedListener(IModelAboutToBeChangedListener listener) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void removeModelChangedListener(IStructuredDocumentListener listener) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void removeModelChangingListener(IStructuredDocumentListener listener) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public StructuredDocumentEvent replaceText(Object source, int oldStart, int replacementLength, String requestedChange) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public NewModelEvent setText(Object requester, String allText) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public RegionParser getParser() {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public IStructuredDocument newInstance() {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public EncodingMemento getEncodingMemento() {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void setEncodingMemento(EncodingMemento encodingMemento) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public IStructuredTextReParser getReParser() {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public boolean containsReadOnly(int startOffset, int length) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void makeReadOnly(int startOffset, int length) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public void clearReadOnly(int startOffset, int length) {
		// TODO: this is called from notifier loop inappropriately
		//	throw new NotImplementedException("intentionally not implemented");
	}

	public void fireNewDocument(Object requester) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}

	public Object getAdapter(Class adapter) {
		throw new NotImplementedException("intentionally not implemented"); //$NON-NLS-1$
	}
}
