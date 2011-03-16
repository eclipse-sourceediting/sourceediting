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
 *     Hajime Katayama  - bug 245216 - correction to get the best node when the ending
 *                                     and starting offsets are equal, and they both
 *                                     contain the offset range.
 *     
 *******************************************************************************/
package org.eclipse.wst.dtd.core.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.wst.dtd.core.internal.document.DTDModelImpl;
import org.eclipse.wst.dtd.core.internal.event.IDTDFileListener;
import org.eclipse.wst.dtd.core.internal.event.NodesEvent;
import org.eclipse.wst.dtd.core.internal.parser.DTDRegionTypes;
import org.eclipse.wst.dtd.core.internal.util.DTDExternalReferenceRemover;
import org.eclipse.wst.dtd.core.internal.util.DTDModelUpdater;
import org.eclipse.wst.dtd.core.internal.util.DTDNotationReferenceRemover;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.events.NewDocumentEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.RegionChangedEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.RegionsReplacedEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentRegionsReplacedEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegionList;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;

public class DTDFile implements IndexedRegion {
	private NodeList attlistList = new NodeList(this, DTDRegionTypes.ATTLIST_TAG);
	private NodeList commentList = new NodeList(this, DTDRegionTypes.COMMENT_START);

	boolean creatingNewModel = false;
	private DTDModelImpl fDTDModel;

	private NodeList elementList = new NodeList(this, DTDRegionTypes.ELEMENT_TAG);
	private NodeList entityList = new NodeList(this, DTDRegionTypes.ENTITY_TAG);

	private List folderList = null;

	private boolean isMovingNode = false;

	private List modelListeners = new ArrayList();

	private List nodeList = new ArrayList();
	private NodeList notationList = new NodeList(this, DTDRegionTypes.NOTATION_TAG);
	private NodeList unrecognizedList = new NodeList(this, DTDRegionTypes.UNKNOWN_CONTENT);

	public DTDFile(DTDModelImpl dtdModel) {
		this.fDTDModel = dtdModel;
	}

	public void addDTDFileListener(IDTDFileListener listener) {
		modelListeners.add(listener);
	}

	protected void addNode(DTDNode node) {
		addNode(nodeList.size(), node);
	}

	protected void addNode(int index, DTDNode node) {
		nodeList.add(index, node);
		/*
		 * if (index < nodeList.size()) { insertBefore(node, (DTDNode)
		 * nodeList.get(index)); } else { appendChild(node); }
		 */
	}

	public DTDNode buildNode(IStructuredDocumentRegion flatNode) {
		// ITextRegionList regions = flatNode.getRegions();
		DTDNode node = null;
		if (isElement(flatNode)) {
			// then this is an element
			node = new Element(this, flatNode);
		}
		else if (isEntity(flatNode)) {
			node = new Entity(this, flatNode);
		}
		else if (isNotation(flatNode)) {
			node = new Notation(this, flatNode);
		}
		else if (isAttributeList(flatNode)) {
			node = new AttributeList(this, flatNode);
		}
		else if (isComment(flatNode)) {
			node = new Comment(this, flatNode);
		}
		else if (isParameterEntityReference(flatNode)) {
			node = new ParameterEntityReference(this, flatNode);
		}
		else if (!flatNode.getText().trim().equals("")) { //$NON-NLS-1$
			node = new Unrecognized(this, flatNode);
		}
		if (node != null) {
			insertNode(node);
			node.resolveRegions();
		}
		return node;
	}

	public void buildNodes(IStructuredDocumentRegionList list) {
		NodesEvent addedDTDNodes = new NodesEvent();

		TopLevelNode previousNode = null;
		for (int i = 0; i < list.getLength(); i++) {
			IStructuredDocumentRegion flatNode = list.item(i);
			TopLevelNode node = (TopLevelNode) buildNode(flatNode);
			// if we don't create a node, then we assume that the flat
			// node was whitespace. Tack it on to a previous toplevel
			// node
			if (node != null) {
				previousNode = node;
				addedDTDNodes.add(node);
			}
			else {
				if (previousNode != null) {
					previousNode.addWhitespaceStructuredDocumentRegion(flatNode);
				}
			}
		}

		if (addedDTDNodes.getNodes().size() > 0)// &&
		// creatingNewModel == false)
		{
			// now tell people about the additions
			notifyNodesAdded(addedDTDNodes);
		}
	}

	// Implements IndexedRegion

	public boolean contains(int testPosition) {
		return getStartOffset() <= testPosition && testPosition <= getEndOffset();
	}

	public void createAttributeList(DTDNode node, String name, boolean isAfter) {
		getDTDModel().beginRecording(this, DTDCoreMessages._UI_LABEL_DTD_FILE_ADD_ATTR_LIST); //$NON-NLS-1$
		String newStream = "<!ATTLIST " + name + ">\n"; //$NON-NLS-1$ //$NON-NLS-2$
		int offset = getInsertOffset(node, isAfter);
		getStructuredDocument().replaceText(this, offset, 0, newStream);
		getDTDModel().endRecording(this);
	}

	public void createComment(DTDNode node, String name, boolean isAfter) {
		getDTDModel().beginRecording(this, DTDCoreMessages._UI_LABEL_DTD_FILE_ADD_COMMENT); //$NON-NLS-1$
		String newStream = "<!-- " + name + " -->\n"; //$NON-NLS-1$ //$NON-NLS-2$
		int offset = getInsertOffset(node, isAfter);
		getStructuredDocument().replaceText(this, offset, 0, newStream);
		getDTDModel().endRecording(this);
	}

	public void createElement(DTDNode node, String name, boolean isAfter) {
		getDTDModel().beginRecording(this, DTDCoreMessages._UI_LABEL_DTD_FILE_ADD_ELEMENT); //$NON-NLS-1$
		String newStream = "<!ELEMENT " + name + " EMPTY>\n"; //$NON-NLS-1$ //$NON-NLS-2$
		int offset = getInsertOffset(node, isAfter);
		getStructuredDocument().replaceText(this, offset, 0, newStream);
		getDTDModel().endRecording(this);
	}

	public void createEntity(DTDNode node, String name, boolean isAfter) {
		getDTDModel().beginRecording(this, DTDCoreMessages._UI_LABEL_DTD_FILE_ADD_ENTITY); //$NON-NLS-1$
		String newStream = "<!ENTITY " + name + " \"\">\n"; //$NON-NLS-1$//$NON-NLS-2$
		int offset = getInsertOffset(node, isAfter);
		getStructuredDocument().replaceText(this, offset, 0, newStream);
		getDTDModel().endRecording(this);
	}

	public void createNotation(DTDNode node, String name, boolean isAfter) {
		getDTDModel().beginRecording(this, DTDCoreMessages._UI_LABEL_DTD_FILE_ADD_NOTATION); //$NON-NLS-1$
		String newStream = "<!NOTATION " + name + " SYSTEM \"\">\n"; //$NON-NLS-1$ //$NON-NLS-2$
		int offset = getInsertOffset(node, isAfter);
		getStructuredDocument().replaceText(this, offset, 0, newStream);
		getDTDModel().endRecording(this);
	}

	public void createParameterEntityReference(DTDNode node, String name, boolean isAfter) {
		getDTDModel().beginRecording(this, DTDCoreMessages._UI_LABEL_DTD_FILE_ADD_PARM_ENTITY_REF); //$NON-NLS-1$
		String newStream = name + "\n"; //$NON-NLS-1$
		int offset = getInsertOffset(node, isAfter);
		getStructuredDocument().replaceText(this, offset, 0, newStream);
		getDTDModel().endRecording(this);
	}

	public void deleteNode(DTDNode node) {
		getDTDModel().beginRecording(this, DTDCoreMessages._UI_LABEL_DTD_FILE_DELETE); //$NON-NLS-1$
		deleteNode(this, node);
		getDTDModel().endRecording(this);
	}

	public void deleteNode(Object requestor, DTDNode node) {
		DTDNode parent = (DTDNode) node.getParentNode();
		if (parent != null) {
			parent.delete(requestor, node);
		}
		else {
			if (!isMovingNode) {
				DTDModelUpdater updater = new DTDModelUpdater();
				updater.objectAboutToBeDeleted(requestor, node);
				if (node instanceof ParameterEntityReference) {
					Entity referencedEntity = ((ParameterEntityReference) node).getEntityObject();
					if (referencedEntity != null) {
						// remove references to all elements and parm entities
						// contained in our current model
						DTDExternalReferenceRemover remover = new DTDExternalReferenceRemover();
						remover.externalReferenceAboutToChange(requestor, referencedEntity);
					}
				}
				else if (node instanceof Notation) {
					Notation notation = ((Notation) node);
					DTDNotationReferenceRemover remover = new DTDNotationReferenceRemover();
					remover.notationAboutToBeDeleted(requestor, notation);
				}
			}

			// no parent? then delete up until the start of the next node
			// if it is a top level node
			int startOffset = node.getStartOffset();
			int endOffset = node.getWhitespaceEndOffset();
			if (node instanceof TopLevelNode) {
				endOffset = getInsertOffset(node, true);
			}
			getStructuredDocument().replaceText(requestor, startOffset, endOffset - startOffset, ""); //$NON-NLS-1$
		}
	}

	public NodeList getComments() {
		return commentList;
	}

	public DTDModelImpl getDTDModel() {
		return fDTDModel;
	}

	public NodeList getElementsAndParameterEntityReferences() {
		return elementList;
	}

	public int getEndOffset() {
		int result = -1;
		IStructuredDocumentRegion region = getStructuredDocument().getLastStructuredDocumentRegion();
		if (region != null) {
			result = region.getEndOffset();
		}
		return result;
	}

	public int getLength() {
		int result = -1;
		int start = getStartOffset();
		if (start >= 0) {
			int end = getEndOffset();
			if (end >= 0) {
				result = end - start;
				if (result < -1) {
					result = -1;
				}
			}
		}
		return result;
	}

	public NodeList getEntities() {
		return entityList;
	}



	public int getInsertOffset(DTDNode node, boolean isAfter) {
		int offset = 0;
		if (node != null) {
			if (isAfter) {
				// then get the next node and use it's start offset
				int index = getNodes().indexOf(getNode(node.getStructuredDTDDocumentRegion()));

				DTDNode afterNode = null;
				if (index + 1 < getNodes().size()) {
					afterNode = (DTDNode) getNodes().get(index + 1);
				}
				if (afterNode != null) {
					offset = afterNode.getStructuredDTDDocumentRegion().getStartOffset();
				}
				else {
					// add to end
					if (getStructuredDocument().getLastStructuredDocumentRegion() != null) {
						offset = getStructuredDocument().getLastStructuredDocumentRegion().getEndOffset();
					}
				}
			}
			else {
				offset = node.getStructuredDTDDocumentRegion().getStartOffset();
			}
		}
		else {
			// add to end
			if (getStructuredDocument().getLastStructuredDocumentRegion() != null) {
				offset = getStructuredDocument().getLastStructuredDocumentRegion().getEndOffset();
			}
		}
		return offset;
	}

	public String getName() {
		org.eclipse.core.runtime.Path path = new org.eclipse.core.runtime.Path(getDTDModel().getId().toString());
		return path.lastSegment();
	}

	public DTDNode getNode(IStructuredDocumentRegion flatNode) {
		for (int i = 0; i < nodeList.size(); i++) {
			DTDNode node = (DTDNode) nodeList.get(i);
			if (node.getStructuredDTDDocumentRegion() == flatNode) {
				return node;
			}
		}
		return null;
	}

	public IndexedRegion getNodeAt(int offset) {
		DTDNode node = getTopLevelNodeAt(offset);
		if (node != null) {
			return node.getDeepestNode(offset);
		}
		return null;
	}

	public IndexedRegion getNodeAt(int startOffset, int endOffset) {
		DTDNode node = getTopLevelNodeAt(startOffset);
		if (node != null) {
			return node.getDeepestNode(startOffset, endOffset);
		}
		return null;
	}

	public List getNodeLists() {
		if (folderList == null) {
			folderList = new ArrayList();
			folderList.add(notationList);
			folderList.add(entityList);
			folderList.add(elementList);
			folderList.add(attlistList);
			folderList.add(commentList);
			folderList.add(unrecognizedList);
		}
		return folderList;
	}

	public List getNodes() {
		return nodeList;
	}

	public NodeList getNotations() {
		return notationList;
	}

	public int getStartOffset() {
		int result = -1;
		IStructuredDocumentRegion region = getStructuredDocument().getFirstStructuredDocumentRegion();
		if (region != null) {
			result = region.getStartOffset();
		}
		return result;
	}

	public IStructuredDocument getStructuredDocument() {
		return fDTDModel.getStructuredDocument();
	}

	public DTDNode getTopLevelNodeAt(int offset) {
		DTDNode bestNode = null;
		for (int i = 0; i < nodeList.size(); i++) {
			DTDNode node = (DTDNode) nodeList.get(i);
			if (node.contains(offset)) {
				if(bestNode == null) {
					bestNode = node;
				} else {
					if(node.getStartOffset() > bestNode.getStartOffset()) {
						bestNode = node;
					}
				}
			}
		}
		return bestNode;
	}

	public NodeList getUnrecognized() {
		return unrecognizedList;
	}

	public void insertIntoModel(Object requestor, DTDNode reference, DTDNode node, boolean isAfter) {
		String nodeText = ""; //$NON-NLS-1$
		if (node instanceof TopLevelNode) {
			nodeText = ((TopLevelNode) node).getFullText();
		}
		else {
			nodeText = node.getNodeText();
		}
		int offset = getInsertOffset(reference, isAfter);
		getStructuredDocument().replaceText(requestor, offset, 0, nodeText);
	}


	public void insertNode(DTDNode node) {
		int startOffset = node.getStartOffset();
		int insertIndex = -1;
		// System.out.println("startoffset = " + startOffset);
		for (int i = 0; i < getNodes().size(); i++) {
			DTDNode currentNode = (DTDNode) getNodes().get(i);
			// System.out.println("currentNode endOffset = "
			// +currentNode.getEndOffset());

			if (currentNode.getEndOffset() > startOffset) {
				// System.out.println("endoffset " +
				// currentNode.getEndOffset() + " > " + startOffset);
				insertIndex = i;
				break;
			}
		}
		if (insertIndex == -1) {
			insertIndex = getNodes().size();
		}


		// System.out.println("insert index = " + insertIndex);

		addNode(insertIndex, node);
	}

	// it is assumed that flatnode contains at least 3 regions
	public boolean isAttributeList(IStructuredDocumentRegion flatNode) {
		if (flatNode.getRegions().size() >= 3) {
			ITextRegion second = flatNode.getRegions().get(1);
			ITextRegion third = flatNode.getRegions().get(2);
			if (second.getType().equals(DTDRegionTypes.EXCLAMATION) && third.getType().equals(DTDRegionTypes.ATTLIST_TAG)) {
				return true;
			}
		}
		return false;
	}

	// it is assumed that flatnode contains at least 3 regions
	public boolean isComment(IStructuredDocumentRegion flatNode) {
		if (flatNode.getRegions().size() >= 2) {
			ITextRegion region = flatNode.getRegions().get(1);
			if (region.getType().equals(DTDRegionTypes.COMMENT_START)) {
				return true;
			}
		}
		return false;
	}

	// it is assumed that flatnode contains at least 3 regions
	public boolean isElement(IStructuredDocumentRegion flatNode) {
		if (flatNode.getRegions().size() >= 3) {
			ITextRegion second = flatNode.getRegions().get(1);
			ITextRegion third = flatNode.getRegions().get(2);
			if (second.getType().equals(DTDRegionTypes.EXCLAMATION) && third.getType().equals(DTDRegionTypes.ELEMENT_TAG)) {
				return true;
			}
		}
		return false;
	}

	// it is assumed that flatnode contains at least 3 regions
	public boolean isEntity(IStructuredDocumentRegion flatNode) {
		if (flatNode.getRegions().size() >= 3) {
			ITextRegion second = flatNode.getRegions().get(1);
			ITextRegion third = flatNode.getRegions().get(2);
			if (second.getType().equals(DTDRegionTypes.EXCLAMATION) && third.getType().equals(DTDRegionTypes.ENTITY_TAG)) {
				return true;
			}
		}
		return false;
	}

	// it is assumed that flatnode contains at least 3 regions
	public boolean isNotation(IStructuredDocumentRegion flatNode) {
		if (flatNode.getRegions().size() >= 3) {
			ITextRegion second = flatNode.getRegions().get(1);
			ITextRegion third = flatNode.getRegions().get(2);
			if (second.getType().equals(DTDRegionTypes.EXCLAMATION) && third.getType().equals(DTDRegionTypes.NOTATION_TAG)) {
				return true;
			}
		}
		return false;
	}

	// it is assumed that flatnode contains at least 3 regions
	public boolean isParameterEntityReference(IStructuredDocumentRegion flatNode) {
		if (flatNode.getRegions().size() == 1) {
			ITextRegion region = flatNode.getRegions().get(0);
			if (region.getType().equals(DTDRegionTypes.ENTITY_PARM)) {
				return true;
			}
		}
		return false;
	}

	boolean isSameTopLevelType(DTDNode affectedNode) {
		IStructuredDocumentRegion flatNode = affectedNode.getStructuredDTDDocumentRegion();
		// return true if the flatnode still matches what the affectedNode
		// is representing
		if (affectedNode instanceof Element && isElement(flatNode)) {
			return true;
		}
		if (affectedNode instanceof Entity && isEntity(flatNode)) {
			return true;
		}
		if (affectedNode instanceof Comment && isComment(flatNode)) {
			return true;
		}
		if (affectedNode instanceof AttributeList && isAttributeList(flatNode)) {
			return true;
		}
		if (affectedNode instanceof Notation && isNotation(flatNode)) {
			return true;
		}
		if (affectedNode instanceof Unrecognized && isUnrecognized(flatNode)) {
			return true;
		}
		return false;
	}

	public boolean isUnrecognized(IStructuredDocumentRegion flatNode) {
		return !isElement(flatNode) && !isEntity(flatNode) && !isNotation(flatNode) && !isParameterEntityReference(flatNode) && !isAttributeList(flatNode) && !isComment(flatNode);
	}

	public void moveNode(Object requestor, DTDNode referenceNode, DTDNode nodeToMove, boolean isAfter) {
		isMovingNode = true;

		deleteNode(requestor, nodeToMove);
		insertIntoModel(requestor, referenceNode, nodeToMove, isAfter);
		isMovingNode = false;
	}

	public void newModel(NewDocumentEvent event) {
		creatingNewModel = true;
		nodeList.clear();
		NodesEvent removeEvent = new NodesEvent();
		removeEvent.getNodes().addAll(nodeList);
		notifyNodesRemoved(removeEvent);
		/* removeChildNodes(); */

		if (event.getStructuredDocument() != null && event.getStructuredDocument().getRegionList() != null) {
			buildNodes(event.getStructuredDocument().getRegionList());
		}
		creatingNewModel = false;
	}

	public void nodesReplaced(StructuredDocumentRegionsReplacedEvent event) {
		IStructuredDocumentRegionList oldNodes = event.getOldStructuredDocumentRegions();
		NodesEvent removedDTDNodes = new NodesEvent();
		for (int i = 0; i < oldNodes.getLength(); i++) {
			IStructuredDocumentRegion flatNode = oldNodes.item(i);

			for (Iterator iter = getNodes().iterator(); iter.hasNext();) {
				DTDNode node = (DTDNode) iter.next();
				if (node.getStructuredDTDDocumentRegion() == flatNode) {
					removedDTDNodes.add(node);
				}
			}
		}

		buildNodes(event.getNewStructuredDocumentRegions());

		if (removedDTDNodes.getNodes().size() > 0) {
			notifyNodesRemoved(removedDTDNodes);
			removeNodes(removedDTDNodes.getNodes());
		}
	}

	public void notifyNodeChanged(DTDNode node) {
		Iterator iter = modelListeners.iterator();
		while (iter.hasNext()) {
			IDTDFileListener listener = (IDTDFileListener) iter.next();
			listener.nodeChanged(node);
		}
	}

	public void notifyNodesAdded(NodesEvent addedNodes) {
		Iterator iter = modelListeners.iterator();
		while (iter.hasNext()) {
			IDTDFileListener listener = (IDTDFileListener) iter.next();
			listener.nodesAdded(addedNodes);
		}
	}

	protected void notifyNodesRemoved(NodesEvent event) {
		Iterator iter = modelListeners.iterator();
		while (iter.hasNext()) {
			IDTDFileListener listener = (IDTDFileListener) iter.next();
			listener.nodesRemoved(event);
		}
	}

	public void rebuildNodes(List nodes) {
		// remove the old nodes
		removeNodes(nodes);

		// now rebuild them
		NodesEvent addedDTDNodes = new NodesEvent();
		Iterator dtdNodes = nodes.iterator();
		while (dtdNodes.hasNext()) {
			DTDNode dtdNode = (DTDNode) dtdNodes.next();
			// System.out.println("rebuilding " +
			// dtdNode.getStructuredDocumentRegion().getText());

			DTDNode node = buildNode(dtdNode.getStructuredDTDDocumentRegion());
			if (node != null) {
				addedDTDNodes.add(node);
			}
		}
		if (addedDTDNodes.getNodes().size() > 0) {
			// now tell people about the additions
			notifyNodesAdded(addedDTDNodes);
		}
	}

	public void regionChanged(RegionChangedEvent event) {
		ITextRegion changedRegion = event.getRegion();
		IStructuredDocumentRegion flatNode = event.getStructuredDocumentRegion();
		DTDNode affectedNode = (DTDNode) getNodeAt(flatNode.getStartOffset(changedRegion), flatNode.getEndOffset(changedRegion));
		if (affectedNode != null) {
			// no need to resolve regions as it is just a change
			// affectedNode.resolveRegions();
			notifyNodeChanged(affectedNode);
		}
	}

	public void regionsReplaced(RegionsReplacedEvent event) {
		List nodesToRebuild = new ArrayList();
		IStructuredDocumentRegion flatNode = event.getStructuredDocumentRegion();
		DTDNode affectedNode = getNode(flatNode);

		if (!isSameTopLevelType(affectedNode)) {
			// Bug 111100 - Fire off a node removal event
			// to remove the node from the tree viewer
			NodesEvent removedDTDNodes = new NodesEvent();
			removedDTDNodes.add(affectedNode);
			notifyNodesRemoved(removedDTDNodes);
			nodesToRebuild.add(affectedNode);
			rebuildNodes(nodesToRebuild);
		}
		else {
			affectedNode.resolveRegions();
			notifyNodeChanged(affectedNode);
			// now try and determine which ones were added
			NodesEvent addedDTDNodes = new NodesEvent();
			ITextRegionList newRegions = event.getNewRegions();
			int size = newRegions.size();
			for (int i = 0; i < size; i++) {
				ITextRegion region = newRegions.get(i);
				DTDNode deepestNode = affectedNode.getDeepestNode(flatNode.getStartOffset(region), flatNode.getEndOffset(region));
				// Bug 111100 - We do not need to fire a node added event
				// for the affectedNode because that is already handled by
				// the node changed event above
				if (deepestNode != affectedNode && !addedDTDNodes.getNodes().contains(deepestNode)) {
					addedDTDNodes.add(deepestNode);
				}
			}
			if (addedDTDNodes.getNodes().size() > 0) {
				notifyNodesAdded(addedDTDNodes);
			}
		}
	}

	public void removeDTDFileListener(IDTDFileListener listener) {
		modelListeners.remove(listener);
	}

	protected void removeNodes(List nodes) {
		getNodes().removeAll(nodes);
		/*
		 * for (int i = 0; i < nodes.size(); i++) {
		 * removeChild((DTDNode)nodes.get(i)); } // end of for ()
		 */
	}
}
