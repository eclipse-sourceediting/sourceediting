/*******************************************************************************
 * Copyright (c) 2009, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xsd.ui.internal.common.commands;


import org.eclipse.wst.xsd.ui.internal.adapters.XSDAttributeGroupDefinitionAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDComplexTypeDefinitionAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDModelGroupDefinitionAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDSimpleTypeDefinitionAdapter;
import org.eclipse.xsd.XSDRedefinableComponent;
import org.eclipse.xsd.XSDRedefine;
import org.w3c.dom.Element;


/**
 * Base class for commands that add redefined components to a schema redefine. 
 */
public abstract class AddRedefinedComponentCommand extends BaseCommand
{
  protected XSDRedefine redefine;

  protected XSDRedefinableComponent redefinableComponent;

  public AddRedefinedComponentCommand(String label, XSDRedefine redefine, XSDRedefinableComponent redefinableComponent)
  {
    super(label);
    this.redefine = redefine;
    this.redefinableComponent = redefinableComponent;
  }

  protected abstract void doExecute();

  public void execute()
  {
    Element element = redefine.getElement();

    try
    {
      beginRecording(element);
      doExecute();
	  Object adapter =  redefinableComponent.eAdapters().get(0);
	  if (adapter instanceof XSDComplexTypeDefinitionAdapter)
	  {    		 
		  ((XSDComplexTypeDefinitionAdapter)adapter).updateDeletedMap(redefinableComponent.getName());
      }
	  else if (adapter instanceof XSDSimpleTypeDefinitionAdapter)
	  {    		 
		  ((XSDSimpleTypeDefinitionAdapter)adapter).updateDeletedMap(redefinableComponent.getName());
      }
	  else if (adapter instanceof XSDAttributeGroupDefinitionAdapter)
	  {    		 
		  ((XSDAttributeGroupDefinitionAdapter)adapter).updateDeletedMap(redefinableComponent.getName());
      }
      else if (adapter instanceof XSDModelGroupDefinitionAdapter)
	  {    		 
		  ((XSDModelGroupDefinitionAdapter)adapter).updateDeletedMap(redefinableComponent.getName());
      }
      redefine.getContents().add(addedXSDConcreteComponent);
      formatChild(addedXSDConcreteComponent.getElement());
    }
    finally
    {
      endRecording();
    }
  }
}
