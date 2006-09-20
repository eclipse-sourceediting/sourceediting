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
import org.eclipse.xsd.XSDMaxExclusiveFacet;
import org.eclipse.xsd.XSDMaxInclusiveFacet;
import org.eclipse.xsd.XSDMinExclusiveFacet;
import org.eclipse.xsd.XSDMinInclusiveFacet;
import org.eclipse.xsd.XSDSimpleTypeDefinition;

public class UpdateNumericBoundsFacetCommand extends BaseCommand
{
  XSDSimpleTypeDefinition xsdSimpleType;
  String max, min;
  boolean includeMin, includeMax;
  private boolean doUpdateMax = false, doUpdateMin = false;
  XSDMinInclusiveFacet minInclusiveFacet;
  XSDMinExclusiveFacet minExclusiveFacet;
  XSDMaxInclusiveFacet maxInclusiveFacet;
  XSDMaxExclusiveFacet maxExclusiveFacet;


  public UpdateNumericBoundsFacetCommand(String label, XSDSimpleTypeDefinition xsdSimpleType, boolean includeMin, boolean includeMax)
  {
    super(label);
    this.xsdSimpleType = xsdSimpleType;
    this.includeMin = includeMin;
    this.includeMax = includeMax;
    
    minInclusiveFacet = xsdSimpleType.getMinInclusiveFacet();
    minExclusiveFacet = xsdSimpleType.getMinExclusiveFacet();
    maxInclusiveFacet = xsdSimpleType.getMaxInclusiveFacet();
    maxExclusiveFacet = xsdSimpleType.getMaxExclusiveFacet();

  }

  public void setMin(String min)
  {
    this.min = min;
    doUpdateMin = true;
  }
  
  public void setMax(String max)
  {
    this.max = max;
    doUpdateMax = true;
  }

  public void execute()
  {
    try
    {
      beginRecording(xsdSimpleType.getElement());

      if (doUpdateMin)
      {
        if (includeMin)
        {
          if (minInclusiveFacet == null && min != null)
          {
            minInclusiveFacet = XSDFactory.eINSTANCE.createXSDMinInclusiveFacet();
            minInclusiveFacet.setLexicalValue(min);
            xsdSimpleType.getFacetContents().add(minInclusiveFacet);

            if (minExclusiveFacet != null)
            {
              xsdSimpleType.getFacetContents().remove(minExclusiveFacet);
            }
          }
          else if (minInclusiveFacet != null && min != null)
          {
            minInclusiveFacet.setLexicalValue(min);
          }
          else if (minInclusiveFacet != null && min == null)
          {
            xsdSimpleType.getFacetContents().remove(minInclusiveFacet);
          }
        }
        else
        // !includeMin
        {
          if (minExclusiveFacet == null && min != null)
          {
            minExclusiveFacet = XSDFactory.eINSTANCE.createXSDMinExclusiveFacet();
            minExclusiveFacet.setLexicalValue(min);
            xsdSimpleType.getFacetContents().add(minExclusiveFacet);

            if (minInclusiveFacet != null)
            {
              xsdSimpleType.getFacetContents().remove(minInclusiveFacet);
            }
          }
          else if (minExclusiveFacet != null && min != null)
          {
            minExclusiveFacet.setLexicalValue(min);
          }
          else if (minExclusiveFacet != null && min == null)
          {
            xsdSimpleType.getFacetContents().remove(minExclusiveFacet);
          }
        }
      }
      else if (doUpdateMax)
      {
        if (includeMax)
        {
          if (maxInclusiveFacet == null && max != null)
          {
            maxInclusiveFacet = XSDFactory.eINSTANCE.createXSDMaxInclusiveFacet();
            maxInclusiveFacet.setLexicalValue(max);
            xsdSimpleType.getFacetContents().add(maxInclusiveFacet);

            if (maxExclusiveFacet != null)
            {
              xsdSimpleType.getFacetContents().remove(maxExclusiveFacet);
            }
          }
          else if (maxInclusiveFacet != null && max != null)
          {
            maxInclusiveFacet.setLexicalValue(max);
          }
          else if (maxInclusiveFacet != null && max == null)
          {
            xsdSimpleType.getFacetContents().remove(maxInclusiveFacet);
          }
        }
        else
        // !includeMax
        {
          if (maxExclusiveFacet == null && max != null)
          {
            maxExclusiveFacet = XSDFactory.eINSTANCE.createXSDMaxExclusiveFacet();
            maxExclusiveFacet.setLexicalValue(max);
            xsdSimpleType.getFacetContents().add(maxExclusiveFacet);

            if (maxInclusiveFacet != null)
            {
              xsdSimpleType.getFacetContents().remove(maxInclusiveFacet);
            }
          }
          else if (maxExclusiveFacet != null && max != null)
          {
            maxExclusiveFacet.setLexicalValue(max);
          }
          else if (maxExclusiveFacet != null && max == null)
          {
            xsdSimpleType.getFacetContents().remove(maxExclusiveFacet);
          }
        }
      }

      formatChild(xsdSimpleType.getElement());
    }
    finally
    {
      endRecording();
    }
  }  
}
