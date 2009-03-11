/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.actions;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.common.core.search.scope.SearchScope;
import org.eclipse.wst.common.ui.internal.search.dialogs.ComponentSearchListDialog;
import org.eclipse.wst.common.ui.internal.search.dialogs.ComponentSearchListDialogConfiguration;
import org.eclipse.wst.common.ui.internal.search.dialogs.ComponentSpecification;
import org.eclipse.wst.common.ui.internal.search.dialogs.IComponentDescriptionProvider;
import org.eclipse.wst.common.ui.internal.search.dialogs.IComponentList;
import org.eclipse.wst.common.ui.internal.search.dialogs.IComponentSearchListProvider;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDAdapterFactory;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.internal.common.commands.AddRedefinedComponentCommand;
import org.eclipse.wst.xsd.ui.internal.editor.Messages;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDRedefinableComponent;
import org.eclipse.xsd.XSDRedefine;
import org.eclipse.xsd.XSDSimpleTypeDefinition;


public abstract class AddXSDRedefinableContentAction extends XSDBaseAction
{
  public static final String COMPLEX_TYPE_ID = "org.eclipse.wst.xsd.ui.actions.RedfineComplexType"; //$NON-NLS-1$

  public static final String SIMPLE_TYPE_ID = "org.eclipse.wst.xsd.ui.actions.RedfineSimpleType"; //$NON-NLS-1$

  public static final String ATTRIBUTE_GROUP_ID = "org.eclipse.wst.xsd.ui.actions.RedfineAttributeGroup"; //$NON-NLS-1$

  public static final String MODEL_GROUP_ID = "org.eclipse.wst.xsd.ui.actions.RedfineModelGroup"; //$NON-NLS-1$

  public static final int COMPLEX_TYPE_REDEFINE = 1;

  public static final int SIMPLE_TYPE_REDEFINE = 2;

  public static final int ATTRIBUTE_GROUP_REDEFINE = 3;

  public static final int MODEL_GROUP_REDEFINE = 4;

  protected int operationType;

  public AddXSDRedefinableContentAction(IWorkbenchPart part, String ID, String text)
  {
    super(part);
    setText(text);
    setId(ID);
  }

  public void run()
  {
    Object selection = ((IStructuredSelection)getSelection()).getFirstElement();

    if (selection instanceof XSDBaseAdapter)
    {
      selection = ((XSDBaseAdapter)selection).getTarget();
    }

    if (selection instanceof XSDRedefine)
    {
      ComponentSearchListDialogConfiguration configuration = new ComponentSearchListDialogConfiguration();
      configuration.setDescriptionProvider(new RedefineDescriptor());
      configuration.setSearchListProvider(new RedefineSearchListProvider((XSDRedefine)selection, this));
      ComponentSearchListDialog dialog = new ComponentSearchListDialog(Display.getDefault().getActiveShell(), Messages._UI_LABEL_REDEFINE_COMPONENT, configuration)
      {
        protected Control createDialogArea(Composite parent)
        {
          // Adjust the dialog's initial size.
          
          Composite mainComposite = (Composite)super.createDialogArea(parent);
          GridData gridData = (GridData)mainComposite.getLayoutData();
          gridData.heightHint = 500;
          gridData.widthHint = 350;
          return mainComposite;
        }
      };
      dialog.create();
      dialog.setBlockOnOpen(true);
      int result = dialog.open();

      if (result == Window.OK)
      {
        ComponentSpecification selectedComponent = dialog.getSelectedComponent();
        buildRedefine((XSDRedefine)selection, selectedComponent);
      }
    }
  }

  protected abstract AddRedefinedComponentCommand getCommand(XSDRedefine redefine, XSDRedefinableComponent redefinableComponent);
  
  protected abstract void buildComponentsList(XSDRedefine xsdRedefine, List names, IComponentList componentList);
  
  protected void buildRedefine(XSDRedefine redefine, ComponentSpecification selectedComponent)
  {
    XSDRedefinableComponent redefinableComponent = (XSDRedefinableComponent)selectedComponent.getObject();
    AddRedefinedComponentCommand command = getCommand(redefine, redefinableComponent);
    getCommandStack().execute(command);
    addedComponent = command.getAddedComponent();
    Adapter adapter = XSDAdapterFactory.getInstance().adapt(addedComponent);
    selectAddedComponent(adapter);
  }

  class RedefineSearchListProvider implements IComponentSearchListProvider
  {
    XSDRedefine xsdRedefine;
    AddXSDRedefinableContentAction action;    
    
    public RedefineSearchListProvider(XSDRedefine xsdRedefine, AddXSDRedefinableContentAction action)
    {
      this.xsdRedefine = xsdRedefine;
      this.action = action;
    }

    // Should refactor this to have a separate subclass for each type of redefine.
    public void populateComponentList(IComponentList list, SearchScope scope, IProgressMonitor pm)
    {      
      List currentRedefines = xsdRedefine.getContents();
      List names = new ArrayList(currentRedefines.size());
      Iterator redefinesIterator = currentRedefines.iterator();
      while (redefinesIterator.hasNext())
      {
        XSDRedefinableComponent component = (XSDRedefinableComponent)redefinesIterator.next();
        names.add(component.getName());
      }
      action.buildComponentsList(xsdRedefine, names, list);      
    }
  }

  class RedefineDescriptor implements IComponentDescriptionProvider
  {
    public IFile getFile(Object component)
    {
      if (component instanceof XSDNamedComponent)
      {
        String location = ((XSDNamedComponent)component).getSchema().getSchemaLocation();
        String platformResource = "platform:/resource"; //$NON-NLS-1$
        if (location != null && location.startsWith(platformResource))
        {
          Path path = new Path(location.substring(platformResource.length()));
          return ResourcesPlugin.getWorkspace().getRoot().getFile(path);
        }
      }
      return null;
    }

    public Image getFileIcon(Object component)
    {
      return XSDEditorPlugin.getXSDImage("icons/XSDFile.gif"); //$NON-NLS-1$
    }

    public ILabelProvider getLabelProvider()
    {
      return new XSDRedefineComponentsLabelProvider();
    }

    public String getName(Object component)
    {
      if (component instanceof XSDNamedComponent)
      {
        return ((XSDNamedComponent)component).getName();
      }
      return "";
    }

    public String getQualifier(Object component)
    {
      if (component instanceof XSDNamedComponent)
      {
        return ((XSDNamedComponent)component).getTargetNamespace();
      }
      return "";
    }

    public boolean isApplicable(Object component)
    {
      return true;
    }

  }

  class XSDRedefineComponentsLabelProvider implements ILabelProvider
  {

    public Image getImage(Object element)
    {
      Image image = null;
      if (element instanceof XSDComplexTypeDefinition)
      {
        image = XSDEditorPlugin.getXSDImage(Messages._UI_IMAGE_COMPLEX_TYPE);
      }
      else if (element instanceof XSDSimpleTypeDefinition)
      {
        image = XSDEditorPlugin.getXSDImage(Messages._UI_IMAGE_SIMPLE_TYPE);
      }
      else if (element instanceof XSDAttributeGroupDefinition)
      {
        image = XSDEditorPlugin.getXSDImage(Messages._UI_IMAGE_ATTRIBUTE_GROUP);
      }
      else if (element instanceof XSDModelGroupDefinition)
      {
        image = XSDEditorPlugin.getXSDImage(Messages._UI_IMAGE_MODEL_GROUP);
      }
      return image;
    }

    public String getText(Object element)
    {
      if (element instanceof XSDNamedComponent)
      {
        return ((XSDNamedComponent)element).getName();
      }
      return "";
    }

    public void addListener(ILabelProviderListener listener)
    {
    }

    public void dispose()
    {
    }

    public boolean isLabelProperty(Object element, String property)
    {
      return false;
    }

    public void removeListener(ILabelProviderListener listener)
    {
    }
  }
}