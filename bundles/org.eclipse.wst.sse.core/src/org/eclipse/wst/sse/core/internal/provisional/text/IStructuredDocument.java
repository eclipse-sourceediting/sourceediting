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
package org.eclipse.wst.sse.core.internal.provisional.text;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.text.IDocumentExtension;
import org.eclipse.wst.sse.core.internal.encoding.EncodingMemento;
import org.eclipse.wst.sse.core.internal.ltk.parser.RegionParser;
import org.eclipse.wst.sse.core.internal.provisional.document.IEncodedDocument;
import org.eclipse.wst.sse.core.internal.provisional.events.IModelAboutToBeChangedListener;
import org.eclipse.wst.sse.core.internal.provisional.events.IStructuredDocumentListener;
import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.internal.undo.IStructuredTextUndoManager;


/**
 * A IStructuredDocument is a collection of StructuredDocumentRegions. It's
 * often called "flat" because its contents by design do not contain much
 * structural information beyond containment. Clients should not implement.
 */
public interface IStructuredDocument extends IEncodedDocument, IDocumentExtension, IAdaptable {

	void addDocumentAboutToChangeListener(IModelAboutToBeChangedListener listener);

	/**
	 * The StructuredDocumentListeners and ModelChangedListeners are very
	 * similar. They both receive identical events. The difference is the
	 * timing. The "pure" StructuredDocumentListeners are notified after the
	 * structuredDocument has been changed, but before other, related models
	 * may have been changed such as the Structural Model. The Structural
	 * model is in fact itself a "pure" StructuredDocumentListner. The
	 * ModelChangedListeners can rest assured that all models and data have
	 * been updated from the change by the tiem they are notified. This is
	 * especially important for the text widget, for example, which may rely
	 * on both structuredDocument and structural model information.
	 */
	void addDocumentChangedListener(IStructuredDocumentListener listener);

	/**
	 * The StructuredDocumentListeners and ModelChangedListeners are very
	 * similar. They both receive identical events. The difference is the
	 * timing. The "pure" StructuredDocumentListeners are notified after the
	 * structuredDocument has been changed, but before other, related models
	 * may have been changed such as the Structural Model. The Structural
	 * model is in fact itself a "pure" StructuredDocumentListner. The
	 * ModelChangedListeners can rest assured that all models and data have
	 * been updated from the change by the tiem they are notified. This is
	 * especially important for the text widget, for example, which may rely
	 * on both structuredDocument and structural model information.
	 */
	void addDocumentChangingListener(IStructuredDocumentListener listener);

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
	 * This method is to remember info about the encoding When the resource
	 * was last loaded or saved. Note: it is not kept "current", that is, can
	 * not be depended on blindly to reflect what encoding to use. For that,
	 * must go through the normal rules expressed in Loaders and Dumpers.
	 */

	EncodingMemento getEncodingMemento();

	org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion getFirstStructuredDocumentRegion();

	org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion getLastStructuredDocumentRegion();

	/**
	 * This can be considered the preferred delimiter.
	 */
	public String getLineDelimiter();

	int getLineOfOffset(int offset); // throws SourceEditingException;

	/**
	 * The parser is now required on constructor, so there are occasions it
	 * needs to be retrieved, such as to be initialized by EmbeddedContentType
	 */
	RegionParser getParser();

	/**
	 * @deprecated use getStructuredDocumentRegions()
	 * @return
	 */
	IStructuredDocumentRegionList getRegionList();

	/**
	 * Returns the <code>IStructuredDocumentRegion</code> at the given character offset.
	 * @param offset
	 * @return the <code>IStructuredDocumentRegion</code> at the given character offset.
	 */
	IStructuredDocumentRegion getRegionAtCharacterOffset(int offset);
	
	/**
	 * Returns <code>IStructuredDocumentRegion</code>s in the specified range.
	 * @param offset
	 * @param length
	 * @return <code>IStructuredDocumentRegion</code>s in the specified range.
	 */
	IStructuredDocumentRegion[] getStructuredDocumentRegions(int offset, int length);
	
	/**
	 * Returns all <code>IStructuredDocumentRegion</code>s in the document.
	 * @return all <code>IStructuredDocumentRegion</code>s in the document.
	 */
	IStructuredDocumentRegion[] getStructuredDocumentRegions();
	
	/**
	 * Note: this method was made public, and part of the interface, for
	 * easier testing. Clients normally never manipulate the reparser directly
	 * (nor should they need to).
	 */
	IStructuredTextReParser getReParser();

	String getText();

	IStructuredTextUndoManager getUndoManager();

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
	IStructuredDocument newInstance();

	void removeDocumentAboutToChangeListener(IModelAboutToBeChangedListener listener);

	void removeDocumentChangedListener(IStructuredDocumentListener listener);

	void removeDocumentChangingListener(IStructuredDocumentListener listener);

	/**
	 * One of the APIs to manipulate the IStructuredDocument.
	 * 
	 * replaceText replaces the text from oldStart to oldEnd with the new text
	 * found in the requestedChange string. If oldStart and oldEnd are equal,
	 * it is an insertion request. If requestedChange is null (or empty) it is
	 * a delete request. Otherwise it is a replace request.
	 */
	StructuredDocumentEvent replaceText(Object source, int oldStart, int replacementLength, String requestedChange);

	/**
	 * Note, same as replaceText API, but will allow readonly areas to be
	 * replaced. This should seldom be called with a value of "true" for
	 * ignoreReadOnlySetting. One case where its ok is with undo operations
	 * (since, presumably, if user just did something that happended to
	 * involve some inserting readonly text, they should normally be allowed
	 * to still undo that operation. Otherwise, I can't think of a single
	 * example, unless its to give the user a choice, e.g. "you are about to
	 * overwrite read only portions, do you want to continue".
	 */
	StructuredDocumentEvent replaceText(Object source, int oldStart, int replacementLength, String requestedChange, boolean ignoreReadOnlySetting);

	/**
	 * This method is to remember info about the encoding When the resource
	 * was last loaded or saved. Note: it is not kept "current", that is, can
	 * not be depended on blindly to reflect what encoding to use. For that,
	 * must go through the normal rules expressed in Loaders and Dumpers.
	 */
	void setEncodingMemento(EncodingMemento encodingMemento);

	public void setLineDelimiter(String delimiter);

	/**
	 * One of the APIs to manipulate the IStructuredDocument in terms of Text.
	 * 
	 * The setText method replaces all text in the model.
	 */
	StructuredDocumentEvent setText(Object requester, String allText);

	void setUndoManager(IStructuredTextUndoManager undoManager);

}
