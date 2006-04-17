package org.eclipse.wst.xsd.ui.internal.common.commands;

import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDInclude;
import org.eclipse.xsd.XSDSchema;

public class AddXSDIncludeCommand extends AddXSDSchemaDirectiveCommand
{
  public AddXSDIncludeCommand(String label, XSDSchema schema)
  {
    super(label);
    this.xsdSchema = schema;
  }

  public void execute()
  {
    XSDInclude xsdInclude = XSDFactory.eINSTANCE.createXSDInclude();
    xsdInclude.setSchemaLocation("");
    xsdSchema.getContents().add(findNextPositionToInsert(), xsdInclude);
    addedXSDConcreteComponent = xsdInclude;
    formatChild(xsdSchema.getElement());
  }
}
