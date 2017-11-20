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
import org.eclipse.xsd.util.XSDResourceImpl;
public abstract class UpdateComponentReferenceAndManageDirectivesCommand extends BaseCommand
{
  protected XSDConcreteComponent concreteComponent;
  protected String componentName;
  protected String componentNamespace;
  protected IFile file;

  public UpdateComponentReferenceAndManageDirectivesCommand(XSDConcreteComponent concreteComponent, String componentName, String componentNamespace, IFile file)
  {
    this.concreteComponent = concreteComponent;
    this.componentName = componentName;
    this.componentNamespace = componentNamespace;
    this.file = file;
  }

  protected XSDComponent computeComponent()
  {
    XSDComponent result = null;
    XSDSchema schema = concreteComponent.getSchema();
    XSDSchemaDirective directive = null;

    // lets see if the element is already visible to our schema
    result = getDefinedComponent(schema, componentName, componentNamespace);
    if (result == null)
    {
      // TODO (cs) we need to provide a separate command to do this part
      //
      // apparently the element is not yet visible, we need to add
      // includes/imports to get to it
      if (componentNamespace != null && componentNamespace.equals(schema.getTargetNamespace()))
      {
        // we need to add an include
        // if the component's namespace is not null and matches the schema's
        // target namespace
        directive = XSDFactory.eINSTANCE.createXSDInclude();
      }
      else if (componentNamespace == null)
      {
        // we need to add an include
        // if the component's namespace is null, then we can just add it
        // only if the current namespace is not null
        directive = XSDFactory.eINSTANCE.createXSDInclude();

        // we have to ensure the schema for schema prefix is NOT null
        if (schema.getSchemaForSchemaQNamePrefix() == null)
        {
          String targetNS = schema.getTargetNamespace();
          if (targetNS == null)
            targetNS = "";
          // this will just update the schema for schema prefix to be, say, xsd
          UpdateNamespaceInformationCommand command = new UpdateNamespaceInformationCommand("", schema, "", targetNS);
          command.execute();
        }
      }
      else
      {
        // we need to add an import
        XSDImport xsdImport = XSDFactory.eINSTANCE.createXSDImport();
        xsdImport.setNamespace(componentNamespace);
        directive = xsdImport;
      }

      String location = computeNiceLocation(schema.getSchemaLocation(), file);
      directive.setSchemaLocation(location);
      // TODO (cs) we should at the directive 'next' in the list of directives
      // for now I'm just adding as the first thing in the schema :-(
      //
      schema.getContents().add(0, directive);
      XSDSchema resolvedSchema = directive.getResolvedSchema();
      if (resolvedSchema == null)
      {
        String platformLocation = "platform:/resource" + file.getFullPath();
        Resource resource = concreteComponent.eResource().getResourceSet().createResource(URI.createURI(platformLocation));
        if (resource instanceof XSDResourceImpl)
        {
          try
          {
            resource.load(null);
            XSDResourceImpl resourceImpl = (XSDResourceImpl) resource;
            resolvedSchema = resourceImpl.getSchema();
            if (resolvedSchema != null)
            {
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
        // TODO (cs) consider setting some error state so that the client can
        // provide a pop-dialog error
        // we should also remove the import/include so save from cluttering
        // the file with bogus directives
      }
    }

    return result;
  }
  
  private final static String PLATFORM_RESOURCE_PREFIX = "platform:/resource";
  private String computeNiceLocation(String baseLocation, IFile file)
  {     
    if (baseLocation.startsWith(PLATFORM_RESOURCE_PREFIX))
    {
      URI baseURI = URI.createURI(baseLocation);
      URI fileURI = URI.createPlatformResourceURI(file.getFullPath().toString());
      URI relative = fileURI.deresolve(baseURI, false, true, true);      
      return relative.toString();
    }
    else
    {
      URI baseURI = URI.createURI(baseLocation);          
      URI fileURI = URI.createFileURI(file.getLocation().toOSString());
      URI relative = fileURI.deresolve(baseURI, false, true, true);     
      return relative.toString();    
    } 
  }

  protected abstract XSDComponent getDefinedComponent(XSDSchema schema, String componentName, String componentNamespace);

  public abstract void execute();
}
