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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPartitioningException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.DocumentPartitioningChangedEvent;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IDocumentPartitionerExtension2;
import org.eclipse.jface.text.IDocumentPartitioningListener;
import org.eclipse.jface.text.IDocumentPartitioningListenerExtension;
import org.eclipse.jface.text.IDocumentPartitioningListenerExtension2;
import org.eclipse.jface.text.IPositionUpdater;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TypedRegion;
import org.eclipse.wst.encoding.EncodingMemento;
import org.eclipse.wst.sse.core.events.AboutToBeChangeEvent;
import org.eclipse.wst.sse.core.events.IModelAboutToBeChangedListener;
import org.eclipse.wst.sse.core.events.IStructuredDocumentListener;
import org.eclipse.wst.sse.core.events.NewModelEvent;
import org.eclipse.wst.sse.core.events.NoChangeEvent;
import org.eclipse.wst.sse.core.events.RegionChangedEvent;
import org.eclipse.wst.sse.core.events.RegionsReplacedEvent;
import org.eclipse.wst.sse.core.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.events.StructuredDocumentRegionsReplacedEvent;
import org.eclipse.wst.sse.core.exceptions.SourceEditingRuntimeException;
import org.eclipse.wst.sse.core.internal.Logger;
import org.eclipse.wst.sse.core.parser.RegionParser;
import org.eclipse.wst.sse.core.text.DeleteEqualPositionUpdater;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegionList;
import org.eclipse.wst.sse.core.text.IStructuredTextReParser;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.core.text.ITextRegionList;
import org.eclipse.wst.sse.core.text.rules.StructuredTextPartitioner;
import org.eclipse.wst.sse.core.util.Assert;
import org.eclipse.wst.sse.core.util.Debug;
import org.eclipse.wst.sse.core.util.Utilities;


/**
 * The standard implementation of structured document.
 */
public class BasicStructuredDocument implements IStructuredDocument, IDocumentExtension, IDocumentExtension3 {
	/**
	 * This NullDocumentEvent is used to complete the "aboutToChange" and
	 * "changed" cycle, when in fact the original change is no longer valid.
	 * The only known (valid) case of this is when a model re-initialize takes
	 * place, which causes setText to be called in the middle of some previous
	 * change. [This architecture will be improved in future].
	 */
	public class NullDocumentEvent extends DocumentEvent {
		public NullDocumentEvent() {
			this(BasicStructuredDocument.this, 0, 0, ""); //$NON-NLS-1$
		}

		private NullDocumentEvent(IDocument doc, int offset, int length, String text) {
			super(doc, offset, length, text);
		}
	}

	class RegisteredReplace {
		/** The owner of this replace operation. */
		IDocumentListener fOwner;
		/** The replace operation */
		IDocumentExtension.IReplace fReplace;

		/**
		 * Creates a new bundle object.
		 * 
		 * @param owner
		 *            the document listener owning the replace operation
		 * @param replace
		 *            the replace operation
		 */
		RegisteredReplace(IDocumentListener owner, IDocumentExtension.IReplace replace) {
			fOwner = owner;
			fReplace = replace;
		}
	}

	private class ThreadLocalDocumentRegion extends ThreadLocal {
		protected Object initialValue() {
			IStructuredDocumentRegion structuredDocumentRegion = getFirstStructuredDocumentRegion();
			set(structuredDocumentRegion);
			return structuredDocumentRegion;
		}
	}
	/** The registered document partitioning listeners */
	private List fDocumentPartitioningListeners;

	/**
	 * The registered document partitioners.
	 * @since 3.0
	 */
	private Map fDocumentPartitioners;	private ThreadLocalDocumentRegion threadLocalDocumentRegion = this.new ThreadLocalDocumentRegion();
	private IStructuredDocumentRegion cachedDocumentRegion;
	private EncodingMemento encodingMemento;
	private boolean fAcceptPostNotificationReplaces = true;
	private DocumentEvent fDocumentEvent;
	private IDocumentListener[] fDocumentListeners;
	private IStructuredDocumentRegion firstDocumentRegion;
	private Object[] fModelAboutToBeChangedListeners;
	private Object[] fModelChangedListeners;
	private Object[] fModelChangingListeners;
	private RegionParser fParser;
	private List fPostNotificationChanges;
	private IDocumentListener[] fPrenotifiedDocumentListeners;
	private int fReentranceCount = 0;
	private IStructuredTextReParser fReParser;
	private int fStoppedCount = 0;
	private IStructuredDocumentRegion lastDocumentRegion;
	private NullDocumentEvent NULL_DOCUMENT_EVENT;
	private IDocument parentDocument;
	//
	/**
	 * in case preferred delimiter is not set, we'll assume the platform
	 * default Note: it is not final static to make sure it won't be inlined
	 * by compiler.
	 */
	private final String PlatformLineDelimiter = System.getProperty("line.separator"); //$NON-NLS-1$
	/**
	 * theoretically, a document can contain mixed line delimiters
	 */
	private String preferedDelimiter;
	private final String READ_ONLY_REGIONS_CATEGORY = "_READ_ONLY_REGIONS_CATEGORY_"; //$NON-NLS-1$
	/**
	 * debug variable only
	 * 
	 * @param parser
	 */
	private long startStreamTime;
	/**
	 * debug variable only
	 * 
	 * @param parser
	 */
	private long startTime;
	private static boolean USE_LOCAL_THREAD = false;

	public BasicStructuredDocument() {
		super();
		NULL_DOCUMENT_EVENT = new NullDocumentEvent();
		parentDocument = new Document();
		addPositionCategory(READ_ONLY_REGIONS_CATEGORY);
		addPositionUpdater(new DeleteEqualPositionUpdater(READ_ONLY_REGIONS_CATEGORY));
	}

	/**
	 * This is the primary way to get a new structuredDocument. Its best to
	 * use the factory methods in ModelManger to create a new
	 * IStructuredDocument, since it will get and initialize the parser
	 * according to the desired content type.
	 */
	public BasicStructuredDocument(RegionParser parser) {
		super();
		Assert.isNotNull(parser, "Program Error: IStructuredDocument can not be created with null parser"); //$NON-NLS-1$
		NULL_DOCUMENT_EVENT = new NullDocumentEvent();
		parentDocument = new Document();
		addPositionCategory(READ_ONLY_REGIONS_CATEGORY);
		addPositionUpdater(new DeleteEqualPositionUpdater(READ_ONLY_REGIONS_CATEGORY));
		// go through setter in case there is side effects
		setParser(parser);
	}

	private void _clearDocumentEvent() {
		// no hard and fast requirement to null out ... just seems like
		// a good idea, since we are done with it.
		fDocumentEvent = null;
	}

	private void _fireDocumentAboutToChange(Object[] listeners) {
		if (fDocumentPartitioners != null) {
			Iterator e= fDocumentPartitioners.values().iterator();
			while (e.hasNext()) {
				IDocumentPartitioner p= (IDocumentPartitioner) e.next();
				p.documentAboutToBeChanged(fDocumentEvent);
			}
		}
		// we must assign listeners to local variable, since the add and
		// remove
		// listner
		// methods can change the actual instance of the listener array from
		// another thread
		if (listeners != null) {
			Object[] holdListeners = listeners;
			// Note: the docEvent is created in replaceText API
			// fire
			for (int i = 0; i < holdListeners.length; i++) {
				if (Debug.perfTest || Debug.perfTestStructuredDocumentEventOnly) {
					startTime = System.currentTimeMillis();
				}
				// this is a safe cast, since addListners requires a
				// IStructuredDocumentListener
				((IDocumentListener) holdListeners[i]).documentAboutToBeChanged(fDocumentEvent);
				if (Debug.perfTest || Debug.perfTestStructuredDocumentEventOnly) {
					long stopTime = System.currentTimeMillis();
					System.out.println("\n\t\t\t\t IStructuredDocument::fireStructuredDocumentEvent. Time was " + (stopTime - startTime) + " msecs to fire NewModelEvent to instance of " + holdListeners[i].getClass()); //$NON-NLS-2$//$NON-NLS-1$
				}
			}
		}
	}

	private void _fireDocumentChanged(Object[] listeners, StructuredDocumentEvent event) {
		if (fDocumentPartitioners != null) {
			Iterator e= fDocumentPartitioners.values().iterator();
			while (e.hasNext()) {
				IDocumentPartitioner p= (IDocumentPartitioner) e.next();
				p.documentChanged(fDocumentEvent);
			}
		}
		// we must assign listeners to local variable, since the add and
		// remove
		// listner
		// methods can change the actual instance of the listener array from
		// another thread
		if (listeners != null) {
			Object[] holdListeners = listeners;
			// NOTE: document event is created in replace Text API and setText
			// API
			// now fire
			for (int i = 0; i < holdListeners.length; i++) {
				if (Debug.perfTest || Debug.perfTestStructuredDocumentEventOnly) {
					startTime = System.currentTimeMillis();
				}
				// this is a safe cast, since addListners requires a
				// IStructuredDocumentListener
				// Notes: fDocumentEvent can be "suddenly" null, if one of the
				// previous changes
				// caused a "setText" to be called. The only known case of
				// this
				// is a model reset
				// due to page directive changing. Eventually we should change
				// archetecture to have
				// event que and be able to "cancel" pending events, but for
				// now, we'll just pass a
				// NullDocumentEvent. By the way, it is important to send
				// something, since clients might
				// have indeterminant state due to "aboutToChange" being sent
				// earlier.
				if (fDocumentEvent == null) {
					((IDocumentListener) holdListeners[i]).documentChanged(NULL_DOCUMENT_EVENT);
				}
				else {
					((IDocumentListener) holdListeners[i]).documentChanged(fDocumentEvent);
				}
				if (Debug.perfTest || Debug.perfTestStructuredDocumentEventOnly) {
					long stopTime = System.currentTimeMillis();
					System.out.println("\n\t\t\t\t IStructuredDocument::fireStructuredDocumentEvent. Time was " + (stopTime - startTime) + " msecs to fire NewModelEvent to instance of " + holdListeners[i].getClass()); //$NON-NLS-2$//$NON-NLS-1$
				}
			}
		}
	}

	private void _fireEvent(Object[] listeners, NewModelEvent event) {
		// we must assign listeners to local variable, since the add and
		// remove
		// listner
		// methods can change the actual instance of the listener array from
		// another thread
		if (listeners != null) {
			Object[] holdListeners = listeners;
			for (int i = 0; i < holdListeners.length; i++) {
				if (Debug.perfTest || Debug.perfTestStructuredDocumentEventOnly) {
					startTime = System.currentTimeMillis();
				}
				// this is a safe cast, since addListners requires a
				// IStructuredDocumentListener
				((IStructuredDocumentListener) holdListeners[i]).newModel(event);
				if (Debug.perfTest || Debug.perfTestStructuredDocumentEventOnly) {
					long stopTime = System.currentTimeMillis();
					System.out.println("\n\t\t\t\t IStructuredDocument::fireStructuredDocumentEvent. Time was " + (stopTime - startTime) + " msecs to fire NewModelEvent to instance of " + holdListeners[i].getClass()); //$NON-NLS-2$//$NON-NLS-1$
				}
			}
		}
	}

	private void _fireEvent(Object[] listeners, NoChangeEvent event) {
		// we must assign listeners to local variable, since the add and
		// remove
		// listner
		// methods can change the actual instance of the listener array from
		// another thread
		if (listeners != null) {
			Object[] holdListeners = listeners;
			for (int i = 0; i < holdListeners.length; i++) {
				if (Debug.perfTest || Debug.perfTestStructuredDocumentEventOnly) {
					startTime = System.currentTimeMillis();
				}
				// this is a safe cast, since addListners requires a
				// IStructuredDocumentListener
				((IStructuredDocumentListener) holdListeners[i]).noChange(event);
				if (Debug.perfTest || Debug.perfTestStructuredDocumentEventOnly) {
					long stopTime = System.currentTimeMillis();
					System.out.println("\n\t\t\t\t IStructuredDocument::fireStructuredDocumentEvent. Time was " + (stopTime - startTime) + " msecs to fire NewModelEvent to instance of " + holdListeners[i].getClass()); //$NON-NLS-2$//$NON-NLS-1$
				}
			}
		}
	}

	private void _fireEvent(Object[] listeners, StructuredDocumentRegionsReplacedEvent event) {
		// we must assign listeners to local variable, since the add and
		// remove
		// listner
		// methods can change the actual instance of the listener array from
		// another thread
		if (listeners != null) {
			Object[] holdListeners = listeners;
			for (int i = 0; i < holdListeners.length; i++) {
				if (Debug.perfTest || Debug.perfTestStructuredDocumentEventOnly) {
					startTime = System.currentTimeMillis();
				}
				// this is a safe cast, since addListners requires a
				// IStructuredDocumentListener
				((IStructuredDocumentListener) holdListeners[i]).nodesReplaced(event);
				if (Debug.perfTest || Debug.perfTestStructuredDocumentEventOnly) {
					long stopTime = System.currentTimeMillis();
					System.out.println("\n\t\t\t\t IStructuredDocument::fireStructuredDocumentEvent. Time was " + (stopTime - startTime) + " msecs to fire NewModelEvent to instance of " + holdListeners[i].getClass()); //$NON-NLS-2$//$NON-NLS-1$
				}
			}
		}
	}

	private void _fireEvent(Object[] listeners, RegionChangedEvent event) {
		// we must assign listeners to local variable, since the add and
		// remove
		// listner
		// methods can change the actual instance of the listener array from
		// another thread
		if (listeners != null) {
			Object[] holdListeners = listeners;
			for (int i = 0; i < holdListeners.length; i++) {
				if (Debug.perfTest || Debug.perfTestStructuredDocumentEventOnly) {
					startTime = System.currentTimeMillis();
				}
				// this is a safe cast, since addListners requires a
				// IStructuredDocumentListener
				((IStructuredDocumentListener) holdListeners[i]).regionChanged(event);
				if (Debug.perfTest || Debug.perfTestStructuredDocumentEventOnly) {
					long stopTime = System.currentTimeMillis();
					System.out.println("\n\t\t\t\t IStructuredDocument::fireStructuredDocumentEvent. Time was " + (stopTime - startTime) + " msecs to fire NewModelEvent to instance of " + holdListeners[i].getClass()); //$NON-NLS-2$//$NON-NLS-1$
				}
			}
		}
	}

	private void _fireEvent(Object[] listeners, RegionsReplacedEvent event) {
		// we must assign listeners to local variable, since the add and
		// remove
		// listner
		// methods can change the actual instance of the listener array from
		// another thread
		if (listeners != null) {
			Object[] holdListeners = listeners;
			for (int i = 0; i < holdListeners.length; i++) {
				if (Debug.perfTest || Debug.perfTestStructuredDocumentEventOnly) {
					startTime = System.currentTimeMillis();
				}
				// this is a safe cast, since addListners requires a
				// IStructuredDocumentListener
				((IStructuredDocumentListener) holdListeners[i]).regionsReplaced(event);
				if (Debug.perfTest || Debug.perfTestStructuredDocumentEventOnly) {
					long stopTime = System.currentTimeMillis();
					System.out.println("\n\t\t\t\t IStructuredDocument::fireStructuredDocumentEvent. Time was " + (stopTime - startTime) + " msecs to fire NewModelEvent to instance of " + holdListeners[i].getClass()); //$NON-NLS-2$//$NON-NLS-1$
				}
			}
		}
	}

	private void _fireModelAboutToBeChanged(Object[] listeners) {
		// we must assign listeners to local variable, since the add and
		// remove
		// listner
		// methods can change the actual instance of the listener array from
		// another thread
		if (listeners != null) {
			Object[] holdListeners = listeners;
			// Note: the docEvent is created in replaceText API
			// fire
			for (int i = 0; i < holdListeners.length; i++) {
				if (Debug.perfTest || Debug.perfTestStructuredDocumentEventOnly) {
					startTime = System.currentTimeMillis();
				}
				// notice the AboutToBeChangedEvent is created from the
				// DocumentEvent, since it is (nearly)
				// the same information. ?What to do about originalRequester?
				AboutToBeChangeEvent aboutToBeChangedEvent = new AboutToBeChangeEvent(this, null, fDocumentEvent.getText(), fDocumentEvent.getOffset(), fDocumentEvent.getLength());
				// this is a safe cast, since addListners requires a
				// IStructuredDocumentListener
				((IModelAboutToBeChangedListener) holdListeners[i]).modelAboutToBeChanged(aboutToBeChangedEvent);
				if (Debug.perfTest || Debug.perfTestStructuredDocumentEventOnly) {
					long stopTime = System.currentTimeMillis();
					System.out.println("\n\t\t\t\t IStructuredDocument::fireStructuredDocumentEvent. Time was " + (stopTime - startTime) + " msecs to fire NewModelEvent to instance of " + holdListeners[i].getClass()); //$NON-NLS-2$//$NON-NLS-1$
				}
			}
		}
	}

	/**
	 * We manage our own document listners, instead of delegating to our
	 * parentDocument, so we can fire at very end (and not when the
	 * parentDocument changes).
	 */
	public synchronized void addDocumentListener(IDocumentListener listener) {
		// make sure listener is not already in listening
		// (and if it is, print a warning to aid debugging, if needed)
		if (!Utilities.contains(fDocumentListeners, listener)) {
			int oldSize = 0;
			if (fDocumentListeners != null) {
				// normally won't be null, but we need to be sure, for first
				// time through
				oldSize = fDocumentListeners.length;
			}
			int newSize = oldSize + 1;
			IDocumentListener[] newListeners = new IDocumentListener[newSize];
			if (fDocumentListeners != null) {
				System.arraycopy(fDocumentListeners, 0, newListeners, 0, oldSize);
			}
			// add listener to last position
			newListeners[newSize - 1] = listener;
			//
			// now switch new for old
			fDocumentListeners = newListeners;
			//
		}
	}

	/**
	 * addModelAboutToBeChangedListener method comment.
	 */
	public synchronized void addModelAboutToBeChangedListener(IModelAboutToBeChangedListener listener) {
		// make sure listener is not already in listening
		// (and if it is, print a warning to aid debugging, if needed)
		if (!Utilities.contains(fModelAboutToBeChangedListeners, listener)) {
			int oldSize = 0;
			if (fModelAboutToBeChangedListeners != null) {
				// normally won't be null, but we need to be sure, for first
				// time through
				oldSize = fModelAboutToBeChangedListeners.length;
			}
			int newSize = oldSize + 1;
			Object[] newListeners = new Object[newSize];
			if (fModelAboutToBeChangedListeners != null) {
				System.arraycopy(fModelAboutToBeChangedListeners, 0, newListeners, 0, oldSize);
			}
			// add listener to last position
			newListeners[newSize - 1] = listener;
			//
			// now switch new for old
			fModelAboutToBeChangedListeners = newListeners;
			//
		}
	}

	/**
	 * The StructuredDocumentListners and ModelChagnedListeners are very
	 * similar. They both receive identical events. The difference is the
	 * timing. The "pure" StructuredDocumentListners are notified after the
	 * structuredDocument has been changed, but before other, related models
	 * may have been changed such as the Structural Model. The Structural
	 * model is in fact itself a "pure" StructuredDocumentListner. The
	 * ModelChangedListeners can rest assured that all models and data have
	 * been updated from the change by the tiem they are notified. This is
	 * especially important for the text widget, for example, which may rely
	 * on both structuredDocument and structural model information.
	 */
	public synchronized void addModelChangedListener(IStructuredDocumentListener listener) {
		if (Debug.debugStructuredDocument) {
			System.out.println("IStructuredDocument::addModelChangedListener. Request to add an instance of " + listener.getClass() + " as a listener on structuredDocument."); //$NON-NLS-2$//$NON-NLS-1$
		}
		// make sure listener is not already in listening
		// (and if it is, print a warning to aid debugging, if needed)
		if (Utilities.contains(fModelChangedListeners, listener)) {
			if (Debug.displayWarnings) {
				System.out.println("IStructuredDocument::addModelChangedListener. listener " + listener + " was addeded more than once. "); //$NON-NLS-2$//$NON-NLS-1$
			}
		}
		else {
			if (Debug.debugStructuredDocument) {
				System.out.println("IStructuredDocument::addModelChangedListener. Adding an instance of " + listener.getClass() + " as a listener on structuredDocument."); //$NON-NLS-2$//$NON-NLS-1$
			}
			int oldSize = 0;
			if (fModelChangedListeners != null) {
				// normally won't be null, but we need to be sure, for first
				// time through
				oldSize = fModelChangedListeners.length;
			}
			int newSize = oldSize + 1;
			Object[] newListeners = new Object[newSize];
			if (fModelChangedListeners != null) {
				System.arraycopy(fModelChangedListeners, 0, newListeners, 0, oldSize);
			}
			// add listener to last position
			newListeners[newSize - 1] = listener;
			//
			// now switch new for old
			fModelChangedListeners = newListeners;
			//
			// when a listener is added,
			// send the new model event to that one particular listener, so it
			// can initialize itself with the current state of the model
			//listener.newModel(new NewModelEvent(this, listener));
		}
	}

	public synchronized void addModelChangingListener(IStructuredDocumentListener listener) {
		if (Debug.debugStructuredDocument) {
			System.out.println("IStructuredDocument::addStructuredDocumentListener. Request to add an instance of " + listener.getClass() + " as a listener on structuredDocument."); //$NON-NLS-2$//$NON-NLS-1$
		}
		// make sure listener is not already in listening
		// (and if it is, print a warning to aid debugging, if needed)
		if (Utilities.contains(fModelChangingListeners, listener)) {
			if (Debug.displayWarnings) {
				System.out.println("IStructuredDocument::addStructuredDocumentListener. listener " + listener + " was addeded more than once. "); //$NON-NLS-2$//$NON-NLS-1$
			}
		}
		else {
			if (Debug.debugStructuredDocument) {
				System.out.println("IStructuredDocument::addStructuredDocumentListener. Adding an instance of " + listener.getClass() + " as a listener on structuredDocument."); //$NON-NLS-2$//$NON-NLS-1$
			}
			int oldSize = 0;
			if (fModelChangingListeners != null) {
				// normally won't be null, but we need to be sure, for first
				// time through
				oldSize = fModelChangingListeners.length;
			}
			int newSize = oldSize + 1;
			Object[] newListeners = new Object[newSize];
			if (fModelChangingListeners != null) {
				System.arraycopy(fModelChangingListeners, 0, newListeners, 0, oldSize);
			}
			// add listener to last position
			newListeners[newSize - 1] = listener;
			//
			// now switch new for old
			fModelChangingListeners = newListeners;
			//
			// when a listener is added,
			// send the new model event to that one particular listener, so it
			// can initialize itself with the current state of the model
			//listener.newModel(new NewModelEvent(this, listener));
		}
	}

	/**
	 * Adds the position to the document's default position category. The
	 * default category must be specified by the implementer. A position that
	 * has been added to a position category is updated at each change applied
	 * to the document.
	 * 
	 * @exception BadLocationException
	 *                If position is not a valid range in the document
	 */
	public void addPosition(Position position) throws BadLocationException {
		parentDocument.addPosition(position);
	}

	/**
	 * @see IDocument#addPosition
	 * @exception BadLocationException
	 *                If position is not a valid range in the document
	 * @exception BadPositionCategoryException
	 *                If the category is not defined for the document
	 */
	public void addPosition(String category, Position position) throws BadLocationException, BadPositionCategoryException {
		parentDocument.addPosition(category, position);
	}

	/**
	 * @see IDocument#addPositionCategory
	 */
	public void addPositionCategory(String category) {
		parentDocument.addPositionCategory(category);
	}

	/**
	 * @see IDocument#addPositionUpdater
	 */
	public void addPositionUpdater(IPositionUpdater updater) {
		parentDocument.addPositionUpdater(updater);
	}

	/**
	 * Adds the given document listener as one which is notified before those
	 * document listeners added with <code>addDocumentListener</code> are
	 * notified. If the given listener is also registered using
	 * <code>addDocumentListener</code> it will be notified twice. If the
	 * listener is already registered nothing happens.
	 * <p>
	 * 
	 * This method is not for public use, it may only be called by
	 * implementers of <code>IDocumentAdapter</code> and only if those
	 * implementers need to implement <code>IDocumentListener</code>.
	 * 
	 * @param documentAdapter
	 *            the listener to be added as prenotified document listener
	 */
	public void addPrenotifiedDocumentListener(IDocumentListener documentAdapter) {
		if (fPrenotifiedDocumentListeners != null) {
			int previousSize = fPrenotifiedDocumentListeners.length;
			IDocumentListener[] listeners = new IDocumentListener[previousSize + 1];
			System.arraycopy(fPrenotifiedDocumentListeners, 0, listeners, 0, previousSize);
			listeners[previousSize] = documentAdapter;
			fPrenotifiedDocumentListeners = listeners;
		}
		else {
			fPrenotifiedDocumentListeners = new IDocumentListener[1];
			fPrenotifiedDocumentListeners[0] = documentAdapter;
		}
	}

	/**
	 * This method is used to adjust another readonly region, if a new request
	 * has been found to overlap with an existing one.
	 * 
	 * @param startOffset
	 * @param length
	 */
	private void adjustReadOnlyRegions(int startOffset, int length) {
		Position[] positions = null;
		try {
			positions = getPositions(READ_ONLY_REGIONS_CATEGORY);
		}
		catch (BadPositionCategoryException e) {
			// impossible to get due to the way we call
			// this method
			Logger.logException("impossible error", e); //$NON-NLS-1$
		}
		for (int i = 0; i < positions.length; i++) {
			Position position = positions[i];
			// easiest to call overlapsWith first, since
			// it checks for deleted, etc. and allows
			// simpler logic below.
			if (position.overlapsWith(startOffset, length)) {
				// now that we know it overlaps, determine if new request is
				// completely
				// overlaped by region which is already readonly,
				// or ovlaped on left, or overlaped on right.
				// Note: following 4 if clauses are order sensitive
				int requestEnd = startOffset + length;
				int positionEnd = position.getOffset() + position.getLength();
				if (startOffset >= position.getOffset() && requestEnd <= positionEnd) {
					// request completly overlapped by existing one, nothing
					// to
					// do
					return;
				}
				if (startOffset >= position.getOffset() && requestEnd <= positionEnd) {
					// existing one completly overlapped by request, so simply
					// use new
					// offset and length.
					position.setLength(length);
					position.setOffset(startOffset);
					return;
				}
				if (startOffset <= positionEnd) {
					// then existing one is "to the left" of request, so we
					// just
					// need to adjust existing one's end
					int diff = requestEnd - positionEnd;
					int newLength = position.getLength() + diff;
					position.setLength(newLength);
					return;
				}
				if (requestEnd >= position.getOffset()) {
					// then existing one is "to the right" of request, so we
					// need to adjust existing one's start and adjust length
					int diff = requestEnd - positionEnd;
					int newLength = position.getLength() + diff;
					position.setOffset(startOffset);
					position.setLength(newLength);
					return;
				}
				// if found to overlap, should never get to here, so for
				// debugging purposes:
				Logger.log(Logger.ERROR, "program error in adjustReadOnlyRegions"); //$NON-NLS-1$
				break;
			}
		}
	}

	/**
	 * This form of the API removes all read only positions, as should be done
	 * we 'setText' is called. Note: an alternative algorithm may simply
	 * remove the category (and it would get added back in later, if/when
	 * readonly regions added.
	 */
	private void clearReadOnly() {
		Position[] positions = null;
		try {
			positions = getPositions(READ_ONLY_REGIONS_CATEGORY);
		}
		catch (BadPositionCategoryException e) {
			Logger.logException("program error: should never occur", e); //$NON-NLS-1$
		}
		for (int i = 0; i < positions.length; i++) {
			Position position = positions[i];
			// note we don't fire the "about to change" or "changed" events,
			// since presumably, text is all going away and being replaced
			// anyway.
			position.delete();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 */
	public void clearReadOnly(int startOffset, int length) {
		// TODO DW I still need to implement smarter algorithm that
		// adust existing RO regions, if needed. For now, I'll just
		// remove any that overlap.
		try {
			Position[] positions = getPositions(READ_ONLY_REGIONS_CATEGORY);
			for (int i = 0; i < positions.length; i++) {
				Position position = positions[i];
				if (position.overlapsWith(startOffset, length)) {
					String effectedText = this.get(startOffset, length);
					//fDocumentEvent = new DocumentEvent(this, startOffset,
					// length, effectedText);
					fireReadOnlyAboutToBeChanged();
					position.delete();
					NoChangeEvent noChangeEvent = new NoChangeEvent(this, null, effectedText, startOffset, length);
					noChangeEvent.reason = NoChangeEvent.READ_ONLY_STATE_CHANGE;
					fireReadOnlyStructuredDocumentEvent(noChangeEvent);
				}
			}
		}
		catch (BadPositionCategoryException e) {
			// just means no readonly regions been defined yet
			// so nothing to do.
		}
	}

	/**
	 * Computes the index at which a <code>Position</code> with the
	 * specified offset would be inserted into the given category. As the
	 * ordering inside a category only depends on the offset, the index must
	 * be choosen to be the first of all positions with the same offset.
	 * 
	 * @param category
	 *            the category in which would be added
	 * @param offset
	 *            the position offset to be considered
	 * @return the index into the category
	 * @exception BadLocationException
	 *                if offset is invalid in this document
	 * @exception BadPositionCategoryException
	 *                if category is undefined in this document
	 */
	public int computeIndexInCategory(String category, int offset) throws org.eclipse.jface.text.BadPositionCategoryException, org.eclipse.jface.text.BadLocationException {
		return parentDocument.computeIndexInCategory(category, offset);
	}

	/**
	 * Computes the number of lines in the given text. For a given implementer
	 * of this interface this method returns the same result as
	 * <code>set(text); getNumberOfLines()</code>.
	 * 
	 * @param text
	 *            the text whose number of lines should be computed
	 * @return the number of lines in the given text
	 */
	public int computeNumberOfLines(String text) {
		return parentDocument.computeNumberOfLines(text);
	}

	/**
	 * Computes the partitioning of the given document range using the
	 * document's partitioner.
	 * 
	 * @param offset
	 *            the document offset at which the range starts
	 * @param length
	 *            the length of the document range
	 * @return a specification of the range's partitioning
	 * @throws BadLocationException
	 * @throws BadPartitioningException
	 */
	public ITypedRegion[] computePartitioning(int offset, int length) throws BadLocationException {
		ITypedRegion[] typedRegions = null;
		try {
			typedRegions = computePartitioning(IDocumentExtension3.DEFAULT_PARTITIONING, offset, length, false);
		}
		catch (BadPartitioningException e) {
			// impossible in this context
			throw new Error(e);
		}
		return typedRegions;
	}

	/**
	 * @see IDocument#containsPosition
	 */
	public boolean containsPosition(String category, int offset, int length) {
		return parentDocument.containsPosition(category, offset, length);
	}

	/**
	 * @see IDocument#containsPositionCategory
	 */
	public boolean containsPositionCategory(String category) {
		return parentDocument.containsPositionCategory(category);
	}

	/*
	 * (non-Javadoc)
	 * 
	 */
	public boolean containsReadOnly(int startOffset, int length) {
		boolean result = false;
		try {
			Position[] positions = getPositions(READ_ONLY_REGIONS_CATEGORY);
			for (int i = 0; i < positions.length; i++) {
				Position position = positions[i];
				if (position.overlapsWith(startOffset, length)) {
					result = true;
					break;
				}
			}
		}
		catch (BadPositionCategoryException e) {
			// just means no readonly regions been defined yet
			// so obviously false
			result = false;
		}
		return result;
	}

	private boolean equivalentRegions(IStructuredDocumentRegion oldNode, IStructuredDocumentRegion newNode) {
		Iterator oldRegions = oldNode.getRegions().iterator();
		Iterator newRegions = newNode.getRegions().iterator();
		while (oldRegions.hasNext()) {
			ITextRegion oldRegion = (ITextRegion) oldRegions.next();
			ITextRegion newRegion = null;
			if (newRegions.hasNext()) {
				newRegion = (ITextRegion) newRegions.next();
			}
			else {
				//ran out of new before old
				return false;
			}
			if (oldRegion.getType() != newRegion.getType())
				return false;
		}
		// at this point, we've run out of oldRegions, let's
		// be sure we are also out of newRegions
		if (newRegions.hasNext())
			return false;
		return true;
	}

	private void executePostNotificationChanges() {
		if (fStoppedCount > 0)
			return;
		while (fPostNotificationChanges != null) {
			List changes = fPostNotificationChanges;
			fPostNotificationChanges = null;
			Iterator e = changes.iterator();
			while (e.hasNext()) {
				RegisteredReplace replace = (RegisteredReplace) e.next();
				replace.fReplace.perform(this, replace.fOwner);
			}
		}
	}

	private void fireModelAboutToBeChanged() {
		_fireModelAboutToBeChanged(fModelAboutToBeChangedListeners);
		// Note: the docEvent is created in replaceText API! (or set Text)
		_fireDocumentAboutToChange(fPrenotifiedDocumentListeners);
		_fireDocumentAboutToChange(fDocumentListeners);
	}

	/**
	 * A temporary method of notifiying listeners that this document is new.
	 */
	public void fireNewDocument(Object requester) {
		NewModelEvent result = new NewModelEvent(this, requester);
		fireStructuredDocumentEvent(result);
	}

	private void fireReadOnlyAboutToBeChanged() {
		_fireModelAboutToBeChanged(fModelAboutToBeChangedListeners);
		// Note: the docEvent is created in replaceText API! (or set Text)
		//		_fireDocumentAboutToChange(fPrenotifiedDocumentListeners);
		//		_fireDocumentAboutToChange(fDocumentListeners);
	}

	private void fireReadOnlyStructuredDocumentEvent(NoChangeEvent event) {
		//	_fireEvent(fModelChangingListeners, event);
		_fireEvent(fModelChangedListeners, event);
		//		_fireDocumentChanged(fPrenotifiedDocumentListeners, event);
		//	_fireDocumentChanged(fDocumentListeners, event);
		//	_clearDocumentEvent();
	}

	private void fireStructuredDocumentEvent(NewModelEvent event) {
		_fireEvent(fModelChangingListeners, event);
		_fireEvent(fModelChangedListeners, event);
		_fireDocumentChanged(fPrenotifiedDocumentListeners, event);
		_fireDocumentChanged(fDocumentListeners, event);
		_clearDocumentEvent();
	}

	private void fireStructuredDocumentEvent(NoChangeEvent event) {
		_fireEvent(fModelChangingListeners, event);
		_fireEvent(fModelChangedListeners, event);
		_fireDocumentChanged(fPrenotifiedDocumentListeners, event);
		_fireDocumentChanged(fDocumentListeners, event);
		_clearDocumentEvent();
	}

	private void fireStructuredDocumentEvent(StructuredDocumentRegionsReplacedEvent event) {
		_fireEvent(fModelChangingListeners, event);
		_fireEvent(fModelChangedListeners, event);
		_fireDocumentChanged(fPrenotifiedDocumentListeners, event);
		_fireDocumentChanged(fDocumentListeners, event);
		_clearDocumentEvent();
	}

	private void fireStructuredDocumentEvent(RegionChangedEvent event) {
		_fireEvent(fModelChangingListeners, event);
		_fireEvent(fModelChangedListeners, event);
		_fireDocumentChanged(fPrenotifiedDocumentListeners, event);
		_fireDocumentChanged(fDocumentListeners, event);
		_clearDocumentEvent();
	}

	private void fireStructuredDocumentEvent(RegionsReplacedEvent event) {
		_fireEvent(fModelChangingListeners, event);
		_fireEvent(fModelChangedListeners, event);
		_fireDocumentChanged(fPrenotifiedDocumentListeners, event);
		_fireDocumentChanged(fDocumentListeners, event);
		_clearDocumentEvent();
	}

	/**
	 * Returns the document's complete text.
	 */
	public String get() {
		return parentDocument.get();
	}

	/**
	 * Returns length characters from the document's text starting from the
	 * specified position.
	 * 
	 * @exception BadLocationException
	 *                If the range is not valid in the document
	 */
	public String get(int offset, int length) {
		String result = null;
		try {
			result = parentDocument.get(offset, length);
		}
		catch (BadLocationException e) {
			// log and return empty String (This matters in adding bookmark
			// with the cursor
			// is right after the carrageReturn-lineFeed.
			Logger.log(Logger.ERROR, "BadLocationException in IStructuredDocument.get(" + offset + "+, " + length + ") -- not to worry if during addBookMark, addTask"); //$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
			result = ""; //$NON-NLS-1$
		}
		return result;
	}

	/**
	 */
	synchronized IStructuredDocumentRegion getCachedDocumentRegion() {
		if (USE_LOCAL_THREAD) {
			return (IStructuredDocumentRegion) threadLocalDocumentRegion.get();
		}
		else {
			return cachedDocumentRegion;
		}
	}

	/**
	 * @see IDocument#getChar
	 * @exception BadLocationException
	 *                If position is not a valid range in the document
	 */
	public char getChar(int pos) throws BadLocationException {
		return parentDocument.getChar(pos);
	}

	/**
	 * Returns the type of the document partition containing the given
	 * character position.
	 */
	public String getContentType(int offset) throws BadLocationException {
		return getDocumentPartitioner().getContentType(offset);
	}

	/**
	 * @deprecated - no longer used
	 */
	public String[] getContentTypes() {
		//	return parentDocument.getContentTypes();
		// Is this what was intended? -- Transitioning to TP5
		return getDocumentPartitioner().getLegalContentTypes();
	}

	/**
	 * Returns the document's partitioner.
	 * 
	 * @see IDocumentPartitioner
	 */
	public IDocumentPartitioner getDocumentPartitioner() {
		return getDocumentPartitioner(IDocumentExtension3.DEFAULT_PARTITIONING);
	}

	/**
	 */
	public EncodingMemento getEncodingMemento() {
		return encodingMemento;
	}

	public synchronized IStructuredDocumentRegion getFirstStructuredDocumentRegion() {
		// should we update cachedNode?
		// We should to keep consistent philosophy of remembering last
		// requested position,
		// for efficiency.
		setCachedDocumentRegion(firstDocumentRegion);
		return firstDocumentRegion;
	}

	public synchronized IStructuredDocumentRegion getLastStructuredDocumentRegion() {
		// should we update cachedNode?
		// We should to keep consistent philosophy of remembering last
		// requested position,
		// for efficiency.
		setCachedDocumentRegion(lastDocumentRegion);
		return lastDocumentRegion;
	}

	/*
	 * -------------------------- partitions
	 * ----------------------------------
	 */
	public String[] getLegalContentTypes() {
		String [] result = null;
		try {
			result = getLegalContentTypes(IDocumentExtension3.DEFAULT_PARTITIONING);
		}
		catch (BadPartitioningException e) {
			// impossible in this context
			throw new Error(e);
		}
		return result;
	}

	/*
	 * ------------------ line delimiter conversion
	 * ---------------------------
	 */
	public String[] getLegalLineDelimiters() {
		return parentDocument.getLegalLineDelimiters();
	}

	/**
	 * @see IDocument#getLength
	 */
	public int getLength() {
		return parentDocument.getLength();
	}

	public String getLineDelimiter() {
		if (preferedDelimiter == null) {
			preferedDelimiter = PlatformLineDelimiter;
		}
		return preferedDelimiter;
	}

	/**
	 * Returns the line delimiter of that line
	 * 
	 * @exception BadLocationException
	 *                If the line number is invalid in the document
	 */
	public String getLineDelimiter(int line) throws org.eclipse.jface.text.BadLocationException {
		return parentDocument.getLineDelimiter(line);
	}

	/**
	 * Returns a description of the specified line. The line is described by
	 * its offset and its length excluding the line's delimiter.
	 * 
	 * @param line
	 *            the line of interest
	 * @return a line description
	 * @exception BadLocationException
	 *                if the line number is invalid in this document
	 */
	public org.eclipse.jface.text.IRegion getLineInformation(int line) throws org.eclipse.jface.text.BadLocationException {
		return parentDocument.getLineInformation(line);
	}

	/**
	 * Returns a description of the line at the given offset. The description
	 * contains the offset and the length of the line excluding the line's
	 * delimiter.
	 * 
	 * @param offset
	 *            the offset whose line should be described
	 * @return a region describing the line
	 * @exception BadLocationException
	 *                if offset is invalid in this document
	 */
	public org.eclipse.jface.text.IRegion getLineInformationOfOffset(int offset) throws org.eclipse.jface.text.BadLocationException {
		return parentDocument.getLineInformationOfOffset(offset);
	}

	/*
	 * ---------------------- line information
	 * --------------------------------
	 */
	public int getLineLength(int line) throws org.eclipse.jface.text.BadLocationException {
		return parentDocument.getLineLength(line);
	}

	/**
	 * Determines the offset of the first character of the given line.
	 * 
	 * @param line
	 *            the line of interest
	 * @return the document offset
	 * @exception BadLocationException
	 *                if the line number is invalid in this document
	 */
	public int getLineOffset(int line) throws org.eclipse.jface.text.BadLocationException {
		return parentDocument.getLineOffset(line);
	}

	public int getLineOfOffset(int offset) {
		int result = -1;
		try {
			result = parentDocument.getLineOfOffset(offset);
		}
		catch (BadLocationException e) {
			Logger.traceException("IStructuredDocument", "Dev. Program Info Only: IStructuredDocument::getLineOfOffset: offset out of range, zero assumed. offset = " + offset, e); //$NON-NLS-1$ //$NON-NLS-2$
			result = 0;
		}
		return result;
	}

	/**
	 * Returns the number of lines in this document
	 * 
	 * @return the number of lines in this document
	 */
	public int getNumberOfLines() {
		return parentDocument.getNumberOfLines();
	}

	/**
	 * Returns the number of lines which are occupied by a given text range.
	 * 
	 * @param offset
	 *            the offset of the specified text range
	 * @param length
	 *            the length of the specified text range
	 * @return the number of lines occupied by the specified range
	 * @exception BadLocationException
	 *                if specified range is invalid in this tracker
	 */
	public int getNumberOfLines(int offset, int length) throws org.eclipse.jface.text.BadLocationException {
		return parentDocument.getNumberOfLines(offset, length);
	}

	/**
	 * This is public, for use by tag lib classes.
	 */
	public RegionParser getParser() {
		if (fParser == null) {
			throw new SourceEditingRuntimeException("IStructuredDocument::getParser. Parser needs to be set before use"); //$NON-NLS-1$
			//fParser = getParserFactory().createParser(fType);
			//System.out.println("Information:
			// IStructuredDocument::getParser.
			// XML Parser assumed.");
			//fParser = new XMLSourceParser();
		}
		return fParser;
	}

	/**
	 * Returns the document partition in which the position is located. The
	 * partition is specified as typed region.
	 */
	public ITypedRegion getPartition(int offset) throws BadLocationException {
		ITypedRegion partition= null;
		try {
			partition = getPartition(IDocumentExtension3.DEFAULT_PARTITIONING, offset, false);
		} catch (BadPartitioningException e) {
			throw new Error(e);
		}
		if(partition == null) {
			throw new Error();
		}
		return  partition;
	}

	/**
	 * Partitions the given document section using the document's partitioner.
	 * The partitioning is returned as a list of typed regions.
	 * 
	 * @deprecated - naming collides with extension3
	 */
	public TypedRegion[] getPartitioning(int offset, int length) throws org.eclipse.jface.text.BadLocationException {
		// for now, only, create typed regions from our regions
		IStructuredDocumentRegion startNode = getRegionAtCharacterOffset(offset);
		IStructuredDocumentRegion endNode = getRegionAtCharacterOffset(offset + length - 1);
		Vector holdRegions = new Vector();
		IStructuredDocumentRegionList nodeList = new CoreNodeList(startNode, endNode);
		Enumeration enum = nodeList.elements();
		while (enum.hasMoreElements()) {
			IStructuredDocumentRegion aNode = (IStructuredDocumentRegion) enum.nextElement();
			ITextRegionList v = aNode.getRegions();
			Iterator vEnum = v.iterator();
			while (vEnum.hasNext()) {
				ITextRegion region = (ITextRegion) vEnum.next();
				holdRegions.addElement(new TypedRegion(region.getStart(), region.getLength(), region.getType()));
			}
		}
		org.eclipse.jface.text.TypedRegion[] tRegions = new org.eclipse.jface.text.TypedRegion[holdRegions.size()];
		holdRegions.copyInto(tRegions);
		return tRegions;
	}

	/**
	 * Returns all position categories added to this document.
	 */
	public String[] getPositionCategories() {
		return parentDocument.getPositionCategories();
	}

	/**
	 * Returns all Positions of the given position category.
	 * 
	 * @exception BadPositionCategoryException
	 *                If category is not defined for the document
	 */
	public Position[] getPositions(String category) throws org.eclipse.jface.text.BadPositionCategoryException {
		return parentDocument.getPositions(category);
	}

	/**
	 * @see IDocument#getPositionUpdaters
	 */
	public IPositionUpdater[] getPositionUpdaters() {
		return parentDocument.getPositionUpdaters();
	}

	/**
	 * This method can return null, which is the case if the offset is just
	 * before or just after the existing text. Compare with
	 * getNodeAtCharacterOffset.
	 */
	public synchronized IStructuredDocumentRegion getRegionAtCharacterOffset(int offset) {
		// cached node can be null when document is empty
		if (getCachedDocumentRegion() == null)
			return null;
		IStructuredDocumentRegion potentialCachedNode = getCachedDocumentRegion();
		// if we already have the right node, return that.
		if (!potentialCachedNode.containsOffset(offset)) {
			// first, find out what direction to go, relative to cachedNode.
			// negative means "towards the front" of the file, postitive means
			// towards the end.
			int direction = offset - potentialCachedNode.getStart();
			if (direction < 0) {
				// search towards beginning
				while (!potentialCachedNode.containsOffset(offset)) {
					IStructuredDocumentRegion tempNode = potentialCachedNode.getPrevious();
					if (tempNode == null) {
						break;
					}
					else {
						potentialCachedNode = tempNode;
					}
				}
			}
			else {
				// search towards end
				// There is a legitamat condition where the
				// offset will not be contained in any node,
				// which is if the offset is just past the last
				// character of text.
				// And, we must gaurd against setting cachedNode to
				// null!
				while (!potentialCachedNode.containsOffset(offset)) {
					IStructuredDocumentRegion tempNode = potentialCachedNode.getNext();
					if (tempNode == null)
						break;
					else
						potentialCachedNode = tempNode;
				}
			}
		}
		// just to be doubly sure we never assign null to cachedNode
		if (potentialCachedNode != null)
			setCachedDocumentRegion(potentialCachedNode);
		else
			Logger.log(Logger.ERROR, "Program Error: IStructuredDocument::getNodeAtCharacterOffset. no node to cache could be found."); //$NON-NLS-1$
		return potentialCachedNode;
	}

	public IStructuredDocumentRegionList getRegionList() {
		CoreNodeList result = null;
		if (getCachedDocumentRegion() == null)
			result = new CoreNodeList(null);
		else
			result = new CoreNodeList(getFirstStructuredDocumentRegion());
		return result;
	}

	/**
	 * was made public for easier testing. Normally should never be used by
	 * client codes.
	 */
	public IStructuredTextReParser getReParser() {
		if (fReParser == null) {
			fReParser = new StructuredDocumentReParser();
			fReParser.setStructuredDocument(this);
		}
		return fReParser;
	}

	public String getText() {
		String result = parentDocument.get();
		return result;
	}

	void initializeFirstAndLastDocumentRegion() {
		// cached Node must also be first, at the initial point. Only valid
		// to call this method right after the first parse.
		// 
		// when starting afresh, our cachedNode should be our firstNode,
		// so be sure to initialize the firstNode
		firstDocumentRegion = getCachedDocumentRegion();
		// be sure to use 'getNext' for this initial finding of the last node,
		// since the implementation of node.getLastNode may simply call
		// structuredDocument.getLastStructuredDocumentRegion!
		IStructuredDocumentRegion aNode = firstDocumentRegion;
		if (aNode == null) {
			// defect 254607: to handle empty documents right, if firstnode is
			// null, make sure last node is null too
			lastDocumentRegion = null;
		}
		else {
			while (aNode != null) {
				lastDocumentRegion = aNode;
				aNode = aNode.getNext();
			}
		}
	}

	/**
	 * @see IDocument#insertPositionUpdater
	 */
	public void insertPositionUpdater(IPositionUpdater updater, int index) {
		parentDocument.insertPositionUpdater(updater, index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 */
	public void makeReadOnly(int startOffset, int length) {
		// doesn't make sense to have a readonly region of 0 length,
		// so we'll ignore those requests
		if (length <= 0)
			return;
		String affectedText = this.get(startOffset, length);
		// a document event for "read only" change ... must
		// be followed by "no change" structuredDocument event
		//	fDocumentEvent = new DocumentEvent(this, startOffset, length,
		// affectedText);
		fireReadOnlyAboutToBeChanged();
		//		if (containsReadOnly(startOffset, length)) {
		//			adjustReadOnlyRegions(startOffset, length);
		//		} else {
		// we can blindly add category, since no harm done if already
		// exists.
		addPositionCategory(READ_ONLY_REGIONS_CATEGORY);
		Position newPosition = new Position(startOffset, length);
		try {
			addPosition(READ_ONLY_REGIONS_CATEGORY, newPosition);
			NoChangeEvent noChangeEvent = new NoChangeEvent(this, null, affectedText, startOffset, length);
			noChangeEvent.reason = NoChangeEvent.READ_ONLY_STATE_CHANGE;
			fireReadOnlyStructuredDocumentEvent(noChangeEvent);
		}
		catch (BadLocationException e) {
			// for now, log and ignore. Perhaps later we
			// could adjust to handle some cases?
			Logger.logException(("could not create readonly region at " + startOffset + " to " + length), e); //$NON-NLS-1$ //$NON-NLS-2$
		}
		catch (BadPositionCategoryException e) {
			// should never occur, since we add category
			Logger.logException(e);
		}
	}

	/**
	 */
	public IStructuredDocument newInstance() {
		BasicStructuredDocument newInstance = new BasicStructuredDocument(getParser().newInstance());
		newInstance.setReParser(getReParser().newInstance());
		if (getDocumentPartitioner() instanceof StructuredTextPartitioner) {
			newInstance.setDocumentPartitioner(((StructuredTextPartitioner) getDocumentPartitioner()).newInstance());
		}
		newInstance.setLineDelimiter(getLineDelimiter());
		if (getEncodingMemento() != null) {
			newInstance.setEncodingMemento((EncodingMemento) getEncodingMemento().clone());
		}
		return newInstance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocumentExtension#registerPostNotificationReplace(org.eclipse.jface.text.IDocumentListener,
	 *      org.eclipse.jface.text.IDocumentExtension.IReplace)
	 */
	public void registerPostNotificationReplace(IDocumentListener owner, IDocumentExtension.IReplace replace) {
		if (fAcceptPostNotificationReplaces) {
			if (fPostNotificationChanges == null)
				fPostNotificationChanges = new ArrayList(1);
			fPostNotificationChanges.add(new RegisteredReplace(owner, replace));
		}
	}

	public synchronized void removeDocumentListener(IDocumentListener listener) {
		if ((fDocumentListeners != null) && (listener != null)) {
			// if its not in the listeners, we'll ignore the request
			if (Utilities.contains(fDocumentListeners, listener)) {
				int oldSize = fDocumentListeners.length;
				int newSize = oldSize - 1;
				IDocumentListener[] newListeners = new IDocumentListener[newSize];
				int index = 0;
				for (int i = 0; i < oldSize; i++) {
					if (fDocumentListeners[i] == listener) { // ignore
					}
					else {
						// copy old to new if its not the one we are removing
						newListeners[index++] = fDocumentListeners[i];
					}
				}
				// now that we have a new array, let's switch it for the old
				// one
				fDocumentListeners = newListeners;
			}
		}
	}

	public synchronized void removeModelAboutToBeChangedListener(IModelAboutToBeChangedListener listener) {
		if ((fModelAboutToBeChangedListeners != null) && (listener != null)) {
			// if its not in the listeners, we'll ignore the request
			if (Utilities.contains(fModelAboutToBeChangedListeners, listener)) {
				int oldSize = fModelAboutToBeChangedListeners.length;
				int newSize = oldSize - 1;
				Object[] newListeners = new Object[newSize];
				int index = 0;
				for (int i = 0; i < oldSize; i++) {
					if (fModelAboutToBeChangedListeners[i] == listener) { // ignore
					}
					else {
						// copy old to new if its not the one we are removing
						newListeners[index++] = fModelAboutToBeChangedListeners[i];
					}
				}
				// now that we have a new array, let's switch it for the old
				// one
				fModelAboutToBeChangedListeners = newListeners;
			}
		}
	}

	/**
	 * removeModelChangedListener method comment.
	 */
	public void removeModelChangedListener(IStructuredDocumentListener listener) {
		if ((fModelChangedListeners != null) && (listener != null)) {
			// if its not in the listeners, we'll ignore the request
			if (Utilities.contains(fModelChangedListeners, listener)) {
				int oldSize = fModelChangedListeners.length;
				int newSize = oldSize - 1;
				Object[] newListeners = new Object[newSize];
				int index = 0;
				for (int i = 0; i < oldSize; i++) {
					if (fModelChangedListeners[i] == listener) { // ignore
					}
					else {
						// copy old to new if its not the one we are removing
						newListeners[index++] = fModelChangedListeners[i];
					}
				}
				// now that we have a new array, let's switch it for the old
				// one
				fModelChangedListeners = newListeners;
			}
		}
	}

	public synchronized void removeModelChangingListener(IStructuredDocumentListener listener) {
		if ((fModelChangingListeners != null) && (listener != null)) {
			// if its not in the listeners, we'll ignore the request
			if (Utilities.contains(fModelChangingListeners, listener)) {
				int oldSize = fModelChangingListeners.length;
				int newSize = oldSize - 1;
				Object[] newListeners = new Object[newSize];
				int index = 0;
				for (int i = 0; i < oldSize; i++) {
					if (fModelChangingListeners[i] == listener) { // ignore
					}
					else {
						// copy old to new if its not the one we are removing
						newListeners[index++] = fModelChangingListeners[i];
					}
				}
				// now that we have a new array, let's switch it for the old
				// one
				fModelChangingListeners = newListeners;
			}
		}
	}

	/**
	 * Removes the given <code>Position</code> from the document's default
	 * position category. The default position category is to be defined by
	 * the implementers. If the position is not part of the document's default
	 * category nothing happens.
	 */
	public void removePosition(Position position) {
		parentDocument.removePosition(position);
	}

	/**
	 * @see IDocument#removePosition
	 * @exception BadPositionCategoryException
	 *                If the category is not defined for the document
	 */
	public void removePosition(String category, Position position) throws BadPositionCategoryException {
		parentDocument.removePosition(category, position);
	}

	/**
	 * @see IDocument#removePositionCategory
	 * @exception BadPositionCategoryException
	 *                If the category is not defined for the document
	 */
	public void removePositionCategory(String category) throws BadPositionCategoryException {
		parentDocument.removePositionCategory(category);
	}

	/**
	 * @see IDocument#removePositionUpdater
	 */
	public void removePositionUpdater(IPositionUpdater updater) {
		parentDocument.removePositionUpdater(updater);
	}

	/**
	 * Removes the given document listener from teh document's list of
	 * prenotified document listeners. If the listener is not registered with
	 * the document nothing happens.
	 * <p>
	 * 
	 * This method is not for public use, it may only be called by
	 * implementers of <code>IDocumentAdapter</code> and only if those
	 * implementers need to implement <code>IDocumentListener</code>.
	 * 
	 * @param documentAdapter
	 *            the listener to be removed
	 * 
	 * @see #addPrenotifiedDocumentListener(IDocumentListener)
	 */
	public void removePrenotifiedDocumentListener(org.eclipse.jface.text.IDocumentListener documentAdapter) {
		if (Utilities.contains(fPrenotifiedDocumentListeners, documentAdapter)) {
			int previousSize = fPrenotifiedDocumentListeners.length;
			if (previousSize > 1) {
				IDocumentListener[] listeners = new IDocumentListener[previousSize - 1];
				int previousIndex = 0;
				int newIndex = 0;
				while (previousIndex < previousSize) {
					if (fPrenotifiedDocumentListeners[previousIndex] != documentAdapter)
						listeners[newIndex++] = fPrenotifiedDocumentListeners[previousIndex];
					previousIndex++;
				}
				fPrenotifiedDocumentListeners = listeners;
			}
			else {
				fPrenotifiedDocumentListeners = null;
			}
		}
	}

	/**
	 * @see IDocument#replace
	 * @exception BadLocationException
	 *                If position is not a valid range in the document
	 */
	public void replace(int pos, int length, String string) throws BadLocationException {
		if (Debug.displayWarnings) {
			System.out.println("Note: IStructuredDocument::replace(int, int, String) .... its better to use replaceText(source, string, int, int) API for structuredDocument updates"); //$NON-NLS-1$
		}
		replaceText(this, pos, length, string);
	}

	/**
	 * Replace the text with "newText" starting at position "start" for a
	 * length of "replaceLength".
	 * <p>
	 * 
	 * @param start
	 *            start offset of text to replace None of the offsets include
	 *            delimiters of preceeding lines. Offset 0 is the first
	 *            character of the document.
	 * @param replaceLength
	 *            start offset of text to replace
	 * @param newText
	 *            start offset of text to replace
	 *            <p>
	 *            Implementors have to notify TextChanged listeners after the
	 *            content has been updated. The TextChangedEvent should be set
	 *            as follows:
	 * 
	 * event.type = SWT.TextReplaced event.start = start of the replaced text
	 * event.numReplacedLines = number of replaced lines event.numNewLines =
	 * number of new lines event.replacedLength = length of the replaced text
	 * event.newLength = length of the new text
	 * 
	 * NOTE: numNewLines is the number of inserted lines and numReplacedLines
	 * is the number of deleted lines based on the change that occurs
	 * visually. For example:
	 * 
	 * replacedText newText numReplacedLines numNewLines "" "\n" 0 1 "\n\n"
	 * "a" 2 0 "a" "\n\n" 0 2
	 */
	/**
	 * One of the APIs to manipulate the IStructuredDocument in terms of text.
	 */
	public StructuredDocumentEvent replaceText(Object requester, int start, int replacementLength, String changes) {
		stopPostNotificationProcessing();
		StructuredDocumentEvent result = null;
		if (changes == null)
			changes = ""; //$NON-NLS-1$
		// 
		if (Debug.debugStructuredDocument)
			System.out.println(getClass().getName() + "::replaceText(" + start + "," + replacementLength + "," + changes + ")"); //$NON-NLS-4$//$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
		if (Debug.perfTestStructuredDocumentOnly || Debug.perfTest || Debug.perfTestRawStructuredDocumentOnly) {
			startStreamTime = System.currentTimeMillis();
		}
		// Note: event must be computed before 'fire' method called
		fDocumentEvent = new DocumentEvent(this, start, replacementLength, changes);
		fireModelAboutToBeChanged();
		result = updateModel(requester, start, replacementLength, changes);
		if (Debug.perfTestRawStructuredDocumentOnly || Debug.perfTest) {
			long stopStreamTime = System.currentTimeMillis();
			System.out.println("\n\t\t\t\t Time for IStructuredDocument raw replaceText: " + (stopStreamTime - startStreamTime)); //$NON-NLS-1$
		}
		if (Debug.debugStructuredDocument) {
			System.out.println("event type returned by replaceTextWithNoDebuggingThread: " + result); //$NON-NLS-1$
		}
		// FUTURE_TO_DO: implement callback mechanism? to avoid instanceof and
		// casting
		if (result instanceof RegionChangedEvent) {
			fireStructuredDocumentEvent((RegionChangedEvent) result);
		}
		else {
			if (result instanceof RegionsReplacedEvent) {
				fireStructuredDocumentEvent((RegionsReplacedEvent) result);
			}
			else {
				if (result instanceof StructuredDocumentRegionsReplacedEvent) {
					fireStructuredDocumentEvent((StructuredDocumentRegionsReplacedEvent) result);
				}
				else {
					if (result instanceof NoChangeEvent) {
						fireStructuredDocumentEvent((NoChangeEvent) result);
					}
					else {
						throw new SourceEditingRuntimeException("Program Error: invalid flat model event: " + result); //$NON-NLS-1$
					}
				}
			}
		}
		if (Debug.perfTest || Debug.perfTestStructuredDocumentOnly) {
			long stopStreamTime = System.currentTimeMillis();
			System.out.println("\n\t\t\t\t Total Time for IStructuredDocument event signaling/processing in replaceText: " + (stopStreamTime - startStreamTime)); //$NON-NLS-1$
		}
		resumePostNotificationProcessing();
		return result;
	}

	void resetParser(int startOffset, int endOffset) {
		String newNodeText = null;
		RegionParser parser = getParser();
		newNodeText = get(startOffset, endOffset - startOffset);
		parser.reset(newNodeText, startOffset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocumentExtension#resumePostNotificationProcessing()
	 */
	public void resumePostNotificationProcessing() {
		--fStoppedCount;
		if (fStoppedCount == 0 && fReentranceCount == 0)
			executePostNotificationChanges();
	}

	/**
	 * @deprecated - use a FindReplaceDocumentAdapter directly
	 * @see IDocument#search
	 */
	public int search(int startPosition, String findString, boolean forwardSearch, boolean caseSensitive, boolean wholeWord) throws BadLocationException {
		int offset = -1;
		IRegion match = new FindReplaceDocumentAdapter(parentDocument).find(startPosition, findString, forwardSearch, caseSensitive, wholeWord, false);
		if (match != null) {
			offset = match.getOffset();
		}
		return offset;
	}

	/**
	 * @see IDocument#setText
	 */
	public void set(String string) {
		if (Debug.displayInfo) {
			System.out.println("Note: IStructuredDocument::setText(String) .... its better to use setText(source, string) API for structuredDocument updates"); //$NON-NLS-1$
		}
		setText(null, string);
	}

	/**
	 * @param strcuturedRegion
	 */
	public void setCachedDocumentRegion(IStructuredDocumentRegion structuredRegion) {
		if (USE_LOCAL_THREAD) {
			threadLocalDocumentRegion.set(structuredRegion);
		}
		else {
			cachedDocumentRegion = structuredRegion;
		}
	}

	/**
	 * Sets the document's partitioner.
	 * 
	 * @see IDocumentPartitioner
	 */
	public void setDocumentPartitioner(IDocumentPartitioner partitioner) {
		Assert.isNotNull(parentDocument, "text store must be set before document partitioner"); //$NON-NLS-1$
		IDocumentPartitioner oldPartioner = null;
		oldPartioner = getDocumentPartitioner(IDocumentExtension3.DEFAULT_PARTITIONING);
		if (oldPartioner != null) {
			oldPartioner.disconnect();
		}
		// some operations, such as convert line delimters, sets the partioner
		// to null
		// then resets it when done.
		if (partitioner != null) {
			partitioner.connect(this);
		}
		setDocumentPartitioner(IDocumentExtension3.DEFAULT_PARTITIONING, partitioner);
	}

	/**
	 */
	public void setEncodingMemento(EncodingMemento encodingMemento) {
		this.encodingMemento = encodingMemento;
	}

	/**
	 * @see IStructuredDocument#setLineDelimiter(String)
	 */
	public void setLineDelimiter(String delimiter) {
		// make sure our preferred delimiter is
		// one of the legal ones
		if (Utilities.containsString(getLegalLineDelimiters(), delimiter)) {
			preferedDelimiter = delimiter;
		}
		else {
			Logger.trace("IStructuredDocument", "Attempt to set linedelimiter to non-legal delimiter"); //$NON-NLS-1$ //$NON-NLS-2$
			preferedDelimiter = PlatformLineDelimiter;
		}
	}

	public void setParser(RegionParser newParser) {
		fParser = newParser;
	}

	/**
	 *  
	 */
	public void setReParser(IStructuredTextReParser newReParser) {
		fReParser = newReParser;
		if (fReParser != null) {
			fReParser.setStructuredDocument(this);
		}
	}

	/**
	 * One of the APIs to manipulate the IStructuredDocument in terms of text.
	 */
	public NewModelEvent setText(Object requester, String theString) {
		stopPostNotificationProcessing();
		clearReadOnly();
		// Note: event must be computed before 'fire' method called
		fDocumentEvent = new DocumentEvent(this, 0, getLength(), theString);
		fireModelAboutToBeChanged();
		NewModelEvent result = null;
		parentDocument.set(theString);
		//
		resetParser(0, parentDocument.getLength());
		setCachedDocumentRegion(getParser().getDocumentRegions());
		// when starting afresh, our cachedNode should be our firstNode,
		// so be sure to initialize the firstNode and lastNode
		initializeFirstAndLastDocumentRegion();
		StructuredDocumentRegionIterator.setParentDocument(getCachedDocumentRegion(), this);
		// initialize the structuredDocument variable of each instance in the
		// new chain
		//StructuredDocumentRegionIterator.setStructuredDocument(getCachedNode(),
		// this);
		result = new NewModelEvent(this, requester);
		fireStructuredDocumentEvent(result);
		resumePostNotificationProcessing();
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocumentExtension#startSequentialRewrite(boolean)
	 */
	public void startSequentialRewrite(boolean normalize) {
		// TODO: why isn't this implemented!?
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocumentExtension#stopPostNotificationProcessing()
	 */
	public void stopPostNotificationProcessing() {
		++fStoppedCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocumentExtension#stopSequentialRewrite()
	 */
	public void stopSequentialRewrite() {
		// TODO: why isn't this implemented!?
	}

	public String toString() {
		//return "an instance of IStructuredDocument: " + "@" +
		// Integer.toHexString(hashCode());
		// made shorter, generic for test printouts
		return "an instance of IStructuredDocument"; // + "@" + //$NON-NLS-1$
		// Integer.toHexString(hashCode());
	}

	private StructuredDocumentEvent updateModel(Object requester, int start, int lengthToReplace, String changes) {
		StructuredDocumentEvent result = null;
		IStructuredTextReParser reParser = getReParser();
		// initialize the IStructuredTextReParser with the standard data
		// that's
		// always needed
		reParser.initialize(requester, start, lengthToReplace, changes);
		result = reParser.reparse();
		// if result is null at this point, then there must be an error, since
		// even if there
		// was no change (either disallow due to readonly, or a person pasted
		// the same thing
		// they had selected) then a "NoChange" event should have been fired.
		Assert.isNotNull(result, "no structuredDocument event was created in IStructuredDocument::updateStructuredDocument"); //$NON-NLS-1$
		return result;
	}

	/**
	 * Called by re-parser. Note: this method may be "public" but should only
	 * be called by re-parsers in the right circumstances.
	 */
	public void updateParentDocument(int start, int lengthToReplace, String changes) {
		stopPostNotificationProcessing();
		// update stored text
		try {
			parentDocument.replace(start, lengthToReplace, changes);
		}
		catch (org.eclipse.jface.text.BadLocationException e) {
			throw new SourceEditingRuntimeException(e);
		}
		resumePostNotificationProcessing();
	}

	/**
	 * purely for debugging/performance measurements In practice, would always
	 * be 'true'. (and should never be called by called by clients). Its not
	 * 'final' just so it can be varied during debugging/performance
	 * measurement runs.
	 * 
	 * @param use_local_thread
	 */
	public static void setUSE_LOCAL_THREAD(final boolean use_local_thread) {
		USE_LOCAL_THREAD = use_local_thread;
	}

	public Object getAdapter(Class adapter) {
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	void setFirstDocumentRegion(IStructuredDocumentRegion region) {
		firstDocumentRegion = region;

	}

	void setLastDocumentRegion(IStructuredDocumentRegion region) {
		lastDocumentRegion = region;

	}


	/*
	 * @see org.eclipse.jface.text.IDocumentExtension3#getPartitionings()
	 * @since 3.0
	 */
	public String[] getPartitionings() {
		if (fDocumentPartitioners == null)
			return new String[0];
		String[] partitionings= new String[fDocumentPartitioners.size()];
		fDocumentPartitioners.keySet().toArray(partitionings);
		return partitionings;
	}

	/*
	 * @see org.eclipse.jface.text.IDocumentExtension3#getLegalContentTypes(java.lang.String)
	 * @since 3.0
	 */
	public String[] getLegalContentTypes(String partitioning) throws BadPartitioningException {
		IDocumentPartitioner partitioner= getDocumentPartitioner(partitioning);
		if (partitioner != null)
			return partitioner.getLegalContentTypes();
		if (DEFAULT_PARTITIONING.equals(partitioning))
			return new String[] { DEFAULT_CONTENT_TYPE };
		throw new BadPartitioningException();
	}

	/*
	 * @see org.eclipse.jface.text.IDocumentExtension3#getZeroLengthContentType(java.lang.String, int)
	 * @since 3.0
	 */
	public String getContentType(String partitioning, int offset, boolean preferOpenPartitions) throws BadLocationException, BadPartitioningException {
		if ((0 > offset) || (offset > getLength()))
			throw new BadLocationException();
		
		IDocumentPartitioner partitioner= getDocumentPartitioner(partitioning);

		if (partitioner instanceof IDocumentPartitionerExtension2)
			return ((IDocumentPartitionerExtension2) partitioner).getContentType(offset, preferOpenPartitions);
		else if (partitioner != null)
			return partitioner.getContentType(offset);
		else if (DEFAULT_PARTITIONING.equals(partitioning))
			return DEFAULT_CONTENT_TYPE;
		else
			throw new BadPartitioningException();
	}

	/*
	 * @see org.eclipse.jface.text.IDocumentExtension3#getZeroLengthPartition(java.lang.String, int)
	 * @since 3.0
	 */
	public ITypedRegion getPartition(String partitioning, int offset, boolean preferOpenPartitions) throws BadLocationException, BadPartitioningException {
		if ((0 > offset) || (offset > getLength()))
			throw new BadLocationException();
		
		IDocumentPartitioner partitioner= getDocumentPartitioner(partitioning);
		
		if (partitioner instanceof IDocumentPartitionerExtension2)
			return ((IDocumentPartitionerExtension2) partitioner).getPartition(offset, preferOpenPartitions);
		else if (partitioner != null)
			return partitioner.getPartition(offset);
		else if (DEFAULT_PARTITIONING.equals(partitioning))
			return new TypedRegion(0, getLength(), DEFAULT_CONTENT_TYPE);
		else
			throw new BadPartitioningException();
	}

	/*
	 * @see org.eclipse.jface.text.IDocumentExtension3#computeZeroLengthPartitioning(java.lang.String, int, int)
	 * @since 3.0
	 */
	public ITypedRegion[] computePartitioning(String partitioning, int offset, int length, boolean includeZeroLengthPartitions) throws BadLocationException, BadPartitioningException {
		if ((0 > offset) || (0 > length) || (offset + length > getLength()))
			throw new BadLocationException();
		
		IDocumentPartitioner partitioner= getDocumentPartitioner(partitioning);
		
		if (partitioner instanceof IDocumentPartitionerExtension2)
			return ((IDocumentPartitionerExtension2) partitioner).computePartitioning(offset, length, includeZeroLengthPartitions);
		else if (partitioner != null)
			return partitioner.computePartitioning(offset, length);
		else if (DEFAULT_PARTITIONING.equals(partitioning))
			return new TypedRegion[] { new TypedRegion(offset, length, DEFAULT_CONTENT_TYPE) };
		else
			throw new BadPartitioningException();
	}

	/*
	 * @see org.eclipse.jface.text.IDocumentExtension3#getDocumentPartitioner(java.lang.String)
	 * @since 3.0
	 */
	public IDocumentPartitioner getDocumentPartitioner(String partitioning)  {
		return fDocumentPartitioners != null ? (IDocumentPartitioner) fDocumentPartitioners.get(partitioning) : null;
	}
	/*
	 * @see org.eclipse.jface.text.IDocumentExtension3#setDocumentPartitioner(java.lang.String, org.eclipse.jface.text.IDocumentPartitioner)
	 * @since 3.0
	 */
	public void setDocumentPartitioner(String partitioning, IDocumentPartitioner partitioner) {
		if (partitioner == null) {
			if (fDocumentPartitioners != null) {
				fDocumentPartitioners.remove(partitioning);
				if (fDocumentPartitioners.size() == 0)
					fDocumentPartitioners= null;
			}
		} else {
			if (fDocumentPartitioners == null)
				fDocumentPartitioners= new HashMap();
			fDocumentPartitioners.put(partitioning, partitioner);
		}
		DocumentPartitioningChangedEvent event= new DocumentPartitioningChangedEvent(this);
		event.setPartitionChange(partitioning, 0, getLength());
		fireDocumentPartitioningChanged(event);
	}
	/**
	 * Fires the document partitioning changed notification to all registered 
	 * document partitioning listeners. Uses a robust iterator.
	 * 
	 * @param event the document partitioning changed event
	 * 
	 * @see IDocumentPartitioningListenerExtension2
	 * @since 3.0
	 */
	protected void fireDocumentPartitioningChanged(DocumentPartitioningChangedEvent event) {
		if (fDocumentPartitioningListeners == null || fDocumentPartitioningListeners.size() == 0)
			return;
			
		List list= new ArrayList(fDocumentPartitioningListeners);
		Iterator e= list.iterator();
		while (e.hasNext()) {
			IDocumentPartitioningListener l= (IDocumentPartitioningListener) e.next();
			if (l instanceof IDocumentPartitioningListenerExtension2) {
				IDocumentPartitioningListenerExtension2 extension2= (IDocumentPartitioningListenerExtension2) l;
				extension2.documentPartitioningChanged(event);
			} else if (l instanceof IDocumentPartitioningListenerExtension) {
				IDocumentPartitioningListenerExtension extension= (IDocumentPartitioningListenerExtension) l;
				extension.documentPartitioningChanged(this, event.getCoverage());
			} else {
				l.documentPartitioningChanged(this);
			}
		}
		
	}
	/*
	 * @see org.eclipse.jface.text.IDocument#addDocumentPartitioningListener(org.eclipse.jface.text.IDocumentPartitioningListener)
	 * 	
	 * Registers the document partitioning listener with the document. After
	 * registration the IDocumentPartitioningListener is informed about each
	 * partition change cause by a document manipulation. If a document
	 * partitioning listener is also a document listener, the following
	 * notification sequence is guaranteed if a document manipulation changes
	 * the document partitioning: 1)
	 * listener.documentAboutToBeChanged(DocumentEvent); 2)
	 * listener.documentPartitioningChanged(); 3)
	 * listener.documentChanged(DocumentEvent); If the listener is already
	 * registered nothing happens.
	 * 
	 * @see IDocumentPartitioningListener
	 */
	 
	public void addDocumentPartitioningListener(IDocumentPartitioningListener listener) {
		Assert.isNotNull(listener);
		if (! fDocumentPartitioningListeners.contains(listener))
			fDocumentPartitioningListeners.add(listener);
	}
	/*
	 * @see org.eclipse.jface.text.IDocument#removeDocumentPartitioningListener(org.eclipse.jface.text.IDocumentPartitioningListener)
	 */
	public void removeDocumentPartitioningListener(IDocumentPartitioningListener listener) {
		Assert.isNotNull(listener);
		fDocumentPartitioningListeners.remove(listener);
	}
}
