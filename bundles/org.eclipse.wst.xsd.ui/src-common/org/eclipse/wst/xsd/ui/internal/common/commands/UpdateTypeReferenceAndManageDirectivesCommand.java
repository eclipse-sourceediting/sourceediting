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
package org.eclipse.wst.xsd.ui.internal.common.commands;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaDirective;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;
import org.eclipse.xsd.util.XSDResourceImpl;

public class UpdateTypeReferenceAndManageDirectivesCommand extends UpdateComponentReferenceAndManageDirectivesCommand
{

  public UpdateTypeReferenceAndManageDirectivesCommand(XSDConcreteComponent concreteComponent,
		  String componentName, String componentNamespace, IFile file)
  {
	  super(concreteComponent, componentName, componentNamespace, file);
  }

  public XSDComponent computeComponent()
  {
    XSDComponent result = null;
    XSDSchema schema = concreteComponent.getSchema();
    XSDSchemaDirective directive = null;
    
    // TODO (cs) handle case where namespace==null
    //
    if (componentNamespace != null)
    {  
      if (XSDConstants.isSchemaForSchemaNamespace(componentNamespace))
      {
        // this is the easy case, its just a built-in type
        //
        result = getDefinedComponent(schema, componentName, componentNamespace);        
      }  
      else
      {
        // lets see if the type is already visible to our schema
        result = getDefinedComponent(schema, componentName, componentNamespace);                     
        if (result == null)
        {
          // TODO (cs) we need to provide a separate command to do this part
          // TODO (trung) directives in the outline view does not get updated
          // apparently the type is not yet visible, we need to add includes/imports to get to it
          if (componentNamespace.equals(schema.getTargetNamespace()))
          {
            // we need to add an include
            directive =XSDFactory.eINSTANCE.createXSDInclude();
          }
          else
          {
            // we need to add an import
            XSDImport xsdImport = XSDFactory.eINSTANCE.createXSDImport();
            xsdImport.setNamespace(componentNamespace);
            directive = xsdImport;              
          }  
          
          // TODO (cs) we need to compute a relative URI to make this nicer!
          //
          String fileURI = file.getLocationURI().toString();
          if (fileURI.startsWith("file:/") && !fileURI.startsWith("file:///"))
          {
            fileURI = "file:///" + fileURI.substring(6);
          }   
          directive.setSchemaLocation(fileURI);
          
          // TODO (cs) we should at the directive 'next' in the list of directives
          // for now I'm just adding as the first thing in the schema :-(         
          //
          schema.getContents().add(0, directive);           
          XSDSchema resolvedSchema = directive.getResolvedSchema();        
         
          if (resolvedSchema == null)
          {
            System.out.println("resolvedSchema is null!!!");
            Resource resource  = concreteComponent.eResource().getResourceSet().createResource(URI.createURI(fileURI));          
            if (resource instanceof XSDResourceImpl)
            {
              try
              {
              resource.load(null);
              XSDResourceImpl resourceImpl = (XSDResourceImpl)resource;
              resolvedSchema = resourceImpl.getSchema();
              if (resolvedSchema != null)
              {
                System.out.println("Strange... managed to make the schema resovle myself 8-P" + resource);
                directive.setResolvedSchema(resolvedSchema);
              }
              }
              catch (Exception e)
              {
                
              }
            }  
          }
          if (resolvedSchema != null)
          {  
            result = getDefinedComponent(resolvedSchema, componentName, componentNamespace);
          }    
          else 
          {
            // TODO (cs) consider setting some error state so that the client can provide a  pop-dialog error
            // we should also remove the import/include so save from cluttering the file with bogus directives                           
          }
        }
      }   
    }  
    return result;
  }
 
  
  private XSDTypeDefinition getDefinedComponent(XSDSchema schema, String componentName, String componentNamespace)
  {
    XSDTypeDefinition result = schema.resolveTypeDefinition(componentNamespace, componentName);
    if (result.eContainer() == null)
    {
      result = null;
    }      
    return result;
  }
  
  
  public void execute()
  {
    try
    {
    XSDComponent td = computeComponent();
    if (td != null && td instanceof XSDTypeDefinition)
    {
      UpdateTypeReferenceCommand command = new UpdateTypeReferenceCommand(
    		  concreteComponent, (XSDTypeDefinition) td);
      command.execute();
    }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
