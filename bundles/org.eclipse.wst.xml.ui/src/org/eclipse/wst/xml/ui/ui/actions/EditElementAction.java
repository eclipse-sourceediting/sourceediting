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
package org.eclipse.wst.xml.ui.ui.actions;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.xml.core.document.XMLElement;
import org.eclipse.wst.xml.core.document.XMLModel;
import org.eclipse.wst.xml.ui.internal.editor.XMLEditorPluginImageHelper;
import org.eclipse.wst.xml.ui.internal.editor.XMLEditorPluginImages;
import org.eclipse.wst.xml.ui.ui.XMLCommonResources;
import org.eclipse.wst.xml.ui.ui.dialogs.EditElementDialog;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



public class EditElementAction extends NodeAction {
	protected AbstractNodeActionManager manager;
	protected String title;
	protected Node parent;
	protected Element element;
	protected int insertionIndex = -1;

	protected static ImageDescriptor imageDescriptor;

	public static ImageDescriptor createImageDescriptor() {
		if (imageDescriptor == null) {
			imageDescriptor = XMLEditorPluginImageHelper.getInstance().getImageDescriptor(XMLEditorPluginImages.IMG_OBJ_ELEMENT);
		}
		return imageDescriptor;
	}

	protected EditElementAction(AbstractNodeActionManager manager, Node parent, int index, Element element, String actionLabel, String title) {
		this.manager = manager;
		this.parent = parent;
		this.insertionIndex = index;
		this.element = element;
		this.title = title;
		setText(actionLabel);
		if (element == null) {
			setImageDescriptor(createImageDescriptor());
		}
	}

	public EditElementAction(AbstractNodeActionManager manager, Node parent, int index, String actionLabel, String title) {
		this(manager, parent, index, null, actionLabel, title);
	}

	public EditElementAction(AbstractNodeActionManager manager, Element element, String actionLabel, String dialogTitle) {
		this(manager, element.getParentNode(), -1, element, actionLabel, dialogTitle);
	}

	public String getUndoDescription() {
		return title;
	}

	protected void setStructuredDocumentRegionElementName(XMLModel model, IStructuredDocumentRegion flatNode, String oldName, String newName) {
		if (flatNode != null) {
			String string = flatNode.getText();
			int index = string.indexOf(oldName);
			if (index != -1) {
				index += flatNode.getStart();
				model.getStructuredDocument().replaceText(this, index, oldName.length(), newName);
			}
		}
	}

	public void run() {
		manager.beginNodeAction(this);
		Shell shell = XMLCommonResources.getInstance().getWorkbench().getActiveWorkbenchWindow().getShell();
		EditElementDialog dialog = new EditElementDialog(shell, element);
		dialog.create();
		dialog.getShell().setText(title);
		dialog.setBlockOnOpen(true);
		dialog.open();

		if (dialog.getReturnCode() == Window.OK) {
			Document document = parent.getNodeType() == Node.DOCUMENT_NODE ? (Document) parent : parent.getOwnerDocument();
			if (element != null) {
				// here we need to do a rename... which seems to be quite hard to do :-(
				if (element instanceof XMLElement) {
					XMLElement elementImpl = (XMLElement) element;
					XMLModel model = elementImpl.getModel();
					String oldName = elementImpl.getNodeName();
					String newName = dialog.getElementName();
					setStructuredDocumentRegionElementName(model, elementImpl.getStartStructuredDocumentRegion(), oldName, newName);
					setStructuredDocumentRegionElementName(model, elementImpl.getEndStructuredDocumentRegion(), oldName, newName);
				}
			}
			else {
				Element newElement = document.createElement(dialog.getElementName());
				NodeList nodeList = parent.getChildNodes();
				int nodeListLength = nodeList.getLength();
				Node refChild = insertionIndex < nodeListLength && insertionIndex >= 0 ? nodeList.item(insertionIndex) : null;
				parent.insertBefore(newElement, refChild);
				manager.reformat(newElement, false);
				manager.setViewerSelection(newElement);
			}
		}
		manager.endNodeAction(this);
	}
}

