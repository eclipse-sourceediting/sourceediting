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


package org.eclipse.wst.dtd.ui.internal.views.contentoutline;

import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;
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
import org.eclipse.wst.sse.ui.internal.StructuredTextEditor;


public class DTDContextMenuHelper // extends FocusAdapter
{

	class DTDMenuListener implements IMenuListener {
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.action.IMenuListener#menuAboutToShow(org.eclipse.jface.action.IMenuManager)
		 */
		public void menuAboutToShow(IMenuManager manager) {
			updateActions();
			List selection = DTDContextMenuHelper.this.fEditor.getSelectedNodes();
			if (selection != null && selection.size() == 1)
				addActionItemsForSelection(selection.get(0), manager);
		}
	}

	private AddAttributeAction addAttributeAction;
	private AddAttributeListAction addAttributeListAction;
	private AddCommentAction addCommentAction;
	private AddElementAction addElementAction;
	private AddElementToContentModelAction addElementToContentModelAction;
	private AddEntityAction addEntityAction;
	private AddGroupToContentModelAction addGroupToContentModelAction;
	private AddNotationAction addNotationAction;
	private AddParameterEntityReferenceAction addParameterEntityReferenceAction;
	private DeleteAction deleteAction;

	// default access, for inner class
	StructuredTextEditor fEditor;

	private IMenuListener fMenuListener;
	private IAction redoAction;

	// protected CutAction cutAction;
	// protected CopyAction copyAction;
	// protected PasteAction pasteAction;
	private IAction undoAction;

	// private List viewerList = new Vector();

	public DTDContextMenuHelper(StructuredTextEditor editor) {
		this.fEditor = editor;
		fMenuListener = new DTDMenuListener();
		addNotationAction = new AddNotationAction(editor, DTDUIMessages._UI_ACTION_ADD_DTD_NOTATION); //$NON-NLS-1$
		addEntityAction = new AddEntityAction(editor, DTDUIMessages._UI_ACTION_ADD_DTD_ENTITY); //$NON-NLS-1$
		addElementAction = new AddElementAction(editor, DTDUIMessages._UI_ACTION_ADD_DTD_ELEMENT); //$NON-NLS-1$
		addCommentAction = new AddCommentAction(editor, DTDUIMessages._UI_ACTION_ADD_DTD_COMMENT); //$NON-NLS-1$

		addParameterEntityReferenceAction = new AddParameterEntityReferenceAction(editor, DTDUIMessages._UI_ACTION_ADD_PARAM_ENTITY_REF); //$NON-NLS-1$
		deleteAction = new DeleteAction(DTDUIMessages._UI_ACTION_DTD_DELETE); //$NON-NLS-1$
		addAttributeAction = new AddAttributeAction(editor, DTDUIMessages._UI_ACTION_ADD_ATTRIBUTE); //$NON-NLS-1$
		addAttributeListAction = new AddAttributeListAction(editor, DTDUIMessages._UI_ACTION_ADD_ATTRIBUTELIST); //$NON-NLS-1$

		addGroupToContentModelAction = new AddGroupToContentModelAction(editor, DTDUIMessages._UI_ACTION_GROUP_ADD_GROUP); //$NON-NLS-1$
		addElementToContentModelAction = new AddElementToContentModelAction(editor, DTDUIMessages._UI_ACTION_ADD_ELEMENT); //$NON-NLS-1$

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
	}

	public void addActionItemsForSelection(Object selectedObject, IMenuManager menu) {
		if (undoAction == null) {
			undoAction = fEditor.getAction(ActionFactory.UNDO.getId());
			redoAction = fEditor.getAction(ActionFactory.REDO.getId());
		}

		menu.add(undoAction);
		menu.add(redoAction);
		menu.add(new Separator());

		if (selectedObject instanceof IndexedNodeList) {
			// http://w3.opensource.ibm.com/bugzilla/show_bug.cgi?id=4201
			// add appropriate menu to logical view
			NodeList folder = ((IndexedNodeList) selectedObject).getTarget();
			if (folder.getListType().equals(DTDRegionTypes.NOTATION_TAG)) {
				menu.add(addNotationAction);
			}
			else if (folder.getListType().equals(DTDRegionTypes.ENTITY_TAG)) {
				menu.add(addEntityAction);
			}
			else if (folder.getListType().equals(DTDRegionTypes.ELEMENT_TAG)) {
				LabelValuePair[] availableEntities = ((DTDModelImpl) fEditor.getModel()).createParmEntityContentItems(null);
				addParameterEntityReferenceAction.setEnabled(availableEntities.length > 0);

				menu.add(addElementAction);
				menu.add(addParameterEntityReferenceAction);
			}
		}
		if (selectedObject instanceof DTDFile || selectedObject == null) {
			LabelValuePair[] availableEntities = ((DTDModelImpl) fEditor.getModel()).createParmEntityContentItems(null);
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

			if (dtdElement.getContentModel() == null) {
				menu.add(addGroupToContentModelAction);
				menu.add(addElementToContentModelAction);
			}
			// if (!(((Element)selectedObject).getContentModel() instanceof
			// CMGroupNode))
			// {
			// menu.add(addGroupToContentModelAction);
			// }
			// addAttributeAction.setElement(selectedObject);
			menu.add(addAttributeAction);
		}
		else if (selectedObject instanceof CMGroupNode) {
			// addElementToContentModelAction.setElement(selectedObject);
			// addGroupToContentModelAction.setElement(selectedObject);
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
			// if (selectedObject instanceof DTDElementContent)
			// {
			// DTDElementContent content = (DTDElementContent) selectedObject;
			// if (content.getElement() != null && (content instanceof
			// DTDPCDataContent || content instanceof DTDEmptyContent))
			// {
			// deleteAction.setEnabled(false);
			// } // end of if ()
			// } // end of if ()
		}
	}

	public void addEditActions(IMenuManager menu) {
		// menu.add(undoAction);
		// menu.add(redoAction);
		// menu.add(new Separator());
		// menu.add(cutAction);
		// menu.add(copyAction);
		// menu.add(pasteAction);
	}

	public void createMenuListenersFor(Viewer viewer) {
		viewer.addSelectionChangedListener(addNotationAction);
		viewer.addSelectionChangedListener(addEntityAction);
		viewer.addSelectionChangedListener(addElementAction);
		viewer.addSelectionChangedListener(addCommentAction);
		viewer.addSelectionChangedListener(addParameterEntityReferenceAction);
		viewer.addSelectionChangedListener(deleteAction);
		viewer.addSelectionChangedListener(addAttributeAction);
		viewer.addSelectionChangedListener(addAttributeListAction);
		viewer.addSelectionChangedListener(addGroupToContentModelAction);
		viewer.addSelectionChangedListener(addElementToContentModelAction);

		IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
		addNotationAction.selectionChanged(selection);
		addEntityAction.selectionChanged(selection);
		addElementAction.selectionChanged(selection);
		addParameterEntityReferenceAction.selectionChanged(selection);
		deleteAction.selectionChanged(selection);
		addAttributeAction.selectionChanged(selection);
		addAttributeListAction.selectionChanged(selection);
		addGroupToContentModelAction.selectionChanged(selection);
		addElementToContentModelAction.selectionChanged(selection);

		// viewer.addSelectionChangedListener(cutAction);
		// viewer.addSelectionChangedListener(copyAction);
		// viewer.addSelectionChangedListener(pasteAction);

		// viewerList.add(viewer);
		// viewer.getControl().addFocusListener(this);
	}

	public DeleteAction getDeleteAction() {
		return deleteAction;
	}

	/**
	 * @return Returns the menuListener.
	 */
	public IMenuListener getMenuListener() {
		return fMenuListener;
	}

	// public void focusGained(FocusEvent event)
	// {
	// updateSelection();
	// }

	public void removeMenuListenersFor(Viewer viewer) {
		viewer.removeSelectionChangedListener(addNotationAction);
		viewer.removeSelectionChangedListener(addEntityAction);
		viewer.removeSelectionChangedListener(addElementAction);
		viewer.removeSelectionChangedListener(addCommentAction);
		viewer.removeSelectionChangedListener(addParameterEntityReferenceAction);
		viewer.removeSelectionChangedListener(deleteAction);
		viewer.removeSelectionChangedListener(addAttributeAction);
		viewer.removeSelectionChangedListener(addAttributeListAction);
		viewer.removeSelectionChangedListener(addGroupToContentModelAction);
		viewer.removeSelectionChangedListener(addElementToContentModelAction);
	}

	// Update all the actions for the viewer in focus
	public void updateActions() {
		// undoAction.update();
		// redoAction.update();
	}

	public void updateEditActions(IActionBars actionBars) {
		// if (actionBars != null)
		// {
		// actionBars.setGlobalActionHandler(IWorkbenchActionConstants.CUT,
		// cutAction);
		// actionBars.setGlobalActionHandler(IWorkbenchActionConstants.COPY,
		// copyAction);
		// actionBars.setGlobalActionHandler(IWorkbenchActionConstants.PASTE,
		// pasteAction);
		// actionBars.setGlobalActionHandler(IWorkbenchActionConstants.UNDO,
		// undoAction);
		// actionBars.setGlobalActionHandler(IWorkbenchActionConstants.REDO,
		// redoAction);
		// }
	}
}
