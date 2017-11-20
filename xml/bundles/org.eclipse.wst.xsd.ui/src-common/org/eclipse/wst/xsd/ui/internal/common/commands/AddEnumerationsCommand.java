/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.commands;

import java.util.List;

import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDEnumerationFacetAction;
import org.eclipse.xsd.XSDEnumerationFacet;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.util.XSDSchemaBuildingTools;

public class AddEnumerationsCommand extends BaseCommand
{
  XSDSimpleTypeDefinition simpleType;
  String value;
  // The index of the currently selected item.  If no item is selected, index will be less than 0
  private int index = -1;  
  // Determines where the element should be inserted based on the currently selected element.  If no
  // element is selected, use the default behaviour of appending the element to the end
  private String addEnumerationLocation;
  
  public AddEnumerationsCommand(String label, XSDSimpleTypeDefinition simpleType)
  {
    super(label);
    this.simpleType = simpleType;
  }

  public AddEnumerationsCommand(String label, XSDSimpleTypeDefinition simpleType, String ID, int index)
  {
    super(label);
    this.simpleType = simpleType;
    this.index = index;
    this.addEnumerationLocation = ID;
  }

  public void setValue(String value)
  {
    this.value = value; 
  }

  public void execute()
  {
    try
    {
      beginRecording(simpleType.getElement());
      XSDFactory factory = XSDSchemaBuildingTools.getXSDFactory();
      XSDEnumerationFacet enumerationFacet = factory.createXSDEnumerationFacet();
      enumerationFacet.setLexicalValue(value);
      
      index = getInsertionIndex();
      List facets = simpleType.getFacetContents();
      if (index >=0 && index < facets.size())
      {
        facets.add(index, enumerationFacet);
      }
      else
      {
        facets.add(enumerationFacet);
      }
      formatChild(simpleType.getElement());
      addedXSDConcreteComponent = enumerationFacet;
    }
    finally
    {
      endRecording();
    }
  }
  
  protected int getInsertionIndex()
  {
    if (index < 0)
      return -1;

    if (addEnumerationLocation.equals(AddXSDEnumerationFacetAction.BEFORE_SELECTED_ID))
    {
      return index;
    }
    else if (addEnumerationLocation.equals(AddXSDEnumerationFacetAction.AFTER_SELECTED_ID))
    {
      index++;
      return index;
    }
    else
    {
      return -1;
    }
  }

}
