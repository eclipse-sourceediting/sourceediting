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
package org.eclipse.wst.xml.ui.dnd;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.swt.dnd.DND;
import org.eclipse.wst.common.ui.dnd.DefaultDragAndDropCommand;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.xml.core.document.IDOMNode;
import org.eclipse.wst.xml.ui.internal.XMLUIPlugin;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class DragNodeCommand extends DefaultDragAndDropCommand {
	public DragNodeCommand(Object target, float location, int operations, int operation, Collection sources) {
		super(target, location, operations, operation, sources);
	}

	protected void beginModelChange(Node node, boolean batchUpdate) {
		IStructuredModel structuredModel = getStructedModel(node);
		if (structuredModel != null) {
			structuredModel.beginRecording(this, XMLUIPlugin.getResourceString("%DragNodeCommand.0")); //$NON-NLS-1$
			if (batchUpdate) {
				//  structuredModel.aboutToChangeModel();
			}
		}
	}

	public boolean canExecute() {
		return executeHelper(true);
	}


	public boolean doMove(Node source, Node parentNode, Node refChild, boolean testOnly) {
		boolean result = false;
		if (source.getNodeType() == Node.ATTRIBUTE_NODE) {
			Attr sourceAttribute = (Attr) source;
			Element sourceAttributeOwnerElement = sourceAttribute.getOwnerElement();
			if (parentNode.getNodeType() == Node.ELEMENT_NODE && sourceAttributeOwnerElement != parentNode) {
				result = true;
				if (!testOnly) {
					try {
						Element targetElement = (Element) parentNode;
						targetElement.setAttribute(sourceAttribute.getName(), sourceAttribute.getValue());
						sourceAttributeOwnerElement.removeAttributeNode(sourceAttribute);
					} catch (Exception e) {
					}
				}
			}
		} else {
			if ((parentNode.getNodeType() == Node.ELEMENT_NODE || parentNode.getNodeType() == Node.DOCUMENT_NODE) && !(refChild instanceof Attr)) {
				result = true;

				if (!testOnly) {
					if (isAncestor(source, parentNode)) {
						//System.out.println("can not perform this drag drop
						// operation.... todo... pop up dialog");
					} else {
						// defect 221055 this test is required or else the
						// node will
						// be removed from the tree and the insert will fail
						if (source != refChild) {
							source.getParentNode().removeChild(source);
							parentNode.insertBefore(source, refChild);
						}
					}
				}
			}
		}
		return result;
	}

	protected void endModelChange(Node node, boolean batchUpdate) {
		IStructuredModel structuredModel = getStructedModel(node);
		if (structuredModel != null) {
			structuredModel.endRecording(this);
			if (batchUpdate) {
				//  structuredModel.changedModel();
			}
		}
	}

	public void execute() {
		executeHelper(false);
	}

	//
	//
	public boolean executeHelper(boolean testOnly) {
		boolean result = true;
		if (target instanceof Node) {
			Node targetNode = (Node) target;
			Node parentNode = getParentForDropPosition(targetNode);
			Node refChild = getRefChild(targetNode);

			Vector sourcesList = new Vector();
			sourcesList.addAll(sources);

			removeMemberDescendants(sourcesList);
			boolean performBatchUpdate = sourcesList.size() > 5;

			if (!testOnly) {
				beginModelChange(targetNode, performBatchUpdate);
			}
			for (Iterator i = sourcesList.iterator(); i.hasNext();) {
				Object source = i.next();
				if (source instanceof Node) {
					if (!(refChild == null && targetNode instanceof Attr)) {
						result = doMove((Node) source, parentNode, refChild, testOnly);
					} else {
						result = false;
					}
					if (!result) {
						break;
					}
				}
			}
			if (!testOnly) {
				endModelChange(targetNode, performBatchUpdate);
			}
		} else {
			result = false;
		}
		return result;
	}


	public int getFeedback() {
		int result = DND.FEEDBACK_SELECT;
		if (location > 0.75) {
			result = DND.FEEDBACK_INSERT_AFTER;
		} else if (location < 0.25) {
			result = DND.FEEDBACK_INSERT_BEFORE;
		}
		return result;
	}

	protected Node getParentForDropPosition(Node node) {
		Node result = null;

		int feedback = getFeedback();
		if (feedback == DND.FEEDBACK_SELECT) {
			result = node;
		} else {
			result = getParentOrOwner(node);
		}
		return result;
	}


	protected Node getParentOrOwner(Node node) {
		return (node.getNodeType() == Node.ATTRIBUTE_NODE) ? ((Attr) node).getOwnerElement() : node.getParentNode();
	}


	protected Node getRefChild(Node node) {
		Node result = null;

		int feedback = getFeedback();

		if (feedback == DND.FEEDBACK_INSERT_BEFORE) {
			result = node;
		} else if (feedback == DND.FEEDBACK_INSERT_AFTER) {
			result = node.getNextSibling();
		}
		return result;
	}

	protected IStructuredModel getStructedModel(Node node) {
		IStructuredModel result = null;
		if (node instanceof IDOMNode) {
			result = ((IDOMNode) node).getModel();
		}
		return result;
	}

	// returns true if a is an ancestore of b
	//
	protected boolean isAncestor(Node a, Node b) {
		boolean result = false;
		for (Node parent = b; parent != null; parent = parent.getParentNode()) {
			if (parent == a) {
				result = true;
				break;
			}
		}
		return result;
	}


	/**
	 * This method removes members of the list that have ancestors that are
	 * also members of the list.
	 */
	protected void removeMemberDescendants(List list) {
		Hashtable table = new Hashtable();
		for (Iterator i = list.iterator(); i.hasNext();) {
			Object node = i.next();
			table.put(node, node);
		}

		for (int i = list.size() - 1; i >= 0; i--) {
			Node node = (Node) list.get(i);
			for (Node parent = getParentOrOwner(node); parent != null; parent = getParentOrOwner(parent)) {
				if (table.get(parent) != null) {
					list.remove(i);
					break;
				}
			}
		}
	}
}
