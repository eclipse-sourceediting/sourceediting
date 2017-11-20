/*******************************************************************************
 * Copyright (c) 2001, 2011 IBM Corporation and others.
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
package org.eclipse.wst.dtd.core.internal;

import java.util.List;

import org.eclipse.wst.dtd.core.internal.parser.DTDRegionTypes;
import org.eclipse.wst.dtd.core.internal.text.RegionIterator;
import org.eclipse.wst.dtd.core.internal.util.DTDUniqueNameHelper;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;


public class CMGroupNode extends CMRepeatableNode {

	public static final char CHOICE = '|';
	public static final char SEQUENCE = ',';

	protected char connector = SEQUENCE;

	// protected ArrayList children = new ArrayList();

	public CMGroupNode(DTDFile file, IStructuredDocumentRegion flatNode) {
		super(file, flatNode);
	}

	public void addChild() {
		beginRecording(this, DTDCoreMessages._UI_LABEL_CM_GRP_NODE_ADD_CHILD); //$NON-NLS-1$
		DTDNode lastNode = (DTDNode) getLastChild();
		String elementName = DTDUniqueNameHelper.getUniqueName(getChildrenList(), "ChildNode"); //$NON-NLS-1$
		if (lastNode != null) {
			replaceText(this, lastNode.getEndOffset(), 0, String.valueOf(getConnector()) + elementName); //$NON-NLS-1$
		}
		else {
			replaceText(this, getStartOffset() + 1, 0, elementName); //$NON-NLS-1$
		}

		endRecording(this);
	}

	public void addGroup() {
		beginRecording(this, DTDCoreMessages._UI_LABEL_CM_GRP_NODE_ADD_GRP); //$NON-NLS-1$
		DTDNode lastNode = (DTDNode) getLastChild();
		if (lastNode != null) {
			replaceText(this, lastNode.getEndOffset(), 0, String.valueOf(getConnector()) + " ()"); //$NON-NLS-1$
		}
		else {
			replaceText(this, getStartOffset() + 1, 0, "()"); //$NON-NLS-1$
		}

		endRecording(this);
	}

	public void delete(Object requestor, DTDNode child) {
		Object[] children = getChildren();

		if (children.length == 1 && getFirstChild() == child) {
			replaceText(requestor, child.getStartOffset(), child.getNodeLength(), null);
			return;
		}

		for (int i = 0; i < children.length - 1; i++) {
			DTDNode childA = (DTDNode) children[i];
			DTDNode childB = (DTDNode) children[i + 1];

			boolean childADeleted = childA == child;
			boolean childBDeleted = childB == child;
			if (childADeleted || childBDeleted) {
				// we found the child
				int startOffset = childADeleted ? childA.getStartOffset() : childA.getEndOffset();
				int endOffset = childADeleted ? childB.getStartOffset() : childB.getEndOffset();
				replaceText(requestor, startOffset, endOffset - startOffset, ""); //$NON-NLS-1$
				removeChild(child);
				break;
			}
		}
	}

	/**
	 * Get the value of connector.
	 * 
	 * @return value of connector.
	 */
	public char getConnector() {
		Object[] children = getChildren();
		for (int i = 0; i < children.length - 1; i++) {
			DTDNode childA = (DTDNode) children[i];
			DTDNode childB = (DTDNode) children[i + 1];

			// create a stream between the two siblings and walk it
			// note that this stream includes the last region of the first
			// sibling and the first region of the next sibling.
			// both these should be ignored
			RegionIterator iter = new RegionIterator(getStructuredDTDDocumentRegion(), childA.getEndOffset(), childB.getStartOffset());
			// stream.setFirstRegion(childA.getLastRegion());
			// stream.setLastRegion(childB.getFirstRegion());
			// Iterator iter = stream.iterator();
			// skip the first region which is the last region of childA
			// do we need this now ?
			// iter.next();
			ITextRegion currentRegion = null;
			while (iter.hasNext() && currentRegion != childB.getStartRegion()) {
				currentRegion = iter.next();
				if (currentRegion.getType() == DTDRegionTypes.CONNECTOR) {
					connector = getStructuredDTDDocumentRegion().getText(currentRegion).charAt(0);
					return connector;
				}
			}
		}
		return connector;
	}

	public String getImagePath() {
		switch (getConnector()) {
			case SEQUENCE :
				return DTDResource.ONESEQUENCEICON;
			/*
			 * switch (getOccurrence()) { case ONCE : return
			 * resourcePlugin.getImage(DTDResource.ONESEQUENCEICON); case
			 * OPTIONAL : return
			 * resourcePlugin.getImage(DTDResource.OPTIONALSEQUENCEICON); case
			 * ONE_OR_MORE : return
			 * resourcePlugin.getImage(DTDResource.ONEORMORESEQUENCEICON);
			 * case ZERO_OR_MORE : return
			 * resourcePlugin.getImage(DTDResource.ZEROORMORESEQUENCEICON); }
			 */
			case CHOICE :
				return DTDResource.ONECHOICEICON;
		/*
		 * switch (getOccurrence()) { case ONCE : return
		 * resourcePlugin.getImage(DTDResource.ONECHOICEICON); case OPTIONAL :
		 * return resourcePlugin.getImage(DTDResource.OPTIONALCHOICEICON);
		 * case ONE_OR_MORE : return
		 * resourcePlugin.getImage(DTDResource.ONEORMORECHOICEICON); case
		 * ZERO_OR_MORE : return
		 * resourcePlugin.getImage(DTDResource.ZEROORMORECHOICEICON); }
		 */
		}
		return null;
	}

	public String getName() {
		return ""; //$NON-NLS-1$
	}

	// returns the occurrenceregion, or the last region where the occurrence
	// region should appear after
	public ITextRegion getOccurrenceRegion() {
		int nesting = 0;

		// we skip past the first left paren we see since that is the
		// beginning of our own node
		RegionIterator iter = iterator();
		// we assume the first region is the '('
		iter.next();
		ITextRegion currentRegion = null;
		while (iter.hasNext() && nesting >= 0) {
			currentRegion = iter.next();
			if (currentRegion.getType() == DTDRegionTypes.LEFT_PAREN) {
				nesting++;
			}
			if (currentRegion.getType() == DTDRegionTypes.RIGHT_PAREN) {
				nesting--;
			}
		}
		if (nesting < 0) {
			// This means we have passed over the right paren that marks the
			// end of our grouping.
			// Look for an occurrence region
			while (iter.hasNext()) {
				currentRegion = iter.next();
				if (currentRegion.getType() == DTDRegionTypes.OCCUR_TYPE) {
					return currentRegion;
				}
			}
		}
		// if we're here, this means that there is no occur region. return the
		// last region
		return iter.previous();
	}

	public String getType() {
		if (isRootElementContent()) {
			if (getFirstChild() != null) {
				CMNode node = (CMNode) getFirstChild();
				if (node.getType().equals(PCDATA)) {
					return MIXED;
				}
				else {
					return CHILDREN;
				}
			}
		}
		return ""; //$NON-NLS-1$
	}

	public void insertChildNode(Object requestor, String nodeText, int position) {
		Object[] children = getChildren();

		int startOffset = 0;
		String newText = ""; //$NON-NLS-1$
		if (position < children.length) {
			DTDNode reference = (DTDNode) children[position];
			startOffset = reference.getStartOffset();
			newText = nodeText + " " + String.valueOf(getConnector()) + " "; //$NON-NLS-1$ //$NON-NLS-2$
		}
		else if (position == children.length) {
			// add to end
			DTDNode reference = (DTDNode) children[position - 1];
			startOffset = reference.getEndOffset();
			newText = " " + String.valueOf(getConnector()) + " " + nodeText; //$NON-NLS-1$ //$NON-NLS-2$
		}
		replaceText(requestor, startOffset, 0, newText);
	}

	public void insertChildNode(String nodeText, int position) {
		beginRecording(this, DTDCoreMessages._UI_LABEL_CM_GRP_NODE_INSERT_ELEMENT); //$NON-NLS-1$
		insertChildNode(this, nodeText, position);
		endRecording(this);
	}

	public void insertIntoModel(Object requestor, CMNode reference, CMNode node, boolean isAfter) {
		String nodeText = node.getNodeText();
		List children = getChildrenList();

		int index = children.indexOf(reference);
		if (index == -1) {
			// no reference node, add it to the end??
			index = children.size();
		}
		else {
			// got an index. if we want to add after, increase by 1
			index = isAfter ? index + 1 : index;
		}
		insertChildNode(requestor, nodeText, index);
	}

	public void resolveRegions() {
		int nesting = 0;
		// children.clear();
		removeChildNodes();
		DTDNode currentGroupNode = null;
		CMBasicNode currentReferenceNode = null;
		RegionIterator iter = iterator();
		// we assume the first region is the '('
		iter.next();
		while (iter.hasNext() && nesting >= 0) {
			ITextRegion currentRegion = iter.next();
			if (nesting == 0) {
				if (currentRegion.getType().equals(DTDRegionTypes.CONTENT_PCDATA)) {
					currentGroupNode = currentReferenceNode = null;
					DTDNode pcData = new CMBasicNode(getDTDFile(), getStructuredDTDDocumentRegion());
					pcData.addRegion(currentRegion);
					appendChild(pcData);
					// children.add(pcData);
				}
				else if (currentRegion.getType().equals(DTDRegionTypes.NAME)) {
					// we have hit a new reference node. Make sure we reset
					// the groupnode var so it doesn't collect more regions
					currentGroupNode = null;
					currentReferenceNode = new CMBasicNode(getDTDFile(), getStructuredDTDDocumentRegion());
					currentReferenceNode.addRegion(currentRegion);
					appendChild(currentReferenceNode);
					// children.add(currentReferenceNode);
				}
				else if (currentRegion.getType().equals(DTDRegionTypes.OCCUR_TYPE)) {
					// we could potentially flag an error here if we hit an
					// occurrence type and currentRefNode and currentGroupNode
					// are null
					if (currentReferenceNode != null) {
						// currentReferenceNode.setOccurrence(currentRegion.getText().toCharArray()[0]);
						currentReferenceNode.addRegion(currentRegion);
						currentReferenceNode = null;
					}
				}
				else if (currentRegion.getType().equals(DTDRegionTypes.CONNECTOR)) {
					// note that if connector is already set and it is
					// different from the current connector region, then we
					// have an error!
					// setConnector(currentRegion.getText().toCharArray()[0]);
				}
				else if (currentRegion.getType().equals(DTDRegionTypes.LEFT_PAREN)) {
					if (currentGroupNode == null) {
						// we have hit a new group. Make sure we reset the
						// referencenode var so it doesn't collect any more
						// regions
						currentReferenceNode = null;
						currentGroupNode = new CMGroupNode(getDTDFile(), getStructuredDTDDocumentRegion());
						appendChild(currentGroupNode);
						// children.add(currentGroupNode);
					}
				}
			}

			if (currentRegion.getType().equals(DTDRegionTypes.LEFT_PAREN)) {
				nesting++;
			}
			if (currentRegion.getType().equals(DTDRegionTypes.RIGHT_PAREN)) {
				nesting--;
				if (nesting == 0 && currentGroupNode != null) {
					currentGroupNode.addRegion(currentRegion);
					// peek at next region to see if it is an occur region. if
					// so, add it to the groupnode
					if (iter.hasNext()) {
						ITextRegion nextRegion = iter.next();
						if (nextRegion.getType().equals(DTDRegionTypes.OCCUR_TYPE)) {
							currentGroupNode.addRegion(nextRegion);
						}
						else {
							// Otherwise, push it back as the next item to be
							// retrieved by a future next() call
							iter.previous();
						}
					}
					currentGroupNode = null;
				}
			}
			if (currentGroupNode != null) {
				currentGroupNode.addRegion(currentRegion);
			}
		}

		if (nesting < 0) {
			// This means we have passed over the right paren that marks the
			// end of our grouping.
			// Look for an occurrence region
			while (iter.hasNext()) {
				ITextRegion currentRegion = iter.next();
				if (currentRegion.getType().equals(DTDRegionTypes.OCCUR_TYPE)) {
					// setOccurrence(currentRegion.getText().toCharArray()[0]);
				}
			} // end of while ()
		}

		// for (org.w3c.dom.Node child = getFirstChild(); child != null; child
		// = child.getNextSibling())
		// {
		// System.out.println("child found = " + child);
		// }

		Object[] children = getChildren();
		// System.out.println("children legnth = " + children.length);

		for (int i = 0; i < children.length; i++) {
			DTDNode currentNode = (DTDNode) children[i];
			currentNode.resolveRegions();
		} // end of while ()

	}

	/**
	 * Set the value of connector.
	 * 
	 * @param v
	 *            Value to assign to connector.
	 */
	public void setConnector(char v) {
		if (connector != v) {
			connector = v;
			// walk through our kids and see if there is a connector between
			// each sibling. if not, create one and set the connector. if
			// there is
			// then just change the text of the connector
			Object[] children = getChildren();
			if (children.length <= 1) {
				// there won't be any connector existing between the children
				// just notify a change in the node and return;
				getDTDFile().notifyNodeChanged(this);
				return;
			}
			beginRecording(this, DTDCoreMessages._UI_LABEL_CM_GRP_NODE_CONNECTOR); //$NON-NLS-1$
			for (int i = 0; i < children.length - 1; i++) {
				DTDNode childA = (DTDNode) children[i];
				DTDNode childB = (DTDNode) children[i + 1];

				// create a stream between the two siblings and walk it
				// note that this stream includes the last region of the first
				// sibling and the first region of the next sibling.
				// both these should be ignored
				RegionIterator iter = new RegionIterator(getStructuredDTDDocumentRegion(), childA.getEndOffset(), childB.getStartOffset());
				// skip the first region which is the last region of childA

				// do we still need this
				// iter.next();
				ITextRegion currentRegion = null;
				boolean foundConnector = false;
				while (iter.hasNext() && currentRegion != childB.getStartRegion()) {
					currentRegion = iter.next();
					if (currentRegion.getType() == DTDRegionTypes.CONNECTOR) {
						foundConnector = true;
						// Region oldRegion = currentRegion.createCopy();
						// found a connector! on to the next sibling pair
						// currentRegion.updateText(String.valueOf(v));
						replaceText(this, getStructuredDTDDocumentRegion().getStartOffset(currentRegion), 1, String.valueOf(connector));
						// changeStructuredDocument(oldRegion, currentRegion);
						break;
					}
				}

				if (!foundConnector) {
					// if we're here, that means we need to insert a new
					// connector region after childA
					replaceText(this, childA.getEndOffset(), 0, String.valueOf(connector));
					// DTDRegion connectorRegion = new
					// DTDRegion(DTDRegionTypes.CONNECTOR,
					// childA.getEndOffset(), 1);
					// insertIntoStructuredDocument(connectorRegion);
				}
			}
			endRecording(this);
		}
	}

	// public Object[] getChildren()
	// {
	// return children.toArray();
	// }
}// CMGroupNode
