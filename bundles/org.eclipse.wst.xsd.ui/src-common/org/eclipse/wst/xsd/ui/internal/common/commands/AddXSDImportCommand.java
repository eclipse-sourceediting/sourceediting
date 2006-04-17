package org.eclipse.wst.xsd.ui.internal.common.commands;

import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDSchema;

public class AddXSDImportCommand extends AddXSDSchemaDirectiveCommand
{
  public AddXSDImportCommand(String label, XSDSchema schema)
  {
    super(label);
    this.xsdSchema = schema;
  }

  public void execute()
  {
    XSDImport xsdImport = XSDFactory.eINSTANCE.createXSDImport();
    xsdSchema.getContents().add(findNextPositionToInsert(), xsdImport);
    addedXSDConcreteComponent = xsdImport;
    formatChild(xsdSchema.getElement());
  }
}
