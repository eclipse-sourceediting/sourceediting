/*******************************************************************************
 * Copyright (c) 2015, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.xml.core.internal.document.DOMModelImpl
 *                                           modified in order to process JSON Objects.
 *******************************************************************************/
package org.eclipse.wst.json.core.internal.document;

import org.eclipse.wst.json.core.contenttype.ContentTypeIdForJSON;
import org.eclipse.wst.json.core.document.IJSONDocument;
import org.eclipse.wst.json.core.document.IJSONModel;
import org.eclipse.wst.json.core.document.IJSONNode;
import org.eclipse.wst.json.core.document.IJSONObject;
import org.eclipse.wst.json.core.document.IJSONPair;
import org.eclipse.wst.json.core.document.IJSONValue;
import org.eclipse.wst.json.core.internal.Logger;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.model.AbstractStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.events.IStructuredDocumentListener;
import org.eclipse.wst.sse.core.internal.provisional.events.NewDocumentEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.NoChangeEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.RegionChangedEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.RegionsReplacedEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentRegionsReplacedEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegionList;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.w3c.dom.Document;

/**
 * SSE {@link IStructuredDocument} implementation for JSON.
 */
public class JSONModelImpl extends AbstractStructuredModel implements
		IStructuredDocumentListener, IJSONModel {

	private static String TRACE_PARSER_MANAGEMENT_EXCEPTION = "parserManagement"; //$NON-NLS-1$
	private Object active = null;
	private JSONDocumentImpl document = null;
	private ISourceGenerator generator = null;
	private JSONModelNotifier notifier = null;
	private JSONModelParser parser = null;
	private boolean refresh = false;
	private JSONModelUpdater updater = null;

	/**
	 * JSONModelImpl constructor
	 */
	public JSONModelImpl() {
		super();
		this.document = (JSONDocumentImpl) internalCreateDocument();
	}

	/**
	 * This API allows clients to declare that they are about to make a "large"
	 * change to the model. This change might be in terms of content or it might
	 * be in terms of the model id or base location.
	 * 
	 * Note that in the case of embedded calls, notification to listeners is
	 * sent only once.
	 * 
	 * Note that the client who is making these changes has the responsibility
	 * to restore the models state once finished with the changes. See
	 * getMemento and restoreState.
	 * 
	 * The method isModelStateChanging can be used by a client to determine if
	 * the model is already in a change sequence.
	 */
	public void aboutToChangeModel() {
		super.aboutToChangeModel();
		// technically, no need to call beginChanging so often,
		// since aboutToChangeModel can be nested.
		// but will leave as is for this release.
		// see modelChanged, and be sure stays coordinated there.
		getModelNotifier().beginChanging();
	}

	public void aboutToReinitializeModel() {
		JSONModelNotifier notifier = getModelNotifier();
		notifier.cancelPending();
		super.aboutToReinitializeModel();
	}

	protected void pairReplaced(IJSONObject element, IJSONPair newAttr,
			IJSONPair oldAttr) {
		if (element == null)
			return;
		if (getActiveParser() == null) {
			JSONModelUpdater updater = getModelUpdater();
			setActive(updater);
			updater.initialize();
			updater.replaceAttr(element, newAttr, oldAttr);
			setActive(null);
		}
		getModelNotifier().pairReplaced(element, newAttr, oldAttr);
	}

	/**
	 * This API allows a client controlled way of notifying all ModelEvent
	 * listners that the model has been changed. This method is a matched pair
	 * to aboutToChangeModel, and must be called after aboutToChangeModel ... or
	 * some listeners could be left waiting indefinitely for the changed event.
	 * So, its suggested that changedModel always be in a finally clause.
	 * Likewise, a client should never call changedModel without calling
	 * aboutToChangeModel first.
	 * 
	 * In the case of embedded calls, the notification is just sent once.
	 * 
	 */
	public void changedModel() {
		// NOTE: the order of 'changedModel' and 'endChanging' is significant.
		// By calling changedModel first, this basically decrements the
		// "isChanging" counter
		// in super class and when zero all listeners to model state events
		// will be notified
		// that the model has been changed. 'endChanging' will notify all
		// deferred adapters.
		// So, the significance of order is that adapters (and methods they
		// call)
		// can count on the state of model "isChanging" to be accurate.
		// But, remember, that this means the "modelChanged" event can be
		// received before all
		// adapters have finished their processing.
		// NOTE NOTE: The above note is obsolete in fact (though still states
		// issue correctly).
		// Due to popular demand, the order of these calls were reversed and
		// behavior
		// changed on 07/22/2004.
		//
		// see also
		// https://w3.opensource.ibm.com/bugzilla/show_bug.cgi?id=4302
		// for motivation for this 'on verge of' call.
		// this could be improved in future if notifier also used counting
		// flag to avoid nested calls. If/when changed be sure to check if
		// aboutToChangeModel needs any changes too.
		if (isModelChangeStateOnVergeOfEnding()) {
			// end lock before noticiation loop, since directly or indirectly
			// we may be "called from foriegn code" during notification.
			endLock();
			// we null out here to avoid spurious"warning" message while debug
			// tracing is enabled
			fLockObject = null;
			// the notifier is what controls adaper notification, which
			// should be sent out before the 'modelChanged' event.
			getModelNotifier().endChanging();
		}
		// changedModel handles 'nesting', so only one event sent out
		// when mulitple calls to 'aboutToChange/Changed'.
		super.changedModel();
		handleRefresh();
	}

	/**
	 * childReplaced method
	 * 
	 * @param parentNode
	 *            org.w3c.dom.Node
	 * @param newChild
	 *            org.w3c.dom.Node
	 * @param oldChild
	 *            org.w3c.dom.Node
	 */
	protected void childReplaced(IJSONNode parentNode, IJSONNode newChild,
			IJSONNode oldChild) {
		if (parentNode == null)
			return;
		if (getActiveParser() == null) {
			JSONModelUpdater updater = getModelUpdater();
			setActive(updater);
			updater.initialize();
			updater.replaceChild(parentNode, newChild, oldChild);
			setActive(null);
		}
		getModelNotifier().childReplaced(parentNode, newChild, oldChild);
	}

	/**
	 */
	// protected void documentTypeChanged() {
	// if (this.refresh)
	// return;
	// // unlike 'resfresh', 'reinitialize' finishes loop
	// // and flushes remaining notification que before
	// // actually reinitializing.
	// // ISSUE: should reinit be used instead of handlerefresh?
	// // this.setReinitializeNeeded(true);
	// if (this.active != null || getModelNotifier().isChanging())
	// return; // defer
	// handleRefresh();
	// }

	// protected void editableChanged(Node node) {
	// if (node != null) {
	// getModelNotifier().editableChanged(node);
	// }
	// }

	/**
	 */
	// protected void endTagChanged(IJSONObject element) {
	// if (element == null)
	// return;
	// if (getActiveParser() == null) {
	// JSONModelUpdater updater = getModelUpdater();
	// setActive(updater);
	// updater.initialize();
	// // updater.changeEndTag(element);
	// setActive(null);
	// }
	// getModelNotifier().endTagChanged(element);
	// }

	/**
	 */
	private JSONModelParser getActiveParser() {
		if (this.parser == null)
			return null;
		if (this.parser != this.active)
			return null;
		return this.parser;
	}

	/**
	 */
	private JSONModelUpdater getActiveUpdater() {
		if (this.updater == null)
			return null;
		if (this.updater != this.active)
			return null;
		return this.updater;
	}

	@Override
	public Object getAdapter(Class adapter) {
		if (Document.class.equals(adapter))
			return getDocument();
		return super.getAdapter(adapter);
	}

	@Override
	public IJSONDocument getDocument() {
		return this.document;
	}

	public ISourceGenerator getGenerator() {
		if (this.generator == null) {
			this.generator = JSONGeneratorImpl.getInstance();
		}
		return this.generator;
	}

	@Override
	public IndexedRegion getIndexedRegion(int offset) {
		if (this.document == null)
			return null;
		// search in document children
		IJSONNode parent = null;
		int length = this.document.getEndOffset();
		if (offset * 2 < length) {
			// search from the first
			IJSONNode child = (IJSONNode) this.document.getFirstChild();
			while (child != null) {
				if (child.getEndOffset() <= offset) {
					child = (IJSONNode) child.getNextSibling();
					continue;
				}
				if (child.getStartOffset() > offset) {
					break;
				}
				IStructuredDocumentRegion startStructuredDocumentRegion = child
						.getStartStructuredDocumentRegion();
				if (startStructuredDocumentRegion != null) {
					if (startStructuredDocumentRegion.getEnd() > offset)
						return child;
				}
				IStructuredDocumentRegion endStructuredDocumentRegion = child
						.getEndStructuredDocumentRegion();
				if (endStructuredDocumentRegion != null) {
					if (endStructuredDocumentRegion.getStart() <= offset)
						return child;
				}
				// dig more
				parent = child;
				if (parent != null
						&& parent.getNodeType() == IJSONNode.PAIR_NODE) {
					IJSONPair pair = (IJSONPair) parent;
					child = pair.getValue();
				} else {
					child = (IJSONNode) parent.getFirstChild();
				}
			}
		} else {
			// search from the last
			IJSONNode child = (IJSONNode) this.document.getLastChild();
			while (child != null) {
				if (child.getStartOffset() > offset) {
					child = (IJSONNode) child.getPreviousSibling();
					continue;
				}
				if (child.getEndOffset() <= offset) {
					break;
				}
				IStructuredDocumentRegion startStructuredDocumentRegion = child
						.getStartStructuredDocumentRegion();
				if (startStructuredDocumentRegion != null) {
					if (startStructuredDocumentRegion.getEnd() > offset)
						return child;
				}
				IStructuredDocumentRegion endStructuredDocumentRegion = child
						.getEndStructuredDocumentRegion();
				if (endStructuredDocumentRegion != null) {
					if (endStructuredDocumentRegion.getStart() <= offset)
						return child;
				}
				// dig more
				parent = child;
				if (parent != null
						&& parent.getNodeType() == IJSONNode.PAIR_NODE) {
					IJSONPair pair = (IJSONPair) parent;
					child = pair.getValue();
				} else {
					child = (IJSONNode) parent.getLastChild();
				}
			}
		}
		return parent != null ? parent : document.getFirstChild();
	}

	/**
	 */
	public JSONModelNotifier getModelNotifier() {
		if (this.notifier == null) {
			this.notifier = new JSONModelNotifierImpl();
		}
		return this.notifier;
	}

	/**
	 */
	private JSONModelParser getModelParser() {
		if (this.parser == null) {
			this.parser = createModelParser();
		}
		return this.parser;
	}

	protected JSONModelParser createModelParser() {
		return new JSONModelParser(this);
	}

	/**
	 */
	private JSONModelUpdater getModelUpdater() {
		if (this.updater == null) {
			this.updater = createModelUpdater();
		}
		return this.updater;
	}

	protected JSONModelUpdater createModelUpdater() {
		return new JSONModelUpdater(this);
	}

	/**
	 */
	private void handleRefresh() {
		if (!this.refresh)
			return;
		JSONModelNotifier notifier = getModelNotifier();
		boolean isChanging = notifier.isChanging();
		if (!isChanging)
			notifier.beginChanging(true);
		JSONModelParser parser = getModelParser();
		setActive(parser);
		this.document.removeChildNodes();
		try {
			this.refresh = false;
			parser.replaceStructuredDocumentRegions(getStructuredDocument()
					.getRegionList(), null);
		} catch (Exception ex) {
			Logger.logException(ex);
		} finally {
			setActive(null);
			if (!isChanging)
				notifier.endChanging();
		}
	}

	protected IJSONDocument internalCreateDocument() {
		JSONDocumentImpl document = new JSONDocumentImpl();
		document.setModel(this);
		return document;
	}

	boolean isReparsing() {
		return (active != null);
	}

	@Override
	public void newModel(NewDocumentEvent structuredDocumentEvent) {
		if (structuredDocumentEvent == null)
			return;
		IStructuredDocument structuredDocument = structuredDocumentEvent
				.getStructuredDocument();
		if (structuredDocument == null)
			return;
		// this should not happen, but for the case
		if (fStructuredDocument != null
				&& fStructuredDocument != structuredDocument)
			setStructuredDocument(structuredDocument);

		internalSetNewDocument(structuredDocument);
	}

	private void internalSetNewDocument(IStructuredDocument structuredDocument) {
		if (structuredDocument == null)
			return;
		IStructuredDocumentRegionList flatNodes = structuredDocument
				.getRegionList();
		if ((flatNodes == null) || (flatNodes.getLength() == 0)) {
			return;
		}
		if (this.document == null)
			return; // being constructed

		JSONModelUpdater updater = getActiveUpdater();
		if (updater != null) { // being updated
			try {
				updater.replaceStructuredDocumentRegions(flatNodes, null);
			} catch (Exception ex) {
				Logger.logException(ex);
				this.refresh = true;
				handleRefresh();
			} finally {
				setActive(null);
			}
			// // for new model, we might need to
			// // re-init, e.g. if someone calls setText
			// // on an existing model
			// checkForReinit();
			return;
		}
		JSONModelNotifier notifier = getModelNotifier();
		boolean isChanging = notifier.isChanging();
		// call even if changing to notify doing new model
		getModelNotifier().beginChanging(true);
		JSONModelParser parser = getModelParser();
		setActive(parser);
		this.document.removeChildNodes();
		try {
			parser.replaceStructuredDocumentRegions(flatNodes, null);
		} catch (Exception ex) {
			Logger.logException(ex);
			// meaningless to refresh, because the result might be the same
		} finally {
			setActive(null);
			if (!isChanging) {
				getModelNotifier().endChanging();
			}
			// ignore refresh
			this.refresh = false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.internal.provisional.events.
	 * IStructuredDocumentListener
	 * #noChange(org.eclipse.wst.sse.core.internal.provisional
	 * .events.NoChangeEvent)
	 */
	@Override
	public void noChange(NoChangeEvent event) {
		JSONModelUpdater updater = getActiveUpdater();
		if (updater != null) { // being updated
			// cleanup updater staffs
			try {
				updater.replaceStructuredDocumentRegions(null, null);
			} catch (Exception ex) {
				Logger.logException(ex);
				this.refresh = true;
				handleRefresh();
			} finally {
				setActive(null);
			}
			// I guess no chanage means the model could not need re-init
			// checkForReinit();
			return;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.internal.provisional.events.
	 * IStructuredDocumentListener
	 * #nodesReplaced(org.eclipse.wst.sse.core.internal
	 * .provisional.events.StructuredDocumentRegionsReplacedEvent)
	 */
	@Override
	public void nodesReplaced(StructuredDocumentRegionsReplacedEvent event) {
		if (event == null)
			return;
		IStructuredDocumentRegionList oldStructuredDocumentRegions = event
				.getOldStructuredDocumentRegions();
		IStructuredDocumentRegionList newStructuredDocumentRegions = event
				.getNewStructuredDocumentRegions();
		JSONModelUpdater updater = getActiveUpdater();
		if (updater != null) { // being updated
			try {
				updater.replaceStructuredDocumentRegions(
						newStructuredDocumentRegions,
						oldStructuredDocumentRegions);
			} catch (Exception ex) {
				Logger.logException(ex);
				this.refresh = true;
				handleRefresh();
			} finally {
				setActive(null);
			}
			// checkForReinit();
			return;
		}
		JSONModelNotifier notifier = getModelNotifier();
		boolean isChanging = notifier.isChanging();
		if (!isChanging)
			notifier.beginChanging();
		JSONModelParser parser = getModelParser();
		setActive(parser);
		try {
			/* workaround for https://bugs.eclipse.org/bugs/show_bug.cgi?id=486860 */
//			parser.replaceStructuredDocumentRegions(
//					newStructuredDocumentRegions, oldStructuredDocumentRegions);
			this.refresh = true;
			handleRefresh();
		} catch (Exception ex) {
			if (ex.getClass().equals(
					StructuredDocumentRegionManagementException.class)) {
				Logger.traceException(TRACE_PARSER_MANAGEMENT_EXCEPTION, ex);
			} else {
				Logger.logException(ex);
			}
			this.refresh = true;
			handleRefresh();
		} finally {
			setActive(null);
			if (!isChanging) {
				notifier.endChanging();
				handleRefresh();
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.internal.provisional.events.
	 * IStructuredDocumentListener
	 * #regionChanged(org.eclipse.wst.sse.core.internal
	 * .provisional.events.RegionChangedEvent)
	 */
	@Override
	public void regionChanged(RegionChangedEvent event) {
		if (event == null)
			return;
		IStructuredDocumentRegion flatNode = event
				.getStructuredDocumentRegion();
		if (flatNode == null)
			return;
		ITextRegion region = event.getRegion();
		if (region == null)
			return;
		JSONModelUpdater updater = getActiveUpdater();
		if (updater != null) { // being updated
			try {
				updater.changeRegion(event, flatNode, region);
			} catch (Exception ex) {
				Logger.logException(ex);
				this.refresh = true;
				handleRefresh();
			} finally {
				setActive(null);
			}
			// checkForReinit();
			return;
		}
		JSONModelNotifier notifier = getModelNotifier();
		boolean isChanging = notifier.isChanging();
		if (!isChanging)
			notifier.beginChanging();
		JSONModelParser parser = getModelParser();
		setActive(parser);
		try {
			/* workaround for https://bugs.eclipse.org/bugs/show_bug.cgi?id=486860 */
//			parser.changeRegion(event, flatNode, region);
			this.refresh = true;
			handleRefresh();
		} catch (Exception ex) {
			Logger.logException(ex);
			this.refresh = true;
			handleRefresh();
		} finally {
			setActive(null);
			if (!isChanging) {
				notifier.endChanging();
				handleRefresh();
			}
		}
		// checkForReinit();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.internal.provisional.events.
	 * IStructuredDocumentListener
	 * #regionsReplaced(org.eclipse.wst.sse.core.internal
	 * .provisional.events.RegionsReplacedEvent)
	 */
	@Override
	public void regionsReplaced(RegionsReplacedEvent event) {
		if (event == null)
			return;
		IStructuredDocumentRegion flatNode = event
				.getStructuredDocumentRegion();
		if (flatNode == null)
			return;
		ITextRegionList oldRegions = event.getOldRegions();
		ITextRegionList newRegions = event.getNewRegions();
		if (oldRegions == null && newRegions == null)
			return;
		JSONModelUpdater updater = getActiveUpdater();
		if (updater != null) { // being updated
			try {
				updater.replaceRegions(flatNode, newRegions, oldRegions);
			} catch (Exception ex) {
				Logger.logException(ex);
				this.refresh = true;
				handleRefresh();
			} finally {
				setActive(null);
			}
			// checkForReinit();
			return;
		}
		JSONModelNotifier notifier = getModelNotifier();
		boolean isChanging = notifier.isChanging();
		if (!isChanging)
			notifier.beginChanging();
		JSONModelParser parser = getModelParser();
		setActive(parser);
		try {
			/* workaround for https://bugs.eclipse.org/bugs/show_bug.cgi?id=486860 */
//			parser.replaceRegions(flatNode, newRegions, oldRegions);
			this.refresh = true;
			handleRefresh();
		} catch (Exception ex) {
			Logger.logException(ex);
			this.refresh = true;
			handleRefresh();
		} finally {
			setActive(null);
			if (!isChanging) {
				notifier.endChanging();
				handleRefresh();
			}
		}
		// checkForReinit();
	}

	/**
	 */
	public void releaseFromEdit() {
		if (!isShared()) {
			// this.document.releaseStyleSheets();
			// this.document.releaseDocumentType();
		}
		super.releaseFromEdit();
	}

	/**
	 */
	public void releaseFromRead() {
		if (!isShared()) {
			// this.document.releaseStyleSheets();
			// this.document.releaseDocumentType();
		}
		super.releaseFromRead();
	}

	/**
	 */
	private void setActive(Object active) {
		this.active = active;
		// side effect
		// when ever becomes active, besure tagNameCache is cleared
		// (and not used)
		// if (active == null) {
		// document.activateTagNameCache(true);
		// } else {
		// document.activateTagNameCache(false);
		// }
	}

	/**
	 */
	// public void setGenerator(ISourceGenerator generator) {
	// this.generator = generator;
	// }

	/**
	 */
	public void setModelNotifier(JSONModelNotifier notifier) {
		this.notifier = notifier;
	}

	/**
	 */
	public void setModelParser(JSONModelParser parser) {
		this.parser = parser;
	}

	/**
	 */
	public void setModelUpdater(JSONModelUpdater updater) {
		this.updater = updater;
	}

	/**
	 * setStructuredDocument method
	 * 
	 * @param structuredDocument
	 */
	public void setStructuredDocument(IStructuredDocument structuredDocument) {
		IStructuredDocument oldStructuredDocument = super
				.getStructuredDocument();
		if (structuredDocument == oldStructuredDocument)
			return; // nothing to do
		if (oldStructuredDocument != null)
			oldStructuredDocument.removeDocumentChangingListener(this);
		super.setStructuredDocument(structuredDocument);
		if (structuredDocument != null) {
			internalSetNewDocument(structuredDocument);
			structuredDocument.addDocumentChangingListener(this);
		}
	}

	/**
	 */
	protected void startTagChanged(IJSONObject element) {
		if (element == null)
			return;
		if (getActiveParser() == null) {
			JSONModelUpdater updater = getModelUpdater();
			setActive(updater);
			updater.initialize();
			updater.changeStartTag(element);
			setActive(null);
		}
		getModelNotifier().startTagChanged(element);
	}

	protected void valueChanged(IJSONNode node) {
		if (node == null)
			return;
		if (getActiveParser() == null) {
			JSONModelUpdater updater = getModelUpdater();
			setActive(updater);
			updater.initialize();
			updater.changeValue(node);
			setActive(null);
		}
		getModelNotifier().valueChanged(node);
	}

}
