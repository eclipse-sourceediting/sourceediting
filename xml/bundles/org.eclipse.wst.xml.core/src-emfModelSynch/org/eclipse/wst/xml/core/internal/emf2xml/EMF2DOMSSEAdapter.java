/*******************************************************************************
 * Copyright (c) 2001, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.core.internal.emf2xml;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.wst.common.internal.emf.resource.EMF2DOMAdapter;
import org.eclipse.wst.common.internal.emf.resource.EMF2DOMAdapterImpl;
import org.eclipse.wst.common.internal.emf.resource.EMF2DOMRenderer;
import org.eclipse.wst.common.internal.emf.resource.Translator;
import org.eclipse.wst.common.internal.emf.resource.TranslatorResource;
import org.eclipse.wst.common.internal.emf.utilities.Assert;
import org.eclipse.wst.common.internal.emf.utilities.DOMUtilities;
import org.eclipse.wst.common.internal.emf.utilities.FeatureValueConversionException;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xml.core.internal.Logger;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class EMF2DOMSSEAdapter extends EMF2DOMAdapterImpl implements INodeAdapter {
	
	private Class resourceClass;
	public EMF2DOMSSEAdapter(Node node, EMF2DOMRenderer renderer, Translator translator) {
		super(node, renderer, translator);
	}

	public EMF2DOMSSEAdapter(Notifier object, Node node, EMF2DOMRenderer renderer, Translator translator) {
		super(object, node, renderer, translator);
	}

	public EMF2DOMSSEAdapter(TranslatorResource resource, Document document, EMF2DOMRenderer renderer, Translator translator) {
		super(resource, document, renderer, translator);
	}

	protected String calcIndentString(Node node) {
		Assert.isNotNull(node);
		Assert.isNotNull(node.getParentNode(), "Node must be connected into the tree"); //$NON-NLS-1$

		Node parent = node.getParentNode();

		String indentString = getNewlineString(node);

		// Find indentation string for this node based on its sibling or
		// parent
		Node previousSibling = DOMUtilities.getPreviousNodeSibling(node);
		if (previousSibling != null) {
			indentString = primGetIndentString(previousSibling);
		}
		else {
			String parentIndentString = primGetIndentString(parent);
			indentString = parentIndentString + DOMUtilities.INDENT_STRING;
		}
		return indentString;
	}

	/*
	 * Prints out a DOM notification for debugging.
	 */
	protected void debugDOMNotify(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue) {
		if (fDebug) {
			String notifType = ""; //$NON-NLS-1$
			switch (eventType) {
				case INodeNotifier.ADD :
					notifType = "ADD"; //$NON-NLS-1$
					break;
				case INodeNotifier.REMOVE :
					notifType = "REMOVE"; //$NON-NLS-1$
					break;
				case INodeNotifier.CHANGE :
					notifType = "CHANGE"; //$NON-NLS-1$
					break;
				case INodeNotifier.CONTENT_CHANGED :
					notifType = "CONTENT_CHANGED"; //$NON-NLS-1$
					break;
				case INodeNotifier.STRUCTURE_CHANGED :
					notifType = "STRUCTURE_CHANGE"; //$NON-NLS-1$
					break;
			}
			Logger.log(Logger.INFO_DEBUG, "DOM Change: " + notifType); //$NON-NLS-1$
			Logger.log(Logger.INFO_DEBUG, "\tnotifier      : " + notifier); //$NON-NLS-1$
			Logger.log(Logger.INFO_DEBUG, "\tchangedFeature: " + changedFeature); //$NON-NLS-1$
			Logger.log(Logger.INFO_DEBUG, "\toldValue      : " + oldValue); //$NON-NLS-1$
			Logger.log(Logger.INFO_DEBUG, "\tnewValue      : " + newValue); //$NON-NLS-1$
		}
	}

	protected void disableUndoManagementIfNecessary() {
		IDOMModel model = getXMLModel();
		if (model != null && model.getUndoManager() != null)
			model.disableUndoManagement();
	}

	protected void enableUndoManagement() {
		IDOMModel model = getXMLModel();
		if (model != null && model.getUndoManager() != null)
			model.enableUndoManagement();
	}

	protected String getNewlineString(Node node) {
		/*
		 * We should always have IDOMNode, and IStructuredDocument, and
		 * consquently a valid "preferred" line delimiter, but just to be
		 * safe, we'll assign something by default.
		 */
		if (node instanceof IDOMNode) {
			IDOMNode xmlNode = (IDOMNode) node;
			IStructuredDocument document = xmlNode.getStructuredDocument();
			if (document != null) {
				return document.getLineDelimiter();
			}
		}
		return DOMUtilities.NEWLINE_STRING;
	}

	protected IDOMModel getXMLModel() {
		if (getNode() != null)
			return ((IDOMNode) getNode()).getModel();
		return null;
	}

	protected IDOMNode getXMLNode() {
		return (IDOMNode) getNode();
	}

	/*
	 * Do nothing for SSE, we will tolerate anything they add
	 */
	protected void handleFeatureValueConversionException(FeatureValueConversionException ex) {
		// Do nothing
	}

	/*
	 * Do nothing for SSE, we will tolerate anything they add
	 */
	protected void handleInvalidMultiNodes(String nodeName) {
		// Do nothing
	}

	protected void indent(Node node, Translator map) {
		Assert.isNotNull(node.getParentNode(), "Node must be connected into the tree"); //$NON-NLS-1$
		Assert.isNotNull(node);

		String indentString = calcIndentString(node);

		// Indent before the start tag
		indentStartTag(indentString, node, map);

		// Indent before the end tag
		indentEndTag(indentString, node, map);
	}

	/**
	 * Indent before the end tag of the <node>passed in.
	 */
	protected void indentEndTag(String indentString, Node node, Translator map) {
		if (!map.shouldIndentEndTag(node))
			return;
		String domPath = map.getDOMPath();

		if ((!map.isManagedByParent() && !map.isDOMTextValue()) || (map.isManagedByParent() && domPath.length() != 0) && node.getNodeName().equals(domPath)) {
			Text newWS = node.getOwnerDocument().createTextNode(getNewlineString(node) + indentString); //$NON-NLS-1$
			DOMUtilities.insertBeforeNode(node, newWS, null);
		}
	}

	/**
	 * Indent before the start tag of the <node>passed in.
	 */
	protected void indentStartTag(String indentString, Node node, Translator map) {
		Node parent = node.getParentNode();
		Text newWS = node.getOwnerDocument().createTextNode(getNewlineString(node) + indentString); //$NON-NLS-1$
		DOMUtilities.insertAfterNode(parent, newWS, DOMUtilities.getPreviousNodeSibling(node));
	}

	protected boolean isEmptyTag(Element parent) {
		return ((IDOMElement) parent).isEmptyTag();
	}

	/*
	 * This method is called when the DOM node changes. It attempts to update
	 * MOF object based on the changes.
	 */
	public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {

		if (!isNotificationEnabled())
			return;

		debugDOMNotify(notifier, eventType, changedFeature, oldValue, newValue);

		if (notifier != getNode() && eventType != INodeNotifier.CHANGE) {
			// This is the case where the notification was sent from a
			// sub node. Use the notifiers name to determine which
			// MOF feature to update. Note that is is assumed that if
			// the eventType is CHANGE then it attribute on a path node
			// changing. This was put in for the EGL group.
			if (notifier instanceof Element) {
				if (eventType == INodeNotifier.STRUCTURE_CHANGED || eventType == INodeNotifier.CONTENT_CHANGED || eventType == INodeNotifier.CHANGE) {
					Element notifyingNode = (Element) notifier;
					Translator map = findTranslator(notifyingNode.getNodeName(), false);
					if (map != null)
						updateMOFFeature(map, getNode(), getEObject());
				}
			}
		}
		else {
			// Update everything on STRUCTURE_CHANGE or CONTENT_CHANGE.
			// Other event types occur too often.
			if (eventType == INodeNotifier.STRUCTURE_CHANGED || eventType == INodeNotifier.CONTENT_CHANGED) {
				updateMOF();
			}
			// Update just the attribute that changed.
			else if (eventType == INodeNotifier.CHANGE) {
				Translator map = findTranslator(changedFeature.toString(), true);
				if (map != null)
					updateMOFFeature(map, getNode(), getEObject());
			}
		}
	}

	protected void postUpdateDOMFeature(Translator map, Node node, EObject mofObject) {
		enableUndoManagement();
	}

	protected void preUpdateDOMFeature(Translator map, Node node, EObject mofObject) {
		super.preUpdateDOMFeature(map, node, mofObject);
		disableUndoManagementIfNecessary();
	}

	protected void primAddDOMAdapter(Node aNode, EMF2DOMAdapter anAdapter) {
		((IDOMNode) aNode).addAdapter((EMF2DOMSSEAdapter) anAdapter);
	}

	/**
	 * Create an adapter for a child DOM node
	 * 
	 * @param node
	 *            org.w3c.dom.Node The node to create the adapter for.
	 */
	protected EMF2DOMAdapter primCreateAdapter(EObject mofObject, Translator childMap) {
		Element newNode = createNewNode(mofObject, childMap);
		return new EMF2DOMSSEAdapter(mofObject, newNode, fRenderer, childMap);
	}

	/**
	 * Create an adapter for a child DOM node
	 * 
	 * @param node
	 *            org.w3c.dom.Node The node to create the adapter for.
	 */
	protected EMF2DOMAdapter primCreateAdapter(Node node, Translator childMap) {
		return new EMF2DOMSSEAdapter(node, fRenderer, childMap);
	}
	protected EMF2DOMAdapter createAdapter(Node node, Translator childMap) {

		//Assert.isNotNull(childMap.getChildAdapterClass());
		Assert.isNotNull(node);

		EMF2DOMAdapter adapter = primGetExistingAdapter(node,childMap);

		if (adapter != null) {
			if (adapter.isMOFProxy() || adapter.getTarget() == null) {
				removeDOMAdapter(node, adapter);
				if (adapter.getTarget() != null) {
					adapter.getTarget().eAdapters().remove(adapter);
				}
				adapter = null;
			}
		}
		if (adapter == null) {
			adapter = primCreateAdapter(node, childMap);
		}
		return adapter;
	}
	protected void addDOMAdapter(Node childNode) {

		// Only add the adapter if this is an child node that will not be
		// adapted. For instance a subtree that maps to a MOF attribute
		// setting.
		if (childNode.getNodeType() == Node.ELEMENT_NODE) {
			EMF2DOMAdapter attrAdapter = primGetExistingAdapter(childNode);

			if (attrAdapter == null || attrAdapter.getNode() != getNode()) {
				// If the node is adapted, but not by this adapter then remove
				// it. This happens to non-object children when the parent tag
				// name is changed.
				removeDOMAdapter(childNode, attrAdapter);

				if (fDebug) {
					org.eclipse.jem.util.logger.proxy.Logger.getLogger().logError("\tCHILD: Adding DOM adapter: " + this); //$NON-NLS-1$
					org.eclipse.jem.util.logger.proxy.Logger.getLogger().logError("\t\tto: " + childNode); //$NON-NLS-1$
				}
				primAddDOMAdapter(childNode, this);
			}
		}
	}
	
	protected EMF2DOMAdapter primGetExistingAdapter(Node aNode, Translator childMap) {
		INodeNotifier sseNode = (INodeNotifier) aNode;
		Collection adapters = sseNode.getAdapters();
		for (Iterator iterator = adapters.iterator(); iterator.hasNext();) {
			INodeAdapter adapter = (INodeAdapter) iterator.next();
			// First Check if it's an EMF2DOMAdapter
			if (adapter != null && adapter.isAdapterForType(EMF2DOMAdapter.ADAPTER_CLASS)) {
				// Cast to EMF2DOMAdapter
				EMF2DOMSSEAdapter e2DAdapter = (EMF2DOMSSEAdapter) adapter;
				Object adapterTarget = e2DAdapter.getTarget();
				//Translator adapterTranslator = e2DAdapter.fTranslator;
				
				//Handle the cases where either adapter's target is null 
				if ((getTarget() == null) || (adapterTarget == null))
					if (resourceClass != null && resourceClass.equals(e2DAdapter.getResourceClass()))
						return e2DAdapter;
					else
						continue;
				
				
				// Check if target is an EMF resource
				if (getTarget() instanceof Resource) {
					/*
					 * Now check if it's the right one (Multiple resources
					 * could be attached)
					 */
					if (adapterTarget != null && adapterTarget == getTarget()) {
						return e2DAdapter;
					}
				}
				else {
					// Check if targets are EObjects with the same resources
					EObject myTarget = (EObject) getTarget();
					/*
					 * Now check if it's the right one (Multiple resources could
					 * be attached)
					 */
					if (adapterTarget != null && ((EObject)adapterTarget).eResource() == myTarget.eResource()) {
						return e2DAdapter;
					}
				}
				
				if (adapterTarget instanceof EObject) {
					if (((EObject) e2DAdapter.getTarget()).eResource() == null && e2DAdapter.fTranslator == childMap) {
						return e2DAdapter;
					}
				}
			}
		}
		return null;
	}
	
	protected EMF2DOMAdapter primGetExistingAdapter(Node aNode) {
		INodeNotifier sseNode = (INodeNotifier) aNode;
		Collection adapters = sseNode.getAdapters();
		for (Iterator iterator = adapters.iterator(); iterator.hasNext();) {
			INodeAdapter adapter = (INodeAdapter) iterator.next();
			// First Check if it's an EMF2DOMAdapter
			if (adapter != null && adapter.isAdapterForType(EMF2DOMAdapter.ADAPTER_CLASS)) {
				// Cast to EMF2DOMAdapter
				EMF2DOMSSEAdapter e2DAdapter = (EMF2DOMSSEAdapter) adapter;
				Object adapterTarget = e2DAdapter.getTarget();
				
				//Handle the cases where either adapter's target is null 
				if ((getTarget() == null) || (adapterTarget == null))
					if (resourceClass != null && resourceClass.equals(e2DAdapter.getResourceClass()))
						return e2DAdapter;
					else
						continue;
				
				
				// Check if target is an EMF resource
				if (getTarget() instanceof Resource) {
					/*
					 * Now check if it's the right one (Multiple resources
					 * could be attached)
					 */
					if (adapterTarget != null && adapterTarget == getTarget()) {
						return e2DAdapter;
					}
				}
				else {
					// Check if targets are EObjects with the same resources
					EObject myTarget = (EObject) getTarget();
					/*
					 * Now check if it's the right one (Multiple resources could
					 * be attached)
					 */
					if (adapterTarget != null && ((EObject)adapterTarget).eResource() == myTarget.eResource()) {
						return e2DAdapter;
					}
				}
				
				if (adapterTarget instanceof EObject) {
					if (((EObject) e2DAdapter.getTarget()).eResource() == null) {
						return e2DAdapter;
					}
				}
			}
		}
		return null;
	}

	protected String primGetIndentString(Node node) {
		IStructuredDocument flatModel = ((IDOMNode) node).getStructuredDocument();
		int nodeStartOff = ((IDOMNode) node).getStartOffset();

		int startOff = Math.max(0, nodeStartOff - 100);
		int endOff = nodeStartOff;

		if(flatModel != null) {
			try {
				String text = flatModel.get(startOff, endOff - startOff);
	
				int inx = text.length() - 1;
				if (inx >= 0) {
					for (; inx >= 0; inx--) {
						char ch = text.charAt(inx);
						if (Character.isWhitespace(ch) && ch != '\n' && ch != '\r') {
							continue;
						}
						inx++;
						break;
					}
	
					return text.substring(inx);
				}
			}
			catch (BadLocationException ex) {
				Logger.logException(ex);
			}
		}
		return ""; //$NON-NLS-1$
	}

	protected void removeDOMAdapter(Node aNode, EMF2DOMAdapter anAdapter) {
		((IDOMNode) aNode).removeAdapter((INodeAdapter) anAdapter);
	}


	protected void reorderDOMChild(Node parentNode, Node childNode, Node insertBeforeNode, Translator map) {
		super.reorderDOMChild(parentNode, childNode, insertBeforeNode, map);
		// Since reordering deletes all the whitespace before the node, we
		// must indent .
		if (insertBeforeNode != null && insertBeforeNode.getNodeType() == Node.ELEMENT_NODE)
			indentStartTag(calcIndentString(insertBeforeNode), insertBeforeNode, map);
		else
			indentStartTag(calcIndentString(childNode), childNode, map);
	}

	protected void setEmptyTag(Element element) {
		((IDOMElement) element).setEmptyTag(true);
	}

	public void updateDOM() {
		if (!isNotificationEnabled())
			return;
		try {
			disableUndoManagementIfNecessary();
			primUpdateDOM();
		}
		finally {
			enableUndoManagement();
		}
	}

	public Class getResourceClass() {
		return resourceClass;
	}

	protected void initChildTranslators() {
		if (fRenderer != null && fRenderer.getResource() != null)
			resourceClass = fRenderer.getResource().getClass();
		super.initChildTranslators();
	}

}
