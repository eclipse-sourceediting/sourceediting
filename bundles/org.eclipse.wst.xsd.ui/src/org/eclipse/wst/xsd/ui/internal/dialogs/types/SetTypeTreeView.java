/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.dialogs.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class SetTypeTreeView {
	  private TreeViewer treeViewer;
	  private TypesDialogTreeObject rootTreeObject;
	  private HashMap expansionStateMap;
	  	  
	  public SetTypeTreeView(Composite base, String stringLabel) {
		Label label = new Label(base, SWT.NONE);
		label.setText(stringLabel);

	  	treeViewer = new TreeViewer(new Tree(base, SWT.SINGLE | SWT.BORDER));
	    treeViewer.setContentProvider(new TreeObjectContentProvider());
	    treeViewer.setLabelProvider(new TreeObjectLabelProvider());

	    Control treeWidget = treeViewer.getTree();
	    GridData gd = new GridData(GridData.FILL_BOTH);
	    treeWidget.setLayoutData(gd);
	    
	    expansionStateMap = new HashMap();
	  }
	  
	  public void setInput(TypesDialogTreeObject input) {	  	
	  	rootTreeObject = input;
	  	treeViewer.setInput(input);
	  	treeViewer.refresh();
	  }
	  
	  /*
	   * Take a snapshot of the current expansion state.  We will use this
	   * snapshot to restore the expanded state of TreeViewer based on the new input.
	   * Usage: setTypeTreeView.storeExpansionState()
	   * 		setTypeTreeView.setInput(input);
	   * 		setTypeTreeView.restoreExpansionState()
	   */
	  public void storeExpansionState() {
	  	// Easy to record expansion state since expansion should only
	  	// occur at the 'top' level
	  	if (rootTreeObject != null) {
	  		expansionStateMap = new HashMap(rootTreeObject.getChildren().size());
	  		Iterator childrenIt = rootTreeObject.getChildren().iterator();
	  		while (childrenIt.hasNext()) {
	  			TypesDialogTreeObject item = (TypesDialogTreeObject) childrenIt.next();
	  			expansionStateMap.put(item.getDataObject(), isExpanded(item));
	  		}
	  	}
	  }
	  
	  public void restoreExpansionState() {
	  	Iterator childrenIt = rootTreeObject.getChildren().iterator();
	  	while (childrenIt.hasNext()) {
	  		TypesDialogTreeObject item = (TypesDialogTreeObject) childrenIt.next();
	  		Boolean expand = (Boolean) expansionStateMap.get(item.getDataObject());
	  		if (expand == Boolean.TRUE) {
	  			TreeItem treeItem = getTreeItem(item);
	  			if (treeItem != null && treeItem.getItemCount() > 0) {
	  				// Expand the TreeItem by calling showItem() on the first child of TreeItem
	  				treeViewer.getTree().showItem(treeItem.getItems()[0]);
	  			}
	  		}
	  	}
	  }
	  
	  private Boolean isExpanded(TypesDialogTreeObject item) {
	  	TreeItem treeItem = getTreeItem(item);
	  	return Boolean.valueOf(treeItem.getExpanded());
	  }
	  
	  private TreeItem getTreeItem(TreeItem root, TypesDialogTreeObject item) {
	  	TreeItem treeItem = null;
	  	
	  	if (root.getData().equals(item)) {
	  		treeItem = root;
  		}
  		else {
  			// Check it's children
  			TreeItem items[] = root.getItems();
  			for (int index = 0; index < items.length; index++) {
  				if (items[index].getData() != null) {
  					treeItem = getTreeItem(items[index], item);
  					if (treeItem != null) {
  						break;
  					}
  				}
  			}
  		}
	  	
	  	return treeItem;
	  }
	  
	  public TreeItem getTreeItem(TypesDialogTreeObject item) {
	  	boolean found = false;
	  	TreeItem treeItem = null;
	  	TreeItem treeItems[] = treeViewer.getTree().getItems();
	  	for (int index = 0; index < treeItems.length; index++) {
	  		if (treeItems[index].getData() != null) { 
	  			treeItem = getTreeItem(treeItems[index], item);
	  			if (treeItem != null) {
	  				break;
	  			}
	  		}
	  	}

	  	return treeItem;
	  }
	  
	  public TypesDialogTreeObject getSelection() {
	  	return (TypesDialogTreeObject) ((StructuredSelection) treeViewer.getSelection()).getFirstElement();
	  }

	  public void selectTreeObject(TypesDialogTreeObject selectionObject) {
	  	if (selectionObject != null) {
			  StructuredSelection structuredSelection = new StructuredSelection(selectionObject);
			  treeViewer.setSelection(structuredSelection, true);
	  	}
	  }	  

	  public void expandAll() {
	  	treeViewer.expandAll();
	  }
	  
	  public void setRedraw(boolean redraw) {
	  	treeViewer.getTree().setRedraw(redraw);
	  }
	  
	  /*
	   * Select the first TypesDialogTreeObject avaliable (where the TypesDialogTreeObject is not a direct
	   * child of the input (rootTreeObject).  For example, select the first avaliable
	   * Complex or Simple type.  Not the Headings)
	   */
	  public void selectFirstItem() {
	  	Iterator rootChildren = rootTreeObject.getChildren().iterator();
	  	while (rootChildren.hasNext()) {
	  		TypesDialogTreeObject rootChild = (TypesDialogTreeObject) rootChildren.next();
	  		if (rootChild.getChildren().size() > 0) {
	  			selectTreeObject((TypesDialogTreeObject) rootChild.getChildren().get(0));	  			
	  			break;
	  		}	  		
	  	}
	  }
	  
	  public void addTreeSelectionChangedListener(ISelectionChangedListener listener) {
	  	treeViewer.addSelectionChangedListener(listener);
	  }
	  
/////////////////////////////////////////////////////////////////////////
	  /*
	   * TreeContentProvider for TypesDialogTreeObject.java
	   */
		private class TreeObjectContentProvider implements ITreeContentProvider {
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
			}
			
			public Object[] getElements(Object inputElement) {
			 	if (inputElement instanceof TypesDialogTreeObject) {
			 		return ((TypesDialogTreeObject) inputElement).getChildren().toArray();
			 	}		 	
			 	return (new ArrayList()).toArray();			
			}
			
			public void dispose() {
			}
			
			 public Object[] getChildren(Object parentElement) {
			 	if (parentElement instanceof TypesDialogTreeObject) {
			 		return ((TypesDialogTreeObject) parentElement).getChildren().toArray();
			 	}		 	
			 	return (new ArrayList()).toArray();
			 }

			 public Object getParent(Object element) {
			 	if (element instanceof TypesDialogTreeObject) {
			 		return ((TypesDialogTreeObject) element).getParent();
			 	}		 	
			 	return null;
			 }
			 
			 public boolean hasChildren(Object element) {
			 	if (element instanceof TypesDialogTreeObject) {
			 		if (((TypesDialogTreeObject) element).getChildren().size() > 0) {
			 			return true;
			 		}
			 	}		 	
			 	return false;
			 }
		}  
		  
		/*
		 * TreeLabelProvider for TypesDialogTreeObject.java
		 */
		private class TreeObjectLabelProvider extends LabelProvider {
			public Image getImage(Object element) {
				if (element instanceof TypesDialogTreeObject) {
					return ((TypesDialogTreeObject) element).getImage();
				}			
				
				return null;
			}
			
			public String getText(Object element) {
				if (element instanceof TypesDialogTreeObject) {
					return ((TypesDialogTreeObject) element).getEntireLabel();
				}			
				return null;
			}
		}
}
