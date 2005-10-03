/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.properties.section;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.wst.common.ui.properties.internal.provisional.ITabbedPropertyConstants;
import org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetWidgetFactory;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDParticleContent;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

public class MinMaxSection extends AbstractSection
{
  CCombo minCombo;
  CCombo maxCombo;

  /**
   * 
   */
  public MinMaxSection()
  {
    super();
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
  }

  private void updateMinAttribute()
  {
    XSDParticle particle = null;
    Object input = getInput();

    if (input instanceof XSDParticleContent)
    {
      particle = getAssociatedParticle((XSDParticleContent)input);
    }
    if (particle != null)
    {
      Element element = particle.getElement();
      String newValue = ""; //$NON-NLS-1$

      newValue = minCombo.getText();
      beginRecording(XSDEditorPlugin.getXSDString("_UI_MINOCCURS_CHANGE"), element); //$NON-NLS-1$        
      if (newValue.length()==0)
      {
        particle.unsetMinOccurs();
      }
      try
      {
        if (newValue.equals("unbounded") || newValue.equals("*")) //$NON-NLS-1$
        {
          particle.setMinOccurs(XSDParticle.UNBOUNDED);
        }
        else
        {
          int val = Integer.parseInt(newValue);
          particle.setMinOccurs(val);
        }
      }
      catch (NumberFormatException e)
      {
      
      }
      finally
      { 
        endRecording(element);
      }
    }
  }
  
  private void updateMaxAttribute()
  {
    XSDParticle particle = null;
    Object input = getInput();

    if (input instanceof XSDParticleContent)
    {
      particle = getAssociatedParticle((XSDParticleContent)input);
    }
    if (particle != null)
    {
      Element element = particle.getElement();
      String newValue = "";
      newValue = maxCombo.getText();
      beginRecording(XSDEditorPlugin.getXSDString("_UI_MAXOCCURS_CHANGE"), element); //$NON-NLS-1$
      if (newValue.length()==0)
      {
        particle.unsetMaxOccurs();
      }
      try
      {
        if (newValue.equals("unbounded") || newValue.equals("*")) //$NON-NLS-1$
        {
          particle.setMaxOccurs(XSDParticle.UNBOUNDED);
        }
        else
        {
          int val = Integer.parseInt(newValue);
            particle.setMaxOccurs(val);
        }
      }
      catch (NumberFormatException e)
      {
        
      }
      finally
      {
        endRecording(element);
      }
    }    
  }
  
	/**
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.ITabbedPropertySection#createControls(org.eclipse.swt.widgets.Composite, org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetWidgetFactory)
	 */

	public void createControls(Composite parent,TabbedPropertySheetWidgetFactory factory)
	{
		super.createControls(parent, factory);
		composite =	getWidgetFactory().createFlatFormComposite(parent);
		FormData data;

    minCombo = getWidgetFactory().createCCombo(composite, SWT.FLAT);
    data = new FormData();
    data.left = new FormAttachment(0, 100);
    data.right = new FormAttachment(100, -rightMarginSpace - ITabbedPropertyConstants.HSPACE);
    data.top = new FormAttachment(0, 0);
    minCombo.setLayoutData(data);
    minCombo.add("0"); //$NON-NLS-1$
    minCombo.add("1"); //$NON-NLS-1$
    minCombo.addListener(SWT.Modify, this);
    minCombo.addSelectionListener(this);
    
		CLabel minLabel = getWidgetFactory().createCLabel(composite, XSDConstants.MINOCCURS_ATTRIBUTE + ":");
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(minCombo, -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(minCombo, 0, SWT.CENTER);
		minLabel.setLayoutData(data);

    maxCombo = getWidgetFactory().createCCombo(composite, SWT.FLAT);
    data = new FormData();
    data.left = new FormAttachment(0, 100);
    data.right = new FormAttachment(100, -rightMarginSpace - ITabbedPropertyConstants.HSPACE);
    data.top = new FormAttachment(minCombo, +ITabbedPropertyConstants.VSPACE);
    maxCombo.setLayoutData(data);
    maxCombo.add("0"); //$NON-NLS-1$
    maxCombo.add("1"); //$NON-NLS-1$
    maxCombo.add("unbounded"); //$NON-NLS-1$
    maxCombo.addListener(SWT.Modify, this);
    maxCombo.addSelectionListener(this);
    
		CLabel maxLabel = getWidgetFactory().createCLabel(composite, XSDConstants.MAXOCCURS_ATTRIBUTE + ":");
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(maxCombo, -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(maxCombo, 0, SWT.CENTER);
		maxLabel.setLayoutData(data);
	}

	/*
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.view.ITabbedPropertySection#refresh()
	 */
	public void refresh()
	{
    if (doRefresh)
    {
      if (isReadOnly)
      {
        composite.setEnabled(false);
      }
      else
      {
        composite.setEnabled(true);
      }

  	  setListenerEnabled(false);
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
  	  Object input = getInput();
  	  if (input != null)
  	  {
  	    if (input instanceof XSDParticleContent)
  	    {
  		    XSDParticle particle = getAssociatedParticle((XSDParticleContent)input);
  		    if (particle != null)
  		    {
  //  	      minText.setText(String.valueOf(particle.getMinOccurs()));
  //	        maxText.setText(String.valueOf(particle.getMaxOccurs()));
            Element element = particle.getElement();
  		      if (element != null)
  		      {
  		        String min = element.getAttribute(XSDConstants.MINOCCURS_ATTRIBUTE);
  		        String max = element.getAttribute(XSDConstants.MAXOCCURS_ATTRIBUTE);
  		        if (min != null && refreshMinText)
  		        {
  		          minCombo.setText(min);
  		        }
  		        if (max != null && refreshMaxText)
  		        {
  		          maxCombo.setText(max);
  		        }
  		      }
  		    }
  	    }
  	  }
  	  setListenerEnabled(true);
    }
	}
	
  public boolean shouldUseExtraSpace()
  {
    return false;
  }

  private XSDParticle getAssociatedParticle(XSDParticleContent particleContent)
  {
    XSDConcreteComponent xsdComp = particleContent.getContainer();
    if (xsdComp instanceof XSDParticle)
    {
      return (XSDParticle)xsdComp;
    }
    return null;
  }
  
}
