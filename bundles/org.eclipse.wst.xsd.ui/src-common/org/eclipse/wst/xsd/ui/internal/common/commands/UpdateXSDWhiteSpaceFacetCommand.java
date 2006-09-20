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

import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDWhiteSpace;
import org.eclipse.xsd.XSDWhiteSpaceFacet;

public class UpdateXSDWhiteSpaceFacetCommand extends BaseCommand
{
  XSDSimpleTypeDefinition xsdSimpleTypeDefinition;
  boolean doAddFacet;
  
  public UpdateXSDWhiteSpaceFacetCommand(String label, XSDSimpleTypeDefinition xsdSimpleType, boolean doAddFacet)
  {
    super(label);
    this.xsdSimpleTypeDefinition = xsdSimpleType;
    this.doAddFacet = doAddFacet;
  }

  public void execute()
  {
    try
    {
      beginRecording(xsdSimpleTypeDefinition.getElement());
      XSDWhiteSpaceFacet whitespaceFacet = xsdSimpleTypeDefinition.getWhiteSpaceFacet();

      if (doAddFacet)
      {
        if (whitespaceFacet == null)
        {
          whitespaceFacet = XSDFactory.eINSTANCE.createXSDWhiteSpaceFacet();
          xsdSimpleTypeDefinition.getFacetContents().add(whitespaceFacet);
        }
        whitespaceFacet.setLexicalValue(XSDWhiteSpace.COLLAPSE_LITERAL.getName());
      }
      else
      {
        if (whitespaceFacet != null)
        {
          xsdSimpleTypeDefinition.getFacetContents().remove(whitespaceFacet);
        }
      }
      formatChild(xsdSimpleTypeDefinition.getElement());
    }
    finally
    {
      endRecording();
    }
  }
}
