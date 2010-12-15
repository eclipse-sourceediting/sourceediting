/*******************************************************************************
 * Copyright (c) 2001, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     Jesper Steen Møller - initial IDocumentExtension4 support - #102822
 *                           (see also #239115)
 *     David Carver (Intalio) - bug 300434 - Make inner classes static where possible
 *     David Carver (Intalio) - bug 300443 - some constants aren't static final
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.core.internal.text;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPartitioningException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DefaultLineTracker;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.DocumentPartitioningChangedEvent;
import org.eclipse.jface.text.DocumentRewriteSession;
import org.eclipse.jface.text.DocumentRewriteSessionEvent;
import org.eclipse.jface.text.DocumentRewriteSessionType;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IDocumentPartitionerExtension;
import org.eclipse.jface.text.IDocumentPartitionerExtension2;
import org.eclipse.jface.text.IDocumentPartitionerExtension3;
import org.eclipse.jface.text.IDocumentPartitioningListener;
import org.eclipse.jface.text.IDocumentPartitioningListenerExtension;
import org.eclipse.jface.text.IDocumentPartitioningListenerExtension2;
import org.eclipse.jface.text.IDocumentRewriteSessionListener;
import org.eclipse.jface.text.ILineTracker;
import org.eclipse.jface.text.ILineTrackerExtension;
import org.eclipse.jface.text.IPositionUpdater;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextStore;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TypedRegion;
import org.eclipse.wst.sse.core.internal.Logger;
import org.eclipse.wst.sse.core.internal.document.StructuredDocumentFactory;
import org.eclipse.wst.sse.core.internal.encoding.EncodingMemento;
import org.eclipse.wst.sse.core.internal.ltk.parser.RegionParser;
import org.eclipse.wst.sse.core.internal.provisional.events.AboutToBeChangedEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.IModelAboutToBeChangedListener;
import org.eclipse.wst.sse.core.internal.provisional.events.IStructuredDocumentListener;
import org.eclipse.wst.sse.core.internal.provisional.events.NoChangeEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.RegionChangedEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.RegionsReplacedEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentRegionsReplacedEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegionList;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredPartitioning;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredTextReParser;
import org.eclipse.wst.sse.core.internal.text.rules.StructuredTextPartitioner;
import org.eclipse.wst.sse.core.internal.undo.IStructuredTextUndoManager;
import org.eclipse.wst.sse.core.internal.undo.StructuredTextUndoManager;
import org.eclipse.wst.sse.core.internal.util.Assert;
import org.eclipse.wst.sse.core.internal.util.Debug;
import org.eclipse.wst.sse.core.internal.util.Utilities;


/**
 * The standard implementation of structured document.
 */
public class BasicStructuredDocument implements IStructuredDocument, IDocumentExtension, IDocumentExtension3, IDocumentExtension4, CharSequence, IRegionComparible {

	/**
	 * This ThreadLocal construct is used so each thread can maintain its only
	 * pointer to the double linked list that manages the documents regions.
	 * The only thing we "gaurd" for is that a previously cached region has
	 * been deleted.
	 * 
	 * The object that is kept in the thread local's map, is just a pointer to
	 * an array position. That's because the object there needs to be "free"
	 * from references to other objects, or it will not be garbage collected.
	 */
	private class CurrentDocumentRegionCache {
		// I'm assuming for now there would never be so many threads that
		// this arrayList needs to be bounded, or 'cleaned up'.
		// this assumption should be tested in practice and long running
		// jobs -- found not to be a good assumption. See below.
		private List cachedRegionPositionArray = Collections.synchronizedList(new ArrayList());
		private final boolean DEBUG = false;
		private static final int MAX_SIZE = 50;


		private ThreadLocal threadLocalCachePosition = new ThreadLocal();

		IStructuredDocumentRegion get() {
			IStructuredDocumentRegion region = null;
			int pos = getThreadLocalPosition();
			try {
				region = (IStructuredDocumentRegion) cachedRegionPositionArray.get(pos);
			}
			catch (IndexOutOfBoundsException e) {
				// even though the cachedRegionPosition is synchronized,
				// that just means each access is syncronized, its
				// still possible for another thread to cause it to
				// be cleared, after this thread gets it position.
				// So, if that happens, all we can do is reset to beginning.
				// This should be extremely rare (in other words, probably
				// not worth using synchronized blocks
				// to access cachedRegionPositionArray.
				reinitThreadLocalPosition();
				resetToInitialState();
			}
			if (region == null) {
				region = resetToInitialState();
			}
			else
			// region not null
			if (region.isDeleted()) {
				region = resetToInitialState();
			}
			return region;
		}

		private int getThreadLocalPosition() {
			Object threadLocalObject = threadLocalCachePosition.get();
			int pos = -1;
			if (threadLocalObject == null) {

				pos = reinitThreadLocalPosition();
			}
			else {
				pos = ((Integer) threadLocalObject).intValue();
			}
			return pos;
		}

		/**
		 * @return
		 */
		private int reinitThreadLocalPosition() {
			Integer position;
			int pos;
			// TODO_future: think of a better solution that doesn't
			// require this kludge. This is especially required because
			// some infrasture, such as reconciler, actually null out
			// their thread object and recreate it, 500 msecs later
			// (approximately).
			// Note: the likely solution in future is to clear after every
			// heavy use of getCachedRegion, such as in creating node
			// lists, or reparsing or partioning.
			if (cachedRegionPositionArray.size() > MAX_SIZE) {
				cachedRegionPositionArray.clear();
				if (DEBUG) {
					System.out.println("cachedRegionPositionArray cleared at size " + MAX_SIZE); //$NON-NLS-1$
				}
			}
			position = new Integer(cachedRegionPositionArray.size());
			threadLocalCachePosition.set(position);
			cachedRegionPositionArray.add(position.intValue(), null);
			pos = position.intValue();
			return pos;
		}

		private IStructuredDocumentRegion resetToInitialState() {
			IStructuredDocumentRegion region;
			region = getFirstStructuredDocumentRegion();
			set(region);
			return region;
		}

		void set(IStructuredDocumentRegion region) {
			try {
				int pos = getThreadLocalPosition();
				cachedRegionPositionArray.set(pos, region);
			}
			catch (IndexOutOfBoundsException e) {
				// even though the cachedRegionPosition is synchronized,
				// that just means each access is syncronized, its
				// still possible for another thread to cause it to
				// be cleared, after this thread gets it position.
				// So, if that happens, all we can do is reset to beginning.
				// This should be extremely rare (in other words, probably
				// not worth using synchronized blocks
				// to access cachedRegionPositionArray.
				reinitThreadLocalPosition();
				resetToInitialState();
			}
		}
	}

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

	static class RegisteredReplace {
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

	/**
	 * these control variable isn't mark as 'final' since there's some unit
	 * tests that manipulate it. For final product, it should be.
	 */

	private static boolean USE_LOCAL_THREAD = true;

	/**
	 * purely for debugging/performance measurements In practice, would always
	 * be 'true'. (and should never be called by called by clients). Its not
	 * 'final' or private just so it can be varied during
	 * debugging/performance measurement runs.
	 * 
	 * @param use_local_thread
	 */
	public static void setUSE_LOCAL_THREAD(final boolean use_local_thread) {
		USE_LOCAL_THREAD = use_local_thread;
	}

	private IStructuredDocumentRegion cachedDocumentRegion;
	private EncodingMemento encodingMemento;
	private boolean fAcceptPostNotificationReplaces = true;
	private CurrentDocumentRegionCache fCurrentDocumentRegionCache;
	private DocumentEvent fDocumentEvent;
	private IDocumentListener[] fDocumentListeners;

	/**
	 * The registered document partitioners.
	 */
	private Map fDocumentPartitioners;
	/** The registered document partitioning listeners */
	private List fDocumentPartitioningListeners;
	private IStructuredDocumentRegion firstDocumentRegion;
	private RegionParser fParser;
	private GenericPositionManager fPositionManager;
	private List fPostNotificationChanges;
	private IDocumentListener[] fPrenotifiedDocumentListeners;
	private int fReentranceCount = 0;
	private IStructuredTextReParser fReParser;
	private int fStoppedCount = 0;

	private ITextStore fStore;
	private Object[] fStructuredDocumentAboutToChangeListeners;
	private Object[] fStructuredDocumentChangedListeners;
	private Object[] fStructuredDocumentChangingListeners;

	private List fDocumentRewriteSessionListeners;

	private ILineTracker fTracker;
	private IStructuredTextUndoManager fUndoManager;
	private IStructuredDocumentRegion lastDocumentRegion;

	private byte[] listenerLock = new byte[0];
	private NullDocumentEvent NULL_DOCUMENT_EVENT;

	/**
	 * Theoretically, a document can contain mixed line delimiters, but the
	 * user's preference is usually to be internally consistent.
	 */
	private String fInitialLineDelimiter;
	private static final String READ_ONLY_REGIONS_CATEGORY = "_READ_ONLY_REGIONS_CATEGORY_"; //$NON-NLS-1$
	/**
	 * Current rewrite session, or none if not presently rewriting.
	 */
	private DocumentRewriteSession fActiveRewriteSession;
	/**
	 * Last modification stamp, automatically updated on change.
	 */
	private long fModificationStamp;
	/**
	 * Keeps track of next modification stamp.
	 */
	private long fNextModificationStamp= IDocumentExtension4.UNKNOWN_MODIFICATION_STAMP;
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

	public BasicStructuredDocument() {
		super();
		fCurrentDocumentRegionCache = new CurrentDocumentRegionCache();
		setTextStore(new StructuredDocumentTextStore(50, 300));
		setLineTracker(new DefaultLineTracker());
		NULL_DOCUMENT_EVENT = new NullDocumentEvent();

		internal_addPositionCategory(READ_ONLY_REGIONS_CATEGORY);
		internal_addPositionUpdater(new DeleteEqualPositionUpdater(READ_ONLY_REGIONS_CATEGORY));

	}

	/**
	 * This is the primary way to get a new structuredDocument. Its best to
	 * use the factory methods in ModelManger to create a new
	 * IStructuredDocument, since it will get and initialize the parser
	 * according to the desired content type.
	 */
	public BasicStructuredDocument(RegionParser parser) {
		this();
		Assert.isNotNull(parser, "Program Error: IStructuredDocument can not be created with null parser"); //$NON-NLS-1$
		// go through setter in case there is side effects
		internal_setParser(parser);
	}

	private void _clearDocumentEvent() {
		// no hard and fast requirement to null out ... just seems like
		// a good idea, since we are done with it.
		fDocumentEvent = null;
	}

	private void _fireDocumentAboutToChange(Object[] listeners) {
		// most DocumentAboutToBeChanged listeners do not anticipate
		// DocumentEvent == null. So make sure documentEvent is not
		// null. (this should never happen, yet it does sometimes)
		if (fDocumentEvent == null) {
			fDocumentEvent = new NullDocumentEvent();
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
				// safeguard from listeners that throw exceptions
				try {
					// this is a safe cast, since addListners requires a
					// IStructuredDocumentListener
					((IDocumentListener) holdListeners[i]).documentAboutToBeChanged(fDocumentEvent);
				}
				catch (Exception exception) {
					Logger.logException(exception);
				}
				if (Debug.perfTest || Debug.perfTestStructuredDocumentEventOnly) {
					long stopTime = System.currentTimeMillis();
					System.out.println("\n\t\t\t\t IStructuredDocument::fireStructuredDocumentEvent. Time was " + (stopTime - startTime) + " msecs to fire NewModelEvent to instance of " + holdListeners[i].getClass()); //$NON-NLS-2$//$NON-NLS-1$
				}
			}
		}
	}

	private void notifyDocumentPartitionersAboutToChange(DocumentEvent documentEvent) {
		if (fDocumentPartitioners != null) {
			Iterator e = fDocumentPartitioners.values().iterator();
			while (e.hasNext()) {
				IDocumentPartitioner p = (IDocumentPartitioner) e.next();
				// safeguard from listeners that throw exceptions
				try {
					p.documentAboutToBeChanged(documentEvent);
				}
				catch (Exception exception) {
					Logger.logException(exception);
				}
			}
		}
	}

	private void _fireDocumentChanged(Object[] listeners, StructuredDocumentEvent event) {

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

				// safeguard from listeners that throw exceptions
				try {
					// this is a safe cast, since addListners requires a
					// IStructuredDocumentListener
					// Notes: fDocumentEvent can be "suddenly" null, if one of
					// the
					// previous changes
					// caused a "setText" to be called. The only known case of
					// this
					// is a model reset
					// due to page directive changing. Eventually we should
					// change
					// archetecture to have
					// event que and be able to "cancel" pending events, but
					// for
					// now, we'll just pass a
					// NullDocumentEvent. By the way, it is important to send
					// something, since clients might
					// have indeterminant state due to "aboutToChange" being
					// sent
					// earlier.
					if (fDocumentEvent == null) {
						((IDocumentListener) holdListeners[i]).documentChanged(NULL_DOCUMENT_EVENT);
					}
					else {
						((IDocumentListener) holdListeners[i]).documentChanged(fDocumentEvent);
					}
				}
				catch (Exception exception) {
					Logger.logException(exception);
				}
				if (Debug.perfTest || Debug.perfTestStructuredDocumentEventOnly) {
					long stopTime = System.currentTimeMillis();
					System.out.println("\n\t\t\t\t IStructuredDocument::fireStructuredDocumentEvent. Time was " + (stopTime - startTime) + " msecs to fire NewModelEvent to instance of " + holdListeners[i].getClass()); //$NON-NLS-2$//$NON-NLS-1$
				}
			}
		}
	}

	private void notifyDocumentPartitionersDocumentChanged(DocumentEvent documentEvent) {
		if (fDocumentPartitioners != null) {
			Iterator e = fDocumentPartitioners.values().iterator();
			while (e.hasNext()) {
				IDocumentPartitioner p = (IDocumentPartitioner) e.next();
				// safeguard from listeners that throw exceptions
				try {
					if (p instanceof IDocumentPartitionerExtension) {
						// IRegion changedPartion =
						((IDocumentPartitionerExtension) p).documentChanged2(documentEvent);
					}
					else {
						p.documentChanged(documentEvent);
					}
				}
				catch (Exception exception) {
					Logger.logException(exception);
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
				// safeguard from listeners that throw exceptions
				try {
					// this is a safe cast, since addListners requires a
					// IStructuredDocumentListener
					((IStructuredDocumentListener) holdListeners[i]).noChange(event);
				}
				catch (Exception exception) {
					Logger.logException(exception);
				}
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
				// safeguard from listeners that throw exceptions
				try {
					// this is a safe cast, since addListners requires a
					// IStructuredDocumentListener
					((IStructuredDocumentListener) holdListeners[i]).regionChanged(event);
				}
				catch (Exception exception) {
					Logger.logException(exception);
				}
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
				// safeguard from listeners that throw exceptions
				try {
					// this is a safe cast, since addListners requires a
					// IStructuredDocumentListener
					((IStructuredDocumentListener) holdListeners[i]).regionsReplaced(event);
				}
				catch (Exception exception) {
					Logger.logException(exception);
				}
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
				// safeguard from listeners that throw exceptions
				try {
					// this is a safe cast, since addListners requires a
					// IStructuredDocumentListener
					((IStructuredDocumentListener) holdListeners[i]).nodesReplaced(event);
				}
				catch (Exception exception) {
					Logger.logException(exception);
				}
				if (Debug.perfTest || Debug.perfTestStructuredDocumentEventOnly) {
					long stopTime = System.currentTimeMillis();
					System.out.println("\n\t\t\t\t IStructuredDocument::fireStructuredDocumentEvent. Time was " + (stopTime - startTime) + " msecs to fire NewModelEvent to instance of " + holdListeners[i].getClass()); //$NON-NLS-2$//$NON-NLS-1$
				}
			}
		}
	}

	private void _fireStructuredDocumentAboutToChange(Object[] listeners) {
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
				// safeguard from listeners that throw exceptions
				try {
					// notice the AboutToBeChangedEvent is created from the
					// DocumentEvent, since it is (nearly)
					// the same information. ?What to do about
					// originalRequester?
					if (fDocumentEvent == null) {
						fDocumentEvent = new NullDocumentEvent();
					}
					AboutToBeChangedEvent aboutToBeChangedEvent = new AboutToBeChangedEvent(this, null, fDocumentEvent.getText(), fDocumentEvent.getOffset(), fDocumentEvent.getLength());
					// this is a safe cast, since addListners requires a
					// IStructuredDocumentListener
					((IModelAboutToBeChangedListener) holdListeners[i]).modelAboutToBeChanged(aboutToBeChangedEvent);
				}
				catch (Exception exception) {
					Logger.logException(exception);
				}
				if (Debug.perfTest || Debug.perfTestStructuredDocumentEventOnly) {
					long stopTime = System.currentTimeMillis();
					System.out.println("\n\t\t\t\t IStructuredDocument::fireStructuredDocumentEvent. Time was " + (stopTime - startTime) + " msecs to fire NewModelEvent to instance of " + holdListeners[i].getClass()); //$NON-NLS-2$//$NON-NLS-1$
				}
			}
		}
	}

	protected void acquireLock() {
		// do nothing here in super class
	}

	/**
	 * addModelAboutToBeChangedListener method comment.
	 */
	public void addDocumentAboutToChangeListener(IModelAboutToBeChangedListener listener) {
		synchronized (listenerLock) {

			// make sure listener is not already in listening
			// (and if it is, print a warning to aid debugging, if needed)
			if (!Utilities.contains(fStructuredDocumentAboutToChangeListeners, listener)) {
				int oldSize = 0;
				if (fStructuredDocumentAboutToChangeListeners != null) {
					// normally won't be null, but we need to be sure, for
					// first
					// time through
					oldSize = fStructuredDocumentAboutToChangeListeners.length;
				}
				int newSize = oldSize + 1;
				Object[] newListeners = new Object[newSize];
				if (fStructuredDocumentAboutToChangeListeners != null) {
					System.arraycopy(fStructuredDocumentAboutToChangeListeners, 0, newListeners, 0, oldSize);
				}
				// add listener to last position
				newListeners[newSize - 1] = listener;
				//
				// now switch new for old
				fStructuredDocumentAboutToChangeListeners = newListeners;
				//
			}
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
	public void addDocumentChangedListener(IStructuredDocumentListener listener) {
		synchronized (listenerLock) {

			if (Debug.debugStructuredDocument) {
				System.out.println("IStructuredDocument::addModelChangedListener. Request to add an instance of " + listener.getClass() + " as a listener on structuredDocument."); //$NON-NLS-2$//$NON-NLS-1$
			}
			// make sure listener is not already in listening
			// (and if it is, print a warning to aid debugging, if needed)
			if (Utilities.contains(fStructuredDocumentChangedListeners, listener)) {
				if (Debug.displayWarnings) {
					System.out.println("IStructuredDocument::addModelChangedListener. listener " + listener + " was addeded more than once. "); //$NON-NLS-2$//$NON-NLS-1$
				}
			}
			else {
				if (Debug.debugStructuredDocument) {
					System.out.println("IStructuredDocument::addModelChangedListener. Adding an instance of " + listener.getClass() + " as a listener on structuredDocument."); //$NON-NLS-2$//$NON-NLS-1$
				}
				int oldSize = 0;
				if (fStructuredDocumentChangedListeners != null) {
					// normally won't be null, but we need to be sure, for
					// first
					// time through
					oldSize = fStructuredDocumentChangedListeners.length;
				}
				int newSize = oldSize + 1;
				Object[] newListeners = new Object[newSize];
				if (fStructuredDocumentChangedListeners != null) {
					System.arraycopy(fStructuredDocumentChangedListeners, 0, newListeners, 0, oldSize);
				}
				// add listener to last position
				newListeners[newSize - 1] = listener;
				//
				// now switch new for old
				fStructuredDocumentChangedListeners = newListeners;
				//
				// when a listener is added,
				// send the new model event to that one particular listener,
				// so it
				// can initialize itself with the current state of the model
				// listener.newModel(new NewModelEvent(this, listener));
			}
		}
	}

	public void addDocumentChangingListener(IStructuredDocumentListener listener) {
		synchronized (listenerLock) {

			if (Debug.debugStructuredDocument) {
				System.out.println("IStructuredDocument::addStructuredDocumentListener. Request to add an instance of " + listener.getClass() + " as a listener on structuredDocument."); //$NON-NLS-2$//$NON-NLS-1$
			}
			// make sure listener is not already in listening
			// (and if it is, print a warning to aid debugging, if needed)
			if (Utilities.contains(fStructuredDocumentChangingListeners, listener)) {
				if (Debug.displayWarnings) {
					System.out.println("IStructuredDocument::addStructuredDocumentListener. listener " + listener + " was addeded more than once. "); //$NON-NLS-2$//$NON-NLS-1$
				}
			}
			else {
				if (Debug.debugStructuredDocument) {
					System.out.println("IStructuredDocument::addStructuredDocumentListener. Adding an instance of " + listener.getClass() + " as a listener on structuredDocument."); //$NON-NLS-2$//$NON-NLS-1$
				}
				int oldSize = 0;
				if (fStructuredDocumentChangingListeners != null) {
					// normally won't be null, but we need to be sure, for
					// first
					// time through
					oldSize = fStructuredDocumentChangingListeners.length;
				}
				int newSize = oldSize + 1;
				Object[] newListeners = new Object[newSize];
				if (fStructuredDocumentChangingListeners != null) {
					System.arraycopy(fStructuredDocumentChangingListeners, 0, newListeners, 0, oldSize);
				}
				// add listener to last position
				newListeners[newSize - 1] = listener;
				//
				// now switch new for old
				fStructuredDocumentChangingListeners = newListeners;
				//
				// when a listener is added,
				// send the new model event to that one particular listener,
				// so it
				// can initialize itself with the current state of the model
				// listener.newModel(new NewModelEvent(this, listener));
			}
		}
	}

	/**
	 * We manage our own document listners, instead of delegating to our
	 * parentDocument, so we can fire at very end (and not when the
	 * parentDocument changes).
	 * 
	 */
	public void addDocumentListener(IDocumentListener listener) {
		synchronized (listenerLock) {

			// make sure listener is not already in listening
			// (and if it is, print a warning to aid debugging, if needed)
			if (!Utilities.contains(fDocumentListeners, listener)) {
				int oldSize = 0;
				if (fDocumentListeners != null) {
					// normally won't be null, but we need to be sure, for
					// first
					// time through
					oldSize = fDocumentListeners.length;
				}
				int newSize = oldSize + 1;
				IDocumentListener[] newListeners = null;
				newListeners = new IDocumentListener[newSize];
				if (fDocumentListeners != null) {
					System.arraycopy(fDocumentListeners, 0, newListeners, 0, oldSize);
				}
				// add listener to last position
				newListeners[newSize - 1] = listener;
				// now switch new for old
				fDocumentListeners = newListeners;
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
		synchronized (listenerLock) {

			Assert.isNotNull(listener);
			if (fDocumentPartitioningListeners == null) {
				fDocumentPartitioningListeners = new ArrayList(1);
			}
			if (!fDocumentPartitioningListeners.contains(listener))
				fDocumentPartitioningListeners.add(listener);
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
		getPositionManager().addPosition(position);
	}

	/**
	 * @see IDocument#addPosition
	 * @exception BadLocationException
	 *                If position is not a valid range in the document
	 * @exception BadPositionCategoryException
	 *                If the category is not defined for the document
	 */
	public void addPosition(String category, Position position) throws BadLocationException, BadPositionCategoryException {
		getPositionManager().addPosition(category, position);
	}

	/**
	 * @see IDocument#addPositionCategory
	 */
	public void addPositionCategory(String category) {
		internal_addPositionCategory(category);
	}

	/**
	 * @see IDocument#addPositionUpdater
	 */
	public void addPositionUpdater(IPositionUpdater updater) {
		internal_addPositionUpdater(updater);
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
		synchronized (listenerLock) {

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
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.CharSequence#charAt(int)
	 */
	public char charAt(int arg0) {
		try {
			return getChar(0);
		}
		catch (BadLocationException e) {
			throw new IndexOutOfBoundsException();
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
					// fDocumentEvent = new DocumentEvent(this, startOffset,
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
		return getPositionManager().computeIndexInCategory(category, offset);
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
		return getTracker().computeNumberOfLines(text);
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
			typedRegions = computePartitioning(IStructuredPartitioning.DEFAULT_STRUCTURED_PARTITIONING, offset, length, false);
		}
		catch (BadPartitioningException e) {
			// impossible in this context
			throw new Error(e);
		}
		if (typedRegions == null) {
			typedRegions = new ITypedRegion[0];
		}
		return typedRegions;
	}


	public ITypedRegion[] computePartitioning(String partitioning, int offset, int length, boolean includeZeroLengthPartitions) throws BadLocationException, BadPartitioningException {
		if ((0 > offset) || (0 > length) || (offset + length > getLength()))
			throw new BadLocationException();

		IDocumentPartitioner partitioner = getDocumentPartitioner(partitioning);

		if (partitioner instanceof IDocumentPartitionerExtension2)
			return ((IDocumentPartitionerExtension2) partitioner).computePartitioning(offset, length, includeZeroLengthPartitions);
		else if (partitioner != null)
			return partitioner.computePartitioning(offset, length);
		else if (IStructuredPartitioning.DEFAULT_STRUCTURED_PARTITIONING.equals(partitioning))
			return new TypedRegion[]{new TypedRegion(offset, length, DEFAULT_CONTENT_TYPE)};
		else
			throw new BadPartitioningException();
	}

	/**
	 * @see IDocument#containsPosition
	 */
	public boolean containsPosition(String category, int offset, int length) {
		return getPositionManager().containsPosition(category, offset, length);
	}

	/**
	 * @see IDocument#containsPositionCategory
	 */
	public boolean containsPositionCategory(String category) {
		return getPositionManager().containsPositionCategory(category);
	}

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

	private void fireDocumentAboutToChanged() {
		// most DocumentAboutToBeChanged listeners do not anticipate
		// DocumentEvent == null. So make sure documentEvent is not
		// null. (this should never happen, yet it does sometimes)
		if (fDocumentEvent == null) {
			fDocumentEvent = new NullDocumentEvent();
		}

		_fireStructuredDocumentAboutToChange(fStructuredDocumentAboutToChangeListeners);
		// Note: the docEvent is created in replaceText API! (or set Text)
		_fireDocumentAboutToChange(fPrenotifiedDocumentListeners);
		notifyDocumentPartitionersAboutToChange(fDocumentEvent);
		_fireDocumentAboutToChange(fDocumentListeners);
	}

	/**
	 * Fires the document partitioning changed notification to all registered
	 * document partitioning listeners. Uses a robust iterator.
	 * 
	 * @param event
	 *            the document partitioning changed event
	 * 
	 * @see IDocumentPartitioningListenerExtension2
	 */
	protected void fireDocumentPartitioningChanged(DocumentPartitioningChangedEvent event) {
		if (fDocumentPartitioningListeners == null || fDocumentPartitioningListeners.size() == 0)
			return;

		List list = new ArrayList(fDocumentPartitioningListeners);
		Iterator e = list.iterator();
		while (e.hasNext()) {
			IDocumentPartitioningListener l = (IDocumentPartitioningListener) e.next();
			if (l instanceof IDocumentPartitioningListenerExtension2) {
				IDocumentPartitioningListenerExtension2 extension2 = (IDocumentPartitioningListenerExtension2) l;
				extension2.documentPartitioningChanged(event);
			}
			else if (l instanceof IDocumentPartitioningListenerExtension) {
				IDocumentPartitioningListenerExtension extension = (IDocumentPartitioningListenerExtension) l;
				extension.documentPartitioningChanged(this, event.getCoverage());
			}
			else {
				l.documentPartitioningChanged(this);
			}
		}

	}

	private void fireReadOnlyAboutToBeChanged() {
		_fireStructuredDocumentAboutToChange(fStructuredDocumentAboutToChangeListeners);
		// Note: the docEvent is created in replaceText API! (or set Text)
		// _fireDocumentAboutToChange(fPrenotifiedDocumentListeners);
		// _fireDocumentAboutToChange(fDocumentListeners);
	}

	private void fireReadOnlyStructuredDocumentEvent(NoChangeEvent event) {
		_fireEvent(fStructuredDocumentChangingListeners, event);
		_fireEvent(fStructuredDocumentChangedListeners, event);
		// _fireDocumentChanged(fPrenotifiedDocumentListeners, event);
		// _fireDocumentChanged(fDocumentListeners, event);
		// _clearDocumentEvent();
	}

	private void fireStructuredDocumentEvent(NoChangeEvent event) {
		_fireEvent(fStructuredDocumentChangingListeners, event);
		_fireEvent(fStructuredDocumentChangedListeners, event);
		_fireDocumentChanged(fPrenotifiedDocumentListeners, event);
		notifyDocumentPartitionersDocumentChanged(event);
		_fireDocumentChanged(fDocumentListeners, event);
		_clearDocumentEvent();
	}

	private void fireStructuredDocumentEvent(RegionChangedEvent event) {
		_fireEvent(fStructuredDocumentChangingListeners, event);
		_fireEvent(fStructuredDocumentChangedListeners, event);
		_fireDocumentChanged(fPrenotifiedDocumentListeners, event);
		notifyDocumentPartitionersDocumentChanged(event);
		_fireDocumentChanged(fDocumentListeners, event);
		_clearDocumentEvent();
	}

	private void fireStructuredDocumentEvent(RegionsReplacedEvent event) {
		_fireEvent(fStructuredDocumentChangingListeners, event);
		_fireEvent(fStructuredDocumentChangedListeners, event);
		_fireDocumentChanged(fPrenotifiedDocumentListeners, event);
		notifyDocumentPartitionersDocumentChanged(event);
		_fireDocumentChanged(fDocumentListeners, event);
		_clearDocumentEvent();
	}

	private void fireStructuredDocumentEvent(StructuredDocumentRegionsReplacedEvent event) {
		_fireEvent(fStructuredDocumentChangingListeners, event);
		_fireEvent(fStructuredDocumentChangedListeners, event);
		_fireDocumentChanged(fPrenotifiedDocumentListeners, event);
		notifyDocumentPartitionersDocumentChanged(event);
		_fireDocumentChanged(fDocumentListeners, event);
		_clearDocumentEvent();
	}

	/**
	 * Returns the document's complete text.
	 */
	public String get() {
		return getStore().get(0, getLength());
	}

	/**
	 * Returns length characters from the document's text starting from the
	 * specified position.
	 * 
	 * @throws BadLocationException
	 * 
	 * @exception BadLocationException
	 *                If the range is not valid in the document
	 */
	public String get(int offset, int length) {
		String result = null;
		int myLength = getLength();
		if (0 > offset)
			offset = 0;
		if (0 > length)
			length = 0;
		if (offset + length > myLength) {
			// first try adjusting length to fit
			int lessLength = myLength - offset;
			if ((lessLength >= 0) && (offset + lessLength == myLength)) {
				length = lessLength;
			}
			else {
				// second, try offset
				int moreOffset = myLength - length;
				if ((moreOffset >= 0) && (moreOffset + length == myLength)) {
					offset = moreOffset;
				}
				else {
					// can happen if myLength is 0.
					// no adjustment possible.
					result = new String();
				}
			}

		}
		if (result == null) {
			result = getStore().get(offset, length);
		}
		return result;
	}

	public Object getAdapter(Class adapter) {
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	IStructuredDocumentRegion getCachedDocumentRegion() {
		IStructuredDocumentRegion result = null;
		if (USE_LOCAL_THREAD) {
			result = fCurrentDocumentRegionCache.get();
		}
		else {
			result = cachedDocumentRegion;
		}
		return result;
	}

	/**
	 * @see IDocument#getChar
	 * @exception BadLocationException
	 *                If position is not a valid range in the document
	 */
	public char getChar(int pos) throws BadLocationException {
		char result = 0x00;
		try {
			result = getStore().get(pos);
		}
		catch (IndexOutOfBoundsException e) {
			throw new BadLocationException(e.getLocalizedMessage());
		}
		return result;
	}

	/**
	 * Returns the type of the document partition containing the given
	 * character position.
	 */
	public String getContentType(int offset) throws BadLocationException {
		return getDocumentPartitioner().getContentType(offset);
	}


	public String getContentType(String partitioning, int offset, boolean preferOpenPartitions) throws BadLocationException, BadPartitioningException {
		if ((0 > offset) || (offset > getLength()))
			throw new BadLocationException();

		IDocumentPartitioner partitioner = getDocumentPartitioner(partitioning);

		if (partitioner instanceof IDocumentPartitionerExtension2)
			return ((IDocumentPartitionerExtension2) partitioner).getContentType(offset, preferOpenPartitions);
		else if (partitioner != null)
			return partitioner.getContentType(offset);
		else if (IStructuredPartitioning.DEFAULT_STRUCTURED_PARTITIONING.equals(partitioning))
			return DEFAULT_CONTENT_TYPE;
		else
			throw new BadPartitioningException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocumentExtension4#getDefaultLineDelimiter()
	 */
	public String getDefaultLineDelimiter() {
		
		String lineDelimiter= null;
		
		try {
			lineDelimiter= getLineDelimiter(0);
		} catch (BadLocationException x) {
		}
	
		if (lineDelimiter != null)
			return lineDelimiter;

		if (fInitialLineDelimiter != null)
			return fInitialLineDelimiter;

		String sysLineDelimiter= System.getProperty("line.separator"); //$NON-NLS-1$
		String[] delimiters= getLegalLineDelimiters();
		Assert.isTrue(delimiters.length > 0);
		for (int i= 0; i < delimiters.length; i++) {
			if (delimiters[i].equals(sysLineDelimiter)) {
				lineDelimiter= sysLineDelimiter;
				break;
			}
		}
		
		if (lineDelimiter == null)
			lineDelimiter= delimiters[0];
	
		return lineDelimiter;
		
	}

	/**
	 * Returns the document's partitioner.
	 * 
	 * @see IDocumentPartitioner
	 */
	public IDocumentPartitioner getDocumentPartitioner() {
		return getDocumentPartitioner(IDocumentExtension3.DEFAULT_PARTITIONING);
	}


	public IDocumentPartitioner getDocumentPartitioner(String partitioning) {

		IDocumentPartitioner documentPartitioner = null;
		if (fDocumentPartitioners != null) {
			documentPartitioner = (IDocumentPartitioner) fDocumentPartitioners.get(partitioning);
		}
		return documentPartitioner;
	}

	public EncodingMemento getEncodingMemento() {
		return encodingMemento;
	}

	public IStructuredDocumentRegion getFirstStructuredDocumentRegion() {
		// should we update cachedNode?
		// We should to keep consistent philosophy of remembering last
		// requested position,
		// for efficiency.
		setCachedDocumentRegion(firstDocumentRegion);
		return firstDocumentRegion;
	}

	public IStructuredDocumentRegion getLastStructuredDocumentRegion() {
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
		String[] result = null;
		try {
			result = getLegalContentTypes(IStructuredPartitioning.DEFAULT_STRUCTURED_PARTITIONING);
		}
		catch (BadPartitioningException e) {
			// impossible in this context
			throw new Error(e);
		}
		return result;
	}

	public String[] getLegalContentTypes(String partitioning) throws BadPartitioningException {
		IDocumentPartitioner partitioner = getDocumentPartitioner(partitioning);
		if (partitioner != null)
			return partitioner.getLegalContentTypes();
		if (IStructuredPartitioning.DEFAULT_STRUCTURED_PARTITIONING.equals(partitioning))
			return new String[]{DEFAULT_CONTENT_TYPE};
		throw new BadPartitioningException();
	}

	/*
	 * ------------------ line delimiter conversion
	 * ---------------------------
	 */
	public String[] getLegalLineDelimiters() {
		return getTracker().getLegalLineDelimiters();
	}

	/**
	 * @see IDocument#getLength
	 */
	public int getLength() {
		return getStore().getLength();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument#getLineDelimiter()
	 */
	public String getLineDelimiter() {
		return getDefaultLineDelimiter();
	}

	/**
	 * Returns the line delimiter of that line
	 * 
	 * @exception BadLocationException
	 *                If the line number is invalid in the document
	 */
	public String getLineDelimiter(int line) throws org.eclipse.jface.text.BadLocationException {
		return getTracker().getLineDelimiter(line);
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
		return getTracker().getLineInformation(line);
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
		return getTracker().getLineInformationOfOffset(offset);
	}

	/*
	 * ---------------------- line information
	 * --------------------------------
	 */
	public int getLineLength(int line) throws org.eclipse.jface.text.BadLocationException {
		return getTracker().getLineLength(line);
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
		return getTracker().getLineOffset(line);
	}

	public int getLineOfOffset(int offset) {
		int result = -1;
		try {
			result = getTracker().getLineNumberOfOffset(offset);
		}
		catch (BadLocationException e) {
			if (Logger.DEBUG_DOCUMENT)
				Logger.log(Logger.INFO, "Dev. Program Info Only: IStructuredDocument::getLineOfOffset: offset out of range, zero assumed. offset = " + offset, e); //$NON-NLS-1$ //$NON-NLS-2$
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
		return getTracker().getNumberOfLines();
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
		return getTracker().getNumberOfLines(offset, length);
	}

	/**
	 * This is public, temporarily, for use by tag lib classes.
	 */
	public RegionParser getParser() {
		if (fParser == null) {
			throw new IllegalStateException("IStructuredDocument::getParser. Parser needs to be set before use"); //$NON-NLS-1$
		}
		return fParser;
	}

	/**
	 * Returns the document partition in which the position is located. The
	 * partition is specified as typed region.
	 */
	public ITypedRegion getPartition(int offset) throws BadLocationException {
		ITypedRegion partition = null;
		try {
			partition = getPartition(IStructuredPartitioning.DEFAULT_STRUCTURED_PARTITIONING, offset, false);
		}
		catch (BadPartitioningException e) {
			throw new Error(e);
		}
		if (partition == null) {
			throw new Error();
		}
		return partition;
	}


	public ITypedRegion getPartition(String partitioning, int offset, boolean preferOpenPartitions) throws BadLocationException, BadPartitioningException {
		if ((0 > offset) || (offset > getLength()))
			throw new BadLocationException();
		ITypedRegion result = null;

		IDocumentPartitioner partitioner = getDocumentPartitioner(partitioning);

		if (partitioner instanceof IDocumentPartitionerExtension2) {
			result = ((IDocumentPartitionerExtension2) partitioner).getPartition(offset, preferOpenPartitions);
		}
		else if (partitioner != null) {
			result = partitioner.getPartition(offset);
		}
		else if (IStructuredPartitioning.DEFAULT_STRUCTURED_PARTITIONING.equals(partitioning)) {
			result = new TypedRegion(0, getLength(), DEFAULT_CONTENT_TYPE);
		}
		else
			throw new BadPartitioningException();
		return result;
	}


	public String[] getPartitionings() {
		if (fDocumentPartitioners == null)
			return new String[0];
		String[] partitionings = new String[fDocumentPartitioners.size()];
		fDocumentPartitioners.keySet().toArray(partitionings);
		return partitionings;
	}

	/**
	 * Returns all position categories added to this document.
	 */
	public String[] getPositionCategories() {
		return getPositionManager().getPositionCategories();
	}

	/**
	 * @return Returns the positionManager.
	 */
	private GenericPositionManager getPositionManager() {
		if (fPositionManager == null) {
			fPositionManager = new GenericPositionManager(this);
		}
		return fPositionManager;
	}

	/**
	 * Returns all Positions of the given position category.
	 * 
	 * @exception BadPositionCategoryException
	 *                If category is not defined for the document
	 */
	public Position[] getPositions(String category) throws org.eclipse.jface.text.BadPositionCategoryException {
		return getPositionManager().getPositions(category);
	}

	/**
	 * @see IDocument#getPositionUpdaters
	 */
	public IPositionUpdater[] getPositionUpdaters() {
		return getPositionManager().getPositionUpdaters();
	}

	/**
	 * This method can return null, which is the case if the offset is just
	 * before or just after the existing text. Compare with
	 * getNodeAtCharacterOffset.
	 */
	public IStructuredDocumentRegion getRegionAtCharacterOffset(int offset) {
		IStructuredDocumentRegion result = null;

		// FIXME: need to synch on 'cachedRegion' (but since that's a
		// constantly changing object, we
		// can't, so need to add a "region_lock" object, and use it here, and
		// in re-parser.
		// Oh, and need to make sure, after synch, that the region is not
		// deleted, and if so, I guess go back
		// to the beginning!

		// cached node can be null when document is empty
		IStructuredDocumentRegion potentialCachedRegion = getCachedDocumentRegion();
		if (potentialCachedRegion != null) {

			// 

			// if we already have the right node, return that.
			if (potentialCachedRegion.containsOffset(offset)) {
				result = potentialCachedRegion;
			}
			else {
				// first, find out what direction to go, relative to
				// cachedNode.
				// negative means "towards the front" of the file,
				// postitive
				// means
				// towards the end.
				int direction = offset - potentialCachedRegion.getStart();
				if (direction < 0) {
					// search towards beginning
					while (!potentialCachedRegion.containsOffset(offset)) {
						IStructuredDocumentRegion tempNode = potentialCachedRegion.getPrevious();
						if (tempNode == null) {
							break;
						}
						else {
							potentialCachedRegion = tempNode;
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
					while (!potentialCachedRegion.containsOffset(offset)) {
						IStructuredDocumentRegion tempNode = potentialCachedRegion.getNext();
						if (tempNode == null)
							break;
						else
							potentialCachedRegion = tempNode;
					}
				}
			}
			result = potentialCachedRegion;
		}
		// just to be doubly sure we never assign null to an already valid
		// cachedRegion.
		// I believe any time 'result' is null at this point, that just means
		// we have an
		// empty document, and the cachedRegion is already null, but we check
		// and print
		// warning, just so during development we be sure we never accidently
		// break this assumption.
		if (result != null)
			setCachedDocumentRegion(result);
		else if (getCachedDocumentRegion() != null) {
			throw new IllegalStateException("Program Error: no region could be found to cache, but cache was non null. Indicates corrupted model or region list"); //$NON-NLS-1$
		}

		return result;
	}

	public IStructuredDocumentRegionList getRegionList() {
		CoreNodeList result = null;
		if (getCachedDocumentRegion() == null)
			result = new CoreNodeList(null);
		else
			result = new CoreNodeList(getFirstStructuredDocumentRegion());

		return result;
	}


	public IStructuredDocumentRegion[] getStructuredDocumentRegions() {
		return getStructuredDocumentRegions(0, getLength());
	}

	/**
	 * <p>
	 * In the case of 0 length, the <code>IStructuredDocumentRegion</code>
	 * at the character offset is returened. In other words, the region to the
	 * right of the caret is returned. except for at the end of the document,
	 * then the last region is returned.
	 * </p>
	 * <p>
	 * Otherwise all the regions "inbetween" the indicated range are returned,
	 * including the regions which overlap the region.
	 * </p>
	 * 
	 * <br>
	 * eg.
	 * <p>
	 * <br>
	 * eg.
	 * 
	 * <pre>
	 *                        &lt;html&gt;[&lt;head&gt;&lt;/head&gt;]&lt;/html&gt; returns &lt;head&gt;,&lt;/head&gt;
	 * </pre>
	 *    <pre>
	 *                        &lt;ht[ml&gt;&lt;head&gt;&lt;/he]ad&gt;&lt;/html&gt; returns &lt;html&gt;,&lt;head&gt;,&lt;/head&gt;
	 * </pre>
	 * 
	 * <pre>
	 *                          &lt;html&gt;[&lt;head&gt;&lt;/head&gt;]&lt;/html&gt; returns &lt;head&gt;,&lt;/head&gt;
	 * </pre>
	 *    <pre>
	 *                          &lt;ht[ml&gt;&lt;head&gt;&lt;/he]ad&gt;&lt;/html&gt; returns &lt;html&gt;,&lt;head&gt;,&lt;/head&gt;
	 * </pre>
	 * 
	 * </p>
	 */
	public IStructuredDocumentRegion[] getStructuredDocumentRegions(int start, int length) {

		if (length < 0)
			throw new IllegalArgumentException("can't have negative length"); //$NON-NLS-1$

		// this will make the right edge of the range point into the selection
		// eg. <html>[<head></head>]</html>
		// will return <head>,</head> instead of <head>,</head>,</html>
		if (length > 0)
			length--;

		List results = new ArrayList();

		// start thread safe block
		try {
			acquireLock();

			IStructuredDocumentRegion currentRegion = getRegionAtCharacterOffset(start);
			IStructuredDocumentRegion endRegion = getRegionAtCharacterOffset(start + length);
			while (currentRegion != endRegion && currentRegion != null) {
				results.add(currentRegion);
				currentRegion = currentRegion.getNext();
			}
			// need to add that last end region
			// can be null in the case of an empty document
			if (endRegion != null)
				results.add(endRegion);
		}
		finally {
			releaseLock();
		}
		// end thread safe block

		return (IStructuredDocumentRegion[]) results.toArray(new IStructuredDocumentRegion[results.size()]);
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

	private ITextStore getStore() {
		return fStore;
	}

	public String getText() {
		String result = get();
		return result;
	}

	/**
	 * Returns the document's line tracker. Assumes that the document has been
	 * initialized with a line tracker.
	 * 
	 * @return the document's line tracker
	 */
	private ILineTracker getTracker() {
		return fTracker;
	}

	public IStructuredTextUndoManager getUndoManager() {
		if (fUndoManager == null) {
			fUndoManager = new StructuredTextUndoManager();
		}
		return fUndoManager;
	}

	void initializeFirstAndLastDocumentRegion() {
		// cached Node must also be first, at the initial point. Only
		// valid
		// to call this method right after the first parse.
		// 
		// when starting afresh, our cachedNode should be our firstNode,
		// so be sure to initialize the firstNode
		firstDocumentRegion = getCachedDocumentRegion();
		// be sure to use 'getNext' for this initial finding of the last
		// node,
		// since the implementation of node.getLastNode may simply call
		// structuredDocument.getLastStructuredDocumentRegion!
		IStructuredDocumentRegion aNode = firstDocumentRegion;
		if (aNode == null) {
			// defect 254607: to handle empty documents right, if
			// firstnode is
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
		getPositionManager().insertPositionUpdater(updater, index);
	}

	private void internal_addPositionCategory(String category) {
		getPositionManager().addPositionCategory(category);
	}

	private void internal_addPositionUpdater(IPositionUpdater updater) {
		getPositionManager().addPositionUpdater(updater);
	}

	private void internal_setParser(RegionParser newParser) {
		fParser = newParser;
	}

	String internalGet(int offset, int length) {
		String result = null;
		// int myLength = getLength();
		// if ((0 > offset) || (0 > length) || (offset + length > myLength))
		// throw new BadLocationException();
		result = getStore().get(offset, length);
		return result;
	}

	/**
	 * @param requester
	 * @param start
	 * @param replacementLength
	 * @param changes
	 * @param modificationStamp
	 * @param ignoreReadOnlySettings
	 * @return
	 */
	private StructuredDocumentEvent internalReplaceText(Object requester, int start, int replacementLength, String changes, long modificationStamp, boolean ignoreReadOnlySettings) {
		StructuredDocumentEvent result = null;

		stopPostNotificationProcessing();
		if (changes == null)
			changes = ""; //$NON-NLS-1$
		// 
		if (Debug.debugStructuredDocument)
			System.out.println(getClass().getName() + "::replaceText(" + start + "," + replacementLength + "," + changes + ")"); //$NON-NLS-4$//$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
		if (Debug.perfTestStructuredDocumentOnly || Debug.perfTest || Debug.perfTestRawStructuredDocumentOnly) {
			startStreamTime = System.currentTimeMillis();
		}
		try {
			// Note: event must be computed before 'fire' method called
			fDocumentEvent = new DocumentEvent(this, start, replacementLength, changes);
			fireDocumentAboutToChanged();

			try {
				acquireLock();

				if (!ignoreReadOnlySettings && (containsReadOnly(start, replacementLength))) {
					NoChangeEvent noChangeEvent = new NoChangeEvent(this, requester, changes, start, replacementLength);
					noChangeEvent.reason = NoChangeEvent.READ_ONLY_STATE_CHANGE;
					result = noChangeEvent;
				}
				else {
					result = updateModel(requester, start, replacementLength, changes);
				}
			}
			finally {
				releaseLock();
			}


			if (Debug.perfTestRawStructuredDocumentOnly || Debug.perfTest) {
				long stopStreamTime = System.currentTimeMillis();
				System.out.println("\n\t\t\t\t Time for IStructuredDocument raw replaceText: " + (stopStreamTime - startStreamTime)); //$NON-NLS-1$
			}
			if (Debug.debugStructuredDocument) {
				System.out.println("event type returned by replaceTextWithNoDebuggingThread: " + result); //$NON-NLS-1$
			}
		}
		finally {
			// FUTURE_TO_DO: implement callback mechanism? to avoid instanceof
			// and casting
			// fireStructuredDocumentEvent must be called in order to end
			// documentAboutToBeChanged state


			// increment modification stamp if modifications were made
			if (result != null && !(result instanceof NoChangeEvent)) {
				fModificationStamp= modificationStamp;
				fNextModificationStamp= Math.max(fModificationStamp, fNextModificationStamp);
				fDocumentEvent.fModificationStamp = fModificationStamp;
			}
				
			if (result == null) {
				// result should not be null, but if an exception was thrown,
				// it will be
				// so send a noChangeEvent and log the problem
				NoChangeEvent noChangeEvent = new NoChangeEvent(this, requester, changes, start, replacementLength);
				noChangeEvent.reason = NoChangeEvent.NO_EVENT;
				fireStructuredDocumentEvent(noChangeEvent);
				Logger.log(Logger.ERROR, "Program Error: invalid structured document event"); //$NON-NLS-1$
			}
			else {
				if (result instanceof RegionChangedEvent) {
					fireStructuredDocumentEvent((RegionChangedEvent) result);
				}
				else {
					if (result instanceof RegionsReplacedEvent) {
						fireStructuredDocumentEvent((RegionsReplacedEvent) result);
					}
					else {
						if (result instanceof StructuredDocumentRegionsReplacedEvent) {
							// probably more efficient to mark old regions as
							// 'deleted' at the time
							// that are determined to be deleted, but I'll do
							// here
							// in then central spot
							// for programming ease.
							updateDeletedFields((StructuredDocumentRegionsReplacedEvent) result);
							fireStructuredDocumentEvent((StructuredDocumentRegionsReplacedEvent) result);
						}
						else {
							if (result instanceof NoChangeEvent) {
								fireStructuredDocumentEvent((NoChangeEvent) result);
							}
							else {
								// if here, this means a new event was created
								// and not handled here
								// just send a no event until this issue is
								// resolved.
								NoChangeEvent noChangeEvent = new NoChangeEvent(this, requester, changes, start, replacementLength);
								noChangeEvent.reason = NoChangeEvent.NO_EVENT;
								fireStructuredDocumentEvent(noChangeEvent);
								Logger.log(Logger.INFO, "Program Error: unexpected structured document event: " + result); //$NON-NLS-1$
							}
						}
					}
				}
			}

			if (Debug.perfTest || Debug.perfTestStructuredDocumentOnly) {
				long stopStreamTime = System.currentTimeMillis();
				System.out.println("\n\t\t\t\t Total Time for IStructuredDocument event signaling/processing in replaceText: " + (stopStreamTime - startStreamTime)); //$NON-NLS-1$
			}
			resumePostNotificationProcessing();
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.CharSequence#length()
	 */
	public int length() {

		return getLength();
	}
	
	public void makeReadOnly(int startOffset, int length) {
		makeReadOnly(startOffset, length, false, false);
	}

	public void makeReadOnly(int startOffset, int length, boolean canInsertBefore, boolean canInsertAfter) {	
		// doesn't make sense to have a readonly region of 0 length,
		// so we'll ignore those requests
		if (length <= 0)
			return;
		String affectedText = this.get(startOffset, length);
		// a document event for "read only" change ... must
		// be followed by "no change" structuredDocument event
		// fDocumentEvent = new DocumentEvent(this, startOffset, length,
		// affectedText);
		fireReadOnlyAboutToBeChanged();
		// if (containsReadOnly(startOffset, length)) {
		// adjustReadOnlyRegions(startOffset, length);
		// } else {
		// we can blindly add category, since no harm done if already
		// exists.
		addPositionCategory(READ_ONLY_REGIONS_CATEGORY);
		Position newPosition = new ReadOnlyPosition(startOffset, length, canInsertBefore);
		try {
			addPosition(READ_ONLY_REGIONS_CATEGORY, newPosition);
			// FIXME: need to change API to pass in requester, so this event
			// can be
			// created correctly, instead of using null.
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

	public IStructuredDocument newInstance() {
		IStructuredDocument newInstance = StructuredDocumentFactory.getNewStructuredDocumentInstance(getParser().newInstance());
		((BasicStructuredDocument) newInstance).setReParser(getReParser().newInstance());
		if (getDocumentPartitioner() instanceof StructuredTextPartitioner) {
			newInstance.setDocumentPartitioner(((StructuredTextPartitioner) getDocumentPartitioner()).newInstance());
			newInstance.getDocumentPartitioner().connect(newInstance);
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
	 * @see org.eclipse.wst.sse.core.internal.text.IRegionComparible#regionMatches(int,
	 *      int, java.lang.String)
	 */
	public boolean regionMatches(int offset, int length, String stringToCompare) {
		boolean result = false;
		ITextStore store = getStore();
		if (store instanceof IRegionComparible) {
			result = ((IRegionComparible) store).regionMatches(offset, length, stringToCompare);
		}
		else {
			result = get(offset, length).equals(stringToCompare);
		}
		return result;
	}

	public boolean regionMatchesIgnoreCase(int offset, int length, String stringToCompare) {
		boolean result = false;
		ITextStore store = getStore();
		if (store instanceof IRegionComparible) {
			result = ((IRegionComparible) store).regionMatchesIgnoreCase(offset, length, stringToCompare);
		}
		else {
			result = get(offset, length).equalsIgnoreCase(stringToCompare);
		}
		return result;
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

	protected void releaseLock() {
		// do nothing here in super class
	}

	public void removeDocumentAboutToChangeListener(IModelAboutToBeChangedListener listener) {
		synchronized (listenerLock) {

			if ((fStructuredDocumentAboutToChangeListeners != null) && (listener != null)) {
				// if its not in the listeners, we'll ignore the request
				if (Utilities.contains(fStructuredDocumentAboutToChangeListeners, listener)) {
					int oldSize = fStructuredDocumentAboutToChangeListeners.length;
					int newSize = oldSize - 1;
					Object[] newListeners = new Object[newSize];
					int index = 0;
					for (int i = 0; i < oldSize; i++) {
						if (fStructuredDocumentAboutToChangeListeners[i] == listener) { // ignore
						}
						else {
							// copy old to new if its not the one we are
							// removing
							newListeners[index++] = fStructuredDocumentAboutToChangeListeners[i];
						}
					}
					// now that we have a new array, let's switch it for the
					// old
					// one
					fStructuredDocumentAboutToChangeListeners = newListeners;
				}
			}
		}
	}

	/**
	 * removeModelChangedListener method comment.
	 */
	public void removeDocumentChangedListener(IStructuredDocumentListener listener) {
		synchronized (listenerLock) {

			if ((fStructuredDocumentChangedListeners != null) && (listener != null)) {
				// if its not in the listeners, we'll ignore the request
				if (Utilities.contains(fStructuredDocumentChangedListeners, listener)) {
					int oldSize = fStructuredDocumentChangedListeners.length;
					int newSize = oldSize - 1;
					Object[] newListeners = new Object[newSize];
					int index = 0;
					for (int i = 0; i < oldSize; i++) {
						if (fStructuredDocumentChangedListeners[i] == listener) { // ignore
						}
						else {
							// copy old to new if its not the one we are
							// removing
							newListeners[index++] = fStructuredDocumentChangedListeners[i];
						}
					}
					// now that we have a new array, let's switch it for the
					// old
					// one
					fStructuredDocumentChangedListeners = newListeners;
				}
			}
		}
	}

	public void removeDocumentChangingListener(IStructuredDocumentListener listener) {
		synchronized (listenerLock) {

			if ((fStructuredDocumentChangingListeners != null) && (listener != null)) {
				// if its not in the listeners, we'll ignore the request
				if (Utilities.contains(fStructuredDocumentChangingListeners, listener)) {
					int oldSize = fStructuredDocumentChangingListeners.length;
					int newSize = oldSize - 1;
					Object[] newListeners = new Object[newSize];
					int index = 0;
					for (int i = 0; i < oldSize; i++) {
						if (fStructuredDocumentChangingListeners[i] == listener) { // ignore
						}
						else {
							// copy old to new if its not the one we are
							// removing
							newListeners[index++] = fStructuredDocumentChangingListeners[i];
						}
					}
					// now that we have a new array, let's switch it for the
					// old
					// one
					fStructuredDocumentChangingListeners = newListeners;
				}
			}
		}
	}

	public void removeDocumentListener(IDocumentListener listener) {
		synchronized (listenerLock) {

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
							// copy old to new if its not the one we are
							// removing
							newListeners[index++] = fDocumentListeners[i];
						}
					}
					// now that we have a new array, let's switch it for the
					// old
					// one
					fDocumentListeners = newListeners;
				}
			}
		}
	}

	/*
	 * @see org.eclipse.jface.text.IDocument#removeDocumentPartitioningListener(org.eclipse.jface.text.IDocumentPartitioningListener)
	 */
	public void removeDocumentPartitioningListener(IDocumentPartitioningListener listener) {
		synchronized (listenerLock) {

			Assert.isNotNull(listener);
			if (fDocumentPartitioningListeners != null)
				fDocumentPartitioningListeners.remove(listener);
		}
	}

	/**
	 * Removes the given <code>Position</code> from the document's default
	 * position category. The default position category is to be defined by
	 * the implementers. If the position is not part of the document's default
	 * category nothing happens.
	 */
	public void removePosition(Position position) {
		getPositionManager().removePosition(position);
	}

	/**
	 * @see IDocument#removePosition
	 * @exception BadPositionCategoryException
	 *                If the category is not defined for the document
	 */
	public void removePosition(String category, Position position) throws BadPositionCategoryException {
		getPositionManager().removePosition(category, position);
	}

	/**
	 * @see IDocument#removePositionCategory
	 * @exception BadPositionCategoryException
	 *                If the category is not defined for the document
	 */
	public void removePositionCategory(String category) throws BadPositionCategoryException {
		getPositionManager().removePositionCategory(category);
	}

	/**
	 * @see IDocument#removePositionUpdater
	 */
	public void removePositionUpdater(IPositionUpdater updater) {
		getPositionManager().removePositionUpdater(updater);
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
		synchronized (listenerLock) {

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
	}

	/**
	 * This method is for INTERNAL USE ONLY and is NOT API.
	 * 
	 * Rebuilds the StructuredDocumentRegion chain from the existing text.
	 * FileBuffer support does not allow clients to know the document's
	 * location before the text contents are set.
	 * 
	 * @see set(String)
	 */
	public void reparse(Object requester) {
		// check if we're already making document-wide changes on this thread
		if (fStoppedCount > 0)
			return;
		
		stopPostNotificationProcessing();
		clearReadOnly();

		try {
			acquireLock();

			CharSequenceReader subSetTextStoreReader = new CharSequenceReader((CharSequence) getStore(), 0, getStore().getLength());
			resetParser(subSetTextStoreReader, 0);
			//
			setCachedDocumentRegion(getParser().getDocumentRegions());
			// when starting afresh, our cachedNode should be our firstNode,
			// so be sure to initialize the firstNode and lastNode
			initializeFirstAndLastDocumentRegion();
			StructuredDocumentRegionIterator.setParentDocument(getCachedDocumentRegion(), this);
		}
		finally {
			releaseLock();
		}

		resumePostNotificationProcessing();
	}

	/**
	 * @see IDocument#replace
	 * @exception BadLocationException
	 *                If position is not a valid range in the document
	 */
	public void replace(int offset, int length, String text) throws BadLocationException {
		if (Debug.displayWarnings) {
			System.out.println("Note: IStructuredDocument::replace(int, int, String) .... its better to use replaceText(source, string, int, int) API for structuredDocument updates"); //$NON-NLS-1$
		}
		replaceText(this, offset, length, text);
	}

	/**
	 * Replace the text with "newText" starting at position "start" for a
	 * length of "replaceLength".
	 * <p>
	 * 
	 * @param pos
	 *            start offset of text to replace None of the offsets include
	 *            delimiters of preceeding lines. Offset 0 is the first
	 *            character of the document.
	 * @param length
	 *            start offset of text to replace
	 * @param text
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
	public StructuredDocumentEvent replaceText(Object requester, int pos, int length, String text) {
		if (length == 0 && (text == null || text.length() == 0))
			return replaceText(requester, pos, length, text, getModificationStamp(), true);
		else
			return replaceText(requester, pos, length, text, getNextModificationStamp(), true);
	}

	public StructuredDocumentEvent replaceText(Object requester, int start, int replacementLength, String changes, boolean ignoreReadOnlySettings) {
		long modificationStamp;
		
		if (replacementLength == 0 && (changes == null || changes.length() == 0))
			modificationStamp = getModificationStamp();
		else
			modificationStamp = getNextModificationStamp();
		
		return replaceText(requester, start, replacementLength, changes, modificationStamp, ignoreReadOnlySettings);
	}
	
	private StructuredDocumentEvent replaceText(Object requester, int start, int replacementLength, String changes, long modificationStamp, boolean ignoreReadOnlySettings) {
		StructuredDocumentEvent event = internalReplaceText(requester, start, replacementLength, changes, modificationStamp, ignoreReadOnlySettings);
		return event;
	}

	void resetParser(int startOffset, int endOffset) {

		RegionParser parser = getParser();
		ITextStore textStore = getStore();
		if (textStore instanceof CharSequence) {
			CharSequenceReader subSetTextStoreReader = new CharSequenceReader((CharSequence) textStore, startOffset, endOffset - startOffset);
			parser.reset(subSetTextStoreReader, startOffset);
		}
		else {
			String newNodeText = get(startOffset, endOffset - startOffset);
			parser.reset(newNodeText, startOffset);

		}

	}

	void resetParser(Reader reader, int startOffset) {
		RegionParser parser = getParser();
		parser.reset(reader, startOffset);
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
	 * @deprecated in superclass in 3.0 - use a FindReplaceDocumentAdapter
	 *             directly
	 * @see IDocument#search
	 */
	public int search(int startPosition, String findString, boolean forwardSearch, boolean caseSensitive, boolean wholeWord) throws BadLocationException {
		// (dmw) I added this warning, to know if still being used. I'm not
		// sure it
		// works as expected any longer.
		// but the warning should be removed, once know.
		Logger.log(Logger.INFO, "WARNING: using unsupported deprecated method 'search'"); //$NON-NLS-1$
		int offset = -1;
		IRegion match = new FindReplaceDocumentAdapter(this).find(startPosition, findString, forwardSearch, caseSensitive, wholeWord, false);
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
	 * This may be marked public, but should be packaged protected, once
	 * refactoring is complete (in other words, not for client use).
	 */
	public void setCachedDocumentRegion(IStructuredDocumentRegion structuredRegion) {
		if (USE_LOCAL_THREAD) {
			fCurrentDocumentRegionCache.set(structuredRegion);
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
		setDocumentPartitioner(IDocumentExtension3.DEFAULT_PARTITIONING, partitioner);
	}


	public void setDocumentPartitioner(String partitioning, IDocumentPartitioner partitioner) {
		if (partitioner == null) {
			if (fDocumentPartitioners != null) {
				fDocumentPartitioners.remove(partitioning);
				if (fDocumentPartitioners.size() == 0)
					fDocumentPartitioners = null;
			}
		}
		else {
			if (fDocumentPartitioners == null)
				fDocumentPartitioners = new HashMap();
			fDocumentPartitioners.put(partitioning, partitioner);
		}
		DocumentPartitioningChangedEvent event = new DocumentPartitioningChangedEvent(this);
		event.setPartitionChange(partitioning, 0, getLength());
		fireDocumentPartitioningChanged(event);
	}

	public void setEncodingMemento(EncodingMemento encodingMemento) {
		this.encodingMemento = encodingMemento;
	}

	void setFirstDocumentRegion(IStructuredDocumentRegion region) {
		firstDocumentRegion = region;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocumentExtension4#setInitialLineDelimiter(java.lang.String)
	 */
	public void setInitialLineDelimiter(String lineDelimiter) {
		// make sure our preferred delimiter is
		// one of the legal ones
		if (Utilities.containsString(getLegalLineDelimiters(), lineDelimiter)) {
			fInitialLineDelimiter= lineDelimiter;
		}
		else {
			if (Logger.DEBUG_DOCUMENT)
				Logger.log(Logger.INFO, "Attempt to set linedelimiter to non-legal delimiter"); //$NON-NLS-1$ //$NON-NLS-2$
			fInitialLineDelimiter = Platform.getPreferencesService().getString(Platform.PI_RUNTIME, Platform.PREF_LINE_SEPARATOR, System.getProperty("line.separator"), new IScopeContext[] { new InstanceScope() });//$NON-NLS-1$
		}
	}

	void setLastDocumentRegion(IStructuredDocumentRegion region) {
		lastDocumentRegion = region;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument#setLineDelimiter(java.lang.String)
	 */
	public void setLineDelimiter(String delimiter) {
		setInitialLineDelimiter(delimiter);
	}

	/**
	 * Sets the document's line tracker. Must be called at the beginning of
	 * the constructor.
	 * 
	 * @param tracker
	 *            the document's line tracker
	 */
	private void setLineTracker(ILineTracker tracker) {
		Assert.isNotNull(tracker);
		fTracker = tracker;
	}

	public void setParser(RegionParser newParser) {
		internal_setParser(newParser);
	}

	/**
	 * @param positionManager
	 *            The positionManager to set.
	 */
	// TODO: make private is needed, else remove
	void setPositionManager(GenericPositionManager positionManager) {
		fPositionManager = positionManager;
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
	public StructuredDocumentEvent setText(Object requester, String theString) {
		StructuredDocumentEvent result = null;
		result = replaceText(requester, 0, getLength(), theString, getNextModificationStamp(), true);
		return result;
	}

	/**
	 * Sets the document's text store. Must be called at the beginning of the
	 * constructor.
	 * 
	 * @param store
	 *            the document's text store
	 */
	private void setTextStore(ITextStore store) {
		Assert.isNotNull(store);
		fStore = store;
	}

	public void setUndoManager(IStructuredTextUndoManager undoManager) {

		// if the undo manager has already been set, then
		// fail fast, since changing the undo manager will lead
		// to unusual results (or at least loss of undo stack).
		if (fUndoManager != null && fUndoManager != undoManager) {
			throw new IllegalArgumentException("can not change undo manager once its been set"); //$NON-NLS-1$
		}
		else {
			fUndoManager = undoManager;
		}
	}


	/*
	 * {@inheritDoc}
	 */
	public void startSequentialRewrite(boolean normalized) {
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
	 * {@inheritDoc}
	 */
	public void stopSequentialRewrite() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.CharSequence#subSequence(int, int)
	 */
	public CharSequence subSequence(int arg0, int arg1) {
		return get(arg0, arg1);
	}

	/**
	 * @param result
	 */
	private void updateDeletedFields(StructuredDocumentRegionsReplacedEvent event) {
		IStructuredDocumentRegionList oldRegions = event.getOldStructuredDocumentRegions();
		for (int i = 0; i < oldRegions.getLength(); i++) {
			IStructuredDocumentRegion structuredDocumentRegion = oldRegions.item(i);
			structuredDocumentRegion.setDeleted(true);
		}

	}

	/**
	 * Called by re-parser. Note: this method may be "public" but should only
	 * be called by re-parsers in the right circumstances.
	 */
	public void updateDocumentData(int start, int lengthToReplace, String changes) {
		stopPostNotificationProcessing();
		getStore().replace(start, lengthToReplace, changes);
		try {
			getTracker().replace(start, lengthToReplace, changes);
		}

		catch (BadLocationException e) {
			// should be impossible here, but will log for now
			Logger.logException(e);
		}
		if (fPositionManager != null) {
			fPositionManager.updatePositions(new DocumentEvent(this, start, lengthToReplace, changes));
		}
		fModificationStamp++;
		fNextModificationStamp= Math.max(fModificationStamp, fNextModificationStamp);
		resumePostNotificationProcessing();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.internal.provisional.document.IEncodedDocument#getPreferredLineDelimiter()
	 */
	public String getPreferredLineDelimiter() {
		return getDefaultLineDelimiter();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.internal.provisional.document.IEncodedDocument#setPreferredLineDelimiter(java.lang.String)
	 */
	public void setPreferredLineDelimiter(String probableLineDelimiter) {
		setInitialLineDelimiter(probableLineDelimiter);

	}


	/**
	 * Class which implements the rewritable session for the SSE.
	 * 
	 */
	static class StructuredDocumentRewriteSession extends DocumentRewriteSession {

		/**
		 * Creates a new session.
		 * 
		 * @param sessionType
		 *            the type of this session
		 */
		protected StructuredDocumentRewriteSession(DocumentRewriteSessionType sessionType) {
			super(sessionType);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocumentExtension4#startRewriteSession(org.eclipse.jface.text.DocumentRewriteSessionType)
	 */
	public DocumentRewriteSession startRewriteSession(DocumentRewriteSessionType sessionType) throws IllegalStateException {
		// delegate to sub-class, so UI threading is handled correctly
		return internalStartRewriteSession(sessionType);
	}

	/**
	 * NOT-API. Final protected so clients may call this method if needed, but
	 * cannot override.
	 * 
	 * @param sessionType
	 * @return
	 * @throws IllegalStateException
	 */
	final protected DocumentRewriteSession internalStartRewriteSession(DocumentRewriteSessionType sessionType) throws IllegalStateException {
		if (getActiveRewriteSession() != null)
			throw new IllegalStateException("already in a rewrite session");

		DocumentRewriteSession session = new StructuredDocumentRewriteSession(sessionType);
		DocumentRewriteSessionEvent event = new DocumentRewriteSessionEvent(this, session, DocumentRewriteSessionEvent.SESSION_START);
		fireDocumentRewriteSessionEvent(event);

		ILineTracker tracker = getTracker();
		if (tracker instanceof ILineTrackerExtension) {
			ILineTrackerExtension extension = (ILineTrackerExtension) tracker;
			extension.startRewriteSession(session);
		}

		startRewriteSessionOnPartitioners(session);

		if (DocumentRewriteSessionType.SEQUENTIAL == sessionType)
			startSequentialRewrite(false);
		else if (DocumentRewriteSessionType.STRICTLY_SEQUENTIAL == sessionType)
			startSequentialRewrite(true);

		fActiveRewriteSession = session;
		return session;
	}

	/**
	 * Starts the given rewrite session.
	 *
	 * @param session the rewrite session
	 * @since 2.0
	 */
	final void startRewriteSessionOnPartitioners(DocumentRewriteSession session) {
		if (fDocumentPartitioners != null) {
			Iterator e= fDocumentPartitioners.values().iterator();
			while (e.hasNext()) {
				Object partitioner= e.next();
				if (partitioner instanceof IDocumentPartitionerExtension3) {
					IDocumentPartitionerExtension3 extension= (IDocumentPartitionerExtension3) partitioner;
					extension.startRewriteSession(session);
				}
			}
		}
	}


	public void stopRewriteSession(DocumentRewriteSession session) {
		// delegate to sub-class, so UI threading is handled correctly
		internalStopRewriteSession(session);
	}

	/**
	 * NOT-API. Final protected so clients may call this method if needed, but
	 * cannot override.
	 * 
	 * @param session
	 */
	final protected void internalStopRewriteSession(DocumentRewriteSession session) {
		if (fActiveRewriteSession == session) {
			DocumentRewriteSessionType sessionType = session.getSessionType();
			if (DocumentRewriteSessionType.SEQUENTIAL == sessionType || DocumentRewriteSessionType.STRICTLY_SEQUENTIAL == sessionType)
				stopSequentialRewrite();

			stopRewriteSessionOnPartitioners(session);

			ILineTracker tracker = getTracker();
			if (tracker instanceof ILineTrackerExtension) {
				ILineTrackerExtension extension = (ILineTrackerExtension) tracker;
				extension.stopRewriteSession(session, get());
			}

			fActiveRewriteSession = null;
			DocumentRewriteSessionEvent event = new DocumentRewriteSessionEvent(this, session, DocumentRewriteSessionEvent.SESSION_STOP);
			fireDocumentRewriteSessionEvent(event);
		}
	}

	/**
	 * Stops the given rewrite session.
	 *
	 * @param session the rewrite session
	 * @since 2.0
	 */
	final void stopRewriteSessionOnPartitioners(DocumentRewriteSession session) {
		if (fDocumentPartitioners != null) {
			DocumentPartitioningChangedEvent event= new DocumentPartitioningChangedEvent(this);
			Iterator e= fDocumentPartitioners.keySet().iterator();
			while (e.hasNext()) {
				String partitioning= (String) e.next();
				IDocumentPartitioner partitioner= (IDocumentPartitioner) fDocumentPartitioners.get(partitioning);
				if (partitioner instanceof IDocumentPartitionerExtension3) {
					IDocumentPartitionerExtension3 extension= (IDocumentPartitionerExtension3) partitioner;
					extension.stopRewriteSession(session);
					event.setPartitionChange(partitioning, 0, getLength());
				}
			}
			if (!event.isEmpty())
				fireDocumentPartitioningChanged(event);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocumentExtension4#getActiveRewriteSession()
	 */
	public DocumentRewriteSession getActiveRewriteSession() {
		return fActiveRewriteSession;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocumentExtension4#addDocumentRewriteSessionListener(org.eclipse.jface.text.IDocumentRewriteSessionListener)
	 */
	public void addDocumentRewriteSessionListener(IDocumentRewriteSessionListener listener) {
		synchronized (listenerLock) {
			Assert.isNotNull(listener);
			if (fDocumentRewriteSessionListeners == null) {
				fDocumentRewriteSessionListeners = new ArrayList(1);
			}
			if (!fDocumentRewriteSessionListeners.contains(listener))
				fDocumentRewriteSessionListeners.add(listener);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocumentExtension4#removeDocumentRewriteSessionListener(org.eclipse.jface.text.IDocumentRewriteSessionListener)
	 */
	public void removeDocumentRewriteSessionListener(IDocumentRewriteSessionListener listener) {
		synchronized (listenerLock) {

			Assert.isNotNull(listener);
			if (fDocumentRewriteSessionListeners != null)
				fDocumentRewriteSessionListeners.remove(listener);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocumentExtension4#replace(int, int,
	 *      java.lang.String, long)
	 */
	public void replace(int offset, int length, String text, long modificationStamp) throws BadLocationException {
		replaceText(this, offset, length, text, modificationStamp, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocumentExtension4#set(java.lang.String,
	 *      long)
	 */
	public void set(String text, long modificationStamp) {
		// bug 151069 - overwrite read only regions when setting entire document
		 replaceText(null, 0, getLength(), text, modificationStamp, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.IDocumentExtension4#getModificationStamp()
	 */
	public long getModificationStamp() {
		return fModificationStamp;
	}
	
	private long getNextModificationStamp() {
		if (fNextModificationStamp == Long.MAX_VALUE || fNextModificationStamp == IDocumentExtension4.UNKNOWN_MODIFICATION_STAMP)
			fNextModificationStamp= 0;
		else
			fNextModificationStamp= fNextModificationStamp + 1;

		return fNextModificationStamp;
	}

	/**
	 * Fires an event, as specified, to the associated listeners.
	 * 
	 * @param event
	 *            The event to fire, either a start or stop event.
	 */
	private void fireDocumentRewriteSessionEvent(final DocumentRewriteSessionEvent event) {
		if (fDocumentRewriteSessionListeners == null || fDocumentRewriteSessionListeners.size() == 0)
			return;

		Object[] listeners = fDocumentRewriteSessionListeners.toArray();
		for (int i = 0; i < listeners.length; i++) {
			final IDocumentRewriteSessionListener l = (IDocumentRewriteSessionListener) listeners[i];
			SafeRunner.run(new ISafeRunnable() {
				public void run() throws Exception {
					l.documentRewriteSessionChanged(event);
				}
				public void handleException(Throwable exception) {
					// logged for us
				}
			});
		}
	}
}
