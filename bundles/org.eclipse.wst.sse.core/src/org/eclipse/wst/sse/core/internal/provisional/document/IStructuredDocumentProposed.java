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
package org.eclipse.wst.sse.core.internal.provisional.document;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.Position;
import org.eclipse.wst.sse.core.internal.encoding.EncodingMemento;
import org.eclipse.wst.sse.core.internal.provisional.events.NewDocumentEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegionList;


/**
 * A IStructuredDocument is a collection of StructuredDocumentRegions. It's
 * often called a "flat model" because its does contain some structural
 * information, but not very much, usually, at most, a few levels of
 * containment.
 * 
 * Clients should not implement.
 * 
 * @deprecated - was never used
 */
public interface IStructuredDocumentProposed extends IDocument, IDocumentExtension, IAdaptable {

	/**
	 * The document changing listeners receives the same events as the
	 * document listeners, but the difference is the timing and
	 * synchronization of data changes and notifications.
	 */
	void addDocumentChangingListener(IDocumentListener listener);

	/**
	 * this API ensures that any portion of the document within startOff to
	 * length is not readonly (that is, that its editable). Note that if the
	 * range overlaps with other readonly regions, those other readonly
	 * regions will be adjusted.
	 * 
	 * @param startOffset
	 * @param length
	 */
	void clearReadOnly(int startOffset, int length);

	/**
	 * returns true if any portion of startOffset to length is readonly
	 * 
	 * @param startOffset
	 * @param length
	 * @return
	 */
	boolean containsReadOnly(int startOffset, int length);

	/**
	 * Returns the region contained by offset.
	 * 
	 * @param offset
	 * @return
	 */
	IStructuredDocumentRegion getRegionAtCharacterOffset(int offset);

	/**
	 * Resturns a list of the structured document regions.
	 * 
	 * Note: possibly expensive call, not to be used casually.
	 * 
	 * @return a list of the structured document regions.
	 */
	IStructuredDocumentRegionList getRegionList();


	/**
	 * Returns the text of this document.
	 * 
	 * Same as 'get' in super class, added for descriptiveness.
	 * 
	 * @return the text of this document.
	 */
	String getText();

	/**
	 * causes that portion of the document from startOffset to length to be
	 * marked as readonly. Note that if this range overlaps with some other
	 * region with is readonly, the regions are effectivly combined.
	 * 
	 * @param startOffset
	 * @param length
	 */
	void makeReadOnly(int startOffset, int length);

	/**
	 * newInstance is similar to clone, except it contains no data. One
	 * important thing to duplicate is the parser, with the parser correctly
	 * "cloned", including its tokeninzer, block tags, etc.
	 * 
	 * NOTE: even after obtaining a 'newInstance' the client may have to do
	 * some initialization, for example, it may need to add its own model
	 * listeners. Or, as another example, if the IStructuredDocument has a
	 * parser of type StructuredDocumentRegionParser, then the client may need
	 * to add its own StructuredDocumentRegionHandler to that parser, if it is
	 * in fact needed.
	 */
	IStructuredDocumentProposed newInstance();

	/**
	 * The document changing listeners receives the same events as the
	 * document listeners, but the difference is the timing and
	 * synchronization of data changes and notifications.
	 */
	void removeDocumentChangingListener(IDocumentListener listener);

	/**
	 * One of the APIs to manipulate the IStructuredDocument.
	 * 
	 * replaceText replaces the text from oldStart to oldEnd with the new text
	 * found in the requestedChange string. If oldStart and oldEnd are equal,
	 * it is an insertion request. If requestedChange is null (or empty) it is
	 * a delete request. Otherwise it is a replace request.
	 * 
	 * Similar to 'replace' in super class.
	 */
	StructuredDocumentEvent replaceText(Object requester, int oldStart, int replacementLength, String requestedChange);

	/**
	 * Note, same as replaceText API, but will allow readonly areas to be
	 * replaced. This method is not to be called by clients, only
	 * infrastructure. For example, one case where its ok is with undo
	 * operations (since, presumably, if user just did something that
	 * happended to involve some inserting readonly text, they should normally
	 * be allowed to still undo that operation. There might be other cases
	 * where its used to give the user a choice, e.g. "you are about to
	 * overwrite read only portions, do you want to continue".
	 */
	StructuredDocumentEvent overrideReadOnlyreplaceText(Object requester, int oldStart, int replacementLength, String requestedChange);

	/**
	 * One of the APIs to manipulate the IStructuredDocument in terms of Text.
	 * 
	 * The setText method replaces all text in the model.
	 * 
	 * @param requester -
	 *            the object requesting the document be created.
	 * @param allText -
	 *            all the text of the document.
	 * @return NewDocumentEvent - besides causing this event to be sent to
	 *         document listeners, the event is returned.
	 */
	NewDocumentEvent setText(Object requester, String allText);

	/**
	 * Returns the encoding memento for this document.
	 * 
	 * @return the encoding memento for this document.
	 */
	EncodingMemento getEncodingMemento();

	/**
	 * Returns the line delimiter detected when this document was read from
	 * storage.
	 * 
	 * @return line delimiter detected when this document was read from
	 *         storage.
	 */
	String getDetectedLineDelimiter();

	/**
	 * Sets the encoding memento for this document.
	 * 
	 * Is not to be called by clients, only document creation classes.
	 * 
	 * @param localEncodingMemento
	 */
	void setEncodingMemento(EncodingMemento localEncodingMemento);

	/**
	 * Sets the detected line delimiter when the document was read. Is not to
	 * be called by clients, only document creation classes.
	 * 
	 * @param probableLineDelimiter
	 */
	void setDetectedLineDelimiter(String probableLineDelimiter);

	/**
	 * This function provides a way for clients to compare a string with a
	 * region of the documnet, without having to retrieve (create) a string
	 * from the document, thus more efficient of lots of comparisons being
	 * done.
	 * 
	 * @param ignoreCase -
	 *            if true the characters are compared based on identity. If
	 *            false, the string are compared with case accounted for.
	 * @param testString -
	 *            the string to compare.
	 * @param documentOffset -
	 *            the document in the offset to start the comparison.
	 * @param length -
	 *            the length in the document to compare (Note: technically,
	 *            clients could just provide the string, and we could infer
	 *            the length from the string supplied, but this leaves every
	 *            client to correctly not even ask us if the the string length
	 *            doesn't match the expected length, so this is an effort to
	 *            maximize performance with correct code.
	 * @return true if matches, false otherwise.
	 */
	boolean stringMatches(boolean ignoreCase, String testString, int documentOffset, int length);
	
	Position createPosition(int offset, String category, String type);
	
	Position createPosition(int offset, int length, String category, String type);

}
