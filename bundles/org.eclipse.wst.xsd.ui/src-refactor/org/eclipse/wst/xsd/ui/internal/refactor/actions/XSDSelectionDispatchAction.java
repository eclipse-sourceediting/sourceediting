package org.eclipse.wst.xsd.ui.internal.refactor.actions;

import org.eclipse.jface.viewers.ISelection;
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

	

}
