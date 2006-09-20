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

import org.eclipse.core.runtime.Assert;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDPatternFacet;
import org.eclipse.xsd.XSDSimpleTypeDefinition;

public class UpdateXSDPatternFacetCommand extends BaseCommand
{
  public static final int ADD = 0;
  public static final int DELETE = 1;
  public static final int UPDATE = 2;
  
  XSDSimpleTypeDefinition xsdSimpleTypeDefinition;
  String value;
  int actionType;
  XSDPatternFacet patternToEdit;
  
  public UpdateXSDPatternFacetCommand(String label, XSDSimpleTypeDefinition xsdSimpleTypeDefinition, int actionType)
  {
    super(label);
    this.xsdSimpleTypeDefinition = xsdSimpleTypeDefinition;
    this.actionType = actionType;
    
  }
  
  public void setValue(String value)
  {
    this.value = value;
  }
  
  public void setPatternToEdit(XSDPatternFacet patternToEdit)
  {
    this.patternToEdit = patternToEdit;
  }

  public void execute()
  {
    try
    {
      beginRecording(xsdSimpleTypeDefinition.getElement());
      if (actionType == ADD)
      {
        XSDPatternFacet pattern = XSDFactory.eINSTANCE.createXSDPatternFacet();
        pattern.setLexicalValue(value);
        xsdSimpleTypeDefinition.getFacetContents().add(pattern);
      }
      else if (actionType == DELETE)
      {
        Assert.isNotNull(patternToEdit);
        if (xsdSimpleTypeDefinition.getFacetContents().contains(patternToEdit))
          xsdSimpleTypeDefinition.getFacetContents().remove(patternToEdit);
      }
      else if (actionType == UPDATE)
      {
        Assert.isNotNull(patternToEdit);
        patternToEdit.setLexicalValue(value);
      }
      formatChild(xsdSimpleTypeDefinition.getElement());
    }
    finally
    {
      endRecording();
    }
  }
}
