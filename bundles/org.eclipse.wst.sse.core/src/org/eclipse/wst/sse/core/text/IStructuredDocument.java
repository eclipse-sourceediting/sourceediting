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
package org.eclipse.wst.sse.core.text;



import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.wst.encoding.EncodingMemento;
import org.eclipse.wst.sse.core.document.IEncodedDocument;
import org.eclipse.wst.sse.core.events.IModelAboutToBeChangedListener;
import org.eclipse.wst.sse.core.events.IStructuredDocumentListener;
import org.eclipse.wst.sse.core.events.NewModelEvent;
import org.eclipse.wst.sse.core.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.parser.RegionParser;



/**
 * A IStructuredDocument is a collection of StructuredDocumentRegions. Its called "flat" because its contents
 * by design do not contain much structural information beyond containment.
 * 
 */
public interface IStructuredDocument extends IEncodedDocument, IAdaptable {

	void addModelAboutToBeChangedListener(IModelAboutToBeChangedListener listener);

	/**
	 * The StructuredDocumentListners and ModelChagnedListeners are very similar. 
	 * They both receive identical events. The difference is the timing.
	 * The "pure" StructuredDocumentListners are notified after the structuredDocument has
	 * been changed, but before other, related models may have been changed
	 * such as the Structural Model. The Structural model is in fact itself a
	 * "pure" StructuredDocumentListner. The ModelChangedListeners can rest assured
	 * that all models and data have been updated from the change by the tiem
	 * they are notified. This is especially important for the text widget, for example,
	 * which may rely on both structuredDocument and structural model information. 
	 */
	void addModelChangedListener(IStructuredDocumentListener listener);

	/**
	 * The StructuredDocumentListners and ModelChagnedListeners are very similar. 
	 * They both receive identical events. The difference is the timing.
	 * The "pure" StructuredDocumentListners are notified after the structuredDocument has
	 * been changed, but before other, related models may have been changed
	 * such as the Structural Model. The Structural model is in fact itself a
	 * "pure" StructuredDocumentListner. The ModelChangedListeners can rest assured
	 * that all models and data have been updated from the change by the tiem
	 * they are notified. This is especially important for the text widget, for example,
	 * which may rely on both structuredDocument and structural model information. 
	 */
	void addModelChangingListener(IStructuredDocumentListener listener);

	org.eclipse.wst.sse.core.text.IStructuredDocumentRegion getFirstStructuredDocumentRegion();

	org.eclipse.wst.sse.core.text.IStructuredDocumentRegion getLastStructuredDocumentRegion();

	/**
	 * This can be considered the preferred delimiter.
	 */
	public String getLineDelimiter();

	public void setLineDelimiter(String delimiter);

	int getLineOfOffset(int offset); // throws SourceEditingException;

	IStructuredDocumentRegion getRegionAtCharacterOffset(int offset);

	IStructuredDocumentRegionList getRegionList();

	String getText();

	void removeModelAboutToBeChangedListener(IModelAboutToBeChangedListener listener);

	void removeModelChangedListener(IStructuredDocumentListener listener);

	void removeModelChangingListener(IStructuredDocumentListener listener);

	/**
	 * One of the APIs to manipulate the IStructuredDocument.
	 *
	 * replaceText replaces the text from oldStart to oldEnd with the new text
	 * found in the requestedChange string. If oldStart and oldEnd are equal,
	 * it is an insertion request. If requestedChange is null (or empty) it
	 * is a delete request. Otherwise it is a replace request.
	 */
	StructuredDocumentEvent replaceText(Object source, int oldStart, int replacementLength, String requestedChange);

	/**
	 * One of the APIs to manipulate the IStructuredDocument in terms of Text.
	 *
	 * The setText method replaces all text in the model.
	 */
	NewModelEvent setText(Object requester, String allText);

	/**
	 * The parser is now required on constructor, so there are occasions
	 * it needs to be retrieved, such as to be initialized by EmbeddedContentType
	 */
	RegionParser getParser();

	/**
	 * newInstance is similar to clone, except it contains
	 * no data. One important thing to duplicate is the parser, 
	 * with the parser correctly "cloned", including its tokeninzer, 
	 * block tags, etc. 
	 * 
	 * NOTE: even after obtaining a 'newInstance' the client may have to 
	 * do some initialization, for example, it may need to add its own 
	 * model listeners. Or, as another example, if the IStructuredDocument has a parser
	 * of type StructuredDocumentRegionParser, then the client may need to add its own
	 * StructuredDocumentRegionHandler to that parser, if it is in fact needed.
	 */
	IStructuredDocument newInstance();

	/**
	 * This method is to remember info about the encoding
	 * When the resource was last loaded or saved. Note: 
	 * it is not kept "current", that is, can not be 
	 * depended on blindly to reflect what encoding to use.
	 * For that, must go through the normal rules expressed in 
	 * Loaders and Dumpers.
	 */

	EncodingMemento getEncodingMemento();

	/**
	 * This method is to remember info about the encoding
	 * When the resource was last loaded or saved. Note: 
	 * it is not kept "current", that is, can not be 
	 * depended on blindly to reflect what encoding to use.
	 * For that, must go through the normal rules expressed in 
	 * Loaders and Dumpers.
	 */
	void setEncodingMemento(EncodingMemento encodingMemento);


	/**
	 * Note: this method was made public, and part of the 
	 * interface, for easier testing. Clients normally never
	 * manipulate the reparser directly (nor should they need
	 * to).
	 */
	IStructuredTextReParser getReParser();

	/**
	 * returns true if any portion of startOffset to length is readonly
	 * @param startOffset
	 * @param length
	 * @return
	 */
	boolean containsReadOnly(int startOffset, int length);

	/**
	 * causes that portion of the document from startOffset to length to 
	 * be marked as readonly. Note that if this range overlaps with some 
	 * other region with is readonly, the regions are effectivly combined.
	 * @param startOffset
	 * @param length
	 */
	void makeReadOnly(int startOffset, int length);

	/**
	 * this API ensures that any portion of the document within startOff
	 * to length is not readonly (that is, that its editable). Note that if 
	 * the range overlaps with other readonly regions, those other readonly 
	 * regions will be adjusted. 
	 * @param startOffset
	 * @param length
	 */
	void clearReadOnly(int startOffset, int length);

	/**
	 * 
	 */
	void fireNewDocument(Object requester);
}
