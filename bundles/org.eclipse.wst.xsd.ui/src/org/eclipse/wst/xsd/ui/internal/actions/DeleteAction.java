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
package org.eclipse.wst.xsd.ui.internal.actions;

import java.util.Iterator;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.eclipse.wst.xml.core.document.XMLNode;
import org.eclipse.wst.xml.core.internal.document.DocumentImpl;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.refactor.delete.BaseGlobalCleanup;
import org.eclipse.wst.xsd.ui.internal.refactor.delete.GlobalAttributeCleanup;
import org.eclipse.wst.xsd.ui.internal.refactor.delete.GlobalAttributeGroupCleanup;
import org.eclipse.wst.xsd.ui.internal.refactor.delete.GlobalElementCleanup;
import org.eclipse.wst.xsd.ui.internal.refactor.delete.GlobalGroupCleanup;
import org.eclipse.wst.xsd.ui.internal.refactor.delete.GlobalSimpleOrComplexTypeCleanup;
import org.eclipse.wst.xsd.ui.internal.refactor.delete.XSDExternalFileCleanup;
import org.eclipse.wst.xsd.ui.internal.util.TypesHelper;
import org.eclipse.wst.xsd.ui.internal.util.XSDDOMHelper;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDInclude;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDRedefine;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaDirective;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Node;


public class DeleteAction extends SelectionListenerAction
{
  protected IEditorPart editor;
  protected XSDSchema xsdSchema;
  protected ISelectionProvider selectionProvider;
  protected XSDConcreteComponent parentXSDComponent;
  
  /**
   * Constructor for DeleteAction.
   * @param text
   */
  public DeleteAction(String text, IEditorPart editor, XSDSchema xsdSchema)
  {
    super(text);
    this.editor = editor;
    this.xsdSchema = xsdSchema;
  }
  
  public void setSelectionProvider(ISelectionProvider selectionProvider)
  {
    this.selectionProvider = selectionProvider;
  }
  
  public IEditorPart getEditor()
  {
    return editor;
  }
  
  public XSDSchema getSchema()
  {
    return xsdSchema;
  }
  
  public void setXSDSchema(XSDSchema xsdSchema)
  {
    this.xsdSchema = xsdSchema;
  }
  
  /*
   * @see IAction#run()
   */
  public void run()
  {
    IStructuredSelection selection = getStructuredSelection();
    
    if (selection.isEmpty())
    {
      return;
    }
    
    Iterator iter = selection.iterator();
    DocumentImpl doc = null;
    while (iter.hasNext())
    {
      Object obj = iter.next();
      Node node = null;
      if (obj instanceof Node)
      {
        node = (Node)obj;
      }
      else if (obj instanceof XSDConcreteComponent)
      {
        xsdSchema = ((XSDConcreteComponent)obj).getSchema();
        
        node = ((XSDConcreteComponent)obj).getElement();
        if (node instanceof XMLNode)
        {
          parentXSDComponent = ((XSDConcreteComponent)obj).getContainer();
        
          if (parentXSDComponent instanceof XSDParticle)
          {
            // need to get the modelGroup
            parentXSDComponent = parentXSDComponent.getContainer();
          }
        }

      }
      if (!XSDDOMHelper.inputEquals(node, XSDConstants.SCHEMA_ELEMENT_TAG, false))
      {
        if (node instanceof XMLNode)
        {
          if (doc == null)
          {
            doc = (DocumentImpl) node.getOwnerDocument();
            doc.getModel().beginRecording(this, XSDEditorPlugin.getXSDString("_UI_ACTION_DELETE_NODES"));
          }
    
          boolean refresh = cleanupReferences(node);
          if (node != null)
          {
            XSDDOMHelper.removeNodeAndWhitespace(node);
          }
          
          // Workaround to reset included elements in XSD model
          if (refresh)
          {
  //          getEditor().reparseSchema();
  //          getEditor().getGraphViewer().setSchema(getEditor().getXSDSchema());
          }
        }
      }
    }
    
    if (parentXSDComponent != null && selectionProvider != null)
    {
      selectionProvider.setSelection(new StructuredSelection(parentXSDComponent));
    }
    
    if (doc != null)
    {
      doc.getModel().endRecording(this);
    }
  }

  protected boolean cleanupReferences(Node deletedNode)
  {
    boolean refresh = false;
    XSDConcreteComponent comp = getSchema().getCorrespondingComponent(deletedNode);
    
    if (comp instanceof XSDInclude ||
        comp instanceof XSDImport ||
        comp instanceof XSDRedefine)
    {
      XSDSchema resolvedSchema = ((XSDSchemaDirective)comp).getResolvedSchema();
      XSDSchema referencedSchema = null;
      if (comp instanceof XSDInclude)
      {
        referencedSchema = ((XSDInclude)comp).getIncorporatedSchema();
        refresh = true;
      }
      else if (comp instanceof XSDRedefine)
      {
        referencedSchema = ((XSDRedefine)comp).getIncorporatedSchema();
        refresh = true;
      }
      else if (comp instanceof XSDImport)
      {
        XSDImport imp = (XSDImport)comp;
        referencedSchema = ((XSDImport)comp).getResolvedSchema();
        refresh = true;
      }

      if (referencedSchema != null)
      {
        XSDExternalFileCleanup cleanHelper = new XSDExternalFileCleanup(referencedSchema);
        cleanHelper.visitSchema(getSchema());
        // populate messages
// TODO        getEditor().createTasksInTaskList(cleanHelper.getMessages());
      }
      if (comp instanceof XSDImport)
      {
        TypesHelper typesHelper = new TypesHelper(getSchema());
        typesHelper.updateMapAfterDelete((XSDImport)comp);
      }
    }
    else if (getSchema().equals(comp.getContainer()))
    {
      BaseGlobalCleanup cleanHelper = null;
      // Only need to clean up references if the component being deleted is global scoped
      if (comp instanceof XSDElementDeclaration)
      {
        cleanHelper = new GlobalElementCleanup(comp);
      }
      else if (comp instanceof XSDModelGroupDefinition)
      {
        cleanHelper = new GlobalGroupCleanup(comp);
      }
      else if (comp instanceof XSDTypeDefinition)
      {
        cleanHelper = new GlobalSimpleOrComplexTypeCleanup(comp);
      }
      else if (comp instanceof XSDAttributeDeclaration)
      {
        cleanHelper = new GlobalAttributeCleanup(comp);
      }
      else if (comp instanceof XSDAttributeGroupDefinition)
      {
        cleanHelper = new GlobalAttributeGroupCleanup(comp);
      }
      
      
      if (cleanHelper != null)
      {
        cleanHelper.visitSchema(getSchema());
      }
    }
    return refresh;
  }

}
