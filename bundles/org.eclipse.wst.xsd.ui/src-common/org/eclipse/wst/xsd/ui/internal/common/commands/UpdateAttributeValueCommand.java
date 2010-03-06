/*******************************************************************************
 * Copyright (c) 2001, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.commands;

import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.xsd.ui.internal.common.util.Messages;
import org.w3c.dom.Element;

/*
 * This command is used from the extension view to edit extension elements
 * and attributes which are implemented as DOM objects (not part of the EMF model)
 * 
 * (trung) also used in XSDComplexTypeAdvancedSection to change attribute of
 * specical attributes like block, restriction which is not part of EMF Model
 */
public class UpdateAttributeValueCommand  extends BaseCommand
{
  protected Element element;
  protected String attributeName;
  protected String attributeValue;
  
  /** Whether the attribute should be deleted if value to 
   *   be set is an empty String or null  */
  protected boolean deleteIfValueEmpty = false;
  
  public UpdateAttributeValueCommand(Element element, String attributeName, String attributeValue, boolean deleteIfValueEmpty)
  {
    this.element = element;
    this.attributeName = attributeName;
    this.attributeValue = attributeValue;
    this.deleteIfValueEmpty = deleteIfValueEmpty;
  }

  public UpdateAttributeValueCommand(Element element, String attributeName, String attributeValue, String label)
  {
    this(element, attributeName, attributeValue, false);

    // The command label is shared with the actual UI label, which contains 
    // mnemonics and colon characters. Since we don't want these to show up in 
    // the undo menu we do a little bit of regular expression magic here to remove them.
    
    setLabel(NLS.bind(Messages._UI_ACTION_CHANGE, label.replaceAll("[&:]", "")));
  }
  
  public UpdateAttributeValueCommand(Element element, String attributeName, String attributeValue)
  {
    this(element, attributeName, attributeValue, false);
    setLabel(NLS.bind(Messages._UI_ACTION_CHANGE, attributeName));
  }

  public void setDeleteIfEmpty(boolean v)
  {
	  deleteIfValueEmpty = v;
  }
  
  public void setAttributeName(String attributeName)
  {
    this.attributeName = attributeName;
  }
  
  public void execute()
  {
    try
    {
      beginRecording(element);
      if (deleteIfValueEmpty && 
    		  (attributeValue == null || attributeValue.equals("") ) )
      {
    	element.removeAttribute(attributeName);
      }
      else
      {
        element.setAttribute(attributeName, attributeValue);
      }
      
      doPostProcessing();
    }
    finally
    {
      endRecording();
    }
  }
  
  protected void doPostProcessing()
  {
    
  }
}
