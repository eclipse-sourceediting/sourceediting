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
package org.eclipse.wst.dtd.core;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.dtd.core.document.DTDModelImpl;
import org.eclipse.wst.dtd.core.event.IDTDFileListener;
import org.eclipse.wst.dtd.core.event.NodesEvent;
import org.eclipse.wst.dtd.core.parser.DTDRegionTypes;
import org.eclipse.wst.dtd.core.util.DTDExternalReferenceRemover;
import org.eclipse.wst.dtd.core.util.DTDModelUpdater;
import org.eclipse.wst.dtd.core.util.DTDNotationReferenceRemover;
import org.eclipse.wst.sse.core.IndexedRegion;
import org.eclipse.wst.sse.core.events.NewModelEvent;
import org.eclipse.wst.sse.core.events.RegionChangedEvent;
import org.eclipse.wst.sse.core.events.RegionsReplacedEvent;
import org.eclipse.wst.sse.core.events.StructuredDocumentRegionsReplacedEvent;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegionList;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.core.text.ITextRegionList;


public class DTDFile implements IndexedRegion {

	protected ArrayList lists = new ArrayList();
	protected DTDModelImpl dtdModel;
	protected IStructuredDocument fStructuredDocument;

	public DTDFile(DTDModelImpl dtdModel) {
		this.dtdModel = dtdModel;
		this.fStructuredDocument = dtdModel.getStructuredDocument();
	}

	protected NodeList elementList = new NodeList(this, DTDRegionTypes.ELEMENT_TAG);
	protected NodeList notationList = new NodeList(this, DTDRegionTypes.NOTATION_TAG);
	protected NodeList entityList = new NodeList(this, DTDRegionTypes.ENTITY_TAG);
	protected NodeList commentList = new NodeList(this, DTDRegionTypes.COMMENT_START);
	protected NodeList unrecognizedList = new NodeList(this, DTDRegionTypes.UNKNOWN_CONTENT);
	protected NodeList attlistList = new NodeList(this, DTDRegionTypes.ATTLIST_TAG);

	public NodeList getElementsAndParameterEntityReferences() {
		return elementList;
	}

	public NodeList getNotations() {
		return notationList;
	}

	public NodeList getEntities() {
		return entityList;
	}

	public NodeList getComments() {
		return commentList;
	}

	public NodeList getUnrecognized() {
		return unrecognizedList;
	}

	protected ArrayList folderList = null;

	public ArrayList getNodeLists() {
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

	public DTDModelImpl getDTDModel() {
		return dtdModel;
	}



	public int getInsertOffset(DTDNode node, boolean isAfter) {
		int offset = 0;
		if (node != null) {
			if (isAfter) {
				// then get the next node and use it's start offset
				int index = getNodes().indexOf(getNode(node.getStructuredDocumentRegion()));

				DTDNode afterNode = null;
				if (index + 1 < getNodes().size()) {
					afterNode = (DTDNode) getNodes().get(index + 1);
				}
				if (afterNode != null) {
					offset = afterNode.getStructuredDocumentRegion().getStartOffset();
				}
				else {
					// add to end
					if (getStructuredDocument().getLastStructuredDocumentRegion() != null) {
						offset = getStructuredDocument().getLastStructuredDocumentRegion().getEndOffset();
					}
				}
			}
			else {
				offset = node.getStructuredDocumentRegion().getStartOffset();
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

	public void createElement(DTDNode node, String name, boolean isAfter) {
		getDTDModel().beginRecording(this, DTDPlugin.getDTDString("_UI_LABEL_DTD_FILE_ADD_ELEMENT")); //$NON-NLS-1$
		DTDNode topLevelNode = null;
		String newStream = "<!ELEMENT " + name + " EMPTY>\n"; //$NON-NLS-1$ //$NON-NLS-2$
		int offset = getInsertOffset(node, isAfter);
		getStructuredDocument().replaceText(this, offset, 0, newStream);
		getDTDModel().endRecording(this);
	}

	public void createEntity(DTDNode node, String name, boolean isAfter) {
		getDTDModel().beginRecording(this, DTDPlugin.getDTDString("_UI_LABEL_DTD_FILE_ADD_ENTITY")); //$NON-NLS-1$
		DTDNode topLevelNode = null;
		String newStream = "<!ENTITY " + name + " \"\">\n";  //$NON-NLS-1$//$NON-NLS-2$
		int offset = getInsertOffset(node, isAfter);
		getStructuredDocument().replaceText(this, offset, 0, newStream);
		getDTDModel().endRecording(this);
	}

	public void createComment(DTDNode node, String name, boolean isAfter) {
		getDTDModel().beginRecording(this, DTDPlugin.getDTDString("_UI_LABEL_DTD_FILE_ADD_COMMENT")); //$NON-NLS-1$
		DTDNode topLevelNode = null;
		String newStream = "<!-- " + name + " -->\n"; //$NON-NLS-1$ //$NON-NLS-2$
		int offset = getInsertOffset(node, isAfter);
		getStructuredDocument().replaceText(this, offset, 0, newStream);
		getDTDModel().endRecording(this);
	}

	public void createParameterEntityReference(DTDNode node, String name, boolean isAfter) {
		getDTDModel().beginRecording(this, DTDPlugin.getDTDString("_UI_LABEL_DTD_FILE_ADD_PARM_ENTITY_REF")); //$NON-NLS-1$
		DTDNode topLevelNode = null;
		String newStream = name + "\n"; //$NON-NLS-1$
		int offset = getInsertOffset(node, isAfter);
		getStructuredDocument().replaceText(this, offset, 0, newStream);
		getDTDModel().endRecording(this);
	}

	public void createNotation(DTDNode node, String name, boolean isAfter) {
		getDTDModel().beginRecording(this, DTDPlugin.getDTDString("_UI_LABEL_DTD_FILE_ADD_NOTATION")); //$NON-NLS-1$
		DTDNode topLevelNode = null;
		String newStream = "<!NOTATION " + name + " SYSTEM \"\">\n"; //$NON-NLS-1$ //$NON-NLS-2$
		int offset = getInsertOffset(node, isAfter);
		getStructuredDocument().replaceText(this, offset, 0, newStream);
		getDTDModel().endRecording(this);
	}

	public void createAttributeList(DTDNode node, String name, boolean isAfter) {
		getDTDModel().beginRecording(this, DTDPlugin.getDTDString("_UI_LABEL_DTD_FILE_ADD_ATTR_LIST")); //$NON-NLS-1$
		DTDNode topLevelNode = null;
		String newStream = "<!ATTLIST " + name + ">\n"; //$NON-NLS-1$ //$NON-NLS-2$
		int offset = getInsertOffset(node, isAfter);
		getStructuredDocument().replaceText(this, offset, 0, newStream);
		getDTDModel().endRecording(this);
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

	private boolean isMovingNode = false;

	public void moveNode(Object requestor, DTDNode referenceNode, DTDNode nodeToMove, boolean isAfter) {
		isMovingNode = true;

		deleteNode(requestor, nodeToMove);
		insertIntoModel(requestor, referenceNode, nodeToMove, isAfter);
		isMovingNode = false;
	}

	public void deleteNode(DTDNode node) {
		getDTDModel().beginRecording(this, DTDPlugin.getDTDString("_UI_LABEL_DTD_FILE_DELETE")); //$NON-NLS-1$
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
						// remove references to all elements and parm entities contained in our current model
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

			// no parent?  then delete up until the start of the next node
			// if it is a top level node
			int startOffset = node.getStartOffset();
			int endOffset = node.getWhitespaceEndOffset();
			if (node instanceof TopLevelNode) {
				endOffset = getInsertOffset(node, true);
			}
			getStructuredDocument().replaceText(requestor, startOffset, endOffset - startOffset, ""); //$NON-NLS-1$
		}
	}

	protected ArrayList nodeList = new ArrayList();

	protected void addNode(DTDNode node) {
		addNode(nodeList.size(), node);
	}

	protected void addNode(int index, DTDNode node) {
		nodeList.add(index, node);
		/*
		 if (index < nodeList.size()) 
		 {
		 insertBefore(node, (DTDNode) nodeList.get(index));
		 }
		 else 
		 {
		 appendChild(node);
		 }*/
	}

	protected void removeNodes(List nodes) {
		getNodes().removeAll(nodes);
		/*    for (int i = 0; i < nodes.size(); i++) 
		 {
		 removeChild((DTDNode)nodes.get(i));
		 } // end of for ()*/
	}

	public ArrayList getNodes() {
		return nodeList;
	}

	public IStructuredDocument getStructuredDocument() {
		return fStructuredDocument;
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

	public DTDNode getTopLevelNodeAt(int offset) {
		for (int i = 0; i < nodeList.size(); i++) {
			DTDNode node = (DTDNode) nodeList.get(i);
			if (node.contains(offset)) {
				return node;
			}
		}
		return null;
	}

	public DTDNode getNode(IStructuredDocumentRegion flatNode) {
		for (int i = 0; i < nodeList.size(); i++) {
			DTDNode node = (DTDNode) nodeList.get(i);
			if (node.getStructuredDocumentRegion() == flatNode) {
				return node;
			}
		}
		return null;
	}

	boolean creatingNewModel = false;

	public void newModel(NewModelEvent event) {
		creatingNewModel = true;
		nodeList.clear();
		NodesEvent removeEvent = new NodesEvent();
		removeEvent.getNodes().addAll(nodeList);
		notifyNodesRemoved(removeEvent);
		/*    removeChildNodes();*/

		if (fStructuredDocument.getRegionList() != null) {
			buildNodes(fStructuredDocument.getRegionList());
		}
		creatingNewModel = false;
	}

	public void buildNodes(IStructuredDocumentRegionList list) {
		NodesEvent addedDTDNodes = new NodesEvent();

		Enumeration flatNodes = list.elements();
		TopLevelNode previousNode = null;
		while (flatNodes.hasMoreElements()) {
			IStructuredDocumentRegion flatNode = (IStructuredDocumentRegion) flatNodes.nextElement();
			TopLevelNode node = (TopLevelNode) buildNode(flatNode);
			// if we don't create a node, then we assume that the flat
			// node was whitespace.  Tack it on to a previous toplevel
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
		//        creatingNewModel == false) 
		{
			// now tell people about the additions
			notifyNodesAdded(addedDTDNodes);
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
			//      System.out.println("rebuilding " + dtdNode.getStructuredDocumentRegion().getText());

			DTDNode node = buildNode(dtdNode.getStructuredDocumentRegion());
			if (node != null) {
				addedDTDNodes.add(node);
			}
		}
		if (addedDTDNodes.getNodes().size() > 0) {
			// now tell people about the additions
			notifyNodesAdded(addedDTDNodes);
		}
	}

	public DTDNode buildNode(IStructuredDocumentRegion flatNode) {
		//    ITextRegionList regions = flatNode.getRegions();
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
	public boolean isParameterEntityReference(IStructuredDocumentRegion flatNode) {
		if (flatNode.getRegions().size() == 1) {
			ITextRegion region = flatNode.getRegions().get(0);
			if (region.getType().equals(DTDRegionTypes.ENTITY_PARM)) {
				return true;
			}
		}
		return false;
	}

	public boolean isUnrecognized(IStructuredDocumentRegion flatNode) {
		return !isElement(flatNode) && !isEntity(flatNode) && !isNotation(flatNode) && !isParameterEntityReference(flatNode) && !isAttributeList(flatNode) && !isComment(flatNode);
	}


	public void insertNode(DTDNode node) {
		int startOffset = node.getStartOffset();
		int insertIndex = -1;
		//    System.out.println("startoffset = " + startOffset);
		for (int i = 0; i < getNodes().size(); i++) {
			DTDNode currentNode = (DTDNode) getNodes().get(i);
			//      System.out.println("currentNode endOffset = " +currentNode.getEndOffset());

			if (currentNode.getEndOffset() > startOffset) {
				//        System.out.println("endoffset " + currentNode.getEndOffset() + " > " + startOffset);
				insertIndex = i;
				break;
			}
		}
		if (insertIndex == -1) {
			insertIndex = getNodes().size();
		}


		//    System.out.println("insert index = " + insertIndex);

		addNode(insertIndex, node);
	}

	public void nodesReplaced(StructuredDocumentRegionsReplacedEvent event) {
		IStructuredDocumentRegionList oldNodes = event.getOldStructuredDocumentRegions();
		NodesEvent removedDTDNodes = new NodesEvent();
		for (int i = 0; i < oldNodes.getLength(); i++) {
			IStructuredDocumentRegion flatNode = oldNodes.item(i);

			for (Iterator iter = getNodes().iterator(); iter.hasNext();) {
				DTDNode node = (DTDNode) iter.next();
				if (node.getStructuredDocumentRegion() == flatNode) {
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

	public void regionsReplaced(RegionsReplacedEvent event) {
		List nodesToRebuild = new ArrayList();
		IStructuredDocumentRegion flatNode = event.getStructuredDocumentRegion();
		DTDNode affectedNode = getNode(flatNode);

		if (!isSameTopLevelType(affectedNode)) {
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
				if (!addedDTDNodes.getNodes().contains(deepestNode)) {
					addedDTDNodes.add(deepestNode);
				}
			}
			if (addedDTDNodes.getNodes().size() > 0) {
				notifyNodesAdded(addedDTDNodes);
			}
		}
	}

	public void regionChanged(RegionChangedEvent event) {
		ITextRegion changedRegion = event.getRegion();
		IStructuredDocumentRegion flatNode = event.getStructuredDocumentRegion();
		DTDNode affectedNode = (DTDNode) getNodeAt(flatNode.getStartOffset(changedRegion), flatNode.getEndOffset(changedRegion));
		if (affectedNode != null) {
			// no need to resolve regions as it is just a change
			//      affectedNode.resolveRegions();
			notifyNodeChanged(affectedNode);
		}
	}

	boolean isSameTopLevelType(DTDNode affectedNode) {
		IStructuredDocumentRegion flatNode = affectedNode.getStructuredDocumentRegion();
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

	public Image getImage() {
		return DTDPlugin.getInstance().getImage(DTDResource.DTDFILEICON);
	}

	public String getName() {
		org.eclipse.core.runtime.Path path = new org.eclipse.core.runtime.Path(getDTDModel().getId().toString());
		return path.lastSegment();
	}

	protected ArrayList modelListeners = new ArrayList();

	public void addDTDFileListener(IDTDFileListener listener) {
		modelListeners.add(listener);
	}

	public void removeDTDFileListener(IDTDFileListener listener) {
		modelListeners.remove(listener);
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

	public void notifyNodeChanged(DTDNode node) {
		Iterator iter = modelListeners.iterator();
		while (iter.hasNext()) {
			IDTDFileListener listener = (IDTDFileListener) iter.next();
			listener.nodeChanged(node);
		}
	}

	/**
	 * Sets the flatModel.
	 * @param flatModel The flatModel to set
	 */
	public void setStructuredDocument(IStructuredDocument flatModel) {
		this.fStructuredDocument = flatModel;
	}

	// Implements IndexedRegion

	public boolean contains(int testPosition) {
		return getStartOffset() <= testPosition && testPosition <= getEndOffset();
	}

	public int getEndOffset() {
		return getStructuredDocument().getFirstStructuredDocumentRegion().getEndOffset();
	}

	public int getStartOffset() {
		return getStructuredDocument().getFirstStructuredDocumentRegion().getStartOffset();
	}
}
