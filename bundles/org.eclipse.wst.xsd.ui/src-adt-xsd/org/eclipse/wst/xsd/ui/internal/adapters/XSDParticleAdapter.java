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
package org.eclipse.wst.xsd.ui.internal.adapters;

import org.eclipse.wst.xsd.ui.internal.adt.design.IAnnotationProvider;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDParticle;

public class XSDParticleAdapter extends XSDBaseAdapter implements IAnnotationProvider
{
  public XSDParticleAdapter()
  {
    super();
  }

  public int getMaxOccurs()
  {
    return getMaxOccurs((XSDConcreteComponent) target);
  }

  public int getMinOccurs()
  {
    return getMinOccurs((XSDConcreteComponent) target);
  }

  public static int getMinOccurs(XSDConcreteComponent component)
  {
    int minOccur = -2;
    if (component != null)
    {
      Object o = component.getContainer();
      if (o instanceof XSDParticle)
      {
        if (((XSDParticle) o).isSetMinOccurs())
        {
          try
          {
            minOccur = ((XSDParticle) o).getMinOccurs();
          }
          catch (Exception e)
          {
          }
        }
      }
    }
    return minOccur;
  }

  public static int getMaxOccurs(XSDConcreteComponent component)
  {
    int maxOccur = -2;
    if (component != null)
    {
      Object o = component.getContainer();
      if (o instanceof XSDParticle)
      {
        if (((XSDParticle) o).isSetMaxOccurs())
        {
          try
          {
            maxOccur = ((XSDParticle) o).getMaxOccurs();
          }
          catch (Exception e)
          {
          }
        }
      }
    }
    return maxOccur;
  }

  public String getNameAnnotationString()
  {
    return buildAnnotationString(true);
  }

  public String getNameAnnotationToolTipString()
  {
    return buildAnnotationString(false);
  }

  public String getTypeAnnotationString()
  {
    return null;
  }

  public String getTypeAnnotationToolTipString()
  {
    return null;
  }

  protected String buildAnnotationString(boolean isForLabel)
  {
    String occurenceDescription = "";
    String toolTipDescription = "";
    // TODO: set int values as defined constants
    // -2 means the user didn't specify (so the default is 1)
    int minOccurs = getMinOccurs();
    int maxOccurs = getMaxOccurs();

    // This is for the attribute field case, which has no
    // occurrence attributes
    if (minOccurs == -3 && maxOccurs == -3)
    {
      occurenceDescription = "";
    }
    else if (minOccurs == 0 && (maxOccurs == -2 || maxOccurs == 1))
    {
      occurenceDescription = "[0..1]";
      toolTipDescription = "optional";
    }
    else if (minOccurs == 0 && maxOccurs == -1)
    {
      occurenceDescription = "[0..*]";
      toolTipDescription = "Zero or more";
    }
    else if ((minOccurs == 1 && maxOccurs == -1) || (minOccurs == -2 && maxOccurs == -1))
    {
      occurenceDescription = "[1..*]";
      toolTipDescription = "One or more";
    }
    else if ((minOccurs == 1 && maxOccurs == 1) || (minOccurs == -2 && maxOccurs == 1) || (minOccurs == 1 && maxOccurs == -2))
    {
      occurenceDescription = "[1..1]";
      toolTipDescription = "required";
    }
    else if (minOccurs == -2 && maxOccurs == -2)
    {
      occurenceDescription = "";
      // none specified, so don't have any toolTip description
    }
    else
    {
      if (maxOccurs == -2)
        maxOccurs = 1;
      String maxSymbol = maxOccurs == -1 ? "*" : "" + maxOccurs;
      String minSymbol = minOccurs == -2 ? "1" : "" + minOccurs;
      occurenceDescription = "[" + minSymbol + ".." + maxSymbol + "]";
      toolTipDescription = "array";
    }

    if (isForLabel)
    {
      return occurenceDescription;
    }
    else
    {
      return toolTipDescription;
    }
  }

}
