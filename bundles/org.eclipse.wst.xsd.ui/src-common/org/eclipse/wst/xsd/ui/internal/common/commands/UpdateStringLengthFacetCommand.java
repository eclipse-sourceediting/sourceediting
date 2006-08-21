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
import org.eclipse.xsd.XSDLengthFacet;
import org.eclipse.xsd.XSDMaxLengthFacet;
import org.eclipse.xsd.XSDMinLengthFacet;
import org.eclipse.xsd.XSDSimpleTypeDefinition;

public class UpdateStringLengthFacetCommand extends BaseCommand
{
  XSDSimpleTypeDefinition xsdSimpleType;
  String max, min;
  private boolean doUpdateMax = false, doUpdateMin = false;

  public UpdateStringLengthFacetCommand(String label, XSDSimpleTypeDefinition xsdSimpleType)
  {
    super(label);
    this.xsdSimpleType = xsdSimpleType;
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
      XSDLengthFacet lengthFacet = xsdSimpleType.getEffectiveLengthFacet();
      XSDMinLengthFacet minLengthFacet = xsdSimpleType.getEffectiveMinLengthFacet();
      XSDMaxLengthFacet maxLengthFacet = xsdSimpleType.getEffectiveMaxLengthFacet();

      String currentLength = null, currentMin = null, currentMax = null;
      if (lengthFacet != null)
      {
        currentLength = lengthFacet.getLexicalValue();
      }
      if (minLengthFacet != null)
      {
        currentMin = minLengthFacet.getLexicalValue();
      }
      if (maxLengthFacet != null)
      {
        currentMax = maxLengthFacet.getLexicalValue();
      }

      if (doUpdateMax && !doUpdateMin)
      {
        if (maxLengthFacet != null)
        {
          if (max != null)
          {
            if (max.equals(currentMin))
            {
              lengthFacet = XSDFactory.eINSTANCE.createXSDLengthFacet();
              lengthFacet.setLexicalValue(max);
              xsdSimpleType.getFacetContents().add(lengthFacet);
              xsdSimpleType.getFacetContents().remove(maxLengthFacet);
              xsdSimpleType.getFacetContents().remove(minLengthFacet);
            }
            else
            {
              if (lengthFacet != null)
              {
                xsdSimpleType.getFacetContents().remove(lengthFacet);
              }
              if (minLengthFacet == null && currentLength != null)
              {
                minLengthFacet = XSDFactory.eINSTANCE.createXSDMinLengthFacet();
                minLengthFacet.setLexicalValue(currentLength);
                xsdSimpleType.getFacetContents().add(minLengthFacet);
              }
              maxLengthFacet.setLexicalValue(max);
            }
          }
          else
          {
            xsdSimpleType.getFacetContents().remove(maxLengthFacet);
          }
        }
        else
        {
          if (currentMin != null && currentMin.equals(max))
          {
            if (lengthFacet == null)
            {
              lengthFacet = XSDFactory.eINSTANCE.createXSDLengthFacet();
              xsdSimpleType.getFacetContents().add(lengthFacet);
            }
            lengthFacet.setLexicalValue(max);
            xsdSimpleType.getFacetContents().remove(minLengthFacet);
          }
          else if (currentLength != null && !currentLength.equals(max))
          {
            xsdSimpleType.getFacetContents().remove(lengthFacet);

            if (max != null)
            {
              maxLengthFacet = XSDFactory.eINSTANCE.createXSDMaxLengthFacet();
              maxLengthFacet.setLexicalValue(max);
              xsdSimpleType.getFacetContents().add(maxLengthFacet);
            }

            minLengthFacet = XSDFactory.eINSTANCE.createXSDMinLengthFacet();
            minLengthFacet.setLexicalValue(currentLength);
            xsdSimpleType.getFacetContents().add(minLengthFacet);
          }
          else
          {
            if (lengthFacet != null)
            {
              xsdSimpleType.getFacetContents().remove(lengthFacet);

              minLengthFacet = XSDFactory.eINSTANCE.createXSDMinLengthFacet();
              minLengthFacet.setLexicalValue(currentLength);
              xsdSimpleType.getFacetContents().add(minLengthFacet);

            }
            maxLengthFacet = XSDFactory.eINSTANCE.createXSDMaxLengthFacet();
            maxLengthFacet.setLexicalValue(max);
            xsdSimpleType.getFacetContents().add(maxLengthFacet);
          }
        }
      }
      else if (!doUpdateMax && doUpdateMin)
      {
        if (minLengthFacet != null)
        {
          if (min != null)
          {
            if (min.equals(currentMax))
            {
              lengthFacet = XSDFactory.eINSTANCE.createXSDLengthFacet();
              lengthFacet.setLexicalValue(min);
              xsdSimpleType.getFacetContents().add(lengthFacet);
              xsdSimpleType.getFacetContents().remove(maxLengthFacet);
              xsdSimpleType.getFacetContents().remove(minLengthFacet);
            }
            else
            {
              if (lengthFacet != null)
              {
                xsdSimpleType.getFacetContents().remove(lengthFacet);
              }
              if (maxLengthFacet == null && currentLength != null)
              {
                maxLengthFacet = XSDFactory.eINSTANCE.createXSDMaxLengthFacet();
                maxLengthFacet.setLexicalValue(currentLength);
                xsdSimpleType.getFacetContents().add(maxLengthFacet);
              }
              minLengthFacet.setLexicalValue(min);
            }
          }
          else
          {
            xsdSimpleType.getFacetContents().remove(minLengthFacet);
          }
        }
        else
        {
          if (currentMax != null && currentMax.equals(min))
          {
            if (lengthFacet == null)
            {
              lengthFacet = XSDFactory.eINSTANCE.createXSDLengthFacet();
              xsdSimpleType.getFacetContents().add(lengthFacet);
            }
            lengthFacet.setLexicalValue(min);
            xsdSimpleType.getFacetContents().remove(maxLengthFacet);
          }
          else if (currentLength != null && !currentLength.equals(min))
          {
            xsdSimpleType.getFacetContents().remove(lengthFacet);

            if (min != null)
            {
              minLengthFacet = XSDFactory.eINSTANCE.createXSDMinLengthFacet();
              minLengthFacet.setLexicalValue(min);
              xsdSimpleType.getFacetContents().add(minLengthFacet);
            }

            maxLengthFacet = XSDFactory.eINSTANCE.createXSDMaxLengthFacet();
            maxLengthFacet.setLexicalValue(currentLength);
            xsdSimpleType.getFacetContents().add(maxLengthFacet);
          }
          else
          {
            minLengthFacet = XSDFactory.eINSTANCE.createXSDMinLengthFacet();
            minLengthFacet.setLexicalValue(min);
            xsdSimpleType.getFacetContents().add(minLengthFacet);

            if (lengthFacet != null)
            {
              xsdSimpleType.getFacetContents().remove(lengthFacet);

              maxLengthFacet = XSDFactory.eINSTANCE.createXSDMaxLengthFacet();
              maxLengthFacet.setLexicalValue(currentLength);
              xsdSimpleType.getFacetContents().add(maxLengthFacet);
            }
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
