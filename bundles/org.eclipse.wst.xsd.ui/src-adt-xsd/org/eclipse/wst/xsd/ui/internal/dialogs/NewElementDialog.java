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
package org.eclipse.wst.xsd.ui.internal.dialogs;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.common.ui.internal.search.dialogs.ComponentSpecification;
import org.eclipse.wst.xsd.ui.internal.adt.edit.IComponentDialog;
import org.eclipse.wst.xsd.ui.internal.editor.Messages;
import org.eclipse.wst.xsd.ui.internal.search.IXSDSearchConstants;

public class NewElementDialog extends NewComponentDialog implements IComponentDialog
{
	  protected Object setObject;
	  protected int typeKind;
	  protected Object selection;

	  public NewElementDialog()
	  {
	    super(Display.getCurrent().getActiveShell(), Messages._UI_LABEL_NEW_ELEMENT, "NewElement");     //$NON-NLS-1$
	  }
	  
	  public int createAndOpen()
	  {
	    int returnCode = super.createAndOpen();
	    if (returnCode == 0)
	    {
	      if (setObject instanceof Adapter)
	      {  
	        //Command command = new AddComplexTypeDefinitionCommand(getName(), schema);
	      }        
	    }  
	    return returnCode;
	  }

	  public ComponentSpecification getSelectedComponent()
	  {
	    ComponentSpecification componentSpecification =  new ComponentSpecification(null, getName(), null);    
	    componentSpecification.setMetaName(IXSDSearchConstants.ELEMENT_META_NAME);
	    componentSpecification.setNew(true);
	    return componentSpecification;
	  }

	  public void setInitialSelection(ComponentSpecification componentSpecification)
	  {
	    // TODO Auto-generated method stub
	  }
	}
