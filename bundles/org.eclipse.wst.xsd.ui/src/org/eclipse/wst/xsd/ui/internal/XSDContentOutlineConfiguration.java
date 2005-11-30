/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.sse.ui.internal.contentoutline.PropertyChangeUpdateAction;
import org.eclipse.wst.sse.ui.internal.contentoutline.PropertyChangeUpdateActionContributionItem;
import org.eclipse.wst.sse.ui.views.contentoutline.ContentOutlineConfiguration;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xsd.ui.internal.actions.OpenSchemaAction;
import org.eclipse.wst.xsd.ui.internal.provider.CategoryAdapter;
import org.eclipse.wst.xsd.ui.internal.provider.XSDAdapterFactoryLabelProvider;
import org.eclipse.wst.xsd.ui.internal.provider.XSDContentProvider;
import org.eclipse.wst.xsd.ui.internal.provider.XSDModelAdapterFactoryImpl;
import org.eclipse.wst.xsd.ui.internal.text.XSDModelAdapter;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaDirective;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Outline configuration for XSD
 */
public class XSDContentOutlineConfiguration extends ContentOutlineConfiguration {
	private XSDContentProvider fContentProvider;
	private ILabelProvider fLabelProvider;
	private KeyListener[] fKeyListeners = null;
	private IMenuListener fMenuListener = null;
	private XSDEditor fEditor = null;
	private TreeViewer treeViewer;
	protected SelectionManagerSelectionChangeListener selectionManagerSelectionChangeListener = new SelectionManagerSelectionChangeListener();
	
	public XSDContentOutlineConfiguration()
	{
		super();
	}

	public IContentProvider getContentProvider(TreeViewer viewer) {
		if (fContentProvider == null) {
			fContentProvider = new XSDContentProvider(XSDModelAdapterFactoryImpl.getInstance());
		}
		this.treeViewer = viewer;
		getXSDEditor().getSelectionManager().addSelectionChangedListener(selectionManagerSelectionChangeListener);
		return fContentProvider;
	}

	public ILabelProvider getLabelProvider(TreeViewer viewer) {
		if (fLabelProvider == null) {
			fLabelProvider = new XSDAdapterFactoryLabelProvider(XSDModelAdapterFactoryImpl.getInstance());
		}
		return fLabelProvider;
	}

	public IMenuListener getMenuListener(TreeViewer viewer) {
		if (fMenuListener == null) {
			// ISSUE: what happens if cannot get XSD Editor? (See
			// getXSDEditor comment)
			if (getXSDEditor() != null)
				fMenuListener = new XSDMenuListener(getXSDEditor().getSelectionManager());
		}
		return fMenuListener;
	}

	public KeyListener[] getKeyListeners(TreeViewer viewer) {
		if (fKeyListeners == null) {
			final TreeViewer finalViewer = viewer;
			KeyAdapter keyListener = new KeyAdapter() {
				public void keyReleased(KeyEvent e) {
					if (e.character == SWT.DEL) {
						IMenuListener menuListener = getMenuListener(finalViewer);
						if (menuListener instanceof XSDMenuListener)
							((XSDMenuListener) menuListener).getDeleteAction().run();
					}
					else if (e.keyCode == SWT.F3) // open editor on any
					// include/import/redefine
					{
						if (e.widget instanceof Tree) {
							Tree tree = (Tree) e.widget;
							TreeItem[] selection = tree.getSelection();
							if (selection.length > 0) {
								if (selection[0].getData() instanceof XSDSchemaDirective) {
									XSDSchemaDirective comp = (XSDSchemaDirective) selection[0].getData();
									OpenSchemaAction openSchema = new OpenSchemaAction(XSDEditorPlugin.getXSDString("_UI_ACTION_OPEN_SCHEMA"), comp);
									openSchema.run();
								}
							}
						}
					}
				}
			};
			fKeyListeners = new KeyListener[]{keyListener};
		}

		return fKeyListeners;
	}

	public ISelection getSelection(TreeViewer viewer, ISelection selection) {
		ISelection sel = selection;

		if (selection instanceof IStructuredSelection) {
			List xsdSelections = new ArrayList();
			for (Iterator i = ((IStructuredSelection) selection).iterator(); i.hasNext();) {
				Object domNode = i.next();
				Object xsdNode = getXSDNode(domNode, viewer);
				if (xsdNode != null) {
					xsdSelections.add(xsdNode);
				}
			}

			if (!xsdSelections.isEmpty()) {
				sel = new StructuredSelection(xsdSelections);
			}
		}
		return sel;
	}

	/**
	 * Determines XSD node based on object (DOM node)
	 * 
	 * @param object
	 * @return
	 */
	private Object getXSDNode(Object object, TreeViewer viewer) {
		// get the element node
		Element element = null;
		if (object instanceof Node) {
			Node node = (Node) object;
			if (node != null) {
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					element = (Element) node;
				}
				else if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
					element = ((Attr) node).getOwnerElement();
				}
			}
		}
		Object o = element;
		if (element != null) {
			Object modelObject = getXSDSchema(viewer).getCorrespondingComponent(element);
			if (modelObject != null) {
				o = modelObject;
			}
		}
		return o;
	}

	/**
	 * Gets the xsd schema from treeviewer's input
	 * 
	 * @param model
	 *            (of type Object but really should be IStructuredModel)
	 * @return Definition
	 */
	private XSDSchema getXSDSchema(TreeViewer viewer) {
		XSDSchema xsdSchema = null;
		Object model = null;
		if (viewer != null)
			model = viewer.getInput();

		if (model instanceof IDOMModel) {
			IDOMDocument domDoc = ((IDOMModel) model).getDocument();
			if (domDoc != null) {
				XSDModelAdapter modelAdapter = (XSDModelAdapter) domDoc.getExistingAdapter(XSDModelAdapter.class);
				/*
				 * ISSUE: Didn't want to go through initializing schema if it
				 * does not already exist, so just attempted to get existing
				 * adapter. If doesn't exist, just don't bother working.
				 */
				if (modelAdapter != null)
					xsdSchema = modelAdapter.getSchema();
			}
		}
		return xsdSchema;
	}

	// ISSUE: There are some cases where outline comes up before editor
	private XSDEditor getXSDEditor() {
		if (fEditor == null) {
			IWorkbench workbench = PlatformUI.getWorkbench();
			if (workbench != null) {
				IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
				if (window != null) {
					IWorkbenchPage page = window.getActivePage();
					if (page != null) {
						IEditorPart editor = page.getActiveEditor();
						if (editor instanceof XSDEditor)
							fEditor = (XSDEditor) editor;
					}
				}
			}
		}
		return fEditor;
	}

	protected IContributionItem[] createMenuContributions(TreeViewer viewer) {
       IContributionItem[] items = super.createMenuContributions(viewer);
		
	   referenceAction = new FilterAction(XSDEditorPlugin.getPlugin().getPreferenceStore(), "referenceContentAction", new ReferenceFilter("Reference Content"), XSDEditorPlugin.getXSDString("_UI_OUTLINE_SHOW_REFERENCES"), ImageDescriptor.createFromFile(XSDEditorPlugin.getPlugin().getClass(), "icons/XSDElementRef.gif"));
       boolean initialRef = getXSDEditor().getXSDModelAdapterFactory().getShowReferences();
       referenceAction.setChecked(initialRef);

       inheritedAction = new FilterAction(XSDEditorPlugin.getPlugin().getPreferenceStore(), "inheritedContentAction", new ReferenceFilter("Inherited Content"), XSDEditorPlugin.getXSDString("_UI_OUTLINE_SHOW_INHERITED"), ImageDescriptor.createFromFile(XSDEditorPlugin.getPlugin().getClass(), "icons/XSDComplexContent.gif"));
		
	   IContributionItem toggleReferenceItem = new PropertyChangeUpdateActionContributionItem(referenceAction);
	   IContributionItem toggleInheritedItem = new PropertyChangeUpdateActionContributionItem(inheritedAction);
	    
	   List list = new ArrayList();
	   list.addAll(Arrays.asList(items));
	   list.add(toggleReferenceItem);
	   list.add(toggleInheritedItem);
	   
	   int length = list.size();
	   IContributionItem[] newItems = new IContributionItem[length];
	   int i = 0;
	   for (Iterator iter = list.iterator(); iter.hasNext(); i++)
	   {
	     newItems[i] = (IContributionItem)iter.next();
	   }
	   
	   return newItems;
	}
	
	  // expose
	  public TreeViewer getTreeViewer()
	  {
	    return treeViewer;
	  }

	  FilterAction referenceAction, inheritedAction;
	  
	  private void updateActions(Action current)
	  {
	    if (referenceAction.isChecked())
	    {
	      getXSDEditor().getXSDModelAdapterFactory().setShowReferences(true);
	    }
	    else
	    {
	      getXSDEditor().getXSDModelAdapterFactory().setShowReferences(false);
	    }
	    if (inheritedAction.isChecked())
	    {
	      getXSDEditor().getXSDModelAdapterFactory().setShowInherited(true);
	    }
	    else
	    {
	      getXSDEditor().getXSDModelAdapterFactory().setShowInherited(false);
	    }
	    getTreeViewer().refresh();
	  }

	  public class FilterAction extends PropertyChangeUpdateAction
	  {
	    ViewerFilter filter;

	    public FilterAction(IPreferenceStore store, String preference, ViewerFilter filter, String label, ImageDescriptor image)
	    {
	      super(label, store, preference, true);
	      setImageDescriptor(image);
	      setUpdateFromPropertyChange(false);
	      this.filter = filter;
	      setChecked(false);
	    }

	    public void update()
	    {
	      super.update();
	      updateActions(this);
	    }
	  }
	  
	  class ReferenceFilter extends ViewerFilter // Dummy filter
	  {
	    public ReferenceFilter(String elementTag)
	    {
	      this.elementTag = elementTag;
	    }
	    protected String elementTag;

	    public boolean select(Viewer viewer, Object parentElement, Object element)
	    {
	      return true;
	    }
	  }
	
	  class SelectionManagerSelectionChangeListener implements ISelectionChangedListener
	  {
	    public void selectionChanged(SelectionChangedEvent event)
	    {
	      if (event.getSelectionProvider() != getTreeViewer())
	      {
	    	StructuredSelection selection = (StructuredSelection)event.getSelection();
	    	if (selection.getFirstElement() instanceof XSDSchema)
	    	{
	    	   StructuredSelection s = (StructuredSelection)getTreeViewer().getSelection();
	    	   if (!(s.getFirstElement() instanceof CategoryAdapter))
	    	   {
	    		 getTreeViewer().setSelection(event.getSelection(), true);
	    	   }
	    	}
	    	else
	    	{
	          getTreeViewer().setSelection(event.getSelection(), true);
	    	}
	      }
	    }
	  }

	  public void unconfigure(TreeViewer viewer) {
		  super.unconfigure(viewer);
		  getXSDEditor().getSelectionManager().removeSelectionChangedListener(selectionManagerSelectionChangeListener);
	  }
}
