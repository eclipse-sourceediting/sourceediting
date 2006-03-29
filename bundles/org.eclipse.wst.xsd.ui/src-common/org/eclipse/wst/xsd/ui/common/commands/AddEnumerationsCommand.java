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
package org.eclipse.wst.xsd.ui.common.commands;

import org.eclipse.xsd.XSDEnumerationFacet;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.util.XSDSchemaBuildingTools;

public class AddEnumerationsCommand extends BaseCommand
{
  XSDSimpleTypeDefinition simpleType;
  String value;
  
  public AddEnumerationsCommand(String label, XSDSimpleTypeDefinition simpleType)
  {
    super(label);
    this.simpleType = simpleType;
  }
  
  public void setValue(String value)
  {
    this.value = value; 
  }

  public void execute()
  {
    XSDFactory factory = XSDSchemaBuildingTools.getXSDFactory();
    XSDEnumerationFacet enumerationFacet = factory.createXSDEnumerationFacet();
    enumerationFacet.setLexicalValue(value);
    simpleType.getFacetContents().add(enumerationFacet);
    formatChild(simpleType.getElement());
  }
}
