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
package org.eclipse.wst.xml.ui.reconcile;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.reconciler.IReconcileResult;
import org.eclipse.wst.common.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.common.contentmodel.CMDocument;
import org.eclipse.wst.common.contentmodel.CMElementDeclaration;
import org.eclipse.wst.common.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.common.contentmodel.CMNode;
import org.eclipse.wst.common.contentmodel.modelquery.CMDocumentManager;
import org.eclipse.wst.common.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.common.contentmodel.util.CMDocumentCache;
import org.eclipse.wst.common.contentmodel.util.CMDocumentCacheListener;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.ui.IReleasable;
import org.eclipse.wst.sse.ui.StructuredTextReconciler;
import org.eclipse.wst.sse.ui.internal.reconcile.IReconcileAnnotationKey;
import org.eclipse.wst.sse.ui.internal.reconcile.TemporaryAnnotation;
import org.eclipse.wst.sse.ui.nls.ResourceHandler;
import org.eclipse.wst.xml.core.document.XMLAttr;
import org.eclipse.wst.xml.core.document.XMLElement;
import org.eclipse.wst.xml.core.document.XMLNode;
import org.eclipse.wst.xml.core.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.core.parser.XMLRegionContext;
import org.eclipse.wst.xml.ui.internal.Logger;
import org.eclipse.wst.xml.ui.internal.correction.ProblemIDsXML;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


public class ReconcileStepAdapterForXML extends AbstractReconcileStepAdapter implements CMDocumentCacheListener, IReleasable {

	/**
	 * Record of notification sent to this adapter. Will be queued up so
	 * they're not dealt with until reconciling is actually called from the
	 * reconciler thread.
	 */
	public class NotificationEvent {

		public Object changedFeature;

		public int eventType;

		public Object newValue;

		public INodeNotifier notifier;

		public Object oldValue;

		public int pos;

		public NotificationEvent(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {

			this.notifier = notifier;
			this.eventType = eventType;
			this.changedFeature = changedFeature;
			this.oldValue = oldValue;
			this.newValue = newValue;
			this.pos = pos;
		}

		// used (to see if notifications vector "contains()")
		// so we don't queue up "duplicate" events,
		// (indicates same eventType, notifier, and changedFeature)
		public boolean equals(Object o) {

			boolean result = false;
			if (o instanceof NotificationEvent) {
				NotificationEvent e2 = (NotificationEvent) o;
				if (this.notifier == null || e2.notifier == null || this.changedFeature == null || e2.changedFeature == null) {
					result = false;
				}
				result = (this.eventType == e2.eventType && this.notifier == e2.notifier && this.changedFeature == e2.changedFeature);
			}
			return result;
		}
	}

	protected boolean fCaseSensitive = true;
	protected CMDocumentCache fCMDocumentCache;
	protected DocumentType fDocumentTypeForRefresh;

	protected boolean fNeedsRefreshAll = false;

	// required for thread safety
	protected List fNotifications = new Vector();

	// these are used in conjunction w/ cacheUpdated() notification
	// in order to refresh the whole document
	protected IProgressMonitor fProgressMonitorForRefresh;

	// counter used for repeated reconcile opreations
	// to yield the thread control to the next thread
	// to improve workbench performance
	protected short fReconcileCount = 0;

	// will not attempt to validate attribute names starting with the
	// following:
	protected String[] ignoreAttributeNamesStartingWith = new String[]{"xmlns", "xsi:", "xml:"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	// changing these elements may have an impact on the current content model
	// (which suggests to mark everything dirty)
	protected String[] mayImpactContentModel = new String[]{"DOCTYPE", "xmlns", "xsi", "xmlns:xsi", "xmlns:xsl", "xsi:schemaLocation", "taglib"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
	protected String SEVERITY_MISSING_REQUIRED_ATTR = TemporaryAnnotation.ANNOT_WARNING;

	// severities for the problems discoverable by this reconciler; possibly
	// user configurable later
	protected String SEVERITY_STRUCTURE = TemporaryAnnotation.ANNOT_ERROR;
	protected String SEVERITY_UNKNOWN_ATTR = TemporaryAnnotation.ANNOT_ERROR;
	protected String SEVERITY_UNKNOWN_ELEMENT = TemporaryAnnotation.ANNOT_ERROR;

	public ReconcileStepAdapterForXML() {

		super();
	}

	public void cacheCleared(CMDocumentCache arg0) {
		// do nothing
	}

	public void cacheUpdated(CMDocumentCache arg0, String arg1, int arg2, int arg3, CMDocument arg4) {

		// revalidate all
		if (Logger.isTracing(StructuredTextReconciler.TRACE_FILTER)) {
			String message = "[trace reconciler] >  \r\n====================" + "\n cache updated:" + "\n arg0 :" + arg0 + "\n arg1 :" + arg1 + "\n arg3 :" + arg3 + "\n arg4 :" + arg4; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			Logger.trace(StructuredTextReconciler.TRACE_FILTER, message);
		}
		if (arg3 == CMDocumentCache.STATUS_LOADED) {
			Logger.trace(StructuredTextReconciler.TRACE_FILTER, "CMDocument finished loading :" + arg1); //$NON-NLS-1$
			doRefreshAll((INodeNotifier) fDocumentTypeForRefresh, fProgressMonitorForRefresh);
		}
	}

	protected IReconcileResult[] doRefreshAll(INodeNotifier notifier, IProgressMonitor monitor) {

		Logger.trace(StructuredTextReconciler.TRACE_FILTER, "[trace reconciler] > refreshing all"); //$NON-NLS-1$

		synchronized (fDirtyElements) {
			fDirtyElements.clear();
		}
		Document doc = (((Node) notifier).getNodeType() != Node.DOCUMENT_NODE) ? ((Node) notifier).getOwnerDocument() : (Document) notifier;
		return reconcileSubtree((INodeNotifier) doc, monitor);
	}

	protected ModelQuery getModelQuery(Node node) {

		return (node.getNodeType() == Node.DOCUMENT_NODE) ? ModelQueryUtil.getModelQuery((Document) node) : ModelQueryUtil.getModelQuery(node.getOwnerDocument());
	}

	/**
	 * returns a list of required CMAttributeDeclarations for the given
	 * element.
	 * 
	 * @param elementDecl
	 * 
	 */
	protected List getRequiredAttributes(CMElementDeclaration elementDecl) {

		CMNamedNodeMap attrMap = elementDecl.getAttributes();
		Iterator it = attrMap.iterator();
		CMAttributeDeclaration attr = null;
		List result = new ArrayList();
		while (it.hasNext()) {
			attr = (CMAttributeDeclaration) it.next();
			if (attr.getUsage() == CMAttributeDeclaration.REQUIRED) {
				result.add(attr);
			}
		}
		return result;
	}

	/**
	 * Determine if this Document is an XML/XHTML Document and whether to be
	 * case sensitive
	 */
	protected boolean isCaseSensitive(Node node) {

		return true;
	}

	// CMVC 254838
	/**
	 * Indicates if the element is not in the ContentModel (if it's not in the
	 * ContentModel, but its parent is)
	 * 
	 * @param element
	 * @param modelQuery
	 * @return whether or not the element is unknown according to its
	 *         associated ContentModel.
	 */
	protected boolean isUnknown(Element element, ModelQuery modelQuery) {

		boolean result = false;
		CMElementDeclaration ed = modelQuery.getCMElementDeclaration(element);
		if (ed == null) {
			// make sure parent declaration exists, and is not inferred
			Node parentNode = element.getParentNode();
			if (parentNode != null && parentNode.getNodeType() == Node.ELEMENT_NODE) {
				CMElementDeclaration parentEd = modelQuery.getCMElementDeclaration((Element) parentNode);
				// 2/19/04 porting iFix for lax schema suppport
				result = (parentEd != null) && !Boolean.TRUE.equals(parentEd.getProperty("isInferred")) //$NON-NLS-1$
							&& !Boolean.TRUE.equals(parentEd.getProperty("isLax")); //$NON-NLS-1$
			}
			// need one error for the root at least
			// to indicate the document is wrong...
			Document ownerDoc = element.getOwnerDocument();
			if (ownerDoc != null && ownerDoc.getDocumentElement() == element) {
				CMDocument cmDoc = modelQuery.getCorrespondingCMDocument(ownerDoc);
				result = (cmDoc != null && cmDoc.getElements().getLength() > 0);
			}
		} else {
			if (ed.getProperty("isInferred") != null && Boolean.TRUE.equals(ed.getProperty("isInferred")) //$NON-NLS-1$ //$NON-NLS-2$
						|| (ed.getProperty("partialContentModel") != null && Boolean.TRUE.equals(ed.getProperty("partialContentModel")))) { //$NON-NLS-1$ //$NON-NLS-2$
				result = false;
			}
		}
		return result;
	}

	/**
	 * Checks if name matches any mayImpactContentModel[] strings
	 * 
	 * @param name
	 * @return if a match is found, return true, else return false
	 */
	private boolean mayAffectContentModel(String name) {

		// TODO (pa) may need to be smarter if the attribute name is broken...
		StringTokenizer st = new StringTokenizer(name, ":", false); //$NON-NLS-1$
		String prefix = ""; //$NON-NLS-1$
		if (st.hasMoreTokens())
			prefix = st.nextToken();
		for (int i = 0; i < mayImpactContentModel.length; i++) {
			if (mayImpactContentModel[i].indexOf(name) != -1 || mayImpactContentModel[i].startsWith(prefix))
				return true;
		}
		return false;
	}

	public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {

		synchronized (fNotifications) {
			NotificationEvent newEvent = new NotificationEvent(notifier, eventType, changedFeature, oldValue, newValue, pos);
			if (!fNotifications.contains(newEvent))
				fNotifications.add(newEvent);
		}
	}


	public void processNotification(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos, IProgressMonitor monitor) {

		if (isCanceled(monitor))
			return;

		// (nsd) pa_TODO: we need to mark more or widen the scope affected by
		// the next reconcile() call
		// TODO: Handle multi-Node changes from Doctype Declarations, Taglib
		// directives,
		// and schema and namespace related attributes (DOCTYPE, taglib,
		// xmlns,
		// xsi...)
		// ** we currently don't get a notify on changed taglib...

		// we're going to validate everything again anyways after
		// proccessNotifications() has completed (in reconcile():
		// processNotifications() > refreshAll()),
		// no sense to do it here if we refreshingAll
		if (fNeedsRefreshAll)
			return;
		if (eventType == INodeNotifier.CHANGE || eventType == INodeNotifier.REMOVE) {
			if (changedFeature instanceof Node && ((Node) changedFeature).getNodeType() == Node.ATTRIBUTE_NODE) {
				if (mayAffectContentModel(((Node) changedFeature).getNodeName())) {
					fNeedsRefreshAll = true;
				}
			} else if (changedFeature instanceof Node && ((Node) changedFeature).getNodeType() == Node.DOCUMENT_TYPE_NODE) {
				fNeedsRefreshAll = true;
			} else if (notifier instanceof Node && ((Node) notifier).getNodeType() == Node.DOCUMENT_TYPE_NODE) {
				fNeedsRefreshAll = true;
			} else {
				// pa_TODO need to handle taglib definition changes...
				// if(mayAffectContentModel(((Node)changedFeature).getNodeName()))
				// {
				// System.out.println("dunno what changed > " +
				// changedFeature);
				// fNeedsRefreshAll = true;
				// }
			}
			if (isCanceled(monitor))
				return;
			fNeedsRefreshAll = true;
		}
		if (eventType == INodeNotifier.CHANGE && changedFeature instanceof Element) {
			markForReconciling(changedFeature);
		} else if (eventType == INodeNotifier.ADD && newValue instanceof Node) {
			Node newNode = (Node) newValue;
			if (newNode.getNodeType() == Node.DOCUMENT_TYPE_NODE || newNode.getNodeName().equals("jsp:directive.taglib")) { // $NON-NLS-1$
				// //$NON-NLS-1$
				fNeedsRefreshAll = true;
			} else {
				markForReconciling(newNode);
			}
		}
		markForReconciling(notifier);
	}

	protected void processNotifications(IProgressMonitor monitor) {

		fProgressMonitorForRefresh = monitor;
		NotificationEvent[] events = null;
		synchronized (fNotifications) {
			if (fNotifications.isEmpty()) {
				return;
			}
			events = (NotificationEvent[]) fNotifications.toArray(new NotificationEvent[0]);
			fNotifications.clear();
		}
		for (int i = 0; i < events.length; i++) {
			processNotification(events[i].notifier, events[i].eventType, events[i].changedFeature, events[i].oldValue, events[i].newValue, events[i].pos, monitor);
		}
	}


	public IReconcileResult[] reconcile(IProgressMonitor monitor, XMLNode xmlNode) {

		processNotifications(monitor);
		IReconcileResult[] results = EMPTY_RECONCILE_RESULT_SET;
		if (fNeedsRefreshAll) {
			results = doRefreshAll(xmlNode, monitor);
			fNeedsRefreshAll = false;
		} else {
			results = super.reconcile(monitor, xmlNode);
		}
		return results;
	}

	/**
	 * Called by super.reconcile(IAnnotationModel) on each Notifier
	 * 
	 */
	protected IReconcileResult[] reconcile(Object o, IProgressMonitor monitor) {

		super.reconcile(o, monitor);
		ModelQuery mq = null;
		if (o instanceof XMLNode) {
			XMLNode xmlNode = (XMLNode) o;
			mq = getModelQuery(xmlNode);
			if (mq != null) {
				fCaseSensitive = isCaseSensitive(xmlNode);
				return validate(mq, xmlNode);
			}
		}

		// if we are in a large reconciling loop (like when reconciling the
		// entire doc), this ensures
		// that other Threads have a chance to run.
		yieldIfNeeded();
		return EMPTY_RECONCILE_RESULT_SET;
	}

	/**
	 * Reconcile the Node and all children of the Notifier passed in.
	 * 
	 * @param notifier
	 * @param monitor
	 */
	protected IReconcileResult[] reconcileSubtree(INodeNotifier notifier, IProgressMonitor monitor) {

		IReconcileResult[] temp = EMPTY_RECONCILE_RESULT_SET;
		List results = new ArrayList();
		if (!isCanceled(monitor)) {
			if (notifier != null && notifier instanceof XMLNode) {
				XMLNode current = (XMLNode) notifier;
				// loop siblings
				while (current != null) {
					// mark whatever type nodes we wanna make dirty
					if (current.getNodeType() == Node.ELEMENT_NODE || current.getNodeType() == Node.DOCUMENT_TYPE_NODE || current.getNodeType() == Node.DOCUMENT_NODE || current.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE) {
						temp = reconcile(current, monitor);
						for (int i = 0; i < temp.length; i++)
							results.add(temp[i]);
					}
					// drop one level deeper if necessary
					if (current.getFirstChild() != null) {
						temp = reconcileSubtree((INodeNotifier) current.getFirstChild(), monitor);
						for (int i = 0; i < temp.length; i++)
							results.add(temp[i]);
					}
					current = (XMLNode) current.getNextSibling();
				}
			}
			temp = new IReconcileResult[results.size()];
			System.arraycopy(results.toArray(), 0, temp, 0, results.size());
		}
		return temp;
	}

	/**
	 * Called from the ReconcileAdapterFactory
	 * 
	 */
	public void release() {

		if (fCMDocumentCache != null) {
			fCMDocumentCache.removeListener(this);
			fCMDocumentCache = null;
		}
	}

	/**
	 * Determines whether the given Attr should not be validated according to
	 * the ignoreAttributeNamesStartingWith array
	 * 
	 * @param attr
	 */
	protected boolean shouldIgnore(Attr attr) {

		boolean result = false;
		String name = attr.getNodeName();
		for (int i = 0; i < ignoreAttributeNamesStartingWith.length; i++) {
			if (fCaseSensitive) {
				if (name.startsWith(ignoreAttributeNamesStartingWith[i]))
					result = true;
			} else {
				try {
					if (name.length() >= ignoreAttributeNamesStartingWith[i].length() && ignoreAttributeNamesStartingWith[i].equalsIgnoreCase(name.substring(0, ignoreAttributeNamesStartingWith[i].length())))
						result = true;
				} catch (StringIndexOutOfBoundsException e) {
					result = true;
				}
			}
		}
		return result;
	}

	private void updateCMDocumentCache(ModelQuery mq, XMLNode xmlNode) {

		if (mq != null) {
			CMDocumentManager cmDocManager = mq.getCMDocumentManager();
			if (cmDocManager != null) {
				CMDocumentCache newCache = cmDocManager.getCMDocumentCache();
				if (newCache != null) {
					if (fCMDocumentCache == null) {
						// create fCMDocCache if necessary
						fCMDocumentCache = newCache;
						fCMDocumentCache.addListener(this);
						fDocumentTypeForRefresh = (DocumentType) xmlNode;
					} else if (fCMDocumentCache != newCache) {
						fCMDocumentCache.removeListener(this);
						fCMDocumentCache = newCache;
						fCMDocumentCache.addListener(this);
						fDocumentTypeForRefresh = (DocumentType) xmlNode;
					}
				}
			}
		}
	}

	/**
	 * Called by reconcile(IAnnotationModel, Object) when the Object is a
	 * Notifier
	 * 
	 */
	protected IReconcileResult[] validate(ModelQuery mq, XMLNode xmlNode) {

		List results = new ArrayList();
		if (xmlNode == null || !(xmlNode.getNodeType() == Node.ELEMENT_NODE || xmlNode.getNodeType() == Node.DOCUMENT_TYPE_NODE))
			return EMPTY_RECONCILE_RESULT_SET;
		// return early if the Node has gone stale
		if (xmlNode.getParentNode() == null || xmlNode.getOwnerDocument() == null) {
			return EMPTY_RECONCILE_RESULT_SET;
		}
		if (xmlNode.getNodeType() == Node.DOCUMENT_TYPE_NODE) {
			// sets CMDocumentCacheListener (this)
			updateCMDocumentCache(mq, xmlNode);
		}
		CMDocument doc = mq.getCorrespondingCMDocument(xmlNode.getOwnerDocument());
		// looks like this is a bad check to do... I thought we took it out
		// before
		// if(doc == null)
		// return EMPTY_RECONCILE_RESULT_SET;
		if (doc != null && doc.getElements().getLength() == 0) {
			// an empty document
			return EMPTY_RECONCILE_RESULT_SET;
		}
		// continue on null or FALSE; inferred grammars aren't predefined so
		// there's no point in continuing
		if (doc != null && doc.getProperty("isInferred") != null && Boolean.TRUE.equals(doc.getProperty("isInferred"))) { //$NON-NLS-1$ //$NON-NLS-2$
			return EMPTY_RECONCILE_RESULT_SET;
		}

		// if xmlNode is DOCTYPE, skip to the first element
		// if there are no elements, return (don't validate)
		XMLNode elementNode = xmlNode;
		if (xmlNode.getNodeType() == Node.DOCUMENT_TYPE_NODE) {
			boolean elementFound = false;
			while ((elementNode = (XMLNode) elementNode.getNextSibling()) != null) {
				if (elementNode.getNodeType() == Node.ELEMENT_NODE) {
					elementFound = true;
					break;
				}
			}
			if (!elementFound)
				return EMPTY_RECONCILE_RESULT_SET;
		}
		XMLElement element = (XMLElement) elementNode;
		// boolean needsEndTag = true;

		// test for a known element, if it's known, continue validating it
		CMElementDeclaration elementDecl = mq.getCMElementDeclaration(element);
		if (elementDecl != null) {
			// needsEndTag = needsEndTag(elementNode, elementDecl);
			NamedNodeMap attrs = element.getAttributes();
			List reqAttrList = getRequiredAttributes(elementDecl);
			for (int i = 0; i < attrs.getLength(); i++) {
				XMLAttr attr = (XMLAttr) attrs.item(i);
				if (!shouldIgnore(attr)) {

					// iFix V511i
					// CMVC 272647, attributes with namespace prefix get
					// marked
					// as error (even though they aren't)
					// CMNode attrDecl =
					// elementDecl.getAttributes().getNamedItem(attr.getNodeName());
					CMNode attrDecl = elementDecl.getAttributes().getNamedItem(attr.getLocalName());

					// test for a known attribute
					if (attrDecl != null) {
						// test for a known value (if there is an enumerated
						// list of them)
						String[] values = mq.getPossibleDataTypeValues(element, attrDecl);
						String currentValue = attr.getValue();
						boolean found = valueMatch(values, currentValue);
						if (!found) {
							int start = attr.getValueRegionStartOffset();
							int length = attr.getValueRegion().getTextLength();
							MessageFormat messageFormat = new MessageFormat(ResourceHandler.getString("Invalid_value_{0}")); //$NON-NLS-1$
							Object[] args = {currentValue.trim()};
							String message = messageFormat.format(args);
							Position p = new Position(start, length);
							IReconcileAnnotationKey key = createKey(elementNode.getFirstStructuredDocumentRegion(), IReconcileAnnotationKey.PARTIAL);
							results.add(new TemporaryAnnotation(p, SEVERITY_UNKNOWN_ATTR, message, key));
						}
						// remove from known required attribute list
						reqAttrList.remove(attrDecl);
					} else {
						MessageFormat messageFormat = new MessageFormat(ResourceHandler.getString("Unknown_attribute_{0}")); //$NON-NLS-1$
						Object[] args = {attr.getName()};
						String message = messageFormat.format(args);
						int start = attr.getNameRegionStartOffset();
						int length = attr.getNameRegion().getTextLength();
						Position p = new Position(start, length);
						IReconcileAnnotationKey key = createKey(elementNode.getFirstStructuredDocumentRegion(), IReconcileAnnotationKey.PARTIAL);
						results.add(new TemporaryAnnotation(p, SEVERITY_UNKNOWN_ATTR, message, key, ProblemIDsXML.UnknownAttr));
					}
				} else {
					// remove so we don't flag "ignored" attributes as missing
					reqAttrList.remove(elementDecl.getAttributes().getNamedItem(attr.getNodeName()));
				}
			}
			// if there are missing required attributes, create annotations
			// for
			// them
			if (reqAttrList != null && !reqAttrList.isEmpty()) {
				Iterator it = reqAttrList.iterator();
				int start = 0;
				int length = 1;
				CMAttributeDeclaration attr = null;
				while (it.hasNext()) {
					attr = (CMAttributeDeclaration) it.next();
					// sometimes getFirstStructuredDocumentRegion can return
					// null, this is a safety
					start = (element.getFirstStructuredDocumentRegion() != null) ? element.getFirstStructuredDocumentRegion().getStartOffset() : element.getStartOffset();
					length = (element.getFirstStructuredDocumentRegion() != null) ? element.getFirstStructuredDocumentRegion().getLength() : 1;

					MessageFormat messageFormat = new MessageFormat(ResourceHandler.getString("Missing_required_attribute_{0}")); //$NON-NLS-1$
					Object[] args = {attr.getAttrName()};
					String message = messageFormat.format(args);

					Position p = new Position(start, length);
					IReconcileAnnotationKey key = createKey(elementNode.getFirstStructuredDocumentRegion(), IReconcileAnnotationKey.PARTIAL);
					TemporaryAnnotation annotation = new TemporaryAnnotation(p, SEVERITY_MISSING_REQUIRED_ATTR, message, key, ProblemIDsXML.MissingRequiredAttr);

					IStructuredDocumentRegion startStructuredDocumentRegion = element.getStartStructuredDocumentRegion();
					if (startStructuredDocumentRegion != null) {
						String requiredAttrName = attr.getAttrName();
						String defaultAttrValue = attr.getDefaultValue();
						String insertString;
						if (defaultAttrValue == null)
							insertString = requiredAttrName + "=\"" + requiredAttrName + "\""; //$NON-NLS-1$ //$NON-NLS-2$
						else
							insertString = requiredAttrName + "=\"" + defaultAttrValue + "\""; //$NON-NLS-1$ //$NON-NLS-2$

						ITextRegion lastRegion = startStructuredDocumentRegion.getLastRegion();
						int insertOffset = lastRegion.getEnd();
						if (lastRegion.getEnd() == lastRegion.getTextEnd())
							insertString = " " + insertString; //$NON-NLS-1$
						if (lastRegion.getType() == XMLRegionContext.XML_TAG_CLOSE)
							insertOffset = lastRegion.getStart();
						Object[] additionalFixInfo = {insertString, new Integer(insertOffset)};
						annotation.setAdditionalFixInfo(additionalFixInfo);
						results.add(annotation);
					}
				}
			}
		} else if (isUnknown(element, mq)) { // CMVC 254838
			int start = element.getStartOffset();
			int length = element.getEndOffset() - element.getStartOffset();
			if (element.getStartStructuredDocumentRegion() != null && element.getStartStructuredDocumentRegion().getNumberOfRegions() > 1) {
				ITextRegion name = element.getStartStructuredDocumentRegion().getRegions().get(1);
				start = element.getStartStructuredDocumentRegion().getStartOffset(name);
				length = name.getTextLength();
			}
			MessageFormat messageFormat = new MessageFormat(ResourceHandler.getString("Unknown_element_{0}")); //$NON-NLS-1$
			Object[] args = {element.getNodeName()};
			String message = messageFormat.format(args);
			Position p = new Position(start, length);
			IReconcileAnnotationKey key = createKey(elementNode.getFirstStructuredDocumentRegion(), IReconcileAnnotationKey.PARTIAL);
			TemporaryAnnotation annotation = new TemporaryAnnotation(p, SEVERITY_UNKNOWN_ELEMENT, message, key, ProblemIDsXML.UnknownElement);

			// quick fix info
			int startTagOffset = -1, startTagLength = -1, endTagOffset = -1, endTagLength = -1;
			if (element.getStartStructuredDocumentRegion() != null) {
				startTagOffset = element.getStartStructuredDocumentRegion().getStartOffset();
				startTagLength = element.getStartStructuredDocumentRegion().getLength();
			}
			if (element.getEndStructuredDocumentRegion() != null) {
				endTagOffset = element.getEndStructuredDocumentRegion().getStartOffset();
				endTagLength = element.getEndStructuredDocumentRegion().getLength();
			}
			Object[] additionalFixInfo = {new Integer(startTagOffset), new Integer(startTagLength), new Integer(endTagOffset), new Integer(endTagLength)};

			annotation.setAdditionalFixInfo(additionalFixInfo);
			results.add(annotation);
		}
		IReconcileResult[] reconcileResults = new IReconcileResult[results.size()];
		System.arraycopy(results.toArray(), 0, reconcileResults, 0, results.size());
		return reconcileResults;
	}

	/**
	 * Determines if String value is within the values array given the current
	 * case sensitivity
	 * 
	 * @param values
	 * @param value
	 */
	protected boolean valueMatch(String[] values, String value) {

		boolean found = (values == null || values.length == 0 || value.length() == 0);
		for (int j = 0; j < values.length && !found; j++) {
			if (fCaseSensitive) {
				if (values[j].equals(value))
					found = true;
			} else if (values[j].equalsIgnoreCase(value))
				found = true;
		}
		return found;
	}

	// CMVC 255301
	// If we are in a large reconciling loop, this ensures
	// that other Threads have a chance to run.
	protected void yieldIfNeeded() {

		// 100 is arbitrary, may need a better number
		if (fReconcileCount >= 100) {
			Thread.yield();
			fReconcileCount = 0;
		} else {
			fReconcileCount++;
		}
	}
}
