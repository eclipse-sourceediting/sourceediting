/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.properties.sections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.xsd.ui.internal.common.commands.AddExtensionAttributeCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.AddExtensionCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.AddExtensionElementCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.ExtensibleAddExtensionCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.ExtensibleRemoveExtensionNodeCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.RemoveExtensionNodeCommand;
import org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo.AddExtensionsComponentDialog;
import org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo.CategoryProvider;
import org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo.DOMExtensionTreeLabelProvider;
import org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo.ExtensionItemFilter;
import org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo.ExtensionsSchemasRegistry;
import org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo.SpecificationForExtensionsSchema;
import org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo.XSDExtensionTreeContentProvider;
import org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo.custom.NodeFilter;
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
  
  protected AddExtensionsComponentDialog createAddExtensionsComponentDialog()
  {
    AddExtensionsComponentDialog dialog =  new AddExtensionsComponentDialog(composite.getShell(), getExtensionsSchemasRegistry())
    {
      protected IStructuredContentProvider getCategoryContentProvider()
      {
        return new XSDCategoryContentProvider();
      }
    };
    dialog.addElementsTableFilter(new AddExtensionsComponentDialogFilter(input, dialog));    
    return dialog;   
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
      ModelReconcileAdapter modelReconcileAdapter = adapter.getModelReconcileAdapter();
      if (modelReconcileAdapter != null)
      {
        modelReconcileAdapter.removeListener(internalNodeAdapter);
      }
    }  
  }

  protected AddExtensionCommand getAddExtensionCommand(Object o)
  {
    AddExtensionCommand addExtensionCommand = null;
    if (o instanceof XSDElementDeclaration)
    {
      XSDElementDeclaration element = (XSDElementDeclaration) o;
      ExtensibleAddExtensionCommand extensibleAddExtensionCommand = getExtensionsSchemasRegistry().getAddExtensionCommand(element.getTargetNamespace());
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
        Node node = (Node)o;
        ExtensibleRemoveExtensionNodeCommand removeCommand = getExtensionsSchemasRegistry().getRemoveExtensionNodeCommand(node.getNamespaceURI());
        if (removeCommand != null)
        {
          removeCommand.setInput((XSDConcreteComponent)input);
          removeCommand.setNode(node);
          return removeCommand;
        }
        else
        {
          command = new RemoveExtensionNodeCommand(Messages._UI_ACTION_DELETE_APPINFO_ELEMENT, node);  
          // command.execute();
        }
      }
    }
    catch (Exception e)
    {
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
  
  static class XSDCategoryContentProvider implements IStructuredContentProvider
  {
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements(Object inputElement)
    {    
      SpecificationForExtensionsSchema[] extensionsSchemaSpecs = null;
      try
      {
        List inputList = (List) inputElement;
        
        List total = new ArrayList();
        total.addAll(inputList);
        
        List dynamicCategories = XSDEditorPlugin.getPlugin().getExtensionsSchemasRegistry().getCategoryProviders();
        for (Iterator iter = dynamicCategories.iterator(); iter.hasNext(); )
        {
          CategoryProvider categoryProvider = (CategoryProvider)iter.next();
          for (Iterator it = categoryProvider.getCategories().iterator(); it.hasNext(); )
          {
            SpecificationForExtensionsSchema sp = (SpecificationForExtensionsSchema)it.next();
            total.add(sp);
          }
        }

        extensionsSchemaSpecs = (SpecificationForExtensionsSchema[]) total.toArray(new SpecificationForExtensionsSchema[0]);
      }
      catch (Exception e)
      {
      }
      return extensionsSchemaSpecs;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose()
    {
      // Do nothing

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
     *      java.lang.Object, java.lang.Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
      // Do nothing

    }           
  }
  
  /**
   * This filter is to be used by the dialog invoked when addButton is pressed
   */
  protected class AddExtensionsComponentDialogFilter extends ViewerFilter
  {
    private Object input;
    private AddExtensionsComponentDialog dialog;

    public AddExtensionsComponentDialogFilter(Object input, AddExtensionsComponentDialog dialog)
    {
      this.input = input;
      this.dialog = dialog;
    }

    public boolean select(Viewer viewer, Object parentElement, Object element)
    {      
      if (input instanceof XSDConcreteComponent &&
          element instanceof XSDConcreteComponent)
      {              
        SpecificationForExtensionsSchema spec = dialog.getSelectedCategory();
        // here we obtain the node filter that was registered for the applicable namespace
        // notied
        NodeFilter filter = XSDEditorPlugin.getPlugin().getNodeCustomizationRegistry().getNodeFilter(spec.getNamespaceURI());
        
        if (filter == null)
        {
          // Check if a node filter has been specified, if so, then use it
          filter = spec.getNodeFilter();
        }

        if (filter instanceof ExtensionItemFilter)
        {
          ExtensionItemFilter extensionItemFilter = (ExtensionItemFilter)filter;
          return extensionItemFilter.isApplicableContext((XSDConcreteComponent)input, (XSDConcreteComponent)element);               
        }
        else
        {
          // TODO cs: even if it's just a plain old NodeFilter we should still be able to use it! 
        }
      }
      return true;
    }
  }  

}