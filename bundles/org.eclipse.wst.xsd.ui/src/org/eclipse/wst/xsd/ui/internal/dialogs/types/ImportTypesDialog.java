/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.common.ui.dialogs.SelectSingleFileDialog;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDResourceImpl;

public class ImportTypesDialog extends SelectSingleFileDialog {
	private List importList;
	private Button importStyleCheckBox;
	private boolean isWSIStyleImport;
	private String kind = "type";
	private Object input;
	private ResourceSet resourceSet;
	private java.util.List importComponents;
	private Object listSelection;
	private boolean allowWSI;
	private Vector loaders;

	/*
	 * String kind should be either "type" or "element"
	 */
	public ImportTypesDialog(Shell parentShell, IStructuredSelection selection, boolean allowWSI, String kind) {
		super(parentShell, selection, true);
		this.allowWSI = allowWSI;
		this.kind = kind;
		
		loaders = new Vector();
		loaders.add(new XSDLoadAvaliableItems());
	}
	
	protected Control createDialogArea(Composite parent) {
		Control area = super.createDialogArea(parent);		
		Composite base = new Composite((Composite) area, SWT.NONE);
		base.setLayout(new GridLayout());

		GridData compositeData = new GridData();
	    compositeData.horizontalAlignment = GridData.FILL;
	    compositeData.verticalAlignment = GridData.FILL;
	    compositeData.grabExcessHorizontalSpace = true;
	    compositeData.grabExcessVerticalSpace = true;
	    base.setLayoutData(compositeData);
		
		Label listLabel = new Label(base, SWT.NONE);
		if (kind.equalsIgnoreCase("type")) {
			listLabel.setText("Types");
		}
		else {
			listLabel.setText("Elements");
		}
		importList = new List(base, SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);

	    GridData importListData = new GridData();
	    importListData.horizontalAlignment = GridData.FILL;
	    importListData.verticalAlignment = GridData.FILL;
	    importListData.grabExcessHorizontalSpace = true;
	    importListData.grabExcessVerticalSpace = true;
	    importList.setLayoutData(importListData);
	    
	    if (allowWSI) {
		    importStyleCheckBox = new Button(base, SWT.CHECK);
//			importStyleCheckBox.setText(WSDLEditorPlugin.getWSDLString("_UI_USE_WS-I_STYLE_SCHEMA_IMPORT"));
			importStyleCheckBox.setText("WS-I style import");
			importStyleCheckBox.setSelection(true);	
			isWSIStyleImport = true;
			importStyleCheckBox.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		    
		    SelectionAdapter selectionAdapter = new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent event)
				{
					if (event.widget == importList && importList.getSelectionCount() > 0) {
						listSelection = importComponents.get(importList.getSelectionIndex());
					}
					else if (event.widget == importStyleCheckBox) {
						isWSIStyleImport = importStyleCheckBox.getSelection();
					}
				}		
			};	    
		    importStyleCheckBox.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent event) {
					isWSIStyleImport = importStyleCheckBox.getSelection();
				}	    		
			});		    
		    
	        Label titleBarSeparator = new Label(base, SWT.HORIZONTAL | SWT.SEPARATOR);
		    titleBarSeparator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    }
	    
	    selectSingleFileView.addSelectionChangedTreeListener(new TreeListener());	    
	    importList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (importList.getSelectionCount() > 0) {
					listSelection = importComponents.get(importList.getSelectionIndex());
				}
			}	    		
		});
	    
		return area;
	}
	
	public Object getListSelection() {
		return listSelection;
	}
	
	public boolean isWSIStyleImport() {
		if (allowWSI) {
			return isWSIStyleImport;
		}
		else {
			return false;
		}
	}
	
	public void addLoadAvaliableItemsClass(LoadAvaliableItems newLoader) {
		loaders.add(newLoader);
	}
	
	private class TreeListener implements ISelectionChangedListener {
		public TreeListener() {
		}
		
		public void selectionChanged(SelectionChangedEvent event) {
			if (delayedEvent != null) {
				delayedEvent.CANCEL = true;
			}
			
			delayedEvent = new DelayedEvent(event);
			Display.getCurrent().timerExec(500, delayedEvent);			
			
			// Do not allow WS-I style imports on WSDL files
			ISelection selection = event.getSelection();
			if (selection instanceof IStructuredSelection && ((IStructuredSelection) selection).getFirstElement() instanceof IFile) {
		        IFile iFile = (IFile) ((IStructuredSelection) selection).getFirstElement();

				String ext = iFile.getFileExtension();
				if (ext.equalsIgnoreCase("wsdl")) {
					importStyleCheckBox.setEnabled(false);
					isWSIStyleImport = false;
				}
				else if (ext.equalsIgnoreCase("xsd")) {
					importStyleCheckBox.setEnabled(true);
					isWSIStyleImport = importStyleCheckBox.getSelection();
				}		        
			}
		}
		
		private DelayedEvent delayedEvent;
		
		private class DelayedEvent implements Runnable {
			public boolean CANCEL = false;
			private SelectionChangedEvent event;
			
			public DelayedEvent(SelectionChangedEvent event) {
				this.event = event;
			}
			public void run() {
				if (!CANCEL) {
			     	// We clear the selection made in the List... it may no longer apply to the newly selected file
			     	listSelection = null;
					importComponents = Collections.EMPTY_LIST;
			        importList.removeAll();
			        
					// We populate the avaliable types/element list
					 ISelection selection = event.getSelection();
				     if (selection instanceof IStructuredSelection && ((IStructuredSelection) selection).getFirstElement() instanceof IFile) {
				        IFile iFile = (IFile) ((IStructuredSelection) selection).getFirstElement();
				        
				        // Attempt to load the avaliable Types/Elements from the selected File
				        int loaderIndex = 0;
				        while (importComponents.size() < 1 && loaderIndex < loaders.size()) {
				        	importComponents = ((LoadAvaliableItems) loaders.elementAt(loaderIndex)).getAvaliableItems(iFile);
				        	loaderIndex++;
				        }
				        	
				        // Make the first item in the list the selection
				        if (importComponents.size() > 0) {
		                	listSelection = importComponents.get(0);
		                }
				        
						for (Iterator it = ((java.util.List) importComponents).iterator(); it.hasNext();) {
							XSDNamedComponent comp = (XSDNamedComponent) it.next();
							importList.add(comp.getName());
						}
				     }
				}
			}
		}
	}
	
	/*
	 * Loads Avaliable Types/Elements from the given IFile.  Handles XSD files
	 */
	private class XSDLoadAvaliableItems extends LoadAvaliableItems {
		public XSDLoadAvaliableItems() {}
		
		protected java.util.List loadFile(IFile iFile) {
		    java.util.List modelObjectList = Collections.EMPTY_LIST;
		    try {
		      if (iFile != null) {
		        URI uri = URI.createPlatformResourceURI(iFile.getFullPath().toString());

		        Object rootModelObject = null;
		        if (uri.toString().endsWith("xsd")) {
		          ResourceSet resourceSet = new ResourceSetImpl();
		          Resource resource = resourceSet.getResource(uri, true);
		          if (resource instanceof XSDResourceImpl) {
		            rootModelObject = ((XSDResourceImpl) resource).getSchema();
		          }
		        }

		        if (rootModelObject != null) {
		          modelObjectList = new ArrayList(getModelObjects(rootModelObject));
		        }
		      }
		    }
		    catch (Exception e) {}

		    return modelObjectList;
		  }
		
		  private Collection getModelObjects(Object rootModelObject) {
		    ArrayList objects = new ArrayList();
		    
		    if (rootModelObject instanceof XSDSchema) {
		      XSDSchema xsdSchema = (XSDSchema)rootModelObject;
		      if (kind.equalsIgnoreCase("type")) {
		        objects.addAll(((XSDSchema) xsdSchema).getTypeDefinitions());
		      }
		      else if (kind.equalsIgnoreCase("element")) {
		        objects.addAll(((XSDSchema) xsdSchema).getElementDeclarations());
		      }
		    }

		    return objects;
		  }
	}
}
