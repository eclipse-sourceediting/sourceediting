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
package org.eclipse.wst.xsd.ui.internal.common.properties.sections;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.wst.xsd.ui.internal.common.commands.UpdateMaxOccursCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.UpdateMinOccursCommand;
import org.eclipse.wst.xsd.ui.internal.common.util.Messages;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDParticleContent;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;


public class MultiplicitySection extends RefactoringSection
{
  protected CCombo minCombo, maxCombo;

  public MultiplicitySection()
  {
    super();
  }

  protected void createContents(Composite parent)
  {
  }

  
  public void doHandleEvent(Event event)
  {
    if (event.widget == minCombo)
    {
      updateMinAttribute();
    }
    else if (event.widget == maxCombo)
    {
      updateMaxAttribute();
    }
  }

  public void doWidgetSelected(SelectionEvent e)
  {
    if (e.widget == minCombo)
    {
      updateMinAttribute();
    }
    else if (e.widget == maxCombo)
    {
      updateMaxAttribute();
    }
    super.doWidgetSelected(e);
  }

  protected void updateMaxAttribute()
  {
    setErrorMessage(null);
    XSDParticle particle = null;

    if (input instanceof XSDParticleContent)
    {
      particle = getAssociatedParticle((XSDParticleContent) input);
    }
    if (particle != null)
    {
      String newValue = maxCombo.getText().trim();
      
      if (newValue.length() == 0)
      {
        particle.unsetMaxOccurs();
        return;
      }
      try
      {
        int newMax = 1;
        if (newValue.equals("unbounded") || newValue.equals("*")) //$NON-NLS-1$ //$NON-NLS-2$
        {
          newMax = XSDParticle.UNBOUNDED;
        }
        else
        {
          if (newValue.length() > 0)
          {
            newMax = Integer.parseInt(newValue);
          }
        }
        setListenerEnabled(false);
        UpdateMaxOccursCommand command = new UpdateMaxOccursCommand(Messages._UI_ACTION_CHANGE_MAXIMUM_OCCURRENCE, particle, newMax);
        getCommandStack().execute(command);
        setListenerEnabled(true);

      }
      catch (NumberFormatException e)
      {
        setErrorMessage(Messages._UI_ERROR_INVALID_VALUE_FOR_MAXIMUM_OCCURRENCE);
      }
    }
  }

  protected void updateMinAttribute()
  {
    setErrorMessage(null);
    XSDParticle particle = null;

    if (input instanceof XSDParticleContent)
    {
      particle = getAssociatedParticle((XSDParticleContent) input);
    }
    if (particle != null)
    {
      String newValue = minCombo.getText();
      if (newValue.length() == 0)
      {
        particle.unsetMinOccurs();
      }
      try
      {
        int newMin = 1;
        if (newValue.equals("unbounded") || newValue.equals("*")) //$NON-NLS-1$ //$NON-NLS-2$
        {
          newMin = XSDParticle.UNBOUNDED;
        }
        else
        {
          newMin = Integer.parseInt(newValue);
        }
        UpdateMinOccursCommand command = new UpdateMinOccursCommand(Messages._UI_ACTION_CHANGE_MINIMUM_OCCURRENCE, particle, newMin);
        getCommandStack().execute(command);
      }
      catch (NumberFormatException e)
      {

      }
    }
  }
  
  protected void refreshMinMax()
  {
    boolean refreshMinText = true;
    boolean refreshMaxText = true;
    if (minCombo.isFocusControl())
    {
      refreshMinText = false;
    }
    if (maxCombo.isFocusControl())
    {
      refreshMaxText = false;
    }
    if (refreshMinText)
    {
      minCombo.setText(""); //$NON-NLS-1$
    }
    if (refreshMaxText)
    {
      maxCombo.setText(""); //$NON-NLS-1$
    }

    if (input != null)
    {
      if (input instanceof XSDParticleContent)
      {
        XSDParticle particle = getAssociatedParticle((XSDParticleContent) input);
        if (particle != null)
        {
          // minText.setText(String.valueOf(particle.getMinOccurs()));
          // maxText.setText(String.valueOf(particle.getMaxOccurs()));
          Element element = particle.getElement();
          if (element != null)
          {
            if (element.hasAttribute(XSDConstants.MINOCCURS_ATTRIBUTE) && refreshMinText)
            {
              String min = element.getAttribute(XSDConstants.MINOCCURS_ATTRIBUTE);
              minCombo.setText(min);
            }
            if (element.hasAttribute(XSDConstants.MAXOCCURS_ATTRIBUTE) && refreshMaxText)
            {
              String max = element.getAttribute(XSDConstants.MAXOCCURS_ATTRIBUTE);
              maxCombo.setText(max);
            }
          }
        }
      }
    }
  }

  protected XSDParticle getAssociatedParticle(XSDParticleContent particleContent)
  {
    XSDConcreteComponent xsdComp = particleContent.getContainer();
    if (xsdComp instanceof XSDParticle)
    {
      return (XSDParticle) xsdComp;
    }
    return null;
  }
  
  public void dispose()
  {
    if (minCombo != null && !minCombo.isDisposed())
      removeListeners(minCombo);
    if (maxCombo != null && !maxCombo.isDisposed())
      removeListeners(maxCombo);
    super.dispose();
  }

}
