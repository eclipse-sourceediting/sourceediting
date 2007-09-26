/*****************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and
 * is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ****************************************************************************/
package org.eclipse.wst.xml.ui.internal.tabletree;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xml.ui.internal.actions.NodeAction;
import org.eclipse.wst.xml.ui.internal.contentoutline.XMLNodeActionManager;
import org.eclipse.wst.xml.ui.internal.dnd.DragNodeCommand;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class XMLTableTreeViewer extends TreeViewer implements IDesignViewer {

	class NodeActionMenuListener implements IMenuListener {
		public void menuAboutToShow(IMenuManager menuManager) {
			// used to disable NodeSelection listening while running
			// NodeAction
			XMLNodeActionManager nodeActionManager = new XMLNodeActionManager(((IDOMDocument) getInput()).getModel(), XMLTableTreeViewer.this) {
				public void beginNodeAction(NodeAction action) {
					super.beginNodeAction(action);
				}

				public void endNodeAction(NodeAction action) {
					super.endNodeAction(action);
				}
			};
			nodeActionManager.fillContextMenu(menuManager, getSelection());
		}
	}
	
	private class SelectionProvider implements IPostSelectionProvider {

		public void addPostSelectionChangedListener(ISelectionChangedListener listener) {
			XMLTableTreeViewer.this.addPostSelectionChangedListener(listener);
		}

		public void removePostSelectionChangedListener(ISelectionChangedListener listener) {
			XMLTableTreeViewer.this.removePostSelectionChangedListener(listener);
		}

		public void addSelectionChangedListener(ISelectionChangedListener listener) {
			XMLTableTreeViewer.this.addSelectionChangedListener(listener);
		}

		public ISelection getSelection() {
			return XMLTableTreeViewer.this.getSelection();
		}

		public void removeSelectionChangedListener(ISelectionChangedListener listener) {
			XMLTableTreeViewer.this.removeSelectionChangedListener(listener);
		}

		public void setSelection(ISelection selection) {
			boolean selectionSet = false;
			if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
				IStructuredSelection structuredSelection = (IStructuredSelection) selection;
				if (selection instanceof ITextSelection) {
					ITextSelection textSelection = (ITextSelection) selection;
					
					if (structuredSelection.size() == 1) {
						if (structuredSelection.getFirstElement() instanceof IDOMNode) {
							IDOMNode domNode = (IDOMNode) structuredSelection.getFirstElement();
							IStructuredDocumentRegion startStructuredDocumentRegion = domNode.getStartStructuredDocumentRegion();
							if (startStructuredDocumentRegion != null) {
								ITextRegion matchingRegion = startStructuredDocumentRegion.getRegionAtCharacterOffset(textSelection.getOffset());
								int allowedIterations = 40;
								while (matchingRegion != null && !matchingRegion.getType().equals(DOMRegionContext.XML_TAG_ATTRIBUTE_NAME) && allowedIterations > 0) {
									matchingRegion = startStructuredDocumentRegion.getRegionAtCharacterOffset(startStructuredDocumentRegion.getStartOffset(matchingRegion) - 1);
									allowedIterations--;
								}
								if (matchingRegion != null && matchingRegion.getType().equals(DOMRegionContext.XML_TAG_ATTRIBUTE_NAME)) {
									String attrName = startStructuredDocumentRegion.getText(matchingRegion);
									NamedNodeMap attributes = domNode.getAttributes();
									if (attributes != null && attrName.length() > 0) {
										Node attr = attributes.getNamedItem(attrName);
										if (attr != null) {
											selectionSet = true;
											XMLTableTreeViewer.this.setSelection(new StructuredSelection(attr));
										}
									}
								}
							}
						}
					}
				}
			}
			if (!selectionSet) {
				XMLTableTreeViewer.this.setSelection(selection);
			}
		}
	}

	protected CellEditor cellEditor;

	protected XMLTreeExtension treeExtension;
	
	private ISelectionProvider fSelectionProvider = new SelectionProvider();

	public XMLTableTreeViewer(Composite parent) {
		super(parent, SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

		// set up providers
		this.treeExtension = new XMLTreeExtension(getTree());

		XMLTableTreeContentProvider provider = new XMLTableTreeContentProvider();
		setContentProvider(provider);
		setLabelProvider(provider);

		createContextMenu();

		DragSource dragSource = new DragSource(getControl(), DND.DROP_COPY | DND.DROP_MOVE);
		dragSource.addDragListener(createDragSourceListener());
		dragSource.setTransfer(new Transfer[] {LocalSelectionTransfer.getTransfer()});
		DropTarget dropTarget = new DropTarget(getControl(), DND.DROP_COPY | DND.DROP_MOVE);
		dropTarget.addDropListener(createDropTargetListener());
		dropTarget.setTransfer(new Transfer[] {LocalSelectionTransfer.getTransfer()});
	}

	/**
	 * This creates a context menu for the viewer and adds a listener as well
	 * registering the menu for extension.
	 */
	protected void createContextMenu() {
		MenuManager contextMenu = new MenuManager("#PopUp"); //$NON-NLS-1$
		contextMenu.add(new Separator("additions")); //$NON-NLS-1$
		contextMenu.setRemoveAllWhenShown(true);
		contextMenu.addMenuListener(new NodeActionMenuListener());
		Menu menu = contextMenu.createContextMenu(getControl());
		getControl().setMenu(menu);
	}

	private DragSourceListener createDragSourceListener() {
		return new DragSourceListener() {
			public void dragFinished(DragSourceEvent event) {
				LocalSelectionTransfer.getTransfer().setSelection(null);
			}

			public void dragSetData(DragSourceEvent event) {
			}

			public void dragStart(DragSourceEvent event) {
				LocalSelectionTransfer.getTransfer().setSelection(getSelection());
			}
		};
	}

	private DropTargetListener createDropTargetListener() {
		return new DropTargetListener() {
			public void dragEnter(DropTargetEvent event) {
			}

			public void dragLeave(DropTargetEvent event) {
			}

			public void dragOperationChanged(DropTargetEvent event) {
			}

			public void drop(DropTargetEvent event) {
				if (event.operations != DND.DROP_NONE && LocalSelectionTransfer.getTransfer().getSelection() instanceof IStructuredSelection) {
					dragOver(event);
					IStructuredSelection selection = (IStructuredSelection) LocalSelectionTransfer.getTransfer().getSelection();
					if (selection != null && !selection.isEmpty() && event.item != null && event.item.getData() != null) {
						/*
						 * the command uses these numbers instead of the
						 * feedback constants (even though it converts in
						 * the other direction as well)
						 */
						float feedbackFloat = getHeightInItem(event);

						final DragNodeCommand command = new DragNodeCommand(event.item.getData(), feedbackFloat, event.operations, event.detail, selection.toList(), XMLTableTreeViewer.this);
						if (command != null && command.canExecute()) {
							SafeRunnable.run(new SafeRunnable() {
								public void run() throws Exception {
									command.execute();
								}
							});
						}
					}
				}
			}

			public void dropAccept(DropTargetEvent event) {
			}

			public void dragOver(DropTargetEvent event) {
				event.feedback = DND.FEEDBACK_SELECT;
				float feedbackFloat = getHeightInItem(event);
				if (feedbackFloat > 0.75) {
					event.feedback = DND.FEEDBACK_INSERT_AFTER;
				}
				else if (feedbackFloat < 0.25) {
					event.feedback = DND.FEEDBACK_INSERT_BEFORE;
				}
				event.feedback |= DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;
			}

			private float getHeightInItem(DropTargetEvent event) {
				if(event.item == null) return .5f;
				if (event.item instanceof TreeItem) {
					TreeItem treeItem = (TreeItem) event.item;
					Control control = treeItem.getParent();
					Point point = control.toControl(new Point(event.x, event.y));
					Rectangle bounds = treeItem.getBounds();
					return (float) (point.y - bounds.y) / (float) bounds.height;
				}
				else if (event.item instanceof TableItem) {
					TableItem tableItem = (TableItem) event.item;
					Control control = tableItem.getParent();
					Point point = control.toControl(new Point(event.x, event.y));
					Rectangle bounds = tableItem.getBounds(0);
					return (float) (point.y - bounds.y) / (float) bounds.height;
				}
				else {
					return 0.0F;
				}
			}
		};
	}
	
	protected void doRefresh(Object o, boolean fromDelayed) {
		treeExtension.resetCachedData();
		super.refresh(o);
	}

	public ISelectionProvider getSelectionProvider() {
		return fSelectionProvider;
	}

	public String getTitle() {
		return XMLEditorMessages.XMLTableTreeViewer_0;
	}

	protected void handleDispose(DisposeEvent event) {
		super.handleDispose(event);
		treeExtension.dispose();
		setDocument(null);
	}

	public void refresh() {
		treeExtension.resetCachedData();
		super.refresh();
	}

	public void refresh(Object o) {
		treeExtension.resetCachedData();
		super.refresh(o);
	}

	public void refresh(boolean updateLabels) {
		treeExtension.resetCachedData();
		super.refresh(updateLabels);
		getControl().redraw();
	}

	public void refresh(Object element, boolean updateLabels) {
		treeExtension.resetCachedData();
		super.refresh(element, updateLabels);
		getControl().redraw();
	}

	public void setDocument(IDocument document) {
		/*
		 * let the text editor to be the one that manages the model's lifetime
		 */
		IStructuredModel model = null;
		try {
			model = StructuredModelManager.getModelManager().getExistingModelForRead(document);

			if ((model != null) && (model instanceof IDOMModel)) {
				Document domDoc = null;
				domDoc = ((IDOMModel) model).getDocument();
				setInput(domDoc);
				treeExtension.setIsUnsupportedInput(false);
			}
			else {
				treeExtension.setIsUnsupportedInput(true);
			}
		}
		finally {
			if (model != null) {
				model.releaseFromRead();
			}
		}

	}

}