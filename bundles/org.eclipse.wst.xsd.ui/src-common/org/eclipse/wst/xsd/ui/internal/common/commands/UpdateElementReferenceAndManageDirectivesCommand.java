package org.eclipse.wst.xsd.ui.internal.common.commands;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaDirective;
import org.eclipse.xsd.util.XSDResourceImpl;

public class UpdateElementReferenceAndManageDirectivesCommand extends 
UpdateComponentReferenceAndManageDirectivesCommand{

	public UpdateElementReferenceAndManageDirectivesCommand(XSDConcreteComponent concreteComponent,
			String componentName, String componentNamespace, IFile file)
	{
		super(concreteComponent, componentName, componentNamespace, file);
	}
	
	protected XSDComponent computeComponent()
	{
		XSDElementDeclaration result = null;
		XSDSchema schema = concreteComponent.getSchema();
		XSDSchemaDirective directive = null;
		
		// TODO (cs) handle case where namespace==null
		//
		if (componentNamespace != null)
		{  
			
			// lets see if the element is already visible to our schema
			result = getDefinedComponent(schema, componentName, componentNamespace);                     
			if (result == null)
			{
				// TODO (cs) we need to provide a separate command to do this part
				//
				// apparently the element is not yet visible, we need to add includes/imports to get to it
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
		return result;
	}

	private XSDElementDeclaration getDefinedComponent(XSDSchema schema,
			String componentName, String componentNamespace) {
	    XSDElementDeclaration result = schema.resolveElementDeclaration(componentNamespace, componentName);
	    if (result.eContainer() == null)
	    {
	      result = null;
	    }
	    return result;
	}

	public void execute() {
	    try
	    {
	    XSDComponent elementDef = computeComponent();
	    if (elementDef != null)
	    {
	      UpdateElementReferenceCommand command = new UpdateElementReferenceCommand(
	    		  "Update Element Reference", (XSDElementDeclaration) concreteComponent,
	    		  (XSDElementDeclaration) elementDef);
	      command.execute();
	    }
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	    }
	}

}
