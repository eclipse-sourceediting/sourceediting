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
package org.eclipse.wst.xsd.ui.internal.actions;

import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.xsd.ui.internal.widgets.EnumerationsDialog;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;


/**
 * Pattern is scoped to Enum Type
 */
public class AddEnumsAction extends CreateElementAction
{
  public AddEnumsAction(String label)
  {
  	super(label);
  }
 
  public Element createAndAddNewChildElement(String token)
  {
    String prefix = parentNode.getPrefix();
    prefix = (prefix == null) ? "" : (prefix + ":");
    Element childNode = getDocument().createElementNS(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, prefix + elementTag);
    if (getAttributes() != null)
    {
      List attributes = getAttributes();
      for (int i = 0; i < attributes.size(); i++)
      {
        DOMAttribute attr = (DOMAttribute) attributes.get(i);
        childNode.setAttribute(attr.getName(), attr.getValue());
      }
    }
    if (getRelativeNode() == null)
    {
      parentNode.appendChild(childNode);
    }
    else
    {
      ((Element)parentNode).insertBefore(childNode,getRelativeNode());
    }
    childNode.setAttribute("value", token);
    return childNode;
  }    
  
  public void run()
  {
    Display display = Display.getCurrent();
    // if it is null, get the default one
    display = display == null ? Display.getDefault() : display;
    Shell parentShell = display.getActiveShell();
    EnumerationsDialog dialog = new EnumerationsDialog(parentShell);
    dialog.setBlockOnOpen(true);
    int result = dialog.open();

    if (result == Window.OK) 
    {
      beginRecording(getDescription());

      String text = dialog.getText();
      String delimiter = dialog.getDelimiter();
      StringTokenizer tokenizer = new StringTokenizer(text, delimiter);
      while (tokenizer.hasMoreTokens()) 
      {
        String token = tokenizer.nextToken();
        if (dialog.isPreserveWhitespace() == false) 
        {
          token = token.trim();
        }

        Element child = createAndAddNewChildElement(token);
        formatChild(child);        
      }
      endRecording();
    }
  }
  
}
