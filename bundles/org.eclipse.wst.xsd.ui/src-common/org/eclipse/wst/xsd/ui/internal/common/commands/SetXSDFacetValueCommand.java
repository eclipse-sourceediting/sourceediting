/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.commands;

import org.eclipse.xsd.XSDFacet;

public class SetXSDFacetValueCommand extends BaseCommand
{
  protected XSDFacet facet;
  protected String value;
  
  public SetXSDFacetValueCommand(String label, XSDFacet facet)
  {
    super(label);
    this.facet = facet;
  }
  
  public SetXSDFacetValueCommand(String label, XSDFacet facet, String value)
  {
    super(label);
    this.facet = facet;
    this.value = value;
  }
  
  public void setValue(String value)
  {
    this.value = value; 
  }
  
  public void execute()
  {
    try
    {
      beginRecording(facet.getElement());
      facet.setLexicalValue(value);
    }
    finally
    {
      endRecording();
    }
  }
}
