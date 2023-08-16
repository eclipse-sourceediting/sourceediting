/*******************************************************************************
 * Copyright (c) 2023 Nitin Dahyabhai
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Nitin Dahyabhai - initial implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.core.internal.text;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.wst.sse.core.internal.encoding.EncodingMemento;
import org.eclipse.wst.sse.core.internal.ltk.parser.RegionParser;
import org.eclipse.wst.sse.core.internal.provisional.events.IModelAboutToBeChangedListener;
import org.eclipse.wst.sse.core.internal.provisional.events.IStructuredDocumentListener;
import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegionList;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredTextReParser;
import org.eclipse.wst.sse.core.internal.text.BasicStructuredDocument.CurrentDocumentRegionCache;
import org.eclipse.wst.sse.core.internal.text.BasicStructuredDocument.NullDocumentEvent;
import org.eclipse.wst.sse.core.internal.undo.IStructuredTextUndoManager;

public class DelegatingStructuredDocument extends Document implements IStructuredDocument {

	private IDocument fDelegateDocument = null;
	private RegionParser fReParser;
	private IDocumentListener fDelegateBridge;
	private IStructuredDocumentRegion cachedDocumentRegion;
	private EncodingMemento encodingMemento;
	private CurrentDocumentRegionCache fCurrentDocumentRegionCache;

	/**
	 * Theoretically, a document can contain mixed line delimiters, but the
	 * user's preference is usually to be internally consistent.
	 */
	private String fInitialLineDelimiter;
	private IStructuredDocumentRegion firstDocumentRegion;
	private Object[] fStructuredDocumentAboutToChangeListeners;
	private Object[] fStructuredDocumentChangedListeners;
	private Object[] fStructuredDocumentChangingListeners;
	private NullDocumentEvent NULL_DOCUMENT_EVENT;

	DelegatingStructuredDocument() {
	}

	public DelegatingStructuredDocument(RegionParser parser, IDocument delegate) {
		super();
		fReParser = parser;
		fDelegateDocument = delegate;
		fDelegateDocument.addDocumentListener(fDelegateBridge);
		fDelegateDocument.addPrenotifiedDocumentListener(null);
	}

	@Override
	public String getPreferredLineDelimiter() {
		return fInitialLineDelimiter;
	}

	@Override
	public void setPreferredLineDelimiter(String preferredLineDelimiter) {
		fInitialLineDelimiter = preferredLineDelimiter;
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	@Override
	public void addDocumentAboutToChangeListener(IModelAboutToBeChangedListener listener) {
	}

	@Override
	public void addDocumentChangedListener(IStructuredDocumentListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addDocumentChangingListener(IStructuredDocumentListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearReadOnly(int startOffset, int length) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean containsReadOnly(int startOffset, int length) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public EncodingMemento getEncodingMemento() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStructuredDocumentRegion getFirstStructuredDocumentRegion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStructuredDocumentRegion getLastStructuredDocumentRegion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLineDelimiter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RegionParser getParser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStructuredDocumentRegionList getRegionList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStructuredDocumentRegion getRegionAtCharacterOffset(int offset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStructuredDocumentRegion[] getStructuredDocumentRegions(int offset, int length) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStructuredDocumentRegion[] getStructuredDocumentRegions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStructuredTextReParser getReParser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStructuredTextUndoManager getUndoManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void makeReadOnly(int startOffset, int length) {
		// TODO Auto-generated method stub

	}

	@Override
	public IStructuredDocument newInstance() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeDocumentAboutToChangeListener(IModelAboutToBeChangedListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeDocumentChangedListener(IStructuredDocumentListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeDocumentChangingListener(IStructuredDocumentListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public StructuredDocumentEvent replaceText(Object source, int oldStart, int replacementLength, String requestedChange) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StructuredDocumentEvent replaceText(Object source, int oldStart, int replacementLength, String requestedChange, boolean ignoreReadOnlySetting) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setEncodingMemento(EncodingMemento encodingMemento) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLineDelimiter(String delimiter) {
		// TODO Auto-generated method stub

	}

	@Override
	public StructuredDocumentEvent setText(Object requester, String allText) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setUndoManager(IStructuredTextUndoManager undoManager) {
		// TODO Auto-generated method stub

	}

}
