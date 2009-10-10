/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     Naoki Akiyama (Fujitsu) - bug 187172 - fix attribute grouping
 *     
 *******************************************************************************/

package org.eclipse.wst.dtd.ui.views.contentoutline;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.wst.dtd.core.internal.AttributeList;
import org.eclipse.wst.dtd.core.internal.CMGroupNode;
import org.eclipse.wst.dtd.core.internal.CMNode;
import org.eclipse.wst.dtd.core.internal.DTDFile;
import org.eclipse.wst.dtd.core.internal.DTDNode;
import org.eclipse.wst.dtd.core.internal.Element;
import org.eclipse.wst.dtd.core.internal.NodeList;
import org.eclipse.wst.dtd.core.internal.document.DTDModelImpl;
import org.eclipse.wst.dtd.core.internal.parser.DTDRegionTypes;
import org.eclipse.wst.dtd.core.internal.util.LabelValuePair;
import org.eclipse.wst.dtd.ui.internal.DTDUIMessages;
import org.eclipse.wst.dtd.ui.internal.editor.DTDEditorPluginImageHelper;
import org.eclipse.wst.dtd.ui.internal.editor.DTDEditorPluginImages;
import org.eclipse.wst.dtd.ui.internal.views.contentoutline.actions.AddAttributeAction;
import org.eclipse.wst.dtd.ui.internal.views.contentoutline.actions.AddAttributeListAction;
import org.eclipse.wst.dtd.ui.internal.views.contentoutline.actions.AddCommentAction;
import org.eclipse.wst.dtd.ui.internal.views.contentoutline.actions.AddElementAction;
import org.eclipse.wst.dtd.ui.internal.views.contentoutline.actions.AddElementToContentModelAction;
import org.eclipse.wst.dtd.ui.internal.views.contentoutline.actions.AddEntityAction;
import org.eclipse.wst.dtd.ui.internal.views.contentoutline.actions.AddGroupToContentModelAction;
import org.eclipse.wst.dtd.ui.internal.views.contentoutline.actions.AddNotationAction;
import org.eclipse.wst.dtd.ui.internal.views.contentoutline.actions.AddParameterEntityReferenceAction;
import org.eclipse.wst.dtd.ui.internal.views.contentoutline.actions.DeleteAction;
import org.eclipse.wst.dtd.ui.internal.views.contentoutline.actions.ReplaceEmptyContentModelWithGroupAction;

/**
 * Menu helper for Content Outline page. This should not be used elsewhere.
 */
class DTDContextMenuHelper {
	class DTDMenuListener implements IMenuListener {
		public void menuAboutToShow(IMenuManager manager) {
			// update the action selection now
			addNotationAction.selectionChanged(fViewerSelection);
			addEntityAction.selectionChanged(fViewerSelection);
			addElementAction.selectionChanged(fViewerSelection);
			addCommentAction.selectionChanged(fViewerSelection);
			addParameterEntityReferenceAction.selectionChanged(fViewerSelection);
			deleteAction.selectionChanged(fViewerSelection);
			addAttributeAction.selectionChanged(fViewerSelection);
			addAttributeListAction.selectionChanged(fViewerSelection);
			addGroupToContentModelAction.selectionChanged(fViewerSelection);
			addElementToContentModelAction.selectionChanged(fViewerSelection);
			replaceEmptyContentModelWithGroupAction.selectionChanged(fViewerSelection);


			if (!fViewerSelection.isEmpty()) {
				addActionItemsForSelection(fViewerSelection.getFirstElement(), manager);
			}
		}
	}

	class ViewerSelectionChangeListener implements ISelectionChangedListener {
		public void selectionChanged(SelectionChangedEvent event) {
			_selectionChanged(event);
		}
	}

	AddAttributeAction addAttributeAction;

	AddAttributeListAction addAttributeListAction;
	AddCommentAction addCommentAction;
	AddElementAction addElementAction;
	AddElementToContentModelAction addElementToContentModelAction;
	AddEntityAction addEntityAction;
	AddGroupToContentModelAction addGroupToContentModelAction;
	AddNotationAction addNotationAction;
	AddParameterEntityReferenceAction addParameterEntityReferenceAction;
	DeleteAction deleteAction;
	private ISelectionChangedListener fInternalSelectionChangedListener = new ViewerSelectionChangeListener();

	private IMenuListener fMenuListener;
	private DTDModelImpl fModel;
	private List fViewerList;

	IStructuredSelection fViewerSelection = StructuredSelection.EMPTY;

	ReplaceEmptyContentModelWithGroupAction replaceEmptyContentModelWithGroupAction;

	public DTDContextMenuHelper(DTDModelImpl model) {
		fModel = model;
		fViewerList = new ArrayList(1);
		fMenuListener = new DTDMenuListener();

		addNotationAction = new AddNotationAction(model, DTDUIMessages._UI_ACTION_ADD_DTD_NOTATION); //$NON-NLS-1$
		addEntityAction = new AddEntityAction(model, DTDUIMessages._UI_ACTION_ADD_DTD_ENTITY); //$NON-NLS-1$
		addElementAction = new AddElementAction(model, DTDUIMessages._UI_ACTION_ADD_DTD_ELEMENT); //$NON-NLS-1$
		addCommentAction = new AddCommentAction(model, DTDUIMessages._UI_ACTION_ADD_DTD_COMMENT); //$NON-NLS-1$

		addParameterEntityReferenceAction = new AddParameterEntityReferenceAction(model, DTDUIMessages._UI_ACTION_ADD_PARAM_ENTITY_REF); //$NON-NLS-1$
		deleteAction = new DeleteAction(DTDUIMessages._UI_ACTION_DTD_DELETE); //$NON-NLS-1$
		addAttributeAction = new AddAttributeAction(model, DTDUIMessages._UI_ACTION_ADD_ATTRIBUTE); //$NON-NLS-1$
		addAttributeListAction = new AddAttributeListAction(model, DTDUIMessages._UI_ACTION_ADD_ATTRIBUTELIST); //$NON-NLS-1$

		addGroupToContentModelAction = new AddGroupToContentModelAction(model, DTDUIMessages._UI_ACTION_GROUP_ADD_GROUP); //$NON-NLS-1$
		addElementToContentModelAction = new AddElementToContentModelAction(model, DTDUIMessages._UI_ACTION_ADD_ELEMENT); //$NON-NLS-1$

		replaceEmptyContentModelWithGroupAction = new ReplaceEmptyContentModelWithGroupAction(model, DTDUIMessages._UI_ACTION_GROUP_ADD_GROUP); //$NON-NLS-1$

		addNotationAction.setImageDescriptor(DTDEditorPluginImageHelper.getInstance().getImageDescriptor(DTDEditorPluginImages.IMG_OBJ_ADD_NOTATION));
		addEntityAction.setImageDescriptor(DTDEditorPluginImageHelper.getInstance().getImageDescriptor(DTDEditorPluginImages.IMG_OBJ_ADD_ENTITY));
		addCommentAction.setImageDescriptor(DTDEditorPluginImageHelper.getInstance().getImageDescriptor(DTDEditorPluginImages.IMG_OBJ_ADD_COMMENT));
		addParameterEntityReferenceAction.setImageDescriptor(DTDEditorPluginImageHelper.getInstance().getImageDescriptor(DTDEditorPluginImages.IMG_OBJ_ADD_ENTITY_REFERENCE));

		// Tri-state images
		addElementAction.setImageDescriptor(DTDEditorPluginImageHelper.getInstance().getImageDescriptor(DTDEditorPluginImages.IMG_ETOOL_ADD_ELEMENT));
		addElementAction.setHoverImageDescriptor(DTDEditorPluginImageHelper.getInstance().getImageDescriptor(DTDEditorPluginImages.IMG_CTOOL_ADD_ELEMENT));
		addElementAction.setDisabledImageDescriptor(DTDEditorPluginImageHelper.getInstance().getImageDescriptor(DTDEditorPluginImages.IMG_DTOOL_ADD_ELEMENT));

		addAttributeAction.setImageDescriptor(DTDEditorPluginImageHelper.getInstance().getImageDescriptor(DTDEditorPluginImages.IMG_ETOOL_ADD_ATTRIBUTE));
		addAttributeAction.setHoverImageDescriptor(DTDEditorPluginImageHelper.getInstance().getImageDescriptor(DTDEditorPluginImages.IMG_CTOOL_ADD_ATTRIBUTE));
		addAttributeAction.setDisabledImageDescriptor(DTDEditorPluginImageHelper.getInstance().getImageDescriptor(DTDEditorPluginImages.IMG_DTOOL_ADD_ATTRIBUTE));

		addAttributeListAction.setImageDescriptor(DTDEditorPluginImageHelper.getInstance().getImageDescriptor(DTDEditorPluginImages.IMG_ETOOL_ADD_ATTRIBUTE));
		addAttributeListAction.setHoverImageDescriptor(DTDEditorPluginImageHelper.getInstance().getImageDescriptor(DTDEditorPluginImages.IMG_CTOOL_ADD_ATTRIBUTE));
		addAttributeListAction.setDisabledImageDescriptor(DTDEditorPluginImageHelper.getInstance().getImageDescriptor(DTDEditorPluginImages.IMG_DTOOL_ADD_ATTRIBUTE));

		addGroupToContentModelAction.setImageDescriptor(DTDEditorPluginImageHelper.getInstance().getImageDescriptor(DTDEditorPluginImages.IMG_ETOOL_ADD_GROUPTOCONMODEL));
		addGroupToContentModelAction.setHoverImageDescriptor(DTDEditorPluginImageHelper.getInstance().getImageDescriptor(DTDEditorPluginImages.IMG_CTOOL_ADD_GROUPTOCONMODEL));
		addGroupToContentModelAction.setDisabledImageDescriptor(DTDEditorPluginImageHelper.getInstance().getImageDescriptor(DTDEditorPluginImages.IMG_DTOOL_ADD_GROUPTOCONMODEL));

		addElementToContentModelAction.setImageDescriptor(DTDEditorPluginImageHelper.getInstance().getImageDescriptor(DTDEditorPluginImages.IMG_ETOOL_ADD_ELEMENTTOCONMODEL));
		addElementToContentModelAction.setHoverImageDescriptor(DTDEditorPluginImageHelper.getInstance().getImageDescriptor(DTDEditorPluginImages.IMG_CTOOL_ADD_ELEMENTTOCONMODEL));
		addElementToContentModelAction.setDisabledImageDescriptor(DTDEditorPluginImageHelper.getInstance().getImageDescriptor(DTDEditorPluginImages.IMG_DTOOL_ADD_ELEMENTTOCONMODEL));

		// use the same images as addGroupToContentModelAction
		replaceEmptyContentModelWithGroupAction.setImageDescriptor(DTDEditorPluginImageHelper.getInstance().getImageDescriptor(DTDEditorPluginImages.IMG_ETOOL_ADD_GROUPTOCONMODEL));
		replaceEmptyContentModelWithGroupAction.setHoverImageDescriptor(DTDEditorPluginImageHelper.getInstance().getImageDescriptor(DTDEditorPluginImages.IMG_CTOOL_ADD_GROUPTOCONMODEL));
		replaceEmptyContentModelWithGroupAction.setDisabledImageDescriptor(DTDEditorPluginImageHelper.getInstance().getImageDescriptor(DTDEditorPluginImages.IMG_DTOOL_ADD_GROUPTOCONMODEL));
	}

	void _selectionChanged(SelectionChangedEvent event) {
		/*
		 * Save the selection so we only notify the actions when the menu is
		 * shown
		 */
		ISelection selection = event.getSelection();
		if (selection instanceof IStructuredSelection) {
			fViewerSelection = (IStructuredSelection) selection;
		}
		else {
			fViewerSelection = StructuredSelection.EMPTY;
		}
	}

	void addActionItemsForSelection(Object selectedObject, IMenuManager menu) {
		if (selectedObject instanceof NodeList) {
			// add appropriate menu to logical view
			NodeList folder = (NodeList) selectedObject;
			if (folder.getListType().equals(DTDRegionTypes.NOTATION_TAG)) {
				menu.add(addNotationAction);
			}
			else if (folder.getListType().equals(DTDRegionTypes.ENTITY_TAG)) {
				menu.add(addEntityAction);
			}
			else if (folder.getListType().equals(DTDRegionTypes.ELEMENT_TAG)) {
				LabelValuePair[] availableEntities = fModel.createParmEntityContentItems(null);
				addParameterEntityReferenceAction.setEnabled(availableEntities.length > 0);

				menu.add(addElementAction);
				menu.add(addParameterEntityReferenceAction);
			}
			else if (folder.getListType().equals(DTDRegionTypes.ATTLIST_TAG)) {
				menu.add(addAttributeListAction);
			}
		}
		if (selectedObject instanceof DTDFile || selectedObject == null) {
			LabelValuePair[] availableEntities = fModel.createParmEntityContentItems(null);
			addParameterEntityReferenceAction.setEnabled(availableEntities.length > 0);

			menu.add(addElementAction);
			menu.add(addEntityAction);
			menu.add(addNotationAction);
			menu.add(addParameterEntityReferenceAction);
			menu.add(addCommentAction);
			menu.add(addAttributeListAction);
			menu.add(new Separator());
		}

		if (selectedObject instanceof Element) {
			Element dtdElement = (Element) selectedObject;

			CMNode contentModel = dtdElement.getContentModel();
			if (contentModel == null) {
				menu.add(addGroupToContentModelAction);
				menu.add(addElementToContentModelAction);
			}
			else if (contentModel != null && CMNode.EMPTY.equals(contentModel.getType())) {
				menu.add(replaceEmptyContentModelWithGroupAction);
			}
			menu.add(addAttributeAction);
		}
		else if (selectedObject instanceof CMGroupNode) {
			menu.add(addElementToContentModelAction);
			menu.add(addGroupToContentModelAction);
		}
		else if (selectedObject instanceof AttributeList) {
			menu.add(addAttributeAction);
		}

		menu.add(new Separator());
		addEditActions(menu);
		menu.add(new Separator());

		if (selectedObject instanceof DTDNode && !(selectedObject instanceof CMNode && ((CMNode) selectedObject).isRootElementContent())) {
			menu.add(deleteAction);
			deleteAction.setEnabled(true);
		}
	}

	void addEditActions(IMenuManager menu) {
		// menu.add(undoAction);
		// menu.add(redoAction);
		// menu.add(new Separator());
		// menu.add(cutAction);
		// menu.add(copyAction);
		// menu.add(pasteAction);
	}

	public void createMenuListenersFor(Viewer viewer) {
		viewer.addSelectionChangedListener(fInternalSelectionChangedListener);
		ISelection selection = viewer.getSelection();
		if (selection instanceof IStructuredSelection) {
			fViewerSelection = (IStructuredSelection) selection;
		}
		else {
			fViewerSelection = StructuredSelection.EMPTY;
		}

		fViewerList.add(viewer);
	}

	public IMenuListener getMenuListener() {
		return fMenuListener;
	}

	public void removeMenuListenersFor(Viewer viewer) {
		viewer.removeSelectionChangedListener(fInternalSelectionChangedListener);
		fViewerList.remove(viewer);
	}
}
