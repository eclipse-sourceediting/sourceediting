/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.properties.sections;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.xsd.ui.internal.common.commands.AddExtensionAttributeCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.AddExtensionCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.AddExtensionElementCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.ExtensibleAddExtensionCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.RemoveExtensionNodeCommand;
import org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo.DOMExtensionTreeLabelProvider;
import org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo.ExtensionsSchemasRegistry;
import org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo.XSDExtensionTreeContentProvider;
import org.eclipse.wst.xsd.ui.internal.common.util.Messages;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.text.XSDModelAdapter;
import org.eclipse.wst.xsd.ui.internal.util.ModelReconcileAdapter;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ExtensionsSection extends AbstractExtensionsSection
{
  XSDModelAdapter adapter = null;
  
  public ExtensionsSection()
  {
    super();
    setExtensionTreeLabelProvider(new DOMExtensionTreeLabelProvider());
    setExtensionTreeContentProvider(new XSDExtensionTreeContentProvider());
  }
  
  public void setInput(IWorkbenchPart part, ISelection selection)
  {
    super.setInput(part, selection); 
    if (adapter == null)
    {
      if (selection instanceof StructuredSelection)
      {
        Object obj = ((StructuredSelection) selection).getFirstElement();
        if (obj instanceof XSDConcreteComponent)
        {  
          Element element = ((XSDConcreteComponent)obj).getElement();
          if (element != null)
          {
            adapter = XSDModelAdapter.lookupOrCreateModelAdapter(element.getOwnerDocument());
            if (adapter != null)
            {
              ModelReconcileAdapter modelReconcileAdapter = adapter.getModelReconcileAdapter();
              if (modelReconcileAdapter != null)
              {
                modelReconcileAdapter.addListener(internalNodeAdapter);
              }
            }  
          }
        }
      }
    }
    extensionTreeViewer.expandToLevel(2);
  }
  
  public void dispose()
  {
    super.dispose();
    if (adapter != null)
    {
      adapter.getModelReconcileAdapter().removeListener(internalNodeAdapter);
    }  
  }

  protected AddExtensionCommand getAddExtensionCommand(Object o)
  {
    AddExtensionCommand addExtensionCommand = null;
    if (o instanceof XSDElementDeclaration)
    {
      XSDElementDeclaration element = (XSDElementDeclaration) o;
      ExtensibleAddExtensionCommand extensibleAddExtensionCommand = getExtensionsSchemasRegistry().getAddExtensionHandler(element.getTargetNamespace());
      if (extensibleAddExtensionCommand != null)
      {
        extensibleAddExtensionCommand.setInputs((XSDConcreteComponent) input, element);
        addExtensionCommand = extensibleAddExtensionCommand;
      }
      else
      {
        addExtensionCommand = new AddExtensionElementCommand(Messages._UI_ACTION_ADD_APPINFO_ELEMENT, (XSDConcreteComponent) input, element);
      }
    }
    else if (o instanceof XSDAttributeDeclaration)
    {
      XSDAttributeDeclaration attribute = (XSDAttributeDeclaration) o;
      addExtensionCommand = new AddExtensionAttributeCommand(Messages._UI_ACTION_ADD_APPINFO_ATTRIBUTE, (XSDConcreteComponent) input, attribute);
    }
    return addExtensionCommand;
  }

  protected Command getRemoveExtensionCommand(Object o)
  {
    Command command = null;
    try
    {     
      if (o instanceof Node)
      {            
        command = new RemoveExtensionNodeCommand(Messages._UI_ACTION_DELETE_APPINFO_ELEMENT, (Node)o);  
        command.execute();
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return command;
  }  
  
  protected ExtensionsSchemasRegistry getExtensionsSchemasRegistry()
  {
    return XSDEditorPlugin.getDefault().getExtensionsSchemasRegistry();
  }
  
  protected boolean isTreeViewerInputElement(Element element)
  {     
    if (input instanceof XSDConcreteComponent)
    {  
      XSDConcreteComponent component = (XSDConcreteComponent)input;
      Element componentElement = component.getElement();
      Node parent = element.getParentNode();
      Node grandParent = parent != null ? parent.getParentNode() : null;
      return componentElement == element || componentElement == parent || componentElement == grandParent;
    }
    return false;
  }
  
  protected IPreferenceStore getPrefStore()
  {
    return XSDEditorPlugin.getPlugin().getPreferenceStore();
  }
}