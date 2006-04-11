package org.eclipse.wst.xsd.ui.internal.common.commands;

import org.eclipse.core.resources.IFile;
import org.eclipse.gef.commands.Command;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDConcreteComponent;

public abstract class UpdateComponentReferenceAndManageDirectivesCommand extends Command{
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

	  protected abstract XSDComponent computeComponent();
	  
	  public abstract void execute() ;

}
