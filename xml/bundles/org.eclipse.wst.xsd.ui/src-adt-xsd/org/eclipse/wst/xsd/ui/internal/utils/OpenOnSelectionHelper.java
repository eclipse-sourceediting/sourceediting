/*******************************************************************************
 * Copyright (c) 2001, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.utils;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.common.uriresolver.internal.util.URIHelper;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.xsd.ui.internal.common.actions.OpenInNewEditor;
import org.eclipse.wst.xsd.ui.internal.editor.InternalXSDMultiPageEditor;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaDirective;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;


// issue (cs) can we remove this?
//
public class OpenOnSelectionHelper
{
  
  protected StructuredTextEditor textEditor;
  protected XSDSchema xsdSchema;

 
  public OpenOnSelectionHelper(StructuredTextEditor textEditor, XSDSchema xsdSchema)
  {
  	this.textEditor = textEditor;
  	this.xsdSchema = xsdSchema;
  }
  

  boolean lastResult;
  
  public static void openXSDEditor(XSDSchema schema)
  {
    IEditorInput editorInput = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput();
    
    OpenInNewEditor.openXSDEditor(editorInput, schema, schema);    
   
  }
  
  public static void openXSDEditor(String schemaLocation)
  {
    IPath schemaPath = new Path(schemaLocation);
		final IFile schemaFile = ResourcesPlugin.getWorkspace().getRoot().getFile(schemaPath);
	
		Display.getDefault().asyncExec(new Runnable()
		{
			/**
			 * @see java.lang.Runnable#run()
			 */
			public void run()
			{
				final IWorkbenchWindow workbenchWindow = XSDEditorPlugin.getPlugin().getWorkbench().getActiveWorkbenchWindow();
				if (workbenchWindow != null)
				{
					try
					{
					  workbenchWindow.getActivePage().openEditor(new FileEditorInput(schemaFile), XSDEditorPlugin.EDITOR_ID);
					}
					catch (PartInitException initEx)
					{
					  initEx.printStackTrace();
					}
					catch(Exception e)
					{
					  e.printStackTrace();
					}
				}          
			}
		});
  }
  
  protected boolean revealObject(final XSDConcreteComponent component)
  {
    if (component.getRootContainer().equals(xsdSchema))
    {
      Node element = component.getElement();
      if (element instanceof IndexedRegion)
      {
        IndexedRegion indexNode = (IndexedRegion) element;
        textEditor.getTextViewer().setRangeIndication(indexNode.getStartOffset(), indexNode.getEndOffset() - indexNode.getStartOffset(), true);
        return true;
      }
      return false;
    }
    else
    {
      lastResult = false;
      if (component.getSchema() != null)
      {
				String schemaLocation = URIHelper.removePlatformResourceProtocol(component.getSchema().getSchemaLocation());
        IPath schemaPath = new Path(schemaLocation);
				final IFile schemaFile = ResourcesPlugin.getWorkspace().getRoot().getFile(schemaPath);
        Display.getDefault().syncExec(new Runnable()
        {
	        /**
	         * @see java.lang.Runnable#run()
	         */
	        public void run()
	        {
		        final IWorkbenchWindow workbenchWindow = XSDEditorPlugin.getPlugin().getWorkbench().getActiveWorkbenchWindow();
		        if (workbenchWindow != null)
		        {
							try
							{
								IEditorPart editorPart = workbenchWindow.getActivePage().openEditor(new FileEditorInput(schemaFile), XSDEditorPlugin.PLUGIN_ID);
								if (editorPart instanceof InternalXSDMultiPageEditor)
								{
									((InternalXSDMultiPageEditor)editorPart).openOnGlobalReference(component);
									lastResult = true;
								}
							}
							catch (PartInitException initEx)
							{
							}
						}          
					}
				});
      }
      return lastResult;
    }
  }
  
  public XSDNamedComponent openOnGlobalReference(XSDConcreteComponent comp)
  {
    XSDSchema schema = xsdSchema;
    String name = null;
    String namespace = null;
    if (comp instanceof XSDNamedComponent)
    {
      name = ((XSDNamedComponent) comp).getName();
      namespace = ((XSDNamedComponent) comp).getTargetNamespace();
    }
    if (name == null)
    {
      // For Anonymous types, just show the element
      if (comp instanceof XSDTypeDefinition)
      {
        XSDTypeDefinition type = (XSDTypeDefinition) comp;
        comp = type.getContainer();
        if (comp instanceof XSDNamedComponent)
        {
          name = ((XSDNamedComponent) comp).getName();
          namespace = ((XSDNamedComponent) comp).getTargetNamespace();
        }
      }
    }
    
    if (schema == null || name == null)
    {
      return null;
    }

    List objects = null;    
    if (comp instanceof XSDElementDeclaration)
    {
      objects = schema.getElementDeclarations();
    }
    else if (comp instanceof XSDTypeDefinition)
    {
      objects = schema.getTypeDefinitions();
    }
    else if (comp instanceof XSDAttributeGroupDefinition)
    {
      objects = schema.getAttributeGroupDefinitions();
    }
    else if (comp instanceof XSDIdentityConstraintDefinition)
    {
      objects = schema.getIdentityConstraintDefinitions();
    }
    else if (comp instanceof XSDModelGroupDefinition)
    {
      objects = schema.getModelGroupDefinitions();
    }
    else if (comp instanceof XSDAttributeDeclaration)
    {
      objects = schema.getAttributeDeclarations();
    }

	if (objects != null)
	{
		if (namespace != null)
		{
			// First, look for a namespace and name match
			for (Iterator iter = objects.iterator(); iter.hasNext();)
			{
				XSDNamedComponent namedComp = (XSDNamedComponent) iter.next();
				String targetNamespace = namedComp.getTargetNamespace();
				if (namedComp.getName().equals(name) && targetNamespace != null && targetNamespace.equals(namespace))
				{
					revealObject(namedComp);
					return namedComp;
				}
			}
		}

		// Next, look for just a name match
		for (Iterator iter = objects.iterator(); iter.hasNext();)
		{
			XSDNamedComponent namedComp = (XSDNamedComponent) iter.next();
			if (namedComp.getName().equals(name))
			{
				revealObject(namedComp);
				return namedComp;
			}
		}
	}
    return null;
  }
  
  public boolean openOnSelection()
  {
    List selectedNodes = null;
    ISelection selection = textEditor.getSelectionProvider().getSelection();
    if (selection instanceof IStructuredSelection) {
      selectedNodes = ((IStructuredSelection) selection).toList();
    }

    if (selectedNodes != null && !selectedNodes.isEmpty())
    {
      for (Iterator i = selectedNodes.iterator(); i.hasNext();)
      {
        Object obj = i.next();
        if (xsdSchema != null)
        {
          XSDConcreteComponent xsdComp = xsdSchema.getCorrespondingComponent((Node)obj);
          XSDConcreteComponent objectToReveal = null;

          if (xsdComp instanceof XSDElementDeclaration)
          {
            XSDElementDeclaration elementDecl = (XSDElementDeclaration) xsdComp;
            if (elementDecl.isElementDeclarationReference())
            {
              objectToReveal = elementDecl.getResolvedElementDeclaration();
            }
            else
            {
              XSDConcreteComponent typeDef = null;
              if (elementDecl.getAnonymousTypeDefinition() == null)
              {
                typeDef = elementDecl.getTypeDefinition();
              }
              
              XSDConcreteComponent subGroupAffiliation = elementDecl.getSubstitutionGroupAffiliation();
              
              if (typeDef != null && subGroupAffiliation != null)
              {
                // we have 2 things we can navigate to, if the cursor is anywhere on the substitution attribute
                // then jump to that, otherwise just go to the typeDef.
                if (obj instanceof Attr && ((Attr)obj).getLocalName().equals(XSDConstants.SUBSTITUTIONGROUP_ATTRIBUTE))
                {
                  objectToReveal = subGroupAffiliation;
                }
                else
                {
                  // try to reveal the type now.  On success, then we return true.
                  // if we fail, set the substitution group as the object to reveal as a backup plan.
                  if (revealObject(typeDef))
                  {
                    return true;
                  }
                  else
                  {
                    objectToReveal = subGroupAffiliation;
                  }
                }
              }
              else
              {
                // one or more of these is null.  If the typeDef is non-null, use it.  Otherwise
                // try and use the substitution group
                objectToReveal = typeDef != null ? typeDef : subGroupAffiliation;
              }
            }
          }
          else if (xsdComp instanceof XSDModelGroupDefinition)
          {
            XSDModelGroupDefinition elementDecl = (XSDModelGroupDefinition) xsdComp;
            if (elementDecl.isModelGroupDefinitionReference())
            {
              objectToReveal = elementDecl.getResolvedModelGroupDefinition();
            }
          }
          else if (xsdComp instanceof XSDAttributeDeclaration)
          {
            XSDAttributeDeclaration attrDecl = (XSDAttributeDeclaration) xsdComp;
            if (attrDecl.isAttributeDeclarationReference())
            {
              objectToReveal = attrDecl.getResolvedAttributeDeclaration();
            }
            else if (attrDecl.getAnonymousTypeDefinition() == null)
            {
              objectToReveal = attrDecl.getTypeDefinition();
            }              
          }
          else if (xsdComp instanceof XSDAttributeGroupDefinition)
          {
            XSDAttributeGroupDefinition attrGroupDef = (XSDAttributeGroupDefinition) xsdComp;
            if (attrGroupDef.isAttributeGroupDefinitionReference())
            {
              objectToReveal = attrGroupDef.getResolvedAttributeGroupDefinition();
            }
          }
          else if (xsdComp instanceof XSDIdentityConstraintDefinition)
          {
            XSDIdentityConstraintDefinition idConstraintDef = (XSDIdentityConstraintDefinition) xsdComp;
            if (idConstraintDef.getReferencedKey() != null)
            {
              objectToReveal = idConstraintDef.getReferencedKey();
            }
          }
          else if (xsdComp instanceof XSDSimpleTypeDefinition)
          {
            XSDSimpleTypeDefinition typeDef = (XSDSimpleTypeDefinition) xsdComp;
            objectToReveal = typeDef.getItemTypeDefinition();
            if (objectToReveal == null)
            {
              // if itemType attribute is not set, then check for memberType
              List memberTypes = typeDef.getMemberTypeDefinitions();
              if (memberTypes != null && memberTypes.size() > 0)
              {
                objectToReveal = (XSDConcreteComponent)memberTypes.get(0);
              }              
            }
          }
          else if (xsdComp instanceof XSDTypeDefinition)
          {
            XSDTypeDefinition typeDef = (XSDTypeDefinition) xsdComp;
            objectToReveal = typeDef.getBaseType();
          }
          else if (xsdComp instanceof XSDSchemaDirective)
          {
          	XSDSchemaDirective directive = (XSDSchemaDirective) xsdComp;
//						String schemaLocation = URIHelper.removePlatformResourceProtocol(directive.getResolvedSchema().getSchemaLocation());
//						openXSDEditor(schemaLocation);
//						return false;
            objectToReveal = directive.getResolvedSchema();						          	          	
          }

          // now reveal the object if this isn't null
          if (objectToReveal != null)
          {
            return revealObject(objectToReveal);
          }
        }
      }
    }
    return false;
  }

}
