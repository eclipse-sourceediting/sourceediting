/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.refactor.actions;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDSchema;

public class XSDSelectionDispatchAction extends SelectionDispatchAction
{

	
	
	public XSDSelectionDispatchAction(ISelection selection, Object model)
	{
		super(selection);
		setModel(model);
	}

	protected XSDSchema getSchema(){
		Object model = getModel();
		if(model instanceof XSDSchema)
		{
			return (XSDSchema) model;
		}
	
		return null;
	}
	
	protected boolean canEnable(Object selectedComponent)
	{
		XSDSchema selectedComponentSchema = null;
		if (selectedComponent instanceof XSDConcreteComponent)
		{
			selectedComponentSchema = ((XSDConcreteComponent)selectedComponent).getSchema();
		}
		
		if (selectedComponentSchema != null && selectedComponentSchema == getSchema() )
		{
			return true;
		}
		return false;
	}

}
